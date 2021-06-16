/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.testing.models.mapping.Employee1;

/**
 * Do the basic ReadAllTest except on a multiple table. The method of setting up the multiple tables
 * can be chosen by passing the amendment method name in the constructor.
 */
public class MultipleTableReadAllTest extends org.eclipse.persistence.testing.framework.ReadAllTest {
    String amendmentMethodName;

    public MultipleTableReadAllTest(Class aClass, int numberOfInstances, String amendmentMethodName) {
        super(aClass, numberOfInstances);
        this.amendmentMethodName = amendmentMethodName;
        setName("MultipleTableReadAllTest(" + amendmentMethodName + "," + aClass + ", " + numberOfInstances + ")");
    }

    protected void setup() {
        super.setup();
        try {
            // Get the Method
            Class[] parms = new Class[1];
            parms[0] = DatabaseSessionImpl.class;
            java.lang.reflect.Method method = Employee1.class.getMethod(this.amendmentMethodName, parms);

            // Invoke it.
            Object[] objectParameters = new Object[1];
            objectParameters[0] = getSession();
            method.invoke(Employee1.class, objectParameters);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new org.eclipse.persistence.testing.framework.TestErrorException("Problem finding or invoking Method on " + getClass() + ": " + this.amendmentMethodName);
        }
    }
}
