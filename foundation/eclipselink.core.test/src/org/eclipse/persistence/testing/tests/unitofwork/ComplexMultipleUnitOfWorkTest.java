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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.remote.RemoteSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.remote.RemoteModel;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;


public class ComplexMultipleUnitOfWorkTest extends AutoVerifyTestCase {
    public Employee readInSession;
    public Employee readInUow;
    public Employee newEmployeeInUow;
    public Employee readInFirstNestedUow;
    public UnitOfWork firstUnitOfWork;
    public UnitOfWork secondUnitOfWork;
    public UnitOfWork thirdUnitOfWork;
    // On some platforms (Sybase) if conn1 updates a row but hasn't yet committed transaction then
    // reading the row through conn2 may hang.
    // To avoid this problem the listener would decrement transaction isolation level,
    // then reading through conn2 no longer hangs, however may result (results on Sybase)
    // in reading of uncommitted data.
    SessionEventListener listener;

    /**
     * MultipleUnitOfWorkTestCase constructor comment.
     */
    public ComplexMultipleUnitOfWorkTest() {
        super();
    }

    public Address addressExample1() {
        Address address = new org.eclipse.persistence.testing.models.employee.domain.Address();

        address.setCity("Toronto");
        address.setPostalCode("L5J2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., suite 4");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample2() {
        Address address = new org.eclipse.persistence.testing.models.employee.domain.Address();

        address.setCity("Ottawa");
        address.setPostalCode("K5J2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., suite 4");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample3() {
        Address address = new org.eclipse.persistence.testing.models.employee.domain.Address();

        address.setCity("Sudbury");
        address.setPostalCode("L5J2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., suite 4");
        address.setCountry("Canada");
        return address;
    }

    public Address addressExample4() {
        Address address = new org.eclipse.persistence.testing.models.employee.domain.Address();

        address.setCity("Niagra");
        address.setPostalCode("L5J2B5");
        address.setProvince("ONT");
        address.setStreet("1450 Acme Cr., suite 4");
        address.setCountry("Canada");
        return address;
    }

    protected void changeObject(Employee employee, UnitOfWork unitOfWork) {
        // Transformation
        employee.setNormalHours(new java.sql.Time[2]);
        employee.setStartTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        employee.setEndTime(Helper.timeFromHourMinuteSecond(1, 1, 1));
        // Aggregate
        employee.setPeriod(new EmploymentPeriod(Helper.dateFromYearMonthDate(1901, 1, 1), 
                                                Helper.dateFromYearMonthDate(1902, 2, 2)));
        // One to many private
        employee.setPhoneNumbers(new Vector());
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("home", "613", 
                                                                                              "2263374"));
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("office", "416", 
                                                                                              "8224599"));
        // Many to many
        employee.setProjects(new Vector());
        employee.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)unitOfWork.readObject(SmallProject.class));
        employee.addProject((org.eclipse.persistence.testing.models.employee.domain.Project)unitOfWork.readObject(LargeProject.class));
        // Direct collection
        employee.setResponsibilitiesList(new Vector());
        employee.addResponsibility("make coffee");
        employee.addResponsibility("buy donuts");
        // One to one private/public
        employee.setAddress(addressExample4());
        ((Employee)unitOfWork.readObject(Employee.class)).addManagedEmployee(employee);
    }

    public Employee createNewEmployeeObject() {
        Employee employee = new org.eclipse.persistence.testing.models.employee.domain.Employee();

        employee.setFirstName("Judy");
        employee.setLastName("Barney");
        employee.setMale();
        employee.setSalary(35000);
        employee.setPeriod(employmentPeriodExample());
        employee.setAddress(addressExample1());
        employee.addResponsibility("Make the coffee.");
        employee.addResponsibility("Clean the kitchen.");
        employee.addPhoneNumber(new org.eclipse.persistence.testing.models.employee.domain.PhoneNumber("Work", "613", 
                                                                                              "2258812"));

        employee.addProject(smallProjectExample());

        return employee;
    }

    public EmploymentPeriod employmentPeriodExample() {
        EmploymentPeriod employmentPeriod = new org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod();

        employmentPeriod.setEndDate(org.eclipse.persistence.internal.helper.Helper.dateFromYearMonthDate(1996, 0, 1));
        employmentPeriod.setStartDate(org.eclipse.persistence.internal.helper.Helper.dateFromYearMonthDate(1993, 0, 1));
        return employmentPeriod;
    }

    public void processFirstUnitOfWork() throws Exception {
        Expression firstNameExpression;

        // Read the object in the session
        firstNameExpression = new ExpressionBuilder().get("firstName").equal("Bob");
        this.readInSession = (Employee)getSession().readObject(Employee.class, firstNameExpression);

        // Acquire unit of work	
        // Read first object in the uow
        this.firstUnitOfWork = getSession().acquireUnitOfWork();
        firstNameExpression = new ExpressionBuilder().get("firstName").equal("John");
        this.readInUow = (Employee)this.firstUnitOfWork.readObject(Employee.class, firstNameExpression);

        Employee regDel = (Employee)this.firstUnitOfWork.registerObject(this.readInSession);
        // Must remove references to deleted objects.
        if (regDel.getManager() != null) {
            regDel.getManager().removeManagedEmployee(regDel);
        }
        for (java.util.Enumeration mgdEnum = regDel.getManagedEmployees().elements(); mgdEnum.hasMoreElements(); 
        ) {
            ((Employee)mgdEnum.nextElement()).setManager(null);
        }
        for (java.util.Enumeration mgdProjEnum = 
             (this.firstUnitOfWork.readAllObjects(org.eclipse.persistence.testing.models.employee.domain.Project.class)).elements(); 
             mgdProjEnum.hasMoreElements(); ) {
            ((org.eclipse.persistence.testing.models.employee.domain.Project)mgdProjEnum.nextElement()).setTeamLeader(null);
        }

        // Delete the object read in the session	without registration.
        this.firstUnitOfWork.deleteObject(this.readInSession);

        // Create object in uow and register it.
        this.newEmployeeInUow = createNewEmployeeObject();
        Employee newEmployeeInFirstUow = (Employee)this.firstUnitOfWork.registerObject(this.newEmployeeInUow);

        // Must remove references to deleted objects.
        if (this.readInUow.getManager() != null) {
            this.readInUow.getManager().removeManagedEmployee(this.readInUow);
        }
        for (java.util.Enumeration mgdEnum = this.readInUow.getManagedEmployees().elements(); 
             mgdEnum.hasMoreElements(); ) {
            ((Employee)mgdEnum.nextElement()).setManager(null);
        }

        // Acquire first nested unit of work
        // Delete read object in the unit of work without registering it.
        // Read some object and change it.
        UnitOfWork firstNestedUow = this.firstUnitOfWork.acquireUnitOfWork();
        firstNestedUow.deleteObject(this.readInUow);

        Employee workingCopyOfNewEmployeeInSecondNestedUow = 
            (Employee)firstNestedUow.registerObject(newEmployeeInFirstUow);

        workingCopyOfNewEmployeeInSecondNestedUow.setAddress(addressExample2());

        firstNestedUow.commit();

        // Acquire second nested 	unit of work
        // Change the new object created in the parent.
        UnitOfWork secondNestedUow = this.firstUnitOfWork.acquireUnitOfWork();

        firstNameExpression = new ExpressionBuilder().get("firstName").equal("Marcus");
        this.readInFirstNestedUow = (Employee)secondNestedUow.readObject(Employee.class, firstNameExpression);
        this.readInFirstNestedUow.setAddress(addressExample3());

        secondNestedUow.commit();
        // Assign correct clone from root unit of work so compare verifies works correctly.
        this.readInFirstNestedUow = (Employee)secondNestedUow.getOriginalVersionOfObject(readInFirstNestedUow);
        this.firstUnitOfWork.commit();
    }

    public void processSecondUnitOfWork() {
        this.secondUnitOfWork = getSession().acquireUnitOfWork();

        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(this.newEmployeeInUow);

        Employee objectFromDatabase = (Employee)secondUnitOfWork.executeQuery(query);

        if (!(((AbstractSession)getSession()).compareObjects(this.newEmployeeInUow, objectFromDatabase))) {
            throw new TestErrorException("The object read from the database, '" + this.newEmployeeInUow + 
                                         "' does not match the originial, '" + objectFromDatabase + ".");
        }

        query = new ReadObjectQuery();
        query.setSelectionObject(this.readInFirstNestedUow);

        objectFromDatabase = (Employee)secondUnitOfWork.executeQuery(query);

        if (!(((AbstractSession)getSession()).compareObjects(this.readInFirstNestedUow, objectFromDatabase))) {
            throw new TestErrorException("The object read from the database, '" + this.readInFirstNestedUow + 
                                         "' does not match the originial, '" + objectFromDatabase + ".");
        }

        Employee workingCopy = (Employee)secondUnitOfWork.registerObject(this.newEmployeeInUow);
        changeObject(workingCopy, this.secondUnitOfWork);
        secondUnitOfWork.release();

        // Use the original session for comparision
        if (!((AbstractSession)getSession()).compareObjectsDontMatch(this.newEmployeeInUow, workingCopy)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }
    }

    public void processThirdUnitOfWork() {
        this.thirdUnitOfWork = getSession().acquireUnitOfWork();

        Employee workingCopy = (Employee)thirdUnitOfWork.registerObject(this.newEmployeeInUow);
        changeObject(workingCopy, this.thirdUnitOfWork);

        if (!((AbstractSession)getSession()).compareObjectsDontMatch(this.newEmployeeInUow, workingCopy)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        this.thirdUnitOfWork.commit();
    }

    public void reset() {
        if(getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        if(listener != null) {
            getAbstractSession().getParent().getEventManager().removeListener(listener);
            listener = null;
        }
    }

    public void setup() {
        if (getSession().isClientSession()) {
            checkTransactionIsolation();
        }
        getAbstractSession().beginTransaction();
    }

    public SmallProject smallProjectExample() {
        SmallProject smallProject = new SmallProject();

        smallProject.setName("Enterprise");
        smallProject.setDescription("A enterprise wide application using Visual J++ to report on the corporations Sybase and DB/2 database through TopLink.");
        return smallProject;
    }

    public void test() throws Exception {
        processFirstUnitOfWork();
        processSecondUnitOfWork();
        processThirdUnitOfWork();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Session sessionToVerifyDelete;

        if (getSession() instanceof RemoteSession) {
            sessionToVerifyDelete = RemoteModel.getServerSession();
        } else {
            sessionToVerifyDelete = getSession();
        }
        // Verify if object deleted in the uow was deleted
        if (!(((AbstractSession)sessionToVerifyDelete).verifyDelete(this.readInSession))) {
            throw new TestErrorException("The object '" + this.readInSession + 
                                         "'deleted in the uow was not completely deleted from the database.");
        }

        // Verify if object deleted in the uow was deleted
        if (!(((AbstractSession)sessionToVerifyDelete).verifyDelete(this.readInUow))) {
            throw new TestErrorException("The object '" + this.readInUow + 
                                         "'deleted in the nested uow was not completely deleted from the database.");
        }

        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(this.newEmployeeInUow);

        Employee objectFromDatabase = (Employee)getSession().executeQuery(query);

        if (!(((AbstractSession)getSession()).compareObjects(this.newEmployeeInUow, objectFromDatabase))) {
            throw new TestErrorException("The object read from the database, '" + this.newEmployeeInUow + 
                                         "' does not match the originial, '" + objectFromDatabase + ".");
        }
    }
}
