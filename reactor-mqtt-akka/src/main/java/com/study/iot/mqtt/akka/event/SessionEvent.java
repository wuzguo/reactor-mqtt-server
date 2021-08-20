package com.study.iot.mqtt.akka.event;

import lombok.Getter;
import lombok.Setter;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 15:30
 */

@Getter
@Setter
public class SessionEvent extends BaseEvent {
    /**
     * 发送消息的客户端ID
     */
    private String identity;

    /**
     * 发送消息的实例ID
     */
    private String instanceId;

    /**
     * 消息对应的ROW
     */
    private String row;

    /**
     * 消息的Topic
     */
    private String topic;

    public SessionEvent(Object source, Long id) {
        super(source, id);
    }
}
