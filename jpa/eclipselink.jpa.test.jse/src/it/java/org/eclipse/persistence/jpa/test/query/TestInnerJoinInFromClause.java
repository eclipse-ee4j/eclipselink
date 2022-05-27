/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.OrderHeader;
import org.eclipse.persistence.jpa.test.query.model.OrderItems;
import org.eclipse.persistence.sessions.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestInnerJoinInFromClause {

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {
                    OrderHeader.class, OrderItems.class, TestInnerJoinInFromClause.Customizer.class
            },
            properties = {
                    @Property(name = "eclipselink.cache.shared.default", value = "false"),
                    // This property remaps String from VARCHAR->NVARCHAR(or db equivalent)
                    @Property(name = "eclipselink.target-database-properties",
                              value = "UseNationalCharacterVaryingTypeForString=true"),
                    // Logging setup
                    @Property(name = "eclipselink.logging.level.sql", value = "FINE"),
                    @Property(name = "eclipselink.logging.parameters", value = "true"),
            })
    private EntityManagerFactory emf;

    private final OrderHeader[] DATA = initData();

    private static OrderHeader[] initData() {
        OrderHeader o1 = new OrderHeader(1);
        o1.addOrderItem(new OrderItems(101, o1));
        o1.addOrderItem(new OrderItems(102, o1));
        OrderHeader o2 = new OrderHeader(2);
        return new OrderHeader[]{o1, o2};
    }

    @Before
    public void setup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (OrderHeader o : DATA) {
                em.persist(o);
                for (OrderItems i : o.getOrderItems()) {
                    em.persist(i);
                }
            }
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    @After
    public void cleanup() {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM OrderItems i").executeUpdate();
            em.createQuery("DELETE FROM OrderHeader o").executeUpdate();
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    private EntityManager createEntityManager() {
        EntityManager em = emf.createEntityManager();
        Session session = em.unwrap(Session.class);
        session.getPlatform().setPrintInnerJoinInWhereClause(false);
        return em;
    }

    // mvn verify -pl :org.eclipse.persistence.jpa.jse.test -Dtest-skip-jpa-jse-deadlock=true -Dit.test=TestInnerJoinInFromClause#testWithJoinJoinInFrom
    @Test
    public void testWithJoinJoinInFrom() {
        try (final EntityManager em = createEntityManager()) {
            final String jpql = "SELECT o FROM OrderHeader o WHERE o.orderItems IS EMPTY";
            List<OrderHeader> result = em.createQuery(jpql, OrderHeader.class).getResultList();
            Assert.assertEquals(1, result.size());
            Assert.assertEquals(2L, result.get(0).getId());
        }
    }

    // mvn verify -pl :org.eclipse.persistence.jpa.jse.test -Dtest-skip-jpa-jse-deadlock=true -Dit.test=TestInnerJoinInFromClause#testWithNotExistQuery
    @Test
    public void testWithNotExistQuery() {
        try (final EntityManager em = createEntityManager()) {
            final String jpql = "SELECT o FROM OrderHeader o WHERE NOT EXISTS (SELECT 1 FROM OrderItems i WHERE i MEMBER OF o.orderItems)";
            List<OrderHeader> result = em.createQuery(jpql, OrderHeader.class).getResultList();
            Assert.assertEquals(1, result.size());
            Assert.assertEquals(2L, result.get(0).getId());
        }
    }

    public static class Customizer implements SessionCustomizer {

        public static boolean useDefaults = true;
        public static boolean printInnerJoinInWhereClause = false;
        public static boolean printOuterJoinInWhereClause = false;

        @Override
        public void customize(Session session) throws Exception {
            if (!useDefaults) {
                session.getPlatform().setPrintInnerJoinInWhereClause(printInnerJoinInWhereClause);
                session.getPlatform().setPrintOuterJoinInWhereClause(printOuterJoinInWhereClause);
            }
        }

    }

}
