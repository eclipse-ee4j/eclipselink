/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     01/05/2010-2.1 Guy Pelletier
//       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
//     07/15/2010-2.2 Guy Pelletier
//       -311395 : Multiple lifecycle callback methods for the same lifecycle event
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.logging.SessionLog;

/**
 * An EntityListener is placed on the owning entity's descriptor.
 * Callback methods from an EntityListener require a signature on the method.
 * Namely, they must have an Object parameter.
 *
 * @author Guy Pelletier
 * @since Eclipselink 1.0
 */
public class EntityListener<T> extends DescriptorEventAdapter {
    public final static String POST_BUILD = "postBuild";
    public final static String POST_CLONE = "postClone";
    public final static String POST_DELETE = "postDelete";
    public final static String POST_INSERT = "postInsert";
    public final static String POST_REFRESH = "postRefresh";
    public final static String POST_UPDATE = "postUpdate";
    public final static String PRE_PERSIST = "prePersist";
    public final static String PRE_REMOVE = "preRemove";
    public final static String PRE_UPDATE_WITH_CHANGES = "preUpdateWithChanges";

    private T m_listener;
    private Class<T> m_listenerClass;
    private Class m_entityClass;
    private Map<String, List<Method>> m_methods;
    private final Map<String, Map<Integer, Boolean>> m_overriddenEvents;
    private static final Map<Integer, String> m_eventStrings;
    private AbstractSession owningSession;

    static {
        // For quick look up of equivalent event strings from event codes.
        Map<Integer, String> mappings = new HashMap<>(9);
        mappings.put(DescriptorEventManager.PostBuildEvent, POST_BUILD);
        mappings.put(DescriptorEventManager.PostCloneEvent, POST_CLONE);
        mappings.put(DescriptorEventManager.PostDeleteEvent, POST_DELETE);
        mappings.put(DescriptorEventManager.PostInsertEvent, POST_INSERT);
        mappings.put(DescriptorEventManager.PostRefreshEvent, POST_REFRESH);
        mappings.put(DescriptorEventManager.PostUpdateEvent, POST_UPDATE);
        mappings.put(DescriptorEventManager.PrePersistEvent, PRE_PERSIST);
        mappings.put(DescriptorEventManager.PreRemoveEvent, PRE_REMOVE);
        mappings.put(DescriptorEventManager.PreUpdateWithChangesEvent, PRE_UPDATE_WITH_CHANGES);
        m_eventStrings = Collections.unmodifiableMap(mappings);
    }
    /**
     * INTERNAL:
     */
    protected EntityListener(Class entityClass) {
        m_entityClass = entityClass;
        m_methods = new ConcurrentHashMap<>();

        // Remember which events are overridden in subclasses. Overridden events
        // must be built for each subclass chain.
        m_overriddenEvents = new ConcurrentHashMap<>();
    }

    public EntityListener(Class<T> listenerClass, Class entityClass){
        this(entityClass);
        this.m_listenerClass = listenerClass;
    }

    /**
     * INTERNAL:
     * You can have multiple event methods for the same event, however, only
     * one event method per class is permitted.
     */
    public void addEventMethod(String event, Method method) {
        if (m_methods.containsKey(event)) {
            Method lastEventMethod = getLastEventMethod(event);

            if (lastEventMethod.getDeclaringClass().equals(method.getDeclaringClass())) {
                throw ValidationException.multipleLifecycleCallbackMethodsForSameLifecycleEvent(getListenerClass(), method, lastEventMethod);
            } else {
                // We must be adding a callback method from a mapped superclass
                // at this point, so validate the method and add it.
                validateMethod(method);
                m_methods.get(event).add(method);
            }
        } else {
            // Validate the method is valid.
            validateMethod(method);

            // Create the methods list and add this method to it.
            List<Method> methods = new ArrayList<>();
            methods.add(method);
            m_methods.put(event, methods);
        }
    }

    /**
     * Create the wrapped listener and trigger CDI injection.
     * @param entityListenerClass the {@link EntityListener} class
     * @return the class instance that has had injection run on it. If injection fails, null.
     */
    protected T createEntityListenerAndInjectDependencies(Class<T> entityListenerClass){
        try{
            return owningSession.<T>getInjectionManager().createManagedBeanAndInjectDependencies(entityListenerClass);
        } catch (Exception e){
            owningSession.logThrowable(SessionLog.FINEST, SessionLog.JPA, e);
        }
        return null;
    }

