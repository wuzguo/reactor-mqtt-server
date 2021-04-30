package com.study.iot.mqtt.transport.server.handler;

import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartHandler implements DirectHandler {


    @Override
    public void handler(MqttMessage message, TransportConnection connection, ConnectConfiguration configuration) {
        switch (message.fixedHeader().messageType()) {
            case PINGREQ:
                connection.sendPingRes().subscribe();
                break;
            case PINGRESP:
                break;
            default:
                break;
        }
    }
}
