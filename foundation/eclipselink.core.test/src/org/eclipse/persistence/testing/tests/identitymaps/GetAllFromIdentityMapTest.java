/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     12/14/2017-3.0 Tomas Kraus
//       - 522635: ConcurrentModificationException when triggering lazy load from conforming query
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Bug# 522635 - Verify that {@code ConcurrentModificationException} is not thrown from {@code IdentityMapManager}
 * when {@code getAllFromIdentityMap} method is being called twice.
 */
public class GetAllFromIdentityMapTest extends TestCase {

    /** Current transaction. */
    private UnitOfWork uow = null;

    /** Query instance. */
    private ReadAllQuery query;

    /**
     * Create an instance of {@link ReadAllQuery} and initialize it.
     *
     * @param c entity class
     * @return new instance of initialized {@link ReadAllQuery}
     */
    private static ReadAllQuery newReadAllQuery(Class<?> c) {
        final ReadAllQuery query = new ReadAllQuery(c);
        query.conformResultsInUnitOfWork();
        query.setInMemoryQueryIndirectionPolicy(new InMemoryQueryIndirectionPolicy(InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION));
        return query;
    }

    /**
     * Test setup.
     * Open transaction, initialize query and initialize cache with 1st query execution.
     */
    public void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        query = newReadAllQuery(Employee.class);
        ExpressionBuilder emp = new ExpressionBuilder();
        query.setSelectionCriteria(emp.get("manager").get("firstName").equal("Bob"));
        uow.executeQuery(query);
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
     * during 2nd query execution when objects are already in the cache.
     */
    public void test() throws Throwable {
        Vector<Employee> employees = (Vector<Employee>)uow.executeQuery(query);
        assertFalse(employees.isEmpty());
        for (Employee employee : employees) {
            assertEquals("Bob", employee.getManager().getFirstName());
        }
    }

}
