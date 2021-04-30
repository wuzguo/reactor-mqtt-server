package com.study.iot.mqtt.transport.server.connection;

import com.study.iot.mqtt.protocal.*;
import com.study.iot.mqtt.protocal.config.ServerConfig;
import com.study.iot.mqtt.protocal.handler.MemoryChannelManager;
import com.study.iot.mqtt.protocal.handler.MemoryTopicManager;
import com.study.iot.mqtt.protocal.session.ServerSession;
import com.study.iot.mqtt.transport.server.handler.ServerMessageRouter;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Attribute;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 10:42
 */

public class ServerConnection implements ServerSession {


    private DisposableServer disposableServer;

    private ChannelManager channelManager;

    private TopicManager topicManager;

    private MessageHandler rsocketMessageHandler;

    private ServerConfig config;

    private ServerMessageRouter messageRouter;

    private DisposableServer wsDisposableServer;

    public ServerConnection(UnicastProcessor<TransportConnection> connections, DisposableServer server, DisposableServer wsDisposableServer, ServerConfig config) {
        this.disposableServer = server;
        this.config = config;
        this.rsocketMessageHandler = config.getMessageHandler();
        this.topicManager = Optional.ofNullable(config.getTopicManager()).orElse(new MemoryTopicManager());
        this.channelManager = Optional.ofNullable(config.getChannelManager()).orElse(new MemoryChannelManager());
        this.messageRouter = new ServerMessageRouter(config);
        this.wsDisposableServer = wsDisposableServer;
        connections.subscribe(this::subscribe);

    }

    private void subscribe(TransportConnection connection) {
        NettyInbound inbound = connection.getInbound();
        Connection c = connection.getConnection();
        Disposable disposable = Mono.fromRunnable(c::dispose)// 定时关闭
                .delaySubscription(Duration.ofSeconds(10))
                .subscribe();
        c.channel().attr(AttributeKeys.connectionAttributeKey).set(connection); // 设置connection
        c.channel().attr(AttributeKeys.closeConnection).set(disposable);   // 设置close
        connection.getConnection().onDispose(() -> { // 关闭  发送will消息
            Optional.ofNullable(connection.getConnection().channel().attr(AttributeKeys.WILL_MESSAGE)).map(Attribute::get)
                    .ifPresent(willMessage -> Optional.ofNullable(topicManager.getConnectionsByTopic(willMessage.getTopicName()))
                            .ifPresent(connections -> connections.forEach(co -> {
                                MqttQoS qoS = MqttQoS.valueOf(willMessage.getQos());
                                switch (qoS) {
                                    case AT_LEAST_ONCE:
                                        co.sendMessage(false, qoS, willMessage.isRetain(), willMessage.getTopicName(), willMessage.getCopyByteBuf()).subscribe();
                                        break;
                                    case EXACTLY_ONCE:
                                    case AT_MOST_ONCE:
                                        co.sendMessageRetry(false, qoS, willMessage.isRetain(), willMessage.getTopicName(), willMessage.getCopyByteBuf()).subscribe();
                                        break;
                                    default:
                                        co.sendMessage(false, qoS, willMessage.isRetain(), willMessage.getTopicName(), willMessage.getCopyByteBuf()).subscribe();
                                        break;
                                }
                            })));
            channelManager.removeConnections(connection); // 删除链接
            connection.getTopics().forEach(topic -> topicManager.deleteTopicConnection(topic, connection)); // 删除topic订阅
            Optional.ofNullable(connection.getConnection().channel().attr(AttributeKeys.device_id))
                    .map(Attribute::get)
                    .ifPresent(channelManager::removeDeviceId); // 设置device Id
            connection.destory();
        });
        inbound.receiveObject().cast(MqttMessage.class)
                .subscribe(message -> messageRouter.handler(message, connection));
    }

    @Override
    public Mono<List<TransportConnection>> getConnections() {
        return null;
    }

    @Override
    public Mono<Void> closeConnect(String clientId) {
        return null;
    }

    @Override
    public void dispose() {

    }
}
