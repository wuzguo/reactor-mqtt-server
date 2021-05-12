package com.study.iot.mqtt.cache.service.memory;


import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import java.util.Collection;
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

    private final Map<String, DisposableConnection> mapConnection = Maps.newConcurrentMap();

    @Override
    public void add(String identity, DisposableConnection connection) {
        mapConnection.put(identity, connection);
    }

    @Override
    public void remove(String identity) {
        mapConnection.remove(identity);
    }

    @Override
    public DisposableConnection getAndRemove(String identity) {
        return mapConnection.remove(identity);
    }

    @Override
    public Boolean containsKey(String identity) {
        return mapConnection.containsKey(identity);
    }

    @Override
    public Collection<DisposableConnection> getConnections() {
        return mapConnection.values();
    }
}
