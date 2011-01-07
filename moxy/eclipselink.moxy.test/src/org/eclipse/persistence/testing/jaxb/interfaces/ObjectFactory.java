/*******************************************************************************
* Copyright (c) 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - July 21/2010 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.interfaces;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRegistry;

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