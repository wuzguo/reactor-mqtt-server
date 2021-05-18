package com.study.iot.mqtt.store.service.memory;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.disposable.SerializerDisposable;
import com.study.iot.mqtt.store.service.ChannelManager;
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

@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.MEMORY)
public class MemoryChannelManager implements ChannelManager {

    private final Map<String, SerializerDisposable> mapDisposable = Maps.newConcurrentMap();

    @Override
    public void add(String identity, SerializerDisposable disposable) {
        mapDisposable.put(identity, disposable);
    }

    @Override
    public void remove(String identity) {
        mapDisposable.remove(identity);
    }

    @Override
    public SerializerDisposable getAndRemove(String identity) {
        return mapDisposable.remove(identity);
    }

    @Override
    public Boolean containsKey(String identity) {
        return mapDisposable.containsKey(identity);
    }

    @Override
    public List<SerializerDisposable> getConnections() {
        return Lists.newArrayList(mapDisposable.values());
    }
}
