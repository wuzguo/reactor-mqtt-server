package com.study.iot.mqtt.transport.server;


import com.study.iot.mqtt.cache.manager.CacheManager;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.config.ServerConfiguration;
import com.study.iot.mqtt.transport.server.router.ServerMessageRouter;
import com.study.iot.mqtt.common.connection.DisposableConnection;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.transport.server.connection.ServerConnection;
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

    public Mono<ServerSession> start(ServerConfiguration config, CacheManager cacheManager,
        ServerMessageRouter messageRouter) {
        // 获取DisposableServer
        List<Disposable> disposables = config.getProtocols().stream()
            .map(protocolType -> protocolFactory.getProtocol(protocolType)
                .get()
                .getTransport()
                .start(config, unicastProcessor).subscribe()).collect(Collectors.toList());
        // 返回
        return Mono.just(this.wrapper(disposables, cacheManager, messageRouter))
            .doOnError(config.getThrowable());
    }

    private ServerSession wrapper(List<Disposable> disposables, CacheManager cacheManager,
        ServerMessageRouter messageRouter) {
        return new ServerConnection(unicastProcessor, disposables, cacheManager, messageRouter);
    }
}
