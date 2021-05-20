package com.study.iot.mqtt.session.manager;

import com.study.iot.mqtt.session.domain.BaseMessage;
import com.study.iot.mqtt.session.domain.ConnectSession;

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
     * @return {@link ConnectSession}
     */
    ConnectSession create(String instanceId, String identity, Boolean isCleanSession);


    /**
     * 添加消息
     *
     * @param identity 客户端标识
     * @param message  消息体
     */
    void add(String identity, BaseMessage message);

    /**
     * 订阅消息
     *
     * @param topic TOPIC
     */
    void subscribe(String topic);
}
