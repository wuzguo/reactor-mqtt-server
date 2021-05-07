package com.study.iot.mqtt.transport.server.router;


import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.transport.constant.Group;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerMessageRouter {

    private final StrategyContainer container;

    public ServerMessageRouter(StrategyContainer container) {
        this.container = container;
    }

    public void handler(MqttMessage message, TransportConnection connection) {
        log.info("accept message channel {} info {}", connection.getConnection(), message);

        if (!message.decoderResult().isSuccess()) {
            log.error("accept message  error {}", message.decoderResult().toString());
            return;
        }

        container.getStrategy(Group.SERVER, message.fixedHeader().messageType()).handler(message, connection);
    }
}
