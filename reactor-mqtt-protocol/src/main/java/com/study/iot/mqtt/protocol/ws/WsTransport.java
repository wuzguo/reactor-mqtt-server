package com.study.iot.mqtt.protocol.ws;


import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.ProtocolTransport;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.config.ConnectConfiguration;
import com.study.iot.mqtt.protocol.config.ServerConfiguration;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;


@Slf4j
public class WsTransport extends ProtocolTransport {

    public WsTransport(WsProtocol wsProtocol) {
        super(wsProtocol);
    }

    @Override
    public Mono<? extends DisposableServer> start(ServerConfiguration configuration,
        UnicastProcessor<DisposableConnection> processor) {
        return buildServer(configuration).doOnConnection(connection -> {
            log.info("websocket protocol connection: {}, ", connection.channel());
            protocol.getHandlers().forEach(connection::addHandlerLast);
            processor.onNext(new DisposableConnection(connection));
        }).bind().doOnSuccess(disposableServer -> {
            log.info("websocket protocol host: {} port: {}", configuration.getHost(), configuration.getPort());
        }).doOnError(configuration.getThrowable());
    }

    private TcpServer buildServer(ServerConfiguration configuration) {
        TcpServer server = TcpServer.create()
            .port(configuration.getPort())
            .host(configuration.getHost())
            .wiretap(configuration.getIsLog())
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
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            return SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } catch (Exception e) {
            log.error("ssl error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Mono<DisposableConnection> connect(ClientConfiguration configuration) {
        return Mono.just(buildClient(configuration)
            .connectNow())
            .map(connection -> {
                protocol.getHandlers().forEach(connection::addHandler);
                return new DisposableConnection(connection);
            });
    }

    private TcpClient buildClient(ConnectConfiguration configuration) {
        TcpClient client = TcpClient.create()
            .port(configuration.getPort())
            .host(configuration.getHost())
            .wiretap(configuration.getIsLog());
        try {
            SslContext sslClient = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            return configuration.getIsSsl() ? client.secure(sslContextSpec -> sslContextSpec.sslContext(sslClient))
                : client;
        } catch (Exception e) {
            log.error("ssl error: {}", e.getMessage());
            return client;
        }
    }
}
