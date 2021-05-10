package com.study.iot.mqtt.cache.strategy;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.common.enums.CacheStrategy;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
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

@AllArgsConstructor
public class CacheStrategyContainer implements ApplicationContextAware {

    private static final Map<String, Map<CacheStrategy, Class<? extends CacheCapable>>> container = Maps
        .newConcurrentMap();

    private final ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initializingContainer(applicationContext);
    }

    private void initializingContainer(ApplicationContext applicationContext) {
        Optional.of(applicationContext.getBeansWithAnnotation(CacheStrategyService.class))
            .ifPresent(annotationBeans -> annotationBeans.forEach((k, v) -> {
                if (!CacheCapable.class.isAssignableFrom(v.getClass())) {
                    throw new BeanDefinitionValidationException(String
                        .format("%s must implemented interface CacheCapable.", v.getClass()));
                }

                Class<? extends CacheCapable> strategyClass = (Class<? extends CacheCapable>) v.getClass();
                CacheStrategyService cacheStrategyService = strategyClass.getAnnotation(CacheStrategyService.class);

                String group = cacheStrategyService.group();
                Map<CacheStrategy, Class<? extends CacheCapable>> storage = container.get(group);
                if (storage == null) {
                    storage = Maps.newConcurrentMap();
                }

                CacheStrategy value = cacheStrategyService.type();
                storage.putIfAbsent(value, strategyClass);
                container.put(group, storage);
            }));
    }

    /**
     * 获取策略类型，抛出异常
     *
     * @param group {@link Group}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link CacheCapable} 结果
     */
    public <T extends CacheCapable> T getStrategy(String group, CacheStrategy value) {
        Map<CacheStrategy, Class<? extends CacheCapable>> storage = container.get(group);
        if (storage == null) {
            throw new BeanDefinitionValidationException(String
                .format("StrategyService group '%s' not found in value container", group));

        }

        Class<? extends CacheCapable> strategy = storage.get(value);
        if (strategy == null) {
            throw new BeanDefinitionValidationException(String
                .format("StrategyService value '%s' not found in value group '%s'", value, group));

        }
        return (T) applicationContext.getBean(strategy);
    }


    /**
     * 获取策略类型，不抛出异常
     *
     * @param group {@link Group}
     * @param value value Id
     * @param <T>   泛型
     * @return {@link CacheCapable} 结果
     */
    public <T extends CacheCapable> T findStrategy(String group, CacheStrategy value) {
        Map<CacheStrategy, Class<? extends CacheCapable>> storage = container.get(group);
        if (storage == null) {
            return null;
        }

        Class<? extends CacheCapable> strategy = storage.get(value);
        if (strategy == null) {
            return null;
        }

        return (T) applicationContext.getBean(strategy);
    }
}
