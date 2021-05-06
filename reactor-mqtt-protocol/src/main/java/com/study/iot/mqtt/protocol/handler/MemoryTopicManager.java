package com.study.iot.mqtt.protocol.handler;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.protocol.TopicManager;
import com.study.iot.mqtt.protocol.TransportConnection;
import com.study.iot.mqtt.protocol.path.CacheTopicManager;

import java.util.List;

public class MemoryTopicManager implements TopicManager {


    private CacheTopicManager topicManager = new CacheTopicManager();

    @Override
    public List<TransportConnection> getConnectionsByTopic(String topic) {
        return topicManager.getTopicConnection(topic).orElse(Lists.newArrayList());
    }

    @Override
    public void addTopicConnection(String topic, TransportConnection connection) {
        topicManager.addTopicConnection(topic,connection);
    }

    @Override
    public void deleteTopicConnection(String topic, TransportConnection connection) {
        topicManager.deleteTopicConnection(topic,connection);
    }

}
