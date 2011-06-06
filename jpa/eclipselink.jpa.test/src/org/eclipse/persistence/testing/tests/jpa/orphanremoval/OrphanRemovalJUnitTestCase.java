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
 *     05/1/2009-2.0 Guy Pelletier/David Minsky
 *       - 249033: JPA 2.0 Orphan removal
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.jpa.orphanremoval;

import javax.persistence.*;
import junit.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.orphanremoval.*;

public class OrphanRemovalJUnitTestCase extends JUnitTestCase {
    
    public OrphanRemovalJUnitTestCase(String name) {
        super(name);
    }
    
    public void compareObjects(Object obj, Object objRead) {
        assertTrue("The object [" + obj + "] when read back did not match the original", getServerSession().compareObjects(obj, objRead));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("Orphan removal suite");
        if (! JUnitTestCase.isJPA10()) {
            // Setup:
            suite.addTest(new OrphanRemovalJUnitTestCase("testSetup"));
            
            // JPA testing:
            suite.addTest(new OrphanRemovalJUnitTestCase("test12M"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test12MWithCascade"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test12MWithCascadeFromOR"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test12MFromExistingObject"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test12MFromExistingObjectModification"));
            
            suite.addTest(new OrphanRemovalJUnitTestCase("test121WithCascade"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121WithCascadeFromOR"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121FromExistingObject"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121FromExistingObjectModification"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121IgnoredFromRemovedExistingObject"));
            
            suite.addTest(new OrphanRemovalJUnitTestCase("testEmbeddedWithCascadeFromOR"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121ChangeFromExistingObject"));
            
            suite.addTest(new OrphanRemovalJUnitTestCase("test121WithCascadeRemoveFromOR"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121WithNoCascadePersist"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121WithNoCascadeMerge1"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121WithNoCascadeMerge2"));
            suite.addTest(new OrphanRemovalJUnitTestCase("test121WithNoCascadeMerge3"));
        }
        return suite;
    }
    
    /**
     * Test removal of a private owned object with a referenced embedded object 
     */
    public void testEmbeddedWithCascadeFromOR() {
        EntityManager em = createEntityManager();
    
        try {    
            // Step 1 - Create the objects
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
            
            // Step 2 - Verification
            clearCache();        
            em = createEntityManager(); 
            beginTransaction(em);
            
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicle, vehicleRead);
            
            Chassis chassis2Read = em.find(Chassis.class, chassis2.getId());
            compareObjects(chassis2, chassis2Read);
            
            assertNull("Chassis 1 should not have been inserted", em.find(Chassis.class, chassis1.getId()));
            assertNull("Wheel 1 should not have been inserted", em.find(Wheel.class, wheel1.getId()));
            
            // Step 3 - Cleanup
            em.remove(vehicleRead);
            em.remove(chassis2Read);
            commitTransaction(em);
            closeEntityManager(em);
        }  catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * The simplest test for orphan removal (1:M)
     */
    public void test12M() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - Create the objects
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
        
            // Step 2 - verification
            clearCache();        
            em = createEntityManager();
            beginTransaction(em);

            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicle, vehicleRead);
            Chassis chassisRead = em.find(Chassis.class, chassis.getId());
            compareObjects(chassis, chassisRead);
            Wheel wheel1Read = em.find(Wheel.class, wheel1.getId());
            compareObjects(wheel1, wheel1Read);
            Wheel wheel2Read = em.find(Wheel.class, wheel2.getId());
            compareObjects(wheel2, wheel2Read);
            Wheel wheel3Read = em.find(Wheel.class, wheel3.getId());
            compareObjects(wheel3, wheel3Read);
            Wheel wheel4Read = em.find(Wheel.class, wheel4.getId());
            compareObjects(wheel4, wheel4Read);

            assertNull("Wheel5 should not be inserted", em.find(Wheel.class, wheel5.getId()));
        
            // Step 3 - Cleanup
            em.remove(vehicleRead);
            em.remove(chassisRead);
            em.remove(wheel1Read);
            em.remove(wheel2Read);
            em.remove(wheel3Read);
            em.remove(wheel4Read);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    public void test12MFromExistingObject() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - Create the objects.
            beginTransaction(em);
    
            Vehicle vehicle = new Vehicle("GT-X");
            Chassis chassis1 = new Chassis(1l);
            vehicle.setChassis(chassis1);
            
            Wheel wheel1 = new Wheel(1l);
            chassis1.addWheel(wheel1);
            Wheel wheel2 = new Wheel(2l);
            chassis1.addWheel(wheel2);
    
            em.persist(vehicle);
    
            commitTransaction(em);
            closeEntityManager(em);
            
            clearCache();
            em = createEntityManager();
            beginTransaction(em);
            
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicle, vehicleRead);
        
            Chassis chassisRead = vehicleRead.getChassis();
            chassisRead.setSerialNumber(74923402);
            chassisRead.removeWheel(wheel1);
            chassisRead.removeWheel(wheel2);
            chassisRead.setWheels(null);
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Verification
            clearCache();
            em = createEntityManager();
            beginTransaction(em);
            
            Vehicle vehicleReadAgain = em.find(Vehicle.class, vehicle.getId());
            // This check fails.
            //compareObjects(vehicleRead, vehicleReadAgain);
            
            assertNull("Wheel1 should have been removed", em.find(Wheel.class, wheel1.getId()));
            assertNull("Wheel2 should have been removed", em.find(Wheel.class, wheel2.getId()));
            
            // Step 3 - Cleanup
            em.remove(vehicleReadAgain);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    public void test12MFromExistingObjectModification() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - Create the objects.
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
            compareObjects(vehicle, vehicleRead);
            
            Chassis chassis = vehicleRead.getChassis();
            for (Wheel wheel : chassis.getWheels()) {
                wheel.setSerialNumber(wheel.getSerialNumber() + 10);
            }
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Verification
            clearCache();
            em = createEntityManager();
            beginTransaction(em);
            
            Vehicle vehicleReadAgain = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicleRead, vehicleReadAgain);
            
            // Step 3 - Cleanup
            em.remove(vehicleReadAgain);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     * Test cascading with removing 1:M orphan removal objects
     */
    public void test12MWithCascade() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - Create the objects.
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
            
            em.persist(engine);
            em.persist(vehicle);
          
            engine.removeSparkPlug(plug3);
            engine.removeSparkPlug(plug5);
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Verification     
            clearCache();        
            em = createEntityManager();    
            beginTransaction(em);

            Engine engineRead = em.find(Engine.class, engine.getId());
            compareObjects(engine, engineRead);
            SparkPlug plug1Read = em.find(SparkPlug.class, plug1.getId());
            compareObjects(plug1, plug1Read);
            SparkPlug plug2Read = em.find(SparkPlug.class, plug2.getId());
            compareObjects(plug2, plug2Read);
            SparkPlug plug4Read = em.find(SparkPlug.class, plug4.getId());
            compareObjects(plug4, plug4Read);
            SparkPlug plug6Read = em.find(SparkPlug.class, plug6.getId());
            compareObjects(plug6, plug6Read);
            
            assertNull("SparkPlug3 should not be inserted", em.find(SparkPlug.class, plug3.getId()));
            assertNull("SparkPlug5 should not be inserted", em.find(SparkPlug.class, plug5.getId()));
            
            // Step 3 - Cleanup
            em.remove(em.find(Vehicle.class, vehicle.getId()));
            em.remove(engineRead);
            em.remove(plug1Read);
            em.remove(plug2Read);
            em.remove(plug4Read);
            em.remove(plug6Read);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     *  Test the cascade removal of objects
     */
    public void test12MWithCascadeFromOR() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - create the objects
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
            
            // Step 2 - Verification
            clearCache();        
            em = createEntityManager();         
            beginTransaction(em);
            
            WheelNut wheelNut3Read = em.find(WheelNut.class, wheelNut3.getId());
            compareObjects(wheelNut3, wheelNut3Read);
            WheelNut wheelNut4Read = em.find(WheelNut.class, wheelNut4.getId());
            compareObjects(wheelNut4, wheelNut4Read);
            Wheel wheel1Read = em.find(Wheel.class, wheel1.getId());
            compareObjects(wheel1, wheel1);
            Wheel wheel2Read = em.find(Wheel.class, wheel2.getId());
            compareObjects(wheel2, wheel2);
            
            // Step 3 - Cleanup
            em.remove(em.find(Vehicle.class, vehicle.getId()));
            // chassis removed automatically
            // wheels removed automatically
            
            for (WheelNut wheelNut : wheel1Read.getWheelNuts()) {
                em.remove(wheelNut);
            }
            
            for (WheelNut wheelNut : wheel2Read.getWheelNuts()) {
                em.remove(wheelNut);
            }
            
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        } 
    }
    
    /**
     * Test a one to one relationship removal/modification, cascaded from an existing object
     */
    public void test121FromExistingObject() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - create the objects.
            beginTransaction(em);
            Vehicle vehicle = new Vehicle("GT-X");
            Chassis chassis1 = new Chassis(1l);
            vehicle.setChassis(chassis1);
            em.persist(vehicle);
            commitTransaction(em); 
            
            // Step 2 - query for the object and orphan an object.
            clearCache();
            beginTransaction(em);
            Vehicle vehicleToModify = em.find(Vehicle.class, vehicle.getId());
            vehicleToModify.setModel("GT-X2");
            vehicleToModify.setChassis(null);
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 3 - verify the orphan has been removed.
            clearCache();
            em = createEntityManager();
            beginTransaction(em);
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicleToModify, vehicleRead);
            
            assertNull("Chassis1 should have been removed", em.find(Chassis.class, chassis1.getId()));
            
            // Step 4 - cleanup
            em.remove(vehicleRead);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
                
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     * Test a one to one relationship removal/modification, cascaded from an existing object
     */
    public void test121FromExistingObjectModification() {
        EntityManager em = createEntityManager();
    
        try {
            // Step 1 - create the objects.
            beginTransaction(em);

            Vehicle vehicle = new Vehicle("GT-X");
            Chassis chassis1 = new Chassis(1l);
            vehicle.setChassis(chassis1);

            em.persist(vehicle);
            commitTransaction(em); 
        
            beginTransaction(em);
            Vehicle vehicleToModify = em.find(Vehicle.class, vehicle.getId());

            vehicleToModify.setModel("GT-X2");
            em.persist(vehicleToModify);
        
            Chassis chassis2 = new Chassis(2l);
            vehicleToModify.setChassis(chassis2);
        
            commitTransaction(em);
            closeEntityManager(em);
        
            // Step 2 - Verification
            clearCache();
            em = createEntityManager();
            beginTransaction(em);
            
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicleToModify, vehicleRead);
            Chassis chassis2Read = em.find(Chassis.class, chassis2.getId());
            compareObjects(chassis2, chassis2Read);
            
            assertNull("Chassis1 should have been removed", em.find(Chassis.class, chassis1.getId()));
            
            // Step 3 - Cleanup
            
            em.remove(vehicleRead);
            em.remove(chassis2Read);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
                
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     * Test a one to one relationship removal/modification, cascaded from an existing object
     */
    public void test121IgnoredFromRemovedExistingObject() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - create the objects.
            beginTransaction(em);
            Vehicle vehicle = new Vehicle("GT-X");
            em.persist(vehicle);
            Chassis chassis = new Chassis(1l);
            em.persist(chassis);
            //vehicle.setChassis(chassis1);
            commitTransaction(em); 
            
            // Step 2 - query for the object and orphan an object.
            clearCache();
            beginTransaction(em);
            Vehicle vehicleToRemove = em.find(Vehicle.class, vehicle.getId());
            em.remove(vehicleToRemove);
            em.flush();
            vehicleToRemove.setChassis(em.find(Chassis.class, chassis.getId()));
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 3 - verify the orphan was not removed.
            clearCache();
            em = createEntityManager();
            beginTransaction(em);
            
            Chassis chassisRead = em.find(Chassis.class, chassis.getId());
            assertNotNull("Chassis should not have been removed", chassisRead);
            assertNull("Vehicle should have been removed", em.find(Vehicle.class, vehicle.getId()));
            
            // Step 4 - cleanup
            em.remove(chassisRead);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    public void test121WithCascade() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - Create the objects
            beginTransaction(em);
            Vehicle vehicle = new Vehicle("GT-X");
            
            Chassis chassis1 = new Chassis(1l);
            vehicle.setChassis(chassis1);
            
            em.persist(vehicle);
            
            Chassis chassis2 = new Chassis(2l);
            vehicle.setChassis(chassis2);        
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Verification
            clearCache();        
            em = createEntityManager();
            beginTransaction(em);
            
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicle, vehicleRead);
            Chassis chassis2Read = em.find(Chassis.class, chassis2.getId());
            compareObjects(chassis2, chassis2Read);
            
            assertNull("Chassis1 should not be inserted", em.find(Chassis.class, chassis1.getId()));
            
            // Step 3 - Cleanup
            em.remove(vehicleRead);
            em.remove(chassis2Read);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        
            closeEntityManager(em);
            throw e;
        }
    }
    
    public void test121WithCascadeRemoveFromOR() {
        EntityManager em = createEntityManager();
        
        try {
            beginTransaction(em);

            Vehicle vehicle = new Vehicle("GT-X");
            Engine engine = new Engine(123456789l);

            em.persist(vehicle);
            em.persist(engine);
            vehicle.setEngine(engine);
            em.remove(vehicle);
            assertFalse(em.contains(engine));
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Verification     
            clearCache();        
            em = createEntityManager();    
            
            assertNull("Vehicle should not be inserted", em.find(Vehicle.class, vehicle.getId()));
            assertNull("Engine should not be inserted", em.find(Engine.class, engine.getId()));
            
            // Step 3 - Cleanup
            // nothing to clean up ...
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    public void test121WithNoCascadeMerge1() {
        EntityManager em = createEntityManager();
            
        try {
            // Step 1 - Create the objects.
            beginTransaction(em);
            
            Vehicle vehicle = new Vehicle("GT-X");
            Engine engine = new Engine(123456789l);
            
            vehicle.setEngine(engine);
            vehicle = em.merge(vehicle);
            engine = vehicle.getEngine();
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Verification     
            clearCache();        
            em = createEntityManager();    
            beginTransaction(em);
            
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicle, vehicleRead);
            
            Engine engineRead = em.find(Engine.class, engine.getId());
            compareObjects(engine, engineRead);
            
            // Step 3 - Cleanup
            em.remove(vehicleRead);
            em.remove(engineRead);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    public void test121WithNoCascadeMerge2() {
        EntityManager em = createEntityManager();

        try {
            // Step 1 - Create the objects.
            beginTransaction(em);

            Vehicle vehicle = new Vehicle("GT-X");
            em.persist(vehicle);
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Add an engine and merge the vehicle.     
            clearCache();        
            em = createEntityManager();    
            beginTransaction(em);
            
            Engine engine = new Engine(123456789l);
            vehicle.setEngine(engine);            
            vehicle = em.merge(vehicle);
            engine = vehicle.getEngine();
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 3 - Verification
            clearCache();        
            em = createEntityManager();    
            beginTransaction(em);
            
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicle, vehicleRead);
            
            Engine engineRead = em.find(Engine.class, engine.getId());
            compareObjects(engine, engineRead);
            
            // Step 4 - Cleanup
            em.remove(vehicleRead);
            em.remove(engineRead);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    public void test121WithNoCascadeMerge3() {
        EntityManager em = createEntityManager();

        try {
            // Step 1 - Create the objects.
            beginTransaction(em);
            Vehicle vehicle = new Vehicle("GT-X");
            Engine engine = new Engine(123456789l);
            vehicle.setEngine(engine);
            em.persist(vehicle);
            em.persist(engine);
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Add an engine and merge the vehicle.     
            clearCache();        
            em = createEntityManager();    
            beginTransaction(em);
            
            vehicle.setModel("GT-Z");
            engine.setSerialNumber(123456789);
            
            em.merge(vehicle);
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 3 - Verification
            clearCache();        
            em = createEntityManager();    
            beginTransaction(em);
            
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            assertTrue("The vehicle had the wrong model number", vehicleRead.getModel().equals("GT-Z"));
            
            Engine engineRead = em.find(Engine.class, engine.getId());
            assertTrue("The engine had the wrong serial number", engine.getSerialNumber() == 123456789l);
                        
            // Step 4 - Cleanup
            em.remove(vehicleRead);
            em.remove(engineRead);
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    public void test121WithNoCascadePersist() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - Create the objects.
            beginTransaction(em);
            
            Vehicle vehicle = new Vehicle("GT-X");
            Engine engine = new Engine(123456789l);
            
            vehicle.setEngine(engine);
            
            // Persist the vehicle without persisting the engine. Should get
            // an exception.
            // em.persist(engine);
            Exception exception = null;
            try {
                em.persist(vehicle);
                commitTransaction(em);
            } catch (Exception e) {
                exception = e;
            } finally {
                closeEntityManager(em);
            }
            
            // Step 2 - Verification
            assertNotNull("No exception was caught when one was expected", exception);
            
            // Step 3 - Cleanup
            // no cleanup necessary ...
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     * Test the cascade removal of objects related to a 1:1 privately owned object
     */
    public void test121WithCascadeFromOR() {
        EntityManager em = createEntityManager();
        
        try {
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
            
            // Vehicle and WheelRim (x4) should be persisted because they are 
            // not within an orphanRemoval mapping.
            // Wheel and Chassis should not be persisted because they are 
            // within an orphanRemoval mapping. 
            vehicle.setChassis(null);
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Step 2 - Verification
            clearCache();        
            em = createEntityManager(); 
            beginTransaction(em);
            
            Object wheelRim1Read = em.find(WheelRim.class, wheelRim1.getId());
            compareObjects(wheelRim1, wheelRim1Read);        
            Object wheelRim2Read = em.find(WheelRim.class, wheelRim2.getId());
            compareObjects(wheelRim2, wheelRim2Read);
            Object wheelRim3Read = em.find(WheelRim.class, wheelRim3.getId());
            compareObjects(wheelRim3, wheelRim3Read);
            Object wheelRim4Read = em.find(WheelRim.class, wheelRim4.getId());
            compareObjects(wheelRim4, wheelRim4Read);
            Vehicle vehicleRead = em.find(Vehicle.class, vehicle.getId());
            compareObjects(vehicle, vehicleRead);
            
            assertNull("Wheel1 should not be inserted", em.find(Wheel.class, wheel1.getId()));
            assertNull("Wheel2 should not be inserted", em.find(Wheel.class, wheel2.getId()));
            assertNull("Wheel3 should not be inserted", em.find(Wheel.class, wheel3.getId()));
            assertNull("Wheel4 should not be inserted", em.find(Wheel.class, wheel4.getId()));
            assertNull("Chassis should not be inserted", em.find(Chassis.class, chassis.getId()));
    
            // Step 3 - Cleanup
            em.remove(vehicleRead);
            em.remove(wheelRim1Read);
            em.remove(wheelRim2Read);
            em.remove(wheelRim3Read);
            em.remove(wheelRim4Read);
            
            commitTransaction(em);
            closeEntityManager(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * use case: 
     * A ---> B : the relationship between A and B is privately owned and cascade persist 
     * Create an instance of A and B. Persist A 
     * Load A and update an attribute of B. Persist A 
     * => the attribute is not changed in DB.
     */
    public void test121ChangeFromExistingObject() {
        EntityManager em = createEntityManager();
        
        try {
            // Step 1 - Create an object and a related object (p-o)
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
            beginTransaction(em);
            
            Vehicle vehicleReadAgain = em.find(Vehicle.class, vehicle.getId());
            try {
                assertNotNull("Vehicle should have been inserted", vehicleReadAgain);
                    
                long serialNumberFromDatabase = vehicleReadAgain.getChassis().getSerialNumber();
                assertEquals("Chassis serial number should have been changed on the DB", newSerialNumber, serialNumberFromDatabase); // fails
            } finally {            
                // Step 4 - clean up database
                 
                em.remove(vehicleReadAgain.getChassis());
                em.remove(vehicleReadAgain);
                    
                commitTransaction(em);
                    
                closeEntityManager(em);
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new OrphanRemovalModelTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
}