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
 *     03/04/09 dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.jpa.privateowned;

import java.util.Iterator;

import javax.persistence.*;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.privateowned.*;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.server.ServerSession;

public class PrivateOwnedJUnitTestCase extends JUnitTestCase {
    
    public PrivateOwnedJUnitTestCase(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Private Owned Suite");
        
        // Setup:
        suite.addTest(new PrivateOwnedJUnitTestCase("testSetup"));
        
        // JPA testing:
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToManyPrivateOwnedRemoval"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToManyPrivateOwnedRemovalWithInheritance"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToManyPrivateOwnedRemovalWithCascade"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToManyPrivateOwnedRemovalWithCascadeFromPO"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToOnePrivateOwnedRemovalWithCascade"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToOnePrivateOwnedRemovalWithCascadeFromPO"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testEmbeddedWithCascadeFromPO"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToOnePrivateOwnedFromExistingObject"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToManyPrivateOwnedExistingObjectModification"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testPrivateOwnedOneToOneChangeFromExistingObject"));
        
        // Classic testing:
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToOnePrivateOwnedRemovalWithCascadeUsingClassic"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToManyPrivateOwnedRemovalUsingClassic"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testEmbeddedWithCascadeFromPOUsingClassic"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testOneToOnePrivateOwnedFromExistingObjectUsingClassic"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testPrivateOwnedCycleWithOneToMany"));
        suite.addTest(new PrivateOwnedJUnitTestCase("testDeleteAll"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new PrivateOwnedModelTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void testOneToManyPrivateOwnedExistingObjectModification() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis1 = new Chassis(1l);
        vehicle.setChassis(chassis1);
        
        chassis1.addWheel(new Wheel(1l));
        chassis1.addWheel(new Wheel(2l));

        em.persist(vehicle);

        commitTransaction(em);
        closeEntityManager(em);
        
        clearCache();
        
        em = createEntityManager();
        beginTransaction(em);
        
        Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
        assertNotNull("Vehicle should have been inserted", vehicleRead);
        Chassis chassis = vehicleRead.getChassis();
        
        for (Wheel wheel : chassis.getWheels()) {
            wheel.setSerialNumber(wheel.getSerialNumber() + 10);
        }
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        
        Vehicle vehicleReadAgain = em.find(Vehicle.class, vehicle.getId());
        assertNotNull("Vehicle should have been inserted", vehicleReadAgain);
        
        for (Wheel wheel : vehicleReadAgain.getChassis().getWheels()) {
            assertTrue("All wheels should have a S/N > 10 after modification", wheel.getSerialNumber() > 10);
        }
        
        // Cleanup
        em.remove(vehicleReadAgain);
        
        commitTransaction(em);
        
        closeEntityManager(em);
    }
    
    /**
     * Test a one to one relationship removal/modification, cascaded from an existing object
     */
    public void testOneToOnePrivateOwnedFromExistingObject() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis1 = new Chassis(1l);
        vehicle.setChassis(chassis1);

        em.persist(vehicle);
        commitTransaction(em); 
        
        beginTransaction(em);
        // query for existing object
        Vehicle vehicleToModify = em.find(Vehicle.class, vehicle.getId());

        vehicleToModify.setModel("GT-X2");
        em.persist(vehicleToModify);
        
        Chassis chassis2 = new Chassis(2l);
        vehicleToModify.setChassis(chassis2);
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();
        em = createEntityManager();
        beginTransaction(em);
        
        Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
        assertNotNull("Vehicle should have been inserted", vehicleRead);
        
        Chassis chassis1Read = em.find(Chassis.class, chassis1.getId());
        assertNull("Chassis1 should have been removed", chassis1Read);
        
        Chassis chassis2Read = em.find(Chassis.class, chassis2.getId());
        assertNotNull("Chassis2 should have been inserted", chassis2Read);
        
        assertSame("Vehicle should reference Chassis2", vehicleRead.getChassis(), chassis2Read);
        
        // Cleanup
        em.remove(vehicleRead);
        em.remove(chassis2Read);
        
        commitTransaction(em);
        
        closeEntityManager(em);
    }
    
    /**
     * Test removal of a private owned object with a referenced embedded object 
     */
    public void testEmbeddedWithCascadeFromPO() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis1 = new Chassis(1l);
        vehicle.setChassis(chassis1);
        
        Wheel wheel1 = new Wheel(1l);
        chassis1.addWheel(wheel1);
        
        Tire tire1 = new Tire();
        tire1.setManufacturer("ACME");
        tire1.setType("Radial");
        wheel1.setTire(tire1);
        
        em.persist(vehicle);
        
        Chassis chassis2 = new Chassis(2l);
        vehicle.setChassis(chassis2);
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();        
        em = createEntityManager(); 
        beginTransaction(em);
        
        Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
        assertNotNull("Vehicle should have been inserted", vehicleRead);

        Chassis chassis1Read = em.find(Chassis.class, chassis1.getId());
        assertNull("Chassis 1 should not have been inserted", chassis1Read);
        
        Chassis chassis2Read = em.find(Chassis.class, chassis2.getId());
        assertNotNull("Chassis 2 should have been inserted", chassis2Read);
        
        assertSame("Vehicle should reference Chassis2", vehicleRead.getChassis(), chassis2Read);

        assertEquals("Chassis 2 should not reference any wheels", 0, chassis2Read.getWheels().size());
        
        Wheel wheel1Read = em.find(Wheel.class, wheel1.getId());
        assertNull("Wheel 1 should not have been inserted", wheel1Read);
        
        // Cleanup
        em.remove(vehicleRead);
        em.remove(chassis2Read);
        
        commitTransaction(em);
        
        closeEntityManager(em);
    }
    
    /**
     *  Test the cascade removal of objects
     */
    public void testOneToManyPrivateOwnedRemovalWithCascadeFromPO() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis = new Chassis(1l);
        vehicle.setChassis(chassis);
        
        Wheel wheel1 = new Wheel(1l);
        chassis.addWheel(wheel1);
        
        WheelNut wheelNut1 = new WheelNut();
        WheelNut wheelNut2 = new WheelNut();
        WheelNut wheelNut3 = new WheelNut();
        WheelNut wheelNut4 = new WheelNut();
        
        wheel1.addWheelNut(wheelNut1);
        wheel1.addWheelNut(wheelNut2);
        wheel1.addWheelNut(wheelNut3);
        wheel1.addWheelNut(wheelNut4);

        em.persist(vehicle);
        
        WheelNut wheelNut5 = new WheelNut();
        WheelNut wheelNut6 = new WheelNut();
        
        wheel1.removeWheelNut(wheelNut3);
        wheel1.removeWheelNut(wheelNut4);
        wheel1.addWheelNut(wheelNut5);
        wheel1.addWheelNut(wheelNut6);

        Wheel wheel2 = new Wheel(2l);
    
        chassis.addWheel(wheel2);

        em.persist(vehicle);
        
        wheel2.addWheelNut(wheelNut3);
        wheel2.addWheelNut(wheelNut4);
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();        
        em = createEntityManager();         
        beginTransaction(em);
        
        WheelNut wheelNut3Read = em.find(WheelNut.class, wheelNut3.getId());
        assertNotNull("WheelNut3 should have been inserted", wheelNut3Read);
        
        WheelNut wheelNut4Read = em.find(WheelNut.class, wheelNut4.getId());
        assertNotNull("WheelNut4 should have been inserted", wheelNut4Read);
        
        Wheel wheel1Read = em.find(Wheel.class, wheel1.getId());
        assertNotNull("Wheel1 should have been inserted", wheel1Read);
        assertEquals("Wheel1 should reference 4 WheelNuts", 4, wheel1Read.getWheelNuts().size());
        assertFalse("Wheel1 should not reference wheelNut3", wheel1Read.getWheelNuts().contains(wheelNut3Read));
        assertFalse("Wheel1 should not reference wheelNut4", wheel1Read.getWheelNuts().contains(wheelNut4Read));
        
        Wheel wheel2Read = em.find(Wheel.class, wheel2.getId());
        assertNotNull("Wheel2 should have been inserted", wheel2Read);
        assertEquals("Wheel2 should reference 2 wheelnuts", 2, wheel2Read.getWheelNuts().size());
        assertTrue("Wheel2 should reference wheelNut3", wheel2Read.getWheelNuts().contains(wheelNut3Read));
        assertTrue("Wheel2 should reference wheelNut4", wheel2Read.getWheelNuts().contains(wheelNut4Read));
        
        // Cleanup
        em.remove(em.find(Vehicle.class, vehicle.getId()));
        // chassis removed automatically
        // wheels removed automatically
        
        for (Iterator<WheelNut> nuts = wheel1Read.getWheelNuts().iterator(); nuts.hasNext();) {
            WheelNut nut = nuts.next();
            em.remove(nut);
        }
        
        for (Iterator<WheelNut> nuts = wheel2Read.getWheelNuts().iterator(); nuts.hasNext();) {
            WheelNut nut = nuts.next();
            em.remove(nut);
        }
        
        commitTransaction(em);
        
        closeEntityManager(em);        
    }
    
    /**
     * Test the cascade removal of objects related to a 1:1 privately owned object
     */
    public void testOneToOnePrivateOwnedRemovalWithCascadeFromPO() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis = new Chassis(1l);
        vehicle.setChassis(chassis);
        
        Wheel wheel1 = new Wheel(1l);
        WheelRim wheelRim1 = new WheelRim();
        wheel1.setWheelRim(wheelRim1);
        chassis.addWheel(wheel1);
        
        Wheel wheel2 = new Wheel(2l);
        WheelRim wheelRim2 = new WheelRim();
        wheel2.setWheelRim(wheelRim2);
        chassis.addWheel(wheel2);
        
        Wheel wheel3 = new Wheel(3l);
        WheelRim wheelRim3 = new WheelRim();
        wheel3.setWheelRim(wheelRim3);
        chassis.addWheel(wheel3);
        
        Wheel wheel4 = new Wheel(4l);
        WheelRim wheelRim4 = new WheelRim();
        wheel4.setWheelRim(wheelRim4);
        chassis.addWheel(wheel4);

        em.persist(vehicle);
        
        // Vehicle and WheelRim (x4) should be persisted because they are not privately owned
        // Wheel and Chassis should not be persisted because they are privately owned 
        vehicle.setChassis(null);
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();        
        em = createEntityManager(); 
        beginTransaction(em);
        
        Object wheel1Read = em.find(Wheel.class, wheel1.getId());
        Object wheel2Read = em.find(Wheel.class, wheel2.getId());
        Object wheel3Read = em.find(Wheel.class, wheel3.getId());
        Object wheel4Read = em.find(Wheel.class, wheel4.getId());
        
        assertNull("Wheel1 should not be inserted", wheel1Read);
        assertNull("Wheel2 should not be inserted", wheel2Read);
        assertNull("Wheel3 should not be inserted", wheel3Read);
        assertNull("Wheel4 should not be inserted", wheel4Read);
        
        Object wheelRim1Read = em.find(WheelRim.class, wheelRim1.getId());
        Object wheelRim2Read = em.find(WheelRim.class, wheelRim2.getId());
        Object wheelRim3Read = em.find(WheelRim.class, wheelRim3.getId());
        Object wheelRim4Read = em.find(WheelRim.class, wheelRim4.getId());
        
        assertNotNull("WheelRim1 should be inserted", wheelRim1Read);
        assertNotNull("WheelRim2 should be inserted", wheelRim2Read);
        assertNotNull("WheelRim3 should be inserted", wheelRim3Read);
        assertNotNull("WheelRim4 should be inserted", wheelRim4Read);
        
        Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
        assertNotNull("Vehicle should be inserted", vehicleRead);
        
        Chassis chassisRead = em.find(Chassis.class, chassis.getId());
        assertNull("Chassis should not be inserted", chassisRead);
        assertNull("Vehicle should not reference Chassis", vehicleRead.getChassis());

        // Cleanup
        em.remove(vehicleRead);
        em.remove(wheelRim1Read);
        em.remove(wheelRim2Read);
        em.remove(wheelRim3Read);
        em.remove(wheelRim4Read);
        
        commitTransaction(em);
        
        closeEntityManager(em);
    }
    
    /**
     * Test cascading with removing 1:M private owned objects
     */
    public void testOneToManyPrivateOwnedRemovalWithCascade() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vehicle vehicle = new Vehicle("GT-X");
        Engine engine = new Engine(123456789l);
        SparkPlug plug1 = new SparkPlug(1);
        SparkPlug plug2 = new SparkPlug(2);
        SparkPlug plug3 = new SparkPlug(3);
        SparkPlug plug4 = new SparkPlug(4);
        SparkPlug plug5 = new SparkPlug(5);
        SparkPlug plug6 = new SparkPlug(6);
    
        vehicle.setEngine(engine);
        
        engine.addSparkPlug(plug1);
        engine.addSparkPlug(plug2);
        engine.addSparkPlug(plug3);
        engine.addSparkPlug(plug4);
        engine.addSparkPlug(plug5);
        engine.addSparkPlug(plug6);
        
        em.persist(vehicle);
        
        engine.removeSparkPlug(plug3);
        engine.removeSparkPlug(plug5);
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();        
        em = createEntityManager();
        beginTransaction(em);
        
        SparkPlug sparkPlug3Read = em.find(SparkPlug.class, plug3.getId());
        assertNull("SparkPlug3 should not be inserted", sparkPlug3Read);
        
        SparkPlug sparkPlug5Read = em.find(SparkPlug.class, plug5.getId());
        assertNull("SparkPlug5 should not be inserted", sparkPlug5Read);

        Engine engineRead = em.find(Engine.class, engine.getId());
        assertNotNull("Engine should be inserted", engineRead);
        assertEquals("Engine should contain 4 spark plugs", 4, engineRead.getSparkPlugs().size());
        assertFalse("Engine should not contain SparkPlug3", engine.getSparkPlugs().contains(sparkPlug3Read));
        assertFalse("Engine should not contain SparkPlug5", engine.getSparkPlugs().contains(sparkPlug5Read));

        // do some additional assertions
        SparkPlug sparkPlug1Read = em.find(SparkPlug.class, plug1.getId());
        assertNotNull(sparkPlug1Read);
        SparkPlug sparkPlug2Read = em.find(SparkPlug.class, plug2.getId());
        assertNotNull(sparkPlug2Read);
        SparkPlug sparkPlug4Read = em.find(SparkPlug.class, plug4.getId());
        assertNotNull(sparkPlug4Read);
        SparkPlug sparkPlug6Read = em.find(SparkPlug.class, plug6.getId());
        assertNotNull(sparkPlug6Read);
        
        assertEquals(4, engineRead.getSparkPlugs().size());
        assertTrue("Engine should contain SparkPlug1", engineRead.getSparkPlugs().contains(sparkPlug1Read));
        assertTrue("Engine should contain SparkPlug2", engineRead.getSparkPlugs().contains(sparkPlug2Read));
        assertTrue("Engine should contain SparkPlug4", engineRead.getSparkPlugs().contains(sparkPlug4Read));
        assertTrue("Engine should contain SparkPlug6", engineRead.getSparkPlugs().contains(sparkPlug6Read));
        
        // Cleanup
        em.remove(em.find(Vehicle.class, vehicle.getId()));
        em.remove(engineRead);
        em.remove(sparkPlug1Read);
        em.remove(sparkPlug2Read);
        em.remove(sparkPlug4Read);
        em.remove(sparkPlug6Read);
        
        commitTransaction(em);
        
        closeEntityManager(em);
    }
    
    /**
     * 
     */
    public void testOneToOnePrivateOwnedRemovalWithCascade() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vehicle vehicle = new Vehicle("GT-X");
        
        Chassis chassis1 = new Chassis(1l);
        vehicle.setChassis(chassis1);
        
        em.persist(vehicle);
        
        Chassis chassis2 = new Chassis(2l);
        vehicle.setChassis(chassis2);        
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();        
        em = createEntityManager();
        beginTransaction(em);
        
        Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
        assertNotNull("Vehicle should be inserted", vehicleRead);
        
        Chassis chassis1Read = em.find(Chassis.class, chassis1.getId());
        assertNull("Chassis1 should not be inserted", chassis1Read);
        
        Chassis chassis2Read = em.find(Chassis.class, chassis2.getId());
        assertNotNull("Chassis2 should be inserted", chassis2Read);

        assertSame("Vehicle should reference Chassis2", chassis2Read, vehicleRead.getChassis());
        
        // Cleanup
        em.remove(vehicleRead);
        em.remove(chassis2Read);
        
        commitTransaction(em);
                
        closeEntityManager(em);
    }
    
    /**
     * The simplest test for private owned object removal (1:M)
     */
    public void testOneToManyPrivateOwnedRemoval() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis = new Chassis(1);
        vehicle.setChassis(chassis);
        
        Wheel wheel1 = new Wheel(1l);
        Wheel wheel2 = new Wheel(2l);
        Wheel wheel3 = new Wheel(3l);
        Wheel wheel4 = new Wheel(4l);
        Wheel wheel5 = new Wheel(5l);
        
        chassis.addWheel(wheel1);
        chassis.addWheel(wheel2);
        chassis.addWheel(wheel3);
        chassis.addWheel(wheel4);
        chassis.addWheel(wheel5);
        
        em.persist(vehicle);
        
        chassis.removeWheel(wheel5); // never a good idea to have a 5th wheel
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();        
        em = createEntityManager();
        beginTransaction(em);
        
        Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
        assertNotNull("Vehicle should be inserted", vehicleRead);
        assertNotNull(vehicleRead.getChassis());
        
        Chassis chassisRead = em.find(Chassis.class, chassis.getId());
        assertNotNull("Chassis should be inserted", chassisRead);
        assertSame("Vehicle should reference Chassis", chassisRead, vehicleRead.getChassis());
        assertNotNull(chassisRead.getWheels());
        assertEquals(4, chassisRead.getWheels().size());
        
        Wheel wheel1Read = em.find(Wheel.class, wheel1.getId());
        Wheel wheel2Read = em.find(Wheel.class, wheel2.getId());
        Wheel wheel3Read = em.find(Wheel.class, wheel3.getId());
        Wheel wheel4Read = em.find(Wheel.class, wheel4.getId());
        
        assertNotNull(wheel1Read);
        assertNotNull(wheel2Read);
        assertNotNull(wheel3Read);
        assertNotNull(wheel4Read);
        
        assertTrue(chassisRead.getWheels().contains(wheel1Read));
        assertTrue(chassisRead.getWheels().contains(wheel2Read));
        assertTrue(chassisRead.getWheels().contains(wheel3Read));
        assertTrue(chassisRead.getWheels().contains(wheel4Read));

        Wheel wheel5Read = em.find(Wheel.class, wheel5.getId());
        assertNull("Wheel5 should not be inserted", wheel5Read);
        assertFalse("Chassis should not reference Wheel5", chassisRead.getWheels().contains(wheel5Read));
        
        // Cleanup
        em.remove(vehicleRead);
        em.remove(chassisRead);
        em.remove(wheel1Read);
        em.remove(wheel2Read);
        em.remove(wheel3Read);
        em.remove(wheel4Read);
        
        commitTransaction(em);
        
        closeEntityManager(em);
    }
    
    /**
     * The simplest test for private owned object removal (1:M)
     */
    public void testOneToManyPrivateOwnedRemovalWithInheritance() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SpecificVehicle vehicle = new SpecificVehicle("GT-X");
        Chassis chassis = new Chassis(1);
        vehicle.setChassis(chassis);
        
        Wheel wheel1 = new Wheel(1l);
        Wheel wheel2 = new Wheel(2l);
        Wheel wheel3 = new Wheel(3l);
        Wheel wheel4 = new Wheel(4l);
        Wheel wheel5 = new Wheel(5l);
        
        chassis.addWheel(wheel1);
        chassis.addWheel(wheel2);
        chassis.addWheel(wheel3);
        chassis.addWheel(wheel4);
        chassis.addWheel(wheel5);
        
        em.persist(vehicle);
        
        chassis.removeWheel(wheel5); // never a good idea to have a 5th wheel
        
        commitTransaction(em);
        closeEntityManager(em);
        
        // Verification
        
        clearCache();        
        em = createEntityManager();
        beginTransaction(em);
        
        SpecificVehicle vehicleRead = em.find(SpecificVehicle.class, vehicle.getId());
        assertNotNull("Vehicle should be inserted", vehicleRead);
        assertNotNull(vehicleRead.getChassis());
        
        Chassis chassisRead = em.find(Chassis.class, chassis.getId());
        assertNotNull("Chassis should be inserted", chassisRead);
        assertSame("Vehicle should reference Chassis", chassisRead, vehicleRead.getChassis());
        assertNotNull(chassisRead.getWheels());
        assertEquals(4, chassisRead.getWheels().size());
        
        Wheel wheel1Read = em.find(Wheel.class, wheel1.getId());
        Wheel wheel2Read = em.find(Wheel.class, wheel2.getId());
        Wheel wheel3Read = em.find(Wheel.class, wheel3.getId());
        Wheel wheel4Read = em.find(Wheel.class, wheel4.getId());
        
        assertNotNull(wheel1Read);
        assertNotNull(wheel2Read);
        assertNotNull(wheel3Read);
        assertNotNull(wheel4Read);
        
        assertTrue(chassisRead.getWheels().contains(wheel1Read));
        assertTrue(chassisRead.getWheels().contains(wheel2Read));
        assertTrue(chassisRead.getWheels().contains(wheel3Read));
        assertTrue(chassisRead.getWheels().contains(wheel4Read));

        Wheel wheel5Read = em.find(Wheel.class, wheel5.getId());
        assertNull("Wheel5 should not be inserted", wheel5Read);
        assertFalse("Chassis should not reference Wheel5", chassisRead.getWheels().contains(wheel5Read));
        
        // Cleanup
        em.remove(vehicleRead);
        em.remove(chassisRead);
        em.remove(wheel1Read);
        em.remove(wheel2Read);
        em.remove(wheel3Read);
        em.remove(wheel4Read);
        
        commitTransaction(em);
        
        closeEntityManager(em);
    }

    public void testOneToOnePrivateOwnedRemovalWithCascadeUsingClassic() {
        ServerSession serverSession = getServerSession();
        UnitOfWork uow = serverSession.acquireUnitOfWork();
        
        Vehicle vehicle = new Vehicle("GT-X Mk2");
        
        Chassis chassis1 = new Chassis(11l);
        vehicle.setChassis(chassis1);
        
        Vehicle vehicleClone = (Vehicle) uow.registerObject(vehicle);
        
        Chassis chassis2 = new Chassis(22l);
        vehicleClone.setChassis(chassis2);    
        
        uow.commit();
        
        // Verification
        
        serverSession.getIdentityMapAccessor().initializeAllIdentityMaps();    
        
        uow = serverSession.acquireUnitOfWork();
        
        Vehicle vehicleRead = (Vehicle) uow.readObject(
                Vehicle.class, 
                new ExpressionBuilder().get("id").equal(vehicle.getId()));
        assertNotNull("Vehicle should be inserted", vehicleRead);
        
        Chassis chassis1Read = (Chassis) uow.readObject(
                Chassis.class, 
                new ExpressionBuilder().get("id").equal(chassis1.getId()));
        assertNull("Chassis1 should not be inserted", chassis1Read);
        
        Chassis chassis2Read = (Chassis) uow.readObject(
                Chassis.class, 
                new ExpressionBuilder().get("id").equal(chassis2.getId()));
        assertNotNull("Chassis2 should be inserted", chassis2Read);

        assertSame("Vehicle should reference Chassis2", chassis2Read, vehicleRead.getChassis());
        
        // Cleanup - classic
        
        uow.deleteObject(vehicleRead);
        uow.deleteObject(chassis2Read);
        
        uow.commit();
    }
    
    public void testOneToManyPrivateOwnedRemovalUsingClassic() {
        ServerSession serverSession = getServerSession();
        UnitOfWork uow = serverSession.acquireUnitOfWork();

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis = new Chassis(1);
        vehicle.setChassis(chassis);
        
        Wheel wheel1 = new Wheel(1l);
        Wheel wheel2 = new Wheel(2l);
        Wheel wheel3 = new Wheel(3l);
        Wheel wheel4 = new Wheel(4l);
        Wheel wheel5 = new Wheel(5l);
        
        chassis.addWheel(wheel1);
        chassis.addWheel(wheel2);
        chassis.addWheel(wheel3);
        chassis.addWheel(wheel4);
        chassis.addWheel(wheel5);
        
        Vehicle vehicleClone = (Vehicle) uow.registerObject(vehicle);
        
        Wheel theFifthWheel = null;
        Iterator<Wheel> wheelsIterator = vehicleClone.getChassis().getWheels().iterator();
        while (wheelsIterator.hasNext()) {
            Wheel wheel = wheelsIterator.next();
            if (wheel.getSerialNumber() == wheel5.getSerialNumber()) {
                theFifthWheel = wheel;
            }
        }
        assertNotNull(theFifthWheel);
        
        vehicleClone.getChassis().removeWheel(theFifthWheel);
        
        assertEquals(4, vehicleClone.getChassis().getWheels().size());
        
        uow.commit();
        
        // Verification
        
        serverSession.getIdentityMapAccessor().initializeAllIdentityMaps();        
        uow = serverSession.acquireUnitOfWork();

        Vehicle vehicleRead = (Vehicle) uow.readObject(
                Vehicle.class, 
                new ExpressionBuilder().get("id").equal(vehicle.getId())); 
        assertNotNull("Vehicle should be inserted", vehicleRead);
        assertNotNull(vehicleRead.getChassis());
        
        Chassis chassisRead = (Chassis) uow.readObject(
                Chassis.class, 
                new ExpressionBuilder().get("id").equal(chassis.getId()));
        assertNotNull("Chassis should be inserted", chassisRead);
        assertSame("Vehicle should reference Chassis", chassisRead, vehicleRead.getChassis());
        assertNotNull(chassisRead.getWheels());
        
        Wheel wheel5Read = (Wheel) uow.readObject(
                Wheel.class, 
                new ExpressionBuilder().get("id").equal(wheel5.getId()));
        assertNull("Wheel5 should not be inserted", wheel5Read);
        assertFalse("Chassis should not reference Wheel5", chassisRead.getWheels().contains(wheel5Read));        
        assertEquals(4, chassisRead.getWheels().size());
        
        Wheel wheel1Read = (Wheel) uow.readObject(
                Wheel.class, 
                new ExpressionBuilder().get("id").equal(wheel1.getId()));
        Wheel wheel2Read = (Wheel) uow.readObject(
                Wheel.class, 
                new ExpressionBuilder().get("id").equal(wheel2.getId()));
        Wheel wheel3Read = (Wheel) uow.readObject(
                Wheel.class, 
                new ExpressionBuilder().get("id").equal(wheel3.getId()));
        Wheel wheel4Read = (Wheel) uow.readObject(
                Wheel.class, 
                new ExpressionBuilder().get("id").equal(wheel4.getId()));
        
        assertNotNull(wheel1Read);
        assertNotNull(wheel2Read);
        assertNotNull(wheel3Read);
        assertNotNull(wheel4Read);
        
        assertTrue(chassisRead.getWheels().contains(wheel1Read));
        assertTrue(chassisRead.getWheels().contains(wheel2Read));
        assertTrue(chassisRead.getWheels().contains(wheel3Read));
        assertTrue(chassisRead.getWheels().contains(wheel4Read));
    
        // Cleanup - classic
        
        uow.deleteObject(vehicleRead);
        uow.deleteObject(chassisRead);
        uow.deleteAllObjects(chassisRead.getWheels());
        
        uow.commit();
    }
    
    public void testEmbeddedWithCascadeFromPOUsingClassic() {
        ServerSession serverSession = getServerSession();
        UnitOfWork uow = serverSession.acquireUnitOfWork();

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis1 = new Chassis(1l);
        vehicle.setChassis(chassis1);
        
        Wheel wheel1 = new Wheel(1l);
        chassis1.addWheel(wheel1);
        
        Tire tire1 = new Tire();
        tire1.setManufacturer("ACME");
        tire1.setType("Radial");
        wheel1.setTire(tire1);
        
        Vehicle vehicleClone = (Vehicle) uow.registerObject(vehicle);
        
        Chassis chassis2 = new Chassis(2l);
        vehicleClone.setChassis(chassis2);
        
        uow.commit();
        
        // Verification
        
        serverSession.getIdentityMapAccessor().initializeAllIdentityMaps();    
        uow = serverSession.acquireUnitOfWork(); 
        
        Vehicle vehicleRead = (Vehicle) uow.readObject(
                Vehicle.class, 
                new ExpressionBuilder().get("id").equal(vehicle.getId()));
        assertNotNull("Vehicle should have been inserted", vehicleRead);

        Chassis chassis1Read = (Chassis) uow.readObject(
                Chassis.class, 
                new ExpressionBuilder().get("id").equal(chassis1.getId())); 
        assertNull("Chassis 1 should not have been inserted", chassis1Read);
        
        Chassis chassis2Read = (Chassis) uow.readObject(
                Chassis.class, 
                new ExpressionBuilder().get("id").equal(chassis2.getId()));
        assertNotNull("Chassis 2 should have been inserted", chassis2Read);
        
        assertSame("Vehicle should reference Chassis2", vehicleRead.getChassis(), chassis2Read);

        assertEquals("Chassis 2 should not reference any wheels", 0, chassis2Read.getWheels().size());
        
        Wheel wheel1Read = (Wheel) uow.readObject(
                Wheel.class, 
                new ExpressionBuilder().get("id").equal(wheel1.getId()));
        assertNull("Wheel 1 should not have been inserted", wheel1Read);
        
        // Cleanup - classic
        
        uow.deleteObject(vehicleRead);
        uow.deleteObject(chassis2Read);
        
        uow.commit();
    }

    public void testOneToOnePrivateOwnedFromExistingObjectUsingClassic() {
        ServerSession serverSession = getServerSession();
        UnitOfWork uow = serverSession.acquireUnitOfWork();

        Vehicle vehicle = new Vehicle("GT-X");
        Chassis chassis1 = new Chassis(1l);
        vehicle.setChassis(chassis1);

        uow.registerObject(vehicle);
        uow.commit(); 
        
        uow = serverSession.acquireUnitOfWork();
        // query for existing object
        Vehicle vehicleToModify = (Vehicle) uow.readObject(
                Vehicle.class,
                new ExpressionBuilder().get("id").equal(vehicle.getId()));

        vehicleToModify.setModel("GT-X2");
        
        Chassis chassis2 = new Chassis(2l);
        vehicleToModify.setChassis(chassis2);
        
        uow.commit();
        serverSession.getIdentityMapAccessor().initializeAllIdentityMaps();
        
        // Verification
        
        clearCache();
        uow = serverSession.acquireUnitOfWork();
        
        Vehicle vehicleRead = (Vehicle) uow.readObject(
                Vehicle.class, 
                new ExpressionBuilder().get("id").equal(vehicle.getId()));
        assertNotNull("Vehicle should have been inserted", vehicleRead);
        
        Chassis chassis1Read = (Chassis) uow.readObject(
                Chassis.class, 
                new ExpressionBuilder().get("id").equal(chassis1.getId()));
        assertNull("Chassis1 should have been removed", chassis1Read);
        
        Chassis chassis2Read = (Chassis) uow.readObject(
                Chassis.class, 
                new ExpressionBuilder().get("id").equal(chassis2.getId()));
        assertNotNull("Chassis2 should have been inserted", chassis2Read);
        
        assertSame("Vehicle should reference Chassis2", vehicleRead.getChassis(), chassis2Read);
        
        // Cleanup - classic
        
        uow.deleteObject(vehicleRead);
        uow.deleteObject(chassis2Read);
        
        uow.commit();
    }
    

    /**
     * use case: 
     * A ---> B : the relationship between A and B is privately owned and cascade persist 
     * Create an instance of A and B. Persist A 
     * Load A and update an attribute of B. Persist A 
     * => the attribute is not changed in DB.
     */
    public void testPrivateOwnedOneToOneChangeFromExistingObject() {
        // Step 1 - Create an object and a related object (p-o)
        EntityManager em = createEntityManager();
        beginTransaction(em);
    
        Vehicle vehicle = new Vehicle("GT-X7");
        Chassis chassis = new Chassis(1234567l);
        vehicle.setChassis(chassis);
    
        em.persist(vehicle);
            
        commitTransaction(em);
        closeEntityManager(em);
            
        // Step 2 - change the related object's attribute
        em = createEntityManager();
        beginTransaction(em);
            
        Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
           
        long newSerialNumber = 7654321l;
        vehicleRead.getChassis().setSerialNumber(newSerialNumber);
            
        em.persist(vehicleRead);
            
        commitTransaction(em);
        closeEntityManager(em);
            
        // Step 3 - verify results
        clearCache();
        em = createEntityManager();
            
        Vehicle vehicleReadAgain = em.find(Vehicle.class, vehicle.getId());
        try {
            assertNotNull("Vehicle should have been inserted", vehicleReadAgain);
                
            long serialNumberFromDatabase = vehicleReadAgain.getChassis().getSerialNumber();
            assertEquals("Chassis serial number should have been changed on the DB", newSerialNumber, serialNumberFromDatabase); // fails
        } finally {            
            // Step 4 - clean up database
            beginTransaction(em);
            vehicleReadAgain = em.find(Vehicle.class, vehicle.getId());
            em.remove(vehicleReadAgain.getChassis());
            em.remove(vehicleReadAgain);
                
            commitTransaction(em);
                
            closeEntityManager(em);
        }
    }
    
    // Bug 350599 
    public void testPrivateOwnedCycleWithOneToMany(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        Vehicle vehicle = new Vehicle();
        Chassis chassis = new Chassis();
        Mount mount = new Mount();
        
        vehicle.setChassis(chassis);
        chassis.addMount(mount);
        mount.setVehicle(vehicle);
        
        em.persist(vehicle);
        em.flush();
        clearCache();
        em.clear();
        MountPK pk = new MountPK(mount.getId(), chassis.getId());
        vehicle = em.find(Vehicle.class, vehicle.getId());

        em.remove(vehicle);

        em.flush();
        
        clearCache();
        em.clear();
        
        vehicle = em.find(Vehicle.class, vehicle.getId());
        assertNull("vehicle was not deleted.", vehicle);
        
        chassis = em.find(Chassis.class, chassis.getId());
        assertNull("chassis was not deleted.", chassis);
        
        mount = em.find(Mount.class, pk);
        assertNull("mount was not deleted.", mount);
        
        rollbackTransaction(em);
    }
    
    // Bug 350599  
    public void testDeleteAll(){
        EntityManager em = createEntityManager();
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        beginTransaction(em);
        Chassis chassis = new Chassis();

        Wheel wheel = new Wheel();
        chassis.addWheel(wheel);
        wheel = new Wheel();
        chassis.addWheel(wheel);
        em.persist(chassis);
        em.flush();
        em.clear();
        clearCache();

        chassis = em.find(Chassis.class, chassis.getId());
        em.remove(chassis);
        
        counter.getQueries().clear();
        counter.getSqlStatements().clear();
        em.flush();
        try{
            // The point of this assert to to ensure that only one SQL statement is executed to remove the two wheels.
            // if at some point in the future, this assert fails as a result of a change in the mappings of chassis
            // this assert can be adjusted as long as the number of delete statements for Wheel is unaffected
            assertTrue("An incorrect number of SQL statements were issued.", counter.getSqlStatements().size() == 2);
        } finally{
            counter.remove();
            rollbackTransaction(em);
        }
        
    }
}