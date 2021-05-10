package com.study.iot.mqtt.cache.service;

import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import java.util.List;


public interface TopicManager extends CacheCapable {

    /**
     * 获取连接
     *
     * @param topic {@link String}
     * @return {@link DisposableConnection}
     */
    List<DisposableConnection> getConnections(String topic);

    /**
     * 添加连接
     *
     * @param topic      {@link String}
     * @param connection {@link DisposableConnection}
     */
    void addConnection(String topic, DisposableConnection connection);

    /**
     * 删除连接
     *
     * @param topic      {@link String}
     * @param connection {@link DisposableConnection}
     */
    void deleteConnection(String topic, DisposableConnection connection);
}
