package com.study.iot.mqtt.store.container.redis;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.TopicContainer;
import com.study.iot.mqtt.store.redis.RedisCacheTemplate;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.TOPIC, type = CacheStrategy.MEMORY)
public class RedisTopicContainer implements TopicContainer {

    @Autowired
    private RedisCacheTemplate redisTemplate;

    @Override
    public List<Disposable> getConnections(String topic) {
        return (List<Disposable>) Optional
            .ofNullable(redisTemplate.hmget(CacheGroup.TOPIC, Disposable.class, topic)).map(Map::values)
            .orElse(Lists.newArrayList());
    }

    @Override
    public void add(String topic, Disposable disposable) {
        redisTemplate.hset(CacheGroup.TOPIC, topic, disposable);
    }

    @Override
    public void remove(String topic, Disposable disposable) {
        redisTemplate.hdel(CacheGroup.TOPIC, topic);
    }
}
