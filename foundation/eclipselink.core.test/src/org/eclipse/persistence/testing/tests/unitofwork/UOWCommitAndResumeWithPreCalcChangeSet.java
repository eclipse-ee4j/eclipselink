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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.IsolatedClientSession;
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
        ClassDescriptor descriptor = getSession().getDescriptor(this.originalObject );
        if (descriptor.isProtectedIsolation() && descriptor.shouldIsolateProtectedObjectsInUnitOfWork() && getSession() instanceof IsolatedClientSession){
            // this will have read a version of the protected Entity into the Isolated Cache even though the test wants to isolated to UOW
            //replace with actual shared cache version
            this.originalObject = (Employee) ((AbstractSession)getSession()).getParentIdentityMapSession(descriptor, false, true).getIdentityMapAccessor().getFromIdentityMap(this.originalObject);
        }
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = (Employee)this.unitOfWork.registerObject(this.originalObject);
    }

    protected void test() {
        if (this.unitOfWork.isRemoteUnitOfWork()){
            //The tested API is not supported by a Remote UnitOfWork
            return;
        }
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
        if (this.unitOfWork.isRemoteUnitOfWork()){
            //The tested API is not supported by a Remote UnitOfWork
            return;
        }
        if (!this.originalObject.getFirstName().equals("Andy-Von-Trumpet")) {
            throw new TestErrorException("The values did not merge correctly");
        }
    }
}
