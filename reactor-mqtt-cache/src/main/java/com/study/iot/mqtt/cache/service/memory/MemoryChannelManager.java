package com.study.iot.mqtt.cache.service.memory;


import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.service.ChannelManager;
import java.util.Collection;
import java.util.Map;
import reactor.core.Disposable;


/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.MEMORY)
public class MemoryChannelManager implements ChannelManager {

    private final Map<String, Disposable> mapDisposable = Maps.newConcurrentMap();

    @Override
    public void add(String identity, Disposable disposable) {
        mapDisposable.put(identity, disposable);
    }

    @Override
    public void remove(String identity) {
        mapDisposable.remove(identity);
    }

    @Override
    public Disposable getAndRemove(String identity) {
        return mapDisposable.remove(identity);
    }

    @Override
    public Boolean containsKey(String identity) {
        return mapDisposable.containsKey(identity);
    }

    @Override
    public Collection<Disposable> getConnections() {
        return mapDisposable.values();
    }
}
