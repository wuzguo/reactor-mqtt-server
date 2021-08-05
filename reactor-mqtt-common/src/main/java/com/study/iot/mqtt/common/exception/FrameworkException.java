package com.study.iot.mqtt.common.exception;


import com.study.iot.mqtt.common.utils.StringUtils;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:29
 */

public class FrameworkException extends RuntimeException {

    private static final long serialVersionUID = -4105635849108257742L;

    public FrameworkException() {
        super();
    }

    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkException(Throwable cause) {
        super(cause);
    }

    public FrameworkException(Throwable cause, String message, Object... args) {
        super(StringUtils.format(message, args), cause);
    }
}
