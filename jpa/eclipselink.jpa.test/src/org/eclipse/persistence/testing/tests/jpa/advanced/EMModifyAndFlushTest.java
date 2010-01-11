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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

public class EMModifyAndFlushTest extends EntityContainerTestBase  {
    public EMModifyAndFlushTest() {
    }

    public Integer[] empIDs = new Integer[3];
    public Integer[] projIDs = new Integer[2];
    public HashMap persistedItems = new HashMap(4);
    
    public void setup (){
        super.setup();

        Employee empClone1 = ModelExamples.employeeExample1();
        empClone1.setAddress(ModelExamples.addressExample1());
        empClone1.addPhoneNumber(ModelExamples.phoneExample1());
        empClone1.addPhoneNumber(ModelExamples.phoneExample9());
        
        Employee empClone2 = ModelExamples.employeeExample2();
        empClone2.setAddress(ModelExamples.addressExample2());
        empClone2.addPhoneNumber(ModelExamples.phoneExample2());
        empClone2.addPhoneNumber(ModelExamples.phoneExample8());

        Employee empClone3 = ModelExamples.employeeExample3();
        empClone3.setAddress(ModelExamples.addressExample3());
        empClone3.addPhoneNumber(ModelExamples.phoneExample3());
        empClone3.addPhoneNumber(ModelExamples.phoneExample7());

        empClone1.addManagedEmployee(empClone2);
        empClone1.addManagedEmployee(empClone3);
        Project projClone1 = ModelExamples.projectExample1(); 
        Project projClone2 = ModelExamples.projectExample2();

        projClone1.setTeamLeader(empClone1);
        projClone1.addTeamMember(empClone1);
        projClone1.addTeamMember(empClone2);
        projClone1.addTeamMember(empClone3);
        empClone1.addProject(projClone1);
        empClone2.addProject(projClone1);
        empClone3.addProject(projClone1);

        try {
            beginTransaction();
            getEntityManager().persist(empClone1);
            getEntityManager().persist(empClone2);
            getEntityManager().persist(empClone3);
            getEntityManager().persist(projClone1);
            getEntityManager().persist(projClone2);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestException("Unable to setup Test" + ex);
        }
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        empIDs[0] = empClone1.getId();
        empIDs[1] = empClone2.getId();
        empIDs[2] = empClone3.getId();
        
        projIDs[0] = projClone1.getId();
        projIDs[1] = projClone2.getId();
       
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
    }
        
    public void test(){
        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[0]);
            employee.setFirstName("Wilfred");

            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

            persistedItems.put("after flush Employee 0", getEntityManager().find(Employee.class, empIDs[0]));

            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring the change of employee's name" + ex);
        }

        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[1]);
            Project project = getEntityManager().find(Project.class, projIDs[1]);
            project.addTeamMember(employee);
            employee.addProject(project);

            Employee manager = getEntityManager().find(Employee.class, empIDs[2]);
            manager.addManagedEmployee(employee);

            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

            persistedItems.put("after flush Employee 1", getEntityManager().find(Employee.class, empIDs[1]));

            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring assignment of employee to a project" + ex);
        }

        try {
            beginTransaction();
            PhoneNumber phone = ModelExamples.phoneExample4();
            getEntityManager().persist(phone);
            Employee employee = getEntityManager().find(Employee.class, empIDs[2]);
            employee.addPhoneNumber(phone);

            Address address = ModelExamples.addressExample4();
            getEntityManager().persist(address);
            employee.setAddress(address);

            EmploymentPeriod period = new EmploymentPeriod();
            period.setStartDate(employee.getPeriod().getStartDate());
            period.setEndDate(new Date(System.currentTimeMillis()));
            employee.setPeriod(period);

            employee.setSalary(0);

            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

            persistedItems.put("after flush Employee 2", getEntityManager().find(Employee.class, empIDs[2]));

            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring assignment of a phone to an employee" + ex);
        }
    
        try {
            beginTransaction();
            Collection employees = getEntityManager().createQuery("SELECT OBJECT(employee) FROM Employee employee").getResultList();
            Iterator employeesIter = employees.iterator();
            int totalRaisePercent = 100 + 3;
            while (employeesIter.hasNext()) {
                Employee employee = (Employee)employeesIter.next();
                int newSalary = (int)(employee.getSalary() * (totalRaisePercent / 100.0));
                employee.setSalary(newSalary);
            }

            getEntityManager().flush();
            //lets initialize the identity map to make sure they were persisted
            ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

            persistedItems.put("after flush Employees 0 with raise", getEntityManager().find(Employee.class, empIDs[0]));
            persistedItems.put("after flush Employees 1 with raise", getEntityManager().find(Employee.class, empIDs[1]));
            persistedItems.put("after flush Employees 2 with raise", getEntityManager().find(Employee.class, empIDs[2]));

            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring employee raises" + ex);
        }
    }    

    public void verify(){
        Employee employee = (Employee)persistedItems.get("after flush Employee 0");
            if ( (!employee.getFirstName().equals("Wilfred"))){
            throw new TestErrorException("Employee ID :" + empIDs[0] + " First Name not Updated");
        }

        employee = (Employee)persistedItems.get("after flush Employee 1");
        if ( !(((Project)((List)employee.getProjects()).get(1)).getName().equals("Feline Demographics Assesment")) ){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Projects not Updated");
        }
        if (!employee.getManager().getId().equals(empIDs[2])){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Manager not Updated");
        }
        
        employee = (Employee)persistedItems.get("after flush Employee 2");
        if ( employee.getManagedEmployees().size() <= 0) {
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Managed Employees not Updated");
        }
        if (employee.getPhoneNumbers().size() <= 2){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " PhoneNumbers not Updated");
        }
        if (!employee.getAddress().getStreet().equals("324 Bay Street")){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Address not Updated");
        }
        if (employee.getPeriod().getEndDate().getTime() >= System.currentTimeMillis()){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " EndDate not Updated");
        }
        
        employee = (Employee)persistedItems.get("after flush Employees 0 with raise");
        if (employee.getSalary() <= 15000){
            throw new TestErrorException("Employee ID :" + empIDs[0] + " Salary Not Updated");
        }
        employee = (Employee)persistedItems.get("after flush Employees 1 with raise");
        if (employee.getSalary() <= 1000) {
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Salary Not updated");
        }
        employee = (Employee)persistedItems.get("after flush Employees 2 with raise");
        if (employee.getSalary() != 0){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Salary Not updated");
        }              
    }
}
