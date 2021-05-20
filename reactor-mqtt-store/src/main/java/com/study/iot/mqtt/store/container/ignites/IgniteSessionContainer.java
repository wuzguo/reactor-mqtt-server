package com.study.iot.mqtt.store.container.ignites;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.io.Serializable;
import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/20 9:15
 */

@CacheStrategyService(group = CacheGroup.SESSION, type = CacheStrategy.IGNITE)
public class IgniteSessionContainer implements StorageContainer {

    @Override
    public void add(String key, Serializable value) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public Serializable get(String key) {
        return null;
    }

    @Override
    public List list(String key) {
        return null;
    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public Serializable getAndRemove(String key) {
        return null;
    }

    @Override
    public Boolean containsKey(String key) {
        return null;
    }
}
