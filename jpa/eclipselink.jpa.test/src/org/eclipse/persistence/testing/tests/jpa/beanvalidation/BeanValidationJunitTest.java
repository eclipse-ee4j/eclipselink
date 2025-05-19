/*
 * Copyright (c) 2009, 2025 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.eclipse.persistence.config.EntityManagerProperties;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.Address;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.BeanValidationTableCreator;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.Employee;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.GermanPhone;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.Phone;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.Project;
import org.eclipse.persistence.testing.models.jpa.beanvalidation.USPhone;

import junit.framework.Test;
import junit.framework.TestSuite;

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
        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new BeanValidationJunitTest("testSetup"));
            suite.addTest(new BeanValidationJunitTest("testPersistWithInvalidData"));
            suite.addTest(new BeanValidationJunitTest("testPersistWithInvalidMethodData"));
//            suite.addTest(new BeanValidationJunitTest("testPersistWithInvalidConstructorParameterData"));
            suite.addTest(new BeanValidationJunitTest("testEmbeddedWithInvalidData"));
            suite.addTest(new BeanValidationJunitTest("testUpdateWithInvalidData"));
            suite.addTest(new BeanValidationJunitTest("testRemoveWithInvalidData"));
            suite.addTest(new BeanValidationJunitTest("testTraversableResolverPreventsLoadingOfLazyRelationships"));
            suite.addTest(new BeanValidationJunitTest("testTraversableResolverPreventsTraversingRelationshipMultipleTimes"));
            suite.addTest(new BeanValidationJunitTest("testValidateChangedData"));
            suite.addTest(new BeanValidationJunitTest("testPersistIrishPhoneNoGroups"));
            suite.addTest(new BeanValidationJunitTest("testPersistUSPhoneWithGermanGroup"));
            suite.addTest(new BeanValidationJunitTest("testPersistGermanPhoneWithUSGroup"));
            suite.addTest(new BeanValidationJunitTest("testPersistGermanPhoneWithGermanGroup"));
            suite.addTest(new BeanValidationJunitTest("testRemoveGermanPhoneWithGermanGroup"));
            suite.addTest(new BeanValidationJunitTest("testRemoveInvalidGermanPhoneWithGermanGroup"));
            suite.addTest(new BeanValidationJunitTest("testUpdateInvalidGermanPhoneWithGermanGroup"));
        }
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

            Collection<Project> projects = new ArrayList<Project>();
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
            ConstraintViolation constraintViolation = constraintViolations.iterator().next();
            Object invalidValue = constraintViolation.getInvalidValue();
            assertTrue("Invalid value should be " + invalidName, invalidName.equals(invalidValue));
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
            ConstraintViolation constraintViolation = constraintViolations.iterator().next();
            Object invalidValue = constraintViolation.getInvalidValue();
            assertTrue("Invalid value should be " + invalidName, invalidName.equals(invalidValue));
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
            ConstraintViolation constraintViolation = constraintViolations.iterator().next();
            Object invalidValue = constraintViolation.getInvalidValue();
            assertTrue("Invalid value should be " + low_salary, invalidName.equals(invalidValue));
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
            assertFalse("Transaction not marked for roll back when ConstraintViolation is thrown", isTransactionActive(em));;
            Object cause = e.getCause();
            while(cause != null) {
                if (cause instanceof ConstraintViolationException){
                    ConstraintViolationException cve = (ConstraintViolationException)cause;
                    Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
                    ConstraintViolation constraintViolation = constraintViolations.iterator().next();
                    assertTrue("Invalid value should be " + invalidName, invalidName.equals(constraintViolation.getInvalidValue() ) );
                    gotConstraintViolations = true;
                    break;
                }else
                    cause = ((Throwable)cause).getCause();
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
        assertTrue( "Lazy field should not be instantiated because of validation", !isInstantiated(employee, "projects", project) );
        assertTrue( "Lazy field should not be instantiated because of validation", !isInstantiated(employee, "managedProject", project) );
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

    public void testPersistIrishPhoneNoGroups() {
      EntityManager em = createEntityManager();
      boolean gotConstraintViolations = false;
      try {
          // Persist an object with Irish phone number (valid default-phone)
          beginTransaction(em);
          Employee e = new Employee();
          e.setPhone(new Phone("+353 (1) 555-5678"));
          em.persist(e);
      } catch (ConstraintViolationException e) {
          assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em));
          gotConstraintViolations = true;
      } finally {
          if (isTransactionActive(em)) {
              rollbackTransaction(em);
          }
          closeEntityManager(em);
      }
      assertFalse("Did not expect Constraint Violation while persisting IrishPhone without groups", gotConstraintViolations);
    }
    
    public void testPersistUSPhoneWithGermanGroup() {
      Map<String, Object> props = new HashMap<>();
      props.put(EntityManagerProperties.VALIDATION_GROUP_PRE_PERSIST, new Class[]{ GermanPhone.class });
      EntityManager em = createEntityManager(props);
      boolean gotConstraintViolations = false;
      try {
          // Persist an object with US phone number
          beginTransaction(em);
          Employee e = new Employee();
          e.setPhone(new Phone("+1 (316) 555-5555"));
          em.persist(e);
      } catch (ConstraintViolationException e) {
          assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em));
          gotConstraintViolations = true;
      } finally {
          if (isTransactionActive(em)) {
              rollbackTransaction(em);
          }
          closeEntityManager(em);
      }
      assertTrue("Did not get Constraint Violation while persisting USPhone with German group active", gotConstraintViolations);
    }

    public void testPersistGermanPhoneWithUSGroup() {
      Map<String, Object> props = new HashMap<>();
      props.put(EntityManagerProperties.VALIDATION_GROUP_PRE_PERSIST, new Class[]{ USPhone.class });
      EntityManager em = createEntityManager(props);
      boolean gotConstraintViolations = false;
      try {
          // Persist an object with German phone number (invalid without German group)
          beginTransaction(em);
          Employee e = new Employee();
          e.setPhone(new Phone("+49 (221) 1555-5678")); 
          em.persist(e);
      } catch (ConstraintViolationException e) {
          assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em));
          gotConstraintViolations = true;
      } finally {
          if (isTransactionActive(em)) {
              rollbackTransaction(em);
          }
          closeEntityManager(em);
      }
      assertTrue("Did not get Constraint Violation while persisting GermanPhone with US group active", gotConstraintViolations);
    }

    public void testPersistGermanPhoneWithGermanGroup() {
      Map<String, Object> props = new HashMap<>();
      props.put(EntityManagerProperties.VALIDATION_GROUP_PRE_PERSIST, new Class[]{ GermanPhone.class });
      EntityManager em = createEntityManager(props);
      boolean gotConstraintViolations = false;
      try {
          // Persist an object with German phone number (valid DE-phone)
          beginTransaction(em);
          Employee e = new Employee();
          e.setPhone(new Phone("+49 (221) 1555-5678"));
          em.persist(e);
      } catch (ConstraintViolationException e) {
          assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em));
          gotConstraintViolations = true;
      } finally {
          if (isTransactionActive(em)) {
              rollbackTransaction(em);
          }
          closeEntityManager(em);
      }
      assertFalse("Did not expect Constraint Violation while persisting GermanPhone with German group active", gotConstraintViolations);
    }
    
    public void testRemoveGermanPhoneWithGermanGroup() {
      Map<String, Object> props = new HashMap<>();
      props.put(EntityManagerProperties.VALIDATION_GROUP_PRE_REMOVE, new Class[]{ GermanPhone.class });
      EntityManager em = createEntityManager(props);
      boolean removeSuccessful = false;
      final int PK = 200;

      try {
          beginTransaction(em);
          Employee e1 = new Employee(PK, "name", "name", 1337);
          e1.setPhone(new Phone("+49 (221) 1555-5678"));
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
          Employee e = em.find(Employee.class, PK);
          em.remove(e);
          commitTransaction(em);
          removeSuccessful = true;
      } catch (RuntimeException ex) {
          if (isTransactionActive(em)) {
              rollbackTransaction(em);
          }
          throw ex;
      }

      closeEntityManager(em);
      assertTrue("Automatic Validation should not be executed for remove with German group", removeSuccessful);
    }

    public void testUpdateGermanPhoneWithGermanGroup() {
      Map<String, Object> props = new HashMap<>();
      props.put(EntityManagerProperties.VALIDATION_GROUP_PRE_UPDATE, new Class[]{ GermanPhone.class });
      EntityManager em = createEntityManager(props);
      boolean updateSuccessful = false;
      final int PK = 201;

      try {
          beginTransaction(em);
          Employee e1 = new Employee(PK, "name", "name", 1337);
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
          Employee e = em.find(Employee.class, PK);
          e.setPhone(new Phone("+49 (221) 1555-5678"));
          commitTransaction(em);
          updateSuccessful = true;
      } catch (RuntimeException ex) {
          if (isTransactionActive(em)) {
              rollbackTransaction(em);
          }
          throw ex;
      }

      closeEntityManager(em);
      assertTrue("Automatic Validation should not be executed for update with German group", updateSuccessful);
    }
    
    public void testRemoveInvalidGermanPhoneWithGermanGroup() {
      Map<String, Object> props = new HashMap<>();
      props.put(EntityManagerProperties.VALIDATION_GROUP_PRE_REMOVE, new Class[]{ GermanPhone.class });
      EntityManager em = createEntityManager(props);
      boolean gotConstraintViolations = false;
      final int PK = 300;

      // first persist with a valid German phone
      try {
          beginTransaction(em);
          Employee e1 = new Employee(PK, "name", "name", 0);
          e1.setPhone(new Phone("+49 (221) 1555-5678"));
          em.persist(e1);
          commitTransaction(em);
      } catch (RuntimeException ex) {
          if (isTransactionActive(em)) rollbackTransaction(em);
          closeEntityManager(em);
          throw ex;
      }

      // now change to a US phone and attempt remove
      try {
          beginTransaction(em);
          Employee e = em.find(Employee.class, PK);
          e.setPhone(new Phone("123-456-7890"));
          em.remove(e);
          commitTransaction(em);
      } catch (ConstraintViolationException e) {
          assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em));
          gotConstraintViolations = true;
      } finally {
          if (isTransactionActive(em)) rollbackTransaction(em);
          closeEntityManager(em);
      }
      assertTrue("Expected Constraint Violation while removing with invalid GermanPhone under German group", gotConstraintViolations);
    }

    public void testUpdateInvalidGermanPhoneWithGermanGroup() {
      Map<String, Object> props = new HashMap<>();
      props.put(EntityManagerProperties.VALIDATION_GROUP_PRE_UPDATE, new Class[]{ GermanPhone.class });
      EntityManager em = createEntityManager(props);
      boolean gotConstraintViolations = false;
      final int PK = 301;

      // first persist without phone
      try {
          beginTransaction(em);
          Employee e1 = new Employee(PK, "name", "name", 0);
          em.persist(e1);
          commitTransaction(em);
      } catch (RuntimeException ex) {
          if (isTransactionActive(em)) rollbackTransaction(em);
          closeEntityManager(em);
          throw ex;
      }

      // now change to a US phone and attempt update
      try {
          beginTransaction(em);
          Employee e = em.find(Employee.class, PK);
          e.setPhone(new Phone("123-456-7890"));
          commitTransaction(em);
      } catch (ConstraintViolationException e) {
          assertTrue("Transaction not marked for roll back when ConstraintViolation is thrown", getRollbackOnly(em));
          gotConstraintViolations = true;
      } finally {
          if (isTransactionActive(em)) rollbackTransaction(em);
          closeEntityManager(em);
      }
      assertTrue("Expected Constraint Violation while updating with invalid GermanPhone under German group", gotConstraintViolations);
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
