package com.study.iot.mqtt.transport.server;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocal.ProtocolFactory;
import com.study.iot.mqtt.protocal.TransportConnection;
import com.study.iot.mqtt.protocal.config.ServerConfig;
import com.study.iot.mqtt.protocal.session.ServerSession;
import com.study.iot.mqtt.protocal.ws.WsProtocol;
import com.study.iot.mqtt.protocal.ws.WsTransport;
import com.study.iot.mqtt.transport.server.connection.ServerConnection;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;


public class TransportServerFactory {

    private ProtocolFactory protocolFactory;

    private UnicastProcessor<TransportConnection> unicastProcessor = UnicastProcessor.create();

    private ServerConfig config;

    private DisposableServer wsServer;

    public TransportServerFactory() {
        protocolFactory = new ProtocolFactory();
    }


    public Mono<ServerSession> start(ServerConfig config) {
        this.config = config;
        if (config.getProtocol() == ProtocolType.MQTT.name()) { // 开启
            WsTransport wsTransport = new WsTransport(new WsProtocol());
            ServerConfig wsConfig = copy(config);
            wsServer = wsTransport.start(wsConfig, unicastProcessor).block();
        }
        return Mono.from(
                protocolFactory.getProtocol(ProtocolType.valueOf(config.getProtocol()))
                        .get().getTransport()
                        .start(config, unicastProcessor))
                .map(this::wrapper)
                .doOnError(config.getThrowableConsumer());
    }

    private ServerConfig copy(ServerConfig config) {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setThrowableConsumer(config.getThrowableConsumer());
        serverConfig.setLog(config.isLog());
        serverConfig.setMessageHandler(config.getMessageHandler());
        serverConfig.setAuth(config.getAuth());
        serverConfig.setChannelManager(config.getChannelManager());
        serverConfig.setIp(config.getIp());
        serverConfig.setPort(8443);
        serverConfig.setSsl(config.isSsl());
        serverConfig.setProtocol(ProtocolType.WEB_SOCKET.name());
        serverConfig.setHeart(config.getHeart());
        serverConfig.setTopicManager(config.getTopicManager());
        serverConfig.setRevBufSize(config.getRevBufSize());
        serverConfig.setSendBufSize(config.getSendBufSize());
        serverConfig.setNoDelay(config.isNoDelay());
        serverConfig.setKeepAlive(config.isKeepAlive());
        return serverConfig;
    }

    private ServerSession wrapper(DisposableServer server) {
        return new ServerConnection(unicastProcessor, server, wsServer, config);
    }
}
