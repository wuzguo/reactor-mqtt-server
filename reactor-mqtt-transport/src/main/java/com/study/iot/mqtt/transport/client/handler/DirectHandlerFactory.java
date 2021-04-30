package com.study.iot.mqtt.transport.client.handler;

import com.study.iot.mqtt.common.exception.NotSuppportHandlerException;
import com.study.iot.mqtt.transport.DirectHandler;
import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.ConcurrentHashMap;

public class DirectHandlerFactory {

    private final  MqttMessageType messageType;

    private  ConcurrentHashMap<MqttMessageType, DirectHandler> messageTypeCollection = new ConcurrentHashMap();

    public DirectHandlerFactory(MqttMessageType messageType) {
        this.messageType = messageType;
    }

    public DirectHandler loadHandler(){
        return messageTypeCollection.computeIfAbsent(messageType,type->{
            switch (type){
                case PUBACK:
                case PUBREC:
                case PUBREL:
                case PUBLISH:
                case PUBCOMP:
                    return new PubHandler();

                case CONNACK:
                    return new ConnectHandler();

                case PINGRESP:
                    return new HeartHandler();

                case UNSUBACK:
                case SUBACK:
                    return new SubHandler();
            }
            throw  new NotSuppportHandlerException(messageType+" not support ");
        });
    }


}
