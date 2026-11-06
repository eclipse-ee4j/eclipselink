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

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.QueryOrder;
import org.eclipse.persistence.jpa.test.query.model.QueryOrderLine;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Regression tests for issue #2965: a BATCH hint must not change a later
 * LEFT_FETCH query through the shared JPQL parse cache.
 * The order line's optional order models the reporter's Item.category.
 */
@RunWith(EmfRunner.class)
public class TestBatchLeftFetch {

    @Emf(name = "batchLeftFetchEMF", createTables = DDLGen.DROP_CREATE,
            classes = {QueryOrder.class, QueryOrderLine.class},
            properties = @Property(name = PersistenceUnitProperties.CACHE_SHARED_DEFAULT, value = "false"))
    private EntityManagerFactory emf;

    /**
     * Create order lines with and without an order, plus an order with no lines,
     * to expose differences between inner and outer fetching.
     */
    @Before
    public void setup() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                QueryOrder order = new QueryOrder();
                order.setOrderKey(1);
                em.persist(order);
                QueryOrder emptyOrder = new QueryOrder();
                emptyOrder.setOrderKey(2);
                em.persist(emptyOrder);

                QueryOrderLine withOrder = new QueryOrderLine();
                withOrder.setOrderLineKey(101);
                withOrder.setOrder(order);
                em.persist(withOrder);

                QueryOrderLine withoutOrder = new QueryOrderLine();
                withoutOrder.setOrderLineKey(102);
                em.persist(withoutOrder);
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    /**
     * Remove order lines before their referenced orders to reset the fixtures.
     */
    @After
    public void cleanup() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.createQuery("DELETE FROM QueryOrderLine").executeUpdate();
                em.createQuery("DELETE FROM QueryOrder").executeUpdate();
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    /**
     * Establish that LEFT_FETCH retains the null relationship without an earlier
     * BATCH hint on the same cached JPQL query.
     */
    @Test
    public void testLeftFetchWithoutBatchHint() {
        // A different alias keeps this control independent of the regression's cache entry.
        assertBothOrderLines("SELECT control FROM QueryOrderLine control", "control.queryOrder");
    }

    /**
     * Reproduce the reporter's sequence: apply BATCH without executing, then use
     * LEFT_FETCH on the same JPQL in another entity manager.
     */
    @Test
    public void testLeftFetchAfterBatchHintInAnotherEntityManager() {
        String jpql = "SELECT line FROM QueryOrderLine line";
        try (EntityManager em = emf.createEntityManager()) {
            // As in the report, applying BATCH is sufficient; do not execute this query.
            em.createQuery(jpql, QueryOrderLine.class).setHint(QueryHints.BATCH, "line.queryOrder");
        }

        assertBothOrderLines(jpql, "line.queryOrder");
    }

    /**
     * Verify that each fetch hint leaves subsequent inner, outer, and unhinted
     * queries independent when they reuse the same cached JPQL.
     */
    @Test
    public void testFetchHintsDoNotAffectSubsequentQueries() {
        String[] hints = {QueryHints.BATCH, QueryHints.FETCH, QueryHints.LEFT_FETCH};
        for (int i = 0; i < hints.length; i++) {
            String alias = "hint" + i;
            String jpql = "SELECT " + alias + " FROM QueryOrderLine " + alias;
            String relationship = alias + ".queryOrder";
            try (EntityManager em = emf.createEntityManager()) {
                assertEquals(hints[i].equals(QueryHints.FETCH) ? 1 : 2,
                        em.createQuery(jpql, QueryOrderLine.class).setHint(hints[i], relationship).getResultList().size());
            }
            assertBothOrderLines(jpql, relationship);
            try (EntityManager em = emf.createEntityManager()) {
                List<QueryOrderLine> lines = em.createQuery(jpql, QueryOrderLine.class)
                        .setHint(QueryHints.FETCH, relationship).getResultList();
                assertEquals(1, lines.size());
                assertEquals(101L, lines.get(0).getOrderLineKey());
                assertEquals(2, em.createQuery(jpql, QueryOrderLine.class).getResultList().size());
            }
            assertBothOrderLines(jpql, relationship);
        }
    }

