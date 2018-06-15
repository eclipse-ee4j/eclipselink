/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

public class ReadWithExpressionTest extends AutoVerifyTestCase {
    Employee employee;

    public ReadWithExpressionTest() {
        setDescription("Tests ReadObject using an Expression using Proxy Indirection.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
    }

    public void test() {
        ReadObjectQuery q = new ReadObjectQuery();
        q.setReferenceClass(Employee.class);
        q.setSelectionCriteria(new ExpressionBuilder().get("firstName").like("Rick%"));
        employee = (Employee)getSession().executeQuery(q);
    }

    public void verify() {
        if (employee == null) {
            throw new TestErrorException("No employee with first name \"Rick\" was returned.");
        }

        // Test the indirection
        if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(AddressImpl.class).getSize() != 0) {
            throw new TestErrorException("ProxyIndirection did not work - Address was read in along with Employee.");
        }
        employee.getAddress().getCity();
        if (((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(EmployeeImpl.class).getSize() == 0) {
            throw new TestErrorException("ProxyIndirection did not work - Address was not read in when triggered from Employee.");
        }
    }
}
