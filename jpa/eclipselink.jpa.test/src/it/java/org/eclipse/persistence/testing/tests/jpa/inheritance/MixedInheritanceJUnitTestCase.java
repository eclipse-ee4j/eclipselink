/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     12/18/2009-2.1 Guy Pelletier
//       - 211323: Add class extractor support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import java.util.List;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;

import org.eclipse.persistence.testing.models.jpa.inheritance.MacBook;
import org.eclipse.persistence.testing.models.jpa.inheritance.MacBookPro;
import org.eclipse.persistence.testing.models.jpa.inheritance.MudTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.RockTireInfo;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireRating;
import org.eclipse.persistence.testing.models.jpa.inheritance.TireRatingComment;

import org.eclipse.persistence.testing.models.jpa.inheritance.SuperclassEntityJoined;
import org.eclipse.persistence.testing.models.jpa.inheritance.SubclassEntityJoined;
import org.eclipse.persistence.testing.models.jpa.inheritance.MappedSuperclassJoined;
import org.eclipse.persistence.testing.models.jpa.inheritance.SuperclassEntitySingleTable;
import org.eclipse.persistence.testing.models.jpa.inheritance.SubclassEntitySingleTable;
import org.eclipse.persistence.testing.models.jpa.inheritance.MappedSuperclassSingleTable;
import org.eclipse.persistence.testing.models.jpa.inheritance.SuperclassEntityTablePerClass;
import org.eclipse.persistence.testing.models.jpa.inheritance.SubclassEntityTablePerClass;
import org.eclipse.persistence.testing.models.jpa.inheritance.MappedSuperclassTablePerClass;

import org.eclipse.persistence.testing.models.jpa.inheritance.listeners.TireInfoListener;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.persistence.EntityManager;

public class MixedInheritanceJUnitTestCase extends JUnitTestCase {
    private static int mudTireId;
    private static int rockTireId;

    public MixedInheritanceJUnitTestCase() {
        super();
    }

