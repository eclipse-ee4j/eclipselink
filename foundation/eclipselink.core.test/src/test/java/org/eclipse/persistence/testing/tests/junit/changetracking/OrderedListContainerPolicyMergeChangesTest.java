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
//       - 3009: OrderedListContainerPolicy.mergeChanges adds null when the added object is not in the target cache
package org.eclipse.persistence.testing.tests.junit.changetracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.internal.queries.OrderedListContainerPolicy;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.OrderedChangeObject;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.testing.tests.junit.changetracking.OrderedListTestSession.Element;
import org.eclipse.persistence.testing.tests.junit.changetracking.OrderedListTestSession.Owner;
import org.junit.Before;
import org.junit.Test;

public class OrderedListContainerPolicyMergeChangesTest {

    private static final int ELEMENT_COUNT = 3;

    private DatabaseSessionImpl session;
    private ClassDescriptor elementDescriptor;
    private UnitOfWorkChangeSet remoteChangeSet;
    private Owner owner;
    private CollectionChangeRecord changeRecord;
    private MergeManager mergeManager;
    private OrderedListContainerPolicy containerPolicy;
    private List<Object> valueOfTarget;

    @Before
    public void before() {
        session = OrderedListTestSession.create();
        elementDescriptor = session.getDescriptor(Element.class);
        remoteChangeSet = new UnitOfWorkChangeSet(session);
        owner = new Owner(1);
        session.getIdentityMapAccessor().putInIdentityMap(owner);
        final ObjectChangeSet ownerChangeSet = new ObjectChangeSet(owner.getId(), session.getDescriptor(Owner.class), owner, remoteChangeSet, false);
        changeRecord = new CollectionChangeRecord(ownerChangeSet);
        changeRecord.setAttribute("elements");
        mergeManager = new MergeManager(session);
        mergeManager.mergeIntoDistributedCache();
        containerPolicy = new OrderedListContainerPolicy(Vector.class);
        valueOfTarget = new Vector<>();
    }

    @Test
    public void attributeTrackingMovedAddOfUncachedObjectInvalidatesOwner() {
        for (int index = 0; index < ELEMENT_COUNT; index++) {
            final ObjectChangeSet elementChangeSet = newUncachedElementChangeSet(10 + index);
            changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.ADD, index, elementChangeSet));
        }

        containerPolicy.mergeChanges(changeRecord, valueOfTarget, false, mergeManager, session, false);

        assertNoNullsAndOwnerInvalid();
    }

    @Test
    public void attributeTrackingNewAddOfUncachedObjectInvalidatesOwner() {
        for (int index = 0; index < ELEMENT_COUNT; index++) {
            final ObjectChangeSet elementChangeSet = newUncachedElementChangeSet(20 + index);
            changeRecord.getAddObjectList().put(elementChangeSet, elementChangeSet);
            changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.ADD, index, elementChangeSet));
        }

        containerPolicy.mergeChanges(changeRecord, valueOfTarget, false, mergeManager, session, false);

        assertNoNullsAndOwnerInvalid();
    }

    @Test
    public void deferredTrackingAddOfUncachedObjectInvalidatesOwner() {
        final List<ObjectChangeSet> orderedAddObjects = new ArrayList<>();
        final Map<ObjectChangeSet, Integer> orderedAddObjectIndices = new HashMap<>();
        for (int index = 0; index < ELEMENT_COUNT; index++) {
            final ObjectChangeSet elementChangeSet = newUncachedElementChangeSet(30 + index);
            orderedAddObjects.add(elementChangeSet);
            orderedAddObjectIndices.put(elementChangeSet, index);
        }
        changeRecord.setOrderedAddObjects(orderedAddObjects);
        changeRecord.setOrderedAddObjectIndices(orderedAddObjectIndices);

        containerPolicy.mergeChanges(changeRecord, valueOfTarget, false, mergeManager, session, false);

        assertNoNullsAndOwnerInvalid();
    }

    @Test
    public void attributeTrackingAddOfCachedObjectIsMerged() {
        final Element element = new Element(40);
        session.getIdentityMapAccessor().putInIdentityMap(element);
        final ObjectChangeSet elementChangeSet = new ObjectChangeSet(element.getId(), elementDescriptor, element, remoteChangeSet, false);
        changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.ADD, 0, elementChangeSet));

        containerPolicy.mergeChanges(changeRecord, valueOfTarget, false, mergeManager, session, false);

        assertEquals(1, valueOfTarget.size());
        assertEquals(element, valueOfTarget.get(0));
        assertTrue(session.getIdentityMapAccessor().isValid(owner));
    }

    private ObjectChangeSet newUncachedElementChangeSet(int id) {
        return new ObjectChangeSet(id, elementDescriptor, new Element(id), remoteChangeSet, true);
    }

    private void assertNoNullsAndOwnerInvalid() {
        assertFalse("target collection contains null: " + valueOfTarget, valueOfTarget.contains(null));
        assertFalse("owner should be invalidated", session.getIdentityMapAccessor().isValid(owner));
    }
}
