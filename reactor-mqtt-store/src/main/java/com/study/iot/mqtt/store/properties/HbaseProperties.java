package com.study.iot.mqtt.store.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */

@Data
@ConfigurationProperties(prefix = "spring.hbase")
public class HbaseProperties {

    /**
     * ZK集群
     */
    private String quorum;

    /**
     *  HDFS 上存储的路径
     */
    private String rootDir;

    /**
     * HBase 的根 ZNode
     */
    private String nodeParent;
}
