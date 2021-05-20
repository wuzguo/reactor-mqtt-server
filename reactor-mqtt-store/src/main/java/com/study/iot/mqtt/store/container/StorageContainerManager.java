package com.study.iot.mqtt.store.container;

import com.study.iot.mqtt.store.strategy.CacheStrategy;
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

    private CacheStrategy strategy;

    public StorageContainerManager(CacheStrategyContainer container) {
        this.container = container;
    }

    @Override
    public void strategy(CacheStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public StorageContainer get(String cacheGroup) {
        return container.getStrategy(cacheGroup, strategy);
    }
}
