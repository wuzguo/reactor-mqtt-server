package com.study.iot.mqtt.transport.handler.connect;


import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.container.TopicContainer;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：取消订阅</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.UNSUBSCRIBE)
public class ServerUnSubscribeHandler implements ConnectCapable {

    @Autowired
    private ContainerManager containerManager;

    @Override
    public void handle(DisposableConnection disposable, MqttMessage mqttMessage) {
        log.info("unSubscribe message: {}, connection: {}", mqttMessage, disposable);

        MqttUnsubscribeMessage unsubscribeMessage = (MqttUnsubscribeMessage) mqttMessage;
        MqttUnsubAckMessage mqttUnsubAckMessage =
            MessageBuilder.buildUnsubAck(unsubscribeMessage.variableHeader().messageId());
        disposable.sendMessage(mqttUnsubAckMessage).subscribe();
        Optional.ofNullable(unsubscribeMessage.payload().topics())
            .ifPresent(topics -> topics.forEach(topic -> {
                TopicContainer topicContainer = containerManager.topic(CacheGroup.TOPIC);
                topicContainer.remove(topic, disposable);
            }));
    }
}
