/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.nosql;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import oracle.kv.Direction;
import oracle.kv.KVStore;
import oracle.kv.Key;

import org.eclipse.persistence.nosql.adapters.nosql.OracleNoSQLPlatform;
import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLConnection;
import org.eclipse.persistence.internal.nosql.adapters.nosql.OracleNoSQLOperation;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.Customer;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.LineItem;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.Order;
import org.eclipse.persistence.testing.models.jpa.nosql.mapped.Address;

/**
 * TestSuite to test Oracle No SQL database
 * ** To run this test suite the Oracle No SQL database must be running.
 */
public class NoSQLMappedTestSuite extends JUnitTestCase {
    
    public static long nextId = 1;
    public static Order existingOrder;
    
    public NoSQLMappedTestSuite(){
    }

    public NoSQLMappedTestSuite(String name){
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NoSQLMappedTestSuite");
        suite.addTest(new NoSQLMappedTestSuite("testSetup"));
        suite.addTest(new NoSQLMappedTestSuite("testInsert"));
        suite.addTest(new NoSQLMappedTestSuite("testFind"));
        suite.addTest(new NoSQLMappedTestSuite("testUpdate"));
        suite.addTest(new NoSQLMappedTestSuite("testMerge"));
        suite.addTest(new NoSQLMappedTestSuite("testRefresh"));
        suite.addTest(new NoSQLMappedTestSuite("testDelete"));
        suite.addTest(new NoSQLMappedTestSuite("testJPQL"));
        suite.addTest(new NoSQLMappedTestSuite("testNativeQuery"));
        return suite;
    }
    
    @Override
    public String getPersistenceUnitName() {
        return "nosql-mapped";
    }

    @Override
    public Map getPersistenceProperties() {
        return new HashMap();
    }
    
    public void testSetup() {
        EntityManager em = createEntityManager();
        // First clear old data from store.
        beginTransaction(em);
        KVStore store = ((OracleNoSQLConnection)em.unwrap(javax.resource.cci.Connection.class)).getStore();
        Iterator<Key> iterator = store.storeKeysIterator(Direction.UNORDERED, 0);
        while (iterator.hasNext()) {
            store.multiDelete(iterator.next(), null, null);
        }
        commitTransaction(em);
        beginTransaction(em);
        try {
            for (int index = 0; index < 10; index++) {
                existingOrder = new Order();
                existingOrder.id = this.nextId++;
                existingOrder.orderedBy = "ACME";
                existingOrder.address = new Address();
                existingOrder.address.city = "Ottawa";
                existingOrder.address.addressee = "Bob Jones";
                existingOrder.address.state = "CA";
                existingOrder.address.country = "Mexico";
                existingOrder.address.zipCode = "12345";
                LineItem line = new LineItem();
                line.itemName = "stuff";
                line.itemPrice = new BigDecimal("10.99");
                line.lineNumber = 1;
                line.quantity = 100;
                existingOrder.lineItems.add(line);
                line = new LineItem();
                line.itemName = "more stuff";
                line.itemPrice = new BigDecimal("20.99");
                line.lineNumber = 2;
                line.quantity = 50;
                existingOrder.lineItems.add(line);
                existingOrder.comments.add("priority order");
                existingOrder.comments.add("next day");
                em.persist(existingOrder);
            }
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
    }
    
    /**
     * Test inserts.
     */
    public void testInsert() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.id = this.nextId++;
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            order.customer = new Customer();
            order.customer.id = String.valueOf(this.nextId++);
            order.customer.name = "ACME";
            em.persist(order.customer);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            Order fromDatabase = em.find(Order.class, order.id);
            compareObjects(order, fromDatabase);
        } finally {
            closeEntityManagerAndTransaction(em);
        }        
    }
    
    /**
     * Test find.
     */
    public void testFind() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = null;
        try {
            order = em.find(Order.class, existingOrder.id);
            compareObjects(existingOrder, order);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test updates.
     */
    public void testUpdate() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.id = this.nextId++;
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, this.existingOrder.id);
            order.orderedBy = "Fred Jones";
            order.address.addressee = "Fred Jones";
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }        
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            Order fromDatabase = em.find(Order.class, order.id);
            compareObjects(order, fromDatabase);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test merge.
     */
    public void testMerge() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.id = this.nextId++;
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            order.orderedBy = "Fred Jones";
            order.address.addressee = "Fred Jones";
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        em = createEntityManager();
        beginTransaction(em);
        try {
            em.merge(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            Order fromDatabase = em.find(Order.class, order.id);
            compareObjects(order, fromDatabase);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test refresh.
     */
    public void testRefresh() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.id = this.nextId++;
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            order.orderedBy = "Fred Jones";
            em.refresh(order);
            if (order.orderedBy.equals("Fred Jones")) {
                fail("Refresh failed: " + order.orderedBy);
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
    /**
     * Test JPQL.
     */
    public void testJPQL() {
        // Clear db first.
        testSetup();
        EntityManager em = createEntityManager();
        try {
            Query query = em.createQuery("Select o from Order o");
            List results = query.getResultList();
            if (results.size() != 10) {
                fail("Find all did not work, expected 10 got: " + results);
            }
            query = em.createQuery("Select o from Order o where o.id = :id");
            query.setParameter("id", nextId - 1);
            results = query.getResultList();
            if (results.size() != 1) {
                fail("Find all did not work, expected 1 got: " + results);
            }
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * Test native query.
     */
    public void testNativeQuery() {
        EntityManager em = createEntityManager();
        
        MappedInteraction interaction = new MappedInteraction();
        interaction.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET.name());
        interaction.addArgumentValue("[Order-mapped," + existingOrder.id + "]", "");
        Query query = em.unwrap(JpaEntityManager.class).createQuery(interaction);
        List result = query.getResultList();
        if ((result.size() != 1)
                || (!(result.get(0) instanceof Record))
                || !(((Record)result.get(0)).containsKey("[Order-mapped," + existingOrder.id + "]"))) {
            fail("Incorrect result: " + result);
        }
        
        interaction = new MappedInteraction();
        interaction.setProperty(OracleNoSQLPlatform.OPERATION, OracleNoSQLOperation.GET.name()); 
        interaction.addArgumentValue("ID", existingOrder.id);
        query = em.unwrap(JpaEntityManager.class).createQuery(interaction, Order.class);
        result = query.getResultList();
        if ((result.size() != 1)
                || (!(result.get(0) instanceof Order))) {
            fail("Incorrect result: " + result);
        }
        
    }
    
    /**
     * Test deletes.
     */
    public void testDelete() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Order order = new Order();
        try {
            order.id = this.nextId++;
            order.orderedBy = "ACME";
            order.address = new Address();
            order.address.city = "Ottawa";
            em.persist(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            order = em.find(Order.class, order.id);
            em.remove(order);
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }        
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        try {
            Order fromDatabase = em.find(Order.class, order.id);
            if (fromDatabase != null) {
                fail("Object not deleted: " + fromDatabase);
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }
    
}
