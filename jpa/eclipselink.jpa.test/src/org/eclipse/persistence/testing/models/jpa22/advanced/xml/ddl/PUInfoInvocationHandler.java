/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/02/2015-2.6.0 Dalia Abo Sheasha
//       - 458462: generateSchema throws a ClassCastException within a container.
package org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;

public class PUInfoInvocationHandler implements InvocationHandler {
    private SEPersistenceUnitInfo sePUInfo;

    public PUInfoInvocationHandler(SEPersistenceUnitInfo sePUInfo) {
        this.sePUInfo = sePUInfo;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(sePUInfo, args);
    }
}
