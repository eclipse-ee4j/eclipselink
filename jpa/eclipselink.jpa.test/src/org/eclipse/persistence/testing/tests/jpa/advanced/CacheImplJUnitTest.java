/*******************************************************************************
 * Copyright (c)  2008, Sun Microsystems, Inc. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
 *     12/04/2008 - 2.0 Darani Yallapragada 
 *       - 248780: Initial contribution for JPA 2.0
 *     06/02/2010 - 2.1 Michael O'Brien 
 *       - 248780: Refactor Cache Implementation surrounding evict()
 *         http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/cache_api#Refactor_20100322
 *         Fix evict() to handle non-Entity classes
 *         Refactor to get IdentityMapAccessor state through EMF reference
 *         Refactor dependencies to use Interfaces instead of Impl subclasses
 *         Handle no CMPPolicy case for getId()
 *         Handle no associated descriptor for Class parameter
 *         MappedSuperclasses passed to evict() cause implementing subclasses to be evicted
 *         Throw an IAE for Interfaces and Embeddable classes passed to evict()
******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.advanced;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.models.jpa.advanced.*;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.AdvancedFetchGroupTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.ChestProtector;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.Gear;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.GoalieGear;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.NonPersistedSubclassOfChestProtector;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.Pads;
import org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTableCreator;
import org.eclipse.persistence.testing.models.jpa.metamodel.Manufacturer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.CMP3Policy;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.jpa.JpaCache;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * @author DaraniY
 */
public class CacheImplJUnitTest extends JUnitTestCase {

    private static final String METAMODEL_PERSISTENCE_UNIT = "metamodel1";
    
    public CacheImplJUnitTest() {
        super();
    }

    public CacheImplJUnitTest(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        clearCache();
    }

