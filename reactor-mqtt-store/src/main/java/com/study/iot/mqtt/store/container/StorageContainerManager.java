package com.study.iot.mqtt.store.container;

import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyContainer;
import java.io.Serializable;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:55
 */

public class StorageContainerManager implements ContainerManager {

    private final CacheStrategyContainer container;

    private CacheStrategy strategy;

    public StorageContainerManager(CacheStrategyContainer container) {
        this.container = container;
    }

    @Override
    public void strategy(CacheStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public <T> StorageContainer<T> take(String cacheGroup) {
        return container.getStrategy(cacheGroup, strategy);
    }

    @Override
    public TopicContainer topic(String cacheGroup) {
        return container.getStrategy(cacheGroup, strategy);
    }

    @Override
    public MetricContainer metric(String cacheGroup) {
        return container.getStrategy(cacheGroup, strategy);
    }
}
