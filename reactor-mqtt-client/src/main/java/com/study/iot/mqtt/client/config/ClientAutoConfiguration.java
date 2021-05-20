package com.study.iot.mqtt.client.config;

import com.study.iot.mqtt.client.router.ClientMessageRouter;
import com.study.iot.mqtt.client.strategy.PublishStrategyContainer;
import com.study.iot.mqtt.client.strategy.StrategyContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/6 14:23
 */

@Configuration
public class ClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StrategyContainer strategyContainer(ApplicationContext applicationContext) {
        return new StrategyContainer(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public PublishStrategyContainer publishStrategyContainer(ApplicationContext applicationContext) {
        return new PublishStrategyContainer(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientMessageRouter clientMessageRouter(StrategyContainer container) {
        return new ClientMessageRouter(container);
    }
}


