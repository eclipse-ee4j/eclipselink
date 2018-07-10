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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


public class UnitOfWorkResumeOnFailureTest extends WriteObjectTest {
    public Employee unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    // On some platforms (Sybase) if conn1 updates a row but hasn't yet committed transaction then
    // reading the row through conn2 may hang.
    // To avoid this problem the listener would decrement transaction isolation level,
    // then reading through conn2 no longer hangs, however may result (results on Sybase)
    // in reading of uncommitted data.
    SessionEventListener listener;

    public UnitOfWorkResumeOnFailureTest(Employee originalObject) {
        super(originalObject);
    }

    protected void changeInFirstUnitOfWork() {
        // Many to many
        unitOfWorkWorkingCopy.setProjects(new Vector());
        unitOfWorkWorkingCopy.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)this.unitOfWork.readObject(SmallProject.class));
        unitOfWorkWorkingCopy.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)this.unitOfWork.readObject(LargeProject.class));
        // Direct collection
        unitOfWorkWorkingCopy.setResponsibilitiesList(new Vector());
        unitOfWorkWorkingCopy.addResponsibility("does not make cafee");
        unitOfWorkWorkingCopy.addResponsibility("does not buy donuts");

        // One to one private/public
        // Ugly test for register new objects.
        Address address =
            (new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator()).addressExample10();
        unitOfWorkWorkingCopy.setAddress(address);
        unitOfWorkWorkingCopy.setManager((Employee)this.unitOfWork.readObject(Employee.class));

        // Cause an error in the employee on the database, set the name too big.
        unitOfWorkWorkingCopy.setFirstName(new String(new byte[100]));
    }

    protected void changeInSecondUnitOfWork() {
        // Many to many
        unitOfWorkWorkingCopy.setProjects(new Vector());
        // Direct collection
        unitOfWorkWorkingCopy.addResponsibility("eat not buy donuts");
        // One to one private/public
        unitOfWorkWorkingCopy.setAddress((new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator()).addressExample12());
        unitOfWorkWorkingCopy.setManager(null);

        // revert the invalid data
        unitOfWorkWorkingCopy.setFirstName("Bojo");
    }

    public void reset() {
        super.reset();
        if(listener != null) {
            getAbstractSession().getParent().getEventManager().removeListener(listener);
            listener = null;
        }
    }

    protected void setup() {
        if (getSession().isRemoteSession()) {
            throwWarning("Test not supported on remote.");
        }
        if (getSession().isClientSession()) {
            listener = checkTransactionIsolation();
        }

        super.setup();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

    }

    protected void test() {
        //First test to see if it works without registering any objects.
        //The motivation for this is a bug that was found.
        this.unitOfWork.commitAndResumeOnFailure();

        //now register the object
        this.unitOfWorkWorkingCopy = (Employee)this.unitOfWork.registerObject(this.objectToBeWritten);

        changeInFirstUnitOfWork();

        // Use the original session for comparision
        if (compareObjects(this.originalObject, this.unitOfWorkWorkingCopy)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        //Force a failure
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update EMPLOYEE set VERSION = 0 where EMP_ID = " +
                                                                                       this.unitOfWorkWorkingCopy.getId()));
        try {
            this.unitOfWork.commitAndResumeOnFailure();
            throw new TestProblemException("Should have failed");
        } catch (Exception exception) {
            getAbstractSession().rollbackTransaction();
            getAbstractSession().beginTransaction();
            changeInSecondUnitOfWork();
        }

        // Use the original session for comparision
        if (compareObjects(this.originalObject, this.unitOfWorkWorkingCopy)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        this.unitOfWork.commitAndResumeOnFailure();

        this.unitOfWork.release();
    }

    /**
     * Verify if the objects match completely through allowing the session
     * to use the descriptors.  This will compare the objects and all of
     * their privately owned parts.
     */
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();
        this.unitOfWork.beginEarlyTransaction();
        this.objectFromDatabase = this.unitOfWork.executeQuery(this.query);

        try{
            if (!(compareObjects(this.unitOfWorkWorkingCopy, this.objectFromDatabase))) {
                throw new TestErrorException("The object inserted into the database, '"
                    + this.objectFromDatabase + "' does not match the original, '"
                    + this.unitOfWorkWorkingCopy + "'.");
            }
            if (!((Employee)this.objectFromDatabase).getResponsibilitiesList().contains("eat not buy donuts")){
                throw new TestErrorException("The Changes made after the resume were lost because the deferred set was not merged into new ChangeSet");
            }
        }finally{
            this.unitOfWork.release();
        }
    }
}
