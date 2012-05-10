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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.tests.performance.PerformanceTest;

/**
 * This tests the performance of named queries.
 * Its purpose is to compare the test result with previous release/label results.
 * It also provides a useful test for profiling performance.
 */
public class ReadObjectNamedQueryEmployeeTest extends PerformanceTest {
    public ReadObjectNamedQueryEmployeeTest() {
        setDescription("This tests the performance of named queries.");
    }

    public void setup() {
        super.setup();
        // Fully load the cache.
        allObjects = getSession().readAllObjects(Employee.class);

        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setName("findByNameCity");
        ExpressionBuilder employee = new ExpressionBuilder();
        query.setSelectionCriteria(employee.get("firstName").equal(employee.getParameter("firstName")).and(employee.get("address").get("city").equal(employee.getParameter("city"))));
        query.addArgument("firstName");
        query.addArgument("city");
        getSession().getDescriptor(Employee.class).getQueryManager().addQuery("findByNameCity", query);
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        super.test();
        Employee result = (Employee)getSession().executeQuery("findByNameCity", Employee.class, "Brendan", "Nepean");
    }
}
