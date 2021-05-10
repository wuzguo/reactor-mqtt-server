package com.study.iot.mqtt.transport.client.handler;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/6 11:24
 */

@Slf4j
@StrategyService(group = StrategyGroup.CLIENT, type = MqttMessageType.UNSUBACK)
public class ClientUnsubAckHandler implements StrategyCapable {

    @Override
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("client UnsubAck message: {}, connection: {}", message, connection);
    }
}