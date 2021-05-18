package com.study.iot.mqtt.akka.topic;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 14:27
 */

public interface AkkaTopic {

    /**
     * 订阅事件TOPIC
     */
    String SUB_EVENT = "akka.sub.event";

    /**
     * 取消订阅TOPIC
     */
    String UN_SUB_EVENT = "akka.unsub.event";

    /**
     * 连接事件Topic
     */
    String CONNECT_EVENT = "akka.connect.event";
}
