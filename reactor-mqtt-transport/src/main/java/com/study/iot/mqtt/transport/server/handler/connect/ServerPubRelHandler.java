package com.study.iot.mqtt.transport.server.handler.connect;


import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
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
@StrategyService(group = StrategyGroup.SERVER, type = MqttMessageType.PUBREL)
public class ServerPubRelHandler implements StrategyCapable {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("server PubRel message: {}, connection: {}", message, connection);
        MqttFixedHeader header = message.fixedHeader();
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
        int messageId = variableHeader.messageId();
        // cancel replay rec
        connection.cancelDisposable(messageId);
        MqttPubAckMessage mqttPubRecMessage = MessageBuilder.buildPubComp(messageId);
        // send comp
        connection.sendMessage(mqttPubRecMessage).subscribe();
        connection.getAndRemoveQos2Message(messageId)
            .ifPresent(transportMessage -> cacheManager.topic().getConnections(transportMessage.getTopicName())
                .stream().filter(disposable -> !connection.equals(disposable) && !disposable.isDispose())
                .forEach(disposable -> {
                    int id = connection.messageId();
                    MqttPublishMessage mqttMessage = MessageBuilder.buildPub(false,
                        MqttQoS.valueOf(transportMessage.getQos()), header.isRetain(), id,
                        transportMessage.getTopicName(), transportMessage.getMessage());
                    disposable.sendMessageRetry(id, mqttMessage).subscribe();
                }));
    }
}
