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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     07/15/2010-2.2 Guy Pelletier 
 *       -311395 : Multiple lifecycle callback methods for the same lifecycle event
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.inherited;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.internal.security.SecurableObjectHolder;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.Accredidation;
import org.eclipse.persistence.testing.models.jpa.inherited.Beer;
import org.eclipse.persistence.testing.models.jpa.inherited.Alpine;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Beverage;
import org.eclipse.persistence.testing.models.jpa.inherited.ExpertBeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.SerialNumber;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
 
@SuppressWarnings("deprecation")
public class InheritedCallbacksJunitTest extends JUnitTestCase {
    private static Integer m_Id;
    
    public InheritedCallbacksJunitTest() {
        super();
    }
    
    public InheritedCallbacksJunitTest(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("InheritedCallbacksJunitTest");
        suite.addTest(new InheritedCallbacksJunitTest("testSetup"));
        suite.addTest(new InheritedCallbacksJunitTest("testPreAndPostPersistAlpine"));
        suite.addTest(new InheritedCallbacksJunitTest("testPrePersistAlpineOnMerge"));
        suite.addTest(new InheritedCallbacksJunitTest("testPrePersistAlpineAndSerialNumberOnBeerConsumerMerge"));
        suite.addTest(new InheritedCallbacksJunitTest("testPreAndPostPersistBeerConsumer"));
        suite.addTest(new InheritedCallbacksJunitTest("testPreAndPostPersistExpertBeerConsumer"));
        suite.addTest(new InheritedCallbacksJunitTest("testPostLoadOnFind"));
        suite.addTest(new InheritedCallbacksJunitTest("testPostLoadOnRefresh"));
        suite.addTest(new InheritedCallbacksJunitTest("testPreAndPostUpdate"));
        suite.addTest(new InheritedCallbacksJunitTest("testPreAndPostRemove"));

        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritedTableManager().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void testPreAndPostPersistAlpine() {
        EntityManager em = createEntityManager();
        
        try {
            int beerPrePersistCount = Beer.BEER_PRE_PERSIST_COUNT;
            int alpinePrePersistCount = Alpine.ALPINE_PRE_PERSIST_COUNT;
            
            int beveragePostPersistCount = Beverage.BEVERAGE_POST_PERSIST_COUNT;
            int beerPostPersistCount = Beer.BEER_POST_PERSIST_COUNT;
            int alpinePostPersistCount = Alpine.ALPINE_POST_PERSIST_COUNT;
            
            beginTransaction(em);
            SerialNumber serialNumber = new SerialNumber();
            em.persist(serialNumber);
            Alpine alpine = new Alpine(serialNumber);
            alpine.setBestBeforeDate(new java.util.Date(2007, 8, 17));
            alpine.setAlcoholContent(5.0);
            
            em.persist(alpine);
            commitTransaction(em);
            
            verifyNotCalled(beerPrePersistCount, Beer.BEER_PRE_PERSIST_COUNT, "celebrate");
            verifyCalled(alpinePrePersistCount, Alpine.ALPINE_PRE_PERSIST_COUNT, "celebrate");
            
            verifyNotCalled(beveragePostPersistCount, Beverage.BEVERAGE_POST_PERSIST_COUNT, "celebrateAgain");
            verifyCalled(beerPostPersistCount, Beer.BEER_POST_PERSIST_COUNT, "celebrateSomeMore");
            verifyCalled(alpinePostPersistCount, Alpine.ALPINE_POST_PERSIST_COUNT, "celebrateAgain");
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testPrePersistAlpineOnMerge() {
        EntityManager em = createEntityManager();
        
        try {
            int beerPrePersistCount = Beer.BEER_PRE_PERSIST_COUNT;
            int alpinePrePersistCount = Alpine.ALPINE_PRE_PERSIST_COUNT;
            
            beginTransaction(em);
            SerialNumber serialNumber = new SerialNumber();
            em.persist(serialNumber);
            Alpine alpine = new Alpine(serialNumber);
            alpine.setBestBeforeDate(new java.util.Date(2007, 8, 17));
            alpine.setAlcoholContent(5.0);
            
            alpine.setClassification(Alpine.Classification.NONE);
            
            Alpine mergedAlpine = em.merge(alpine);
        
            verifyNotCalled(beerPrePersistCount, Beer.BEER_PRE_PERSIST_COUNT, "PrePersist");
            verifyCalled(alpinePrePersistCount, Alpine.ALPINE_PRE_PERSIST_COUNT, "PrePersist");
            assertTrue("The merged alpine classification was not updated from the PrePersist lifecycle method", mergedAlpine.getClassification() == Alpine.Classification.STRONG);
            
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testPrePersistAlpineAndSerialNumberOnBeerConsumerMerge() {
        EntityManager em = createEntityManager();
                
        try {
            int beerPrePersistCount = Beer.BEER_PRE_PERSIST_COUNT;
            int alpinePrePersistCount = Alpine.ALPINE_PRE_PERSIST_COUNT;
            int serialNumberPrePersistCount = SerialNumber.SERIAL_NUMBER_PRE_PERSIST_COUNT;
            
            beginTransaction(em);
            BeerConsumer beerConsumer = new BeerConsumer();
            beerConsumer.setName("A consumer to delete eventually");
            em.persist(beerConsumer);
            em.clear();
         
            SerialNumber serialNumber = new SerialNumber();
                    
            Alpine alpine = new Alpine(serialNumber);
            alpine.setBestBeforeDate(new java.util.Date(2007, 8, 17));
            alpine.setAlcoholContent(5.0);
                    
            alpine.setClassification(Alpine.Classification.NONE);
            beerConsumer.addAlpineBeerToConsume(alpine);
                    
            BeerConsumer mergedBeerConsumer = em.merge(beerConsumer);
            
            mergedBeerConsumer.getAlpineBeerToConsume(0).setId(mergedBeerConsumer.getAlpineBeerToConsume(0).getSerialNumber().getNumber());
            verifyNotCalled(beerPrePersistCount, Beer.BEER_PRE_PERSIST_COUNT, "PrePersist");
            verifyCalled(alpinePrePersistCount, Alpine.ALPINE_PRE_PERSIST_COUNT, "PrePersist");
            verifyCalled(serialNumberPrePersistCount, SerialNumber.SERIAL_NUMBER_PRE_PERSIST_COUNT, "PrePersist");
            assertTrue("The merged alpine classification was not updated from the PrePersist lifecycle method", mergedBeerConsumer.getAlpineBeerToConsume(0).getSerialNumber().getIssueDate() != null);
                     
            commitTransaction(em);
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testPreAndPostPersistBeerConsumer() {
        EntityManager em = createEntityManager();
        
        try {
            beginTransaction(em);
            BeerConsumer beerConsumer = new BeerConsumer();
            beerConsumer.setName("A consumer to delete eventually");
            em.persist(beerConsumer);
            m_Id = beerConsumer.getId();
            commitTransaction(em);
            
            verifyCalled(0, beerConsumer.pre_persist_count, "PrePersist");
            verifyCalled(0, beerConsumer.post_persist_count, "PostPersist");
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testPreAndPostPersistExpertBeerConsumer() {
        EntityManager em = createEntityManager();
        
        try {
            beginTransaction(em);
            ExpertBeerConsumer beerConsumer = new ExpertBeerConsumer();
            beerConsumer.setName("An expert consumer to delete eventually");
            Accredidation accredidation = new Accredidation();
            accredidation.setDetails("SuperElite");
            beerConsumer.setAccredidation(accredidation);
            em.persist(beerConsumer);
            commitTransaction(em);
            
            verifyNotCalled(0, beerConsumer.pre_persist_count, "PrePersist");
            verifyCalled(0, beerConsumer.ebc_pre_persist_count, "PrePersist");
            verifyCalled(0, beerConsumer.post_persist_count, "PostPersist");
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testPostLoadOnFind() {
        BeerConsumer beerConsumer = createEntityManager().find(BeerConsumer.class, m_Id);
        verifyCalled(0, beerConsumer.post_load_count, "PostLoad");
    }
    
    public void testPostLoadOnRefresh() {
        EntityManager em = createEntityManager();
        
        try {
            beginTransaction(em);
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_Id);
            em.refresh(beerConsumer);
            commitTransaction(em);
            
            verifyCalled(0, beerConsumer.post_load_count, "PostLoad");
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testPreAndPostUpdate() {        
        EntityManager em = createEntityManager();

        try {
            int count1, count2 = 0;
        
            beginTransaction(em);
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_Id);
            count1 = beerConsumer.pre_update_count;
            beerConsumer.setName("An updated name");
            count2 = beerConsumer.post_update_count;
            commitTransaction(em);
            
            verifyCalled(count1, beerConsumer.pre_update_count, "PreUpdate");
            verifyCalled(count2, beerConsumer.post_update_count, "PostUpdate");
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void testPreAndPostRemove() {
        EntityManager em = createEntityManager();
        
        try {
            int count1, count2 = 0;
            
            beginTransaction(em);
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_Id);
            count1 = beerConsumer.pre_remove_count;
            em.remove(beerConsumer);
            count2 = beerConsumer.post_remove_count;
            commitTransaction(em);
            
            verifyCalled(count1, beerConsumer.pre_remove_count, "PreRemove");
            verifyCalled(count2, beerConsumer.post_remove_count, "PostRemove");
        } catch (RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    public void verifyCalled(int countBefore, int countAfter, String callback) {
        assertFalse("The callback method [" + callback + "] was not called", countBefore == countAfter);
        assertTrue("The callback method [" + callback + "] was called more than once", countAfter == (countBefore + 1));
    }
    
    public void verifyNotCalled(int countBefore, int countAfter, String callback) {
        assertTrue("The callback method [" + callback + "] was called.", countBefore == countAfter);
    }
}
