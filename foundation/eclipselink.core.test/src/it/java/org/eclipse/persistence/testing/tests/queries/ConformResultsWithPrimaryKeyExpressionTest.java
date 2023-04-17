/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * <b>Purpose</b>: Test for bug 2782991: Find by Primary Key with
 * conforming does Linear Search.
 * <b>Responsibilities</b>:
 * <ul><li>Find a way to tell if a cache check takes constant or linear time
 * relative to the size of the cache.
 * <li>For most reads by primary key the cache check should take constant time.
 * </ul>
 * <p>Cases:
 * <ul><li>Existing Object: Should take constant time.
 * <li>New Object (new objects not cached): Should take time relative to the
 * size of the new objects cache only.
 * <li>New Object (new objects cached): Not tested.  Should take constant
 * time.
 * <li>Deleted Object: Should take constant time.  Even though the query should
 * return null, the cache hit should succeed.
 * <li>Not Existing Object: Should take constant time, for if can't find by
 * exact primary key the object is not there.
 * </ul>
 * In the case of find by inexact primary key, the new and not existing cases
 * take a linear number of extra calls.
 * <p><b>Future testing:</b>
 * <ul><li>Set conforming on the descriptor also (regression).
 * <li>Set conforming on the descriptor only (regression).
 * <li>Test all the other non conforming options, such as check cache by exact
 * primary key.
 * <li>Test case where no selection criteria is specified, and the first object
 * returned happens to be deleted.
 * <li>Test case where selection object is specified.  (See {@link ConformResultsWithSelectionObjectTest}).
 * <li>A true not existing case: the object does not exist on the database either.
 * </ul>
 * @author Stephen McRitchie
 * @since 9.0.4.0
 */
public class ConformResultsWithPrimaryKeyExpressionTest extends ConformResultsInUnitOfWorkTest {
    public static final int CASE_NEW = 0;
    public static final int CASE_DELETED = 1;
    public static final int CASE_EXISTING = 2;
    public static final int CASE_NOTEXISTING = 3;
    public final int testCase;
    public final boolean checkCacheByExactPrimaryKey;
    public int expectedGetIdCallCount;
    public int actualGetIdCallCount;
    Employee selectionObject;
    AttributeAccessor overwrittenAccessor;

    public ConformResultsWithPrimaryKeyExpressionTest(int testCase, boolean checkCacheByExactPrimaryKey) {
        this.testCase = testCase;
        this.checkCacheByExactPrimaryKey = checkCacheByExactPrimaryKey;
        String modifier = null;
        switch (testCase) {
        case CASE_NEW:
            modifier = "NEW";
            break;
        case CASE_DELETED:
            modifier = "DELETED";
            break;
        case CASE_EXISTING:
            modifier = "EXISTING";
            break;
        case CASE_NOTEXISTING:
            modifier = "NOTEXISTING";
            break;
        }
        if (shouldCheckCacheByExactPrimaryKey()) {
            setName("ConformResultsWithExactPrimaryKeyExpressionTest:" + modifier);
        } else {
            setName("ConformResultsWithInexactPrimaryKeyExpressionTest:" + modifier);
        }
    }

    @Override
    public void buildConformQuery() {
        conformedQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression exactPrimaryKeyExpression = null;
        if (!getSession().getPlatform().isOracle()) {
            exactPrimaryKeyExpression = emp.get("id").equal(selectionObject.getId());
        } else {
            exactPrimaryKeyExpression = emp.get("id").equal("" + selectionObject.getId());
        }
        if (shouldCheckCacheByExactPrimaryKey()) {
            conformedQuery.setSelectionCriteria(exactPrimaryKeyExpression);
        } else {
            Expression inexactPrimaryKeyExpression = exactPrimaryKeyExpression.and(emp.get("firstName").equal(selectionObject.getFirstName()));
            conformedQuery.setSelectionCriteria(inexactPrimaryKeyExpression);
        }
        conformedQuery.conformResultsInUnitOfWork();
    }

    public static Vector buildTests() {
        Vector tests = new Vector(4);
        tests.add(new ConformResultsWithPrimaryKeyExpressionTest(CASE_DELETED, true));
        tests.add(new ConformResultsWithPrimaryKeyExpressionTest(CASE_EXISTING, true));
        tests.add(new ConformResultsWithPrimaryKeyExpressionTest(CASE_NEW, true));
        tests.add(new ConformResultsWithPrimaryKeyExpressionTest(CASE_NOTEXISTING, true));
        tests.add(new ConformResultsWithPrimaryKeyExpressionTest(CASE_DELETED, false));
        tests.add(new ConformResultsWithPrimaryKeyExpressionTest(CASE_EXISTING, false));
        tests.add(new ConformResultsWithPrimaryKeyExpressionTest(CASE_NEW, false));
        tests.add(new ConformResultsWithPrimaryKeyExpressionTest(CASE_NOTEXISTING, false));
        return tests;
    }

