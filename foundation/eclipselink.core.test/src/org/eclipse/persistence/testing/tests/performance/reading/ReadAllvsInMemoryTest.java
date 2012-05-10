/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read all vs in-memory read all.
 */
public class ReadAllvsInMemoryTest extends PerformanceComparisonTestCase {
    protected List allObjects;

    public ReadAllvsInMemoryTest() {
        setDescription("This test compares the performance of read all vs in-memory read all.");
        addReadAllInMemoryTest();
    }

    /**
     * Fill the cache.
     */
    public void setup() {
        allObjects = getSession().readAllObjects(Employee.class);
    }

    /**
     * Read all employees with salary > 0.
     */
    public void test() throws Exception {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        query.setSelectionCriteria(builder.get("salary").greaterThan(0));
        List results = (List)getSession().executeQuery(query);
    }

    /**
     * Read all employees in-memory.
     */
    public void addReadAllInMemoryTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                ExpressionBuilder builder = new ExpressionBuilder();
                query.setSelectionCriteria(builder.get("salary").greaterThan(0));
                query.checkCacheOnly();
                List results = (List)getSession().executeQuery(query);
            }
        };
        test.setName("ReadAllInMemoryTest");
        test.setAllowableDecrease(2000);
        addTest(test);
    }
}
