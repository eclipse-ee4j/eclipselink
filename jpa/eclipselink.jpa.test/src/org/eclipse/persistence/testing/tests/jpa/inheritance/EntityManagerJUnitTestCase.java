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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.inheritance;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.AAA;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bus;
import org.eclipse.persistence.testing.models.jpa.inheritance.Company;
import org.eclipse.persistence.testing.models.jpa.inheritance.DDD;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.PerformanceTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.SeniorEngineer;
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
import javax.persistence.PersistenceException;

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
        
        rollbackTransaction(em);;
    }
    
    public void testRemoveInheritedManyToMany(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            SeniorEngineer eng = new SeniorEngineer();
            eng.setName("Vela");
            em.persist(eng);
            Laptop laptop = new Laptop();
            ComputerPK pk = new ComputerPK("Dell", 2111);
            laptop.setComputerPK(pk);
            em.persist(laptop);
            eng.getLaptops().add(laptop);
            em.flush();
            
            em.remove(eng);
            em.flush();
        } catch (PersistenceException ex){
            if (ex.getCause() instanceof DatabaseException){
                // An Integrity Constraint Violation was the reason for the initial exception
                fail("SQLException thrown when removing an object that inherits a ManyToMany");
            } else {
                fail("UnexpectedException thrown");
            }
        } finally {
            rollbackTransaction(em);
        }
    }
    
    // Bug 336133
    public void testGenericCollectionOnSuperclass(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            Bus bus = new Bus();
            bus.setDescription("a fast bus");
            PerformanceTireInfo tire = new PerformanceTireInfo();
            tire.setPressure(100);
            bus.getTires().add(tire);
            em.persist(bus);
            em.flush();
            
            em.clear();
            clearCache();
            
            bus = em.find(Bus.class, bus.getId());
            assertNotNull("Bus is null.", bus);
            assertTrue("Bus has no tires.", bus.getTires().size() == 1);
        } finally {
            rollbackTransaction(em);
        }
        
    }
    
    // bug 325035
    public void testAddToUninstantiatedSet(){
        EntityManager em = createEntityManager();
        beginTransaction(em);

        AAA a = new AAA();
        em.persist(a);
        
        DDD d = new DDD();
        em.persist(d);
        
        Set<DDD> ds = new HashSet<DDD>();
        ds.add(d);
        a.setDdds(ds);
        d.setAaa(a);
        commitTransaction(em);
        
        clearCache();
        try{
            em = createEntityManager();
            beginTransaction(em);
            a = em.getReference(AAA.class, a.getId());
            a.getDdds().add(d);
            d.setAaa(a);
            commitTransaction(em);
            
            assertTrue("The collection contains too many elements", a.getDdds().size() == 1);
           
        } finally  {
            em = createEntityManager();
            beginTransaction(em);
            a = em.find(AAA.class, a.getId());
            d = em.find(DDD.class, d.getId());
            em.remove(a);
            em.remove(d);
            commitTransaction(em);
        }
    }
    
    // bug 325035
    public void testLazySetInstantiationLazy(){
        if (!isWeavingEnabled()){
            return;
        }
        EntityManager em = createEntityManager();
        CollectionMapping mapping = ((CollectionMapping)getServerSession().getProject().getClassDescriptor(AAA.class).getMappingForAttributeName("ddds"));
        Boolean lazyIndirection = mapping.shouldUseLazyInstantiationForIndirectCollection();
        mapping.setUseLazyInstantiationForIndirectCollection(true);
        beginTransaction(em);

        AAA a = new AAA();
        em.persist(a);
        
        DDD d = new DDD();
        em.persist(d);
        
        Set<DDD> ds = new HashSet<DDD>();
        ds.add(d);
        a.setDdds(ds);
        d.setAaa(a);
        commitTransaction(em);
        
        clearCache();
        try{
            em = createEntityManager();
            a = em.find(AAA.class, a.getId());
            a.getDdds().add(new DDD());
            
            assertTrue("Lazy instantiation was not enabled for IndirectSet.", ((IndirectSet)a.getDdds()).getAddedElements().size() == 1);
           
        } finally  {
            em = createEntityManager();
            beginTransaction(em);
            a = em.find(AAA.class, a.getId());
            d = em.find(DDD.class, d.getId());
            em.remove(a);
            em.remove(d);
            commitTransaction(em);
            mapping.setUseLazyInstantiationForIndirectCollection(lazyIndirection);
        }
    }
    
    // bug 325035
    public void testLazySetInstantiationEager(){
        EntityManager em = createEntityManager();
        beginTransaction(em);

        AAA a = new AAA();
        em.persist(a);
        
        DDD d = new DDD();
        em.persist(d);
        
        Set<DDD> ds = new HashSet<DDD>();
        ds.add(d);
        a.setDdds(ds);
        d.setAaa(a);
        commitTransaction(em);
        
        clearCache();
        try{
            em = createEntityManager();
            a = em.find(AAA.class, a.getId());
            a.getDdds().add(new DDD());
            
            assertTrue("Lazy instantiation was not disabled for IndirectSet.", ((IndirectSet)a.getDdds()).getAddedElements().size() == 0);
           
        } finally  {
            em = createEntityManager();
            beginTransaction(em);
            a = em.find(AAA.class, a.getId());
            d = em.find(DDD.class, d.getId());
            em.remove(a);
            em.remove(d);
            commitTransaction(em);
        }
    }
    
    // bug 325035
    public void testLazyListInstantiationLazy(){
        if (!isWeavingEnabled()){
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Company company = new Company();
        company.setName("ListCo");
        em.persist(company);
        
        Car car = new Car();
        em.persist(car);
        
        company.getVehicles().add(car);
        car.setOwner(company);

        commitTransaction(em);
        
        clearCache();
        try{
            em = createEntityManager();
            company = em.find(Company.class, company.getId());
            company.getVehicles().add(new Car());
            
            assertTrue("Lazy instantiation was not enabled for IndirectList.", ((IndirectList)company.getVehicles()).getAddedElements().size() == 1);
           
        } finally  {
            em = createEntityManager();
            beginTransaction(em);
            company = em.find(Company.class, company.getId());
            car = em.find(Car.class, car.getId());
            em.remove(company);
            em.remove(car);
            commitTransaction(em);
        }
    }
    
    // bug 325035
    public void testLazyListInstantiationEager(){
        EntityManager em = createEntityManager();
        CollectionMapping mapping = ((CollectionMapping)getServerSession().getProject().getClassDescriptor(Company.class).getMappingForAttributeName("vehicles"));
        Boolean lazyIndirection = mapping.shouldUseLazyInstantiationForIndirectCollection();
        mapping.setUseLazyInstantiationForIndirectCollection(false);
        beginTransaction(em);

        Company company = new Company();
        company.setName("ListCo");
        em.persist(company);
        
        Car car = new Car();
        em.persist(car);
        
        company.getVehicles().add(car);
        car.setOwner(company);

        commitTransaction(em);
        
        clearCache();
        try{
            em = createEntityManager();
            company = em.find(Company.class, company.getId());
            company.getVehicles().add(new Car());
            
            assertTrue("Lazy instantiation was not disabled for IndirectList.", ((IndirectList)company.getVehicles()).getAddedElements().size() == 0);
           
        } finally  {
            em = createEntityManager();
            beginTransaction(em);
            company = em.find(Company.class, company.getId());
            car = em.find(Car.class, car.getId());
            em.remove(company);
            em.remove(car);
            commitTransaction(em);
            mapping.setUseLazyInstantiationForIndirectCollection(lazyIndirection);
        }
    }
}
