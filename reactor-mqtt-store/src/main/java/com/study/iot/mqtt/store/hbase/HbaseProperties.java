package com.study.iot.mqtt.store.hbase;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:51
 */

@ConfigurationProperties(prefix = "spring.data.hbase")
public class HbaseProperties {

    private String quorum;

    private String rootDir;

    private String nodeParent;

    public String getQuorum() {
        return quorum;
    }

    public void setQuorum(String quorum) {
        this.quorum = quorum;
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getNodeParent() {
        return nodeParent;
    }

    public void setNodeParent(String nodeParent) {
        this.nodeParent = nodeParent;
    }
}
