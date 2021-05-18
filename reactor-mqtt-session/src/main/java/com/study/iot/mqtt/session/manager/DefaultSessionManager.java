package com.study.iot.mqtt.session.manager;

import com.study.iot.mqtt.session.domain.ConnectSession;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/18 13:46
 */

@Component
public class DefaultSessionManager implements SessionManager {

    @Override
    public ConnectSession create(String instanceId, String clientIdentity, Boolean isCleanSession) {
        return null;
    }
}
