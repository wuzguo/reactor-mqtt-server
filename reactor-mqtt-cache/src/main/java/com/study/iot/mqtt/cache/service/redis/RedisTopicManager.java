package com.study.iot.mqtt.cache.service.redis;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.service.TopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.cache.template.RedisOpsTemplate;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.List;
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
    private RedisOpsTemplate redisOpsTemplate;

    @Override
    public List<DisposableConnection> getConnections(String topic) {
        return Optional.ofNullable(redisOpsTemplate.getList(topic, DisposableConnection.class))
            .orElse(Lists.newArrayList());
    }

    @Override
    public void addConnection(String topic, DisposableConnection connection) {
        redisOpsTemplate.sadd(topic, connection);
    }

    @Override
    public void deleteConnection(String topic, DisposableConnection connection) {
        redisOpsTemplate.del(topic);
    }
}
