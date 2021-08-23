package com.study.iot.mqtt.common.domain;

import com.study.iot.mqtt.common.message.BaseMessage;
import java.io.Serializable;
import java.util.List;
import java.util.Queue;
import lombok.Builder;
import lombok.Data;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 11:50
 */

@Data
@Builder
public class ConnectSession implements Serializable {

    /**
     * SESSION ID
     */
    private Long sessionId;

    /**
     * 客户端标识
     */
    private String identity;

    /**
     * 对应的实例ID
     */
    private String instanceId;


    public Boolean isCleanSession() {
        return cleanSession;
    }

    /**
     * ClearSession
     */
    private Boolean cleanSession;

    /**
     * 订阅的Topic
     */
    private List<String> topics;

    /**
     * 消息队列，保证顺序
     */
    private Queue<BaseMessage> messages;

    /**
     * 添加Topic
     *
     * @param topic 主题
     */
    public void addTopic(String topic) {
        topics.add(topic);
    }

    /**
     * 移除Topic
     *
     * @param topic 主题
     */
    public void removeTopic(String topic) {
        topics.remove(topic);
    }

    /**
     * 添加消息
     * @param message {@link BaseMessage}
     */
    public void add(BaseMessage message) {
        messages.add(message);
    }

    public void poll() {
        messages.poll();
    }
}
