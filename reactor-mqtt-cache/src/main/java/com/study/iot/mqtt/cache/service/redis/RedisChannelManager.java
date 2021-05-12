package com.study.iot.mqtt.cache.service.redis;


import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.template.RedisOpsTemplate;
import com.study.iot.mqtt.cache.strategy.CacheStrategy;
import com.study.iot.mqtt.common.utils.ObjectUtil;
import java.util.Collection;
import java.util.Collections;
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
    private RedisOpsTemplate redisOpsTemplate;

    @Override
    public void add(String identity, Disposable disposable) {
        redisOpsTemplate.sadd(identity, disposable);
    }

    @Override
    public void remove(String identity) {
        redisOpsTemplate.del(identity);
    }

    @Override
    public Disposable getAndRemove(String identity) {
        Disposable disposable = redisOpsTemplate.get(identity, Disposable.class);
        redisOpsTemplate.del(identity);
        return disposable;
    }

    @Override
    public Boolean containsKey(String identity) {
        return ObjectUtil.isNull(redisOpsTemplate.get(identity, Disposable.class));
    }

    @Override
    public Collection<Disposable> getConnections() {
        return Collections.emptyList();
    }
}
