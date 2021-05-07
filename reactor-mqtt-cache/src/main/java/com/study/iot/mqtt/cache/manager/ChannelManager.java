package com.study.iot.mqtt.cache.manager;

import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.connection.TransportConnection;

import java.util.List;

public interface ChannelManager extends CacheCapable {

    /**
     * 获取连接
     *
     * @return {@link TransportConnection}
     */
    List<TransportConnection> getConnections();

    /**
     * 添加连接
     *
     * @param connection {@link TransportConnection}
     */
    void addConnections(TransportConnection connection);

    /**
     * 移除连接
     *
     * @param connection 连接
     */
    void removeConnection(TransportConnection connection);

    /**
     * 添加连接
     *
     * @param deviceId   设备标识
     * @param connection 连接
     */
    void add(String deviceId, TransportConnection connection);

    /**
     * 移除通道
     *
     * @param deviceId 设备标识
     */
    void removeChannel(String deviceId);

    /**
     * 获取并移除
     *
     * @param deviceId 设备ID
     * @return {@link TransportConnection}
     */
    TransportConnection getAndRemove(String deviceId);

    /**
     * 检查
     *
     * @param deviceId 设备ID
     * @return {@link Boolean}
     */
    Boolean check(String deviceId);
}
