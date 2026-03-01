/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.jpa.persistence32;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import junit.framework.Test;
import org.eclipse.persistence.testing.models.jpa.persistence32.InnerEntitiesContainer;

/**
 * Verify jakarta.persistence 3.2 API - Entity and embeddable classes may now be static inner classes.
 */
public class StaticInnerEntitiesTest extends AbstractSuite {

    private static final InnerEntitiesContainer.InnerTeamEntity[] ENTITIES = new InnerEntitiesContainer.InnerTeamEntity[] {
            null, // Skip array index 0
            new InnerEntitiesContainer.InnerTeamEntity(1, "Name1", new InnerEntitiesContainer.InnerEmbeddableClass("aaaa1", "bbbb1")),
            new InnerEntitiesContainer.InnerTeamEntity(2, "Name2", new InnerEntitiesContainer.InnerEmbeddableClass("aaaa2", "bbbb2"))
    };

    public static Test suite() {
        return suite(
                "StaticInnerEntitiesTest",
                new StaticInnerEntitiesTest("testFindInnerTeamEntity"),
                new StaticInnerEntitiesTest("testQueryInnerTeamEntity")
        );
    }

    public StaticInnerEntitiesTest() {
    }

    public StaticInnerEntitiesTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "persistence32";
    }

    @Override
    protected void suiteSetUp() {
        super.suiteSetUp();
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction et = em.getTransaction();
            try {
                et.begin();
                for (int i = 1; i < ENTITIES.length; i++) {
                    em.persist(ENTITIES[i]);
                }
                et.commit();
            } catch (Exception e) {
                et.rollback();
                throw e;
            }
        }
    }

    public void testFindInnerTeamEntity() {
        InnerEntitiesContainer.InnerTeamEntity innerTeamEntity = emf.callInTransaction(em -> em
                .find(InnerEntitiesContainer.InnerTeamEntity.class, ENTITIES[1].getInnerPK()));
        assertEquals(ENTITIES[1], innerTeamEntity);
    }

    public void testQueryInnerTeamEntity() {
        InnerEntitiesContainer.InnerTeamEntity innerTeamEntity = emf.callInTransaction(em -> em
                .createQuery("SELECT e FROM InnerEntitiesContainer$InnerTeamEntity e WHERE e.innerPK=:innerPK",
                        InnerEntitiesContainer.InnerTeamEntity.class)
                .setParameter("innerPK", ENTITIES[1].getInnerPK())
                .getSingleResult());
        assertEquals(ENTITIES[1], innerTeamEntity);
    }
}
