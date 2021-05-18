package com.study.iot.mqtt.store.service;

import com.study.iot.mqtt.store.disposable.SerializerDisposable;
import com.study.iot.mqtt.store.strategy.CacheCapable;
import java.util.Collection;
import java.util.List;
import reactor.core.Disposable;

public interface ChannelManager extends CacheCapable {

    /**
     * 添加连接
     *
     * @param identity   设备标识
     * @param disposable 连接
     */
    void add(String identity, SerializerDisposable disposable);

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
    SerializerDisposable getAndRemove(String identity);

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
    List<SerializerDisposable> getConnections();
}
