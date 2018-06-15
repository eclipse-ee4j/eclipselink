/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     04/07/2012-2.5 Guy Pelletier
//       - 384275: Customizer from a mapped superclass is not overridden by an entity customizer
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.AAA;
import org.eclipse.persistence.testing.models.jpa.inheritance.Betta;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bicycle;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bus;
import org.eclipse.persistence.testing.models.jpa.inheritance.CitrusFruit;
import org.eclipse.persistence.testing.models.jpa.inheritance.Company;
import org.eclipse.persistence.testing.models.jpa.inheritance.DDD;
import org.eclipse.persistence.testing.models.jpa.inheritance.Fish;
import org.eclipse.persistence.testing.models.jpa.inheritance.FishTank;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.PerformanceTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.Seed;
import org.eclipse.persistence.testing.models.jpa.inheritance.SeededFruit;
import org.eclipse.persistence.testing.models.jpa.inheritance.SeniorEngineer;
import org.eclipse.persistence.testing.models.jpa.inheritance.SportsCar;
import org.eclipse.persistence.testing.models.jpa.inheritance.Car;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.models.jpa.inheritance.PetStore;
import org.eclipse.persistence.testing.models.jpa.inheritance.Engineer;
import org.eclipse.persistence.testing.models.jpa.inheritance.Computer;
import org.eclipse.persistence.testing.models.jpa.inheritance.ComputerPK;
import org.eclipse.persistence.testing.models.jpa.inheritance.Desktop;
import org.eclipse.persistence.testing.models.jpa.inheritance.Laptop;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.VehicleDirectory;

