package com.study.iot.mqtt.auth.service;

import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 8:17
 */

public interface ConnectAuthentication {

    /**
     * 认证验证
     *
     * @param key    Key,用户名
     * @param secret 密码
     * @return {@link Mono<Boolean>}
     */
    Mono<Boolean> authenticate(String key, String secret);
}
