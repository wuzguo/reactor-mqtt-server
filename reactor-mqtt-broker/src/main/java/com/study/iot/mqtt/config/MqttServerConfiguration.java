package com.study.iot.mqtt.config;

import com.study.iot.mqtt.cache.manager.DefaultCacheManager;
import com.study.iot.mqtt.server.MqttServer;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MqttServerConfiguration {

    @Autowired
    private ServerMessageRouter messageRouter;

    @Autowired
    private DefaultCacheManager cacheManager;

    @Bean
    @ConditionalOnMissingBean
    public MqttServer mqttServer() {
        return new MqttServer(cacheManager, messageRouter);
    }
}
