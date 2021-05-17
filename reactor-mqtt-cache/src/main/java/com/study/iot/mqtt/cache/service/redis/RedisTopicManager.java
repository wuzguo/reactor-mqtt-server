package com.study.iot.mqtt.cache.service.redis;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.disposable.SerializerDisposable;
import com.study.iot.mqtt.cache.service.TopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.template.RedisTemplate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.TOPIC, type = CacheStrategy.MEMORY)
public class RedisTopicManager implements TopicManager {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<SerializerDisposable> getConnections(String topic) {
        return (List<SerializerDisposable>) Optional
            .ofNullable(redisTemplate.hmget(CacheGroup.TOPIC, SerializerDisposable.class, topic)).map(Map::values)
            .orElse(Lists.newArrayList());
    }

    @Override
    public void add(String topic, SerializerDisposable disposable) {
        redisTemplate.hset(CacheGroup.TOPIC, topic, disposable);
    }

    @Override
    public void remove(String topic, SerializerDisposable disposable) {
        redisTemplate.hdel(CacheGroup.TOPIC, topic);
    }
}
