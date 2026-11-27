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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import org.eclipse.persistence.internal.sessions.DirectToFieldChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.OrderedChangeObject;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand;
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
        changeRecord.setMapping(session.getDescriptor(Owner.class).getMappingForAttributeName("elements"));
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

    @Test
    public void deferredTrackingAddOfCachedObjectIsMerged() {
        final Element element = cacheElement(50);
        addOperation(true, elementChanges(element, false), 0, false);

        merge(false);

        assertEquals(List.of(element), valueOfTarget);
        assertTrue(session.getIdentityMapAccessor().isValid(owner));
    }

    @Test
    public void attributeTrackingMissingTargetStopsAfterValidPrefix() {
        assertStopsAfterValidPrefix(false);
    }

    @Test
    public void deferredTrackingMissingTargetStopsAfterValidPrefix() {
        assertStopsAfterValidPrefix(true);
    }

    @Test
    public void attributeTrackingRemoteTrueAddDoesNotDuplicateExistingMember() {
        assertDuplicateSuppression(false);
    }

    @Test
    public void deferredTrackingRemoteTrueAddDoesNotDuplicateExistingMember() {
        assertDuplicateSuppression(true);
    }

    @Test
    public void localMergePreservesAddMoveAndRemove() {
        mergeManager = new MergeManager(session);
        final Element removed = cacheElement(60);
        final Element moved = cacheElement(61);
        final Element added = cacheElement(62);
        valueOfTarget.addAll(List.of(removed, moved));
        final ObjectChangeSet removedChanges = elementChanges(removed, false);
        final ObjectChangeSet movedChanges = elementChanges(moved, false);
        changeRecord.getRemoveObjectList().put(removedChanges, removedChanges);
        changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.REMOVE, 0, removedChanges));
        changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.REMOVE, 0, movedChanges));
        addOperation(false, elementChanges(added, false), 0, true);
        addOperation(false, movedChanges, 1, false);

        merge(false);

        assertEquals(List.of(added, moved), valueOfTarget);
        assertTrue(session.getIdentityMapAccessor().isValid(owner));
    }

    @Test
    public void attributeTrackingReceivingCommandKeepsMissingMemberOwnerInvalid() throws Exception {
        assertReceivingCommandInvalidates(false);
    }

    @Test
    public void deferredTrackingReceivingCommandKeepsMissingMemberOwnerInvalid() throws Exception {
        assertReceivingCommandInvalidates(true);
    }

    @Test
    public void attributeTrackingReceivingCommandCascadesNewMember() throws Exception {
        assertReceivingCommandCascades(false);
    }

    @Test
    public void deferredTrackingReceivingCommandCascadesNewMember() throws Exception {
        assertReceivingCommandCascades(true);
    }

    private void assertStopsAfterValidPrefix(boolean deferred) {
        final Element first = cacheElement(70);
        final Element last = cacheElement(72);
        addOperation(deferred, elementChanges(first, false), 0, false);
        addOperation(deferred, elementChanges(new Element(71), false), 1, false);
        addOperation(deferred, elementChanges(last, false), 2, false);

        merge(false);

        assertEquals(List.of(first), valueOfTarget);
        assertFalse(session.getIdentityMapAccessor().isValid(owner));
    }

    private void assertDuplicateSuppression(boolean deferred) {
        final Element existing = cacheElement(80);
        valueOfTarget.add(existing);
        addOperation(deferred, elementChanges(existing, false), 1, true);

        merge(false);

        assertEquals(List.of(existing), valueOfTarget);
        assertTrue(session.getIdentityMapAccessor().isValid(owner));
    }

    private void assertReceivingCommandInvalidates(boolean deferred) throws Exception {
        final Element missing = new Element(90);
        owner.getElements().add(missing);
        final ObjectChangeSet changes = elementChanges(missing, false);
        if (deferred) {
            changeRecord.getRemoveObjectList().put(changes, changes);
        } else {
            changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.REMOVE, null, changes));
        }
        addOperation(deferred, changes, 0, false);
        assertFalse(session.getDescriptor(Owner.class).getCacheInvalidationPolicy().shouldUpdateReadTimeOnUpdate());

        receivingCommand().executeWithSession(session);

        assertEquals(List.of(missing), owner.getElements());
        assertFalse("Full receiving finalization must preserve invalidation", session.getIdentityMapAccessor().isValid(owner));
    }

    private void assertReceivingCommandCascades(boolean deferred) throws Exception {
        final ObjectChangeSet changes = elementChanges(new Element(100), true);
        final DirectToFieldChangeRecord id = new DirectToFieldChangeRecord(changes);
        id.setAttribute("id");
        id.setNewValue(100);
        changes.addChange(id);
        addOperation(deferred, changes, 0, true);

        receivingCommand().executeWithSession(session);

        assertEquals(1, owner.getElements().size());
        final Element added = owner.getElements().get(0);
        assertEquals(Integer.valueOf(100), added.getId());
        assertSame(added, session.getIdentityMapAccessor().getFromIdentityMap(100, Element.class));
        assertTrue(session.getIdentityMapAccessor().isValid(owner));
    }

    private MergeChangeSetCommand receivingCommand() throws Exception {
        final ObjectChangeSet ownerChanges = (ObjectChangeSet) changeRecord.getOwner();
        ownerChanges.addChange(changeRecord);
        remoteChangeSet.addObjectChangeSet(ownerChanges, session, false);
        final MergeChangeSetCommand command = new MergeChangeSetCommand();
        command.setChangeSet(remoteChangeSet);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(command);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            final MergeChangeSetCommand received = (MergeChangeSetCommand) input.readObject();
            final ObjectChangeSet receivedOwner = received.getChangeSet(session).getAllChangeSets().keySet().stream()
                    .filter(changes -> changes.getClassType(session) == Owner.class).findFirst().orElseThrow();
            assertTrue("Serialized changes must not carry sender clones", receivedOwner.getUnitOfWorkClone() == null);
            return received;
        }
    }

    private Element cacheElement(int id) {
        final Element element = new Element(id);
        session.getIdentityMapAccessor().putInIdentityMap(element);
        return element;
    }

    private ObjectChangeSet elementChanges(Element element, boolean isNew) {
        return new ObjectChangeSet(element.getId(), elementDescriptor, element, remoteChangeSet, isNew);
    }

    private void addOperation(boolean deferred, ObjectChangeSet changes, int index, boolean isAddition) {
        if (isAddition) {
            changeRecord.getAddObjectList().put(changes, changes);
        }
        if (deferred) {
            changeRecord.getOrderedAddObjects().add(changes);
            changeRecord.getOrderedAddObjectIndices().put(changes, index);
        } else {
            changeRecord.getOrderedChangeObjectList().add(new OrderedChangeObject(CollectionChangeEvent.ADD, index, changes));
        }
    }

    private void merge(boolean cascade) {
        containerPolicy.mergeChanges(changeRecord, valueOfTarget, cascade, mergeManager, session, false);
    }

    private ObjectChangeSet newUncachedElementChangeSet(int id) {
        return new ObjectChangeSet(id, elementDescriptor, new Element(id), remoteChangeSet, true);
    }

    private void assertNoNullsAndOwnerInvalid() {
        assertFalse("target collection contains null: " + valueOfTarget, valueOfTarget.contains(null));
        assertFalse("owner should be invalidated", session.getIdentityMapAccessor().isValid(owner));
    }
}
