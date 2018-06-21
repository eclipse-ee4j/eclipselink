/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     01/19/2010-2.1 Guy Pelletier
//       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.tests.jpa.xml.advanced.fetchgroup;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.GoalieGear.AgeGroup;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.AdvancedFetchGroupTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.ChestProtector;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.HockeyGear;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.Pads;

public class EntityMappingsFetchGroupJunitTest extends JUnitTestCase {
    private String m_persistenceUnit;
    private static Integer padsId;
    private static Integer chestProtectorId;

    public EntityMappingsFetchGroupJunitTest() {
        super();
    }

    public EntityMappingsFetchGroupJunitTest(String name) {
        super(name);
    }

    public EntityMappingsFetchGroupJunitTest(String name, String persistenceUnit) {
        super(name);

        m_persistenceUnit = persistenceUnit;
    }

    public void setUp() {
        clearCache(m_persistenceUnit);
    }

    public static Test suite() {
        // This test suite can only be configured from an extended configuration.
        TestSuite suite = new TestSuite();
        suite.setName("EntityMappingsFetchGroupJunitTest");

        suite.addTest(new EntityMappingsFetchGroupJunitTest("testSetup", "extended-advanced"));
        suite.addTest(new EntityMappingsFetchGroupJunitTest("testVerifyFetchGroups", "extended-advanced"));
        suite.addTest(new EntityMappingsFetchGroupJunitTest("testCreateHockeyGear", "extended-advanced"));
        suite.addTest(new EntityMappingsFetchGroupJunitTest("testFetchGroupOnPads", "extended-advanced"));
        suite.addTest(new EntityMappingsFetchGroupJunitTest("testFetchGroupOnChestProtector", "extended-advanced"));
        suite.addTest(new EntityMappingsFetchGroupJunitTest("testFetchGroupOnPadsFromInheritanceParent", "extended-advanced"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedFetchGroupTableCreator().replaceTables(JUnitTestCase.getServerSession(m_persistenceUnit));
        clearCache(m_persistenceUnit);
    }

    public void testVerifyFetchGroups() {
        if (isWeavingEnabled(m_persistenceUnit)) {
            ClassDescriptor hockeyGearDescriptor = getServerSession(m_persistenceUnit).getDescriptor(HockeyGear.class);
            FetchGroupManager hockeyGearFetchGroupManager = hockeyGearDescriptor.getFetchGroupManager();
            assertTrue("Wrong number of fetch groups for HockeyGear", hockeyGearFetchGroupManager.getFetchGroups().size() == 1);
            assertNotNull("The 'MSRP' fetch group was not found for HockeyGear", hockeyGearFetchGroupManager.getFetchGroup("MSRP"));

            ClassDescriptor padsDescriptor = getServerSession(m_persistenceUnit).getDescriptor(Pads.class);
            FetchGroupManager padsFetchGroupManager = padsDescriptor.getFetchGroupManager();
            assertTrue("Wrong number of fetch groups for Pads", padsFetchGroupManager.getFetchGroups().size() == 3);
            assertNotNull("The 'HeightAndWidth' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("HeightAndWidth"));
            assertNotNull("The 'Weight' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("Weight"));
            assertNotNull("The 'AgeGroup' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("AgeGroup"));

            ClassDescriptor chestProtectorDescriptor = getServerSession(m_persistenceUnit).getDescriptor(ChestProtector.class);
            FetchGroupManager chestProtectorFetchGroupManager = chestProtectorDescriptor.getFetchGroupManager();
            assertTrue("Wrong number of fetch groups for ChestProtector", chestProtectorFetchGroupManager.getFetchGroups().size() == 1);
            assertNotNull("The 'AgeGroup' fetch group was not found for ChestProtector", chestProtectorFetchGroupManager.getFetchGroup("AgeGroup"));
        }
    }

    public void testCreateHockeyGear() {
        if (isWeavingEnabled(m_persistenceUnit)) {
            EntityManager em = createEntityManager(m_persistenceUnit);
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
        if (isWeavingEnabled(m_persistenceUnit)) {
            EntityManager em = createEntityManager(m_persistenceUnit);
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
        if (isWeavingEnabled(m_persistenceUnit)) {
            EntityManager em = createEntityManager(m_persistenceUnit);
            Map properties = new HashMap();
            properties.put(QueryHints.FETCH_GROUP_NAME, "AgeGroup");
            Class ChestProtecterClass = ChestProtector.class;
            ChestProtector chestProtector = (ChestProtector) em.find(ChestProtecterClass, chestProtectorId, properties);

            try {
                verifyFetchedField(ChestProtecterClass.getField("ageGroup"), chestProtector, AgeGroup.INTERMEDIATE);

                verifyNonFetchedField(ChestProtecterClass.getField("description"), chestProtector);
                verifyNonFetchedField(ChestProtecterClass.getField("msrp"), chestProtector);
                verifyNonFetchedField(ChestProtecterClass.getDeclaredField("size"), chestProtector);
            } catch (Exception e) {
                fail("Error verifying field content: " + e.getMessage());
            } finally {
                closeEntityManager(em);
            }
        }
    }

    public void testFetchGroupOnPadsFromInheritanceParent() {
        if (isWeavingEnabled(m_persistenceUnit)) {
            EntityManager em = createEntityManager(m_persistenceUnit);
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
