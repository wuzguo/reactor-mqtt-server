package com.study.iot.mqtt.transport;


import com.study.iot.mqtt.common.domain.ProtocolProperties;
import com.study.iot.mqtt.common.utils.BeanUtils;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.transport.connection.ServerConnection;
import com.study.iot.mqtt.transport.router.ServerMessageRouter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:20
 */

public class TransportServerFactory {

    private final ProtocolFactory protocolFactory;

    private final UnicastProcessor<DisposableConnection> unicastProcessor;

    public TransportServerFactory() {
        protocolFactory = new ProtocolFactory();
        unicastProcessor = UnicastProcessor.create();
    }

    public Mono<ServerSession> start(ServerProperties properties, Set<ProtocolProperties> protocols,
        ContainerManager containerManager, ServerMessageRouter messageRouter) {
        // 获取 DisposableServer
        List<Disposable> disposables = protocols.stream()
            .map(protocolProperties -> protocolFactory.getProtocol(protocolProperties.getType())
                .map(protocol -> {
                    ServerProperties serverProperties = BeanUtils.copy(properties, ServerProperties.class);
                    serverProperties.setPort(protocolProperties.getPort());
                   return protocol.getTransport().start(serverProperties, unicastProcessor).subscribe();
                })
                .orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
        // 返回
        return Mono.just(this.wrapper(disposables, containerManager, messageRouter))
            .doOnError(properties.getThrowable());
    }

    private ServerSession wrapper(List<Disposable> disposables, ContainerManager containerManager,
        ServerMessageRouter messageRouter) {
        return new ServerConnection(unicastProcessor, disposables, containerManager, messageRouter);
    }
}
