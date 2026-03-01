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
package org.eclipse.persistence.testing.tests.queries.optimization;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Test query opt in uow.
 */
public class BatchReadingUnitOfWorkTest extends TestCase {
    BatchFetchType batchType;

    public BatchReadingUnitOfWorkTest(BatchFetchType batchType) {
        setDescription("This test verifies that batch reading works correctly in a unit of work.");
        this.batchType = batchType;
        setName(getName() + batchType);
    }

    @Override
    public void test() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ReadAllQuery query = new ReadAllQuery();
        query.setBatchFetchType(batchType);
        query.setReferenceClass(Employee.class);
        query.addBatchReadAttribute("address");
        Vector employees = (Vector)uow.executeQuery(query);
        ((Employee)employees.get(0)).getAddress().getCity();
        uow.commit(); // no changes so rollback not required.
    }
}
