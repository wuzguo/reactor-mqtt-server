package com.study.iot.mqtt.cache.service;

import com.study.iot.mqtt.cache.disposable.SerializerDisposable;
import com.study.iot.mqtt.cache.strategy.CacheCapable;
import java.util.List;
import reactor.core.Disposable;


public interface TopicManager extends CacheCapable {

    /**
     * 获取连接
     *
     * @param topic {@link String}
     * @return {@link Disposable}
     */
    List<SerializerDisposable> getConnections(String topic);

    /**
     * 添加连接
     *
     * @param topic      {@link String}
     * @param connection {@link Disposable}
     */
    void add(String topic, SerializerDisposable connection);

    /**
     * 删除连接
     *
     * @param topic      {@link String}
     * @param connection {@link Disposable}
     */
    void remove(String topic, SerializerDisposable connection);
}
