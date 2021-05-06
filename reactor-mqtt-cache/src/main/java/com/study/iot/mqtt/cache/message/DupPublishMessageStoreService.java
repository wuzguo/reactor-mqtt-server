/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.study.iot.mqtt.cache.message;

import cn.recallcode.iot.mqtt.server.common.message.DupPublishMessageStore;
import cn.recallcode.iot.mqtt.server.common.message.IDupPublishMessageStoreService;
import cn.recallcode.iot.mqtt.server.common.message.IMessageIdService;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DupPublishMessageStoreService implements IDupPublishMessageStoreService {

	@Autowired
	private IMessageIdService messageIdService;

	@Resource
	private IgniteCache<String, ConcurrentHashMap<Integer, DupPublishMessageStore>> dupPublishMessageCache;

	@Override
	public void put(String clientId, DupPublishMessageStore dupPublishMessageStore) {
		ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.containsKey(clientId) ? dupPublishMessageCache.get(clientId) : new ConcurrentHashMap<Integer, DupPublishMessageStore>();
		map.put(dupPublishMessageStore.getMessageId(), dupPublishMessageStore);
		dupPublishMessageCache.put(clientId, map);
	}

	@Override
	public List<DupPublishMessageStore> get(String clientId) {
		if (dupPublishMessageCache.containsKey(clientId)) {
			ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
			Collection<DupPublishMessageStore> collection = map.values();
			return new ArrayList<DupPublishMessageStore>(collection);
		}
		return new ArrayList<DupPublishMessageStore>();
	}

	@Override
	public void remove(String clientId, int messageId) {
		if (dupPublishMessageCache.containsKey(clientId)) {
			ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
			if (map.containsKey(messageId)) {
				map.remove(messageId);
				if (map.size() > 0) {
					dupPublishMessageCache.put(clientId, map);
				} else {
					dupPublishMessageCache.remove(clientId);
				}
			}
		}
	}

	@Override
	public void removeByClient(String clientId) {
		if (dupPublishMessageCache.containsKey(clientId)) {
			ConcurrentHashMap<Integer, DupPublishMessageStore> map = dupPublishMessageCache.get(clientId);
			map.forEach((messageId, dupPublishMessageStore) -> {
				messageIdService.releaseMessageId(messageId);
			});
			map.clear();
			dupPublishMessageCache.remove(clientId);
		}
	}
}
