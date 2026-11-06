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
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.framework.SQLCallListener;
import org.eclipse.persistence.jpa.test.mapping.model.OrderedFlushChild;
import org.eclipse.persistence.jpa.test.mapping.model.OrderedFlushOwner;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Changes after a flush must reach the shared cache as well as the database. */
@RunWith(EmfRunner.class)
public class TestOrderedCollectionFlush {
    @Emf(name = "orderedFlush", createTables = DDLGen.DROP_CREATE,
            classes = {OrderedFlushOwner.class, OrderedFlushChild.class},
            properties = @Property(name = PersistenceUnitProperties.WEAVING, value = "false"))
    private EntityManagerFactory emf;

    @Emf(name = "isolatedOrderedFlush", createTables = DDLGen.NONE,
            classes = {OrderedFlushOwner.class, OrderedFlushChild.class}, properties = {
            @Property(name = PersistenceUnitProperties.WEAVING, value = "false"),
            @Property(name = PersistenceUnitProperties.CACHE_SHARED_DEFAULT, value = "false")})
    private EntityManagerFactory isolatedEmf;

    @SQLCallListener(name = "orderedFlush")
    private List<String> sql;

    /** Deploy the schema before using the isolated persistence unit. */
    @Before
    public void initializeSchema() {
        emf.createEntityManager().close();
    }

    /** Control without an intermediate flush. */
    @Test
    public void noIntermediateFlush() {
        run(emf, "owner", false, false, "", "AB");
    }

    /** Control without ordered-list metadata. */
    @Test
    public void unorderedControl() {
        run(emf, "unorderedOwner", false, false, "", "A", "AB");
    }

    /** The reporter's sequences, including the no-flush control. */
    @Test
    public void additionsAcrossFlushes() {
        scenarios("", "A", "AB");
        scenarios("", "AB", "ABC");
        scenarios("", "A", "AB", "ABC");
        scenarios("", "AB");
    }

    /** Additions must be positioned relative to earlier additions and removals. */
    @Test
    public void insertionsAndRemovals() {
        scenarios("CE", "ACE", "ABCDE");
        scenarios("ACE", "ABCE", "BDE");
        scenarios("ABCDE", "BDE", "DE");
    }

    /** A later removal cancels a flushed addition. */
    @Test
    public void addThenRemove() {
        scenarios("C", "AC", "C");
        scenarios("C", "ABC", "BC");
    }

    /** Reattach the same entity rather than creating another one with its name. */
    @Test
    public void removeThenReadd() {
        scenarios("ABC", "AC", "ABC");
        scenarios("ABC", "", "ABC");
    }

    /** Empty flushes must preserve earlier ordered changes. */
    @Test
    public void unchangedFlushes() {
        scenarios("", "A", "A", "AB");
    }

    /** Persistent list positions exercise moves in both directions. */
    @Test
    public void movesAcrossFlushes() {
        run(emf, "indexedOwner", false, false, "ABCDE", "CEABD", "DBEAC");
        run(emf, "indexedOwner", false, false, "ABC", "CAB", "ABC");
    }

    /** AUTO queries flush pending changes just like explicit flush. */
    @Test
    public void automaticFlush() {
        run(emf, "owner", true, false, "", "A", "AB", "ABC");
    }

    /** Controls for the shared-cache boundary reported in the issue. */
    @Test
    public void cacheDisabled() {
        run(isolatedEmf, "owner", false, false, "", "A", "AB");
    }

    /** Also run on the combined #2899/#2967 configuration. */
    @Test
    public void inspectBetweenFlushes() {
        run(emf, "owner", false, true, "AC", "ABC", "BC", "ABCD");
    }

    private void scenarios(String initial, String... states) {
        for (String kind : List.of("owner", "indexedOwner", "unorderedOwner")) {
            run(emf, kind, false, false, initial, states);
        }
    }

    private void run(EntityManagerFactory factory, String kind, boolean queryFlush,
                     boolean inspect, String initial, String... states) {
        Long id;
        try (EntityManager em = factory.createEntityManager()) {
            em.getTransaction().begin();
            OrderedFlushOwner owner = new OrderedFlushOwner();
            em.persist(owner);
            change(em, owner, kind, initial, new ArrayList<>());
            em.getTransaction().commit();
            id = owner.getId();
        }
        // Populate the shared collection before the transaction that changes it.
        try (EntityManager em = factory.createEntityManager()) {
            em.find(OrderedFlushOwner.class, id).children(kind).size();
        }
        try (EntityManager em = factory.createEntityManager()) {
            try {
                em.getTransaction().begin();
                OrderedFlushOwner owner = em.find(OrderedFlushOwner.class, id);
                List<OrderedFlushChild> known = new ArrayList<>(owner.children(kind));
                for (int i = 0; i < states.length; i++) {
                    change(em, owner, kind, states[i], known);
                    if (inspect) {
                        UnitOfWork uow = em.unwrap(UnitOfWork.class);
                        uow.hasChanges();
                        uow.getCurrentChanges();
                        uow.hasChanges();
                    }
                    if (i + 1 < states.length) {
                        if (queryFlush) {
                            em.createQuery("select count(o) from OrderedFlushOwner o", Long.class).getSingleResult();
                        } else {
                            em.flush();
                        }
                    }
                }
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
        List<String> expected = states[states.length - 1].chars().mapToObj(c -> String.valueOf((char)c)).toList();
        String context = kind + ": " + initial + " -> " + List.of(states);
        sql.clear();
        try (EntityManager em = factory.createEntityManager()) {
            List<String> cached = em.find(OrderedFlushOwner.class, id).children(kind).stream()
                    .map(OrderedFlushChild::getName).toList();
            if (factory == emf) {
                assertTrue(context + " should use the shared cache: " + sql, sql.isEmpty());
            }
            assertEquals(context + " shared cache", kind.equals("unorderedOwner") ? expected.stream().sorted().toList() : expected,
                    kind.equals("unorderedOwner") ? cached.stream().sorted().toList() : cached);
            List<String> database = em.createQuery("select c.name from OrderedFlushChild c where c."
                    + kind + ".id = :id order by c.name", String.class).setParameter("id", id).getResultList();
            assertEquals(context + " database", expected.stream().sorted().toList(), database);
        }
        factory.getCache().evict(OrderedFlushOwner.class, id);
        try (EntityManager em = factory.createEntityManager()) {
            List<String> reloaded = em.find(OrderedFlushOwner.class, id).children(kind).stream()
                    .map(OrderedFlushChild::getName).toList();
            assertEquals(context + " reload", kind.equals("unorderedOwner") ? expected.stream().sorted().toList() : expected,
                    kind.equals("unorderedOwner") ? reloaded.stream().sorted().toList() : reloaded);
        }
    }

    private void change(EntityManager em, OrderedFlushOwner owner, String kind, String names,
                        List<OrderedFlushChild> known) {
        List<OrderedFlushChild> desired = new ArrayList<>();
        for (char name : names.toCharArray()) {
            String text = String.valueOf(name);
            OrderedFlushChild child = known.stream().filter(c -> c.getName().equals(text)).findFirst().orElse(null);
            if (child == null) {
                child = new OrderedFlushChild(text);
                em.persist(child);
                known.add(child);
            }
            child.setOwner(kind, owner);
            desired.add(child);
        }
        for (OrderedFlushChild child : owner.children(kind)) {
            if (!desired.contains(child)) {
                child.setOwner(kind, null);
            }
        }
        owner.children(kind).clear();
        owner.children(kind).addAll(desired);
    }
}
