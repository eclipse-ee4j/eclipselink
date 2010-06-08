/*******************************************************************************
 * Copyright (c) 2009 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.beanvalidation;

import java.util.*;

import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import javax.validation.ConstraintViolation;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.*;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

public class BeanValidationJunitTest extends JUnitTestCase {

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
        suite.addTest(new BeanValidationJunitTest("testEmbeddedWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testUpdateWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testRemoveWithInvalidData"));
        suite.addTest(new BeanValidationJunitTest("testTraversableResolverPreventsLoadingOfLazyRelationships"));
        suite.addTest(new BeanValidationJunitTest("testTraversableResolverPreventsTraversingRelationshipdMultipleTimes"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new BeanValidationTableCreator().replaceTables(JUnitTestCase.getServerSession());
        EntityManager em = createBVEntityManager();
        BeanValidationPopulator.createEmployeeProject(em);
        em.close();
    }


    /**
     * Strategy:
     * 1. Persit an object with invalid value
     * 2. Assert - a ConstraintViolationException is raised
     * 3. Assert - transaction is rolled back
     * 4. Assert - The validation exception is due to invalid value given by us.
     */
    public void testPersistWithInvalidData() {
        EntityManager em = createBVEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean gotConstraintViolations = false;
        String invalidName = BeanValidationPopulator.getFilledStringOfLength(Employee.NAME_MAX_SIZSE + 1);
        try {
            // Persist an object with invalid value
            tx.begin();
            Employee e1 = new Employee(100, invalidName, 1000);
            em.persist(e1);
        } catch (ConstraintViolationException e) {
            assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", tx.getRollbackOnly()) ;
            tx.rollback(); // Actually rollback the transaction
            Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
            ConstraintViolation constraintViolation = constraintViolations.iterator().next();
            Object invalidaValue = constraintViolation.getInvalidValue();
            assertTrue("Invalid value should be " + invalidName, invalidName.equals(invalidaValue));
            gotConstraintViolations = true;
        }
        assertTrue("Did not get Constraint Violation while persisting invalid data ", gotConstraintViolations);

    }

    /**
     * Strategy:
     * 1. Persit an object with invalid value
     * 2. Assert - a ConstraintViolationException is raised
     * 3. Assert - transaction is rolled back
     * 4. Assert - The validation exception is due to invalid value given by us.
     */
    public void testEmbeddedWithInvalidData() {
        EntityManager em = createBVEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean gotConstraintViolations = false;
        try {
            // Persist an object with invalid value
            tx.begin();
            Employee e1 = new Employee(100, "name", 1000);
            Address address = new Address("street", "city", "state" /*passing invalid value for state */);
            e1.setAddress(address);
            em.persist(e1);
        } catch (ConstraintViolationException e) {
            assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", tx.getRollbackOnly()) ;
            tx.rollback();
            gotConstraintViolations = true;
        }
        assertTrue("Did not get Constraint Violation while persisting invalid data ", gotConstraintViolations);
    }

    /**
     * Strategy:
     * 1. Persit an object with valid values
     * 2. Update it with invalid data and commit transaction
     * 3. Assert - a Persistence Exception  is raised
     * 4. Assert - Cause of the persitence exception is ConstraintViolationException
     * 5  Assert - The ConstraintViolationException exception is due to invalid value given by us.
     * 6  Assert - transaction is marked for rolled back

     */
    public void testUpdateWithInvalidData() {
        EntityManager em = createBVEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean gotConstraintViolations = false;
        final String invalidName = BeanValidationPopulator.getFilledStringOfLength(Employee.NAME_MAX_SIZSE + 1 );
        try {
            // Update it with invalid value
            tx.begin();
            Employee e = em.find(Employee.class, BeanValidationPopulator.EMPLOYEE_PK);
            e.setName(invalidName);
            tx.commit();

        } catch (PersistenceException e) {
            assertFalse("Transaction not marked for roll back when ConstraintViolation is thrown", tx.isActive());
            Object cause = e.getCause();
            assertTrue("The nested cause should be instance of ConstraintViolationException", cause instanceof ConstraintViolationException) ;
            ConstraintViolationException cve = (ConstraintViolationException)cause;
            Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
            ConstraintViolation constraintViolation = constraintViolations.iterator().next();
            assertTrue("Invalid value should be " + invalidName, invalidName.equals(constraintViolation.getInvalidValue() ) );
            gotConstraintViolations = true;
        }

        assertTrue("Did not get Constraint Violation while updating with invalid data ", gotConstraintViolations);

    }

    public void testRemoveWithInvalidData() {
        EntityManager em = createBVEntityManager();
        EntityTransaction tx = em.getTransaction();
        boolean removeSuccessfull = false;
        final int EMPLOYEE_PK_TO_REMOVE = 2;
        tx.begin();
        Employee e1 = new Employee(EMPLOYEE_PK_TO_REMOVE, BeanValidationPopulator.getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 1 ), 1000);
        em.persist(e1);
        tx.commit();

        tx.begin();
        Employee e = em.find(Employee.class, EMPLOYEE_PK_TO_REMOVE);
        e.setName(BeanValidationPopulator.getFilledStringOfLength(Employee.NAME_MAX_SIZSE + 1 ));
        em.remove(e);
        tx.commit();
        removeSuccessfull = true;

        assertTrue("Automatic Validation should not be executed for remove", removeSuccessfull);
    }

    /**
     * Strategy:
     * 1. Update an Employee to trigger validation on it
     * 2. Assert no lazy relationships are loaded as side effect
     */
    public void testTraversableResolverPreventsLoadingOfLazyRelationships() {
        EntityManager em = createBVEntityManager();
        EntityManagerFactory emf = em.getEntityManagerFactory();

        //Empty the cache to make sure that lazy fields of Employee are uninstantiated
        emf.getCache().evictAll();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Employee employee = em.find(Employee.class, BeanValidationPopulator.EMPLOYEE_PK);
        employee.setName(BeanValidationPopulator.getFilledStringOfLength(Employee.NAME_MAX_SIZSE - 2));
        tx.commit();
        org.eclipse.persistence.sessions.Project project = JpaHelper.getServerSession(emf).getProject();
        assertTrue( "Lazy field should not be instantiated because of validation", !isInstantiated(employee, "projects", project) );
        assertTrue( "Lazy field should not be instantiated because of validation", !isInstantiated(employee, "managedProject", project) );
    }

    /**
     * Strategy:
     * 1. Update an Employee and related project to trigger validation on it
     * 2. Assert that the objects are traversed only once
     */
    public void testTraversableResolverPreventsTraversingRelationshipdMultipleTimes() {
        // Write a validator that sets value of boolean @Transient flag validationPerformed for each entity that visits it.
        // If it ever finds an entity with such flag set, the entity has visited the validator twice. It should be flagged as error.
    }



    //--------------------Helper Methods ---------------//
    private boolean isInstantiated(Object entityObject, String attributeName, org.eclipse.persistence.sessions.Project project) {
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) project.getDescriptor(Employee.class).getObjectBuilder().getMappingForAttributeName(attributeName);
        Object attributeValue = mapping.getAttributeAccessor().getAttributeValueFromObject(entityObject);
        return mapping.getIndirectionPolicy().objectIsInstantiatedOrChanged(attributeValue);
    }

    public EntityManager createBVEntityManager() {
        return JUnitTestCase.createEntityManager("beanvalidation");
    }
}