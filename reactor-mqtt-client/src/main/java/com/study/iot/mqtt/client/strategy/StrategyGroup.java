package com.study.iot.mqtt.client.strategy;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 11:50
 */

public interface StrategyGroup {

    /**
     * 连接消息
     */
    String CONNECT = "mqtt.connect";

    /**
     * 发布消息
     */
    String PUBLISH = "mqtt.publish";
}
