package com.study.iot.mqtt.transport.handler.connect;

import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：发布收到（QoS 2，第一步）</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.PUBREC)
public class ServerPubRecHandler implements ConnectCapable {

    @Override
    public void handle(DisposableConnection disposable, MqttMessage mqttMessage) {
        log.info("pubRec message: {}", mqttMessage);
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        disposable.sendMessageRetry(messageId, MessageBuilder.buildPubRel(messageId));
        disposable.cancelDisposable(messageId);
    }
}
