package com.study.iot.mqtt.akka.bus;

import com.study.iot.mqtt.akka.event.BaseEvent;
import lombok.Getter;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 17:00
 */

@Getter
public class EventBusMessage {

    /**
     * Topic
     */
    private final String topic;

    /**
     * 事件
     */
    private final BaseEvent event;

    public EventBusMessage(String topic, BaseEvent event) {
        this.topic = topic;
        this.event = event;
    }
}
