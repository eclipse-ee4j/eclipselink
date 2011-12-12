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
 *     James Sutherland - initial impl
 ******************************************************************************/  
 package org.eclipse.persistence.testing.models.jpa.performance;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.jpa.JpaEntityManager;

/**
 * EmployeeService session bean.
 */
@Stateless(mappedName="EmployeeService")
@Remote(EmployeeService.class)
public class EmployeeServiceBean implements EmployeeService {
    @PersistenceContext(name="performance")
    protected EntityManager entityManager;

    public List findAll() {
        Query query = this.entityManager.createQuery("Select e from Employee e");
        return query.getResultList();
    }
    
    public Employee findById(long id) {
        Employee employee = this.entityManager.find(Employee.class, id);
        employee.getAddress();
        // Used to test impact of caching with more complex objects.
        //employee.getProjects().size();
        //employee.getManagedEmployees().size();
        //employee.getManager();
        //employee.getPhoneNumbers().size();
        return employee;
    }
    
    public Employee fetchById(long id) {
        Employee employee = this.entityManager.find(Employee.class, id);
        employee.getAddress();
        employee.getManager();
        return employee;
    }
    
    public void update(Employee employee) {
        this.entityManager.merge(employee);
    }
    
    public long insert(Employee employee) {
        this.entityManager.persist(employee);
        this.entityManager.flush();
        return employee.getId();
    }
    
    public void createTables() {
        new EmployeeTableCreator().replaceTables(((JpaEntityManager)this.entityManager.getDelegate()).getServerSession());
        //((JpaEntityManager)this.entityManager.getDelegate()).getServerSession().logout();
        //((JpaEntityManager)this.entityManager.getDelegate()).getServerSession().login();
    }
    
    public void populate() {
        // Populate database.
        for (int j = 0; j < 1000; j++) { //1000
            Employee empInsert = new Employee();
            empInsert.setFirstName("Brendan");
            empInsert.setMale();
            empInsert.setLastName("" + j + "");
            empInsert.setSalary(100000);
            EmploymentPeriod employmentPeriod = new EmploymentPeriod();
            java.sql.Date startDate = Helper.dateFromString("1901-12-31");
            java.sql.Date endDate = Helper.dateFromString("1895-01-01");
            employmentPeriod.setEndDate(startDate);
            employmentPeriod.setStartDate(endDate);
            empInsert.setPeriod(employmentPeriod);
            empInsert.setAddress(new Address());
            empInsert.getAddress().setCity("Nepean");
            empInsert.getAddress().setPostalCode("N5J2N5");
            empInsert.getAddress().setProvince("ON");
            empInsert.getAddress().setStreet("1111 Mountain Blvd. Floor 13, suite " + j);
            empInsert.getAddress().setCountry("Canada");
            empInsert.addPhoneNumber(new PhoneNumber("Work Fax", "613", "2255943"));
            empInsert.addPhoneNumber(new PhoneNumber("Home", "613", "2224599"));
            this.entityManager.persist(empInsert);
        }

        for (int j = 0; j < 50; j++) { //50
            Project project = new SmallProject();
            project.setName("Tracker");
            this.entityManager.persist(project);
            project = new LargeProject();
            project.setName("Tracker");
            this.entityManager.persist(project);
        }
    }
}
