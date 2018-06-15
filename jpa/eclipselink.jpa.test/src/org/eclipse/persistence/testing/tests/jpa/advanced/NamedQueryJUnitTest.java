/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.Equipment;
import org.eclipse.persistence.testing.models.jpa.advanced.EquipmentCode;

public class NamedQueryJUnitTest extends JUnitTestCase {

    /** Persistence unit name. */
    protected String PUName = "MulitPU-1";

    /**
     * Constructs an instance of <code>NamedQueryJUnitTestm</code> class.
     */
    public NamedQueryJUnitTest() {
        super();
    }

    /**
     * Constructs an instance of <code>NamedQueryJUnitTestm</code> class with given test case name.
     * @param name Test case name.
     */
    public NamedQueryJUnitTest(String name) {
        super(name);
        setPuName(PUName);
    }

    /**
     * Build collection of test cases for this jUnit test instance.
     * @return {@link Test} class containing collection of test cases for this jUnit test instance.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new NamedNativeQueryJUnitTest("testSetup"));
        return addNamedQueryTests(suite);
    }

    /**
     * Adds test, similar to suite() but without adding a setup. Used from <code>suite()</code> to add individual tests.
     * @param suite Target {@link TestSuite} class where to store tests.
     * @return {@link Test} class containing collection of test cases for this jUnit test instance.
     */
    public static Test addNamedQueryTests(TestSuite suite){
        suite.setName("NamedQueryJUnitTest");
        suite.addTest(new NamedQueryJUnitTest("testSelectNamedQueryWithNamedParameters"));
        suite.addTest(new NamedQueryJUnitTest("testSelectNamedQueryWithNamedParametersReverseOrder"));
        suite.addTest(new NamedQueryJUnitTest("testSelectNamedQueryWithIndexedParameters"));
        suite.addTest(new NamedQueryJUnitTest("testSelectNamedQueryWithIndexedParametersReverseOrder"));
        return suite;
    }

    /**
     * Initial setup is done as first test in collection, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        System.out.println("testSetup");
        new AdvancedTableCreator().replaceTables(getServerSession(PUName));
        clearCache(PUName);
    }

    /**
     * Persist {@link Employee} entity including all related entities.
     * Transaction must be opened for provided <code>EntityManager</code> instance.
     * @param em       An <code>EntityManager</code> instance used to persist entities.
     * @param employee An <code>Employee</code> entity to be persisted.
     */
    private void persistEmployee(EntityManager em, Employee employee) {
        Collection<Equipment> equipmentColl = employee.getDepartment().getEquipment().values();
        for (Equipment equipment : equipmentColl) {
            EquipmentCode ec = equipment.getEquipmentCode();
            if (ec != null) {
                em.persist(ec);
            }
            em.persist(equipment);
        }
        em.persist(employee.getAddress());
        em.persist(employee.getDepartment());
        em.persist(employee);
    }

    /**
     * Remove {@link Employee} entity including all related entities.
     * Transaction must be opened for provided <code>EntityManager</code> instance.
     * @param em       An <code>EntityManager</code> instance used to remove entities.
     * @param employee An <code>Employee</code> entity to be removed.
     */
    private void removeEmployee(EntityManager em, Employee employee) {
        Collection<Equipment> equipmentColl = employee.getDepartment().getEquipment().values();
        em.remove(employee);
        em.remove(employee.getDepartment());
        em.remove(employee.getAddress());
        for (Equipment equipment : equipmentColl) {
            EquipmentCode ec = equipment.getEquipmentCode();
            em.remove(equipment);
            if (ec != null) {
                em.remove(ec);
            }
        }
    }

   /**
     * Initialize, persist and retrieve some employee and address IDs for testing.
     * @param em An <code>EntityManager</code> instance used to persist sample data.
     * @return Collection of initialized and persisted <code>Employee</code> entities.
     */
    private Collection<Employee> createEmployees(EntityManager em) {
        Collection<Employee> employees = new LinkedList<Employee>();
        // Get some employees.
        beginTransaction(em);
        try {
            EmployeePopulator ep = new EmployeePopulator();
            Employee e1 = ep.basicEmployeeExample1();
            Employee e2 = ep.basicEmployeeExample2();
            Employee e3 = ep.basicEmployeeExample3();
            persistEmployee(em, e1);
            persistEmployee(em, e2);
            persistEmployee(em, e3);
            employees.add(e1);
            employees.add(e2);
            employees.add(e3);
        } catch (RuntimeException ex) {
            rollbackTransaction(em);
            throw ex;
        }
        em.flush();
        commitTransaction(em);
        return employees;
    }

