package com.study.iot.mqtt.transport.handler.publish;

import com.study.iot.mqtt.common.utils.IdUtil;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.container.TopicContainer;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
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
@PublishStrategyService(group = StrategyGroup.SERVER_PUBLISH, type = MqttQoS.AT_LEAST_ONCE)
public class ServerPublishAtLeastHandler implements PublishStrategyCapable {

    @Autowired
    private ContainerManager containerManager;

    @Override
    public void handle(DisposableConnection disposableConnection, MqttPublishMessage message, byte[] bytes) {
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        MqttFixedHeader header = message.fixedHeader();
        // back
        MqttPubAckMessage mqttPubAckMessage = MessageBuilder.buildPubAck(header.isDup(), header.qosLevel(),
            header.isRetain(), variableHeader.packetId());
        disposableConnection.sendMessage(mqttPubAckMessage).subscribe();
        TopicContainer topicContainer = (TopicContainer) containerManager.get(CacheGroup.TOPIC);
        topicContainer.getConnections(variableHeader.topicName())
            .stream().map(disposable -> (DisposableConnection) disposable)
            .filter(disposable -> !disposableConnection.equals(disposable) && !disposable.isDispose())
            .forEach(disposable -> {
                int messageId = IdUtil.messageId();
                MqttMessage mqttMessage = MessageBuilder.buildPub(false, header.qosLevel(), header.isRetain(),
                    messageId, variableHeader.topicName(), bytes);
                disposable.sendMessageRetry(messageId, mqttMessage).subscribe();
            });
    }
}
