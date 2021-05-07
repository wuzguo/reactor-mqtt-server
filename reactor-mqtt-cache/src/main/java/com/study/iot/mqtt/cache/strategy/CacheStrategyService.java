package com.study.iot.mqtt.cache.strategy;

import com.study.iot.mqtt.common.enums.CacheStrategy;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:14
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Service
public @interface CacheStrategyService {

    @AliasFor(value = "value", annotation = Service.class)
    String value() default "";

    /**
     * 策略
     *
     * @return {@link CacheStrategy}
     */
    CacheStrategy type();

    /**
     * 策略组
     *
     * @return {@link String}
     */
    String group();
}
