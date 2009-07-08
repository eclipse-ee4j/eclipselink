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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.EntityManagerSetupException;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

/**
 * INTERNAL:
 * Common helper methods for the metadata processing. Security sensitive methods
 * from this class must remain package accessible only.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.1 
 */
public class MetadataHelper {
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