    protected Employee findWorstCaseEmployee() {
        Vector searchOrder = unitOfWork.getIdentityMapAccessor().getAllFromIdentityMap(null, Employee.class, null, null);
        return (Employee)searchOrder.lastElement();
    }

    /**
     * prepareTest method comment.
     */
    @Override
    public void prepareTest() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        Vector employees = (Vector)getSession().executeQuery(query);
        for (int i = 0; i < (employees.size() - 1); i++) {
            unitOfWork.registerExistingObject(employees.elementAt(i));
        }
        Employee unregisteredEmployee = (Employee)employees.elementAt(employees.size() - 1);

        // Further tests the not exists case when the query goes to the session.
        getSession().getIdentityMapAccessor().removeFromIdentityMap(unregisteredEmployee);
        int n = employees.size() - 1;

        Employee newEmployee = new Employee();
        newEmployee.setFirstName("Bobert");
        newEmployee.setLastName("Schmit");
        newEmployee.setId(new java.math.BigDecimal(45));
        unitOfWork.registerNewObject(newEmployee);

        Employee registeredEmployee = findWorstCaseEmployee();

        switch (testCase) {
        case CASE_NEW: {
            selectionObject = newEmployee;
            if (shouldCheckCacheByExactPrimaryKey()) {
                expectedGetIdCallCount = 0;
            } else {
                expectedGetIdCallCount = n + 1;
            }
            break;
        }
        case CASE_DELETED: {
            selectionObject = registeredEmployee;
            unitOfWork.deleteObject(selectionObject);
            if (shouldCheckCacheByExactPrimaryKey()) {
                expectedGetIdCallCount = 0;
            } else {
                // S.M changed from 3 - 4 from session read refactoring.  In the
                // old code, we would not go to the database if we got a
                // cache hit on the session cache.  Now we do. Gray area
                expectedGetIdCallCount = 3;
            }
            break;
        }
        case CASE_EXISTING: {
            selectionObject = registeredEmployee;
            if (shouldCheckCacheByExactPrimaryKey()) {
                expectedGetIdCallCount = 0;
            } else {
                expectedGetIdCallCount = 1;
            }
            break;
        }
        case CASE_NOTEXISTING: {
            selectionObject = unregisteredEmployee;
            if (shouldCheckCacheByExactPrimaryKey()) {
                // S.M. This went from 5 calls to 4, which is good.
                // When checking the one new object + registration +
                // building clone + building backup clone.
                expectedGetIdCallCount = 2;
            } else {
                expectedGetIdCallCount = n + 4;
            }
            break;
        }
        }
    }

    @Override
    public void setup() {
        // Change how the primary key attribute 'id' in Employee is accessed.
        // Now everytime TopLink extracts the primary key from an Employee object it
        // will call Employee.getId() reflectively, which will update a count.
        DatabaseMapping mapping = getSession().getDescriptor(Employee.class).getMappingForAttributeName("id");
        overwrittenAccessor = mapping.getAttributeAccessor();
        mapping.setGetMethodName("getId");
        mapping.setSetMethodName("setId");
        mapping.getAttributeAccessor().initializeAttributes(Employee.class);
        super.setup();
    }

    @Override
    public void reset() {
        DatabaseMapping mapping = getSession().getDescriptor(Employee.class).getMappingForAttributeName("id");
        mapping.setAttributeAccessor(overwrittenAccessor);
        super.reset();
    }

    /**
     * Override test to count the calls to Employee.getId just for the query.
     */
    @Override
    public void test() {
        int initialCount = Employee.getGetIdCallCount();
        result = unitOfWork.executeQuery(conformedQuery);
        actualGetIdCallCount = Employee.getGetIdCallCount() - initialCount;
        unitOfWork.release();
    }

    /**
     * verify method comment.
     */
    @Override
    public void verify() {
        if ((result == null) && (testCase != CASE_DELETED)) {
            throw new TestErrorException("object existed in unit of work but not returned in query.");
        }
        if ((result != null) && (testCase == CASE_DELETED)) {
            throw new TestErrorException("object was deleted in unit of work but returned in query.");
        }
        if (actualGetIdCallCount != expectedGetIdCallCount) {
            throw new TestErrorException("The performance of find by primary key has changed.  Expected calls to getId: " + expectedGetIdCallCount + ".  Actual calls: " + actualGetIdCallCount + ".  As long as the algorithmic complexity does not change (linear/constant) this should be ok.");
        }
    }

    public boolean shouldCheckCacheByExactPrimaryKey() {
        return checkCacheByExactPrimaryKey;
    }
}
