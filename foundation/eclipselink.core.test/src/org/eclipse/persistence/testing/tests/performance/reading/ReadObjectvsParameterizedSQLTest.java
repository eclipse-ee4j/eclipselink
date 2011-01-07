/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read object vs using parameterized SQL.
 */
public class ReadObjectvsParameterizedSQLTest extends PerformanceComparisonTestCase {
    protected Employee employee;
    protected ReadObjectQuery bindQuery;
    protected ReadObjectQuery query;

    public ReadObjectvsParameterizedSQLTest() {
        setDescription("This test compares the performance of read object vs using parameterized SQL.");
        addReadObjectSameQueryTest();
        addReadObjectParameterizedSQLTest();
        addReadObjectParameterizedSQLSameQueryTest();
        addReadObjectPreparedStatementTest();
        addReadObjectDynamicTest();
    }

    /**
     * Find any employee.
     */
    public void setup() {
        employee = (Employee)getSession().readObject(Employee.class);

        bindQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        bindQuery.setSelectionCriteria(builder.get("firstName").equal(builder.getParameter("firstName")).and(builder.get("lastName").equal(builder.getParameter("lastName"))));
        bindQuery.addArgument("firstName");
        bindQuery.addArgument("lastName");
        bindQuery.bindAllParameters();
        bindQuery.cacheStatement();

        query = new ReadObjectQuery(Employee.class);
        builder = new ExpressionBuilder();
        query.setSelectionCriteria(builder.get("firstName").equal(builder.getParameter("firstName")).and(builder.get("lastName").equal(builder.getParameter("lastName"))));
        query.addArgument("firstName");
        query.addArgument("lastName");
    }

    /**
     * Read object.
     */
    public void test() throws Exception {
        getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        query.setSelectionCriteria(builder.get("firstName").equal(employee.getFirstName()).and(builder.get("lastName").equal(employee.getLastName())));
        getSession().executeQuery(query);
    }

    /**
     * Read object using same query.
     */
    public void addReadObjectSameQueryTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
                Vector arguments = new Vector(2);
                arguments.add(employee.getFirstName());
                arguments.add(employee.getLastName());
                getSession().executeQuery(query, arguments);
            }
        };
        test.setName("ReadObjectSameQueryTest");
        //test.setAllowableDecrease(5); - should be about same now because of expression parse cache
        addTest(test);
    }

    /**
     * Read object using param SQL.
     */
    public void addReadObjectParameterizedSQLTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
                ReadObjectQuery query = new ReadObjectQuery(Employee.class);
                ExpressionBuilder builder = new ExpressionBuilder();
                query.setSelectionCriteria(builder.get("firstName").equal(employee.getFirstName()).and(builder.get("lastName").equal(employee.getLastName())));
                query.bindAllParameters();
                query.cacheStatement();
                getSession().executeQuery(query);
            }
        };
        test.setName("ReadObjectParameterizedSQLTest");
        test.setAllowableDecrease(30);
        addTest(test);
    }

    /**
     * Read object using param SQL using same query.
     */
    public void addReadObjectParameterizedSQLSameQueryTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
                Vector arguments = new Vector(2);
                arguments.add(employee.getFirstName());
                arguments.add(employee.getLastName());
                getSession().executeQuery(bindQuery, arguments);
            }
        };
        test.setName("ReadObjectParameterizedSQLSameQueryTest");
        test.setAllowableDecrease(40);
        addTest(test);
    }

    /**
     * Read object using old prepared statements.
     */
    public void addReadObjectPreparedStatementTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                DatabaseAccessor.shouldUseDynamicStatements = false;
                getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
                ReadObjectQuery query = new ReadObjectQuery(Employee.class);
                query.dontBindAllParameters();
                ExpressionBuilder builder = new ExpressionBuilder();
                query.setSelectionCriteria(builder.get("firstName").equal(employee.getFirstName()).and(builder.get("lastName").equal(employee.getLastName())));
                getSession().executeQuery(query);
                DatabaseAccessor.shouldUseDynamicStatements = true;
            }
        };
        test.setName("ReadObjectPreparedStatementTest");
        test.setAllowableDecrease(-30);
        addTest(test);
    }

    /**
     * Read object using binding only.
     */
    public void addReadObjectDynamicTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                getSession().getIdentityMapAccessor().removeFromIdentityMap(employee);
                ReadObjectQuery query = new ReadObjectQuery(Employee.class);
                ExpressionBuilder builder = new ExpressionBuilder();
                query.setSelectionCriteria(builder.get("firstName").equal(employee.getFirstName()).and(builder.get("lastName").equal(employee.getLastName())));
                query.dontBindAllParameters();
                getSession().executeQuery(query);
            }
        };
        test.setName("ReadObjectDynamicTest");
        test.setAllowableDecrease(-20);
        addTest(test);
    }
}