    /**
     * Construct an instance of the wrapped entity listener
     * This method will attempt to create the listener in a CDI injection
     * friendly manner and if that fails, reflectively instantiate the class
     * @return the entity listener instance
     */
    protected T constructListenerInstance(){
        T entityListenerClassInstance =  createEntityListenerAndInjectDependencies(m_listenerClass);
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    if (entityListenerClassInstance == null){
                        entityListenerClassInstance = AccessController.doPrivileged(new PrivilegedNewInstanceFromClass<>(m_listenerClass));
                    }
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.errorInstantiatingClass(m_listenerClass, exception.getException());
                }
            } else {
                if (entityListenerClassInstance == null){
                    entityListenerClassInstance = PrivilegedAccessHelper.newInstanceFromClass(m_listenerClass);
                }
            }
        } catch (IllegalAccessException | InstantiationException exception) {
            throw ValidationException.errorInstantiatingClass(m_listenerClass, exception);
        }
        return entityListenerClassInstance;
    }

    /**
     * INTERNAL:
     */
    public Class getEntityClass() {
        return m_entityClass;
    }

    /**
     * INTERNAL:
     */
    public Map<String, List<Method>> getAllEventMethods() {
        return m_methods;
    }

    /**
     * INTERNAL:
     */
    public void setAllEventMethods(Map<String, List<Method>> methods) {
        m_methods = methods;
    }

    /**
     * INTERNAL:
     */
    public void setOwningSession(AbstractSession owningSession) {
        this.owningSession = owningSession;
    }

    /**
     * INTERNAL:
     */
    protected List<Method> getEventMethods(int eventCode) {
        String eventString = m_eventStrings.get(eventCode);
        return eventString != null ? getEventMethods(eventString) : null;
    }

    /**
     * INTERNAL:
     */
    protected List<Method> getEventMethods(String event) {
        return m_methods.get(event);
    }

    /**
     * INTERNAL:
     * Assumes a check for event methods for the given event has been called
     * beforehand.
     */
    protected Method getLastEventMethod(String event) {
        List<Method> methods = m_methods.get(event);
        return methods.get(methods.size()-1);
    }

    public T getListener() {
        if (m_listener == null){
            m_listener = constructListenerInstance();
        }
        return m_listener;
    }
    
    /**
     * INTERNAL:
     */
    public Class getListenerClass() {
        return m_listenerClass;
    }

    /**
     * INTERNAL:
     */
    public AbstractSession getOwningSession() {
        return owningSession;
    }

    /**
     * INTERNAL:
     */
    public boolean hasCallbackMethods() {
        return m_methods.size() > 0;
    }

    /**
     * INTERNAL:
     */
    protected boolean hasEventMethods(int eventCode) {
        return getEventMethods(eventCode) != null;
    }

    /**
     * INTERNAL:
     */
    protected boolean hasEventMethods(String event) {
        return getEventMethods(event) != null;
    }

    /**
     * INTERNAL:
     */
    protected boolean hasOverriddenEventMethod(List<Method> eventMethods, Method eventMethod) {
        if (eventMethods != null) {
            for (Method method : eventMethods) {
                if (method.getName().equals(eventMethod.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * INTERNAL:
     */
    protected boolean hasOverriddenEventMethod(Method eventMethod, int eventCode) {
        return hasOverriddenEventMethod(getEventMethods(eventCode), eventMethod);
    }

    /**
     * INTERNAL:
     */
    protected boolean hasOverriddenEventMethod(Method eventMethod, String eventCode) {
        return hasOverriddenEventMethod(getEventMethods(eventCode), eventMethod);
    }

    /**
     * INTERNAL:
     */
    void invokeMethod(Method method, Object onObject, Object[] objectList) {
        if (method != null) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        AccessController.doPrivileged(new PrivilegedMethodInvoker(method, onObject, objectList));
                    } catch (PrivilegedActionException exception) {
                        Exception throwableException = exception.getException();
                        if (throwableException instanceof IllegalAccessException) {
                            throw ValidationException.invalidCallbackMethod(onObject.getClass(), method.toString());
                        } else {
                            Throwable cause = throwableException.getCause();

                            if (cause instanceof RuntimeException) {
                                throw (RuntimeException) cause;
                            } else {
                                throw (Error) cause;
                            }
                        }
                    }
                } else {
                    PrivilegedAccessHelper.invokeMethod(method, onObject, objectList);
                }
            } catch (IllegalAccessException exception) {
                throw ValidationException.invalidCallbackMethod(onObject.getClass(), method.toString());
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();

                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else {
                    throw (Error) cause;
                }
            }
        }
    }

    /**
     * INTERNAL:
     */
    void invokeMethod(String event, DescriptorEvent descriptorEvent) {
        List<Method> eventMethods = getEventMethods(event);
        if (eventMethods != null) {
            for (Method method : eventMethods) {
                Object[] objectList = { descriptorEvent.getSource() };
                invokeMethod(method, getListener(), objectList);
            }
        }
    }

    /**
     * INTERNAL:
     * Return true if listener has a lifecycle callback method that is
     * overridden in a subclass.
     */
    @Override
    public boolean isOverriddenEvent(DescriptorEvent event, List<DescriptorEventManager> eventManagers) {
        int eventCode = event.getEventCode();
        String forSubclass = event.getDescriptor().getJavaClassName();
        Map<Integer, Boolean> subClassMap = m_overriddenEvents.get(forSubclass);

        // If we haven't built an overridden events map for this subclass, do so now.
        if (subClassMap == null) {
            subClassMap = new ConcurrentHashMap<>();
        }

        // Now check the individual events for this subclass.
        if (! subClassMap.containsKey(eventCode)) {
            boolean hasOverrides = false;
            if (hasEventMethods(eventCode)) {
                List<Method> eventMethods = getEventMethods(eventCode);
                for (Method eventMethod : eventMethods) {
                    for (DescriptorEventManager eventManager : eventManagers) {
                        EntityListener childListener = (EntityListener) eventManager.getEntityEventListener();

                        // We can't override ourselves ...
                        if (childListener == this) {
                            break;
                        } else {
                            if (childListener.hasOverriddenEventMethod(eventMethod, eventCode)) {
                                hasOverrides = true;
                                break;
                            }
                        }
                    }
                    if (hasOverrides){
                        break;
                    }
                }
            }
            subClassMap.put(eventCode, hasOverrides);
            //putting this here prevents a vm reorder from putting an unbuilt Map in the
            //m_overriddenEvents collection
            m_overriddenEvents.put(forSubclass, subClassMap);
        }

        return subClassMap.get(eventCode);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void postBuild(DescriptorEvent event) {
        invokeMethod(POST_BUILD, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void postClone(DescriptorEvent event) {
        invokeMethod(POST_CLONE, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void postDelete(DescriptorEvent event) {
        invokeMethod(POST_DELETE, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void postInsert(DescriptorEvent event) {
        invokeMethod(POST_INSERT, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void postRefresh(DescriptorEvent event) {
        invokeMethod(POST_REFRESH, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void postUpdate(DescriptorEvent event) {
        invokeMethod(POST_UPDATE, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void prePersist(DescriptorEvent event) {
        invokeMethod(PRE_PERSIST, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void preRemove(DescriptorEvent event) {
        invokeMethod(PRE_REMOVE, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void preUpdateWithChanges(DescriptorEvent event) {
        invokeMethod(PRE_UPDATE_WITH_CHANGES, event);
    }

    /**
     * INTERNAL:
     */
    public void setPostBuildMethod(Method method) {
        addEventMethod(POST_BUILD, method);
    }

    /**
     * INTERNAL:
     */
    public void setPostCloneMethod(Method method) {
        addEventMethod(POST_CLONE, method);
    }

    /**
     * INTERNAL:
     */
    public void setPostDeleteMethod(Method method) {
        addEventMethod(POST_DELETE, method);
    }

    /**
     * INTERNAL:
     */
    public void setPostInsertMethod(Method method) {
        addEventMethod(POST_INSERT, method);
    }

    /**
     * INTERNAL:
     */
    public void setPostRefreshMethod(Method method) {
        addEventMethod(POST_REFRESH, method);
    }

    /**
     * INTERNAL:
     */
    public void setPostUpdateMethod(Method method) {
        addEventMethod(POST_UPDATE, method);
    }

    /**
     * INTERNAL:
     */
    public void setPrePersistMethod(Method method) {
        addEventMethod(PRE_PERSIST, method);
    }

    /**
     * INTERNAL:
     */
    public void setPreRemoveMethod(Method method) {
        addEventMethod(PRE_REMOVE, method);
    }

    /**
     * INTERNAL:
     */
    public void setPreUpdateWithChangesMethod(Method method) {
        addEventMethod(PRE_UPDATE_WITH_CHANGES, method);
    }

    /**
     * INTERNAL:
     * Used in the debugger.
     */
    @Override
    public String toString() {
        return getEntityClass().getName();
    }

    /**
     * INTERNAL:
     */
    protected void validateMethod(Method method) {
        int numberOfParameters = method.getParameterTypes().length;

        if (numberOfParameters == 1 && method.getParameterTypes()[0].isAssignableFrom(m_entityClass)) {
            // So far so good, now check the method modifiers.
            validateMethodModifiers(method);
        } else {
            Class parameterClass = (numberOfParameters == 0) ? null : method.getParameterTypes()[0];
            throw ValidationException.invalidEntityListenerCallbackMethodArguments(m_entityClass, parameterClass, getListenerClass(), method.getName());
        }
    }

    /**
     * INTERNAL:
     */
    protected void validateMethodModifiers(Method method) {
        int modifiers = method.getModifiers();

        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
            throw ValidationException.invalidCallbackMethodModifier(getListenerClass(), method.getName());
        }
    }
}
