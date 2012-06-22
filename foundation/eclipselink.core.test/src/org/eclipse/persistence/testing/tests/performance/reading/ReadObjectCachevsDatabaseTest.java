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
 * This test compares the performance of cache hits and the various in-memory querying options.
 */
public class ReadObjectCachevsDatabaseTest extends PerformanceComparisonTestCase {
    protected List allObjects;
    protected Employee employee;
    protected ReadObjectQuery idQuery;
    protected ReadObjectQuery cacheOnlyNameQuery;
    protected ReadObjectQuery cacheDatabaseNameQuery;

    public ReadObjectCachevsDatabaseTest() {
        setDescription("This test compares the performance of cache hits and the various in-memory querying options.");
        addCachedReadObjectTest();
        addInMemoryReadObjectTest();
        addCacheThenDatabaseMissReadObjectTest();
    }

    /**
     * Find any employee.
     */
    public void setup() {
        Expression expression = new ExpressionBuilder().get("firstName").equal("Bob");
        employee = (Employee)getSession().readObject(Employee.class, expression);
        // Fully load the cache.
        allObjects = getSession().readAllObjects(Employee.class);

        idQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        idQuery.setSelectionCriteria(builder.get("id").equal(employee.getId()));

        cacheOnlyNameQuery = new ReadObjectQuery(Employee.class);
        builder = new ExpressionBuilder();
        cacheOnlyNameQuery.setSelectionCriteria(builder.get("firstName").equal(employee.getFirstName()).and(builder.get("lastName").equal(employee.getLastName())));
        cacheOnlyNameQuery.checkCacheOnly();

        cacheDatabaseNameQuery = new ReadObjectQuery(Employee.class);
        builder = new ExpressionBuilder();
        cacheDatabaseNameQuery.setSelectionCriteria(builder.get("firstName").equal(employee.getFirstName()).and(builder.get("lastName").equal(employee.getLastName())));
        cacheDatabaseNameQuery.checkCacheThenDatabase();
    }

    /**
     * Read employee and clear the cache, test database read.
     */
    public void test() throws Exception {
        getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
        getSession().executeQuery(idQuery);
    }

    /**
     * Read by primary key and get a cache hit.
     */
    public void addCachedReadObjectTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().executeQuery(idQuery);
            }
        };
        test.setName("CachedReadObjectTest");
        test.setAllowableDecrease(10000);
        addTest(test);
    }

    /**
     * Read by first name in memory.
     */
    public void addInMemoryReadObjectTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().executeQuery(cacheOnlyNameQuery);
            }
        };
        test.setName("InMemoryReadObjectTest");
        test.setAllowableDecrease(1000);
        addTest(test);
    }

    /**
     * Read by first name in memory then hit the database.
     */
    public void addCacheThenDatabaseMissReadObjectTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
                getSession().executeQuery(cacheDatabaseNameQuery);
            }
        };
        test.setName("CacheThenDatabaseMissReadObjectTest");
        test.setAllowableDecrease(-40);
        addTest(test);
    }
}
