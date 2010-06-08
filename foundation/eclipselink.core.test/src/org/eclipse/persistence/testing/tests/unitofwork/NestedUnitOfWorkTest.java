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

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


public class NestedUnitOfWorkTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;
    public Object nestedUnitOfWorkWorkingCopy;
    public UnitOfWork nestedUnitOfWork;

    public NestedUnitOfWorkTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeNestedUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.nestedUnitOfWorkWorkingCopy;
        // Transformation
        employee.setNormalHours(new java.sql.Time[3]);
        employee.setStartTime(Helper.timeFromHourMinuteSecond(2, 2, 2));
        employee.setEndTime(Helper.timeFromHourMinuteSecond(2, 2, 2));
        // Aggregate
        employee.setPeriod(new EmploymentPeriod(Helper.dateFromYearMonthDate(1903, 3, 3), Helper.dateFromYearMonthDate(1904, 4, 4)));
        // One to many private
        employee.setPhoneNumbers(new Vector());
        employee.addPhoneNumber(new PhoneNumber("cell", "514", "2263374"));
        employee.addPhoneNumber(new PhoneNumber("shop", "509", "8224599"));
        employee.addPhoneNumber(new PhoneNumber("fax", "509", "8224798"));

        // Many to many
        employee.setProjects(new Vector());
        employee.addProject((Project)this.nestedUnitOfWork.readObject(SmallProject.class));
        employee.addProject(new SmallProject()); // Test adding new objects.
        employee.addProject(new SmallProject());

        // Direct collection
        employee.setResponsibilitiesList(new Vector());
        employee.addResponsibility("does not make cofffee");
        employee.addResponsibility("does not buy donuts");
        // One to one private/public
        employee.setAddress((new EmployeePopulator()).addressExample1());
    }

    protected void changeUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy;
        // Transformation
        employee.setNormalHours(new java.sql.Time[2]);
        employee.setStartTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        employee.setEndTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        // Aggregate
        employee.setPeriod(new EmploymentPeriod(Helper.dateFromYearMonthDate(1901, 1, 1), Helper.dateFromYearMonthDate(1902, 2, 2)));
        // One to many private
        employee.setPhoneNumbers(new Vector());
        employee.addPhoneNumber(new PhoneNumber("home", "613", "2263374"));
        employee.addPhoneNumber(new PhoneNumber("office", "416", "8224599"));
        // Many to many
        employee.setProjects(new Vector());
        employee.addProject((Project)this.unitOfWork.readObject(SmallProject.class));
        employee.addProject((Project)this.unitOfWork.readObject(LargeProject.class));
        // Direct collection
        employee.setResponsibilitiesList(new Vector());
        employee.addResponsibility("make coffee");
        employee.addResponsibility("buy donuts");
        // One to one private/public
        employee.setAddress(new Address());
        // make sure that the employee is not his own manager
        employee.setManager((Employee)this.unitOfWork.readObject(Employee.class, (new ExpressionBuilder()).get("id").notEqual(employee.getId())));
    }

    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = this.unitOfWork.registerObject(this.objectToBeWritten);
        changeUnitOfWorkWorkingCopy();

        // Use the original session for comparision
        if (!((AbstractSession)getSession()).compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        // Acquire nested unit of work
        this.nestedUnitOfWork = this.unitOfWork.acquireUnitOfWork();
        this.nestedUnitOfWorkWorkingCopy = this.nestedUnitOfWork.registerObject(this.unitOfWorkWorkingCopy);
        changeNestedUnitOfWorkWorkingCopy();

        if (nestedUnitOfWork.registerObject(originalObject) == null) {
            throw new TestException("The nested uow register object returns null");
        }

        if (!((AbstractSession)getSession()).compareObjectsDontMatch(this.unitOfWorkWorkingCopy, 
                                                                     this.nestedUnitOfWorkWorkingCopy)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
    }

    protected void test() {
        this.nestedUnitOfWork.commit();

        if (!(((AbstractSession)getSession()).compareObjects(this.unitOfWorkWorkingCopy, 
                                                             this.nestedUnitOfWorkWorkingCopy))) {
            throw new TestErrorException("The object in the nested unit of work has not been commited properly to its parent");
        }

        this.unitOfWork.commit();
    }
}
