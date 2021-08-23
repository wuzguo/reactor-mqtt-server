package com.study.iot.mqtt.common.message;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 11:52
 */

@Data
@SuperBuilder
public class BaseMessage implements Serializable {
    /**
     * 消息ID
     */
    private String row;
}
