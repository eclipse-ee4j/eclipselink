/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * @author Kyle Chen
 * @since Eclipselink 1.0
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.tables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;

import org.eclipse.persistence.exceptions.EntityManagerSetupException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;

/**
 * This class only contains the common helper methods that can be accessed at
 * package-private level.
 * 
 * @author Kyle Chen
 * @since TopLink 11g
 */
final class MetadataHelper {
    MetadataHelper(){
    }
    /** 
     * INTERNAL:
     * Invoke the specified named method on the object,
     * handling the necessary exceptions.
     */
    static Object invokeMethod(String methodName, Object target ,Object[] params) {
        ArrayList<Class<?>> parmClasses = new ArrayList<Class<?>>();
        if(params!=null){
            for(Object parm : params) {
                parmClasses.add(parm.getClass());
            }
        }
        Method method=null;
        try {
            method = Helper.getDeclaredMethod(target.getClass(), methodName,
                    parmClasses.size() == 0 ? (Class<?>[])null : (Class<?>[]) parmClasses.toArray());            
        } catch (NoSuchMethodException e) {
            EntityManagerSetupException.methodInvocationFailed(method, target,e);
        }
        if(method!=null){
             try {
                 if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                     try {
                         return AccessController.doPrivileged(new PrivilegedMethodInvoker(method, target, params));
                     } catch (PrivilegedActionException exception) {
                         Exception throwableException = exception.getException();
                         if (throwableException instanceof IllegalAccessException) {
                             throw EntityManagerSetupException.cannotAccessMethodOnObject(method, target);
                         } else {
                             throw EntityManagerSetupException.methodInvocationFailed(method, target, throwableException);
                         }
                     }
                 } else {
                     return PrivilegedAccessHelper.invokeMethod(method, target, params);
                 }
             } catch (IllegalAccessException ex1) {
                 throw EntityManagerSetupException.cannotAccessMethodOnObject(method, target);
             } catch (InvocationTargetException ex2) {
                 throw EntityManagerSetupException.methodInvocationFailed(method, target, ex2);
             }
        }else{
            return null;
        }
    }
}
