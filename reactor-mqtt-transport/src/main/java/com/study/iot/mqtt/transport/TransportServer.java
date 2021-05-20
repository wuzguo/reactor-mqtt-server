package com.study.iot.mqtt.transport;


import com.study.iot.mqtt.store.container.ContainerManager;
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

    public Mono<ServerSession> start(ContainerManager containerManager, ServerMessageRouter messageRouter) {
        containerManager.strategy(properties.getStrategy());
        return transportFactory.start(properties, containerManager, messageRouter);
    }
}
