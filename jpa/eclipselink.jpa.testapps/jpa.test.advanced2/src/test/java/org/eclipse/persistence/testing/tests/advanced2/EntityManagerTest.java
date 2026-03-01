/*
 * Copyright (c) 2013, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation
//     02/11/2013-2.5 Guy Pelletier
//       - 365931: @JoinColumn(name="FK_DEPT",insertable = false, updatable = true) causes INSERT statement to include this data value that it is associated with
package org.eclipse.persistence.testing.tests.advanced2;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.TransactionRequiredException;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.Item;
import org.eclipse.persistence.testing.models.jpa21.advanced.Order;
import org.junit.Assert;

public class EntityManagerTest extends JUnitTestCase {

    public EntityManagerTest() {}

    public EntityManagerTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced2x";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerTest");

        suite.addTest(new EntityManagerTest("testSetup"));
        suite.addTest(new EntityManagerTest("testGetLockModeForObject"));
        suite.addTest(new EntityManagerTest("testNonInsertableAndUpdatable121Mappings"));

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

            assertNull("The item pair was inserted", order.getItemPair());
            order.setItemPair(em.merge(itemPair));

            Item newItem = new Item();
            newItem.setName("Party Boots");
            order.setItem(newItem);

            em.persist(newItem);
            commitTransaction(em);

            em.clear();
            clearCache();

            order = em.find(Order.class, order.getOrderId());

            assertNotNull("The item pair was not updated.", order.getItemPair());
            assertEquals("Orginal item was replaced", order.getItem().getItemId(), item.getItemId());
            Assert.assertNotEquals("New item replaced original item", order.getItem().getItemId(), newItem.getItemId());

        } catch (IllegalStateException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        } finally {
            closeEntityManager(em);
        }
    }
}
