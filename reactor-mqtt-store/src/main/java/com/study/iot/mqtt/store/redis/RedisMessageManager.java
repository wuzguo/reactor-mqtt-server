package com.study.iot.mqtt.store.redis;

import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.manager.MessageManager;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import com.study.iot.mqtt.store.redis.RedisCacheTemplate;
import com.study.iot.mqtt.common.message.RetainMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.MESSAGE, type = CacheStrategy.REDIS)
public class RedisMessageManager implements MessageManager {

    @Autowired
    private RedisCacheTemplate redisTemplate;

    @Override
    public void saveRetain(RetainMessage message) {
        redisTemplate.hset(CacheGroup.MESSAGE, message.getTopic(), message);
    }

    @Override
    public RetainMessage getRetain(String topicName) {
        return redisTemplate.hget(CacheGroup.MESSAGE, topicName, RetainMessage.class);
    }
}