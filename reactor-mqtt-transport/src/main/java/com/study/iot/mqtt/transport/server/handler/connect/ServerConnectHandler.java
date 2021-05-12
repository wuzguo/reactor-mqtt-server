package com.study.iot.mqtt.transport.server.handler.connect;


import com.study.iot.mqtt.auth.service.IAuthService;
import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.cache.service.ChannelManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.common.message.MessageBuilder;
import com.study.iot.mqtt.transport.annotation.MqttMetric;
import com.study.iot.mqtt.transport.constant.MetricMatterName;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.StrategyCapable;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import com.study.iot.mqtt.common.message.WillMessage;
import com.study.iot.mqtt.common.utils.StringUtil;
import com.study.iot.mqtt.protocol.AttributeKeys;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttIdentifierRejectedException;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttUnacceptableProtocolVersionException;
import io.netty.util.Attribute;
import io.netty.util.CharsetUtil;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;

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

    @Override
    @MqttMetric(name = MetricMatterName.TOTAL_CONNECTION_COUNT)
    public void handle(MqttMessage message, DisposableConnection connection) {
        log.info("server connect message: {}, connection: {}", message, connection);
        MqttConnectMessage connectMessage = (MqttConnectMessage) message;

        // 消息解码器出现异常
        if (connectMessage.decoderResult().isFailure()) {
            Throwable cause = connectMessage.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                MqttConnAckMessage connAckMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false);
                connection.sendMessage(connAckMessage).subscribe();
                connection.dispose();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的设备ID
                MqttConnAckMessage connAckMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false);
                connection.sendMessage(connAckMessage).subscribe();
                connection.dispose();
                return;
            }
        }

        MqttConnectVariableHeader variableHeader = connectMessage.variableHeader();
        MqttConnectPayload mqttPayload = connectMessage.payload();
        ChannelManager channelManager = cacheManager.channel();
        String identity = mqttPayload.clientIdentifier();
        if (channelManager.containsKey(identity)) {
            MqttConnAckMessage connAckMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false);
            connection.sendMessage(connAckMessage).subscribe();
            connection.dispose();
            return;
        }

        // 用户名和密码
        String key = mqttPayload.userName();
        String secret = mqttPayload.passwordInBytes() == null ?
            null : new String(mqttPayload.passwordInBytes(), CharsetUtil.UTF_8);
        if (StringUtil.isAnyBlank(key, secret)) {
            MqttConnAckMessage connAckMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false);
            connection.sendMessage(connAckMessage).subscribe();
            connection.dispose();
            return;
        }

        // 验证账号密码
        authService.login(key, secret).doOnError((throwable) -> {
            log.error("auth server check error: {}", throwable.getMessage());
            MqttConnAckMessage connAckMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED, false);
            connection.sendMessage(connAckMessage).subscribe();
            connection.dispose();
        }).subscribe((success) -> {
            if (!success) {
                MqttConnAckMessage connAckMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false);
                connection.sendMessage(connAckMessage).subscribe();
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
    private void setWillMessage(DisposableConnection connection, String topicName, boolean isRetain, byte[] message,
        int qoS) {
        WillMessage willMessage = WillMessage.builder().copyByteBuf(message)
            .qos(qoS).isRetain(isRetain).topic(topicName).build();
        // 设置遗嘱消息
        connection.getConnection().channel().attr(AttributeKeys.willMessage).set(willMessage);
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
        // 设置连接保持时间
        connection.getConnection().channel().attr(AttributeKeys.keepalived).set(keepalive);
        // 设置设备标识
        connection.getConnection().channel().attr(AttributeKeys.identity).set(identity);
        // 保持标识和连接的关系
        cacheManager.channel().add(identity, connection);
        // 取消关闭连接
        Optional.ofNullable(connection.getConnection().channel().attr(AttributeKeys.closeConnection))
            .map(Attribute::get)
            .ifPresent(Disposable::dispose);
        // 连接成功
        connection.sendMessage(MessageBuilder.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED)).subscribe();
    }
}
