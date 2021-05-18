package com.study.iot.mqtt.session.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Queue;
import lombok.Data;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 11:50
 */

@Data
public class ConnectSession implements Serializable {

    /**
     * 客户端标识
     */
    private String clientIdentity;

    /**
     * 对应的实例ID
     */
    private String instanceId;

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
}
