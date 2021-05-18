package com.study.iot.mqtt.akka.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/13 14:29
 */

@Configuration
@ConfigurationProperties(prefix = "spring.akka")
public class AkkaProperties {

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 配置文件
     */
    private Config config;


    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = ConfigFactory.load(config);
    }
}
