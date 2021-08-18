package com.study.iot.mqtt.common.beans;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * <B>说明：copy key</B>
 *
 * @author L.cm
 * @version 1.0.0
 * @date 2021/5/20 8:47
 */

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class BeanCopierKey {

    private final Class<?> source;

    private final Class<?> target;

    private final boolean useConverter;

    private final boolean nonNull;
}
