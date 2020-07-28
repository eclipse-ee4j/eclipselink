/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 19 April 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Allows for the reflective creation of an implementation of Sun's
 * com.sun.xml.bind.CycleRecoverable$Context interface.
 */
public class CycleRecoverableContextProxy implements InvocationHandler {

    private Object marshaller;

    private CycleRecoverableContextProxy(Object m) {
        this.marshaller = m;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.marshaller;
    }

    public static <A> A getProxy(Class<A> cycleRecoverableContextClass, Object marshaller) {
        return (A) Proxy.newProxyInstance(cycleRecoverableContextClass.getClassLoader(),
                new Class[] { cycleRecoverableContextClass },
                new CycleRecoverableContextProxy(marshaller));
    }

}
