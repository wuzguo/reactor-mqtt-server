package com.study.iot.mqtt.common.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/20 11:47
 */

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class WillMessage extends BaseMessage {

    /**
     * 表名
     */
    public static final String TABLE_NAME = "reactor-will-message";

    /**
     * 列族
     */
    public static final String COLUMN_FAMILY = "columns";

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
     * 消息质量
     */
    private Integer qos;

    /**
     * 消息
     */
    private byte[] copyByteBuf;
}
