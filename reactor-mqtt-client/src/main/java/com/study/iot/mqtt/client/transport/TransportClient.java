package com.study.iot.mqtt.client.transport;


import com.study.iot.mqtt.client.router.ClientMessageRouter;
import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.session.ClientSession;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:00
 */

public class TransportClient {

    private ClientProperties properties;

    private final TransportClientFactory transportFactory;

    public TransportClient() {
        transportFactory = new TransportClientFactory();
    }

    public TransportClient create(ClientProperties properties) {
        this.properties = properties;
        return this;
    }

    public Mono<ClientSession> connect(ClientMessageRouter messageRouter) {
        return transportFactory.connect(properties, messageRouter);
    }
}
