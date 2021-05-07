package com.study.iot.mqtt.transport.client.router;


import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.transport.constant.Group;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ClientMessageRouter {

    private final StrategyContainer container;

    public ClientMessageRouter(StrategyContainer container) {
        this.container = container;
    }

    public void handler(MqttMessage message, TransportConnection connection) {
        log.info("accept message channel {} info {}", connection.getConnection(), message);

        if (!message.decoderResult().isSuccess()) {
            log.error("accept message  error {}", message.decoderResult().toString());
            return;
        }

        container.getStrategy(Group.CLIENT, message.fixedHeader().messageType()).handler(message, connection);
    }
}
