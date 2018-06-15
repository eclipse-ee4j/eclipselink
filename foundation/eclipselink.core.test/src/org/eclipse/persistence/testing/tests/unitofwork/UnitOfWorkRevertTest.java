/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


public class UnitOfWorkRevertTest extends WriteObjectTest {
    public Employee unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;

    public UnitOfWorkRevertTest(Employee originalObject) {
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
        unitOfWorkWorkingCopy.setAddress((new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator()).addressExample10());
        unitOfWorkWorkingCopy.setManager((Employee)this.unitOfWork.readObject(Employee.class));
    }

    protected void changeInSecondUnitOfWork() {
        // Many to many
        unitOfWorkWorkingCopy.setProjects(new Vector());
        // Direct collection
        unitOfWorkWorkingCopy.addResponsibility("eat not buy donuts");
        // One to one private/public
        unitOfWorkWorkingCopy.setAddress((new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator()).addressExample12());
        unitOfWorkWorkingCopy.setManager(null);
    }

    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

    }

    protected void test() {
        //First test to see if it works without registering any objects.
        //The motivation for this is a bug that was found.
        this.unitOfWork.revertAndResume();

        //now register the object
        this.unitOfWorkWorkingCopy = (Employee)this.unitOfWork.registerObject(this.objectToBeWritten);

        changeInFirstUnitOfWork();

        // Use the original session for comparision
        if (!compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        this.unitOfWork.revertAndResume();

        // Use the original session for comparision
        if (!compareObjects(this.objectToBeWritten, this.unitOfWorkWorkingCopy)) {
            throw new TestErrorException("The clone does not match the original after the revert.");
        }

        changeInSecondUnitOfWork();

        if (!getAbstractSession().compareObjectsDontMatch(this.objectToBeWritten, this.unitOfWorkWorkingCopy)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        this.unitOfWork.commitAndResume();

        this.unitOfWork.release();
    }
}
