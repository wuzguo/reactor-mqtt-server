package com.study.iot.mqtt.transport.client.router;


import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import io.netty.handler.codec.mqtt.MqttMessage;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class ClientMessageRouter {

    private final StrategyContainer container;

    public ClientMessageRouter(StrategyContainer container) {
        this.container = container;
    }

    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("accept message channel {}，message {}", connection.getConnection().channel(), message);

        if (!message.decoderResult().isSuccess()) {
            log.error("accept message  error {}", message.decoderResult());
            return;
        }

        // 策略分发
        Optional.ofNullable(container.findStrategy(StrategyGroup.CLIENT, message.fixedHeader().messageType()))
            .ifPresent(capable -> ((StrategyCapable) capable).handle(message, connection));
    }
}
