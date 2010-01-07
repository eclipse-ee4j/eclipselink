/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.performance.reading;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of conforming read-all queries in an unit of work.
 * This emulates a common CMP use case.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadAllUnitOfWorkConformingEmployeeTest extends PerformanceTest {
    public ReadAllUnitOfWorkConformingEmployeeTest() {
        setDescription("This tests the performance of conforming read-all queries in a unit of work.");
    }

    public void setup() {
        super.setup();
        // Fully load the cache.
        allObjects = getSession().readAllObjects(Employee.class);
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        super.test();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setSelectionCriteria(new ExpressionBuilder().get("firstName").equal("Brendan"));
        query.conformResultsInUnitOfWork();
        List employees = (List)uow.executeQuery(query);
        uow.commit();
    }
}
