package com.study.iot.mqtt.store.service.memory;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.service.MessageManager;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.message.RetainMessage;
import java.util.Map;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.MESSAGE, type = CacheStrategy.MEMORY)
public class MemoryMessageManager implements MessageManager {

    private final Map<String, RetainMessage> messages = Maps.newConcurrentMap();

    @Override
    public void saveRetain(RetainMessage message) {
        messages.put(message.getTopic(), message);
    }

    @Override
    public RetainMessage getRetain(String topicName) {
        return messages.get(topicName);
    }
}
