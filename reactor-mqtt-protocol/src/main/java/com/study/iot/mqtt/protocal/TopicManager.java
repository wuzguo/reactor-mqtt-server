package com.study.iot.mqtt.protocal;

import java.util.List;


public interface TopicManager {

    List<TransportConnection> getConnectionsByTopic(String topic);

    void addTopicConnection(String topic, TransportConnection connection);

    void deleteTopicConnection(String topic, TransportConnection connection);
}
