package com.study.iot.mqtt.transport.annotation;

import com.study.iot.mqtt.transport.metric.MetricType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttMetric {

    @AliasFor("value")
    String name() default "";

    /**
     * 操作类型
     *
     * @return {@link MetricType}
     */
    MetricType type() default MetricType.INCREASE;
}
