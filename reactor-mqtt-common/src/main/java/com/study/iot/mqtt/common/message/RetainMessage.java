package com.study.iot.mqtt.common.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 8:46
 */

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RetainMessage extends BaseMessage {

    /**
     * 表名
     */
    public static final String TABLE_NAME = "reactor-retain-message";

    /**
     * 列族
     */
    public static final String COLUMN_FAMILY = "columns";

    /**
     * 是否重复分发标志
     */
    private Boolean dup;

    /**
     * 保留标准
     */
    private Boolean retain;

    /**
     * 消息质量
     */
    private Integer qos;


    /**
     * 主题
     */
    private String topic;

    /**
     * 消息
     */
    private byte[] copyByteBuf;
}
