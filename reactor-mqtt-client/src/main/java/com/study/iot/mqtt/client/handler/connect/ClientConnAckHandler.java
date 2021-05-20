package com.study.iot.mqtt.client.handler.connect;

import com.study.iot.mqtt.client.strategy.StrategyCapable;
import com.study.iot.mqtt.client.strategy.StrategyGroup;
import com.study.iot.mqtt.client.strategy.StrategyService;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:17
 */

@Slf4j
@StrategyService(group = StrategyGroup.CLIENT, type = MqttMessageType.CONNACK)
public class ClientConnAckHandler implements StrategyCapable {

    @Override
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("client ConnAck message: {}, connection: {}", message, disposableConnection);
        MqttConnAckMessage mqttConnAckMessage = (MqttConnAckMessage) message;
        MqttConnAckVariableHeader variableHeader = mqttConnAckMessage.variableHeader();
        // 取消重发
        if (variableHeader.connectReturnCode().equals(MqttConnectReturnCode.CONNECTION_ACCEPTED)) {
            disposableConnection.getConnection().channel().attr(AttributeKeys.closeConnection).get().dispose();
            return;
        }

        log.error("login failed: {}, message: {}", variableHeader.connectReturnCode(), variableHeader);
    }
}
