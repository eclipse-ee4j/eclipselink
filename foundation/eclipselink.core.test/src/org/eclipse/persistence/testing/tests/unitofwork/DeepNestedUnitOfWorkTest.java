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

import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


public class DeepNestedUnitOfWorkTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy[];
    public UnitOfWork unitOfWork[];

    public DeepNestedUnitOfWorkTest() {
        super();
        unitOfWorkWorkingCopy = new Object[5];
        unitOfWork = new UnitOfWork[5];
    }

    public DeepNestedUnitOfWorkTest(Object originalObject) {
        super(originalObject);
        unitOfWorkWorkingCopy = new Object[5];
        unitOfWork = new UnitOfWork[5];
    }

    protected void changeFirstLevelUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy[0];

        // Transformation
        employee.setNormalHours(new java.sql.Time[2]);
        employee.setStartTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        employee.setEndTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        // Aggregate
        employee.setPeriod(new EmploymentPeriod(Helper.dateFromYearMonthDate(1901, 1, 1), 
                                                Helper.dateFromYearMonthDate(1902, 2, 2)));
    }

    protected void changeSecondLevelUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy[1];

        // One to many private
        employee.setPhoneNumbers(new Vector());
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("home", "613", 
                                                                                              "2263374"));
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("office", "416", 
                                                                                              "8224599"));
        // Many to many
        employee.setProjects(new Vector());
        employee.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)this.unitOfWork[1].readObject(SmallProject.class));
        employee.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)this.unitOfWork[1].readObject(LargeProject.class));
    }

    protected void changeThirdLevelUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.unitOfWorkWorkingCopy[2];

        // Direct collection
        employee.setResponsibilitiesList(new Vector());
        employee.addResponsibility("make coffee");
        employee.addResponsibility("buy donuts");
        // One to one private/public
        employee.setAddress(new org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator().addressExample10());
        // make sure that the employee is not his own manager
        employee.setManager((Employee)this.unitOfWork[2].readObject(Employee.class, (new ExpressionBuilder()).get("id").notEqual(employee.getId())));
    }

    protected void setup() {
        super.setup();

        // Acquire first unit of work
        this.unitOfWork[0] = getSession().acquireUnitOfWork();
        this.unitOfWorkWorkingCopy[0] = this.unitOfWork[0].registerObject(this.objectToBeWritten);
        changeFirstLevelUnitOfWorkWorkingCopy();
        if (!((AbstractSession)getSession()).compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        // Acquire second unit of work
        this.unitOfWork[1] = this.unitOfWork[0].acquireUnitOfWork();
        this.unitOfWorkWorkingCopy[1] = this.unitOfWork[1].registerObject(this.unitOfWorkWorkingCopy[0]);
        changeSecondLevelUnitOfWorkWorkingCopy();
        if (!((AbstractSession)getSession()).compareObjectsDontMatch(this.unitOfWorkWorkingCopy[0], 
                                                                     this.unitOfWorkWorkingCopy[1])) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        // Acquire second unit of work
        this.unitOfWork[2] = this.unitOfWork[1].acquireUnitOfWork();
        this.unitOfWorkWorkingCopy[2] = this.unitOfWork[2].registerObject(this.unitOfWorkWorkingCopy[1]);
        changeThirdLevelUnitOfWorkWorkingCopy();
        if (!((AbstractSession)getSession()).compareObjectsDontMatch(this.unitOfWorkWorkingCopy[1], 
                                                                     this.unitOfWorkWorkingCopy[2])) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
    }

    protected void test() {
        this.unitOfWork[2].commit();

        if (!(((AbstractSession)getSession()).compareObjects(this.unitOfWorkWorkingCopy[1], 
                                                             this.unitOfWorkWorkingCopy[2]))) {
            throw new TestErrorException("The object in the nested unit of work has not been commited properly to its parent");
        }

        this.unitOfWork[1].commit();

        if (!(((AbstractSession)getSession()).compareObjects(this.unitOfWorkWorkingCopy[0], 
                                                             this.unitOfWorkWorkingCopy[1]))) {
            throw new TestErrorException("The object in the nested unit of work has not been commited properly to its parent");
        }

        this.unitOfWork[0].commit();
    }
}
