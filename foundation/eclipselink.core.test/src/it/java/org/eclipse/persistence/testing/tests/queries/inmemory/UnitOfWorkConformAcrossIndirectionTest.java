/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

import java.util.Vector;

/**
 * Test that simple expressions can be conformed across indirection.
 * bug 2637555
 */
public class UnitOfWorkConformAcrossIndirectionTest extends TestCase {
    protected int expected;
    protected UnitOfWork uow;
    protected Vector result;
    protected Employee readObjectResult;
    protected ReadAllQuery query;
    protected ReadObjectQuery readObjectQuery;

    public UnitOfWorkConformAcrossIndirectionTest() {
        this.expected = 2;
        this.query = null;
        setDescription("Test that simple expressions can be conformed across indirection.");
    }

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow.release();
    }

    @Override
    protected void setup() {
        beginTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        uow = getSession().acquireUnitOfWork();
        org.eclipse.persistence.tools.schemaframework.PopulationManager manager = org.eclipse.persistence.tools.schemaframework.PopulationManager.getDefaultManager();
        Employee example = (Employee)manager.getObject(Employee.class, "0001");
        Employee example2 = (Employee)manager.getObject(Employee.class, "0002");

        // The point of this test is that there must be an object in cache with an
        // untriggered valueholder, and which appears first.
        uow.readAllObjects(Employee.class);
        Employee clone = (Employee)uow.readObject(example2);
        clone.getAddress().setCity(example.getAddress().getCity());
        clone.getAddress().setProvince("Jeju");
        this.query = new ReadAllQuery(Employee.class);
        this.query.conformResultsInUnitOfWork();
        this.query.setSelectionCriteria(new ExpressionBuilder(Employee.class).get("address").get("city").equal(example.getAddress().getCity()));
        // Now must test this for read object queries too.
        this.readObjectQuery = new ReadObjectQuery(Employee.class);
        this.readObjectQuery.conformResultsInUnitOfWork();
        this.readObjectQuery.setSelectionCriteria(new ExpressionBuilder(Employee.class).get("address").get("province").equal("Jeju"));

    }

    @Override
    public void test() {
        this.result = (Vector)this.uow.executeQuery(this.query);
        this.readObjectResult = (Employee)this.uow.executeQuery(this.readObjectQuery);
    }

    @Override
    protected void verify() {
        if (this.result.size() != this.expected) {
            throw new TestErrorException("Expecting: " + this.expected + " retrieved: " + this.result.size());
        }
        if (this.readObjectResult == null) {
            throw new TestErrorException("Fix was not ported to read object queries aswell.");
        }
    }
}
