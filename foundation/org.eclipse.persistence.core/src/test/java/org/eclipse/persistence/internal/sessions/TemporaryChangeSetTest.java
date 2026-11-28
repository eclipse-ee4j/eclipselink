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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.queries.ListContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests isolation of mutable bookkeeping during temporary change calculation. */
class TemporaryChangeSetTest {

    private final AbstractSession session = new DatabaseSessionImpl(new DatabaseLogin());
    private final UnitOfWorkChangeSet live = new UnitOfWorkChangeSet(session);
    private final UnitOfWorkChangeSet temporary = new UnitOfWorkChangeSet(session, true);
    private final ObjectChangeSet owner = objectChanges(false);
    private final ObjectChangeSet target = objectChanges(false);

    /** The original identity must still cancel an addition after temporary merging. */
    @Test
    void collectionRemovalCancelsLiveAddition() {
        CollectionChangeRecord record = new CollectionChangeRecord(owner);
        record.getAddObjectList().put(target, target);
        CollectionChangeRecord copy = merge(record);
        ObjectChangeSet copiedTarget = (ObjectChangeSet)temporary.getObjectChangeSetForClone(target.getUnitOfWorkClone());
        assertNotSame(record, copy);
        assertSame(owner, record.getOwner());
        assertSame(temporary.getObjectChangeSetForClone(owner.getUnitOfWorkClone()), copy.getOwner());
        assertNotSame(target, copiedTarget);
        assertTrue(copy.getAddObjectList().containsKey(copiedTarget));
        new ListContainerPolicy(ArrayList.class).recordRemoveFromCollectionInChangeRecord(target, record);
        assertTrue(record.getAddObjectList().isEmpty());
        assertTrue(record.getRemoveObjectList().isEmpty());
        assertEquals(1, copy.getAddObjectList().size());
    }

    /** Duplicate additions and ordered operations must use one destination identity. */
    @Test
    void orderedCollectionBookkeepingIsIndependent() {
        CollectionChangeRecord record = new CollectionChangeRecord(owner);
        record.getAddObjectList().put(target, target);
        record.getAddOverFlow().add(target);
        record.getOrderedAddObjects().add(target);
        record.getOrderedAddObjectIndices().put(target, 1);
        record.getOrderedRemoveObjects().put(0, target);
        record.getOrderedRemoveObjectIndices().add(0);
        OrderedChangeObject operation = new OrderedChangeObject(1, 1, target, target.getUnitOfWorkClone());
        record.getOrderedChangeObjectList().add(operation);
        CollectionChangeRecord copy = merge(record);
        ObjectChangeSet copiedTarget = copy.getOrderedAddObjects().get(0);
        assertSame(copiedTarget, copy.getAddOverFlow().get(0));
        assertSame(copiedTarget, copy.getOrderedRemoveObjects().get(0));
        assertSame(copiedTarget, copy.getOrderedChangeObjectList().get(0).getChangeSet());
        assertSame(target.getUnitOfWorkClone(), copy.getOrderedChangeObjectList().get(0).getAddedOrRemovedObject());
        copy.getOrderedChangeObjectList().get(0).setIndex(7);
        copy.getOrderedRemoveObjectIndices().clear();
        copy.getOrderedAddObjectIndices().clear();
        copy.getAddOverFlow().clear();
        assertEquals(1, operation.getIndex());
        assertEquals(List.of(0), record.getOrderedRemoveObjectIndices());
        assertEquals(1, record.getOrderedAddObjectIndex(target));
        assertEquals(List.of(target), record.getAddOverFlow());
    }

    /** Deferred containers and the set of pending attributes must not be shared. */
    @Test
    void deferredCollectionStateIsIndependent() {
        CollectionChangeRecord record = new CollectionChangeRecord(owner);
        ManyToManyMapping mapping = new ManyToManyMapping();
        mapping.setContainerPolicy(new ListContainerPolicy(ArrayList.class));
        record.setMapping(mapping);
        record.setOriginalCollection(new ArrayList<>());
        record.setLatestCollection(new ArrayList<>(List.of(target.getUnitOfWorkClone())));
        record.setIsDeferred(true);
        owner.getDeferredSet().add("value");
        CollectionChangeRecord copy = merge(record);
        ((List<?>)copy.getLatestCollection()).clear();
        copy.setIsDeferred(false);
        ((ObjectChangeSet)copy.getOwner()).getDeferredSet().clear();
        assertNotSame(record.getOriginalCollection(), copy.getOriginalCollection());
        assertEquals(List.of(target.getUnitOfWorkClone()), record.getLatestCollection());
        assertEquals(Set.of("value"), owner.getDeferredSet());
        assertTrue(record.isDeferred());
    }

