package com.study.iot.mqtt.cache.service.ignite;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.cache.config.IgniteProperties;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.MetricManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import javax.annotation.Resource;
import org.apache.ignite.IgniteCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

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
    public void add(String key, LongAdder count) {
        metricCache.put(key, count);
    }

    @Override
    public void remove(String key) {
        metricCache.remove(key);
    }

    @Override
    public LongAdder getAndRemove(String key) {
        return metricCache.getAndRemove(key);
    }

    @Override
    public Boolean containsKey(String key) {
        return metricCache.containsKey(key);
    }

    @Override
    public Map<String, LongAdder> loadAll() {
        return Maps.newHashMap();
    }
}
