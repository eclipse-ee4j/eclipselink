/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties;

import java.util.ArrayList;
import java.util.Vector;

import javax.persistence.EntityManager;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.ModelExamples;
import org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.JPAPropertiesRelationshipTableManager;
 


/**
 * JUnit test case(s) for the TopLink JPAAdvPropertiesJUnitTestCase.
 */
public class JPAAdvPropertiesJUnitTestCase extends JUnitTestCase {
    private static String persistenceUnitName = "JPAADVProperties";

    public JPAAdvPropertiesJUnitTestCase() {
        super();
    }
    
    public JPAAdvPropertiesJUnitTestCase(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("JPA Advanced Properties Model");
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testSessionXMLProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testSessionEventListenerProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testExceptionHandlerProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testNativeSQLProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testCacheStatementsAndItsSizeProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testBatchwritingProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testCopyDescriptorNamedQueryToSessionProperty"));
        suite.addTest(new JPAAdvPropertiesJUnitTestCase("testLoggingTyperProperty"));
        return new TestSetup(suite) {
            
            protected void setUp(){               
                DatabaseSession session = JUnitTestCase.getServerSession(persistenceUnitName); 
                JPAPropertiesRelationshipTableManager tm = new JPAPropertiesRelationshipTableManager();
                tm.replaceTables(session);
            }
        
            protected void tearDown() {
                clearCache();
            }
        };
    }
    
    public void testSessionEventListenerProperty() {
        
        EntityManager em = createEntityManager(persistenceUnitName);
        org.eclipse.persistence.sessions.server.ServerSession session = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).getServerSession();
        try {
            //Create new customer
            em.getTransaction().begin();
            Customer customer = ModelExamples.customerExample1();
            em.persist(customer);
            Integer customerId = customer.getCustomerId();            
            em.getTransaction().commit();
            
            //Purge it
            em.getTransaction().begin();
            em.remove(em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId));
            em.getTransaction().commit();

