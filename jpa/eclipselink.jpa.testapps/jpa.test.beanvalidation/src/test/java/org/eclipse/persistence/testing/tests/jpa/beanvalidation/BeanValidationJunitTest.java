/*
 * Copyright (c) 2009, 2024 Oracle and/or its affiliates. All rights reserved.
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
//      Marcel Valovy
package org.eclipse.persistence.testing.tests.jpa.beanvalidation;

import java.util.Vector;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.RollbackException;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.Address;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.BeanValidationTableCreator;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.Employee;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.Project;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BeanValidationJunitTest extends JUnitTestCase {

    public static final int EMPLOYEE_PK = 1;
    public static final int PROJECT_PK = 1;

    public BeanValidationJunitTest() {
        super();
    }

    public BeanValidationJunitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("BeanValidationJunitTest");
        suite.addTest(new BeanValidationJunitTest("testSetup"));
        suite.addTest(new BeanValidationJunitTest("testPersistWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testPersistWithInvalidMethodData"));
//        suite.addTest(new BeanValidationJunitTest("testPersistWithInvalidConstructorParameterData"));
        suite.addTest(new BeanValidationJunitTest("testEmbeddedWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testUpdateWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testRemoveWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testTraversableResolverPreventsLoadingOfLazyRelationships"));
        suite.addTest(new BeanValidationJunitTest("testTraversableResolverPreventsTraversingRelationshipMultipleTimes"));
        suite.addTest(new BeanValidationJunitTest("testValidateChangedData"));
        suite.addTest(new BeanValidationJunitTest("testPessimisticLockWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testPessimisticLockUpdateObjectWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testPessimisticLockUpdateObjectWithValidData"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new BeanValidationTableCreator().replaceTables(JUnitTestCase.getServerSession("beanvalidation"));
        EntityManager em = createEntityManager();
        createEmployeeProject(em);
    }

    public void createEmployeeProject(EntityManager em){
        try {
            beginTransaction(em);
            Employee e1 = new Employee(EMPLOYEE_PK, getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1),
                    getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1), 1337);
            Project p1 = new Project(PROJECT_PK, "proj");
            Project p2 = new Project(PROJECT_PK + 1, "proj");
            em.persist(p1);
            em.persist(p2);

            Collection<Project> projects = new ArrayList<>();
            projects.add(p1);
            projects.add(p2);
            e1.setProjects(projects);
            e1.setManagedProject(p1);

            em.persist(e1);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    public String getFilledStringOfLength(int length) {
        char[] stringChars = new char[length];
        Arrays.fill(stringChars, 'a');
        return new String(stringChars);
    }

    /**
     * Strategy:
     * 1. Persist an object with invalid value
     * 2. Assert - a ConstraintViolationException is raised
     * 3. Assert - transaction is rolled back
     * 4. Assert - The validation exception is due to invalid value given by us.
     */
    public void testPersistWithInvalidData() {
        EntityManager em = createEntityManager();
        boolean gotConstraintViolations = false;
        String invalidName = getFilledStringOfLength(Employee.NAME_MAX_SIZSE + 1);
        String validName = getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1);
        try {
            // Persist an object with invalid value
            beginTransaction(em);
            Employee e1 = new Employee(100, invalidName, validName, 1337);
            em.persist(e1);
        } catch (ConstraintViolationException e) {
            assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em)) ;
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            ConstraintViolation<?> constraintViolation = constraintViolations.iterator().next();
            Object invalidValue = constraintViolation.getInvalidValue();
            assertEquals("Invalid value should be " + invalidName, invalidName, invalidValue);
            gotConstraintViolations = true;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        assertTrue("Did not get Constraint Violation while persisting invalid data ", gotConstraintViolations);

    }

    /**
     * Strategy:
     * 1. Persist an object with invalid value
     * 2. Assert - a ConstraintViolationException is raised
     * 3. Assert - transaction is rolled back
     * 4. Assert - The validation exception is due to invalid value given by us.
     */
    public void testPersistWithInvalidMethodData() {
        EntityManager em = createEntityManager();
        boolean gotConstraintViolations = false;
        String invalidName = getFilledStringOfLength(Employee.NAME_MAX_SIZSE + 1);
        String validName = getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1);
        try {
            // Persist an object with invalid value
            beginTransaction(em);
            Employee e1 = new Employee(100, validName, invalidName, 1337);
            em.persist(e1);
        } catch (ConstraintViolationException e) {
            assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em)) ;
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            ConstraintViolation<?> constraintViolation = constraintViolations.iterator().next();
            Object invalidValue = constraintViolation.getInvalidValue();
            assertEquals("Invalid value should be " + invalidName, invalidName, invalidValue);
            gotConstraintViolations = true;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        assertTrue("Did not get Constraint Violation while persisting invalid data ", gotConstraintViolations);

    }

    /* This test unfortunately doesn't pass now, because HV 5.1.0.Final does not correctly detect constructor
     constraints. See similar test in moxy, where through reflection we check correctness of BeanValidationHelper.
     The test case is BeanValidationSpecialtiesTestCase#testConstructorAnnotations.*/
    /**
     * Strategy:
     * 1. Persist an object with invalid value
     * 2. Assert - a ConstraintViolationException is raised
     * 3. Assert - transaction is rolled back
     * 4. Assert - The validation exception is due to invalid value given by us.
     */
    public void testPersistWithInvalidConstructorParameterData() {
        EntityManager em = createEntityManager();
        boolean gotConstraintViolations = false;
        String invalidName = getFilledStringOfLength(Employee.NAME_MAX_SIZSE + 1);
        String validName = getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1);
        long low_salary = "too_low".compareTo("1337_salary");
        try {
            // Persist an object with invalid value
            beginTransaction(em);
            Employee e1 = new Employee(100, validName, validName, low_salary);
            em.persist(e1);
        } catch (ConstraintViolationException e) {
            assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em)) ;
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            ConstraintViolation<?> constraintViolation = constraintViolations.iterator().next();
            Object invalidValue = constraintViolation.getInvalidValue();
            assertEquals("Invalid value should be " + low_salary, invalidName, invalidValue);
            gotConstraintViolations = true;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        assertTrue("Did not get Constraint Violation while persisting invalid data ", gotConstraintViolations);

    }

    /**
     * Strategy:
     * 1. Persist an object with invalid value
     * 2. Assert - a ConstraintViolationException is raised
     * 3. Assert - transaction is rolled back
     * 4. Assert - The validation exception is due to invalid value given by us.
     */
    public void testEmbeddedWithInvalidData() {
        EntityManager em = createEntityManager();
        boolean gotConstraintViolations = false;
        try {
            // Persist an object with invalid value
            beginTransaction(em);
            Employee e1 = new Employee(100, "name", "name", 1337);
            Address address = new Address("street", "city", "state" /*passing invalid value for state */);
            e1.setAddress(address);
            em.persist(e1);
        } catch (ConstraintViolationException e) {
            assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em)) ;
            gotConstraintViolations = true;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        assertTrue("Did not get Constraint Violation while persisting invalid data ", gotConstraintViolations);
    }

    /**
     * Strategy:
     * 1. Persist an object with valid values
     * 2. Update it with invalid data and commit transaction
     * 3. Assert - a Persistence Exception  is raised
     * 4. Assert - Cause of the Persistence exception is ConstraintViolationException
     * 5  Assert - The ConstraintViolationException exception is due to invalid value given by us.
     * 6  Assert - transaction is marked for roll back

     */
    public void testUpdateWithInvalidData() {
        EntityManager em = createEntityManager();
        boolean gotConstraintViolations = false;
        final String invalidName = getFilledStringOfLength(Employee.NAME_MAX_SIZSE + 1 );
        try {
            // Update it with invalid value
            beginTransaction(em);
            Employee e = em.find(Employee.class, EMPLOYEE_PK);
            e.setName(invalidName);
            commitTransaction(em);
          } catch (RuntimeException e) {
            assertFalse("Transaction not marked for roll back when ConstraintViolation is thrown", isTransactionActive(em));
            Throwable cause = e.getCause();
            while(cause != null) {
                if (cause instanceof ConstraintViolationException cve){
                    Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
                    ConstraintViolation<?> constraintViolation = constraintViolations.iterator().next();
                    assertEquals("Invalid value should be " + invalidName, invalidName, constraintViolation.getInvalidValue());
                    gotConstraintViolations = true;
                    break;
                }else
                    cause = cause.getCause();
            }
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        assertTrue("Did not get Constraint Violation while updating with invalid data ", gotConstraintViolations);
    }

    public void testRemoveWithInvalidData() {
        EntityManager em = createEntityManager();
        boolean removeSuccessfull = false;
        final int EMPLOYEE_PK_TO_REMOVE = 2;

        try {
            beginTransaction(em);
            Employee e1 = new Employee(EMPLOYEE_PK_TO_REMOVE, getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1 ),
                    getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1 ), 1337);
            em.persist(e1);
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        }

        try {
            beginTransaction(em);
            Employee e = em.find(Employee.class, EMPLOYEE_PK_TO_REMOVE);
            e.setName(getFilledStringOfLength(Employee.NAME_MAX_SIZSE + 1 ));
            em.remove(e);
            commitTransaction(em);
            removeSuccessfull = true;
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        }

        closeEntityManager(em);
        assertTrue("Automatic Validation should not be executed for remove", removeSuccessfull);
    }

    /**
     * Strategy:
     * 1. Update an Employee to trigger validation on it
     * 2. Assert no lazy relationships are loaded as side effect
     */
    public void testTraversableResolverPreventsLoadingOfLazyRelationships() {
        //Empty the cache to make sure that lazy fields of Employee are uninstantiated
        clearCache();
        EntityManager em = createEntityManager();

        Employee employee = null;
        try {
            beginTransaction(em);
            employee = em.find(Employee.class, EMPLOYEE_PK);
            employee.setName(getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 2));
            employee.setSurname(getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 2));
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }

        org.eclipse.persistence.sessions.Project project = getDatabaseSession().getProject();
        assertFalse("Lazy field should not be instantiated because of validation", isInstantiated(employee, "projects", project));
        assertFalse("Lazy field should not be instantiated because of validation", isInstantiated(employee, "managedProject", project));
    }

    /**
     * Strategy:
     * 1. Update an Employee and related project to trigger validation on it
     * 2. Assert that the objects are traversed only once
     */
    public void testTraversableResolverPreventsTraversingRelationshipMultipleTimes() {
        // Write a validator that sets value of boolean @Transient flag validationPerformed for each entity that visits it.
        // If it ever finds an entity with such flag set, the entity has visited the validator twice. It should be flagged as error.
    }

    //Bug #411013
    public void testValidateChangedData() {
        try {
            getDatabaseSession().executeNonSelectingSQL("insert into CMP3_BV_PROJECT values (895, \"some long name\")");
        } catch (Throwable t) {
            getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, t);
        }
        clearCache();
        Map<String, Object> props = new HashMap<>();
        props.put("eclipselink.weaving", "false");
        EntityManagerFactory factory = getEntityManagerFactory(props);
        EntityManager em = factory.createEntityManager();
        try {
            beginTransaction(em);
            TypedQuery<Project> query = em.createQuery("select p from CMP3_BV_PROJECT p", Project.class);
            for (Project p: query.getResultList()) {
                System.out.println(p.getName());
            }
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * Strategy:
     * 1. Update an Employee and related project to trigger validation on it
     * 2. Find the object using its primary key, with a pessimistic lock
     * 3. Do not change the object
     * 4. End the transaction
     * 5. The version field should be incremented in the database, but no other changes
     * 6. No validation exceptions should be thrown
     */
    public void testPessimisticLockWithInvalidData() throws Exception {
        try {
            getDatabaseSession().executeNonSelectingSQL("insert into CMP3_BV_TASK values (900, 1, NULL, 1)");
        } catch (Throwable t) {
            getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, t);
        }
        clearCache();
        Map<String, Object> props = new HashMap<>();
        props.put("eclipselink.weaving", "false");
        EntityManagerFactory factory = getEntityManagerFactory(props);
        EntityManager em = factory.createEntityManager();
        try {
            beginTransaction(em);
            
            Task task = em.find(Task.class, 900, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            
            commitTransaction(em);
            
            Vector resultSet = getDatabaseSession().executeSQL("select * from CMP3_BV_TASK where ID=900");
            assertEquals(1, resultSet.size());
            
            final DatabaseRecord dr = (DatabaseRecord) resultSet.firstElement();
            assertEquals(900L, dr.get("ID"));
            assertEquals(2L, dr.get("VERSION")); // should be incremented by the pessimistic lock
            assertNull(dr.get("NAME")); // should be unchanged
            assertEquals(1L, dr.get("PRIORITY")); // should be unchanged
            
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * Strategy:
     * 1. Update an Employee and related project to trigger validation on it
     * 2. Find the object using its primary key, with a pessimistic lock
     * 3. Change the object with still invalid data
     * 4. End the transaction
     * 5. The version field should be incremented in the database, but no other changes
     * 6. A validation exception should be thrown
     */
    public void testPessimisticLockUpdateObjectWithInvalidData() throws Exception {
        try {
            getDatabaseSession().executeNonSelectingSQL("insert into CMP3_BV_TASK values (901, 1, NULL, 1)");
        } catch (Throwable t) {
            getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, t);
        }
        clearCache();
        Map<String, Object> props = new HashMap<>();
        props.put("eclipselink.weaving", "false");
        boolean gotConstraintViolations = false;
        EntityManagerFactory factory = getEntityManagerFactory(props);
        EntityManager em = factory.createEntityManager();
        try {
            beginTransaction(em);
            
            Task task = em.find(Task.class, 901, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            task.setPriority(2);
            
            commitTransaction(em);
        } catch (ConstraintViolationException e) {
            assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em));
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            ConstraintViolation constraintViolation = constraintViolations.iterator().next();
            Object invalidValue = constraintViolation.getInvalidValue();
            System.out.println(invalidValue);
            gotConstraintViolations = true;
        } catch (RollbackException e) {
            e.printStackTrace();
            final ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
            Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
            ConstraintViolation constraintViolation = constraintViolations.iterator().next();
            assertEquals("must not be null", constraintViolation.getMessage());
            gotConstraintViolations = true;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        assertTrue("Did not get Constraint Violation while persisting invalid data ", gotConstraintViolations);
        
        Vector resultSet = getDatabaseSession().executeSQL("select * from CMP3_BV_TASK where ID=901");
        assertEquals(1, resultSet.size());
        
        final DatabaseRecord dr = (DatabaseRecord) resultSet.firstElement();
        assertEquals(901L, dr.get("ID"));
        assertEquals(1L, dr.get("VERSION")); // should be unchanged
        assertNull(dr.get("NAME")); // should be unchanged
        assertEquals(1L, dr.get("PRIORITY")); // should be unchanged
    }

    /**
     * Strategy:
     * 1. Update an Employee and related project to trigger validation on it
     * 2. Find the object using its primary key, with a pessimistic lock
     * 3. Change the object to have valid data
     * 4. End the transaction
     * 5. The version field should be incremented in the database along with the other changes
     * 6. No validation exceptions should be thrown
     */
    public void testPessimisticLockUpdateObjectWithValidData() throws Exception {
        try {
            getDatabaseSession().executeNonSelectingSQL("insert into CMP3_BV_TASK values (902, 1, NULL, 1)");
        } catch (Throwable t) {
            getDatabaseSession().getSessionLog().logThrowable(SessionLog.WARNING, t);
        }
        clearCache();
        Map<String, Object> props = new HashMap<>();
        props.put("eclipselink.weaving", "false");
        boolean gotConstraintViolations = false;
        EntityManagerFactory factory = getEntityManagerFactory(props);
        EntityManager em = factory.createEntityManager();
        try {
            beginTransaction(em);
            
            Task task = em.find(Task.class, 902, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            task.setPriority(2);
            task.setName("Do some work");
            
            commitTransaction(em);
        } catch (ConstraintViolationException e) {
            gotConstraintViolations = true;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        assertFalse("Got Constraint Violation while persisting valid data ", gotConstraintViolations);
        
        Vector resultSet = getDatabaseSession().executeSQL("select * from CMP3_BV_TASK where ID=902");
        assertEquals(1, resultSet.size());
        
        final DatabaseRecord dr = (DatabaseRecord) resultSet.firstElement();
        assertEquals(902L, dr.get("ID"));
        assertEquals(2L, dr.get("VERSION")); // should be incremented by the pessimistic lock
        assertEquals("Do some work", dr.get("NAME")); // new value
        assertEquals(2L, dr.get("PRIORITY")); // new value
    }

    //--------------------Helper Methods ---------------//
    private boolean isInstantiated(Object entityObject, String attributeName, org.eclipse.persistence.sessions.Project project) {
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) project.getDescriptor(Employee.class).getObjectBuilder().getMappingForAttributeName(attributeName);
        Object attributeValue = mapping.getAttributeAccessor().getAttributeValueFromObject(entityObject);
        return mapping.getIndirectionPolicy().objectIsInstantiatedOrChanged(attributeValue);
    }

    @Override
    public String getPersistenceUnitName() {
        return "beanvalidation";
    }
}
