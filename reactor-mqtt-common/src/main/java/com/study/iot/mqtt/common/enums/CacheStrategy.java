package com.study.iot.mqtt.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

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

    MEMORY(0, "内存"),
    IGNITE(1, "ignite"),
    REDIS(2, "redis");

    private Integer code;

    private String desc;

    public static CacheStrategy valueOf(Integer code) {
        for (CacheStrategy strategyEnum : CacheStrategy.values()) {
            if (Objects.equals(code, strategyEnum.code)) {
                return strategyEnum;
            }
        }
        return null;
    }
}
