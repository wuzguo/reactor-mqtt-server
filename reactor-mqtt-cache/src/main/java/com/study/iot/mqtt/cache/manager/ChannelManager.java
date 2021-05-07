package com.study.iot.mqtt.cache.manager;

import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.connection.TransportConnection;

import java.util.List;

public interface ChannelManager extends CacheCapable {

    List<TransportConnection> getConnections();


    void addConnections(TransportConnection connection);


    void removeConnections(TransportConnection connection);

    void addDeviceId(String deviceId, TransportConnection connection);

    void removeDeviceId(String deviceId);

    TransportConnection getRemoveDeviceId(String deviceId);

    boolean checkDeviceId(String deviceId);
}
