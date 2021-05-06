package com.study.iot.mqtt.common.service;

/**
 * 分布式生成报文标识符
 */
public interface IMessageIdService {

	/**
	 * 获取报文标识符
	 */
	int getNextMessageId();

	/**
	 * 释放报文标识符
	 */
	void releaseMessageId(int messageId);
}
