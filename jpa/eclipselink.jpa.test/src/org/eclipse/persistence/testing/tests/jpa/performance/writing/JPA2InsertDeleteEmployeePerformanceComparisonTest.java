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
 package org.eclipse.persistence.testing.tests.jpa.performance.writing;

import javax.persistence.*;

import org.eclipse.persistence.testing.models.jpa.performance2.Address;
import org.eclipse.persistence.testing.models.jpa.performance2.Employee;
import org.eclipse.persistence.testing.models.jpa.performance2.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.performance2.Gender;
import org.eclipse.persistence.testing.models.jpa.performance2.JobTitle;
import org.eclipse.persistence.testing.models.jpa.performance2.PhoneNumber;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of inserting Employee.
 */
public class JPA2InsertDeleteEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    public JPA2InsertDeleteEmployeePerformanceComparisonTest() {
        setDescription("This test compares the performance of insert Employee.");
    }

    /**
     * Read an existing employee for emulated database run.
     */
    public void setup() {
        EntityManager manager = createEntityManager();
        Employee any = (Employee)manager.createQuery("Select e from Employee e").getResultList().get(0);
        // Create a query to avoid a cache hit to load emulated data.
        Query query = manager.createQuery("Select e from Employee e where e.id = :id");
        query.setParameter("id", new Long(any.getId()));
        any = (Employee)query.getSingleResult();
        manager.close();
        manager = createEntityManager();
        // Also call find, as may use different SQL.
        any = manager.find(Employee.class, any.getId());
        manager.close();
    }
    
    /**
     * Insert employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        Employee employee = new Employee();
        employee.setFirstName("NewGuy");
        employee.setGender(Gender.Male);
        employee.setLastName("Doe");
        employee.setSalary(100000);
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        employmentPeriod.setEndDate(1895, 1, 1);
        employmentPeriod.setStartDate(1901, 12, 31);
        employee.setPeriod(employmentPeriod);
        employee.setAddress(new Address());
        employee.getAddress().setCity("Nepean");
        employee.getAddress().setPostalCode("N5J2N5");
        employee.getAddress().setProvince("ON");
        employee.getAddress().setStreet("Hasting Perf");
        employee.getAddress().setCountry("Canada");
        employee.addPhoneNumber(new PhoneNumber("Work Fax", "613", "9991111"));
        employee.addPhoneNumber(new PhoneNumber("Home", "613", "9991111"));
        employee.setJobTitle(new JobTitle("CEO"));
        employee.addDegree("HighSchool");
        employee.addEmailAddress("work", "ceo@foo.com");
        employee.addResponsibility("code");
        manager.persist(employee);
        manager.getTransaction().commit();
        manager.close();

        manager = createEntityManager();
        manager.getTransaction().begin();
        employee = manager.getReference(Employee.class, new Long(employee.getId()));
        manager.remove(employee);
        manager.getTransaction().commit();
        manager.close();
    }
}
