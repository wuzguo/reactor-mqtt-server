package com.study.iot.mqtt.store.container.ignites;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.MetricContainer;
import com.study.iot.mqtt.store.properties.IgniteProperties;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import javax.annotation.Resource;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:30
 */

@ConditionalOnBean(value = IgniteProperties.class)
@CacheStrategyService(group = CacheGroup.METRIC, type = CacheStrategy.IGNITE)
public class IgniteMetricContainer implements MetricContainer {

    @Resource
    private IgniteCache<String, LongAdder> metricCache;

    @Override
    public Mono<Void> increase(String key) {
        LongAdder adder = Optional.ofNullable(metricCache.get(key)).orElse(new LongAdder());
        adder.add(1);
        metricCache.put(key, adder);
        return Mono.empty();
    }

    @Override
    public Mono<Void> increase(String key, Long count) {
        LongAdder adder = Optional.ofNullable(metricCache.get(key)).orElse(new LongAdder());
        adder.add(count);
        metricCache.put(key, adder);
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key) {
        LongAdder adder = Optional.ofNullable(metricCache.get(key)).orElse(new LongAdder());
        if (adder.longValue() > 1) {
            adder.increment();
        }
        metricCache.put(key, adder);
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key, Long count) {
        LongAdder adder = Optional.ofNullable(metricCache.get(key)).orElse(new LongAdder());
        if (adder.longValue() > count) {
            adder.add(-count);
        }
        metricCache.put(key, adder);
        return Mono.empty();
    }

    @Override
    public Mono<Void> remove(String key) {
        metricCache.remove(key);
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> containsKey(String key) {
        return Mono.just(metricCache.containsKey(key));
    }

    @Override
    public Mono<Map<String, LongAdder>> loadAll() {
        Map<String, LongAdder> mapAdder = Maps.newHashMap();
        metricCache.query(new ScanQuery<String, LongAdder>())
            .getAll()
            .stream()
            .map(adderEntry -> mapAdder.put(adderEntry.getKey(), adderEntry.getValue()));
        return Mono.just(mapAdder);
    }
}
