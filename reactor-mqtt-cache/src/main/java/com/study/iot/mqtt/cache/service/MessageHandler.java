package com.study.iot.mqtt.cache.service;


import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.message.RetainMessage;

public interface MessageHandler extends CacheCapable {

    /**
     * 保存消息
     *
     * @param message {@link RetainMessage}
     */
    void saveRetain(RetainMessage message);

    /**
     * 获取消息
     *
     * @param topicName Topic对象
     * @return {@link RetainMessage}
     */
    RetainMessage getRetain(String topicName);
}
