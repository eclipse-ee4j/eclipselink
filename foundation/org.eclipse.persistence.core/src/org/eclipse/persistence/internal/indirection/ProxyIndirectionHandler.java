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
package org.eclipse.persistence.internal.indirection;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetClassLoaderForClass;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.exceptions.QueryException;

/**
 * <H2>ProxyIndirectionHandler</H2>
 *
 * Transparently handles EclipseLink indirection for 1:1 relationships through use of  the Java Proxy framework
 * in JDK 1.3.  This class intercepts messages sent to the proxy object, and instantiates its internal
 * <CODE>ValueHolder</CODE> when necessary.
 *
 * @see            org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy
 * @author        Rick Barkhouse
 * @since        TopLink 3.0
 */
public class ProxyIndirectionHandler implements InvocationHandler, Serializable {
    private ValueHolderInterface valueHolder;

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Just in here to allow for Serialization.
     */
    public ProxyIndirectionHandler() {
    }

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Store the value holder.
     */
    private ProxyIndirectionHandler(ValueHolderInterface valueHolder) {
        this.valueHolder = valueHolder;
    }

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Handle the method calls on the proxy object.
     */
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result = null;

        try {
            if ((!ValueHolderInterface.shouldToStringInstantiate) && m.getName().equals("toString")) {
                if (valueHolder.isInstantiated()) {
                    if (valueHolder.getValue() == null) {
                        result = "null";
                    } else {
                        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                            String toString = (String)AccessController.doPrivileged(new PrivilegedMethodInvoker(m, valueHolder.getValue(), args));
                            result = "{ " + toString + " }";
                        }else{
                            String toString = (String)PrivilegedAccessHelper.invokeMethod(m, valueHolder.getValue(), args);
                            result = "{ " + toString + " }";
                        }
                    }
                } else {
                    result = "{ IndirectProxy: not instantiated }";
                }
            } else if (m.getName().equals("equals") && (valueHolder.getValue() == null) && (args[0] == null)) {
                result = Boolean.TRUE;
            } else {
                Object value = valueHolder.getValue();

                // CR2718
                if (value == null) {
                    throw ValidationException.nullUnderlyingValueHolderValue(m.getName());
                } else {
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        result = AccessController.doPrivileged(new PrivilegedMethodInvoker(m, value, args));
                    }else{
                        result = PrivilegedAccessHelper.invokeMethod(m, value, args);
                    }
                }
            }
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (ValidationException e) {
            // need to re-throw the validation exception
            throw e;
        } catch (Exception e) {
            throw QueryException.unexpectedInvocation(e.getMessage());
        }
        return result;
    }

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Utility method to create a new proxy object.
     */
    public static Object newProxyInstance(Class anInterface, Class[] interfaces, ValueHolderInterface valueHolder) {
        ClassLoader classLoader = null;
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
            try{
                classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedGetClassLoaderForClass(anInterface));
            }catch (PrivilegedActionException ex){
                throw (RuntimeException) ex.getCause();
            }
        }else{
            classLoader = PrivilegedAccessHelper.getClassLoaderForClass(anInterface);
        }
        return Proxy.newProxyInstance(classLoader, interfaces, new ProxyIndirectionHandler(valueHolder));
    }

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Get the ValueHolder associated with this handler.
     */
    public ValueHolderInterface getValueHolder() {
        return this.valueHolder;
    }

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Set the ValueHolder associated with this handler.
     */
    public void setValueHolder(ValueHolderInterface value) {
        this.valueHolder = value;
    }
}
