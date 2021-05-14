package com.study.iot.mqtt.common.utils;

import com.study.iot.mqtt.common.worker.IdWorker;
import lombok.experimental.UtilityClass;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/14 15:48
 */

@UtilityClass
public class IdUtil {

    /**
     * 生成消息ID
     *
     * @return {@link Long} 消息ID
     */
    public static Long messageId() {
        return IdWorker.getId();
    }
}
