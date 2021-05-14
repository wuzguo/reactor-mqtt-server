package com.study.iot.mqtt.transport.client.connection;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.utils.IdUtil;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ClientProperties.ConnectOptions;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
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

    public ClientConnection(DisposableConnection connection, ClientProperties properties,
        ClientMessageRouter messageRouter) {
        this.connection = connection;
        this.clientMessageRouter = messageRouter;
        this.init(properties);
    }

    @Override
    public void init(ClientProperties properties) {
        ConnectOptions connectOptions = properties.getOptions();
        Disposable disposable = Mono.fromRunnable(() -> connection.sendMessage(MessageBuilder.buildConnect(
            connectOptions.getClientId(),
            connectOptions.getWillTopic(),
            connectOptions.getWillMessage(),
            connectOptions.getUserName(),
            connectOptions.getPassword(),
            connectOptions.getHasUserName(),
            connectOptions.getHasPassword(),
            connectOptions.getHasWillFlag(),
            connectOptions.getWillQos().value(),
            properties.getKeepAliveSeconds()
        )).subscribe()).delaySubscription(Duration.ofSeconds(10)).repeat().subscribe();

        connection.sendMessage(MessageBuilder.buildConnect(
            connectOptions.getClientId(),
            connectOptions.getWillTopic(),
            connectOptions.getWillMessage(),
            connectOptions.getUserName(),
            connectOptions.getPassword(),
            connectOptions.getHasUserName(),
            connectOptions.getHasPassword(),
            connectOptions.getHasWillFlag(),
            connectOptions.getWillQos().value(),
            properties.getKeepAliveSeconds()
        )).doOnError(throwable -> log.error(throwable.getMessage())).subscribe();
        connection.getConnection().channel().attr(AttributeKeys.closeConnection).set(disposable);
        // 发送心跳
        connection.getConnection().onWriteIdle(properties.getKeepAliveSeconds(), () -> {
            MqttMessage message = new MqttMessage(
                new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0));
            connection.sendMessage(message).subscribe();
        });
        // 发送心跳
        connection.getConnection().onReadIdle(properties.getKeepAliveSeconds() * 2L, () -> {
            MqttMessage message = new MqttMessage(
                new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0));
            connection.sendMessage(message).subscribe();
        });
        connection.getConnection().onDispose(() -> properties.getOnClose().run());

        // 各种策略模式处理
        connection.receive(MqttMessage.class)
            .subscribe(message -> clientMessageRouter.handle(message, connection));

        connection.getConnection().channel().attr(AttributeKeys.clientConnection).set(this);
        List<MqttTopicSubscription> mqttTopicSubscriptions = connection.getTopics().stream()
            .map(topicFilter -> new MqttTopicSubscription(topicFilter, MqttQoS.AT_MOST_ONCE))
            .collect(Collectors.toList());

        if (mqttTopicSubscriptions.size() > 0) {
            int messageId = IdUtil.messageId();
            MqttSubscribeMessage mqttMessage = MessageBuilder.buildSub(messageId, mqttTopicSubscriptions);
            connection.sendMessageRetry(messageId, mqttMessage);
        }
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, boolean retained, MqttQoS mqttQoS) {
        switch (mqttQoS) {
            case AT_MOST_ONCE:
                return connection.sendMessage(
                    MessageBuilder.buildPub(false, MqttQoS.AT_MOST_ONCE, retained, 1, topic, message));
            case EXACTLY_ONCE:
            case AT_LEAST_ONCE:
                int messageId = IdUtil.messageId();
                return connection.sendMessageRetry(messageId,
                    MessageBuilder.buildPub(false, mqttQoS, retained, messageId, topic, message));
            default:
                return Mono.empty();
        }
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message) {
        return pub(topic, message, false, MqttQoS.AT_MOST_ONCE);
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, MqttQoS mqttQoS) {
        return pub(topic, message, false, mqttQoS);
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, boolean retained) {
        return pub(topic, message, retained, MqttQoS.AT_MOST_ONCE);
    }

    @Override
    public Mono<Void> sub(String... subMessages) {
        topics.addAll(Arrays.asList(subMessages));
        List<MqttTopicSubscription> topicSubscriptions = Arrays.stream(subMessages)
            .map(topicFilter -> new MqttTopicSubscription(topicFilter, MqttQoS.AT_MOST_ONCE))
            .collect(Collectors.toList());
        // retry
        int messageId = IdUtil.messageId();
        return connection.sendMessageRetry(messageId, MessageBuilder.buildSub(messageId, topicSubscriptions));
    }

    @Override
    public Mono<Void> unsub(List<String> topics) {
        this.topics.removeAll(topics);
        // retry
        int messageId = IdUtil.messageId();
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
