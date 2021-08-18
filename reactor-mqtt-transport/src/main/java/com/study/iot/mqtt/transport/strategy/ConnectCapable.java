package com.study.iot.mqtt.transport.strategy;

import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/18 15:45
 */

public interface ConnectCapable extends StrategyCapable {

    /**
     * 处理消息
     *
     * @param connection {@link DisposableConnection}
     * @param message    {@link MqttMessage}
     */
    void handle(DisposableConnection connection, MqttMessage message);
}
