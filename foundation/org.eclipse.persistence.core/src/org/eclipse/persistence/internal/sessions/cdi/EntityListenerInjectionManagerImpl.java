/*******************************************************************************
 * Copyright (c) 2012, 2014 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implemenation
 *     06/24/2014 - 438105 - 2.6.0 - Rick Curtis - Fix bug in EntityListenerInjectionManagerImpl constructor.
 *     07/01/2014-2.5.2 Rick Curtis 
 *       - 438663: Fix injection ordering bug.
 ******************************************************************************/
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
 * Manages calls to CDI to inject into EntityListeners
 * This class will be created reflectively to avoid dependencies on CDI classes in environments
 * that do not support CDI
 */
public class EntityListenerInjectionManagerImpl implements EntityListenerInjectionManager {

    protected BeanManager beanManager = null;
    protected CreationalContext<Object> creationalContext = null;
    protected Map<Object, InjectionTarget<Object>> injectionTargets = null;
    
    
    public EntityListenerInjectionManagerImpl(Object beanManagerInstance) throws NamingException {
        if (beanManagerInstance == null) {
            Context context = new InitialContext();
            beanManagerInstance = context.lookup("java:comp/BeanManager");
        }
        beanManager = (BeanManager) beanManagerInstance;

        injectionTargets = new HashMap<Object, InjectionTarget<Object>>();
    }

    /**
     * Creates an instance of {@link javax.persistence.Entity} listener.
     * Calls CDI API to inject into listener.
     * @param entityListenerClass Listener class to be instantiated.
     * @return New instance of listener class with injected content.
     */
    public Object createEntityListenerAndInjectDependancies(final Class entityListenerClass) throws NamingException{
        final AnnotatedType<Object> aType = beanManager.createAnnotatedType(entityListenerClass);
        final InjectionTarget<Object> injectionTarget = beanManager.<Object>createInjectionTarget(aType);
        creationalContext = beanManager.<Object>createCreationalContext(null);
        final Object entityListener = injectionTarget.produce(creationalContext);
        synchronized (injectionTargets) {
            injectionTargets.put(entityListener, injectionTarget);
        }
        injectionTarget.inject(entityListener, creationalContext);
        injectionTarget.postConstruct(entityListener);
        return entityListener;
    }
    
    public void cleanUp(AbstractSession session){
        Set<Object> keys = new HashSet<Object>();
        synchronized(injectionTargets){
            keys.addAll(injectionTargets.keySet());
            for (Object listener: keys){
                try{
                    InjectionTarget<Object> target = injectionTargets.get(listener);
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

