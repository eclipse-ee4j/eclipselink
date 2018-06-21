/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.testing.models.mapping.Employee1;

/**
 * Do the basic ReadObjectTest except on a multiple table. The method of setting up the multiple tables
 * can be chosen by passing the amendment method name in the constructor.
 */
public class MultipleTableReadObjectTest extends org.eclipse.persistence.testing.framework.ReadObjectTest {
    String amendmentMethodName;

    public MultipleTableReadObjectTest(Object originalObject, String amendmentMethodName) {
        super(originalObject);
        this.amendmentMethodName = amendmentMethodName;
        setName("MultipleTableReadObjectTest(" + amendmentMethodName + "," + originalObject + ")");
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
