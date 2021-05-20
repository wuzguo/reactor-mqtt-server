package com.study.iot.mqtt.store.container.memory;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.common.message.RetainMessage;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.List;
import java.util.Map;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.MESSAGE, type = CacheStrategy.MEMORY)
public class MemoryMessageContainer implements StorageContainer<RetainMessage> {

    private final Map<String, RetainMessage> messages = Maps.newConcurrentMap();

    @Override
    public void add(String key, RetainMessage value) {
        messages.put(key, value);
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public RetainMessage get(String key) {
        return messages.get(key);
    }

    @Override
    public List<RetainMessage> list(String key) {
        return null;
    }

    @Override
    public List<RetainMessage> getAll() {
        return null;
    }

    @Override
    public RetainMessage getAndRemove(String key) {
        return null;
    }

    @Override
    public Boolean containsKey(String key) {
        return null;
    }
}
