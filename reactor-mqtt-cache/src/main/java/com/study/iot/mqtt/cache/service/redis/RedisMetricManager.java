package com.study.iot.mqtt.cache.service.redis;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.MetricManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.template.RedisOpsTemplate;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.common.utils.ObjectUtil;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import org.springframework.beans.factory.annotation.Autowired;

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
    public void increase(String key) {
        LongAdder total = redisOpsTemplate.get(key, LongAdder.class);
        total.increment();
        redisOpsTemplate.setex(key, total);
    }

    @Override
    public void increase(String key, LongAdder count) {
        LongAdder total = redisOpsTemplate.get(key, LongAdder.class);
        total.add(count.longValue());
        redisOpsTemplate.setex(key, total);
    }

    @Override
    public void decrease(String key) {
        LongAdder total = redisOpsTemplate.get(key, LongAdder.class);
        total.decrement();
        redisOpsTemplate.setex(key, total);
    }

    @Override
    public void decrease(String key, LongAdder count) {
        LongAdder total = redisOpsTemplate.get(key, LongAdder.class);
        total.add(-count.longValue());
        redisOpsTemplate.setex(key, total);
    }

    @Override
    public void remove(String key) {
        redisOpsTemplate.del(key);

    }

    @Override
    public Boolean containsKey(String key) {
        return ObjectUtil.isNull(redisOpsTemplate.get(key, LongAdder.class));
    }

    @Override
    public Map<String, LongAdder> loadAll() {
        return Maps.newHashMap();
    }
}
