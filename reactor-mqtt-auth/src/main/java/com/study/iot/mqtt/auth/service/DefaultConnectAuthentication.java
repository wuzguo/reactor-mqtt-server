package com.study.iot.mqtt.auth.service;

import com.study.iot.mqtt.common.utils.DigestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import reactor.core.publisher.Mono;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 8:23
 */

@Slf4j
public class DefaultConnectAuthentication implements ConnectAuthentication {

    @Override
    public Mono<Boolean> authenticate(String key, String secret) {
        log.info("mqtt server auth, key: {}, secret: {}", key, secret);
        String md5Key = DigestUtils.md5Hex(key);
        return Mono.just(StringUtils.equals(md5Key, secret));
    }
}
