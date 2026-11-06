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

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.QueryOrder;
import org.eclipse.persistence.jpa.test.query.model.QueryOrderLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

    @Before
    public void setup() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                QueryOrder order = new QueryOrder();
                order.setOrderKey(1);
                em.persist(order);

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

    @Test
    public void testLeftFetchWithoutBatchHint() {
        // A different alias keeps this control independent of the regression's cache entry.
        assertBothOrderLines("SELECT control FROM QueryOrderLine control", "control.queryOrder");
    }

    @Test
    public void testLeftFetchAfterBatchHintInAnotherEntityManager() {
        String jpql = "SELECT line FROM QueryOrderLine line";
        try (EntityManager em = emf.createEntityManager()) {
            // As in the report, applying BATCH is sufficient; do not execute this query.
            em.createQuery(jpql, QueryOrderLine.class).setHint(QueryHints.BATCH, "line.queryOrder");
        }

        assertBothOrderLines(jpql, "line.queryOrder");
    }

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
