package com.study.iot.mqtt.cache.service.memory;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.MetricManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.Map;
import java.util.Optional;
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
    public void increase(String key) {
        LongAdder total = Optional.ofNullable(mapMetric.get(key)).orElse(new LongAdder());
        total.increment();
        mapMetric.put(key, total);
    }

    @Override
    public void increase(String key, LongAdder count) {
        LongAdder total = Optional.ofNullable(mapMetric.get(key)).orElse(new LongAdder());
        total.add(count.longValue());
        mapMetric.put(key, total);
    }

    @Override
    public void decrease(String key) {
        LongAdder total = Optional.ofNullable(mapMetric.get(key)).orElse(new LongAdder());
        total.decrement();
        mapMetric.put(key, total);
    }

    @Override
    public void decrease(String key, LongAdder count) {
        LongAdder total = Optional.ofNullable(mapMetric.get(key)).orElse(new LongAdder());
        total.add(-count.longValue());
        mapMetric.put(key, total);
    }

    @Override
    public void remove(String key) {
        mapMetric.remove(key);
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
