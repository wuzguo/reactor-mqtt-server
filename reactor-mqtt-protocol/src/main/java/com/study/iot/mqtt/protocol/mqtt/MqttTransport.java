package com.study.iot.mqtt.protocol.mqtt;


import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.config.ConnectConfiguration;
import com.study.iot.mqtt.protocol.config.ServerConfiguration;
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


@Slf4j
public class MqttTransport extends ProtocolTransport {

    public MqttTransport(MqttProtocol protocol) {
        super(protocol);
    }

    @Override
    public Mono<? extends DisposableServer> start(ServerConfiguration config,
        UnicastProcessor<DisposableConnection> processor) {
        return buildServer(config).doOnConnection(connection -> {
            log.info("mqtt protocol connection: {}, ", connection.channel());
            protocol.getHandlers().forEach(connection::addHandlerLast);
            processor.onNext(new DisposableConnection(connection));
        }).bind().doOnSuccess(disposableServer -> {
            log.info("mqtt protocol host: {} port: {}", config.getHost(), config.getPort());
        }).doOnError(config.getThrowable());
    }

    private TcpServer buildServer(ServerConfiguration configuration) {
        TcpServer server = TcpServer.create()
            .port(configuration.getPort())
            .wiretap(configuration.getIsLog())
            .host(configuration.getHost())
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .option(ChannelOption.SO_KEEPALIVE, configuration.getKeepAlive())
            .option(ChannelOption.TCP_NODELAY, configuration.getNoDelay())
            .option(ChannelOption.SO_BACKLOG, configuration.getBacklog())
            .option(ChannelOption.SO_RCVBUF, configuration.getRevBufSize())
            .option(ChannelOption.SO_SNDBUF, configuration.getSendBufSize());
        return configuration.getIsSsl() ? server
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
    public Mono<DisposableConnection> connect(ClientConfiguration configuration) {
        return buildClient(configuration).connect().map(connection -> {
            Connection connect = connection;
            protocol.getHandlers().forEach(connect::addHandler);
            DisposableConnection disposableConnection = new DisposableConnection(connection);
            connection.onDispose(() -> retryConnect(configuration, disposableConnection));
            log.info("connected successes !");
            return disposableConnection;
        });
    }

    private void retryConnect(ClientConfiguration configuration, DisposableConnection disposableConnection) {
        log.info("short-term reconnection");
        buildClient(configuration)
            .connect()
            .doOnError(configuration.getThrowable())
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
                    clientSession.init(configuration);
                });
            });
    }

    private TcpClient buildClient(ConnectConfiguration configuration) {
        TcpClient client = TcpClient.create()
            .port(configuration.getPort())
            .host(configuration.getHost())
            .wiretap(configuration.getIsLog());
        try {
            SslContext sslClient = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
            return configuration.getIsSsl() ? client.secure(sslContextSpec -> sslContextSpec.sslContext(sslClient)) :
                client;
        } catch (Exception e) {
            configuration.getThrowable().accept(e);
            return client;
        }
    }
}
