package com.study.iot.mqtt.common.utils;

import com.study.iot.mqtt.common.io.FastStringWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import lombok.experimental.UtilityClass;

/**
 * <B>说明：异常处理工具类</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/18 11:02
 */

@UtilityClass
public class Exceptions {

    /**
     * 将CheckedException转换为UncheckedException.
     *
     * @param e Throwable
     * @return {RuntimeException}
     */
    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof Error) {
            throw (Error) e;
        } else if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
            || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return new IllegalStateException(e);
    }

    /**
     * 代理异常解包
     *
     * @param wrapped 包装过得异常
     * @return 解包后的异常
     */
    public static Throwable unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    /**
     * 将ErrorStack转化为String.
     *
     * @param e Throwable
     * @return {String}
     */
    public static String getStackTraceAsString(Throwable e) {
        FastStringWriter stringWriter = new FastStringWriter(1024);
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
