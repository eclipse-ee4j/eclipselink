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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.querydef.InternalSelection;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.test.criteria.model.CoalesceTemporalEntity;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.queries.ReportQuery;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestDerbyCoalesceTemporal {
    @Emf(name = "derbyCoalesceTemporal",
         createTables = DDLGen.DROP_CREATE,
         classes = { CoalesceTemporalEntity.class, CoalesceTemporalEntity.IdentityTimestampConverter.class,
                 CoalesceTemporalEntity.ShiftedTimestampConverter.class,
                 CoalesceTemporalEntity.NormalizingTimestampConverter.class },
         properties = @Property(name = "eclipselink.jdbc.native-sql", value = "false"))
    private EntityManagerFactory emf;

    @Test
    public void testUnboundCoalesceReturnsTemporalFallback() {
        verifyTemporalFallbacks(false, false);
    }

    @Test
    public void testBoundCoalesceReturnsTemporalFallback() {
        verifyTemporalFallbacks(true, false);
    }

    @Test
    public void testUnboundCoalescePreservesJdbcTypedFallbacks() {
        verifyTemporalFallbacks(false, true);
    }

    @Test
    public void testBoundCoalescePreservesJdbcTypedFallbacks() {
        verifyTemporalFallbacks(true, true);
    }

    @Test
    public void testMutatingConverterPreservesTemporalLiteralValues() {
        Assume.assumeTrue(emf.unwrap(EntityManagerFactoryImpl.class)
                .getDatabaseSession().getPlatform().isDerby());
        emf.runInTransaction(em -> em.persist(new CoalesceTemporalEntity(1)));
        try {
            withHandlers(handler -> {
                for (boolean subquery : List.of(false, true)) {
                    for (boolean bound : List.of(false, true)) {
                        for (boolean jdbcTyped : List.of(false, true)) {
                            Date fallback = jdbcTyped ? Timestamp.valueOf("2026-04-13 17:57:41.123456789")
                                    : new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
                            long originalMillis = fallback.getTime();
                            int originalNanos = fallback instanceof Timestamp timestamp ? timestamp.getNanos() : 0;
                            CriteriaBuilder builder = handler.getCriteriaBuilder();
                            CriteriaQuery<Date> criteria = builder.createQuery(Date.class);
                            Root<CoalesceTemporalEntity> root = criteria.from(CoalesceTemporalEntity.class);
                            Expression<Date> value = root.get("normalizedValue");
                            if (subquery) {
                                Subquery<Date> select = criteria.subquery(Date.class);
                                Root<CoalesceTemporalEntity> inner = select.from(CoalesceTemporalEntity.class);
                                select.select(inner.<Date>get("normalizedValue"));
                                select.where(builder.equal(inner.get("id"), 1));
                                value = select;
                            }
                            Expression<Date> selected = builder.coalesce(value, builder.literal(fallback));
                            criteria.select(selected).where(builder.equal(root.get("id"), 1));
                            FunctionExpression node = (FunctionExpression) ((InternalSelection) selected).getCurrentNode();
                            List<org.eclipse.persistence.expressions.Expression> children = node.getChildren();
                            List<org.eclipse.persistence.expressions.Expression> originalChildren = new ArrayList<>(children);
                            ConstantExpression literal = (ConstantExpression) children.get(1);
                            org.eclipse.persistence.expressions.Expression originalBase = literal.getLocalBase();
                            String context = "subquery=" + subquery + ", bound=" + bound + ", jdbcTyped=" + jdbcTyped
                                    + ", handler=" + handler.getClass().getSimpleName();
                            TypedQuery<Date> query = handler.createQuery(criteria);
                            query.setHint(QueryHints.BIND_PARAMETERS, bound);
                            for (int execution = 0; execution < 3; execution++) {
                                if (execution == 2) {
                                    query = handler.createQuery(criteria);
                                    query.setHint(QueryHints.BIND_PARAMETERS, bound);
                                }
                                List<Date> results = query.getResultList();
                                Assert.assertEquals(context, 1, results.size());
                                Assert.assertEquals(context, Timestamp.valueOf("2026-04-13 17:57:41").getTime(),
                                        results.get(0).getTime());
                                Assert.assertSame(context, fallback, literal.getValue());
                                Assert.assertSame(context, originalBase, literal.getLocalBase());
                                Assert.assertEquals(context, originalMillis, fallback.getTime());
                                if (fallback instanceof Timestamp timestamp) {
                                    Assert.assertEquals(context, originalNanos, timestamp.getNanos());
                                }
                                Assert.assertSame(context, children, node.getChildren());
                                Assert.assertEquals(context, originalChildren.size(), children.size());
                                for (int index = 0; index < originalChildren.size(); index++) {
                                    Assert.assertSame(context, originalChildren.get(index), children.get(index));
                                }
                            }
                        }
                    }
                }
            });
        } finally {
            emf.runInTransaction(em -> em.createQuery("DELETE FROM CoalesceTemporalEntity").executeUpdate());
        }
    }

    @Test
    public void testNativeCoalescePreservesExplicitConverterContext() {
        emf.runInTransaction(em -> em.persist(new CoalesceTemporalEntity(1)));
        try (EntityManager em = emf.createEntityManager()) {
            for (boolean incremental : List.of(false, true)) {
                for (boolean reversed : List.of(false, true)) {
                    for (boolean bound : List.of(false, true)) {
                        Date fallback = new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
                        long originalMillis = fallback.getTime();
                        ExpressionBuilder builder = new ExpressionBuilder(CoalesceTemporalEntity.class);
                        org.eclipse.persistence.expressions.Expression selected = builder.get("identityValue");
                        org.eclipse.persistence.expressions.Expression context = builder.get("shiftedValue");
                        ArgumentListFunctionExpression coalesce;
                        if (incremental) {
                            coalesce = builder.coalesce();
                            org.eclipse.persistence.expressions.Expression literal = context.value(fallback);
                            coalesce.addChild(reversed ? literal : selected);
                            coalesce.addChild(reversed ? selected : literal);
                        } else {
                            coalesce = context.coalesce(reversed ? List.of(fallback, selected) : List.of(selected, fallback));
                        }
                        ConstantExpression literal = (ConstantExpression) coalesce.getChildren().get(reversed ? 0 : 1);
                        ReportQuery query = new ReportQuery(CoalesceTemporalEntity.class, builder);
                        query.addItem("value", coalesce);
                        query.setSelectionCriteria(builder.get("id").equal(1));
                        query.setShouldReturnSingleValue(true);
                        query.setResultClass(Timestamp.class);
                        query.setShouldBindAllParameters(bound);
                        String message = "incremental=" + incremental + ", reversed=" + reversed + ", bound=" + bound;
                        for (int execution = 0; execution < 3; execution++) {
                            Object result = em.unwrap(JpaEntityManager.class).getActiveSession().executeQuery(query);
                            Assert.assertTrue(message, result instanceof Date);
                            Assert.assertEquals(message, Timestamp.valueOf("2026-04-14 17:57:41.123").getTime(),
                                    ((Date) result).getTime());
                            Assert.assertSame(message, fallback, literal.getValue());
                            Assert.assertSame(message, context, literal.getLocalBase());
                            Assert.assertEquals(message, originalMillis, fallback.getTime());
                        }
                    }
                }
            }
        } finally {
            emf.runInTransaction(em -> em.createQuery("DELETE FROM CoalesceTemporalEntity").executeUpdate());
        }
    }

    private void verifyTemporalFallbacks(boolean bound, boolean jdbcTyped) {
        Assume.assumeTrue(emf.unwrap(EntityManagerFactoryImpl.class)
                .getDatabaseSession().getPlatform().isDerby());
        // Null columns exercise the fallback, while the row makes COALESCE evaluate.
        emf.runInTransaction(em -> em.persist(new CoalesceTemporalEntity(1)));
        try {
            withHandlers(handler -> {
                for (boolean subquery : List.of(false, true)) {
                    for (boolean reversed : List.of(false, true)) {
                        for (boolean literal : List.of(false, true)) {
                            for (String attribute : List.of("timestampValue", "dateValue", "timeValue")) {
                                Date fallback = fallback(attribute, jdbcTyped);
                                long originalMillis = fallback.getTime();
                                int originalNanos = fallback instanceof Timestamp timestamp ? timestamp.getNanos() : 0;
                                CriteriaBuilder builder = handler.getCriteriaBuilder();
                                CriteriaQuery<Date> criteria = builder.createQuery(Date.class);
                                Root<CoalesceTemporalEntity> root = criteria.from(CoalesceTemporalEntity.class);
                                Expression<Date> value = root.get(attribute);
                                if (subquery) {
                                    Subquery<Date> select = criteria.subquery(Date.class);
                                    Root<CoalesceTemporalEntity> inner = select.from(CoalesceTemporalEntity.class);
                                    select.select(inner.<Date>get(attribute));
                                    select.where(builder.equal(inner.get("id"), 1));
                                    value = select;
                                }
                                Expression<Date> selected;
                                if (reversed) {
                                    selected = literal ? builder.coalesce(builder.literal(fallback), value)
                                            : builder.<Date>coalesce().value(fallback).value(value);
                                } else {
                                    selected = literal ? builder.coalesce(value, builder.literal(fallback))
                                            : builder.coalesce(value, fallback);
                                }
                                criteria.select(selected).where(builder.equal(root.get("id"), 1));
                                FunctionExpression node = (FunctionExpression) ((InternalSelection) selected).getCurrentNode();
                                List<org.eclipse.persistence.expressions.Expression> children = node.getChildren();
                                List<org.eclipse.persistence.expressions.Expression> originalChildren = new ArrayList<>(children);
                                List<ConstantState> constants = new ArrayList<>();
                                for (org.eclipse.persistence.expressions.Expression child : children) {
                                    if (child instanceof ConstantExpression constant) {
                                        constants.add(new ConstantState(constant, constant.getValue(), constant.getLocalBase()));
                                    }
                                }
                                String context = attribute + ", subquery=" + subquery + ", reversed=" + reversed
                                        + ", literal=" + literal + ", bound=" + bound + ", jdbcTyped=" + jdbcTyped
                                        + ", handler=" + handler.getClass().getSimpleName();
                                TypedQuery<Date> query = handler.createQuery(criteria);
                                query.setHint(QueryHints.BIND_PARAMETERS, bound);
                                for (int execution = 0; execution < 3; execution++) {
                                    if (execution == 2) {
                                        query = handler.createQuery(criteria);
                                        query.setHint(QueryHints.BIND_PARAMETERS, bound);
                                    }
                                    assertTemporalResult(context, attribute, query.getResultList());
                                    Assert.assertSame(context, children, node.getChildren());
                                    Assert.assertEquals(context, originalChildren.size(), children.size());
                                    for (int index = 0; index < originalChildren.size(); index++) {
                                        Assert.assertSame(context, originalChildren.get(index), children.get(index));
                                    }
                                    for (ConstantState constant : constants) {
                                        Assert.assertSame(context, constant.value(), constant.expression().getValue());
                                        Assert.assertSame(context, constant.base(), constant.expression().getLocalBase());
                                    }
                                    Assert.assertEquals(context, originalMillis, fallback.getTime());
                                    if (fallback instanceof Timestamp timestamp) {
                                        Assert.assertEquals(context, originalNanos, timestamp.getNanos());
                                    }
                                }
                            }
                        }
                    }
                }
            });
        } finally {
            emf.runInTransaction(em -> em.createQuery("DELETE FROM CoalesceTemporalEntity").executeUpdate());
        }
    }

    private static Date fallback(String attribute, boolean jdbcTyped) {
        if (jdbcTyped) {
            if ("dateValue".equals(attribute)) {
                return java.sql.Date.valueOf("2026-04-13");
            }
            if ("timeValue".equals(attribute)) {
                return Time.valueOf("17:57:41");
            }
            return Timestamp.valueOf("2026-04-13 17:57:41.123");
        }
        return new Date(Timestamp.valueOf("2026-04-13 17:57:41.123").getTime());
    }

    private static void assertTemporalResult(String context, String attribute, List<Date> results) {
        Assert.assertEquals(context, 1, results.size());
        Date actual = results.get(0);
        Assert.assertNotNull(context, actual);
        if ("dateValue".equals(attribute)) {
            Assert.assertEquals(context, java.sql.Date.valueOf("2026-04-13").toLocalDate(),
                    new java.sql.Date(actual.getTime()).toLocalDate());
        } else if ("timeValue".equals(attribute)) {
            Assert.assertEquals(context, Time.valueOf("17:57:41").toLocalTime(),
                    new Time(actual.getTime()).toLocalTime());
        } else {
            Assert.assertEquals(context, Timestamp.valueOf("2026-04-13 17:57:41.123").getTime(), actual.getTime());
        }
    }

    private void withHandlers(Consumer<EntityHandler> assertions) {
        try (EntityManager em = emf.createEntityManager()) {
            assertions.accept(em);
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertions.accept(agent);
        }
    }

    private record ConstantState(ConstantExpression expression, Object value,
            org.eclipse.persistence.expressions.Expression base) {
    }
}
