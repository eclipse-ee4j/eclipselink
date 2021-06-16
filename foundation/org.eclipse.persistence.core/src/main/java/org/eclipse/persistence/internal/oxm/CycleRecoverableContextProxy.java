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
//  - rbarkhouse - 19 April 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Allows for the reflective creation of an implementation of Sun's
 * org.glassfish.jaxb.runtime.CycleRecoverable$Context interface.
 */
public class CycleRecoverableContextProxy implements InvocationHandler {

    private Object marshaller;

    private CycleRecoverableContextProxy(Object m) {
        this.marshaller = m;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.marshaller;
    }

    public static <A> A getProxy(Class<A> cycleRecoverableContextClass, Object marshaller) {
        return (A) Proxy.newProxyInstance(cycleRecoverableContextClass.getClassLoader(),
                new Class[] { cycleRecoverableContextClass },
                new CycleRecoverableContextProxy(marshaller));
    }

}
