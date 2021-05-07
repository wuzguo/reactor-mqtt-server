package com.study.iot.mqtt.cache.redis;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.MessageHandler;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.common.message.RetainMessage;

import java.util.Map;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.MESSAGE, type = CacheStrategy.REDIS)
public class RedisMessageHandler implements MessageHandler {

    private final Map<String, RetainMessage> messages = Maps.newConcurrentMap();

    @Override
    public void saveRetain(boolean dup, boolean retain, int qos, String topicName, byte[] copyByteBuf) {
        messages.put(topicName, new RetainMessage(dup, retain, qos, topicName, copyByteBuf));
    }

    @Override
    public RetainMessage getRetain(String topicName) {
        return messages.get(topicName);
    }
}
