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
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
//     04/21/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 5)
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
package org.eclipse.persistence.testing.tests.jpa.xml.advanced;

import jakarta.persistence.EntityManager;
import junit.framework.*;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.AdvancedMultiTenantTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Boss;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Capo;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Contract;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.MafiaFamily;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Mafioso;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Soldier;
import org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Underboss;

import java.util.List;

import static org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant.Mafioso.Gender.Female;

public class EntityMappingsMultitenant123Test extends EntityMappingsMultitenantJunitBase {

    public EntityMappingsMultitenant123Test() {
        super();
    }

    public EntityMappingsMultitenant123Test(String name) {
        super(name);
    }

    @Override
    public String getPersistenceUnitName() {
        return "extended-multi-tenant-123";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Extended Advanced Multitenant123Test Suite");
        suite.addTest(new EntityMappingsMultitenant123Test("testSetup"));
        suite.addTest(new EntityMappingsMultitenant123Test("testCreateMafiaFamily123"));
        suite.addTest(new EntityMappingsMultitenant123Test("testValidateMafiaFamily123"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    @Override
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(JUnitTestCase.getServerSession(getPersistenceUnitName()));
    }

    public void testCreateMafiaFamily123() {
        EntityManager em = createEntityManager(getPersistenceUnitName());

        try {
            beginTransaction(em);

            MafiaFamily family = new MafiaFamily();
            family.setName("Galore");
            family.setRevenue(4321.03);
            family.addTag("newtag1");

            Boss boss = new Boss();
            boss.setFirstName("Kitty");
            boss.setLastName("Galore");
            boss.setGender(Female);

            Underboss underboss = new Underboss();
            underboss.setFirstName("Number2");
            underboss.setLastName("Galore");
            underboss.setGender(Mafioso.Gender.Male);

            Capo capo1 = new Capo();
            capo1.setFirstName("Capo");
            capo1.setLastName("Galore");
            capo1.setGender(Mafioso.Gender.Male);

            Soldier soldier1 = new Soldier();
            soldier1.setFirstName("Grunt");
            soldier1.setLastName("Galore");
            soldier1.setGender(Mafioso.Gender.Male);

            Contract contract1 = new Contract();
            contract1.setDescription("Whack all other family boss");

            Contract contract2 = new Contract();
            contract2.setDescription("Pillage, pillage, pillage!");

            // Populate the relationships.
            contract1.addSoldier(soldier1);

            contract2.addSoldier(soldier1);

            boss.setUnderboss(underboss);

            capo1.setUnderboss(underboss);

            capo1.addSoldier(soldier1);

            underboss.addCapo(capo1);

            family.addMafioso(boss);
            family.addMafioso(underboss);

            family.addMafioso(capo1);

            family.addMafioso(soldier1);

            // Will cascade through the whole family.
            em.persist(family);
            family123 = family.getId();
            family123Mafiosos.add(boss.getId());
            family123Mafiosos.add(underboss.getId());
            family123Mafiosos.add(capo1.getId());
            family123Mafiosos.add(soldier1.getId());
            family123Contracts.add(contract1.getId());
            family123Contracts.add(contract2.getId());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    public void testValidateMafiaFamily123() {
        EntityManager em = createEntityManager(getPersistenceUnitName());

        try {
            clearCache(getPersistenceUnitName());
            em.clear();

            MafiaFamily family =  em.find(MafiaFamily.class, family123);
            assertNotNull("The Mafia Family with id: " + family123 + ", was not found", family);
            assertEquals("The Mafia Family had an incorrect number of tags [" + family.getTags().size() + "], expected [1]", 1, family.getTags().size());
            assertNull("The Mafia Family with id: " + family707 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family707));
            assertNull("The Mafia Family with id: " + family007 + ", was found (when it should not have been)", em.find(MafiaFamily.class, family007));
            assertFalse("No mafiosos part of 123 family", family.getMafiosos().isEmpty());

            // See if we can find any members of the other family.
            for (Integer id : family707Mafiosos) {
                assertNull("Found family 707 mafioso.", em.find(Mafioso.class, id));
            }

            // Try a native sql query. Should get an exception since the
            // eclipselink.jdbc.allow-native-sql-queries property is set to
            // false for this PU.
            boolean exceptionCaught = false;
            try {
                @SuppressWarnings({"unchecked"})
                List<MafiaFamily> mafiaFamilies = em.createNativeQuery("select * from XML_MAFIA_FAMILY").getResultList();
            } catch (Exception e) {
                exceptionCaught = true;
            }

            assertTrue("No exception was caught from issuing a native query.", exceptionCaught);

//            // Query directly for the boss from the other family.
//            Boss otherBoss = em.find(Boss.class, family707Mafiosos.get(0));
//            assertNull("Found family 707 boss.", otherBoss);
//
//            // See if we can find any contracts of the other family.
//            for (Integer id : family707Contracts) {
//                assertNull("Found family 707 contract. ", em.find(Contract.class, id));
//            }

            // Try a select named query
            List<MafiaFamily> families = em.createNamedQuery("findJPQLXMLMafiaFamilies", MafiaFamily.class).getResultList();
            assertEquals("Incorrect number of families were returned [" + families.size() + "], expected [1]", 1, families.size());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }
}

