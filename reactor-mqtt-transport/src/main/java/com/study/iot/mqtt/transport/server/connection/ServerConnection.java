package com.study.iot.mqtt.transport.server.connection;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import com.study.iot.mqtt.transport.strategy.WillCapable;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Attribute;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.Connection;
import reactor.netty.DisposableChannel;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 10:42
 */

public class ServerConnection implements ServerSession {

    private final DisposableServer disposableServer;

    private final DisposableServer wsDisposableServer;

    private final CacheManager cacheManager;

    private final ServerMessageRouter messageRouter;

    public ServerConnection(UnicastProcessor<DisposableConnection> processor, DisposableServer disposableServer,
        DisposableServer wsDisposableServer, CacheManager cacheManager, ServerMessageRouter messageRouter) {
        this.disposableServer = disposableServer;
        this.wsDisposableServer = wsDisposableServer;
        this.messageRouter = messageRouter;
        this.cacheManager = cacheManager;
        processor.subscribe(this::subscribe);
    }

    private void subscribe(DisposableConnection disposableConnection) {
        NettyInbound inbound = disposableConnection.getInbound();
        Connection connection = disposableConnection.getConnection();
        // 定时关闭
        Disposable disposable = Mono.fromRunnable(connection::dispose)
            .delaySubscription(Duration.ofSeconds(10))
            .subscribe();
        // 设置 connection
        connection.channel().attr(AttributeKeys.disposableConnection).set(disposableConnection);
        // 设置 close
        connection.channel().attr(AttributeKeys.closeConnection).set(disposable);
        // 关闭发送will消息
        disposableConnection.getConnection().onDispose(() -> {
            Optional.ofNullable(disposableConnection.getConnection().channel().attr(AttributeKeys.willMessage))
                .map(Attribute::get)
                .ifPresent(
                    willMessage -> Optional.ofNullable(cacheManager.topic().getConnections(willMessage.getTopicName()))
                        .ifPresent(connections -> connections.forEach(connect -> {
                            MqttQoS qoS = MqttQoS.valueOf(willMessage.getQos());
                            Optional.ofNullable(
                                messageRouter.getWillContainer().findStrategy(StrategyGroup.WILL_SERVER, qoS))
                                .ifPresent(capable -> ((WillCapable) capable).handle(qoS, connect, willMessage));
                        })));
            disposableConnection.destory();
        });
        // 订阅各种消息
        inbound.receiveObject().cast(MqttMessage.class)
            .subscribe(message -> messageRouter.handle(message, disposableConnection));
    }

    @Override
    public Mono<List<DisposableConnection>> getConnections() {
        return Mono.just(Lists.newArrayList(cacheManager.channel().getConnections()));
    }

    @Override
    public Mono<Void> closeConnect(String identity) {
        return Mono.fromRunnable(() -> Optional.ofNullable(cacheManager.channel().getAndRemove(identity))
            .ifPresent(DisposableConnection::dispose));
    }

    @Override
    public void dispose() {
        disposableServer.dispose();
        Optional.ofNullable(wsDisposableServer).ifPresent(DisposableChannel::dispose);
    }
}
