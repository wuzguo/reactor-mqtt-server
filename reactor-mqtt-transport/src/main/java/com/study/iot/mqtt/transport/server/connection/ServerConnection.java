package com.study.iot.mqtt.transport.server.connection;

import com.google.common.collect.Lists;
import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.common.utils.CollectionUtil;
import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.transport.constant.StrategyGroup;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import com.study.iot.mqtt.transport.strategy.WillCapable;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Attribute;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.Connection;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 10:42
 */

public class ServerConnection implements ServerSession {

    private final List<Disposable> disposables;

    private final CacheManager cacheManager;

    private final ServerMessageRouter messageRouter;

    public ServerConnection(UnicastProcessor<DisposableConnection> processor, List<Disposable> disposables,
        CacheManager cacheManager, ServerMessageRouter messageRouter) {
        this.disposables = disposables;
        this.messageRouter = messageRouter;
        this.cacheManager = cacheManager;
        processor.subscribe(this::subscribe);
    }

    private void subscribe(DisposableConnection disposableConnection) {
        Connection connection = disposableConnection.getConnection();
        // 定时关闭
        Disposable closeConnection = Mono.fromRunnable(connection::dispose)
            .delaySubscription(Duration.ofSeconds(10))
            .subscribe();
        // 设置 close
        connection.channel().attr(AttributeKeys.closeConnection).set(closeConnection);
        // 设置 connection
        connection.channel().attr(AttributeKeys.disposableConnection).set(disposableConnection);
        // 关闭连接时处理的逻辑
        this.onDispose(disposableConnection);
        // 订阅各种消息
        disposableConnection.receive(MqttMessage.class)
            .subscribe(message -> messageRouter.handle(message, disposableConnection));
    }

    @Override
    public Mono<List<Disposable>> getConnections() {
        Collection<Disposable> collection = cacheManager.channel().getConnections();
        return CollectionUtil.isEmpty(collection) ? Mono.empty() : Mono.just(Lists.newArrayList(collection));
    }

    @Override
    public Mono<Void> closeConnect(String identity) {
        return Mono.fromRunnable(() -> Optional.ofNullable(cacheManager.channel().getAndRemove(identity))
            .ifPresent(Disposable::dispose));
    }

    @Override
    public void onDispose(DisposableConnection disposableConnection) {
        Connection connection = disposableConnection.getConnection();
        connection.onDispose(() -> {
            Optional.ofNullable(connection.channel().attr(AttributeKeys.willMessage)).map(Attribute::get)
                .ifPresent(
                    willMessage -> Optional.ofNullable(cacheManager.topic().getConnections(willMessage.getTopic()))
                        .ifPresent(disposables -> disposables.forEach(disposable -> {
                            MqttQoS qoS = MqttQoS.valueOf(willMessage.getQos());
                            Optional.ofNullable(
                                messageRouter.getWillContainer().findStrategy(StrategyGroup.WILL_SERVER, qoS))
                                .ifPresent(capable -> ((WillCapable) capable)
                                    .handle(qoS, (DisposableConnection) disposable, willMessage));
                        })));
            // 自己超时关闭的时候会走这段代码，要清除缓存中的连接信息
            // 删除设备标识
            Optional.ofNullable(connection.channel().attr(AttributeKeys.identity)).map(Attribute::get)
                .ifPresent(cacheManager.channel()::remove);
            // 删除topic订阅
            Optional.ofNullable(disposableConnection.getTopics()).ifPresent(topics -> topics
                .forEach(topic -> cacheManager.topic().remove(topic, disposableConnection)));
            disposableConnection.destory();
        });
    }

    @Override
    public void dispose() {
        Optional.ofNullable(disposables).ifPresent(disposables -> disposables.forEach(Disposable::dispose));
    }
}
