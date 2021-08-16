package com.study.iot.mqtt.protocol.session;

import com.study.iot.mqtt.protocol.config.ClientProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

public interface ClientSession extends Disposable {

    /**
     * 发布消息
     *
     * @param topic    主题
     * @param message  消息体
     * @param retained 是否保留
     * @param mqttQoS  {@link MqttQoS} 消息QOS
     * @return {@link Mono}
     */
    Mono<Void> publish(String topic, byte[] message, boolean retained, MqttQoS mqttQoS);

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param message 消息体
     * @return {@link Mono}
     */
    Mono<Void> publish(String topic, byte[] message);

    /**
     * 发布消息
     *
     * @param topic   主题
     * @param message 消息体
     * @param mqttQoS {@link MqttQoS} 消息QOS
     * @return {@link Mono}
     */
    Mono<Void> publish(String topic, byte[] message, MqttQoS mqttQoS);

    /**
     * 发布消息
     *
     * @param topic    主题
     * @param message  消息体
     * @param retained 是否保留
     * @return {@link Mono}
     */
    Mono<Void> publish(String topic, byte[] message, boolean retained);

    /**
     * 订阅
     *
     * @param topicNames {@link String Topic集合}
     * @return {@link Mono}
     */
    Mono<Void> subscribe(String... topicNames);

    /**
     * 取消订阅
     *
     * @param topicNames {@link String Topic集合}
     * @return {@link Mono}
     */
    Mono<Void> unSubscribe(String... topicNames);

    /**
     * 取消订阅
     *
     * @return {@link Mono}
     */
    Mono<Void> unSubscribe();

    /**
     * 连接操作
     *
     * @param properties {@link ClientProperties}
     */
    void doConnect(ClientProperties properties);
}
