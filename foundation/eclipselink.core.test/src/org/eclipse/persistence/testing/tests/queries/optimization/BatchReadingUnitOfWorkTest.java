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
package org.eclipse.persistence.testing.tests.queries.optimization;

import java.util.*;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

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

    public void test() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ReadAllQuery query = new ReadAllQuery();
        query.setBatchFetchType(batchType);
        query.setReferenceClass(Employee.class);
        query.addBatchReadAttribute("address");
        Vector employees = (Vector)uow.executeQuery(query);
        ((Employee)employees.firstElement()).getAddress().getCity();
        uow.commit(); // no changes so rollback not required.
    }
}
