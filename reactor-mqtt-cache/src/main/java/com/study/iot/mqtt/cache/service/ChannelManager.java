package com.study.iot.mqtt.cache.service;

import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.connection.DisposableConnection;

import java.util.List;

public interface ChannelManager extends CacheCapable {

    /**
     * 获取连接
     *
     * @return {@link DisposableConnection}
     */
    List<DisposableConnection> getConnections();

    /**
     * 添加连接
     *
     * @param connection {@link DisposableConnection}
     */
    void addConnections(DisposableConnection connection);

    /**
     * 移除连接
     *
     * @param connection 连接
     */
    void removeConnection(DisposableConnection connection);

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
    void removeChannel(String identity);

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
    Boolean check(String identity);
}
