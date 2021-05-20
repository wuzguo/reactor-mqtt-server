package com.study.iot.mqtt.store.container;

import com.study.iot.mqtt.store.strategy.CacheCapable;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/20 10:45
 */

public interface StorageContainer<T> extends CacheCapable {

    /**
     * 添加连接
     *
     * @param key   标识
     * @param value {@link Serializable} 连接
     */
    void add(@NotNull String key, T value);

    /**
     * 移除通道
     *
     * @param key 标识
     */
    void remove(@NotNull String key);


    /**
     * 获取
     *
     * @param key 标识
     * @return {@link Serializable}
     */
    T get(@NotNull String key);

    /**
     * 获取
     *
     * @param key 标识
     * @return {@link Serializable}
     */
    List<T> list(@NotNull String key);

    /**
     * 获取所有的记录
     *
     * @return {@link Serializable}
     */
    List<T> getAll();

    /**
     * 获取并移除
     *
     * @param key 标识
     * @return {@link Serializable}
     */
    T getAndRemove(@NotNull String key);

    /**
     * 检查
     *
     * @param key 标识
     * @return {@link Boolean}
     */
    Boolean containsKey(@NotNull String key);
}
