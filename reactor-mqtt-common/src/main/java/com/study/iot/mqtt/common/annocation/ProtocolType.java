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
    WS(2, "Websocket"),
    COAP(3, "CoAP"),
    HTTP(4, "Http"),
    XMPP(5, "XMPP");

    /**
     * 类型
     */
    private Integer value;

    /**
     * 描述
     */
    private String desc;

    /**
     * 类型转换
     *
     * @param value 编码
     * @return {@link ProtocolType}
     */
    public static ProtocolType valueOf(Integer value) {
        for (ProtocolType typeEnum : ProtocolType.values()) {
            if (Objects.equals(value, typeEnum.value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
