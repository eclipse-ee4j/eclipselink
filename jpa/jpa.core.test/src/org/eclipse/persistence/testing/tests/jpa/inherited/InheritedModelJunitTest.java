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
import org.eclipse.persistence.testing.models.jpa.inherited.Blue;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inherited.InheritedTableManager;
 
public class InheritedModelJunitTest extends JUnitTestCase {
    private static Integer m_blueId;
    
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
        Blue blue = (Blue) createEntityManager().find(Blue.class, m_blueId);
        
        assertTrue("Error on reading back a Blue beer", blue != null);
    }
    
    public static void main(String[] args) {
        // Now run JUnit.
        junit.swingui.TestRunner.main(args);
    }
}
