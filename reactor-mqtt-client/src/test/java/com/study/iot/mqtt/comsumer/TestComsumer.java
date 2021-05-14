package com.study.iot.mqtt.comsumer;

import com.study.iot.mqtt.client.MqttClient;
import com.study.iot.mqtt.common.annocation.ProtocolType;
import com.study.iot.mqtt.protocol.config.ClientProperties;
import com.study.iot.mqtt.protocol.config.ClientProperties.ConnectOptions;
import com.study.iot.mqtt.protocol.session.ClientSession;
import com.study.iot.mqtt.transport.client.router.ClientMessageRouter;
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
public class TestComsumer {

    @Autowired
    private ClientMessageRouter messageRouter;

    @Test
    public void testComsumer1() throws InterruptedException {
        ConnectOptions options = ConnectOptions.builder()
            .clientId("329a0cc95adb4030bb183a2e0535280b")
            .userName("123456")
            .password("e10adc3949ba59abbe56e057f20f883e")
            .willMessage("hello，I'm consumer")
            .willTopic("/session/will/consumer")
            .willQos(MqttQoS.AT_LEAST_ONCE)
            .hasWillFlag(true)
            .hasCleanSession(true)
            .build();

        ClientProperties configuration = ClientProperties.builder()
            .host("localhost").port(1884)
            .protocol(ProtocolType.MQTT)
            .keepAliveSeconds(60)
            .isLog(true)
            .onClose(() -> { })
            .options(options)
            .throwable(e -> log.error("starting mqtt client exception：{}", e.getMessage()))
            .build();
        CountDownLatch latch = new CountDownLatch(1);
        ClientSession connect = new MqttClient(configuration).connect(messageRouter).block();
        Thread.sleep(1000);
        connect.sub("/session/123456").subscribe();
        latch.await();
    }
}
