/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2012, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - initial implemenation
//     06/24/2014 - 438105 - 2.6.0 - Rick Curtis - Fix bug in EntityListenerInjectionManagerImpl constructor.
//     07/01/2014-2.5.2 Rick Curtis
//       - 438663: Fix injection ordering bug.
package org.eclipse.persistence.internal.sessions.cdi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Manages calls to CDI to inject into managed beans
 * This class will be created reflectively to avoid dependencies on CDI classes in environments
 * that do not support CDI
 */
public class InjectionManagerImpl<T> implements InjectionManager<T> {
    protected BeanManager beanManager = null;
    protected CreationalContext<T> creationalContext = null;
    protected final Map<T, InjectionTarget<T>> injectionTargets = new HashMap<>();

    public InjectionManagerImpl(Object beanManagerInstance) throws NamingException {
        if (beanManagerInstance == null) {
            Context context = new InitialContext();
            try {
                beanManagerInstance = context.lookup("java:comp/BeanManager");
            } catch(NamingException e) {
                beanManagerInstance = context.lookup("java:comp/env/BeanManager");
            }
        }
        beanManager = (BeanManager) beanManagerInstance;
    }

    /**
     * Creates an instance of the CDI managed bean.
     * Calls CDI API to inject into the bean.
     * @param managedBeanClass bean class to be instantiated.
     * @return New instance of bean class with injected content.
     */
    public T createManagedBeanAndInjectDependencies(final Class<T> managedBeanClass) throws NamingException{
        final AnnotatedType<T> aType = beanManager.createAnnotatedType(managedBeanClass);
        final InjectionTarget<T> injectionTarget = beanManager.createInjectionTarget(aType);
        creationalContext = beanManager.createCreationalContext(null);
        final T beanInstance = injectionTarget.produce(creationalContext);
        synchronized (injectionTargets) {
            injectionTargets.put(beanInstance, injectionTarget);
        }
        injectionTarget.inject(beanInstance, creationalContext);
        injectionTarget.postConstruct(beanInstance);
        return beanInstance;
    }

    public void cleanUp(AbstractSession session){
        Set<T> keys = new HashSet<>();
        synchronized(injectionTargets){
            keys.addAll(injectionTargets.keySet());
            for (T listener: keys){
                try{
                    InjectionTarget<T> target = injectionTargets.get(listener);
                    target.preDestroy(listener);
                    target.dispose(listener);
                    injectionTargets.remove(listener);
                } catch (Exception e){
                    session.logThrowable(SessionLog.FINEST, SessionLog.JPA, e);
                }
            }
        }
        if (creationalContext != null){
            creationalContext.release();
        }
    }

}

