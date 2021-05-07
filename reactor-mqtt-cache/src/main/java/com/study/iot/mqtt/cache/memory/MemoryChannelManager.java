package com.study.iot.mqtt.cache.memory;


import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.MEMORY)
public class MemoryChannelManager implements ChannelManager {

    private CopyOnWriteArrayList<TransportConnection> connections = new CopyOnWriteArrayList<>();

    private ConcurrentHashMap<String, TransportConnection> connectionMap = new ConcurrentHashMap<>();

    @Override
    public List<TransportConnection> getConnections() {
        return connections;
    }

    @Override
    public void addConnections(TransportConnection connection) {
        connections.add(connection);
    }

    @Override
    public void removeConnections(TransportConnection connection) {
        connections.remove(connection);
    }

    @Override
    public void addDeviceId(String deviceId, TransportConnection connection) {
        connectionMap.put(deviceId, connection);
    }

    @Override
    public void removeDeviceId(String deviceId) {
        connectionMap.remove(deviceId);
    }

    @Override
    public TransportConnection getRemoveDeviceId(String deviceId) {
        return connectionMap.remove(deviceId);
    }

    @Override
    public boolean checkDeviceId(String deviceId) {
        return connectionMap.containsKey(deviceId);
    }


}
