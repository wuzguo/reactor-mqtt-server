package com.study.iot.mqtt.transport.config;

import com.study.iot.mqtt.transport.router.ServerMessageRouter;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
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
public class TransportAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public StrategyContainer strategyContainer(ApplicationContext applicationContext) {
        return new StrategyContainer(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerMessageRouter serverMessageRouter(StrategyContainer container) {
        return new ServerMessageRouter(container);
    }
}


