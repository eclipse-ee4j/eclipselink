/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//     08/01/2012-2.5 Chris Delahunt
//       - 371950: Metadata caching
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.descriptors.SerializableDescriptorEventHolder;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public class JPAEntityListenerHolder implements SerializableDescriptorEventHolder, Cloneable {
    public String listenerClassName;

    public Boolean isDefaultListener;

    public transient DescriptorEventListener listener;

    public Map<String,java.util.List<MethodSerialImpl>> serializableMethods;

    public void setIsDefaultListener(Boolean isDefaultListener) {
        this.isDefaultListener = isDefaultListener;
    }

    @Override
    public void addListenerToEventManager(ClassDescriptor descriptor, AbstractSession session, ClassLoader loader) {

        if (listener == null) {
            if (listenerClassName !=null) {
                Class listenerClass = getListenerClass(loader);

                if (DescriptorEventListener.class.isAssignableFrom(listenerClass)){
                    listener = (DescriptorEventListener)constructListenerInstance(listenerClass);
                } else {
                    EntityListener entityListener = new EntityListener(listenerClass, descriptor.getJavaClass());
                    entityListener.setOwningSession(session);
                    if (!(serializableMethods == null)) {
                        //The user class is not a DescriptorEventListener, so wrap it in a JPA EntityListener instance
                        entityListener.setAllEventMethods(this.convertToMethods(loader));

                    }
                    listener = entityListener;
                }

            } else {
              //it must be an EntityClassListener
                EntityListener entityListener = new EntityClassListener(descriptor.getJavaClass());
                entityListener.setAllEventMethods(this.convertToMethods(loader));
                listener = entityListener;
            }
        }
        //need to also check if this is a EntityClassListener and call
        if (listenerClassName!=null) {
            if (isDefaultListener) {
                descriptor.getEventManager().addDefaultEventListener(listener);
            } else {
                descriptor.getEventManager().addEntityListenerEventListener(listener);
            }
        } else {
            descriptor.getEventManager().setEntityEventListener(listener);
        }

    }

    protected Object constructListenerInstance(Class listenerClass){
        Object entityListenerClassInstance = null;
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    entityListenerClassInstance = AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(listenerClass));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.errorInstantiatingClass(listenerClass, exception.getException());
                }
            } else {
                entityListenerClassInstance = PrivilegedAccessHelper.newInstanceFromClass(listenerClass);
            }
        } catch (IllegalAccessException exception) {
            throw ValidationException.errorInstantiatingClass(listenerClass, exception);
        } catch (InstantiationException exception) {
            throw ValidationException.errorInstantiatingClass(listenerClass, exception);
        }
        return entityListenerClassInstance;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void convertToSerializableMethods(Map<String,java.util.List<Method>> methods) {
        this.serializableMethods = new ConcurrentHashMap<>();
        for (String event: methods.keySet()){
            java.util.List<Method> methodList = methods.get(event);
            java.util.List<MethodSerialImpl> newMethodList = new java.util.ArrayList();
            for (Method method: methodList) {
                MethodSerialImpl serializableMethod = new MethodSerialImpl(method);
                newMethodList.add(serializableMethod);
            }
            this.serializableMethods.put(event, newMethodList);
        }
    }

    /**
     * INTERNAL:
     * used to return an instance of the listenerClassName
     * @return an instance of listenerClassName
     */
    private Class getListenerClass(ClassLoader loader){
        Class entityListenerClass = null;
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    entityListenerClass = AccessController.doPrivileged(new PrivilegedClassForName(listenerClassName, true, loader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.unableToLoadClass(listenerClassName, exception.getException());
                }

            } else {
                entityListenerClass =  PrivilegedAccessHelper.getClassForName(listenerClassName, true, loader);
            }
        } catch (ClassNotFoundException exception) {
            throw ValidationException.unableToLoadClass(listenerClassName, exception);
        }
        return entityListenerClass;
    }

    /**
     * INTERNAL:
     * You can have multiple event methods for the same event, however, only
     * one event method per class is permitted.
     */
    public void addEventMethod(String event, Method method) {
        if (!getMethods().containsKey(event)) {
            List methodsList = new ArrayList<MethodSerialImpl>();
            methodsList.add(method);
            serializableMethods.put(event, methodsList);
        }

        MethodSerialImpl convertedMethod = new MethodSerialImpl(method);
        serializableMethods.get(event).add(convertedMethod);
    }

    /**
     * INTERNAL:
     * This returns a hashtable of methods which are used in a JPA EntityListener instance, built from
     * the MethodSerialImpl representation since Methods are not serializable
     * @param loader
     * @return
     */
    public Map<String,java.util.List<Method>> convertToMethods(ClassLoader loader) {
        Map<String,java.util.List<Method>> table = new ConcurrentHashMap<>();
        for (String event: serializableMethods.keySet()){
            java.util.List<MethodSerialImpl> methodList = serializableMethods.get(event);
            java.util.List<Method> newMethodList = new java.util.ArrayList();
            for (MethodSerialImpl serializedMethod: methodList) {
                try {
                    Method method = serializedMethod.convertToMethod(loader);
                    newMethodList.add(method);
                } catch (Exception e) {
                    throw new javax.persistence.PersistenceException(e);
                }
            }
            table.put(event, newMethodList);
        }
        return table;
    }

    public Map<String,java.util.List<MethodSerialImpl>> getMethods() {
        if (serializableMethods == null) {
            serializableMethods = new ConcurrentHashMap<String, List<MethodSerialImpl>>();
        }
        return serializableMethods;
    }

}

