package com.study.iot.mqtt.session.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/14 10:21
 */

@Data
@Configuration
public class InstanceUtil {

    /**
     * IP地址
     */
    @Value("${spring.mqtt.broker.host}")
    private String host;

    /**
     * 端口号
     */
    @Value("${spring.mqtt.broker.port}")
    private Integer port;

    /**
     * 获取实例ID
     *
     * @return {@link String}
     */
    public String getInstanceId() {
        return host + ":" + port;
    }
}