    /** Copy incoming records even when a destination record already exists and calculation fails. */
    @Test
    void failedMergeIntoExistingRecordPreservesSource() {
        CollectionChangeRecord first = new CollectionChangeRecord(owner);
        first.getAddObjectList().put(target, target);
        CollectionChangeRecord destination = merge(first);
        RuntimeException failure = new RuntimeException("Deliberate deferred calculation failure");
        ManyToManyMapping mapping = new ManyToManyMapping() {
            @Override
            public void calculateDeferredChanges(ChangeRecord changes, AbstractSession session) {
                CollectionChangeRecord record = (CollectionChangeRecord)changes;
                record.setIsDeferred(false);
                record.getAddObjectList().clear();
                ((List<?>)record.getLatestCollection()).clear();
                throw failure;
            }
        };
        mapping.setContainerPolicy(new ListContainerPolicy(ArrayList.class));
        first.setMapping(mapping);
        first.setIsDeferred(true);
        first.setLatestCollection(new ArrayList<>(List.of(target.getUnitOfWorkClone())));
        assertSame(failure, assertThrows(RuntimeException.class, () -> temporary.mergeObjectChanges(owner, live)));
        assertTrue(first.isDeferred());
        assertTrue(first.getAddObjectList().containsKey(target));
        assertEquals(List.of(target.getUnitOfWorkClone()), first.getLatestCollection());
        assertSame(owner, first.getOwner());
        assertEquals(1, destination.getAddObjectList().size());
    }

    /** Scalar application values stay shared, while record ownership is isolated. */
    @Test
    void directFieldPreservesApplicationValues() {
        DirectToFieldChangeRecord record = new DirectToFieldChangeRecord(owner);
        Object oldValue = new Object();
        Object newValue = new Object();
        record.setOldValue(oldValue);
        record.setNewValue(newValue);
        DirectToFieldChangeRecord copy = merge(record);
        assertSame(oldValue, copy.getOldValue());
        assertSame(newValue, copy.getNewValue());
        copy.setNewValue(null);
        assertSame(newValue, record.getNewValue());
        assertSame(owner, record.getOwner());
    }

    /** References point into the destination graph without changing the source graph. */
    @Test
    void objectReferenceUsesDestinationIdentity() {
        ObjectReferenceChangeRecord record = new ObjectReferenceChangeRecord(owner);
        record.setNewValue(target);
        record.setOldValue(target.getUnitOfWorkClone());
        ObjectReferenceChangeRecord copy = merge(record);
        assertSame(target, record.getNewValue());
        assertSame(temporary.getObjectChangeSetForClone(target.getUnitOfWorkClone()), copy.getNewValue());
        assertSame(target.getUnitOfWorkClone(), copy.getOldValue());
    }

    /** Direct collection counters and both sets of order indexes must be detached. */
    @Test
    void directCollectionCountsAndIndexesAreIndependent() {
        DirectCollectionChangeRecord record = new DirectCollectionChangeRecord(owner);
        record.addObjectMap = new HashMap<>(Map.of("a", 2));
        record.removeObjectMap = new HashMap<>(Map.of("b", 1));
        record.commitAddMap = new HashMap<>(Map.of("a", 1));
        Set<?>[] indexes = {new HashSet<>(Set.of(1)), new HashSet<>(Set.of(2))};
        record.changedIndexes = new HashMap<>(Map.of("a", indexes));
        DirectCollectionChangeRecord copy = merge(record);
        copy.addObjectMap.clear();
        copy.removeObjectMap.clear();
        copy.commitAddMap.clear();
        Set<?>[] copiedIndexes = (Set<?>[])copy.changedIndexes.get("a");
        copiedIndexes[0].clear();
        copiedIndexes[1] = Set.of(3);
        assertEquals(Map.of("a", 2), record.addObjectMap);
        assertEquals(Map.of("b", 1), record.removeObjectMap);
        assertEquals(Map.of("a", 1), record.commitAddMap);
        assertEquals(Set.of(1), indexes[0]);
        assertEquals(Set.of(2), indexes[1]);
    }

    /** Map container copies preserve keys and values without sharing mutable maps. */
    @Test
    void directMapAndDeferredMapsAreIndependent() {
        DirectMapChangeRecord record = new DirectMapChangeRecord(owner);
        DirectMapMapping mapping = new DirectMapMapping();
        mapping.setContainerPolicy(new MappedKeyMapContainerPolicy(HashMap.class));
        record.setMapping(mapping);
        Object key = new Object();
        Object value = new Object();
        record.addObjectsList = new HashMap<>(Map.of(key, value));
        record.removeObjectsList = new HashMap<>(Map.of("old", value));
        record.setOriginalCollection(new HashMap<>(Map.of("old", value)));
        record.setLatestCollection(new HashMap<>(Map.of(key, value)));
        DirectMapChangeRecord copy = merge(record);
        assertSame(value, ((Map<?, ?>)copy.getLatestCollection()).get(key));
        copy.addObjectsList.clear();
        copy.removeObjectsList.clear();
        ((Map<?, ?>)copy.getOriginalCollection()).clear();
        ((Map<?, ?>)copy.getLatestCollection()).clear();
        assertEquals(Map.of(key, value), record.addObjectsList);
        assertEquals(Map.of("old", value), record.removeObjectsList);
        assertEquals(Map.of("old", value), record.getOriginalCollection());
        assertEquals(Map.of(key, value), record.getLatestCollection());
    }

