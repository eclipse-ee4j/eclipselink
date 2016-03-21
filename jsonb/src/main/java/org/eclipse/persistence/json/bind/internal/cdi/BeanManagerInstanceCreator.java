/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * CDI instance manager.
 * Instances are are created and stored per instance of {@link org.eclipse.persistence.json.bind.JsonBinding}.
 * Calling close on JsonBinding, cleans up Jsonb CDI instances and in case of "dependant" scope its dependencies.
 *
 * @author Roman Grigoriadi
 */
public class BeanManagerInstanceCreator implements JsonbComponentInstanceCreator {

    public static final String BEAN_MANAGER_NAME = "java:comp/BeanManager";
    private BeanManager beanManager;

    private final ConcurrentMap<Class<?>, CDIManagedBean<?>> injectionTargets = new ConcurrentHashMap<>();

    /**
     * Create instance.
     * @throws NamingException In case BeanManager must be looked up and is not found
     */
    public BeanManagerInstanceCreator() throws NamingException {
        beanManager = lookupBeanManager();
        if (beanManager == null) {
            throw new BeanManagerNotFoundException();
        }
    }

    private BeanManager lookupBeanManager() throws NamingException {
        BeanManager cdiBeanManager = CDI.current().getBeanManager();
        if (cdiBeanManager != null) {
            return cdiBeanManager;
        }
        InitialContext context = new InitialContext();
        return (BeanManager) context.lookup(BEAN_MANAGER_NAME);
    }

    /**
     * Creates an instance of the CDI managed bean.
     * Calls CDI API to inject into the bean.
     *
     * @param componentClass bean class to be instantiated.
     * @return New instance of bean class with injected content.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrCreateComponent(Class<T> componentClass) {
        return (T) injectionTargets.computeIfAbsent(componentClass, clazz -> {
            final AnnotatedType<T> aType = beanManager.createAnnotatedType(componentClass);
            final InjectionTarget<T> injectionTarget = beanManager.createInjectionTarget(aType);
            CreationalContext<T> creationalContext = beanManager.createCreationalContext(null);
            final T beanInstance = injectionTarget.produce(creationalContext);
            injectionTarget.inject(beanInstance, creationalContext);
            injectionTarget.postConstruct(beanInstance);
            return new CDIManagedBean(beanInstance, injectionTarget, creationalContext);
        }).getInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void close() throws IOException {
        injectionTargets.forEach((clazz,target)-> cleanupBean(target));
        injectionTargets.clear();
    }

    private <T> void cleanupBean(CDIManagedBean<T> bean) {
        bean.getInjectionTarget().preDestroy(bean.getInstance());
        bean.getInjectionTarget().dispose(bean.getInstance());
        bean.getCreationalContext().release();
    }

    /**
     * Holder for bean instance and its injection target.
     */
    private static final class CDIManagedBean<T> {
        private final T instance;
        private final InjectionTarget<T> injectionTarget;
        private final CreationalContext<T> creationalContext;

        public CDIManagedBean(T instance, InjectionTarget<T> injectionTarget, CreationalContext<T> creationalContext) {
            this.instance = instance;
            this.injectionTarget = injectionTarget;
            this.creationalContext = creationalContext;
        }

        /**
         * @return CDI InjectionTarget
         */
        private InjectionTarget<T> getInjectionTarget() {
            return injectionTarget;
        }

        /**
         * @return managed instance of a bean
         */
        private T getInstance() {
            return instance;
        }

        /**
         * @return creational context
         */
        private CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }
    }
}
