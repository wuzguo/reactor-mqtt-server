package com.study.iot.mqtt.transport.metric;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 8:54
 */

@Getter
@AllArgsConstructor
public enum MetricType {

    INCREASE(0),
    DECREASE(1);

    private int value;


    public static MetricType valueOf(Integer value) {
        for (MetricType typeEnum : MetricType.values()) {
            if (Objects.equals(value, typeEnum.value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
