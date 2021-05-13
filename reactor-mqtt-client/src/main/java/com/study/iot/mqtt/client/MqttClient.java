package com.study.iot.mqtt.client;

import com.study.iot.mqtt.protocol.config.ClientProperties;
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

    public MqttClient(ClientProperties properties) {
        this.transportClient = new TransportClient().create(properties);
    }

    public Mono<ClientSession> connect(ClientMessageRouter messageRouter) {
        return transportClient.connect(messageRouter);
    }
}
