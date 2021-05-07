package com.study.iot.mqtt.common.service;

import com.study.iot.mqtt.common.session.SessionStore;

/**
 * 会话存储服务接口
 */
public interface ISessionStoreService {

    /**
     * 存储会话
     */
    void put(String clientId, SessionStore sessionStore);

    /**
     * 获取会话
     */
    SessionStore get(String clientId);

    /**
     * clientId的会话是否存在
     */
    boolean containsKey(String clientId);

    /**
     * 删除会话
     */
    void remove(String clientId);

}
