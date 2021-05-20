package com.study.iot.mqtt.transport.metric;

import com.study.iot.mqtt.common.exception.FrameworkException;
import com.study.iot.mqtt.store.constant.CacheGroup;
import com.study.iot.mqtt.store.container.ContainerManager;
import com.study.iot.mqtt.store.container.MetricContainer;
import com.study.iot.mqtt.transport.annotation.MqttMetric;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/12 8:59
 */

@Slf4j
@Aspect
@Component
public class MqttMetricAspect {

    @Autowired
    private ContainerManager containerManager;

    @Around("@annotation(metric)")
    public Object mqttMetric(ProceedingJoinPoint joinPoint, MqttMetric metric) {
        return doMetric(joinPoint, metric);
    }

    /**
     * 业务逻辑
     *
     * @param joinPoint {@link MqttMetric}
     * @param metric    {@link ProceedingJoinPoint}
     * @return {@link Object}
     */
    private Object doMetric(ProceedingJoinPoint joinPoint, MqttMetric metric) {
        log.info("mqtt metric {}", metric);
        this.proceed(joinPoint);
        MetricContainer metricContainer = (MetricContainer) containerManager.get(CacheGroup.METRIC);
        // 统计数据
        if (metric.type().equals(MetricType.INCREASE)) {
            return metricContainer.increase(metric.matter());
        }
        return metricContainer.decrease(metric.matter());
    }

    /**
     * 执行业务逻辑
     *
     * @param joinPoint {@link ProceedingJoinPoint}
     */
    private void proceed(ProceedingJoinPoint joinPoint) {
        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            log.error("mqtt metric aop proceed: {}", e.getMessage());
            throw new FrameworkException(e);
        }
    }
}
