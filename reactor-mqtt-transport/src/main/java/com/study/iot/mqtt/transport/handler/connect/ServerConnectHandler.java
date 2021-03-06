package com.study.iot.mqtt.transport.handler.connect;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.study.iot.mqtt.akka.actor.Publisher;
import com.study.iot.mqtt.akka.event.WillEvent;
import com.study.iot.mqtt.akka.spring.SpringProps;
import com.study.iot.mqtt.auth.service.Authentication;
import com.study.iot.mqtt.common.domain.ConnectSession;
import com.study.iot.mqtt.common.message.WillMessage;
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
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Disposable;
import reactor.netty.Connection;

/**
 * <B>????????????????????????</B>
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

    // ??????????????????
    private ActorRef publisher;

    @Override
    @MqttMetric(MetricMatterName.TOTAL_CONNECTION_COUNT)
    public void handle(DisposableConnection disposable, MqttMessage mqttMessage) {
        log.info("connect message: {}", mqttMessage);
        MqttConnectMessage message = (MqttConnectMessage) mqttMessage;

        // ???????????????????????????
        if (message.decoderResult().isFailure()) {
            Throwable cause = message.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // ????????????????????????
                MqttConnAckMessage ackMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false);
                disposable.sendMessage(ackMessage).subscribe();
                disposable.dispose();
                return;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // ??????????????????ID
                MqttConnAckMessage ackMessage = MessageBuilder
                    .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false);
                disposable.sendMessage(ackMessage).subscribe();
                disposable.dispose();
                return;
            }
        }

        MqttConnectVariableHeader variableHeader = message.variableHeader();
        MqttConnectPayload payload = message.payload();
        StorageContainer<Disposable> storageContainer = containerManager.take(CacheGroup.CHANNEL);
        String identity = payload.clientIdentifier();
        // ??????????????????
        if (storageContainer.containsKey(identity)) {
            DisposableConnection disposableConnection = (DisposableConnection) storageContainer.get(identity);
            disposableConnection.dispose();
            // ??????Session?????????
            ConnectSession session = (ConnectSession) containerManager.take(CacheGroup.SESSION).get(identity);
            if (session.isCleanSession()) {

            }
        }

        // ??????????????????
        String key = payload.userName();
        String secret = payload.passwordInBytes() == null ? null : new String(payload.passwordInBytes(), CharsetUtil.UTF_8);
        if (StringUtils.isAnyBlank(key, secret)) {
            MqttConnAckMessage ackMessage = MessageBuilder
                .buildConnAck(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false);
            disposable.sendMessage(ackMessage).subscribe();
            disposable.dispose();
            return;
        }

        // ?????????????????????
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

            // ??????Session?????????????????? CleanSession = 0???????????????????????????????????????????????????????????????????????????
            // ?????????????????? CleanSession = 1???????????????????????????????????????????????????????????????????????????????????????
            sessionManager.add(instanceUtil.getInstanceId(), identity, variableHeader.isCleanSession());
            // ???????????????????????????????????????????????????
            this.acceptConnect(disposable, identity, variableHeader.keepAliveTimeSeconds());
            // ???????????????????????????
            disposable.sendMessage(MessageBuilder.buildConnectAck(MqttConnectReturnCode.CONNECTION_ACCEPTED))
                .subscribe();
            // ??????????????????????????????????????????
            if (variableHeader.isWillFlag()) {
                // ????????????ID
                String rowId = this.setWillMessage(identity, payload.willTopic(), variableHeader.isWillRetain(),
                    payload.willMessageInBytes(), variableHeader.willQos());
                // ????????????????????????
                Connection connection = disposable.getConnection();
                connection.onDispose(() -> this.sendWillMessage(identity, rowId, payload.willTopic()));
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param identity  ??????
     * @param topicName ????????????
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
     * ??????????????????
     *
     * @param identity  ????????????
     * @param topicName Topic
     * @param isRetain  ????????????
     * @param message   ??????
     * @param qoS       QOS
     */
    private String setWillMessage(String identity, String topicName, boolean isRetain, byte[] message, int qoS) {
        // ?????????????????????????????????????????????????????????????????????????????????
        WillMessage willMessage = WillMessage.builder().row(IdUtils.idGen().toString())
            .identity(identity)
            .sessionId(IdUtils.idGen().toString())
            .messageId(-1)
            .topic(topicName)
            .retain(isRetain)
            .qos(qoS)
            .copyByteBuf(message).build();
        // ??????
        sessionManager.save(identity, willMessage);
        // ???????????????????????????ID??????????????????????????????
        return willMessage.getRow();
    }

    /**
     * ????????????
     *
     * @param disposableConnection {@link DisposableConnection}
     * @param identity             ????????????
     * @param keepAliveSeconds     ????????????
     */
    private void acceptConnect(DisposableConnection disposableConnection, String identity, Integer keepAliveSeconds) {
        // ????????????
        Connection connection = disposableConnection.getConnection();
        // ??????????????????
        connection.onReadIdle(keepAliveSeconds * 1000L, connection::dispose);
        // ????????????????????????
        connection.channel().attr(AttributeKeys.keepalive).set(keepAliveSeconds);
        // ??????????????????
        connection.channel().attr(AttributeKeys.identity).set(identity);
        // ??????????????????????????????
        containerManager.take(CacheGroup.CHANNEL).add(identity, disposableConnection);
        // ??????????????????
        Optional.ofNullable(connection.channel().attr(AttributeKeys.closeConnection)).map(Attribute::get)
            .ifPresent(Disposable::dispose);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // ??????????????????
        publisher = actorSystem.actorOf(SpringProps.create(actorSystem, Publisher.class), "will-publisher");
    }
}
