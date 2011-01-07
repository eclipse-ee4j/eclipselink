/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.relationships;


import java.util.Collection;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import javax.persistence.EntityNotFoundException;

import org.eclipse.persistence.testing.models.jpa.relationships.*;
import org.eclipse.persistence.exceptions.DatabaseException;

import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EMQueryJUnitTestSuite extends JUnitTestCase {
    protected Integer nonExistingCustomerId = new Integer(999999);
        
    public EMQueryJUnitTestSuite() {
    }
    
    public EMQueryJUnitTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EMQueryJUnitTestSuite");
        
        suite.addTest(new EMQueryJUnitTestSuite("testSetup"));
        suite.addTest(new EMQueryJUnitTestSuite("testgetReference"));
        suite.addTest(new EMQueryJUnitTestSuite("testcreateNativeQuery"));
        suite.addTest(new EMQueryJUnitTestSuite("testcreateNativeQueryWithSelectSQL"));
        suite.addTest(new EMQueryJUnitTestSuite("testNativeNamedQuery"));
        suite.addTest(new EMQueryJUnitTestSuite("testSetParameterUsingNull"));
        suite.addTest(new EMQueryJUnitTestSuite("testExcludingUnneccesaryJoin"));
        suite.addTest(new EMQueryJUnitTestSuite("testRemoveUnneccesaryDistinctFromJoin"));
        
        return suite;
    }
    
    public void testSetup() {
        clearCache();
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());        
    }

    
    /*
     * bug 4628215: changed ObjectNotFoundException to EntityNotFoundException, and modified getReference to 
     * throw this exception instead of returning null.
     */
    public void testgetReference() throws Exception {
        Customer customer=null;
        Exception exception= null;
        try{
            customer = createEntityManager().getReference(Customer.class,nonExistingCustomerId );
            customer.getCity();
        }catch(Exception e){
            exception=e;
        }
        assertTrue("getReference() did not throw an instance of EntityNotFoundException", exception instanceof EntityNotFoundException);
    }

    /*
     * createNativeQuery(string) feature test
     *   tests that delete/insert and selects can be executed.
     */
    public void testcreateNativeQuery() throws Exception {
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            Query query1 = em.createNativeQuery("Select * FROM CMP3_CUSTOMER");
            Query query2 = em.createNativeQuery("INSERT INTO CMP3_CUSTOMER (CUST_ID, NAME, CITY, CUST_VERSION) VALUES (1111, NULL, NULL, 1)");
            Query query3 = em.createNativeQuery("DELETE FROM CMP3_CUSTOMER WHERE (CUST_ID=1111)");

            Collection c1 = query1.getResultList();
            if ((c1!=null) &&(c1.size()>0))
            assertTrue("getResultList returned null ",c1!=null );
            query2.executeUpdate();
            Collection c2 = query1.getResultList();
            assertTrue("getResultList returned null ",c2!=null );
            query3.executeUpdate();
            Collection c3 = query1.getResultList();
            assertTrue("getResultList returned null ",c3!=null );

            assertTrue("Native Select query gave unexpected result after Native Insert query ", c2.size()==(c1.size()+1) );
            assertTrue("Native Select query gave unexpected result after Native Delete query ", c3.size()==c1.size() );
        }finally{
            try{
                rollbackTransaction(em);
                closeEntityManager(em);
            }catch(Exception ee){}
        }
    }

    /*
     * createNativeQuery(string) feature test
     *   tests that Query with Select SQL can be executed using getResultList() after it
     *   has run using executeUpdate()
     */
    public void testcreateNativeQueryWithSelectSQL() throws Exception {
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            Query query1 = em.createNativeQuery("Select * FROM CMP3_CUSTOMER");

            Collection c1 = query1.getResultList();
            assertTrue("getResultList returned null ",c1!=null );

            // this may fail with some drivers
            int result = 0;
            try {
                result = query1.executeUpdate();
            } catch (RuntimeException ex) {
                rollbackTransaction(em);
                closeEntityManager(em);
                em = createEntityManager(); 
                beginTransaction(em);
                // inside container need to query again in order to attach em
                query1 = em.createNativeQuery("Select * FROM CMP3_CUSTOMER");
            }
        
            Query query2 = em.createNativeQuery("INSERT INTO CMP3_CUSTOMER (CUST_ID, NAME, CITY, CUST_VERSION) VALUES (1111, NULL, NULL, 1)");
            Query query3 = em.createNativeQuery("DELETE FROM CMP3_CUSTOMER WHERE (CUST_ID=1111)");

            query2.executeUpdate();
            Collection c2 = query1.getResultList();
            assertTrue("getResultList returned null ",c2!=null );
            query3.executeUpdate();
            Collection c3 = query1.getResultList();
            assertTrue("getResultList returned null ",c3!=null );

            assertTrue("Native Select query run with executeUpdate modified "+result+" rows ", result==0 );
            assertTrue("Native Select query gave unexpected result after Native Insert query ", c2.size()==(c1.size()+1) );
            assertTrue("Native Select query gave unexpected result after Native Delete query ", c3.size()==c1.size() );
        }finally{
            try{
                rollbackTransaction(em);
                closeEntityManager(em);
            }catch(Exception ee){}
        }
    }

    /*
     * createNativeQuery(string) feature test
     *   tests that delete/insert and selects defined through annotations can be executed.
     */
    public void testNativeNamedQuery() throws Exception {
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
            Query query1 = em.createNamedQuery("findAllSQLCustomers");
            Query query2 = em.createNamedQuery("insertCustomer1111SQL");
            Query query3 = em.createNamedQuery("deleteCustomer1111SQL");

            Collection c1 = query1.getResultList();
            assertTrue("getResultList returned null ",c1!=null );
            query2.executeUpdate();
            Collection c2 = query1.getResultList();
            assertTrue("getResultList returned null ",c2!=null );
            query3.executeUpdate();
            Collection c3 = query1.getResultList();
            assertTrue("getResultList returned null ",c3!=null );

            assertTrue("Named Native Select query gave unexpected result after Named Native Insert query ", c2.size()==(c1.size()+1) );
            assertTrue("Named Native Select query gave unexpected result after Named Native Delete query ", c3.size()==c1.size() );
        }finally{
            try{
                rollbackTransaction(em);
                closeEntityManager(em);
            }catch(Exception ee){}
        }
    }
    
    /*
     * bug 4775066: base EJBQueryImpl to check for null arguments to avoid throwing a NPE.
     * result doesn't matter, only that it doesn't throw or cause an NPE or other exception.
     */
    public void testSetParameterUsingNull() throws Exception {
        try {
            createEntityManager().createQuery(
                "Select Distinct Object(c) from Customer c where c.name = :cName")
                .setParameter("cName", null)
                .getResultList();
        } catch (DatabaseException e) {
            // Above query generates following sql
            // SELECT DISTINCT CUST_ID, CITY, NAME, CUST_VERSION FROM CMP3_CUSTOMER WHERE (NAME = NULL)
            // which will not work on most of the dbs
            // Ignore any resulting DatabaseException
        }
    }
    
    /*
     * bug 5683148/2380: Reducing unnecessary joins on an equality check between the a statement
     *   and itself
     */
    public void testExcludingUnneccesaryJoin() throws Exception {
        EntityManager em = createEntityManager();
        try{
            beginTransaction(em);
                
            Order o = new Order();
            Order o2 = new Order();
            em.persist(o);
            em.persist(o2);
                
            java.util.List results = em.createQuery(
                    "Select Distinct Object(a) From OrderBean a where a.item Is Null OR a.item <> a.item")
                    .getResultList();
            assertTrue("Incorrect results returned when testing equal does not produce an unnecessary join ", results.size()==2 );
        }finally{
            try{
                rollbackTransaction(em);
                closeEntityManager(em);
            }catch(Exception ee){}
        }
    }
    
    /*
     * gf bug 1395: test that not using Distinct in JPQL produces different results than using Distinct
     */
    public void testRemoveUnneccesaryDistinctFromJoin() throws Exception {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Customer c = new Customer();
            Order o = new Order();
            o.setShippingAddress("somerandomaddress");
            o.setCustomer(c);
            Order o2 = new Order();
            o2.setShippingAddress("somerandomaddress");
            o2.setCustomer(c);
            
            c.getOrders().add(o);
            c.getOrders().add(o2);
            
            em.persist(c);
            em.flush();
            Collection results1 = em.createQuery(
                "Select Object(a) From Customer a JOIN a.orders o where o.shippingAddress = 'somerandomaddress'")
                .getResultList();
            
            Collection results2=em.createQuery(
                "Select Distinct a From Customer a JOIN a.orders o where o.shippingAddress = 'somerandomaddress'")
                .getResultList();
            assertTrue("Unexpected results returned from query without distinct clause", results1.size()==results2.size()+1);
        }finally{
            rollbackTransaction(em);
        }
    }

}
