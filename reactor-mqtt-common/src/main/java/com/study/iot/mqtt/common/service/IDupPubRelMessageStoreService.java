package com.study.iot.mqtt.common.service;

import com.study.iot.mqtt.common.message.DupPubRelMessageStore;

import java.util.List;

/**
 * PUBREL重发消息存储服务接口, 当QoS=2时存在该重发机制
 */
public interface IDupPubRelMessageStoreService {

    /**
     * 存储消息
     */
    void put(String clientId, DupPubRelMessageStore dupPubRelMessageStore);

    /**
     * 获取消息集合
     */
    List<DupPubRelMessageStore> get(String clientId);

    /**
     * 删除消息
     */
    void remove(String clientId, int messageId);

    /**
     * 删除消息
     */
    void removeByClient(String clientId);

}
