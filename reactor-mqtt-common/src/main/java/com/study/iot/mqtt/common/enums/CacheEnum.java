package com.study.iot.mqtt.common.enums;

import com.study.iot.mqtt.common.utils.StringUtils;
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
public enum CacheEnum {

    MEMORY(0, "memory"),
    IGNITE(1, "ignite"),
    REDIS(2, "redis");

    private Integer value;

    private String name;

    public static CacheEnum from(String name) {
        for (CacheEnum cacheEnum : CacheEnum.values()) {
            if (StringUtils.equalsIgnoreCase(name, cacheEnum.name)) {
                return cacheEnum;
            }
        }
        return null;
    }
}
