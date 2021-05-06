package com.study.iot.mqtt.cache.subscribe;

import com.study.iot.mqtt.common.service.ISubscribeStoreService;
import com.study.iot.mqtt.common.subscribe.SubscribeStore;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅存储服务
 */
@Service
public class SubscribeStoreService implements ISubscribeStoreService {

    @Resource
    private IgniteCache<String, ConcurrentHashMap<String, SubscribeStore>> subscribeNotWildcardCache;

    @Resource
    private IgniteCache<String, ConcurrentHashMap<String, SubscribeStore>> subscribeWildcardCache;

    @Override
    public void put(String topicFilter, SubscribeStore subscribeStore) {
        if (topicFilter.contains("#") || topicFilter.contains("+")) {
            ConcurrentHashMap<String, SubscribeStore> map =
                    subscribeWildcardCache.containsKey(topicFilter) ? subscribeWildcardCache.get(topicFilter) : new ConcurrentHashMap<String, SubscribeStore>();
            map.put(subscribeStore.getClientId(), subscribeStore);
            subscribeWildcardCache.put(topicFilter, map);
        } else {
            ConcurrentHashMap<String, SubscribeStore> map =
                    subscribeNotWildcardCache.containsKey(topicFilter) ? subscribeNotWildcardCache.get(topicFilter) : new ConcurrentHashMap<String, SubscribeStore>();
            map.put(subscribeStore.getClientId(), subscribeStore);
            subscribeNotWildcardCache.put(topicFilter, map);
        }
    }

    @Override
    public void remove(String topicFilter, String clientId) {
        if (topicFilter.contains("#") || topicFilter.contains("+")) {
            if (subscribeWildcardCache.containsKey(topicFilter)) {
                ConcurrentHashMap<String, SubscribeStore> map = subscribeWildcardCache.get(topicFilter);
                if (map.containsKey(clientId)) {
                    map.remove(clientId);
                    if (map.size() > 0) {
                        subscribeWildcardCache.put(topicFilter, map);
                    } else {
                        subscribeWildcardCache.remove(topicFilter);
                    }
                }
            }
        } else {
            if (subscribeNotWildcardCache.containsKey(topicFilter)) {
                ConcurrentHashMap<String, SubscribeStore> map = subscribeNotWildcardCache.get(topicFilter);
                if (map.containsKey(clientId)) {
                    map.remove(clientId);
                    if (map.size() > 0) {
                        subscribeNotWildcardCache.put(topicFilter, map);
                    } else {
                        subscribeNotWildcardCache.remove(topicFilter);
                    }
                }
            }
        }
    }

    @Override
    public void removeForClient(String clientId) {
        subscribeNotWildcardCache.forEach(entry -> {
            ConcurrentHashMap<String, SubscribeStore> map = entry.getValue();
            if (map.containsKey(clientId)) {
                map.remove(clientId);
                if (map.size() > 0) {
                    subscribeNotWildcardCache.put(entry.getKey(), map);
                } else {
                    subscribeNotWildcardCache.remove(entry.getKey());
                }
            }
        });
        subscribeWildcardCache.forEach(entry -> {
            ConcurrentHashMap<String, SubscribeStore> map = entry.getValue();
            if (map.containsKey(clientId)) {
                map.remove(clientId);
                if (map.size() > 0) {
                    subscribeWildcardCache.put(entry.getKey(), map);
                } else {
                    subscribeWildcardCache.remove(entry.getKey());
                }
            }
        });
    }

    @Override
    public List<SubscribeStore> search(String topic) {
        List<SubscribeStore> subscribeStores = new ArrayList<SubscribeStore>();
        if (subscribeNotWildcardCache.containsKey(topic)) {
            ConcurrentHashMap<String, SubscribeStore> map = subscribeNotWildcardCache.get(topic);
            Collection<SubscribeStore> collection = map.values();
            List<SubscribeStore> list = new ArrayList<SubscribeStore>(collection);
            subscribeStores.addAll(list);
        }
        subscribeWildcardCache.forEach(entry -> {
            String topicFilter = entry.getKey();
            if (topic.split("/").length >= topicFilter.split("/").length) {
                List<String> splitTopics = Arrays.asList(topic.split("/"));
                List<String> spliteTopicFilters = Arrays.asList(topicFilter.split("/"));
                String newTopicFilter = "";
                for (int i = 0; i < spliteTopicFilters.size(); i++) {
                    String value = spliteTopicFilters.get(i);
                    if (value.equals("+")) {
                        newTopicFilter = newTopicFilter + "+/";
                    } else if (value.equals("#")) {
                        newTopicFilter = newTopicFilter + "#/";
                        break;
                    } else {
                        newTopicFilter = newTopicFilter + splitTopics.get(i) + "/";
                    }
                }
                // 去掉指定后缀
                newTopicFilter = newTopicFilter.substring(0, newTopicFilter.lastIndexOf("/"));
                if (topicFilter.equals(newTopicFilter)) {
                    ConcurrentHashMap<String, SubscribeStore> map = entry.getValue();
                    Collection<SubscribeStore> collection = map.values();
                    List<SubscribeStore> list = new ArrayList<SubscribeStore>(collection);
                    subscribeStores.addAll(list);
                }
            }
        });
        return subscribeStores;
    }

}
