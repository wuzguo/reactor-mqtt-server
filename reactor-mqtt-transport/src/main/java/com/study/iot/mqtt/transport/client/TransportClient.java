package com.study.iot.mqtt.transport.client;


import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.session.ClientSession;
import io.netty.handler.codec.mqtt.MqttQoS;
import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class TransportClient {

    private static ClientConfiguration config;

    private static TransportClientFactory transportFactory;

    private static ClientConfiguration.Options options;

    private TransportClient() {

    }

    public static TransportBuilder create(String ip, int port) {
        return new TransportBuilder(ip, port);
    }

    public TransportBuilder create() {
        return new TransportBuilder();
    }

    public static class TransportBuilder {

        public TransportBuilder() {
            config = new ClientConfiguration();
            transportFactory = new TransportClientFactory();
            options = config.new Options();
        }

        public TransportBuilder(String ip, int port) {
            this();
            config = new ClientConfiguration();
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

        public TransportBuilder onClose(Runnable onClose) {
            config.setOnClose(onClose);
            return this;
        }

        public TransportBuilder exception(Consumer<Throwable> exceptionConsumer) {
            config.setThrowableConsumer(exceptionConsumer);
            return this;
        }


        public TransportBuilder ssl(boolean ssl) {
            config.setSsl(ssl);
            return this;
        }

        public TransportBuilder messageAcceptor(BiConsumer<String, byte[]> messageAcceptor) {
            config.setMessageAcceptor(messageAcceptor);
            return this;
        }


        public TransportBuilder clientId(String clientId) {
            options.setClientIdentifier(clientId);
            return this;
        }

        public TransportBuilder username(String username) {
            options.setUserName(username);
            options.setHasUserName(true);
            return this;
        }


        public TransportBuilder password(String password) {
            options.setPassword(password);
            options.setHasPassword(true);
            return this;
        }

        public TransportBuilder willTopic(String willTopic) {
            options.setWillTopic(willTopic);
            options.setHasWillFlag(true);
            return this;
        }

        public TransportBuilder willMessage(String willMessage) {
            options.setWillMessage(willMessage);
            options.setHasWillFlag(true);
            return this;
        }

        public TransportBuilder willQos(MqttQoS qoS) {
            options.setWillQos(qoS.value());
            options.setHasWillFlag(true);
            return this;
        }


        public TransportBuilder log(boolean log) {
            config.setLog(log);
            return this;
        }


        public Mono<ClientSession> connect() {
            config.setOptions(options);
            config.checkConfig();
            return transportFactory.connect(config);
        }

    }
}
