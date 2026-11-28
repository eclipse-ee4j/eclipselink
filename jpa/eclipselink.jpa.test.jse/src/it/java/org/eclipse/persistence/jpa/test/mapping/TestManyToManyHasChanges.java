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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.mapping.model.HasChangesOwner;
import org.eclipse.persistence.jpa.test.mapping.model.HasChangesTarget;
import org.eclipse.persistence.jpa.test.mapping.model.HasChangesUnrelated;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.changesets.UnitOfWorkChangeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
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

    private enum Inspection {
        NONE, HAS_CHANGES, CURRENT_CHANGES, REPEATED_HAS_CHANGES, REPEATED_CURRENT_CHANGES,
        ALTERNATING, FAIL_HAS_CHANGES, FAIL_CURRENT_CHANGES
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

    /** Verify that getCurrentChanges leaves the live relationship delta intact. */
    @Test
    public void testCurrentChanges() {
        changeRelationships(Inspection.CURRENT_CHANGES, PersistTiming.NONE, false, false);
    }

    /** Repeated hasChanges calls must not accumulate temporary identities. */
    @Test
    public void testRepeatedHasChanges() {
        changeRelationships(Inspection.REPEATED_HAS_CHANGES, PersistTiming.NONE, false, false);
    }

    /** Each getCurrentChanges result must be an independent snapshot. */
    @Test
    public void testRepeatedCurrentChanges() {
        changeRelationships(Inspection.REPEATED_CURRENT_CHANGES, PersistTiming.NONE, false, false);
    }

    /** Both inspection APIs must be safe to alternate in one transaction. */
    @Test
    public void testAlternatingInspections() {
        changeRelationships(Inspection.ALTERNATING, PersistTiming.NONE, false, false);
    }

    /** Flush must still consume the live changes correctly after inspection. */
    @Test
    public void testFlushAfterInspection() {
        changeRelationships(Inspection.ALTERNATING, PersistTiming.NONE, false, true);
    }

    /** hasChanges must preserve deferred work after replacing a collection. */
    @Test
    public void testDeferredHasChanges() {
        changeRelationships(Inspection.REPEATED_HAS_CHANGES, PersistTiming.NONE, true, false);
    }

    /** getCurrentChanges must preserve deferred work and detach its snapshot. */
    @Test
    public void testDeferredCurrentChanges() {
        changeRelationships(Inspection.REPEATED_CURRENT_CHANGES, PersistTiming.NONE, true, false);
    }

    /** Failure after temporary calculation must not corrupt the next commit. */
    @Test
    public void testHasChangesException() {
        changeRelationships(Inspection.FAIL_HAS_CHANGES, PersistTiming.NONE, false, false);
    }

    /** The exceptional path must also preserve pending deferred calculations. */
    @Test
    public void testCurrentChangesException() {
        changeRelationships(Inspection.FAIL_CURRENT_CHANGES, PersistTiming.NONE, true, false);
    }

    /** Run the original reproducer and its persist-timing controls. */
    private void changeRelationships(boolean checkChanges, PersistTiming persistTiming) {
        changeRelationships(checkChanges ? Inspection.HAS_CHANGES : Inspection.NONE, persistTiming, false, false);
    }

    /**
     * Apply the reporter's changes to both ends, then check the committed join
     * table and reload both sides with the shared cache disabled.
     */
    private void changeRelationships(Inspection inspection, PersistTiming persistTiming, boolean replaceCollection, boolean flush) {
        List<String> commitSql;
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                HasChangesOwner a1 = em.find(HasChangesOwner.class, 1L);
                HasChangesTarget b1 = em.find(HasChangesTarget.class, 1L);
                HasChangesTarget b2 = em.find(HasChangesTarget.class, 2L);
                HasChangesTarget b3 = em.find(HasChangesTarget.class, 3L);

                if (replaceCollection) {
                    a1.setTargets(new ArrayList<>(List.of(b1)));
                    b1.getOwners().add(a1);
                } else {
                    a1.addTarget(b1);
                }
                if (persistTiming == PersistTiming.BEFORE_HAS_CHANGES) {
                    em.persist(new HasChangesUnrelated(1));
                }
                ObjectChangeSet live = liveChanges(a1);
                CollectionChangeRecord liveRecord = (CollectionChangeRecord)live.getChangesForAttributeNamed("targets");
                Map<ObjectChangeSet, ObjectChangeSet> additions = new IdentityHashMap<>(liveRecord.getAddObjectList());
                Set<String> deferred = Set.copyOf(live.getDeferredSet());
                Object originalCollection = liveRecord.getOriginalCollection();
                Object latestCollection = liveRecord.getLatestCollection();
                List<UnitOfWorkChangeSet> snapshots = inspect(em.unwrap(UnitOfWorkImpl.class), inspection);
                assertSame(live, liveChanges(a1));
                assertSame(liveRecord, live.getChangesForAttributeNamed("targets"));
                assertSame(live, liveRecord.getOwner());
                assertEquals(additions, liveRecord.getAddObjectList());
                assertEquals(deferred, live.getDeferredSet());
                assertSame(originalCollection, liveRecord.getOriginalCollection());
                assertSame(latestCollection, liveRecord.getLatestCollection());
                assertEquals(replaceCollection, liveRecord.isDeferred());
                a1.addTarget(b2);
                if (inspection == Inspection.ALTERNATING) {
                    inspect(em.unwrap(UnitOfWorkImpl.class), inspection);
                }
                a1.addTarget(b3);
                a1.removeTarget(b1);
                assertEquals(List.of(b2, b3), a1.getTargets());
                assertTrue(b1.getOwners().isEmpty());
                assertEquals(List.of(a1), b2.getOwners());
                assertEquals(List.of(a1), b3.getOwners());

                for (UnitOfWorkChangeSet snapshot : snapshots) {
                    ObjectChangeSet owner = (ObjectChangeSet)snapshot.getObjectChangeSetForClone(a1);
                    CollectionChangeRecord record = (CollectionChangeRecord)owner.getChangesForAttributeNamed("targets");
                    assertNotSame(liveRecord, record);
                    assertSame(owner, record.getOwner());
                    assertEquals(1, record.getAddObjectList().size());
                    assertTrue(record.getAddObjectList().containsKey(snapshot.getObjectChangeSetForClone(b1)));
                    assertTrue(record.getRemoveObjectList().isEmpty());
                }

                if (persistTiming == PersistTiming.BEFORE_COMMIT) {
                    em.persist(new HasChangesUnrelated(1));
                }
                sql.clear();
                if (flush) {
                    em.flush();
                }
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

    /** Read the live listener state and require weaving so this test exercises attribute tracking. */
    private ObjectChangeSet liveChanges(HasChangesOwner owner) {
        assertTrue("This regression requires a woven entity", owner instanceof ChangeTracker);
        Object listener = ((ChangeTracker)owner)._persistence_getPropertyChangeListener();
        assertTrue("This regression requires attribute change tracking", listener instanceof AttributeChangeListener);
        return ((AttributeChangeListener)listener).getObjectChangeSet();
    }

    /** Inspect pending changes, retaining snapshots for checks after subsequent edits. */
    private List<UnitOfWorkChangeSet> inspect(UnitOfWorkImpl uow, Inspection inspection) {
        List<UnitOfWorkChangeSet> snapshots = new ArrayList<>();
        int count = switch (inspection) {
            case NONE -> 0;
            case REPEATED_HAS_CHANGES, REPEATED_CURRENT_CHANGES, ALTERNATING -> 3;
            default -> 1;
        };
        for (int i = 0; i < count; i++) {
            switch (inspection) {
                case HAS_CHANGES, REPEATED_HAS_CHANGES -> assertTrue(uow.hasChanges());
                case CURRENT_CHANGES, REPEATED_CURRENT_CHANGES -> snapshots.add(uow.getCurrentChanges());
                case ALTERNATING -> {
                    assertTrue(uow.hasChanges());
                    snapshots.add(uow.getCurrentChanges());
                }
                case FAIL_HAS_CHANGES, FAIL_CURRENT_CHANGES -> {
                    RuntimeException failure = new RuntimeException("Deliberate inspection failure");
                    SessionEventAdapter listener = new SessionEventAdapter() {
                        @Override
                        public void postCalculateUnitOfWorkChangeSet(SessionEvent event) {
                            throw failure;
                        }
                    };
                    uow.getEventManager().addListener(listener);
                    try {
                        assertSame(failure, assertThrows(RuntimeException.class, () -> {
                            if (inspection == Inspection.FAIL_HAS_CHANGES) {
                                uow.hasChanges();
                            } else {
                                uow.getCurrentChanges();
                            }
                        }));
                    } finally {
                        uow.getEventManager().removeListener(listener);
                    }
                }
                default -> throw new AssertionError(inspection);
            }
        }
        for (UnitOfWorkChangeSet snapshot : snapshots) {
            assertTrue(snapshot.hasChanges());
        }
        return snapshots;
    }
}
