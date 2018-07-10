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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;


public class RefreshCascadeNonPrivateTest extends TestCase {
    protected Employee employee, r_employee;
    protected String originalFirstName;

    public RefreshCascadeNonPrivateTest() {
        setDescription("Tests if remote refresh cascades no-private parts correctly.");
    }

    public void reset() {
        ((AbstractSession)RemoteModel.getServerSession()).rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    public void setup() {
        ((AbstractSession)RemoteModel.getServerSession()).beginTransaction();
    }

    public void test() throws Exception {

        // Objects to test:  Employee and Manager.  Employee doesn't privately own Manager

        // Update object on server side
        UnitOfWork uow = org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().acquireUnitOfWork();
        employee = (Employee)uow.readObject(Employee.class, new ExpressionBuilder().get("manager").notEqual(null));
        originalFirstName = employee.getManager().getFirstName();
        employee.setManager(new Employee());
        uow.commit();

        // Refresh the object on remote side
        r_employee = (Employee)getSession().refreshObject(employee);

    }

    public void verify() throws Exception {

        if (originalFirstName.equals(r_employee.getManager().getFirstName()))
            throw new TestErrorException("Remote refresh didn't cascade non-private parts correctly.");
    }
}
