package com.study.iot.mqtt.store.config;

import com.study.iot.mqtt.store.mapper.DefaultStoreMapper;
import com.study.iot.mqtt.store.strategy.CacheStrategyContainer;
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
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheStrategyContainer cacheStrategyContainer(ApplicationContext applicationContext) {
        return new CacheStrategyContainer(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultStoreMapper cacheManager(CacheStrategyContainer container) {
        return new DefaultStoreMapper(container);
    }
}
