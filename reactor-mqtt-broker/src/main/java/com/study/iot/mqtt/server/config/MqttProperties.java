package com.study.iot.mqtt.server.config;

import com.study.iot.mqtt.common.enums.CacheStrategy;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/14 10:21
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mqtt.broker")
public class MqttProperties {

    /**
     * IP地址
     */
    private String host;

    /**
     * MQTT端口号
     */
    private Integer port;

    /**
     * Websocket端口号
     */
    private Integer wsPort;

    /**
     * XMPP端口号
     */
    private Integer xmPort;

    /**
     * COAP端口号
     */
    private Integer coPort;

    /**
     * 开启日志
     */
    private Boolean enableLog;

    /**
     * 开启SSL
     */
    private Boolean enableSsl;

    /**
     * 缓存策略，默认为 memory
     */
    @Value("${spring.cache.mode: memory}")
    private CacheStrategy strategy;

    public void setStrategy(String strategy) {
        this.strategy = CacheStrategy.from(strategy);
    }

    /**
     * 服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
     */
    private Integer backlog = 128;

    /**
     * Socket参数，连接保活，默认值为False
     */
    private Boolean keepAlive = false;

    /**
     * 开启Epoll模式, linux下建议开启
     */
    private Boolean useEpoll = false;
}
