package com.study.iot.mqtt.server;

import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.config.ClientConfiguration.Options;
import com.study.iot.mqtt.transport.client.TransportClient;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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

    private final ClientMessageRouter messageRouter;

    public MqttClient(ClientMessageRouter messageRouter) {
        this.messageRouter = messageRouter;
    }

    public Mono<ClientSession> connect(ClientConfiguration configuration) {
        return new TransportClient().create(configuration).connect(messageRouter);
    }
}
