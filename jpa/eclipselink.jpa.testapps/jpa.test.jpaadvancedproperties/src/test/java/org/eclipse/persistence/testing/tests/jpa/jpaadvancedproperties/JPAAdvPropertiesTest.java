/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.SessionEventListener;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.JPAPropertiesRelationshipTableManager;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * JUnit test case(s) for the TopLink JPAAdvPropertiesTest.
 */
public class JPAAdvPropertiesTest extends JUnitTestCase {

    public JPAAdvPropertiesTest() {
        super();
    }

    public JPAAdvPropertiesTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "JPAADVProperties";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Jakarta Persistence Advanced Properties Model");
        suite.addTest(new JPAAdvPropertiesTest("testSetup"));
        suite.addTest(new JPAAdvPropertiesTest("testSessionXMLProperty"));
        suite.addTest(new JPAAdvPropertiesTest("testSessionEventListenerProperty"));
        suite.addTest(new JPAAdvPropertiesTest("testExceptionHandlerProperty"));
        suite.addTest(new JPAAdvPropertiesTest("testNativeSQLProperty"));
        suite.addTest(new JPAAdvPropertiesTest("testCacheStatementsAndItsSizeProperty"));
        suite.addTest(new JPAAdvPropertiesTest("testBatchwritingProperty"));
        suite.addTest(new JPAAdvPropertiesTest("testCopyDescriptorNamedQueryToSessionProperty"));
        suite.addTest(new JPAAdvPropertiesTest("testLoggingTyperProperty"));
        suite.addTest(new JPAAdvPropertiesTest("testLoginEncryptorProperty"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new JPAPropertiesRelationshipTableManager().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }


    public void testSessionEventListenerProperty() {
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        try {
            //Create new customer
            beginTransaction(em);
            Customer customer = ModelExamples.customerExample1();
            em.persist(customer);
            em.flush();
            Integer customerId = customer.getCustomerId();
            commitTransaction(em);

            //Purge it
            beginTransaction(em);
            em.remove(em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId));
            commitTransaction(em);

            List<SessionEventListener> listeners = session.getEventManager().getListeners();
            boolean doseCustomizedSessionEventListenerExists=false;
            for (Object aListener : listeners) {
                if (aListener instanceof CustomizedSessionEventListener requiredListener) {
                    doseCustomizedSessionEventListenerExists = true;
                    if (!requiredListener.preCommitTransaction) {
                        fail(" The preCommitTransaction event did not fire");
                    }
                    if (!requiredListener.postCommitTransaction) {
                        fail("The postCommitTransaction event did not fire");
                    }
                    if (!requiredListener.preBeginTransaction) {
                        fail("The preBeginTransaction event did not fire");
                    }
                    if (!requiredListener.postBeginTransaction) {
                        fail("The postBeginTransaction event did not fire");
                    }
                    if (!requiredListener.postLogin) {
                        fail("The postlogin event did not fire");
                    }
                    if (!requiredListener.preLogin) {
                        fail("The preLogin event did not fire");
                    }
                }
            }
            if(!doseCustomizedSessionEventListenerExists){
                fail("The session event listener specified by the property eclipselink.session-event-listener in persistence.xml not be processed properly.");
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        }finally{
            closeEntityManager(em);
        }
    }

    public void testExceptionHandlerProperty() {
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        Expression exp = new ExpressionBuilder().get("name1").equal("George W.");
        try {
            Object result = session.readObject(Customer.class, exp);
            if(!result.equals("return from CustomizedExceptionHandler")){
                fail("The exception handler specified by the property eclipselink.exception-handler in persistence.xml not be processed.");
            }
        } finally {
            closeEntityManager(em);
        }
    }

    public void testNativeSQLProperty() {
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        if(!session.getProject().getLogin().shouldUseNativeSQL()){
            fail("The native sql flag specified as true by the property eclipselink.jdbc.native-sql in persistence.xml, it however read as false.");
        }
        closeEntityManager(em);
    }

    public void testBatchwritingProperty(){
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        if(!(session.getPlatform().usesBatchWriting() &&
           !session.getPlatform().usesJDBCBatchWriting() &&
           !session.getPlatform().usesNativeBatchWriting())){
            fail("The BatcheWriting setting set to BUFFERED by the property eclipselink.jdbc.batch-writing in persistence.xml, JDBC batch writing or native batch writing however may be wrong.");
        }
        closeEntityManager(em);
    }

    public void testCopyDescriptorNamedQueryToSessionProperty(){
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        org.eclipse.persistence.queries.DatabaseQuery query = session.getQuery("customerReadByName");
        if(query == null){
            fail("The copy descriptor named query is enable by the property eclipselink.session.include.descriptor.queries in persistence.xml, one descriptor named query has not been copied to the session");
        }
        closeEntityManager(em);
    }

    public void testCacheStatementsAndItsSizeProperty() {
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        if(!session.getConnectionPools().isEmpty()){//And if connection pooling is configured,
            if(!session.getProject().getLogin().shouldCacheAllStatements()){
                fail("Caching all statements flag set equals to true by property eclipselink.jdbc.cache-statements in persistence.xml, it however read as false.");
            }
        }

        if(!(session.getProject().getLogin().getStatementCacheSize()==100)){
            fail("Statiment cache size set to 100 by property eclipselink.jdbc.cache-statements in persistence.xml, it however read as wrong size.");
        }
        closeEntityManager(em);
    }

    public void testLoggingTyperProperty(){
        EntityManager em = createEntityManager();
        ServerSession session = em.unwrap(ServerSession.class);
        if(!(session.getSessionLog() instanceof org.eclipse.persistence.logging.JavaLog)){
            fail("Logging type set to JavaLog, it however has been detected as different type logger.");
        }
        closeEntityManager(em);
    }

    public void testSessionXMLProperty() {
        Integer customerId;
        EntityManager em = createEntityManager();

        //Create customer
        beginTransaction(em);
        try {
            Customer customer = ModelExamples.customerExample1();
            List<Order> orders = new ArrayList<>();
            orders.add(ModelExamples.orderExample1());
            orders.add(ModelExamples.orderExample2());
            orders.add(ModelExamples.orderExample3());
            customer.setOrders(orders);
            em.persist(customer);
            em.flush();
            customerId = customer.getCustomerId();
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        //Find customer
        beginTransaction(em);
        try {
        Customer cm1 = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
        if(cm1==null){
            fail("Error finding customer ");
        }

        Collection<?> orders = cm1.getOrders();
        if (orders == null || orders.size()!=3){
            fail("Error finding order pertaining to the customer ");
            }
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }

        //Query the customer
        Customer cm2 = null;
        beginTransaction(em);
        try {
            Query query = createEntityManager().createNamedQuery("customerReadByName");
            query.setParameter("name", "George W.");
            cm2 = (Customer) query.getSingleResult();
            commitTransaction(em);
        }catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNotNull("Error executing named query 'customerReadByName'", cm2);

        //Update customer
        beginTransaction(em);
        String originalName = null;
        try {
            Customer cm = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
            originalName=cm.getName();
            cm.setName(originalName+"-modified");
            em.merge(cm);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        clearCache();
        Customer cm3 = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
        assertEquals("Error updating Customer", cm3.getName(), originalName + "-modified");

        //Delete customer
        beginTransaction(em);
        try {
            em.remove(em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId));
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
        assertNull("Error deleting Customer", em.find(Customer.class, customerId));

    }

    public void testLoginEncryptorProperty() {
        EntityManager em = createEntityManager();
        try {
            //Create new customer
            beginTransaction(em);
            Customer customer = ModelExamples.customerExample1();
            em.persist(customer);
            em.flush();
            Integer customerId = customer.getCustomerId();
            commitTransaction(em);

            customer = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
            //Purge it
            beginTransaction(em);
            em.remove(customer);
            commitTransaction(em);

            assertNotNull(customer);
            assertTrue("CustomizedEncryptor.encryptPassword() method wasn't called.", CustomizedEncryptor.encryptPasswordCounter > 0);
            assertTrue("CustomizedEncryptor.decryptPassword() method wasn't called.", CustomizedEncryptor.decryptPasswordCounter > 0);
        } finally {
            closeEntityManager(em);
        }
    }
}
