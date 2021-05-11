package com.study.iot.mqtt.cache.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ignite属性配置
 */
@ConditionalOnProperty(value = "spring.mqtt.broker.cache")
@ConfigurationProperties(prefix = "spring.mqtt.broker.cache")
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
    private int NotPersistenceInitialSize = 64;

    /**
     * 非持久化缓存占用内存最大值(MB), 默认值: 128
     */
    private int NotPersistenceMaxSize = 128;

    public int getPersistenceInitialSize() {
        return persistenceInitialSize;
    }

    public IgniteProperties setPersistenceInitialSize(int persistenceInitialSize) {
        this.persistenceInitialSize = persistenceInitialSize;
        return this;
    }

    public int getPersistenceMaxSize() {
        return persistenceMaxSize;
    }

    public IgniteProperties setPersistenceMaxSize(int persistenceMaxSize) {
        this.persistenceMaxSize = persistenceMaxSize;
        return this;
    }

    public String getPersistenceStorePath() {
        return persistenceStorePath;
    }

    public IgniteProperties setPersistenceStorePath(String persistenceStorePath) {
        this.persistenceStorePath = persistenceStorePath;
        return this;
    }

    public int getNotPersistenceInitialSize() {
        return NotPersistenceInitialSize;
    }

    public IgniteProperties setNotPersistenceInitialSize(int notPersistenceInitialSize) {
        NotPersistenceInitialSize = notPersistenceInitialSize;
        return this;
    }

    public int getNotPersistenceMaxSize() {
        return NotPersistenceMaxSize;
    }

    public IgniteProperties setNotPersistenceMaxSize(int notPersistenceMaxSize) {
        NotPersistenceMaxSize = notPersistenceMaxSize;
        return this;
    }
}
