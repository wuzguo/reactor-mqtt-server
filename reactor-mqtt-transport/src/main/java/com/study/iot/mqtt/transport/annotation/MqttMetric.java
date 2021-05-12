package com.study.iot.mqtt.transport.annotation;

import com.study.iot.mqtt.transport.metric.MetricType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 9:45
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMetric {

    @AliasFor("matter")
    String value() default "";

    @AliasFor("value")
    String matter() default "";

    /**
     * 操作类型
     *
     * @return {@link MetricType}
     */
    MetricType type() default MetricType.INCREASE;
}
