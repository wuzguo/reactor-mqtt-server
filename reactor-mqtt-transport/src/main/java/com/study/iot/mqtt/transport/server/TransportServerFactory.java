package com.study.iot.mqtt.transport.server;


import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.config.ServerConfiguration;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.protocol.ws.WsProtocol;
import com.study.iot.mqtt.protocol.ws.WsTransport;
import com.study.iot.mqtt.transport.server.connection.ServerConnection;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;


public class TransportServerFactory {

    private final ProtocolFactory protocolFactory;

    private final UnicastProcessor<DisposableConnection> unicastProcessor = UnicastProcessor.create();

    private DisposableServer disposableServer;

    public TransportServerFactory() {
        protocolFactory = new ProtocolFactory();
    }

    public Mono<ServerSession> start(ServerConfiguration config, CacheManager cacheManager,
        ServerMessageRouter messageRouter) {
        // 开启
        if (config.getProtocol().equals(ProtocolType.MQTT.name())) {
            WsTransport wsTransport = new WsTransport(new WsProtocol());
            ServerConfiguration wsConfig = this.copy(config);
            disposableServer = wsTransport.start(wsConfig, unicastProcessor).block();
        }

        return Mono.from(protocolFactory.getProtocol(ProtocolType.valueOf(config.getProtocol()))
            .get()
            .getTransport()
            .start(config, unicastProcessor))
            .map(disposable -> this.wrapper(disposable, cacheManager, messageRouter))
            .doOnError(config.getThrowableConsumer());
    }

    private ServerConfiguration copy(ServerConfiguration config) {
        ServerConfiguration serverConfiguration = new ServerConfiguration();
        serverConfiguration.setThrowableConsumer(config.getThrowableConsumer());
        serverConfiguration.setLog(config.isLog());
        serverConfiguration.setAuth(config.getAuth());
        serverConfiguration.setIp(config.getIp());
        serverConfiguration.setPort(8443);
        serverConfiguration.setSsl(config.isSsl());
        serverConfiguration.setProtocol(ProtocolType.WEB_SOCKET.name());
        serverConfiguration.setHeart(config.getHeart());
        serverConfiguration.setRevBufSize(config.getRevBufSize());
        serverConfiguration.setSendBufSize(config.getSendBufSize());
        serverConfiguration.setNoDelay(config.isNoDelay());
        serverConfiguration.setKeepAlive(config.isKeepAlive());
        return serverConfiguration;
    }

    private ServerSession wrapper(DisposableServer disposable, CacheManager cacheManager,
        ServerMessageRouter messageRouter) {
        return new ServerConnection(unicastProcessor, disposable, disposableServer, cacheManager, messageRouter);
    }
}
