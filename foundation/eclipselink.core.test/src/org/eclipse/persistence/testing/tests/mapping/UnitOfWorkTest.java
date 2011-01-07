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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.mapping.Employee;

/**
 * <p>
 * <b>Purpose</b>: This test checks to see if the Unit of Work feature functions correctly
 * within the context of Complex mappings.
 *
 * <p>
 * <b>Motivation </b>: This test was written to test Unit-Of-Work features.
 * <p>
 * <b>Design</b>: The Complex Mapping model is used. An Employee is registered into the UOW,
 *                     and then various member fields (which represent various different types
 *                    of mappings) are changed and commited to to the database, read back and
 *                    compared.
 * <p>
 * <b>Responsibilities</b>: Check if the unit of work functions properly with complex mappings
 *
 * <p>
 *     <b>Features Used</b>: Complex Mappings, Unit Of Work
 *
 * <p>
 * <b>Paths Covered</b>:
 *        <ul>
 *        <li><i>Adding an object</i> - creating an object within a UOW</li>
 *        <li><i>Modifying an object</i> - modify contents of an existing object within a UOW</li>
 *        <li><i>Removing an object</i> - deletion of an object</li>
 *        </ul>
 * <p>
 * For each of the above paths, the work is done w.r.t. the 1:1 mappings, 1:M mappings, the
 * Transformational mappings, etc.
 * */
public class UnitOfWorkTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;

    /**
     * UnitOfWorkTest constructor comment.
     */
    public UnitOfWorkTest() {
        super();
    }

    /**
     * UnitOfWorkTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public UnitOfWorkTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy;

        // root object - change some field data
        // employee.jobDescription = JobDescription.example1();
        // 1:1 employee -> foo added
        // 1:1 employee -> manager changed, 1:M managedEmployee -> manager changed
        Employee manager = Employee.example7();
        employee.getManager().removeManagedEmployee(employee);
        employee.setManager(manager);
        manager.addManagedEmployee(employee);

        //1:1 x -> y deleted
        //1:M a -> b deleted
    }

    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = this.unitOfWork.registerObject(this.objectToBeWritten);
        changeUnitOfWorkWorkingCopy();
        // Use the original session for comparision
        if (!compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
    }

    protected void test() {
        this.unitOfWork.commit();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        if (!(compareObjects(this.unitOfWorkWorkingCopy, this.objectToBeWritten))) {
            throw new TestErrorException("The object in the unit of work has not been commited properly to its parent");
        }

        super.verify();
    }
}
