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
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.*;

public class RelationshipModelJUnitTestSuite extends JUnitTestCase {
    
    public RelationshipModelJUnitTestSuite() {
        super();
    }
    
    public RelationshipModelJUnitTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("RelationshipModelJUnitTestSuite (field access)");
        
        suite.addTest(new RelationshipModelJUnitTestSuite("testSetup")); 
        suite.addTest(new RelationshipModelJUnitTestSuite("testExecuteUpdateTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testGetResultCollectionTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testGetResultListTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testGetSingleResultTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testNamedQueryDoesNotExistTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testNamedQueryDoesNotExistTest"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    /*
     * Tests for the executeUpdate method on the EJBQueryImpl class.
     * Also tests bugs 4288845 and 4293920, that params are passed in and used correctly.
     */
    public void testExecuteUpdateTest() {
        EntityManager em = createEntityManager();
        Integer[] cusIDs = new Integer[2];
        String nameChange1 = "New Name1";
        String nameChange2 = "New Name2";
        String nameChange3 = "New Name3";
        String returnedName1, returnedName2, returnedName3;
        Exception expectedException = null;
        Exception expectedException2 = null;
        try {
            Customer cusClone1 = RelationshipsExamples.customerExample1();
            beginTransaction(em);
            em.persist(cusClone1);
            commitTransaction(em);
            em.clear();
            clearCache();
            cusIDs[0] = cusClone1.getCustomerId();

            beginTransaction(em);
            Customer cus = em.find(Customer.class, cusIDs[0]);
            Query query = em.createQuery("UPDATE FieldAccessCustomer customer SET customer.name = '" + nameChange1 + "' WHERE customer.customerId = " + cusIDs[0]);
            query.executeUpdate();
            em.clear();
            clearCache();
            // getEntityManager().refresh(cus);
            cus = em.find(Customer.class, cusIDs[0]);
            returnedName1 = cus.getName();

            // tests bug 4288845
            Query query2 = em.createQuery("UPDATE FieldAccessCustomer customer SET customer.name = :name WHERE customer.customerId = " + cusIDs[0]);
            query2.setParameter("name", nameChange2);
            query2.executeUpdate();
            em.clear();
            clearCache();
            // getEntityManager().refresh(cus);
            cus = em.find(Customer.class, cusIDs[0]);
            returnedName2 = cus.getName();

            // tests bug 4293920
            Query query3 = em.createQuery("UPDATE FieldAccessCustomer customer SET customer.name = :name WHERE customer.customerId = :id");
            query3.setParameter("name", nameChange3);
            query3.setParameter("id", cusIDs[0]);
            query3.executeUpdate();
            em.clear();
            clearCache();
            // getEntityManager().refresh(cus);
            cus = em.find(Customer.class, cusIDs[0]);
            returnedName3 = cus.getName();

            // tests bug 4294241
            try {
                Query query4 = em.createNamedQuery("findAllCustomersFieldAccess");
                query4.executeUpdate();
            } catch (IllegalStateException expected) {
                expectedException = expected;
            }

            expectedException2 = null;
            try {
                commitTransaction(em);
                expectedException2 = null;
            } catch (Exception ex) {
                expectedException2 = ex;
            }

            if ((returnedName1 == null || !returnedName1.equals(nameChange1))) {
                fail("Customer name did not get updated correctly should be:" + nameChange1 + " is :" + returnedName1);
            }
            if ((returnedName2 == null || !returnedName2.equals(nameChange2))) {
                fail("Customer name did not get updated correctly should be:" + nameChange2 + " is :" + returnedName2);
            }
            if ((returnedName3 == null || !returnedName3.equals(nameChange3))) {
                fail("Customer name did not get updated correctly should be:" + nameChange3 + " is :" + returnedName3);
            }
            if (expectedException == null) {
                fail("excuteUpdate did not result in an exception on findAllCustomersFieldAccess named ReadAllQuery");
            }
            if (expectedException2 == null) {
                fail("commit did not throw expected RollbackException");
            }
    
            beginTransaction(em);
            Customer cus1 = em.find(Customer.class, cusIDs[0]);
            em.remove(cus1);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
    }
    
    /*
     * Tests using the 'getResultCollection' api on a Query object obtained from the
     * EntityManager Also tests bugs 4300879 - check non Collection container
     * policy error and 4297903 - check ReadObjectQuery fails
     */
    public void testGetResultCollectionTest() {
        Collection returnedCustomers1, returnedCustomers2;
        QueryException expectedException1 = null;
        String ejbql1 = "SELECT OBJECT(thecust) FROM FieldAccessCustomer thecust WHERE thecust.customerId = :id";
        Integer[] cusIDs = new Integer[3];

        Customer cusClone1 = RelationshipsExamples.customerExample1();
        Customer cusClone2 = RelationshipsExamples.customerExample2();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        em.persist(cusClone1);
        em.persist(cusClone2);
        commitTransaction(em);
        em.clear();
        clearCache();

        cusIDs[0] = cusClone1.getCustomerId();
        cusIDs[1] = cusClone2.getCustomerId();
        try {
            beginTransaction(em);
            EntityManagerImpl entityManagerImpl = (EntityManagerImpl) em.getDelegate();

            EJBQueryImpl query1 = (EJBQueryImpl) entityManagerImpl.createNamedQuery("findAllCustomersFieldAccess");
            returnedCustomers1 = query1.getResultCollection();

            EJBQueryImpl query2 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
            query2.setParameter("id", new Integer(-10));
            returnedCustomers2 = query2.getResultCollection();

            // bug:4297903, check container policy failure
            EJBQueryImpl query3 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
            ReadAllQuery readAllQuery = new ReadAllQuery(Customer.class);
            MapContainerPolicy mapContainerPolicy = new MapContainerPolicy();
            mapContainerPolicy.setContainerClass(HashMap.class);
            mapContainerPolicy.setKeyName("hashCode");
            readAllQuery.setContainerPolicy(mapContainerPolicy);
            query3.setDatabaseQuery(readAllQuery);
            try {
                query3.getResultCollection();
            } catch (QueryException exceptionExpected1) {// QueryException.INVALID_CONTAINER_CLASS
                expectedException1 = exceptionExpected1;
                rollbackTransaction(em);
                beginTransaction(em);
            }

            entityManagerImpl = (EntityManagerImpl) em.getDelegate();
            // bug:4300879, check ReadObjectQuery fails
            EJBQueryImpl query4 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
            query4.setParameter("id", new Integer(-10));
            ReadObjectQuery readObjectQuery2 = new ReadObjectQuery(Customer.class);
            readObjectQuery2.setEJBQLString(ejbql1);
            query4.setDatabaseQuery(readObjectQuery2);
            query4.getResultCollection();

            commitTransaction(em);
            if (returnedCustomers1 == null || (returnedCustomers1.size() < 2)) {
                fail("Not all customers were returned from findAllCustomers query ");
            }
            if (returnedCustomers2 == null || (returnedCustomers2.size() != 0)) {
                fail("Customer from ReadObjectQuery was not returned using getResultCollection");
            }
            if (expectedException1 == null || (expectedException1.getErrorCode() != QueryException.INVALID_CONTAINER_CLASS)) {
                fail("getResultCollection on query returning a hashtable did not throw expected INVALID_CONTAINER_CLASS QueryException");
            }
            beginTransaction(em);
            Customer cus1 = em.find(Customer.class, cusIDs[0]);
            em.remove(cus1);
            Customer cus2 = em.find(Customer.class, cusIDs[1]);
            em.remove(cus2);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
    }
    
    /*
     * Tests using the 'getSingleResult' api on a Query object obtained from the
     * EntityManager Also tests bugs 4300879 - check non Collection container
     * policy error and 4297903 - check ReadObjectQuery fails
     */
    public void testGetResultListTest() {
        Collection returnedCustomers1, returnedCustomers2;
        QueryException expectedException1 = null;
        String ejbql1 = "SELECT OBJECT(thecust) FROM FieldAccessCustomer thecust WHERE thecust.customerId = :id";
        Integer[] cusIDs = new Integer[3];

        Customer cusClone1 = RelationshipsExamples.customerExample1();
        Customer cusClone2 = RelationshipsExamples.customerExample2();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        em.persist(cusClone1);
        em.persist(cusClone2);
        commitTransaction(em);
        em.clear();
        clearCache();

        cusIDs[0] = cusClone1.getCustomerId();
        cusIDs[1] = cusClone2.getCustomerId();
        try {
            beginTransaction(em);
            EntityManagerImpl entityManagerImpl = (EntityManagerImpl) em.getDelegate();

            Query query1 = em.createNamedQuery("findAllCustomersFieldAccess");
            returnedCustomers1 = query1.getResultList();

            Query query2 = em.createQuery(ejbql1);
            query2.setParameter("id", new Integer(-10));
            returnedCustomers2 = query2.getResultList();

            // bug:4297903, check container policy failure
            EJBQueryImpl query3 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
            ReadAllQuery readAllQuery = new ReadAllQuery(Customer.class);
            MapContainerPolicy mapContainerPolicy = new MapContainerPolicy();
            mapContainerPolicy.setContainerClass(HashMap.class);
            mapContainerPolicy.setKeyName("hashCode");
            readAllQuery.setContainerPolicy(mapContainerPolicy);
            query3.setDatabaseQuery(readAllQuery);
            try {
                query3.getResultList();
            } catch (QueryException exceptionExpected1) {// QueryException.INVALID_CONTAINER_CLASS
                expectedException1 = exceptionExpected1;
                rollbackTransaction(em);
                beginTransaction(em);
            }

            entityManagerImpl = (EntityManagerImpl) em.getDelegate();
            // bug:4300879, check ReadObjectQuery fails
            EJBQueryImpl query4 = (EJBQueryImpl) entityManagerImpl.createQuery(ejbql1);
            query4.setParameter("id", new Integer(-10));
            ReadObjectQuery readObjectQuery2 = new ReadObjectQuery(Customer.class);
            readObjectQuery2.setEJBQLString(ejbql1);
            query4.setDatabaseQuery(readObjectQuery2);
            query4.getResultList();

            commitTransaction(em);
            if (returnedCustomers1 == null || (returnedCustomers1.size() < 2)) {
                fail("Not all customers were returned from findAllCustomers query ");
            }
            if (returnedCustomers2 == null || (returnedCustomers2.size() != 0)) {
                fail("Customer from ReadObjectQuery was not returned using getResultCollection");
            }
            if (expectedException1 == null || (expectedException1.getErrorCode() != QueryException.INVALID_CONTAINER_CLASS)) {
                fail("getResultCollection on query returning a hashtable did not throw expected INVALID_CONTAINER_CLASS QueryException");
            }
            beginTransaction(em);
            Customer cus1 = em.find(Customer.class, cusIDs[0]);
            em.remove(cus1);
            Customer cus2 = em.find(Customer.class, cusIDs[1]);
            em.remove(cus2);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
    }
    
    /*
     * Tests using the 'getSingleResult' api on a Query object obtained from the
     * EntityManager Tests fixes for bugs 4202835 and 4301674
     * 
     * modified for changes in bug:4628215 (EntityNotFoundException)
     * EntityNotFoundException changed to NoResultException as per new spec
     */
    public void testGetSingleResultTest() {
        // used for verification
        Customer returnedCustomer1, returnedCustomer2 = null;
        NonUniqueResultException expectedException1 = null;
        NoResultException expectedException2 = null;
        String searchString = "notAnItemName";
        Integer[] cusIDs = new Integer[3];

        Customer cusClone1 = RelationshipsExamples.customerExample1();
        Customer cusClone2 = RelationshipsExamples.customerExample2();
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(cusClone1);
            em.persist(cusClone2);
            commitTransaction(em);
                
            clearCache();
            cusIDs[0] = cusClone1.getCustomerId();
            cusIDs[1] = cusClone2.getCustomerId();
            beginTransaction(em);
            try {
                returnedCustomer1 = (Customer) em.createNamedQuery("findAllCustomersFieldAccess").getSingleResult();
            } catch (NonUniqueResultException exceptionExpected1) {
                expectedException1 = exceptionExpected1;
            }
            try {
                // should be no Items to find, which should cause an
                // NoResultException
                Query query1 = em.createNamedQuery("findAllFieldAccessItemsByName");
                Item item = (Item) query1.setParameter(1, searchString).getSingleResult();
                item.toString();
            } catch (NoResultException exceptionExpected2) {
                expectedException2 = exceptionExpected2;
            }
            // bug 4301674 test
            EJBQueryImpl query2 = (EJBQueryImpl) em.createNamedQuery("findAllCustomersFieldAccess");
            ReadAllQuery readAllQuery = new ReadAllQuery(Customer.class);
            MapContainerPolicy mapContainerPolicy = new MapContainerPolicy();
            mapContainerPolicy.setContainerClass(HashMap.class);
            mapContainerPolicy.setKeyName("hashCode");
            readAllQuery.setContainerPolicy(mapContainerPolicy);
            query2.setDatabaseQuery(readAllQuery);
            Map result = (Map) query2.getSingleResult();
            result.toString();
    
            // check for single result found.
            Query query3 = em.createQuery("SELECT OBJECT(thecust) FROM FieldAccessCustomer thecust WHERE thecust.customerId = :id");
            returnedCustomer1 = (Customer) query3.setParameter("id", cusIDs[0]).getSingleResult();
    
            // check for single result using a ReadObjectQuery (tests previous
            // fix for 4202835)
            EJBQueryImpl query4 = (EJBQueryImpl) em.createQuery("SELECT OBJECT(thecust) FROM FieldAccessCustomer thecust WHERE thecust.customerId = :id");
            query4.setParameter("id", cusIDs[0]);
            ReadObjectQuery readObjectQuery = new ReadObjectQuery(Customer.class);
            readObjectQuery.setEJBQLString("SELECT OBJECT(thecust) FROM FieldAccessCustomer thecust WHERE thecust.customerId = :id");
            query4.setDatabaseQuery(readObjectQuery);
            returnedCustomer2 = (Customer) query4.getSingleResult();
            commitTransaction(em);
            
            beginTransaction(em);
            Customer cus1 = em.find(Customer.class, cusIDs[0]);
            em.remove(cus1);
            Customer cus2 = em.find(Customer.class, cusIDs[1]);
            em.remove(cus2);
            commitTransaction(em);
            
            if (expectedException1 == null) {
                fail("getSingelResult on query returning multiple values did not throw a NonUniqueResultException");
            }
            if (expectedException2 == null) {
                fail("getSingelResult on query returning multiple values did not throw an NoResultException");
            }
            if (returnedCustomer1 == null || (!returnedCustomer1.getCustomerId().equals(cusIDs[0]))) {
                fail("Incorrect Single Customer returned, found: " + returnedCustomer1);
            }
            if (returnedCustomer2 == null || (!returnedCustomer2.getCustomerId().equals(cusIDs[0]))) {
                fail("Incorrect Single Customer returned, found: " + returnedCustomer2);
            }
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
    }
    
    /**
     * Tests trying to execute a named query that does not exist.
     * 
     * @author Guy Pelletier
     */
    public void testNamedQueryDoesNotExistTest() {
        Exception m_exception = null;
        boolean m_npeCaught;
        boolean m_illegalArgumentExceptionCaught;
        EntityManager em = createEntityManager();
        m_npeCaught = false;
        m_illegalArgumentExceptionCaught = false;
  
        try {
            em.createNamedQuery("doesNotExist").getResultList();
        } catch (NullPointerException e) {
            m_npeCaught = true;
        } catch (IllegalArgumentException e) {
            m_illegalArgumentExceptionCaught = true;
        } catch (Exception e) {
            m_exception = e;
        }
  
        if (m_npeCaught) {
            fail("A null pointer exception caught on the query.");
        } else if (!m_illegalArgumentExceptionCaught) {
            if (m_exception != null) {
                fail("Expected IllegalArgumentException, caught: " + m_exception);
            } else {
                fail("No exception was caught on a named query that does not exist.");
            }
        }
    }
    
    // Bug#4646580 Query arguments are added in EJBQL
    public void testNamedQueryWithArgumentsTest() {
        Integer[] cusIDs = new Integer[3];
        Integer[] orderIDs = new Integer[3];
        Integer[] itemIDs = new Integer[3];
        Exception exception = null;
        List list = null;

        Customer cusClone1 = RelationshipsExamples.customerExample1();
        Item item1 = RelationshipsExamples.itemExample1();
        Order order1 = RelationshipsExamples.orderExample1();
        order1.setCustomer(cusClone1);
        order1.setItem(item1);
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.persist(cusClone1);
            em.persist(order1);
            commitTransaction(em);
            cusIDs[0] = cusClone1.getCustomerId();
            orderIDs[0] = order1.getOrderId();
            itemIDs[0] = item1.getItemId();
    
            clearCache();
    
            try {
                ServerSession ss = getServerSession();
                Vector vec = new Vector();
                vec.add(itemIDs[0]);
                list = (List) ss.executeQuery("findAllFieldAccessOrdersByItem", vec);
            } catch (Exception ex) {
                exception = ex;
            }
            beginTransaction(em);
            Customer cus1 = em.find(Customer.class, cusIDs[0]);
            em.remove(cus1);
            Order ord1 = em.find(Order.class, orderIDs[0]);
            em.remove(ord1);
            Item it1 = em.find(Item.class, itemIDs[0]);
            em.remove(it1);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }

        if (exception != null) {
            fail("An exception is thrown: " + exception);
        }
        if (list.size() != 1) {
            fail("One order is expected but " + list.size() + " was returned");
        }
    }
}
