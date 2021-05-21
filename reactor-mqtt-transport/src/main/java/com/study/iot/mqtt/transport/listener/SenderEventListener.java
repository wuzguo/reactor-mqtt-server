package com.study.iot.mqtt.transport.listener;

import com.study.iot.mqtt.akka.event.SenderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 9:32
 */

@Slf4j
@Component
public class SenderEventListener {

    @EventListener
    public void listen(SenderEvent event) {
        log.info("receive sender event info: {}", event);
    }
}
