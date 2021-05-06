package com.study.iot.mqtt.transport.server;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.ProtocolFactory;
import com.study.iot.mqtt.protocol.TransportConnection;
import com.study.iot.mqtt.protocol.config.ServerConfiguration;
import com.study.iot.mqtt.protocol.session.ServerSession;
import com.study.iot.mqtt.protocol.ws.WsProtocol;
import com.study.iot.mqtt.protocol.ws.WsTransport;
import com.study.iot.mqtt.transport.server.connection.ServerConnection;
import com.study.iot.mqtt.transport.strategy.StrategyContainer;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;


public class TransportServerFactory {

    private ProtocolFactory protocolFactory;

    private UnicastProcessor<TransportConnection> unicastProcessor = UnicastProcessor.create();

    private ServerConfiguration config;

    private DisposableServer wsServer;

    private StrategyContainer container;

    public TransportServerFactory() {
        protocolFactory = new ProtocolFactory();
    }


    public Mono<ServerSession> start(ServerConfiguration config, StrategyContainer container) {
        this.config = config;
        this.container = container;
        // 开启
        if (config.getProtocol().equals(ProtocolType.MQTT.name())) {
            WsTransport wsTransport = new WsTransport(new WsProtocol());
            ServerConfiguration wsConfig = copy(config);
            wsServer = wsTransport.start(wsConfig, unicastProcessor).block();
        }
        return Mono.from(protocolFactory.getProtocol(ProtocolType.valueOf(config.getProtocol()))
                .get()
                .getTransport()
                .start(config, unicastProcessor))
                .map(this::wrapper)
                .doOnError(config.getThrowableConsumer());
    }

    private ServerConfiguration copy(ServerConfiguration config) {
        ServerConfiguration serverConfiguration = new ServerConfiguration();
        serverConfiguration.setThrowableConsumer(config.getThrowableConsumer());
        serverConfiguration.setLog(config.isLog());
        serverConfiguration.setMessageHandler(config.getMessageHandler());
        serverConfiguration.setAuth(config.getAuth());
        serverConfiguration.setChannelManager(config.getChannelManager());
        serverConfiguration.setIp(config.getIp());
        serverConfiguration.setPort(8443);
        serverConfiguration.setSsl(config.isSsl());
        serverConfiguration.setProtocol(ProtocolType.WEB_SOCKET.name());
        serverConfiguration.setHeart(config.getHeart());
        serverConfiguration.setTopicManager(config.getTopicManager());
        serverConfiguration.setRevBufSize(config.getRevBufSize());
        serverConfiguration.setSendBufSize(config.getSendBufSize());
        serverConfiguration.setNoDelay(config.isNoDelay());
        serverConfiguration.setKeepAlive(config.isKeepAlive());
        return serverConfiguration;
    }

    private ServerSession wrapper(DisposableServer server) {
        return new ServerConnection(unicastProcessor, server, wsServer, config, container);
    }
}
