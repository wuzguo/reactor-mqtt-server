package com.study.iot.mqtt.producer;

import com.study.iot.mqtt.client.MqttClient;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ClientConfiguration;
import com.study.iot.mqtt.protocol.config.ClientConfiguration.Options;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/11 13:40
 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestProducer {

    @Autowired
    private ClientMessageRouter messageRouter;

    @Test
    public void testProducer1() throws InterruptedException {
        Options options = Options.builder()
            .clientId("345a0cc95adb4030bb183a2e0535381b")
            .userName("123456")
            .password("e10adc3949ba59abbe56e057f20f883e")
            .hasUserName(true)
            .hasPassword(true)
            .willMessage("hello，I'm producer")
            .hasWillFlag(true)
            .willTopic("/session/will/producer")
            .willQos(MqttQoS.AT_LEAST_ONCE)
            .build();

        ClientConfiguration configuration = ClientConfiguration.builder()
            .host("localhost").port(1884)
            .protocol(ProtocolType.MQTT)
            .heart(10000)
            .sendBufSize(32 * 1024)
            .revBufSize(32 * 1024)
            .backlog(128)
            .noDelay(true)
            .isSsl(false)
            .isLog(true)
            .onClose(() -> {
            })
            .options(options)
            .throwable(e -> log.error("starting mqtt client exception：{}", e.getMessage()))
            .build();
        CountDownLatch latch = new CountDownLatch(1);
        ClientSession connect = new MqttClient(configuration).connect(messageRouter).block();
        Thread.sleep(2000);
        connect.pub("/session/123456", "Hello, EveryOne".getBytes(StandardCharsets.UTF_8)).subscribe();
        latch.await();
    }
}
