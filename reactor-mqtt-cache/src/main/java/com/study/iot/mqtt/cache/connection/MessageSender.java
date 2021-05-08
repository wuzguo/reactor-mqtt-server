package com.study.iot.mqtt.cache.connection;

import com.study.iot.mqtt.cache.message.IMessageIdService;
import com.study.iot.mqtt.common.connection.MessageBuilder;
import com.study.iot.mqtt.common.message.TransportMessage;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/8 14:21
 */

@Component
public class MessageSender {

    private final ConcurrentHashMap<Integer, Disposable> disposableMessageHashMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, TransportMessage> qos2Message = new ConcurrentHashMap<>();

    @Autowired
    private IMessageIdService idService;

    private void addRetrySubscriber(NettyOutbound outbound, int messageId, MqttMessage mqttMessage) {
        this.addDisposable(messageId, Mono.fromRunnable(() ->
                outbound.sendObject(mqttMessage).then())
                .delaySubscription(Duration.ofSeconds(10)).repeat(5).doFinally(e -> removeQos2Message(messageId))
                .subscribe());
    }

    public void addDisposable(Integer messageId, Disposable disposable) {
        disposableMessageHashMap.put(messageId, disposable);
    }

    public void cancelDisposable(Integer messageId) {
        Optional.ofNullable(disposableMessageHashMap.get(messageId))
                .ifPresent(Disposable::dispose);
        disposableMessageHashMap.remove(messageId);
    }


    private Mono<Void> sendMessage(NettyOutbound outbound, MqttMessage message) {
        return outbound.sendObject(message).then();
    }

    public Mono<Void> sendPublishMessage(NettyOutbound outbound, MqttPublishMessage message) {
        return sendMessage(outbound, message);
    }

    public Mono<Void> sendPublishMessage(NettyOutbound outbound, MqttQoS qos, boolean isRetain, String topic, byte[] message) {
        MqttPublishMessage mqttPublishMessage = MessageBuilder.buildPub(false, qos, isRetain, idService.next(),
                topic, Unpooled.wrappedBuffer(message));
        return sendMessage(outbound, mqttPublishMessage);
    }

    public Mono<Void> sendPublishMessage(NettyOutbound outbound, MqttMessageType mqttMessageType, MqttQoS qos, boolean isDup, boolean isRetain,
                                         String topic, Integer messageId, byte[] message) {
        if (messageId == null) {
            messageId = idService.next();
        }
        MqttPublishMessage mqttPublishMessage = MessageBuilder.buildPub(isDup, qos, isRetain, messageId, topic, Unpooled.wrappedBuffer(message));
        return sendMessage(outbound, mqttPublishMessage);
    }


    public Mono<Void> sendPublishMessageRetry(NettyOutbound outbound, MqttMessageType mqttMessageType, MqttQoS qos, boolean isRetain,
                                              String topic, Integer messageId, byte[] message) {
        MqttPublishMessage mqttPublishMessage = MessageBuilder.buildPub(false, qos, false, messageId, topic, Unpooled.wrappedBuffer(message));
        MqttPublishMessage retryMqttPublishMessage = MessageBuilder.buildPub(false, qos, true, messageId, topic, Unpooled.wrappedBuffer(message));
        addRetrySubscriber(outbound, messageId, retryMqttPublishMessage);
        return sendMessage(outbound, mqttPublishMessage);
    }

    public Mono<Void> sendPublishMessageRetry(NettyOutbound outbound, MqttQoS qos, boolean isRetain, String topic, byte[] message) {
        int messageId = idService.next();
        MqttPublishMessage mqttPublishMessage = MessageBuilder.buildPub(false, qos, isRetain, messageId, topic, Unpooled.wrappedBuffer(message));
        MqttPublishMessage retryMqttPublishMessage = MessageBuilder.buildPub(false, qos, true, messageId, topic, Unpooled.wrappedBuffer(message));
        addRetrySubscriber(outbound, messageId, retryMqttPublishMessage);
        return sendMessage(outbound, mqttPublishMessage);
    }

    public Mono<Void> sendPubAckMessage(NettyOutbound outbound, MqttMessageType mqttMessageType, boolean isRetain, boolean isDup,
                                        int messageId) {
        MqttPubAckMessage pubAckMessage = MessageBuilder.buildPubAck(isDup, AT_MOST_ONCE, isRetain, messageId);
        return sendMessage(outbound, pubAckMessage);
    }

    public Mono<Void> sendPubAckMessage(NettyOutbound outbound, MqttMessageType mqttMessageType, MqttQoS qos, boolean isRetain, boolean isDup,
                                        int messageId) {
        MqttPubAckMessage pubAckMessage = MessageBuilder.buildPubAck(isDup, qos,  isRetain, messageId);
        return sendMessage(outbound, pubAckMessage);
    }

    public Mono<Void> sendPubAckMessageRetry(NettyOutbound outbound, MqttMessageType mqttMessageType, boolean isRetain, boolean isDup,
                                             int messageId) {
        MqttPubAckMessage pubAckMessage = MessageBuilder.buildPubAck(isDup, AT_MOST_ONCE, isRetain, messageId);
        addRetrySubscriber(outbound, messageId, pubAckMessage);
        return sendMessage(outbound, pubAckMessage);
    }


    public Mono<Void> sendConnAckMessage(NettyOutbound outbound, MqttConnectReturnCode mqttConnectReturnCode, boolean sessionPresent) {
        MqttConnAckMessage connAckMessage = MessageBuilder.buildConnAck(mqttConnectReturnCode, sessionPresent);
        return sendMessage(outbound, connAckMessage);
    }

    public Mono<Void> sendPingResp(NettyOutbound outbound) {
        MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);
        return sendMessage(outbound, pingRespMessage);
    }

    public Mono<Void> sendPubRecMessageRetry(NettyOutbound outbound, Integer messageId) {
        MqttPubAckMessage ackMessage = MessageBuilder.buildPubRec(messageId);
        addRetrySubscriber(outbound, messageId, ackMessage);
        return sendMessage(outbound, ackMessage);
    }

    public Mono<Void> sendSubAck(NettyOutbound outbound, int messageId, List<Integer> qos) {
        MqttSubAckMessage mqttSubAckMessage = MessageBuilder.buildSubAck(messageId, qos);
        return sendMessage(outbound, mqttSubAckMessage);
    }

    public Mono<Void> sendFailureSubAck(NettyOutbound outbound, int messageId, List<Integer> qos) {
        MqttSubAckMessage mqttSubAckMessage = MessageBuilder.buildFailureSubAck(messageId, qos);
        return sendMessage(outbound, mqttSubAckMessage);
    }

    public Mono<Void> sendUnsubAck(NettyOutbound outbound, int messageId) {
        MqttUnsubAckMessage mqttUnsubAckMessage = MessageBuilder.buildUnsubAck(messageId);
        return sendMessage(outbound, mqttUnsubAckMessage);
    }

    public void saveQos2Message(Integer messageId, TransportMessage message) {
        qos2Message.put(messageId, message);
    }

    public void removeQos2Message(Integer messageId) {
        qos2Message.remove(messageId);
    }

    public Optional<TransportMessage> getAndRemoveQos2Message(Integer messageId) {
        TransportMessage message = qos2Message.get(messageId);
        qos2Message.remove(messageId);
        return Optional.ofNullable(message);
    }
}
