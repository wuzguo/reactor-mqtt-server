package com.study.iot.mqtt.transport.client.handler;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
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
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("client SubAck message: {}, connection: {}", message, connection);
        MqttMessageIdVariableHeader variableHeader =(MqttMessageIdVariableHeader) message.variableHeader();
        connection.cancelDisposable(variableHeader.messageId());
    }
}
