/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredFields;
import org.eclipse.persistence.internal.security.PrivilegedGetMethods;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

/**
 * INTERNAL:
 * Common helper methods for the metadata processing. Security sensitive methods
 * from this class must remain package accessible only.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class MetadataHelper {
    public static final String PERSISTENCE_PACKAGE_PREFIX = "javax.persistence";
    
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
     */
    static boolean havePersistenceAnnotationsDefined(AnnotatedElement[] annotatedElements) {
        for (AnnotatedElement annotatedElement : annotatedElements) {
            for (Annotation annotation : annotatedElement.getDeclaredAnnotations()) {
                if (annotation.annotationType().getName().startsWith(PERSISTENCE_PACKAGE_PREFIX)) {
                    return true;
                }
            }
        }
        
        return false;
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
