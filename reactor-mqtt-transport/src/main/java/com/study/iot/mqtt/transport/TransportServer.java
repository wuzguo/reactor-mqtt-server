package com.study.iot.mqtt.transport;


import com.study.iot.mqtt.store.mapper.StoreMapper;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.transport.router.ServerMessageRouter;
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

    public Mono<ServerSession> start(StoreMapper storeMapper, ServerMessageRouter messageRouter) {
        storeMapper.strategy(properties.getStrategy());
        return transportFactory.start(properties, storeMapper, messageRouter);
    }
}
