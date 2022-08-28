/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.advanced.fetchgroup;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.AdvancedFetchGroupTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.ChestProtector;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.GoalieGear.AgeGroup;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.HockeyGear;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup.Pads;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class XmlExtendedFetchGroupTest extends JUnitTestCase {
    private static Integer padsId;
    private static Integer chestProtectorId;

    public XmlExtendedFetchGroupTest() {
        super();
    }

    public XmlExtendedFetchGroupTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "extended-advanced";
    }

    @Override
    public void setUp() {
        clearCache();
    }

    public static Test suite() {
        // This test suite can only be configured from an extended configuration.
        TestSuite suite = new TestSuite();
        suite.setName("XmlExtendedFetchGroupTest");

        suite.addTest(new XmlExtendedFetchGroupTest("testSetup"));
        suite.addTest(new XmlExtendedFetchGroupTest("testVerifyFetchGroups"));
        suite.addTest(new XmlExtendedFetchGroupTest("testCreateHockeyGear"));
        suite.addTest(new XmlExtendedFetchGroupTest("testFetchGroupOnPads"));
        suite.addTest(new XmlExtendedFetchGroupTest("testFetchGroupOnChestProtector"));
        suite.addTest(new XmlExtendedFetchGroupTest("testFetchGroupOnPadsFromInheritanceParent"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedFetchGroupTableCreator().replaceTables(getPersistenceUnitServerSession());
        clearCache();
    }

    public void testVerifyFetchGroups() {
        if (isWeavingEnabled()) {
            ClassDescriptor hockeyGearDescriptor = getPersistenceUnitServerSession().getDescriptor(HockeyGear.class);
            FetchGroupManager hockeyGearFetchGroupManager = hockeyGearDescriptor.getFetchGroupManager();
            assertEquals("Wrong number of fetch groups for HockeyGear", 1, hockeyGearFetchGroupManager.getFetchGroups().size());
            assertNotNull("The 'MSRP' fetch group was not found for HockeyGear", hockeyGearFetchGroupManager.getFetchGroup("MSRP"));

            ClassDescriptor padsDescriptor = getPersistenceUnitServerSession().getDescriptor(Pads.class);
            FetchGroupManager padsFetchGroupManager = padsDescriptor.getFetchGroupManager();
            assertEquals("Wrong number of fetch groups for Pads", 3, padsFetchGroupManager.getFetchGroups().size());
            assertNotNull("The 'HeightAndWidth' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("HeightAndWidth"));
            assertNotNull("The 'Weight' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("Weight"));
            assertNotNull("The 'AgeGroup' fetch group was not found for Pads", padsFetchGroupManager.getFetchGroup("AgeGroup"));

            ClassDescriptor chestProtectorDescriptor = getPersistenceUnitServerSession().getDescriptor(ChestProtector.class);
            FetchGroupManager chestProtectorFetchGroupManager = chestProtectorDescriptor.getFetchGroupManager();
            assertEquals("Wrong number of fetch groups for ChestProtector", 1, chestProtectorFetchGroupManager.getFetchGroups().size());
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
            Map<String, Object> properties = new HashMap<>();
            properties.put(QueryHints.FETCH_GROUP_NAME, "HeightAndWidth");
            Class<Pads> PadsClass = Pads.class;
            Pads pads = em.find(PadsClass, padsId, properties);

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
            Map<String, Object> properties = new HashMap<>();
            properties.put(QueryHints.FETCH_GROUP_NAME, "AgeGroup");
            Class<ChestProtector> ChestProtecterClass = ChestProtector.class;
            ChestProtector chestProtector = em.find(ChestProtecterClass, chestProtectorId, properties);

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
        if (isWeavingEnabled()) {
            EntityManager em = createEntityManager();
            Map<String, Object> properties = new HashMap<>();
            properties.put(QueryHints.FETCH_GROUP_NAME, "MSRP");
            Class<Pads> PadsClass = Pads.class;
            Pads pads = em.find(PadsClass, padsId, properties);

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
            assertEquals("The field [" + field.getName() + "] was not fetched", field.get(obj), value);
        } catch (IllegalAccessException e) {
            fail("Error verifying field content: " + e.getMessage());
        }
    }

    protected void verifyNonFetchedField(Field field, Object obj) {
        try {
            field.setAccessible(true);
            assertNull("The field [" + field.getName() + "] was fetched", field.get(obj));
        } catch (IllegalAccessException e) {
            fail("Error verifying field content: " + e.getMessage());
        }
    }
}
