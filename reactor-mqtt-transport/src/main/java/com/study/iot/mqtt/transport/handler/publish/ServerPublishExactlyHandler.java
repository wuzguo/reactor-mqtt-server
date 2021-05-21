package com.study.iot.mqtt.transport.handler.publish;

import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.common.message.TransportMessage;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
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
    public void handle(DisposableConnection disposableConnection, SessionMessage message) {
        //  send rec
        int messageId = message.getMessageId();
        MqttMessage mqttMessage = MessageBuilder.buildPubRec(messageId);
        disposableConnection.sendMessageRetry(messageId, mqttMessage);

        TransportMessage transportMessage = TransportMessage.builder().isRetain(message.getRetain())
            .isDup(false).topic(message.getTopic())
            .copyByteBuf(message.getCopyByteBuf())
            .qos(message.getQos())
            .build();
        disposableConnection.saveQos2Message(messageId, transportMessage);
    }
}
