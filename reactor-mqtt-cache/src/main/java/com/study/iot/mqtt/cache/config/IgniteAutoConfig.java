package com.study.iot.mqtt.cache.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * 自动配置apache ignite
 */

@Configuration
@ConfigurationProperties(prefix = "spring.mqtt.broker", ignoreInvalidFields = true)
public class IgniteAutoConfig {

    /**
     * 实例ID
     */
    private String id;

    /**
     * 是否启动多播组
     */
    private boolean enableMulticastGroup = true;

    /**
     * 多播组
     */
    private String multicastGroup = "239.255.255.255";

    /**
     * 静态IP地址
     */
    private String[] staticIpAddresses = new String[0];

    @Bean
    public IgniteProperties igniteProperties() {
        return new IgniteProperties();
    }

    @Bean
    public Ignite ignite() throws Exception {
        IgniteConfiguration configuration = new IgniteConfiguration();
        // Ignite实例名称
        configuration.setIgniteInstanceName(id);

        // The node will be started as a client node.
        configuration.setClientMode(true);

        // Classes of custom Java logic will be transferred over the wire from this app.
        configuration.setPeerClassLoadingEnabled(true);

        // Ignite日志
        Logger logger = LoggerFactory.getLogger("org.apache.ignite");
        configuration.setGridLogger(new Slf4jLogger(logger));
        // 非持久化数据区域
        DataRegionConfiguration notPersistence = new DataRegionConfiguration().setPersistenceEnabled(false)
                .setInitialSize(igniteProperties().getNotPersistenceInitialSize() * 1024 * 1024L)
                .setMaxSize(igniteProperties().getNotPersistenceMaxSize() * 1024 * 1024L).setName("not-persistence-data-region");
        // 持久化数据区域
        DataRegionConfiguration persistence = new DataRegionConfiguration().setPersistenceEnabled(true)
                .setInitialSize(igniteProperties().getPersistenceInitialSize() * 1024 * 1024L)
                .setMaxSize(igniteProperties().getPersistenceMaxSize() * 1024 * 1024L).setName("persistence-data-region");
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration().setDefaultDataRegionConfiguration(notPersistence)
                .setDataRegionConfigurations(persistence)
                .setWalArchivePath(!StringUtils.isEmpty(igniteProperties().getPersistenceStorePath()) ? igniteProperties().getPersistenceStorePath() : null)
                .setWalPath(!StringUtils.isEmpty(igniteProperties().getPersistenceStorePath()) ? igniteProperties().getPersistenceStorePath() : null)
                .setStoragePath(!StringUtils.isEmpty(igniteProperties().getPersistenceStorePath()) ? igniteProperties().getPersistenceStorePath() : null);
        configuration.setDataStorageConfiguration(dataStorageConfiguration);
        // 集群, 基于组播或静态IP配置
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        if (this.enableMulticastGroup) {
            TcpDiscoveryMulticastIpFinder tcpDiscoveryMulticastIpFinder = new TcpDiscoveryMulticastIpFinder();
            tcpDiscoveryMulticastIpFinder.setMulticastGroup(multicastGroup);
            tcpDiscoverySpi.setIpFinder(tcpDiscoveryMulticastIpFinder);
        } else {
            TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
            tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList(staticIpAddresses));
            tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
        }
        configuration.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(configuration);
        ignite.cluster().active(true);
        return ignite;
    }

    @Bean
    public IgniteCache<Object, Object> messageIdCache() throws Exception {
        CacheConfiguration<Object, Object> cacheConfiguration = new CacheConfiguration<>().setDataRegionName("not-persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL).setName("messageIdCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<Object, Object> retainMessageCache() throws Exception {
        CacheConfiguration<Object, Object> cacheConfiguration = new CacheConfiguration<>().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("retainMessageCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<Object, Object> subscribeNotWildcardCache() throws Exception {
        CacheConfiguration<Object, Object> cacheConfiguration = new CacheConfiguration<>().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("subscribeNotWildcardCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<Object, Object> subscribeWildcardCache() throws Exception {
        CacheConfiguration<Object, Object> cacheConfiguration = new CacheConfiguration<>().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("subscribeWildcardCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<Object, Object> dupPublishMessageCache() throws Exception {
        CacheConfiguration<Object, Object> cacheConfiguration = new CacheConfiguration<>().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("dupPublishMessageCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<Object, Object> dupPubRelMessageCache() throws Exception {
        CacheConfiguration<Object, Object> cacheConfiguration = new CacheConfiguration<>().setDataRegionName("persistence-data-region")
                .setCacheMode(CacheMode.PARTITIONED).setName("dupPubRelMessageCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteMessaging igniteMessaging() throws Exception {
        return ignite().message(ignite().cluster().forRemotes());
    }
}
