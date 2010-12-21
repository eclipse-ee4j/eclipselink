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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * <p>
 * <b>Purpose</b>: This test checks if TopLink raises exceptions with incorrect operations in UOW
 *
 *     <p>
 * <b>Motivation </b>: This test was written to fix a bug. While in a unit of work, if an object was
 *                                  assigned from the UOW's root session, an exception should be thrown.
 *                                        raised.
 * <p>
 * <b>Design</b>: An employee is read through the session. A unit of work is acquired, an employee
 *                             is read in and assigned to the UOW. The UOW employee sets as its address the address
 *                             of the employee read in from the session. A boolean tracks whether an exception was
 *                             thrown or not. The test passes if an exception was thrown.
 *
 *     <p>
 * <b>Responsibilities</b>: Try to commit an object with a part from an object in the parent session. Verify that
 *                                             an exception is thrown
 * <p>
 *     <b>Features Used</b>: Unit Of Work
 *
 * <p>
 * <b>Paths Covered</b>: Within the unit of work, different parts listed below were set to null:
 *                                <ul>
 *                                <li>    <i>1:1 mapping, QueryException caught</i>, parents sessions address assigned to child session's employee
 *
 *
 *
 * */
public class ExceptionsRaisedUnitOfWorkTest extends AutoVerifyTestCase {

    /**
     * This method was created by a SmartGuide.
     */
    public ExceptionsRaisedUnitOfWorkTest() {
        setDescription("This test verifies that illegal operations will raise exceptions in Unit Of Work.");
    }

    protected void setup() {
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() { // Read some object from the database.
        boolean exceptionCaught = false;
        Employee employee = (Employee)getSession().readAllObjects(Employee.class).firstElement();

        UnitOfWork firstUOW = getSession().acquireUnitOfWork();

        // Read some object from the database.
        Vector employees = firstUOW.readAllObjects(Employee.class);
        Employee uowEmployee = (Employee)employees.elementAt(2);
        uowEmployee.setAddress(employee.getAddress());

        // commit the unit of work		
        try {
            firstUOW.commit();
        } catch (QueryException exception) {
            exceptionCaught = true;
        }
        if (!exceptionCaught) {
            throw new TestErrorException("Performed illegal operations in UOW yet no exception was thrown");
        }
    }
}
