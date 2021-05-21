package com.study.iot.mqtt.akka.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/21 9:29
 */

@Setter
@Getter
public class SenderEvent extends BaseEvent {

    /**
     * 客户端标识
     */
    private String identity;

    /**
     * 对应的实例ID
     */
    private String instanceId;

    /**
     * 路径
     */
    private String path;

    public SenderEvent(Object source, Long id) {
        super(source, id);
    }
}
