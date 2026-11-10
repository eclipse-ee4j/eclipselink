/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.EntityAgentProperties;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethods;
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

    public static final String POST_LOAD = "postLoad";
    public static final String POST_PERSIST = "postPersist";
    public static final String POST_REMOVE = "postRemove";
    public static final String POST_UPSERT = "postUpsert";
    public static final String PRE_DELETE = "preDelete";
    public static final String PRE_INSERT = "preInsert";
    public static final String PRE_MERGE = "preMerge";
    public static final String PRE_UPSERT = "preUpsert";

    private T m_listener;
    private Class<T> m_listenerClass;
    private Class<?> m_entityClass;
    private Map<String, List<Method>> m_methods;
    private final Map<String, Map<Integer, Boolean>> m_overriddenEvents;
    private static final Map<Integer, String> m_eventStrings;
    private AbstractSession owningSession;

    static {
        // For quick look up of equivalent event strings from event codes.
        m_eventStrings = Map.ofEntries(
                Map.entry(DescriptorEventManager.PostBuildEvent, POST_LOAD),
                Map.entry(DescriptorEventManager.PostCloneEvent, POST_CLONE),
                Map.entry(DescriptorEventManager.PostDeleteEvent, POST_DELETE),
                Map.entry(DescriptorEventManager.PostInsertEvent, POST_INSERT),
                Map.entry(DescriptorEventManager.PostRefreshEvent, POST_REFRESH),
                Map.entry(DescriptorEventManager.PostUpdateEvent, POST_UPDATE),
                Map.entry(DescriptorEventManager.PrePersistEvent, PRE_PERSIST),
                Map.entry(DescriptorEventManager.PreRemoveEvent, PRE_REMOVE),
                Map.entry(DescriptorEventManager.PreUpdateWithChangesEvent, PRE_UPDATE_WITH_CHANGES),
                Map.entry(DescriptorEventManager.PostPersistEvent, POST_PERSIST),
                Map.entry(DescriptorEventManager.PostRemoveEvent, POST_REMOVE),
                Map.entry(DescriptorEventManager.PreInsertEvent, PRE_INSERT),
                Map.entry(DescriptorEventManager.PreDeleteEvent, PRE_DELETE),
                Map.entry(DescriptorEventManager.PreMergeEvent, PRE_MERGE),
                Map.entry(DescriptorEventManager.PreUpsertEvent, PRE_UPSERT),
                Map.entry(DescriptorEventManager.PostUpsertEvent, POST_UPSERT));
    }
    /**
     * INTERNAL:
     */
    protected EntityListener(Class<?> entityClass) {
        m_entityClass = entityClass;
        m_methods = new ConcurrentHashMap<>();

        // Remember which events are overridden in subclasses. Overridden events
        // must be built for each subclass chain.
        m_overriddenEvents = new ConcurrentHashMap<>();
    }

    public EntityListener(Class<T> listenerClass, Class<?> entityClass){
        this(entityClass);
        this.m_listenerClass = listenerClass;
    }

    /**
     * INTERNAL:
     * You can have multiple event methods for the same event, however, only
     * one event method per class is permitted.
     */
    public void addEventMethod(String event, Method method) {
        validateMethod(method);
        List<Method> methods = m_methods.computeIfAbsent(event, key -> new ArrayList<>());
        for (Method existing : methods) {
            if (existing.getDeclaringClass().equals(method.getDeclaringClass())
                    && Arrays.equals(existing.getParameterTypes(), method.getParameterTypes())) {
                throw ValidationException.multipleLifecycleCallbackMethodsForSameLifecycleEvent(
                        getListenerClass(), method, existing);
            }
        }
        methods.add(method);
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
    public Class<?> getEntityClass() {
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
    public Class<?> getListenerClass() {
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
        return !m_methods.isEmpty();
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
                if (method.getName().equals(eventMethod.getName())
                        && Arrays.equals(method.getParameterTypes(), eventMethod.getParameterTypes())) {
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
     * INTERNAL: An override suppresses an inherited callback even when the
     * overriding method does not itself carry a lifecycle annotation.
     */
    protected static boolean isMethodOverridden(Method method, Class<?> targetClass) {
        if (Modifier.isPrivate(method.getModifiers())) {
            return false;
        }
        for (Class<?> type = targetClass; type != null && type != method.getDeclaringClass();
                type = type.getSuperclass()) {
            Method[] methods = PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                    ? AccessController.doPrivileged(new PrivilegedGetDeclaredMethods(type))
                    : PrivilegedAccessHelper.getDeclaredMethods(type);
            for (Method candidate : methods) {
                if (candidate.getName().equals(method.getName())
                        && Arrays.equals(candidate.getParameterTypes(), method.getParameterTypes())
                        && !Modifier.isPrivate(candidate.getModifiers())
                        && (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())
                        || type.getPackageName().equals(method.getDeclaringClass().getPackageName()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * INTERNAL:
     */
    void invokeMethod(Method method, Object onObject, Object[] objectList) {
        if (method != null) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        AccessController.doPrivileged(new PrivilegedMethodInvoker<Void>(method, onObject, objectList));
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
        // Agent reads notify PostLoad on the final detached copy through PostClone.
        // Never notify application callbacks on a cache/source object here.
        invokeMethod(POST_BUILD, event);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void postClone(DescriptorEvent event) {
        Object suppressedSource = event.getSession().getProperty(EntityAgentProperties.SUPPRESSED_COPY_SOURCE);
        if (!isInternalRead(event) && (suppressedSource == null || suppressedSource != event.getOriginalObject())) {
            invokeMethod(POST_CLONE, event);
        }
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
        // Invalidated cache reconstruction is followed by a clone callback.
        // Preserve requested refresh delivery, including ordinary EntityManager
        // source refreshes, while suppressing the agent's marked internal reads.
        boolean entityTarget = event.getSession().isUnitOfWork()
                || (event.getSession().isClientSession()
                && Boolean.TRUE.equals(event.getSession().getProperty(EntityAgentProperties.ENTITY_AGENT)));
        boolean requestedRefresh = event.getQuery() instanceof org.eclipse.persistence.queries.ObjectLevelReadQuery read
                && read.shouldRefreshIdentityMapResult();
        if ((entityTarget || requestedRefresh) && !isInternalRead(event)) {
            invokeMethod(POST_REFRESH, event);
        }
    }

    private boolean isInternalRead(DescriptorEvent event) {
        return Boolean.TRUE.equals(event.getSession().getProperty(EntityAgentProperties.INTERNAL_READ))
                || (event.getQuery() != null && Boolean.TRUE.equals(
                event.getQuery().getProperty(EntityAgentProperties.INTERNAL_READ)));
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
     */
    public void setPostLoadMethod(Method method) {
        addEventMethod(POST_LOAD, method);
    }

    @Override
    public void postPersist(DescriptorEvent event) {
        invokeMethod(POST_PERSIST, event);
    }

    /**
     * INTERNAL:
     */
    public void setPostPersistMethod(Method method) {
        addEventMethod(POST_PERSIST, method);
    }

    @Override
    public void postRemove(DescriptorEvent event) {
        invokeMethod(POST_REMOVE, event);
    }

    /**
     * INTERNAL:
     */
    public void setPostRemoveMethod(Method method) {
        addEventMethod(POST_REMOVE, method);
    }

    @Override
    public void postUpsert(DescriptorEvent event) {
        invokeMethod(POST_UPSERT, event);
    }

    /**
     * INTERNAL:
     */
    public void setPostUpsertMethod(Method method) {
        addEventMethod(POST_UPSERT, method);
    }

    @Override
    public void preDelete(DescriptorEvent event) {
        invokeMethod(PRE_DELETE, event);
    }

    /**
     * INTERNAL:
     */
    public void setPreDeleteMethod(Method method) {
        addEventMethod(PRE_DELETE, method);
    }

    @Override
    public void preInsert(DescriptorEvent event) {
        invokeMethod(PRE_INSERT, event);
    }

    /**
     * INTERNAL:
     */
    public void setPreInsertMethod(Method method) {
        addEventMethod(PRE_INSERT, method);
    }

    @Override
    public void preMerge(DescriptorEvent event) {
        invokeMethod(PRE_MERGE, event);
    }

    /**
     * INTERNAL:
     */
    public void setPreMergeMethod(Method method) {
        addEventMethod(PRE_MERGE, method);
    }

    @Override
    public void preUpsert(DescriptorEvent event) {
        invokeMethod(PRE_UPSERT, event);
    }

    /**
     * INTERNAL:
     */
    public void setPreUpsertMethod(Method method) {
        addEventMethod(PRE_UPSERT, method);
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
            Class<?> parameterClass = (numberOfParameters == 0) ? null : method.getParameterTypes()[0];
            throw ValidationException.invalidEntityListenerCallbackMethodArguments(m_entityClass, parameterClass, getListenerClass(), method.getName());
        }
    }

    /**
     * INTERNAL:
     */
    protected void validateMethodModifiers(Method method) {
        int modifiers = method.getModifiers();

        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || method.getReturnType() != void.class) {
            throw ValidationException.invalidCallbackMethodModifier(getListenerClass(), method.getName());
        }
    }
}
