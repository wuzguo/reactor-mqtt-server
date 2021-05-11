package com.study.iot.mqtt.cache.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ignite属性配置
 */

@Data
@ConfigurationProperties(prefix = "spring.mqtt.broker.cache")
@ConditionalOnProperty(value = "spring.cache.mode", havingValue = "ignite")
@Configuration
public class IgniteProperties {

    /**
     * 持久化缓存内存初始化大小(MB), 默认值: 64
     */
    private int persistenceInitialSize = 64;

    /**
     * 持久化缓存占用内存最大值(MB), 默认值: 128
     */
    private int persistenceMaxSize = 128;

    /**
     * 持久化磁盘存储路径
     */
    private String persistenceStorePath;

    /**
     * 非持久化缓存内存初始化大小(MB), 默认值: 64
     */
    private int notPersistenceInitialSize = 64;

    /**
     * 非持久化缓存占用内存最大值(MB), 默认值: 128
     */
    private int notPersistenceMaxSize = 128;
}
