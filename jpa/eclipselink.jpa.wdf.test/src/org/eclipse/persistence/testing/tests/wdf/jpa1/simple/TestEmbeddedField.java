/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1.simple;

import java.util.Date;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Department;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.Employee;
import org.eclipse.persistence.testing.models.wdf.jpa1.employee.EmploymentPeriod;
import org.eclipse.persistence.testing.tests.wdf.jpa1.JPA1Base;
import org.junit.Test;

public class TestEmbeddedField extends JPA1Base {

    private static final long MYSQL_TIMESTAMP_PRECISION = 1000;

    @Test
    public void testSimple() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            final Integer employeeId = new Integer(25);
            final Department department = new Department(9, "R&D");
            final Employee employee = new Employee(employeeId.intValue(), "Emil", "Bahr", department);
            final EmploymentPeriod period = new EmploymentPeriod();
            final Date startDate = new Date((System.currentTimeMillis() / MYSQL_TIMESTAMP_PRECISION)
                    * MYSQL_TIMESTAMP_PRECISION);
            period.setStartDate(startDate);
            employee.setEmploymentPeriod(period);
            env.beginTransaction(em);
            em.persist(department);
            em.persist(employee);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee savedEmployee = em.find(Employee.class, employeeId);
            verify(savedEmployee != null, "employee is null");
            verify(savedEmployee.getEmploymentPeriod() != null, "employmentPeriod is null");
            verify(savedEmployee.getEmploymentPeriod().getStartDate() != null, "startDate is null");
            final long retrievedStartTime = savedEmployee.getEmploymentPeriod().getStartDate().getTime();
            verify(retrievedStartTime == startDate.getTime(),
                    "Employee.period.startDate differs from original value (inserted " + startDate.getTime() + ", retrieved "
                            + retrievedStartTime + ").");
            em.remove(savedEmployee);
            em.remove(em.merge(department));
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testNullPeriod() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            final Integer employeeId = new Integer(26);
            final Department department = new Department(10, "R&D");
            final Employee employee = new Employee(employeeId.intValue(), "Emil", "Bahr", department);
            env.beginTransaction(em);
            em.persist(department);
            em.persist(employee);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee employeeFirst = em.find(Employee.class, employeeId);
            verify(employeeFirst != null, "employee is null");
            final EmploymentPeriod periodFirst = employeeFirst.getEmploymentPeriod();
            if (periodFirst != null) {
                verify(periodFirst.getStartDate() == null, "got a value for start date: " + periodFirst.getStartDate());
                verify(periodFirst.getEndDate() == null, "got a value for end date: " + periodFirst.getEndDate());
            }
            employeeFirst.setEmploymentPeriod(new EmploymentPeriod());
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee employeeSecond = em.find(Employee.class, employeeId);
            verify(employeeSecond != null, "employee is null");
            final EmploymentPeriod periodSecond = employeeSecond.getEmploymentPeriod();
            if (periodSecond != null) {
                verify(periodSecond.getStartDate() == null, "got a value for start date: " + periodSecond.getStartDate());
                verify(periodSecond.getEndDate() == null, "got a value for end date: " + periodSecond.getEndDate());
            }
            em.remove(employeeSecond);
            em.remove(em.merge(department));
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }

    @Test
    public void testDirty() {
        final JPAEnvironment env = getEnvironment();
        final EntityManager em = env.getEntityManager();
        try {
            final Integer employeeId = new Integer(27);
            final Department department = new Department(11, "R&D");
            final Employee employee = new Employee(employeeId.intValue(), "Emil", "Bahr", department);
            final EmploymentPeriod period = new EmploymentPeriod();
            final Date startDate = new Date((System.currentTimeMillis() / MYSQL_TIMESTAMP_PRECISION)
                    * MYSQL_TIMESTAMP_PRECISION);
            final long INC = 1000L;
            period.setStartDate(startDate);
            employee.setEmploymentPeriod(period);
            env.beginTransaction(em);
            em.persist(department);
            em.persist(employee);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee savedEmployee = em.find(Employee.class, employeeId);
            if (savedEmployee.getEmploymentPeriod() == null) {
                // its broken, makes no sense to continue this test
                env.rollbackTransactionAndClear(em);
                flop("embedded field employmentPeriod is null");
            }
            final long retrievedStartTime = savedEmployee.getEmploymentPeriod().getStartDate().getTime();
            verify(retrievedStartTime == startDate.getTime(),
                    "Employee.period.startDate differs from original value (inserted " + startDate.getTime() + ", retrieved "
                            + retrievedStartTime + ").");
            // now lets see if a change of the -mutable- date gets noticed.
            savedEmployee.getEmploymentPeriod().getStartDate().setTime(retrievedStartTime + INC);
            env.commitTransactionAndClear(em);
            env.beginTransaction(em);
            final Employee changedEmployee = em.find(Employee.class, employeeId);
            verify(retrievedStartTime + INC == changedEmployee.getEmploymentPeriod().getStartDate().getTime(),
                    "wrong startDate of employment (expected " + (retrievedStartTime + INC) + ", retrieved "
                            + changedEmployee.getEmploymentPeriod().getStartDate().getTime() + ").");
            em.remove(em.merge(savedEmployee));
            em.remove(em.merge(department));
            env.commitTransactionAndClear(em);
        } finally {
            closeEntityManager(em);
        }
    }
}
