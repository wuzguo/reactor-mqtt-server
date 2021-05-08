package com.study.iot.mqtt.transport.server.handler;


import com.study.iot.mqtt.auth.service.IAuthService;
import com.study.iot.mqtt.cache.connection.MessageSender;
import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.connection.MessageBuilder;
import com.study.iot.mqtt.common.message.WillMessage;
import com.study.iot.mqtt.common.utils.StringUtil;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.Attribute;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;

import java.util.Optional;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:17
 */

@Slf4j
@StrategyService(group = StrategyGroup.SERVER, type = MqttMessageType.CONNECT)
public class ServerConnectHandler implements StrategyCapable {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IAuthService authService;

    @Autowired
    private MessageSender messageSender;

    @Override
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("server connect message: {}, connection: {}", message, connection);
        MqttConnectMessage connectMessage = (MqttConnectMessage) message;

        // 消息解码器出现异常
        if (connectMessage.decoderResult().isFailure()) {
            Throwable cause = connectMessage.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                messageSender.sendConnAckMessage(connection.getOutbound(),
                        MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false)
                        .subscribe();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的设备ID
                messageSender.sendConnAckMessage(connection.getOutbound(),
                        MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false)
                        .subscribe();
                return;
            }
            connection.dispose();
        }

        MqttConnectVariableHeader variableHeader = connectMessage.variableHeader();
        MqttConnectPayload mqttPayload = connectMessage.payload();
        ChannelManager channelManager = cacheManager.channel();
        String identity = mqttPayload.clientIdentifier();
        if (channelManager.check(identity)) {
            messageSender.sendConnAckMessage(connection.getOutbound(),
                    MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false)
                    .subscribe();

            connection.dispose();
            return;
        }

        // 没有用户密码
        String key = mqttPayload.userName();
        String secret = mqttPayload.passwordInBytes() == null ? null : new String(mqttPayload.passwordInBytes(), CharsetUtil.UTF_8);
        if (StringUtil.isAnyBlank(key, secret)) {
            messageSender.sendConnAckMessage(connection.getOutbound(),
                    MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false)
                    .subscribe();
            connection.dispose();
            return;
        }

        // 验证账号密码
        authService.check(key, secret).doOnError((throwable) -> {
            log.error("auth server check error: {}", throwable.getMessage());
            messageSender.sendConnAckMessage(connection.getOutbound(),
                    MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED, false)
                    .subscribe();
            connection.dispose();
        }).subscribe((success) -> {
            if (!success) {
                messageSender.sendConnAckMessage(connection.getOutbound(),
                        MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false)
                        .subscribe();
                connection.dispose();
                return;
            }
            // 连接成功
            acceptConnect(connection, identity, variableHeader.keepAliveTimeSeconds());
            if (variableHeader.isWillFlag()) {
                setWillMessage(connection, mqttPayload.willTopic(), variableHeader.isWillRetain(),
                        mqttPayload.willMessageInBytes(), variableHeader.willQos());
            }
        });
    }


    /**
     * 设置遗嘱消息
     *
     * @param connection 连接
     * @param topicName  Topic
     * @param retain     retain
     * @param message    消息
     * @param qoS        QOS
     */
    private void setWillMessage(DisposableConnection connection, String topicName, boolean retain, byte[] message, int qoS) {
        WillMessage willMessage = WillMessage.builder().message(message).qos(qoS).retain(retain).topicName(topicName).build();
        // 设置遗嘱消息
        connection.getConnection().channel().attr(AttributeKeys.WILL_MESSAGE).set(willMessage);
    }

    /**
     * 连接成功
     *
     * @param connection {@link DisposableConnection}
     * @param identity   设备标识
     * @param keepalive  超时时间
     */
    private void acceptConnect(DisposableConnection connection, String identity, int keepalive) {
        // 心跳超时关闭
        connection.getConnection().onReadIdle(keepalive * 2000L, () -> connection.getConnection().dispose());
        // 设置设备标识
        connection.getConnection().channel().attr(AttributeKeys.keepalived).set(keepalive);
        cacheManager.channel().add(identity, connection);
        // 取消关闭连接
        Optional.ofNullable(connection.getConnection().channel().attr(AttributeKeys.closeConnection))
                .map(Attribute::get)
                .ifPresent(Disposable::dispose);
        cacheManager.channel().addConnections(connection);
        // 连接成功
        messageSender.sendMessage(connection.getOutbound(),
                MessageBuilder.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED))
                .subscribe();
    }
}
