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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.BooleanExpression;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.BooleanAttribute;

import org.eclipse.persistence.jpa.test.criteria.model.BooleanCriteriaGroup;
import org.eclipse.persistence.jpa.test.criteria.model.BooleanCriteriaGroup_;
import org.eclipse.persistence.jpa.test.criteria.model.BooleanCriteriaRecord;
import org.eclipse.persistence.jpa.test.criteria.model.BooleanCriteriaRecord_;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/** Three-valued Boolean expression semantics through the public Criteria API. */
@RunWith(EmfRunner.class)
public class TestBooleanCriteriaExpressions {
    private static final Boolean T = Boolean.TRUE;
    private static final Boolean F = Boolean.FALSE;
    private static final Boolean N = null;
    private static final List<Boolean> LEFT = values(T, T, T, F, F, F, N, N, N);
    private static final List<Boolean> AND = values(T, F, N, F, F, F, N, F, N);
    private static final List<Boolean> OR = values(T, T, T, T, F, N, T, N, N);
    private static final List<Boolean> COALESCED = values(T, T, T, F, F, F, T, F, N);
    private static final List<Boolean> NULLIFIED = values(N, T, T, F, N, F, N, N, N);
    private static final List<Integer> ALL_IDS = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {BooleanCriteriaRecord.class, BooleanCriteriaGroup.class,
                    BooleanCriteriaRecord.YesNoConverter.class, BooleanCriteriaRecord.IntegerBooleanConverter.class},
            properties = @Property(name = "eclipselink.cache.shared.default", value = "false"))
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        emf.runInTransaction(em -> {
            List<Boolean> flags = values(T, F, N);
            int id = 1;
            for (int groupId = 1; groupId <= flags.size(); groupId++) {
                Boolean left = flags.get(groupId - 1);
                BooleanCriteriaGroup group = new BooleanCriteriaGroup(groupId, left);
                em.persist(group);
                for (Boolean right : flags) {
                    em.persist(new BooleanCriteriaRecord(id++, left, right, group));
                }
            }
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createQuery("DELETE FROM BooleanCriteriaRecord").executeUpdate();
            em.createQuery("DELETE FROM BooleanCriteriaGroup").executeUpdate();
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canonicalAndRuntimeBooleanAttributesPreserveSelectionType() {
        verifyValues(Query::left, q -> q.root.get("leftValue"), LEFT);
        verifyValues(q -> {
            BooleanAttribute<BooleanCriteriaRecord> attribute = (BooleanAttribute<BooleanCriteriaRecord>) emf
                    .getMetamodel().entity(BooleanCriteriaRecord.class).getSingularAttribute("leftValue", Boolean.class);
            BooleanExpression expression = q.root.get(attribute);
            assertEquals(Boolean.class, expression.getJavaType());
            return expression;
        }, Query::left, LEFT);
    }

    @Test
    public void primitiveBooleanAttributeSupportsFluentOperations() {
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.primitiveValue),
                q -> q.root.get("primitiveValue"), values(T, T, T, F, F, F, F, F, F));
        verifyTruth(q -> q.root.get(BooleanCriteriaRecord_.primitiveValue).not(),
                List.of(4, 5, 6, 7, 8, 9), List.of(1, 2, 3));
    }

    @Test
    public void booleanLiteralsAndTypedNullAreSelectable() {
        verifyValues(q -> q.cb.booleanLiteral(true), q -> q.cb.literal(true), all(T));
        verifyValues(q -> q.cb.booleanLiteral(false), q -> q.cb.literal(false), all(F));
        verifyValues(q -> q.cb.booleanLiteral(true).nullif(true),
                q -> q.cb.nullLiteral(Boolean.class), all(N));
    }

    @Test
    public void scalarAndUsesThreeValuedLogic() {
        verifyTruth(q -> q.left().and(q.right()), List.of(1), List.of(2, 4, 5, 6, 8));
    }

    @Test
    public void scalarOrUsesThreeValuedLogic() {
        verifyTruth(q -> q.left().or(q.right()), List.of(1, 2, 3, 4, 7), List.of(5));
    }

    @Test
    public void scalarNotPreservesUnknown() {
        verifyTruth(q -> q.left().not(), List.of(4, 5, 6), List.of(1, 2, 3));
    }

    @Test
    public void fluentAndCanBeSelectedWithBuilderEquivalentResults() {
        verifyValues(q -> q.left().and(q.right()), q -> q.cb.and(q.left(), q.right()), AND);
    }

    @Test
    public void fluentOrCanBeSelectedWithBuilderEquivalentResults() {
        verifyValues(q -> q.left().or(q.right()), q -> q.cb.or(q.left(), q.right()), OR);
    }

    @Test
    public void fluentNotCanBeSelectedWithBuilderEquivalentResults() {
        verifyValues(q -> q.left().not(), q -> q.cb.not(q.left()), values(F, F, F, T, T, T, N, N, N));
    }

    @Test
    public void andCombinesScalarAndPredicateInEitherPosition() {
        verifyTruth(q -> q.left().and(q.right().equalTo(true)), List.of(1), List.of(2, 4, 5, 6, 8));
        verifyTruth(q -> q.left().equalTo(true).and(q.right()), List.of(1), List.of(2, 4, 5, 6, 8));
    }

    @Test
    public void orCombinesScalarAndPredicateInEitherPosition() {
        verifyTruth(q -> q.left().or(q.right().equalTo(true)), List.of(1, 2, 3, 4, 7), List.of(5));
        verifyTruth(q -> q.left().equalTo(true).or(q.right()), List.of(1, 2, 3, 4, 7), List.of(5));
    }

    @Test
    public void equalityPredicateSupportsFluentOr() {
        verifyTruth(q -> q.root.get(BooleanCriteriaRecord_.id).equalTo(6).or(q.left()),
                List.of(1, 2, 3, 6), List.of(4, 5));
    }

    @Test
    public void scalarCoalesceAcceptsTrueFalseAndNullValues() {
        verifyValues(q -> q.left().coalesce(true), q -> q.cb.coalesce(q.left(), true),
                values(T, T, T, F, F, F, T, T, T));
        verifyValues(q -> q.left().coalesce(false), q -> q.cb.coalesce(q.left(), false),
                values(T, T, T, F, F, F, F, F, F));
        verifyValues(q -> q.left().coalesce((Boolean) null),
                q -> q.cb.coalesce(q.left(), q.cb.nullLiteral(Boolean.class)), LEFT);
    }

    @Test
    public void scalarCoalesceAcceptsExpressionsIncludingTypedNull() {
        verifyValues(q -> q.left().coalesce(q.right()), q -> q.cb.coalesce(q.left(), q.right()), COALESCED);
        verifyValues(q -> q.left().coalesce(q.cb.nullLiteral(Boolean.class)), Query::left, LEFT);
    }

    @Test
    public void scalarNullifAcceptsTrueFalseAndNullValues() {
        verifyValues(q -> q.left().nullif(true), q -> q.cb.nullif(q.left(), true),
                values(N, N, N, F, F, F, N, N, N));
        verifyValues(q -> q.left().nullif(false), q -> q.cb.nullif(q.left(), false),
                values(T, T, T, N, N, N, N, N, N));
        verifyValues(q -> q.left().nullif((Boolean) null),
                q -> q.cb.nullif(q.left(), q.cb.nullLiteral(Boolean.class)), LEFT);
    }

    @Test
    public void scalarNullifAcceptsExpressionsIncludingTypedNull() {
        verifyValues(q -> q.left().nullif(q.right()), q -> q.cb.nullif(q.left(), q.right()), NULLIFIED);
        verifyValues(q -> q.left().nullif(q.cb.nullLiteral(Boolean.class)), Query::left, LEFT);
    }

    @Test
    public void correlatedBooleanSubqueriesSupportScalarAndPredicateNullHandling() {
        for (boolean predicate : List.of(false, true)) {
            try (EntityManager em = emf.createEntityManager()) {
                Query q = new Query(em);
                Subquery<Boolean> subquery = q.criteria.subquery(Boolean.class);
                Root<BooleanCriteriaRecord> inner = subquery.from(BooleanCriteriaRecord.class);
                subquery.select(inner.get(BooleanCriteriaRecord_.rightValue))
                        .where(q.cb.equal(inner.get("id"), q.root.get("id")));
                for (boolean coalesce : List.of(true, false)) {
                    BooleanExpression value = coalesce ? receiver(q, predicate).coalesce(subquery)
                            : receiver(q, predicate).nullif(subquery);
                    List<Boolean> expected = coalesce ? COALESCED : NULLIFIED;
                    assertEquals(matchingIds(expected, T), q.ids(value));
                    assertEquals(matchingIds(expected, F), q.ids(value.not()));
                    assertEquals(matchingIds(expected, N), q.ids(value.isNull()));
                    assertEquals(Set.of(q.root), q.criteria.getRoots());
                }
            }
        }
    }

    @Test
    public void noncorrelatedBooleanSubqueriesRetainParametersAndRebindNullAndMissingRows() {
        for (boolean predicate : List.of(false, true)) {
            for (boolean coalesce : List.of(true, false)) {
                try (EntityManager em = emf.createEntityManager()) {
                    Query q = new Query(em);
                    ParameterExpression<Integer> rowId = q.cb.parameter(Integer.class, "subqueryRow");
                    Subquery<Boolean> subquery = q.criteria.subquery(Boolean.class);
                    Root<BooleanCriteriaRecord> inner = subquery.from(BooleanCriteriaRecord.class);
                    subquery.select(inner.get(BooleanCriteriaRecord_.rightValue))
                            .where(q.cb.equal(inner.get("id"), rowId));
                    BooleanExpression value = coalesce ? receiver(q, predicate).coalesce(subquery)
                            : receiver(q, predicate).nullif(subquery);
                    q.criteria.multiselect(q.root.get("id")).where(value);
                    TypedQuery<Tuple> trueRows = em.createQuery(q.criteria);
                    TypedQuery<Tuple> falseRows = em.createQuery(q.criteria.where(value.not()));
                    TypedQuery<Tuple> nullRows = em.createQuery(q.criteria.where(value.isNull()));
                    assertEquals(Set.of(rowId), q.criteria.getParameters());
                    assertEquals(Integer.class, trueRows.getParameter("subqueryRow").getParameterType());
                    List<Integer> bindings = List.of(1, 2, 3, 99, 1);
                    List<Boolean> whenTrue = coalesce ? values(T, T, T, F, F, F, T, T, T)
                            : values(N, N, N, F, F, F, N, N, N);
                    List<Boolean> whenFalse = coalesce ? values(T, T, T, F, F, F, F, F, F)
                            : values(T, T, T, N, N, N, N, N, N);
                    List<List<Boolean>> expected = List.of(whenTrue, whenFalse, LEFT, LEFT, whenTrue);
                    for (int i = 0; i < bindings.size(); i++) {
                        assertEquals(matchingIds(expected.get(i), T),
                                selectedIds(trueRows.setParameter(rowId, bindings.get(i))));
                        assertEquals(matchingIds(expected.get(i), F),
                                selectedIds(falseRows.setParameter("subqueryRow", bindings.get(i))));
                        assertEquals(matchingIds(expected.get(i), N),
                                selectedIds(nullRows.setParameter(rowId, bindings.get(i))));
                    }
                    assertEquals(Set.of(q.root), q.criteria.getRoots());
                }
            }
        }
    }

    @Test
    public void predicateCoalesceAcceptsTrueFalseAndNullValues() {
        verifyValues(q -> q.left().equalTo(true).coalesce(true), q -> q.cb.coalesce(q.left(), true),
                values(T, T, T, F, F, F, T, T, T));
        verifyValues(q -> q.left().equalTo(true).coalesce(false), q -> q.cb.coalesce(q.left(), false),
                values(T, T, T, F, F, F, F, F, F));
        verifyValues(q -> q.left().equalTo(true).coalesce((Boolean) null), Query::left, LEFT);
    }

    @Test
    public void predicateCoalesceAcceptsExpressionsIncludingTypedNull() {
        verifyValues(q -> q.left().equalTo(true).coalesce(q.right()),
                q -> q.cb.coalesce(q.left(), q.right()), COALESCED);
        verifyValues(q -> q.left().equalTo(true).coalesce(q.cb.nullLiteral(Boolean.class)), Query::left, LEFT);
    }

    @Test
    public void predicateNullifAcceptsTrueFalseAndNullValues() {
        verifyValues(q -> q.left().equalTo(true).nullif(true), q -> q.cb.nullif(q.left(), true),
                values(N, N, N, F, F, F, N, N, N));
        verifyValues(q -> q.left().equalTo(true).nullif(false), q -> q.cb.nullif(q.left(), false),
                values(T, T, T, N, N, N, N, N, N));
        verifyValues(q -> q.left().equalTo(true).nullif((Boolean) null), Query::left, LEFT);
    }

    @Test
    public void predicateNullifAcceptsExpressionsIncludingTypedNull() {
        verifyValues(q -> q.left().equalTo(true).nullif(q.right()),
                q -> q.cb.nullif(q.left(), q.right()), NULLIFIED);
        verifyValues(q -> q.left().equalTo(true).nullif(q.cb.nullLiteral(Boolean.class)), Query::left, LEFT);
    }

    @Test
    public void nullHandlingAcceptsPredicateRightOperandsAndPreservesUnknown() {
        for (boolean predicateReceiver : List.of(false, true)) {
            verifyValues(q -> receiver(q, predicateReceiver).coalesce(q.right().equalTo(true)),
                    q -> q.cb.coalesce(q.left(), q.right()), COALESCED);
            verifyValues(q -> receiver(q, predicateReceiver).nullif(q.right().equalTo(true)),
                    q -> q.cb.nullif(q.left(), q.right()), NULLIFIED);
        }
    }

    @Test
    public void emptyJunctionsCombineAndNegateAsBooleanConstants() {
        verifyTruth(q -> q.cb.conjunction().and(q.left()), List.of(1, 2, 3), List.of(4, 5, 6));
        verifyTruth(q -> q.cb.disjunction().or(q.left()), List.of(1, 2, 3), List.of(4, 5, 6));
        verifyTruth(q -> q.cb.conjunction().not(), List.of(), ALL_IDS);
        verifyTruth(q -> q.cb.disjunction().not(), ALL_IDS, List.of());
    }

    @Test
    public void emptyJunctionsSupportScalarNullHandling() {
        verifyValues(q -> q.cb.conjunction().coalesce(q.left()), q -> q.cb.booleanLiteral(true), all(T));
        verifyValues(q -> q.cb.disjunction().coalesce(q.left()), q -> q.cb.booleanLiteral(false), all(F));
        verifyValues(q -> q.cb.conjunction().nullif(true), q -> q.cb.nullLiteral(Boolean.class), all(N));
        verifyValues(q -> q.cb.disjunction().nullif(false), q -> q.cb.nullLiteral(Boolean.class), all(N));
        // NULLIF(TRUE, right) is NULL when right is TRUE, and TRUE otherwise.
        // The CASE control uses a typed, always-null expression to avoid
        // unrelated literal-first NULLIF and untyped null-parameter inference.
        verifyValues(q -> q.cb.conjunction().nullif(q.right()),
                q -> q.cb.<Boolean>selectCase().when(q.cb.isTrue(q.right()), q.cb.nullif(q.right(), q.right()))
                        .otherwise(true), values(N, T, T, N, T, T, N, T, T));
    }

    @Test
    public void directBooleanSelectionRetainsPredicateParametersAndNullResults() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Boolean> criteria = cb.createQuery(Boolean.class);
            Root<BooleanCriteriaRecord> root = criteria.from(BooleanCriteriaRecord.class);
            ParameterExpression<Boolean> expected = cb.parameter(Boolean.class, "expected");
            Predicate predicate = root.get(BooleanCriteriaRecord_.leftValue).equalTo(expected)
                    .or(root.get(BooleanCriteriaRecord_.rightValue));
            criteria.select(predicate).orderBy(cb.asc(root.get("id")));
            assertEquals(Set.of(expected), criteria.getParameters());
            TypedQuery<Boolean> query = em.createQuery(criteria);
            assertEquals(OR, query.setParameter(expected, true).getResultList());
            assertEquals(values(T, F, N, T, T, T, T, N, N), query.setParameter("expected", false).getResultList());
            assertEquals(OR, query.setParameter(expected, true).getResultList());
        }
    }

    @Test
    public void aliasedTupleSelectionPreservesPredicateValuesAndElementAliases() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.criteria.select(q.cb.tuple(q.root.get("id").alias("rowId"), q.left().and(q.right()).alias("resultFlag")));
            List<Tuple> rows = em.createQuery(q.criteria).getResultList();
            assertEquals(ALL_IDS, rows.stream().map(row -> row.get("rowId", Integer.class)).toList());
            assertEquals(AND, rows.stream().map(row -> row.get("resultFlag", Boolean.class)).toList());
            assertEquals("resultFlag", rows.getFirst().getElements().get(1).getAlias());
            assertEquals(Boolean.class, rows.getFirst().getElements().get(1).getJavaType());
        }
    }

    @Test
    public void arraySelectionPreservesPredicateValuesAlongsideOrdinaryColumns() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Object[]> criteria = cb.createQuery(Object[].class);
            Root<BooleanCriteriaRecord> root = criteria.from(BooleanCriteriaRecord.class);
            criteria.select(cb.array(root.get("id"), root.get(BooleanCriteriaRecord_.leftValue)
                    .and(root.get(BooleanCriteriaRecord_.rightValue)))).orderBy(cb.asc(root.get("id")));
            List<Object[]> rows = em.createQuery(criteria).getResultList();
            assertEquals(ALL_IDS, rows.stream().map(row -> row[0]).toList());
            assertEquals(AND, rows.stream().map(row -> row[1]).toList());
        }
    }

    @Test
    public void constructorSelectionPreservesPredicateParametersAndNullArguments() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<BooleanProjection> criteria = cb.createQuery(BooleanProjection.class);
            Root<BooleanCriteriaRecord> root = criteria.from(BooleanCriteriaRecord.class);
            ParameterExpression<Boolean> expected = cb.parameter(Boolean.class, "expected");
            Predicate predicate = root.get(BooleanCriteriaRecord_.leftValue).equalTo(expected)
                    .or(root.get(BooleanCriteriaRecord_.rightValue));
            criteria.select(cb.construct(BooleanProjection.class, root.get("id"), predicate))
                    .orderBy(cb.asc(root.get("id")));
            assertEquals(Set.of(expected), criteria.getParameters());
            TypedQuery<BooleanProjection> query = em.createQuery(criteria);
            List<BooleanProjection> first = query.setParameter(expected, true).getResultList();
            assertEquals(ALL_IDS, first.stream().map(row -> row.id).toList());
            assertEquals(OR, first.stream().map(row -> row.value).toList());
            assertEquals(values(T, F, N, T, T, T, T, N, N),
                    query.setParameter(expected, false).getResultList().stream().map(row -> row.value).toList());
        }
    }

    @Test
    public void nestedConstructorSelectionPreservesPredicateAndNullArguments() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<NestedBooleanProjection> criteria = cb.createQuery(NestedBooleanProjection.class);
            Root<BooleanCriteriaRecord> root = criteria.from(BooleanCriteriaRecord.class);
            Predicate predicate = root.get(BooleanCriteriaRecord_.leftValue).and(root.get(BooleanCriteriaRecord_.rightValue));
            criteria.select(cb.construct(NestedBooleanProjection.class,
                    root.get(BooleanCriteriaRecord_.group).get(BooleanCriteriaGroup_.id),
                    cb.construct(BooleanProjection.class, root.get("id"), predicate)))
                    .orderBy(cb.asc(root.get("id")));
            List<NestedBooleanProjection> rows = em.createQuery(criteria).getResultList();
            assertEquals(List.of(1, 1, 1, 2, 2, 2, 3, 3, 3), rows.stream().map(row -> row.groupId).toList());
            assertEquals(ALL_IDS, rows.stream().map(row -> row.nested.id).toList());
            assertEquals(AND, rows.stream().map(row -> row.nested.value).toList());
        }
    }

    @Test
    public void selectedConstantJunctionsRetainEverySourceRow() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Boolean> direct = cb.createQuery(Boolean.class);
            Root<BooleanCriteriaRecord> root = direct.from(BooleanCriteriaRecord.class);
            direct.select(cb.conjunction()).orderBy(cb.asc(root.get("id")));
            assertEquals(all(T), em.createQuery(direct).getResultList());
            direct.select(cb.disjunction());
            assertEquals(all(F), em.createQuery(direct).getResultList());

            Query tuple = new Query(em);
            tuple.criteria.select(tuple.cb.tuple(tuple.root.get("id"), tuple.cb.conjunction().alias("always"),
                    tuple.cb.disjunction().alias("never")));
            List<Tuple> rows = em.createQuery(tuple.criteria).getResultList();
            assertEquals(ALL_IDS, rows.stream().map(row -> row.get(0, Integer.class)).toList());
            assertEquals(all(T), rows.stream().map(row -> row.get("always", Boolean.class)).toList());
            assertEquals(all(F), rows.stream().map(row -> row.get("never", Boolean.class)).toList());
        }
    }

    @Test
    public void allLiteralLogicalExpressionsSupportSelectionAndNullHandling() {
        verifyValues(q -> q.cb.booleanLiteral(true).and(q.cb.booleanLiteral(false)),
                q -> q.cb.literal(false), all(F));
        verifyValues(q -> q.cb.booleanLiteral(true).or(q.cb.booleanLiteral(false)),
                q -> q.cb.literal(true), all(T));
        verifyValues(q -> q.cb.booleanLiteral(true).not(), q -> q.cb.literal(false), all(F));
        verifyValues(q -> q.cb.booleanLiteral(true).and(q.cb.booleanLiteral(false)).coalesce(true),
                q -> q.cb.literal(false), all(F));
        verifyValues(q -> q.cb.booleanLiteral(true).or(q.cb.booleanLiteral(false)).nullif(true),
                q -> q.cb.nullLiteral(Boolean.class), all(N));
        verifyValues(q -> q.cb.booleanLiteral(true).not().coalesce(true).nullif(false),
                q -> q.cb.nullLiteral(Boolean.class), all(N));

        verifyValues(q -> q.cb.booleanLiteral(true).nullif(true).and(q.cb.booleanLiteral(true)),
                q -> q.cb.nullLiteral(Boolean.class), all(N));
        verifyValues(q -> q.cb.booleanLiteral(true).nullif(true).and(q.cb.booleanLiteral(false)),
                q -> q.cb.literal(false), all(F));
        verifyValues(q -> q.cb.booleanLiteral(true).nullif(true).or(q.cb.booleanLiteral(true)),
                q -> q.cb.literal(true), all(T));
        verifyValues(q -> q.cb.booleanLiteral(true).nullif(true).or(q.cb.booleanLiteral(false)),
                q -> q.cb.nullLiteral(Boolean.class), all(N));
        verifyValues(q -> q.cb.booleanLiteral(true).nullif(true).and(q.cb.booleanLiteral(true)).coalesce(false),
                q -> q.cb.literal(false), all(F));
        verifyValues(q -> q.cb.booleanLiteral(true).nullif(true).or(q.cb.booleanLiteral(false)).nullif(true),
                q -> q.cb.nullLiteral(Boolean.class), all(N));
    }

    @Test
    public void whereBooleanVarargsCombineRestrictionsAndEmptyArrayClearsThem() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.criteria.multiselect(q.root.get("id")).where(new BooleanExpression[] {q.left(), q.right()});
            assertEquals(List.of(1), q.selectedIds());
            q.criteria.where(new BooleanExpression[0]);
            assertEquals(ALL_IDS, q.selectedIds());
            verifyTruth(p -> p.cb.and(new BooleanExpression[] {p.left(), p.right()}),
                    List.of(1), List.of(2, 4, 5, 6, 8));
            verifyTruth(p -> p.cb.or(new BooleanExpression[] {p.left(), p.right()}),
                    List.of(1, 2, 3, 4, 7), List.of(5));
        }
    }

    @Test
    public void joinOnBooleanVarargsFiltersMatchesAndEmptyArrayClearsCondition() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Join<BooleanCriteriaRecord, BooleanCriteriaGroup> group = q.root.join(BooleanCriteriaRecord_.group, JoinType.LEFT);
            BooleanExpression active = group.get(BooleanCriteriaGroup_.active);
            assertEquals(Boolean.class, active.getJavaType());
            group.on(new BooleanExpression[] {active, q.cb.booleanLiteral(true)});
            q.criteria.multiselect(q.root.get("id"), group.get(BooleanCriteriaGroup_.id));
            assertEquals(Arrays.asList(1, 1, 1, null, null, null, null, null, null),
                    em.createQuery(q.criteria).getResultList().stream().map(row -> row.get(1, Integer.class)).toList());
            group.on(new BooleanExpression[0]);
            assertEquals(List.of(1, 1, 1, 2, 2, 2, 3, 3, 3),
                    em.createQuery(q.criteria).getResultList().stream().map(row -> row.get(1, Integer.class)).toList());
        }
    }

    @Test
    public void singleExpressionJoinOnTreatsBooleanNullHandlingAsARestriction() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Join<BooleanCriteriaRecord, BooleanCriteriaGroup> group = q.root.join(BooleanCriteriaRecord_.group, JoinType.LEFT);
            BooleanExpression active = group.get(BooleanCriteriaGroup_.active);
            q.criteria.multiselect(q.root.get("id"), group.get(BooleanCriteriaGroup_.id));

            Expression<Boolean> condition = active.coalesce(true);
            group.on(condition);
            assertEquals(Arrays.asList(1, 1, 1, null, null, null, 3, 3, 3),
                    em.createQuery(q.criteria).getResultList().stream().map(row -> row.get(1, Integer.class)).toList());

            condition = active.nullif(false);
            group.on(condition);
            assertEquals(Arrays.asList(1, 1, 1, null, null, null, null, null, null),
                    em.createQuery(q.criteria).getResultList().stream().map(row -> row.get(1, Integer.class)).toList());

            condition = active.nullif(true);
            group.on(condition);
            assertEquals(Collections.nCopies(9, null),
                    em.createQuery(q.criteria).getResultList().stream().map(row -> row.get(1, Integer.class)).toList());
        }
    }

    @Test
    public void havingBooleanVarargsFiltersGroupsAndEmptyArrayClearsRestrictions() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Join<BooleanCriteriaRecord, BooleanCriteriaGroup> group = q.root.join(BooleanCriteriaRecord_.group);
            q.criteria.multiselect(group.get(BooleanCriteriaGroup_.id))
                    .groupBy(group.get(BooleanCriteriaGroup_.id), group.get(BooleanCriteriaGroup_.active))
                    .orderBy(q.cb.asc(group.get(BooleanCriteriaGroup_.id)))
                    .having(new BooleanExpression[] {group.get(BooleanCriteriaGroup_.active), q.cb.gt(q.cb.count(q.root), 1)});
            assertEquals(List.of(1), q.selectedIds());
            q.criteria.having(new BooleanExpression[0]);
            assertEquals(List.of(1, 2, 3), q.selectedIds());
        }
    }

    @Test
    public void singleExpressionHavingTreatsBooleanNullHandlingAsARestriction() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Join<BooleanCriteriaRecord, BooleanCriteriaGroup> group = q.root.join(BooleanCriteriaRecord_.group);
            BooleanExpression active = group.get(BooleanCriteriaGroup_.active);
            q.criteria.multiselect(group.get(BooleanCriteriaGroup_.id))
                    .groupBy(group.get(BooleanCriteriaGroup_.id), active)
                    .orderBy(q.cb.asc(group.get(BooleanCriteriaGroup_.id)));

            Expression<Boolean> condition = active.coalesce(true);
            q.criteria.having(condition);
            assertEquals(List.of(1, 3), q.selectedIds());

            condition = active.nullif(false);
            q.criteria.having(condition);
            assertEquals(List.of(1), q.selectedIds());

            condition = active.nullif(true);
            q.criteria.having(condition);
            assertEquals(List.of(), q.selectedIds());
        }
    }

    @Test
    public void booleanAttributeSupportsGroupingIncludingNullGroup() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.criteria.multiselect(q.left(), q.cb.count(q.root)).groupBy(q.left()).orderBy(List.of());
            Map<Boolean, Long> counts = new HashMap<>();
            for (Tuple row : em.createQuery(q.criteria).getResultList()) {
                counts.put(row.get(0, Boolean.class), row.get(1, Long.class));
            }
            Map<Boolean, Long> expected = new HashMap<>();
            expected.put(T, 3L);
            expected.put(F, 3L);
            expected.put(N, 3L);
            assertEquals(expected, counts);
        }
    }

    @Test
    public void distinctBooleanNullHandlingSelectionCanOrderByTheSelectedExpression() {
        for (boolean converted : List.of(false, true)) {
            try (EntityManager em = emf.createEntityManager()) {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Boolean> criteria = cb.createQuery(Boolean.class);
                Root<BooleanCriteriaRecord> root = criteria.from(BooleanCriteriaRecord.class);
                BooleanExpression value = root.get(converted ? BooleanCriteriaRecord_.convertedValue
                        : BooleanCriteriaRecord_.leftValue).coalesce(false);
                criteria.select(value).distinct(true).orderBy(value.asc());
                assertEquals("Converted Boolean path=" + converted, List.of(false, true),
                        em.createQuery(criteria).getResultList());
            }
        }
    }

    @Test
    public void coalesceParametersAreDiscoveredAndReboundIncludingNull() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<Boolean> fallback = q.cb.parameter(Boolean.class, "fallback");
            q.criteria.multiselect(q.root.get("id"), q.left().coalesce(fallback));
            assertEquals(Set.of(fallback), q.criteria.getParameters());
            TypedQuery<Tuple> query = em.createQuery(q.criteria);
            assertEquals(Boolean.class, query.getParameter("fallback").getParameterType());
            assertEquals(values(T, T, T, F, F, F, T, T, T), selectedValues(query.setParameter(fallback, true)));
            assertEquals(values(T, T, T, F, F, F, F, F, F), selectedValues(query.setParameter("fallback", false)));
            assertEquals(LEFT, selectedValues(query.setParameter(fallback, null)));
            assertEquals(values(T, T, T, F, F, F, T, T, T), selectedValues(query.setParameter(fallback, true)));
        }
    }

    @Test
    public void predicateOperandsRetainBothParameterBindingsDuringQueryReuse() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<Boolean> expected = q.cb.parameter(Boolean.class, "expected");
            ParameterExpression<Boolean> fallback = q.cb.parameter(Boolean.class, "fallback");
            BooleanExpression expression = q.left().equalTo(expected).coalesce(fallback);
            q.criteria.multiselect(q.root.get("id")).where(expression.or(q.right()));
            assertEquals(Set.of(expected, fallback), q.criteria.getParameters());
            TypedQuery<Tuple> query = em.createQuery(q.criteria);
            assertEquals(Set.of("expected", "fallback"), query.getParameters().stream()
                    .map(parameter -> parameter.getName()).collect(Collectors.toSet()));
            assertEquals(List.of(1, 2, 3, 4, 7), selectedIds(query.setParameter(expected, true).setParameter(fallback, false)));
            assertEquals(List.of(1, 4, 5, 6, 7), selectedIds(query.setParameter("expected", false)));
            assertEquals(List.of(1, 2, 3, 4, 7, 8, 9),
                    selectedIds(query.setParameter(expected, true).setParameter("fallback", true)));
            assertEquals(List.of(1, 2, 3, 4, 7), selectedIds(query.setParameter(fallback, false)));
        }
    }

    @Test
    public void convertedBooleanPathsKeepDomainValuesAndLogicalSemantics() {
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.convertedValue), Query::left, LEFT);
        verifyTruth(q -> q.root.get(BooleanCriteriaRecord_.convertedValue).and(q.right()),
                List.of(1), List.of(2, 4, 5, 6, 8));
        verifyTruth(q -> q.root.get(BooleanCriteriaRecord_.convertedValue).not(),
                List.of(4, 5, 6), List.of(1, 2, 3));
    }

    @Test
    public void builderNotMatchesFluentNotForConvertedNativeAndLiteralValues() {
        verifyTruth(q -> q.cb.not(q.root.get(BooleanCriteriaRecord_.convertedValue)),
                List.of(4, 5, 6), List.of(1, 2, 3));
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.convertedValue).not(),
                q -> q.cb.not(q.root.get(BooleanCriteriaRecord_.convertedValue)),
                values(F, F, F, T, T, T, N, N, N));
        verifyTruth(q -> q.cb.not(q.left()), List.of(4, 5, 6), List.of(1, 2, 3));
        verifyTruth(q -> q.cb.not(q.cb.booleanLiteral(true)), List.of(), ALL_IDS);
        verifyTruth(q -> q.cb.not(q.cb.booleanLiteral(false)), ALL_IDS, List.of());
    }

    @Test
    public void convertedBooleanNullHandlingKeepsDomainValues() {
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.convertedValue).coalesce(false),
                q -> q.cb.coalesce(q.left(), false), values(T, T, T, F, F, F, F, F, F));
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.convertedValue).nullif(true),
                q -> q.cb.nullif(q.left(), true), values(N, N, N, F, F, F, N, N, N));
    }

    @Test
    public void numericBooleanConverterPreservesDomainValuesAndNullHandling() {
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.numericConvertedValue), Query::left, LEFT);
        verifyTruth(q -> q.root.get(BooleanCriteriaRecord_.numericConvertedValue).not(),
                List.of(4, 5, 6), List.of(1, 2, 3));
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.numericConvertedValue).coalesce(false),
                q -> q.cb.coalesce(q.left(), false), values(T, T, T, F, F, F, F, F, F));
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.numericConvertedValue).nullif(true),
                q -> q.cb.nullif(q.left(), true), values(N, N, N, F, F, F, N, N, N));
    }

    @Test
    public void convertedValuesAndPredicateOperandsShareBooleanDomainSemantics() {
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.convertedValue).coalesce(q.right().equalTo(true)),
                q -> q.cb.coalesce(q.left(), q.right()), COALESCED);
        verifyValues(q -> q.root.get(BooleanCriteriaRecord_.convertedValue).nullif(q.right().equalTo(true)),
                q -> q.cb.nullif(q.left(), q.right()), NULLIFIED);
        verifyValues(q -> q.right().equalTo(true).coalesce(q.root.get(BooleanCriteriaRecord_.convertedValue)),
                q -> q.cb.coalesce(q.right(), q.left()), values(T, F, T, T, F, F, T, F, N));
        verifyValues(q -> q.right().equalTo(true).nullif(q.root.get(BooleanCriteriaRecord_.convertedValue)),
                q -> q.cb.nullif(q.right(), q.left()), values(N, F, N, T, N, N, T, F, N));
    }

    @Test
    public void fluentOperationsDoNotMutateScalarOrPredicateInputs() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            BooleanExpression left = q.left();
            Predicate original = left.equalTo(true);
            List<Expression<Boolean>> operands = new ArrayList<>(original.getExpressions());
            Predicate or = original.or(q.right());
            Predicate negated = original.not();
            BooleanExpression fallback = original.coalesce(true);
            assertNotSame(original, or);
            assertNotSame(original, negated);
            assertFalse(original.isNegated());
            assertTrue(negated.isNegated());
            assertEquals(operands, original.getExpressions());
            assertEquals(List.of(1, 2, 3), q.ids(original));
            assertEquals(List.of(1, 2, 3, 4, 7), q.ids(or));
            assertEquals(List.of(4, 5, 6), q.ids(negated));
            assertEquals(List.of(1, 2, 3, 7, 8, 9), q.ids(fallback));
            assertEquals(LEFT, q.values(left));
            assertEquals(List.of(1, 2, 3), q.ids(original));
        }
    }

    private static BooleanExpression receiver(Query q, boolean predicate) {
        return predicate ? q.left().equalTo(true) : q.left();
    }

    private void verifyValues(Function<Query, ? extends Expression<Boolean>> fluent,
            Function<Query, ? extends Expression<Boolean>> builder, List<Boolean> expected) {
        for (Function<Query, ? extends Expression<Boolean>> expression : List.of(fluent, builder)) {
            try (EntityManager em = emf.createEntityManager()) {
                Query q = new Query(em);
                assertEquals(expected, q.values(expression.apply(q)));
            }
        }
    }

    private void verifyTruth(Function<Query, ? extends Expression<Boolean>> expression,
            List<Integer> trueIds, List<Integer> falseIds) {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Expression<Boolean> value = expression.apply(q);
            assertEquals(trueIds, q.ids(value));
            assertEquals(falseIds, q.ids(q.cb.not(value)));
        }
    }

    private static List<Boolean> values(Boolean... flags) {
        return Arrays.asList(flags);
    }

    private static List<Boolean> all(Boolean flag) {
        return Collections.nCopies(9, flag);
    }

    private static List<Integer> matchingIds(List<Boolean> flags, Boolean expected) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < flags.size(); i++) {
            if (flags.get(i) == expected) {
                ids.add(i + 1);
            }
        }
        return ids;
    }

    private static List<Boolean> selectedValues(TypedQuery<Tuple> query) {
        return query.getResultList().stream().map(row -> row.get(1, Boolean.class)).toList();
    }

    private static List<Integer> selectedIds(TypedQuery<Tuple> query) {
        return query.getResultList().stream().map(row -> row.get(0, Integer.class)).toList();
    }

    public static final class BooleanProjection {
        public final Integer id;
        public final Boolean value;

        public BooleanProjection(Integer id, Boolean value) {
            this.id = id;
            this.value = value;
        }
    }

    public static final class NestedBooleanProjection {
        public final Integer groupId;
        public final BooleanProjection nested;

        public NestedBooleanProjection(Integer groupId, BooleanProjection nested) {
            this.groupId = groupId;
            this.nested = nested;
        }
    }

    private static final class Query {
        final EntityManager em;
        final CriteriaBuilder cb;
        final CriteriaQuery<Tuple> criteria;
        final Root<BooleanCriteriaRecord> root;

        Query(EntityManager em) {
            this.em = em;
            cb = em.getCriteriaBuilder();
            criteria = cb.createTupleQuery();
            root = criteria.from(BooleanCriteriaRecord.class);
            criteria.orderBy(cb.asc(root.get("id")));
        }

        BooleanExpression left() {
            return root.get(BooleanCriteriaRecord_.leftValue);
        }

        BooleanExpression right() {
            return root.get(BooleanCriteriaRecord_.rightValue);
        }

        List<Boolean> values(Expression<Boolean> expression) {
            assertTrue(expression.getJavaType() == Boolean.class || expression.getJavaType() == boolean.class);
            criteria.multiselect(root.get("id"), expression).where(cb.conjunction());
            return selectedValues(em.createQuery(criteria));
        }

        List<Integer> ids(Expression<Boolean> expression) {
            criteria.multiselect(root.get("id")).where(expression);
            return selectedIds();
        }

        List<Integer> selectedIds() {
            return TestBooleanCriteriaExpressions.selectedIds(em.createQuery(criteria));
        }
    }
}
