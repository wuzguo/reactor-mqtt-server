package com.study.iot.mqtt.cache.manager;


import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.message.RetainMessage;

import java.util.Optional;

public interface MessageHandler extends CacheCapable {

    void saveRetain(boolean dup, boolean retain, int qos, String topicName, byte[] copyByteBuf);

    RetainMessage getRetain(String topicName);
}
