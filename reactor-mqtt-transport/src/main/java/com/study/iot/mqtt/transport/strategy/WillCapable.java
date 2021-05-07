package com.study.iot.mqtt.transport.strategy;

import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.common.message.WillMessage;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 11:50
 */

public interface WillCapable {

    /**
     * 处理消息
     *
     * @param qoS         {@link MqttQoS}
     * @param connection {@link TransportConnection}
     * @param willMessage {@link WillMessage}
     */
    void handler(MqttQoS qoS, TransportConnection connection, WillMessage willMessage);
}
