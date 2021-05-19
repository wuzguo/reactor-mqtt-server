package com.study.iot.mqtt.api.service.impl;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.api.service.IMqttMetricService;
import com.study.iot.mqtt.store.mapper.StoreMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 10:23
 */

@Slf4j
@Service
public class MqttMetricServiceImpl implements IMqttMetricService {

    @Autowired
    private StoreMapper storeMapper;

    @Override
    public Mono<Map<String, Long>> stat(String matterName) {
        return storeMapper.metric().loadAll().map(mapLongAdder -> {
            Map<String, Long> metrics = Maps.newHashMap();
            mapLongAdder.forEach((key, adder) -> metrics.put(key, adder.longValue()));
            return metrics;
        }).doOnError((throwable) -> log.error("错误信息: {}", throwable.getMessage()));
    }
}
