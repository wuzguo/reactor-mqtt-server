package com.study.iot.mqtt.common.enums;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:29
 */

@Getter
@AllArgsConstructor
public enum CacheStrategy {

    MEMORY(0),
    IGNITE(1),
    REDIS(2);

    private Integer value;

    public static CacheStrategy valueOf(Integer value) {
        for (CacheStrategy strategyEnum : CacheStrategy.values()) {
            if (Objects.equals(value, strategyEnum.value)) {
                return strategyEnum;
            }
        }
        return null;
    }
}
