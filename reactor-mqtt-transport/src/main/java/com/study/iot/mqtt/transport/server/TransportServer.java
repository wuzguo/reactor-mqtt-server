package com.study.iot.mqtt.transport.server;


import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import com.study.iot.mqtt.protocol.session.ServerSession;
import reactor.core.publisher.Mono;

public class TransportServer {

    private ServerProperties properties;

    private final TransportServerFactory transportFactory;

    public TransportServer() {
        transportFactory = new TransportServerFactory();
    }

    public TransportServer create(ServerProperties properties) {
        this.properties = properties;
        return this;
    }

    public Mono<ServerSession> start(CacheManager cacheManager, ServerMessageRouter messageRouter) {
        cacheManager.strategy(properties.getStrategy());
        return transportFactory.start(properties, cacheManager, messageRouter);
    }
}
