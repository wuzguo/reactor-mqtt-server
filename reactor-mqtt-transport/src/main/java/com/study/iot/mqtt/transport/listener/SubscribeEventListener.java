package com.study.iot.mqtt.transport.listener;

import com.study.iot.mqtt.akka.event.SubscribeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 14:32
 */

@Slf4j
@Component
public class SubscribeEventListener {

    @EventListener
    public void listen(SubscribeEvent event) {
        log.info("receive subscribe event info: {}", event);
    }
}
