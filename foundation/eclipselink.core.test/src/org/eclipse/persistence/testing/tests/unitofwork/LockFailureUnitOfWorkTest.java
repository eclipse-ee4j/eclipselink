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
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class LockFailureUnitOfWorkTest extends AutoVerifyTestCase {

    protected void setup() {
        getAbstractSession().beginTransaction();
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void test() {
        UnitOfWork firstUOW = getSession().acquireUnitOfWork();

        // Read some object from the database.
        Employee employee = (Employee)firstUOW.readAllObjects(Employee.class).firstElement();
        String lastName = employee.getLastName();

        // Change the object
        boolean isMale = employee.getGender().equals("Male");
        if (isMale) {
            employee.setFemale();
        } else {
            employee.setMale();
        }

        // Update the version field explicitily
        firstUOW.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE EMPLOYEE SET VERSION = VERSION + 66 WHERE L_NAME = " + 
                                                                                   "'" + lastName + "'"));

        // commit the unit of work		
        try {
            //For the same uow to be reused, only use commitAndResumeOnFailure if any exception is expected.
            //So that UnitOfWorkChangeSet can be reset properly.
            firstUOW.commitAndResumeOnFailure();
        } catch (OptimisticLockException exception) {
            firstUOW.refreshObject(employee);
            // Check that refresh works.
            if (isMale != employee.getGender().equals("Male")) {
                throw new TestErrorException("Refresh does not work in unit of work.");
            }

            if (isMale) {
                employee.setFemale();
            } else {
                employee.setMale();
            }

            firstUOW.commit();
        }

        UnitOfWork secondUOW = getSession().acquireUnitOfWork();
        Employee sameEmployee = (Employee)secondUOW.readObject(employee);

        if (sameEmployee.getGender().equals("Male")) {
            sameEmployee.setFemale();
        } else {
            sameEmployee.setMale();
        }

        secondUOW.commit();
    }
}
