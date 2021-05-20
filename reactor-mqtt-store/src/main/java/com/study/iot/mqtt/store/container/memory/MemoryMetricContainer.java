package com.study.iot.mqtt.store.container.memory;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.MetricContainer;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:28
 */

@CacheStrategyService(group = CacheGroup.METRIC, type = CacheStrategy.MEMORY)
public class MemoryMetricContainer implements MetricContainer {

    private final Map<String, LongAdder> mapMetric = Maps.newConcurrentMap();

    @Override
    public Mono<Void> increase(String key) {
        LongAdder total = Optional.ofNullable(mapMetric.get(key)).orElse(new LongAdder());
        total.increment();
        mapMetric.put(key, total);
        return Mono.empty();
    }

    @Override
    public Mono<Void> increase(String key, Long count) {
        LongAdder total = Optional.ofNullable(mapMetric.get(key)).orElse(new LongAdder());
        total.add(count);
        mapMetric.put(key, total);
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key) {
        LongAdder total = Optional.ofNullable(mapMetric.get(key)).orElse(new LongAdder());
        total.decrement();
        mapMetric.put(key, total);
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key, Long count) {
        LongAdder total = Optional.ofNullable(mapMetric.get(key)).orElse(new LongAdder());
        total.add(-count);
        mapMetric.put(key, total);
        return Mono.empty();
    }

    @Override
    public Mono<Void> remove(String key) {
        mapMetric.remove(key);
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> containsKey(String key) {
        return Mono.just(mapMetric.containsKey(key));
    }

    @Override
    public Mono<Map<String, LongAdder>> loadAll() {
        return Mono.just(mapMetric);
    }
}
