package com.study.iot.mqtt.common.message;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 8:46
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportMessage implements Serializable {

    private String topic;

    private byte[] copyByteBuf;

    private Integer qos;

    private Boolean isRetain;

    private Boolean isDup;
}
