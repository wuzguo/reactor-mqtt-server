package com.study.iot.mqtt.akka.event;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 14:32
 */

@Getter
@Setter
public class SubscribeEvent extends BaseEvent {

    /**
     * 客户端标识
     */
    private String identity;

    /**
     * 对应的实例ID
     */
    private String instanceId;

    /**
     * 主题名称
     */
    private Set<String> topicNames;

    public SubscribeEvent(Object source, Long id) {
        super(source, id);
    }
}
