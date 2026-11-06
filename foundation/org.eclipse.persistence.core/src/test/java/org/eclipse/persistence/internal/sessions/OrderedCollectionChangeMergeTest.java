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

package org.eclipse.persistence.internal.sessions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Verify that composing deltas has the same result as applying them successively. */
class OrderedCollectionChangeMergeTest {
    private final AbstractSession session = new DatabaseSessionImpl(new DatabaseLogin());
    private final RelationalDescriptor descriptor = new RelationalDescriptor();

    OrderedCollectionChangeMergeTest() {
        descriptor.setJavaClass(Object.class);
    }

    @Test
    void additionsRemovalsAndMoves() {
        verify(List.of("CE", "ACE", "ABCDE", "BDE", "DBE", ""));
        verify(List.of("ABC", "AC", "ABC", "CAB", "ABC"));
        verify(List.of("", "A", "AB", "B", "BC", "C"));
    }

    @Test
    void generatedSequencesMatchSuccessiveApplication() {
        Random random = new Random(2899);
        for (int sample = 0; sample < 1000; sample++) {
            List<String> states = new ArrayList<>();
            for (int step = 0; step < 6; step++) {
                List<String> values = new ArrayList<>(List.of("A", "B", "C", "D", "E"));
                Collections.shuffle(values, random);
                states.add(String.join("", values.subList(0, random.nextInt(values.size() + 1))));
            }
            verify(states);
        }
    }

    @Test
    void preservesOrderRepairFlag() {
        UnitOfWorkChangeSet destination = new UnitOfWorkChangeSet(session);
        UnitOfWorkChangeSet source = new UnitOfWorkChangeSet(session);
        CollectionChangeRecord combined = delta("A", "AB", destination);
        CollectionChangeRecord next = delta("AB", "ABC", source);
        next.setOrderHasBeenRepaired(true);
        combined.mergeRecord(next, destination, source);
        assertTrue(combined.orderHasBeenRepaired());
        combined.mergeRecord(delta("ABC", "ABC", source), destination, source);
        assertTrue(combined.orderHasBeenRepaired());
    }

    private void verify(List<String> states) {
        UnitOfWorkChangeSet destination = new UnitOfWorkChangeSet(session);
        CollectionChangeRecord combined = delta(states.get(0), states.get(1), destination);
        for (int step = 2; step < states.size(); step++) {
            UnitOfWorkChangeSet source = new UnitOfWorkChangeSet(session);
            CollectionChangeRecord next = delta(states.get(step - 1), states.get(step), source);
            List<ObjectChangeSet> sourceAdds = new ArrayList<>(next.getOrderedAddObjects());
            combined.mergeRecord(next, destination, source);
            assertEquals(sourceAdds, next.getOrderedAddObjects(), "Do not rewrite incoming identities");
            List<String> actual = new ArrayList<>(names(states.get(0)));
            List<Integer> removals = combined.getOrderedRemoveObjectIndices();
            for (int i = removals.size() - 1; i >= 0; i--) {
                int index = removals.get(i);
                assertEquals(combined.getOrderedRemoveObjects().get(index).getId(), actual.remove(index), states.toString());
            }
            for (ObjectChangeSet addition : combined.getOrderedAddObjects()) {
                assertSame(destination.getObjectChangeSetForClone(addition.getUnitOfWorkClone()), addition);
                actual.add(combined.getOrderedAddObjectIndex(addition), (String)addition.getId());
            }
            for (ObjectChangeSet removal : combined.getOrderedRemoveObjects().values()) {
                assertSame(destination.getObjectChangeSetForClone(removal.getUnitOfWorkClone()), removal);
            }
            assertEquals(names(states.get(step)), actual, states + " at step " + step);
            Set<String> expectedAdds = new HashSet<>(names(states.get(step)));
            expectedAdds.removeAll(names(states.get(0)));
            Set<String> expectedRemoves = new HashSet<>(names(states.get(0)));
            expectedRemoves.removeAll(names(states.get(step)));
            assertEquals(expectedAdds, ids(combined.getAddObjectList().keySet()));
            assertEquals(expectedRemoves, ids(combined.getRemoveObjectList().keySet()));
        }
    }

    private Set<String> ids(Set<ObjectChangeSet> changes) {
        Set<String> ids = new HashSet<>();
        for (ObjectChangeSet change : changes) {
            ids.add((String)change.getId());
        }
        return ids;
    }

    private List<String> names(String text) {
        return text.chars().mapToObj(c -> String.valueOf((char)c).intern()).toList();
    }

    /** Build a valid delta by retaining a common subsequence of the two lists. */
    private CollectionChangeRecord delta(String before, String after, UnitOfWorkChangeSet changes) {
        CollectionChangeRecord record = new CollectionChangeRecord(new ObjectChangeSet("owner", descriptor, this, changes, false));
        Set<String> retained = new HashSet<>();
        int lastIndex = -1;
        for (String name : names(before)) {
            int index = after.indexOf(name);
            if (index > lastIndex) {
                retained.add(name);
                lastIndex = index;
            }
        }
        for (int i = 0; i < before.length(); i++) {
            String name = names(before).get(i);
            ObjectChangeSet change = identity(name, changes);
            if (!retained.contains(name)) {
                record.getOrderedRemoveObjectIndices().add(i);
                record.getOrderedRemoveObjects().put(i, change);
            }
            if (!after.contains(name)) {
                record.getRemoveObjectList().put(change, change);
            }
        }
        for (int i = 0; i < after.length(); i++) {
            String name = names(after).get(i);
            ObjectChangeSet change = identity(name, changes);
            if (!retained.contains(name)) {
                record.getOrderedAddObjects().add(change);
                record.getOrderedAddObjectIndices().put(change, i);
            }
            if (!before.contains(name)) {
                record.getAddObjectList().put(change, change);
            }
        }
        return record;
    }

    private ObjectChangeSet identity(String name, UnitOfWorkChangeSet changes) {
        ObjectChangeSet change = (ObjectChangeSet)changes.getObjectChangeSetForClone(name);
        if (change == null) {
            change = new ObjectChangeSet(name, descriptor, name, changes, false);
            changes.addObjectChangeSetForIdentity(change, name);
        }
        return change;
    }
}
