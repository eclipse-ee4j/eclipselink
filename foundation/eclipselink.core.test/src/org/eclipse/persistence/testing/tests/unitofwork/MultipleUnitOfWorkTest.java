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

import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


public class MultipleUnitOfWorkTest extends org.eclipse.persistence.testing.framework.WriteObjectTest {
    public Object firstUnitOfWorkWorkingCopy;
    public UnitOfWork firstUnitOfWork;
    public Object secondUnitOfWorkWorkingCopy;
    public UnitOfWork secondUnitOfWork;
    // On some platforms (Sybase) if conn1 updates a row but hasn't yet committed transaction then
    // reading the row through conn2 may hang.
    // To avoid this problem the listener would decrement transaction isolation level,
    // then reading through conn2 no longer hangs, however may result (results on Sybase)
    // in reading of uncommitted data.
    SessionEventListener listener;

    /**
     * MultipleUnitOfWorkTest constructor comment.
     */
    public MultipleUnitOfWorkTest() {
        super();
    }

    /**
     * MultipleUnitOfWorkTest constructor comment.
     * @param originalObject java.lang.Object
     */
    public MultipleUnitOfWorkTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeFirstUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.firstUnitOfWorkWorkingCopy;

        // Transformation
        employee.setNormalHours(new java.sql.Time[3]);
        employee.setStartTime(Helper.timeFromHourMinuteSecond(2, 2, 2));
        employee.setEndTime(Helper.timeFromHourMinuteSecond(2, 2, 2));
        // Aggregate
        employee.setPeriod(new org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod(Helper.dateFromYearMonthDate(1903, 
                                                                                                                           3, 
                                                                                                                           3), 
                                                                                              Helper.dateFromYearMonthDate(1904, 
                                                                                                                           4, 
                                                                                                                           4)));
        // One to many private
        employee.setPhoneNumbers(new Vector());
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("home", "514", 
                                                                                              "2263374"));
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("office", "509", 
                                                                                              "8224599"));
    }

    protected void changeSecondUnitOfWorkWorkingCopy() {
        Employee employee = (Employee)this.secondUnitOfWorkWorkingCopy;

        // Many to many
        employee.setProjects(new Vector());
        employee.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)this.secondUnitOfWork.readObject(SmallProject.class));
        employee.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)this.secondUnitOfWork.readObject(LargeProject.class));
        // Direct collection
        employee.setResponsibilitiesList(new Vector());
        employee.addResponsibility("does not make cafee");
        employee.addResponsibility("does not buy donuts");
        // One to one private/public
        employee.setAddress(new org.eclipse.persistence.testing.models.employee.domain.Address());
        employee.setManager((Employee)this.secondUnitOfWork.readObject(Employee.class));
    }

    public void reset() {
        super.reset();
        if(listener != null) {
            getAbstractSession().getParent().getEventManager().removeListener(listener);
            listener = null;
        }
    }

    protected void setup() {
        if(getSession().isClientSession()) {
            checkTransactionIsolation();
        }

        super.setup();

        // Acquire first unit of work
        this.firstUnitOfWork = getSession().acquireUnitOfWork();

        this.firstUnitOfWorkWorkingCopy = this.firstUnitOfWork.registerObject(this.objectToBeWritten);
        changeFirstUnitOfWorkWorkingCopy();

        // Use the original session for comparision
        if (!compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        // Acquire second unit of work
        this.secondUnitOfWork = getSession().acquireUnitOfWork();
        this.secondUnitOfWorkWorkingCopy = this.secondUnitOfWork.registerObject(this.objectToBeWritten);
        changeSecondUnitOfWorkWorkingCopy();

        if (!getAbstractSession().compareObjectsDontMatch(this.firstUnitOfWorkWorkingCopy, 
                                                          this.secondUnitOfWorkWorkingCopy)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
    }

    protected void test() {
        this.firstUnitOfWork.commit();
        try {
            this.secondUnitOfWork.commit();
        } catch (OptimisticLockException exception) {
            ;
        }
    }
}
