package com.study.iot.mqtt.protocal;


import com.study.iot.mqtt.common.connection.RetainMessage;

import java.util.Optional;

public interface MessageHandler {

    void saveRetain(boolean dup, boolean retain, int qos, String topicName, byte[] copyByteBuf);

    Optional<RetainMessage> getRetain(String topicName);
}
