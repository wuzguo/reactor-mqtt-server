package com.study.iot.mqtt.transport.server;


import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.protocol.config.ServerConfiguration;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import com.study.iot.mqtt.protocol.session.ServerSession;
import reactor.core.publisher.Mono;

public class TransportServer {

    private ServerConfiguration configuration;

    private final TransportServerFactory transportFactory;

    public TransportServer() {
        transportFactory = new TransportServerFactory();
    }

    public TransportServer create(ServerConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public Mono<ServerSession> start(CacheManager cacheManager, ServerMessageRouter messageRouter) {
        cacheManager.strategy(configuration.getStrategy());
        return transportFactory.start(configuration, cacheManager, messageRouter);
    }
}
