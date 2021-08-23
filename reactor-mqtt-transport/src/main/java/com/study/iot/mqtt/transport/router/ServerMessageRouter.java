package com.study.iot.mqtt.transport.router;


import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import io.netty.handler.codec.mqtt.MqttMessage;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:20
 */

@Slf4j
@Getter
public class ServerMessageRouter {

    private final StrategyContainer container;

    public ServerMessageRouter(StrategyContainer container) {
        this.container = container;
    }

    public void handle(MqttMessage message, DisposableConnection disposableConnection) {
        log.info("accept message channel {}，message {}", disposableConnection.getConnection().channel(), message);
        // 如果消息解密不成功
        if (!message.decoderResult().isSuccess()) {
            log.error("accept message  error {}", message.decoderResult());
            return;
        }

        // 消息分发，策略模式
        Optional.ofNullable(container.find(StrategyGroup.CONNECT, StrategyEnum.valueOf(message.fixedHeader().messageType())))
            .ifPresent(capable -> ((ConnectCapable) capable).handle(disposableConnection, message));
    }
}