    /**
     * Note: These tests are setup with ID generation - but it is not used.
     * We set the Id manually so we can test cache operations like eviction properly.
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CacheImplJUnitTest");

        suite.addTest(new CacheImplJUnitTest("testSetup"));
        suite.addTest(new CacheImplJUnitTest("testContains"));
        suite.addTest(new CacheImplJUnitTest("testEvictClassObject"));
        suite.addTest(new CacheImplJUnitTest("testEvictClass"));
        suite.addTest(new CacheImplJUnitTest("testEvictAll"));
        suite.addTest(new CacheImplJUnitTest("testEvictContains"));
        suite.addTest(new CacheImplJUnitTest("testCacheAPI"));
        // 20100322: 248780: CacheImpl refactor for non-Entity classes
        // the following external test models are used [AdvancedFetchGroupTableCreator, CacheableTableCreator]
        suite.addTest(new CacheImplJUnitTest("testEvictClass_MappedSuperclass_RemovesAssignableSubclasses"));
        suite.addTest(new CacheImplJUnitTest("testEvictClassObject_MappedSuperclass_RemovesAssignableSubclasses"));
        suite.addTest(new CacheImplJUnitTest("testEvictClass_JavaLangClass_hasNoEffect"));
        suite.addTest(new CacheImplJUnitTest("testEvictClass_NonPersistableParentOfEntityMappedSuperclassChain_RemovesAssignableSubclasses"));
        suite.addTest(new CacheImplJUnitTest("testEvictClass_NonPersistableSubclassOfEntityMappedSuperclassChain_hasNoEffect"));        
        
        suite.addTest(new CacheImplJUnitTest("testGetId_fromUnmanagedMappedSuperclass_handles_null_descriptor"));
        suite.addTest(new CacheImplJUnitTest("testGetId_fromUnsupportedJavaLangInteger_throwsIAE_on_null_descriptor"));
        // Run these tests last as they modify the state of the ClassDescriptor permanently to verify variant corner use cases        
        suite.addTest(new CacheImplJUnitTest("testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_weaving_on"));
        // 315714: comment out 3 of the 10 new tests requiring setup() table creation - until the metamodel one is added to setup()
        // Tests fail to create tables when run outside of ant or before the metamodel model is created
        /*
        suite.addTest(new CacheImplJUnitTest("testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_and_null_pk_with_weaving_on"));
        suite.addTest(new CacheImplJUnitTest("testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_and_null_pk_with_weaving_off"));
        */
        // test null descriptor on closed entityManager                
        return suite;
    }
    
    public String getPersistenceUnitName(){
        return "default1";
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());
        new AdvancedFetchGroupTableCreator().replaceTables(JUnitTestCase.getServerSession());
        new CacheableTableCreator().replaceTables(JUnitTestCase.getServerSession());
        // 315714: metamodel model requires a table creator now that we are persisting with it
        clearCache();
    }
    
    /**
     * Test of contains method, of class CacheImpl.
     */
    public void testContains() {
        EntityManager em1 = createEntityManager();
        beginTransaction(em1);
        Employee e1 = new Employee();
        e1.setFirstName("ellie1");
        e1.setId(101);
        em1.persist(e1);
        commitTransaction(em1);
        closeEntityManager(em1);
        boolean result = getEntityManagerFactory().getCache().contains(Employee.class, 101);
        assertTrue("Employee not found in cache", result);
    }
    
    /**
     * Test cache API.
     */
    public void testCacheAPI() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Employee employee = new Employee();
        employee.setFirstName("testCacheAPI");
        em.persist(employee);
        commitTransaction(em);
        closeEntityManager(em);
        JpaCache cache = (JpaCache)getEntityManagerFactory().getCache();
        assertTrue("Employee not valid in cache", cache.isValid(employee));
        assertTrue("Employee not valid in cache", cache.isValid(Employee.class, employee.getId()));
        cache.timeToLive(employee);
        assertTrue("Employee not found in cache", cache.getObject(Employee.class, employee.getId()) != null);
        assertTrue("Employee not found in cache", cache.contains(employee));
        cache.evict(employee);
        cache.putObject(employee);
        cache.print();        
        cache.removeObject(employee);
        cache.removeObject(Employee.class, employee.getId());
        cache.clear();
        cache.clear(Employee.class);
        cache.clearQueryCache();
        cache.clearQueryCache("findAllEmployeesByIdAndFirstName");
        assertTrue("Employee id not correct", employee.getId().equals(cache.getId(employee)));
        cache.print();
        cache.print(Employee.class);
        cache.printLocks();
        cache.validate();
    }

    /**
     * Test of evict method, of class CacheImpl.
     */
    public void testEvictClassObject() {
        String beforeCache;
        String afterCache;
        EntityManager em2 = createEntityManager();
        beginTransaction(em2);
        Employee e2 = new Employee();
        e2.setFirstName("ellie");
        e2.setId(121);
        em2.persist(e2);
        commitTransaction(em2);
        closeEntityManager(em2);
        EntityManager em3 = createEntityManager();
        EntityManager em4=createEntityManager();
        try {
            Employee emp1 = (Employee) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e2);
            emp1.setFirstName("foo");
            beforeCache = em3.find(Employee.class, 121).getFirstName();
            getEntityManagerFactory().getCache().evict(Employee.class, 121);
            Employee e3 = em4.find(Employee.class, 121);
            afterCache = e3.getFirstName();
            assertNotSame("Assertion Error", beforeCache, afterCache);
        } finally {
            closeEntityManager(em3);
            closeEntityManager(em4);
        }
    }

    /**
     * Test of evict method, of class CacheImpl.
     */
    public void testEvictClass() {
        EntityManager em5 = createEntityManager();
        beginTransaction(em5);
        Employee e4 = new Employee();
        e4.setFirstName("ellie");
        e4.setId(131);
        em5.persist(e4);
        commitTransaction(em5);
        closeEntityManager(em5);
        EntityManager em6 = createEntityManager();
        EntityManager em7 = createEntityManager();
        try {
            Employee emp2 = (Employee) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e4);
            emp2.setFirstName("food");
            String expected = em6.find(Employee.class, 131).getFirstName();
            getEntityManagerFactory().getCache().evict(Employee.class);
            Employee e5 = em7.find(Employee.class, 131);
            String actual = e5.getFirstName();
            assertNotSame("Assertion Error", expected, actual);
        } finally {
            closeEntityManager(em6);
            closeEntityManager(em7);
        }
    }

    /**
     * Test of evictAll method, of class CacheImpl.
     */
    public void testEvictAll() {
        EntityManager em8 = createEntityManager();
        beginTransaction(em8);
        Employee e6 = new Employee();
        e6.setFirstName("ellie");
        e6.setId(141);
        Department d1 = new Department();
        d1.setId(3);
        d1.setName("Computers");
        em8.persist(d1);
        em8.persist(e6);
        commitTransaction(em8);
        String expectedEmp = e6.getFirstName();
        String expectedDept = d1.getName();
        closeEntityManager(em8);
        EntityManager em9 = createEntityManager();
        try {
            Employee emp3 = (Employee) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e6);
            Department dept1 = (Department) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(d1);
            emp3.setFirstName("foo");
            dept1.setName("science");
            getEntityManagerFactory().getCache().evictAll();
            Employee e4 = em9.find(Employee.class, 141);
            String actualEmp = e4.getFirstName();
            Department d2 = em9.find(Department.class, 3);
            String actualDept = d2.getName();
            assertEquals("Assertion Error", expectedEmp, actualEmp);
            assertEquals("Assertion Error", expectedDept, actualDept);
        } finally {
            closeEntityManager(em9);
        }

    }
    
    public void testEvictContains() {
        EntityManager em =  createEntityManager();
        beginTransaction(em);
        Employee emp = new Employee();
        emp.setFirstName("evictContains");
        em.persist(emp);
        commitTransaction(em);

        try {
            assertTrue(em.getEntityManagerFactory().getCache().contains(Employee.class, emp.getId()));
    
            em.clear();
            Employee findEmp = em.find(Employee.class, emp.getId());
            assertNotNull(findEmp);
    
            em.getEntityManagerFactory().getCache().evict(Employee.class, emp.getId());
            assertFalse(em.getEntityManagerFactory().getCache().contains(Employee.class, emp.getId()));
        } finally {
            closeEntityManager(em);
        }
    }
    
    // for entity1-mappedSuperclass-entity2 we only have cache entries for entity1 and entity2
    // evict lowest 3nd level subclass of entity-mappedSuperclass-entity does not evict 1st level root entity
    //evict(Pads) will evict Pads but leave Chest and HockeyGear
    //evict(GoalieGear) will evict all Pads, Chest and HockeyGear
    //evict(HockeyGear) will evict all Pads, Chest and HockeyGear
    //(evict(MappedSuperclass:GoalieGear) and evict(Entity:HockeyGear) have the same effect)
    // evict middle 2nd level mappedSuperclass evicts 3rd level implementing entities but not 1st level root entity
    // evict 1st level root entity removes both 2nd level mappedSuperclasses and 3rd level implementing entities 
    /**
     * In this test we attempt to remove the MappedSuperclass (superclass) from the entity cache.
     * The resulting action removes all implementing subclasses - which has the same effect
     * as just removing the implementing Entity in the first case.
     * 
     * Hierarchy:
     * HockeyGear (Abstract Entity)
     *       +----- GoalieGear (Concrete MappedSuperclass) --- (we will evict this one)
     *                  +----- Chest Protector (Concrete Entity) ---- cacheable (but this will actually be evicted)
     *                  +----- Pads (Concrete Entity) ---- cacheable (as well as this one)
     */
    public void testEvictClass_MappedSuperclass_RemovesAssignableSubclasses() {
        if (! isJPA10()) {        
            int ID_PADS = ID_TEST_BASE + 2100;
            int ID_CHESTPROTECTOR = ID_PADS++;
            final EntityManager em = createEntityManager();
            // Persist both implementing subclasses of GoalieGear
            beginTransaction(em);
            // HockeyGear(Abstract Entity) << GoalieGear (Concrete MappedSuperclass) << ChestProtector (Concrete Entity)
            ChestProtector e1 = new ChestProtector();
            e1.setDescription("chest_protector");
            e1.setSerialNumber(new Integer(ID_PADS));
            em.persist(e1);
            commitTransaction(em);
            // do not close the entityManager between transactions or you will get bug# 307445
            
            beginTransaction(em);
            // HockeyGear(Abstract Entity) << GoalieGear (Concrete MappedSuperclass) << ChestProtector (Concrete Entity)
            Pads p1 = new Pads();
            p1.setDescription("pads");
            p1.setSerialNumber(new Integer(ID_CHESTPROTECTOR));
            em.persist(p1);
            commitTransaction(em);
            closeEntityManager(em);
        
            EntityManager em1 = createEntityManager();
            EntityManager em2 = createEntityManager();
            try {
                ChestProtector e2 = (ChestProtector) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e1);
                Pads p2 = (Pads) ((JpaEntityManagerFactory) getEntityManagerFactory())
                    .getServerSession().getIdentityMapAccessor().getFromIdentityMap(p1);
                // change the entity in the cache (only) - later a find() will get the unmodified version in the database
                e2.setDescription("new_chest_protector");
                p2.setDescription("new_pads");
                
                String expected_chestProtector = em1.find(GoalieGear.class, ID_CHESTPROTECTOR).getDescription();
                String expected_pads = em1.find(GoalieGear.class, ID_PADS).getDescription();
            
                // evict the inheriting entities via their MappedSuperclass (without referencing their ID)
                getEntityManagerFactory().getCache().evict(GoalieGear.class);            
            
                // read the inheriting entities directly from the database (because they are no longer in the cache - hopefully)
                GoalieGear g5 = em2.find(GoalieGear.class, ID_CHESTPROTECTOR);
                GoalieGear p5 = em2.find(GoalieGear.class, ID_PADS);            
            
                // check that the root entity is also evicted
                
                // Check that we are not actually getting the supposed "to be evicted" cached version (that was modified)
                String actual_chestProtector = g5.getDescription();
                String actual_pads = p5.getDescription();
                // verify that assignable subclasses (ChestProtector and Pads) are removed from the cache
                assertNotSame("Assertion Error - There should be no modified cached entity instance in the cache - the entity read from the database should be different. "
                    , expected_chestProtector, actual_chestProtector);
                assertNotSame("Assertion Error - There should be no modified cached entity instance in the cache - the entity read from the database should be different. "
                    , expected_pads, actual_pads);            
            } finally {
                closeEntityManager(em1);
                closeEntityManager(em2);
            }
        }
    }

    /**
     * In this test we attempt to remove the Entity subclasses of a root (non-persistable) plain java class
     * from the entity cache.
     * The resulting action removes all implementing subclasses - which has the same effect
     * as just removing the root implementing Entity in the first case.
     * 
     * Hierarchy:
     * Gear (non-entity/non-mappedSuperclass plain java class - with non-persistable state) - (we will evict this one)
     *    +-----HockeyGear (Abstract Entity) (not cacheable)
     *                +----- GoalieGear (Concrete MappedSuperclass) --- (no cache entry) 
     *                              +----- Chest Protector (Concrete Entity) ---- cacheable (but this will actually be evicted)
     *                              +----- Pads (Concrete Entity) ---- cacheable (as well as this one)
     */
    public void testEvictClass_NonPersistableParentOfEntityMappedSuperclassChain_RemovesAssignableSubclasses() {
        if (! isJPA10()) {        
            int ID_PADS = ID_TEST_BASE + 800;
            int ID_CHESTPROTECTOR = ID_PADS++;
            final EntityManager em = createEntityManager();
            // Persist both implementing subclasses of GoalieGear
            beginTransaction(em);
            // HockeyGear(Abstract Entity) << GoalieGear (Concrete MappedSuperclass) << ChestProtector (Concrete Entity)
            ChestProtector e1 = new ChestProtector();
            e1.setDescription("chest_protector");
            e1.setSerialNumber(new Integer(ID_PADS));
            em.persist(e1);
            commitTransaction(em);
            // do not close the entityManager between transactions or you will get bug# 307445
            
            beginTransaction(em);
            // HockeyGear(Abstract Entity) << GoalieGear (Concrete MappedSuperclass) << ChestProtector (Concrete Entity)
            Pads p1 = new Pads();
            p1.setDescription("pads");
            p1.setSerialNumber(new Integer(ID_CHESTPROTECTOR));
            em.persist(p1);
            commitTransaction(em);
            closeEntityManager(em);
        
            EntityManager em1 = createEntityManager();
            EntityManager em2 = createEntityManager();
            try {
                ChestProtector e2 = (ChestProtector) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e1);
                Pads p2 = (Pads) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(p1);
                // change the entity in the cache (only) - later a find() will get the unmodified version in the database
                e2.setDescription("new_chest_protector");
                p2.setDescription("new_pads");
                
                String expected_chestProtector = em1.find(GoalieGear.class, ID_CHESTPROTECTOR).getDescription();
                String expected_pads = em1.find(GoalieGear.class, ID_PADS).getDescription();
            
                // evict the inheriting entities via their non-persistable root (without referencing their ID)
                getEntityManagerFactory().getCache().evict(Gear.class);            
            
                // read the inheriting entities directly from the database (because they are no longer in the cache - hopefully)
                GoalieGear g5 = em2.find(GoalieGear.class, ID_CHESTPROTECTOR);
                GoalieGear p5 = em2.find(GoalieGear.class, ID_PADS);            
            
                // check that the root entity is also evicted
                
                // Check that we are not actually getting the supposed "to be evicted" cached version (that was modified)
                String actual_chestProtector = g5.getDescription();
                String actual_pads = p5.getDescription();
                // verify that assignable subclasses (ChestProtector and Pads) are removed from the cache
                assertNotSame("Assertion Error - There should be no modified cached entity instance in the cache - the entity read from the database should be different. "
                    , expected_chestProtector, actual_chestProtector);
                assertNotSame("Assertion Error - There should be no modified cached entity instance in the cache - the entity read from the database should be different. "
                    , expected_pads, actual_pads);            
            } finally {
                closeEntityManager(em1);
                closeEntityManager(em2);
            }
        }
    }

    /**
     * In this test we attempt to remove the (non-persistable) plain java class subclass of an Entity inheritance tree
     * from the entity cache.
     * The resulting action removes all implementing subclasses - which has the same effect
     * as just removing the root implementing Entity in the first case.
     * 
     * Hierarchy:
     * Gear (non-entity/non-mappedSuperclass plain java class - with non-persistable state) - (we will evict this one)
     *    +-----HockeyGear (Abstract Entity) (not cacheable)
     *                +----- GoalieGear (Concrete MappedSuperclass) --- (no cache entry) 
     *                              +----- Chest Protector (Concrete Entity) ---- cacheable (but this will actually be evicted)
     *                              +----- Pads (Concrete Entity) ---- cacheable (as well as this one)
     */
    public void testEvictClass_NonPersistableSubclassOfEntityMappedSuperclassChain_hasNoEffect() {
        if (! isJPA10()) {        
            int ID_PADS = ID_TEST_BASE + 900;
            int ID_CHESTPROTECTOR = ID_PADS++;
            final EntityManager em = createEntityManager();
            // Persist both implementing subclasses of GoalieGear
            beginTransaction(em);
            // HockeyGear(Abstract Entity) << GoalieGear (Concrete MappedSuperclass) << ChestProtector (Concrete Entity)
            ChestProtector e1 = new ChestProtector();
            e1.setDescription("chest_protector");
            e1.setSerialNumber(new Integer(ID_PADS));
            em.persist(e1);
            commitTransaction(em);
            // do not close the entityManager between transactions or you will get bug# 307445
            
            beginTransaction(em);
            // HockeyGear(Abstract Entity) << GoalieGear (Concrete MappedSuperclass) << ChestProtector (Concrete Entity)
            Pads p1 = new Pads();
            p1.setDescription("pads");
            p1.setSerialNumber(new Integer(ID_CHESTPROTECTOR));
            em.persist(p1);
            commitTransaction(em);
            closeEntityManager(em);
        
            EntityManager em1 = createEntityManager();
            EntityManager em2 = createEntityManager();
            try {
                ChestProtector e2 = (ChestProtector) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e1);
                Pads p2 = (Pads) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(p1);
                // change the entity in the cache (only) - later a find() will get the unmodified version in the database
                e2.setDescription("new_chest_protector");
                p2.setDescription("new_pads");
                
                String expected_chestProtector = em1.find(GoalieGear.class, ID_CHESTPROTECTOR).getDescription();
                String expected_pads = em1.find(GoalieGear.class, ID_PADS).getDescription();
            
                // evict the inheriting entities via their non-persistable root (without referencing their ID)
                getEntityManagerFactory().getCache().evict(NonPersistedSubclassOfChestProtector.class);            
                //getEntityManagerFactory().getCache().evict(Pads.class);                
            
                // read the inheriting entities directly from the cache (because they were not evicted)            
                GoalieGear g5 = em2.find(GoalieGear.class, ID_CHESTPROTECTOR);
                GoalieGear p5 = em2.find(GoalieGear.class, ID_PADS);            
                String actual_chestProtector = g5.getDescription();
                String actual_pads = p5.getDescription();                
                assertSame("Assertion Error - There should be a modified cached entity instance in the cache - the entity is not actually read from the database"
                    , expected_pads, actual_pads);
                assertSame("Assertion Error - There should be a modified cached entity instance in the cache - the entity is not actually read from the database"
                    , expected_chestProtector, actual_chestProtector);
            } finally {
                closeEntityManager(em1);
                closeEntityManager(em2);
            }
        }
    }
    
    /**
     * In this test we attempt to remove the MappedSuperclass (superclass) from the entity cache.
     * The resulting action removes all implementing subclasses - which has the same effect
     * as just removing the implementing Entity in the first case.
     * 
     * Hierarchy:
     * Gear (non-entity/non-mappedSuperclass plain java class - with non-persistable state) - (we will evict this one)
     *    +-----HockeyGear (Abstract Entity) (not cacheable)
     *                +----- GoalieGear (Concrete MappedSuperclass) --- (no cache entry) 
     *                              +----- Chest Protector (Concrete Entity) ---- cacheable (but this will actually be evicted)
     *                              +----- Pads (Concrete Entity) ---- cacheable (as well as this one)
     */
    public void testEvictClassObject_MappedSuperclass_RemovesAssignableSubclasses() {
        if (! isJPA10()) {        
            int ID_PADS = ID_TEST_BASE + 200;
            int ID_CHESTPROTECTOR = ID_PADS++;
            EntityManager em = createEntityManager();
            // Persist both implementing subclasses of GoalieGear
            beginTransaction(em);
            // HockeyGear(Abstract Entity) << GoalieGear (Concrete MappedSuperclass) << ChestProtector (Concrete Entity)
            ChestProtector e1 = new ChestProtector();
            e1.setDescription("chest_protector");
            e1.setSerialNumber(new Integer(ID_PADS));
            em.persist(e1);
            commitTransaction(em);
            // do not close the entityManager between transactions or you will get bug# 307445
        
            beginTransaction(em);
            Pads p1 = new Pads();
            p1.setDescription("pads");
            p1.setSerialNumber(new Integer(ID_CHESTPROTECTOR));
            em.persist(p1);
            commitTransaction(em);
            closeEntityManager(em);
        
            EntityManager em1 = createEntityManager();
            EntityManager em2 = createEntityManager();
            try {
                GoalieGear c2 = (GoalieGear) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e1);
                Pads p2 = (Pads) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(p1);
                // change the entity in the cache (only) - later a find() will get the unmodified version in the database
                c2.setDescription("new_chest_protector");
                p2.setDescription("new_pads");
                String expected_chestProtector = em1.find(GoalieGear.class, ID_CHESTPROTECTOR).getDescription();
                String expected_pads = em1.find(GoalieGear.class, ID_PADS).getDescription();
            
                // evict the inheriting entity (by Id) via its' MappedSuperclass
                    getEntityManagerFactory().getCache().evict(GoalieGear.class, c2.getSerialNumber()); // ChestProtector            
                    getEntityManagerFactory().getCache().evict(GoalieGear.class, p2.getSerialNumber()); // Pad
            
                // read the inheriting entities directly from the database (because they are no longer in the cache - hopefully)
                GoalieGear c5 = em2.find(GoalieGear.class, ID_CHESTPROTECTOR);
                GoalieGear p5 = em2.find(GoalieGear.class, ID_PADS);            
            
                // Check that we are not actually getting the supposed "to be evicted" cached version (that was modified)
                String actual_chestProtector = c5.getDescription();
                String actual_pads = p5.getDescription();
                // verify that assignable subclasses (ChestProtector and Pads) are removed from the cache
                assertNotSame("Assertion Error - There should be no modified cached entity instance in the cache - the entity read from the database should be different. "
                    , expected_chestProtector, actual_chestProtector);
                assertNotSame("Assertion Error - There should be no modified cached entity instance in the cache - the entity read from the database should be different. "
                    , expected_pads, actual_pads);            
            } finally {
                closeEntityManager(em1);
                closeEntityManager(em2);
            }
        }
    }
    
    
    //org.eclipse.persistence.testing.models.jpa.jpaadvancedproperties.Customer
    
    public void testEvictClass_JavaLangClass_hasNoEffect() {
        if (! isJPA10()) {
            int ID = ID_TEST_BASE + 300;
            boolean _exceptionThrown = false;
            EntityManager em = createEntityManager();
            // Persist
            beginTransaction(em);
            // HockeyGear(Abstract Entity) << GoalieGear (Concrete MappedSuperclass) << ChestProtector (Concrete Entity)
            ChestProtector e1 = new ChestProtector();
            e1.setDescription("gear");
            e1.setSerialNumber(new Integer(ID));
            em.persist(e1);
            commitTransaction(em);
            closeEntityManager(em);
        
            EntityManager em1 = createEntityManager();
            EntityManager em2 = createEntityManager();
            try {
                GoalieGear e2 = (GoalieGear) getDatabaseSession().getIdentityMapAccessor().getFromIdentityMap(e1);
                // change the entity in the cache (only) - later a find() will get the unmodified version in the database
                e2.setDescription("new_gear");
                String expected = em1.find(GoalieGear.class, ID).getDescription();
                // The following does not throws an IAE on a plain java (Integer) class - it will leave the cached entities as-is in the cache
                // This evict exercises the else clause in the evict when the descriptor is null on the session
                getEntityManagerFactory().getCache().evict(Integer.class);
                // read the inheriting entities directly from the cache (because they were not evicted)            
                GoalieGear g5 = em2.find(GoalieGear.class, ID);
                String actual = g5.getDescription();
                assertSame("Assertion Error - There should be a modified cached entity instance in the cache - the entity is not actually read from the database"
                    , expected, actual);
            } catch (IllegalArgumentException iae) {
                _exceptionThrown = true;
            } finally {
                closeEntityManager(em1);
                closeEntityManager(em2);
                assertFalse("IllegalArgumentException was thrown.", _exceptionThrown);            
            }
        }
    }
            
    // The xml model version is not managed
    public void testGetId_fromUnmanagedMappedSuperclass_handles_null_descriptor() {
        if (!isJPA10()) {        
            int ID = ID_TEST_BASE + 400;
            boolean _exceptionThrown = false;
            EntityManager em = createEntityManager();
            beginTransaction(em);
            // CacheableTrueMappedSuperclass (MappedSuperclass with Id) << SubCacheableFalseEntity (Entity)
            org.eclipse.persistence.testing.models.jpa.xml.cacheable.SubCacheableFalseEntity anEntity 
                = new org.eclipse.persistence.testing.models.jpa.xml.cacheable.SubCacheableFalseEntity();
            org.eclipse.persistence.testing.models.jpa.xml.cacheable.CacheableTrueMappedSuperclass aMappedSuperclass 
                = new org.eclipse.persistence.testing.models.jpa.xml.cacheable.CacheableTrueMappedSuperclass();
            anEntity.setId(ID);
            // Can we place non-persistable objects into only the cache (non-entities)?
            // We do not need to persist this entity for this specific test
            //em.persist(anEntity);
            commitTransaction(em);
            closeEntityManager(em);
        
            EntityManager em1 = createEntityManager();
            EntityManager em2 = createEntityManager();
            Cache aJPACache = getEntityManagerFactory().getCache();
            JpaCache anEclipseLinkCache = (JpaCache)getEntityManagerFactory().getCache();
            Object anId = null;
            try {
                anId = anEclipseLinkCache.getId(anEntity);
            } catch (IllegalArgumentException iae) {
                _exceptionThrown = true;
            } finally {
                closeEntityManager(em1);
                closeEntityManager(em2);
                assertTrue("IllegalArgumentException should have been thrown on the unmanaged entity lacking a descriptor.", _exceptionThrown);            
            }
        }
    }

    public void testGetId_fromUnsupportedJavaLangInteger_throwsIAE_on_null_descriptor() {
        if (!isJPA10()) {
            boolean _exceptionThrown = false;
            EntityManager em1 = createEntityManager();            
            JpaCache anEclipseLinkCache = (JpaCache)getEntityManagerFactory().getCache();
            try {
                anEclipseLinkCache.getId(new Integer(1));
            } catch (IllegalArgumentException iae) {
                _exceptionThrown = true;
            } finally {
                closeEntityManager(em1);                
                assertTrue("IllegalArgumentException should have been thrown on a getId() call on an Integer which obviously has no descriptor.", _exceptionThrown);                
            }
        }
    }
    
    /**
     * In order to test handling of a null CMP3Policy on the descriptor during a cache.getId() call,
     * we must have an Id on an abstract MappedSuperclass root that is defined via sessions.xml.
     * Or we can simulate a null CMP3Policy by clearing it - so that the alternate code handling is run
     */
    public void testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_weaving_on() {
        if (!isJPA10()) {
            int ID = ID_TEST_BASE + 500;
            Object originalId = null;
            EntityManager em = createEntityManager();//);
            // Persist both implementing subclasses of GoalieGear
            beginTransaction(em);
            // CacheableTrueMappedSuperclass (MappedSuperclass with Id) << SubCacheableFalseEntity (Entity)
            org.eclipse.persistence.testing.models.jpa.cacheable.SubCacheableFalseEntity anEntity = new org.eclipse.persistence.testing.models.jpa.cacheable.SubCacheableFalseEntity();
            org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTrueMappedSuperclass aMappedSuperclass = new org.eclipse.persistence.testing.models.jpa.cacheable.CacheableTrueMappedSuperclass();
            anEntity.setId(ID);
            // Can we place non-persistable objects into only the cache (non-entities)?
            em.persist(anEntity);
            commitTransaction(em);
            closeEntityManager(em);
        
            EntityManager em1 = createEntityManager();
            JpaCache anEclipseLinkCache = (JpaCache)getEntityManagerFactory().getCache();
            Object anId = null;
            originalId = anEntity.getId();
        
            // clear the CMP3Policy to simulate a native pojo
            ClassDescriptor cdesc = ((EntityManagerImpl)em1).getSession().getClassDescriptor(anEntity.getClass());
            CMP3Policy policy = (CMP3Policy) (cdesc.getCMPPolicy());
            assertNotNull("CMP3Policy should exist", policy);
            cdesc.setCMPPolicy(null);
            try {
                // This call will test the weaving [on] section of getId()
                anId = anEclipseLinkCache.getId(anEntity);
                assertNotNull("Id instance should not be null", anId);
                assertTrue("Id instance should be of type Integer", anId instanceof Integer);            
                assertEquals(((Integer)anId).intValue(), ID);
                assertEquals(anId, originalId);
            } finally {
                closeEntityManager(em1);
            }
        }
    }

    public void testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_and_null_pk_with_weaving_on() {
        if (!isJPA10()) {
            //int ID = ID_TEST_BASE + 600;
            Integer ID = null; // keep object as null for unset testing
            Object originalId = null;
            EntityManager em = createEntityManager(METAMODEL_PERSISTENCE_UNIT);
            beginTransaction(em);
            // org.eclipse.persistence.testing.models.jpa.metamodel.Person(Concrete MappedSuperclass) <-- Corporation(Abstract MappedSuperclass) <-- Manufacturer (Entity)
            Manufacturer anEntity = new Manufacturer();
            //anEntity.setId(ID);
            // Can we place non-persistable objects into only the cache (non-entities)?
            em.persist(anEntity);
            commitTransaction(em);
            closeEntityManager(em);
        
            EntityManager em1 = createEntityManager(METAMODEL_PERSISTENCE_UNIT);
            JpaCache anEclipseLinkCache = (JpaCache)getEntityManagerFactory(METAMODEL_PERSISTENCE_UNIT).getCache();
            Object anId = null;
            // The originalId will be set by automatic sequence generation - if we require it
            originalId = anEntity.getId();
        
            // clear the CMP3Policy to simulate a native pojo
            ClassDescriptor cdesc = ((EntityManagerImpl)em1).getSession().getClassDescriptor(anEntity.getClass());
            CMP3Policy policy = (CMP3Policy) (cdesc.getCMPPolicy());
            assertNotNull("CMP3Policy should exist prior to having been cleared manually for this test", policy);
            cdesc.setCMPPolicy(null);
            try {
                anId = anEclipseLinkCache.getId(anEntity);
                assertNotNull("Id instance should not be null", anId);
                assertTrue("Id instance should be of type Integer", anId instanceof Integer);            
                assertEquals(anId, originalId);
                // set the CMPPolicy back for out of order testing
                cdesc.setCMPPolicy(policy);
            } finally {
                closeEntityManager(em1);
            }
        }
    }
    
    // We need a MappedSuperclass that is defined by a persistence.xml where eclipselink.weaving="false"
    // This test requires that weaving is off in persistence.xml for METAMODEL_PERSISTENCE_UNIT
    public void testGetId_fromNativeMappedSuperclass_handles_null_cmp3policy_and_null_pk_with_weaving_off() {
        if (!isJPA10()) {
            int ID = ID_TEST_BASE + 700;
            //Integer ID = null; // keep object as null for unset testing
            Object originalId = null;
            EntityManager em = createEntityManager(METAMODEL_PERSISTENCE_UNIT);
            beginTransaction(em);
            Manufacturer anEntity = new Manufacturer();            
            anEntity.setName("test");
            anEntity.setId(ID);
            // Can we place non-persistable objects into only the cache (non-entities)?
            em.persist(anEntity);
            commitTransaction(em);
            //closeEntityManager(em); // keep the EM open
        
            EntityManager em1 = createEntityManager(METAMODEL_PERSISTENCE_UNIT);
            JpaCache anEclipseLinkCache = (JpaCache)getEntityManagerFactory(METAMODEL_PERSISTENCE_UNIT).getCache();
            Object anId = null;
            // The originalId will be set by automatic sequence generation - if we require it
            originalId = anEntity.getId();
        
            // clear the CMP3Policy to simulate a native pojo
            ClassDescriptor cdesc = ((EntityManagerImpl)em).getSession().getClassDescriptor(anEntity.getClass());
            assertNotNull("ClassDescriptor for Entity must not be null", cdesc); // check that the entityManager is not null
            CMP3Policy policy = (CMP3Policy) (cdesc.getCMPPolicy());
            assertNotNull("CMP3Policy should exist prior to having been cleared manually for this test", policy);
            cdesc.setCMPPolicy(null);
            try {
                anId = anEclipseLinkCache.getId(anEntity);
                assertNotNull("Id instance should not be null", anId);
                assertTrue("Id instance should be of type Integer", anId instanceof Integer);            
                assertEquals(anId, ID);
                assertEquals(anId, originalId);
                // set the CMPPolicy back for out of order testing
                cdesc.setCMPPolicy(policy);
            } finally {
                closeEntityManager(em1);
                closeEntityManager(em);
            }
        }
    }
    
    // 20100422: 248780: CacheImpl refactor for non-Entity classes
    public static int ID_TEST_BASE = 3710; // change this value during iterative testing
}
