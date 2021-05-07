package com.study.iot.mqtt.cache.memory;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.TopicManager;
import com.study.iot.mqtt.cache.path.CacheTopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.TransportConnection;
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
public class MemoryTopicManager implements TopicManager {

    private final CacheTopicManager topicManager = new CacheTopicManager();

    @Override
    public List<TransportConnection> getConnections(String topic) {
        return topicManager.getConnections(topic).orElse(Lists.newArrayList());
    }

    @Override
    public void addConnection(String topic, TransportConnection connection) {
        topicManager.addConnection(topic, connection);
    }

    @Override
    public void deleteConnection(String topic, TransportConnection connection) {
        topicManager.deleteConnection(topic, connection);
    }
}
