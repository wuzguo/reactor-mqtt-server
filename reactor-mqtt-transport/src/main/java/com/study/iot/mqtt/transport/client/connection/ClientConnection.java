package com.study.iot.mqtt.transport.client.connection;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.MqttMessageApi;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 10:44
 */

@Slf4j
public class ClientConnection implements ClientSession {

    private final TransportConnection connection;

    private final ClientMessageRouter clientMessageRouter;

    private List<String> topics = Lists.newArrayList();

    private final ClientConfiguration configuration;

    public ClientConnection(TransportConnection connection, ClientConfiguration configuration, ClientMessageRouter messageRouter) {
        this.connection = connection;
        this.configuration = configuration;
        this.clientMessageRouter = messageRouter;
        this.init();
    }

    @Override
    public void init() {
        ClientConfiguration.Options options = configuration.getOptions();
        Disposable disposable = Mono.fromRunnable(() -> connection.write(MqttMessageApi.buildConnect(
                options.getClientIdentifier(),
                options.getWillTopic(),
                options.getWillMessage(),
                options.getUserName(),
                options.getPassword(),
                options.isHasUserName(),
                options.isHasPassword(),
                options.isHasWillFlag(),
                options.getWillQos(),
                configuration.getHeart()
        )).subscribe()).delaySubscription(Duration.ofSeconds(10)).repeat().subscribe();
        connection.write(MqttMessageApi.buildConnect(
                options.getClientIdentifier(),
                options.getWillTopic(),
                options.getWillMessage(),
                options.getUserName(),
                options.getPassword(),
                options.isHasUserName(),
                options.isHasPassword(),
                options.isHasWillFlag(),
                options.getWillQos(),
                configuration.getHeart()
        )).doOnError(throwable -> log.error(throwable.getMessage())).subscribe();
        connection.getConnection().channel().attr(AttributeKeys.closeConnection).set(disposable);
        // 发送心跳
        connection.getConnection().onWriteIdle(configuration.getHeart(), () -> connection.sendPingReq().subscribe());
        // 发送心跳
        connection.getConnection().onReadIdle(configuration.getHeart() * 2, () -> connection.sendPingReq().subscribe());
        connection.getConnection().onDispose(() -> configuration.getOnClose().run());

        NettyInbound inbound = connection.getInbound();
        inbound.receiveObject().cast(MqttMessage.class)
                .subscribe(message -> clientMessageRouter.handler(message, connection));
        connection.getConnection().channel().attr(AttributeKeys.clientConnectionAttributeKey).set(this);
        List<MqttTopicSubscription> mqttTopicSubscriptions = connection.getTopics().stream()
                .map(s -> new MqttTopicSubscription(s, MqttQoS.AT_MOST_ONCE)).collect(Collectors.toList());

        if (mqttTopicSubscriptions.size() > 0) {
            int messageId = connection.messageId();
            connection.addDisposable(messageId, Mono.fromRunnable(() ->
                    connection.write(MqttMessageApi.buildSub(messageId, mqttTopicSubscriptions)).subscribe())
                    .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
            connection.write(MqttMessageApi.buildSub(messageId, mqttTopicSubscriptions)).subscribe();
        }
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, boolean retained, int qos) {
        int messageId = qos == 0 ? 1 : connection.messageId();
        MqttQoS mqttQoS = MqttQoS.valueOf(qos);
        switch (mqttQoS) {
            case AT_MOST_ONCE:
                return connection.write(MqttMessageApi.buildPub(false, MqttQoS.AT_MOST_ONCE, retained, messageId, topic, Unpooled.wrappedBuffer(message)));
            case EXACTLY_ONCE:
            case AT_LEAST_ONCE:
                return Mono.fromRunnable(() -> {
                    connection.write(MqttMessageApi.buildPub(false, mqttQoS, retained, messageId, topic, Unpooled.wrappedBuffer(message))).subscribe();
                    connection.addDisposable(messageId, Mono.fromRunnable(() ->
                            connection.write(MqttMessageApi.buildPub(true, mqttQoS, retained, messageId, topic, Unpooled.wrappedBuffer(message))).subscribe())
                            .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe()); // retry
                });
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
                .map(topicFilter -> new MqttTopicSubscription(topicFilter, MqttQoS.AT_MOST_ONCE)).collect(Collectors.toList());
        int messageId = connection.messageId();
        connection.addDisposable(messageId, Mono.fromRunnable(() ->
                connection.write(MqttMessageApi.buildSub(messageId, topicSubscriptions)).subscribe())
                // retry
                .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
        return connection.write(MqttMessageApi.buildSub(messageId, topicSubscriptions));
    }

    @Override
    public Mono<Void> unsub(List<String> topics) {
        this.topics.removeAll(topics);
        int messageId = connection.messageId();
        connection.addDisposable(messageId, Mono.fromRunnable(() ->
                connection.write(MqttMessageApi.buildUnSub(messageId, topics)).subscribe())
                // retry
                .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
        return connection.write(MqttMessageApi.buildUnSub(messageId, topics));
    }

    @Override
    public Mono<Void> unsub() {
        return unsub(this.topics);
    }

    @Override
    public Mono<Void> messageAcceptor(BiConsumer<String, byte[]> messageAcceptor) {
        return Mono.fromRunnable(() -> configuration.setMessageAcceptor(messageAcceptor));
    }

    @Override
    public void dispose() {
        connection.dispose();
    }
}
