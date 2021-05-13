package com.study.iot.mqtt.protocol.session;

import com.study.iot.mqtt.protocol.config.ClientProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.List;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public interface ClientSession extends Disposable {


    Mono<Void> pub(String topic, byte[] message, boolean retained, MqttQoS mqttQoS);

    Mono<Void> pub(String topic, byte[] message);

    Mono<Void> pub(String topic, byte[] message, MqttQoS mqttQoS);

    Mono<Void> pub(String topic, byte[] message, boolean retained);

    Mono<Void> sub(String... subMessages);

    Mono<Void> unsub(List<String> topics);

    Mono<Void> unsub();

    void init(ClientProperties properties);
}
