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

// Contributors:
//     11/13/2026 - Jonathan S. Fisher
//       - 3008: CollectionChangeRecord.updateReferences does not remap orderedChangeObjectList
package org.eclipse.persistence.testing.tests.junit.changetracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.OrderedChangeObject;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.testing.tests.junit.changetracking.OrderedListTestSession.Element;
import org.eclipse.persistence.testing.tests.junit.changetracking.OrderedListTestSession.Owner;
import org.junit.Before;
import org.junit.Test;

public class CollectionChangeRecordUpdateReferencesTest {

    private static final int ELEMENT_COUNT = 3;

    private DatabaseSessionImpl session;
    private ClassDescriptor elementDescriptor;
    private UnitOfWorkChangeSet mergeFromChangeSet;
    private UnitOfWorkChangeSet mergeToChangeSet;
    private CollectionChangeRecord changeRecord;

    @Before
    public void before() {
        session = OrderedListTestSession.create();
        elementDescriptor = session.getDescriptor(Element.class);
        mergeFromChangeSet = new UnitOfWorkChangeSet(session);
        mergeToChangeSet = new UnitOfWorkChangeSet(session);
        final ObjectChangeSet ownerChangeSet = new ObjectChangeSet(1, session.getDescriptor(Owner.class), new Owner(1), mergeFromChangeSet, false);
        changeRecord = new CollectionChangeRecord(ownerChangeSet);
        changeRecord.setAttribute("elements");
    }

