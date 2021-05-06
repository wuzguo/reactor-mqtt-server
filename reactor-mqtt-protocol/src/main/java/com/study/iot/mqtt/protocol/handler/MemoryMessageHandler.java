package com.study.iot.mqtt.protocol.handler;

import com.study.iot.mqtt.common.message.RetainMessage;
import com.study.iot.mqtt.protocol.MessageHandler;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryMessageHandler implements MessageHandler {

    private ConcurrentHashMap<String , RetainMessage> messages = new ConcurrentHashMap();

    @Override
    public void saveRetain(boolean dup, boolean retain, int qos, String topicName, byte[] copyByteBuf) {
        messages.put(topicName,new RetainMessage(dup,retain,qos,topicName,copyByteBuf));
    }

    @Override
    public Optional<RetainMessage> getRetain(String topicName) {
        return Optional.ofNullable(messages.get(topicName));
    }
}
