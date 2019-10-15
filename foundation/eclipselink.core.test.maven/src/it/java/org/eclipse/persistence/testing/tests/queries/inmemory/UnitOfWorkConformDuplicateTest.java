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
