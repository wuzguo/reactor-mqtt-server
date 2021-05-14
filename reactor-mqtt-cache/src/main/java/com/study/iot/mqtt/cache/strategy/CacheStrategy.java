package com.study.iot.mqtt.cache.strategy;

import com.study.iot.mqtt.common.utils.StringUtil;
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

    MEMORY(0, "memory"),
    IGNITE(1, "ignite"),
    REDIS(2, "redis");

    private Integer value;

    private String name;

    public static CacheStrategy fromName(String name) {
        for (CacheStrategy strategyEnum : CacheStrategy.values()) {
            if (StringUtil.equalsIgnoreCase(name, strategyEnum.name)) {
                return strategyEnum;
            }
        }
        return null;
    }
}
