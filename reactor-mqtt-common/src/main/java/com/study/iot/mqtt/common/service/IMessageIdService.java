package com.study.iot.mqtt.common.service;

/**
 * 分布式生成报文标识符
 */
public interface IMessageIdService {

    /**
     * 获取报文标识符
     *
     * @return {@link Integer}
     */
    Integer next();

    /**
     * 释放报文标识符
     *
     * @param id {@link Integer} 消息ID
     */
    void release(Integer id);
}
