/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.jaxb;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.jaxb.compiler.UnmarshalCallback;
import org.eclipse.persistence.oxm.XMLUnmarshalListener;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>Provide a wrapper for a JAXB 2.0 Unmarshal Listener that implements
 * XMLUnmarshalListener
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement the XMLUnmarshalListener API</li>
 * <li>Delegate event callbacks to the wrapped Listener instance</li>
 * <li>Perform JAXB 2.0 Class-Based unmarshal event callbacks</li>
 * </ul>
 *
 * @since Oracle TopLink 11.1.1.0.0
 * @author mmacivor
 * @see javax.xml.bind.Marshaller.Listener
 * @see org.eclipse.persistence.oxm.XMLMarshalListener
 */

public class JAXBUnmarshalListener implements XMLUnmarshalListener {
    private Unmarshaller.Listener listener;
    private Map classBasedUnmarshalEvents;
    private Unmarshaller unmarshaller;

    public JAXBUnmarshalListener(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public void setListener(Unmarshaller.Listener jaxbListener) {
        this.listener = jaxbListener;
    }

    public Unmarshaller.Listener getListener() {
        return listener;
    }

    @Override
    public void beforeUnmarshal(Object target, Object parent) {
        if(classBasedUnmarshalEvents != null) {
            UnmarshalCallback callback = (UnmarshalCallback)classBasedUnmarshalEvents.get(target.getClass().getName());
            if(callback != null && callback.getBeforeUnmarshalCallback() != null) {
                try {
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try{
                            AccessController.doPrivileged(new PrivilegedMethodInvoker(callback.getBeforeUnmarshalCallback(), target, new Object[]{unmarshaller, parent}));
                        }catch (PrivilegedActionException ex){
                            if (ex.getCause() instanceof IllegalAccessException){
                                throw (IllegalAccessException) ex.getCause();
                            }
                            if (ex.getCause() instanceof InvocationTargetException){
                                throw (InvocationTargetException) ex.getCause();
                            }
                            throw (RuntimeException)ex.getCause();
                        }
                    }else{
                        PrivilegedAccessHelper.invokeMethod(callback.getBeforeUnmarshalCallback(), target, new Object[]{unmarshaller, parent});
                    }
                } catch(Exception ex) {
                    throw XMLMarshalException.unmarshalException(ex);
                }
            }
        }
        if(listener != null) {
            listener.beforeUnmarshal(target, parent);
        }
    }
    @Override
    public void afterUnmarshal(Object target, Object parent) {
        if(classBasedUnmarshalEvents != null) {
            UnmarshalCallback callback = (UnmarshalCallback)classBasedUnmarshalEvents.get(target.getClass().getName());
            if(callback != null && callback.getAfterUnmarshalCallback() != null) {
                try {
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try{
                            AccessController.doPrivileged(new PrivilegedMethodInvoker(callback.getAfterUnmarshalCallback(), target, new Object[]{unmarshaller, parent}));
                        }catch (PrivilegedActionException ex){
                            if (ex.getCause() instanceof IllegalAccessException){
                                throw (IllegalAccessException) ex.getCause();
                            }
                            if (ex.getCause() instanceof InvocationTargetException){
                                throw (InvocationTargetException) ex.getCause();
                            }
                            throw (RuntimeException)ex.getCause();
                        }
                    }else{
                        PrivilegedAccessHelper.invokeMethod(callback.getAfterUnmarshalCallback(), target, new Object[]{unmarshaller, parent});
                    }
                } catch(Exception ex) {
                    throw XMLMarshalException.unmarshalException(ex);
                }
            }
        }
        if(listener != null) {
            listener.afterUnmarshal(target, parent);
        }
    }

    public void setClassBasedUnmarshalEvents(Map events) {
        this.classBasedUnmarshalEvents = events;
    }
}
