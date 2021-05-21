package com.study.iot.mqtt.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/20 8:56
 */

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class SessionMessage extends BaseMessage {

    /**
     * 客户端标识
     */
    private String identity;

    /**
     * SessionId
     */
    private String sessionId;

    /**
     * 消息ID
     */
    private Integer messageId;

    /**
     * TOPIC
     */
    private String topic;

    /**
     * 保持
     */
    private Boolean retain;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 消息质量
     */
    private Integer qos;

    /**
     * 是否重发
     */
    private Boolean dup;

    /**
     * 消息
     */
    private byte[] copyByteBuf;
}
