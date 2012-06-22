/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     12/02/2010-2.2 Guy Pelletier 
 *       - 251554: ExcludeDefaultMapping annotation needed
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.relationships;

import java.util.Iterator;
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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.descriptors.copying.CopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.Auditor;
import org.eclipse.persistence.testing.models.jpa.relationships.Customer2;
import org.eclipse.persistence.testing.models.jpa.relationships.CustomerCollection;
import org.eclipse.persistence.testing.models.jpa.relationships.CustomerServiceRepresentative;
import org.eclipse.persistence.testing.models.jpa.relationships.Lego;
import org.eclipse.persistence.testing.models.jpa.relationships.Item;
import org.eclipse.persistence.testing.models.jpa.relationships.Mattel;
import org.eclipse.persistence.testing.models.jpa.relationships.MegaBrands;
import org.eclipse.persistence.testing.models.jpa.relationships.Namco;
import org.eclipse.persistence.testing.models.jpa.relationships.OrderCard;
import org.eclipse.persistence.testing.models.jpa.relationships.OrderLabel;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsExamples;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.models.jpa.relationships.Order;
import org.eclipse.persistence.testing.models.jpa.relationships.ServiceCall;
import org.eclipse.persistence.testing.models.jpa.relationships.TestInstantiationCopyPolicy;

import org.eclipse.persistence.testing.models.jpa.relationships.Customer;

public class RelationshipModelJUnitTestSuite extends JUnitTestCase {
    private static Integer itemId;
    
    public RelationshipModelJUnitTestSuite() {
        super();
    }
    
    public RelationshipModelJUnitTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("RelationshipModelJUnitTestSuite");
        
