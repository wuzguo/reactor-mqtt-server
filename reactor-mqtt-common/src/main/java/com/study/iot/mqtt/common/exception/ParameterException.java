package com.study.iot.mqtt.common.exception;


import com.study.iot.mqtt.common.utils.StringUtil;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:29
 */


public class ParameterException extends RuntimeException {

    private static final long serialVersionUID = 8174189235584876349L;

    public ParameterException() {
        super();
    }

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterException(Throwable cause) {
        super(cause);
    }

    public ParameterException(Throwable cause, String message, Object... args) {
        super(StringUtil.format(message, args), cause);
    }
}
