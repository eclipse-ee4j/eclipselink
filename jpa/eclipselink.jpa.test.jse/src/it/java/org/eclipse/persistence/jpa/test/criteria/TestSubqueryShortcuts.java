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
package org.eclipse.persistence.jpa.test.criteria;

import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.eclipse.persistence.jpa.test.criteria.model.ScalarCriteriaGroup;
import org.eclipse.persistence.jpa.test.criteria.model.ScalarCriteriaRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** SQL result semantics and parameter ownership for the four Subquery shortcuts. */
@RunWith(EmfRunner.class)
public class TestSubqueryShortcuts {
    private static final List<Integer> ALL_RECORDS = List.of(1, 2, 3, 4, 5, 6, 7);
    private static final List<Integer> ALL_GROUPS = List.of(1, 2, 3, 4);
    private static final List<Integer> GREATER_THAN_SOME_MIXED = List.of(2, 5, 7);

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {ScalarCriteriaRecord.class, ScalarCriteriaGroup.class,
                    ScalarCriteriaRecord.PrefixConverter.class, ScalarCriteriaRecord.ScaledIntegerConverter.class},
            properties = @Property(name = "eclipselink.cache.shared.default", value = "false"))
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        emf.runInTransaction(em -> {
            ScalarCriteriaGroup mixed = new ScalarCriteriaGroup(1, "mixed", 6);
            ScalarCriteriaGroup nulls = new ScalarCriteriaGroup(2, "nulls", 6);
            ScalarCriteriaGroup empty = new ScalarCriteriaGroup(3, "empty", 6);
            ScalarCriteriaGroup values = new ScalarCriteriaGroup(4, "values", 6);
            for (ScalarCriteriaGroup group : List.of(mixed, nulls, empty, values)) {
                em.persist(group);
            }
            em.persist(record(1, 2, "b", mixed));
            em.persist(record(2, 7, "g", mixed));
            em.persist(record(3, null, "n", mixed));
            em.persist(record(4, null, "n", nulls));
            em.persist(record(5, 10, "j", values));
            em.persist(record(6, -1, "a", values));
            em.persist(record(7, 5, "e", values));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createQuery("DELETE FROM ScalarCriteriaRecord").executeUpdate();
            em.createQuery("DELETE FROM ScalarCriteriaGroup").executeUpdate();
        });
    }

    @Test
    public void existsCorrelatedFindsRowsIncludingNullSelections() {
        verifyExists(true, false, 0, List.of(1, 2, 4));
    }

    @Test
    public void notExistsCorrelatedFindsOnlyEmptyGroup() {
        verifyExists(true, true, 0, List.of(3));
    }

    @Test
    public void existsUncorrelatedDependsOnRowsRatherThanNonNullValues() {
        verifyExists(false, false, 1, ALL_GROUPS);
        verifyExists(false, false, 2, ALL_GROUPS);
        verifyExists(false, false, 3, List.of());
    }

    @Test
    public void notExistsUncorrelatedDistinguishesNullOnlyAndEmptySets() {
        verifyExists(false, true, 1, List.of());
        verifyExists(false, true, 2, List.of());
        verifyExists(false, true, 3, ALL_GROUPS);
    }

    @Test
    public void correlatedExistsDiscoversParametersAndSupportsRebinding() {
        for (boolean shortcut : List.of(false, true)) {
            try (EntityManager em = emf.createEntityManager()) {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                Root<ScalarCriteriaGroup> group = criteria.from(ScalarCriteriaGroup.class);
                Subquery<Integer> subquery = criteria.subquery(Integer.class);
                Root<ScalarCriteriaGroup> correlated = subquery.correlate(group);
                Root<ScalarCriteriaRecord> record = subquery.from(ScalarCriteriaRecord.class);
                ParameterExpression<Integer> minimum = cb.parameter(Integer.class, "minimum");
                subquery.select(record.get("amount")).where(cb.equal(record.get("group"), correlated),
                        cb.gt(record.get("amount"), minimum));
                criteria.select(group.get("id")).where(shortcut ? subquery.exists() : cb.exists(subquery))
                        .orderBy(cb.asc(group.get("id")));
                TypedQuery<Integer> query = em.createQuery(criteria);
                assertEquals(Set.of(minimum), criteria.getParameters());
                assertEquals(Integer.class, query.getParameter("minimum").getParameterType());
                assertEquals(List.of(1, 4), query.setParameter(minimum, 4).getResultList());
                assertEquals(List.of(4), query.setParameter("minimum", 9).getResultList());
                assertEquals(List.of(), query.setParameter(minimum, 20).getResultList());
                assertEquals(List.of(1, 4), query.setParameter(minimum, 4).getResultList());
            }
        }
    }

    @Test
    public void allNonNullValuesRequiresEveryComparisonToBeTrue() {
        verifyQuantified(Quantifier.ALL, 1, true, false, List.of(5));
    }

    @Test
    public void someNonNullValuesRequiresOneComparisonToBeTrue() {
        verifyQuantified(Quantifier.SOME, 1, true, false, GREATER_THAN_SOME_MIXED);
    }

    @Test
    public void anyNonNullValuesRequiresOneComparisonToBeTrue() {
        verifyQuantified(Quantifier.ANY, 1, true, false, GREATER_THAN_SOME_MIXED);
    }

    @Test
    public void allMixedNullValuesNeverTreatsUnknownAsTrue() {
        verifyQuantified(Quantifier.ALL, 1, false, false, List.of());
    }

    @Test
    public void someMixedNullValuesRetainsTrueComparisons() {
        verifyQuantified(Quantifier.SOME, 1, false, false, GREATER_THAN_SOME_MIXED);
    }

    @Test
    public void anyMixedNullValuesRetainsTrueComparisons() {
        verifyQuantified(Quantifier.ANY, 1, false, false, GREATER_THAN_SOME_MIXED);
    }

    @Test
    public void allNullOnlyValuesProducesUnknown() {
        verifyQuantified(Quantifier.ALL, 2, false, false, List.of());
    }

    @Test
    public void someNullOnlyValuesProducesUnknown() {
        verifyQuantified(Quantifier.SOME, 2, false, false, List.of());
    }

    @Test
    public void anyNullOnlyValuesProducesUnknown() {
        verifyQuantified(Quantifier.ANY, 2, false, false, List.of());
    }

    @Test
    public void allEmptySetIsTrueIncludingForNullLeftOperand() {
        verifyQuantified(Quantifier.ALL, 3, false, false, ALL_RECORDS);
    }

    @Test
    public void someEmptySetIsFalseIncludingForNullLeftOperand() {
        verifyQuantified(Quantifier.SOME, 3, false, false, List.of());
    }

    @Test
    public void anyEmptySetIsFalseIncludingForNullLeftOperand() {
        verifyQuantified(Quantifier.ANY, 3, false, false, List.of());
    }

    @Test
    public void negationPreservesThreeValuedLogicAndEmptySetSemantics() {
        for (Quantifier quantifier : Quantifier.values()) {
            verifyQuantified(quantifier, 2, false, true, List.of());
            verifyQuantified(quantifier, 3, false, true,
                    quantifier == Quantifier.ALL ? List.of() : ALL_RECORDS);
            verifyQuantified(quantifier, 1, false, true,
                    quantifier == Quantifier.ALL ? List.of(1, 2, 6, 7) : List.of());
        }
    }

    @Test
    public void quantifiedQueriesDiscoverParametersAndCanBeReusedWithDifferentSets() {
        for (Quantifier quantifier : Quantifier.values()) {
            for (boolean shortcut : List.of(false, true)) {
                try (EntityManager em = emf.createEntityManager()) {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                    Root<ScalarCriteriaRecord> outer = criteria.from(ScalarCriteriaRecord.class);
                    Subquery<Integer> subquery = criteria.subquery(Integer.class);
                    Root<ScalarCriteriaRecord> inner = subquery.from(ScalarCriteriaRecord.class);
                    ParameterExpression<Integer> groupId = cb.parameter(Integer.class, "groupId");
                    subquery.select(inner.get("amount")).where(cb.equal(inner.get("group").get("id"), groupId));
                    Expression<Integer> quantified = quantifier.apply(cb, subquery, shortcut);
                    criteria.select(outer.get("id")).where(cb.gt(outer.get("amount"), quantified))
                            .orderBy(cb.asc(outer.get("id")));
                    assertEquals(Set.of(groupId), criteria.getParameters());
                    TypedQuery<Integer> query = em.createQuery(criteria);
                    assertEquals(Set.of(groupId), query.getParameters());
                    assertEquals(Integer.class, query.getParameter("groupId").getParameterType());
                    List<Integer> mixed = quantifier == Quantifier.ALL ? List.of() : GREATER_THAN_SOME_MIXED;
                    assertEquals(mixed, query.setParameter(groupId, 1).getResultList());
                    assertEquals(quantifier == Quantifier.ALL ? ALL_RECORDS : List.of(),
                            query.setParameter("groupId", 3).getResultList());
                    assertEquals(List.of(), query.setParameter(groupId, 2).getResultList());
                    assertEquals(mixed, query.setParameter(groupId, 1).getResultList());
                }
            }
        }
    }

    @Test
    public void correlatedQuantifiersUseTheCurrentOuterGroup() {
        for (Quantifier quantifier : Quantifier.values()) {
            for (boolean shortcut : List.of(false, true)) {
                try (EntityManager em = emf.createEntityManager()) {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                    Root<ScalarCriteriaGroup> group = criteria.from(ScalarCriteriaGroup.class);
                    Subquery<Integer> subquery = criteria.subquery(Integer.class);
                    Root<ScalarCriteriaGroup> correlated = subquery.correlate(group);
                    Root<ScalarCriteriaRecord> record = subquery.from(ScalarCriteriaRecord.class);
                    subquery.select(record.get("amount")).where(cb.equal(record.get("group"), correlated));
                    criteria.select(group.get("id"))
                            .where(cb.gt(group.get("rank"), quantifier.apply(cb, subquery, shortcut)))
                            .orderBy(cb.asc(group.get("id")));
                    assertEquals(quantifier == Quantifier.ALL ? List.of(3) : List.of(1, 4),
                            em.createQuery(criteria).getResultList());
                }
            }
        }
    }

    @Test
    public void quantifiedStringExpressionsRetainTheirTypeAndComparisonSemantics() {
        for (Quantifier quantifier : Quantifier.values()) {
            for (boolean shortcut : List.of(false, true)) {
                try (EntityManager em = emf.createEntityManager()) {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                    Root<ScalarCriteriaRecord> outer = criteria.from(ScalarCriteriaRecord.class);
                    Subquery<String> subquery = criteria.subquery(String.class);
                    Root<ScalarCriteriaRecord> inner = subquery.from(ScalarCriteriaRecord.class);
                    subquery.select(inner.get("text")).where(cb.equal(inner.get("group").get("id"), 4));
                    Expression<String> quantified = quantifier.apply(cb, subquery, shortcut);
                    assertEquals(String.class, quantified.getJavaType());
                    criteria.select(outer.get("id")).where(cb.greaterThan(outer.get("text"), quantified))
                            .orderBy(cb.asc(outer.get("id")));
                    assertEquals(quantifier == Quantifier.ALL ? List.of(3, 4) : List.of(1, 2, 3, 4, 5, 7),
                            em.createQuery(criteria).getResultList());
                }
            }
        }
    }

    @Test
    public void quantifiedDoubleExpressionsRetainTheirTypeAndComparisonSemantics() {
        for (Quantifier quantifier : Quantifier.values()) {
            for (boolean shortcut : List.of(false, true)) {
                try (EntityManager em = emf.createEntityManager()) {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                    Root<ScalarCriteriaRecord> outer = criteria.from(ScalarCriteriaRecord.class);
                    Subquery<Double> subquery = criteria.subquery(Double.class);
                    Root<ScalarCriteriaRecord> inner = subquery.from(ScalarCriteriaRecord.class);
                    subquery.select(inner.get("fraction")).where(cb.equal(inner.get("group").get("id"), 1),
                            cb.isNotNull(inner.get("fraction")));
                    Expression<Double> quantified = quantifier.apply(cb, subquery, shortcut);
                    assertEquals(Double.class, quantified.getJavaType());
                    criteria.select(outer.get("id")).where(cb.gt(outer.get("fraction"), quantified))
                            .orderBy(cb.asc(outer.get("id")));
                    assertEquals(quantifier == Quantifier.ALL ? List.of(5) : GREATER_THAN_SOME_MIXED,
                            em.createQuery(criteria).getResultList());
                }
            }
        }
    }

    private void verifyExists(boolean correlated, boolean negated, int groupId, List<Integer> expected) {
        for (boolean shortcut : List.of(false, true)) {
            try (EntityManager em = emf.createEntityManager()) {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                Root<ScalarCriteriaGroup> group = criteria.from(ScalarCriteriaGroup.class);
                Subquery<Integer> subquery = criteria.subquery(Integer.class);
                Root<ScalarCriteriaRecord> record = subquery.from(ScalarCriteriaRecord.class);
                subquery.select(record.get("amount"));
                if (correlated) {
                    subquery.where(cb.equal(record.get("group"), subquery.correlate(group)));
                } else {
                    subquery.where(cb.equal(record.get("group").get("id"), groupId));
                }
                Predicate predicate = shortcut ? subquery.exists() : cb.exists(subquery);
                assertTrue(predicate.getJavaType() == Boolean.class || predicate.getJavaType() == boolean.class);
                criteria.select(group.get("id")).where(negated ? cb.not(predicate) : predicate)
                        .orderBy(cb.asc(group.get("id")));
                assertEquals(expected, em.createQuery(criteria).getResultList());
            }
        }
    }

    private void verifyQuantified(Quantifier quantifier, int groupId, boolean nonNull, boolean negated,
            List<Integer> expected) {
        for (boolean shortcut : List.of(false, true)) {
            try (EntityManager em = emf.createEntityManager()) {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
                Root<ScalarCriteriaRecord> outer = criteria.from(ScalarCriteriaRecord.class);
                Subquery<Integer> subquery = criteria.subquery(Integer.class);
                Root<ScalarCriteriaRecord> inner = subquery.from(ScalarCriteriaRecord.class);
                Predicate selectedGroup = cb.equal(inner.get("group").get("id"), groupId);
                subquery.select(inner.get("amount")).where(nonNull
                        ? cb.and(selectedGroup, cb.isNotNull(inner.get("amount"))) : selectedGroup);
                Expression<Integer> quantified = quantifier.apply(cb, subquery, shortcut);
                assertEquals(Integer.class, quantified.getJavaType());
                Predicate comparison = cb.gt(outer.get("amount"), quantified);
                criteria.select(outer.get("id")).where(negated ? cb.not(comparison) : comparison)
                        .orderBy(cb.asc(outer.get("id")));
                assertEquals(quantifier + ", shortcut=" + shortcut + ", group=" + groupId + ", negated=" + negated,
                        expected, em.createQuery(criteria).getResultList());
            }
        }
    }

    private static ScalarCriteriaRecord record(int id, Integer amount, String text, ScalarCriteriaGroup group) {
        return new ScalarCriteriaRecord(id, amount, amount == null ? null : amount.doubleValue(), text, null, group);
    }

    private enum Quantifier {
        ALL, SOME, ANY;

        <T> Expression<T> apply(CriteriaBuilder builder, Subquery<T> subquery, boolean shortcut) {
            return switch (this) {
                case ALL -> shortcut ? subquery.all() : builder.all(subquery);
                case SOME -> shortcut ? subquery.some() : builder.some(subquery);
                case ANY -> shortcut ? subquery.any() : builder.any(subquery);
            };
        }
    }
}
