/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - initial implementation
//     02/11/2013-2.5 Guy Pelletier
//       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Item;
import org.eclipse.persistence.testing.models.jpa21.advanced.Order;

public class EntityManagerTestSuite extends JUnitTestCase {

    public EntityManagerTestSuite() {}

    public EntityManagerTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerTestSuite");

        suite.addTest(new EntityManagerTestSuite("testSetup"));
        suite.addTest(new EntityManagerTestSuite("testGetLockModeForObject"));
        suite.addTest(new EntityManagerTestSuite("testNonInsertableAndUpdatable121Mappings"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
        EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(getPersistenceUnitServerSession());
        clearCache();
    }

    public void testGetLockModeForObject(){
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            Query query = em.createQuery("select e from Employee e where e.firstName = 'Sarah' and e.lastName = 'Way'");
            query.setLockMode(LockModeType.OPTIMISTIC);
            Employee emp = (Employee)query.getSingleResult();
            commitTransaction(em);
            try{
                em.getLockMode(emp);
                fail("TransactionRequiredException not thrown for getLockMode() with no transction open.");
            } catch (TransactionRequiredException e){}
            clearCache();
            try{
                em.find(Employee.class, emp.getId(), LockModeType.OPTIMISTIC);
                fail("TransactionRequiredException not thrown for find(Class, Object, LockModeType) with no transction open.");

            } catch (TransactionRequiredException e){}
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    public void testNonInsertableAndUpdatable121Mappings() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            Order order = new Order();
            order.setQuantity(10);

            Item item = new Item();
            item.setName("Party Balloons");
            order.setItem(item);

            Item itemPair = new Item();
            itemPair.setName("Ribbons");
            order.setItemPair(itemPair);

            em.persist(order);
            em.persist(item);
            em.persist(itemPair);
            commitTransaction(em);

            em.clear();
            clearCache();

            // Now try to read it back and update.
            beginTransaction(em);

            order = em.find(Order.class, order.getOrderId());

            assertTrue("The item pair was inserted", order.getItemPair() == null);
            order.setItemPair(em.merge(itemPair));

            Item newItem = new Item();
            newItem.setName("Party Boots");
            order.setItem(newItem);

            em.persist(newItem);
            commitTransaction(em);

            em.clear();
            clearCache();

            order = em.find(Order.class, order.getOrderId());

            assertTrue("The item pair was not updated.", order.getItemPair() != null);
            assertTrue("Orginal item was replaced", order.getItem().getItemId().equals(item.getItemId()));
            assertFalse("New item replaced original item", order.getItem().getItemId().equals(newItem.getItemId()));

        } catch (IllegalStateException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    @Override
    public String getPersistenceUnitName() {
       return "MulitPU-1";
    }
}
