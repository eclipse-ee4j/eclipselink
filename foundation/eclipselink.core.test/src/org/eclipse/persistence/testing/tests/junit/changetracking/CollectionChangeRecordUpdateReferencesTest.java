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

    private ObjectChangeSet newElementChangeSet(Element element, boolean isNew) {
        final ObjectChangeSet elementChangeSet = new ObjectChangeSet(element.getId(), elementDescriptor, element, mergeFromChangeSet, isNew);
        mergeFromChangeSet.addObjectChangeSetForIdentity(elementChangeSet, element);
        return elementChangeSet;
    }
}
