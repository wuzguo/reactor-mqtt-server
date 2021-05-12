package com.study.iot.mqtt.api.service.impl;

import com.study.iot.mqtt.api.domain.bo.MqttMessageBo;
import com.study.iot.mqtt.api.service.IMqttMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/11 16:40
 */

@Slf4j
@Service
public class MqttMessageServiceImpl implements IMqttMessageService {

    @Override
    public Mono<Void> send(MqttMessageBo messageBo) {
        return null;
    }
}
