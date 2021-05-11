package com.study.iot.mqtt.api.service;

import com.study.iot.mqtt.api.domain.bo.MqttMessageBo;
import javax.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/11 16:36
 */

public interface IMqttMessageService {

    /**
     * 发送消息
     *
     * @param messageBo {@link MqttMessageBo}
     * @return {@link Mono}
     */
    Mono<Void> send(@NotNull MqttMessageBo messageBo);
}
