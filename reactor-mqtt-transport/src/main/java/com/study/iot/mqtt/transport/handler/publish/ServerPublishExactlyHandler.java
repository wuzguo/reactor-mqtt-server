package com.study.iot.mqtt.transport.handler.publish;

import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.common.message.TransportMessage;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:54
 */

@Slf4j
@StrategyService(group = StrategyGroup.PUBLISH, type = StrategyEnum.EXACTLY_ONCE)
public class ServerPublishExactlyHandler implements PublishCapable {

    @Override
    public void handle(DisposableConnection disposableConnection, SessionMessage message) {
        log.info("publish exactly_once message: {}", message);
        //  send rec
        int messageId = message.getMessageId();
        MqttMessage mqttMessage = MessageBuilder.buildPubRec(messageId);
        // 发送消息
        disposableConnection.sendMessageRetry(messageId, mqttMessage);
        // 保存消息
        TransportMessage transportMessage = TransportMessage.builder().isRetain(message.getRetain())
            .isDup(false).topic(message.getTopic())
            .copyByteBuf(message.getCopyByteBuf())
            .qos(message.getQos())
            .build();
        disposableConnection.saveQos2Message(messageId, transportMessage);
    }
}
