package com.study.iot.mqtt.transport.client.handler.connect;

import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.AttributeKeys;
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
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("client ConnAck message: {}, connection: {}", message, connection);
        MqttConnAckMessage mqttConnAckMessage = (MqttConnAckMessage) message;
        MqttConnAckVariableHeader variableHeader = mqttConnAckMessage.variableHeader();
        // 取消重发
        if (variableHeader.connectReturnCode().equals(MqttConnectReturnCode.CONNECTION_ACCEPTED)) {
            connection.getConnection().channel().attr(AttributeKeys.closeConnection).get().dispose();
            return;
        }

        log.error("login failed: {}, message: {}", variableHeader.connectReturnCode(), variableHeader);
    }
}