    /**
     * Delete provided <code>Employee</code> entities.
     * @param em        An <code>EntityManager</code> instance used to delete sample data.
     * @param employees Collection of <code>Employee</code> entities to be deleted.
     */
    private void deleteEmployees(EntityManager em, Collection<Employee> employees) {
        beginTransaction(em);
        try {
            for (Employee employee : employees) {
                removeEmployee(em, employee);
            }
        } catch (RuntimeException ex) {
            rollbackTransaction(em);
            throw ex;
        }
           em.flush();
        commitTransaction(em);
    }

    // Bug# 426129 - Verify order of named query named parameters.
    /**
     * Test <code>[Query].setParameter</code> methods order for named query and named parameters.
     * Named query is defined in {@link Employee} entity class.
     */
    public void testSelectNamedQueryWithNamedParameters() {
        // Create sample data.
        EntityManager em = createEntityManager(PUName);
        try {
            // Two dimensional Integer array containing array of [Employee.id, Address.id] pairs.
            Collection<Employee> employees = createEmployees(em);
            // Test queries for every [Employee.id, Address.id] pair.
            for (Employee employee : employees) {
                // Query with parameters set in the same order as they are in query.
                Query query = em.createNamedQuery("employee.findPhoneNumberByEmployeIdAndPhoneNumberIdNamedParams");
                query.setParameter("eId", employee.getId());
                query.setParameter("dId", employee.getDepartment().getId());
                List<Object> results = query.getResultList();
                assertTrue("No records found", results.size() > 0);
                for (Object result : results) {
                    Employee e = (Employee)result;
                    assertEquals("Returned Employee.id does not match ID from query", employee.getId(), e.getId());
                    assertEquals("Returned Department.id does not match ID from query", employee.getDepartment().getId(), e.getDepartment().getId());
                }
                // Query with parameters set in reverse order as they are in query.
                query = em.createNamedQuery("employee.findPhoneNumberByEmployeIdAndPhoneNumberIdNamedParams");
                query.setParameter("dId", employee.getDepartment().getId());
                query.setParameter("eId", employee.getId());
                results = query.getResultList();
                assertTrue("No records found", results.size() > 0);
                for (Object result : results) {
                    Employee e = (Employee)result;
                    assertEquals("Returned Employee.id does not match ID from query", employee.getId(), e.getId());
                    assertEquals("Returned Department.id does not match ID from query", employee.getDepartment().getId(), e.getDepartment().getId());
                }
            }
            // Delete sample data.
            deleteEmployees(em, employees);
        } catch (RuntimeException ex) {
            System.out.printf("EXCEPTION %s in testSelectNamedQueryWithNamedParameters: %s\n", ex.getClass().getSimpleName(), ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } finally {
            em.close();
        }
    }

    // Bug# 426129 - Verify order of named query named parameters with where condition in reverse order.
    /**
     * Test <code>[Query].setParameter</code> methods order for named query and named parameters
     * with where condition in reverse order.
     * Named query is defined in {@link Employee} entity class.
     */
    public void testSelectNamedQueryWithNamedParametersReverseOrder() {
        // Create sample data.
        EntityManager em = createEntityManager(PUName);
        try {
            // Two dimensional Integer array containing array of [Employee.id, Address.id] pairs.
            Collection<Employee> employees = createEmployees(em);
            // Test queries for every [Employee.id, Address.id] pair.
            for (Employee employee : employees) {
                // Query with parameters set in the same order as they are in query.
                Query query = em.createNamedQuery("employee.findPhoneNumberByEmployeIdAndPhoneNumberIdNamedParamsReverseOrder");
                query.setParameter("eId", employee.getId());
                query.setParameter("dId", employee.getDepartment().getId());
                List<Object> results = query.getResultList();
                assertTrue("No records found", results.size() > 0);
                for (Object result : results) {
                    Employee e = (Employee)result;
                    assertEquals("Returned Employee.id does not match ID from query", employee.getId(), e.getId());
                    assertEquals("Returned Department.id does not match ID from query", employee.getDepartment().getId(), e.getDepartment().getId());
                }
                // Query with parameters set in reverse order as they are in query.
                query = em.createNamedQuery("employee.findPhoneNumberByEmployeIdAndPhoneNumberIdNamedParamsReverseOrder");
                query.setParameter("dId", employee.getDepartment().getId());
                query.setParameter("eId", employee.getId());
                results = query.getResultList();
                assertTrue("No records found", results.size() > 0);
                for (Object result : results) {
                    Employee e = (Employee)result;
                    assertEquals("Returned Employee.id does not match ID from query", employee.getId(), e.getId());
                    assertEquals("Returned Department.id does not match ID from query", employee.getDepartment().getId(), e.getDepartment().getId());
                }
            }
            // Delete sample data.
            deleteEmployees(em, employees);
        } catch (RuntimeException ex) {
            System.out.printf("EXCEPTION %s in testSelectNamedQueryWithNamedParametersReverseOrder: %s\n", ex.getClass().getSimpleName(), ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } finally {
            em.close();
        }
    }

    // Bug# 426129 - Verify order of named query indexed parameters.
    /**
     * Test <code>[Query].setParameter</code> methods order for named query and indexed parameters.
     * Named query is defined in {@link Employee} entity class.
     */
    public void testSelectNamedQueryWithIndexedParameters() {
        // Create sample data.
        EntityManager em = createEntityManager(PUName);
        try {
            // Two dimensional Integer array containing array of [Employee.id, Address.id] pairs.
            Collection<Employee> employees = createEmployees(em);
            // Test queries for every [Employee.id, Address.id] pair.
            for (Employee employee : employees) {
                // Query with parameters set in the same order as they are in query.
                Query query = em.createNamedQuery("employee.findPhoneNumberByEmployeIdAndPhoneNumberIdIndexedParams");
                query.setParameter(1, employee.getId());
                query.setParameter(2, employee.getDepartment().getId());
                List<Object> results = query.getResultList();
                assertTrue("No records found", results.size() > 0);
                for (Object result : results) {
                    Employee e = (Employee)result;
                    assertEquals("Returned Employee.id does not match ID from query", employee.getId(), e.getId());
                    assertEquals("Returned Department.id does not match ID from query", employee.getDepartment().getId(), e.getDepartment().getId());
                }
                // Query with parameters set in reverse order as they are in query.
                query = em.createNamedQuery("employee.findPhoneNumberByEmployeIdAndPhoneNumberIdIndexedParams");
                query.setParameter(2, employee.getDepartment().getId());
                query.setParameter(1, employee.getId());
                results = query.getResultList();
                assertTrue("No records found", results.size() > 0);
                for (Object result : results) {
                    Employee e = (Employee)result;
                    assertEquals("Returned Employee.id does not match ID from query", employee.getId(), e.getId());
                    assertEquals("Returned Department.id does not match ID from query", employee.getDepartment().getId(), e.getDepartment().getId());
                }
            }
            // Delete sample data.
            deleteEmployees(em, employees);
        } catch (RuntimeException ex) {
            System.out.printf("EXCEPTION %s in testSelectNamedQueryWithIndexedParameters: %s\n", ex.getClass().getSimpleName(), ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } finally {
            em.close();
        }
    }

    // Bug# 426129 - Verify order of named query indexed parameters with where condition in reverse order.
    /**
     * Test <code>[Query].setParameter</code> methods order for named query and indexed parameters
     * with where condition in reverse order.
     * Named query is defined in {@link Employee} entity class.
     */
    public void testSelectNamedQueryWithIndexedParametersReverseOrder() {
        // Create sample data.
        EntityManager em = createEntityManager(PUName);
        try {
            // Two dimensional Integer array containing array of [Employee.id, Address.id] pairs.
            Collection<Employee> employees = createEmployees(em);
            // Test queries for every [Employee.id, Address.id] pair.
            for (Employee employee : employees) {
                // Query with parameters set in the same order as they are in query.
                Query query = em.createNamedQuery("employee.findPhoneNumberByEmployeIdAndPhoneNumberIdIndexedParamsReverseOrder");
                query.setParameter(2, employee.getId());
                query.setParameter(1, employee.getDepartment().getId());
                List<Object> results = query.getResultList();
                assertTrue("No records found", results.size() > 0);
                for (Object result : results) {
                    Employee e = (Employee)result;
                    assertEquals("Returned Employee.id does not match ID from query", employee.getId(), e.getId());
                    assertEquals("Returned Department.id does not match ID from query", employee.getDepartment().getId(), e.getDepartment().getId());
                }
                // Query with parameters set in reverse order as they are in query.
                query = em.createNamedQuery("employee.findPhoneNumberByEmployeIdAndPhoneNumberIdIndexedParamsReverseOrder");
                query.setParameter(1, employee.getDepartment().getId());
                query.setParameter(2, employee.getId());
                results = query.getResultList();
                assertTrue("No records found", results.size() > 0);
                for (Object result : results) {
                    Employee e = (Employee)result;
                    assertEquals("Returned Employee.id does not match ID from query", employee.getId(), e.getId());
                    assertEquals("Returned Department.id does not match ID from query", employee.getDepartment().getId(), e.getDepartment().getId());
                }
            }
            // Delete sample data.
            deleteEmployees(em, employees);
        } catch (RuntimeException ex) {
            System.out.printf("EXCEPTION %s in testSelectNamedQueryWithIndexedParametersReverseOrder: %s\n", ex.getClass().getSimpleName(), ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } finally {
            em.close();
        }
    }

}
