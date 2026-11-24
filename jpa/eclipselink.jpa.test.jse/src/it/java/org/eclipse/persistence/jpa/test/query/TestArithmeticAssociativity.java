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
import jakarta.persistence.EntityTransaction;
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
        emf.runInTransaction(em -> {
            em.persist(new ArithmeticEntity(1, 100, 30, 20, 1, null));
            em.persist(new ArithmeticEntity(2, 90, 10, 40, 1, 7));
            em.persist(new ArithmeticEntity(3, 60, 20, 5, 2, 11));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> em.createQuery("SELECT e" + FROM, ArithmeticEntity.class)
                .getResultList().forEach(em::remove));
        emf.getCache().evictAll();
    }

    @Test
    public void entityManagerLiteralChainsAssociateLeft() {
        try (EntityManager em = emf.createEntityManager()) {
            assertLiteralChains(em);
        }
    }

    @Test
    public void entityAgentLiteralChainsAssociateLeft() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertLiteralChains(agent);
        }
    }

    @Test
    public void entityManagerMappedChainsAssociateLeft() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMappedChains(em);
        }
    }

    @Test
    public void entityAgentMappedChainsAssociateLeft() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMappedChains(agent);
        }
    }

    @Test
    public void entityManagerLongerChainsAssociateLeft() {
        try (EntityManager em = emf.createEntityManager()) {
            assertLongerChains(em);
        }
    }

    @Test
    public void entityAgentLongerChainsAssociateLeft() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertLongerChains(agent);
        }
    }

    @Test
    public void entityManagerNamedQueryAssociatesLeft() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNamedQuery(em);
        }
    }

    @Test
    public void entityAgentNamedQueryAssociatesLeft() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNamedQuery(agent);
        }
    }

    @Test
    public void entityManagerNamedParametersCanBeRebound() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNamedParameters(em);
        }
    }

    @Test
    public void entityAgentNamedParametersCanBeRebound() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNamedParameters(agent);
        }
    }

    @Test
    public void entityManagerPositionalParametersCanBeRebound() {
        try (EntityManager em = emf.createEntityManager()) {
            assertPositionalParameters(em);
        }
    }

    @Test
    public void entityAgentPositionalParametersCanBeRebound() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertPositionalParameters(agent);
        }
    }

    @Test
    public void entityManagerExplicitGroupingRemainsIntact() {
        try (EntityManager em = emf.createEntityManager()) {
            assertExplicitGrouping(em);
        }
    }

    @Test
    public void entityAgentExplicitGroupingRemainsIntact() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertExplicitGrouping(agent);
        }
    }

    @Test
    public void entityManagerMultiplicativeControlsRemainValid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMultiplicativeControls(em);
        }
    }

    @Test
    public void entityAgentMultiplicativeControlsRemainValid() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMultiplicativeControls(agent);
        }
    }

    @Test
    public void entityManagerUnarySignsRemainOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            assertUnarySigns(em);
        }
    }

    @Test
    public void entityAgentUnarySignsRemainOperands() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertUnarySigns(agent);
        }
    }

    @Test
    public void entityManagerNegativeMappedFactorsRemainOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNegativeMappedFactors(em);
        }
    }

    @Test
    public void entityAgentNegativeMappedFactorsRemainOperands() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNegativeMappedFactors(agent);
        }
    }

    @Test
    public void entityManagerNegativeGroupedFactorsRemainOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNegativeGroupedFactors(em);
        }
    }

    @Test
    public void entityAgentNegativeGroupedFactorsRemainOperands() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNegativeGroupedFactors(agent);
        }
    }

    @Test
    public void entityManagerSignedExponentsRemainLiterals() {
        try (EntityManager em = emf.createEntityManager()) {
            assertSignedExponents(em);
        }
    }

    @Test
    public void entityAgentSignedExponentsRemainLiterals() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertSignedExponents(agent);
        }
    }

    @Test
    public void entityManagerPredicatesUseLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertPredicates(em);
        }
    }

    @Test
    public void entityAgentPredicatesUseLeftAssociation() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertPredicates(agent);
        }
    }

    @Test
    public void entityManagerOrderByUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertOrderBy(em);
        }
    }

    @Test
    public void entityAgentOrderByUsesLeftAssociation() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertOrderBy(agent);
        }
    }

    @Test
    public void entityManagerHavingUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertHaving(em);
        }
    }

    @Test
    public void entityAgentHavingUsesLeftAssociation() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertHaving(agent);
        }
    }

    @Test
    public void entityManagerExplicitConstructorUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertExplicitConstructor(em);
        }
    }

    @Test
    public void entityAgentExplicitConstructorUsesLeftAssociation() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertExplicitConstructor(agent);
        }
    }

    @Test
    public void entityManagerImplicitConstructorUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertImplicitConstructor(em);
        }
    }

    @Test
    public void entityAgentImplicitConstructorUsesLeftAssociation() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertImplicitConstructor(agent);
        }
    }

    @Test
    public void entityManagerBulkUpdateUsesLeftAssociation() {
        try (EntityManager em = emf.createEntityManager()) {
            assertBulkUpdate(em);
        }
    }

    @Test
    public void entityAgentBulkUpdateUsesLeftAssociation() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertBulkUpdate(agent);
        }
    }

    @Test
    public void entityManagerNullableOperandsPreserveNull() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNullableOperands(em);
        }
    }

    @Test
    public void entityAgentNullableOperandsPreserveNull() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNullableOperands(agent);
        }
    }

    @Test
    public void entityManagerFunctionsAndLogicRemainValid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertFunctionsAndLogic(em);
        }
    }

    @Test
    public void entityAgentFunctionsAndLogicRemainValid() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertFunctionsAndLogic(agent);
        }
    }

    @Test
    public void entityManagerCriteriaRetainsExplicitTrees() {
        try (EntityManager em = emf.createEntityManager()) {
            assertCriteria(em);
        }
    }

    @Test
    public void entityAgentCriteriaRetainsExplicitTrees() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertCriteria(agent);
        }
    }

    @Test
    public void entityManagerNativeSqlRemainsIndependent() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNativeSql(em);
        }
    }

    @Test
    public void entityAgentNativeSqlRemainsIndependent() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNativeSql(agent);
        }
    }

    @Test
    public void entityManagerMalformedArithmeticRemainsInvalid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMalformedArithmetic(em);
        }
    }

    @Test
    public void entityAgentMalformedArithmeticRemainsInvalid() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMalformedArithmetic(agent);
        }
    }

    private void assertLiteralChains(EntityHandler handler) {
        assertScalar(handler, "100 - 0 - 30 - 20", 50);
        assertScalar(handler, "100 - 30 - 20", 50);
        assertScalar(handler, "100 - 30 + 20", 90);
    }

    private void assertMappedChains(EntityHandler handler) {
        assertScalar(handler, "e.amount - 0 - e.deduction - e.adjustment", 50);
        assertScalar(handler, "e.amount - e.deduction - e.adjustment", 50);
        assertScalar(handler, "e.amount - e.deduction + e.adjustment", 90);
        assertScalar(handler, "e.amount - 2 * 3 - 4", 90);
    }

    private void assertLongerChains(EntityHandler handler) {
        assertScalar(handler, "100 - 1 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9", 55);
        assertScalar(handler, "100 - 1 + 2 - 3 + 4 - 5 + 6 - 7 + 8 - 9", 95);
    }

    private void assertNamedQuery(EntityHandler handler) {
        TypedQuery<Integer> query = handler.createNamedQuery("ArithmeticEntity.balance", Integer.class);
        assertEquals(Integer.valueOf(50), query.setParameter("id", 1).getSingleResult());
        assertEquals(Integer.valueOf(40), query.setParameter("id", 2).getSingleResult());
    }

    private void assertNamedParameters(EntityHandler handler) {
        TypedQuery<Integer> query = handler.createQuery("SELECT e.amount - :first - :second" + FIRST, Integer.class);
        assertEquals(Integer.valueOf(50), query.setParameter("first", 30).setParameter("second", 20).getSingleResult());
        assertEquals(Integer.valueOf(85), query.setParameter("first", 10).setParameter("second", 5).getSingleResult());
        assertEquals(Integer.valueOf(80), query.setParameter("first", 0).setParameter("second", 20).getSingleResult());
    }

    private void assertPositionalParameters(EntityHandler handler) {
        TypedQuery<Integer> query = handler.createQuery("SELECT e.amount - ?1 + ?2" + FIRST, Integer.class);
        assertEquals(Integer.valueOf(90), query.setParameter(1, 30).setParameter(2, 20).getSingleResult());
        assertEquals(Integer.valueOf(70), query.setParameter(1, 40).setParameter(2, 10).getSingleResult());
        assertEquals(Integer.valueOf(105), query.setParameter(1, 0).setParameter(2, 5).getSingleResult());
    }

    private void assertExplicitGrouping(EntityHandler handler) {
        assertScalar(handler, "((100 - 0) - 30) - 20", 50);
        assertScalar(handler, "100 - (0 - (30 - 20))", 110);
        assertScalar(handler, "100 - (30 + 20)", 50);
        assertScalar(handler, "100 / (10 / 2)", 20);
    }

    private void assertMultiplicativeControls(EntityHandler handler) {
        assertScalar(handler, "120 / 3 / 2", 20);
        assertScalar(handler, "120 / 3 * 2", 80);
        assertScalar(handler, "100 - 2 * 3", 94);
        assertScalar(handler, "2 + 3 * 4", 14);
    }

    private void assertUnarySigns(EntityHandler handler) {
        assertScalar(handler, "-100 - 30 - 20", -150);
        assertScalar(handler, "+100 - 30 + 20", 90);
        assertScalar(handler, "100 - -30 - 20", 110);
        assertScalar(handler, "100 + -30 - 20", 50);
        assertScalar(handler, "100 - +30 - 20", 50);
    }

    private void assertNegativeMappedFactors(EntityHandler handler) {
        assertScalar(handler, "100 - -e.amount - 20", 180);
        assertScalar(handler, "100 - -e.deduction * 2 - 20", 140);
    }

    private void assertNegativeGroupedFactors(EntityHandler handler) {
        assertScalar(handler, "100 - -(e.deduction - e.adjustment) - 5", 105);
        assertScalar(handler, "100 - -(30 - 20) - 5", 105);
    }

    private void assertSignedExponents(EntityHandler handler) {
        assertEquals(Double.valueOf(50), handler.createQuery("SELECT 1e+2 - 3e+1 - 2e+1" + FIRST, Double.class)
                .getSingleResult());
        assertEquals(Double.valueOf(0.5), handler.createQuery("SELECT 1e-1 + 2e-1 + 2e-1" + FIRST, Double.class)
                .getSingleResult());
    }

    private void assertPredicates(EntityHandler handler) {
        assertEquals(List.of(1), handler.createQuery("SELECT e.id" + FROM
                + " WHERE e.amount - e.deduction - e.adjustment = 50 AND e.amount >= 100", Integer.class)
                .getResultList());
        assertEquals(List.of(1, 2), handler.createQuery("SELECT e.id" + FROM
                + " WHERE e.amount - e.deduction + e.adjustment > 85 ORDER BY e.id", Integer.class).getResultList());
    }

    private void assertOrderBy(EntityHandler handler) {
        assertEquals(List.of(3, 2, 1), handler.createQuery("SELECT e.id" + FROM
                + " ORDER BY e.amount - e.deduction - e.adjustment, e.id", Integer.class).getResultList());
    }

    private void assertHaving(EntityHandler handler) {
        assertEquals(List.of(1), handler.createQuery("SELECT e.groupId" + FROM + " GROUP BY e.groupId"
                + " HAVING SUM(e.amount) - SUM(e.deduction) - SUM(e.adjustment) = 90", Integer.class).getResultList());
    }

    private void assertExplicitConstructor(EntityHandler handler) {
        ArithmeticResult result = handler.createQuery("SELECT NEW " + ArithmeticResult.class.getName()
                + "(e.amount - e.deduction - e.adjustment, e.amount - e.deduction + e.adjustment)" + FIRST,
                ArithmeticResult.class).getSingleResult();
        assertEquals(Integer.valueOf(50), result.balance);
        assertEquals(Integer.valueOf(90), result.adjusted);
    }

    private void assertImplicitConstructor(EntityHandler handler) {
        ArithmeticResult result = handler.createQuery("SELECT e.amount - e.deduction - e.adjustment,"
                + " e.amount - e.deduction + e.adjustment" + FIRST, ArithmeticResult.class).getSingleResult();
        assertEquals(Integer.valueOf(50), result.balance);
        assertEquals(Integer.valueOf(90), result.adjusted);
    }

    private void assertBulkUpdate(EntityHandler handler) {
        EntityTransaction transaction = handler.getTransaction();
        transaction.begin();
        try {
            assertEquals(1, handler.createStatement("UPDATE ArithmeticEntity e"
                    + " SET e.amount = e.amount - :first - :second WHERE e.id = 1")
                    .setParameter("first", 30).setParameter("second", 20).execute());
            assertScalar(handler, "e.amount", 50);
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        assertScalar(handler, "e.amount", 100);
    }

    private void assertNullableOperands(EntityHandler handler) {
        assertEquals(Arrays.asList(null, 4, 8), handler.createQuery("SELECT e.nullableAmount - 1 - 2" + FROM
                + " ORDER BY e.id", Integer.class).getResultList());
        assertEquals(List.of(1), handler.createQuery("SELECT e.id" + FROM
                + " WHERE e.nullableAmount - 1 - 2 IS NULL", Integer.class).getResultList());
    }

    private void assertFunctionsAndLogic(EntityHandler handler) {
        assertScalar(handler, "ABS(e.adjustment - e.deduction)", 10);
        assertEquals(List.of(1, 2), handler.createQuery("SELECT e.id" + FROM
                + " WHERE (e.amount >= 90 AND e.deduction < 40) OR e.amount = 999 ORDER BY e.id", Integer.class)
                .getResultList());
    }

    private void assertCriteria(EntityHandler handler) {
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

    private void assertNativeSql(EntityHandler handler) {
        assertEquals(Integer.valueOf(50), handler.createNativeQuery(
                "SELECT 100 - 0 - 30 - 20 FROM JPA_ARITHMETIC_ENTITY WHERE ID = 1", Integer.class).getSingleResult());
        assertEquals(Integer.valueOf(110), handler.createNativeQuery(
                "SELECT 100 - (0 - (30 - 20)) FROM JPA_ARITHMETIC_ENTITY WHERE ID = 1", Integer.class).getSingleResult());
    }

    private void assertMalformedArithmetic(EntityHandler handler) {
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

    private void assertScalar(EntityHandler handler, String expression, int expected) {
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
