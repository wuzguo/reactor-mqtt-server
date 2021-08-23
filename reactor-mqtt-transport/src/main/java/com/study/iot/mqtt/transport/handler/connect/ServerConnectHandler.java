package com.study.iot.mqtt.transport.handler.connect;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.study.iot.mqtt.akka.actor.Publisher;
import com.study.iot.mqtt.akka.event.WillEvent;
import com.study.iot.mqtt.akka.spring.SpringProps;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;
import reactor.netty.Connection;

/**
 * <B>说明：连接服务端</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:17
 */

@Slf4j
@StrategyService(group = StrategyGroup.CONNECT, type = StrategyEnum.CONNECT)
public class ServerConnectHandler implements ConnectCapable, InitializingBean {

    @Autowired
    private ContainerManager containerManager;

    @Autowired
    private Authentication authentication;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private InstanceUtil instanceUtil;

    @Autowired
    private ActorSystem actorSystem;

    // 发布订阅消息
    private ActorRef publisher;

    @Override
    @MqttMetric(MetricMatterName.TOTAL_CONNECTION_COUNT)
    public void handle(DisposableConnection disposable, MqttMessage mqttMessage) {
        log.info("connect message: {}", mqttMessage);
        MqttConnectMessage message = (MqttConnectMessage) mqttMessage;

        // 消息解码器出现异常
        if (message.decoderResult().isFailure()) {
            Throwable cause = message.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                MqttConnAckMessage ackMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false);
                disposable.sendMessage(ackMessage).subscribe();
                disposable.dispose();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的设备ID
                MqttConnAckMessage ackMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false);
                disposable.sendMessage(ackMessage).subscribe();
                disposable.dispose();
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
            disposable.sendMessage(ackMessage).subscribe();
            disposable.dispose();
            return;
        }

        // 用户名和密码
        String key = payload.userName();
        String secret =
            payload.passwordInBytes() == null ? null : new String(payload.passwordInBytes(), CharsetUtil.UTF_8);
        if (StringUtils.isAnyBlank(key, secret)) {
            MqttConnAckMessage ackMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false);
            disposable.sendMessage(ackMessage).subscribe();
            disposable.dispose();
            return;
        }

        // 认证账号和密码
        authentication.authenticate(key, secret).doOnError((throwable) -> {
            log.error("auth server check error: {}", throwable.getMessage());
            MqttConnAckMessage ackMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_NOT_AUTHORIZED, false);
            disposable.sendMessage(ackMessage).subscribe();
            disposable.dispose();
        }).subscribe((success) -> {
            if (!success) {
                MqttConnAckMessage ackMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false);
                disposable.sendMessage(ackMessage).subscribe();
                disposable.dispose();
                return;
            }

            // 创建Session的保存
            sessionManager.add(instanceUtil.getInstanceId(), identity, variableHeader.isCleanSession());
            // 连接成功，超时时间来自客户端的设置
            this.acceptConnect(disposable, identity, variableHeader.keepAliveTimeSeconds());
            // 发送连接成功的消息
            disposable.sendMessage(MessageBuilder.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED)).subscribe();
            // 如果有遗嘱消息，这里需要处理
            if (variableHeader.isWillFlag()) {
                // 返回消息ID
                String row = this.setWillMessage(identity, payload.willTopic(), variableHeader.isWillRetain(),
                    payload.willMessageInBytes(), variableHeader.willQos());
                // 注册发送遗嘱消息
                Connection connection = disposable.getConnection();
                connection.onDispose(() -> this.sendWillMessage(identity, row, payload.willTopic()));
            }
        });
    }

    /**
     * 发送遗嘱消息
     *
     * @param identity  标识
     * @param topicName 主题名称
     */
    private void sendWillMessage(String identity, String row, String topicName) {
        WillEvent event = new WillEvent(this, IdUtils.idGen());
        event.setIdentity(identity);
        event.setTopic(topicName);
        event.setInstanceId(instanceUtil.getInstanceId());
        event.setRow(row);
        publisher.tell(event, ActorRef.noSender());
    }

    /**
     * 设置遗嘱消息
     *
     * @param identity  连接标识
     * @param topicName Topic
     * @param isRetain  保留消息
     * @param message   消息
     * @param qoS       QOS
     */
    private String setWillMessage(String identity, String topicName, boolean isRetain, byte[] message, int qoS) {
        // 构造消息，消息不能放在本地，要放在数据库中，因为有集群
        WillMessage willMessage = WillMessage.builder().row(IdUtils.idGen().toString())
            .identity(identity)
            .sessionId(IdUtils.idGen().toString())
            .messageId(-1)
            .topic(topicName)
            .retain(isRetain)
            .qos(qoS)
            .copyByteBuf(message).build();
        // 入库
        sessionManager.save(identity, willMessage);
        // 返回，这里要把消息ID保存在连接上下文里面
        return willMessage.getRow();
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
        // 保持标识和连接的关系
        containerManager.take(CacheGroup.CHANNEL).add(identity, disposableConnection);
        // 取消关闭连接
        Optional.ofNullable(connection.channel().attr(AttributeKeys.closeConnection)).map(Attribute::get)
            .ifPresent(Disposable::dispose);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 发布订阅消息
        publisher = actorSystem.actorOf(SpringProps.create(actorSystem, Publisher.class), "will-publisher");
    }
}
