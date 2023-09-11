/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties;
 
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.Assert;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.ModelExamples;

public class OptimisticLockingBatchWritingTest extends JUnitTestCase {

    public OptimisticLockingBatchWritingTest() {
    }

    public OptimisticLockingBatchWritingTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "PU-BatchWriting";
    }

    public void testSingleStatementBatch() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Customer customer = ModelExamples.customerExample1();
            em.persist(customer);
            Integer customerId = customer.getCustomerId();
            commitTransaction(em);
            callTestImpl(customerId, new Integer[] { customerId });
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }finally{
            closeEntityManager(em);
        }
    }

    public void testMultipleStatementsBatch() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Customer customer1 = ModelExamples.customerExample1();
            em.persist(customer1);
            Integer customerId1 = customer1.getCustomerId();
            Customer customer2 = ModelExamples.customerExample2();
            em.persist(customer2);
            Integer customerId2 = customer2.getCustomerId();
            commitTransaction(em);
            callTestImpl(customerId2, new Integer[] { customerId1, customerId2 });
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }finally{
            closeEntityManager(em);
        }
    }

    protected void callTestImpl(Integer customerId, Integer[] customerIds) {

        new Thread(() -> changeOrderAndWaitInTransaction(500, new Integer[] { customerId })).start();

        try {
            changeOrderAndWaitInTransaction(1000, customerIds);
            Assert.fail("There should have been an optimistic lock");
        } catch (Throwable e) {
            Assert.assertTrue(e.getMessage(),
                    e.getMessage().contains("One or more objects of class"));
            Assert.assertTrue(e.getMessage(),
                    e.getMessage().contains("org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer"));
        }
        
    }

    protected void changeOrderAndWaitInTransaction(int sleepInMillis, Integer[] customerIds) {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            for (Integer customerId : customerIds) {
                 Customer customer = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
                 customer.setName(customer.getName() + "-modified");
            }
            try {
                Thread.sleep(sleepInMillis);
            } catch (InterruptedException e) {
                // ignore for this test
            }

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }finally{
            closeEntityManager(em);
        }

   }

}
