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
package org.eclipse.persistence.jpa.test.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.query.model.OrderByChild;
import org.eclipse.persistence.jpa.test.query.model.OrderByDetail;
import org.eclipse.persistence.jpa.test.query.model.OrderByFetchedParent;
import org.eclipse.persistence.jpa.test.query.model.OrderByGrandchild;
import org.eclipse.persistence.jpa.test.query.model.OrderByGroup;
import org.eclipse.persistence.jpa.test.query.model.OrderByKey;
import org.eclipse.persistence.jpa.test.query.model.OrderByKeyedParent;
import org.eclipse.persistence.jpa.test.query.model.OrderByParent;
import org.eclipse.persistence.jpa.test.query.model.OrderByTag;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A fetch join retrieves the elements of a collection in the order of its {@code @OrderBy}, as the specification of
 * {@code jakarta.persistence.OrderBy} requires "at the point when the association or collection is retrieved". The
 * ordering of the query itself orders the parents, and the rows of one parent stay together.
 */
@RunWith(EmfRunner.class)
public class TestOrderByFetchJoin {

    private static final String FETCH_JOIN = "SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.children";

    @Emf(createTables = DDLGen.DROP_CREATE, classes = { OrderByParent.class, OrderByChild.class, OrderByFetchedParent.class, OrderByKeyedParent.class,
            OrderByGrandchild.class, OrderByGroup.class, OrderByTag.class, OrderByDetail.class, OrderByKey.class })
    private EntityManagerFactory emf;

