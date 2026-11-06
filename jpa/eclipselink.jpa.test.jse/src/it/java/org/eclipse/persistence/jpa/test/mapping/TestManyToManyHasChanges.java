/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.mapping;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.mapping.model.HasChangesOwner;
import org.eclipse.persistence.jpa.test.mapping.model.HasChangesTarget;
import org.eclipse.persistence.jpa.test.mapping.model.HasChangesUnrelated;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Reproducer for issue #2967: checking for changes between adding and removing
 * a many-to-many relationship must not cause the removed link to be committed.
 */
@RunWith(EmfRunner.class)
public class TestManyToManyHasChanges {

    private enum PersistTiming {
        NONE, BEFORE_HAS_CHANGES, BEFORE_COMMIT
    }

    @Emf(name = "manyToManyHasChangesEMF", createTables = DDLGen.DROP_CREATE,
            classes = {HasChangesOwner.class, HasChangesTarget.class, HasChangesUnrelated.class},
            properties = @Property(name = PersistenceUnitProperties.CACHE_SHARED_DEFAULT, value = "false"))
    private EntityManagerFactory emf;

    @SQLCallListener(name = "manyToManyHasChangesEMF")
    private List<String> sql;

    /**
     * Store A1 and B1, B2, B3 without links, so the test modifies existing rows.
     */
    @Before
    public void setup() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(new HasChangesOwner(1));
                for (long id = 1; id <= 3; id++) {
                    em.persist(new HasChangesTarget(id));
                }
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    /**
     * Delete the join rows first, including any incorrectly committed B1 link.
     */
    @After
    public void cleanup() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.createNativeQuery("DELETE FROM HC_OWNER_TARGET").executeUpdate();
                em.createQuery("DELETE FROM HasChangesOwner").executeUpdate();
                em.createQuery("DELETE FROM HasChangesTarget").executeUpdate();
                em.createQuery("DELETE FROM HasChangesUnrelated").executeUpdate();
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    /**
     * Reproduce add B1, hasChanges, add B2 and B3, remove B1, then commit.
     */
    @Test
    public void testRemovedRelationshipAfterHasChanges() {
        changeRelationships(true, PersistTiming.NONE);
    }

    /**
     * Control: the same changes must work when hasChanges is not called.
     */
    @Test
    public void testRemovedRelationshipWithoutHasChanges() {
        changeRelationships(false, PersistTiming.NONE);
    }

    /**
     * Check whether persisting unrelated C after hasChanges prevents the defect.
     */
    @Test
    public void testRemovedRelationshipAfterHasChangesAndPersist() {
        changeRelationships(true, PersistTiming.BEFORE_COMMIT);
    }

    /**
     * Control: persist unrelated C before hasChanges to distinguish the timing
     * of the workaround mentioned in the report.
     */
    @Test
    public void testRemovedRelationshipWithPersistBeforeHasChanges() {
        changeRelationships(true, PersistTiming.BEFORE_HAS_CHANGES);
    }

    /**
     * Apply the reporter's changes to both ends, then check the committed join
     * table and reload both sides with the shared cache disabled.
     */
    private void changeRelationships(boolean checkChanges, PersistTiming persistTiming) {
        List<String> commitSql;
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                HasChangesOwner a1 = em.find(HasChangesOwner.class, 1L);
                HasChangesTarget b1 = em.find(HasChangesTarget.class, 1L);
                HasChangesTarget b2 = em.find(HasChangesTarget.class, 2L);
                HasChangesTarget b3 = em.find(HasChangesTarget.class, 3L);

                a1.addTarget(b1);
                if (persistTiming == PersistTiming.BEFORE_HAS_CHANGES) {
                    em.persist(new HasChangesUnrelated(1));
                }
                if (checkChanges) {
                    assertTrue("The newly added B1 link must be detected", em.unwrap(UnitOfWork.class).hasChanges());
                }
                a1.addTarget(b2);
                a1.addTarget(b3);
                a1.removeTarget(b1);
                assertEquals(List.of(b2, b3), a1.getTargets());
                assertTrue(b1.getOwners().isEmpty());
                assertEquals(List.of(a1), b2.getOwners());
                assertEquals(List.of(a1), b3.getOwners());

                if (persistTiming == PersistTiming.BEFORE_COMMIT) {
                    em.persist(new HasChangesUnrelated(1));
                }
                sql.clear();
                em.getTransaction().commit();
                commitSql = List.copyOf(sql);
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }

        try (EntityManager em = emf.createEntityManager()) {
            List<?> rows = em.createNativeQuery(
                    "SELECT TARGET_ID FROM HC_OWNER_TARGET WHERE OWNER_ID = 1 ORDER BY TARGET_ID").getResultList();
            List<Long> targetIds = rows.stream().map(id -> ((Number)id).longValue()).toList();
            assertEquals("Only B2 and B3 should be linked after commit. Commit SQL: " + commitSql,
                    List.of(2L, 3L), targetIds);

            HasChangesOwner a1 = em.find(HasChangesOwner.class, 1L);
            assertEquals(List.of(2L, 3L), a1.getTargets().stream().map(HasChangesTarget::getId).sorted().toList());
            assertTrue(em.find(HasChangesTarget.class, 1L).getOwners().isEmpty());
            assertEquals(List.of(a1), em.find(HasChangesTarget.class, 2L).getOwners());
            assertEquals(List.of(a1), em.find(HasChangesTarget.class, 3L).getOwners());
        }
    }
}
