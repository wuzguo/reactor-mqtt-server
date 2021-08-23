package com.study.iot.mqtt.store.container.memory;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.common.enums.CacheEnum;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.List;
import java.util.Map;
import reactor.core.Disposable;


/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheEnum.MEMORY)
public class MemoryChannelContainer implements StorageContainer<Disposable> {

    private final Map<String, Disposable> mapDisposable = Maps.newConcurrentMap();

    @Override
    public void add(String key, Disposable value) {
        mapDisposable.put(key, value);
    }

    @Override
    public void remove(String identity) {
        mapDisposable.remove(identity);
    }

    @Override
    public Disposable get(String key) {
        return mapDisposable.get(key);
    }

    @Override
    public List<Disposable> list(String key) {
        return null;
    }

    @Override
    public List<Disposable> getAll() {
        return Lists.newArrayList(mapDisposable.values());
    }

    @Override
    public Disposable getAndRemove(String identity) {
        return mapDisposable.remove(identity);
    }

    @Override
    public Boolean containsKey(String identity) {
        return mapDisposable.containsKey(identity);
    }
}
