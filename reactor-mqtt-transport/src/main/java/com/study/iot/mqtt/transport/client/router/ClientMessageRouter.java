package com.study.iot.mqtt.transport.client.router;


import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Getter
@Slf4j
public class ClientMessageRouter {

    private final StrategyContainer container;

    public ClientMessageRouter(StrategyContainer container) {
        this.container = container;
    }

    public void handler(MqttMessage message, DisposableConnection connection) {
        log.info("accept message channel {} info {}", connection.getConnection(), message);

        if (!message.decoderResult().isSuccess()) {
            log.error("accept message  error {}", message.decoderResult().toString());
            return;
        }

        Optional.ofNullable(container.getStrategy(StrategyGroup.CLIENT, message.fixedHeader().messageType()))
                .ifPresent(capable -> ((StrategyCapable) capable).handle(message, connection));
    }
}
