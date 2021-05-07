package com.study.iot.mqtt.cache.redis;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.TopicManager;
import com.study.iot.mqtt.cache.path.CacheTopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;

import java.util.List;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@CacheStrategyService(group = CacheGroup.TOPIC, type = CacheStrategy.MEMORY)
public class RedisTopicManager implements TopicManager {

    private final CacheTopicManager topicManager = new CacheTopicManager();

    @Override
    public List<DisposableConnection> getConnections(String topic) {
        return topicManager.getConnections(topic).orElse(Lists.newArrayList());
    }

    @Override
    public void addConnection(String topic, DisposableConnection connection) {
        topicManager.addConnection(topic, connection);
    }

    @Override
    public void deleteConnection(String topic, DisposableConnection connection) {
        topicManager.deleteConnection(topic, connection);
    }
}
