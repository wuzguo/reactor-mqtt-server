package com.study.iot.mqtt.client.producer;

import com.study.iot.mqtt.client.connect.MqttClient;
import com.study.iot.mqtt.client.router.ClientMessageRouter;
import com.study.iot.mqtt.client.transport.TransportClient;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ClientProperties.ConnectOptions;
import com.study.iot.mqtt.protocol.session.ClientSession;
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
        ConnectOptions options = new ConnectOptions("123456", "e10adc3949ba59abbe56e057f20f883e")
            .setClientId("345a0cc95adb4030bb183a2e0535381b")
            .setWillMessage("hello，I'm producer")
            .setWillTopic("/session/will/consumer")
            .setWillQos(MqttQoS.AT_LEAST_ONCE)
            .setHasCleanSession(true);

        ClientProperties properties = ClientProperties.builder()
            .host("localhost").port(1884)
            .protocol(ProtocolType.MQTT)
            .keepAliveSeconds(10000)
            .isSsl(false)
            .isLog(true)
            .onClose(() -> { })
            .options(options)
            .throwable(e -> log.error("starting mqtt client exception：{}", e.getMessage()))
            .build();
        CountDownLatch latch = new CountDownLatch(1);
        ClientSession connect = new TransportClient().create(properties).connect(messageRouter).block();
        Thread.sleep(2000);
        connect.publish("/session/123456", "Hello, EveryOne".getBytes(StandardCharsets.UTF_8)).subscribe();
        latch.await();
    }
}
