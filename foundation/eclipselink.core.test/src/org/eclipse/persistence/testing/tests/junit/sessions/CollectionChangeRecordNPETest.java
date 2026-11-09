/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.junit.sessions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests that verify the null-guard in
 * {@link CollectionChangeRecord#addOrderedRemoveChange}.
 *
 * <p>Root cause: {@code OrderedListContainerPolicy.compareCollectionsForChange} can place
 * {@code null} values into {@code oldListIndexValue} when the backup collection contains
 * null elements (for example after a cluster cache desync caused by a JPA lifecycle callback
 * modifying a {@code @OneToMany} collection after the change set was already built). Those
 * null values flow into {@code addOrderedRemoveChange} through the {@code objectChanges} map,
 * triggering a {@code NullPointerException} on {@code object.getClass()}.
 *
 * <p>A secondary NPE occurs when {@code session.getDescriptor(object.getClass())} returns
 * {@code null} for an object whose class is not registered in the session.
 *
 * <p>Both cases must be skipped and reported with a {@code WARNING} through {@link SessionLog}.
 * The tests use a real (not logged-in) session with an empty {@link Project} (no connection is made) and a log that
 * records the entries it receives.
 */
public class CollectionChangeRecordNPETest {

    /** Session log that records every entry instead of writing it anywhere. */
    private static class RecordingSessionLog extends AbstractSessionLog {
        final List<SessionLogEntry> entries = new ArrayList<>();

        @Override
        public void log(SessionLogEntry sessionLogEntry) {
            entries.add(sessionLogEntry);
        }
    }

    private RecordingSessionLog log;
    private AbstractSession session;
    private UnitOfWorkChangeSet changeSet;
    private CollectionChangeRecord record;

    @Before
    public void setUp() {
        log = new RecordingSessionLog();
        log.setLevel(SessionLog.WARNING);
        session = new DatabaseSessionImpl(new Project(new DatabaseLogin()));
        session.setSessionLog(log);
        changeSet = new UnitOfWorkChangeSet();
        record = new CollectionChangeRecord();
    }

    private boolean orderedRemoveObjectsIsEmpty() {
        return record.getOrderedRemoveObjects() == null || record.getOrderedRemoveObjects().isEmpty();
    }

    /**
     * A null value in {@code objectChanges} must be skipped with a {@code WARNING}, not crash.
     * Before the fix: {@code NullPointerException} on {@code object.getClass()}.
     */
    @Test
    public void nullObjectAtIndex_doesNotThrowNPE_andLogsWarning() {
        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, null);

        record.addOrderedRemoveChange(Collections.singletonList(0), objectChanges, changeSet, session);

        assertTrue("orderedRemoveObjects must be empty when the only entry is null", orderedRemoveObjectsIsEmpty());
        assertEquals("Exactly one warning must be logged", 1, log.entries.size());
        assertEquals(SessionLog.WARNING, log.entries.get(0).getLevel());
        assertEquals(SessionLog.TRANSACTION, log.entries.get(0).getNameSpace());
    }

    /**
     * When logging is disabled, a null object must still be skipped without crashing and
     * nothing must be logged.
     */
    @Test
    public void nullObjectAtIndex_withLoggingDisabled_doesNotThrowNPE_andDoesNotLog() {
        log.setLevel(SessionLog.OFF);
        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, null);

        record.addOrderedRemoveChange(Collections.singletonList(0), objectChanges, changeSet, session);

        assertTrue("orderedRemoveObjects must be empty when all entries are null", orderedRemoveObjectsIsEmpty());
        assertTrue("Nothing must be logged when logging is disabled", log.entries.isEmpty());
    }

    /**
     * When no descriptor is registered for the object's class the entry must be skipped with a
     * {@code WARNING}, not crash.
     * Before the fix: {@code NullPointerException} on {@code null.getObjectBuilder()}.
     */
    @Test
    public void noDescriptorForClass_doesNotThrowNPE_andLogsWarning() {
        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, new Object());

        record.addOrderedRemoveChange(Collections.singletonList(0), objectChanges, changeSet, session);

        assertTrue("No change set entry must be added for an unregistered class", orderedRemoveObjectsIsEmpty());
        assertEquals("Exactly one warning must be logged", 1, log.entries.size());
        assertEquals(SessionLog.WARNING, log.entries.get(0).getLevel());
    }

    /**
     * Every null entry must be skipped and reported; the removal indices are still recorded.
     */
    @Test
    public void allNullObjects_allSkipped() {
        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, null);
        objectChanges.put(1, null);
        objectChanges.put(2, null);
        List<Integer> indicesToRemove = Arrays.asList(0, 1, 2);

        record.addOrderedRemoveChange(indicesToRemove, objectChanges, changeSet, session);

        assertTrue("orderedRemoveObjects must be empty when all entries are null", orderedRemoveObjectsIsEmpty());
        assertEquals("One warning per skipped entry", 3, log.entries.size());
        assertEquals(indicesToRemove, record.getOrderedRemoveObjectIndices());
    }

    /**
     * An empty {@code indicesToRemove} list must produce no iterations and no log output.
     */
    @Test
    public void emptyIndicesToRemove_isNoOp() {
        record.addOrderedRemoveChange(Collections.<Integer>emptyList(), Collections.emptyMap(), changeSet, session);

        assertTrue("orderedRemoveObjects must be empty for an empty indicesToRemove list", orderedRemoveObjectsIsEmpty());
        assertTrue(log.entries.isEmpty());
    }
}
