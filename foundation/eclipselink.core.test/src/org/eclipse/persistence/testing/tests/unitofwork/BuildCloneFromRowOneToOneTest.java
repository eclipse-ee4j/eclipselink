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

import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;


public class BuildCloneFromRowOneToOneTest extends AutoVerifyTestCase {
    public Employee emp;
    public Employee manager;
    public Address address;

    /**
     * MultipleUnitOfWorkTestCase constructor comment.
     */
    public BuildCloneFromRowOneToOneTest() {
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

    public Employee createNewEmployeeObject() {
        Employee employee = new org.eclipse.persistence.testing.models.employee.domain.Employee();

        employee.setFirstName("Judy");
        employee.setLastName("Barney");
        employee.setFemale();
        employee.setSalary(35000);
        employee.setPeriod(employmentPeriodExample());
        return employee;
    }

    public Employee createNewManagerObject() {
        Employee employee = new org.eclipse.persistence.testing.models.employee.domain.Employee();

        employee.setFirstName("Markus" + System.currentTimeMillis());
        employee.setLastName("Markson");
        employee.setMale();
        employee.setSalary(35000);
        employee.setPeriod(employmentPeriodExample());
        return employee;
    }

    public EmploymentPeriod employmentPeriodExample() {
        EmploymentPeriod employmentPeriod = new org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod();

        employmentPeriod.setEndDate(org.eclipse.persistence.internal.helper.Helper.dateFromYearMonthDate(1996, 0, 1));
        employmentPeriod.setStartDate(org.eclipse.persistence.internal.helper.Helper.dateFromYearMonthDate(1993, 0, 1));
        return employmentPeriod;
    }


    public void reset() {
        if(getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

    public void test() throws Exception {
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.emp = (Employee) uow.registerObject(createNewEmployeeObject());
        this.manager = (Employee) uow.registerObject(createNewManagerObject());
        this.manager.setAddress((Address) uow.registerObject(addressExample1()));
        uow.commit();
        
        uow = getSession().acquireUnitOfWork();
        Employee cloneEmp = (Employee) uow.readObject(this.emp);
        
        ReadObjectQuery roq = new ReadObjectQuery(Employee.class);
        roq.setSelectionCriteria(roq.getExpressionBuilder().get("firstName").equal(this.manager.getFirstName()));
        
        Employee cloneManager = (Employee)uow.executeQuery(roq);
        cloneEmp.setManager(cloneManager);
        cloneManager.addManagedEmployee(cloneManager);
        uow.commit(); // this regression will cause exception here.
        
    }

    public void verify() {
    }
}
