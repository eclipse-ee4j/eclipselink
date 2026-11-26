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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Parameter;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.ArithmeticEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestArithmeticAssociativity {

    private static final String FROM = " FROM ArithmeticEntity e";
    private static final String FIRST = FROM + " WHERE e.id = 1";

    @Emf(createTables = DDLGen.DROP_CREATE, classes = {ArithmeticEntity.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;

    @Before
    public void seed() {
        cleanup();
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                em.persist(new ArithmeticEntity(1, 100, 30, 20, 1, null));
                em.persist(new ArithmeticEntity(2, 90, 10, 40, 1, 7));
                em.persist(new ArithmeticEntity(3, 60, 20, 5, 2, 11));
                transaction.commit();
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }
    }

    @After
    public void cleanup() {
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                em.createQuery("SELECT e" + FROM, ArithmeticEntity.class)
                        .getResultList().forEach(em::remove);
                transaction.commit();
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }
        emf.getCache().evictAll();
    }

    @Test
    public void entityManagerLiteralChainsAssociateLeft() {
        try (EntityManager em = emf.createEntityManager()) {
            assertLiteralChains(em);
        }
    }

    @Test
    public void entityManagerMappedChainsAssociateLeft() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMappedChains(em);
        }
    }

    @Test
    public void entityManagerLongerChainsAssociateLeft() {
        try (EntityManager em = emf.createEntityManager()) {
            assertLongerChains(em);
        }
    }

    @Test
    public void entityManagerNamedQueryAssociatesLeft() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNamedQuery(em);
        }
    }

    @Test
    public void entityManagerNamedParametersCanBeRebound() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNamedParameters(em);
        }
    }

    @Test
    public void entityManagerPositionalParametersCanBeRebound() {
        try (EntityManager em = emf.createEntityManager()) {
            assertPositionalParameters(em);
        }
    }

    @Test
    public void entityManagerExplicitGroupingRemainsIntact() {
        try (EntityManager em = emf.createEntityManager()) {
            assertExplicitGrouping(em);
        }
    }

    @Test
    public void entityManagerMultiplicativeControlsRemainValid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMultiplicativeControls(em);
        }
    }

    @Test
    public void entityManagerUnarySignsRemainOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            assertUnarySigns(em);
        }
    }

    @Test
    public void entityManagerNegativeMappedFactorsRemainOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNegativeMappedFactors(em);
        }
    }

    @Test
    public void entityManagerNegativeGroupedFactorsRemainOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNegativeGroupedFactors(em);
        }
    }

    @Test
    public void entityManagerSignedExponentsRemainLiterals() {
        try (EntityManager em = emf.createEntityManager()) {
            assertSignedExponents(em);
        }
    }

    @Test
    public void entityManagerPredicatesUseLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertPredicates(em);
        }
    }

    @Test
    public void entityManagerOrderByUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertOrderBy(em);
        }
    }

    @Test
    public void entityManagerHavingUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertHaving(em);
        }
    }

    @Test
    public void entityManagerExplicitConstructorUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertExplicitConstructor(em);
        }
    }

    @Test
    public void entityManagerBulkUpdateUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertBulkUpdate(em);
        }
    }

    @Test
    public void entityManagerNullableOperandsPreserveNull() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNullableOperands(em);
        }
    }

    @Test
    public void entityManagerFunctionsAndLogicRemainValid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertFunctionsAndLogic(em);
        }
    }

    @Test
    public void entityManagerCriteriaRetainsExplicitTrees() {
        try (EntityManager em = emf.createEntityManager()) {
            assertCriteria(em);
        }
    }

    @Test
    public void entityManagerNativeSqlRemainsIndependent() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNativeSql(em);
        }
    }

    @Test
    public void entityManagerMalformedArithmeticRemainsInvalid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMalformedArithmetic(em);
        }
    }

    @Test
    public void entityManagerNamedArithmeticParameterMetadata() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNamedMetadata(em.createQuery("SELECT e.amount - 1 - :last" + FIRST), Integer.class, 5, 10, 94, 89);
            assertNamedMetadata(em.createQuery("SELECT e.amount - e.deduction - :last" + FIRST),
                    Integer.class, 5, 10, 65, 60);
            assertNamedMetadata(em.createQuery("SELECT e.amount - 1 - 2 - :last" + FIRST),
                    Integer.class, 5, 10, 92, 87);
            assertRepeatedNullParameter(em.createQuery("SELECT e.amount - 1 - :last" + FIRST
                    + " AND (e.amount > :last OR :last IS NULL)"));
        }
    }

    @Test
    public void entityManagerPositionalArithmeticParameterMetadata() {
        try (EntityManager em = emf.createEntityManager()) {
            for (String expression : List.of("e.amount - 1 - ?1", "e.amount - e.deduction - ?1")) {
                Query query = em.createQuery("SELECT " + expression + FIRST);
                assertEquals(Integer.class, query.getParameter(1).getParameterType());
                Parameter<Integer> parameter = query.getParameter(1, Integer.class);
                assertEquals(Integer.valueOf(1), parameter.getPosition());
                assertNull(parameter.getName());
                assertParameterEnumeration(query, parameter);
                assertFalse(query.isBound(parameter));
                assertThrows(IllegalArgumentException.class, () -> query.getParameter(1, String.class));
                assertNumber(expression.contains("e.deduction") ? 65 : 94,
                        query.setParameter(parameter, 5).getSingleResult());
                assertEquals(Integer.valueOf(5), query.getParameterValue(parameter));
                query.setParameter(parameter, null);
                assertTrue(query.isBound(parameter));
                assertNull(query.getParameterValue(parameter));
                assertNull(query.getSingleResult());
                assertEquals(Integer.class, query.getParameter(1, Integer.class).getParameterType());
                assertNumber(expression.contains("e.deduction") ? 60 : 89,
                        query.setParameter(parameter, 10).getSingleResult());
                assertEquals(Integer.valueOf(10), query.getParameterValue(parameter));
                assertEquals(Integer.class, query.getParameter(1).getParameterType());
            }
        }
    }

    @Test
    public void entityManagerArithmeticParameterOperatorsAndGrouping() {
        try (EntityManager em = emf.createEntityManager()) {
            String[] expressions = {"e.amount + 1 - :last", "e.amount - 1 + :last",
                    "e.amount * 2 - :last", "e.amount / 2 - :last", "e.amount * 2 * :last",
                    "e.amount / 2 / :last", "(e.amount - 1 - :last)", "e.amount - 1 - -:last",
                    "e.amount - (1 - :last)"};
            int[] firstResults = {96, 104, 195, 45, 1000, 10, 94, 104, 104};
            int[] reboundResults = {91, 109, 190, 40, 2000, 5, 89, 109, 109};
            for (int index = 0; index < expressions.length; index++) {
                assertNamedMetadata(em.createQuery("SELECT " + expressions[index] + FIRST),
                        Integer.class, 5, 10, firstResults[index], reboundResults[index]);
            }
        }
    }

    @Test
    public void entityManagerArithmeticParameterNumericWitnesses() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNamedMetadata(em.createQuery("SELECT e.amount - 1 - :last" + FIRST), Integer.class, 5, 10, 94, 89);
            assertNamedMetadata(em.createQuery("SELECT e.amount - 1L - :last" + FIRST), Long.class, 5L, 10L, 94, 89);
            assertNamedMetadata(em.createQuery("SELECT 1L + e.amount - :last" + FIRST), Long.class, 5L, 10L, 96, 91);
            assertNamedMetadata(em.createQuery("SELECT e.amount - 1.5 - :last" + FIRST),
                    Double.class, 5.25, 10.5, 93.25, 88.0);
            assertNamedMetadata(em.createQuery("SELECT 1.5 + e.amount - :last" + FIRST),
                    Double.class, 5.25, 10.5, 96.25, 91.0);
            assertNamedMetadata(em.createQuery("SELECT ABS(e.amount) - 1 - :last" + FIRST),
                    Integer.class, 5, 10, 94, 89);
            Query sum = em.createQuery("SELECT SUM(e.amount) - 1 - :last" + FIRST);
            assertNamedMetadata(sum, Long.class, 5L, 10L, 94, 89);
            assertThrows(IllegalArgumentException.class, () -> sum.getParameter("last", Integer.class));
            // Numeric value conversion remains supported even when typed lookup requires Long.
            assertNumber(94, sum.setParameter("last", 5).getSingleResult());
            assertNamedMetadata(em.createQuery("SELECT e.nullableAmount - 1 - :last" + FIRST),
                    Integer.class, 5, 10, null, null);
            assertNamedMetadata(em.createQuery("SELECT e.nullableAmount - 1 - :last" + FROM + " WHERE e.id = 2"),
                    Integer.class, 5, 10, 1, -4);
        }
    }

    @Test
    public void entityManagerNamedQueryParameterMetadataRemainsStable() {
        try (EntityManager em = emf.createEntityManager()) {
            Query first = em.createNamedQuery("ArithmeticEntity.parameterBalance");
            Query second = em.createNamedQuery("ArithmeticEntity.parameterBalance");
            assertNamedMetadata(first, Integer.class, 5, 10, 65, 60);
            assertNamedMetadata(second, Integer.class, 20, 30, 50, 40);
            assertNumber(60, first.getSingleResult());
            assertEquals(Integer.class, first.getParameter("last", Integer.class).getParameterType());
        }
    }

    @Test
    public void entityManagerArithmeticParameterFallbackAndRejection() {
        try (EntityManager em = emf.createEntityManager()) {
            Query multiple = em.createQuery("SELECT e.amount - :first - :last" + FIRST);
            assertEquals(Integer.class, multiple.getParameter("first").getParameterType());
            assertEquals(Object.class, multiple.getParameter("last").getParameterType());
            assertThrows(IllegalArgumentException.class, () -> multiple.getParameter("last", Integer.class));
            assertEquals(2, multiple.getParameters().size());
            multiple.setParameter(multiple.getParameter("first", Integer.class), 30);
            assertNumber(65, multiple.setParameter("last", 5).getSingleResult());

            Query parameterOnly = em.createQuery("SELECT (:first - :last)" + FIRST);
            assertEquals(Object.class, parameterOnly.getParameter("first").getParameterType());
            assertEquals(Object.class, parameterOnly.getParameter("last").getParameterType());
            assertNumber(25, parameterOnly.setParameter("first", 30).setParameter("last", 5).getSingleResult());

            String[] expressions = {"(e.amount - 1) - :last", "-(e.amount - 1) - :last"};
            int[] results = {94, -104};
            for (int index = 0; index < expressions.length; index++) {
                Query query = em.createQuery("SELECT " + expressions[index] + FIRST);
                assertEquals(Object.class, query.getParameter("last").getParameterType());
                assertThrows(IllegalArgumentException.class, () -> query.getParameter("last", Integer.class));
                assertNumber(results[index], query.setParameter("last", 5).getSingleResult());
            }
            assertRepeatedNullParameter(em.createQuery("SELECT e.amount - 1 - :last" + FIRST
                    + " AND (:last IS NULL OR e.amount > :last)"));

            Query comparison = em.createQuery("SELECT e.amount" + FROM + " WHERE e.amount > :last AND e.id = 1");
            assertEquals(Integer.class, comparison.getParameter("last").getParameterType());
            Parameter<Integer> parameter = comparison.getParameter("last", Integer.class);
            assertEquals("last", parameter.getName());
            assertNull(parameter.getPosition());
            assertParameterEnumeration(comparison, parameter);
            assertFalse(comparison.isBound(parameter));
            assertThrows(IllegalArgumentException.class, () -> comparison.getParameter("last", String.class));
            assertNumber(100, comparison.setParameter(parameter, 5).getSingleResult());
            assertTrue(comparison.isBound(parameter));
            assertEquals(Integer.valueOf(5), comparison.getParameterValue(parameter));
            comparison.setParameter(parameter, null);
            assertTrue(comparison.isBound(parameter));
            assertNull(comparison.getParameterValue(parameter));
            assertTrue(comparison.getResultList().isEmpty());
            assertEquals(Integer.class, comparison.getParameter("last", Integer.class).getParameterType());
            assertNumber(100, comparison.setParameter(parameter, 10).getSingleResult());
            assertEquals(Integer.valueOf(10), comparison.getParameterValue(parameter));
            assertEquals(Integer.class, comparison.getParameter("last", Integer.class).getParameterType());
            assertThrows(IllegalArgumentException.class, () -> comparison.setParameter("last", "invalid"));
        }
    }

    private <T extends Number> void assertNamedMetadata(Query query, Class<T> type, T value, T rebound,
            Number expected, Number reboundExpected) {
        assertEquals(type, query.getParameter("last").getParameterType());
        Parameter<T> parameter = query.getParameter("last", type);
        assertEquals("last", parameter.getName());
        assertNull(parameter.getPosition());
        assertParameterEnumeration(query, parameter);
        assertFalse(query.isBound(parameter));
        assertThrows(IllegalArgumentException.class, () -> query.getParameter("last", String.class));
        assertNumber(expected, query.setParameter(parameter, value).getSingleResult());
        assertTrue(query.isBound(parameter));
        assertEquals(value, query.getParameterValue(parameter));
        query.setParameter(parameter, null);
        assertTrue(query.isBound(parameter));
        assertNull(query.getParameterValue(parameter));
        assertNull(query.getSingleResult());
        assertEquals(type, query.getParameter("last", type).getParameterType());
        assertNumber(reboundExpected, query.setParameter(parameter, rebound).getSingleResult());
        assertEquals(rebound, query.getParameterValue(parameter));
        assertEquals(type, query.getParameter("last", type).getParameterType());
    }

    private void assertParameterEnumeration(Query query, Parameter<?> expected) {
        assertEquals(1, query.getParameters().size());
        Parameter<?> actual = query.getParameters().iterator().next();
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPosition(), actual.getPosition());
        assertEquals(expected.getParameterType(), actual.getParameterType());
    }

    private void assertRepeatedNullParameter(Query query) {
        // Existing repeated-NULL metadata depends on occurrence order; binding and results remain stable.
        Parameter<Object> parameter = query.getParameter("last", Object.class);
        assertEquals("last", parameter.getName());
        assertNull(parameter.getPosition());
        assertParameterEnumeration(query, parameter);
        assertFalse(query.isBound(parameter));
        assertNumber(94, query.setParameter(parameter, 5).getSingleResult());
        assertEquals(Integer.valueOf(5), query.getParameterValue(parameter));
        assertNumber(89, query.setParameter(parameter, 10).getSingleResult());
        assertEquals(Integer.valueOf(10), query.getParameterValue(parameter));
    }

    private void assertNumber(Number expected, Object actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertTrue("Expected a numeric result: " + actual, actual instanceof Number);
            assertEquals(expected.doubleValue(), ((Number) actual).doubleValue(), 0.0);
        }
    }

    private void assertLiteralChains(EntityManager handler) {
        assertScalar(handler, "100 - 0 - 30 - 20", 50);
        assertScalar(handler, "100 - 30 - 20", 50);
        assertScalar(handler, "100 - 30 + 20", 90);
    }

    private void assertMappedChains(EntityManager handler) {
        assertScalar(handler, "e.amount - 0 - e.deduction - e.adjustment", 50);
        assertScalar(handler, "e.amount - e.deduction - e.adjustment", 50);
        assertScalar(handler, "e.amount - e.deduction + e.adjustment", 90);
        assertScalar(handler, "e.amount - 2 * 3 - 4", 90);
    }

    private void assertLongerChains(EntityManager handler) {
        assertScalar(handler, "100 - 1 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9", 55);
        assertScalar(handler, "100 - 1 + 2 - 3 + 4 - 5 + 6 - 7 + 8 - 9", 95);
    }

    private void assertNamedQuery(EntityManager handler) {
        TypedQuery<Integer> query = handler.createNamedQuery("ArithmeticEntity.balance", Integer.class);
        assertEquals(Integer.valueOf(50), query.setParameter("id", 1).getSingleResult());
        assertEquals(Integer.valueOf(40), query.setParameter("id", 2).getSingleResult());
    }

    private void assertNamedParameters(EntityManager handler) {
        TypedQuery<Integer> query = handler.createQuery("SELECT e.amount - :first - :second" + FIRST, Integer.class);
        assertNumber(50, query.setParameter("first", 30).setParameter("second", 20).getSingleResult());
        assertNumber(85, query.setParameter("first", 10).setParameter("second", 5).getSingleResult());
        assertNumber(80, query.setParameter("first", 0).setParameter("second", 20).getSingleResult());
    }

    private void assertPositionalParameters(EntityManager handler) {
        TypedQuery<Integer> query = handler.createQuery("SELECT e.amount - ?1 + ?2" + FIRST, Integer.class);
        assertNumber(90, query.setParameter(1, 30).setParameter(2, 20).getSingleResult());
        assertNumber(70, query.setParameter(1, 40).setParameter(2, 10).getSingleResult());
        assertNumber(105, query.setParameter(1, 0).setParameter(2, 5).getSingleResult());
    }

    private void assertExplicitGrouping(EntityManager handler) {
        assertScalar(handler, "((100 - 0) - 30) - 20", 50);
        assertScalar(handler, "100 - (0 - (30 - 20))", 110);
        assertScalar(handler, "100 - (30 + 20)", 50);
        assertNumber(20, handler.createQuery("SELECT 100 / (10 / 2)" + FIRST).getSingleResult());
    }

    private void assertMultiplicativeControls(EntityManager handler) {
        assertNumber(20, handler.createQuery("SELECT 120 / 3 / 2" + FIRST).getSingleResult());
        assertNumber(80, handler.createQuery("SELECT 120 / 3 * 2" + FIRST).getSingleResult());
        assertScalar(handler, "100 - 2 * 3", 94);
        assertScalar(handler, "2 + 3 * 4", 14);
    }

    private void assertUnarySigns(EntityManager handler) {
        assertScalar(handler, "-100 - 30 - 20", -150);
        assertScalar(handler, "+100 - 30 + 20", 90);
        assertScalar(handler, "100 - -30 - 20", 110);
        assertScalar(handler, "100 + -30 - 20", 50);
        assertScalar(handler, "100 - +30 - 20", 50);
    }

    private void assertNegativeMappedFactors(EntityManager handler) {
        assertScalar(handler, "100 - -e.amount - 20", 180);
        assertScalar(handler, "100 - -e.deduction * 2 - 20", 140);
    }

    private void assertNegativeGroupedFactors(EntityManager handler) {
        assertScalar(handler, "100 - -(e.deduction - e.adjustment) - 5", 105);
        assertScalar(handler, "100 - -(30 - 20) - 5", 105);
    }

    private void assertSignedExponents(EntityManager handler) {
        assertEquals(Double.valueOf(50), handler.createQuery("SELECT 1e+2 - 3e+1 - 2e+1" + FIRST, Double.class)
                .getSingleResult());
        assertEquals(Double.valueOf(0.5), handler.createQuery("SELECT 1e-1 + 2e-1 + 2e-1" + FIRST, Double.class)
                .getSingleResult());
    }

    private void assertPredicates(EntityManager handler) {
        assertEquals(List.of(1), handler.createQuery("SELECT e.id" + FROM
                + " WHERE e.amount - e.deduction - e.adjustment = 50 AND e.amount >= 100", Integer.class)
                .getResultList());
        assertEquals(List.of(1, 2), handler.createQuery("SELECT e.id" + FROM
                + " WHERE e.amount - e.deduction + e.adjustment > 85 ORDER BY e.id", Integer.class).getResultList());
    }

    private void assertOrderBy(EntityManager handler) {
        assertEquals(List.of(3, 2, 1), handler.createQuery("SELECT e.id" + FROM
                + " ORDER BY e.amount - e.deduction - e.adjustment, e.id", Integer.class).getResultList());
    }

    private void assertHaving(EntityManager handler) {
        assertEquals(List.of(1), handler.createQuery("SELECT e.groupId" + FROM + " GROUP BY e.groupId"
                + " HAVING SUM(e.amount) - SUM(e.deduction) - SUM(e.adjustment) = 90", Integer.class).getResultList());
    }

    private void assertExplicitConstructor(EntityManager handler) {
        ArithmeticResult result = handler.createQuery("SELECT NEW " + ArithmeticResult.class.getName()
                + "(e.amount - e.deduction - e.adjustment, e.amount - e.deduction + e.adjustment)" + FIRST,
                ArithmeticResult.class).getSingleResult();
        assertEquals(Integer.valueOf(50), result.balance);
        assertEquals(Integer.valueOf(90), result.adjusted);
    }

    private void assertBulkUpdate(EntityManager handler) {
        EntityTransaction transaction = handler.getTransaction();
        transaction.begin();
        try {
            assertEquals(1, handler.createQuery("UPDATE ArithmeticEntity e"
                    + " SET e.amount = e.amount - :first - :second WHERE e.id = 1")
                    .setParameter("first", 30).setParameter("second", 20).executeUpdate());
            assertScalar(handler, "e.amount", 50);
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        assertScalar(handler, "e.amount", 100);
    }

    private void assertNullableOperands(EntityManager handler) {
        assertEquals(Arrays.asList(null, 4, 8), handler.createQuery("SELECT e.nullableAmount - 1 - 2" + FROM
                + " ORDER BY e.id", Integer.class).getResultList());
        assertEquals(List.of(1), handler.createQuery("SELECT e.id" + FROM
                + " WHERE e.nullableAmount - 1 - 2 IS NULL", Integer.class).getResultList());
    }

    private void assertFunctionsAndLogic(EntityManager handler) {
        assertScalar(handler, "ABS(e.adjustment - e.deduction)", 10);
        assertEquals(List.of(1, 2), handler.createQuery("SELECT e.id" + FROM
                + " WHERE (e.amount >= 90 AND e.deduction < 40) OR e.amount = 999 ORDER BY e.id", Integer.class)
                .getResultList());
    }

    private void assertCriteria(EntityManager handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        CriteriaQuery<Integer> left = builder.createQuery(Integer.class);
        Root<ArithmeticEntity> root = left.from(ArithmeticEntity.class);
        left.select(builder.diff(builder.diff(root.<Integer>get("amount"), root.<Integer>get("deduction")),
                root.<Integer>get("adjustment"))).where(builder.equal(root.get("id"), 1));
        assertEquals(Integer.valueOf(50), handler.createQuery(left).getSingleResult());

        CriteriaQuery<Integer> right = builder.createQuery(Integer.class);
        Root<ArithmeticEntity> other = right.from(ArithmeticEntity.class);
        right.select(builder.diff(other.<Integer>get("amount"),
                builder.diff(other.<Integer>get("deduction"), other.<Integer>get("adjustment"))))
                .where(builder.equal(other.get("id"), 1));
        assertEquals(Integer.valueOf(90), handler.createQuery(right).getSingleResult());
    }

    private void assertNativeSql(EntityManager handler) {
        assertNumber(50, handler.createNativeQuery(
                "SELECT 100 - 0 - 30 - 20 FROM JPA_ARITHMETIC_ENTITY WHERE ID = 1").getSingleResult());
        assertNumber(110, handler.createNativeQuery(
                "SELECT 100 - (0 - (30 - 20)) FROM JPA_ARITHMETIC_ENTITY WHERE ID = 1").getSingleResult());
    }

    private void assertMalformedArithmetic(EntityManager handler) {
        for (String expression : List.of("e.amount - e.deduction -", "e.amount - (e.deduction -)",
                "e.amount - e.deduction + -", "e.amount - * e.deduction")) {
            String query = "SELECT " + expression + FIRST;
            IllegalArgumentException failure = assertThrows(query, IllegalArgumentException.class,
                    () -> handler.createQuery(query));
            Throwable cause = failure;
            while (cause != null && !(cause instanceof JPQLException)) {
                cause = cause.getCause();
            }
            assertTrue(query + ": " + failure, cause instanceof JPQLException);
        }
    }

    private void assertScalar(EntityManager handler, String expression, int expected) {
        assertEquals(expression, Integer.valueOf(expected),
                handler.createQuery("SELECT " + expression + FIRST, Integer.class).getSingleResult());
    }

    public static class ArithmeticResult {
        private final Integer balance;
        private final Integer adjusted;

        public ArithmeticResult(Integer balance, Integer adjusted) {
            this.balance = balance;
            this.adjusted = adjusted;
        }
    }
}
