package com.study.iot.mqtt.api.controller;

import com.study.iot.mqtt.api.domain.vo.ReqVo;
import com.study.iot.mqtt.api.service.IMqttMetricService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/11 16:50
 */

@Slf4j
@Validated
@RequestMapping("/mqtt/metric")
@RestController
public class MqttMetricController {

    @Autowired
    private IMqttMetricService mqttMetricService;

    /**
     * 统计数据
     *
     * @param matterVo {@link ReqVo}
     * @return {@link Mono<Map<String, Long>>}
     */
    @GetMapping(value = "/info")
    public Mono<Map<String, Long>> info(@RequestBody @Validated ReqVo<String> matterVo) {
        return mqttMetricService.stat(matterVo.getValue());
    }
}
