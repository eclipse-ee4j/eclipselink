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

import java.util.Enumeration;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;


/**
 * <p>
 * <b>Purpose</b>: This test checks if UOW merge is perfromed properly.
 * <p>
 * <b>Motivation </b>: This test was written to fix a bug. While in a unit of work, a new
 * object that refrenced clones, after the merge still refrenced clones.
 * <p>
 * <b>Design</b>: A unit of work is acquired, an employee is read in and registered to the UOW. The
 * returned clone is stored. A new employee is created, parts of the new employee are initialized
 * with parts of the clone. A commit is issued. After the commit is done, the merge should
 * have replaced the clones in the new employee with the original objects that were cloned.
 * This is verified.
 * <p>
 * <b>Responsibilities</b>: Verify that the merge works properly. Make sure that the original objects
 * are pointed to by the new employee.
 * <p>
 * <b>Features Used</b>: Unit Of Work, Merge feature
 * <p>
 * <b>Paths Covered</b>: Within the unit of work, different parts of the employee were set with clones:
 * <ul>
 * <li> <i>Address</i> 1:1 Mapping
 * <li> <i>Period</i> Simple Aggregate
 * <li> <i>NormalHours</i>
 */
public class MergeUnitOfWorkTest extends org.eclipse.persistence.testing.framework.WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    public Object objectBeforeMerge;

    /**
     * MergeUnitOfWorkTest constructor comment.
     */
    public MergeUnitOfWorkTest() {
        super();
    }

    /**
     * MergeUnitOfWorkTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public MergeUnitOfWorkTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy;
        Employee managedEmployee = (Employee)employee.getManagedEmployees().firstElement();
        Employee newEmployee;
        newEmployee = (Employee)unitOfWork.registerObject(new Employee());

        newEmployee.setNormalHours(employee.getNormalHours());
        newEmployee.setPeriod((EmploymentPeriod)employee.getPeriod().clone());
        newEmployee.setAddress(managedEmployee.getAddress());

        newEmployee.setFirstName("New Man");
        newEmployee.setLastName("Smith");

        employee.addManagedEmployee(newEmployee);
        this.setObjectBeforeMerge(newEmployee);
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void compareForProperMerge(Employee employeeBeforeMerge, Employee employeeAfterMerge) {
        if (employeeBeforeMerge.getAddress() == employeeAfterMerge.getAddress()) {
            throw new TestErrorException("The object '" + employeeAfterMerge + 
                                         "'Was not merged properly. It is still referencing cloned address instead of original address");
        }
        if (employeeBeforeMerge.getPeriod() == employeeAfterMerge.getPeriod()) {
            throw new TestErrorException("The object '" + employeeAfterMerge + 
                                         "'Was not merged properly. It is still referencing cloned Period instead of original Period");
        }
        if (employeeBeforeMerge.getNormalHours() == employeeAfterMerge.getNormalHours()) {
            throw new TestErrorException("The object '" + employeeAfterMerge + 
                                         "'Was not merged properly. It is still referencing cloned NormalHours instead of original NormalHours");
        }
    }

    public void setObjectBeforeMerge(Object preMergeObject) {
        objectBeforeMerge = preMergeObject;
    }

    /**
     * This method was created by a SmartGuide.
     */
    public void setup() {
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
        boolean found = false;
        Employee employeeToBeWritten = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        Enumeration enumeration = ((Employee)this.objectToBeWritten).getManagedEmployees().elements();
        while ((!found) && (enumeration.hasMoreElements())) {
            Employee employee = (Employee)enumeration.nextElement();
            if (employee.getFirstName() == "New Man") {
                employeeToBeWritten = employee;
                found = true;
            }
        }

        this.compareForProperMerge((Employee)this.objectBeforeMerge, employeeToBeWritten);

    }
}