    /**
     * Verify that applying BATCH cannot affect queries already created from the
     * shared template in either the same or another entity manager.
     */
    @Test
    public void testQueriesCreatedBeforeHintsRemainIndependent() {
        String jpql = "SELECT early FROM QueryOrderLine early";
        try (EntityManager first = emf.createEntityManager(); EntityManager second = emf.createEntityManager()) {
            TypedQuery<QueryOrderLine> batch = first.createQuery(jpql, QueryOrderLine.class);
            TypedQuery<QueryOrderLine> sameManager = first.createQuery(jpql, QueryOrderLine.class);
            TypedQuery<QueryOrderLine> otherManager = second.createQuery(jpql, QueryOrderLine.class);
            batch.setHint(QueryHints.BATCH, "early.queryOrder");
            assertEquals(2, sameManager.setHint(QueryHints.LEFT_FETCH, "early.queryOrder").getResultList().size());
            assertEquals(2, otherManager.setHint(QueryHints.LEFT_FETCH, "early.queryOrder").getResultList().size());
        }
    }

    /**
     * Verify that a query made private by a timeout hint can later receive BATCH
     * without modifying the shared template's expression graph.
     */
    @Test
    public void testNonFetchHintBeforeBatchHint() {
        String jpql = "SELECT timeoutFirst FROM QueryOrderLine timeoutFirst";
        try (EntityManager em = emf.createEntityManager()) {
            em.createQuery(jpql, QueryOrderLine.class)
                    .setHint(QueryHints.JDBC_TIMEOUT, 10)
                    .setHint(QueryHints.BATCH, "timeoutFirst.queryOrder");
        }
        assertBothOrderLines(jpql, "timeoutFirst.queryOrder");
    }

