package com.study.iot.mqtt.cache.memory;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.constant.CacheGroup;
import com.study.iot.mqtt.cache.manager.TopicManager;
import com.study.iot.mqtt.cache.path.CacheTopicManager;
import com.study.iot.mqtt.cache.strategy.CacheStrategyService;
import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.common.enums.CacheStrategy;

import java.util.List;

@CacheStrategyService(group = CacheGroup.TOPIC, type = CacheStrategy.MEMORY)
public class MemoryTopicManager implements TopicManager {


    private CacheTopicManager topicManager = new CacheTopicManager();

    @Override
    public List<TransportConnection> getConnectionsByTopic(String topic) {
        return topicManager.getTopicConnection(topic).orElse(Lists.newArrayList());
    }

    @Override
    public void addTopicConnection(String topic, TransportConnection connection) {
        topicManager.addTopicConnection(topic, connection);
    }

    @Override
    public void deleteTopicConnection(String topic, TransportConnection connection) {
        topicManager.deleteTopicConnection(topic, connection);
    }

}
