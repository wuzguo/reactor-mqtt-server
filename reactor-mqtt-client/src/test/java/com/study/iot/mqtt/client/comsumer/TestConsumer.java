package com.study.iot.mqtt.client.comsumer;

import com.study.iot.mqtt.client.router.ClientMessageRouter;
import com.study.iot.mqtt.client.transport.TransportClient;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ClientProperties.ConnectOptions;
import com.study.iot.mqtt.protocol.session.ClientSession;
import io.netty.handler.codec.mqtt.MqttQoS;
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
 * @date 2021/5/11 11:39
 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestConsumer {

    @Autowired
    private ClientMessageRouter messageRouter;

    @Test
    public void testConsumer() throws InterruptedException {
        ConnectOptions options = new ConnectOptions("123456", "e10adc3949ba59abbe56e057f20f883e")
            .setClientId("329a0cc95adb4030bb183a2e0535280b")
            .setWillMessage("hello，I'm consumer")
            .setWillTopic("/session/will/consumer")
            .setWillQos(MqttQoS.AT_LEAST_ONCE)
            .setHasCleanSession(true);

        ClientProperties properties = ClientProperties.builder()
            .host("localhost").port(1800)
            .protocol(ProtocolType.MQTT)
            .keepAliveSeconds(60)
            .isLog(true)
            .onClose(() -> { })
            .options(options)
            .throwable(e -> log.error("starting mqtt client exception：{}", e.getMessage()))
            .build();
        CountDownLatch latch = new CountDownLatch(1);
        ClientSession connect = new TransportClient().create(properties).connect(messageRouter).block();
        Thread.sleep(1000);
        connect.subscribe("/session/123456").subscribe();
        latch.await();
    }
}
