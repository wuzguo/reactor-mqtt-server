package com.study.iot.mqtt.transport.handler.connect;


import com.study.iot.mqtt.akka.event.SubscribeEvent;
import com.study.iot.mqtt.common.message.RetainMessage;
import com.study.iot.mqtt.common.utils.IdUtils;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.session.config.InstanceUtil;
import com.study.iot.mqtt.session.service.EventService;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.transport.annotation.MqttMetric;
import com.study.iot.mqtt.transport.constant.MetricMatterName;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：订阅主题</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.SUBSCRIBE)
public class ServerSubscribeHandler implements ConnectCapable {

    @Autowired
    private ContainerManager containerManager;

    @Autowired
    private EventService eventService;

    @Autowired
    private InstanceUtil instanceUtil;

    @Override
    @MqttMetric(MetricMatterName.TOTAL_RECEIVE_COUNT)
    public void handle(DisposableConnection disposable, MqttMessage mqttMessage) {
        log.info("subscribe message: {}", mqttMessage);
        MqttFixedHeader header = mqttMessage.fixedHeader();
        MqttSubscribeMessage subscribeMessage = (MqttSubscribeMessage) mqttMessage;

        List<Integer> qosLevels = subscribeMessage.payload().topicSubscriptions().stream()
            .map(topicSubscription -> topicSubscription.qualityOfService().value()).collect(Collectors.toList());
        // 消息ID
        int messageId = subscribeMessage.variableHeader().messageId();
        MqttSubAckMessage mqttSubAckMessage = MessageBuilder.buildSubAck(messageId, qosLevels);
        disposable.sendMessage(mqttSubAckMessage).subscribe();
        List<MqttTopicSubscription> subscriptions = subscribeMessage.payload().topicSubscriptions();
        // 获取订阅的Topic名称
        Set<String> topicNames = subscriptions.stream().map(MqttTopicSubscription::topicName).collect(Collectors.toSet());
        // 获取当前客户端ID
        String identity = disposable.getConnection().channel().attr(AttributeKeys.identity).get();

        // 发布订阅
        SubscribeEvent event = new SubscribeEvent(this, IdUtils.idGen());
        event.setTopicNames(topicNames);
        event.setInstanceId(instanceUtil.getInstanceId());
        event.setIdentity(identity);
        eventService.tellEvent(event);
        // 遍历
        subscriptions.forEach(topicSubscription -> {
            String topicName = topicSubscription.topicName();
            // 存储本地的订阅主题
            containerManager.topic(CacheGroup.TOPIC).add(topicName, disposable);
            // 保存Topic和客户端ID的对应关系
            containerManager.take(CacheGroup.ID_TOPIC).add(topicName, identity);
            Optional.ofNullable(containerManager.take(CacheGroup.MESSAGE).get(topicName)).ifPresent(message -> {
                RetainMessage retainMessage = (RetainMessage) message;
                if (retainMessage.getQos() == 0) {
                    MqttPublishMessage publishMessage = MessageBuilder.buildPub(retainMessage.getDup(),
                        MqttQoS.valueOf(retainMessage.getQos()), retainMessage.getRetain(), 1,
                        retainMessage.getTopic(), retainMessage.getCopyByteBuf());
                    disposable.sendMessage(publishMessage).subscribe();
                } else {
                    int connMessageId = IdUtils.messageId();
                    // retry
                    MqttPublishMessage publishMessage = MessageBuilder.buildPub(true, header.qosLevel(),
                        header.isRetain(), connMessageId, retainMessage.getTopic(), retainMessage.getCopyByteBuf());
                    disposable.sendMessageRetry(connMessageId, publishMessage);
                }
            });
        });
    }
}
