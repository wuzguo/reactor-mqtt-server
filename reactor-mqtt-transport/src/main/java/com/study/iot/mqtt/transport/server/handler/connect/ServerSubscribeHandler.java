package com.study.iot.mqtt.transport.server.handler.connect;


import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
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
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

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

    @Override
    @MqttMetric(name = MetricMatterName.TOTAL_RECEIVE_COUNT)
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("server Subscribe message: {}, connection: {}", message, connection);
        MqttFixedHeader header = message.fixedHeader();
        MqttSubscribeMessage subscribeMessage = (MqttSubscribeMessage) message;

        List<Integer> qosLevels = subscribeMessage.payload().topicSubscriptions().stream()
            .map(topicSubscription -> topicSubscription.qualityOfService().value()).collect(Collectors.toList());
        // 消息ID
        int messageId = subscribeMessage.variableHeader().messageId();
        MqttSubAckMessage mqttSubAckMessage = MessageBuilder.buildSubAck(messageId, qosLevels);
        connection.sendMessage(mqttSubAckMessage).subscribe();
        subscribeMessage.payload().topicSubscriptions().forEach(topicSubscription -> {
            String topicName = topicSubscription.topicName();
            cacheManager.topic().add(topicName, connection);
            Optional.ofNullable(cacheManager.message().getRetain(topicName)).ifPresent(retainMessage -> {
                if (retainMessage.getQos() == 0) {
                    MqttPublishMessage mqttMessage = MessageBuilder.buildPub(retainMessage.getIsDup(),
                        MqttQoS.valueOf(retainMessage.getQos()),
                        retainMessage.getIsRetain(), 1, retainMessage.getTopic(), retainMessage.getCopyByteBuf());
                    connection.sendMessage(mqttMessage).subscribe();
                } else {
                    int connMessageId = connection.messageId();
                    // retry
                    connection.addDisposable(connMessageId, Mono.fromRunnable(() -> {
                        MqttPublishMessage mqttMessage = MessageBuilder.buildPub(true, header.qosLevel(),
                            header.isRetain(), connMessageId, retainMessage.getTopic(),
                            retainMessage.getCopyByteBuf());
                        connection.sendMessage(mqttMessage).subscribe();
                    }).delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
                    // pub
                    MqttPublishMessage mqttMessage = MessageBuilder.buildPub(false, header.qosLevel(),
                        header.isRetain(), connMessageId, retainMessage.getTopic(), retainMessage.getCopyByteBuf());
                    connection.sendMessage(mqttMessage).subscribe();
                }
            });
        });
    }
}
