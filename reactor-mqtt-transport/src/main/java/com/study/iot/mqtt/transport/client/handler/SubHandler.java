package com.study.iot.mqtt.transport.client.handler;


import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;


public class SubHandler implements DirectHandler {


    @Override
    public void handler(MqttMessage message, TransportConnection connection, ConnectConfiguration config) {
        MqttFixedHeader header = message.fixedHeader();
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
        switch (header.messageType()) {
            case SUBACK:
            case UNSUBACK:
                connection.cancelDisposable(mqttMessageIdVariableHeader.messageId());
                break;
            default:
                break;
        }
    }
}
