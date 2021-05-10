package com.study.iot.mqtt.cache.message;

import java.util.concurrent.locks.Lock;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageIdService implements IMessageIdService {

    private final int MIN_MSG_ID = 1;

    private final int MAX_MSG_ID = 65535;

    private final int lock = 0;

    @Resource
    private IgniteCache<Integer, Integer> messageIdCache;

    private int nextMsgId = MIN_MSG_ID - 1;

    @Override
    public Integer next() {
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
            log.error("get message id exception: {}", e.getMessage());
        } finally {
            lock.unlock();
        }
        return nextMsgId;
    }

    @Override
    public void release(Integer messageId) {
        Lock lock = messageIdCache.lock(this.lock);
        lock.lock();
        try {
            messageIdCache.remove(messageId);
        } catch (Exception e) {
            log.error("release message id exception: {}", e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
