package com.study.iot.mqtt.transport.server.router;


import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import com.study.iot.mqtt.transport.strategy.WillStrategyContainer;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Getter
public class ServerMessageRouter {

    private final StrategyContainer container;

    private final WillStrategyContainer willContainer;

    public ServerMessageRouter(StrategyContainer container, WillStrategyContainer willContainer) {
        this.container = container;
        this.willContainer = willContainer;
    }

    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("accept message channel {}，message {}", connection.getConnection().channel(), message);

        if (!message.decoderResult().isSuccess()) {
            log.error("accept message  error {}", message.decoderResult());
            return;
        }

        Optional.ofNullable(container.getStrategy(StrategyGroup.SERVER, message.fixedHeader().messageType()))
                .ifPresent(capable -> ((StrategyCapable) capable).handle(message, connection));
    }
}
