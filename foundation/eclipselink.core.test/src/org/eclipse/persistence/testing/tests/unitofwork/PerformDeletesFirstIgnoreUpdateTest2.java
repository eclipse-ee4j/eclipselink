/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Enumeration;

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
        Employee employee = (Employee)getSession().readObject(Employee.class);

        // the first uow removes all dependencies but phones - so that employee may be deleted
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Employee employeeClone = (Employee)uow.registerObject(employee);
        Enumeration employees = employeeClone.getManagedEmployees().elements();
        while (employees.hasMoreElements()) {
            Employee managedEmployee = (Employee)employees.nextElement();
            employeeClone.removeManagedEmployee(managedEmployee);
        }
        Enumeration projects = employeeClone.getProjects().elements();
        while (projects.hasMoreElements()) {
            org.eclipse.persistence.testing.models.employee.domain.Project project = 
                (org.eclipse.persistence.testing.models.employee.domain.Project)projects.nextElement();
            employeeClone.removeProject(project);
            if (project.getTeamLeader() == employeeClone) {
                project.setTeamLeader(null);
            }
        }
        uow.commit();

        // the second uow does the actual testing: updates the phone number and deletes the employee.
        // Note that the test couldn't be performed using a single uow: shouldPerformDeletesFirst setting
        // would have precluded updating the dependent objects before employee deletion - and the deletion
        // would have failed because of the dependencies left.
        UnitOfWork uow2 = getSession().acquireUnitOfWork();
        uow2.setShouldPerformDeletesFirst(true);
        employeeClone = (Employee)uow2.registerObject(employee);
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
