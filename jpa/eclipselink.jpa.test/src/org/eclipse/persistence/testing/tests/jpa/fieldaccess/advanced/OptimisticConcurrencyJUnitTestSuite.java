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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.*;
import org.eclipse.persistence.exceptions.OptimisticLockException;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.PersistenceException;

/**
 * <p>
 * <b>Purpose</b>: Test TopLink's EJB 3.0 optimistic concurrency functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite and adds tests to the
 * suite. The database gets initialized prior to the test methods.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for TopLink's EJB 3.0 optimistic concurrency functionality.
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator
 */
 public class OptimisticConcurrencyJUnitTestSuite extends JUnitTestCase {

    public OptimisticConcurrencyJUnitTestSuite() {
        super();
    }

    public OptimisticConcurrencyJUnitTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Optimistic Concurrency (fieldaccess)");
        suite.addTest(new OptimisticConcurrencyJUnitTestSuite("testSetup"));
        suite.addTest(new OptimisticConcurrencyJUnitTestSuite("testCreateProjects"));
        suite.addTest(new OptimisticConcurrencyJUnitTestSuite("testCreateEmployeeWithFlush"));
        suite.addTest(new OptimisticConcurrencyJUnitTestSuite("testVersionUpdateWithCorrectValue"));
        suite.addTest(new OptimisticConcurrencyJUnitTestSuite("testVersionUpdateWithIncorrectValue"));
        suite.addTest(new OptimisticConcurrencyJUnitTestSuite("testVersionUpdateWithNullValue"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession("fieldaccess"));
        clearCache("fieldaccess");
    }
    
    /**
     * Creates two projects used in later tests.
     */
    public void testCreateProjects() {
        EntityManager em = createEntityManager("fieldaccess");
        Project project1, project2;

        beginTransaction(em);
        project1 = ModelExamples.projectExample1();
        project2 = ModelExamples.projectExample2();
        em.persist(project1);
        em.persist(project2);
        commitTransaction(em);
    }

    /**
     * test for issue 635: NullPointerException occuring for Object typed version fields.
     * Employee has a write lock (version) field of type Integer
     * The NullPointerException is thrown when comparing versions in
     * ObjectChangeSet#compareWriteLockValues
     */
    public void testCreateEmployeeWithFlush() {
        EntityManager em = createEntityManager("fieldaccess");
        Project project1, project2;
        Employee employee;

        try {
            beginTransaction(em);
            employee = ModelExamples.employeeExample1();
            em.persist(employee);

            // first flush: Employee is written to the database
            Query query = em.createNamedQuery("findFieldAccessProjectByName");
            query.setParameter("name", "Farmer effecency evaluations");
            project1 = (Project) query.getSingleResult();
            employee.getProjects().add(project1);

            // second flush: Employee is modified, but
            // no update to EMPLOYEE table; only join table entry is written
            query = em.createNamedQuery("findFieldAccessProjectByName");
            query.setParameter("name", "Feline Demographics Assesment");
            project2 = (Project) query.getSingleResult();
            employee.getProjects().add(project2);

            // third flush: Employee is modified, but
            // no update to EMPLOYEE table; only join table entry is written
            // A NullPointerException in ObjectChangeSet#compareWriteLockValues
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * test: updating the version field with the in-memory value.
     * This should be allowed; there's no change for TopLink to detect this.
     */
    public void testVersionUpdateWithCorrectValue() {
        EntityManager em = createEntityManager("fieldaccess");
        Employee employee;

        try {
            beginTransaction(em);
            employee = ModelExamples.employeeExample1();
            em.persist(employee);
            commitTransaction(em);

            beginTransaction(em);
            employee.setVersion(1);
            commitTransaction(em);
        } catch (RuntimeException re) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw re;
        }
    }

    /**
     * test: updating the version field with value != in-memory value.
     * This should throw an OptimisticLockException
     */
    public void testVersionUpdateWithIncorrectValue() {
        EntityManager em = createEntityManager("fieldaccess");
        Employee employee;

        try {
            beginTransaction(em);
            employee = ModelExamples.employeeExample1();
            em.persist(employee);
            commitTransaction(em);

            beginTransaction(em);
            Employee employee1 = em.find(Employee.class, employee.getId());
            employee1.setVersion(2);
            commitTransaction(em);
            fail("updating object version with wrong value didn't throw exception");
        } catch (PersistenceException pe) {
            // expected behavior
        } catch (Exception exception) {
            Throwable persistenceException = exception;
            // Remove an wrapping exceptions such as rollback, runtime, etc.
            while (persistenceException != null && !(persistenceException instanceof OptimisticLockException)) {
                // In the server this is always a rollback exception, need to get nested exception.
                persistenceException = persistenceException.getCause();
            }
            if (persistenceException instanceof OptimisticLockException) {
                return;
            } else {
                fail("updating object version with wrong value threw a wrong exception: " + exception.getMessage());
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * test: updating the version field with null value.
     * This should throw an exception
     */
    public void testVersionUpdateWithNullValue() {
        EntityManager em = createEntityManager("fieldaccess");
        Employee employee;

        try {
            beginTransaction(em);
            employee = ModelExamples.employeeExample1();
            em.persist(employee);
            commitTransaction(em);

            beginTransaction(em);
            Employee employee2 = em.find(Employee.class, employee.getId());
            employee2.setVersion(null);
            commitTransaction(em);
            fail("employee2.setVersion(null) didn't throw exception");
        } catch (PersistenceException pe) {
            // expected behavior
        } catch (Exception exception) {
            Throwable persistenceException = exception;
            // Remove an wrapping exceptions such as rollback, runtime, etc.
            while (persistenceException != null && !(persistenceException instanceof OptimisticLockException)) {
                // In the server this is always a rollback exception, need to get nested exception.
                persistenceException = persistenceException.getCause();
            }
            if (persistenceException instanceof OptimisticLockException) {
                return;
            } else {
                fail("employee2.setVersion(null) threw a wrong exception: " + exception.getMessage());
            }
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