        suite.addTest(new RelationshipModelJUnitTestSuite("testSetup")); 
        suite.addTest(new RelationshipModelJUnitTestSuite("testCreateItem")); 
        suite.addTest(new RelationshipModelJUnitTestSuite("testModifyItem"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testVerifyItem"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testInstantiationCopyPolicy"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCopyPolicy"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCloneCopyPolicy"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCollectionImplementation"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testCustomerServiceRepMap"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testOne2OneRelationTables"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testExecuteUpdateTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testGetResultCollectionTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testGetResultListTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testGetSingleResultTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testNamedQueryDoesNotExistTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testNamedQueryDoesNotExistTest"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testExcludeDefaultMappings"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testChangeSetForNewObject"));
        suite.addTest(new RelationshipModelJUnitTestSuite("testRemoveReference"));
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    /**
     * Verifies the exclude default mappings annotation.
     */
    public void testExcludeDefaultMappings() {
        ClassDescriptor descriptor = getServerSession().getDescriptor(Mattel.class);
        assertNull("The 'ignoredBasic' attribute from the clas Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredBasic"));
        assertNull("The 'ignoredOneToOne' attribute from the clas Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredOneToOne"));
        assertNull("The 'ignoredVariableOneToOne' attribute from the clas Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredVariableOneToOne"));
        assertNull("The 'ignoredOneToMany' attribute from the clas Mattel was mapped despite an exclude-default-mappings setting of true.", descriptor.getMappingForAttributeName("ignoredOneToMany"));
    }
    
    /**
     * Create a new item that has a variable one to one to a manufacturer.
     */
    public void testCreateItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            Item item = new Item();
            item.setName("Synergizer2000");
            item.setDescription("Every kid must have one ... ");
            
            // Manufacturer does not cascade persist
            Mattel mattel = new Mattel();
            mattel.setName("Mattel Inc.");
            em.persist(mattel);
            item.setManufacturer(mattel);
            
            // Distributor will cascade persist
            Namco namco = new Namco();
            namco.setName("Namco Games");
            item.setDistributor(namco);
            
            em.persist(item);
            itemId = item.getItemId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);
    }
    
    /**
     * Read an item, verify it contents, modify it and commit.
     */
    public void testModifyItem() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        try {
            Item item = em.find(Item.class, itemId);
            item.setName("Willy Waller");
            item.setDescription("For adults only!");
            
            assertTrue("The manufacturer was not persisted", item.getManufacturer() != null);
            assertTrue("The manufacturer of the item was incorrect", item.getManufacturer().getName().equals("Mattel Inc."));
            
            Lego lego = new Lego();
            lego.setName("The LEGO Group");
            item.setManufacturer(lego);

            assertTrue("The distributor was not persisted", item.getDistributor() != null);
            assertTrue("The distributor of the item was incorrect", item.getDistributor().getName().equals("Namco Games"));
                    
            MegaBrands megaBrands = new MegaBrands();
            megaBrands.setName("MegaBrands Inc.");
            item.setDistributor(megaBrands);
            
            em.merge(item);
            em.persist(lego);
            em.persist(megaBrands);
            commitTransaction(em);    
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);
    }
    
    /**
     * Verify the final contents of item.
     */
    public void testVerifyItem() {
        EntityManager em = createEntityManager();
        Item item = em.find(Item.class, itemId);
        
        assertTrue("The manufacturer was not persisted", item.getManufacturer() != null);
        assertTrue("The manufacturer of the item was incorrect", item.getManufacturer().getName().equals("The LEGO Group"));

        assertTrue("The distributor was not persisted", item.getDistributor() != null);
        assertTrue("The distributor of the item was incorrect", item.getDistributor().getName().equals("MegaBrands Inc."));
        
        closeEntityManager(em);
    }
    
    public void testInstantiationCopyPolicy(){
        assertTrue("The InstantiationCopyPolicy was not properly set.", getServerSession().getDescriptor(Item.class).getCopyPolicy() instanceof InstantiationCopyPolicy);
    }
    
    public void testCopyPolicy(){
        assertTrue("The CopyPolicy was not properly set.", getServerSession().getDescriptor(Order.class).getCopyPolicy() instanceof TestInstantiationCopyPolicy);
    }
    
    public void testCloneCopyPolicy(){
        CopyPolicy copyPolicy = getServerSession().getDescriptor(Namco.class).getCopyPolicy();
        assertTrue("The CloneCopyPolicy was not properly set.", copyPolicy  instanceof CloneCopyPolicy);
        assertTrue("The method on CloneCopyPolicy was not properly set.", ((CloneCopyPolicy)copyPolicy).getMethodName().equals("cloneNamco"));
        assertTrue("The workingCopyMethod on CloneCopyPolicy was not properly set.", ((CloneCopyPolicy)copyPolicy).getWorkingCopyMethodName().equals("cloneWorkingCopyNamco"));
    }
    
    /**
     * bug 236275: Use a Collection implementation type that does not implement List in an eager relationship
     * This test uses a HashSet 
     */
    public void testCollectionImplementation(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Customer c = new Customer();
            //Customer uses HashSet by default, but set it anyway to ensure the model doesn't change. 
            CustomerCollection collection = new CustomerCollection();
            c.setCCustomers(collection);
            em.persist(c);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        closeEntityManager(em);
    }
    
    // Bug 282571 - Map and ManyToMany
    public void testCustomerServiceRepMap(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Customer cust = new Customer();
        cust.setName("Kovie");
        cust.setCity("Ottawa");
        
        CustomerServiceRepresentative rep = new CustomerServiceRepresentative();
        rep.setName("Brian");
        
        ServiceCall call = new ServiceCall();
        call.setDescription("Trade from Habs.");
        
        cust.addCSInteraction(call, rep);
        
        em.persist(call);
        em.persist(cust);
        em.flush();
        
        rollbackTransaction(em);
    }
    
    /**
     * This tests a couple scenarios:
     * - 1-M mapped by a M-1 using a JoinTable
     * - 1-1 mapped using a JoinTable (uni-directional)
     * - 1-1 mapped using a JoinTable (bi-directional)
     */
    public void testOne2OneRelationTables() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Order order1 = new Order();
        Order order2 = new Order();
        Auditor auditor = new Auditor();
                
        try {
            OrderCard order1Card = new OrderCard();
            OrderLabel order1Label = new OrderLabel();
            order1Label.setDescription("I describe order 1");
            order1.setOrderLabel(order1Label);
            order1.setOrderCard(order1Card);
            em.persist(order1);
            
            OrderCard order2Card = new OrderCard();
            OrderLabel order2Label = new OrderLabel();
            order2Label.setDescription("I describe order 2");
            order2.setOrderLabel(order2Label);
            order2.setOrderCard(order2Card);
            em.persist(order2);
                
            auditor.setName("Guillaume");
            auditor.addOrder(order1);
            auditor.addOrder(order2);
            em.persist(auditor);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);

        
        clearCache();
        em = createEntityManager();
        
        Auditor refreshedAuditor = em.find(Auditor.class, auditor.getId());
        Order refreshedOrder1 = em.find(Order.class, order1.getOrderId());
        Order refreshedOrder2 = em.find(Order.class, order2.getOrderId());
        
        assertTrue("Auditor read back did not match the original", getServerSession().compareObjects(auditor, refreshedAuditor));
        assertTrue("Order1 read back did not match the original", getServerSession().compareObjects(order1, refreshedOrder1));
        assertTrue("Order2 read back did not match the original", getServerSession().compareObjects(order2, refreshedOrder2));
        
        closeEntityManager(em);
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
            Query query = em.createQuery("UPDATE Customer customer SET customer.name = '" + nameChange1 + "' WHERE customer.customerId = " + cusIDs[0]);
            query.executeUpdate();
            em.clear();
            clearCache();
            // getEntityManager().refresh(cus);
            cus = em.find(Customer.class, cusIDs[0]);
            returnedName1 = cus.getName();

            // tests bug 4288845
            Query query2 = em.createQuery("UPDATE Customer customer SET customer.name = :name WHERE customer.customerId = " + cusIDs[0]);
            query2.setParameter("name", nameChange2);
            query2.executeUpdate();
            em.clear();
            clearCache();
            // getEntityManager().refresh(cus);
            cus = em.find(Customer.class, cusIDs[0]);
            returnedName2 = cus.getName();

            // tests bug 4293920
            Query query3 = em.createQuery("UPDATE Customer customer SET customer.name = :name WHERE customer.customerId = :id");
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
                Query query4 = em.createNamedQuery("findAllCustomers");
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
                fail("excuteUpdate did not result in an exception on findAllCustomers named ReadAllQuery");
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
        String ejbql1 = "SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id";
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

            EJBQueryImpl query1 = (EJBQueryImpl) entityManagerImpl.createNamedQuery("findAllCustomers");
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
        String ejbql1 = "SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id";
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

            Query query1 = em.createNamedQuery("findAllCustomers");
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
                returnedCustomer1 = (Customer) em.createNamedQuery("findAllCustomers").getSingleResult();
            } catch (NonUniqueResultException exceptionExpected1) {
                expectedException1 = exceptionExpected1;
            }
            try {
                // should be no Items to find, which should cause an
                // NoResultException
                Query query1 = em.createNamedQuery("findAllItemsByName");
                Item item = (Item) query1.setParameter(1, searchString).getSingleResult();
                item.toString();
            } catch (NoResultException exceptionExpected2) {
                expectedException2 = exceptionExpected2;
            }
            // bug 4301674 test
            EJBQueryImpl query2 = (EJBQueryImpl) em.createNamedQuery("findAllCustomers");
            ReadAllQuery readAllQuery = new ReadAllQuery(Customer.class);
            MapContainerPolicy mapContainerPolicy = new MapContainerPolicy();
            mapContainerPolicy.setContainerClass(HashMap.class);
            mapContainerPolicy.setKeyName("hashCode");
            readAllQuery.setContainerPolicy(mapContainerPolicy);
            query2.setDatabaseQuery(readAllQuery);
            Map result = (Map) query2.getSingleResult();
            result.toString();
    
            // check for single result found.
            Query query3 = em.createQuery("SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id");
            returnedCustomer1 = (Customer) query3.setParameter("id", cusIDs[0]).getSingleResult();
    
            // check for single result using a ReadObjectQuery (tests previous
            // fix for 4202835)
            EJBQueryImpl query4 = (EJBQueryImpl) em.createQuery("SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id");
            query4.setParameter("id", cusIDs[0]);
            ReadObjectQuery readObjectQuery = new ReadObjectQuery(Customer.class);
            readObjectQuery.setEJBQLString("SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id");
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
            Query query = em.createNamedQuery("doesNotExist");
            // Query will just be a wrapper on WLS until the query is run because we are not in a Tx
            // workaround that issue 
            if (getServerSession().getServerPlatform().getClass().getName().contains("WebLogic")){
                query.getResultList();
            }
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
                list = (List) ss.executeQuery("findAllOrdersByItem", vec);
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
    
    // Bug 357103
    public void testChangeSetForNewObject(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Customer cust = new Customer();
        cust.setName("Joe");
        em.persist(cust);
        Order order = new Order();
        order.setQuantity(1);
        em.persist(order);
        cust.addOrder(order);
        commitTransaction(em);
        
        RepeatableWriteUnitOfWork uow = null;
        try{
            beginTransaction(em);
            cust = em.find(Customer.class, cust.getCustomerId());
            Order order2 = new Order();
            order2.setQuantity(2);
            order2.setShippingAddress("123 Main St.");
            em.persist(order2);
            cust.addOrder(order2);
            
            EntityManagerImpl impl = (EntityManagerImpl)JpaHelper.getEntityManager(em);
            uow = impl.getActivePersistenceContext(null);
            em.flush();
            UnitOfWorkChangeSet uowChangeSet = (UnitOfWorkChangeSet)uow.getCumulativeUOWChangeSet();
            ObjectChangeSet customerChangeSet = uowChangeSet.getCloneToObjectChangeSet().get(cust);
            CollectionChangeRecord orderChangeRecord = (CollectionChangeRecord)customerChangeSet.getChangesForAttributeNamed("orders");
            Iterator<ObjectChangeSet> i = orderChangeRecord.getAddObjectList().keySet().iterator();
            while(i.hasNext()){
                ObjectChangeSet orderChangeSet = i.next();
                assertTrue("There are changes in the change set.  There should be no changes for a new object.", orderChangeSet.getChanges().isEmpty());
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally{
            rollbackTransaction(em);
            beginTransaction(em);
            cust = em.find(Customer.class, cust.getCustomerId());
            em.remove(cust);
            commitTransaction(em);
        }
    }

    public void testRemoveReference() {
        EntityManager em = createEntityManager();
        
        try {
            
            beginTransaction(em);
            Customer2 customer = new Customer2();
            em.persist(customer);
            
            int id = customer.getCustomerId();
            
            commitTransaction(em);
            
            em.clear();
            closeEntityManager(em);
            clearCache();
            
            em = createEntityManager();
            Customer2 customerReference = em.getReference(Customer2.class, id);
            
            beginTransaction(em);
            em.remove(customerReference);
            commitTransaction(em);
            
            assertNull("Customer was not removed", em.find(Customer2.class, id));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
