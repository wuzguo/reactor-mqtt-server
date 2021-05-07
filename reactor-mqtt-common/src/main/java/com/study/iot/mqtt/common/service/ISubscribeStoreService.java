package com.study.iot.mqtt.common.service;

import com.study.iot.mqtt.common.subscribe.SubscribeStore;

import java.util.List;

/**
 * 订阅存储服务接口
 */
public interface ISubscribeStoreService {

    /**
     * 存储订阅
     */
    void put(String topicFilter, SubscribeStore subscribeStore);

    /**
     * 删除订阅
     */
    void remove(String topicFilter, String clientId);

    /**
     * 删除clientId的订阅
     */
    void removeForClient(String clientId);

    /**
     * 获取订阅存储集
     */
    List<SubscribeStore> search(String topic);
}
