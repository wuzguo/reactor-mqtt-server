package com.study.iot.mqtt.protocol;

import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ServerProperties;
import com.study.iot.mqtt.protocol.connection.DisposableConnection;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;


public abstract class ProtocolTransport {

    protected Protocol protocol;

    public ProtocolTransport(Protocol protocol) {
        this.protocol = protocol;
    }


    public abstract Mono<? extends DisposableServer> start(ServerProperties properties,
        UnicastProcessor<DisposableConnection> processor);


    public abstract Mono<DisposableConnection> connect(ClientProperties configuration);
}
