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
package org.eclipse.persistence.jpa.test.query;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.EntityTbl01;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionCustomizer;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ClientSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestCaseCoalesce {
    private static final String SIMPLE = "case t.itemInteger1 when 2 then t.itemString1 else null end";
    private static final String FROM = " from EntityTbl01 t order by t.KeyString";
    private static final List<String> TITLES = List.of("Alpha", "Beta", "Gamma", "Delta");
    private final ThreadLocal<List<DatabaseQuery>> executedQueries = new ThreadLocal<>();

    @Emf(createTables = DDLGen.DROP_CREATE, classes = EntityTbl01.class,
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "eclipselink.jdbc.allow-partial-bind-parameters", value = "true")})
    private EntityManagerFactory emf;

    @Emf(name = "case-coalesce-no-bind", createTables = DDLGen.NONE, classes = EntityTbl01.class,
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "eclipselink.jdbc.bind-parameters", value = "false")})
    private EntityManagerFactory noBindEmf;

    @Emf(name = "case-coalesce-no-partial-bind", createTables = DDLGen.NONE, classes = EntityTbl01.class,
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "eclipselink.jdbc.allow-partial-bind-parameters", value = "false")})
    private EntityManagerFactory noPartialBindEmf;

    @Emf(name = "case-coalesce-unbound-literals", createTables = DDLGen.NONE, classes = EntityTbl01.class,
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "eclipselink.jdbc.allow-partial-bind-parameters", value = "true"),
                    @Property(name = "eclipselink.session.customizer",
                            value = "org.eclipse.persistence.jpa.test.query.TestCaseCoalesce$UnboundLiterals")})
    private EntityManagerFactory unboundLiteralsEmf;

    public static class UnboundLiterals implements SessionCustomizer {
        @Override
        public void customize(Session session) {
            session.getPlatform().setShouldBindLiterals(false);
            session.getEventManager().addListener(new SessionEventAdapter() {
                @Override
                public void postLogin(SessionEvent event) {
                    // Auto-detection may replace the platform configured before login.
                    event.getSession().getPlatform().setShouldBindLiterals(false);
                }
            });
        }
    }

    @Before
    public void seed() {
        assertNotNull("The test factory must be injected", emf);
        cleanup();
        emf.runInTransaction(em -> {
            for (int index = 0; index < TITLES.size(); index++) {
                EntityTbl01 row = new EntityTbl01();
                row.setKeyString("case-" + (index + 1));
                row.setItemString1(TITLES.get(index));
                row.setItemString2(index == 2 ? "third" : null);
                row.setItemString3(null);
                row.setItemString4("fallback");
                row.setItemInteger1(index == 3 ? null : index + 1);
                em.persist(row);
            }
        });
    }

    @After
    public void cleanup() {
        assertNotNull("The test factory must be injected", emf);
        emf.runInTransaction(em -> em.createQuery("delete from EntityTbl01").executeUpdate());
        for (EntityManagerFactory factory : List.of(emf, noBindEmf, noPartialBindEmf, unboundLiteralsEmf)) {
            factory.getCache().evictAll();
        }
    }

    @Test
    public void entityManagerDirectCaseInSelectAndPredicate() {
        runOn(emf, false, this::assertDirect);
    }

    @Test
    public void entityAgentDirectCaseInSelectAndPredicate() {
        runOn(emf, true, this::assertDirect);
    }

    @Test
    public void entityManagerNestedCaseReceivers() {
        runOn(emf, false, this::assertNested);
    }

    @Test
    public void entityAgentNestedCaseReceivers() {
        runOn(emf, true, this::assertNested);
    }

    @Test
    public void entityManagerNamedParametersCanBeRebound() {
        runOn(emf, false, this::assertNamedParameters);
    }

    @Test
    public void entityAgentNamedParametersCanBeRebound() {
        runOn(emf, true, this::assertNamedParameters);
    }

    @Test
    public void entityManagerCriteriaParametersAndPredicate() {
        runOn(emf, false, this::assertCriteria);
    }

    @Test
    public void entityAgentCriteriaParametersAndPredicate() {
        runOn(emf, true, this::assertCriteria);
    }

    @Test
    public void entityManagerMultipleBranchesNullsAndNumericType() {
        runOn(emf, false, this::assertBranchesAndTypes);
    }

    @Test
    public void entityAgentMultipleBranchesNullsAndNumericType() {
        runOn(emf, true, this::assertBranchesAndTypes);
    }

    @Test
    public void entityManagerSafeReceiversRemainUnchanged() {
        runOn(emf, false, this::assertSafeReceivers);
    }

    @Test
    public void entityAgentSafeReceiversRemainUnchanged() {
        runOn(emf, true, this::assertSafeReceivers);
    }

    @Test
    public void entityManagerBindingAlternatives() {
        assertBindingAlternatives(false);
    }

    @Test
    public void entityAgentBindingAlternatives() {
        assertBindingAlternatives(true);
    }

    @Test
    public void clonedLoginUsesItsOwnLiteralBinding() {
        for (EntityManagerFactory factory : List.of(emf, unboundLiteralsEmf)) {
            ServerSession server = factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
            var sourcePlatform = server.getPlatform();
            boolean sourceBindsLiterals = sourcePlatform.shouldBindLiterals();
            assertEquals(factory == emf, sourceBindsLiterals);
            // Initialize the operators before cloning: the platform clone shares these instances.
            ExpressionOperator coalesce = sourcePlatform.getOperator(ExpressionOperator.Coalesce);
            DatasourceLogin login = ((DatasourceLogin) server.getDatasourceLogin()).clone();
            assertNotSame(sourcePlatform, login.getPlatform());
            assertSame(coalesce, login.getPlatform().getOperator(ExpressionOperator.Coalesce));
            login.getPlatform().setShouldBindLiterals(!sourceBindsLiterals);
            ClientSession client = server.acquireClientSession(login);
            List<String> executedSql = new ArrayList<>();
            SessionEventAdapter listener = new SessionEventAdapter() {
                @Override
                public void postExecuteQuery(SessionEvent event) {
                    if (event.getQuery().isReportQuery() && event.getQuery().getSQLString() != null) {
                        executedSql.add(event.getQuery().getSQLString());
                    }
                }
            };
            client.getEventManager().addListener(listener);
            try {
                // A transaction makes the client use its cloned login instead of the parent's read session.
                client.beginTransaction();
                assertSame(login.getPlatform(), client.getPlatform());
                ExpressionBuilder builder = new ExpressionBuilder(EntityTbl01.class);
                var simple = builder.get("itemInteger1").caseStatement(
                        Map.of(2, builder.get("itemString1")), builder.get("itemString3"));
                ReportQuery query = new ReportQuery(EntityTbl01.class, builder);
                query.addAttribute("result", builder.coalesce(List.of(builder.value("literal"), simple,
                        builder.value("fallback"))), String.class);
                query.addOrdering(builder.get("KeyString").ascending());
                query.returnSingleAttribute();
                assertEquals(List.of("literal", "literal", "literal", "literal"), client.executeQuery(query));
                assertFalse("The cloned client must publish its executed SQL", executedSql.isEmpty());
                assertGuard(executedSql.get(executedSql.size() - 1), !sourceBindsLiterals);
                assertEquals("The original factory must retain its literal-binding flag",
                        sourceBindsLiterals, sourcePlatform.shouldBindLiterals());
            } finally {
                client.getEventManager().removeListener(listener);
                try {
                    if (client.isInTransaction()) {
                        client.rollbackTransaction();
                    }
                } finally {
                    client.release();
                }
            }
        }
    }

    @Test
    public void automaticCastUntypedParameterCanBeRebound() {
        assertAutomaticCastParameter(null);
    }

    @Test
    public void automaticCastDecimalParameterCanBeRebound() {
        assertAutomaticCastParameter(BigDecimal.class);
    }

    @Test
    public void automaticCastStringParameterCanBeRebound() {
        assertAutomaticCastParameter(String.class);
    }

    private void assertAutomaticCastParameter(Class<?> parameterType) {
        ServerSession server = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        DatasourceLogin login = ((DatasourceLogin) server.getDatasourceLogin()).clone();
        if (login.getPlatform() instanceof DB2Platform platform) {
            platform.setIsCastRequired(true);
        }
        login.getPlatform().setShouldBindPartialParameters(true);
        ClientSession client = server.acquireClientSession(login);
        List<String> executedSql = new ArrayList<>();
        SessionEventAdapter listener = new SessionEventAdapter() {
            @Override
            public void postExecuteQuery(SessionEvent event) {
                if (event.getQuery().isReportQuery() && event.getQuery().getSQLString() != null) {
                    executedSql.add(event.getQuery().getSQLString());
                }
            }
        };
        client.getEventManager().addListener(listener);
        try {
            client.beginTransaction();
            ExpressionBuilder builder = new ExpressionBuilder(EntityTbl01.class);
            var leading = (org.eclipse.persistence.internal.expressions.ParameterExpression)
                    builder.getParameter("leading", parameterType);
            assertEquals(parameterType, leading.getType());
            boolean decimal = parameterType == BigDecimal.class;
            var value = decimal ? builder.get("itemInteger1").cast("DECIMAL(10,2)") : builder.get("itemString1");
            var otherwise = decimal ? builder.get("itemString3").cast("DECIMAL(10,2)") : builder.get("itemString3");
            var simple = builder.get("itemInteger1").caseStatement(Map.of(2, value), otherwise);
            Object fallback = decimal ? new BigDecimal("9.00") : "fallback";
            ReportQuery query = new ReportQuery(EntityTbl01.class, builder);
            query.addAttribute("result", builder.coalesce(List.of(leading, simple, builder.value(fallback))),
                    decimal ? BigDecimal.class : String.class);
            query.addArgument("leading", parameterType == null ? Object.class : parameterType);
            query.addOrdering(builder.get("KeyString").ascending());
            query.returnSingleAttribute();
            for (Object bound : Arrays.asList(null, decimal ? new BigDecimal("7.00") : "leading")) {
                List<?> result = (List<?>) client.executeQuery(query, Arrays.asList(bound));
                assertEquals(4, result.size());
                if (decimal) {
                    for (int index = 0; index < result.size(); index++) {
                        assertTrue(result.get(index) instanceof BigDecimal);
                        BigDecimal expected = bound == null ? new BigDecimal(index == 1 ? "2.00" : "9.00")
                                : (BigDecimal) bound;
                        assertEquals(0, expected.compareTo((BigDecimal) result.get(index)));
                    }
                } else {
                    assertEquals(bound == null ? List.of("fallback", "Beta", "fallback", "fallback")
                            : List.of("leading", "leading", "leading", "leading"), result);
                }
                assertFalse("The client must publish its executed SQL", executedSql.isEmpty());
                String sql = executedSql.get(executedSql.size() - 1);
                assertGuard(sql, true);
                if (login.getPlatform().isDerby() && parameterType == String.class) {
                    assertTrue("The String parameter must retain its automatic CAST", sql.contains("CAST (? AS VARCHAR("));
                }
            }
        } finally {
            client.getEventManager().removeListener(listener);
            try {
                if (client.isInTransaction()) {
                    client.rollbackTransaction();
                }
            } finally {
                client.release();
            }
        }
    }

    @Test
    public void concurrentMixedArityQueriesDoNotChangeSharedOperators() throws Exception {
        ExecutorService workers = Executors.newFixedThreadPool(4);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> results = new ArrayList<>();
        try {
            for (int worker = 0; worker < 4; worker++) {
                final int lane = worker;
                results.add(workers.submit(() -> {
                    start.await();
                    for (int iteration = 0; iteration < 8; iteration++) {
                        String fallback = "fallback-" + lane + "-" + iteration;
                        String expression = switch ((lane + iteration) % 3) {
                            case 0 -> "coalesce(" + SIMPLE + ", '" + fallback + "')";
                            case 1 -> "coalesce(t.itemString3, " + SIMPLE + ", '" + fallback + "')";
                            default -> "coalesce(t.itemString3, t.itemString1, '" + fallback + "', 'last')";
                        };
                        boolean ordinary = (lane + iteration) % 3 == 2;
                        runOn(emf, lane % 2 == 1, handler -> assertEquals(
                                ordinary ? TITLES : List.of(fallback, "Beta", fallback, fallback),
                                select(handler, expression, String.class).getResultList()));
                    }
                    return null;
                }));
            }
            start.countDown();
            for (Future<?> result : results) {
                result.get(60, TimeUnit.SECONDS);
            }
        } finally {
            workers.shutdownNow();
            assertTrue("All query workers must terminate", workers.awaitTermination(60, TimeUnit.SECONDS));
        }
        runOn(emf, false, this::assertSafeReceivers);
    }

    private void assertDirect(EntityHandler handler) {
        TypedQuery<String> select = select(handler, "coalesce(" + SIMPLE + ", 'fallback')", String.class);
        assertEquals(List.of("fallback", "Beta", "fallback", "fallback"), select.getResultList());
        assertGuard(select, true);
        TypedQuery<String> predicate = handler.createQuery("select t.KeyString from EntityTbl01 t "
                + "where coalesce(" + SIMPLE + ", 'fallback') = :wanted order by t.KeyString", String.class);
        assertEquals(List.of("case-2"), predicate.setParameter("wanted", "Beta").getResultList());
        assertGuard(predicate, true);
    }

    private void assertNested(EntityHandler handler) {
        String nested = "case t.itemInteger1 when 1 then "
                + "case t.itemString1 when 'Alpha' then t.itemString1 else null end "
                + "when 2 then t.itemString1 else null end";
        TypedQuery<String> query = select(handler, "coalesce(" + nested + ", 'fallback')", String.class);
        assertEquals(List.of("Alpha", "Beta", "fallback", "fallback"), query.getResultList());
        assertGuard(query, true);
        query = select(handler, "coalesce(case when t.itemInteger1 < 3 then " + SIMPLE
                + " else null end, 'fallback')", String.class);
        assertEquals(List.of("fallback", "Beta", "fallback", "fallback"), query.getResultList());
        assertGuard(query, true);
    }

    private void assertNamedParameters(EntityHandler handler) {
        TypedQuery<String> query = select(handler, "coalesce(:leading, case t.itemInteger1 "
                + "when :match then t.itemString1 else null end, :fallback)", String.class);
        query.setParameter("leading", (String) null).setParameter("match", 2).setParameter("fallback", "first");
        assertEquals(List.of("first", "Beta", "first", "first"), query.getResultList());
        assertGuard(query, true);
        query.setParameter("match", 1).setParameter("fallback", "second");
        assertEquals(List.of("Alpha", "second", "second", "second"), query.getResultList());
        query.setParameter("leading", "leading").setParameter("fallback", (String) null);
        assertEquals(List.of("leading", "leading", "leading", "leading"), query.getResultList());
        query.setParameter("leading", (String) null);
        assertEquals(Arrays.asList("Alpha", null, null, null), query.getResultList());
    }

    private void assertCriteria(EntityHandler handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        CriteriaQuery<String> criteria = builder.createQuery(String.class);
        Root<EntityTbl01> root = criteria.from(EntityTbl01.class);
        ParameterExpression<String> leading = builder.parameter(String.class, "leading");
        ParameterExpression<String> branch = builder.parameter(String.class, "branch");
        ParameterExpression<String> fallback = builder.parameter(String.class, "fallback");
        Expression<String> simple = builder.<Integer, String>selectCase(root.get("itemInteger1"))
                .when(1, branch).when(2, root.<String>get("itemString2")).otherwise(builder.nullLiteral(String.class));
        Expression<String> coalesce = builder.<String>coalesce().value(leading).value(simple).value(fallback);
        criteria.select(coalesce).orderBy(builder.asc(root.get("KeyString")));
        TypedQuery<String> query = handler.createQuery(criteria);
        query.setParameter(leading, (String) null).setParameter(branch, "one").setParameter(fallback, "other");
        assertEquals(List.of("one", "other", "other", "other"), query.getResultList());
        assertGuard(query, true);
        query.setParameter(branch, "changed").setParameter(fallback, (String) null);
        assertEquals(Arrays.asList("changed", null, null, null), query.getResultList());
        query.setParameter(leading, "prefix");
        assertEquals(List.of("prefix", "prefix", "prefix", "prefix"), query.getResultList());

        CriteriaQuery<String> predicate = builder.createQuery(String.class);
        Root<EntityTbl01> other = predicate.from(EntityTbl01.class);
        Expression<String> selected = builder.<Integer, String>selectCase(other.get("itemInteger1"))
                .when(2, other.<String>get("itemString1")).otherwise(builder.nullLiteral(String.class));
        predicate.select(other.get("KeyString")).where(builder.equal(builder.coalesce(selected, "fallback"), "Beta"));
        TypedQuery<String> predicateQuery = handler.createQuery(predicate);
        assertEquals(List.of("case-2"), predicateQuery.getResultList());
        assertGuard(predicateQuery, true);
    }

    private void assertBranchesAndTypes(EntityHandler handler) {
        TypedQuery<String> query = select(handler, "coalesce(case t.itemInteger1 when 1 then t.itemString1 "
                + "when 2 then t.itemString2 when 3 then t.itemString1 else null end, t.itemString3, 'fallback')",
                String.class);
        assertEquals(List.of("Alpha", "fallback", "Gamma", "fallback"), query.getResultList());
        assertGuard(query, true);
        TypedQuery<Integer> numeric = select(handler, "coalesce(case t.itemInteger1 when 1 then 10 "
                + "when 2 then 20 else t.itemInteger1 end, 99)", Integer.class);
        assertEquals(List.of(10, 20, 3, 99), numeric.getResultList());
        assertGuard(numeric, true);
    }

    private void assertSafeReceivers(EntityHandler handler) {
        TypedQuery<String> ordinary = select(handler, "coalesce(t.itemString3, t.itemString1, 'fallback')", String.class);
        assertEquals(TITLES, ordinary.getResultList());
        assertGuard(ordinary, false);
        TypedQuery<String> searched = select(handler,
                "coalesce(case when t.itemInteger1 = 2 then t.itemString1 else null end, 'fallback')", String.class);
        assertEquals(List.of("fallback", "Beta", "fallback", "fallback"), searched.getResultList());
        assertGuard(searched, false);
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        CriteriaQuery<String> criteria = builder.createQuery(String.class);
        Root<EntityTbl01> root = criteria.from(EntityTbl01.class);
        Expression<String> searchedCase = builder.<String>selectCase()
                .when(builder.equal(root.get("itemInteger1"), 2), root.<String>get("itemString1"))
                .otherwise(builder.nullLiteral(String.class));
        criteria.select(builder.coalesce(searchedCase, "fallback")).orderBy(builder.asc(root.get("KeyString")));
        TypedQuery<String> searchedCriteria = handler.createQuery(criteria);
        assertEquals(List.of("fallback", "Beta", "fallback", "fallback"), searchedCriteria.getResultList());
        assertGuard(searchedCriteria, false);
        TypedQuery<String> later = select(handler, "coalesce(t.itemString3, " + SIMPLE + ", 'fallback')", String.class);
        assertEquals(List.of("fallback", "Beta", "fallback", "fallback"), later.getResultList());
        assertGuard(later, false);
        if (isDerby()) {
            TypedQuery<String> cast = select(handler, "coalesce(cast(:leading as varchar(40)), "
                    + SIMPLE + ", 'fallback')", String.class);
            assertEquals(List.of("fallback", "Beta", "fallback", "fallback"),
                    cast.setParameter("leading", (String) null).getResultList());
            assertGuard(cast, false);
            assertEquals(List.of("safe", "safe", "safe", "safe"),
                    cast.setParameter("leading", "safe").getResultList());
        }
    }

    private void assertBindingAlternatives(boolean agent) {
        for (EntityManagerFactory factory : List.of(noBindEmf, noPartialBindEmf, unboundLiteralsEmf)) {
            runOn(factory, agent, handler -> {
                assertDirect(handler);
                TypedQuery<String> query = select(handler, "coalesce(:leading, " + SIMPLE + ", :fallback)", String.class);
                // Without partial binding, complex CASE disables binding for the whole call.
                // In that mode, or with binding explicitly disabled, a null becomes unsupported bare NULL.
                String leading = factory == noBindEmf || factory == noPartialBindEmf ? "initial" : null;
                query.setParameter("leading", leading).setParameter("fallback", "alternative");
                assertEquals(leading == null ? List.of("alternative", "Beta", "alternative", "alternative")
                        : List.of("initial", "initial", "initial", "initial"), query.getResultList());
                query.setParameter("leading", "first");
                assertEquals(List.of("first", "first", "first", "first"), query.getResultList());
            });
        }
        assertFalse("The initialized factory must retain the unbound-literal setting",
                unboundLiteralsEmf.unwrap(JpaEntityManagerFactory.class).getServerSession().getPlatform().shouldBindLiterals());
        runOn(unboundLiteralsEmf, agent, handler -> {
            CriteriaBuilder builder = handler.getCriteriaBuilder();
            CriteriaQuery<String> criteria = builder.createQuery(String.class);
            Root<EntityTbl01> root = criteria.from(EntityTbl01.class);
            Expression<String> simple = builder.<Integer, String>selectCase(root.get("itemInteger1"))
                    .when(2, root.<String>get("itemString1")).otherwise(builder.nullLiteral(String.class));
            criteria.select(builder.<String>coalesce().value(builder.literal("literal")).value(simple).value("fallback"))
                    .orderBy(builder.asc(root.get("KeyString")));
            TypedQuery<String> safeLiteral = handler.createQuery(criteria);
            assertEquals(List.of("literal", "literal", "literal", "literal"), safeLiteral.getResultList());
            assertGuard(safeLiteral, false);
        });
    }

    private <T> TypedQuery<T> select(EntityHandler handler, String expression, Class<T> resultType) {
        return handler.createQuery("select " + expression + FROM, resultType);
    }

    private void assertGuard(TypedQuery<?> query, boolean expectedOnDerby) {
        String sql = query.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString();
        if (sql == null) {
            List<DatabaseQuery> executed = executedQueries.get();
            assertNotNull("SQL capture must be installed for this handler", executed);
            assertFalse("The current execution must publish a query event", executed.isEmpty());
            for (int index = executed.size() - 1; index >= 0 && sql == null; index--) {
                sql = executed.get(index).getSQLString();
            }
        }
        assertGuard(sql, expectedOnDerby);
    }

    private void assertGuard(String sql, boolean expectedOnDerby) {
        assertNotNull("The consumed query must expose its prepared SQL", sql);
        String compact = sql.replaceAll("[\\s()]", "").toUpperCase(Locale.ROOT);
        assertEquals("Only an affected Derby receiver should gain the false CASE prefix: " + sql,
                isDerby() && expectedOnDerby, compact.contains("COALESCECASEWHEN1=0THEN"));
        assertFalse("The false condition must not use bound parameters", compact.contains("COALESCECASEWHEN?=?THEN"));
    }

    private boolean isDerby() {
        return emf.unwrap(JpaEntityManagerFactory.class).getServerSession().getPlatform().isDerby();
    }

    private void runOn(EntityManagerFactory factory, boolean agent, Consumer<EntityHandler> assertion) {
        if (agent) {
            try (EntityAgent handler = factory.createEntityAgent()) {
                captureQueries(handler, handler.unwrap(Session.class), assertion);
            }
        } else {
            try (EntityManager handler = factory.createEntityManager()) {
                captureQueries(handler, handler.unwrap(JpaEntityManager.class).getActiveSession(), assertion);
            }
        }
    }

    private void captureQueries(EntityHandler handler, Session session, Consumer<EntityHandler> assertion) {
        List<DatabaseQuery> executed = new ArrayList<>();
        SessionEventAdapter listener = new SessionEventAdapter() {
            @Override
            public void preExecuteQuery(SessionEvent event) {
                // A later assertion must never use SQL retained from an earlier query execution.
                executed.clear();
            }

            @Override
            public void postExecuteQuery(SessionEvent event) {
                executed.add(event.getQuery());
            }
        };
        executedQueries.set(executed);
        session.getEventManager().addListener(listener);
        try {
            assertion.accept(handler);
        } finally {
            session.getEventManager().removeListener(listener);
            executedQueries.remove();
        }
    }
}