    /** Nested aggregates are copied recursively and back references reuse the copied owner. */
    @Test
    void aggregateGraphPreservesOwnershipAndCanonicalReferences() {
        ObjectChangeSet aggregate = objectChanges(true);
        ObjectReferenceChangeRecord backReference = new ObjectReferenceChangeRecord(aggregate);
        backReference.setAttribute("parent");
        backReference.setNewValue(owner);
        aggregate.addChange(backReference);
        AggregateChangeRecord record = new AggregateChangeRecord(owner);
        record.setChangedObject(aggregate);
        AggregateChangeRecord copy = merge(record);
        ObjectChangeSet copiedAggregate = (ObjectChangeSet)copy.getChangedObject();
        assertNotSame(aggregate, copiedAggregate);
        assertTrue(copiedAggregate.isAggregate());
        ObjectReferenceChangeRecord copiedReference = (ObjectReferenceChangeRecord)copiedAggregate.getChangesForAttributeNamed("parent");
        assertSame(copy.getOwner(), copiedReference.getNewValue());
        assertSame(copiedAggregate, copiedReference.getOwner());
        copiedReference.setNewValue(target);
        assertSame(owner, backReference.getNewValue());
        assertSame(aggregate, backReference.getOwner());
    }

    /** Aggregate collection elements and their nested field records must be independent. */
    @Test
    void aggregateCollectionCopiesNestedChanges() {
        ObjectChangeSet aggregate = objectChanges(true);
        DirectToFieldChangeRecord field = new DirectToFieldChangeRecord(aggregate);
        field.setAttribute("name");
        field.setNewValue("before");
        aggregate.addChange(field);
        AggregateCollectionChangeRecord record = new AggregateCollectionChangeRecord(owner);
        record.getChangedValues().add(aggregate);
        AggregateCollectionChangeRecord copy = merge(record);
        ObjectChangeSet copiedAggregate = copy.getChangedValues().get(0);
        ((DirectToFieldChangeRecord)copiedAggregate.getChangesForAttributeNamed("name")).setNewValue("after");
        copy.getChangedValues().clear();
        assertEquals(List.of(aggregate), record.getChangedValues());
        assertEquals("before", field.getNewValue());
        assertSame(aggregate, field.getOwner());
    }

    /** Removing an aggregate must also detach the change set retained as its old value. */
    @Test
    void removedAggregatePreservesOldValueChanges() {
        ObjectChangeSet aggregate = objectChanges(true);
        DirectToFieldChangeRecord field = new DirectToFieldChangeRecord(aggregate);
        field.setAttribute("name");
        field.setOldValue("original");
        field.setNewValue("changed");
        aggregate.addChange(field);
        AggregateChangeRecord record = new AggregateChangeRecord(owner);
        record.setChangedObject(aggregate);
        record.setChangedObject(null);
        AggregateChangeRecord copy = merge(record);
        ObjectChangeSet copiedOldValue = (ObjectChangeSet)copy.oldValue;
        assertNotSame(aggregate, copiedOldValue);
        DirectToFieldChangeRecord copiedField = (DirectToFieldChangeRecord)copiedOldValue.getChangesForAttributeNamed("name");
        assertEquals("original", copiedField.getOldValue());
        copiedField.setOldValue("different");
        assertEquals("original", field.getOldValue());
        assertSame(aggregate, record.oldValue);
    }

    /** An existing aggregate must receive each incoming ordered operation only once. */
    @Test
    void mergingIntoExistingAggregateDoesNotDuplicateOperations() {
        ObjectChangeSet aggregate = objectChanges(true);
        CollectionChangeRecord nested = new CollectionChangeRecord(aggregate);
        nested.setAttribute("children");
        nested.getOrderedChangeObjectList().add(new OrderedChangeObject(1, 0, target));
        aggregate.addChange(nested);
        AggregateChangeRecord record = new AggregateChangeRecord(owner);
        record.setChangedObject(aggregate);
        AggregateChangeRecord copy = merge(record);
        temporary.mergeObjectChanges(owner, live);
        ObjectChangeSet copiedAggregate = (ObjectChangeSet)copy.getChangedObject();
        CollectionChangeRecord copiedNested = (CollectionChangeRecord)copiedAggregate.getChangesForAttributeNamed("children");
        assertEquals(2, copiedNested.getOrderedChangeObjectList().size());
        assertEquals(1, nested.getOrderedChangeObjectList().size());
    }

