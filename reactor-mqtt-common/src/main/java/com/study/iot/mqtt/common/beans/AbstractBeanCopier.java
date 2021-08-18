package com.study.iot.mqtt.common.beans;

import com.google.common.collect.Maps;
import com.study.iot.mqtt.common.utils.BeanUtils;
import com.study.iot.mqtt.common.utils.ClassUtils;
import com.study.iot.mqtt.common.utils.ReflectionUtils;
import com.study.iot.mqtt.common.utils.StringUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.Converter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.lang.Nullable;

/**
 * <B>说明：cspring cglib 魔改</B>
 * 1. 支持链式 bean，支持 map 2. ClassLoader 跟 target 保持一致
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/20 8:47
 */

public abstract class AbstractBeanCopier {

    private static final Type CONVERTER = TypeUtils.parseType("org.springframework.cglib.core.Converter");

    private static final Type CLASS_UTIL = TypeUtils.parseType(ClassUtils.class.getName());

    private static final Type BEAN_COPIER = TypeUtils.parseType(AbstractBeanCopier.class.getName());

    private static final Type BEAN_MAP = TypeUtils.parseType(Map.class.getName());

    private static final Signature COPY = new Signature("copy", Type.VOID_TYPE,
        new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER});

    private static final Signature CONVERT = TypeUtils.parseSignature("Object convert(Object, Class, Object)");

    private static final Signature BEAN_MAP_GET = TypeUtils.parseSignature("Object get(Object)");

    private static final Signature IS_ASSIGNABLE_VALUE = TypeUtils.parseSignature("boolean isAssignableValue(Class, Object)");

    /**
     * The map to store {@link AbstractBeanCopier} of source type and class type for copy.
     */
    private static final ConcurrentMap<BeanCopierKey, AbstractBeanCopier> MAP_BEAN_COPIER = Maps.newConcurrentMap();

    public static AbstractBeanCopier create(Class<?> source, Class<?> target, boolean useConverter) {
        return AbstractBeanCopier.create(source, target, useConverter, false);
    }

    public static AbstractBeanCopier create(Class<?> source, Class<?> target, boolean useConverter, boolean nonNull) {
        BeanCopierKey copierKey = new BeanCopierKey(source, target, useConverter, nonNull);
        // 利用 ConcurrentMap 缓存 提高性能，接近 直接 get set
        return MAP_BEAN_COPIER.computeIfAbsent(copierKey, key -> {
            Generator<?> generator = new Generator<>();
            generator.setSource(key.getSource());
            generator.setTarget(key.getTarget());
            generator.setUseConverter(key.isUseConverter());
            generator.setNonNull(key.isNonNull());
            return generator.create(key);
        });
    }

    /**
     * copy 抽象
     *
     * @param from      Object
     * @param to        Object
     * @param converter 类型转换器
     */
    abstract public void copy(Object from, Object to, @Nullable Converter converter);

    public static class Generator<T> extends AbstractClassGenerator<T> {

        private static final Source CODE_SOURCE = new Source(AbstractBeanCopier.class.getName());

        private Class<?> source;

        private Class<?> target;

        private boolean useConverter;

        private boolean nonNull;

        Generator() {
            super(CODE_SOURCE);
        }

        public void setSource(Class<?> source) {
            if (!Modifier.isPublic(source.getModifiers())) {
                setNamePrefix(source.getName());
            }
            this.source = source;
        }

        public void setTarget(Class<?> target) {
            if (!Modifier.isPublic(target.getModifiers())) {
                setNamePrefix(target.getName());
            }
            this.target = target;
        }

        public void setUseConverter(boolean useConverter) {
            this.useConverter = useConverter;
        }

        public void setNonNull(boolean nonNull) {
            this.nonNull = nonNull;
        }

        @Override
        protected ClassLoader getDefaultClassLoader() {
            // L.cm 保证 和 返回使用同一个 ClassLoader
            return target.getClassLoader();
        }

        @Override
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(source);
        }

        @Override
        public AbstractBeanCopier create(Object key) {
            return (AbstractBeanCopier) super.create(key);
        }

        @Override
        public void generateClass(ClassVisitor visitor) {
            final Type sourceType = Type.getType(source);
            final Type targetType = Type.getType(target);
            ClassEmitter emitter = new ClassEmitter(visitor);
            emitter.begin_class(Constants.V1_2, Constants.ACC_PUBLIC, getClassName(), BEAN_COPIER, null,
                Constants.SOURCE_FILE);
            EmitUtils.null_constructor(emitter);
            CodeEmitter codeEmitter = emitter.begin_method(Constants.ACC_PUBLIC, COPY, null);

            // map 单独处理
            if (Map.class.isAssignableFrom(source)) {
                generateMapClass(emitter, codeEmitter, sourceType, targetType);
                return;
            }

            // 2018.12.27 by L.cm 支持链式 bean
            // 注意：此处需兼容链式bean 使用了 spring 的方法，比较耗时
            PropertyDescriptor[] getters = ReflectionUtils.getBeanGetters(source);
            PropertyDescriptor[] setters = ReflectionUtils.getBeanSetters(target);
            Map<String, PropertyDescriptor> names = Maps.newHashMap();
            for (PropertyDescriptor descriptor : getters) {
                names.put(descriptor.getName(), descriptor);
            }

            Local targetLocal = codeEmitter.make_local();
            Local sourceLocal = codeEmitter.make_local();
            codeEmitter.load_arg(1);
            codeEmitter.checkcast(targetType);
            codeEmitter.store_local(targetLocal);
            codeEmitter.load_arg(0);
            codeEmitter.checkcast(sourceType);
            codeEmitter.store_local(sourceLocal);

            for (PropertyDescriptor descriptor : setters) {
                String propName = descriptor.getName();
                CopyProperty targetIgnoreCopy = ReflectionUtils.getAnnotation(target, propName, CopyProperty.class);
                // set 上有忽略的 注解
                if (targetIgnoreCopy != null) {
                    if (targetIgnoreCopy.ignore()) {
                        continue;
                    }
                    // 注解上的别名，如果别名不为空，使用别名
                    String aliasTargetPropName = targetIgnoreCopy.value();
                    if (StringUtils.isNotBlank(aliasTargetPropName)) {
                        propName = aliasTargetPropName;
                    }
                }
                // 找到对应的 get
                PropertyDescriptor getter = names.get(propName);
                // 没有 get 跳出
                if (getter == null) {
                    continue;
                }

                MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
                Method writeMethod = descriptor.getWriteMethod();
                MethodInfo write = ReflectUtils.getMethodInfo(writeMethod);
                Type returnType = read.getSignature().getReturnType();
                Type setterType = write.getSignature().getArgumentTypes()[0];
                Class<?> getterPropertyType = getter.getPropertyType();
                Class<?> setterPropertyType = descriptor.getPropertyType();

                // L.cm 2019.01.12 优化逻辑，先判断类型，类型一致直接 set，不同再判断 是否 类型转换
                // nonNull Label
                Label label = codeEmitter.make_label();
                // 判断类型是否一致，包括 包装类型
                if (ClassUtils.isAssignable(setterPropertyType, getterPropertyType)) {
                    // 2018.12.27 by L.cm 支持链式 bean
                    codeEmitter.load_local(targetLocal);
                    codeEmitter.load_local(sourceLocal);
                    codeEmitter.invoke(read);
                    boolean getterIsPrimitive = getterPropertyType.isPrimitive();
                    boolean setterIsPrimitive = setterPropertyType.isPrimitive();

                    if (nonNull) {
                        // 需要落栈，强制装箱
                        codeEmitter.box(returnType);
                        Local var = codeEmitter.make_local();
                        codeEmitter.store_local(var);
                        codeEmitter.load_local(var);
                        // nonNull Label
                        codeEmitter.ifnull(label);
                        codeEmitter.load_local(targetLocal);
                        codeEmitter.load_local(var);
                        // 需要落栈，强制拆箱
                        codeEmitter.unbox_or_zero(setterType);
                    } else {
                        // 如果 get 为原始类型，需要装箱
                        if (getterIsPrimitive && !setterIsPrimitive) {
                            codeEmitter.box(returnType);
                        }
                        // 如果 set 为原始类型，需要拆箱
                        if (!getterIsPrimitive && setterIsPrimitive) {
                            codeEmitter.unbox_or_zero(setterType);
                        }
                    }

                    // 构造 set 方法
                    invokeWrite(codeEmitter, write, writeMethod, nonNull, label);
                } else if (useConverter) {
                    codeEmitter.load_local(targetLocal);
                    codeEmitter.load_arg(2);
                    codeEmitter.load_local(sourceLocal);
                    codeEmitter.invoke(read);
                    codeEmitter.box(returnType);

                    if (nonNull) {
                        Local var = codeEmitter.make_local();
                        codeEmitter.store_local(var);
                        codeEmitter.load_local(var);
                        codeEmitter.ifnull(label);
                        codeEmitter.load_local(targetLocal);
                        codeEmitter.load_arg(2);
                        codeEmitter.load_local(var);
                    }

                    EmitUtils.load_class(codeEmitter, setterType);
                    // 更改成了属性名，之前是 set 方法名
                    codeEmitter.push(propName);
                    codeEmitter.invoke_interface(CONVERTER, CONVERT);
                    codeEmitter.unbox_or_zero(setterType);

                    // 构造 set 方法
                    invokeWrite(codeEmitter, write, writeMethod, nonNull, label);
                }
            }
            codeEmitter.return_value();
            codeEmitter.end_method();
            emitter.end_class();
        }

        private static void invokeWrite(CodeEmitter codeEmitter, MethodInfo write, Method writeMethod, boolean nonNull,
            Label label) {
            // 返回值，判断 链式 bean
            Class<?> returnType = writeMethod.getReturnType();
            codeEmitter.invoke(write);
            // 链式 bean，有返回值需要 pop
            if (!returnType.equals(Void.TYPE)) {
                codeEmitter.pop();
            }
            if (nonNull) {
                codeEmitter.visitLabel(label);
            }
        }

        @Override
        protected Object firstInstance(Class type) {
            return BeanUtils.newInstance(type);
        }

        @Override
        protected Object nextInstance(Object instance) {
            return instance;
        }

        /**
         * 处理 map 的 copy
         *
         * @param classEmitter ClassEmitter
         * @param codeEmitter  CodeEmitter
         * @param sourceType   sourceType
         * @param targetType   targetType
         */
        public void generateMapClass(ClassEmitter classEmitter, CodeEmitter codeEmitter, Type sourceType,
            Type targetType) {
            // 2018.12.27 by L.cm 支持链式 bean
            PropertyDescriptor[] setters = ReflectionUtils.getBeanSetters(target);
            // 入口变量
            Local targetLocal = codeEmitter.make_local();
            Local sourceLocal = codeEmitter.make_local();
            codeEmitter.load_arg(1);
            codeEmitter.checkcast(targetType);
            codeEmitter.store_local(targetLocal);
            codeEmitter.load_arg(0);
            codeEmitter.checkcast(sourceType);
            codeEmitter.store_local(sourceLocal);
            Type mapBox = Type.getType(Object.class);

            for (PropertyDescriptor setter : setters) {
                String propName = setter.getName();
                // set 上有忽略的 注解
                CopyProperty targetIgnoreCopy = ReflectionUtils.getAnnotation(target, propName, CopyProperty.class);
                if (targetIgnoreCopy != null) {
                    if (targetIgnoreCopy.ignore()) {
                        continue;
                    }
                    // 注解上的别名
                    String aliasTargetPropName = targetIgnoreCopy.value();
                    if (StringUtils.isNotBlank(aliasTargetPropName)) {
                        propName = aliasTargetPropName;
                    }
                }

                Method writeMethod = setter.getWriteMethod();
                MethodInfo write = ReflectUtils.getMethodInfo(writeMethod);
                Type setterType = write.getSignature().getArgumentTypes()[0];

                codeEmitter.load_local(targetLocal);
                codeEmitter.load_local(sourceLocal);

                codeEmitter.push(propName);
                // 执行 map get
                codeEmitter.invoke_interface(BEAN_MAP, BEAN_MAP_GET);
                // box 装箱，避免 array[] 数组问题
                codeEmitter.box(mapBox);

                // 生成变量
                Local var = codeEmitter.make_local();
                codeEmitter.store_local(var);
                codeEmitter.load_local(var);

                // 先判断 不为null，然后做类型判断
                Label label0 = codeEmitter.make_label();
                codeEmitter.ifnull(label0);
                EmitUtils.load_class(codeEmitter, setterType);
                codeEmitter.load_local(var);
                // ClassUtil.isAssignableValue(Integer.class, id)
                codeEmitter.invoke_static(CLASS_UTIL, IS_ASSIGNABLE_VALUE);
                // 返回值，判断 链式 bean
                Class<?> returnType = writeMethod.getReturnType();
                if (useConverter) {
                    Label label = new Label();
                    codeEmitter.if_jump(Opcodes.IFEQ, label);
                    codeEmitter.load_local(targetLocal);
                    codeEmitter.load_local(var);
                    codeEmitter.unbox_or_zero(setterType);
                    codeEmitter.invoke(write);
                    if (!returnType.equals(Void.TYPE)) {
                        codeEmitter.pop();
                    }
                    codeEmitter.goTo(label0);
                    codeEmitter.visitLabel(label);
                    codeEmitter.load_local(targetLocal);
                    codeEmitter.load_arg(2);
                    codeEmitter.load_local(var);
                    EmitUtils.load_class(codeEmitter, setterType);
                    codeEmitter.push(propName);
                    codeEmitter.invoke_interface(CONVERTER, CONVERT);
                    codeEmitter.unbox_or_zero(setterType);
                    codeEmitter.invoke(write);
                } else {
                    codeEmitter.if_jump(Opcodes.IFEQ, label0);
                    codeEmitter.load_local(targetLocal);
                    codeEmitter.load_local(var);
                    codeEmitter.unbox_or_zero(setterType);
                    codeEmitter.invoke(write);
                }
                // 返回值，判断 链式 bean
                if (!returnType.equals(Void.TYPE)) {
                    codeEmitter.pop();
                }
                codeEmitter.visitLabel(label0);
            }
            codeEmitter.return_value();
            codeEmitter.end_method();
            classEmitter.end_class();
        }
    }
}
