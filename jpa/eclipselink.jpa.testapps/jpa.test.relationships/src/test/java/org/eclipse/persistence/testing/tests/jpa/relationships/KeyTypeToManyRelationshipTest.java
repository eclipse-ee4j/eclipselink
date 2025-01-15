/*
 * Copyright (c) 2014, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2014, 2025 IBM Corporation.
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
//     06/25/2014-2.5.2 Rick Curtis
//       - 438177: Test M2M map
package org.eclipse.persistence.testing.tests.jpa.relationships;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.MtoMEntityA;
import org.eclipse.persistence.testing.models.jpa.relationships.MtoMEntityB;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;

import java.util.HashMap;

public class KeyTypeToManyRelationshipTest extends JUnitTestCase{

    public KeyTypeToManyRelationshipTest() {

    }

    public KeyTypeToManyRelationshipTest(String name) {
        super(name);
    }

    public void testSetup () {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("KeyTypeToManyRelationshipTest");

        suite.addTest(new KeyTypeToManyRelationshipTest("testSetup"));
        suite.addTest(new KeyTypeToManyRelationshipTest("testManyToManyMapTypeWithDefaultKey"));
        suite.addTest(new KeyTypeToManyRelationshipTest("testManyToManyMapTypeWithDefaultJointTable"));

        return suite;
    }

    public void testManyToManyMapTypeWithDefaultJointTable() {
        HashMap<String, String> emProps = new HashMap<>();
        emProps.put("eclipselink.cache.shared.default", "false");

        EntityManager em = createEntityManager(emProps);

        beginTransaction(em);
        try {
            MtoMEntityA entityA = new MtoMEntityA();
            entityA.setId(2);
            entityA.setName("Entity A");
            em.persist(entityA);

            MtoMEntityB entityB = new MtoMEntityB();
            entityB.setId(2);
            entityB.setName("Entity B");
            em.persist(entityB);

            entityA.getEntityBDefault().put(2, entityB);

            commitTransaction(em);

            beginTransaction(em);

            em.clear();

            MtoMEntityA entityA_find = em.find(MtoMEntityA.class, 2);
            assertNotSame(entityA, entityA_find);
            assertNotNull(entityA_find);
            assertNotNull(entityA_find.getEntityBDefault());
            assertEquals(1, entityA_find.getEntityBDefault().size());

            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testManyToManyMapTypeWithDefaultKey() {
        HashMap<String, String> emProps = new HashMap<>();
        emProps.put("eclipselink.cache.shared.default", "false");

        EntityManager em = createEntityManager(emProps);

        beginTransaction(em);
        try {
            MtoMEntityA entityA = new MtoMEntityA();
            entityA.setId(1);
            entityA.setName("Entity A");
            em.persist(entityA);

            MtoMEntityB entityB = new MtoMEntityB();
            entityB.setId(1);
            entityB.setName("Entity B");
            em.persist(entityB);

            entityA.getEntityB().put(1, entityB);

            commitTransaction(em);

            beginTransaction(em);

            em.clear();

            MtoMEntityA entityA_find = em.find(MtoMEntityA.class, 1);
            assertNotSame(entityA, entityA_find);
            assertNotNull(entityA_find);
            assertNotNull(entityA_find.getEntityB());
            assertEquals(1, entityA_find.getEntityB().size());

            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
}
