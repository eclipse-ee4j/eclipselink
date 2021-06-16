/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     06/25/2014-2.5.2 Rick Curtis
//       - 438177: Test M2M map
package org.eclipse.persistence.testing.tests.jpa.relationships;

import java.util.HashMap;

import jakarta.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.MtoMEntityA;
import org.eclipse.persistence.testing.models.jpa.relationships.MtoMEntityB;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;

public class TestKeyTypeToManyRelationship extends JUnitTestCase{

    public TestKeyTypeToManyRelationship() {

    }

    public TestKeyTypeToManyRelationship(String name) {
        super(name);
    }

    public void testSetup () {
        new RelationshipsTableManager().replaceTables(JUnitTestCase.getServerSession());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("TestKeyTypeToManyRelationship");

        suite.addTest(new TestKeyTypeToManyRelationship("testSetup"));
        suite.addTest(new TestKeyTypeToManyRelationship("testManyToManyMapTypeWithDefaultKey"));
        suite.addTest(new TestKeyTypeToManyRelationship("testManyToManyMapTypeWithDefaultJointTable"));

        return suite;
    }

    public void testManyToManyMapTypeWithDefaultJointTable() throws Exception {
        HashMap<String, String> emProps = new HashMap<String, String>();
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

    public void testManyToManyMapTypeWithDefaultKey() throws Exception {
        HashMap<String, String> emProps = new HashMap<String, String>();
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
