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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;

/**
 * INTERNAL:
 * Class used to represent java.lang.reflect.Method instances to allow serialization
 * 
 * @author Chris Delahunt
 * @since EclipseLink 2.5
 */
public class MethodSerialImpl implements Serializable {
    public String methodName;
    public String declaringClassName;
    public java.util.List<String> paramList;

    public MethodSerialImpl(Method method) {
        methodName = method.getName();
        declaringClassName = method.getDeclaringClass().getName();
        paramList = new java.util.ArrayList((method.getParameterTypes()).length);
        for (Class clazz: method.getParameterTypes()) {
            paramList.add(clazz.getName());
        }
    }

    public Method convertToMethod(ClassLoader loader) throws NoSuchMethodException {
        //Build the class
        Class declaringClass = null;
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    declaringClass = (Class) AccessController.doPrivileged(new PrivilegedClassForName(declaringClassName, true, loader));
                } catch (PrivilegedActionException exception) {
                    throw ValidationException.unableToLoadClass(declaringClassName, exception.getException());
                }
            } else {
                declaringClass = PrivilegedAccessHelper.getClassForName(declaringClassName, true, loader);
            }
        } catch (ClassNotFoundException exception) {
            throw ValidationException.unableToLoadClass(declaringClassName, exception);
        }

        //Build the method argument class types.
        Class[] argTypes = new Class[paramList.size()];
        int i=0;
        for (String paramType: paramList) {
            try {
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        argTypes[i++] = (Class) AccessController.doPrivileged(new PrivilegedClassForName(paramType, true, loader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.unableToLoadClass(paramType, exception.getException());
                    }
                } else {
                    argTypes[i++] = PrivilegedAccessHelper.getClassForName(paramType, true, loader);
                }
            } catch (ClassNotFoundException exception) {
                throw ValidationException.unableToLoadClass(paramType, exception);
            }
        }
        return org.eclipse.persistence.internal.helper.Helper.getDeclaredMethod(declaringClass, methodName, argTypes);
    }
}
