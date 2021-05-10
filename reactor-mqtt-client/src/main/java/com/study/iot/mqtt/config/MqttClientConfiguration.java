package com.study.iot.mqtt.config;

import com.study.iot.mqtt.server.MqttClient;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/10 17:21
 */

@Configuration
public class MqttClientConfiguration {

    @Autowired
    private ClientMessageRouter messageRouter;

    @Bean
    @ConditionalOnMissingBean
    public MqttClient mqttServer() {
        return new MqttClient(messageRouter);
    }
}
