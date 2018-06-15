/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/19/2010-2.1 Guy Pelletier
//       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
//     01/15/2016-2.7 Mythily Parthasarathy
//       - 485984: Add test for retrieval of cached getReference within a txn
package org.eclipse.persistence.testing.tests.jpa.advanced.fetchgroup;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.config.QueryHints;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.AdvancedFetchGroupTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.ChestProtector;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.Helmet;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.HockeyGear;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.Pads;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.GoalieGear.AgeGroup;
import org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup.Shelf;

public class AdvancedFetchGroupJunitTest extends JUnitTestCase {
    private static Integer padsId;
    private static Integer chestProtectorId;

    public AdvancedFetchGroupJunitTest() {
        super();
    }

    public AdvancedFetchGroupJunitTest(String name) {
        super(name);
    }

    public void setUp() {
        clearCache();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedFetchGroupJunitTest");

        suite.addTest(new AdvancedFetchGroupJunitTest("testSetup"));
        suite.addTest(new AdvancedFetchGroupJunitTest("testVerifyFetchGroups"));
        suite.addTest(new AdvancedFetchGroupJunitTest("testCreateHockeyGear"));
        suite.addTest(new AdvancedFetchGroupJunitTest("testFetchGroupOnPads"));
        suite.addTest(new AdvancedFetchGroupJunitTest("testFetchGroupOnChestProtector"));
        suite.addTest(new AdvancedFetchGroupJunitTest("testFetchGroupOnPadsFromInheritanceParent"));
        // Bug 434120
        suite.addTest(new AdvancedFetchGroupJunitTest("testFetchGroupMergeMapAttribute"));
        // Bug 485984
        suite.addTest(new AdvancedFetchGroupJunitTest("testFetchGroupForCachedReference"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedFetchGroupTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    public void testVerifyFetchGroups() {
        if (isWeavingEnabled()) {
            ClassDescriptor hockeyGearDescriptor = getServerSession().getDescriptor(HockeyGear.class);
            FetchGroupManager hockeyGearFetchGroupManager = hockeyGearDescriptor.getFetchGroupManager();
            assertTrue("Wrong number of fetch groups for HockeyGear", hockeyGearFetchGroupManager.getFetchGroups().size() == 1);
            assertNotNull("The 'MSRP' fetch group was not found for HockeyGear", hockeyGearFetchGroupManager.getFetchGroup("MSRP"));

            ClassDescriptor padsDescriptor = getServerSession().getDescriptor(Pads.class);
            FetchGroupManager padsFetchGroupManager = padsDescriptor.getFetchGroupManager();
            assertTrue("Wrong number of fetch groups for Pads", padsFetchGroupManager.getFetchGroups().size() == 3);
            assertNotNull("The 'HeightAndWidth' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("HeightAndWidth"));
            assertNotNull("The 'Weight' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("Weight"));
            assertNotNull("The 'AgeGroup' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("AgeGroup"));

            ClassDescriptor chestProtectorDescriptor = getServerSession().getDescriptor(ChestProtector.class);
            FetchGroupManager chestProtectorFetchGroupManager = chestProtectorDescriptor.getFetchGroupManager();
            assertTrue("Wrong number of fetch groups for ChestProtector", chestProtectorFetchGroupManager.getFetchGroups().size() == 1);
            assertNotNull("The 'AgeGroup' fetch group was not found for ChestProtector", chestProtectorFetchGroupManager.getFetchGroup("AgeGroup"));
        }
    }

    public void testCreateHockeyGear() {
        if (isWeavingEnabled()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);

            try {
                Pads pads = new Pads();
                pads.setAgeGroup(AgeGroup.SENIOR);
                pads.setDescription("Louisville TPS");
                pads.setHeight(35.5);
                pads.setMsrp(999.99);
                pads.setWeight(4.9);
                pads.setWidth(11.0);
                em.persist(pads);

                ChestProtector chestProtector = new ChestProtector();
                chestProtector.setAgeGroup(AgeGroup.INTERMEDIATE);
                chestProtector.setDescription("RBK Premier");
                chestProtector.setMsrp(599.99);
                chestProtector.setSize("Large");
                em.persist(chestProtector);

                commitTransaction(em);

                padsId = pads.getSerialNumber();
                chestProtectorId = chestProtector.getSerialNumber();
            } catch (RuntimeException e) {
                if (isTransactionActive(em)){
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
                throw e;
            }
        }
    }

    public void testFetchGroupOnPads() {
        if (isWeavingEnabled()) {
            EntityManager em = createEntityManager();
            Map properties = new HashMap();
            properties.put(QueryHints.FETCH_GROUP_NAME, "HeightAndWidth");
            Class PadsClass = Pads.class;
            Pads pads = (Pads) em.find(PadsClass, padsId, properties);

            try {
                verifyFetchedField(PadsClass.getDeclaredField("height"), pads, 35.5);
                verifyFetchedField(PadsClass.getDeclaredField("width"), pads, 11.0);

                verifyNonFetchedField(PadsClass.getDeclaredField("weight"), pads);
                verifyNonFetchedField(PadsClass.getField("ageGroup"), pads);
                verifyNonFetchedField(PadsClass.getField("description"), pads);
                verifyNonFetchedField(PadsClass.getField("msrp"), pads);
            } catch (Exception e) {
                fail("Error verifying field content: " + e.getMessage());
            } finally {
                closeEntityManager(em);
            }
        }
    }

    public void testFetchGroupOnChestProtector() {
        if (isWeavingEnabled()) {
            EntityManager em = createEntityManager();
            Map properties = new HashMap();
            properties.put(QueryHints.FETCH_GROUP_NAME, "AgeGroup");
            Class chestProtectorClass = ChestProtector.class;
            ChestProtector chestProtector = (ChestProtector) em.find(chestProtectorClass, chestProtectorId, properties);

            try {
                verifyFetchedField(chestProtectorClass.getField("ageGroup"), chestProtector, AgeGroup.INTERMEDIATE);

                verifyNonFetchedField(chestProtectorClass.getField("description"), chestProtector);
                verifyNonFetchedField(chestProtectorClass.getField("msrp"), chestProtector);
                verifyNonFetchedField(chestProtectorClass.getDeclaredField("size"), chestProtector);
            } catch (Exception e) {
                fail("Error verifying field content: " + e.getMessage());
            } finally {
                closeEntityManager(em);
            }
        }
    }

    public void testFetchGroupOnPadsFromInheritanceParent() {
        if (isWeavingEnabled()) {
            EntityManager em = createEntityManager();
            Map properties = new HashMap();
            properties.put(QueryHints.FETCH_GROUP_NAME, "MSRP");
            Class PadsClass = Pads.class;
            Pads pads = (Pads) em.find(PadsClass, padsId, properties);

            try {
                verifyFetchedField(PadsClass.getField("msrp"), pads, 999.99);

                verifyNonFetchedField(PadsClass.getDeclaredField("height"), pads);
                verifyNonFetchedField(PadsClass.getDeclaredField("width"), pads);
                verifyNonFetchedField(PadsClass.getDeclaredField("weight"), pads);
                verifyNonFetchedField(PadsClass.getField("ageGroup"), pads);
                verifyNonFetchedField(PadsClass.getField("description"), pads);

            } catch (Exception e) {
                fail("Error verifying field content: " + e.getMessage());
            } finally {
                closeEntityManager(em);
            }
        }
    }

    public void testFetchGroupMergeMapAttribute() {
        if (!isWeavingEnabled()) {
            return;
        }

        // test data - manual creation
        int helmetId = 1;
        Helmet helmet = null;
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            em.createNativeQuery("DELETE FROM JPA_HELMET_PROPERTIES WHERE HELMET_ID=" + helmetId).executeUpdate();
            em.createNativeQuery("DELETE FROM JPA_HELMET WHERE ID=" + helmetId).executeUpdate();
            em.createNativeQuery("INSERT INTO JPA_HELMET (ID, COLOR) VALUES (" + helmetId + ", 'red')").executeUpdate();
            commitTransaction(em);
        } catch (Exception e) {
            fail("Error creating test data: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }

        // test
        em = createEntityManager();
        try {
            beginTransaction(em);
            helmet = em.find(Helmet.class, helmetId);
            assertNotNull("Found Helmet entity with id: " + helmetId + " should be non-null", helmet);
            em.clear();

            helmet.getColor();
            helmet.addProperty("random", "This parrot is deceased");
            Helmet helmetMerged = em.merge(helmet);

            commitTransaction(em);
        } catch (Exception e) {
            fail("Error merging an Entity with a Map attribute: " + e.getMessage());
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            beginTransaction(em);
            helmet = em.find(Helmet.class, helmetId);
            if (helmet != null) {
                em.remove(helmet);
            }
            commitTransaction(em);
            closeEntityManager(em);
        }
    }
    
    public void testFetchGroupForCachedReference() {
        if (isWeavingEnabled()) {
            EntityManager em = createEntityManager();
            beginTransaction(em);

            Shelf original = new Shelf();
            original.setId(1L);
            original.setName("original");
            em.persist(original);

            Shelf modified = new Shelf();
            modified.setId(2L);
            modified.setName("modified");
            em.persist(modified);

            Helmet helmet = new Helmet();
            helmet.setId(3);
            helmet.setShelf(original);
            em.persist(helmet);

            commitTransaction(em);
            closeEntityManager(em);
            clearCache();

            em = createEntityManager();
            //Create a changeset to ensure that the getReference() result is pushed to cache
            beginTransaction(em);
            em.unwrap(UnitOfWorkImpl.class).beginEarlyTransaction();
            
            helmet = em.find(Helmet.class,  helmet.getId());
            modified = em.getReference(Shelf.class, modified.getId());
            helmet.setShelf(modified);

            commitTransaction(em);
            closeEntityManager(em);

            try {
                em = createEntityManager();
                beginTransaction(em);
                em.unwrap(UnitOfWorkImpl.class).beginEarlyTransaction();

                modified = em.find(Shelf.class, modified.getId());
                if (modified.getName() == null) {
                    fail("find returned entity with missing attribute");
                }
            } finally {
                if (isTransactionActive(em)){
                   rollbackTransaction(em); 
                }
                beginTransaction(em);
                helmet = em.find(Helmet.class,  helmet.getId());
                em.remove(helmet);
                modified = em.find(Shelf.class,  modified.getId());
                em.remove(modified);
                original = em.find(Shelf.class,  original.getId());
                em.remove(original);
                commitTransaction(em);
                closeEntityManager(em);
            }
        }

    }

    protected void verifyFetchedField(Field field, Object obj, Object value) {
        try {
            field.setAccessible(true);
            assertTrue("The field [" + field.getName() +"] was not fetched", field.get(obj).equals(value));
        } catch (IllegalAccessException e) {
            fail("Error verifying field content: " + e.getMessage());
        }
    }

    protected void verifyNonFetchedField(Field field, Object obj) {
        try {
            field.setAccessible(true);
            assertTrue("The field [" + field.getName() +"] was fetched", field.get(obj) == null);
        } catch (IllegalAccessException e) {
            fail("Error verifying field content: " + e.getMessage());
        }
    }
}
