package com.study.iot.mqtt.protocol.mqtt;


import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ConnectProperties;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.ProtocolTransport;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.Attribute;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

@Slf4j
public class MqttTransport extends ProtocolTransport {

    public MqttTransport(MqttProtocol protocol) {
        super(protocol);
    }

    @Override
    public Mono<? extends DisposableServer> start(ServerProperties properties,
        UnicastProcessor<DisposableConnection> processor) {
        return buildServer(properties).doOnConnection(connection -> {
            log.info("mqtt protocol connection: {}, ", connection.channel());
            protocol.getHandlers().forEach(connection::addHandlerLast);
            processor.onNext(new DisposableConnection(connection));
        }).bind().doOnSuccess(disposableServer -> {
            log.info("mqtt protocol host: {} port: {}", properties.getHost(), properties.getPort());
        }).doOnError(properties.getThrowable());
    }

    private TcpServer buildServer(ServerProperties properties) {
        TcpServer server = TcpServer.create()
            .host(properties.getHost())
            .port(properties.getPort())
            .wiretap(properties.getIsLog())
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .option(ChannelOption.SO_KEEPALIVE, properties.getKeepAlive())
            .option(ChannelOption.TCP_NODELAY, properties.getNoDelay())
            .option(ChannelOption.SO_BACKLOG, properties.getBacklog())
            .option(ChannelOption.SO_RCVBUF, properties.getRevBufSize())
            .option(ChannelOption.SO_SNDBUF, properties.getSendBufSize());
        return properties.getIsSsl() ? server
            .secure(sslContextSpec -> sslContextSpec.sslContext(Objects.requireNonNull(buildContext()))) : server;

    }

    private SslContext buildContext() {
        try {
            SelfSignedCertificate certificate = new SelfSignedCertificate();
            return SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
        } catch (Exception e) {
            log.error("ssl error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Mono<DisposableConnection> connect(ClientProperties properties) {
        return buildClient(properties).connect().map(connection -> {
            Connection connect = connection;
            protocol.getHandlers().forEach(connect::addHandler);
            DisposableConnection disposableConnection = new DisposableConnection(connection);
            connection.onDispose(() -> retryConnect(properties, disposableConnection));
            log.info("connected successes");
            return disposableConnection;
        });
    }

    private void retryConnect(ClientProperties properties, DisposableConnection disposableConnection) {
        log.info("short-term reconnection");
        buildClient(properties)
            .connect()
            .doOnError(properties.getThrowable())
            .retry()
            .cast(Connection.class)
            .subscribe(connection -> {
                protocol.getHandlers().forEach(connection::addHandler);
                Optional.ofNullable(
                    disposableConnection.getConnection().channel().attr(AttributeKeys.clientConnection))
                    .map(Attribute::get).ifPresent(clientSession -> {
                    disposableConnection.setConnection(connection);
                    disposableConnection.setInbound(connection.inbound());
                    disposableConnection.setOutbound(connection.outbound());
                    clientSession.doConnect(properties);
                });
            });
    }

    private TcpClient buildClient(ConnectProperties properties) {
        TcpClient client = TcpClient.create().host(properties.getHost())
            .port(properties.getPort())
            .wiretap(properties.getIsLog());
        try {
            SslContext sslClient = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
            return properties.getIsSsl() ? client.secure(sslContextSpec -> sslContextSpec.sslContext(sslClient)) :
                client;
        } catch (Exception e) {
            properties.getThrowable().accept(e);
            return client;
        }
    }
}
