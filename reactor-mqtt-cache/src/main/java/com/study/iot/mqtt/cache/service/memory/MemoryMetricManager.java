package com.study.iot.mqtt.cache.service.memory;

import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.MetricManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:28
 */

@CacheStrategyService(group = CacheGroup.METRIC, type = CacheStrategy.MEMORY)
public class MemoryMetricManager implements MetricManager {

    @Override
    public void add(String key, LongAdder count) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public LongAdder getAndRemove(String key) {
        return null;
    }

    @Override
    public Boolean containsKey(String key) {
        return null;
    }

    @Override
    public Map<String, LongAdder> loadAll() {
        return null;
    }
}