import junit.framework.Test;
import junit.framework.TestSuite;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class EntityManagerJUnitTestCase extends JUnitTestCase {

    public EntityManagerJUnitTestCase() {
        super();
    }

    public EntityManagerJUnitTestCase(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityManagerJUnitTestCase");
        suite.addTest(new EntityManagerJUnitTestCase("testSetup"));
        suite.addTest(new EntityManagerJUnitTestCase("testOverriddenCustomizer"));
        suite.addTest(new EntityManagerJUnitTestCase("testAddToUninstantiatedSet"));
        suite.addTest(new EntityManagerJUnitTestCase("testAddDuplicateToUninstantiatedSet"));
        suite.addTest(new EntityManagerJUnitTestCase("testAssociationWithEmbeddedIdSubclassEntityInJoinedStrategy"));
        suite.addTest(new EntityManagerJUnitTestCase("testGenericCollectionOnSuperclass"));
        suite.addTest(new EntityManagerJUnitTestCase("testLazyListInstantiationEager"));
        suite.addTest(new EntityManagerJUnitTestCase("testLazyListInstantiationLazy"));
        suite.addTest(new EntityManagerJUnitTestCase("testLazySetInstantiationEager"));
        suite.addTest(new EntityManagerJUnitTestCase("testMapKeyInheritance"));
        suite.addTest(new EntityManagerJUnitTestCase("testPersistPolymorphicRelationship"));
        suite.addTest(new EntityManagerJUnitTestCase("testRemoveInheritedManyToMany"));
        suite.addTest(new EntityManagerJUnitTestCase("testUpateTireInfo"));
        suite.addTest(new EntityManagerJUnitTestCase("testUpateTireInfo"));
        // EL bug 336486
        suite.addTest(new EntityManagerJUnitTestCase("testCacheExpiryInitializationForInheritance"));
        // Bug 404071
        suite.addTest(new EntityManagerJUnitTestCase("testJoinedInheritanceOneToManyJoinFetch"));
        // Bug 415526
        suite.addTest(new EntityManagerJUnitTestCase("testCascadeMergeWithTargetInheritance"));
        // Bug 458177
        suite.addTest(new EntityManagerJUnitTestCase("testJoinedInheritanceWithAbstractSuperclass"));
        // Bug 355721
        suite.addTest(new EntityManagerJUnitTestCase("testJoinedInheritancePersistWithReadOnlyEntity"));

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

    public void testCacheExpiryInitializationForInheritance() {
        ServerSession session = JUnitTestCase.getServerSession();

        ClassDescriptor personDescriptor = session.getDescriptor(Person.class); // parent
        ClassDescriptor engineerDescriptor = session.getDescriptor(Engineer.class); // subclass
        ClassDescriptor seniorEngineerDescriptor = session.getDescriptor(SeniorEngineer.class); // subclass of subclass

        // Policy existence check
        assertNotNull("personDescriptor's cacheInvalidationPolicy should not be null",
                personDescriptor.getCacheInvalidationPolicy());
        assertNotNull("engineerDescriptor's cacheInvalidationPolicy should not be null",
                engineerDescriptor.getCacheInvalidationPolicy());
        assertNotNull("seniorEngineerDescriptor's cacheInvalidationPolicy should not be null",
                seniorEngineerDescriptor.getCacheInvalidationPolicy());

        // Policy class check
        assertTrue("personDescriptor's cacheInvalidationPolicy should be TimeToLiveCacheInvalidationPolicy",
                personDescriptor.getCacheInvalidationPolicy() instanceof TimeToLiveCacheInvalidationPolicy);
        assertTrue("engineerDescriptor's cacheInvalidationPolicy should be TimeToLiveCacheInvalidationPolicy",
                engineerDescriptor.getCacheInvalidationPolicy() instanceof TimeToLiveCacheInvalidationPolicy);
        assertTrue("seniorEngineerDescriptor's cacheInvalidationPolicy should be TimeToLiveCacheInvalidationPolicy",
                seniorEngineerDescriptor.getCacheInvalidationPolicy() instanceof TimeToLiveCacheInvalidationPolicy);

        // Subclass clone check
        assertFalse("engineerDescriptor's cacheInvalidationPolicy should be a clone",
                engineerDescriptor.getCacheInvalidationPolicy() == personDescriptor.getCacheInvalidationPolicy());
        assertFalse("seniorEngineerDescriptor's cacheInvalidationPolicy should be a clone",
                seniorEngineerDescriptor.getCacheInvalidationPolicy() == personDescriptor.getCacheInvalidationPolicy());

        // Subclass TTL check
        long ttl = ((TimeToLiveCacheInvalidationPolicy)personDescriptor.getCacheInvalidationPolicy()).getTimeToLive();
        assertEquals("engineerDescriptor's invalidation TTL should be " + ttl,
                ttl, ((TimeToLiveCacheInvalidationPolicy)engineerDescriptor.getCacheInvalidationPolicy()).getTimeToLive());
        assertEquals("seniorEngineerDescriptor's invalidation TTL should be " + ttl,
                ttl, ((TimeToLiveCacheInvalidationPolicy)seniorEngineerDescriptor.getCacheInvalidationPolicy()).getTimeToLive());
    }

    /**
     * Verifies that alias name is not customized from an invalid customizer.
     */
    public void testOverriddenCustomizer() {
        ServerSession session = JUnitTestCase.getServerSession();

        ClassDescriptor computerDescriptor = session.getDescriptor(Computer.class);
        assertFalse("Computer alias was incorrect", computerDescriptor.getAlias().equals("InvalidAliasName"));
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

     // bug391833 - Duplicate insert on lazy set with attribute change tracking
    public void testAddDuplicateToUninstantiatedSet(){
        String string1 = "String1";
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try{
            AAA a = new AAA();
            Set<String> ds = new HashSet<String>();
            ds.add(string1);
            a.setStringSet(ds);

            em.persist(a);

            em.flush();
            em.clear();

            a = em.find(AAA.class, a.getId());
            em.refresh(a);
            a.getStringSet().add(string1);

            em.flush();

            assertTrue("The collection contains too many elements", a.getStringSet().size() == 1);

            java.util.List results = em.createQuery("select s from AAA a join a.stringSet s where a.id = "+a.getId()).getResultList();
            assertTrue("The Database contains too many elements", results.size() == 1);
        } finally  {
            if (this.isTransactionActive(em)) {
                this.rollbackTransaction(em);
            }
            this.closeEntityManager(em);
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

    // Bug 404071
    public void testJoinedInheritanceOneToManyJoinFetch() {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            CitrusFruit fruit = new CitrusFruit();
            fruit.setName("Orange");
            fruit.setGrade(1);
            for (int i = 0; i < 4; i++) {
                fruit.addSeed(new Seed());
            }
            em.persist(fruit);

            commitTransaction(em);
            em.clear();
            clearCache();

            // query on superclass
            SeededFruit fruitRead = em.find(SeededFruit.class, fruit.getId());

            assertNotNull("Fruit should not be null", fruitRead);
            assertNotNull("Fruit's seeds should not be null", fruitRead.getSeeds());
            assertTrue("Fruit's seeds should contain 4 elements", fruitRead.getSeeds().size() == 4);
        } finally {
            closeEntityManager(em);
        }
    }

    // Bug 415526
    public void testCascadeMergeWithTargetInheritance() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Company company = new Company();
            company.setName("CascadeMerge, Inc");
            em.persist(company);
            em.flush();

            Bicycle bicycle = new Bicycle();
            bicycle.setDescription("road bike");
            company.getVehicles().add(bicycle);
            bicycle.setOwner(company);
            company = em.merge(company);
            em.flush();

            Number bicycleId = company.getVehicles().iterator().next().getId();
            String classDiscriminatorValue = (String)em.createNativeQuery("SELECT VEH_TYPE FROM CMP3_VEHICLE WHERE ID = " + bicycleId).getSingleResult();

            if (classDiscriminatorValue == null) {
                fail("Class discriminator value written into the db for the merged Bicycle is null");
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /*
     * Added for Bug 458177
     * - Instantiate a FishTank, unidirectionally lazy-referencing an
     *   abstract Entity (Fish). Persist.
     * - Instantiate a Betta (concrete extension of Fish) and reference
     *   from FishTank's 'fishes' collection. Persist and commit.
     * - Acquire new EM and retrieve cached FishTank. Remove Entity.
     * - This 'simple' operation previously attempted to instantiate the abstract
     *   Fish class during UOW resume (Resulting in InstantiationException).
     */
    public void testJoinedInheritanceWithAbstractSuperclass() {
        Long tankId = null;
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            Betta fish = new Betta();
            fish.setName("swimmy");
            fish.setColor("blue");

            em.persist(fish);

            FishTank fishTank = new FishTank();
            List<Fish> list = new ArrayList<Fish>();
            list.add(fish);
            fishTank.setFishes(list);

            em.persist(fishTank);

            tankId = fishTank.getId();

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }

        assertNotNull("FishTank ID should not be null", tankId);

        em = createEntityManager();
        try {
            beginTransaction(em);
            FishTank fishTank = em.find(FishTank.class, tankId);
            assertNotNull("FishTank should not be null", fishTank);

            em.remove(fishTank);

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    /*
     * Added for Bug 355721
     * - Instantiate and persist (but do not associate) a FishTank Entity and
     *   PetStore Entity.
     * - Clear the EMF cache
     * - Acquire new EM and retrieve the PetStore Entity
     * - Retrieve all FishTank Entities with a RO NamedQuery
     * - Associate the PetStore (lazy unidirectional 1:M) with a FishTank, persist.
     * - This operation previously failed with a ClassCastException.
     */
    public void testJoinedInheritancePersistWithReadOnlyEntity() {
        EntityManager em = createEntityManager();
        Long petStoreId = null;

        try {
            beginTransaction(em);

            FishTank fishTank = new FishTank();
            em.persist(fishTank);

            PetStore petStore = new PetStore();
            petStore.setStoreName("Bob's Fish");
            petStore.setFishTanks(new ArrayList<FishTank>());

            em.persist(petStore);
            petStoreId = petStore.getId();

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }

        // cache clear is necessary (reset lazy loading)
        getEntityManagerFactory().getCache().evictAll();

        em = createEntityManager();
        try {
            beginTransaction(em);

            PetStore petStore = em.find(PetStore.class, petStoreId);

            Query query = em.createNamedQuery("findAllFishTanks");
            query.setHint(QueryHints.READ_ONLY, HintValues.TRUE);

            List<FishTank> allFishTanks = query.getResultList();
            FishTank fishTank = allFishTanks.get(0);

            // add read-only entity to referencing unidirectional relationship
            petStore.getFishTanks().add(fishTank);
            em.persist(petStore);

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

}
