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

import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;


public class UOWCommitAndResumeWithPreCalcChangeSet extends TransactionalTestCase {
    public Employee unitOfWorkWorkingCopy;
    public Employee originalObject;
    public UnitOfWork unitOfWork;
    public UnitOfWork tempUnitOfWork;

    public UOWCommitAndResumeWithPreCalcChangeSet(Employee originalObject) {
        this.originalObject = originalObject;
    }

    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.originalObject = (Employee)getSession().readObject(Employee.class);
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = (Employee)this.unitOfWork.registerObject(this.originalObject);
    }

    protected void test() {
        this.unitOfWorkWorkingCopy.setFirstName("Andy-Von-Trumpet");
        this.unitOfWorkWorkingCopy.addPhoneNumber(new PhoneNumber("OldCell", "555", "4545"));
        Employee newEmp = new Employee();
        newEmp.setFirstName("New");
        newEmp.setLastName("Employee");
        newEmp.setSalary(13);
        PhoneNumber phone = (PhoneNumber)this.unitOfWorkWorkingCopy.getPhoneNumbers().get(0);
        this.unitOfWorkWorkingCopy.getPhoneNumbers().remove(phone);
        this.unitOfWork.deleteObject(phone);
        this.unitOfWorkWorkingCopy.addManagedEmployee(newEmp);

        // Use the original session for comparision
        if (compareObjects(this.originalObject, this.unitOfWorkWorkingCopy)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        UnitOfWorkChangeSet changeSet = (UnitOfWorkChangeSet)this.unitOfWork.getCurrentChanges();
        this.unitOfWork.assignSequenceNumbers();

        this.unitOfWork.revertAndResume();

        ((UnitOfWorkImpl)this.unitOfWork).commitAndResumeWithPreBuiltChangeSet(changeSet);

        this.unitOfWork.release();
    }

    protected void verify() {
        if (!this.originalObject.getFirstName().equals("Andy-Von-Trumpet")) {
            throw new TestErrorException("The values did not merge correctly");
        }
    }
}
