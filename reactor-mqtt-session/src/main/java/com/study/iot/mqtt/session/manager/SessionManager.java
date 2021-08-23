package com.study.iot.mqtt.session.manager;

import com.study.iot.mqtt.common.domain.ConnectSession;
import com.study.iot.mqtt.common.message.SessionMessage;
import com.study.iot.mqtt.common.message.WillMessage;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 13:39
 */

public interface SessionManager {

    /**
     * 创建Session
     *
     * @param instanceId     实例ID
     * @param identity       客户端标识
     * @param isCleanSession 是否清除Session
     */
    void add(String instanceId, String identity, Boolean isCleanSession);

    /**
     * 获取Session
     *
     * @param identity 连接标识
     * @return {@link ConnectSession}
     */
    ConnectSession get(String identity);

    /**
     * 添加消息
     *
     * @param identity 客户端标识
     * @param message  消息体
     */
    void saveAndTell(String identity, SessionMessage message);


    /**
     * 添加消息
     *
     * @param identity 客户端标识
     * @param message  消息体
     */
    void save(String identity, WillMessage message);

    /**
     * 订阅消息
     *
     * @param topic TOPIC
     */
    void doReady(String topic);
}
