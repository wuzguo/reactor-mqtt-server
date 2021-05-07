package com.study.iot.mqtt.protocol.session;

import com.study.iot.mqtt.protocol.TransportConnection;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ServerSession extends Disposable {


    Mono<List<TransportConnection>> getConnections();


    Mono<Void> closeConnect(String clientId);
}