    @Test
    public void orderedAddsReferenceSameChangeSetsAsAddObjectList() {
        final List<Element> elements = new ArrayList<>();
        for (int index = 0; index < ELEMENT_COUNT; index++) {
            final Element element = new Element(10 + index);
            elements.add(element);
            final ObjectChangeSet elementChangeSet = newElementChangeSet(element, true);
            changeRecord.getAddObjectList().put(elementChangeSet, elementChangeSet);
            changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.ADD, index, elementChangeSet, element));
        }

        changeRecord.updateReferences(mergeToChangeSet, mergeFromChangeSet);

        final List<OrderedChangeObject> orderedChanges = changeRecord.getOrderedChangeObjectList();
        assertEquals(ELEMENT_COUNT, orderedChanges.size());
        assertEquals(ELEMENT_COUNT, changeRecord.getAddObjectList().size());
        for (int index = 0; index < ELEMENT_COUNT; index++) {
            final OrderedChangeObject orderedChange = orderedChanges.get(index);
            final Element element = elements.get(index);
            assertTrue("ordered add " + index + " not found in addObjectList", changeRecord.getAddObjectList().containsKey(orderedChange.getChangeSet()));
            assertSame(mergeToChangeSet.getObjectChangeSetForClone(element), orderedChange.getChangeSet());
            assertEquals(CollectionChangeEvent.ADD, orderedChange.getChangeType());
            assertEquals(Integer.valueOf(index), orderedChange.getIndex());
            assertSame(element, orderedChange.getAddedOrRemovedObject());
        }
    }

    @Test
    public void orderedMovesReferenceMergeToChangeSets() {
        final Element element = new Element(20);
        final ObjectChangeSet elementChangeSet = newElementChangeSet(element, false);
        changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.REMOVE, 0, elementChangeSet, element));
        changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.ADD, 0, elementChangeSet, element));

        changeRecord.updateReferences(mergeToChangeSet, mergeFromChangeSet);

        final List<OrderedChangeObject> orderedChanges = changeRecord.getOrderedChangeObjectList();
        final Object mergeToElementChangeSet = mergeToChangeSet.getObjectChangeSetForClone(element);
        assertEquals(2, orderedChanges.size());
        assertTrue(changeRecord.getAddObjectList().isEmpty());
        assertTrue(changeRecord.getRemoveObjectList().isEmpty());
        assertSame(mergeToElementChangeSet, orderedChanges.get(0).getChangeSet());
        assertSame(mergeToElementChangeSet, orderedChanges.get(1).getChangeSet());
        assertEquals(CollectionChangeEvent.REMOVE, orderedChanges.get(0).getChangeType());
        assertEquals(CollectionChangeEvent.ADD, orderedChanges.get(1).getChangeType());
    }

    @Test
    public void orderedRemovalReusesExistingTargetAndPreservesNullIndex() {
        final Element element = new Element(30);
        final ObjectChangeSet original = newElementChangeSet(element, false);
        final ObjectChangeSet target = mergeToChangeSet.findOrIntegrateObjectChangeSet(original, mergeFromChangeSet);
        final OrderedChangeObject removal = new OrderedChangeObject(CollectionChangeEvent.REMOVE, null, original, element);
        changeRecord.getRemoveObjectList().put(original, original);
        changeRecord.getOrderedChangeObjectList().add(removal);

        changeRecord.updateReferences(mergeToChangeSet, mergeFromChangeSet);

        final OrderedChangeObject updated = changeRecord.getOrderedChangeObjectList().get(0);
        assertSame(target, updated.getChangeSet());
        assertTrue(changeRecord.getRemoveObjectList().containsKey(target));
        assertFalse(changeRecord.getRemoveObjectList().containsKey(original));
        assertNotSame(removal, updated);
        assertSame(original, removal.getChangeSet());
        assertEquals(CollectionChangeEvent.REMOVE, updated.getChangeType());
        assertNull(updated.getIndex());
        assertSame(element, updated.getAddedOrRemovedObject());
    }

    @Test
    public void mixedOperationsPreserveSequenceAndClassification() {
        final Element removed = new Element(40);
        final Element added = new Element(41);
        final Element moved = new Element(42);
        final ObjectChangeSet removeChanges = newElementChangeSet(removed, false);
        final ObjectChangeSet addChanges = newElementChangeSet(added, true);
        final ObjectChangeSet moveChanges = newElementChangeSet(moved, false);
        changeRecord.getRemoveObjectList().put(removeChanges, removeChanges);
        changeRecord.getAddObjectList().put(addChanges, addChanges);
        final List<OrderedChangeObject> original = List.of(
                new OrderedChangeObject(CollectionChangeEvent.REMOVE, 0, removeChanges, removed),
                new OrderedChangeObject(CollectionChangeEvent.REMOVE, 1, moveChanges, moved),
                new OrderedChangeObject(CollectionChangeEvent.ADD, 0, addChanges, added),
                new OrderedChangeObject(CollectionChangeEvent.ADD, 1, moveChanges, moved));
        changeRecord.getOrderedChangeObjectList().addAll(original);

        changeRecord.updateReferences(mergeToChangeSet, mergeFromChangeSet);

        assertEquals(original.size(), changeRecord.getOrderedChangeObjectList().size());
        for (int index = 0; index < original.size(); index++) {
            final OrderedChangeObject before = original.get(index);
            final OrderedChangeObject after = changeRecord.getOrderedChangeObjectList().get(index);
            assertSame(mergeToChangeSet.getObjectChangeSetForClone(before.getAddedOrRemovedObject()), after.getChangeSet());
            assertEquals(before.getChangeType(), after.getChangeType());
            assertEquals(before.getIndex(), after.getIndex());
            assertSame(before.getAddedOrRemovedObject(), after.getAddedOrRemovedObject());
        }
        final ObjectChangeSet targetMove = (ObjectChangeSet) mergeToChangeSet.getObjectChangeSetForClone(moved);
        assertFalse(changeRecord.getAddObjectList().containsKey(targetMove));
        assertFalse(changeRecord.getRemoveObjectList().containsKey(targetMove));
    }

    @Test
    public void deferredOrderedReferencesStillUseTargetIdentity() {
        final Element added = new Element(50);
        final Element removed = new Element(51);
        final ObjectChangeSet addChanges = newElementChangeSet(added, true);
        final ObjectChangeSet removeChanges = newElementChangeSet(removed, false);
        changeRecord.getAddObjectList().put(addChanges, addChanges);
        changeRecord.getRemoveObjectList().put(removeChanges, removeChanges);
        changeRecord.getOrderedAddObjects().add(addChanges);
        changeRecord.getOrderedAddObjectIndices().put(addChanges, 2);
        changeRecord.getOrderedRemoveObjects().put(0, removeChanges);
        changeRecord.getOrderedRemoveObjectIndices().add(0);

        changeRecord.updateReferences(mergeToChangeSet, mergeFromChangeSet);

        final ObjectChangeSet targetAdd = (ObjectChangeSet) mergeToChangeSet.getObjectChangeSetForClone(added);
        final ObjectChangeSet targetRemove = (ObjectChangeSet) mergeToChangeSet.getObjectChangeSetForClone(removed);
        assertSame(targetAdd, changeRecord.getOrderedAddObjects().get(0));
        assertEquals(Integer.valueOf(2), changeRecord.getOrderedAddObjectIndices().get(targetAdd));
        assertSame(targetRemove, changeRecord.getOrderedRemoveObject(0));
        assertTrue(changeRecord.getAddObjectList().containsKey(targetAdd));
        assertTrue(changeRecord.getRemoveObjectList().containsKey(targetRemove));
        assertEquals(List.of(0), changeRecord.getOrderedRemoveObjectIndices());
        assertTrue(changeRecord.getOrderedChangeObjectList().isEmpty());
    }

    @Test
    public void cumulativeFirstAndSubsequentRecordsKeepCanonicalReferences() {
        final ObjectChangeSet ownerChanges = (ObjectChangeSet) changeRecord.getOwner();
        final Object owner = ownerChanges.getUnitOfWorkClone();
        final Element first = new Element(60);
        final ObjectChangeSet firstChanges = newElementChangeSet(first, true);
        changeRecord.getAddObjectList().put(firstChanges, firstChanges);
        changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.ADD, 0, firstChanges, first));
        ownerChanges.addChange(changeRecord);

        final ObjectChangeSet cumulativeOwner = mergeToChangeSet.mergeObjectChanges(ownerChanges, mergeFromChangeSet);
        final CollectionChangeRecord cumulative = (CollectionChangeRecord) cumulativeOwner.getChangesForAttributeNamed("elements");
        assertSame(cumulativeOwner, cumulative.getOwner());
        assertSame(mergeToChangeSet.getObjectChangeSetForClone(first), cumulative.getOrderedChangeObjectList().get(0).getChangeSet());
        assertTrue(cumulative.getAddObjectList().containsKey(cumulative.getOrderedChangeObjectList().get(0).getChangeSet()));

        final ObjectChangeSet nextOwnerChanges = new ObjectChangeSet(1, session.getDescriptor(Owner.class), owner, mergeFromChangeSet, false);
        final CollectionChangeRecord nextRecord = new CollectionChangeRecord(nextOwnerChanges);
        nextRecord.setAttribute("elements");
        final Element second = new Element(61);
        final ObjectChangeSet secondChanges = newElementChangeSet(second, true);
        nextRecord.getAddObjectList().put(secondChanges, secondChanges);
        nextRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.ADD, 1, secondChanges, second));
        nextOwnerChanges.addChange(nextRecord);

        assertSame(cumulativeOwner, mergeToChangeSet.mergeObjectChanges(nextOwnerChanges, mergeFromChangeSet));
        assertEquals(2, cumulative.getOrderedChangeObjectList().size());
        for (OrderedChangeObject operation : cumulative.getOrderedChangeObjectList()) {
            assertTrue(cumulative.getAddObjectList().containsKey(operation.getChangeSet()));
        }
        assertSame(mergeToChangeSet.getObjectChangeSetForClone(second), cumulative.getOrderedChangeObjectList().get(1).getChangeSet());
    }

    @Test
    public void emptyRecordRemainsEmpty() {
        changeRecord.updateReferences(mergeToChangeSet, mergeFromChangeSet);

        assertTrue(changeRecord.getAddObjectList().isEmpty());
        assertTrue(changeRecord.getRemoveObjectList().isEmpty());
        assertTrue(changeRecord.getOrderedChangeObjectList().isEmpty());
    }

    private ObjectChangeSet newElementChangeSet(Element element, boolean isNew) {
        final ObjectChangeSet elementChangeSet = new ObjectChangeSet(element.getId(), elementDescriptor, element, mergeFromChangeSet, isNew);
        mergeFromChangeSet.addObjectChangeSetForIdentity(elementChangeSet, element);
        return elementChangeSet;
    }
}