    @Before
    public void persistParents() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM OrderByGrandchild").executeUpdate();
            em.createQuery("DELETE FROM OrderByChild").executeUpdate();
            em.createQuery("DELETE FROM OrderByFetchedParent").executeUpdate();
            em.createQuery("DELETE FROM OrderByKeyedParent").executeUpdate();
            em.createQuery("DELETE FROM OrderByParent").executeUpdate();
            OrderByParent first = new OrderByParent(1);
            first.addChild(11, "b");
            first.addChild(12, "a");
            first.addChild(13, "c");
            first.getGroup().getMembers().addAll(first.getChildren());
            first.getCoded().addAll(first.getChildren());
            first.getDetailed().addAll(first.getChildren());
            first.getTags().addAll(List.of(new OrderByTag("y"), new OrderByTag("x"), new OrderByTag("z")));
            first.getLabels().addAll(List.of("y", "x", "z"));
            first.getNotes().addAll(List.of("n2", "n1", "n3"));
            first.getRemarks().addAll(List.of("r2", "r1", "r3"));
            first.getQuoted().addAll(List.of("q2", "q1", "q3"));
            first.getChildren().get(0).addGrandchild(111, "g2");
            first.getChildren().get(0).addGrandchild(112, "g1");
            first.getChildren().get(0).addGrandchild(113, "g3");
            first.getChildren().get(2).addGrandchild(131, "h1");
            first.getChildren().get(2).addGrandchild(132, "h2");
            OrderByParent second = new OrderByParent(2);
            second.addChild(21, "d");
            second.addChild(22, "bb");
            em.persist(first);
            em.persist(second);
            em.persist(new OrderByParent(3));
            OrderByFetchedParent fetched = new OrderByFetchedParent(1);
            fetched.getChildren().addAll(first.getChildren());
            fetched.getOthers().addAll(second.getChildren());
            em.persist(fetched);
            OrderByKeyedParent keyed = new OrderByKeyedParent(new OrderByKey(1, 2));
            keyed.getChildren().addAll(first.getChildren());
            em.persist(keyed);
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        emf.getCache().evictAll();
    }

    @Test
    public void testJpqlFetchJoin() {
        assertChildNames(List.of(List.of("c", "b", "a"), List.of("d", "bb")),
                em -> em.createQuery(FETCH_JOIN + " ORDER BY p.id", OrderByParent.class).getResultList());
    }

    @Test
    public void testJpqlFetchJoinOrderedDescending() {
        assertChildNames(List.of(List.of("d", "bb"), List.of("c", "b", "a")),
                em -> em.createQuery(FETCH_JOIN + " ORDER BY p.id DESC", OrderByParent.class).getResultList());
    }

    @Test
    public void testJpqlFetchJoinWithoutOrderingOfQuery() {
        assertChildNames(List.of(List.of("c", "b", "a"), List.of("d", "bb")),
                em -> em.createQuery(FETCH_JOIN, OrderByParent.class).getResultList());
    }

    @Test
    public void testJpqlOuterFetchJoinKeepsParentWithoutChildren() {
        assertChildNames(List.of(List.of("c", "b", "a"), List.of("d", "bb"), List.of()),
                em -> em.createQuery("SELECT DISTINCT p FROM OrderByParent p LEFT JOIN FETCH p.children ORDER BY p.id", OrderByParent.class).getResultList());
    }

    @Test
    public void testCriteriaFetch() {
        assertChildNames(List.of(List.of("c", "b", "a"), List.of("d", "bb")), em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<OrderByParent> query = cb.createQuery(OrderByParent.class);
            Root<OrderByParent> root = query.from(OrderByParent.class);
            root.fetch("children");
            return em.createQuery(query.select(root).distinct(true).orderBy(cb.asc(root.get("id")))).getResultList();
        });
    }

    /**
     * The rows of one parent stay together, so that a range over the rows does not split the children of a parent.
     */
    @Test
    public void testFetchJoinWithMaxResultsAndFirstResult() {
        assertChildNames(List.of(List.of("c", "b", "a")), em -> em.createQuery(FETCH_JOIN, OrderByParent.class).setMaxResults(3).getResultList());
        assertChildNames(List.of(List.of("d", "bb")), em -> em.createQuery(FETCH_JOIN, OrderByParent.class).setFirstResult(3).getResultList());
    }

    @Test
    public void testFetchJoinWithScrollableCursor() {
        assertChildNames(List.of(List.of("c", "b", "a"), List.of("d", "bb")), em -> readCursor(em, "SELECT p FROM OrderByParent p JOIN FETCH p.children"));
    }

    /**
     * Ordering by a function of the primary key does not order by the primary key itself, so the rows of one owner still
     * stay together.
     */
    @Test
    public void testFetchJoinOrderedByFunctionOfIdWithScrollableCursor() {
        assertChildNames(List.of(List.of("c", "b", "a"), List.of("d", "bb")),
                em -> readCursor(em, "SELECT p FROM OrderByParent p JOIN FETCH p.children ORDER BY MOD(p.id, 1)"));
    }

    @Test
    public void testFetchJoinOfCollectionInEmbeddable() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByParent> query = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.group.members ORDER BY p.id", OrderByParent.class);
            List<OrderByParent> parents = query.getResultList();
            assertEquals(List.of("c", "b", "a"), parents.get(0).getGroup().getMembers().stream().map(OrderByChild::getName).toList());
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    @Test
    public void testFetchJoinOfEmbeddableElementCollection() {
        EntityManager em = emf.createEntityManager();
        try {
            List<OrderByParent> parents = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.tags ORDER BY p.id", OrderByParent.class).getResultList();
            assertEquals(List.of("z", "y", "x"), parents.get(0).getTags().stream().map(OrderByTag::getLabel).toList());
        } finally {
            em.close();
        }
    }

    /**
     * Ordering the query by the field which the order by of the fetched collection names does not repeat that field.
     */
    @Test
    public void testFetchJoinOrderedByOrderByFieldDoesNotRepeatOrderByItems() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByParent> query = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.children c ORDER BY c.name", OrderByParent.class);
            query.getResultList();
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    /**
     * A query hint prepares the query once more, which does not repeat an item of the order by.
     */
    @Test
    public void testFetchJoinPreparedAgainDoesNotRepeatOrderByItems() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByParent> query = em.createQuery(FETCH_JOIN + " ORDER BY p.id", OrderByParent.class)
                    .setHint(QueryHints.FETCH, "p.children.parent");
            assertEquals(List.of(List.of("c", "b", "a"), List.of("d", "bb")), childNames(query.getResultList()));
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    /**
     * A report query on an entity with a join fetch of a mapping does not join the collection.
     */
    @Test
    public void testReportQueriesOnEntityWithJoinFetchOfMapping() {
        EntityManager em = emf.createEntityManager();
        try {
            assertEquals(Long.valueOf(1), em.createQuery("SELECT COUNT(f) FROM OrderByFetchedParent f", Long.class).getSingleResult());
            assertEquals(List.of(1L), em.createQuery("SELECT f.id FROM OrderByFetchedParent f", Long.class).getResultList());
        } finally {
            em.close();
        }
    }

    /**
     * A join fetch of a mapping and a join fetch of the query on the same owner, in a query with a second range variable,
     * order by the primary key of the owner once.
     */
    @Test
    public void testJoinFetchOfMappingAndQueryWithSecondRangeVariableDoesNotRepeatOrderByItems() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByFetchedParent> query = em.createQuery(
                    "SELECT f FROM OrderByFetchedParent f JOIN FETCH f.others, OrderByChild x WHERE x.id = 11", OrderByFetchedParent.class);
            OrderByFetchedParent parent = query.getResultList().get(0);
            assertEquals(List.of("c", "b", "a"), parent.getChildren().stream().map(OrderByChild::getName).toList());
            assertEquals(List.of("d", "bb"), parent.getOthers().stream().map(OrderByChild::getName).toList());
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    /**
     * A join fetch on the expression builder of the query, with criteria and ordering on another expression builder of a
     * native query, orders by the primary key of the owner once.
     */
    @Test
    public void testJoinFetchOnQueryBuilderWithCriteriaOnOtherBuilderDoesNotRepeatOrderByItems() {
        EntityManager em = emf.createEntityManager();
        try {
            ReadAllQuery query = new ReadAllQuery(OrderByParent.class);
            query.addJoinedAttribute(query.getExpressionBuilder().anyOf("children"));
            ExpressionBuilder other = new ExpressionBuilder();
            query.setSelectionCriteria(other.get("id").lessThan(3));
            query.addOrdering(other.get("id"));
            @SuppressWarnings("unchecked")
            List<OrderByParent> parents = (List<OrderByParent>) em.unwrap(JpaEntityManager.class).getActiveSession().executeQuery(query);
            assertEquals(List.of(List.of("c", "b", "a"), List.of("d", "bb")), childNames(parents));
            assertNoRepeatedOrderByItems(query.getSQLString());
            query.setMaxRows(3);
            @SuppressWarnings("unchecked")
            List<OrderByParent> firstParents = (List<OrderByParent>) em.unwrap(JpaEntityManager.class).getActiveSession().executeQuery(query);
            assertEquals("Executed again", List.of(List.of("c", "b", "a")), childNames(firstParents));
        } finally {
            em.close();
        }
    }

    /**
     * Ordering by the primary key of a second range variable of the same entity does not order by the owner of the
     * fetched collection, so the order by holds the primary keys of both.
     */
    @Test
    public void testFetchJoinOrderedBySecondRangeVariableKeepsPrimaryKeyOfOwner() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByParent> query = em.createQuery(
                    FETCH_JOIN + ", OrderByParent q WHERE q.id = 1 ORDER BY q.id", OrderByParent.class);
            query.getResultList();
            query.getResultList();
            List<String> columns = assertNoRepeatedOrderByItems(query);
            assertEquals("Order by columns " + columns, 3, columns.size());
        } finally {
            em.close();
        }
    }

    @Test
    public void testFetchJoinOfBasicElementCollection() {
        EntityManager em = emf.createEntityManager();
        try {
            List<OrderByParent> parents = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.labels ORDER BY p.id", OrderByParent.class).getResultList();
            assertEquals(List.of("z", "y", "x"), List.copyOf(parents.get(0).getLabels()));
        } finally {
            em.close();
        }
    }

    /**
     * A custom ordering of a basic element collection on a field of its collection table, added without an order by,
     * orders the collection when it is loaded lazily as well as when it is fetched.
     */
    @Test
    public void testCustomOrderingOfBasicElementCollection() {
        EntityManager em = emf.createEntityManager();
        try {
            assertEquals("Lazy", List.of("n3", "n2", "n1"), List.copyOf(em.find(OrderByParent.class, 1L).getNotes()));
        } finally {
            em.close();
        }
        emf.getCache().evictAll();
        em = emf.createEntityManager();
        try {
            List<OrderByParent> parents = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.notes ORDER BY p.id", OrderByParent.class).getResultList();
            assertEquals("Fetched", List.of("n3", "n2", "n1"), List.copyOf(parents.get(0).getNotes()));
        } finally {
            em.close();
        }
    }

    /**
     * A custom ordering of a basic element collection on a field without table orders the collection when it is loaded
     * lazily, and leaves a fetch join of it working.
     */
    @Test
    public void testCustomOrderingWithoutTableOfBasicElementCollection() {
        EntityManager em = emf.createEntityManager();
        try {
            assertEquals("Lazy", List.of("r3", "r2", "r1"), List.copyOf(em.find(OrderByParent.class, 1L).getRemarks()));
        } finally {
            em.close();
        }
        emf.getCache().evictAll();
        em = emf.createEntityManager();
        try {
            List<OrderByParent> parents = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.remarks ORDER BY p.id", OrderByParent.class).getResultList();
            assertEquals("Fetched", Set.of("r1", "r2", "r3"), Set.copyOf(parents.get(0).getRemarks()));
        } finally {
            em.close();
        }
    }

    /**
     * An order by on a basic element collection whose column name has delimiters leaves a fetch join of it working.
     */
    @Test
    public void testFetchJoinOfBasicElementCollectionWithDelimitedColumn() {
        EntityManager em = emf.createEntityManager();
        try {
            List<OrderByParent> parents = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.quoted ORDER BY p.id", OrderByParent.class).getResultList();
            assertEquals(Set.of("q1", "q2", "q3"), Set.copyOf(parents.get(0).getQuoted()));
        } finally {
            em.close();
        }
    }

    /**
     * A collection fetched through a fetched collection is ordered within each of its owners, which the primary key of the
     * owner keeps together.
     */
    @Test
    public void testNestedFetchJoin() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByParent> query = em.createQuery("SELECT DISTINCT p FROM OrderByParent p ORDER BY p.id", OrderByParent.class)
                    .setHint(QueryHints.LEFT_FETCH, "p.children.grandchildren");
            List<OrderByChild> children = query.getResultList().get(0).getChildren();
            assertEquals(List.of("c", "b", "a"), children.stream().map(OrderByChild::getName).toList());
            assertEquals(List.of(List.of("h2", "h1"), List.of("g3", "g2", "g1"), List.of()),
                    children.stream().map(child -> child.getGrandchildren().stream().map(OrderByGrandchild::getName).toList()).toList());
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    @Test
    public void testFetchJoinOrderedByEmbeddableAttribute() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByParent> query = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.coded ORDER BY p.id", OrderByParent.class);
            assertEquals(List.of("c", "b", "a"), query.getResultList().get(0).getCoded().stream().map(OrderByChild::getName).toList());
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    /**
     * An order by on a whole embeddable orders by the attributes of that embeddable, when the collection is loaded lazily
     * as well as when it is fetched.
     */
    @Test
    public void testOrderByWholeEmbeddable() {
        EntityManager em = emf.createEntityManager();
        try {
            assertEquals("Lazy", List.of("c", "b", "a"), em.find(OrderByParent.class, 1L).getDetailed().stream().map(OrderByChild::getName).toList());
        } finally {
            em.close();
        }
        emf.getCache().evictAll();
        em = emf.createEntityManager();
        try {
            TypedQuery<OrderByParent> query = em.createQuery("SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.detailed ORDER BY p.id", OrderByParent.class);
            assertEquals("Fetched", List.of("c", "b", "a"), query.getResultList().get(0).getDetailed().stream().map(OrderByChild::getName).toList());
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    /**
     * Ordering the query by the embeddable attribute which the order by of the fetched collection names does not repeat
     * that column.
     */
    @Test
    public void testFetchJoinOrderedByOrderByEmbeddableAttributeDoesNotRepeatOrderByItems() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByParent> query = em.createQuery(
                    "SELECT DISTINCT p FROM OrderByParent p JOIN FETCH p.coded c ORDER BY c.detail.code", OrderByParent.class);
            query.getResultList();
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    /**
     * Ordering the query by an embedded id does not repeat its columns as the primary key of the owner.
     */
    @Test
    public void testFetchJoinOnOwnerWithEmbeddedIdOrderedByIdDoesNotRepeatOrderByItems() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<OrderByKeyedParent> query = em.createQuery(
                    "SELECT DISTINCT k FROM OrderByKeyedParent k JOIN FETCH k.children ORDER BY k.id", OrderByKeyedParent.class);
            assertEquals(List.of("c", "b", "a"), query.getResultList().get(0).getChildren().stream().map(OrderByChild::getName).toList());
            assertNoRepeatedOrderByItems(query);
        } finally {
            em.close();
        }
    }

    @Test
    public void testJoinFetchOfMapping() {
        EntityManager em = emf.createEntityManager();
        try {
            OrderByFetchedParent parent = em.createQuery("SELECT f FROM OrderByFetchedParent f", OrderByFetchedParent.class).getSingleResult();
            assertEquals(List.of("c", "b", "a"), parent.getChildren().stream().map(OrderByChild::getName).toList());
        } finally {
            em.close();
        }
    }

    private static List<OrderByParent> readCursor(EntityManager em, String jpql) {
        ScrollableCursor cursor = (ScrollableCursor) em.createQuery(jpql, OrderByParent.class)
                .setHint(QueryHints.SCROLLABLE_CURSOR, true).unwrap(JpaQuery.class).getResultCursor();
        try {
            List<OrderByParent> parents = new ArrayList<>();
            while (cursor.hasNext()) {
                parents.add((OrderByParent) cursor.next());
            }
            return parents;
        } finally {
            cursor.close();
        }
    }

    private static List<String> assertNoRepeatedOrderByItems(TypedQuery<?> query) {
        return assertNoRepeatedOrderByItems(query.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString());
    }

    /**
     * Asserts that the given SQL has no repeated column in its order by, whatever the direction, because a database such
     * as SQL Server rejects a repeated column in the order by. The items of the order by must be plain columns.
     */
    private static List<String> assertNoRepeatedOrderByItems(String sql) {
        assertTrue("No order by in " + sql, sql.contains("ORDER BY"));
        String orderBy = sql.substring(sql.lastIndexOf("ORDER BY") + "ORDER BY".length()).split("(?i)\\s+(OFFSET|FETCH|LIMIT|FOR\\s+UPDATE)\\b")[0];
        List<String> columns = Arrays.stream(orderBy.trim().split(",\\s*"))
                .map(item -> item.replaceAll("(?i)(\\s+(ASC|DESC))?(\\s+NULLS\\s+(FIRST|LAST))?$", ""))
                .toList();
        assertEquals("Order by columns of " + sql, columns.stream().distinct().toList(), columns);
        return columns;
    }

    private void assertChildNames(List<List<String>> expected, Function<EntityManager, List<OrderByParent>> query) {
        EntityManager em = emf.createEntityManager();
        try {
            assertEquals(expected, childNames(query.apply(em)));
        } finally {
            em.close();
        }
    }

    private static List<List<String>> childNames(List<OrderByParent> parents) {
        return parents.stream().map(parent -> parent.getChildren().stream().map(OrderByChild::getName).toList()).toList();
    }
}
