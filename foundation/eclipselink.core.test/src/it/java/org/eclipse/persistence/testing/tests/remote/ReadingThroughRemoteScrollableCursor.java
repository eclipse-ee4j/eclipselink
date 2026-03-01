/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class ReadingThroughRemoteScrollableCursor extends TestCase {

    public ReadingThroughRemoteScrollableCursor() {
        setDescription("ReadingThroughRemoteScrollableCursor");
    }

    @Override
    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        getAbstractSession().beginTransaction();
    }

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Employee.class);
        query.useScrollableCursor();
        ScrollableCursor cursor = (ScrollableCursor)getSession().executeQuery(query);
        Employee emp = (Employee)cursor.next();
        emp = (Employee)uow.registerObject(emp);
        emp.setFirstName("Sami");
        uow.commit();
    }
}
