package com.study.iot.mqtt.transport.server.handler;


import com.study.iot.mqtt.auth.service.IAuthService;
import com.study.iot.mqtt.cache.connection.MessageSender;
import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.connection.MessageBuilder;
import com.study.iot.mqtt.common.message.WillMessage;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.Attribute;
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
            connection.write(MessageBuilder.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED))
                    .subscribe();
            connection.dispose();
            return;
        }

        if (!variableHeader.hasPassword() || !variableHeader.hasUserName()) {
            connectSuccess(connection, mqttPayload.clientIdentifier(), variableHeader.keepAliveTimeSeconds());
            if (variableHeader.isWillFlag()) {
                saveWill(connection, mqttPayload.willTopic(), variableHeader.isWillRetain(),
                        mqttPayload.willMessageInBytes(), variableHeader.willQos());
            }
        }

        // 验证账号密码
        authService.check(mqttPayload.userName(), mqttPayload.passwordInBytes()).subscribe((success) -> {
            if (success) {
                connectSuccess(connection, identity, variableHeader.keepAliveTimeSeconds());
            } else {
                connection.write(MessageBuilder.buildConnectAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD))
                        .subscribe();
            }

            if (variableHeader.isWillFlag()) {
                saveWill(connection, mqttPayload.willTopic(), variableHeader.isWillRetain(),
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
    private void saveWill(DisposableConnection connection, String topicName, boolean retain, byte[] message, int qoS) {
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
    private void connectSuccess(DisposableConnection connection, String identity, int keepalive) {
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
        connection.write(MessageBuilder.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED)).subscribe();
    }
}
