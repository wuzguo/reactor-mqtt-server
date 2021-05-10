package com.study.iot.mqtt.cache.service;


import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.message.RetainMessage;

public interface MessageHandler extends CacheCapable {

    void saveRetain(boolean dup, boolean retain, int qos, String topicName, byte[] copyByteBuf);

    RetainMessage getRetain(String topicName);
}
