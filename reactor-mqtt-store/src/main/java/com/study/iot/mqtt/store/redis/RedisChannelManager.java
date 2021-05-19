package com.study.iot.mqtt.store.redis;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.disposable.SerializerDisposable;
import com.study.iot.mqtt.store.mapper.ChannelManager;
import com.study.iot.mqtt.store.strategy.CacheStrategy;
import com.study.iot.mqtt.store.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.utils.ObjectUtil;
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

@CacheStrategyService(group = CacheGroup.CHANNEL, type = CacheStrategy.REDIS)
public class RedisChannelManager implements ChannelManager {

    @Autowired
    private RedisCacheTemplate redisTemplate;

    @Override
    public void add(String identity, SerializerDisposable disposable) {
        redisTemplate.hset(CacheGroup.CHANNEL, identity, disposable);
    }

    @Override
    public void remove(String identity) {
        redisTemplate.hdel(CacheGroup.CHANNEL, identity);
    }

    @Override
    public SerializerDisposable getAndRemove(String identity) {
        SerializerDisposable disposable = redisTemplate.hget(CacheGroup.CHANNEL, identity, SerializerDisposable.class);
        redisTemplate.hdel(CacheGroup.CHANNEL, identity);
        return disposable;
    }

    @Override
    public Boolean containsKey(String identity) {
        return ObjectUtil.isNull(redisTemplate.hget(CacheGroup.CHANNEL, identity, Disposable.class));
    }

    @Override
    public List<SerializerDisposable> getConnections() {
        return Lists.newArrayList(redisTemplate.hmget(CacheGroup.CHANNEL, SerializerDisposable.class).values());
    }
}
