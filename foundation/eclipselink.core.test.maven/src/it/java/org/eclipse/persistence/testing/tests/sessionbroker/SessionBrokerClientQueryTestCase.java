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
package org.eclipse.persistence.testing.tests.sessionbroker;

import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test to ensure the getCursorSize() method on CursoredStream returns the correct value for
 * Objects using inheritance and the same table.
 * @author Tom Ware
 */
public class SessionBrokerClientQueryTestCase extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    private Exception ex1;
    private Exception ex2;

    public void reset() {

    }

    public void setup() {
    }

    public void test() {
        if (!getAbstractSession().containsQuery(ServerBrokerTestModel.QUERY_NAME)) {
            throw new TestErrorException("The Query is not available to the client session");
        }
        try {
            //Bug#3473441 getQuery(String) should always call super first to ensure any change
            //in Session will be reflected in SessionBroker.  No more ClassCastException
            getSession().executeQuery(ServerBrokerTestModel.QUERY_NAME);
        } catch (Exception e) {
            ex1 = e;
        }
        try {
            //Bug#3551263  Override getQuery(String, Vector) in Session to handle client broker
            Employee employee = (Employee)getSession().readObject(Employee.class);
            getSession().executeQuery(ServerBrokerTestModel.QUERY_NAME, employee.getId());
        } catch (Exception e) {
            ex2 = e;
        }

    }

    public void verify() {
        if (ex1 != null) {
            throw new TestErrorException("SessionBroker.executeQuery(String) throws an exception: " + ex1);
        }
        if (ex2 != null) {
            throw new TestErrorException("SessionBroker.executeQuery(String, Object) throws an exception: " + ex2);
        }

    }
}