    public MixedInheritanceJUnitTestCase(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        clearCache();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("MixedInheritanceJUnitTestCase");
        suite.addTest(new MixedInheritanceJUnitTestCase("testSetup"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewMudTire"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewRockTire"));

        suite.addTest(new MixedInheritanceJUnitTestCase("testReadNewMudTire"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testReadNewRockTire"));

        suite.addTest(new MixedInheritanceJUnitTestCase("testAppleComputers"));

        // bug 396587 - Weaving of mapped superclass hierarchy ends at superclass without attributes
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewJoinedTableSuperclass"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewSingleTableSuperclass"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewTablePerClassSuperclass"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewJoinedTableSubclass"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewSingleTableSubclass"));
        suite.addTest(new MixedInheritanceJUnitTestCase("testCreateNewTablePerClassSubclass"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritanceTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    public void testAppleComputers() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        MacBook macBook1 = new MacBook();
        macBook1.setRam(2);
        MacBook macBook2 = new MacBook();
        macBook2.setRam(4);

        MacBookPro macBookPro1 = new MacBookPro();
        macBookPro1.setRam(4);
        macBookPro1.setColor("Black");
        MacBookPro macBookPro2 = new MacBookPro();
        macBookPro2.setRam(6);
        macBookPro2.setColor("Red");
        MacBookPro macBookPro3 = new MacBookPro();
        macBookPro3.setRam(8);
        macBookPro3.setColor("Green");
        MacBookPro macBookPro4 = new MacBookPro();
        macBookPro4.setRam(8);
        macBookPro4.setColor("Blue");

        try {
            em.persist(macBook1);
            em.persist(macBook2);

            em.persist(macBookPro1);
            em.persist(macBookPro2);
            em.persist(macBookPro3);
            em.persist(macBookPro4);

            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting macbooks: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }

        clearCache();
        em = createEntityManager();

        List macBooks = em.createNamedQuery("findAllMacBooks").getResultList();
        assertTrue("The wrong number of mac books were returned: " + macBooks.size() + ", expected: 6", macBooks.size() == 6);

        List macBookPros = em.createNamedQuery("findAllMacBookPros").getResultList();
        assertTrue("The wrong number of mac book pros were returned: " + macBookPros.size() + ", expected: 4", macBookPros.size() == 4);
    }

    public void testCreateNewMudTire() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        MudTireInfo mudTire = new MudTireInfo();
        mudTire.setName("Goodyear Mud Tracks");
        mudTire.setCode("MT-674-A4");
        mudTire.setPressure(new Integer(100));
        mudTire.setTreadDepth(3);

        TireRating tireRating = new TireRating();
        tireRating.setRating("Excellent");
        tireRating.setComment(new TireRatingComment("Tire outperformed all others in adverse conditions"));

        mudTire.setTireRating(tireRating);

        try {
            int prePersistCountBefore = TireInfoListener.PRE_PERSIST_COUNT;
            em.persist(mudTire);
            mudTireId = mudTire.getId();
            commitTransaction(em);
            int prePersistCountAfter = TireInfoListener.PRE_PERSIST_COUNT;

            int perPersistCountTotal = prePersistCountAfter - prePersistCountBefore;
            assertTrue("The pre persist method was called more than once (" + perPersistCountTotal + ")", perPersistCountTotal == 1);
        } catch (Exception exception ) {
            fail("Error persisting mud tire: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    public void testCreateNewRockTire() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        RockTireInfo rockTire = new RockTireInfo();
        rockTire.setName("Goodyear Mud Tracks");
        rockTire.setCode("AE-678");
        rockTire.setPressure(new Integer(100));
        rockTire.setGrip(RockTireInfo.Grip.SUPER);

        try {
            em.persist(rockTire);
            rockTireId = rockTire.getId();
            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting rock tire: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    public void testReadNewMudTire() {
        assertNotNull("The new mud tire info could not be read back.", createEntityManager().find(MudTireInfo.class, mudTireId));
    }

    public void testReadNewRockTire() {
        assertNotNull("The new rock tire info could not be read back.", createEntityManager().find(RockTireInfo.class, rockTireId));
    }

    public void testCreateNewJoinedTableSuperclass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SuperclassEntityJoined newObject = new SuperclassEntityJoined();
        newObject.setSuperclassAttribute("superclass-entity-joined");
        long persistedId = 0;

        try {
            em.persist(newObject);
            persistedId = newObject.getId();
            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting new object: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }

        try {
            em = createEntityManager();
            beginTransaction(em);

            SuperclassEntityJoined readObject = em.find(SuperclassEntityJoined.class, persistedId);

            assertNotNull("Object read back with pk " + persistedId + " must not be null", readObject);
            assertEquals("id", newObject.getId(), readObject.getId());
            assertEquals("superclassAttribute", newObject.getSuperclassAttribute(), readObject.getSuperclassAttribute());

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testCreateNewSingleTableSuperclass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SuperclassEntitySingleTable newObject = new SuperclassEntitySingleTable();
        newObject.setSuperclassAttribute("superclass-entity-singletable");
        long persistedId = 0;

        try {
            em.persist(newObject);
            persistedId = newObject.getId();
            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting new object: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }

        try {
            em = createEntityManager();
            beginTransaction(em);

            SuperclassEntitySingleTable readObject = em.find(SuperclassEntitySingleTable.class, persistedId);

            assertNotNull("Object read back with pk " + persistedId + " must not be null", readObject);
            assertEquals("id", newObject.getId(), readObject.getId());
            assertEquals("superclassAttribute", newObject.getSuperclassAttribute(), readObject.getSuperclassAttribute());

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testCreateNewTablePerClassSuperclass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SuperclassEntityTablePerClass newObject = new SuperclassEntityTablePerClass();
        newObject.setSuperclassAttribute("superclass-entity-tableperclass");
        long persistedId = 0;

        try {
            em.persist(newObject);
            persistedId = newObject.getId();
            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting new object: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }

        try {
            em = createEntityManager();
            beginTransaction(em);

            SuperclassEntityTablePerClass readObject = em.find(SuperclassEntityTablePerClass.class, persistedId);

            assertNotNull("Object read back with pk " + persistedId + " must not be null", readObject);
            assertEquals("id", newObject.getId(), readObject.getId());
            assertEquals("superclassAttribute", newObject.getSuperclassAttribute(), readObject.getSuperclassAttribute());

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testCreateNewJoinedTableSubclass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SubclassEntityJoined newObject = new SubclassEntityJoined();
        newObject.setSuperclassAttribute("superclass-entity-joined");
        newObject.setSubclassAttribute("subclass-entity-joined");
        long persistedId = 0;

        try {
            em.persist(newObject);
            persistedId = newObject.getId();
            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting new object: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }

        try {
            em = createEntityManager();
            beginTransaction(em);

            SubclassEntityJoined readObject = em.find(SubclassEntityJoined.class, persistedId);

            assertNotNull("Object read back with pk " + persistedId + " must not be null", readObject);
            assertEquals("id", newObject.getId(), readObject.getId());
            assertEquals("superclassAttribute", newObject.getSuperclassAttribute(), readObject.getSuperclassAttribute());
            assertEquals("subclassAttribute", newObject.getSubclassAttribute(), readObject.getSubclassAttribute());

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testCreateNewSingleTableSubclass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SubclassEntitySingleTable newObject = new SubclassEntitySingleTable();
        newObject.setSuperclassAttribute("superclass-entity-singletable");
        newObject.setSubclassAttribute("subclass-entity-singletable");
        long persistedId = 0;

        try {
            em.persist(newObject);
            persistedId = newObject.getId();
            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting new object: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }

        try {
            em = createEntityManager();
            beginTransaction(em);

            SubclassEntitySingleTable readObject = em.find(SubclassEntitySingleTable.class, persistedId);

            assertNotNull("Object read back with pk " + persistedId + " must not be null", readObject);
            assertEquals("id", newObject.getId(), readObject.getId());
            assertEquals("superclassAttribute", newObject.getSuperclassAttribute(), readObject.getSuperclassAttribute());
            assertEquals("subclassAttribute", newObject.getSubclassAttribute(), readObject.getSubclassAttribute());

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testCreateNewTablePerClassSubclass() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        SubclassEntityTablePerClass newObject = new SubclassEntityTablePerClass();
        newObject.setSuperclassAttribute("superclass-entity-tableperclass");
        newObject.setSubclassAttribute("subclass-entity-tableperclass");
        long persistedId = 0;

        try {
            em.persist(newObject);
            persistedId = newObject.getId();
            commitTransaction(em);
        } catch (Exception exception ) {
            fail("Error persisting new object: " + exception.getMessage());
        } finally {
            closeEntityManager(em);
        }

        try {
            em = createEntityManager();
            beginTransaction(em);

            SubclassEntityTablePerClass readObject = em.find(SubclassEntityTablePerClass.class, persistedId);

            assertNotNull("Object read back with pk " + persistedId + " must not be null", readObject);
            assertEquals("id", newObject.getId(), readObject.getId());
            assertEquals("superclassAttribute", newObject.getSuperclassAttribute(), readObject.getSuperclassAttribute());
            assertEquals("subclassAttribute", newObject.getSubclassAttribute(), readObject.getSubclassAttribute());

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

}
