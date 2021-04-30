package com.study.iot.mqtt.transport.server.handler;


import com.study.iot.mqtt.protocal.ConnectConfiguration;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.protocal.config.ServerConfig;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PubHandler implements DirectHandler {


    @Override
    public void handler(MqttMessage message, TransportConnection connection, ConnectConfiguration config) {
        ServerConfig serverConfig = (ServerConfig) config;
        MqttFixedHeader header = message.fixedHeader();
        switch (header.messageType()) {
            case PUBLISH:
                break;
            case PUBACK:
                break;
            case PUBREC:
                break;
            case PUBREL:
                break;
            case PUBCOMP:
                break;
            default:
                break;
        }
    }

    private byte[] copyByteBuf(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }
}
