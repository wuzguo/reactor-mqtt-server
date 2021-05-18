package com.study.iot.mqtt.store.service.redis;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.service.MetricManager;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import com.study.iot.mqtt.store.template.RedisCacheTemplate;
import com.study.iot.mqtt.common.utils.ObjectUtil;
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

@CacheStrategyService(group = CacheGroup.METRIC, type = CacheStrategy.REDIS)
public class RedisMetricManager implements MetricManager {

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
        return Mono.just(ObjectUtil.isNull(redisTemplate.hget(CacheGroup.METRIC, key, LongAdder.class)));
    }

    @Override
    public Mono<Map<String, LongAdder>> loadAll() {
        return Mono.just(redisTemplate.hmget(CacheGroup.METRIC, LongAdder.class));
    }
}
