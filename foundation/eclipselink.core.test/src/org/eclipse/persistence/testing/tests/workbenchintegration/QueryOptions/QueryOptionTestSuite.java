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
package org.eclipse.persistence.testing.tests.workbenchintegration.QueryOptions;

import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.Project;


/**
 * Test that deployment XML and deployment class work with named queries.
 */
public class QueryOptionTestSuite extends TestSuite {
    public QueryOptionTestSuite() {
        setDescription("This suite tests all of the functionality of the query options.");
    }

    public void addTests() {
        addTest(new RefreshIdentityMapResultsTest());
        addTest(new QueryTimeOutTest());
        addTest(new MaxRowsTest());
        addTest(new FirstResultTest());
        addTest(new MemoryQueryReturnConfirmedTest());
        addTest(new MemoryQueryReturnNotConfirmedTest());
        addTest(new MemoryQueryThrowExceptionTest());
        addTest(new MemoryQueryTriggerIndirectionTest());
        addTest(new DoNotUseDistinctTest());
        addTest(new UseDistinctTest());
        addTest(new ShouldPrepareTest());
        addTest(new QueryManagerTimeoutTest());
        addTest(buildReadOnlyTest());
        addTest(buildJoinSubclassesTest());
    }

    /**
     * Test the read-only query option.
     */
    public TestCase buildReadOnlyTest() {
        TestCase test = new TestCase() {
                public void test() {
                    UnitOfWork uow = getSession().acquireUnitOfWork();
                    Employee employee = (Employee)uow.executeQuery("readOnlyQuery", Employee.class);
                    if (employee != getSession().readObject(employee)) {
                        throwError("Read-only option not used, employee registered in the unit of work.");
                    }
                }
            };
        test.setName("ReadOnlyTest");
        test.setDescription("Test the read-only query option in named queries.");
        return test;
    }

    /**
     * Test the join-subclasses query option.
     */
    public TestCase buildJoinSubclassesTest() {
        TestCase test = new TestCase() {
                public void test() {
                    UnitOfWork uow = getSession().acquireUnitOfWork();
                    Cursor cursor = (Cursor)uow.executeQuery("joinSubclassesQuery", Project.class);
                    cursor.close();
                }
            };
        test.setName("JoinSubclassesTest");
        test.setDescription("Test the read-only query option in named queries.");
        return test;
    }
}
