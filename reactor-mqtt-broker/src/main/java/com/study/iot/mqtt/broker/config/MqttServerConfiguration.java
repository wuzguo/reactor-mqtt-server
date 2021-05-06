package com.study.iot.mqtt.broker.config;

import com.study.iot.mqtt.broker.server.MqttServer;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MqttServerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MqttServer mqttServer(@Qualifier("strategyContainer") StrategyContainer container) {
        return new MqttServer(container);
    }
}
