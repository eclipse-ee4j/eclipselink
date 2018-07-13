/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.transactions;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;

/**
 * This class tests if the rollback transaction feature
 * works for database deletes.
 */
public class DeleteRollbackTransactionTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    Employee employee;
    Expression searchExpression;

    public DeleteRollbackTransactionTest() {
        super();
        createEmployeeAndSearchExpression();
    }

    private void createEmployeeAndSearchExpression() {
        // Create the example employee
        employee = (org.eclipse.persistence.testing.models.employee.domain.Employee)new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator().basicEmployeeExample1();
        employee.setFirstName("Timugen");
        employee.setLastName("Singaera");
        employee.addResponsibility("Answer the phones.");

        // Create an expression to retreive the employee from the database
        ExpressionBuilder expressionBuilder = new ExpressionBuilder();
        Expression exp1;
        Expression exp2;
        Expression expression;

        exp1 = expressionBuilder.get("firstName").equal(employee.getFirstName());
        exp2 = expressionBuilder.get("lastName").equal(employee.getLastName());

        searchExpression = exp1.or(exp2);
    }

    public String getDescription() {
        return "This test verifies that the commit transaction feature works for database inserts.";
    }

    private Employee getEmployee() {
        return employee;
    }

    private Expression getSearchExpression() {
        return searchExpression;
    }

    public void reset() {
        // Check if there the employee object is in the database
        Employee databaseEmployee = (Employee)getSession().readObject(Employee.class, getSearchExpression());

        // If the employee object IS in the database then delete it.
        if (databaseEmployee != null) {
            getDatabaseSession().deleteObject(databaseEmployee);
        }
    }

    protected void resetVerify() {
        Session session = getSession();

        // Read the object from the database
        Employee databaseEmployee = (Employee)session.readObject(Employee.class, getSearchExpression());

        // If the employee object IS in the database then there is a problem.
        if (databaseEmployee != null) {
            throw new TestErrorException("The example employee object should have been deleted from the database.");
        }
    }

    protected void setup() {
        // Add an example employee to the database
        getDatabaseSession().insertObject(getEmployee());
    }

    protected void test() {
        getDatabaseSession().beginTransaction();
        getDatabaseSession().deleteObject(getEmployee());
        getDatabaseSession().rollbackTransaction();
    }

    protected void verify() {
        Session session = getSession();

        // Read the object from the database
        Employee databaseEmployee = (Employee)session.readObject(Employee.class, getSearchExpression());

        // If the employee object IS NOT in the database then there is a problem.
        if (databaseEmployee == null) {
            throw new TestErrorException("Employee object should not have been deleted after transaction rollback");
        }
    }
}
