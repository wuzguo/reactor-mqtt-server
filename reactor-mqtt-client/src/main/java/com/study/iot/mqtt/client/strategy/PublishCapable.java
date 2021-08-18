package com.study.iot.mqtt.client.strategy;

import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/10 11:04
 */

public interface PublishCapable extends StrategyCapable {

    /**
     * 处理消息
     *
     * @param message    {@link MqttPublishMessage}
     * @param connection {@link DisposableConnection}
     * @param bytes      {@link Byte}
     */
    void handle(DisposableConnection connection, MqttPublishMessage message, byte[] bytes);
}
