package com.study.iot.mqtt.transport.handler.connect;

import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：发布完成（QoS 2，第三步）</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.PUBCOMP)
public class ServerPubCompHandler implements ConnectCapable {

    @Override
    public void handle(DisposableConnection disposable, MqttMessage mqttMessage) {
        log.info("pubComp message: {}, connection: {}", mqttMessage, disposable);
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        disposable.cancelDisposable(variableHeader.messageId());
    }
}
