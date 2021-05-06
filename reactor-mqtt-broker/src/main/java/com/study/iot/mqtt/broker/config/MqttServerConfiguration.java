package com.study.iot.mqtt.broker.config;

import com.study.iot.mqtt.broker.server.MqttServer;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MqttServerConfiguration {

    @Autowired
    private StrategyContainer container;

    @Bean
    @ConditionalOnMissingBean
    public MqttServer mqttServer() {
        return new MqttServer(container);
    }
}
