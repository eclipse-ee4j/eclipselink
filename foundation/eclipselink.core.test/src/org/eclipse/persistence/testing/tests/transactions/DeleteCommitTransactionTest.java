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
package org.eclipse.persistence.testing.tests.transactions;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.expressions.*;

/**
 * This class tests if the commit transaction feature
 * works for database deletes.
 */
public class DeleteCommitTransactionTest extends AutoVerifyTestCase {
    Employee employee;
    Expression searchExpression;

    public DeleteCommitTransactionTest() {
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
        // Read the object from the database 
        Employee databaseEmployee = (Employee)getSession().readObject(Employee.class, getSearchExpression());

        // If the employee object IS in the database then there is a problem.
        if (databaseEmployee != null) {
            getDatabaseSession().deleteObject(getEmployee());
            throw new TestErrorException("The example employee object should have been deleted from the database.");
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
        getDatabaseSession().commitTransaction();
    }

    protected void verify() {
        Session session = getSession();

        // Read the object from the database 
        Employee databaseEmployee = (Employee)session.readObject(Employee.class, getSearchExpression());

        // If the employee object IS in the database then there is a problem.
        if (databaseEmployee != null) {
            throw new TestErrorException("Employee object should have been deleted after commit transaction");
        }
    }
}
