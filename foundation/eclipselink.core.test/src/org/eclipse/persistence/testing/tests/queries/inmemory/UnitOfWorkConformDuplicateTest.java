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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test conform when new objects are stored in the cache.
 */
public class UnitOfWorkConformDuplicateTest extends UnitOfWorkConformTest {
    public UnitOfWorkConformDuplicateTest(ReadAllQuery query, int size) {
        super(query, size);
    }

    protected void setup() {
        uow = getSession().acquireUnitOfWork();
        uow.readAllObjects(Employee.class);
        Employee newEmployee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        newEmployee.setId(new java.math.BigDecimal(123456789));
        newEmployee.setFirstName("Bob");
        uow.registerObject(newEmployee);
    }
}
