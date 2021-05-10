package com.study.iot.mqtt.protocol.mqtt;


import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.ConnectConfiguration;
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

    public MqttTransport(MqttProtocol mqttProtocol) {
        super(mqttProtocol);
    }

    @Override
    public Mono<? extends DisposableServer> start(ConnectConfiguration config,
        UnicastProcessor<DisposableConnection> processor) {
        return buildServer(config).doOnConnection(connection -> {
            log.info("mqtt protocol connection: {}, ", connection.channel());
            protocol.getHandlers().forEach(connection::addHandlerLast);
            processor.onNext(new DisposableConnection(connection));
        }).bind().doOnSuccess(disposableServer -> {
            log.info("server successfully started，mqtt protocol listening ip: {} port: {}", config.getIp(),
                config.getPort());
        }).doOnError(config.getThrowableConsumer());
    }

    private TcpServer buildServer(ConnectConfiguration config) {
        TcpServer server = TcpServer.create()
            .port(config.getPort())
            .wiretap(config.isLog())
            .host(config.getIp())
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .option(ChannelOption.SO_KEEPALIVE, config.isKeepAlive())
            .option(ChannelOption.TCP_NODELAY, config.isNoDelay())
            .option(ChannelOption.SO_BACKLOG, config.getBacklog())
            .option(ChannelOption.SO_RCVBUF, config.getRevBufSize())
            .option(ChannelOption.SO_SNDBUF, config.getSendBufSize());
        return config.isSsl() ? server
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
    public Mono<DisposableConnection> connect(ConnectConfiguration config) {
        return buildClient(config).connect().map(connection -> {
            Connection connect = connection;
            protocol.getHandlers().forEach(connect::addHandler);
            DisposableConnection disposableConnection = new DisposableConnection(connection);
            connection.onDispose(() -> retryConnect(config, disposableConnection));
            log.info("connected successes !");
            return disposableConnection;
        });
    }

    private void retryConnect(ConnectConfiguration config, DisposableConnection disposableConnection) {
        log.info("short-term reconnection");
        buildClient(config)
            .connect()
            .doOnError(config.getThrowableConsumer())
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
                    clientSession.init();
                });
            });
    }

    private TcpClient buildClient(ConnectConfiguration config) {
        TcpClient client = TcpClient.create()
            .port(config.getPort())
            .host(config.getIp())
            .wiretap(config.isLog());
        try {
            SslContext sslClient = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
            return config.isSsl() ? client.secure(sslContextSpec -> sslContextSpec.sslContext(sslClient)) : client;
        } catch (Exception e) {
            config.getThrowableConsumer().accept(e);
            return client;
        }
    }
}