            Vector listeners = session.getEventManager().getListeners();
            boolean doseCustomizedSessionEventListenerExists=false;
            for (int i =0;i<listeners.size();i++){
                Object aListener = listeners.get(i);
                if(aListener instanceof CustomizedSessionEventListener){
                    doseCustomizedSessionEventListenerExists=true;
                    CustomizedSessionEventListener requiredListener = ((CustomizedSessionEventListener)aListener); 
                    if (!requiredListener.preCommitTransaction) {
                        assertTrue(" The preCommitTransaction event did not fire", true);
                    }
                    if (!requiredListener.postCommitTransaction) {
                        assertTrue("The postCommitTransaction event did not fire", true);
                    }
                    if (!requiredListener.preBeginTransaction) {
                        assertTrue("The preBeginTransaction event did not fire", true);
                    }
                    if (!requiredListener.postBeginTransaction) {
                        assertTrue("The postBeginTransaction event did not fire", true);
                    }
                    if (!requiredListener.postLogin) {
                        assertTrue("The postlogin event did not fire", true);
                    }
                    if (!requiredListener.preLogin) {
                        assertTrue("The preLogin event did not fire", true);
                    }
                }
            }
            if(!doseCustomizedSessionEventListenerExists){
                assertTrue("The session event listener specified by the property eclipselink.session-event-listener in persistence.xml not be processed properly.", true);
            }
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw e;
        }finally{
            em.close();
        }
    }

    public void testExceptionHandlerProperty() {
        EntityManager em = createEntityManager(persistenceUnitName);
        org.eclipse.persistence.sessions.server.ServerSession session = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).getServerSession();
        Expression exp = new ExpressionBuilder().get("name1").equal("George W.");
        try {
            Object result = session.readObject(Customer.class, exp);
            if(!((String)result).equals("return from CustomizedExceptionHandler")){
                assertTrue("The exception handler specified by the property eclipselink.exception-handler in persistence.xml not be processed.", true);
            }
        } finally {
            em.close();
        }
    }
    
    public void testNativeSQLProperty() {
        EntityManager em = createEntityManager(persistenceUnitName);
        org.eclipse.persistence.sessions.server.ServerSession session = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).getServerSession();
        if(!session.getProject().getLogin().shouldUseNativeSQL()){
            assertTrue("The native sql flag specified as true by the property eclipselink.jdbc.native-sql in persistence.xml, it however read as false.", true);
        }
        em.close();
    }
    
    public void testBatchwritingProperty(){
        EntityManager em = createEntityManager(persistenceUnitName);
        org.eclipse.persistence.sessions.server.ServerSession session = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).getServerSession();
        
        if(!(session.getPlatform().usesBatchWriting() && 
           !session.getPlatform().usesJDBCBatchWriting() &&
           !session.getPlatform().usesNativeBatchWriting())){
            assertTrue("The BatcheWriting setting set to BUFFERED by the property eclipselink.jdbc.batch-writing in persistence.xml, JDBC batch writing or natvie batch writing however may be wrong.", true);
        }
        em.close();
    }
    
    public void testCopyDescriptorNamedQueryToSessionProperty(){
        EntityManager em = createEntityManager(persistenceUnitName);
        org.eclipse.persistence.sessions.server.ServerSession session = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).getServerSession();
        org.eclipse.persistence.queries.DatabaseQuery query = session.getQuery("customerReadByName");
        if(query == null){
            assertTrue("The copy descriptor named query is enable by the property eclipselink.session.include.descriptor.queries in persistence.xml, one descriptor named query has not been copied to the session", true);
        }
        em.close();
    }
    
    public void testCacheStatementsAndItsSizeProperty() {
        EntityManager em = createEntityManager(persistenceUnitName);
        org.eclipse.persistence.sessions.server.ServerSession session = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).getServerSession();
        if(session.getConnectionPools().size()>0){//And if connection pooling is configured,
            if(!session.getProject().getLogin().shouldCacheAllStatements()){
                assertTrue("Caching all statements flag set equals to true by property eclipselink.jdbc.cache-statements in persistence.xml, it however read as false.", true);
            }
        }

        if(!(session.getProject().getLogin().getStatementCacheSize()==100)){
            assertTrue("Statiment cache size set to 100 by property eclipselink.jdbc.cache-statements in persistence.xml, it however read as wrong size.", true);
        }
        em.close();
    }
    
    public void testLoggingTyperProperty(){
        EntityManager em = createEntityManager(persistenceUnitName);
        org.eclipse.persistence.sessions.server.ServerSession session = ((org.eclipse.persistence.jpa.JpaEntityManager)em.getDelegate()).getServerSession();
        if(!(session.getSessionLog() instanceof org.eclipse.persistence.logging.JavaLog)){
            assertTrue("Logging type set to JavaLog, it however has been detected as different type logger.", true);
        }
        em.close();
    }
    
    public void testSessionXMLProperty() {
        Integer customerId;
        EntityManager em = createEntityManager(persistenceUnitName);
        
        //Create customer
        em.getTransaction().begin();
        try {
            Customer customer = ModelExamples.customerExample1();       
            ArrayList orders = new ArrayList();
            orders.add(ModelExamples.orderExample1());
            orders.add(ModelExamples.orderExample2());
            orders.add(ModelExamples.orderExample3());
            customer.setOrders(orders);
            em.persist(customer);
            customerId = customer.getCustomerId();
            em.getTransaction().commit();    
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        
        //Find customer
        em.getTransaction().begin();
        Customer cm1 = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
        if(cm1==null){
            assertTrue("Error finding customer ", true);    
        }

        ArrayList orders = (ArrayList)cm1.getOrders();
        if(orders == null || orders.size()!=3){
            assertTrue("Error finding order pertaining to the customer ", true);    
        }
        
        //Query the customer
        Customer cm2 = null;
        try {
            EJBQueryImpl query = (EJBQueryImpl) createEntityManager(persistenceUnitName).createNamedQuery("customerReadByName");
            query.setParameter("name", "George W.");
            cm2 = (Customer) query.getSingleResult();
        }catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        assertTrue("Error executing named query 'customerReadByName'", cm2 != null);

        
        //Update customer
        String originalName = null;
        try {
            Customer cm = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
            originalName=cm.getName();
            cm.setName(originalName+"-modified");
            em.merge(cm);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        clearCache();
        Customer cm3 = em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId);
        assertTrue("Error updating Customer", cm3.getName().equals(originalName+"-modified"));

        //Delete customer
        em.getTransaction().begin();
        try {
            em.remove(em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId));
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            em.close();
            throw e;
        }
        assertTrue("Error deleting Customer", em.find(org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer.class, customerId) == null);
        
    }
}
