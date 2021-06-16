/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - July 21/2010 - Initial implementation
package org.eclipse.persistence.testing.jaxb.interfaces;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public Customer createCustomer() {
        return createInstance(Customer.class);
    }

    public Address createAddress() {
        return createInstance(Address.class);
    }

    public PhoneNumber createPhoneNumber() {
        return createInstance(PhoneNumber.class);
    }

    private <T> T createInstance(Class<T> anInterface) {
        return (T) Proxy.newProxyInstance(anInterface.getClassLoader(), new Class[] {anInterface}, new InterfaceInvocationHandler());
    }

    private static class InterfaceInvocationHandler implements InvocationHandler {

        private Map<String, Object> values = new HashMap<String, Object>();

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if(methodName.startsWith("get")) {
                return values.get(methodName.substring(3));
            } else {
                values.put(methodName.substring(3), args[0]);
                return null;
            }
        }

    }

}
