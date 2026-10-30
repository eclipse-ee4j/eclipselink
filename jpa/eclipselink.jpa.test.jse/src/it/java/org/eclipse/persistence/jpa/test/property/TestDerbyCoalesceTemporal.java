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
package org.eclipse.persistence.jpa.test.property;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.jpa.test.criteria.model.CoalesceTemporalEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestDerbyCoalesceTemporal {
    @Emf(name = "derbyCoalesceTemporal",
         createTables = DDLGen.DROP_CREATE,
         classes = { CoalesceTemporalEntity.class },
         properties = {
             @Property(name = "eclipselink.jdbc.bind-parameters", value = "false"),
             @Property(name = "eclipselink.jdbc.native-sql", value = "false")
         })
    private EntityManagerFactory emf;

    @Test
    public void testUnboundCoalesceReturnsTemporalFallback() {
        Assume.assumeTrue(emf.unwrap(EntityManagerFactoryImpl.class)
                .getDatabaseSession().getPlatform().isDerby());
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // All temporal columns are null, but a row must exist to evaluate COALESCE.
            em.persist(new CoalesceTemporalEntity(1));
            em.getTransaction().commit();
            em.clear();

            Date fallback = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
            for (boolean subquery : new boolean[] { false, true }) {
                for (boolean literal : new boolean[] { false, true }) {
                    for (String attribute : new String[] { "timestampValue", "dateValue", "timeValue" }) {
                        CriteriaBuilder builder = em.getCriteriaBuilder();
                        CriteriaQuery<Date> query = builder.createQuery(Date.class);
                        Root<CoalesceTemporalEntity> root = query.from(CoalesceTemporalEntity.class);
                        Expression<Date> value = root.get(attribute);
                        if (subquery) {
                            Subquery<Date> select = query.subquery(Date.class);
                            Root<CoalesceTemporalEntity> inner = select.from(CoalesceTemporalEntity.class);
                            select.select(inner.<Date>get(attribute));
                            select.where(builder.equal(inner.get("id"), 1));
                            value = select;
                        }
                        query.select(literal
                                ? builder.coalesce(value, builder.literal(fallback))
                                : builder.coalesce(value, fallback));
                        query.where(builder.equal(root.get("id"), 1));
                        List<Date> results = em.createQuery(query).getResultList();
                        String context = attribute + ", subquery=" + subquery + ", literal=" + literal;
                        Assert.assertEquals(context, 1, results.size());
                        Date actual = results.get(0);
                        Assert.assertNotNull(context, actual);
                        if ("dateValue".equals(attribute)) {
                            Assert.assertEquals(context,
                                    java.sql.Date.valueOf("2026-04-13").toLocalDate(),
                                    new java.sql.Date(actual.getTime()).toLocalDate());
                        } else if ("timeValue".equals(attribute)) {
                            Assert.assertEquals(context, Time.valueOf("17:57:41").toLocalTime(),
                                    new Time(actual.getTime()).toLocalTime());
                        } else {
                            Assert.assertEquals(context, fallback.getTime(), actual.getTime());
                        }
                        Assert.assertEquals("Rendering must not change the fallback",
                                Timestamp.valueOf("2026-04-13 17:57:41.123").getTime(), fallback.getTime());
                    }
                }
            }
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }
}
