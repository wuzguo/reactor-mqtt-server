package com.study.iot.mqtt.transport.client.handler.publish;

import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.PublishStrategyCapable;
import com.study.iot.mqtt.transport.strategy.PublishStrategyService;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.common.message.TransportMessage;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
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

@PublishStrategyService(group = StrategyGroup.CLIENT_PUBLISH, type = MqttQoS.EXACTLY_ONCE)
public class ClientPublishExactlyHandler implements PublishStrategyCapable {

    @Override
    public void handle(MqttPublishMessage message, DisposableConnection connection, byte[] bytes) {
        MqttFixedHeader header = message.fixedHeader();
        MqttPublishVariableHeader variableHeader = message.variableHeader();

        int messageId = variableHeader.packetId();
        MqttPubAckMessage mqttPubRecMessage = MessageBuilder.buildPubRec(messageId);
        // retry send rec
        connection.addDisposable(messageId, Mono.fromRunnable(() ->
            connection.sendMessage(MessageBuilder.buildPubRec(messageId)).subscribe())
            .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
        //  send rec
        connection.sendMessage(mqttPubRecMessage).subscribe();
        TransportMessage transportMessage = TransportMessage.builder().isRetain(header.isRetain())
            .isDup(false)
            .topic(variableHeader.topicName())
            .copyByteBuf(bytes)
            .qos(header.qosLevel().value())
            .build();
        connection.saveQos2Message(messageId, transportMessage);
    }
}
