package com.study.iot.mqtt.transport.handler.connect;


import com.study.iot.mqtt.common.utils.IdUtils;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.container.TopicContainer;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：发布释放（QoS 2，第二步）</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.PUBREL)
public class ServerPubRelHandler implements ConnectCapable {

    @Autowired
    private ContainerManager containerManager;

    @Override
    public void handle(DisposableConnection disposable, MqttMessage mqttMessage) {
        log.info("pubRel message: {}, connection: {}", mqttMessage, disposable);
        MqttFixedHeader header = mqttMessage.fixedHeader();
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        int messageId = variableHeader.messageId();
        // cancel replay rec
        disposable.cancelDisposable(messageId);
        MqttPubAckMessage mqttPubRecMessage = MessageBuilder.buildPubComp(messageId);
        // send comp
        disposable.sendMessage(mqttPubRecMessage).subscribe();
        disposable.getAndRemoveQos2Message(messageId)
            .ifPresent(transportMessage -> {
                TopicContainer topicContainer = containerManager.topic(CacheGroup.TOPIC);
                topicContainer.getConnections(transportMessage.getTopic())
                    .stream().map(disposableConnection -> (DisposableConnection) disposableConnection)
                    .filter(disposableConnection -> !disposable.equals(disposableConnection) && !disposableConnection.isDispose())
                    .forEach(disposableConnection -> {
                        int connMessageId = IdUtils.messageId();
                        MqttPublishMessage publishMessage = MessageBuilder.buildPub(false,
                            MqttQoS.valueOf(transportMessage.getQos()), header.isRetain(), connMessageId,
                            transportMessage.getTopic(), transportMessage.getCopyByteBuf());
                        disposableConnection.sendMessageRetry(connMessageId, publishMessage).subscribe();
                    });
            });
    }
}
