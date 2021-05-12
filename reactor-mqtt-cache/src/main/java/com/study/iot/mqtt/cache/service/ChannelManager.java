package com.study.iot.mqtt.cache.service;

import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import java.util.Collection;

public interface ChannelManager extends CacheCapable {

    /**
     * 添加连接
     *
     * @param identity   设备标识
     * @param connection 连接
     */
    void add(String identity, DisposableConnection connection);

    /**
     * 移除通道
     *
     * @param identity 设备标识
     */
    void remove(String identity);

    /**
     * 获取并移除
     *
     * @param identity 设备ID
     * @return {@link DisposableConnection}
     */
    DisposableConnection getAndRemove(String identity);

    /**
     * 检查
     *
     * @param identity 设备ID
     * @return {@link Boolean}
     */
    Boolean containsKey(String identity);

    /**
     * 获取所有连接
     *
     * @return {@link DisposableConnection}
     */
    Collection<DisposableConnection> getConnections();
}
