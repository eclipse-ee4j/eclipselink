/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     02/02/2015-2.6.0 Dalia Abo Sheasha
//       - 458462: generateSchema throws a ClassCastException within a container.
package org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl;

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
