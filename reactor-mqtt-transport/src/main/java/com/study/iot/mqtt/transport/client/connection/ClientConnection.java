package com.study.iot.mqtt.transport.client.connection;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.utils.CollectionUtil;
import com.study.iot.mqtt.common.utils.IdUtil;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ClientProperties.ConnectOptions;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
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
import reactor.netty.Connection;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 10:44
 */

@Slf4j
public class ClientConnection implements ClientSession {

    private final DisposableConnection disposableConnection;

    private final ClientMessageRouter clientMessageRouter;

    private final List<String> topics = Lists.newArrayList();

    public ClientConnection(DisposableConnection disposableConnection, ClientProperties properties,
        ClientMessageRouter messageRouter) {
        this.disposableConnection = disposableConnection;
        this.clientMessageRouter = messageRouter;
        this.init(properties);
    }

    @Override
    public void init(ClientProperties properties) {
        // 构造消息
        ConnectOptions connectOptions = properties.getOptions();
        MqttConnectMessage connectMessage = MessageBuilder.buildConnect(
            connectOptions.getClientId(),
            connectOptions.getWillTopic(),
            connectOptions.getWillMessage(),
            connectOptions.getUserName(),
            connectOptions.getPassword(),
            connectOptions.getHasUserName(),
            connectOptions.getHasPassword(),
            connectOptions.getHasWillFlag(),
            connectOptions.getWillQos().value(),
            properties.getKeepAliveSeconds());

        Disposable disposable = Mono.fromRunnable(() -> disposableConnection.sendMessage(connectMessage).subscribe())
            .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe();
        // 获取连接
        Connection connection = disposableConnection.getConnection();
        connection.channel().attr(AttributeKeys.closeConnection).set(disposable);
        // 发送消息
        disposableConnection.sendMessage(connectMessage).subscribe();

        // 发送心跳，每分钟发一次
        MqttMessage message = new MqttMessage(
            new MqttFixedHeader(MqttMessageType.PINGREQ, false, MqttQoS.AT_MOST_ONCE, false, 0));
        // 在给定的超时时间内读取变为空闲时调用
        connection.onReadIdle(60 * 1000L, () -> disposableConnection.sendMessage(message).subscribe());

        // 设置关闭时执行
        connection.onDispose(() -> {
            properties.getOnClose().run();
            connection.channel().attr(AttributeKeys.closeConnection).set(null);
            disposableConnection.destroy();
        });

        // 各种策略模式处理
        disposableConnection.receive(MqttMessage.class)
            .subscribe(mqttMessage -> clientMessageRouter.handle(mqttMessage, disposableConnection));

        // 设置客户端连接
        connection.channel().attr(AttributeKeys.clientConnection).set(this);

        List<MqttTopicSubscription> topicSubscriptions = disposableConnection.getTopics().stream()
            .map(topicFilter -> new MqttTopicSubscription(topicFilter, MqttQoS.AT_MOST_ONCE))
            .collect(Collectors.toList());
        // 如果连接的Topic不为空就发送消息
        if (CollectionUtil.isNotEmpty(topicSubscriptions)) {
            int messageId = IdUtil.messageId();
            MqttSubscribeMessage mqttMessage = MessageBuilder.buildSub(messageId, topicSubscriptions);
            disposableConnection.sendMessageRetry(messageId, mqttMessage);
        }
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, boolean retained, MqttQoS mqttQoS) {
        switch (mqttQoS) {
            case AT_MOST_ONCE:
                return disposableConnection.sendMessage(
                    MessageBuilder.buildPub(false, MqttQoS.AT_MOST_ONCE, retained, 1, topic, message));
            case EXACTLY_ONCE:
            case AT_LEAST_ONCE:
                int messageId = IdUtil.messageId();
                return disposableConnection.sendMessageRetry(messageId,
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
        return disposableConnection.sendMessageRetry(messageId, MessageBuilder.buildSub(messageId, topicSubscriptions));
    }

    @Override
    public Mono<Void> unsub(List<String> topics) {
        this.topics.removeAll(topics);
        // retry
        int messageId = IdUtil.messageId();
        return disposableConnection.sendMessageRetry(messageId, MessageBuilder.buildUnSub(messageId, topics));
    }

    @Override
    public Mono<Void> unsub() {
        return unsub(this.topics);
    }

    @Override
    public void dispose() {
        disposableConnection.dispose();
    }
}
