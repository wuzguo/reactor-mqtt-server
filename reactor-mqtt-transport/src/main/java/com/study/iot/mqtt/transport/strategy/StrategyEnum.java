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
    CONNECT(1),
    CONNACK(2),
    PUBLISH(3),
    PUBACK(4),
    PUBREC(5),
    PUBREL(6),
    PUBCOMP(7),
    SUBSCRIBE(8),
    SUBACK(9),
    UNSUBSCRIBE(10),
    UNSUBACK(11),
    PINGREQ(12),
    PINGRESP(13),
    DISCONNECT(14),

    AT_MOST_ONCE(100),
    AT_LEAST_ONCE(101),
    EXACTLY_ONCE(102),
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
