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

import javax.xml.bind.Marshaller;

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
 *  A Map of MarshalCallbacks are created during JAXB 2.0 Generation and are set on a
 *  JAXBMarshallerListener on each JAXBMarshaller. These callbacks are used to invoke
 *  the JAXB 2.0 Class Based callbacks on each object at the appropriate time.
 *  
 *  @author mmacivor
 *  @since Oracle TopLink 11.1.1.0.0
 *  @see org.eclipse.persistence.jaxb.JAXBMarshalListener
 *  @see org.eclipse.persistence.jaxb.JAXBMarshaller
 */
public class MarshalCallback {
    private Class domainClass;
    private String domainClassName;
    private Method beforeMarshalCallback;
    private Method afterMarshalCallback;
    private boolean hasBeforeMarshalCallback = false;
    private boolean hasAfterMarshalCallback = false;
    
    public Method getAfterMarshalCallback() {
        return afterMarshalCallback;
    }

    public Method getBeforeMarshalCallback() {
        return beforeMarshalCallback;
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
        Class[] params = new Class[] { Marshaller.class };
        if (hasBeforeMarshalCallback) {
            try {
                Method beforeMarshal = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        beforeMarshal = (Method)AccessController.doPrivileged(new PrivilegedGetMethod(domainClass, "beforeMarshal", params, false));
                    }catch (PrivilegedActionException ex){
                        if (ex.getCause() instanceof NoSuchMethodException){
                            throw (NoSuchMethodException) ex.getCause();
                        }
                        throw (RuntimeException)ex.getCause();
                    }
                }else{
                    beforeMarshal = PrivilegedAccessHelper.getMethod(domainClass, "beforeMarshal", params, false);
                }
                setBeforeMarshalCallback(beforeMarshal);
            } catch (NoSuchMethodException nsmex) {}
        }
        if (hasAfterMarshalCallback) {
            try {
                Method afterMarshal = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try{
                        afterMarshal = (Method)AccessController.doPrivileged(new PrivilegedGetMethod(domainClass, "afterMarshal", params, false));
                    }catch (PrivilegedActionException ex){
                        if (ex.getCause() instanceof NoSuchMethodException){
                            throw (NoSuchMethodException) ex.getCause();
                        }
                        throw (RuntimeException)ex.getCause();
                    }
                }else{
                    afterMarshal = PrivilegedAccessHelper.getMethod(domainClass, "afterMarshal", params, false);
                }
                setAfterMarshalCallback(afterMarshal);
            } catch (NoSuchMethodException nsmex) {}
        }
    }

    /**
     * Should not use this method - the init method will 
     * overwrite the set value. 
     */
    public void setAfterMarshalCallback(Method method) {
        afterMarshalCallback = method;
    }

    public void setHasAfterMarshalCallback() {
        hasAfterMarshalCallback = true;
    }

    /**
     * Should not use this method - the init method will 
     * overwrite the set value. 
     */
    public void setBeforeMarshalCallback(Method method) {
        beforeMarshalCallback = method;
    }

    public void setHasBeforeMarshalCallback() {
        hasBeforeMarshalCallback = true;
    }

    /**
     * Should use setDomainClassName - the init method will overwrite
     * the set value with Class.forName(domainClassName). 
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
