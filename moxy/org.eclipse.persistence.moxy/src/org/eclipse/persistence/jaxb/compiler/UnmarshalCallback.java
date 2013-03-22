/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.compiler;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;

/**
 * INTERNAL:
 *  <p><b>Purpose:</b>Hold information about class based JAXB 2.0 Callback methods
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Store information about domainClass and the callback methods</li>
 *  <li>Act as a means to integrate JAXB 2.0 Class based callbacks with TopLink OXM 
 *  Listener based callbacks.</li>
 *  
 *  A Map of UnmarshalCallbacks are created during JAXB 2.0 Generation and are set on a
 *  JAXBUnmarshallerListener on each JAXBUnmarshaller. These callbacks are used to invoke
 *  the JAXB 2.0 Class Based callbacks on each object at the appropriate time.
 *  
 *  @author mmacivor
 *  @since Oracle TopLink 11.1.1.0.0
 *  @see org.eclipse.persistence.jaxb.JAXBUnmarshalListener
 *  @see org.eclipse.persistence.jaxb.JAXBUnmarshaller
 */
public class UnmarshalCallback {
    private Class domainClass;
    private String domainClassName;
    private Method afterUnmarshalCallback;
    private Method beforeUnmarshalCallback;
    private boolean hasAfterUnmarshalCallback = false;
    private boolean hasBeforeUnmarshalCallback = false;
    
    public Method getAfterUnmarshalCallback() {
        return afterUnmarshalCallback;
    }

    public Method getBeforeUnmarshalCallback() {
        return beforeUnmarshalCallback;
    }

    public Class getDomainClass() {
        return domainClass;
    }
    
    /**
     * @param loader
     */
    public void initialize(ClassLoader loader) {        
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try{
                    domainClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(domainClassName, true, loader));
                }catch (PrivilegedActionException ex){
                    if (ex.getCause() instanceof ClassNotFoundException){
                        throw (ClassNotFoundException) ex.getCause();
                    }
                    throw (RuntimeException)ex.getCause();
                }
            }else{
                domainClass = PrivilegedAccessHelper.getClassForName(domainClassName, true, loader);
            }
        } catch (ClassNotFoundException ex) {
            return;
        }
        Class[] params = new Class[]{ Unmarshaller.class, Object.class };
        if (hasBeforeUnmarshalCallback) {
            try {
                Method beforeUnmarshal = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        beforeUnmarshal = (Method)AccessController.doPrivileged(new PrivilegedGetMethod(domainClass, "beforeUnmarshal", params, false));
                    }catch (PrivilegedActionException ex){
                        if (ex.getCause() instanceof NoSuchMethodException){
                            throw (NoSuchMethodException) ex.getCause();
                        }
                        throw (RuntimeException)ex.getCause();
                    }
                }else{
                    beforeUnmarshal = PrivilegedAccessHelper.getMethod(domainClass, "beforeUnmarshal", params, false);
                }
                setBeforeUnmarshalCallback(beforeUnmarshal);
            } catch (NoSuchMethodException nsmex) {}
        }
        if (hasAfterUnmarshalCallback) {
            try {
                Method afterUnmarshal = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        afterUnmarshal = (Method)AccessController.doPrivileged(new PrivilegedGetMethod(domainClass, "afterUnmarshal", params, false));
                    }catch (PrivilegedActionException ex){
                        if (ex.getCause() instanceof NoSuchMethodException){
                            throw (NoSuchMethodException) ex.getCause();
                        }
                        throw (RuntimeException)ex.getCause();
                    }
                }else{
                    afterUnmarshal = PrivilegedAccessHelper.getMethod(domainClass, "afterUnmarshal", params, false);
                }
                setAfterUnmarshalCallback(afterUnmarshal);
            } catch (NoSuchMethodException nsmex) {}
        }
    }

    /**
     * Should not use this method - the init method will 
     * overwrite the set value. 
     */
    public void setAfterUnmarshalCallback(Method method) {
        afterUnmarshalCallback = method;
    }

    public void setHasAfterUnmarshalCallback() {
        hasAfterUnmarshalCallback = true;
    }

    /**
     * Should not use this method - the init method will 
     * overwrite the set value. 
     */
    public void setBeforeUnmarshalCallback(Method method) {
        beforeUnmarshalCallback = method;
    }

    public void setHasBeforeUnmarshalCallback() {
        hasBeforeUnmarshalCallback = true;
    }

    /**
     * Should use setDomainClassName - the init method will overwrite
     * the set value with Class.forName(domainClassName)
     * 
     * @param clazz
     */
    public void setDomainClass(Class clazz) {
        domainClass = clazz;
        setDomainClassName(clazz.getName());
    }

    public void setDomainClassName(String className) {
        domainClassName = className;
    }
}
