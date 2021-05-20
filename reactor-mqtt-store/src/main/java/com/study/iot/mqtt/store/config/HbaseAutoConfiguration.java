package com.study.iot.mqtt.store.config;


import com.study.iot.mqtt.store.properties.HbaseProperties;
import com.study.iot.mqtt.store.hbase.HbaseTemplate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */

@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(HbaseProperties.class)
@ConditionalOnClass(HbaseTemplate.class)
public class HbaseAutoConfiguration {

    private static final String HBASE_QUORUM = "hbase.zookeeper.quorum";

    private static final String HBASE_ROOTDIR = "hbase.rootdir";

    private static final String HBASE_ZNODE_PARENT = "hbase.zookeeper.property.dataDir";

    @Autowired
    private HbaseProperties hbaseProperties;

    @Bean
    @ConditionalOnMissingBean(HbaseTemplate.class)
    public HbaseTemplate hbaseTemplate() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set(HBASE_QUORUM, this.hbaseProperties.getQuorum());
        configuration.set(HBASE_ROOTDIR, hbaseProperties.getRootDir());
        configuration.set(HBASE_ZNODE_PARENT, hbaseProperties.getNodeParent());
        configuration.set("zookeeper.znode.parent", "/hbase");
        configuration.set("zookeeper.session.timeout", "60000");
        configuration.set("hbase.rpc.timeout", "60000");
        configuration.set("hbase.client.operation.timeout", "30000");
        configuration.set("hbase.client.scanner.timeout.period", "200000");
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.rest.ssl.enabled", "false");
        configuration.set("hbase.cluster.distributed", "true");
        configuration.set("hbase.master", "hadoop001:16010");

//        configuration.set("hbase.client.write.buffer", "2097152");
//        configuration.set("hbase.client.pause", "100");
//        configuration.set("hbase.client.retries.number", "10");
//        configuration.set("hbase.client.scanner.caching", "100");
//        configuration.set("hbase.client.keyvalue.maxsize", "10485760");
//        configuration.set("hbase.ipc.client.allowsInterrupt", "true");
//        configuration.set("hbase.client.primaryCallTimeout.get", "10");
//        configuration.set("hbase.client.primaryCallTimeout.multiget", "10");
//        configuration.set("hbase.client.scanner.timeout.period", "60000");
//        configuration.set("hbase.coprocessor.region.classes", "org.apache.hadoop.hbase.security.access.SecureBulkLoadEndpoint");
//        configuration.set("hbase.regionserver.thrift.http", "false");
//        configuration.set("hbase.thrift.support.proxyuser", "false");
//        configuration.set("hbase.rpc.timeout", "60000");
//        configuration.set("hbase.client.operation.timeout", "30000");
//        configuration.set("hbase.client.scanner.timeout.period", "200000");
//        configuration.set("hbase.snapshot.enabled", "true");
//        configuration.set("hbase.snapshot.region.timeout", "300000");
//        configuration.set("hbase.snapshot.master.timeout.millis", "300000");
//        configuration.set("hbase.security.authentication", "simple");
//        configuration.set("hbase.rpc.protection", "authentication");
//        configuration.set("zookeeper.session.timeout", "60000");
//        configuration.set("zookeeper.znode.parent", "/hbase");
//        configuration.set("zookeeper.request.timeout", "30000");
//        configuration.set("zookeeper.znode.rootserver", "root-region-server");
//        configuration.set("hbase.rest.ssl.enabled", "false");
//        configuration.set("hbase.cluster.distributed", "true");
//        configuration.set("hbase.master", "hadoop001:16010");

        return new HbaseTemplate(configuration);
    }
}
