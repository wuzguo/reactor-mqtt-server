package com.study.iot.mqtt.api.domain.vo;

import lombok.Data;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 10:33
 */

@Data
public class ReqVo<T> {

    /**
     * 值
     */
    private T value;
}
