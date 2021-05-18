package com.study.iot.mqtt.store.config;

import com.study.iot.mqtt.store.disposable.SerializerDisposable;
import com.study.iot.mqtt.common.message.RetainMessage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;

import com.study.iot.mqtt.store.ignite.IgniteProperties;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterState;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置apache ignite
 */

@Configuration
@ConditionalOnBean(value = IgniteProperties.class)
public class IgniteAutoConfiguration {

    @Autowired
    private IgniteProperties igniteProperties;

    @Bean
    public Ignite ignite() throws Exception {
        IgniteConfiguration configuration = new IgniteConfiguration();
        // Ignite实例名称
        configuration.setIgniteInstanceName(igniteProperties.getInstanceName());
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
            .setWalArchivePath(igniteProperties.getPersistenceStorePath())
            .setWalPath(igniteProperties.getPersistenceStorePath())
            .setStoragePath(igniteProperties.getPersistenceStorePath());
        configuration.setDataStorageConfiguration(dataStorageConfiguration);
        // 集群, 基于组播或静态IP配置
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        if (igniteProperties.isEnableMulticastGroup()) {
            TcpDiscoveryMulticastIpFinder tcpDiscoveryMulticastIpFinder = new TcpDiscoveryMulticastIpFinder();
            tcpDiscoveryMulticastIpFinder.setMulticastGroup(igniteProperties.getMulticastGroup());
            tcpDiscoverySpi.setIpFinder(tcpDiscoveryMulticastIpFinder);
        } else {
            TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
            tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList(igniteProperties.getStaticIpAddresses()));
            tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
        }
        configuration.setDiscoverySpi(tcpDiscoverySpi);
        Ignite ignite = Ignition.start(configuration);
        ignite.cluster().state(ClusterState.ACTIVE);
        return ignite;
    }

    @Bean
    public IgniteCache<Integer, Integer> messageIdCache() throws Exception {
        CacheConfiguration<Integer, Integer> cacheConfiguration = new CacheConfiguration<Integer, Integer>()
            .setDataRegionName("not-persistence-data-region").setBackups(2)
            .setCacheMode(CacheMode.PARTITIONED).setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
            .setName("messageIdCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<String, LongAdder> metricCache() throws Exception {
        CacheConfiguration<String, LongAdder> cacheConfiguration = new CacheConfiguration<String, LongAdder>()
            .setDataRegionName("not-persistence-data-region").setBackups(2)
            .setCacheMode(CacheMode.PARTITIONED).setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
            .setName("metricCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<String, SerializerDisposable> disposableCache() throws Exception {
        CacheConfiguration<String, SerializerDisposable> cacheConfiguration = new CacheConfiguration<String, SerializerDisposable>()
            .setDataRegionName("persistence-data-region").setBackups(2)
            .setCacheMode(CacheMode.PARTITIONED).setName("disposableCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<String, RetainMessage> messageCache() throws Exception {
        CacheConfiguration<String, RetainMessage> cacheConfiguration = new CacheConfiguration<String, RetainMessage>()
            .setDataRegionName("persistence-data-region").setBackups(2)
            .setCacheMode(CacheMode.PARTITIONED).setName("messageCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteCache<String, List<SerializerDisposable>> topicSerializerDisposableCache() throws Exception {
        CacheConfiguration<String, List<SerializerDisposable>> cacheConfiguration = new CacheConfiguration<String, List<SerializerDisposable>>()
            .setDataRegionName("persistence-data-region").setBackups(2)
            .setCacheMode(CacheMode.PARTITIONED).setName("topicSerializerDisposableCache");
        return ignite().getOrCreateCache(cacheConfiguration);
    }

    @Bean
    public IgniteMessaging igniteMessaging() throws Exception {
        return ignite().message(ignite().cluster().forRemotes());
    }
}
