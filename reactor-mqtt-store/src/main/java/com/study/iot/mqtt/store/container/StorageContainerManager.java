package com.study.iot.mqtt.store.container;

import com.study.iot.mqtt.common.enums.CacheEnum;
import com.study.iot.mqtt.store.strategy.CacheStrategyContainer;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:55
 */

public class StorageContainerManager implements ContainerManager {

    private final CacheStrategyContainer container;

    private CacheEnum cacheEnum;

    public StorageContainerManager(CacheStrategyContainer container) {
        this.container = container;
    }

    @Override
    public void strategy(CacheEnum cacheEnum) {
        this.cacheEnum = cacheEnum;
    }

    @Override
    public <T> StorageContainer<T> take(String cacheGroup) {
        return container.getStrategy(cacheGroup, cacheEnum);
    }

    @Override
    public TopicContainer topic(String cacheGroup) {
        return container.getStrategy(cacheGroup, cacheEnum);
    }

    @Override
    public MetricContainer metric(String cacheGroup) {
        return container.getStrategy(cacheGroup, cacheEnum);
    }
}
