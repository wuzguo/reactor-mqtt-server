package com.study.iot.mqtt.transport.server.handler.connect;


import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import java.util.Optional;
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
@StrategyService(group = StrategyGroup.SERVER, type = MqttMessageType.UNSUBSCRIBE)
public class ServerUnSubscribeHandler implements StrategyCapable {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void handle(DisposableConnection disposableConnection, MqttMessage message) {
        log.info("server UnSubscribe message: {}, connection: {}", message, disposableConnection);

        MqttUnsubscribeMessage unsubscribeMessage = (MqttUnsubscribeMessage) message;
        MqttUnsubAckMessage mqttUnsubAckMessage =
            MessageBuilder.buildUnsubAck(unsubscribeMessage.variableHeader().messageId());
        disposableConnection.sendMessage(mqttUnsubAckMessage).subscribe();
        Optional.ofNullable(unsubscribeMessage.payload().topics())
            .ifPresent(topics -> topics.forEach(topic -> cacheManager.topic().remove(topic, disposableConnection)));
    }
}
