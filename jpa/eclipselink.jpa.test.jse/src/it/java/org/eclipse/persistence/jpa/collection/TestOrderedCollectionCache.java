/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpa.collection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.function.Consumer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.sessions.CollectionChangeRecord;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.internal.queries.OrderedListContainerPolicy;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.jpa.collection.model.OrderedCacheElement;
import org.eclipse.persistence.jpa.collection.model.OrderedCacheOwner;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Exercises committed ordered-list changes through the cache coordination payload
 * and receiving command, without requiring a network transport.
 */
@RunWith(EmfRunner.class)
public class TestOrderedCollectionCache {

    @Emf(name = "ordered-cache-sender", createTables = DDLGen.DROP_CREATE,
            classes = {OrderedCacheOwner.class, OrderedCacheElement.class},
            properties = @Property(name = PersistenceUnitProperties.WEAVING, value = "static"))
    private EntityManagerFactory sender;

    @Emf(name = "ordered-cache-receiver", createTables = DDLGen.NONE,
            classes = {OrderedCacheOwner.class, OrderedCacheElement.class},
            properties = @Property(name = PersistenceUnitProperties.WEAVING, value = "static"))
    private EntityManagerFactory receiver;

    @Before
    public void before() {
        assertNotSame(session(sender), session(receiver));
        assertTrue(session(sender).getDescriptor(OrderedCacheOwner.class)
                .getObjectChangePolicy().isAttributeChangeTrackingPolicy());
        assertTrue(session(receiver).getDescriptor(OrderedCacheOwner.class)
                .getObjectChangePolicy().isAttributeChangeTrackingPolicy());
        CollectionMapping mapping = (CollectionMapping) session(sender).getDescriptor(OrderedCacheOwner.class)
                .getMappingForAttributeName("elements");
        assertTrue("The fixture must use the ordered merge path: " + mapping.getContainerPolicy(),
                mapping.getContainerPolicy() instanceof OrderedListContainerPolicy);
        cleanup();
    }

    @After
    public void cleanup() {
        commit(em -> {
            em.createQuery("DELETE FROM OrderedCacheElement").executeUpdate();
            em.createQuery("DELETE FROM OrderedCacheOwner").executeUpdate();
        });
        sender.getCache().evictAll();
        receiver.getCache().evictAll();
    }

    @Test
    public void testMultipleFlushesPropagateOrderedAdds() throws Exception {
        propagateAdds(true);
    }

    @Test
    public void testSingleFlushPropagatesOrderedAdds() throws Exception {
        propagateAdds(false);
    }

    @Test
    public void testExistingMembersCanBeReordered() throws Exception {
        mergeDetached(false);
    }

    @Test
    public void testDetachedMergeWithMissingReceiverMemberInvalidatesOwner() throws Exception {
        mergeDetached(true);
    }

    private void propagateAdds(boolean multipleFlushes) throws Exception {
        seed(10);
        assertEquals(List.of(10), readIds(receiver));
        OrderedCacheOwner receiverOwner = cachedOwner(receiver);
        MergeChangeSetCommand command = captureChanges(em -> {
            OrderedCacheOwner owner = em.find(OrderedCacheOwner.class, 1);
            if (multipleFlushes) {
                // The first flush creates a cumulative record without the collection.
                // The second enters updateReferences; the third enters mergeRecord.
                owner.setLabel("before collection changes");
                em.flush();
            }
            owner.addElement(new OrderedCacheElement(20));
            if (multipleFlushes) {
                em.flush();
            }
            owner.addElement(new OrderedCacheElement(30));
            em.flush();
        });

        assertEquals(List.of(10, 20, 30), ids(cachedOwner(sender)));
        assertEquals(List.of(10), ids(receiverOwner));
        assertOrderedOperations(command);
        command.executeWithSession(session(receiver));
        assertTrue(session(receiver).getIdentityMapAccessor().isValid(receiverOwner));
        assertEquals(List.of(10, 20, 30), ids(receiverOwner));
        assertFreshDatabaseState(List.of(10, 20, 30));
    }

