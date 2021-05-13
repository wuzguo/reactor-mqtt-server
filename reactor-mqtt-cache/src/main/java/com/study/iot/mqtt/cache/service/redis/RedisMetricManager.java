package com.study.iot.mqtt.cache.service.redis;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.MetricManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.template.RedisOpsTemplate;
import com.study.iot.mqtt.common.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:31
 */

@CacheStrategyService(group = CacheGroup.METRIC, type = CacheStrategy.REDIS)
public class RedisMetricManager implements MetricManager {

    @Autowired
    private RedisOpsTemplate redisOpsTemplate;

    @Override
    public Mono<Void> increase(String key) {
        redisOpsTemplate.incr(CacheGroup.METRIC, key);
        return Mono.empty();
    }

    @Override
    public Mono<Void> increase(String key, LongAdder count) {
        redisOpsTemplate.incrBy(CacheGroup.METRIC, key, count.longValue());
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key) {
        redisOpsTemplate.decr(CacheGroup.METRIC, key);
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key, LongAdder count) {
        redisOpsTemplate.decrBy(CacheGroup.METRIC, key, count.longValue());
        return Mono.empty();
    }

    @Override
    public Mono<Void> remove(String key) {
        redisOpsTemplate.del(CacheGroup.METRIC, key);
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> containsKey(String key) {
        return Mono.just(ObjectUtil.isNull(redisOpsTemplate.get(CacheGroup.METRIC, key, LongAdder.class)));
    }

    @Override
    public Mono<Map<String, LongAdder>> loadAll() {
        return Mono.just(Maps.newHashMap());
    }
}
