/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Bug 320764 - Conforming query returns first object in a uow when it depends on an untriggered lazy attribute
 * Tests a UnitOfWork conforming ReadObjectQuery with parameterized criteria
 */
public class UnitOfWorkConformReadObjectWithCriteriaTest extends TestCase {

    protected UnitOfWork uow;
    protected ObjectLevelReadQuery queryToExecute;
    protected List<Employee> employees;

    public UnitOfWorkConformReadObjectWithCriteriaTest() {
        super();
        setDescription("UnitOfWorkConformReadObjectWithCriteriaTest");
    }

    public void setup() {
        this.employees = getSession().readAllObjects(Employee.class, new ExpressionBuilder().get("manager").notNull());
        for (Employee employee : employees) {
            assertNotNull(employee);
            assertNotNull(employee.getManager());
        }

        // Set up conforming query to execute
        ExpressionBuilder builder = new ExpressionBuilder();
        this.queryToExecute = new ReadObjectQuery(Employee.class, builder);
        Expression expression = builder.get("lastName").equal(builder.getParameter("employeeLastName"));
        expression = expression.and(builder.get("manager").get("id").equal(builder.getParameter("managerId")));
        this.queryToExecute.setSelectionCriteria(expression);
        this.queryToExecute.conformResultsInUnitOfWork();
        this.queryToExecute.addArgument("managerId", BigDecimal.class);
        this.queryToExecute.addArgument("employeeLastName", String.class);

        // start the test with an empty cache
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        // use the same uow for conforming queries
        this.uow = getSession().acquireUnitOfWork();
    }

    public void test() {
        for (Employee sampleEmployee : this.employees) {
            Employee sampleManager = (Employee)sampleEmployee.getManager();

            // execute the conforming query
            Vector args = new Vector();
            args.add(sampleEmployee.getManager().getId());
            args.add(sampleEmployee.getLastName());

            Employee employeeResult = (Employee) uow.executeQuery(this.queryToExecute, args);

            assertNotNull("Employee should not be null", employeeResult);
            Employee managerResult = (Employee) employeeResult.getManager();
            assertNotNull("Manager should not be null", managerResult);

            assertEquals("The returned Employee should have the same id as the sample Employee",
                    sampleEmployee.getId(), employeeResult.getId());
            assertEquals("The manager of the returned Employee should have the same id as the manager of the sample Employee",
                    sampleManager.getId(), managerResult.getId());
        }
    }

    public void reset() {
        if (this.uow != null) {
            this.uow.release();
            this.uow = null;
            this.queryToExecute = null;
            this.employees = null;
        }
    }

}
