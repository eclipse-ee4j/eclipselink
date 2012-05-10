/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.readonly;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * <p>
 * <b>Purpose</b>: Verify that declaring aggregate classes read-only has no effect if the source
 *    class is non-read-only.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Declare an aggregate class to be read-only.
 * <li> Make changes to the aggregate object.
 * <li> Write the source to the database and check that the changes were written to the database.
 * </ul>
 */
public class ReadOnlyClassAggregateTestCase extends TestCase {
    org.eclipse.persistence.testing.models.aggregate.Employee employee;

    public ReadOnlyClassAggregateTestCase() {
        super();
    }

    public void reset() {
        getSession().getProject().setDefaultReadOnlyClasses(new Vector());
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        beginTransaction();
        // Build a new Employee to be inserted into the database.
        employee = new org.eclipse.persistence.testing.models.aggregate.Employee();
        employee.setFirstName("Marc");
        employee.setLastName("Fullard");
        employee.setProjectDescription(org.eclipse.persistence.testing.models.aggregate.ProjectDescription.example1(employee));
        employee.setAddressDescription(org.eclipse.persistence.testing.models.aggregate.AddressDescription.example4());
    }

    protected void test() {
        // Try to insert the new employee when some of its aggregate parts are read-only.
        // Get the unit of work.
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.removeAllReadOnlyClasses();
        uow.addReadOnlyClass(org.eclipse.persistence.testing.models.aggregate.PeriodDescription.class);

        // Register the employee
        org.eclipse.persistence.testing.models.aggregate.Employee empClone = (org.eclipse.persistence.testing.models.aggregate.Employee)uow.registerObject(employee);

        // Commit the new employee.
        uow.commit();
    }

    protected void verify() {
        // Check that the employee was written completely despite the two aggregate 
        // classes being declared read-only.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        ExpressionBuilder expBuilder = new ExpressionBuilder();
        Expression exp = expBuilder.get("firstName").equal(employee.getFirstName()).and(expBuilder.get("lastName").equal(employee.getLastName()));
        org.eclipse.persistence.testing.models.aggregate.Employee dbEmployee = (org.eclipse.persistence.testing.models.aggregate.Employee)getSession().readObject(org.eclipse.persistence.testing.models.aggregate.Employee.class, exp);
        if (dbEmployee.getAddressDescription().getPeriodDescription() != null) {
            throw new TestErrorException("The insert of a non-read-only object with read-only aggregate objects failed.!");
        }
    }
}
