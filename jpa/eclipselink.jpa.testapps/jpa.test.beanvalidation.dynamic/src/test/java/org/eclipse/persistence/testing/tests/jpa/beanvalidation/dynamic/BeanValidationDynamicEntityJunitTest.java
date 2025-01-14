/*
 * Copyright (c) 2015, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.jpa.beanvalidation.dynamic;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.jpa.config.metadata.ReflectiveDynamicClassLoader;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
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
        suite.addTest(new BeanValidationDynamicEntityJunitTest("testPersistDynamicEntityWithInvalidData"));
        return suite;
    }

    /**
     * Strategy:
     * 1. Persist a dynamic entity with invalid values
     * 2. Assert - a ConstraintViolationException is raised
     * 3. Assert - transaction is rolled back
     * 4. Assert - The validation exception is due to invalid value given by us.
     */
    public void testPersistDynamicEntityWithInvalidData() {
        EntityManager em;
        if (isOnServer()) {
            em = createEntityManager();
        } else {
            // Create an entity manager factory with a dynamic class loader.
            DynamicClassLoader dcl = new ReflectiveDynamicClassLoader(Thread.currentThread().getContextClassLoader());
            @SuppressWarnings({"unchecked"})
            Map<String, Object> properties = new HashMap<>(getPersistenceProperties());
            properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
//            properties.put(PersistenceUnitProperties.BEAN_VALIDATION_NO_OPTIMISATION, "true");
            em = createEntityManager("beanvalidation-dynamic", properties);
        }
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

        try {
            beginTransaction(em);
            DynamicEntity emp = em.find(empType.getJavaClass(), employeeId);

            emp.set("name", null);
            emp.set("surname", null);

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
