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
package org.eclipse.persistence.testing.tests.transactions;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;

/**
 * This class tests if the rollback transaction feature
 * works for database updates.
 */
public class UpdateRollbackTransactionTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    Employee employee;
    Expression searchExpression;
    String oldFirstName;
    String oldLastName;

    public UpdateRollbackTransactionTest() {
        super();
        employee = createEmployee();
        searchExpression = createSearchExpression(employee.getFirstName(), employee.getLastName());
    }

    private Employee createEmployee() {
        // Create the example employee
        employee = (org.eclipse.persistence.testing.models.employee.domain.Employee)new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator().basicEmployeeExample1();
        employee.setFirstName("Timugen");
        employee.setLastName("Singaera");
        employee.addResponsibility("Answer the phones.");

        oldFirstName = employee.getFirstName();
        oldLastName = employee.getLastName();

        return employee;
    }

    private Expression createSearchExpression(String firstName, String lastName) {
        // Create an expression to retreive the employee from the database
        ExpressionBuilder expressionBuilder = new ExpressionBuilder();
        Expression exp1;
        Expression exp2;
        Expression expression;

        exp1 = expressionBuilder.get("firstName").equal(firstName);
        exp2 = expressionBuilder.get("lastName").equal(lastName);

        return exp1.or(exp2);
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
        DatabaseSession session = getDatabaseSession();
        Employee databaseEmployee = (Employee)session.readObject(Employee.class, getSearchExpression());

        if (databaseEmployee != null) {
            session.deleteObject(databaseEmployee);
        }

        // Re-create the example employee and search expression
        employee = createEmployee();
        searchExpression = createSearchExpression(employee.getFirstName(), employee.getLastName());
    }

    protected void resetVerify() {
        Session session = getSession();

        Employee databaseEmployee = (Employee)session.readObject(Employee.class, getSearchExpression());

        if (databaseEmployee != null) {
            throw new TestErrorException("Employee object has not been removed from the database");
        }
    }

    protected void setup() {
        // Add an example employee to the database
        getDatabaseSession().insertObject(getEmployee());
    }

    protected void test() {
        DatabaseSession session = getDatabaseSession();

        // Read the object from the database 
        Employee databaseEmployee = (Employee)session.readObject(Employee.class, getSearchExpression());

        session.beginTransaction();
        databaseEmployee.setFirstName("Alex");
        databaseEmployee.setLastName("Runparts");
        session.updateObject(databaseEmployee);
        session.rollbackTransaction();
    }

    protected void verify() {
        Session session = getSession();

        // Read the employee from the database, search using the original first and last name. 
        Employee databaseEmployee = (Employee)session.refreshObject(getEmployee());

        // Verify that no update took place
        if (!(databaseEmployee.getFirstName().equals(oldFirstName) || databaseEmployee.getLastName().equals(oldLastName))) {
            throw new TestErrorException("Employee object should not have been updated after rollback transaction");
        }
    }
}
