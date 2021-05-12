package com.study.iot.mqtt.transport.server.handler.publish;

import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.common.message.TransportMessage;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:54
 */

@PublishStrategyService(group = StrategyGroup.SERVER_PUBLISH, type = MqttQoS.EXACTLY_ONCE)
public class ServerPublishExactlyHandler implements PublishStrategyCapable {

    @Override
    public void handle(MqttPublishMessage message, DisposableConnection connection, byte[] bytes) {
        MqttFixedHeader header = message.fixedHeader();
        MqttPublishVariableHeader variableHeader = message.variableHeader();

        //  send rec
        int messageId = variableHeader.packetId();
        MqttMessage mqttMessage = MessageBuilder.buildPubRec(messageId);
        connection.sendMessageRetry(messageId, mqttMessage);

        TransportMessage transportMessage = TransportMessage.builder().isRetain(header.isRetain())
            .isDup(false).topic(variableHeader.topicName())
            .copyByteBuf(bytes)
            .qos(header.qosLevel().value())
            .build();
        connection.saveQos2Message(messageId, transportMessage);
    }
}
