package com.study.iot.mqtt.common.connection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.study.iot.mqtt.common.message.TransportMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;


@Slf4j
@Data
public class DisposableConnection implements Disposable, Serializable {

    private NettyInbound inbound;

    private NettyOutbound outbound;

    private Connection connection;

    private Map<Integer, Disposable> mapDisposable = Maps.newHashMap();

    private Map<Integer, TransportMessage> mapQosMessage = Maps.newHashMap();

    private List<String> topics = Lists.newCopyOnWriteArrayList();

    private LongAdder longAdder = new LongAdder();

    public DisposableConnection(Connection connection) {
        this.connection = connection;
        this.inbound = connection.inbound();
        this.outbound = connection.outbound();
    }

    public <T> Flux<T> receive(Class<T> cls) {
        return inbound.receive().cast(cls);
    }

    /**
     * 生成消息 ID
     *
     * @return {@link Integer}
     */
    public int messageId() {
        longAdder.increment();
        int value = longAdder.intValue();
        if (value == Integer.MAX_VALUE) {
            longAdder.reset();
            longAdder.increment();
            return longAdder.intValue();
        }
        return value;
    }

    public void addTopic(String topic) {
        topics.add(topic);
    }

    public void removeTopic(String topic) {
        topics.remove(topic);
    }

    public Mono<Void> sendMessage(MqttMessage message) {
        return outbound.sendObject(message).then().doOnError(Throwable::printStackTrace);
    }

    public Mono<Void> sendMessageRetry(Integer messageId, MqttMessage message) {
        // retry
        this.addDisposable(messageId, Mono.fromRunnable(() -> this.sendMessage(message).subscribe())
            .delaySubscription(Duration.ofSeconds(10)).repeat().subscribe());
        // pub
        return this.sendMessage(message);
    }


    public void saveQos2Message(Integer messageId, TransportMessage message) {
        mapQosMessage.put(messageId, message);
    }


    public Optional<TransportMessage> getAndRemoveQos2Message(Integer messageId) {
        TransportMessage message = mapQosMessage.get(messageId);
        mapQosMessage.remove(messageId);
        return Optional.ofNullable(message);
    }

    public boolean containQos2Message(Integer messageId, byte[] bytes) {
        return mapQosMessage.containsKey(messageId);
    }


    private void addDisposable(Integer messageId, Disposable disposable) {
        mapDisposable.put(messageId, disposable);
    }


    public void cancelDisposable(Integer messageId) {
        Optional.ofNullable(mapDisposable.get(messageId)).ifPresent(Disposable::dispose);
        mapDisposable.remove(messageId);
    }


    @Override
    public void dispose() {
        connection.dispose();
    }

    public boolean isDispose() {
        return connection.isDisposed();
    }

    public void destory() {
        mapDisposable.values().forEach(Disposable::dispose);
        mapDisposable.clear();
        mapQosMessage.clear();
        topics.clear();
    }
}
