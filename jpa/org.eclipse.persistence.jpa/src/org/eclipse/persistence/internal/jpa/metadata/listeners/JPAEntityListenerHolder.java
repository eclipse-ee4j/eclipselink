/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *     08/01/2012-2.5 Chris Delahunt
 *       - 371950: Metadata caching 
 ******************************************************************************/ 
package org.eclipse.persistence.internal.jpa.metadata.listeners;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.SerializableDescriptorEventHolder;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;

public class JPAEntityListenerHolder implements SerializableDescriptorEventHolder, Cloneable {
    public String listenerClassName;

    public Boolean isDefaultListener;
    public Boolean extendsDescriptorEvent;

    public transient DescriptorEventListener listener;

    public java.util.Hashtable<String,java.util.List<MethodSerialImpl>> serializableMethods;

    public void setIsDefaultListener(Boolean isDefaultListener) {
        this.isDefaultListener = isDefaultListener;
    }

    @Override
    public void addListenerToEventManager(ClassDescriptor descriptor, ClassLoader loader) {
        if (listener == null) {
            if (listenerClassName !=null) {
                //This must be an EntityListener, create the user class and the instance
                Object entityListenerClassInstance = getListenerInstance(loader);
                if (serializableMethods == null) {
                    //if it has no methods, it must implement DescriptorEventListener and not need to be wrapped
                    listener = (DescriptorEventListener) entityListenerClassInstance;
                } else {
                    //The user class is not a DescriptorEventListener, so wrap it in a JPA EntityListener instance
                    EntityListener entityListener = new EntityListener(entityListenerClassInstance, descriptor.getJavaClass());
                    entityListener.setAllEventMethods(this.convertToMethods(loader));
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
    
    /**
     * INTERNAL:
     * used to return an instance of the listenerClassName
     * @return an instance of listenerClassName
     */
    private Object getListenerInstance(ClassLoader loader){
        Object entityListenerClassInstance = null;
        Class entityListenerClass = null;
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    entityListenerClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(listenerClassName, true, loader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.unableToLoadClass(listenerClassName, exception.getException());
                }
                try {
                    entityListenerClassInstance = AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(entityListenerClass));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.errorInstantiatingClass(entityListenerClass, exception.getException());
                }
            } else {
                entityListenerClass =  PrivilegedAccessHelper.getClassForName(listenerClassName, true, loader);
                entityListenerClassInstance = PrivilegedAccessHelper.newInstanceFromClass(entityListenerClass);
            }
        } catch (ClassNotFoundException exception) {
            throw ValidationException.unableToLoadClass(listenerClassName, exception);
        } catch (IllegalAccessException exception) {
            throw ValidationException.errorInstantiatingClass(entityListenerClass, exception);
        } catch (InstantiationException exception) {
            throw ValidationException.errorInstantiatingClass(entityListenerClass, exception);
        }
        return entityListenerClassInstance;
    }
    
    public void convertToSerializableMethods(java.util.Hashtable<String,java.util.List<Method>> methods) {
        this.serializableMethods = new java.util.Hashtable();
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
    public java.util.Hashtable<String,java.util.List<Method>> convertToMethods(ClassLoader loader) {
        java.util.Hashtable<String,java.util.List<Method>> table = new java.util.Hashtable();
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

    public java.util.Hashtable<String,java.util.List<MethodSerialImpl>> getMethods() {
        if (serializableMethods == null) {
            serializableMethods = new Hashtable<String, List<MethodSerialImpl>>();
        }
        return serializableMethods;
    }

}
