package com.study.iot.mqtt.transport.handler.publish;

import com.study.iot.mqtt.common.message.SessionMessage;
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
 * @date 2021/5/7 13:54
 */

@Slf4j
@StrategyService(group = StrategyGroup.PUBLISH, type = StrategyEnum.AT_MOST_ONCE)
public class ServerPublishAtMostHandler implements PublishCapable {

    @Autowired
    private ContainerManager containerManager;

    @Override
    public void handle(DisposableConnection disposableConnection, SessionMessage message) {
        log.info("publish at_most_once message: {}", message);
        // 过滤掉本身 已经关闭的dispose
        TopicContainer topicContainer = containerManager.topic(CacheGroup.TOPIC);
        Optional.ofNullable(topicContainer.getConnections(message.getTopic())).orElse(Collections.emptyList())
            .stream().map(disposable -> (DisposableConnection) disposable)
            .filter(disposable -> !disposable.isDispose())
            .forEach(disposable -> {
                MqttMessage mqttMessage = MessageBuilder.buildPub(false, MqttQoS.valueOf(message.getQos()),
                    message.getRetain(), IdUtils.messageId(), message.getTopic(), message.getCopyByteBuf());
                disposable.sendMessage(mqttMessage).subscribe();
            });
    }
}
