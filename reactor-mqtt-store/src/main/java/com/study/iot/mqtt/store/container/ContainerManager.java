package com.study.iot.mqtt.store.container;

import com.study.iot.mqtt.store.strategy.CacheStrategy;
import java.io.Serializable;
import javax.validation.constraints.NotNull;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */


public interface ContainerManager {

    /**
     * 初始化
     *
     * @param strategy {@link CacheStrategy}
     */
    void strategy(@NotNull CacheStrategy strategy);

    /**
     * 获取连接对象
     *
     * @param cacheGroup {@link String}
     * @return {@link StorageContainer}
     */
    <T extends Serializable> StorageContainer<T> get(@NotNull String cacheGroup);
}
