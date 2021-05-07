package com.study.iot.mqtt.cache.manager;

import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.connection.TransportConnection;

import java.util.List;


public interface TopicManager extends CacheCapable {

    /**
     * 获取连接
     *
     * @param topic {@link String}
     * @return {@link TransportConnection}
     */
    List<TransportConnection> getConnections(String topic);

    /**
     * 添加连接
     *
     * @param topic      {@link String}
     * @param connection {@link TransportConnection}
     */
    void addConnection(String topic, TransportConnection connection);

    /**
     * 删除连接
     *
     * @param topic      {@link String}
     * @param connection {@link TransportConnection}
     */
    void deleteConnection(String topic, TransportConnection connection);
}
