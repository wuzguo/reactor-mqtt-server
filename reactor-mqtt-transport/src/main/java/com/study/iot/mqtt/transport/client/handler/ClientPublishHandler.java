package com.study.iot.mqtt.transport.client.handler;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.common.message.TransportMessage;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.util.CharsetUtil;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:21
 */

@Slf4j
@StrategyService(group = StrategyGroup.CLIENT, type = MqttMessageType.PUBLISH)
public class ClientPublishHandler implements StrategyCapable {

    @Override
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("client Publish message: {}, connection: {}", message, connection);

        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) message;
        MqttPublishVariableHeader variableHeader = mqttPublishMessage.variableHeader();
        MqttFixedHeader header = message.fixedHeader();
        ByteBuf byteBuf = mqttPublishMessage.payload();

        byte[] bytes = copyByteBuf(byteBuf);
        switch (header.qosLevel()) {
            case AT_MOST_ONCE:
                log.info("client publish topic: {}, message: {}", variableHeader.topicName(), new String(bytes,
                    CharsetUtil.UTF_8));
                break;
            case AT_LEAST_ONCE:
                log.info("client publish topic: {}, message: {}", variableHeader.topicName(), new String(bytes,
                    CharsetUtil.UTF_8));
                MqttPubAckMessage mqttPubAckMessage = MessageBuilder.buildPubAck(header.isDup(), header.qosLevel(),
                    header.isRetain(), variableHeader.packetId()); // back
                connection.sendMessage(mqttPubAckMessage).subscribe();
                break;
            case EXACTLY_ONCE:
                int messageId = variableHeader.packetId();
                MqttPubAckMessage mqttPubRecMessage = MessageBuilder.buildPubRec(messageId);
                connection.addDisposable(messageId, Mono.fromRunnable(() ->
                    connection.sendMessage(MessageBuilder.buildPubRec(messageId)).subscribe()) // retry send rec
                    .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
                connection.sendMessage(mqttPubRecMessage).subscribe();  //  send rec
                TransportMessage transportMessage = TransportMessage.builder().isRetain(header.isRetain())
                    .isDup(false)
                    .topic(variableHeader.topicName())
                    .copyByteBuf(bytes)
                    .qos(header.qosLevel().value())
                    .build();
                connection.saveQos2Message(messageId, transportMessage);
                break;
            case FAILURE:
                log.error(" publish FAILURE {} {} ", header, variableHeader);
                break;
            default:
                break;
        }
    }

    private byte[] copyByteBuf(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
