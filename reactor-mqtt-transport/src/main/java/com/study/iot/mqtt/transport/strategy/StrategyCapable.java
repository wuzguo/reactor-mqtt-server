package com.study.iot.mqtt.transport.strategy;


import com.study.iot.mqtt.common.connection.DisposableConnection;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:13
 */

public interface StrategyCapable {

    /**
     * 处理消息
     *
     * @param message       {@link MqttMessage}
     * @param connection    {@link DisposableConnection}
     */
    void handle(MqttMessage message, DisposableConnection connection);
}
