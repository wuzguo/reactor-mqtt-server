package com.study.iot.mqtt.api.domain.vo;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/11 16:31
 */

@Data
public class MqttMessageVo {

    /**
     * TOPIC
     */
    @NotBlank(message = "Topic不能为空")
    private String topic;

    /**
     * 消息体
     */
    @NotBlank(message = "消息体不能为空")
    private String message;
}
