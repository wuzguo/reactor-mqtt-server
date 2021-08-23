package com.study.iot.mqtt.transport.strategy;

import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/18 15:55
 */

public enum StrategyEnum {
    /**
     * 连接服务端
     */
    CONNECT(1),
    /**
     * 连接报文确认
     */
    CONNACK(2),
    /**
     * 发布消息
     */
    PUBLISH(3),
    /**
     * QoS 1消息发布收到确认
     */
    PUBACK(4),
    /**
     * 发布收到（保证交付第一步）
     */
    PUBREC(5),
    /**
     * 发布释放（保证交付第二步）
     */
    PUBREL(6),
    /**
     * QoS 2消息发布完成（保证交互第三步）
     */
    PUBCOMP(7),
    /**
     * 客户端订阅请求
     */
    SUBSCRIBE(8),
    /**
     * 订阅请求报文确认
     */
    SUBACK(9),
    /**
     * 客户端取消订阅请求
     */
    UNSUBSCRIBE(10),
    /**
     * 取消订阅报文确认
     */
    UNSUBACK(11),
    /**
     * 心跳请求
     */
    PINGREQ(12),
    /**
     * 心跳响应
     */
    PINGRESP(13),
    /**
     * 客户端断开连接
     */
    DISCONNECT(14),
    /**
     * 至多一次
     */
    AT_MOST_ONCE(100),
    /**
     * 最少一次
     */
    AT_LEAST_ONCE(101),
    /**
     * 仅有一次
     */
    EXACTLY_ONCE(102),
    /**
     * 失败
     */
    FAILURE(0x80);

    private final int value;

    StrategyEnum(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static StrategyEnum valueOf(int type) {
        for (StrategyEnum strategy : values()) {
            if (strategy.value == type) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("unknown message type: " + type);
    }

    public static StrategyEnum valueOf(MqttMessageType type) {
        for (StrategyEnum strategy : values()) {
            if (strategy.value == type.value()) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("unknown message type: " + type);
    }

    public static StrategyEnum valueOf(MqttQoS type) {
        if (type == MqttQoS.FAILURE) {
            return StrategyEnum.FAILURE;
        }

        for (StrategyEnum strategy : values()) {
            if (strategy.value == (type.value() + 100)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("unknown message type: " + type);
    }
}
