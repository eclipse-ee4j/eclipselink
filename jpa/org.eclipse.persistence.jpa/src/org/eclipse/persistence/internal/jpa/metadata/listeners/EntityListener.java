/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     01/05/2010-2.1 Guy Pelletier 
 *       - 211324: Add additional event(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventManager;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

/**
 * An EntityListener is placed on the owning entity's descriptor. 
 * Callback methods from an EntityListener require a signature on the method. 
 * Namely, they must have an Object parameter.
 * 
 * @author Guy Pelletier
 * @since Eclipselink 1.0
 */
public class EntityListener extends DescriptorEventAdapter {
    public final static String POST_BUILD = "postBuild";
    public final static String POST_CLONE = "postClone";
    public final static String POST_DELETE = "postDelete";
    public final static String POST_INSERT = "postInsert";
    public final static String POST_REFRESH = "postRefresh";
    public final static String POST_UPDATE = "postUpdate";
    public final static String PRE_PERSIST = "prePersist";
    public final static String PRE_REMOVE = "preRemove";
    public final static String PRE_UPDATE_WITH_CHANGES = "preUpdateWithChanges";
    
    private Object m_listener;
    private Class m_entityClass;
    private Hashtable<String, Method> m_methods;
    private Hashtable<Integer, Boolean> m_overriddenEvents;
    private static Hashtable<Integer, String> m_eventStrings;

    /**
     * INTERNAL: 
     */
    protected EntityListener(Class entityClass) {
        m_entityClass = entityClass;
        m_methods = new Hashtable<String, Method>();
        
        // Remember which events are overridden in subclasses.
        m_overriddenEvents = new Hashtable<Integer, Boolean>();
        
        // For quick look up of equivalent event strings from event codes.
        if (m_eventStrings == null) {
            m_eventStrings = new Hashtable<Integer, String>();   
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PostBuildEvent), POST_BUILD);
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PostCloneEvent), POST_CLONE);
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PostDeleteEvent), POST_DELETE);
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PostInsertEvent), POST_INSERT);
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PostRefreshEvent), POST_REFRESH);
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PostUpdateEvent), POST_UPDATE);
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PrePersistEvent), PRE_PERSIST);
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PreRemoveEvent), PRE_REMOVE);
            m_eventStrings.put(Integer.valueOf(DescriptorEventManager.PreUpdateWithChangesEvent), PRE_UPDATE_WITH_CHANGES);
        }
    }
    
    /**
     * INTERNAL: 
     */
    public EntityListener(Object listener, Class entityClass) { 
        this(entityClass);
        
        m_listener = listener;
    }
    
    /**
     * INTERNAL:
     */
    public void addEventMethod(String event, Method method) {
        if (m_methods.containsKey(event)) {
            Method existingMethod = (Method) getMethods().get(event);
            
            // We have already set the same callback method name from XML,
            // so just ignore it if this is the case, otherwise throw an
            // exception.
            if (! existingMethod.getName().equals(method.getName())) {
                throw ValidationException.multipleLifecycleCallbackMethodsForSameLifecycleEvent(getListenerClass(), method, getEventMethod(event));                
            }
        } else {
            validateMethod(method);
            m_methods.put(event, method);
        }
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
    protected Method getEventMethod(int eventCode) {
        String eventString = m_eventStrings.get(eventCode);
        
        if (eventString != null) {
            return getEventMethod(eventString);
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     */
    protected Method getEventMethod(String event) {
        return m_methods.get(event);
    }
    
    /**
     * INTERNAL:
     */
    protected String getEventMethodName(String eventName) {
        Method method = getEventMethod(eventName);
        
        if (method != null) {
            return method.getName();
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     */
    public Class getListenerClass() {
        return m_listener.getClass();    
    }
    
    /**
     * INTERNAL:
     */
    public Hashtable getMethods() {
        return m_methods;    
    }
    
    /**
     * INTERNAL:
     */
    public String getPostBuildMethodName() {
        return getEventMethodName(POST_BUILD);
    }
    
    /**
     * INTERNAL:
     */
    public String getPostCloneMethodName() {
        return getEventMethodName(POST_CLONE);
    }

    /**
     * INTERNAL:
     */
    public String getPostDeleteMethodName() {
        return getEventMethodName(POST_DELETE);
    }

    /**
     * INTERNAL:
     */
    public String getPostInsertMethodName() {
        return getEventMethodName(POST_INSERT);
    }
    
    /**
     * INTERNAL:
     */
    public String getPostRefreshMethodName() {
        return getEventMethodName(POST_REFRESH);
    }
    
    /**
     * INTERNAL:
     */
    public String getPostUpdateMethodName() {
        return getEventMethodName(POST_UPDATE);
    }
    
    /**
     * INTERNAL:
     */
    public String getPrePersistMethodName() {
        return getEventMethodName(PRE_PERSIST);
    }
    
    /**
     * INTERNAL:
     */
    public String getPreRemoveMethodName() {
        return getEventMethodName(PRE_REMOVE);
    }
    
    /**
     * INTERNAL:
     */
    public String getPreUpdateWithChangesMethodName() {
        return getEventMethodName(PRE_UPDATE_WITH_CHANGES);
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
    public boolean hasOverriddenEventMethod(Method eventMethod, int eventCode) {
        return hasOverriddenEventMethod(getEventMethod(eventCode), eventMethod);
    }
    
    /**
     * INTERNAL:
     */
    protected boolean hasOverriddenEventMethod(Method eventMethod1, Method eventMethod2) {
        return (eventMethod1 != null && eventMethod1.getName().equals(eventMethod2.getName()));
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasOverriddenEventMethod(Method eventMethod, String eventCode) {
        return hasOverriddenEventMethod(getEventMethod(eventCode), eventMethod);
    }
    
    /**
     * INTERNAL:
     */
    protected void invokeMethod(Method method, Object onObject, Object[] objectList, DescriptorEvent event) {
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
    protected void invokeMethod(String event, DescriptorEvent descriptorEvent) {
        Object[] objectList = { descriptorEvent.getSource() };
        invokeMethod(getEventMethod(event), m_listener, objectList, descriptorEvent);
    }

    /**
     * INTERNAL:
     * Return true is listener has a lifecycle callback method that is 
     * overridden in a subclass.
     */
    public boolean isOverriddenEvent(DescriptorEvent event, Vector eventManagers) {
        int eventCode = event.getEventCode();
        if (! m_overriddenEvents.containsKey(eventCode)) {
            m_overriddenEvents.put(eventCode, false); // default
            
            Method eventMethod = getEventMethod(eventCode);
        
            if (eventMethod != null) {
                for (DescriptorEventManager eventManager : (Vector<DescriptorEventManager>) eventManagers) {
                    EntityListener childListener = (EntityListener) eventManager.getEntityEventListener();
                    
                    // We can't override ourself ...
                    if (childListener == this) {
                        break;
                    } else {
                        if (childListener.hasOverriddenEventMethod(eventMethod, eventCode)) {
                            m_overriddenEvents.put(eventCode, true);
                            break; // stop looking
                        }
                    }
                }
            }
        }
        
        return m_overriddenEvents.get(eventCode);
    }
    
    /**
     * INTERNAL:
     */
    public void postBuild(DescriptorEvent event) {
        invokeMethod(POST_BUILD, event);
    }
    
    /**
     * INTERNAL:
     */
    public void postClone(DescriptorEvent event) {
        invokeMethod(POST_CLONE, event);
    }
    
    /**
     * INTERNAL:
     */
    public void postDelete(DescriptorEvent event) {
        invokeMethod(POST_DELETE, event);
    }
    
    /**
     * INTERNAL:
     */
    public void postInsert(DescriptorEvent event) {
        invokeMethod(POST_INSERT, event);
    }
    
    /**
     * INTERNAL:
     */
    public void postRefresh(DescriptorEvent event) {
        invokeMethod(POST_REFRESH, event);
    }

    /**
     * INTERNAL:
     */
    public void postUpdate(DescriptorEvent event) {
        invokeMethod(POST_UPDATE, event);
    }

    /**
     * INTERNAL:
     */
    public void prePersist(DescriptorEvent event) {
        invokeMethod(PRE_PERSIST, event);
    }
    
    /**
     * INTERNAL:
     */
    public void preRemove(DescriptorEvent event) {
        invokeMethod(PRE_REMOVE, event);
    }
    
    /**
     * INTERNAL:
     */
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
    protected void validateMethod(Method method) {
        int numberOfParameters = method.getParameterTypes().length;
           
        if (numberOfParameters == 1 && method.getParameterTypes()[0].isAssignableFrom(m_entityClass)) {
            // So far so good, now check the method modifiers.
            validateMethodModifiers(method);
        } else {
            Class parameterClass = (numberOfParameters == 0) ? null : method.getParameterTypes()[0];
            throw ValidationException.invalidEntityListenerCallbackMethodArguments(m_entityClass, parameterClass, m_listener.getClass(), method.getName());
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
