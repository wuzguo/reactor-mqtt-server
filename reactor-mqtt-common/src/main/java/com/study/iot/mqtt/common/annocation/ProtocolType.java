package com.study.iot.mqtt.common.annocation;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 8:46
 */

@Getter
@AllArgsConstructor
public enum ProtocolType {

    MQTT(0, "MQTT"),
    WEB_SOCKET(2, "WEB SOCKET"),
    COAP(3, "COAP"),
    HTTP(4, "HTTP");

    /**
     * 类型
     */
    private Integer code;

    /**
     * 描述
     */
    private String value;

    /**
     * 类型转换
     *
     * @param code 编码
     * @return {@link ProtocolType}
     */
    public static ProtocolType valueOf(Integer code) {
        for (ProtocolType typeEnum : ProtocolType.values()) {
            if (Objects.equals(code, typeEnum.code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
