package com.study.iot.mqtt.protocol.config;


import java.util.function.Consumer;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/10 14:58
 */

@Data
@SuperBuilder
public class ConnectProperties {

    /**
     * IP地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 心跳
     */
    private Integer heart = 60;

    /**
     * 开启日志
     */
    private Boolean isLog;

    /**
     * 开启SSL
     */
    private Boolean isSsl;

    /**
     * 异常处理
     */
    private Consumer<Throwable> throwable = throwable -> { };
}
