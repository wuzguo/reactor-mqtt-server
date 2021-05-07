package com.study.iot.mqtt.transport.server.connection;

import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.connection.TransportConnection;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Attribute;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.Connection;
import reactor.netty.DisposableChannel;
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

    private CacheManager cacheManager;

    private ServerMessageRouter messageRouter;

    private DisposableServer wsDisposableServer;

    public ServerConnection(UnicastProcessor<TransportConnection> connections, DisposableServer disposableServer,
                            DisposableServer wsDisposableServer, CacheManager cacheManager, ServerMessageRouter messageRouter) {
        this.disposableServer = disposableServer;
        this.wsDisposableServer = wsDisposableServer;
        this.messageRouter = messageRouter;
        this.cacheManager = cacheManager;
        connections.subscribe(this::subscribe);
    }

    private void subscribe(TransportConnection transport) {
        NettyInbound inbound = transport.getInbound();
        Connection connection = transport.getConnection();
        // 定时关闭
        Disposable disposable = Mono.fromRunnable(connection::dispose)
                .delaySubscription(Duration.ofSeconds(10))
                .subscribe();
        // 设置 connection
        connection.channel().attr(AttributeKeys.connectionAttributeKey).set(transport);
        // 设置 close
        connection.channel().attr(AttributeKeys.closeConnection).set(disposable);
        // 关闭发送will消息
        transport.getConnection().onDispose(() -> {
            Optional.ofNullable(transport.getConnection().channel().attr(AttributeKeys.WILL_MESSAGE)).map(Attribute::get)
                    .ifPresent(willMessage -> Optional.ofNullable(cacheManager.topic().getConnectionsByTopic(willMessage.getTopicName()))
                            .ifPresent(connections -> connections.forEach(connect -> {
                                MqttQoS qoS = MqttQoS.valueOf(willMessage.getQos());
                                switch (qoS) {
                                    case AT_LEAST_ONCE:
                                        connect.sendMessage(false, qoS, willMessage.isRetain(), willMessage.getTopicName(), willMessage.getCopyByteBuf()).subscribe();
                                        break;
                                    case EXACTLY_ONCE:
                                    case AT_MOST_ONCE:
                                        connect.sendMessageRetry(false, qoS, willMessage.isRetain(), willMessage.getTopicName(), willMessage.getCopyByteBuf()).subscribe();
                                        break;
                                    default:
                                        connect.sendMessage(false, qoS, willMessage.isRetain(), willMessage.getTopicName(), willMessage.getCopyByteBuf()).subscribe();
                                        break;
                                }
                            })));
            // 删除链接
            cacheManager.channel().removeConnections(transport);
            // 删除topic订阅
            transport.getTopics().forEach(topic -> cacheManager.topic().deleteTopicConnection(topic, transport));
            Optional.ofNullable(transport.getConnection().channel().attr(AttributeKeys.device_id))
                    .map(Attribute::get)
                    // 设置device
                    .ifPresent(cacheManager.channel()::removeDeviceId);
            transport.destory();
        });
        inbound.receiveObject().cast(MqttMessage.class)
                .subscribe(message -> messageRouter.handler(message, transport));
    }

    @Override
    public Mono<List<TransportConnection>> getConnections() {
        return Mono.just(cacheManager.channel().getConnections());
    }

    @Override
    public Mono<Void> closeConnect(String clientId) {
        return Mono.fromRunnable(() -> Optional.ofNullable(cacheManager.channel().getRemoveDeviceId(clientId))
                .ifPresent(TransportConnection::dispose));
    }

    @Override
    public void dispose() {
        disposableServer.dispose();
        Optional.ofNullable(wsDisposableServer)
                .ifPresent(DisposableChannel::dispose);
    }
}
