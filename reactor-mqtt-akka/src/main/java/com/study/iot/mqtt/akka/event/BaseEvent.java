package com.study.iot.mqtt.akka.event;

import java.io.Serializable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 14:26
 */

@Getter
public class BaseEvent extends ApplicationEvent implements Serializable {

    /**
     * 事件ID
     */
    private final Long id;


    public BaseEvent(Object source, Long id) {
        super(source);
        this.id = id;
    }
}
