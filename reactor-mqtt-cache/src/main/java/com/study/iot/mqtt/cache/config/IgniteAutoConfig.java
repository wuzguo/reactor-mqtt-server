package com.study.iot.mqtt.cache.config;

import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.RetainMessage;
import java.util.Arrays;
import java.util.List;
import lombok.Setter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * 自动配置apache ignite
 */

@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.mqtt.broker", ignoreInvalidFields = true)
@ConditionalOnBean(value = IgniteProperties.class)
public class IgniteAutoConfig {

    /**
     * 实例ID
     */
    private String instanceName;

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

    @Autowired
    private IgniteProperties igniteProperties;

    @Bean
    public Ignite ignite() throws Exception {
        IgniteConfiguration configuration = new IgniteConfiguration();
        // Ignite实例名称
        configuration.setIgniteInstanceName(instanceName);
        // Ignite日志
        Logger logger = LoggerFactory.getLogger("org.apache.ignite");
        configuration.setGridLogger(new Slf4jLogger(logger));
        // 非持久化数据区域
        DataRegionConfiguration notPersistence = new DataRegionConfiguration().setPersistenceEnabled(false)
            .setInitialSize(igniteProperties.getNotPersistenceInitialSize() * 1024 * 1024L)
            .setMaxSize(igniteProperties.getNotPersistenceMaxSize() * 1024 * 1024L)
            .setName("not-persistence-data-region");
        // 持久化数据区域
        DataRegionConfiguration persistence = new DataRegionConfiguration().setPersistenceEnabled(true)
            .setInitialSize(igniteProperties.getPersistenceInitialSize() * 1024 * 1024L)
            .setMaxSize(igniteProperties.getPersistenceMaxSize() * 1024 * 1024L).setName("persistence-data-region");
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration()
            .setDefaultDataRegionConfiguration(notPersistence)
            .setDataRegionConfigurations(persistence)
            .setWalArchivePath(!StringUtils.isEmpty(igniteProperties.getPersistenceStorePath()) ? igniteProperties
                .getPersistenceStorePath() : null)
            .setWalPath(!StringUtils.isEmpty(igniteProperties.getPersistenceStorePath()) ? igniteProperties
                .getPersistenceStorePath() : null)
            .setStoragePath(!StringUtils.isEmpty(igniteProperties.getPersistenceStorePath()) ? igniteProperties
                .getPersistenceStorePath() : null);
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
    public IgniteCache<Integer, Integer> messageIdCache() throws Exception {
        CacheConfiguration<Integer, Integer> cacheConfiguration = new CacheConfiguration<Integer, Integer>()
            .setDataRegionName("not-persistence-data-region")
            .setCacheMode(CacheMode.PARTITIONED).setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
            .setName("messageIdCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<String, DisposableConnection> disposableCache() throws Exception {
        CacheConfiguration<String, DisposableConnection> cacheConfiguration = new CacheConfiguration<String, DisposableConnection>()
            .setDataRegionName("persistence-data-region")
            .setCacheMode(CacheMode.PARTITIONED).setName("disposableCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<String, RetainMessage> messageCache() throws Exception {
        CacheConfiguration<String, RetainMessage> cacheConfiguration = new CacheConfiguration<String, RetainMessage>()
            .setDataRegionName("persistence-data-region")
            .setCacheMode(CacheMode.PARTITIONED).setName("messageCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<String, List<DisposableConnection>> topicDisposableCache() throws Exception {
        CacheConfiguration<String, List<DisposableConnection>> cacheConfiguration = new CacheConfiguration<String, List<DisposableConnection>>()
            .setDataRegionName("persistence-data-region")
            .setCacheMode(CacheMode.PARTITIONED).setName("topicDisposableCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteMessaging igniteMessaging() throws Exception {
        return ignite().message(ignite().cluster().forRemotes());
    }
}
