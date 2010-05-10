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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.Company;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.Engineer;
import org.eclipse.persistence.testing.models.jpa.inheritance.ComputerPK;
import org.eclipse.persistence.testing.models.jpa.inheritance.Desktop;
import org.eclipse.persistence.testing.models.jpa.inheritance.Laptop;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.VehicleDirectory;

import junit.framework.Test;
import junit.framework.TestSuite;
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

        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritanceTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    // gf issue 1356 - persisting a polymorphic relationship throws a NPE.
    // The order of persist operations is important for this test.
    public void testPersistPolymorphicRelationship() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Person p = new Person();
        p.setName("Evil Knievel");
        
        Car c = new SportsCar();
        c.setDescription("Ferrari");
        ((SportsCar) c).setMaxSpeed(200);
        p.setCar(c);
        
        try {
            em.persist(c);
            em.persist(p);
            commitTransaction(em);
        
        } catch (Exception exception ) {
            fail("Error persisting polymorphic relationship: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    // test if we can associate with a subclass entity 
    // whose root entity has EmbeddedId in Joined inheritance strategy
    // Issue: GF#1153 && GF#1586 (desktop amendment)
    public void testAssociationWithEmbeddedIdSubclassEntityInJoinedStrategy() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

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
            
            commitTransaction(em);
        } catch(RuntimeException ex) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            
            throw ex;
        } finally {
            closeEntityManager(em);
        }
    }
    
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=241979
    public void testUpateTireInfo(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        TireInfo tireInfo = new TireInfo();
        tireInfo.setPressure(35);
        em.persist(tireInfo);
        commitTransaction(em);

        beginTransaction(em);
        TireInfo localTire = em.find(TireInfo.class, tireInfo.getId());
        assertTrue("TireInfo was not persisted with the proper pressure", localTire.getPressure().equals(35));
        localTire.setPressure(40);
        commitTransaction(em);
        em.clear();

        localTire = em.find(TireInfo.class, tireInfo.getId());
        assertTrue("TireInfo was not updated", localTire.getPressure().equals(40));
    }
    
    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=312253
    // Note: this test will potentially fail in a number of different ways
    // see the above bug for details of the non-derminism
    // also: This test only tests the above bug when weaving is disabled
    // also note: This test is testing for exceptions on the final flush, that
    // is why where are no asserts
    public void testMapKeyInheritance(){
        EntityManager em = createEntityManager();
        beginTransaction(em);

        VehicleDirectory directory = new VehicleDirectory();
        directory.setName("MyVehicles");
        em.persist(directory);
        
        Company company = new Company();
        company.setName("A Blue Company");
        em.persist(company);
        em.flush();
        
        Car car = new Car();
        car.setDescription("a Blue Car");
        car.setOwner(company);
        em.persist(car);
        
        directory.getVehicleDirectory().put(car.getOwner(), car);
        company.getVehicles().add(car);

        try{
            em.flush();
        } catch(RuntimeException e){
            fail("Exception was thrown while flushing a MapKey with inheritance. " + e.getMessage());
        }
        
        em.getTransaction().rollback();
    }
}
