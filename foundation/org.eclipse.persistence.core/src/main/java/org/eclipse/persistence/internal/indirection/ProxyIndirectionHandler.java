/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.indirection;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
public class ProxyIndirectionHandler<T> implements InvocationHandler, Serializable {
    private ValueHolderInterface<T> valueHolder;

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
    private ProxyIndirectionHandler(ValueHolderInterface<T> valueHolder) {
        this.valueHolder = valueHolder;
    }

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Handle the method calls on the proxy object.
     */
    @Override
    public Object invoke(final Object proxy, final Method m, final Object[] args) throws Throwable {
        Object result = null;

        try {
            if ((!ValueHolderInterface.shouldToStringInstantiate) && m.getName().equals("toString")) {
                if (valueHolder.isInstantiated()) {
                    if (valueHolder.getValue() == null) {
                        result = "null";
                    } else {
                        final String toString = PrivilegedAccessHelper.callDoPrivilegedWithException(
                                () -> PrivilegedAccessHelper.invokeMethod(m, valueHolder.getValue(), args)
                        );
                        result = "{ " + toString + " }";
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
                    result = PrivilegedAccessHelper.callDoPrivilegedWithException(
                            () -> PrivilegedAccessHelper.invokeMethod(m, value, args)
                    );
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
    public static <T> Object newProxyInstance(final Class<?> anInterface, final Class<?>[] interfaces, final ValueHolderInterface<T> valueHolder) {
        final ClassLoader classLoader = PrivilegedAccessHelper.callDoPrivileged(
                () -> PrivilegedAccessHelper.getClassLoaderForClass(anInterface)
        );
        return Proxy.newProxyInstance(classLoader, interfaces, new ProxyIndirectionHandler<>(valueHolder));
    }

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Get the ValueHolder associated with this handler.
     */
    public ValueHolderInterface<T> getValueHolder() {
        return this.valueHolder;
    }

    // =====================================================================

    /**
     * INTERNAL:
     *
     * Set the ValueHolder associated with this handler.
     */
    public void setValueHolder(ValueHolderInterface<T> value) {
        this.valueHolder = value;
    }
}
