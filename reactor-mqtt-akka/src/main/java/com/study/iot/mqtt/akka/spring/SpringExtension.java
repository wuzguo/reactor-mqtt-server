package com.study.iot.mqtt.akka.spring;

import akka.actor.AbstractActor;
import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/8/16 15:06
 */

public class SpringExtension extends AbstractExtensionId<SpringExtension.SpringExt> {

    private SpringExtension() {

    }

    // thread safety is needed
    private static class Holder {
        static final SpringExtension INSTANCE = new SpringExtension();
    }

    /**
     * The method used to access the SpringExtension.
     */
    public static SpringExtension instance() {
        return Holder.INSTANCE;
    }

    /**
     * Is used by Akka to instantiate the Extension identified by this ExtensionId, internal use only.
     */
    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    /**
     * The lookup method is required by ExtensionIdProvider, so we return ourselves here, this allows us to configure
     * our extension to be loaded when the ActorSystem starts up
     *
     * @return extension itself
     */
    public SpringExtension lookup() {
        return SpringExtension.instance();
    }

    /**
     * The Extension implementation.
     */
    public static class SpringExt implements Extension {

        private volatile ApplicationContext applicationContext;

        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

        /**
         * Create a Props for the specified actorBeanName using the SpringActorProducer class.
         *
         * @param actorBeanName The name of the actor bean to create Props for
         * @return a Props that will create the named actor bean using Spring
         */
        public Props create(String actorBeanName, Object... args) {
            return (args != null && args.length > 0) ? Props.create(SpringActorProducer.class, applicationContext,
                actorBeanName, args) : Props.create(SpringActorProducer.class, applicationContext, actorBeanName);
        }

        /**
         * Create a Props for the specified actorBeanName using the SpringActorProducer class.
         *
         * @param requiredType Type of the actor bean must match. Can be an interface or superclass of the actual class,
         *                     or {@code null} for any match. For example, if the value is {@code Object.class}, this
         *                     method will succeed whatever the class of the returned instance.
         * @return a Props that will create the actor bean using Spring
         */
        public Props create(Class<?> requiredType, Object... args) {
            return (args != null && args.length > 0) ? Props.create(SpringActorProducer.class, applicationContext,
                requiredType, args) : Props.create(SpringActorProducer.class, applicationContext, requiredType);
        }

        /**
         * Create a Props for the specified actorBeanName using the SpringActorProducer class.
         *
         * @param actorBeanName The name of the actor bean to create Props for
         * @param requiredType  Type of the actor bean must match. Can be an interface or superclass of the actual
         *                      class, or {@code null} for any match. For example, if the value is {@code Object.class},
         *                      this method will succeed whatever the class of the returned instance.
         * @return a Props that will create the actor bean using Spring
         */
        public Props create(String actorBeanName, Class<? extends AbstractActor> actorBeanClass) {
            return Props.create(SpringActorProducer.class, applicationContext, actorBeanName, actorBeanClass);
        }
    }
}
