package com.study.iot.mqtt.transport.handler.connect;


import com.study.iot.mqtt.akka.event.SubscribeEvent;
import com.study.iot.mqtt.store.manager.CacheManager;
import com.study.iot.mqtt.common.utils.IdUtil;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.session.service.EventService;
import com.study.iot.mqtt.transport.annotation.MqttMetric;
import com.study.iot.mqtt.transport.constant.MetricMatterName;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.SERVER, type = MqttMessageType.SUBSCRIBE)
public class ServerSubscribeHandler implements StrategyCapable {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EventService eventService;

    @Override
    @MqttMetric(MetricMatterName.TOTAL_RECEIVE_COUNT)
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("server Subscribe message: {}, connection: {}", message, disposableConnection);
        MqttFixedHeader header = message.fixedHeader();
        MqttSubscribeMessage subscribeMessage = (MqttSubscribeMessage) message;

        SubscribeEvent event = new SubscribeEvent(this, IdUtil.idGen());
        event.setTopic("/session/123456");
        event.setInstanceId("123456");
        event.setClientIdentity("Identity");
        eventService.tellEvent(event);

        List<Integer> qosLevels = subscribeMessage.payload().topicSubscriptions().stream()
            .map(topicSubscription -> topicSubscription.qualityOfService().value()).collect(Collectors.toList());
        // 消息ID
        int messageId = subscribeMessage.variableHeader().messageId();
        MqttSubAckMessage mqttSubAckMessage = MessageBuilder.buildSubAck(messageId, qosLevels);
        disposableConnection.sendMessage(mqttSubAckMessage).subscribe();
        subscribeMessage.payload().topicSubscriptions().forEach(topicSubscription -> {
            String topicName = topicSubscription.topicName();
            cacheManager.topic().add(topicName, disposableConnection);
            Optional.ofNullable(cacheManager.message().getRetain(topicName)).ifPresent(retainMessage -> {
                if (retainMessage.getQos() == 0) {
                    MqttPublishMessage mqttMessage = MessageBuilder.buildPub(retainMessage.getIsDup(),
                        MqttQoS.valueOf(retainMessage.getQos()), retainMessage.getIsRetain(), 1,
                        retainMessage.getTopic(), retainMessage.getCopyByteBuf());
                    disposableConnection.sendMessage(mqttMessage).subscribe();
                } else {
                    int connMessageId = IdUtil.messageId();
                    // retry
                    MqttPublishMessage mqttMessage = MessageBuilder.buildPub(true, header.qosLevel(),
                        header.isRetain(), connMessageId, retainMessage.getTopic(), retainMessage.getCopyByteBuf());
                    disposableConnection.sendMessageRetry(connMessageId, mqttMessage);
                }
            });
        });
    }
}
