package com.study.iot.mqtt.transport.client.connection;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.protocal.AttributeKeys;
import com.study.iot.mqtt.protocal.MqttMessageApi;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.protocal.config.ClientConfig;
import com.study.iot.mqtt.protocal.session.ClientSession;
import com.study.iot.mqtt.transport.client.handler.ClientMessageRouter;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;

import java.time.Duration;
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

    private final ClientConfig clientConfig;

    private ClientMessageRouter clientMessageRouter;

    private List<String> topics = Lists.newArrayList();

    public ClientConnection(TransportConnection connection, ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        this.connection = connection;
        this.clientMessageRouter = new ClientMessageRouter(clientConfig);

        initHandler();
    }

    public void initHandler() {
        ClientConfig.Options options = clientConfig.getOptions();
        NettyInbound inbound = connection.getInbound();
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
                clientConfig.getHeart()
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
                clientConfig.getHeart()
        )).doOnError(throwable -> log.error(throwable.getMessage())).subscribe();
        connection.getConnection().channel().attr(AttributeKeys.closeConnection).set(disposable);
        connection.getConnection().onWriteIdle(clientConfig.getHeart(), () -> connection.sendPingReq().subscribe()); // 发送心跳
        connection.getConnection().onReadIdle(clientConfig.getHeart() * 2, () -> connection.sendPingReq().subscribe()); // 发送心跳
        connection.getConnection().onDispose(() -> clientConfig.getOnClose().run());
        inbound.receiveObject().cast(MqttMessage.class)
                .subscribe(message -> clientMessageRouter.handler(message, connection));
        connection.getConnection().channel().attr(AttributeKeys.clientConnectionAttributeKey).set(this);
        List<MqttTopicSubscription> mqttTopicSubscriptions = connection.getTopics().stream().map(s -> new MqttTopicSubscription(s, MqttQoS.AT_MOST_ONCE)).collect(Collectors.toList());
        if (mqttTopicSubscriptions != null && mqttTopicSubscriptions.size() > 0) {
            int messageId = connection.messageId();
            connection.addDisposable(messageId, Mono.fromRunnable(() ->
                    connection.write(MqttMessageApi.buildSub(messageId, mqttTopicSubscriptions)).subscribe())
                    .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe()); // retryPooledConnectionProvider
            connection.write(MqttMessageApi.buildSub(messageId, mqttTopicSubscriptions)).subscribe();
        }
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, boolean retained, int qos) {
        return null;
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message) {
        return null;
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, int qos) {
        return null;
    }

    @Override
    public Mono<Void> pub(String topic, byte[] message, boolean retained) {
        return null;
    }

    @Override
    public Mono<Void> sub(String... subMessages) {
        return null;
    }

    @Override
    public Mono<Void> unsub(List<String> topics) {
        return null;
    }

    @Override
    public Mono<Void> unsub() {
        return null;
    }

    @Override
    public Mono<Void> messageAcceptor(BiConsumer<String, byte[]> messageAcceptor) {
        return null;
    }

    @Override
    public void dispose() {

    }
}
