/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/17/2010-2.2 Tom Ware
//       - 321041 ConcurrentModificationException on getFromIdentityMap() fix
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Bug# 321041 - EclipseLink throws ConcurrentModificationException when triggering lazy load from conforming query
 */
public class TriggerValueHoldersSelfReferencingOneToOneTest extends TestCase {

    /** Current transaction. */
    protected UnitOfWork uow = null;

    /**
     * Test setup.
     * Open transaction, initialize query and initialize cache with 1st query executions.
     */
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        // Preload the UnitOfWork identity map with 2 Employees which have different managers
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression queryExp = emp.get("firstName").equal("Charles").and(emp.get("lastName").equal("Chanley"));
        uow.readObject(Employee.class, queryExp);
        emp = new ExpressionBuilder();
        queryExp = emp.get("firstName").equal("Marcus").and(emp.get("lastName").equal("Saunders"));
        uow.readObject(Employee.class, queryExp);
    }

    /**
     * Test cleanup.
     * Release transaction and reset cache.
     */
    public void reset() {
        uow.release();
        uow = null;
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    /**
     * Test case.
     * Verify that {@code ConcurrentModificationException} is not thrown from {@code IdentityMapManager}
     * during 2nd queries execution when objects are already in the cache.
     */
    public void test() {
        // We query for both Employees here because it is impossible to tell which order
        // keys will be returned from the identity map in
        // This bug only occurs when the first key returned is non-conforming and
        // a future key must be looked up
        ExpressionBuilder emp = new ExpressionBuilder();
        Expression queryExp = emp.get("manager").get("firstName").equal("Bob");
        ReadObjectQuery query = new ReadObjectQuery(Employee.class, queryExp);
        query.conformResultsInUnitOfWork();
        query.getInMemoryQueryIndirectionPolicy().triggerIndirection();
        Employee bob = (Employee)uow.executeQuery(query);
        assertNotNull(bob);
        assertEquals("Bob", bob.getManager().getFirstName());
        emp = new ExpressionBuilder();
        queryExp = emp.get("manager").get("firstName").equal("John");
        query = new ReadObjectQuery(Employee.class, queryExp);
        query.conformResultsInUnitOfWork();
        query.getInMemoryQueryIndirectionPolicy().triggerIndirection();
        Employee john = (Employee)uow.executeQuery(query);
        assertNotNull(john);
        assertEquals("John", john.getManager().getFirstName());
    }

}
