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
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.Engineer;
import org.eclipse.persistence.testing.models.jpa.inheritance.ComputerPK;
import org.eclipse.persistence.testing.models.jpa.inheritance.Desktop;
import org.eclipse.persistence.testing.models.jpa.inheritance.Laptop;
import org.eclipse.persistence.sessions.DatabaseSession;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.extensions.TestSetup;

import javax.persistence.EntityManager;

public class EntityManagerJUnitTestCase extends JUnitTestCase {

    public EntityManagerJUnitTestCase() {
        super();
    }

    public EntityManagerJUnitTestCase(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(EntityManagerJUnitTestCase.class);

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

    // gf issue 1356 - persisting a polymorphic relationship throws a NPE.
    // The order of persist operations is important for this test.
    public void testPersistPolymorphicRelationship() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();
        Person p = new Person();
        p.setName("Evil Knievel");
        
        Car c = new SportsCar();
        c.setDescription("Ferrari");
        ((SportsCar) c).setMaxSpeed(200);
        p.setCar(c);
        
        try {
            em.persist(c);
            em.persist(p);
            em.getTransaction().commit();
        
        } catch (Exception exception ) {
            fail("Error persisting polymorphic relationship: " + exception.getMessage());
        } finally {
            em.close();
        }
    }

    // test if we can associate with a subclass entity 
    // whose root entity has EmbeddedId in Joined inheritance strategy
    // Issue: GF#1153 && GF#1586 (desktop amendment)
    public void testAssociationWithEmbeddedIdSubclassEntityInJoinedStrategy() {
        EntityManager em = createEntityManager();
        em.getTransaction().begin();

        try {
            Engineer engineer = new Engineer();
            em.persist(engineer);
        
            ComputerPK laptopPK = new ComputerPK("Dell", 10001);    
            Laptop laptop = em.find(Laptop.class, laptopPK);
            if (laptop == null){
                laptop = new Laptop(laptopPK);
                em.persist(laptop);
            }
        
            ComputerPK desktopPK = new ComputerPK("IBM", 10002);    
            Desktop desktop = em.find(Desktop.class, desktopPK);
            if (desktop == null){
                desktop = new Desktop(desktopPK);
                em.persist(desktop);
            }
            
            // associate many-to-many relationships
            engineer.getLaptops().add(laptop);
            engineer.getDesktops().add(desktop);
            
            em.getTransaction().commit();
        } catch(RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            
            throw ex;
        } finally {
            em.close();
        }
    }
}
