package com.study.iot.mqtt.store.container.redis;

import com.study.iot.mqtt.common.message.RetainMessage;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.store.redis.RedisCacheTemplate;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.MESSAGE, type = CacheStrategy.REDIS)
public class RedisMessageContainer implements StorageContainer<RetainMessage> {

    @Autowired
    private RedisCacheTemplate redisTemplate;

    @Override
    public void add(String key, RetainMessage value) {
        redisTemplate.hset(CacheGroup.MESSAGE, key, value);
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public RetainMessage get(String key) {
        return redisTemplate.hget(CacheGroup.MESSAGE, key, RetainMessage.class);
    }

    @Override
    public List<RetainMessage> list(String key) {
        return null;
    }

    @Override
    public List<RetainMessage> getAll() {
        return null;
    }

    @Override
    public RetainMessage getAndRemove(String key) {
        return null;
    }

    @Override
    public Boolean containsKey(String key) {
        return null;
    }
}
