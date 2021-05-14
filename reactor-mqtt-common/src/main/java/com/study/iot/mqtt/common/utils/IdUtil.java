package com.study.iot.mqtt.common.utils;

import com.study.iot.mqtt.common.worker.IdWorker;
import java.util.concurrent.atomic.LongAdder;
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

    private final LongAdder longAdder = new LongAdder();

    /**
     * 生成消息 ID
     *
     * @return {@link Integer}
     */
    public int messageId() {
        longAdder.increment();
        int value = longAdder.intValue();
        if (value == Integer.MAX_VALUE) {
            longAdder.reset();
            longAdder.increment();
            return longAdder.intValue();
        }
        return value;
    }

    /**
     * 生成消息ID
     *
     * @return {@link Long} 消息ID
     */
    public static Long idGen() {
        return IdWorker.getId();
    }
}
