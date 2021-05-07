package com.study.iot.mqtt.transport.server.router;


import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.transport.constant.Group;
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

    public void handler(MqttMessage message, TransportConnection connection) {
        log.info("accept message channel {} info {}", connection.getConnection(), message);

        if (!message.decoderResult().isSuccess()) {
            log.error("accept message  error {}", message.decoderResult().toString());
            return;
        }

        Optional.ofNullable(container.getStrategy(Group.SERVER, message.fixedHeader().messageType()))
                .ifPresent(capable -> ((StrategyCapable) capable).handler(message, connection));
    }
}
