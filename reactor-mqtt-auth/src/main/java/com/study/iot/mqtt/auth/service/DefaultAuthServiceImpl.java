package com.study.iot.mqtt.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/30 8:23
 */

@Slf4j
public class DefaultAuthServiceImpl implements IAuthService {

    @Override
    public Mono<Boolean> check(String key, String secret) {
        log.info("mqtt server auth, key: {}, secret: {}", key, secret);
        String md5Key = Md5Crypt.md5Crypt(key.getBytes(StandardCharsets.UTF_8), secret);
        return Mono.just(StringUtils.equals(md5Key, secret));
    }
}
