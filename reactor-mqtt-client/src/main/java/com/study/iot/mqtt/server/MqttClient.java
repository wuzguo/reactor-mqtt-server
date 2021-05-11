package com.study.iot.mqtt.server;

import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.TransportClient;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/10 17:21
 */

@Slf4j
public class MqttClient {

    private final TransportClient transportClient;

    public MqttClient(ClientConfiguration configuration) {
        this.transportClient = new TransportClient().create(configuration);
    }

    public Mono<ClientSession> connect(ClientMessageRouter messageRouter) {
        return transportClient.connect(messageRouter);
    }
}
