package com.study.iot.mqtt.cache.service.redis;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.TopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.template.RedisOpsTemplate;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.List;
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
public class RedisTopicManager implements TopicManager {

    @Autowired
    private RedisOpsTemplate redisOpsTemplate;

    @Override
    public List<Disposable> getConnections(String topic) {
        return Optional.ofNullable(redisOpsTemplate.getList(topic, Disposable.class))
            .orElse(Lists.newArrayList());
    }

    @Override
    public void add(String topic, Disposable disposable) {
        redisOpsTemplate.sadd(topic, disposable);
    }

    @Override
    public void remove(String topic, Disposable disposable) {
        redisOpsTemplate.del(topic);
    }
}
