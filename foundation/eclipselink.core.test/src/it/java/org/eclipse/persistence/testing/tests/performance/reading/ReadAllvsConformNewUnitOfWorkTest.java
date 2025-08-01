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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.PerformanceComparisonTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.performance.toplink.Employee;

import java.util.List;

/**
 * This test compares the performance of read all in unit of work vs conform read all.
 */
public class ReadAllvsConformNewUnitOfWorkTest extends PerformanceComparisonTestCase {
    protected List allObjects;

    public ReadAllvsConformNewUnitOfWorkTest() {
        setDescription("This test compares the performance of read all in unit of work vs conform read all.");
        addReadAllConformTest();
    }

    /**
     * Fill cache.
     */
    @Override
    public void setup() {
        allObjects = getSession().readAllObjects(Employee.class);
    }

    /**
     * Read all employees with salary {@literal >} 0.
     */
    @Override
    public void test() throws Exception {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        query.setSelectionCriteria(builder.get("salary").greaterThan(0));
        UnitOfWork uow = getSession().acquireUnitOfWork();
        List results = (List)uow.executeQuery(query);
    }

    /**
     * Read all employees in-memory.
     */
    public void addReadAllConformTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            @Override
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                ExpressionBuilder builder = new ExpressionBuilder();
                query.setSelectionCriteria(builder.get("salary").greaterThan(0));
                query.conformResultsInUnitOfWork();
                UnitOfWork uow = getSession().acquireUnitOfWork();
                List results = (List)uow.executeQuery(query);
            }
        };
        test.setName("ReadAllConformTest");
        test.setAllowableDecrease(-5);
        addTest(test);
    }

    /**
     * Throw a warning until this CR is fixed.
     */
    @Override
    public void verify() {
        try {
            super.verify();
        } catch (TestErrorException slow) {
            throw new TestWarningException("Conforming in a new unit of work should not conform known bug 3602166");
        }
    }
}
