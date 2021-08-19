package com.study.iot.mqtt.store.strategy;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.common.enums.CacheEnum;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/4/22 9:13
 */

@Slf4j
@AllArgsConstructor
public class CacheStrategyContainer implements ApplicationContextAware {

    private static final Map<String, Map<CacheEnum, Class<? extends CacheCapable>>> CONTAINER = Maps.newConcurrentMap();

    private final ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializingContainer(applicationContext);
    }

    private void initializingContainer(ApplicationContext applicationContext) {
        Optional.of(applicationContext.getBeansWithAnnotation(CacheStrategyService.class))
            .ifPresent(annotationBeans -> annotationBeans.forEach((beanName, instance) -> {
                if (!CacheCapable.class.isAssignableFrom(instance.getClass())) {
                    log.error("{} must implemented interface StrategyCapable.", instance.getClass());
                    throw new BeanDefinitionValidationException(String
                        .format("%s must implemented interface CacheCapable.", instance.getClass()));
                }

                Class<? extends CacheCapable> strategyClass = (Class<? extends CacheCapable>) instance.getClass();
                CacheStrategyService cacheStrategyService = strategyClass.getAnnotation(CacheStrategyService.class);

                String group = cacheStrategyService.group();
                Map<CacheEnum, Class<? extends CacheCapable>> storage = CONTAINER.get(group);
                if (storage == null) {
                    storage = Maps.newConcurrentMap();
                }

                CacheEnum value = cacheStrategyService.type();
                storage.putIfAbsent(value, strategyClass);
                CONTAINER.put(group, storage);
            }));
    }

    /**
     * 获取策略类型，抛出异常
     *
     * @param group {@link String}
     * @param value {@link CacheEnum}
     * @param <T>   泛型
     * @return {@link CacheCapable} 结果
     */
    public <T extends CacheCapable> T get(String group, CacheEnum value) {
        Map<CacheEnum, Class<? extends CacheCapable>> storage = CONTAINER.get(group);
        if (storage == null) {
            log.error("group '{}' not found in value container", group);
            throw new BeanDefinitionValidationException(String
                .format("StrategyService group '%s' not found in value container", group));

        }

        Class<? extends CacheCapable> strategy = storage.get(value);
        if (strategy == null) {
            log.error("value '{}' not found in value group '{}'", value, group);
            throw new BeanDefinitionValidationException(String
                .format("StrategyService value '%s' not found in value group '%s'", value, group));

        }
        return (T) applicationContext.getBean(strategy);
    }


    /**
     * 获取策略类型，不抛出异常
     *
     * @param group {@link String}
     * @param value {@link CacheEnum}
     * @param <T>   泛型
     * @return {@link CacheCapable} 结果
     */
    public <T extends CacheCapable> T find(String group, CacheEnum value) {
        Map<CacheEnum, Class<? extends CacheCapable>> storage = CONTAINER.get(group);
        if (storage == null) {
            log.error("group '{}' not found in value container", group);
            return null;
        }

        Class<? extends CacheCapable> strategy = storage.get(value);
        if (strategy == null) {
            log.error("value '{}' not found in value group '{}'", value, group);
            return null;
        }

        return (T) applicationContext.getBean(strategy);
    }
}
