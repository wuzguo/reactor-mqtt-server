package com.study.iot.mqtt.cache.service.memory;

import com.google.common.collect.Maps;
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

    private final Map<String, LongAdder> mapMetric = Maps.newConcurrentMap();

    @Override
    public void add(String key, LongAdder count) {
        mapMetric.put(key, count);
    }

    @Override
    public void remove(String key) {
        mapMetric.remove(key);
    }

    @Override
    public LongAdder getAndRemove(String key) {
        return mapMetric.remove(key);
    }

    @Override
    public Boolean containsKey(String key) {
        return mapMetric.containsKey(key);
    }

    @Override
    public Map<String, LongAdder> loadAll() {
        return mapMetric;
    }
}