    /** Transformation rows must not share their mutable field/value storage. */
    @Test
    void transformationRowsAreIndependent() {
        TransformationMappingChangeRecord record = new TransformationMappingChangeRecord(owner);
        record.rowCollection = new DatabaseRecord();
        record.rowCollection.put("field", "before");
        TransformationMappingChangeRecord copy = merge(record);
        copy.rowCollection.put("field", "after");
        assertEquals("before", record.rowCollection.get("field"));
    }

    /** EIS lists detach simple values and translate referenced change-set identities. */
    @Test
    void eisCollectionsAreIndependent() {
        EISCollectionChangeRecord record = new EISCollectionChangeRecord(owner, "value", null);
        record.addAddedChangeSet(target);
        record.addRemovedChangeSet("removed");
        record.addChangedMapKeyChangeSet(target);
        EISCollectionChangeRecord copy = merge(record);
        assertSame(temporary.getObjectChangeSetForClone(target.getUnitOfWorkClone()), copy.getAdds().get(0));
        assertSame(copy.getAdds().get(0), copy.getChangedMapKeys().get(0));
        copy.getAdds().clear();
        copy.getRemoves().clear();
        copy.getChangedMapKeys().clear();
        assertEquals(List.of(target), record.getAdds());
        assertEquals(List.of("removed"), record.getRemoves());
        assertEquals(List.of(target), record.getChangedMapKeys());
    }

    /** Ordered EIS copies detach every index array, including nested move pairs. */
    @Test
    void eisOrderedIndexesAreIndependent() {
        EISOrderedCollectionChangeRecord record = new EISOrderedCollectionChangeRecord(owner, "value", null);
        record.addAddedChangeSet(target, 1);
        record.addMovedChangeSet(target, 2, 3);
        record.addRemovedChangeSet("removed", 4);
        EISOrderedCollectionChangeRecord copy = merge(record);
        assertSame(copy.getAdds().get(0), copy.getMoves().get(0));
        assertNotSame(target, copy.getAdds().get(0));
        copy.getAddIndexes()[0] = 8;
        copy.getMoveIndexPairs()[0][1] = 8;
        copy.getRemoveIndexes()[0] = 8;
        copy.getMoves().clear();
        assertEquals(1, record.getAddIndexes()[0]);
        assertEquals(3, record.getMoveIndexPairs()[0][1]);
        assertEquals(4, record.getRemoveIndexes()[0]);
        assertEquals(List.of(target), record.getMoves());
    }

    /** Ordinary merges must continue combining additions and removals correctly. */
    @Test
    void ordinaryMergeStillCancelsRelationships() {
        UnitOfWorkChangeSet destination = new UnitOfWorkChangeSet(session);
        CollectionChangeRecord addition = new CollectionChangeRecord(owner);
        addition.setAttribute("value");
        addition.getAddObjectList().put(target, target);
        owner.addChange(addition);
        ObjectChangeSet combined = destination.mergeObjectChanges(owner, live);
        CollectionChangeRecord removal = new CollectionChangeRecord(owner);
        removal.setAttribute("value");
        removal.getRemoveObjectList().put(target, target);
        owner.clear(false);
        owner.addChange(removal);
        destination.mergeObjectChanges(owner, live);
        CollectionChangeRecord result = (CollectionChangeRecord)combined.getChangesForAttributeNamed("value");
        assertTrue(result.getAddObjectList().isEmpty());
        assertTrue(result.getRemoveObjectList().isEmpty());
        assertFalse(destination.shouldPreserveSourceRecords());
    }

    /** Create a source identity without database access or entity cloning. */
    private ObjectChangeSet objectChanges(boolean aggregate) {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Object.class);
        Object clone = new Object();
        ObjectChangeSet changes = aggregate
                ? new AggregateObjectChangeSet(null, descriptor, clone, live, false)
                : new ObjectChangeSet(clone, descriptor, clone, live, false);
        live.addObjectChangeSetForIdentity(changes, clone);
        return changes;
    }

    /** Merge one source attribute into the temporary graph and return its copied record. */
    @SuppressWarnings("unchecked")
    private <T extends ChangeRecord> T merge(T record) {
        record.setAttribute("value");
        owner.addChange(record);
        ObjectChangeSet destination = temporary.mergeObjectChanges(owner, live);
        return (T)destination.getChangesForAttributeNamed("value");
    }
}
