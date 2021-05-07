package com.study.iot.mqtt.cache.manager;

import com.study.iot.mqtt.cache.strategy.CacheCapable;
import com.study.iot.mqtt.common.connection.TransportConnection;

import java.util.List;


public interface TopicManager extends CacheCapable {

    List<TransportConnection> getConnectionsByTopic(String topic);

    void addTopicConnection(String topic, TransportConnection connection);

    void deleteTopicConnection(String topic, TransportConnection connection);
}
