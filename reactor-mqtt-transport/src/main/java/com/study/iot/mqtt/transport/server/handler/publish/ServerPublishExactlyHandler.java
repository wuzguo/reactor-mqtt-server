package com.study.iot.mqtt.transport.server.handler.publish;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.common.message.TransportMessage;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.QosStrategyService;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.time.Duration;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 13:54
 */

@QosStrategyService(group = StrategyGroup.SERVER_PUBLISH, type = MqttQoS.EXACTLY_ONCE)
public class ServerPublishExactlyHandler implements PublishStrategyCapable {

    @Override
    public void handle(MqttPublishMessage message, DisposableConnection connection, byte[] bytes) {
        MqttFixedHeader header = message.fixedHeader();
        MqttPublishVariableHeader variableHeader = message.variableHeader();

        int messageId = variableHeader.packetId();
        //  send rec
        MqttPubAckMessage mqttPubRecMessage = MessageBuilder.buildPubRec(messageId);
        connection.sendMessage(mqttPubRecMessage).subscribe();
        // retry
        connection.addDisposable(messageId, Mono.fromRunnable(() ->
            connection.sendMessage(MessageBuilder.buildPubRel(messageId)).subscribe())
            .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
        TransportMessage transportMessage = TransportMessage.builder().retain(header.isRetain())
            .dup(false)
            .topicName(variableHeader.topicName())
            .message(bytes)
            .qos(header.qosLevel().value())
            .build();
        connection.saveQos2Message(messageId, transportMessage);
    }
}
