package com.study.iot.mqtt.transport.client.handler;


import io.netty.handler.codec.mqtt.MqttMessageType;

public interface DirectHandlerAdaptor {

    DirectHandlerFactory handler(MqttMessageType messageType);

}
