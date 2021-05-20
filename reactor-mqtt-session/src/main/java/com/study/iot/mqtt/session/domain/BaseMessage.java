package com.study.iot.mqtt.session.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 11:52
 */

@Data
public class BaseMessage implements Serializable {

    /**
     * 消息ID
     */
    private Long id;
}
