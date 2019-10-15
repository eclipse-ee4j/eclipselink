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
package org.eclipse.persistence.testing.tests.sessioncache;

import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class WriteNewObjectTest extends TestCase {
    public WriteNewObjectTest() {
        setDescription("The test ensures that new objects are put in the session cache");
    }

    protected void setup() {
        // Flush the cache
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getAbstractSession().beginTransaction();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Employee empInsert = new Employee();
        empInsert.setFirstName("TestPerson");
        empInsert.setFemale();
        empInsert.setLastName("Smith");
        empInsert.setSalary(55555);
        uow.registerObject(empInsert);
        uow.commit();
    }

    protected void verify() {
        //ensure changes were merged into the session cache
        IdentityMap im = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
        if ((im == null) || (im.getSize() != 1)) {
            throw new TestErrorException("Employee should have been put into session cache.");
        }
    }

    public void reset() throws Exception {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
