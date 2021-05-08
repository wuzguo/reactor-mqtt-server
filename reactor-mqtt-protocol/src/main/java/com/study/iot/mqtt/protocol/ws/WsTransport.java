package com.study.iot.mqtt.protocol.ws;


import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.ConnectConfiguration;
import com.study.iot.mqtt.protocol.ProtocolTransport;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.util.Objects;


@Slf4j
public class WsTransport extends ProtocolTransport {

    public WsTransport(WsProtocol wsProtocol) {
        super(wsProtocol);
    }

    @Override
    public Mono<? extends DisposableServer> start(ConnectConfiguration config, UnicastProcessor<DisposableConnection> processor) {
        return buildServer(config).doOnConnection(connection -> {
            log.info("websocket protocol connection: {}, ", connection.channel());
            protocol.getHandlers().forEach(connection::addHandlerLast);
            processor.onNext(new DisposableConnection(connection));
        }).bind().doOnSuccess(disposableServer -> {
            log.info("server successfully started，websocket protocol listening ip: {} port: {}", config.getIp(), config.getPort());
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
        return config.isSsl() ? server.secure(sslContextSpec -> sslContextSpec.sslContext(Objects.requireNonNull(buildContext()))) : server;
    }


    private SslContext buildContext() {
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } catch (Exception e) {
            log.error("ssl error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Mono<DisposableConnection> connect(ConnectConfiguration config) {
        return Mono.just(buildClient(config)
                .connectNow())
                .map(connection -> {
                    protocol.getHandlers().forEach(connection::addHandler);
                    return new DisposableConnection(connection);
                });
    }

    private TcpClient buildClient(ConnectConfiguration config) {
        TcpClient client = TcpClient.create()
                .port(config.getPort())
                .host(config.getIp())
                .wiretap(config.isLog());
        try {
            SslContext sslClient = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            return config.isSsl() ? client.secure(sslContextSpec -> sslContextSpec.sslContext(sslClient)) : client;
        } catch (Exception e) {
            log.error("ssl error: {}", e.getMessage());
            return client;
        }
    }
}
