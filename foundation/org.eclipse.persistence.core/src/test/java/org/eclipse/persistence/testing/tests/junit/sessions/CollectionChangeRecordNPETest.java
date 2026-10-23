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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.logging.SessionLog;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that verifies the null-guard fix in
 * {@link CollectionChangeRecord#addOrderedRemoveChange}.
 *
 * <p>Root cause: {@code OrderedListContainerPolicy.compareCollectionsForChange} can place
 * {@code null} values into {@code oldListIndexValue} when the backup collection contains
 * null elements (caused by cluster cache desync after a JPA lifecycle callback modifies
 * a {@code @OneToMany} collection after the changeset was already built). Those null values
 * flow into {@code addOrderedRemoveChange} via the {@code objectChanges} map, triggering
 * {@code NullPointerException} on {@code object.getClass()}.
 *
 * <p>A secondary NPE occurs when {@code session.getDescriptor(object.getClass())} returns
 * {@code null} for an object whose class is not registered in the session descriptor map.
 *
 * <p>Both paths are guarded by the fix: the entry is skipped and a {@code WARNING} is
 * emitted via {@link SessionLog} so the mapping and descriptor name appear in logs,
 * making the root cause diagnosable without a debugger or reproducer.
 */
public class CollectionChangeRecordNPETest {

    // -------------------------------------------------------------------------
    // NPE #1 — null object in objectChanges (cluster cache desync)
    // -------------------------------------------------------------------------

    /**
     * A null value in {@code objectChanges} at any index must be skipped with a
     * {@code WARNING} log, not crash. The valid entry at index 0 must still produce
     * a change set entry in {@code orderedRemoveObjects}.
     *
     * <p>Before the fix: {@code NullPointerException} on {@code object.getClass()} at index 1.
     */
    @Test
    public void nullObjectAtIndex_doesNotThrowNPE_andLogsWarning() {
        AbstractSession session = mock(AbstractSession.class);
        when(session.shouldLog(eq(SessionLog.WARNING), anyString())).thenReturn(true);

        ClassDescriptor descriptor = mock(ClassDescriptor.class);
        ObjectBuilder builder = mock(ObjectBuilder.class);
        ObjectChangeSet ocs = mock(ObjectChangeSet.class);
        when(session.getDescriptor(any(Class.class))).thenReturn(descriptor);
        when(descriptor.getObjectBuilder()).thenReturn(builder);
        when(builder.createObjectChangeSet(any(), any(), any())).thenReturn(ocs);

        UnitOfWorkChangeSet changeSet = mock(UnitOfWorkChangeSet.class);
        CollectionChangeRecord record = new CollectionChangeRecord();

        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, new Object());  // valid — processed normally
        objectChanges.put(1, null);           // null — simulates cluster cache desync

        record.addOrderedRemoveChange(Arrays.asList(0, 1), objectChanges, changeSet, session);

        assertNotNull("orderedRemoveObjects must not be null", record.getOrderedRemoveObjects());
        assertEquals("Index 0 must produce a change set entry", ocs, record.getOrderedRemoveObjects().get(0));
        assertNull("Index 1 (null object) must be skipped silently", record.getOrderedRemoveObjects().get(1));
        verify(session, atLeastOnce()).log(eq(SessionLog.WARNING), anyString(), anyString(), any(Object[].class));
    }

    /**
     * When logging is disabled, a null object must still be skipped without crash,
     * and {@code session.log()} must never be called.
     */
    @Test
    public void nullObjectAtIndex_withLoggingDisabled_doesNotThrowNPE_andDoesNotLog() {
        AbstractSession session = mock(AbstractSession.class);
        when(session.shouldLog(anyInt(), anyString())).thenReturn(false);

        UnitOfWorkChangeSet changeSet = mock(UnitOfWorkChangeSet.class);
        CollectionChangeRecord record = new CollectionChangeRecord();

        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, null);

        record.addOrderedRemoveChange(Collections.singletonList(0), objectChanges, changeSet, session);

        verify(session, never()).log(anyInt(), anyString(), anyString(), any(Object[].class));
        assertTrue("orderedRemoveObjects must be empty when all entries are null", record.getOrderedRemoveObjects() == null || record.getOrderedRemoveObjects().isEmpty());
    }

    // -------------------------------------------------------------------------
    // NPE #2 — no descriptor registered for the object's class
    // -------------------------------------------------------------------------

    /**
     * When {@code session.getDescriptor()} returns {@code null} for the object's class,
     * the entry must be skipped with a {@code WARNING} log, not crash.
     *
     * <p>Before the fix: {@code NullPointerException} on {@code null.getObjectBuilder()}.
     */
    @Test
    public void noDescriptorForClass_doesNotThrowNPE_andLogsWarning() {
        AbstractSession session = mock(AbstractSession.class);
        when(session.shouldLog(eq(SessionLog.WARNING), anyString())).thenReturn(true);
        when(session.getDescriptor(any(Class.class))).thenReturn(null);

        UnitOfWorkChangeSet changeSet = mock(UnitOfWorkChangeSet.class);
        CollectionChangeRecord record = new CollectionChangeRecord();

        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, new Object());

        record.addOrderedRemoveChange(Collections.singletonList(0), objectChanges, changeSet, session);

        assertNull("No change set entry must be added for an unregistered class", record.getOrderedRemoveObjects() == null ? null : record.getOrderedRemoveObjects().get(0));
        verify(session, atLeastOnce()).log(eq(SessionLog.WARNING), anyString(), anyString(), any(Object[].class));
    }

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    /**
     * Valid objects with registered descriptors must each produce an
     * {@link ObjectChangeSet} entry in {@code orderedRemoveObjects}.
     */
    @Test
    public void validObjects_producesChangeSetEntries() {
        AbstractSession session = mock(AbstractSession.class);
        when(session.shouldLog(anyInt(), anyString())).thenReturn(false);

        ClassDescriptor descriptor = mock(ClassDescriptor.class);
        ObjectBuilder builder = mock(ObjectBuilder.class);
        ObjectChangeSet ocs0 = mock(ObjectChangeSet.class);
        ObjectChangeSet ocs1 = mock(ObjectChangeSet.class);
        Object entity0 = new Object();
        Object entity1 = new Object();

        when(session.getDescriptor(any(Class.class))).thenReturn(descriptor);
        when(descriptor.getObjectBuilder()).thenReturn(builder);
        when(builder.createObjectChangeSet(eq(entity0), any(), any())).thenReturn(ocs0);
        when(builder.createObjectChangeSet(eq(entity1), any(), any())).thenReturn(ocs1);

        UnitOfWorkChangeSet changeSet = mock(UnitOfWorkChangeSet.class);
        CollectionChangeRecord record = new CollectionChangeRecord();

        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, entity0);
        objectChanges.put(1, entity1);

        record.addOrderedRemoveChange(Arrays.asList(0, 1), objectChanges, changeSet, session);

        assertNotNull(record.getOrderedRemoveObjects());
        assertEquals(ocs0, record.getOrderedRemoveObjects().get(0));
        assertEquals(ocs1, record.getOrderedRemoveObjects().get(1));
    }

    /**
     * An empty {@code indicesToRemove} list must produce no iterations and no side effects.
     */
    @Test
    public void emptyIndicesToRemove_isNoOp() {
        AbstractSession session = mock(AbstractSession.class);
        UnitOfWorkChangeSet changeSet = mock(UnitOfWorkChangeSet.class);
        CollectionChangeRecord record = new CollectionChangeRecord();

        record.addOrderedRemoveChange(Collections.emptyList(), Collections.emptyMap(), changeSet, session);

        verify(session, never()).getDescriptor(any());
        assertTrue("orderedRemoveObjects must be empty for an empty indicesToRemove list", record.getOrderedRemoveObjects() == null || record.getOrderedRemoveObjects().isEmpty());
    }

    /**
     * All null entries in {@code objectChanges}: every index must be skipped and
     * the resulting {@code orderedRemoveObjects} must remain empty.
     */
    @Test
    public void allNullObjects_allSkipped() {
        AbstractSession session = mock(AbstractSession.class);
        when(session.shouldLog(eq(SessionLog.WARNING), anyString())).thenReturn(true);

        UnitOfWorkChangeSet changeSet = mock(UnitOfWorkChangeSet.class);
        CollectionChangeRecord record = new CollectionChangeRecord();

        Map<Integer, Object> objectChanges = new HashMap<>();
        objectChanges.put(0, null);
        objectChanges.put(1, null);
        objectChanges.put(2, null);

        List<Integer> indicesToRemove = Arrays.asList(0, 1, 2);
        record.addOrderedRemoveChange(indicesToRemove, objectChanges, changeSet, session);

        assertTrue("orderedRemoveObjects must be empty when all entries are null", record.getOrderedRemoveObjects() == null || record.getOrderedRemoveObjects().isEmpty());
        verify(session, atLeastOnce()).log(eq(SessionLog.WARNING), anyString(), anyString(), any(Object[].class));
    }
}
