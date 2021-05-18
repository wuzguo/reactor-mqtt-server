package com.study.iot.mqtt.transport;


import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.transport.connection.ServerConnection;
import com.study.iot.mqtt.transport.router.ServerMessageRouter;
import java.util.List;
import java.util.stream.Collectors;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;


public class TransportServerFactory {

    private final ProtocolFactory protocolFactory;

    private final UnicastProcessor<DisposableConnection> unicastProcessor;

    public TransportServerFactory() {
        protocolFactory = new ProtocolFactory();
        unicastProcessor = UnicastProcessor.create();
    }

    public Mono<ServerSession> start(ServerProperties properties, CacheManager cacheManager,
        ServerMessageRouter messageRouter) {
        // 获取DisposableServer
        List<Disposable> disposables = properties.getProtocols().stream()
            .map(protocolType -> protocolFactory.getProtocol(protocolType)
                .get()
                .getTransport()
                .start(properties, unicastProcessor).subscribe()).collect(Collectors.toList());
        // 返回
        return Mono.just(this.wrapper(disposables, cacheManager, messageRouter))
            .doOnError(properties.getThrowable());
    }

    private ServerSession wrapper(List<Disposable> disposables, CacheManager cacheManager,
        ServerMessageRouter messageRouter) {
        return new ServerConnection(unicastProcessor, disposables, cacheManager, messageRouter);
    }
}
