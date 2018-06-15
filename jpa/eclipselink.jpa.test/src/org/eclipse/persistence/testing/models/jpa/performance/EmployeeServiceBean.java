/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James Sutherland - initial impl
 package org.eclipse.persistence.testing.models.jpa.performance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

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

    /**
     * Batch find used to off load driver machine.
     * Simulates n find requests.
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void batchFind(long ids[]) {
        for (long id : ids) {
            Employee employee = findById(id);
            serialize(employee);
        }
    }

    /**
     * Batch find used to off load driver machine.
     * Simulates n find requests.
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int batchUpdate(long ids[], int retry) {
        int errors = 0;
        for (long id : ids) {
            boolean success = false;
            int attempt = 1;
            while (!success && (attempt <= retry)) {
                attempt++;
                beginTransaction();
                Employee employee = findById(id);
                commitTransaction();
                byte[] bytes = (byte[])serialize(employee);
                try {
                    employee = (Employee)deserialize(bytes);
                    int random = (int)(Math.random() * 1000000);
                    employee.setFirstName(String.valueOf(random));
                    employee.setLastName(String.valueOf(random));
                    employee.setSalary(random);
                    beginTransaction();
                    try {
                        update(employee);
                    } finally {
                        commitTransaction();
                    }
                    success = true;
                } catch (Exception failed) {
                    System.out.println(failed.toString());
                    errors++;
                }
            }
        }
        return errors;
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

    /**
     * Start a new JTA transaction.
     */
    public void beginTransaction() {
        try {
            getUserTransaction().begin();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Commit the existing JTA transaction.
     */
    public void commitTransaction() {
        try {
            getUserTransaction().commit();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Roll back the existing JTA transaction.
     */
    public void rollbackTransaction() {
        try {
            getUserTransaction().rollback();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public UserTransaction getUserTransaction() {
        try {
            return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        } catch (NamingException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Object deserialize(Object bytes) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream((byte[])bytes);
        try {
            ObjectInputStream objectIn = new ObjectInputStream(byteIn);
            return objectIn.readObject();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public Object serialize(Object object) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(object);
            objectOut.flush();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return byteOut.toByteArray();
    }
}
