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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.jpa.querydef.ConstructorSelectionImpl;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaBuilderImpl;
import org.eclipse.persistence.internal.queries.ReportItem;
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
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ScrollableCursor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A fetch join retrieves the elements of a collection in the order of its {@code @OrderBy}, as the specification of
 * {@code jakarta.persistence.OrderBy} requires "at the point when the association or collection is retrieved". The
 * explicit ordering of the query takes precedence, and collection ordering supplies trailing SQL terms.
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

    @Test
    public void testReportJpqlFetchJoin() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Object[]> rows = em.createQuery(
                    "SELECT p, p.id FROM OrderByParent p JOIN FETCH p.children WHERE p.id = 1", Object[].class).getResultList();
            assertTrue("Report must return the fetched owner", !rows.isEmpty());
            for (Object[] row : rows) {
                assertEquals(2, row.length);
                OrderByParent parent = (OrderByParent) row[0];
                assertEquals(parent.getId(), row[1]);
                assertTrue("Report collection must be fetched", emf.getPersistenceUnitUtil().isLoaded(parent, "children"));
                assertEquals(List.of("c", "b", "a"), parent.getChildren().stream().map(OrderByChild::getName).toList());
            }
        }
    }

    @Test
    public void testReportCriteriaTypedMultiselectFetch() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ParentResult> criteria = cb.createQuery(ParentResult.class);
            Root<OrderByParent> parent = criteria.from(OrderByParent.class);
            parent.fetch("children");
            criteria.multiselect(parent, parent.get("id")).where(cb.equal(parent.get("id"), 1));
            List<ParentResult> rows = em.createQuery(criteria).getResultList();
            assertTrue("Constructor must return the fetched owner", !rows.isEmpty());
            for (ParentResult row : rows) {
                assertEquals(row.parent.getId(), row.id.longValue());
                assertTrue("Constructor collection must be fetched", emf.getPersistenceUnitUtil().isLoaded(row.parent, "children"));
                assertEquals(List.of("c", "b", "a"), row.parent.getChildren().stream().map(OrderByChild::getName).toList());
            }
        }
    }

    public static class ParentResult {
        public final OrderByParent parent;
        public final Long id;

        public ParentResult(OrderByParent parent, Long id) {
            this.parent = parent;
            this.id = id;
        }
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

    @Test
    public void testReportJpqlScalarPositionsAndDistinct() {
        for (boolean scalarFirst : List.of(false, true)) {
            for (boolean distinct : List.of(false, true)) {
                withColdHandlers(handler -> {
                    String selection = scalarFirst ? "p.id, p" : "p, p.id";
                    TypedQuery<Object[]> query = handler.createQuery("SELECT " + (distinct ? "DISTINCT " : "") + selection
                            + " FROM OrderByParent p JOIN FETCH p.children ORDER BY p.id DESC", Object[].class);
                    List<Object[]> rows = query.getResultList();
                    assertReportRows(rows, scalarFirst, distinct ? List.of(2L, 1L) : List.of(2L, 2L, 1L, 1L, 1L));
                    assertNoRepeatedOrderByItems(query);
                });
            }
        }
    }

    @Test
    public void testReportCriteriaArraySelections() {
        for (boolean explicitArray : List.of(false, true)) {
            withColdHandlers(handler -> {
                CriteriaBuilder cb = handler.getCriteriaBuilder();
                CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
                Root<OrderByParent> parent = criteria.from(OrderByParent.class);
                parent.fetch("children");
                if (explicitArray) {
                    criteria.select(cb.array(parent.get("id"), parent));
                } else {
                    criteria.multiselect(parent.get("id"), parent);
                }
                criteria.orderBy(cb.asc(parent.get("id")));
                assertReportRows(handler.createQuery(criteria).getResultList(), true, List.of(1L, 1L, 1L, 2L, 2L));
            });
        }
    }

    @Test
    public void testReportCriteriaTupleSelections() {
        for (boolean explicitTuple : List.of(false, true)) {
            withColdHandlers(handler -> {
                CriteriaBuilder cb = handler.getCriteriaBuilder();
                CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
                Root<OrderByParent> parent = criteria.from(OrderByParent.class);
                parent.fetch("children", JoinType.LEFT);
                var id = parent.get("id").alias("number");
                parent.alias("owner");
                if (explicitTuple) {
                    criteria.select(cb.tuple(id, parent));
                } else {
                    criteria.multiselect(id, parent);
                }
                criteria.orderBy(cb.asc(parent.get("id")));
                List<Tuple> rows = handler.createQuery(criteria).getResultList();
                assertEquals(List.of(1L, 1L, 1L, 2L, 2L, 3L), rows.stream().map(row -> row.get("number", Long.class)).toList());
                for (Tuple row : rows) {
                    assertEquals(2, row.getElements().size());
                    assertEquals("number", row.getElements().get(0).getAlias());
                    assertEquals("owner", row.getElements().get(1).getAlias());
                    assertEquals(id.getJavaType(), row.getElements().get(0).getJavaType());
                    assertEquals(OrderByParent.class, row.getElements().get(1).getJavaType());
                    assertEquals(row.get(0), row.get("number"));
                    assertSame(row.get(1), row.get("owner"));
                    assertSame(row.get(1), row.get(row.getElements().get(1)));
                    assertReportParent(row.get("owner", OrderByParent.class));
                }
            });
        }
    }

    @Test
    public void testReportCriteriaConstructors() {
        for (boolean multiselect : List.of(false, true)) {
            withColdHandlers(handler -> {
                CriteriaBuilder cb = handler.getCriteriaBuilder();
                CriteriaQuery<ParentResult> criteria = cb.createQuery(ParentResult.class);
                Root<OrderByParent> parent = criteria.from(OrderByParent.class);
                parent.fetch("children");
                if (multiselect) {
                    criteria.multiselect(parent, parent.get("id"));
                } else {
                    criteria.select(cb.construct(ParentResult.class, parent, parent.get("id")));
                }
                criteria.orderBy(cb.asc(parent.get("id")));
                List<ParentResult> rows = handler.createQuery(criteria).getResultList();
                assertEquals(List.of(1L, 1L, 1L, 2L, 2L), rows.stream().map(row -> row.id).toList());
                rows.forEach(this::assertParentResult);
            });
        }
    }

    @Test
    public void testReportNestedCriteriaConstructors() {
        withColdHandlers(handler -> {
            CriteriaBuilder cb = handler.getCriteriaBuilder();
            CriteriaQuery<ParentEnvelope> criteria = cb.createQuery(ParentEnvelope.class);
            Root<OrderByParent> parent = criteria.from(OrderByParent.class);
            parent.fetch("children", JoinType.LEFT);
            criteria.select(cb.construct(ParentEnvelope.class,
                    cb.construct(ParentResult.class, parent, parent.get("id")), parent.get("id")));
            criteria.where(cb.equal(parent.get("id"), 3));
            ParentEnvelope row = handler.createQuery(criteria).getSingleResult();
            assertEquals(Long.valueOf(3), row.id);
            assertParentResult(row.result);
        });
        withColdHandlers(handler -> {
            CriteriaBuilder cb = handler.getCriteriaBuilder();
            CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
            Root<OrderByParent> parent = criteria.from(OrderByParent.class);
            parent.fetch("children");
            criteria.select(cb.tuple(cb.construct(ParentResult.class, parent, parent.get("id")).alias("result"),
                    parent.get("id").alias("number")));
            criteria.where(cb.equal(parent.get("id"), 1));
            List<Tuple> rows = handler.createQuery(criteria).getResultList();
            assertEquals(3, rows.size());
            for (Tuple row : rows) {
                assertEquals(2, row.getElements().size());
                assertEquals(ParentResult.class, row.getElements().get(0).getJavaType());
                assertEquals(Long.valueOf(1), row.get("number"));
                assertParentResult(row.get("result", ParentResult.class));
            }
        });
    }

    @Test
    public void testReportJpqlConstructors() {
        for (boolean explicitConstructor : List.of(false, true)) {
            withColdHandlers(handler -> {
                String selection = explicitConstructor ? "NEW " + ParentResult.class.getName() + "(p, p.id)" : "p, p.id";
                List<ParentResult> rows = handler.createQuery("SELECT " + selection
                        + " FROM OrderByParent p JOIN FETCH p.children WHERE p.id = 1", ParentResult.class).getResultList();
                assertEquals(3, rows.size());
                rows.forEach(this::assertParentResult);
            });
        }
        withColdHandlers(handler -> {
            List<ParentOnly> rows = handler.createQuery(
                    "SELECT p FROM OrderByParent p JOIN FETCH p.children WHERE p.id = 1", ParentOnly.class).getResultList();
            assertEquals(3, rows.size());
            rows.forEach(row -> assertReportParent(row.parent));
        });
    }

    @Test
    public void testReportTwoFetchedOwners() {
        withColdHandlers(handler -> {
            TypedQuery<Object[]> query = handler.createQuery(
                    "SELECT p, p.id, q, q.id FROM OrderByParent p JOIN FETCH p.children, OrderByParent q JOIN FETCH q.children"
                            + " WHERE q.id = 2 AND p.id = 1 ORDER BY q.id DESC, p.id", Object[].class);
            List<Object[]> rows = query.getResultList();
            assertEquals(6, rows.size());
            for (Object[] row : rows) {
                assertEquals(4, row.length);
                assertEquals(1L, row[1]);
                assertEquals(2L, row[3]);
                assertReportParent((OrderByParent) row[0]);
                assertReportParent((OrderByParent) row[2]);
            }
            assertEquals(4, assertNoRepeatedOrderByItems(query).size());
        });
    }

    @Test
    public void testReportOwnerDistinctFromQueryReferenceClass() {
        try (EntityManager em = emf.createEntityManager()) {
            ReportQuery query = new ReportQuery(OrderByChild.class, new ExpressionBuilder(OrderByChild.class));
            ExpressionBuilder parent = new ExpressionBuilder(OrderByParent.class);
            query.addAttribute("id", parent.get("id"));
            query.addItem("parent", parent, List.of(parent.anyOf("children")));
            query.setShouldReturnWithoutReportQueryResult(true);
            ExpressionBuilder child = query.getExpressionBuilder();
            query.setSelectionCriteria(child.get("id").equal(11).and(child.get("parent").equal(parent)));
            assertEquals(OrderByChild.class, query.getReferenceClass());
            assertEquals(OrderByParent.class, query.getItems().get(1).getAttributeExpression().getBuilder().getQueryClass());
            @SuppressWarnings("unchecked")
            List<Object[]> rows = (List<Object[]>) em.unwrap(JpaEntityManager.class).getActiveSession().executeQuery(query);
            assertReportRows(rows, true, List.of(1L, 1L, 1L));
            assertEquals(OrderByChild.class, query.getDescriptor().getJavaClass());
            assertEquals(OrderByParent.class, query.getItems().get(1).getDescriptor().getJavaClass());
        }
    }

    @Test
    public void testReportTwoFetchedCollections() {
        withColdHandlers(handler -> {
            TypedQuery<Object[]> query = handler.createQuery(
                    "SELECT f, f.id FROM OrderByFetchedParent f JOIN FETCH f.children JOIN FETCH f.others, OrderByChild x WHERE x.id = 11", Object[].class);
            List<Object[]> rows = query.getResultList();
            assertEquals(6, rows.size());
            for (Object[] row : rows) {
                assertEquals(2, row.length);
                assertEquals(1L, row[1]);
                OrderByFetchedParent parent = (OrderByFetchedParent) row[0];
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(parent, "children"));
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(parent, "others"));
                assertEquals(List.of("c", "b", "a"), parent.getChildren().stream().map(OrderByChild::getName).toList());
                assertEquals(List.of("d", "bb"), parent.getOthers().stream().map(OrderByChild::getName).toList());
            }
            assertEquals(3, assertNoRepeatedOrderByItems(query).size());
        });
    }

    @Test
    public void testReportOuterFetchKeepsEmptyCollection() {
        withColdHandlers(handler -> {
            List<Object[]> rows = handler.createQuery(
                    "SELECT p, p.id FROM OrderByParent p LEFT JOIN FETCH p.children ORDER BY p.id", Object[].class).getResultList();
            assertReportRows(rows, false, List.of(1L, 1L, 1L, 2L, 2L, 3L));
        });
        withColdHandlers(handler -> assertTrue(handler.createQuery(
                "SELECT p, p.id FROM OrderByParent p JOIN FETCH p.children WHERE p.id = 3", Object[].class).getResultList().isEmpty()));
    }

    @Test
    public void testReportExplicitChildOrderTakesPrecedence() {
        try (EntityManager em = emf.createEntityManager()) {
            ReportQuery query = new ReportQuery(OrderByParent.class, new ExpressionBuilder(OrderByParent.class));
            Expression children = query.getExpressionBuilder().anyOf("children");
            query.addItem("parent", query.getExpressionBuilder(), List.of(children));
            query.addAttribute("id");
            query.setShouldReturnWithoutReportQueryResult(true);
            query.addOrdering(children.get("name").ascending());
            @SuppressWarnings("unchecked")
            List<Object[]> rows = (List<Object[]>) em.unwrap(JpaEntityManager.class).getActiveSession().executeQuery(query);
            assertEquals(5, rows.size());
            assertEquals(List.of(1L, 1L, 2L, 1L, 2L), rows.stream().map(row -> (Long) row[1]).toList());
            for (Object[] row : rows) {
                OrderByParent parent = (OrderByParent) row[0];
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(parent, "children"));
                assertEquals(parent.getId() == 1 ? List.of("a", "b", "c") : List.of("bb", "d"),
                        parent.getChildren().stream().map(OrderByChild::getName).toList());
            }
            List<String> columns = assertNoRepeatedOrderByItems(query.getSQLString());
            assertEquals(2, columns.size());
            assertTrue(columns.get(0), columns.get(0).endsWith(".NAME"));
        }
        withColdHandlers(handler -> {
            TypedQuery<Object[]> query = handler.createQuery(
                    "SELECT p, p.id FROM OrderByParent p JOIN FETCH p.children ORDER BY MOD(p.id, 1)", Object[].class);
            List<Object[]> rows = query.getResultList();
            assertEquals(5, rows.size());
            rows.forEach(row -> assertReportParent((OrderByParent) row[0]));
            String sql = query.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString();
            assertTrue(sql, sql.contains("ORDER BY"));
            assertEquals(1, ((ReadAllQuery) query.unwrap(JpaQuery.class).getDatabaseQuery()).getOrderByExpressions().size());
        });
    }

    @Test
    public void testReportReprepareAndRepeatDoNotAccumulateOrdering() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT p, p.id FROM OrderByParent p JOIN FETCH p.children ORDER BY p.id", Object[].class);
            assertReportRows(query.getResultList(), false, List.of(1L, 1L, 1L, 2L, 2L));
            List<String> original = assertNoRepeatedOrderByItems(query);
            em.clear();
            emf.getCache().evictAll();
            query.setHint(QueryHints.HINT, "/* report-order-reprepare */");
            assertFalse("Hint must invalidate the prepared query", query.unwrap(JpaQuery.class).getDatabaseQuery().isPrepared());
            assertReportRows(query.getResultList(), false, List.of(1L, 1L, 1L, 2L, 2L));
            assertTrue(query.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString().contains("/* report-order-reprepare */"));
            assertEquals(original.size(), assertNoRepeatedOrderByItems(query).size());
            em.clear();
            emf.getCache().evictAll();
            assertReportRows(query.getResultList(), false, List.of(1L, 1L, 1L, 2L, 2L));
            assertEquals(original.size(), assertNoRepeatedOrderByItems(query).size());
        }
    }

    @Test
    public void testReportConstructorSnapshotIndependence() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ParentEnvelope> criteria = cb.createQuery(ParentEnvelope.class);
            Root<OrderByParent> parent = criteria.from(OrderByParent.class);
            parent.fetch("children");
            criteria.select(cb.construct(ParentEnvelope.class,
                    cb.construct(ParentResult.class, parent, parent.get("id")), parent.get("id")));
            criteria.where(cb.equal(parent.get("id"), 1));
            criteria.orderBy(cb.asc(parent.get("id")));
            TypedQuery<ParentEnvelope> original = em.createQuery(criteria);
            parent.fetch("labels", JoinType.LEFT);
            criteria.where(cb.equal(parent.get("id"), 2));
            TypedQuery<ParentEnvelope> changed = em.createQuery(criteria);
            for (TypedQuery<ParentEnvelope> query : List.of(original, changed, original)) {
                em.clear();
                emf.getCache().evictAll();
                List<ParentEnvelope> rows = query.getResultList();
                long id = query == changed ? 2L : 1L;
                assertEquals(query == changed ? 2 : 3, rows.size());
                for (ParentEnvelope row : rows) {
                    assertEquals(Long.valueOf(id), row.id);
                    assertParentResult(row.result);
                    assertEquals(query == changed, emf.getPersistenceUnitUtil().isLoaded(row.result.parent, "labels"));
                }
                assertNoRepeatedOrderByItems(query);
            }
        }
    }

    @Test
    public void testReportOrderedElementCollections() {
        for (String attribute : List.of("labels", "tags", "coded", "remarks", "quoted")) {
            withColdHandlers(handler -> {
                TypedQuery<Object[]> query = handler.createQuery("SELECT p, p.id FROM OrderByParent p JOIN FETCH p." + attribute
                        + " WHERE p.id = 1", Object[].class);
                List<Object[]> rows = query.getResultList();
                assertEquals(3, rows.size());
                for (Object[] row : rows) {
                    assertEquals(2, row.length);
                    assertEquals(1L, row[1]);
                    OrderByParent parent = (OrderByParent) row[0];
                    assertTrue(attribute, emf.getPersistenceUnitUtil().isLoaded(parent, attribute));
                    switch (attribute) {
                        case "labels" -> assertEquals(List.of("z", "y", "x"), parent.getLabels());
                        case "tags" -> assertEquals(List.of("z", "y", "x"), parent.getTags().stream().map(OrderByTag::getLabel).toList());
                        case "coded" -> assertEquals(List.of("c", "b", "a"), parent.getCoded().stream().map(OrderByChild::getName).toList());
                        case "remarks" -> assertEquals(Set.of("r1", "r2", "r3"), Set.copyOf(parent.getRemarks()));
                        case "quoted" -> assertEquals(Set.of("q1", "q2", "q3"), Set.copyOf(parent.getQuoted()));
                        default -> throw new AssertionError(attribute);
                    }
                }
                if (attribute.equals("remarks") || attribute.equals("quoted")) {
                    assertFalse(query.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString().contains("ORDER BY"));
                } else {
                    assertNoRepeatedOrderByItems(query);
                }
            });
        }
    }

    @Test
    public void testReportScalarAndUnfetchedControls() {
        withColdHandlers(handler -> {
            TypedQuery<Long> scalar = handler.createQuery("SELECT p.id FROM OrderByParent p ORDER BY p.id", Long.class);
            assertEquals(List.of(1L, 2L, 3L), scalar.getResultList());
            assertEquals(1, assertNoRepeatedOrderByItems(scalar).size());
            TypedQuery<Long> count = handler.createQuery("SELECT COUNT(p) FROM OrderByParent p", Long.class);
            assertEquals(Long.valueOf(3), count.getSingleResult());
            assertFalse(count.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString().contains("ORDER BY"));
            TypedQuery<Object[]> grouped = handler.createQuery(
                    "SELECT c.parent.id, COUNT(c) FROM OrderByChild c GROUP BY c.parent.id ORDER BY c.parent.id", Object[].class);
            List<Object[]> rows = grouped.getResultList();
            assertEquals(2, rows.size());
            assertArrayEquals(new Object[] {1L, 3L}, rows.get(0));
            assertArrayEquals(new Object[] {2L, 2L}, rows.get(1));
            assertEquals(1, assertNoRepeatedOrderByItems(grouped).size());
            TypedQuery<ParentResult> constructor = handler.createQuery("SELECT p, p.id FROM OrderByParent p WHERE p.id = 1", ParentResult.class);
            ParentResult row = constructor.getSingleResult();
            assertEquals(Long.valueOf(1), row.id);
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.parent, "children"));
            assertFalse(constructor.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString().contains("ORDER BY"));
        });
        try (EntityManager em = emf.createEntityManager()) {
            emf.getCache().evictAll();
            OrderByParent parent = (OrderByParent) em.createQuery(
                    "SELECT p, p.id FROM OrderByParent p WHERE p.id = 1", Object[].class).getSingleResult()[0];
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(parent, "children"));
            assertEquals(List.of("c", "b", "a"), parent.getChildren().stream().map(OrderByChild::getName).toList());
        }
    }

    @Test
    public void testConstructorFetchIsCompleteBeforeConstructionAndAfterCursorDrain() {
        for (ResultMode mode : List.of(ResultMode.SCROLLABLE, ResultMode.CURSOR, ResultMode.LIST)) {
            for (boolean distinct : List.of(false, true)) {
                withColdHandlers(handler -> {
                    TypedQuery<FetchedParentResult> query = fetchedConstructor(handler, "children", 1, distinct, false);
                    List<FetchedParentResult> rows = results(query, mode);
                    assertEquals(distinct ? 1 : 3, rows.size());
                    assertFetchedResults(rows, "children", List.of("c", "b", "a"));
                    assertFalse("Unrequested collection must remain lazy", emf.getPersistenceUnitUtil().isLoaded(rows.get(0).parent, "labels"));
                    assertConstructorLoadMetadata(query, "children");
                });
            }
        }
    }

    @Test
    public void testConstructorCursorRetainsTheCompleteFirstOwner() {
        for (ResultMode mode : List.of(ResultMode.SCROLLABLE, ResultMode.CURSOR)) {
            withColdHandlers(handler -> {
                CriteriaBuilder cb = handler.getCriteriaBuilder();
                CriteriaQuery<ParentResult> criteria = cb.createQuery(ParentResult.class);
                Root<OrderByParent> parent = criteria.from(OrderByParent.class);
                parent.fetch("children");
                criteria.select(cb.construct(ParentResult.class, parent, parent.get("id")));
                criteria.where(cb.equal(parent.get("id"), 1)).orderBy(cb.asc(parent.get("id")));
                TypedQuery<ParentResult> query = handler.createQuery(criteria);
                Cursor cursor = openCursor(query, mode);
                try {
                    assertTrue(cursor.hasNext());
                    ParentResult first = (ParentResult) cursor.next();
                    assertTrue(emf.getPersistenceUnitUtil().isLoaded(first.parent, "children"));
                    assertEquals(List.of("c", "b", "a"), collectionNames(first.parent, "children"));
                    assertParentResult(first);
                    int count = 1;
                    while (cursor.hasNext()) {
                        assertParentResult((ParentResult) cursor.next());
                        count++;
                    }
                    assertEquals(3, count);
                    assertParentResult(first);
                } finally {
                    cursor.close();
                }
                assertConstructorLoadMetadata(query, "children");
            });
        }
    }

    @Test
    public void testConstructorFetchPreservesDuplicateCollectionOccurrences() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            OrderByParent labels = new OrderByParent(4);
            labels.getLabels().addAll(List.of("b", "a", "a", "c"));
            OrderByParent tags = new OrderByParent(5);
            tags.getTags().addAll(List.of(new OrderByTag("b"), new OrderByTag("a"), new OrderByTag("a"), new OrderByTag("c")));
            em.persist(labels);
            em.persist(tags);
            em.getTransaction().commit();
        }
        for (String attribute : List.of("labels", "tags")) {
            long id = attribute.equals("labels") ? 4L : 5L;
            emf.getCache().evictAll();
            try (EntityManager em = emf.createEntityManager()) {
                assertEquals(Long.valueOf(4), em.createQuery("SELECT COUNT(p.id) FROM OrderByParent p JOIN p."
                        + attribute + " element WHERE p.id = :id", Long.class).setParameter("id", id).getSingleResult());
                OrderByParent parent = em.find(OrderByParent.class, id);
                assertFalse(emf.getPersistenceUnitUtil().isLoaded(parent, attribute));
                assertEquals(List.of("c", "b", "a", "a"), collectionNames(parent, attribute));
            }
            for (ResultMode mode : ResultMode.values()) {
                for (boolean distinct : List.of(false, true)) {
                    withColdHandlers(handler -> {
                        TypedQuery<FetchedParentResult> query = fetchedConstructor(handler, attribute, id, distinct, false);
                        List<FetchedParentResult> rows = results(query, mode);
                        assertEquals(distinct ? 1 : 4, rows.size());
                        assertFetchedResults(rows, attribute, List.of("c", "b", "a", "a"));
                        assertFalse(emf.getPersistenceUnitUtil().isLoaded(rows.get(0).parent, "children"));
                        assertConstructorLoadMetadata(query, attribute);
                    });
                }
            }
        }
    }

    @Test
    public void testConstructorOuterFetchKeepsAnEmptyLoadedCollection() {
        for (ResultMode mode : ResultMode.values()) {
            withColdHandlers(handler -> {
                List<FetchedParentResult> rows = results(fetchedConstructor(handler, "children", 3, false, true), mode);
                assertEquals(1, rows.size());
                assertFetchedResults(rows, "children", List.of());
                assertTrue(results(fetchedConstructor(handler, "children", 3, false, false), mode).isEmpty());
            });
        }
    }

    @Test
    public void testConstructorLeavesKeepIndependentSameClassFetchGroups() {
        for (ResultMode mode : ResultMode.values()) {
            withColdHandlers(handler -> {
                CriteriaBuilder cb = handler.getCriteriaBuilder();
                CriteriaQuery<FetchedPairResult> criteria = cb.createQuery(FetchedPairResult.class);
                Root<OrderByParent> first = criteria.from(OrderByParent.class);
                Root<OrderByParent> second = criteria.from(OrderByParent.class);
                first.fetch("children");
                second.fetch("labels");
                criteria.select(cb.construct(FetchedPairResult.class,
                        cb.construct(FetchedParentResult.class, first, first.get("id"), cb.literal("children")),
                        cb.construct(FetchedParentResult.class, second, second.get("id"), cb.literal("labels"))));
                criteria.where(cb.and(cb.equal(first.get("id"), 2), cb.equal(second.get("id"), 1)));
                criteria.orderBy(cb.desc(first.get("id")), cb.asc(second.get("id")));
                TypedQuery<FetchedPairResult> query = handler.createQuery(criteria);
                List<FetchedPairResult> rows = results(query, mode);
                assertEquals(6, rows.size());
                for (FetchedPairResult row : rows) {
                    assertFetchedResults(List.of(row.first), "children", List.of("d", "bb"));
                    assertFetchedResults(List.of(row.second), "labels", List.of("z", "y", "x"));
                    assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.first.parent, "labels"));
                    assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.second.parent, "children"));
                }
            });
        }
    }

    @Test
    public void testConstructorNestedFetchesUseOwnerRelativePaths() {
        for (ResultMode mode : ResultMode.values()) {
            withColdHandlers(handler -> {
                CriteriaBuilder cb = handler.getCriteriaBuilder();
                CriteriaQuery<FetchedParentResult> criteria = cb.createQuery(FetchedParentResult.class);
                Root<OrderByParent> parent = criteria.from(OrderByParent.class);
                parent.fetch("children").fetch("grandchildren", JoinType.LEFT);
                parent.fetch("labels");
                criteria.select(cb.construct(FetchedParentResult.class, parent, parent.get("id"), cb.literal("nested")));
                criteria.where(cb.equal(parent.get("id"), 1));
                TypedQuery<FetchedParentResult> query = handler.createQuery(criteria);
                List<FetchedParentResult> rows = results(query, mode);
                assertEquals(18, rows.size());
                assertFetchedResults(rows, "nested", List.of("c", "b", "a"));
                for (FetchedParentResult row : rows) {
                    assertTrue(row.nestedLoadedAtConstruction);
                    assertEquals(List.of(List.of("h2", "h1"), List.of("g3", "g2", "g1"), List.of()), row.grandchildrenAtConstruction);
                    assertTrue(emf.getPersistenceUnitUtil().isLoaded(row.parent, "labels"));
                    assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.parent, "tags"));
                }
                ReportItem item = constructorLeaf(query);
                assertTrue(item.getLoadGroup().getGroup("children").containsAttribute("grandchildren"));
                assertTrue(item.getLoadGroup().containsAttribute("labels"));
            });
        }
    }

    @Test
    public void testRepeatedConstructorFetchQueryCreation() {
        withColdHandlers(handler -> {
            CriteriaBuilder cb = handler.getCriteriaBuilder();
            CriteriaQuery<ParentResult> criteria = cb.createQuery(ParentResult.class);
            Root<OrderByParent> parent = criteria.from(OrderByParent.class);
            parent.fetch("children").fetch("grandchildren", JoinType.LEFT);
            parent.fetch("children").fetch("grandchildren", JoinType.LEFT);
            criteria.select(cb.construct(ParentResult.class, parent, parent.get("id")));
            criteria.where(cb.equal(parent.get("id"), 1));
            assertTrue(handler.createQuery(criteria).unwrap(JpaQuery.class).getDatabaseQuery().isReportQuery());
        });
    }

    @Test
    public void testConstructorRepeatedNestedFetchesComposePaths() {
        for (ResultMode mode : ResultMode.values()) {
            for (String shape : List.of("identical", "bare-first", "nested-first")) {
                for (long id : List.of(1L, 3L)) {
                    withColdHandlers(handler -> {
                        CriteriaBuilder cb = handler.getCriteriaBuilder();
                        CriteriaQuery<FetchedParentResult> criteria = cb.createQuery(FetchedParentResult.class);
                        Root<OrderByParent> parent = criteria.from(OrderByParent.class);
                        JoinType joinType = id == 3 ? JoinType.LEFT : JoinType.INNER;
                        var first = parent.fetch("children", joinType);
                        if (!shape.equals("bare-first")) {
                            first.fetch("grandchildren", JoinType.LEFT);
                        }
                        var second = parent.fetch("children", joinType);
                        if (!shape.equals("nested-first")) {
                            second.fetch("grandchildren", JoinType.LEFT);
                        }
                        criteria.select(cb.construct(FetchedParentResult.class, parent, parent.get("id"), cb.literal("nested")));
                        criteria.where(cb.equal(parent.get("id"), id));
                        TypedQuery<FetchedParentResult> query = handler.createQuery(criteria);
                        List<FetchedParentResult> rows = results(query, mode);
                        assertEquals(id == 3 ? 1 : shape.equals("identical") ? 36 : 18, rows.size());
                        assertFetchedResults(rows, "nested", id == 3 ? List.of() : List.of("c", "b", "a"));
                        for (FetchedParentResult row : rows) {
                            assertTrue(row.nestedLoadedAtConstruction);
                            assertEquals(id == 3 ? List.of() : List.of(List.of("h2", "h1"), List.of("g3", "g2", "g1"), List.of()),
                                    row.grandchildrenAtConstruction);
                            for (OrderByChild child : row.parent.getChildren()) {
                                assertTrue(emf.getPersistenceUnitUtil().isLoaded(child, "grandchildren"));
                            }
                            assertEquals(row.grandchildrenAtConstruction, row.parent.getChildren().stream()
                                    .map(child -> child.getGrandchildren().stream().map(OrderByGrandchild::getName).toList()).toList());
                            assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.parent, "labels"));
                            assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.parent, "tags"));
                        }
                        assertTrue(constructorLeaf(query).getLoadGroup().getGroup("children").containsAttribute("grandchildren"));
                        assertIndependentSqlFetches(query, shape.equals("identical") ? 4 : 3);
                    });
                }
            }
        }
    }

    @Test
    public void testConstructorRepeatedParentFetchesKeepDivergentPaths() {
        for (ResultMode mode : ResultMode.values()) {
            for (boolean tuple : List.of(false, true)) {
                withColdHandlers(handler -> {
                    CriteriaBuilder cb = handler.getCriteriaBuilder();
                    CriteriaQuery<?> criteria;
                    Root<OrderByChild> child;
                    if (tuple) {
                        CriteriaQuery<Tuple> projection = cb.createTupleQuery();
                        child = projection.from(OrderByChild.class);
                        projection.multiselect(cb.construct(FetchedChildGraphResult.class, child, child.get("parent")).alias("graph"),
                                child.get("id").alias("number"));
                        criteria = projection;
                    } else {
                        CriteriaQuery<FetchedChildGraphResult> projection = cb.createQuery(FetchedChildGraphResult.class);
                        child = projection.from(OrderByChild.class);
                        projection.select(cb.construct(FetchedChildGraphResult.class, child, child.get("parent")));
                        criteria = projection;
                    }
                    child.fetch("parent").fetch("children");
                    child.fetch("parent").fetch("labels");
                    criteria.where(cb.equal(child.get("id"), 11));
                    TypedQuery<?> query = handler.createQuery(criteria);
                    List<?> rows = results(query, mode);
                    assertEquals(9, rows.size());
                    for (Object result : rows) {
                        FetchedChildGraphResult row;
                        if (tuple) {
                            Tuple value = (Tuple) result;
                            assertEquals(2, value.getElements().size());
                            assertEquals(Long.valueOf(11), value.get("number", Long.class));
                            row = value.get("graph", FetchedChildGraphResult.class);
                            assertSame(row, value.get(0));
                        } else {
                            row = (FetchedChildGraphResult) result;
                        }
                        assertTrue(row.loadedAtConstruction);
                        assertEquals(List.of("c", "b", "a"), row.childrenAtConstruction);
                        assertEquals(List.of("z", "y", "x"), row.labelsAtConstruction);
                        assertTrue(emf.getPersistenceUnitUtil().isLoaded(row.parent, "children"));
                        assertTrue(emf.getPersistenceUnitUtil().isLoaded(row.parent, "labels"));
                        assertEquals(row.childrenAtConstruction, collectionNames(row.parent, "children"));
                        assertEquals(row.labelsAtConstruction, collectionNames(row.parent, "labels"));
                        assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.parent, "tags"));
                        assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.child, "grandchildren"));
                    }
                    var group = constructorLeaf(query).getLoadGroup().getGroup("parent");
                    assertTrue(group.containsAttribute("children"));
                    assertTrue(group.containsAttribute("labels"));
                    assertFalse(group.containsAttribute("tags"));
                    assertIndependentSqlFetches(query, 4);
                });
            }
        }
    }

    private static void assertIndependentSqlFetches(TypedQuery<?> query, int count) {
        List<Expression> expressions = ((ReportQuery) query.unwrap(JpaQuery.class).getDatabaseQuery()).getNonFetchJoinAttributeExpressions();
        assertEquals(count, expressions.size());
        for (int i = 0; i < expressions.size(); i++) {
            for (int j = i + 1; j < expressions.size(); j++) {
                assertNotSame("Repeated loading paths must retain their independent SQL expressions", expressions.get(i), expressions.get(j));
            }
        }
    }

    @Test
    public void testConstructorSelectedJoinLoadsItsOwnDescriptorAndAllowsNullOwner() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(new OrderByChild(99, "orphan", null));
            em.getTransaction().commit();
        }
        for (ResultMode mode : ResultMode.values()) {
            for (long childId : List.of(11L, 99L)) {
                withColdHandlers(handler -> {
                    CriteriaBuilder cb = handler.getCriteriaBuilder();
                    CriteriaQuery<FetchedParentResult> criteria = cb.createQuery(FetchedParentResult.class);
                    Root<OrderByChild> child = criteria.from(OrderByChild.class);
                    var parent = child.join("parent", JoinType.LEFT);
                    parent.fetch("children", JoinType.LEFT);
                    criteria.select(cb.construct(FetchedParentResult.class, parent, child.get("id"), cb.literal("children")));
                    criteria.where(cb.equal(child.get("id"), childId));
                    TypedQuery<FetchedParentResult> query = handler.createQuery(criteria);
                    List<FetchedParentResult> rows = results(query, mode);
                    assertEquals(childId == 11 ? 3 : 1, rows.size());
                    if (childId == 11) {
                        assertFetchedResults(rows, "children", List.of("c", "b", "a"));
                    } else {
                        assertNull(rows.get(0).parent);
                        assertNull(rows.get(0).namesAtConstruction);
                    }
                    ReportQuery report = (ReportQuery) query.unwrap(JpaQuery.class).getDatabaseQuery();
                    assertEquals(OrderByChild.class, report.getReferenceClass());
                    var session = emf.unwrap(org.eclipse.persistence.jpa.JpaEntityManagerFactory.class).getServerSession();
                    var mapping = constructorLeaf(query).getAttributeExpression()
                            .getLeafMapping(report, session.getDescriptor(OrderByChild.class), session);
                    assertEquals(OrderByParent.class, mapping.getReferenceDescriptor().getJavaClass());
                });
            }
        }
    }

    @Test
    public void testConstructorExtensionJoinsRetainNormalMappingGraph() {
        for (ResultMode mode : ResultMode.values()) {
            for (String extension : List.of("ON", "WHERE", "ASC")) {
                withColdHandlers(handler -> {
                    CriteriaBuilder cb = handler.getCriteriaBuilder();
                    CriteriaQuery<FetchedParentResult> criteria = cb.createQuery(FetchedParentResult.class);
                    Root<OrderByParent> parent = criteria.from(OrderByParent.class);
                    var child = (jakarta.persistence.criteria.Join<?, ?>) parent.fetch("children");
                    if (extension.equals("ON")) {
                        child.on(cb.notEqual(child.get("name"), cb.parameter(String.class, "excluded")));
                    }
                    criteria.select(cb.construct(FetchedParentResult.class, parent, parent.get("id"), cb.literal("children")));
                    criteria.where(extension.equals("WHERE")
                            ? cb.and(cb.equal(parent.get("id"), 1), cb.notEqual(child.get("name"), cb.parameter(String.class, "excluded")))
                            : cb.equal(parent.get("id"), 1));
                    criteria.orderBy(cb.asc(extension.equals("ASC") ? child.get("name") : parent.get("id")));
                    TypedQuery<FetchedParentResult> query = handler.createQuery(criteria);
                    if (!extension.equals("ASC")) {
                        query.setParameter("excluded", "a");
                    }
                    List<FetchedParentResult> rows = results(query, mode);
                    assertEquals(extension.equals("ASC") ? 3 : 2, rows.size());
                    assertFetchedResults(rows, "children", List.of("c", "b", "a"));
                    assertConstructorLoadMetadata(query, "children");
                });
            }
        }
    }

    @Test
    public void testExistingFilteredReportFetchRetainsItsSubset() {
        withColdHandlers(handler -> {
            CriteriaBuilder cb = handler.getCriteriaBuilder();
            CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
            Root<OrderByParent> root = criteria.from(OrderByParent.class);
            var child = (jakarta.persistence.criteria.Join<?, ?>) root.fetch("children");
            child.on(cb.equal(child.get("name"), "c"));
            criteria.multiselect(root, root.get("id")).where(cb.equal(root.get("id"), 1));
            TypedQuery<Object[]> query = handler.createQuery(criteria);
            query.setHint(QueryHints.MAINTAIN_CACHE, false);
            List<Object[]> rows = query.getResultList();
            assertEquals(1, rows.size());
            OrderByParent parent = (OrderByParent) rows.get(0)[0];
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(parent, "children"));
            assertEquals(List.of("c"), collectionNames(parent, "children"));
            ReportItem item = ((ReportQuery) query.unwrap(JpaQuery.class).getDatabaseQuery()).getItems().get(0);
            assertTrue(item.hasJoining());
            assertNull(item.getLoadGroup());
        });
    }

    @Test
    public void testConstructorSelectionSerializationPreservesMetadataAndLegacyTranslation() throws Exception {
        assertEquals(-8383364112562934574L, ObjectStreamClass.lookup(ConstructorSelectionImpl.class).getSerialVersionUID());
        // Detached literal selections keep live sessions and metamodels out of the serialized graph.
        CriteriaBuilder cb = new CriteriaBuilderImpl(null);
        var owner = cb.nullLiteral(OrderByParent.class).alias("owner");
        var number = cb.literal(7L).alias("number");
        ConstructorSelectionImpl<ParentResult> nested = (ConstructorSelectionImpl<ParentResult>)
                cb.construct(ParentResult.class, owner, number).alias("nested");
        ConstructorSelectionImpl<ParentEnvelope> selection = (ConstructorSelectionImpl<ParentEnvelope>)
                cb.construct(ParentEnvelope.class, nested, number).alias("envelope");
        selection.setConstructor(ParentEnvelope.class.getConstructor(ParentResult.class, Long.class));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(selection);
        }
        selection.alias("changedEnvelope");
        nested.alias("changedNested");
        number.alias("changedNumber");
        ConstructorSelectionImpl<?> restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (ConstructorSelectionImpl<?>) input.readObject();
        }
        assertNotSame(selection, restored);
        assertEquals("envelope", restored.getAlias());
        assertEquals(ParentEnvelope.class, restored.getJavaType());
        assertEquals(2, restored.getCompoundSelectionItems().size());
        ConstructorSelectionImpl<?> restoredNested = (ConstructorSelectionImpl<?>) restored.getCompoundSelectionItems().get(0);
        assertNotSame(nested, restoredNested);
        assertEquals("nested", restoredNested.getAlias());
        assertEquals(ParentResult.class, restoredNested.getJavaType());
        assertEquals("number", restored.getCompoundSelectionItems().get(1).getAlias());
        assertSame(restored.getCompoundSelectionItems().get(1), restoredNested.getCompoundSelectionItems().get(1));
        ConstructorReportItem translated = restored.translate();
        assertEquals(ParentEnvelope.class, translated.getResultType());
        assertNull("The reflection constructor remains transient", translated.getConstructor());
        assertEquals(2, translated.getReportItems().size());
        ConstructorReportItem translatedNested = (ConstructorReportItem) translated.getReportItems().get(0);
        assertEquals(ParentResult.class, translatedNested.getResultType());
        assertEquals(2, translatedNested.getReportItems().size());
        ReportItem ownerItem = translatedNested.getReportItems().get(0);
        ReportItem numberItem = translatedNested.getReportItems().get(1);
        assertEquals(OrderByParent.class, ownerItem.getResultType());
        assertNull(((ConstantExpression) ownerItem.getAttributeExpression()).getValue());
        assertEquals(Long.class, numberItem.getResultType());
        assertEquals(7L, ((ConstantExpression) numberItem.getAttributeExpression()).getValue());
        for (ReportItem item : List.of(ownerItem, numberItem, translated.getReportItems().get(1))) {
            assertNull(item.getLoadGroup());
            assertFalse(item.hasJoining());
        }
    }

    @Test
    public void testLegacyConstructorTranslationAndUnfetchedLeavesKeepNoLoadGroup() {
        withColdHandlers(handler -> {
            CriteriaBuilder cb = handler.getCriteriaBuilder();
            CriteriaQuery<ParentResult> criteria = cb.createQuery(ParentResult.class);
            Root<OrderByParent> parent = criteria.from(OrderByParent.class);
            parent.fetch("children");
            ConstructorSelectionImpl<ParentResult> selection = (ConstructorSelectionImpl<ParentResult>)
                    cb.construct(ParentResult.class, parent, parent.get("id"));
            ConstructorReportItem legacy = selection.translate();
            for (ReportItem item : legacy.getReportItems()) {
                assertNull(item.getLoadGroup());
                assertFalse(item.hasJoining());
            }

            CriteriaQuery<ParentResult> unfetched = cb.createQuery(ParentResult.class);
            Root<OrderByParent> plain = unfetched.from(OrderByParent.class);
            unfetched.select(cb.construct(ParentResult.class, plain, plain.get("id"))).where(cb.equal(plain.get("id"), 1));
            TypedQuery<ParentResult> query = handler.createQuery(unfetched);
            ParentResult row = query.getSingleResult();
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.parent, "children"));
            for (ReportItem item : ((ConstructorReportItem) ((ReportQuery) query.unwrap(JpaQuery.class).getDatabaseQuery()).getItems().get(0)).getReportItems()) {
                assertNull(item.getLoadGroup());
                assertFalse(item.hasJoining());
            }
        });
    }

    @Test
    public void testConstructorToOneFetchDoesNotLoadUnrelatedCollections() {
        for (ResultMode mode : ResultMode.values()) {
            withColdHandlers(handler -> {
                CriteriaBuilder cb = handler.getCriteriaBuilder();
                CriteriaQuery<FetchedChildResult> criteria = cb.createQuery(FetchedChildResult.class);
                Root<OrderByChild> child = criteria.from(OrderByChild.class);
                child.fetch("parent");
                criteria.select(cb.construct(FetchedChildResult.class, child)).where(cb.equal(child.get("id"), 11));
                TypedQuery<FetchedChildResult> query = handler.createQuery(criteria);
                List<FetchedChildResult> rows = results(query, mode);
                assertEquals(1, rows.size());
                assertTrue(rows.get(0).parentLoadedAtConstruction);
                assertFalse(emf.getPersistenceUnitUtil().isLoaded(rows.get(0).child, "grandchildren"));
                assertConstructorLoadMetadata(query, "parent");
            });
        }
    }

    @Test
    public void testConstructorCursorEarlyCloseNavigationAndListReuse() {
        for (ResultMode mode : List.of(ResultMode.SCROLLABLE, ResultMode.CURSOR)) {
            withColdHandlers(handler -> {
                var server = emf.unwrap(org.eclipse.persistence.jpa.JpaEntityManagerFactory.class).getServerSession();
                var pool = server.getReadConnectionPool();
                int borrowedBefore = pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size();
                List<java.sql.Statement> statements = new ArrayList<>();
                List<java.sql.ResultSet> resultSets = new ArrayList<>();
                int[] calls = new int[2];
                var listener = cursorCalls(statements, resultSets, calls);
                server.getEventManager().addListener(listener);
                try {
                    TypedQuery<FetchedParentResult> query = fetchedConstructor(handler, "children", 1, false, false);
                    Cursor cursor = openCursor(query, mode);
                    try {
                        assertTrue(cursor.hasNext());
                        FetchedParentResult first = (FetchedParentResult) cursor.next();
                        assertFetchedResults(List.of(first), "children", List.of("c", "b", "a"));
                        assertFalse(cursor.isClosed());
                        if (cursor instanceof ScrollableCursor scrollable) {
                            assertTrue(scrollable.absolute(2));
                            FetchedParentResult previous = (FetchedParentResult) scrollable.previous();
                            assertFetchedResults(List.of(previous), "children", List.of("c", "b", "a"));
                        } else {
                            ((org.eclipse.persistence.queries.CursoredStream) cursor).releasePrevious();
                            assertTrue(cursor.hasNext());
                            assertFetchedResults(List.of((FetchedParentResult) cursor.next()), "children", List.of("c", "b", "a"));
                        }
                    } finally {
                        cursor.close();
                    }
                    assertCursorResourcesClosed(statements, resultSets);
                    assertEquals("The report must execute once, without replay", 1, calls[0]);
                    assertTrue("Fetch completion uses normal relationship SQL", calls[1] > 0);
                    ReportQuery report = (ReportQuery) query.unwrap(JpaQuery.class).getDatabaseQuery();
                    report.useCollectionClass(ArrayList.class);
                    report.setIsPrepared(false);
                    if (handler instanceof EntityManager em) {
                        em.clear();
                    }
                    emf.getCache().evictAll();
                    List<FetchedParentResult> rows = query.getResultList();
                    assertEquals(3, rows.size());
                    assertFetchedResults(rows, "children", List.of("c", "b", "a"));
                    assertEquals(2, calls[0]);
                    if (handler instanceof EntityManager em) {
                        em.clear();
                    }
                    emf.getCache().evictAll();
                    cursor = openCursor(query, mode);
                    rows = new ArrayList<>();
                    try {
                        assertTrue(cursor.hasNext());
                        FetchedParentResult first = (FetchedParentResult) cursor.next();
                        assertFetchedResults(List.of(first), "children", List.of("c", "b", "a"));
                        rows.add(first);
                        while (cursor.hasNext()) {
                            rows.add((FetchedParentResult) cursor.next());
                        }
                    } finally {
                        cursor.close();
                    }
                    assertEquals(3, rows.size());
                    assertFetchedResults(rows, "children", List.of("c", "b", "a"));
                    assertTrue(cursor.isClosed());
                    assertEquals(2, statements.size());
                    assertCursorResourcesClosed(statements, resultSets);
                    assertEquals("Cursor, list and cursor must each execute the report once", 3, calls[0]);
                    assertConstructorLoadMetadata(query, "children");
                } finally {
                    server.getEventManager().removeListener(listener);
                }
                assertEquals(borrowedBefore, pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size());
            });
        }
    }

    @Test
    public void testConstructorFailureClosesCursorResources() {
        for (ResultMode mode : List.of(ResultMode.SCROLLABLE, ResultMode.CURSOR)) {
            withColdHandlers(handler -> {
                var server = emf.unwrap(org.eclipse.persistence.jpa.JpaEntityManagerFactory.class).getServerSession();
                var pool = server.getReadConnectionPool();
                int borrowedBefore = pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size();
                List<java.sql.Statement> statements = new ArrayList<>();
                List<java.sql.ResultSet> resultSets = new ArrayList<>();
                var listener = cursorCalls(statements, resultSets, new int[2]);
                server.getEventManager().addListener(listener);
                Cursor cursor = null;
                try {
                    CriteriaBuilder cb = handler.getCriteriaBuilder();
                    CriteriaQuery<FailingFetchedParentResult> criteria = cb.createQuery(FailingFetchedParentResult.class);
                    Root<OrderByParent> parent = criteria.from(OrderByParent.class);
                    parent.fetch("children");
                    criteria.select(cb.construct(FailingFetchedParentResult.class, parent, parent.get("id"), cb.literal("children")));
                    criteria.where(cb.equal(parent.get("id"), 1));
                    try {
                        cursor = openCursor(handler.createQuery(criteria), mode);
                        cursor.next();
                        throw new AssertionError("Constructor failure must propagate");
                    } catch (RuntimeException expected) {
                        Throwable cause = expected;
                        while (cause.getCause() != null) {
                            cause = cause.getCause();
                        }
                        assertEquals("constructor failure after complete fetch", cause.getMessage());
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    server.getEventManager().removeListener(listener);
                }
                assertFalse(statements.isEmpty());
                assertCursorResourcesClosed(statements, resultSets);
                assertEquals(borrowedBefore, pool.getTotalNumberOfConnections() - pool.getConnectionsAvailable().size());
            });
        }
    }

    private static org.eclipse.persistence.sessions.SessionEventAdapter cursorCalls(List<java.sql.Statement> statements,
            List<java.sql.ResultSet> results, int[] calls) {
        return new org.eclipse.persistence.sessions.SessionEventAdapter() {
            @Override
            public void postExecuteCall(org.eclipse.persistence.sessions.SessionEvent event) {
                if (event.getCall() instanceof org.eclipse.persistence.internal.databaseaccess.DatabaseCall call) {
                    calls[call.getQuery().isReportQuery() ? 0 : 1]++;
                    if (call.isCursorReturned()) {
                        statements.add(call.getStatement());
                        results.add(call.getResult());
                    }
                }
            }
        };
    }

    private static void assertCursorResourcesClosed(List<java.sql.Statement> statements, List<java.sql.ResultSet> results) {
        assertFalse("The cursor statement must be observed", statements.isEmpty());
        assertEquals(statements.size(), results.size());
        try {
            for (java.sql.Statement statement : statements) {
                assertTrue(statement.isClosed());
            }
            for (java.sql.ResultSet result : results) {
                assertTrue(result.isClosed());
            }
        } catch (java.sql.SQLException exception) {
            throw new AssertionError(exception);
        }
    }

    private TypedQuery<FetchedParentResult> fetchedConstructor(EntityHandler handler, String attribute, long id, boolean distinct, boolean outer) {
        CriteriaBuilder cb = handler.getCriteriaBuilder();
        CriteriaQuery<FetchedParentResult> criteria = cb.createQuery(FetchedParentResult.class);
        Root<OrderByParent> parent = criteria.from(OrderByParent.class);
        parent.fetch(attribute, outer ? JoinType.LEFT : JoinType.INNER);
        criteria.select(cb.construct(FetchedParentResult.class, parent, parent.get("id"), cb.literal(attribute)));
        criteria.where(cb.equal(parent.get("id"), id)).orderBy(cb.asc(parent.get("id"))).distinct(distinct);
        return handler.createQuery(criteria);
    }

    private static ReportItem constructorLeaf(TypedQuery<?> query) {
        ReportQuery report = (ReportQuery) query.unwrap(JpaQuery.class).getDatabaseQuery();
        return ((ConstructorReportItem) report.getItems().get(0)).getReportItems().get(0);
    }

    private static void assertConstructorLoadMetadata(TypedQuery<?> query, String attribute) {
        ReportItem item = constructorLeaf(query);
        assertFalse("New constructor fetches must not use joined hydration", item.hasJoining());
        assertTrue(item.getLoadGroup().containsAttribute(attribute));
    }

    private void assertFetchedResults(List<FetchedParentResult> rows, String attribute, List<String> expected) {
        for (FetchedParentResult row : rows) {
            assertTrue("Fetch must be loaded before the constructor consumes its argument", row.loadedAtConstruction);
            assertEquals(expected, row.namesAtConstruction);
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(row.parent, attribute.equals("nested") ? "children" : attribute));
            assertEquals("Collection must remain complete after draining", expected, collectionNames(row.parent, attribute));
        }
    }

    private static List<String> collectionNames(OrderByParent parent, String attribute) {
        return switch (attribute) {
            case "labels" -> new ArrayList<>(parent.getLabels());
            case "tags" -> parent.getTags().stream().map(OrderByTag::getLabel).toList();
            default -> parent.getChildren().stream().map(OrderByChild::getName).toList();
        };
    }

    private enum ResultMode { LIST, SCROLLABLE, CURSOR }

    @SuppressWarnings("unchecked")
    private static <T> List<T> results(TypedQuery<T> query, ResultMode mode) {
        if (mode == ResultMode.LIST) {
            return query.getResultList();
        }
        Cursor cursor = openCursor(query, mode);
        List<T> rows = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                rows.add((T) cursor.next());
            }
        } finally {
            cursor.close();
        }
        assertTrue(cursor.isClosed());
        return rows;
    }

    private static Cursor openCursor(TypedQuery<?> query, ResultMode mode) {
        if (mode == ResultMode.SCROLLABLE) {
            query.setHint(QueryHints.SCROLLABLE_CURSOR, true).setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ReadOnly);
        } else {
            query.setHint(QueryHints.CURSOR, true).setHint(QueryHints.CURSOR_INITIAL_SIZE, 1).setHint(QueryHints.CURSOR_PAGE_SIZE, 1);
        }
        return (Cursor) query.unwrap(JpaQuery.class).getResultCursor();
    }

    public static class FetchedParentResult extends ParentResult {
        public final boolean loadedAtConstruction;
        public final List<String> namesAtConstruction;
        public final boolean nestedLoadedAtConstruction;
        public final List<List<String>> grandchildrenAtConstruction;

        public FetchedParentResult(OrderByParent parent, Long id, String attribute) {
            super(parent, id);
            String collection = attribute.equals("nested") ? "children" : attribute;
            loadedAtConstruction = parent != null && Persistence.getPersistenceUtil().isLoaded(parent, collection);
            namesAtConstruction = loadedAtConstruction ? collectionNames(parent, attribute) : null;
            nestedLoadedAtConstruction = attribute.equals("nested") && loadedAtConstruction
                    && parent.getChildren().stream().allMatch(child -> Persistence.getPersistenceUtil().isLoaded(child, "grandchildren"));
            grandchildrenAtConstruction = nestedLoadedAtConstruction
                    ? parent.getChildren().stream().map(child -> child.getGrandchildren().stream().map(OrderByGrandchild::getName).toList()).toList() : null;
        }
    }

    public static class FetchedPairResult {
        public final FetchedParentResult first;
        public final FetchedParentResult second;

        public FetchedPairResult(FetchedParentResult first, FetchedParentResult second) {
            this.first = first;
            this.second = second;
        }
    }

    public static class FetchedChildResult {
        public final OrderByChild child;
        public final boolean parentLoadedAtConstruction;

        public FetchedChildResult(OrderByChild child) {
            this.child = child;
            parentLoadedAtConstruction = Persistence.getPersistenceUtil().isLoaded(child, "parent");
        }
    }

    public static class FetchedChildGraphResult {
        public final OrderByChild child;
        public final OrderByParent parent;
        public final boolean loadedAtConstruction;
        public final List<String> childrenAtConstruction;
        public final List<String> labelsAtConstruction;

        public FetchedChildGraphResult(OrderByChild child, OrderByParent parent) {
            this.child = child;
            this.parent = parent;
            loadedAtConstruction = Persistence.getPersistenceUtil().isLoaded(child, "parent")
                    && Persistence.getPersistenceUtil().isLoaded(parent, "children")
                    && Persistence.getPersistenceUtil().isLoaded(parent, "labels");
            childrenAtConstruction = loadedAtConstruction ? collectionNames(parent, "children") : null;
            labelsAtConstruction = loadedAtConstruction ? collectionNames(parent, "labels") : null;
        }
    }

    public static class FailingFetchedParentResult extends FetchedParentResult {
        public FailingFetchedParentResult(OrderByParent parent, Long id, String attribute) {
            super(parent, id, attribute);
            assertTrue(loadedAtConstruction);
            assertEquals(List.of("c", "b", "a"), namesAtConstruction);
            throw new IllegalStateException("constructor failure after complete fetch");
        }
    }

    private void withColdHandlers(Consumer<EntityHandler> assertions) {
        emf.getCache().evictAll();
        try (EntityManager em = emf.createEntityManager()) {
            assertions.accept(em);
        }
        emf.getCache().evictAll();
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertions.accept(agent);
        }
    }

    private void assertReportRows(List<Object[]> rows, boolean scalarFirst, List<Long> ids) {
        assertEquals(ids.size(), rows.size());
        List<Long> actual = new ArrayList<>();
        for (Object[] row : rows) {
            assertEquals(2, row.length);
            OrderByParent parent = (OrderByParent) row[scalarFirst ? 1 : 0];
            assertEquals(parent.getId(), row[scalarFirst ? 0 : 1]);
            assertReportParent(parent);
            actual.add(parent.getId());
        }
        assertEquals(ids, actual);
    }

    private void assertParentResult(ParentResult row) {
        assertEquals(Long.valueOf(row.parent.getId()), row.id);
        assertReportParent(row.parent);
    }

    private void assertReportParent(OrderByParent parent) {
        assertTrue("Report collection must be fetched", emf.getPersistenceUnitUtil().isLoaded(parent, "children"));
        List<String> expected = switch ((int) parent.getId()) {
            case 1 -> List.of("c", "b", "a");
            case 2 -> List.of("d", "bb");
            case 3 -> List.of();
            default -> throw new AssertionError("Unexpected owner " + parent.getId());
        };
        assertEquals(expected, parent.getChildren().stream().map(OrderByChild::getName).toList());
    }

    public static class ParentEnvelope {
        public final ParentResult result;
        public final Long id;

        public ParentEnvelope(ParentResult result, Long id) {
            this.result = result;
            this.id = id;
        }
    }

    public static class ParentOnly {
        public final OrderByParent parent;

        public ParentOnly(OrderByParent parent) {
            this.parent = parent;
        }
    }

    private static List<OrderByParent> readCursor(EntityManager em, String jpql) {
        ScrollableCursor cursor = (ScrollableCursor) em.createQuery(jpql, OrderByParent.class)
                .setHint(QueryHints.SCROLLABLE_CURSOR, true)
                .setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ReadOnly)
                .unwrap(JpaQuery.class).getResultCursor();
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
