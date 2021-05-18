package com.study.iot.mqtt.store.manager;

import com.study.iot.mqtt.store.strategy.CacheCapable;
import com.study.iot.mqtt.common.message.RetainMessage;

public interface MessageManager extends CacheCapable {

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
