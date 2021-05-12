package com.study.iot.mqtt.transport.client.connection;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 10:44
 */

@Slf4j
public class ClientConnection implements ClientSession {

    private final DisposableConnection connection;

    private final ClientMessageRouter clientMessageRouter;

    private final List<String> topics = Lists.newArrayList();

    public ClientConnection(DisposableConnection connection, ClientConfiguration configuration,
        ClientMessageRouter messageRouter) {
        this.connection = connection;
        this.clientMessageRouter = messageRouter;
        this.init(configuration);
    }

    @Override
    public void init(ClientConfiguration configuration) {
        ClientConfiguration.Options options = configuration.getOptions();
        Disposable disposable = Mono.fromRunnable(() -> connection.sendMessage(MessageBuilder.buildConnect(
            options.getClientId(),
            options.getWillTopic(),
            options.getWillMessage(),
            options.getUserName(),
            options.getPassword(),
            options.getHasUserName(),
            options.getHasPassword(),
            options.getHasWillFlag(),
            options.getWillQos().value(),
            configuration.getHeart()
        )).subscribe()).delaySubscription(Duration.ofSeconds(10)).repeat().subscribe();

        connection.sendMessage(MessageBuilder.buildConnect(
            options.getClientId(),
            options.getWillTopic(),
            options.getWillMessage(),
            options.getUserName(),
            options.getPassword(),
            options.getHasUserName(),
            options.getHasPassword(),
            options.getHasWillFlag(),
            options.getWillQos().value(),
            configuration.getHeart()
        )).doOnError(throwable -> log.error(throwable.getMessage())).subscribe();
        connection.getConnection().channel().attr(AttributeKeys.closeConnection).set(disposable);
        // 发送心跳
        connection.getConnection().onWriteIdle(configuration.getHeart(), () -> {
            MqttMessage message = new MqttMessage(
                new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0));
            connection.sendMessage(message).subscribe();
        });
        // 发送心跳
        connection.getConnection().onReadIdle(configuration.getHeart() * 2L, () -> {
            MqttMessage message = new MqttMessage(
                new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0));
            connection.sendMessage(message).subscribe();
        });
        connection.getConnection().onDispose(() -> configuration.getOnClose().run());

        NettyInbound inbound = connection.getInbound();
        // 各种策略模式处理
        inbound.receiveObject().cast(MqttMessage.class)
            .subscribe(message -> clientMessageRouter.handle(message, connection));

        connection.getConnection().channel().attr(AttributeKeys.clientConnection).set(this);
        List<MqttTopicSubscription> mqttTopicSubscriptions = connection.getTopics().stream()
            .map(topicFilter -> new MqttTopicSubscription(topicFilter, MqttQoS.AT_MOST_ONCE))
            .collect(Collectors.toList());

        if (mqttTopicSubscriptions.size() > 0) {
            int messageId = connection.messageId();
            MqttSubscribeMessage mqttMessage = MessageBuilder.buildSub(messageId, mqttTopicSubscriptions);
            connection.sendMessageRetry(messageId, mqttMessage);
        }
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, boolean retained, int qos) {
        int messageId = qos == 0 ? 1 : connection.messageId();
        MqttQoS mqttQoS = MqttQoS.valueOf(qos);
        switch (mqttQoS) {
            case AT_MOST_ONCE:
                return connection.sendMessage(
                    MessageBuilder.buildPub(false, MqttQoS.AT_MOST_ONCE, retained, messageId, topic, message));
            case EXACTLY_ONCE:
            case AT_LEAST_ONCE:
                return connection.sendMessageRetry(messageId,
                    MessageBuilder.buildPub(false, mqttQoS, retained, messageId, topic, message));
            default:
                return Mono.empty();
        }
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message) {
        return pub(topic, message, false, 0);
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, int qos) {
        return pub(topic, message, false, qos);
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, boolean retained) {
        return pub(topic, message, retained, 0);
    }

    @Override
    public Mono<Void> sub(String... subMessages) {
        topics.addAll(Arrays.asList(subMessages));
        List<MqttTopicSubscription> topicSubscriptions = Arrays.stream(subMessages)
            .map(topicFilter -> new MqttTopicSubscription(topicFilter, MqttQoS.AT_MOST_ONCE))
            .collect(Collectors.toList());
        // retry
        int messageId = connection.messageId();
        return connection.sendMessageRetry(messageId, MessageBuilder.buildSub(messageId, topicSubscriptions));
    }

    @Override
    public Mono<Void> unsub(List<String> topics) {
        this.topics.removeAll(topics);
        // retry
        int messageId = connection.messageId();
        return connection.sendMessageRetry(messageId, MessageBuilder.buildUnSub(messageId, topics));
    }

    @Override
    public Mono<Void> unsub() {
        return unsub(this.topics);
    }

    @Override
    public void dispose() {
        connection.dispose();
    }
}