    private void mergeDetached(boolean missingReceiverMember) throws Exception {
        seed(10, 20, 30);
        assertEquals(List.of(10, 20, 30), readIds(receiver));
        OrderedCacheOwner receiverOwner = cachedOwner(receiver);
        if (missingReceiverMember) {
            // Keep the owner's list while evicting its members, as can happen on
            // a receiving node. All REMOVE operations then precede unresolved ADDs.
            for (OrderedCacheElement element : receiverOwner.getElements()) {
                session(receiver).getIdentityMapAccessor().removeFromIdentityMap(element.getId(), OrderedCacheElement.class);
                assertNull(session(receiver).getIdentityMapAccessor()
                        .getFromIdentityMap(element.getId(), OrderedCacheElement.class));
            }
        }
        OrderedCacheOwner detached;
        try (EntityManager em = sender.createEntityManager()) {
            detached = em.find(OrderedCacheOwner.class, 1);
            assertEquals(List.of(10, 20, 30), ids(detached));
        }
        OrderedCacheElement moved = detached.getElements().remove(1);
        moved.setPosition(40);
        detached.getElements().add(moved);
        MergeChangeSetCommand command = captureChanges(em -> em.merge(detached));

        assertOrderedOperations(command);
        command.executeWithSession(session(receiver));
        assertFalse("Missing members must never be inserted as null", receiverOwner.getElements().contains(null));
        if (missingReceiverMember) {
            assertFalse(session(receiver).getIdentityMapAccessor().isValid(receiverOwner));
            // An ordinary read must reload the invalid owner and its complete list.
            assertEquals(List.of(10, 30, 20), readIds(receiver));
        } else {
            assertTrue(session(receiver).getIdentityMapAccessor().isValid(receiverOwner));
            assertEquals(List.of(10, 30, 20), ids(receiverOwner));
        }
        assertEquals(List.of(10, 30, 20), ids(cachedOwner(sender)));
        assertFreshDatabaseState(List.of(10, 30, 20));
    }

    private void seed(int... elementIds) {
        commit(em -> {
            OrderedCacheOwner owner = new OrderedCacheOwner(1);
            for (int id : elementIds) {
                owner.addElement(new OrderedCacheElement(id));
            }
            em.persist(owner);
        });
    }

    private MergeChangeSetCommand captureChanges(Consumer<EntityManager> change) throws Exception {
        ChangeCapture capture = new ChangeCapture();
        ServerSession source = session(sender);
        source.getEventManager().addListener(capture);
        try {
            commit(em -> {
                capture.unitOfWork = (UnitOfWorkImpl) em.unwrap(JpaEntityManager.class).getUnitOfWork();
                change.accept(em);
            });
        } finally {
            source.getEventManager().removeListener(capture);
        }
        assertNotNull("The commit must publish its cumulative change set", capture.changes);
        UnitOfWorkChangeSet remote = capture.changes;
        assertNotNull("SEND_OBJECT_CHANGES must produce a coordination payload", remote);
        MergeChangeSetCommand command = new MergeChangeSetCommand();
        command.setChangeSet(remote);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(command);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (MergeChangeSetCommand) input.readObject();
        }
    }

    private void assertOrderedOperations(MergeChangeSetCommand command) {
        UnitOfWorkChangeSet changes = command.getChangeSet(session(receiver));
        ObjectChangeSet owner = changes.getAllChangeSets().values().stream()
                .filter(change -> change.getClassType(session(receiver)) == OrderedCacheOwner.class)
                .findFirst().orElseThrow();
        // The attribute lookup map is transient and is initialized by the receiver.
        CollectionChangeRecord collection = (CollectionChangeRecord) owner.getChanges().stream()
                .filter(record -> "elements".equals(record.getAttribute())).findFirst().orElse(null);
        assertNotNull("The test must exercise an ordered collection change; actual changes: " + owner.getChanges(), collection);
        assertFalse("Attribute tracking must record ordered operations", collection.getOrderedChangeObjectList().isEmpty());
        collection.getOrderedChangeObjectList().forEach(change ->
                assertNull("Serialized changes must not retain sender clones",
                        change.getChangeSet().getUnitOfWorkClone()));
    }

    private void assertFreshDatabaseState(List<Integer> expected) {
        sender.getCache().evictAll();
        assertEquals(expected, readIds(sender));
    }

    private List<Integer> readIds(EntityManagerFactory factory) {
        try (EntityManager em = factory.createEntityManager()) {
            return ids(em.find(OrderedCacheOwner.class, 1));
        }
    }

    private static List<Integer> ids(OrderedCacheOwner owner) {
        assertNotNull(owner);
        assertFalse("The cached ordered collection must not contain null", owner.getElements().contains(null));
        return owner.getElements().stream().map(OrderedCacheElement::getId).toList();
    }

    private OrderedCacheOwner cachedOwner(EntityManagerFactory factory) {
        return (OrderedCacheOwner) session(factory).getIdentityMapAccessor()
                .getFromIdentityMap(1, OrderedCacheOwner.class);
    }

    private static ServerSession session(EntityManagerFactory factory) {
        return factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
    }

    private void commit(Consumer<EntityManager> work) {
        try (EntityManager em = sender.createEntityManager()) {
            em.getTransaction().begin();
            try {
                work.accept(em);
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    private static final class ChangeCapture extends SessionEventAdapter {
        private UnitOfWorkChangeSet changes;
        private UnitOfWorkImpl unitOfWork;

        @Override
        public void postMergeUnitOfWorkChangeSet(SessionEvent event) {
            UnitOfWorkChangeSet committed = (UnitOfWorkChangeSet) event.getProperty("UnitOfWorkChangeSet");
            changes = committed.buildCacheCoordinationMergeChangeSet(unitOfWork);
        }
    }
}
