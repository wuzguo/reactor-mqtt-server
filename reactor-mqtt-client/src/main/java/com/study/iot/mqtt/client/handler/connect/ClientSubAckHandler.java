package com.study.iot.mqtt.client.handler.connect;

import com.study.iot.mqtt.client.strategy.StrategyCapable;
import com.study.iot.mqtt.client.strategy.StrategyGroup;
import com.study.iot.mqtt.client.strategy.StrategyService;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/6 11:25
 */

@Slf4j
@StrategyService(group = StrategyGroup.CLIENT, type = MqttMessageType.SUBACK)
public class ClientSubAckHandler implements StrategyCapable {

    @Override
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("client SubAck message: {}, connection: {}", message, disposableConnection);
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
        disposableConnection.cancelDisposable(variableHeader.messageId());
    }
}
