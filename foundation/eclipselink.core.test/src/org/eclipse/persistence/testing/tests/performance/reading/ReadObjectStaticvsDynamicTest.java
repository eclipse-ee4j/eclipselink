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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance between having a static (cached SQL) read-object-query and not.
 */
public class ReadObjectStaticvsDynamicTest extends PerformanceComparisonTestCase {
    protected static int ITERATIONS = 20;
    protected Employee employee;

    public ReadObjectStaticvsDynamicTest() {
        setName("ReadObjectStaticvsDynamicTest");
        setDescription("Compares the performance between having a static (cached SQL) read-object-query and not.");
        addStaticReadObjectTest();
        addParameterizedDynamicReadObjectTest();
        addParameterizedReadObjectTest();
    }

    /**
     * Find any employee.
     */
    public void setup() {
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getSession().readObject(Employee.class, expression);
    }

    /**
     * Read employee and clear the cache.
     * Clear the static read-object query first (use to be default).
     */
    public void test() throws Exception {
        ReadObjectQuery query = getSession().getDescriptor(Employee.class).getQueryManager().getReadObjectQuery();
        getSession().getDescriptor(Employee.class).getQueryManager().setReadObjectQuery(null);
        if (query == null) {
            throw new TestErrorException("Query not static");
        }
        for (int index = 0; index < ITERATIONS; index++) {
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
            getSession().readObject(employee);
        }
        getSession().getDescriptor(Employee.class).getQueryManager().setReadObjectQuery(query);
    }

    /**
     * Read employee and clear the cache.
     * Uses the static read-object query in the descriptor to avoid generating SQL every time (is now default).
     */
    public void addStaticReadObjectTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                for (int index = 0; index < ITERATIONS; index++) {
                    getSession().getIdentityMapAccessor().initializeIdentityMaps();
                    getSession().readObject(employee);
                }
            }
        };
        test.setName("StaticReadObjectTest");
        test.setAllowableDecrease(5);
        addTest(test);
    }

    /**
     * Read employee and clear the cache.
     * Assume default in static read-object.
     */
    public void addParameterizedDynamicReadObjectTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadObjectQuery readQuery = getSession().getDescriptor(Employee.class).getQueryManager().getReadObjectQuery();
                getSession().getDescriptor(Employee.class).getQueryManager().setReadObjectQuery(null);
                for (int index = 0; index < ITERATIONS; index++) {
                    getSession().getIdentityMapAccessor().initializeIdentityMaps();
                    ReadObjectQuery query = new ReadObjectQuery(Employee.class);
                    query.setSelectionObject(employee);
                    query.bindAllParameters();
                    query.cacheStatement();
                    getSession().executeQuery(query);
                }
                getSession().getDescriptor(Employee.class).getQueryManager().setReadObjectQuery(readQuery);
            }
        };
        test.setName("ParameterizedDynamicReadObjectTest");
        test.setAllowableDecrease(20);
        addTest(test);
    }

    /**
     * Read employee and clear the cache.
     * Set static query to be parameterized.
     */
    public void addParameterizedReadObjectTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadObjectQuery readQuery = getSession().getDescriptor(Employee.class).getQueryManager().getReadObjectQuery();
                //readQuery.bindAllParameters();
                readQuery.cacheStatement();
                for (int index = 0; index < ITERATIONS; index++) {
                    getSession().getIdentityMapAccessor().initializeIdentityMaps();
                    ReadObjectQuery query = new ReadObjectQuery(Employee.class);
                    query.setSelectionObject(employee);
                    getSession().executeQuery(query);
                }
                //readQuery.dontBindAllParameters();
                readQuery.dontCacheStatement();
            }
        };
        test.setName("ParameterizedReadObjectTest");
        test.setAllowableDecrease(20);
        addTest(test);
    }
}
