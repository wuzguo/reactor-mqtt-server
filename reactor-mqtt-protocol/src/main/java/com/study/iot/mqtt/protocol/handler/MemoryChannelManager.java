package com.study.iot.mqtt.protocol.handler;


import com.study.iot.mqtt.protocol.ChannelManager;
import com.study.iot.mqtt.protocol.TransportConnection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


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
