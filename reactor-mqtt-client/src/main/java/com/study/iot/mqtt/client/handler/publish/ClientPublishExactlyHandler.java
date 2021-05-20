package com.study.iot.mqtt.client.handler.publish;

import com.study.iot.mqtt.client.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.client.strategy.PublishStrategyService;
import com.study.iot.mqtt.client.strategy.StrategyGroup;
import com.study.iot.mqtt.common.message.TransportMessage;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
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

@PublishStrategyService(group = StrategyGroup.CLIENT_PUBLISH, type = MqttQoS.EXACTLY_ONCE)
public class ClientPublishExactlyHandler implements PublishStrategyCapable {

    @Override
    public void handle(DisposableConnection connection, MqttPublishMessage message, byte[] bytes) {
        MqttFixedHeader header = message.fixedHeader();
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        // retry send rec
        int messageId = variableHeader.packetId();
        connection.sendMessageRetry(messageId, MessageBuilder.buildPubRec(messageId));

        TransportMessage transportMessage = TransportMessage.builder().isRetain(header.isRetain())
            .isDup(false)
            .topic(variableHeader.topicName())
            .copyByteBuf(bytes)
            .qos(header.qosLevel().value())
            .build();
        connection.saveQos2Message(messageId, transportMessage);
    }
}
