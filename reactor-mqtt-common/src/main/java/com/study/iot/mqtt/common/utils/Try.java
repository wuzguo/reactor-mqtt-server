package com.study.iot.mqtt.common.utils;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

/**
 * <B>说明：Lambda 受检异常处理</B>
 *
 * @author L.cm
 * @version 1.0.0
 * @date 2021/5/20 8:47
 */


@UtilityClass
public class Try {

    public static <T, R> Function<T, R> of(UncheckedFunction<T, R> mapper) {
        Objects.requireNonNull(mapper);
        return t -> {
            try {
                return mapper.apply(t);
            } catch (Throwable e) {
                throw Exceptions.unchecked(e);
            }
        };
    }

    public static <T> Consumer<T> of(UncheckedConsumer<T> mapper) {
        Objects.requireNonNull(mapper);
        return t -> {
            try {
                mapper.accept(t);
            } catch (Throwable e) {
                throw Exceptions.unchecked(e);
            }
        };
    }

    public static <T> Supplier<T> of(UncheckedSupplier<T> mapper) {
        Objects.requireNonNull(mapper);
        return () -> {
            try {
                return mapper.get();
            } catch (Throwable e) {
                throw Exceptions.unchecked(e);
            }
        };
    }

    public static Runnable of(UncheckedRunnable runnable) {
        Objects.requireNonNull(runnable);
        return () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                throw Exceptions.unchecked(e);
            }
        };
    }

    public static <T> Callable<T> of(UncheckedCallable<T> callable) {
        Objects.requireNonNull(callable);
        return () -> {
            try {
                return callable.call();
            } catch (Throwable e) {
                throw Exceptions.unchecked(e);
            }
        };
    }

    public static <T> Comparator<T> of(UncheckedComparator<T> comparator) {
        Objects.requireNonNull(comparator);
        return (T o1, T o2) -> {
            try {
                return comparator.compare(o1, o2);
            } catch (Throwable e) {
                throw Exceptions.unchecked(e);
            }
        };
    }


    @FunctionalInterface
    public interface UncheckedFunction<T, R> {
        /**
         * Run the Consumer
         *
         * @param t T
         * @return R R
         * @throws Throwable UncheckedException
         */
        @Nullable
        R apply(@Nullable T t) throws Throwable;
    }

    @FunctionalInterface
    public interface UncheckedConsumer<T> {
        /**
         * Run the Consumer
         *
         * @param t T
         * @throws Throwable UncheckedException
         */
        @Nullable
        void accept(@Nullable T t) throws Throwable;
    }

    @FunctionalInterface
    public interface UncheckedSupplier<T> {
        /**
         * Run the Supplier
         *
         * @return T
         * @throws Throwable UncheckedException
         */
        @Nullable
        T get() throws Throwable;
    }

    @FunctionalInterface
    public interface UncheckedRunnable {
        /**
         * Run this runnable.
         *
         * @throws Throwable UncheckedException
         */
        void run() throws Throwable;
    }

    @FunctionalInterface
    public interface UncheckedCallable<T> {
        /**
         * Run this callable.
         *
         * @return result
         * @throws Throwable UncheckedException
         */
        @Nullable
        T call() throws Throwable;
    }

    @FunctionalInterface
    public interface UncheckedComparator<T> {
        /**
         * Compares its two arguments for order.
         *
         * @param o1 o1
         * @param o2 o2
         * @return int
         * @throws Throwable UncheckedException
         */
        int compare(T o1, T o2) throws Throwable;
    }
}
