package com.study.iot.mqtt.api.controller;

import com.study.iot.mqtt.api.domain.bo.MqttMessageBo;
import com.study.iot.mqtt.api.domain.vo.MqttMessageVo;
import com.study.iot.mqtt.api.service.IMqttMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/11 16:28
 */


@Slf4j
@Validated
@RequestMapping("/mqtt/web")
@RestController
public class MqttWebController {

    @Autowired
    private IMqttMessageService mqttMessageService;

    @PostMapping(value = "/send")
    public Mono<Void> send(@RequestBody @Validated MqttMessageVo messageVo) {
        MqttMessageBo messageBo = MqttMessageBo.builder().topic(messageVo.getTopic())
            .message(messageVo.getMessage()).build();
        return mqttMessageService.send(messageBo);
    }
}
