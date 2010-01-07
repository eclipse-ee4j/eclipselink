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

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


public class NestedUnitOfWorkMultipleCommitTest extends WriteObjectTest {
    public Employee unitOfWorkWorkingCopy;
    public Employee nestedUnitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    public UnitOfWork nestedUnitOfWork;

    public NestedUnitOfWorkMultipleCommitTest(Employee originalObject) {
        super(originalObject);
    }

    protected void changeNestedUnitOfWork() {
        // Many to many
        nestedUnitOfWorkWorkingCopy.setProjects(new Vector());
        nestedUnitOfWorkWorkingCopy.addProject((Project)this.nestedUnitOfWork.readObject(SmallProject.class));
        nestedUnitOfWorkWorkingCopy.addProject((Project)this.nestedUnitOfWork.readObject(LargeProject.class));
        // Direct collection
        nestedUnitOfWorkWorkingCopy.setResponsibilitiesList(new Vector());
        nestedUnitOfWorkWorkingCopy.addResponsibility("does not make cafee");
        nestedUnitOfWorkWorkingCopy.addResponsibility("does not buy donuts");
        // One to one private/public
        nestedUnitOfWorkWorkingCopy.setAddress(new Address());
        nestedUnitOfWorkWorkingCopy.setManager((Employee)this.nestedUnitOfWork.readObject(Employee.class));
    }

    protected void changeNestedUnitOfWorkAgain() {
        // Many to many
        nestedUnitOfWorkWorkingCopy.setProjects(new Vector());
        // Direct collection
        nestedUnitOfWorkWorkingCopy.addResponsibility("eat not buy donuts");
        // One to one private/public
        nestedUnitOfWorkWorkingCopy.setAddress(new Address());
        nestedUnitOfWorkWorkingCopy.setManager(null);
    }

    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();
        this.nestedUnitOfWork = unitOfWork.acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = (Employee)this.unitOfWork.registerObject(this.objectToBeWritten);
        this.nestedUnitOfWorkWorkingCopy = 
                (Employee)this.nestedUnitOfWork.registerObject(this.unitOfWorkWorkingCopy);
    }

    protected void test() {
        changeNestedUnitOfWork();

        // Use the original session for comparision
        if (!((AbstractSession)getSession()).compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        this.nestedUnitOfWork.commitAndResume();

        if (!(((AbstractSession)getSession()).compareObjects(this.unitOfWorkWorkingCopy, 
                                                             this.nestedUnitOfWorkWorkingCopy))) {
            throw new TestErrorException("The object in the nested unit of work has not been commited properly to its parent");
        }

        this.changeNestedUnitOfWorkAgain();

        if (!((AbstractSession)getSession()).compareObjectsDontMatch(this.unitOfWorkWorkingCopy, 
                                                                     this.nestedUnitOfWorkWorkingCopy)) {
            throw new TestErrorException("The parent session was changed through changing the nested UOW.");
        }

        this.nestedUnitOfWork.commitAndResume();

        if (!(((AbstractSession)getSession()).compareObjects(this.unitOfWorkWorkingCopy, 
                                                             this.nestedUnitOfWorkWorkingCopy))) {
            throw new TestErrorException("The object in the nested unit of work has not been commited properly to its parent");
        }

        this.nestedUnitOfWork.release();
    }
}
