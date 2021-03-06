package com.study.iot.mqtt.store.container.redis;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.MetricContainer;
import com.study.iot.mqtt.store.redis.RedisCacheTemplate;
import com.study.iot.mqtt.common.enums.CacheEnum;
import com.study.iot.mqtt.store.strategy.StrategyService;
import com.study.iot.mqtt.common.utils.ObjectUtils;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:31
 */

@StrategyService(group = CacheGroup.METRIC, type = CacheEnum.REDIS)
public class RedisMetricContainer implements MetricContainer {

    @Autowired
    private RedisCacheTemplate redisTemplate;

    @Override
    public Mono<Void> increase(String key) {
        redisTemplate.hIncr(CacheGroup.METRIC, key);
        return Mono.empty();
    }

    @Override
    public Mono<Void> increase(String key, Long count) {
        redisTemplate.hIncrBy(CacheGroup.METRIC, key, count);
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key) {
        redisTemplate.hDecr(CacheGroup.METRIC, key);
        return Mono.empty();
    }

    @Override
    public Mono<Void> decrease(String key, Long count) {
        redisTemplate.hDecrBy(CacheGroup.METRIC, key, count);
        return Mono.empty();
    }

    @Override
    public Mono<Void> remove(String key) {
        redisTemplate.hdel(CacheGroup.METRIC, key);
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> containsKey(String key) {
        return Mono.just(ObjectUtils.isNull(redisTemplate.hget(CacheGroup.METRIC, key, LongAdder.class)));
    }

    @Override
    public Mono<Map<String, LongAdder>> loadAll() {
        return Mono.just(redisTemplate.hmget(CacheGroup.METRIC, LongAdder.class));
    }
}
