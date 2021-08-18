package com.study.iot.mqtt.transport;


import com.study.iot.mqtt.common.domain.ProtocolProperties;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.transport.router.ServerMessageRouter;
import java.util.Set;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:20
 */

public class TransportServer {

    private ServerProperties properties;

    private Set<ProtocolProperties> protocols;

    private final TransportServerFactory transportFactory;

    public TransportServer() {
        transportFactory = new TransportServerFactory();
    }

    public TransportServer create(ServerProperties properties, Set<ProtocolProperties> protocols) {
        this.properties = properties;
        this.protocols = protocols;
        return this;
    }

    public Mono<ServerSession> start(ContainerManager containerManager, ServerMessageRouter messageRouter) {
        containerManager.strategy(properties.getStrategy());
        return transportFactory.start(properties, protocols, containerManager, messageRouter);
    }
}
