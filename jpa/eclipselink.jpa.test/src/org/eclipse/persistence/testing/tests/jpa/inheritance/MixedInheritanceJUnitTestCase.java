/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;

import org.eclipse.persistence.testing.models.jpa.inheritance.MudTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.RockTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireRating;

import org.eclipse.persistence.testing.models.jpa.inheritance.listeners.TireInfoListener;

import org.eclipse.persistence.sessions.DatabaseSession;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;

import javax.persistence.EntityManager;

public class MixedInheritanceJUnitTestCase extends JUnitTestCase {
    private static int mudTireId;
    private static int rockTireId;
    
    public MixedInheritanceJUnitTestCase() {
        super();
    }

    public MixedInheritanceJUnitTestCase(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.main(args);
    }
    
    public void setUp() {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("MixedInheritanceJUnitTestCase");
        
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewMudTire"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewRockTire"));
        
        suite.addTest(new MixedInheritanceJUnitTestCase("testReadNewMudTire"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testReadNewRockTire"));

        return new TestSetup(suite) {

            protected void setUp(){
                DatabaseSession session = JUnitTestCase.getServerSession();
                new InheritanceTableCreator().replaceTables(session);
            }

            protected void tearDown() {
                clearCache();
            }
        };
    }

    public void testCreateNewMudTire() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        
        MudTireInfo mudTire = new MudTireInfo();
        mudTire.setName("Goodyear Mud Tracks");
        mudTire.setCode("MT-674-A4");
        mudTire.setPressure(new Integer(100));
        mudTire.setTreadDepth(3);
        
        TireRating tireRating = new TireRating();
        tireRating.setRating("Excellent");
        tireRating.setComments("Tire outperformed all others in adverse conditions");
        
        mudTire.setTireRating(tireRating);
        
        try {
            int prePersistCountBefore = TireInfoListener.PRE_PERSIST_COUNT;
            em.persist(mudTire);
            mudTireId = mudTire.getId();
            em.getTransaction().commit();
            int prePersistCountAfter = TireInfoListener.PRE_PERSIST_COUNT;
            
            int perPersistCountTotal = prePersistCountAfter - prePersistCountBefore;
            assertTrue("The pre persist method was called more than once (" + perPersistCountTotal + ")", perPersistCountTotal == 1);
        } catch (Exception exception ) {
            fail("Error persisting mud tire: " + exception.getMessage());
        } finally {
            em.close();
        }
    }
    
    public void testCreateNewRockTire() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        
        RockTireInfo rockTire = new RockTireInfo();
        rockTire.setName("Goodyear Mud Tracks");
        rockTire.setCode("AE-678");
        rockTire.setPressure(new Integer(100));
        rockTire.setGrip(RockTireInfo.Grip.SUPER);
        
        try {
            em.persist(rockTire);
            rockTireId = rockTire.getId();
            em.getTransaction().commit();
        } catch (Exception exception ) {
            fail("Error persisting rock tire: " + exception.getMessage());
        } finally {
            em.close();
        }
    }
    
    public void testReadNewMudTire() {
        assertNotNull("The new mud tire info could not be read back.", createEntityManager().find(MudTireInfo.class, mudTireId));
    }
    
    public void testReadNewRockTire() {
        assertNotNull("The new rock tire info could not be read back.", createEntityManager().find(RockTireInfo.class, rockTireId));
    }
}
