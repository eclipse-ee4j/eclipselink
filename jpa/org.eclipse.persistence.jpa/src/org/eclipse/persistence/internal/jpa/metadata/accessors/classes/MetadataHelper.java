/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     Guy Pelletier (Oracle), March 6, 2008 
 *        - New file introduced for bug 221658.  
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredFields;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethods;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedGetMethods;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

/**
 * Common helper methods for the class metadata processing.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class MetadataHelper {
    public static final String PERSISTENCE_PACKAGE_PREFIX = "javax.persistence";
    
    /**
     * INTERNAL:
     * Return only the actual methods declared on this entity class.
     */
    static Method[] getCandidateCallbackMethodsForEntityClass(Class entityClass) {
        return getDeclaredMethods(entityClass);
    }
    
    /**
     * INTERNAL:
     * Returns a list of methods from the given class, which can have private, 
     * protected, package and public access, AND will also return public 
     * methods from superclasses.
     */
    static Method[] getCandidateCallbackMethodsForEntityListener(EntityListenerMetadata listener) {
        HashSet candidateMethods = new HashSet();
        Class listenerClass = listener.getListenerClass();
        
        // Add all the declared methods ...
        Method[] declaredMethods = getDeclaredMethods(listenerClass);
        for (int i = 0; i < declaredMethods.length; i++) {
            candidateMethods.add(declaredMethods[i]);
        }
        
        // Now add any public methods from superclasses ...
        Method[] methods = getMethods(listenerClass);
        for (int i = 0; i < methods.length; i++) {
            if (candidateMethods.contains(methods[i])) {
                continue;
            }
            
            candidateMethods.add(methods[i]);
        }
        
        return (Method[]) candidateMethods.toArray(new Method[candidateMethods.size()]);
    }
    
    /**
     * INTERNAL:
     * Return potential lifecyle callback event methods for a mapped superclass. 
     * We must 'convert' the method to the entity class context before adding it 
     * to the listener.
     */
    static Method[] getCandidateCallbackMethodsForMappedSuperclass(Class mappedSuperclass, Class entityClass) {
        ArrayList candidateMethods = new ArrayList();
        Method[] allMethods = getMethods(entityClass);
        Method[] declaredMethods = getDeclaredMethods(mappedSuperclass);
        
        for (int i = 0; i < declaredMethods.length; i++) {
            Method method = getMethodForName(allMethods, declaredMethods[i].getName());
            
            if (method != null) {
                candidateMethods.add(method);
            }
        }
        
        return (Method[]) candidateMethods.toArray(new Method[candidateMethods.size()]);
    }
    
    /**
     * INTERNAL:
     * Load a class from a given class name.
     */
    static Class getClassForName(String classname, ClassLoader loader) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (Class) AccessController.doPrivileged(new PrivilegedClassForName(classname, true, loader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.unableToLoadClass(classname, exception.getException());
                }
            } else {
                return PrivilegedAccessHelper.getClassForName(classname, true, loader);
            }
        } catch (ClassNotFoundException exception) {
            throw ValidationException.unableToLoadClass(classname, exception);
        }
    }
    
    /**
     * INTERNAL:
     */
    static int getDeclaredAnnotationsCount(AnnotatedElement annotatedElement, MetadataDescriptor descriptor) {
        if (descriptor.ignoreAnnotations()) {
            return 0;
        } else {
            // Look for javax.persistence annotations only.
            int count = 0;
            
            for (Annotation annotation : annotatedElement.getDeclaredAnnotations()) {
                if (annotation.annotationType().getName().startsWith(PERSISTENCE_PACKAGE_PREFIX)) {
                    count++;
                }
            }
            
            return count;
        }
    }
    
    /**
     * INTERNAL:
     * Get the declared methods from a class using the doPriveleged security
     * access. This call returns all methods (private, protected, package and
     * public) on the give class ONLY. It does not traverse the superclasses.
     */
    static Method[] getDeclaredMethods(Class cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try {
                return (Method[])AccessController.doPrivileged(new PrivilegedGetDeclaredMethods(cls));
            } catch (PrivilegedActionException exception) {
                // we will not get here, there are no checked exceptions in this call
                return null;
            }
        } else {
            return org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getDeclaredMethods(cls);
        }
    }
    
    /**
     * INTERNAL:
     * Get the declared fields from a class using the doPriveleged security
     * access.
     */
    static Field[] getFields(Class cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try {
                return (Field[])AccessController.doPrivileged(new PrivilegedGetDeclaredFields(cls));
            } catch (PrivilegedActionException exception) {
                // no checked exceptions are thrown, so we should not get here
                return null;
            }
        } else {
            return PrivilegedAccessHelper.getDeclaredFields(cls);
        }
    }  
    
    /**
     * INTERNAL:
     * If the methodName passed in is a declared method on cls, then return
     * the methodName. Otherwise return null to indicate it does not exist.
     */
    static Method getMethod(String methodName, Class cls, Class[] params) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return AccessController.doPrivileged(new PrivilegedGetMethod(cls, methodName, params, true));
                } catch (PrivilegedActionException exception) {
                    return null;
                }
            } else {
                return PrivilegedAccessHelper.getMethod(cls, methodName, params, true);
            }
        } catch (NoSuchMethodException e1) {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Find the method in the list where method.getName() == methodName.
     */
    static Method getMethodForName(Method[] methods, String methodName) {
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
        
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        
        return null;
    }
    
    /**
     * INTERNAL:
     * Get the methods from a class using the doPriveleged security access. 
     * This call returns only public methods from the given class and its 
     * superclasses.
     */
    static Method[] getMethods(Class cls) {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try {
                return (Method[])AccessController.doPrivileged(new PrivilegedGetMethods(cls));
            } catch (PrivilegedActionException exception) {
                return null;
            }
        } else {
            return PrivilegedAccessHelper.getMethods(cls);
        }
    }
    
    /** 
     * INTERNAL:
     * Invoke the specified named method on the object, handling the necessary 
     * exceptions.
     */
    static Object invokeMethod(String methodName, Object target) {
        Method method = null;
        
        try {
            method = Helper.getDeclaredMethod(target.getClass(), methodName);            
        } catch (NoSuchMethodException e) {
            EntityManagerSetupException.methodInvocationFailed(method, target,e);
        }
        
        if (method != null) {
             try {
                 if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                     try {
                         return AccessController.doPrivileged(new PrivilegedMethodInvoker(method, target));
                     } catch (PrivilegedActionException exception) {
                         Exception throwableException = exception.getException();
                         if (throwableException instanceof IllegalAccessException) {
                             throw EntityManagerSetupException.cannotAccessMethodOnObject(method, target);
                         } else {
                             throw EntityManagerSetupException.methodInvocationFailed(method, target, throwableException);
                         }
                     }
                 } else {
                     return PrivilegedAccessHelper.invokeMethod(method, target);
                 }
             } catch (IllegalAccessException ex1) {
                 throw EntityManagerSetupException.cannotAccessMethodOnObject(method, target);
             } catch (InvocationTargetException ex2) {
                 throw EntityManagerSetupException.methodInvocationFailed(method, target, ex2);
             }
        } else {
            return null;
        }
    }
}
