package com.study.iot.mqtt.comsumer;

import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.config.ClientConfiguration.Options;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.server.MqttClient;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import com.study.iot.mqtt.transport.config.TransportAutoConfiguration;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/11 11:39
 */

@Slf4j
@ContextConfiguration(classes = {TransportAutoConfiguration.class})
@RunWith(SpringRunner.class)
public class TestComsumer {

    @Autowired
    private ClientMessageRouter messageRouter;

    @Test
    public void testComsumer1() {
        Options options = Options.builder()
            .clientId("1a9a0cc95adb4030bb183a2e0535280b")
            .password("123456")
            .userName("e10adc3949ba59abbe56e057f20f883e")
            .willMessage("hello，I'm offline")
            .willTopic("/session/will/123456")
            .willQos(MqttQoS.AT_LEAST_ONCE)
            .build();

        ClientConfiguration configuration = ClientConfiguration.builder()
            .host("localhost").port(1884)
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
        ClientSession connect = new MqttClient(messageRouter).connect(configuration).block();
        connect.sub("/session/client/123456").subscribe();
    }
}
