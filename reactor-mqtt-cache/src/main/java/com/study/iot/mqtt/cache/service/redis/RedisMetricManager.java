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
    public void add(String key, LongAdder count) {
        redisOpsTemplate.sadd(key, count);
    }

    @Override
    public void remove(String key) {
        redisOpsTemplate.del(key);

    }

    @Override
    public LongAdder getAndRemove(String key) {
        LongAdder adder = redisOpsTemplate.get(key, LongAdder.class);
        redisOpsTemplate.del(key);
        return adder;
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
