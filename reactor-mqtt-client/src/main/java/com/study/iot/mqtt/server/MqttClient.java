package com.study.iot.mqtt.server;

import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.config.ClientConfiguration.Options;
import com.study.iot.mqtt.transport.client.TransportClient;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/10 17:21
 */

@Slf4j
public class MqttClient implements ApplicationRunner {

    private final ClientMessageRouter messageRouter;

    public MqttClient(ClientMessageRouter messageRouter) {
        this.messageRouter = messageRouter;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Options options = Options.builder()
            .clientId("1a9a0cc95adb4030bb183a2e0535280b")
            .password("123456")
            .userName("e10adc3949ba59abbe56e057f20f883e")
            .willMessage("hello，I'm offline")
            .willTopic("/session/will/123456")
            .willQos(MqttQoS.AT_LEAST_ONCE)
            .build();

        ClientConfiguration configuration = ClientConfiguration.builder()
            .host("localhost").port(2884)
            .protocol(ProtocolType.MQTT)
            .heart(100000)
            .sendBufSize(32 * 1024)
            .revBufSize(32 * 1024)
            .backlog(128)
            .keepAlive(false)
            .noDelay(true)
            .isSsl(false)
            .isLog(true)
            .options(options)
            .throwable(e -> log.error("starting mqtt client exception：{}", e.getMessage()))
            .build();

        Mono<ClientSession> connect = new TransportClient().create(configuration).connect(messageRouter);
        connect.map(clientSession -> clientSession.sub("/session/client/123456")).subscribe();
    }
}
