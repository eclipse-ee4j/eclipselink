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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class RefreshObjectTest extends TestCase {
    public Employee emp1, emp2;

    public RefreshObjectTest() {
        setDescription("Test the refresh object call, one to refresh from the database, the other from the server session cache");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getAbstractSession().beginTransaction();


    }

    public void test() {

        emp1 = (Employee)getSession().readObject(Employee.class);
        emp1.setAddress(null);
        getSession().refreshObject(emp1);

        emp2 = (Employee)getSession().readObject(Employee.class);
        emp2.setFirstName("FunkyJunky");
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(emp2);
        query.refreshRemoteIdentityMapResult();
        query.cascadePrivateParts();
        getSession().executeQuery(query);
    }

    protected void verify() {
        if (emp1.getAddress() == null) {
            throw (new TestErrorException("The address field was still NULL after refresh"));
        } else if (emp2.getFirstName().equals("FunkyJunky")) {
            throw (new TestErrorException("RemoteSession refresh from the database fails"));
        }
    }
}
