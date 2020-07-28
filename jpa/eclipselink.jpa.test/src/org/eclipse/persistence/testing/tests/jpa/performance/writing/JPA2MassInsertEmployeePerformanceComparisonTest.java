/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
public class JPA2MassInsertEmployeePerformanceComparisonTest extends PerformanceRegressionTestCase {
    public JPA2MassInsertEmployeePerformanceComparisonTest() {
        setDescription("This test compares the performance of insert Employee.");
    }

    /**
     * Delete all employees.
     */
    public void reset() {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        manager.createQuery("Delete from Email where address = 'ceo@foo.com'").executeUpdate();
        manager.createQuery("Delete from Degree where name = 'HighSchool'").executeUpdate();
        manager.createQuery("Delete from PhoneNumber where number = '9991111'").executeUpdate();
        manager.createQuery("Delete from Employee where firstName = 'NewGuy'").executeUpdate();
        manager.createQuery("Delete from Address where street = 'Hasting Perf'").executeUpdate();
        manager.createQuery("Delete from JobTitle where name = 'CEO'").executeUpdate();
        manager.getTransaction().commit();
        manager.close();
    }

    /**
     * Insert employee.
     */
    public void test() throws Exception {
        EntityManager manager = createEntityManager();
        manager.getTransaction().begin();
        for (int index = 0; index < 50; index++) {
            Employee empInsert = new Employee();
            empInsert.setFirstName("NewGuy");
            empInsert.setGender(Gender.Male);
            empInsert.setLastName("Doe");
            empInsert.setSalary(100000);
            EmploymentPeriod employmentPeriod = new EmploymentPeriod();
            employmentPeriod.setEndDate(1895, 1, 1);
            employmentPeriod.setStartDate(1901, 12, 31);
            empInsert.setPeriod(employmentPeriod);
            empInsert.setAddress(new Address());
            empInsert.getAddress().setCity("Nepean");
            empInsert.getAddress().setPostalCode("N5J2N5");
            empInsert.getAddress().setProvince("ON");
            empInsert.getAddress().setStreet("Hasting Perf");
            empInsert.getAddress().setCountry("Canada");
            empInsert.addPhoneNumber(new PhoneNumber("Work Fax", "613", "9991111"));
            empInsert.addPhoneNumber(new PhoneNumber("Home", "613", "9991111"));
            empInsert.setJobTitle(new JobTitle("CEO"));
            empInsert.addDegree("HighSchool");
            empInsert.addEmailAddress("work", "ceo@foo.com");
            empInsert.addResponsibility("code");
            manager.persist(empInsert);
        }
        manager.getTransaction().commit();
        manager.close();
    }
}
