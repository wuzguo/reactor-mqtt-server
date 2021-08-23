package com.study.iot.mqtt.transport.handler.publish;

import com.study.iot.mqtt.common.domain.SessionMessage;
import com.study.iot.mqtt.common.utils.IdUtils;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.container.TopicContainer;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:53
 */

@Slf4j
@StrategyService(group = StrategyGroup.PUBLISH, type = StrategyEnum.AT_LEAST_ONCE)
public class ServerPublishAtLeastHandler implements PublishCapable {

    @Autowired
    private ContainerManager containerManager;

    @Override
    public void handle(DisposableConnection disposableConnection, SessionMessage message) {
        log.info("publish at_last_once message: {}", message);
        // back
        MqttQoS mqttQoS = MqttQoS.valueOf(message.getQos());

        MqttPubAckMessage mqttPubAckMessage = MessageBuilder.buildPubAck(message.getDup(), mqttQoS,
            message.getRetain(), message.getMessageId());
        disposableConnection.sendMessage(mqttPubAckMessage).subscribe();
        TopicContainer topicContainer = containerManager.topic(CacheGroup.TOPIC);
        Optional.ofNullable(topicContainer.getConnections(message.getTopic())).orElse(Collections.emptyList())
            .stream().map(disposable -> (DisposableConnection) disposable)
            .filter(disposable -> !disposable.isDispose())
            .forEach(disposable -> {
                int messageId = IdUtils.messageId();
                MqttMessage mqttMessage = MessageBuilder.buildPub(false, mqttQoS, message.getRetain(),
                    messageId, message.getTopic(), message.getCopyByteBuf());
                disposable.sendMessageRetry(messageId, mqttMessage).subscribe();
            });
    }
}