    /**
     * Verify that IN batch fetching preserves parameters and ordering and leaves
     * a later LEFT_FETCH query's null relationship intact.
     */
    @Test
    public void testParameterizedOrderedBatchQuery() {
        String jpql = "SELECT ordered FROM QueryOrderLine ordered WHERE ordered.orderLineKey IN :ids ORDER BY ordered.orderLineKey";
        try (EntityManager em = emf.createEntityManager()) {
            List<QueryOrderLine> lines = em.createQuery(jpql, QueryOrderLine.class)
                    .setParameter("ids", List.of(101L, 102L))
                    .setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN)
                    .setHint(QueryHints.BATCH, "ordered.queryOrder").getResultList();
            assertEquals(2, lines.size());
            assertEquals(1L, lines.get(0).getOrder().getOrderKey());
            assertNull(lines.get(1).getOrder());
        }
        try (EntityManager em = emf.createEntityManager()) {
            List<QueryOrderLine> lines = em.createQuery(jpql, QueryOrderLine.class)
                    .setParameter("ids", List.of(101L, 102L))
                    .setHint(QueryHints.LEFT_FETCH, "ordered.queryOrder").getResultList();
            assertEquals(List.of(101L, 102L), lines.stream().map(QueryOrderLine::getOrderLineKey).toList());
        }
    }

    /**
     * Verify independent fetch hints on a collection, including whether an owner
     * with an empty collection remains in the results.
     */
    @Test
    public void testCollectionFetchHints() {
        String jpql = "SELECT collectionOwner FROM QueryOrder collectionOwner";
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals(2, em.createQuery(jpql, QueryOrder.class)
                    .setHint(QueryHints.BATCH, "collectionOwner.queryOrderLines").getResultList().size());
        }
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals(2, em.createQuery(jpql, QueryOrder.class)
                    .setHint(QueryHints.LEFT_FETCH, "collectionOwner.queryOrderLines").getResultList().size());
            assertEquals(1, em.createQuery(jpql, QueryOrder.class)
                    .setHint(QueryHints.FETCH, "collectionOwner.queryOrderLines").getResultList().size());
        }
    }

    /**
     * Verify that BATCH on a nested relationship path cannot turn a later
     * LEFT_FETCH on that path into an inner join.
     */
    @Test
    public void testNestedFetchHints() {
        String jpql = "SELECT nested FROM QueryOrderLine nested";
        String path = "nested.queryOrder.queryOrderLines";
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals(2, em.createQuery(jpql, QueryOrderLine.class)
                    .setHint(QueryHints.BATCH, path).getResultList().size());
        }
        assertBothOrderLines(jpql, path);
    }

    /**
     * Verify that BATCH preserves an explicit outer join and FETCH changes only
     * its own query, leaving the cached JPQL join unchanged.
     */
    @Test
    public void testBatchPreservesExplicitOuterJoin() {
        String jpql = "SELECT explicitJoin FROM QueryOrderLine explicitJoin LEFT JOIN FETCH explicitJoin.queryOrder";
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals(2, em.createQuery(jpql, QueryOrderLine.class)
                    .setHint(QueryHints.BATCH, "explicitJoin.queryOrder").getResultList().size());
            assertEquals(1, em.createQuery(jpql, QueryOrderLine.class)
                    .setHint(QueryHints.FETCH, "explicitJoin.queryOrder").getResultList().size());
        }
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals(2, em.createQuery(jpql, QueryOrderLine.class).getResultList().size());
        }
    }

    /**
     * Verify that instances of a registered named query retain independent fetch
     * hints across entity managers.
     */
    @Test
    public void testNamedQueriesRemainIndependent() {
        String name = "batchLeftFetchNamedQuery";
        try (EntityManager em = emf.createEntityManager()) {
            emf.addNamedQuery(name, em.createQuery("SELECT namedLine FROM QueryOrderLine namedLine", QueryOrderLine.class));
            em.createNamedQuery(name, QueryOrderLine.class).setHint(QueryHints.BATCH, "namedLine.queryOrder");
        }
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals(2, em.createNamedQuery(name, QueryOrderLine.class)
                    .setHint(QueryHints.LEFT_FETCH, "namedLine.queryOrder").getResultList().size());
            assertEquals(1, em.createNamedQuery(name, QueryOrderLine.class)
                    .setHint(QueryHints.FETCH, "namedLine.queryOrder").getResultList().size());
        }
    }

    /**
     * Verify that a non-SQL hint preserves a report query's prepared state and
     * grouped results, and subsequent hints reuse the private query instance.
     */
    @Test
    public void testReportQueryWithNonSqlHint() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Object[]> query = em.createQuery("SELECT reportLine.orderLineKey, COUNT(reportLine) FROM QueryOrderLine reportLine "
                    + "GROUP BY reportLine.orderLineKey HAVING COUNT(reportLine) > 0 ORDER BY reportLine.orderLineKey", Object[].class);
            query.setHint(QueryHints.READ_ONLY, true);
            DatabaseQuery databaseQuery = query.unwrap(JpaQuery.class).getDatabaseQuery();
            assertTrue("Copying must preserve prepared SQL", databaseQuery.isPrepared());
            query.setHint(QueryHints.READ_ONLY, false);
            assertSame(databaseQuery, query.unwrap(JpaQuery.class).getDatabaseQuery());
            List<Object[]> rows = query.getResultList();
            assertEquals(2, rows.size());
            assertEquals(101L, rows.get(0)[0]);
            assertEquals(1L, rows.get(0)[1]);
            assertEquals(102L, rows.get(1)[0]);
        }
    }

    /**
     * Verify that fetch hints remain isolated when the shared JPQL contains a
     * subquery correlated with the outer query's builder.
     */
    @Test
    public void testCorrelatedSubqueryWithFetchHint() {
        String jpql = "SELECT correlated FROM QueryOrderLine correlated WHERE EXISTS "
                + "(SELECT otherLine.orderLineKey FROM QueryOrderLine otherLine WHERE otherLine.orderLineKey = correlated.orderLineKey) "
                + "ORDER BY correlated.orderLineKey";
        try (EntityManager em = emf.createEntityManager()) {
            em.createQuery(jpql, QueryOrderLine.class).setHint(QueryHints.BATCH, "correlated.queryOrder");
        }
        assertBothOrderLines(jpql, "correlated.queryOrder");
    }

    /**
     * Execute LEFT_FETCH in a fresh entity manager and verify both order lines,
     * including the line whose optional order is null.
     */
    private void assertBothOrderLines(String jpql, String relationship) {
        try (EntityManager em = emf.createEntityManager()) {
            List<QueryOrderLine> lines = em.createQuery(jpql, QueryOrderLine.class)
                    .setHint(QueryHints.LEFT_FETCH, relationship)
                    .getResultList();

            assertEquals("LEFT_FETCH must retain the order line whose order is null", 2, lines.size());
            QueryOrderLine withOrder = lines.stream().filter(line -> line.getOrderLineKey() == 101)
                    .findFirst().orElseThrow();
            QueryOrderLine withoutOrder = lines.stream().filter(line -> line.getOrderLineKey() == 102)
                    .findFirst().orElseThrow();
            assertEquals(1L, withOrder.getOrder().getOrderKey());
            assertNull(withoutOrder.getOrder());
        }
    }
}
