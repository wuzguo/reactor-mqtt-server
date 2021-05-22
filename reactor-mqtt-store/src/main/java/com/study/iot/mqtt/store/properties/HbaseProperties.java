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
     * master
     */
    private String master;

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
    private String znodeParent;

    /**
     * 客户端端口
     */
    private Integer clientPort;

    /**
     * Session
     */
    private Integer sessionTimeout;

    /**
     * RPC
     */
    private Integer rpcTimeout;

    /**
     * 操作超时
     */
    private Integer operationTimeout;

    /**
     * Scanner
     */
    private Integer scannerTimeout;

    /**
     * SSL
     */
    private Boolean sslEnabled;

    /**
     * Distributed
     */
    private Boolean distributed;
}
