package com.study.iot.mqtt.transport.strategy;


import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.protocol.ConnectConfiguration;
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
     * @param connection    {@link TransportConnection}
     * @param configuration {@link ConnectConfiguration}
     */
    void handler(MqttMessage message, TransportConnection connection);
}
