package com.study.iot.mqtt.transport.handler.connect;


import com.study.iot.mqtt.auth.service.Authentication;
import com.study.iot.mqtt.common.domain.WillMessage;
import com.study.iot.mqtt.common.utils.IdUtils;
import com.study.iot.mqtt.common.utils.StringUtils;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.MessageBuilder;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.session.config.InstanceUtil;
import com.study.iot.mqtt.session.manager.SessionManager;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.container.StorageContainer;
import com.study.iot.mqtt.transport.annotation.MqttMetric;
import com.study.iot.mqtt.transport.constant.MetricMatterName;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.strategy.ConnectCapable;
import com.study.iot.mqtt.transport.strategy.StrategyEnum;
import com.study.iot.mqtt.transport.strategy.StrategyService;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttIdentifierRejectedException;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttUnacceptableProtocolVersionException;
import io.netty.util.Attribute;
import io.netty.util.CharsetUtil;
import java.io.Serializable;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;
import reactor.netty.Connection;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:17
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.CONNECT)
public class ServerConnectHandler implements ConnectCapable {

    @Autowired
    private ContainerManager containerManager;

    @Autowired
    private Authentication authentication;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private InstanceUtil instanceUtil;

    @Override
    @MqttMetric(MetricMatterName.TOTAL_CONNECTION_COUNT)
    public void handle(DisposableConnection connection, MqttMessage mqttMessage) {
        log.info("server connect message: {}, connection: {}", mqttMessage, connection);
        MqttConnectMessage message = (MqttConnectMessage) mqttMessage;

        // 消息解码器出现异常
        if (message.decoderResult().isFailure()) {
            Throwable cause = message.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                MqttConnAckMessage ackMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false);
                connection.sendMessage(ackMessage).subscribe();
                connection.dispose();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的设备ID
                MqttConnAckMessage ackMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false);
                connection.sendMessage(ackMessage).subscribe();
                connection.dispose();
                return;
            }
        }

        MqttConnectVariableHeader variableHeader = message.variableHeader();
        MqttConnectPayload payload = message.payload();
        StorageContainer<Serializable> storageContainer = containerManager.take(CacheGroup.CHANNEL);
        String identity = payload.clientIdentifier();
        if (storageContainer.containsKey(identity)) {
            MqttConnAckMessage ackMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false);
            connection.sendMessage(ackMessage).subscribe();
            connection.dispose();
            return;
        }

        // 用户名和密码
        String key = payload.userName();
        String secret =
            payload.passwordInBytes() == null ? null : new String(payload.passwordInBytes(), CharsetUtil.UTF_8);
        if (StringUtils.isAnyBlank(key, secret)) {
            MqttConnAckMessage ackMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false);
            connection.sendMessage(ackMessage).subscribe();
            connection.dispose();
            return;
        }

        // 认证账号和密码
        authentication.authenticate(key, secret).doOnError((throwable) -> {
            log.error("auth server check error: {}", throwable.getMessage());
            MqttConnAckMessage ackMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED, false);
            connection.sendMessage(ackMessage).subscribe();
            connection.dispose();
        }).subscribe((success) -> {
            if (!success) {
                MqttConnAckMessage ackMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false);
                connection.sendMessage(ackMessage).subscribe();
                connection.dispose();
                return;
            }

            // 创建Session的保存
            sessionManager.add(instanceUtil.getInstanceId(), identity, variableHeader.isCleanSession());
            // 连接成功，超时时间来自客户端的设置
            this.acceptConnect(connection, identity, variableHeader.keepAliveTimeSeconds());
            // 如果有遗嘱消息，这里需要处理
            if (variableHeader.isWillFlag()) {
                this.setWillMessage(identity, payload.willTopic(), variableHeader.isWillRetain(),
                    payload.willMessageInBytes(), variableHeader.willQos());
            }
        });
    }


    /**
     * 设置遗嘱消息
     *
     * @param identity 连接标识
     * @param topicName  Topic
     * @param isRetain   保留消息
     * @param message    消息
     * @param qoS        QOS
     */
    private void setWillMessage(String identity, String topicName, boolean isRetain, byte[] message, int qoS) {
        // 消息不能放在本地，要放在数据库中，因为有集群
        WillMessage willMessage = WillMessage.builder().row(IdUtils.idGen().toString())
            .identity(identity)
            .sessionId(IdUtils.idGen().toString())
            .messageId(-1)
            .topic(topicName)
            .retain(isRetain)
            .qos(qoS)
            .copyByteBuf(message).build();
        sessionManager.add(identity, willMessage);
    }

    /**
     * 连接成功
     *
     * @param disposableConnection {@link DisposableConnection}
     * @param identity             设备标识
     * @param keepAliveSeconds     超时时间
     */
    private void acceptConnect(DisposableConnection disposableConnection, String identity, Integer keepAliveSeconds) {
        // 连接信息
        Connection connection = disposableConnection.getConnection();
        // 心跳超时关闭
        connection.onReadIdle(keepAliveSeconds * 1000L, connection::dispose);
        // 设置连接保持时间
        connection.channel().attr(AttributeKeys.keepalive).set(keepAliveSeconds);
        // 设置设备标识
        connection.channel().attr(AttributeKeys.identity).set(identity);
        // 设置 connection
        connection.channel().attr(AttributeKeys.disposableConnection).set(disposableConnection);
        // 保持标识和连接的关系
        containerManager.take(CacheGroup.CHANNEL).add(identity, disposableConnection);
        // 取消关闭连接
        Optional.ofNullable(disposableConnection.getConnection().channel().attr(AttributeKeys.closeConnection))
            .map(Attribute::get).ifPresent(Disposable::dispose);
        // 发送连接成功的消息
        disposableConnection.sendMessage(MessageBuilder.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED))
            .subscribe();
    }
}
