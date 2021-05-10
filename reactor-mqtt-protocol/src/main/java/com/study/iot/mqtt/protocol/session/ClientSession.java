package com.study.iot.mqtt.protocol.session;

import java.util.List;
import java.util.function.BiConsumer;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public interface ClientSession extends Disposable {


    Mono<Void> pub(String topic, byte[] message, boolean retained, int qos);

    Mono<Void> pub(String topic, byte[] message);

    Mono<Void> pub(String topic, byte[] message, int qos);

    Mono<Void> pub(String topic, byte[] message, boolean retained);

    Mono<Void> sub(String... subMessages);

    Mono<Void> unsub(List<String> topics);

    Mono<Void> unsub();

    Mono<Void> messageAcceptor(BiConsumer<String, byte[]> messageAcceptor);

    void init();
}
