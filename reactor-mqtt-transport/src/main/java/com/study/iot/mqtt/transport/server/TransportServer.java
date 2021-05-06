package com.study.iot.mqtt.transport.server;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocal.MessageHandler;
import com.study.iot.mqtt.protocal.config.ServerConfiguration;
import com.study.iot.mqtt.protocal.session.ServerSession;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TransportServer {

    private static ServerConfiguration config;

    private static TransportServerFactory transportFactory;


    private TransportServer() {
    }

    public static class TransportBuilder {

        public TransportBuilder() {
            config = new ServerConfiguration();
            transportFactory = new TransportServerFactory();
        }

        public TransportBuilder(String ip, int port) {
            this();
            config.setIp(ip);
            config.setPort(port);
        }

        public TransportBuilder protocol(ProtocolType protocolType) {
            config.setProtocol(protocolType.name());
            return this;
        }

        public TransportBuilder heart(int heart) {
            config.setHeart(heart);
            return this;
        }

        public TransportBuilder ssl(boolean ssl) {
            config.setSsl(ssl);
            return this;
        }

        public TransportBuilder log(boolean log) {
            config.setLog(log);
            return this;
        }

        public TransportBuilder keepAlive(boolean isKeepAlive) {
            config.setKeepAlive(isKeepAlive);
            return this;
        }

        public TransportBuilder noDelay(boolean noDelay) {
            config.setNoDelay(noDelay);
            return this;
        }

        public TransportBuilder backlog(int length) {
            config.setBacklog(length);
            return this;
        }

        public TransportBuilder sendBufSize(int size) {
            config.setSendBufSize(size);
            return this;
        }

        public TransportBuilder revBufSize(int size) {
            config.setRevBufSize(size);
            return this;
        }

        public TransportBuilder auth(BiFunction<String, String, Boolean> auth) {
            config.setAuth(auth);
            return this;
        }

        public TransportBuilder messageHandler(MessageHandler messageHandler) {
            Optional.ofNullable(messageHandler)
                    .ifPresent(config::setMessageHandler);
            return this;
        }

        public TransportBuilder exception(Consumer<Throwable> exceptionConsumer) {
            Optional.ofNullable(exceptionConsumer)
                    .ifPresent(config::setThrowableConsumer);
            return this;
        }

        public Mono<ServerSession> start() {
            config.checkConfig();
            return transportFactory.start(config);
        }
    }

    public static TransportBuilder create(String ip, int port) {
        return new TransportBuilder(ip, port);
    }

    public static TransportBuilder create() {
        return new TransportBuilder();
    }
}
