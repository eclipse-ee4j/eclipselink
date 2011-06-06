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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


/**
 * Test for bug 3815959: SETSHOULDPERFORMDELETESFIRST(TRUE) PERFORMS REDUNDANT UPDATE STATEMENTS
 */
public class PerformDeletesFirstIgnoreUpdateTest2 extends PerformDeletesFirstIgnoreUpdateTest {

    public PerformDeletesFirstIgnoreUpdateTest2() {
        setDescription("Verifies that TopLink doesn't issue UPDATE phone number after deleting employee owning it");
    }

    public void test() {
        // create a new Employee with phone
        String firstName = "PerformDeletesFirstIgnoreUpdateTest2";
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.addPhoneNumber(new PhoneNumber("home", "123", "1234567"));
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(employee);
        uow.commit();

        // the second uow does the actual testing: updates the phone number and deletes the employee.
        // Note that the test couldn't be performed using a single uow: shouldPerformDeletesFirst setting
        // would have precluded updating the dependent objects before employee deletion - and the deletion
        // would have failed because of the dependencies left.
        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        uow2.setShouldPerformDeletesFirst(true);
        Employee employeeClone = (Employee)uow2.registerObject(employee);
        PhoneNumber phone = (PhoneNumber)employeeClone.getPhoneNumbers().firstElement();
        // The new phone number is too long (it should be no more than 7 characters)
        // therefore is update goes through and not preceded with DELETE DatabaseException will result
        phone.setNumber("1234567890");
        uow2.deleteObject(employeeClone);
        try {
            uow2.commit();
        } catch (DatabaseException ex) {
            dbException = ex;
        }
    }
}
