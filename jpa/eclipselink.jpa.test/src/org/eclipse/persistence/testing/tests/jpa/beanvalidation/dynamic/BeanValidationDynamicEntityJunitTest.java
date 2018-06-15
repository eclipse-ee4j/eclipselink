/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.jpa.beanvalidation.dynamic;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.validation.ConstraintViolationException;
import javax.validation.ConstraintViolation;

import junit.framework.Test;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.jpa.config.metadata.ReflectiveDynamicClassLoader;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Purpose of this test is to verify that bean validation configuration for a
 * dynamic entity is correctly applied
 */
public class BeanValidationDynamicEntityJunitTest extends JUnitTestCase {

    public BeanValidationDynamicEntityJunitTest() {
        super();
    }

    public BeanValidationDynamicEntityJunitTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("BeanValidationDynamicEntityJunitTest");
        if (! JUnitTestCase.isJPA10()) {
            suite.addTest(new BeanValidationDynamicEntityJunitTest("testPersistDynamicEntityWithInvalidData"));
        }
        return suite;
    }

    /**
     * Strategy:
     * 1. Persist a dynamic entity with invalid values
     * 2. Assert - a ConstraintViolationException is raised
     * 3. Assert - transaction is rolled back
     * 4. Assert - The validation exception is due to invalid value given by us.
     */
    @SuppressWarnings("unchecked")
    public void testPersistDynamicEntityWithInvalidData() {        
        // Create an entity manager factory with a dynamic class loader.
        DynamicClassLoader dcl = new ReflectiveDynamicClassLoader(Thread.currentThread().getContextClassLoader());
        Map<String,Object> properties = new HashMap<>(getPersistenceProperties());
        properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
//        properties.put(PersistenceUnitProperties.BEAN_VALIDATION_NO_OPTIMISATION, "true");
        EntityManager em = createEntityManager("beanvalidation-dynamic", properties);
        EntityManagerFactory entityManagerFactory = em.getEntityManagerFactory();

        // Create types
        JPADynamicHelper helper = new JPADynamicHelper(entityManagerFactory);
        
        DynamicType empType = helper.getType("DynamicEmployee");
        ClassDescriptor empTypeDescriptor = empType.getDescriptor();
        DynamicEntity employee = (DynamicEntity) empTypeDescriptor.getInstantiationPolicy().buildNewInstance();

        employee.set("name", "Bob");
        employee.set("surname", "Smith");

        try {
            beginTransaction(em);
            em.persist(employee);
            commitTransaction(em);
        } catch (Exception e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            Assert.fail(e.getMessage());
        }
        Integer employeeId = employee.<Integer>get("id");
        assertTrue("Employee id not set!", employeeId > 0);

        DynamicEntity emp = em.find(empType.getJavaClass(), employeeId);

        emp.set("name", null);
        emp.set("surname", null);

        try {
            beginTransaction(em);
            em.flush();
        } catch (ConstraintViolationException e) {
            System.out.println("ConstraintException: " + e.getMessage());
            for (ConstraintViolation<?> cv : e.getConstraintViolations()) {
                System.out.println("\t>> " + cv.getPropertyPath() + "::" + cv.getMessage());
            }
            return;
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        Assert.fail("ConstraintViolationException not thrown");
    }
}
