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
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.sql.Date;
import java.util.*;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * Test modification of each attribute and verifies the change was made after commit.
 */
public class EMModifyAndCommitTest extends EntityContainerTestBase  {
    public EMModifyAndCommitTest() {
		setDescription("Test modify and commit in EntityManager");
    }

    //reset gets called twice on error
    protected boolean reset = false;
    
    public Integer[] empIDs = new Integer[3];
    public Integer[] projIDs = new Integer[2];
    
    public void setup (){
        super.setup();
        this.reset = true;

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
    
    public void reset (){
        if (reset){
            reset = false;
        }
        super.reset();
    }
    
    public void test(){
        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[1]);
            Project project = getEntityManager().find(Project.class, projIDs[1]);
            project.addTeamMember(employee);
            employee.addProject(project);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring assignment of employee to a project" + ex);
        }

        try {
            beginTransaction();
            Employee managed = getEntityManager().find(Employee.class, empIDs[1]);
            Employee manager = getEntityManager().find(Employee.class, empIDs[2]);
            manager.addManagedEmployee(managed);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring assignement of employe to a manager" + ex);
        }
 
        try {
            beginTransaction();
            PhoneNumber phone = ModelExamples.phoneExample4();
            getEntityManager().persist(phone);
            Employee employee = getEntityManager().find(Employee.class, empIDs[2]);
            employee.addPhoneNumber(phone);
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
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring employee raises" + ex);
        }

        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[0]);
            employee.setFirstName("Wilfred");
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring the change of employee's name" + ex);
        }

        try {
            beginTransaction();
            Address address = ModelExamples.addressExample4();
            getEntityManager().persist(address);
            Employee employee = getEntityManager().find(Employee.class, empIDs[2]);
            employee.setAddress(address);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring the change of employee's address" + ex);
        }

        try {
            beginTransaction();
            EmploymentPeriod period = new EmploymentPeriod();
            Employee employee = getEntityManager().find(Employee.class, empIDs[2]);
            period.setStartDate(employee.getPeriod().getStartDate());
            period.setEndDate(new Date(System.currentTimeMillis()));
            employee.setPeriod(period);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring the firing of an employee" + ex);
        }

        try {
            beginTransaction();
            Employee employee = getEntityManager().find(Employee.class, empIDs[2]);
            employee.setSalary(0);
            commitTransaction();
        } catch (Exception ex) {
            rollbackTransaction();
            throw new TestErrorException("Exception thrown durring setting of employee's salary" + ex);
        }
    }    

    public void verify(){
        Employee employee = getEntityManager().find(Employee.class, empIDs[0]);
        if ( (!employee.getFirstName().equals("Wilfred"))){
            throw new TestErrorException("Employee ID :" + empIDs[0] + " First Name not Updated");
        }
        if (employee.getSalary() <= 15000){
            throw new TestErrorException("Employee ID :" + empIDs[0] + " Salary Not Updated");
        }

        employee = getEntityManager().find(Employee.class, empIDs[2]);
        if ( employee.getManagedEmployees().size() <= 0) {
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Managed Employees not Updated");
        }
        if (employee.getPhoneNumbers().size() <= 2){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " PhoneNumbers not Updated");
        }
        if (!employee.getAddress().getStreet().equals("324 Bay Street")){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Address not Updated");
        }
        if (employee.getPeriod().getEndDate().getTime() > System.currentTimeMillis()){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " EndDate not Updated");
        }
        if (employee.getSalary() != 0){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Salary Not updated");
        }

        employee = getEntityManager().find(Employee.class, empIDs[1]);
        if ( !(((Project)((List)employee.getProjects()).get(1)).getName().equals("Feline Demographics Assesment")) ) {
            // Since mapping does not have an oder-by, order cannot be gaurenteed,
            // with change tracking projects comes from database as add does not instantiate.
            if ( !(((Project)((List)employee.getProjects()).get(0)).getName().equals("Feline Demographics Assesment")) ) {
                throw new TestErrorException("Employee ID :" + empIDs[1] + " Projects not Updated");
            }
        }
        if (!employee.getManager().getId().equals(empIDs[2])){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Manager not Updated");
        }
        if (employee.getSalary() <= 1000) {
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Salary Not updated");
        }
        
        //lets initialize the identity map to make sure they were persisted
        ((JpaEntityManager)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
            
        employee = getEntityManager().find(Employee.class, empIDs[0]);
        if ( (!employee.getFirstName().equals("Wilfred"))){
            throw new TestErrorException("Employee ID :" + empIDs[0] + " First Name not Updated on Database");
        }
        if (employee.getSalary() <= 15000){
            throw new TestErrorException("Employee ID :" + empIDs[0] + " Salary Not Updated on Database");
        }

        employee = getEntityManager().find(Employee.class, empIDs[2]);
        if ( employee.getManagedEmployees().size() <= 0) {
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Managed Employees not Updated on Database");
        }
        if (employee.getPhoneNumbers().size() <= 2){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " PhoneNumbers not Updated on Database");
        }
        if (!employee.getAddress().getStreet().equals("324 Bay Street")){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Address not Updated on Database");
        }
        if (employee.getPeriod().getEndDate().getTime() > System.currentTimeMillis()){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " EndDate not Updated on Database");
        }
        if (employee.getSalary() != 0){
            throw new TestErrorException("Employee ID :" + empIDs[2] + " Salary Not updated on Database");
        }

        employee = getEntityManager().find(Employee.class, empIDs[1]);
        if ( ((List)employee.getProjects()).size() != 2) {
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Projects not Updated on Database");
        }
        if (!employee.getManager().getId().equals(empIDs[2])){
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Manager not Updated on Database");
        }
        if (employee.getSalary() <= 1000) {
            throw new TestErrorException("Employee ID :" + empIDs[1] + " Salary Not updated on Database");
        }
        
    }
}
