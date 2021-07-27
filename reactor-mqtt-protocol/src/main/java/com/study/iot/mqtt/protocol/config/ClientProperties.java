package com.study.iot.mqtt.protocol.config;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.function.Consumer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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
    private Integer keepAliveSeconds;

    /**
     * 配置项目
     */
    private ConnectOptions options;

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

    @Getter
    public static class ConnectOptions {

        /**
         * 客户端ID
         */
        private String clientId;

        /**
         * 遗嘱Topic
         */
        private String willTopic;


        /**
         * 遗嘱消息
         */
        private String willMessage;

        /**
         * 用户名称
         */
        private final String userName;

        /**
         * 密码
         */
        private final String password;

        /**
         * 是否有用户名
         */
        private final Boolean hasUserName;

        /**
         * 是否有密码
         */
        private final Boolean hasPassword;


        private Boolean hasWillRetain;

        /**
         * 遗嘱消息质量
         */
        private MqttQoS willQos;

        /**
         * 是否有遗嘱
         */
        private Boolean hasWillFlag;

        /**
         * CleanSession 标识
         */
        private Boolean hasCleanSession;

        public ConnectOptions(String userName, String password) {
            this.userName = userName;
            this.hasUserName = true;
            this.password = password;
            this.hasPassword = true;
        }

        public ConnectOptions setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public ConnectOptions setWillQos(MqttQoS willQos) {
            this.willQos = willQos;
            return this;
        }

        public ConnectOptions setHasCleanSession(Boolean hasCleanSession) {
            this.hasCleanSession = hasCleanSession;
            return this;
        }

        public ConnectOptions setHasWillRetain(Boolean hasWillRetain) {
            this.hasWillRetain = hasWillRetain;
            return this;
        }


        public ConnectOptions setWillTopic(String willTopic) {
            this.willTopic = willTopic;
            this.hasWillFlag = true;
            return this;
        }


        public ConnectOptions setWillMessage(String willMessage) {
            this.willMessage = willMessage;
            this.hasWillFlag = true;
            return this;
        }
    }
}
