package com.study.iot.mqtt.cache.memory;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;

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

    private final List<TransportConnection> connections = Lists.newCopyOnWriteArrayList();


    private final Map<String, TransportConnection> mapConnection = Maps.newConcurrentMap();

    @Override
    public List<TransportConnection> getConnections() {
        return connections;
    }

    @Override
    public void addConnections(TransportConnection connection) {
        connections.add(connection);
    }

    @Override
    public void removeConnection(TransportConnection connection) {
        connections.remove(connection);
    }

    @Override
    public void add(String deviceId, TransportConnection connection) {
        mapConnection.put(deviceId, connection);
    }

    @Override
    public void removeChannel(String deviceId) {
        mapConnection.remove(deviceId);
    }

    @Override
    public TransportConnection getAndRemove(String deviceId) {
        return mapConnection.remove(deviceId);
    }

    @Override
    public Boolean check(String deviceId) {
        return mapConnection.containsKey(deviceId);
    }
}
