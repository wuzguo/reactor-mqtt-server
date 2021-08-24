package com.study.iot.mqtt.store.container.redis;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.store.redis.RedisCacheTemplate;
import com.study.iot.mqtt.common.enums.CacheEnum;
import com.study.iot.mqtt.store.strategy.StrategyService;
import com.study.iot.mqtt.common.utils.ObjectUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;


/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@StrategyService(group = CacheGroup.CHANNEL, type = CacheEnum.REDIS)
public class RedisChannelContainer implements StorageContainer<Disposable> {

    @Autowired
    private RedisCacheTemplate redisTemplate;

    @Override
    public void add(String identity, Disposable disposable) {
        redisTemplate.hset(CacheGroup.CHANNEL, identity, disposable);
    }

    @Override
    public void remove(String identity) {
        redisTemplate.hdel(CacheGroup.CHANNEL, identity);
    }

    @Override
    public Disposable get(String key) {
        return null;
    }

    @Override
    public List<Disposable> list(String key) {
        return null;
    }

    @Override
    public List<Disposable> getAll() {
        return Lists.newArrayList(redisTemplate.hmget(CacheGroup.CHANNEL, Disposable.class).values());
    }

    @Override
    public Disposable getAndRemove(String identity) {
        Disposable disposable = redisTemplate.hget(CacheGroup.CHANNEL, identity, Disposable.class);
        redisTemplate.hdel(CacheGroup.CHANNEL, identity);
        return disposable;
    }

    @Override
    public Boolean containsKey(String identity) {
        return ObjectUtils.isNull(redisTemplate.hget(CacheGroup.CHANNEL, identity, Disposable.class));
    }
}
