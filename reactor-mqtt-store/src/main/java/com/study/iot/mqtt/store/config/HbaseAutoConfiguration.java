package com.study.iot.mqtt.store.config;


import com.study.iot.mqtt.store.hbase.HbaseProperties;
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

    private static final String HBASE_ZNODE_PARENT = "zookeeper.znode.parent";

    @Autowired
    private HbaseProperties hbaseProperties;

    @Bean
    @ConditionalOnMissingBean(HbaseTemplate.class)
    public HbaseTemplate hbaseTemplate() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set(HBASE_QUORUM, this.hbaseProperties.getQuorum());
        configuration.set(HBASE_ROOTDIR, hbaseProperties.getRootDir());
        configuration.set(HBASE_ZNODE_PARENT, hbaseProperties.getNodeParent());
        return new HbaseTemplate(configuration);
    }
}
