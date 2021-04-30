package com.study.iot.mqtt.protocal.handler;


import com.google.common.collect.Lists;
import com.study.iot.mqtt.protocal.TopicManager;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.protocal.path.CacheTopicManager;

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
