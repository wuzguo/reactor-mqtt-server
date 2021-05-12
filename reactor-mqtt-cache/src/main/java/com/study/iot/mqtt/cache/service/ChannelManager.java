package com.study.iot.mqtt.cache.service;

import com.study.iot.mqtt.cache.strategy.CacheCapable;
import java.util.Collection;
import reactor.core.Disposable;

public interface ChannelManager extends CacheCapable {

    /**
     * 添加连接
     *
     * @param identity   设备标识
     * @param disposable 连接
     */
    void add(String identity, Disposable disposable);

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
     * @return {@link Disposable}
     */
    Disposable getAndRemove(String identity);

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
     * @return {@link Disposable}
     */
    Collection<Disposable> getConnections();
}
