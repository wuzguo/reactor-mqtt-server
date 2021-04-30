package com.study.iot.mqtt.transport.strategy;

import com.google.common.collect.Maps;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.Objects;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:15
 */

@AllArgsConstructor
public class StrategyContainer implements ApplicationContextAware {

    public static final Map<MqttMessageType, Class<? extends StrategyCapable>> strategyContainer = Maps
            .newConcurrentMap();
    private final ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.initializingContainer(applicationContext);
    }


    private void initializingContainer(ApplicationContext applicationContext) {
        Map<String, Object> annotationBeans = applicationContext.getBeansWithAnnotation(StrategyComponent.class);
        strategyContainer.clear();
        annotationBeans.forEach((k, v) -> {
            if (!StrategyCapable.class.isAssignableFrom(v.getClass())) {
                throw new BeanDefinitionValidationException(
                        String.format("%s must implemented interface StrategyCapable.", v.getClass()));
            }

            Class<? extends StrategyCapable> strategyClass = (Class<? extends StrategyCapable>) v.getClass();
            StrategyComponent component = strategyClass.getAnnotation(StrategyComponent.class);

            MqttMessageType value = component.strategyValue();
            strategyContainer.putIfAbsent(value, strategyClass);
        });
    }

    /**
     * 获取策略类型，抛出异常
     *
     * @param value value Id
     * @param <T>   泛型
     * @return {@link StrategyCapable} 结果
     */
    public <T extends StrategyCapable> T getStrategy(MqttMessageType value) {
        Class<? extends StrategyCapable> strategy = strategyContainer.get(value);
        if (Objects.isNull(strategy)) {
            throw new BeanDefinitionValidationException(
                    String.format("StrategyService group '%s' not found in value container", value));
        }
        return (T) applicationContext.getBean(strategy);
    }


    /**
     * 获取策略类型，不抛出异常
     *
     * @param value value Id
     * @param <T>   泛型
     * @return {@link StrategyCapable} 结果
     */
    public <T extends StrategyCapable> T findStrategy(MqttMessageType value) {
        Class<? extends StrategyCapable> strategy = strategyContainer.get(value);
        if (Objects.isNull(strategy)) {
            return null;
        }
        return (T) applicationContext.getBean(strategy);
    }
}