package com.study.iot.mqtt.transport.server.handler;


import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.protocal.config.ServerConfig;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;

public class SubHandler implements DirectHandler {

    @Override
    public void handler(MqttMessage message, TransportConnection connection, ConnectConfiguration config) {
        ServerConfig serverConfig = (ServerConfig) config;

        MqttFixedHeader header = message.fixedHeader();
        switch (header.messageType()) {
            case SUBSCRIBE:
                break;
            case UNSUBSCRIBE:
                break;
            default:
                break;
        }
    }
}
