package com.study.iot.mqtt.store.container.path;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 16:18
 */

@Slf4j
public class TopicMap<K, V> {

    private final Map<K, Node<K, V>> mapNodes = Maps.newConcurrentMap();

    public boolean put(K[] topic, V v) {
        if (topic.length == 1) {
            Node<K, V> kvNode = build(topic[0], v);
            if (kvNode != null && kvNode.topic.equals(topic[0])) {
                return true;
            }
        } else {
            Node<K, V> kvNode = build(topic[0], null);
            for (int i = 1; i < topic.length; i++) {
                if (i == topic.length - 1) {
                    kvNode = kvNode.put(topic[i], v);
                } else {
                    kvNode = kvNode.put(topic[i], null);
                }
            }
        }
        return true;
    }

    public boolean delete(K[] ks, V v) {
        if (ks.length == 1) {
            return mapNodes.get(ks[0]).remove(v);
        } else {
            Node<K, V> kvNode = mapNodes.get(ks[0]);
            for (int i = 1; i < ks.length && kvNode != null; i++) {
                kvNode = kvNode.get(ks[i]);
            }
            return kvNode.remove(v);

        }
    }

    public List<V> getData(K[] ks) {
        if (ks.length == 1) {
            return mapNodes.get(ks[0]).get();
        } else {
            Node<K, V> node = mapNodes.get(ks[0]);
            if (node != null) {
                List<V> all = Lists.newArrayList();
                all.addAll(node.get());
                for (int i = 1; i < ks.length; i++) {
                    node = node.get(ks[i]);
                    if (node == null) {
                        break;
                    }
                    all.addAll(node.get());
                }
                return all;
            }
            return null;
        }
    }

    public Node<K, V> build(K k, V v) {
        Node<K, V> node = this.mapNodes.computeIfAbsent(k, key -> {
            Node<K, V> kNode = new Node<>(k);
            return kNode;
        });

        if (v != null) {
            node.put(v);
        }
        return node;
    }

    class Node<K, V> {

        private final K topic;

        private final Map<K, Node<K, V>> mapNodes = Maps.newConcurrentMap();

        private final List<V> vs = Lists.newCopyOnWriteArrayList();

        Node(K topic) {
            this.topic = topic;
        }

        public K getTopic() {
            return topic;
        }

        public boolean remove(V v) {
            return vs.remove(v);
        }

        public Node<K, V> put(K k, V v) {
            Node<K, V> kvNode = mapNodes.computeIfAbsent(k, key -> new Node<>(k));
            if (v != null) {
                kvNode.put(v);
            }
            return kvNode;
        }

        public Node<K, V> get(K k) {
            return mapNodes.get(k);
        }

        public boolean put(V v) {
            return vs.add(v);
        }

        public List<V> get() {
            return vs;
        }
    }
}
