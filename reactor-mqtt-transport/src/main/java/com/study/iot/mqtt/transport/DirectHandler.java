package com.study.iot.mqtt.transport;


import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.TransportConnection;
import io.netty.handler.codec.mqtt.MqttMessage;

public interface DirectHandler {

    /**
     * 处理消息
     *
     * @param message       {@link MqttMessage}
     * @param connection    {@link TransportConnection}
     * @param configuration {@link ConnectConfiguration}
     */
    void handler(MqttMessage message, TransportConnection connection, ConnectConfiguration configuration);
}
