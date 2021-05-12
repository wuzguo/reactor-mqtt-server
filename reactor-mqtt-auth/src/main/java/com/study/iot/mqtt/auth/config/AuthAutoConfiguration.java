package com.study.iot.mqtt.auth.config;

import com.study.iot.mqtt.auth.service.DefaultConnectAuthentication;
import com.study.iot.mqtt.auth.service.ConnectAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 8:34
 */

@Slf4j
@Configuration
public class AuthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConnectAuthentication authService() {
        return new DefaultConnectAuthentication();
    }
}
