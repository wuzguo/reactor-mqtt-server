package com.study.iot.mqtt.api.service;

import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 10:22
 */

public interface IMqttMetricService {

    /**
     * 统计数据
     *
     * @param matterName 事项
     * @return {@link Map<String, Long>}
     */
    Mono<Map<String, Long>> stat(String matterName);
}
