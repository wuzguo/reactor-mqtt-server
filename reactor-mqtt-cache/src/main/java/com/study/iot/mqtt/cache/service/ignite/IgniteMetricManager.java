package com.study.iot.mqtt.cache.service.ignite;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.config.IgniteProperties;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.MetricManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import javax.annotation.Resource;
import org.apache.ignite.IgniteCache;
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
public class IgniteMetricManager implements MetricManager {

    @Resource
    private IgniteCache<String, LongAdder> metricCache;

    @Override
    public Mono<Void> increase(String key) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> increase(String key, LongAdder count) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key, LongAdder count) {
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
        return Mono.just(Maps.newHashMap());
    }
}
