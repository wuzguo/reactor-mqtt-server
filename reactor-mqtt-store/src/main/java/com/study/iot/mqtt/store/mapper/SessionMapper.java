package com.study.iot.mqtt.store.mapper;

import com.study.iot.mqtt.store.strategy.CacheCapable;
import java.io.Serializable;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/19 19:02
 */

public interface SessionMapper extends CacheCapable {

    /**
     * 添加连接
     *
     * @param identity 设备标识
     * @param value    对象
     */
    void add(String identity, Serializable value);

    /**
     * 移除通道
     *
     * @param identity 设备标识
     */
    void remove(String identity);

    /**
     * 获取对象
     *
     * @param identity 设备标识
     * @return {@link Serializable}
     */
    <T extends Serializable> T get(String identity);

}
