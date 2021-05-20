package com.study.iot.mqtt.store.container;

import com.study.iot.mqtt.store.strategy.CacheCapable;
import java.util.List;
import reactor.core.Disposable;


public interface TopicContainer extends CacheCapable {

    /**
     * 获取连接
     *
     * @param topic {@link String}
     * @return {@link Disposable}
     */
    List<Disposable> getConnections(String topic);

    /**
     * 添加连接
     *
     * @param topic      {@link String}
     * @param connection {@link Disposable}
     */
    void add(String topic, Disposable connection);

    /**
     * 删除连接
     *
     * @param topic      {@link String}
     * @param connection {@link Disposable}
     */
    void remove(String topic, Disposable connection);
}
