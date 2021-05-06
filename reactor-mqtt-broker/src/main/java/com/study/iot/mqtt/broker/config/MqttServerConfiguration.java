package com.study.iot.mqtt.broker.config;

import com.study.iot.mqtt.broker.server.MqttServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MqttServerConfiguration {

    @Bean
    public MqttServer mqttServer() {
        return new MqttServer();
    }
}
