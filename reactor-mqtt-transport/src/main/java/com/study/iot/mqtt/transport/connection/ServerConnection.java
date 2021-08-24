package com.study.iot.mqtt.transport.connection;

import com.study.iot.mqtt.protocol.AttributeKeys;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.container.TopicContainer;
import com.study.iot.mqtt.transport.router.ServerMessageRouter;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.Attribute;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    private final ContainerManager containerManager;

    private final ServerMessageRouter messageRouter;

    public ServerConnection(UnicastProcessor<DisposableConnection> processor, List<Disposable> disposables,
        ContainerManager containerManager, ServerMessageRouter messageRouter) {
        this.disposables = disposables;
        this.messageRouter = messageRouter;
        this.containerManager = containerManager;
        processor.subscribe(this::subscribe);
    }

    private void subscribe(DisposableConnection disposableConnection) {
        Connection connection = disposableConnection.getConnection();
        // 定时关闭
        Disposable closeConnection = Mono.fromRunnable(connection::dispose)
            .delaySubscription(Duration.ofSeconds(10)).subscribe();
        // 设置 close
        connection.channel().attr(AttributeKeys.closeConnection).set(closeConnection);
        // 关闭连接时处理的逻辑
        this.setDisposeDeal(disposableConnection);
        // 订阅各种消息
        disposableConnection.receive(MqttMessage.class)
            .subscribe(message -> messageRouter.handle(message, disposableConnection));
    }

    @Override
    public Mono<List<Disposable>> getConnections() {
        return Mono.just(containerManager.take(CacheGroup.CHANNEL).getAll().stream()
            .map(disposable -> (Disposable) disposable)
            .collect(Collectors.toList()));
    }

    @Override
    public Mono<Void> closeConnect(String identity) {
        return Mono.fromRunnable(
            () -> Optional.ofNullable(containerManager.take(CacheGroup.CHANNEL).getAndRemove(identity))
                .ifPresent(serializable -> {
                    Disposable disposable = (Disposable) serializable;
                    disposable.dispose();
                }));
    }

    @Override
    public void setDisposeDeal(DisposableConnection disposableConnection) {
        Connection connection = disposableConnection.getConnection();
        // 自己超时关闭的时候会走这段代码，要清除缓存中的连接信息
        connection.onDispose(() -> {
            // 删除设备标识
            Optional.ofNullable(connection.channel().attr(AttributeKeys.identity)).map(Attribute::get)
                .ifPresent(identity -> {
                    containerManager.take(CacheGroup.CHANNEL).remove(identity);
                    connection.channel().attr(AttributeKeys.identity).set(null);
                });
            // 删除topic订阅
            Optional.ofNullable(disposableConnection.getTopics())
                .ifPresent(topics -> topics.forEach(topic -> {
                    TopicContainer topicContainer = containerManager.topic(CacheGroup.TOPIC);
                    topicContainer.remove(topic, disposableConnection);
                }));
            // 清空各种缓存
            disposableConnection.destroy();
        });
    }

    @Override
    public void dispose() {
        Optional.ofNullable(disposables).ifPresent(disposables -> disposables.forEach(Disposable::dispose));
    }
}
