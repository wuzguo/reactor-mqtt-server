package com.study.iot.mqtt.protocol.config;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.function.Consumer;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class ClientProperties extends ConnectProperties {
    /**
     * 心跳
     */
    private Integer keepAliveSeconds = 60;

    /**
     * 配置项目
     */
    private Options options;

    /**
     * 异常处理
     */
    private Consumer<Throwable> throwable;

    /**
     * 协议
     */
    private ProtocolType protocol;

    /**
     * 关闭处理
     */
    private Runnable onClose;

    @Data
    @Builder
    public static class Options {

        private String clientId;

        private String willTopic;

        private String willMessage;

        private String userName;

        private String password;

        private Boolean hasUserName;

        private Boolean hasPassword;

        private Boolean hasWillRetain;

        private MqttQoS willQos;

        private Boolean hasWillFlag;

        private Boolean hasCleanSession;
    }
}
