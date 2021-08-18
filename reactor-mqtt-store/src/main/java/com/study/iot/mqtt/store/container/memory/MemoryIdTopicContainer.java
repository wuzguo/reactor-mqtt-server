package com.study.iot.mqtt.store.container.memory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.study.iot.mqtt.common.enums.CacheEnum;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 15:55
 */

@CacheStrategyService(group = CacheGroup.ID_TOPIC, type = CacheEnum.MEMORY)
public class MemoryIdTopicContainer implements StorageContainer<String> {

    private final Map<String, List<String>> messages = Maps.newConcurrentMap();

    @Override
    public void add(String key, String value) {
        List<String> values = Optional.ofNullable(messages.get(key)).orElse(Lists.newArrayList());
        values.add(value);
        messages.put(key, values);
    }

    @Override
    public void remove(String key) {
        messages.remove(key);
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public List<String> list(String key) {
        return messages.get(key);
    }

    @Override
    public List<String> getAll() {
        return null;
    }

    @Override
    public String getAndRemove(String key) {
        return null;
    }

    @Override
    public Boolean containsKey(String key) {
        return null;
    }
}
