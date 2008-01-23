/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.inherited;

import javax.persistence.EntityManager;

import junit.framework.*;
import junit.extensions.TestSetup;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.BeerConsumer;
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
 
public class InheritedModelJunitTest extends JUnitTestCase {
    private static Integer m_blueId;
    private static Integer m_beerConsumerId;
    
    public InheritedModelJunitTest() {
        super();
    }
    
    public InheritedModelJunitTest(String name) {
        super(name);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("InheritedModelJunitTest");
        suite.addTest(new InheritedModelJunitTest("testCreateBlue"));
        suite.addTest(new InheritedModelJunitTest("testReadBlue"));
        suite.addTest(new InheritedModelJunitTest("testCreateBeerConsumer"));
        suite.addTest(new InheritedModelJunitTest("testUpdateBeerConsumer"));

        return new TestSetup(suite) {
        
            protected void setUp() {               
                DatabaseSession session = JUnitTestCase.getServerSession();
                
                new InheritedTableManager().replaceTables(session);
            }

            protected void tearDown() {
                clearCache();
            }
        };
    }
    
    public void testCreateBlue() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        
        try {
            Blue blue = new Blue();
            blue.setAlcoholContent(5.3);
            em.persist(blue);
            m_blueId = blue.getId();
            blue.setUniqueKey(m_blueId);
            em.getTransaction().commit();    
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            
            em.close();
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        em.close();
    }
    
    public void testReadBlue() {
        Blue blue = createEntityManager().find(Blue.class, m_blueId);
        
        assertTrue("Error on reading back a Blue beer", blue != null);
    }
    
    public void testCreateBeerConsumer() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        
        try {    
            BeerConsumer beerConsumer = new BeerConsumer();
            beerConsumer.setName("Blue Consumer");
            em.persist(beerConsumer);
            m_beerConsumerId = beerConsumer.getId();
            em.getTransaction().commit();    
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            
            em.close();
            fail("An exception was caught during create operation: [" + e.getMessage() + "]");
        }
        
        em.close();
    }
    
    public void testUpdateBeerConsumer() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        
        try {    
            BeerConsumer beerConsumer = em.find(BeerConsumer.class, m_beerConsumerId);
            
            // Create a detached object that mirrors the beer consumer from
            // testCreateBeerConsumer.
            BeerConsumer beerConsumerDetached = new BeerConsumer();
            beerConsumerDetached.setName(beerConsumer.getName());
            beerConsumerDetached.setId(beerConsumer.getId());
            beerConsumerDetached.setVersion(beerConsumer.getVersion());
            
            Blue blue = em.find(Blue.class, m_blueId);
            beerConsumerDetached.addBlueBeerToConsume(blue);
            
            em.merge(beerConsumerDetached);
            em.getTransaction().commit();    
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            
            em.close();
            fail("An exception was caught during the merge of the detached beer consumer: [" + e.getMessage() + "]");
        }
        
        em.close();
    }
}
