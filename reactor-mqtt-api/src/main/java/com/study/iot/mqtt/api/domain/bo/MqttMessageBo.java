package com.study.iot.mqtt.api.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/11 16:36
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MqttMessageBo {

    /**
     * TOPIC
     */
    private String topic;

    /**
     * 消息体
     */
    private String message;
}
