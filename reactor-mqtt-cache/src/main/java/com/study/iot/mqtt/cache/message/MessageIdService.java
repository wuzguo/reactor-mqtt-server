/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.study.iot.mqtt.cache.message;

import cn.recallcode.iot.mqtt.server.common.message.IMessageIdService;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;

@Service
public class MessageIdService implements IMessageIdService {

	private final int MIN_MSG_ID = 1;

	private final int MAX_MSG_ID = 65535;

	private final int lock = 0;

	@Resource
	private IgniteCache<Integer, Integer> messageIdCache;

	private int nextMsgId = MIN_MSG_ID - 1;

	@Override
	public int getNextMessageId() {
		Lock lock = messageIdCache.lock(this.lock);
		lock.lock();
		try {
			do {
				nextMsgId++;
				if (nextMsgId > MAX_MSG_ID) {
					nextMsgId = MIN_MSG_ID;
				}
			} while (messageIdCache.containsKey(nextMsgId));
			messageIdCache.put(nextMsgId, nextMsgId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return nextMsgId;
	}

	@Override
	public void releaseMessageId(int messageId) {
		Lock lock = messageIdCache.lock(this.lock);
		lock.lock();
		try {
			messageIdCache.remove(messageId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
