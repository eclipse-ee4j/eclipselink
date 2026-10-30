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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.ComparableExpression;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.Trimspec;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.NumericExpression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.TextExpression;
import jakarta.persistence.metamodel.ComparableAttribute;
import jakarta.persistence.metamodel.NumericAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.TextAttribute;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.jpa.JpaCriteriaBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.test.criteria.model.ScalarCriteriaGroup;
import org.eclipse.persistence.jpa.test.criteria.model.ScalarCriteriaGroup_;
import org.eclipse.persistence.jpa.test.criteria.model.ScalarCriteriaRecord;
import org.eclipse.persistence.jpa.test.criteria.model.ScalarCriteriaRecord_;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.sessions.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Public-query coverage for specialized scalar Criteria expressions. */
@RunWith(EmfRunner.class)
public class TestScalarCriteriaExpressions {
    private static final String MARKED_TEXT = "Alpha_beta%[x]\\Tail";

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {ScalarCriteriaRecord.class, ScalarCriteriaGroup.class,
                    ScalarCriteriaRecord.PrefixConverter.class, ScalarCriteriaRecord.ScaledIntegerConverter.class},
            properties = @Property(name = "eclipselink.cache.shared.default", value = "false"))
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                ScalarCriteriaGroup first = new ScalarCriteriaGroup(1, "first", 10);
                ScalarCriteriaGroup second = new ScalarCriteriaGroup(2, "second", 20);
                em.persist(first);
                em.persist(second);
                em.persist(new ScalarCriteriaRecord(1, 6, 6.25, MARKED_TEXT, "b", first));
                em.persist(new ScalarCriteriaRecord(2, -3, -3.5, "beta Alpha", null, second));
                em.persist(new ScalarCriteriaRecord(3, 9, 9.75, "xxMiXeDxx", "a", first));
                ScalarCriteriaRecord nullRecord = new ScalarCriteriaRecord(4, null, null, "delta", null, second);
                nullRecord.convertedText = null;
                em.persist(nullRecord);
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
            em.getTransaction().begin();
            try {
                em.createQuery("DELETE FROM ScalarCriteriaRecord").executeUpdate();
                em.createQuery("DELETE FROM ScalarCriteriaGroup").executeUpdate();
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRuntimeAndCanonicalAttributePaths() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            NumericAttribute<ScalarCriteriaRecord, Integer> amount =
                    (NumericAttribute<ScalarCriteriaRecord, Integer>) emf.getMetamodel()
                            .entity(ScalarCriteriaRecord.class).getSingularAttribute("amount", Integer.class);
            TextAttribute<ScalarCriteriaRecord> text = (TextAttribute<ScalarCriteriaRecord>) emf.getMetamodel()
                    .entity(ScalarCriteriaRecord.class).getSingularAttribute("text", String.class);
            ComparableAttribute<ScalarCriteriaRecord, Integer> comparable = amount;
            NumericExpression<Integer> runtimeNumber = q.root.get(amount);
            ComparableExpression<Integer> runtimeComparable = q.root.get(comparable);
            TextExpression runtimeText = q.root.get(text);
            assertEquals(Integer.class, runtimeNumber.getJavaType());
            assertEquals(String.class, runtimeText.getJavaType());
            assertValues(q.row(runtimeNumber.plus(1), runtimeComparable.coalesce(0),
                    runtimeText.left(5), q.number().plus(2)), 7, 6, "Alpha", 8);
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testJoinedPathsAndBasicNavigation() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Join<ScalarCriteriaRecord, ScalarCriteriaGroup> group = q.root.join(ScalarCriteriaRecord_.group);
            assertValues(q.row(group.get(ScalarCriteriaGroup_.rank).plus(2),
                    group.get(ScalarCriteriaGroup_.name).upper()), 12, "FIRST");
            Path<String> ordinary = q.root.get((SingularAttribute<ScalarCriteriaRecord, String>) ScalarCriteriaRecord_.text);
            assertSame(q.root, ordinary.getParentPath());
            Path basic = ordinary;
            assertThrows(IllegalStateException.class, () -> basic.get(ScalarCriteriaRecord_.text));
            assertThrows(IllegalStateException.class, () -> basic.get(ScalarCriteriaRecord_.amount));
            ComparableAttribute<ScalarCriteriaRecord, Integer> comparable = ScalarCriteriaRecord_.amount;
            assertThrows(IllegalStateException.class, () -> basic.get(comparable));
        }
    }

    @Test
    public void testLiteralTypesAndChains() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            assertThrows(IllegalArgumentException.class, () -> q.cb.numericLiteral((Integer) null));
            assertThrows(IllegalArgumentException.class, () -> q.cb.stringLiteral(null));
            assertEquals(Byte.class, q.cb.numericLiteral((byte) 2).getJavaType());
            assertEquals(Short.class, q.cb.numericLiteral((short) 2).getJavaType());
            assertEquals(Integer.class, q.cb.numericLiteral(2).getJavaType());
            assertEquals(Long.class, q.cb.numericLiteral(2L).getJavaType());
            assertEquals(Float.class, q.cb.numericLiteral(2F).getJavaType());
            assertEquals(Double.class, q.cb.numericLiteral(2D).getJavaType());
            assertEquals(BigInteger.class, q.cb.numericLiteral(BigInteger.TWO).getJavaType());
            assertEquals(BigDecimal.class, q.cb.numericLiteral(BigDecimal.TEN).getJavaType());
            assertValues(q.row(q.cb.numericLiteral(3).plus(2).times(4),
                    q.cb.stringLiteral("ab").append("c").upper()), 20, "ABC");
        }
    }

    @Test
    public void testNumericComparisonOverloads() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            NumericExpression<Integer> n = q.number();
            ParameterExpression<Double> lower = q.parameter(Double.class, "lower", 5.5);
            ParameterExpression<Double> fractionalUpper = q.parameter(Double.class, "fractionalUpper", 6.5);
            ParameterExpression<Long> upper = q.parameter(Long.class, "upper", 7L);
            Predicate all = q.cb.and(n.gt(5.5), n.gt(lower), n.ge(6L), n.ge(lower),
                    n.lt(6.5F), n.lt(fractionalUpper), n.le(6.0), n.le(upper));
            assertEquals(List.of(1), q.ids(all));
        }
    }

    @Test
    public void testNumericComparisonOperandsPreserveDomainConversion() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            NumericExpression<Integer> number = q.root.get(ScalarCriteriaRecord_.convertedAmount);
            ParameterExpression<Integer> lower = q.parameter(Integer.class, "lower", 5);
            ParameterExpression<Integer> equal = q.parameter(Integer.class, "equal", 6);
            ParameterExpression<Integer> upper = q.parameter(Integer.class, "upper", 7);
            Expression<Integer> ordinary = q.root.get("convertedAmount");
            Predicate matches = q.cb.and(number.gt(5), number.ge(6), number.lt(7), number.le(6),
                    number.gt(lower), number.ge(equal), number.lt(upper), number.le(equal),
                    number.gt(q.cb.literal(5)), number.ge(q.cb.literal(6)),
                    number.lt(q.cb.literal(7)), number.le(q.cb.literal(6)),
                    q.cb.gt(ordinary, 5), q.cb.ge(ordinary, 6), q.cb.lt(ordinary, 7), q.cb.le(ordinary, 6),
                    q.cb.gt(ordinary, lower), q.cb.ge(ordinary, equal),
                    q.cb.lt(ordinary, upper), q.cb.le(ordinary, equal));
            // The stored value is 60; failing to convert an upper bound of 7 to 70
            // excludes this row even though the domain comparison must match.
            assertEquals(List.of(1), q.ids(matches));
        }
    }

    @Test
    public void testPrimitiveNumericComparisonOperandsPreserveDomainConversion() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            NumericExpression<Integer> number = q.root.get(ScalarCriteriaRecord_.primitiveConvertedAmount);
            Expression<Integer> ordinary = q.root.get("primitiveConvertedAmount");
            Predicate first = q.cb.equal(q.root.get(ScalarCriteriaRecord_.id), 1);
            assertEquals(int.class, number.getJavaType());
            assertEquals(int.class, ScalarCriteriaRecord_.primitiveConvertedAmount.getJavaType());
            assertEquals(List.of(1), q.ids(q.cb.and(first,
                    number.gt(5), number.ge(6), number.lt(7), number.le(6),
                    number.gt(q.cb.literal(5)), number.ge(q.cb.literal(6)),
                    number.lt(q.cb.literal(7)), number.le(q.cb.literal(6)),
                    q.cb.gt(ordinary, 5), q.cb.ge(ordinary, 6), q.cb.lt(ordinary, 7), q.cb.le(ordinary, 6))));
            for (Predicate comparison : List.of(number.gt(7), number.ge(7), number.lt(6), number.le(5),
                    number.gt(q.cb.literal(7)), number.ge(q.cb.literal(7)),
                    number.lt(q.cb.literal(6)), number.le(q.cb.literal(5)))) {
                assertTrue(q.ids(q.cb.and(first, comparison)).isEmpty());
            }

            ParameterExpression<Integer> bound = q.parameter(Integer.class, "bound", 7);
            int operation = 0;
            for (Function<Expression<Integer>, Predicate> comparison : List.<Function<Expression<Integer>, Predicate>>of(
                    number::lt, number::le, number::gt, number::ge)) {
                Predicate predicate = comparison.apply(bound);
                q.cq.multiselect(q.root.get(ScalarCriteriaRecord_.id)).where(q.cb.and(first, predicate));
                TypedQuery<Tuple> query = q.query();
                for (int value : new int[]{7, 6, 5, 7}) {
                    query.setParameter(bound, value);
                    assertEquals(Integer.valueOf(value), query.getParameterValue(bound));
                    boolean matches = switch (operation) {
                        case 0 -> 6 < value;
                        case 1 -> 6 <= value;
                        case 2 -> 6 > value;
                        default -> 6 >= value;
                    };
                    assertEquals(matches ? List.of(1) : List.of(),
                            query.getResultList().stream().map(tuple -> tuple.get(0, Integer.class)).toList());
                }
                operation++;
            }
            assertSame(bound, q.cq.getParameters().iterator().next());
            assertEquals(Integer.class, bound.getParameterType());
            assertEquals(int.class, number.getJavaType());
        }
    }

    @Test
    public void testComparableComparisonOverloads() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ComparableAttribute<ScalarCriteriaRecord, Integer> attribute = ScalarCriteriaRecord_.amount;
            ComparableExpression<Integer> n = q.root.get(attribute);
            ParameterExpression<Integer> low = q.parameter(Integer.class, "low", 5);
            ParameterExpression<Integer> high = q.parameter(Integer.class, "high", 7);
            assertEquals(List.of(1), q.ids(q.cb.and(n.greaterThan(5), n.greaterThan(low),
                    n.greaterThanOrEqualTo(6), n.greaterThanOrEqualTo(low), n.lessThan(7), n.lessThan(high),
                    n.lessThanOrEqualTo(6), n.lessThanOrEqualTo(high), n.between(6, 6), n.between(low, high))));
        }
    }

    @Test
    public void testValueFirstBetweenRetainsBothBounds() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<Integer> lower = q.parameter(Integer.class, "lower", 6);
            ParameterExpression<Integer> upper = q.parameter(Integer.class, "upper", 6);
            assertEquals(List.of(1, 2, 3, 4), q.ids(q.cb.between(6, lower, upper)));
            Query outside = new Query(em);
            assertEquals(List.of(), outside.ids(outside.cb.between(4,
                    outside.cb.numericLiteral(5), outside.cb.numericLiteral(8))));
            Query asymmetric = new Query(em);
            assertEquals(List.of(1, 2, 3, 4), asymmetric.ids(asymmetric.cb.between(7,
                    asymmetric.cb.numericLiteral(5), asymmetric.cb.numericLiteral(8))));
        }
    }

    @Test
    public void testArithmeticValueAndExpressionOverloads() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            NumericExpression<Double> n = q.fraction();
            ParameterExpression<Double> two = q.parameter(Double.class, "two", 2.0);
            assertNumbers(q.row(n.plus(2.0), n.plus(two), n.times(2.0), n.times(two),
                    n.minus(2.0), n.minus(two), n.dividedBy(2.0), n.dividedBy(two),
                    n.subtractedFrom(20.0), n.dividedInto(25.0)),
                    8.25, 8.25, 12.5, 12.5, 4.25, 4.25, 3.125, 3.125, 13.75, 4.0);
        }
    }

    @Test
    public void testUnaryMathAndResultTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.whereId(2);
            NumericExpression<Double> n = q.fraction();
            assertEquals(Integer.class, n.sign().getJavaType());
            assertEquals(Double.class, n.abs().getJavaType());
            Tuple result = q.row(n.sign(), n.negated(), n.abs(), n.ceiling(), n.floor());
            assertSame(Integer.class, result.get(0).getClass());
            assertNumbers(result, -1, 3.5, 3.5, -3, -4);
            Query rounding = new Query(em);
            assertNumbers(rounding.row(rounding.cb.numericLiteral(-3.6).round(0),
                    rounding.cb.numericLiteral(-3.4).round(0), rounding.cb.numericLiteral(3.6).round(0),
                    rounding.cb.numericLiteral(3.4).round(0)), -4, -3, 4, 3);
        }
    }

    @Test
    public void testFloatingPointFunctions() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            NumericExpression<Double> n = q.fraction();
            ParameterExpression<Integer> exponent = q.parameter(Integer.class, "exponent", 2);
            NumericExpression<Double>[] expressions = numericArray(n.sqrt(), n.exp(), n.ln(),
                    n.power(2), n.power(exponent));
            for (NumericExpression<Double> expression : expressions) {
                assertEquals(Double.class, expression.getJavaType());
            }
            Tuple result = q.row(expressions);
            assertNumbers(result, 2.5, Math.exp(6.25), Math.log(6.25), 39.0625, 39.0625);
            for (Object value : result.toArray()) {
                assertSame(Double.class, value.getClass());
            }
        }
    }

    @Test
    public void testNumericConversionsProjectDeclaredTypesWithoutMutatingSource() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            NumericExpression<Integer> source = q.number();
            source.alias("original");
            Expression<?>[] conversions = {source.toLong(), source.toInteger(), source.toFloat(),
                    source.toDouble(), source.toBigDecimal(), source.toBigInteger()};
            Class<?>[] classes = {Long.class, Integer.class, Float.class, Double.class,
                    BigDecimal.class, BigInteger.class};
            for (int i = 0; i < conversions.length; i++) {
                assertNotSame(source, conversions[i]);
                assertEquals(classes[i], conversions[i].getJavaType());
                conversions[i].alias("converted" + i);
            }
            assertEquals(Integer.class, source.getJavaType());
            assertEquals("original", source.getAlias());
            Tuple result = q.row(conversions);
            for (int i = 0; i < classes.length; i++) {
                Object value = result.get("converted" + i);
                assertSame(classes[i], value.getClass());
                assertEquals(6.0, ((Number) value).doubleValue(), 0.0);
            }
            Query chain = new Query(em);
            assertValues(chain.row(chain.number().toLong().plus(3L).toDouble().dividedBy(2.0)), 4.5);
        }
    }

    @Test
    public void testNumericAggregatesAndCounts() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.allRows();
            NumericExpression<Integer> n = q.number();
            Tuple result = q.row(n.avg(), n.sum(), n.toLong().sumAsLong(), n.sumAsDouble(),
                    n.max(), n.min(), n.count(), n.countDistinct());
            assertNumbers(result, 4, 12, 12, 12, 9, -3, 3, 3);
            assertSame(Double.class, result.get(0).getClass());
            assertSame(Long.class, result.get(2).getClass());
            assertSame(Double.class, result.get(3).getClass());
            assertSame(Long.class, result.get(6).getClass());
            assertSame(Long.class, result.get(7).getClass());
            assertEquals(Long.class, n.count().getJavaType());
            assertEquals(Long.class, n.countDistinct().getJavaType());
        }
    }

    @Test
    public void testEmptyNumericAggregates() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.whereId(99);
            NumericExpression<Integer> n = q.number();
            assertValues(q.row(n.avg(), n.sum(), n.max(), n.min(), n.count(), n.countDistinct()),
                    null, null, null, null, 0L, 0L);
        }
    }

    @Test
    public void testNumericNullHandlingOverloadsAndNullableValues() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.whereId(4);
            ParameterExpression<Integer> fallback = q.parameter(Integer.class, "fallback", 8);
            assertValues(q.row(q.number().coalesce(7), q.number().coalesce(fallback)), 7, 8);
            Query values = new Query(em);
            ParameterExpression<Integer> match = values.parameter(Integer.class, "match", 6);
            ParameterExpression<Integer> absent = values.parameter(Integer.class, "absent", null);
            Expression<Integer> nullLiteral = values.cb.nullLiteral(Integer.class);
            assertValues(values.row(values.number().nullif(6), values.number().nullif(match),
                    values.number().coalesce((Integer) null), values.number().nullif((Integer) null),
                    values.number().coalesce(nullLiteral), values.number().nullif(nullLiteral),
                    values.number().coalesce(absent), values.number().nullif(absent)),
                    null, null, 6, 6, 6, 6, 6, 6);
            Query nullable = new Query(em);
            nullable.whereId(4);
            ParameterExpression<Integer> absentFallback = nullable.parameter(Integer.class, "absentFallback", null);
            Expression<Integer> absentLiteral = nullable.cb.nullLiteral(Integer.class);
            assertValues(nullable.row(nullable.number().coalesce(absentLiteral), nullable.number().nullif(absentLiteral),
                    nullable.number().coalesce(absentFallback), nullable.number().nullif(absentFallback)),
                    null, null, null, null);
            Query allNull = new Query(em);
            ParameterExpression<Integer> first = allNull.parameter(Integer.class, "firstNull", null);
            ParameterExpression<Integer> second = allNull.parameter(Integer.class, "secondNull", null);
            Expression<Integer> combined = allNull.cb.coalesce(allNull.cb.nullif(allNull.number(), first), second);
            allNull.whereId(4);
            assertValues(allNull.row(combined), (Object) null);
            allNull.whereId(1);
            assertValues(allNull.row(combined), 6);
            allNull.bindings.put(first, 6);
            allNull.bindings.put(second, 8);
            assertValues(allNull.row(combined), 8);
        }
    }

    @Test
    public void testComparableNullHandlingAndAggregateTraversal() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ComparableAttribute<ScalarCriteriaRecord, Integer> attribute = ScalarCriteriaRecord_.amount;
            ComparableExpression<Integer> n = q.root.get(attribute);
            ParameterExpression<Integer> match = q.parameter(Integer.class, "match", 6);
            assertValues(q.row(n.coalesce(1), n.coalesce(match), n.nullif(6), n.nullif(match)),
                    6, 6, null, null);
            Query extrema = new Query(em);
            extrema.allRows();
            ParameterExpression<String> fallback = extrema.parameter(String.class, "fallback", "c");
            TextExpression text = extrema.root.get(ScalarCriteriaRecord_.nullableText).coalesce(fallback);
            assertValues(extrema.row(text.max(), text.min()), "c", "a");
        }
    }

    @Test
    public void testTextAppendPrependAndCase() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TextExpression text = q.cb.stringLiteral("Ab");
            ParameterExpression<String> suffix = q.parameter(String.class, "suffix", "yz");
            assertValues(q.row(text.append("cd"), text.append(suffix), text.prepend("cd"),
                    text.prepend(suffix), text.lower(), text.upper(), text.length()),
                    "Abcd", "Abyz", "cdAb", "yzAb", "ab", "AB", 2);
        }
    }

    @Test
    public void testTextSubstringOverloads() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TextExpression text = q.cb.stringLiteral("abcdef");
            ParameterExpression<Integer> start = q.parameter(Integer.class, "start", 2);
            ParameterExpression<Integer> length = q.parameter(Integer.class, "length", 3);
            assertValues(q.row(text.substring(2), text.substring(start),
                    text.substring(2, 3), text.substring(start, length)),
                    "bcdef", "bcdef", "bcd", "bcd");
        }
    }

    @Test
    public void testTextLeftRightOperandTraversal() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TextExpression text = q.cb.stringLiteral("abcdef");
            ParameterExpression<Integer> leftLength = q.parameter(Integer.class, "leftLength", 2);
            ParameterExpression<Integer> rightLength = q.parameter(Integer.class, "rightLength", 3);
            assertValues(q.row(text.left(2), text.right(3), text.left(leftLength), text.right(rightLength)),
                    "ab", "def", "ab", "def");
        }
    }

    @Test
    public void testTextTrimOverloads() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TextExpression spaces = q.cb.stringLiteral("  Ab  ");
            TextExpression marked = q.cb.stringLiteral("xxAbxx");
            ParameterExpression<Character> marker = q.parameter(Character.class, "marker", 'x');
            assertValues(q.row(spaces.trim(), spaces.trim(Trimspec.LEADING), spaces.trim(Trimspec.TRAILING),
                    spaces.trim(Trimspec.BOTH), marked.trim('x'), marked.trim(marker),
                    marked.trim(Trimspec.LEADING, 'x'), marked.trim(Trimspec.TRAILING, 'x'),
                    marked.trim(Trimspec.BOTH, 'x'), marked.trim(Trimspec.LEADING, marker),
                    marked.trim(Trimspec.TRAILING, marker), marked.trim(Trimspec.BOTH, marker)),
                    "Ab", "Ab  ", "  Ab", "Ab", "Ab", "Ab", "Abxx", "xxAb", "Ab", "Abxx", "xxAb", "Ab");
        }
    }

    @Test
    public void testTextReplaceOperandCombinations() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TextExpression text = q.cb.stringLiteral("ababa");
            ParameterExpression<String> needle = q.parameter(String.class, "needle", "ba");
            ParameterExpression<String> replacement = q.parameter(String.class, "replacement", "XY");
            assertReplacement(q, new Selection<?>[]{text.replace("ba", "XY"), text.replace(needle, "XY"),
                    text.replace("ba", replacement), text.replace(needle, replacement)},
                    "aXYXY", "aXYXY", "aXYXY", "aXYXY");
        }
    }

    @Test
    public void testTextLocateOverloadsAndNumericChaining() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TextExpression text = q.cb.stringLiteral("ababa");
            ParameterExpression<String> needle = q.parameter(String.class, "needle", "ba");
            ParameterExpression<Integer> offset = q.parameter(Integer.class, "offset", 3);
            assertValues(q.row(text.locate("ba"), text.locate(needle), text.locate("ba", 3),
                    text.locate(needle, offset), text.locate("absent"), text.locate("ba").plus(10)),
                    2, 2, 4, 4, 0, 12);
            assertEquals(Integer.class, text.locate("ba").getJavaType());
        }
    }

    @Test
    public void testLikeAndNotLikePatternOverloads() {
        try (EntityManager em = emf.createEntityManager()) {
            for (boolean negated : new boolean[]{false, true}) {
                Query q = new Query(em);
                TextExpression text = q.text();
                ParameterExpression<String> pattern = q.parameter(String.class, "pattern", "Alpha%Tail");
                Predicate strings = negated ? text.notLike("Alpha%Tail") : text.like("Alpha%Tail");
                Predicate expressions = negated ? text.notLike(pattern) : text.like(pattern);
                assertEquals(negated ? List.of(2, 3, 4) : List.of(1), q.ids(q.cb.and(strings, expressions)));
            }
        }
    }

    @Test
    public void testLikeAndNotLikeEscapeOverloads() {
        try (EntityManager em = emf.createEntityManager()) {
            String patternValue = "%!_%!%%";
            for (boolean negated : new boolean[]{false, true}) {
                Query q = new Query(em);
                TextExpression text = q.text();
                ParameterExpression<String> pattern = q.parameter(String.class, "pattern", patternValue);
                ParameterExpression<Character> escape = q.parameter(Character.class, "escape", '!');
                Predicate[] predicates = negated
                        ? new Predicate[]{text.notLike(patternValue, '!'), text.notLike(pattern, '!'),
                                text.notLike(patternValue, escape), text.notLike(pattern, escape)}
                        : new Predicate[]{text.like(patternValue, '!'), text.like(pattern, '!'),
                                text.like(patternValue, escape), text.like(pattern, escape)};
                assertEquals(negated ? List.of(2, 3, 4) : List.of(1), q.ids(q.cb.and(predicates)));
            }
        }
    }

    @Test
    public void testLiteralSubstringPredicatesDoNotInterpretPatterns() {
        try (EntityManager em = emf.createEntityManager()) {
            for (String literal : List.of("_", "%", "[x]", "\\")) {
                assertMatching(em, text -> text.contains(literal), 1);
                assertMatching(em, text -> text.notContains(literal), 2, 3, 4);
            }
            assertMatching(em, text -> text.startsWith("Alpha_"), 1);
            assertMatching(em, text -> text.notStartsWith("Alpha_"), 2, 3, 4);
            assertMatching(em, text -> text.endsWith("[x]\\Tail"), 1);
            assertMatching(em, text -> text.notEndsWith("[x]\\Tail"), 2, 3, 4);
            assertMatching(em, text -> text.contains("AlphaXbeta"));
            assertMatching(em, text -> text.startsWith("%"));
            assertMatching(em, text -> text.endsWith("%"));
        }
    }

    @Test
    public void testLiteralPrefixAndSuffixMetacharacters() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TextExpression text = q.cb.stringLiteral("%_[x]\\middle\\[x]_%");
            assertEquals(List.of(1, 2, 3, 4), q.ids(q.cb.and(text.startsWith("%_[x]\\"),
                    text.endsWith("\\[x]_%"), text.notStartsWith("_[x]"), text.notEndsWith("[x]_"))));
            String supplementary = "\uD83D\uDE00";
            for (boolean present : new boolean[]{true, false}) {
                String value = present ? supplementary + "middle" + supplementary : "middle";
                Query positive = new Query(em);
                TextExpression unicode = positive.cb.stringLiteral(value);
                Predicate[] matches = {unicode.startsWith(supplementary), unicode.endsWith(supplementary),
                        unicode.contains(supplementary)};
                assertEquals(present ? List.of(1, 2, 3, 4) : List.of(), positive.ids(
                        present ? positive.cb.and(matches) : positive.cb.or(matches)));
                Query negative = new Query(em);
                TextExpression inverse = negative.cb.stringLiteral(value);
                Predicate[] nonmatches = {inverse.notStartsWith(supplementary), inverse.notEndsWith(supplementary),
                        inverse.notContains(supplementary)};
                assertEquals(present ? List.of() : List.of(1, 2, 3, 4), negative.ids(
                        present ? negative.cb.or(nonmatches) : negative.cb.and(nonmatches)));
            }
            Query trailing = new Query(em);
            assertEquals(List.of(1, 2, 3, 4), trailing.ids(trailing.cb.and(
                    trailing.cb.stringLiteral("middle tail  ").endsWith("tail  "),
                    trailing.cb.stringLiteral("middle tailX ").notEndsWith("tail  "),
                    trailing.cb.stringLiteral("tail").notEndsWith("tail "),
                    trailing.cb.stringLiteral("tail ").endsWith("tail "))));
            Query trailingInverse = new Query(em);
            assertEquals(List.of(), trailingInverse.ids(trailingInverse.cb.or(
                    trailingInverse.cb.stringLiteral("middle tail  ").notEndsWith("tail  "),
                    trailingInverse.cb.stringLiteral("middle tailX ").endsWith("tail  "),
                    trailingInverse.cb.stringLiteral("tail").endsWith("tail "),
                    trailingInverse.cb.stringLiteral("tail ").notEndsWith("tail "))));
            Query shorter = new Query(em);
            assertEquals(List.of(), shorter.ids(shorter.cb.stringLiteral("delta").endsWith("long-suffix")));
            Query shorterInverse = new Query(em);
            assertEquals(List.of(1, 2, 3, 4), shorterInverse.ids(
                    shorterInverse.cb.stringLiteral("delta").notEndsWith("long-suffix")));
        }
    }

    @Test
    public void testLiteralSubstringNullSemantics() {
        try (EntityManager em = emf.createEntityManager()) {
            for (Function<TextExpression, Predicate> operation : List.<Function<TextExpression, Predicate>>of(
                    text -> text.contains("a"), text -> text.notContains("a"),
                    text -> text.startsWith("a"), text -> text.notStartsWith("a"),
                    text -> text.endsWith("a"), text -> text.notEndsWith("a"),
                    text -> text.endsWith("a "), text -> text.notEndsWith("a "))) {
                Query q = new Query(em);
                List<Integer> ids = q.ids(operation.apply(q.root.get(ScalarCriteriaRecord_.nullableText)));
                assertFalse(ids.contains(2));
                assertFalse(ids.contains(4));
            }
            for (Function<TextExpression, Predicate> operation : List.<Function<TextExpression, Predicate>>of(
                    text -> text.contains(""), text -> text.startsWith(""), text -> text.endsWith(""))) {
                Query q = new Query(em);
                assertEquals(List.of(1, 3), q.ids(operation.apply(q.root.get(ScalarCriteriaRecord_.nullableText))));
            }
            for (Function<TextExpression, Predicate> operation : List.<Function<TextExpression, Predicate>>of(
                    text -> text.notContains(""), text -> text.notStartsWith(""), text -> text.notEndsWith(""))) {
                Query q = new Query(em);
                assertEquals(List.of(), q.ids(operation.apply(q.root.get(ScalarCriteriaRecord_.nullableText))));
            }
        }
    }

    @Test
    public void testTextNullHandlingOverloadsAndNullValues() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.whereId(4);
            TextExpression text = q.root.get(ScalarCriteriaRecord_.nullableText);
            ParameterExpression<String> fallback = q.parameter(String.class, "fallback", "fallback");
            assertValues(q.row(text.coalesce("literal"), text.coalesce(fallback)), "literal", "fallback");
            Query values = new Query(em);
            TextExpression value = values.root.get(ScalarCriteriaRecord_.nullableText);
            ParameterExpression<String> match = values.parameter(String.class, "match", "b");
            assertValues(values.row(value.nullif("b"), value.nullif(match),
                    value.coalesce((String) null), value.nullif((String) null)), null, null, "b", "b");
        }
    }

    @Test
    public void testExplicitNullOrderingAndOrderMetadata() {
        try (EntityManager em = emf.createEntityManager()) {
            Query ascending = new Query(em);
            TextExpression text = ascending.root.get(ScalarCriteriaRecord_.nullableText);
            Order asc = text.asc(Nulls.FIRST);
            assertEquals(Nulls.FIRST, asc.getNullPrecedence());
            assertTrue(asc.isAscending());
            assertEquals(List.of(2, 4, 3, 1), ascending.orderedIds(asc));
            Query descending = new Query(em);
            Order desc = descending.root.get(ScalarCriteriaRecord_.nullableText).desc(Nulls.LAST);
            assertEquals(Nulls.LAST, desc.getNullPrecedence());
            assertFalse(desc.isAscending());
            assertEquals(List.of(1, 3, 2, 4), descending.orderedIds(desc));
            Query numeric = new Query(em);
            assertEquals(List.of(2, 1, 3, 4), numeric.orderedIds(numeric.number().asc(Nulls.LAST)));
        }
    }

    @Test
    public void testDefaultOrdersAndOrderOnlyParameters() {
        try (EntityManager em = emf.createEntityManager()) {
            Query ascending = new Query(em);
            assertEquals(List.of(4, 2, 1, 3), ascending.orderedIds(ascending.number().coalesce(-10).asc()));
            Query descending = new Query(em);
            ParameterExpression<Integer> fallback = descending.parameter(Integer.class, "fallback", -10);
            assertEquals(List.of(3, 1, 2, 4), descending.orderedIds(descending.number().coalesce(fallback).desc()));
        }
    }

    @Test
    public void testTupleAliasesAndConstructorProjection() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            NumericExpression<Integer> source = q.number();
            source.alias("amount");
            Expression<Long> changed = source.toLong().plus(2L);
            changed.alias("adjusted");
            Tuple tuple = q.row(source, changed, q.text().left(5).alias("prefix"));
            assertEquals(6, tuple.get("amount"));
            assertEquals(8L, tuple.get("adjusted"));
            assertEquals("Alpha", tuple.get("prefix"));
            assertEquals("amount", source.getAlias());
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ScalarProjection> query = cb.createQuery(ScalarProjection.class);
            Root<ScalarCriteriaRecord> root = query.from(ScalarCriteriaRecord.class);
            query.select(cb.construct(ScalarProjection.class, root.get(ScalarCriteriaRecord_.amount).toLong(),
                    root.get(ScalarCriteriaRecord_.text).left(5)));
            query.where(cb.equal(root.get(ScalarCriteriaRecord_.id), 1));
            ScalarProjection projection = em.createQuery(query).getSingleResult();
            assertEquals(Long.valueOf(6), projection.number);
            assertEquals("Alpha", projection.text);
        }
    }

    @Test
    public void testConvertedPathComparisonAndProjection() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TextExpression converted = q.root.get(ScalarCriteriaRecord_.convertedText);
            assertEquals(List.of(1), q.ids(q.cb.equal(converted, "code1")));
            Query projection = new Query(em);
            assertValues(projection.row(projection.root.get(ScalarCriteriaRecord_.convertedText)), "code1");
        }
    }

    @Test
    public void testExistingSubqueryAsScalarOperand() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Subquery<Integer> maximum = q.cq.subquery(Integer.class);
            Root<ScalarCriteriaRecord> subroot = maximum.from(ScalarCriteriaRecord.class);
            maximum.select(q.cb.max(subroot.get("amount")));
            assertEquals(List.of(1, 2), q.ids(q.number().lessThan(maximum)));
            Query arithmetic = new Query(em);
            Subquery<Integer> one = arithmetic.cq.subquery(Integer.class);
            Root<ScalarCriteriaGroup> group = one.from(ScalarCriteriaGroup.class);
            one.select(group.get("rank")).where(arithmetic.cb.equal(group.get("id"), 1));
            assertValues(arithmetic.row(arithmetic.number().plus(one)), 16);
        }
    }

    @Test
    public void testFoldedCoalesceInRebuiltSubqueryOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            Query selected = new Query(em);
            Subquery<Integer> one = selected.cq.subquery(Integer.class);
            Root<ScalarCriteriaGroup> group = one.from(ScalarCriteriaGroup.class);
            one.select(selected.cb.coalesce(group.<Integer>get("rank"), selected.cb.nullLiteral(Integer.class)))
                    .where(selected.cb.equal(group.get("id"), 1));
            Expression<Integer> sum = selected.cb.numericLiteral(6).plus(one);
            assertValues(selected.row(sum), 16);
            assertValues(selected.row(sum), 16);
            assertValues(selected.row(selected.cb.sum(selected.cb.literal(6), one)), 16);

            Query predicate = new Query(em);
            ParameterExpression<Integer> expected = predicate.parameter(Integer.class, "expectedRank", 10);
            Subquery<Integer> matching = predicate.cq.subquery(Integer.class);
            Root<ScalarCriteriaGroup> matchingGroup = matching.from(ScalarCriteriaGroup.class);
            Expression<Integer> rank = matchingGroup.get("rank");
            matching.select(rank).where(predicate.cb.and(predicate.cb.equal(matchingGroup.get("id"), 1),
                    predicate.cb.equal(predicate.cb.coalesce(rank, predicate.cb.nullLiteral(Integer.class)), expected)));
            Expression<Integer> result = predicate.cb.numericLiteral(6).plus(matching);
            assertValues(predicate.row(result), 16);
            predicate.bindings.put(expected, 11);
            assertValues(predicate.row(result), (Object) null);
            predicate.bindings.put(expected, 10);
            assertValues(predicate.row(result), 16);
            assertEquals(Integer.class, expected.getParameterType());
        }
    }

    @Test
    public void testLegacyBuilderExtremaKeepParameterTraversal() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.allRows();
            ParameterExpression<String> fallback = q.parameter(String.class, "fallback", "c");
            Expression<String> ordinary = q.cb.coalesce(q.root.<String>get("nullableText"), fallback);
            assertValues(q.row(q.cb.greatest(ordinary), q.cb.least(ordinary)), "c", "a");
        }
    }

    @Test
    public void testLegacyBuilderLengthOperandsRemainBindable() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<Integer> left = q.parameter(Integer.class, "left", 2);
            ParameterExpression<Integer> right = q.parameter(Integer.class, "right", 3);
            Expression<String> ordinary = q.cb.literal("abcdef");
            assertValues(q.row(q.cb.left(ordinary, left), q.cb.right(ordinary, right)), "ab", "def");
        }
    }

    @Test
    public void testLegacyBuilderReplaceOperandsRemainBindable() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<String> needle = q.parameter(String.class, "needle", "ba");
            ParameterExpression<String> replacement = q.parameter(String.class, "replacement", "XY");
            Expression<String> ordinary = q.cb.literal("ababa");
            assertReplacement(q, new Selection<?>[]{q.cb.replace(ordinary, needle, replacement),
                    q.cb.replace(ordinary, needle, "XY"), q.cb.replace(ordinary, "ba", replacement)},
                    "aXYXY", "aXYXY", "aXYXY");
        }
    }

    @Test
    public void testLegacyBuilderUnicodeLengthCountsCharacters() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<String> value = q.parameter(String.class, "lengthValue", "AbC");
            Tuple lengths = q.row(q.cb.length(q.cb.literal("AbC")), q.cb.length(value));
            assertValues(lengths, 3, 3);
            assertSame(Integer.class, lengths.get(0).getClass());
            assertSame(Integer.class, lengths.get(1).getClass());
            if (emf.unwrap(Session.class).getPlatform().isMySQL() || usesDb2CharacterUnits()) {
                for (String input : List.of("A\u00e9B", "A\uD83D\uDE00B")) {
                    Query unicode = new Query(em);
                    ParameterExpression<String> text = unicode.parameter(String.class, "unicode", input);
                    assertValues(unicode.row(unicode.cb.length(unicode.cb.literal(input)), unicode.cb.length(text)), 3, 3);
                }
            }
        }
    }

    @Test
    public void testCharacterUnitsInMappedTextAndJpqlOperations() {
        try (EntityManager em = emf.createEntityManager()) {
            for (String middle : characterUnitMiddleCharacters()) {
                em.getTransaction().begin();
                try {
                    em.createQuery("UPDATE ScalarCriteriaRecord r SET r.text = :text WHERE r.id = 1")
                            .setParameter("text", "A" + middle + "B").executeUpdate();
                    em.getTransaction().commit();
                } finally {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                }
                Query q = new Query(em);
                assertCharacterUnits(q.row(characterUnitSelections(q, q.root.<String>get("text"), q.text())), middle);
                Object[] jpql = em.createQuery("SELECT LENGTH(r.text), LOCATE('B', r.text), "
                                + "SUBSTRING(r.text, 2, 1), SUBSTRING(r.text, 3) "
                                + "FROM ScalarCriteriaRecord r WHERE r.id = 1", Object[].class).getSingleResult();
                assertEquals(3, jpql[0]);
                assertEquals(3, jpql[1]);
                assertEquals(middle, jpql[2]);
                assertEquals("B", jpql[3]);
            }
            Query nullable = new Query(em);
            nullable.whereId(2);
            assertValues(nullable.row(characterUnitSelections(nullable, nullable.root.<String>get("nullableText"),
                    nullable.root.get(ScalarCriteriaRecord_.nullableText))), new Object[14]);
        }
    }

    @Test
    public void testCharacterUnitsRetainOriginalParametersWhenQueriesAreReused() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.whereId(2);
            ParameterExpression<String> input = q.parameter(String.class, "input", "ACB");
            Expression<String> ordinary = q.cb.coalesce(q.root.<String>get("nullableText"), input);
            TextExpression text = q.root.get(ScalarCriteriaRecord_.nullableText).coalesce(input);
            q.cq.multiselect(characterUnitSelections(q, ordinary, text));
            TypedQuery<Tuple> query = q.query();
            for (String middle : characterUnitMiddleCharacters()) {
                query.setParameter(input, "A" + middle + "B");
                assertCharacterUnits(query.getSingleResult(), middle);
                assertEquals("A" + middle + "B", query.getParameterValue(input));
            }
            query.setParameter(input, "ACB");
            assertCharacterUnits(query.getSingleResult(), "C");
            for (ParameterExpression<?> parameter : q.bindings.keySet()) {
                assertTrue(q.cq.getParameters().stream().anyMatch(actual -> actual == parameter));
            }
            for (ParameterExpression<?> parameter : q.bindings.keySet()) {
                Object value = switch (parameter.getName()) {
                    case "needle" -> "A";
                    case "start", "edgeLength" -> 1;
                    case "length", "rest" -> 2;
                    default -> q.bindings.get(parameter);
                };
                Query.bind(query, parameter, value);
            }
            assertValues(query.getSingleResult(), 3, 3, 1, 1, 1, 1, "AC", "AC", "CB", "CB", "A", "A", "B", "B");
            q.bindings.forEach((parameter, value) -> Query.bind(query, parameter, value));
            assertCharacterUnits(query.getSingleResult(), "C");
        }
    }

    @Test
    public void testCharacterUnitsInLiteralTextOperations() {
        try (EntityManager em = emf.createEntityManager()) {
            for (String middle : characterUnitMiddleCharacters()) {
                Query q = new Query(em);
                String input = "A" + middle + "B";
                assertCharacterUnits(q.row(characterUnitSelections(q, q.cb.literal(input), q.cb.stringLiteral(input))), middle);
            }
            Query quoted = new Query(em);
            TextExpression value = quoted.cb.stringLiteral("A'_%\\B");
            assertValues(quoted.row(value.length(), value.locate("'"), value.substring(2, 1)), 6, 2, "'");
            assertEquals(List.of(1, 2, 3, 4), quoted.ids(quoted.cb.and(value.contains("'_%\\"),
                    value.endsWith("%\\B"), value.notEndsWith("'_%"))));
        }
    }

    private boolean usesDb2CharacterUnits() {
        return emf.unwrap(Session.class).getPlatform().getClass() == DB2Platform.class;
    }

    @Test
    public void testLocateStartNullsAndNotFoundRetainOriginalParameters() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.whereId(2);
            ParameterExpression<String> input = q.parameter(String.class, "input", "ababa");
            ParameterExpression<String> needle = q.parameter(String.class, "needle", "ba");
            ParameterExpression<Integer> start = q.parameter(Integer.class, "start", 2);
            Expression<String> ordinary = q.cb.coalesce(q.root.<String>get("nullableText"), input);
            TextExpression text = q.root.get(ScalarCriteriaRecord_.nullableText).coalesce(input);
            q.cq.multiselect(q.cb.locate(ordinary, needle), text.locate(needle),
                    q.cb.locate(ordinary, needle, start), text.locate(needle, start));
            TypedQuery<Tuple> query = q.query();
            Object[][] cases = {
                    {"ababa", "ba", 2, 2, 2},
                    {null, "ba", 2, null, null},
                    {"ababa", null, 2, null, null},
                    {"ababa", "ba", null, 2, null},
                    {null, null, null, null, null},
                    {"ababa", "missing", 2, 0, 0},
                    {"ababa", "ba", 4, 2, 4},
                    {"ababa", "ba", 6, 2, 0},
                    {"ababa", "ba", 2, 2, 2}
            };
            for (Object[] values : cases) {
                query.setParameter(input, (String) values[0]);
                query.setParameter(needle, (String) values[1]);
                query.setParameter(start, (Integer) values[2]);
                Tuple result = query.getSingleResult();
                assertValues(result, values[3], values[3], values[4], values[4]);
                for (Object value : result.toArray()) {
                    if (value != null) {
                        assertSame(Integer.class, value.getClass());
                    }
                }
                assertEquals(values[0], query.getParameterValue(input));
                assertEquals(values[1], query.getParameterValue(needle));
                assertEquals(values[2], query.getParameterValue(start));
            }
            assertEquals(Set.of(input, needle, start), q.cq.getParameters());
            for (ParameterExpression<?> parameter : q.bindings.keySet()) {
                assertTrue(q.cq.getParameters().stream().anyMatch(actual -> actual == parameter));
            }
        }
    }

    @Test
    public void testTrailingSpacesInMappedAndLiteralTextOperations() {
        try (EntityManager em = emf.createEntityManager()) {
            for (String input : List.of("a  ", "  ", "abc")) {
                em.getTransaction().begin();
                try {
                    em.createQuery("UPDATE ScalarCriteriaRecord r SET r.text = :text WHERE r.id = 1")
                            .setParameter("text", input).executeUpdate();
                    em.getTransaction().commit();
                } finally {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                }
                Query mapped = new Query(em);
                assertTrailingSpaces(mapped.row(trailingSpaceSelections(mapped,
                        mapped.root.<String>get("text"), mapped.text(), mapped.cb.literal(2))), input, 2);
                Query literal = new Query(em);
                assertTrailingSpaces(literal.row(trailingSpaceSelections(literal,
                        literal.cb.literal(input), literal.cb.stringLiteral(input), literal.cb.literal(2))), input, 2);
                Object[] jpql = em.createQuery("SELECT LENGTH(r.text), SUBSTRING(r.text, 1), SUBSTRING(r.text, 2) "
                        + "FROM ScalarCriteriaRecord r WHERE r.id = 1", Object[].class).getSingleResult();
                assertEquals(input.length(), jpql[0]);
                assertSame(Integer.class, jpql[0].getClass());
                assertEquals(input, jpql[1]);
                assertEquals(input.substring(1), jpql[2]);
            }
            Query nullable = new Query(em);
            nullable.whereId(2);
            assertValues(nullable.row(trailingSpaceSelections(nullable, nullable.root.<String>get("nullableText"),
                    nullable.root.get(ScalarCriteriaRecord_.nullableText), nullable.cb.literal(2))), new Object[6]);
            Object[] jpqlNull = em.createQuery("SELECT LENGTH(r.nullableText), SUBSTRING(r.nullableText, 1), "
                    + "SUBSTRING(r.nullableText, 2) FROM ScalarCriteriaRecord r WHERE r.id = 2", Object[].class)
                    .getSingleResult();
            assertEquals(Arrays.asList(null, null, null), Arrays.asList(jpqlNull));
        }
    }

    @Test
    public void testTrailingSpacesRetainOriginalParametersWhenQueriesAreReused() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.whereId(2);
            ParameterExpression<String> input = q.parameter(String.class, "input", "a  ");
            ParameterExpression<Integer> start = q.parameter(Integer.class, "start", 1);
            Expression<String> ordinary = q.cb.coalesce(q.root.<String>get("nullableText"), input);
            TextExpression text = q.root.get(ScalarCriteriaRecord_.nullableText).coalesce(input);
            q.cq.multiselect(trailingSpaceSelections(q, ordinary, text, start));
            TypedQuery<Tuple> query = q.query();
            for (String value : List.of("a  ", "  ", "abc", "a  ")) {
                query.setParameter(input, value);
                for (int index : List.of(1, 2, 1)) {
                    query.setParameter(start, index);
                    assertTrailingSpaces(query.getSingleResult(), value, index);
                    assertEquals(value, query.getParameterValue(input));
                    assertEquals(Integer.valueOf(index), query.getParameterValue(start));
                }
            }
            if (emf.unwrap(Session.class).getPlatform().isSQLServer()) {
                TypedQuery<Integer> jpql = em.createQuery("SELECT LENGTH(COALESCE(r.nullableText, :input)) "
                        + "FROM ScalarCriteriaRecord r WHERE r.id = 2", Integer.class);
                for (String value : Arrays.asList(null, "a  ", "  ", null, "abc", null)) {
                    jpql.setParameter("input", value);
                    Integer length = jpql.getSingleResult();
                    assertEquals(value == null ? null : Integer.valueOf(value.length()), length);
                    assertEquals(value, jpql.getParameterValue("input"));
                    if (length != null) {
                        assertSame(Integer.class, length.getClass());
                    }
                }
                // Exercise empty values and the boundary that would truncate
                // a sentinel appended before conversion to nvarchar(max).
                for (String value : List.of("", "A\u00e9  ", "x".repeat(8000) + "  ")) {
                    query.setParameter(input, value);
                    query.setParameter(start, 2);
                    assertTrailingSpaces(query.getSingleResult(), value, 2);
                    Query literal = new Query(em);
                    assertTrailingSpaces(literal.row(trailingSpaceSelections(literal,
                            literal.cb.literal(value), literal.cb.stringLiteral(value), literal.cb.literal(2))), value, 2);
                }
                String supplementary = "A\uD83D\uDE00  ";
                query.setParameter(input, supplementary);
                query.setParameter(start, 2);
                Tuple unicode = query.getSingleResult();
                assertSame(Integer.class, unicode.get(0).getClass());
                // SQL Server counts a surrogate pair as one or two characters
                // according to the existing collation; both trailing spaces count.
                int firstUnit = ((Number) em.createNativeQuery("SELECT UNICODE(?)")
                        .setParameter(1, "\uD83D\uDE00").getSingleResult()).intValue();
                assertTrue(firstUnit == 0x1f600 || firstUnit == 0xd83d);
                int length = firstUnit == 0x1f600 ? 4 : 5;
                assertValues(unicode, length, length, supplementary, supplementary,
                        supplementary.substring(1), supplementary.substring(1));
                query.setParameter(start, (Integer) null);
                Tuple nullStart = query.getSingleResult();
                assertEquals(null, nullStart.get(4));
                assertEquals(null, nullStart.get(5));
                query.setParameter(start, 2);
            }
            query.setParameter(input, (String) null);
            assertValues(query.getSingleResult(), new Object[6]);
            query.setParameter(input, "a  ");
            query.setParameter(start, 2);
            assertTrailingSpaces(query.getSingleResult(), "a  ", 2);
            assertEquals(Set.of(input, start), q.cq.getParameters());
            assertTrue(q.cq.getParameters().stream().anyMatch(parameter -> parameter == input));
            assertTrue(q.cq.getParameters().stream().anyMatch(parameter -> parameter == start));
        }
    }

    private static Selection<?>[] trailingSpaceSelections(Query q, Expression<String> ordinary,
                                                           TextExpression text, Expression<Integer> start) {
        return new Selection<?>[]{q.cb.length(ordinary), text.length(), q.cb.substring(ordinary, 1),
                text.substring(1), q.cb.substring(ordinary, start), text.substring(start)};
    }

    private static void assertTrailingSpaces(Tuple result, String input, int start) {
        String suffix = input.substring(Math.min(start - 1, input.length()));
        assertValues(result, input.length(), input.length(), input, input, suffix, suffix);
        assertSame(Integer.class, result.get(0).getClass());
        assertSame(Integer.class, result.get(1).getClass());
        for (int index = 2; index < 6; index++) {
            assertSame(String.class, result.get(index).getClass());
        }
    }

    @Test
    public void testLeftAndRightLengthsRetainOriginalParametersWhenQueriesAreReused() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            q.whereId(2);
            ParameterExpression<String> input = q.parameter(String.class, "input", "ACB");
            ParameterExpression<Integer> length = q.parameter(Integer.class, "length", 2);
            Expression<String> ordinary = q.cb.coalesce(q.root.<String>get("nullableText"), input);
            TextExpression text = q.root.get(ScalarCriteriaRecord_.nullableText).coalesce(input);
            q.cq.multiselect(q.cb.left(ordinary, length), text.left(length),
                    q.cb.right(ordinary, length), text.right(length));
            TypedQuery<Tuple> query = q.query();
            for (int count : List.of(2, 1, 3, 2)) {
                query.setParameter(length, count);
                assertPrefixAndSuffix(query.getSingleResult(), "ACB", count);
            }
            if (usesDb2CharacterUnits()) {
                // Db2's native functions pad overlong counts; the Criteria
                // prefix/suffix interpretation returns only available text.
                for (String value : List.of("", "A\u00e9B", "A\uD83D\uDE00B", "a  ", "ACB")) {
                    em.getTransaction().begin();
                    try {
                        em.createQuery("UPDATE ScalarCriteriaRecord r SET r.text = :text WHERE r.id = 1")
                                .setParameter("text", value).executeUpdate();
                        em.getTransaction().commit();
                    } finally {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                    }
                    for (int count : List.of(2, 0, 3, 8, 1, 8, 2)) {
                        query.setParameter(input, value);
                        query.setParameter(length, count);
                        assertPrefixAndSuffix(query.getSingleResult(), value, count);
                        assertEquals(value, query.getParameterValue(input));
                        assertEquals(Integer.valueOf(count), query.getParameterValue(length));
                        Query literal = new Query(em);
                        assertPrefixAndSuffix(literal.row(literal.cb.left(literal.cb.literal(value), count),
                                literal.cb.stringLiteral(value).left(count),
                                literal.cb.right(literal.cb.literal(value), count),
                                literal.cb.stringLiteral(value).right(count)), value, count);
                        Query mapped = new Query(em);
                        Expression<String> mappedText = mapped.root.get("text");
                        ParameterExpression<Integer> mappedCount = mapped.parameter(Integer.class, "count", count);
                        assertPrefixAndSuffix(mapped.row(mapped.cb.left(mappedText, mappedCount),
                                mapped.text().left(mappedCount), mapped.cb.right(mappedText, mappedCount),
                                mapped.text().right(mappedCount)), value, count);
                    }
                }
                query.setParameter(length, (Integer) null);
                assertValues(query.getSingleResult(), new Object[4]);
            }
            query.setParameter(input, (String) null);
            query.setParameter(length, 2);
            assertValues(query.getSingleResult(), new Object[4]);
            query.setParameter(input, "ACB");
            assertPrefixAndSuffix(query.getSingleResult(), "ACB", 2);
            assertEquals(Set.of(input, length), q.cq.getParameters());
            assertTrue(q.cq.getParameters().stream().anyMatch(parameter -> parameter == input));
            assertTrue(q.cq.getParameters().stream().anyMatch(parameter -> parameter == length));
        }
    }

    private static void assertPrefixAndSuffix(Tuple result, String input, int count) {
        int length = input.codePointCount(0, input.length());
        int bounded = Math.min(length, count);
        String prefix = input.substring(0, input.offsetByCodePoints(0, bounded));
        String suffix = input.substring(input.offsetByCodePoints(0, length - bounded));
        assertValues(result, prefix, prefix, suffix, suffix);
        for (int index = 0; index < 4; index++) {
            assertSame(String.class, result.get(index).getClass());
        }
    }

    private List<String> characterUnitMiddleCharacters() {
        // Db2 LUW uses Unicode code points. Other platform character-unit
        // interpretations are not changed by this platform-specific correction.
        return usesDb2CharacterUnits() ? List.of("C", "\u00e9", "\uD83D\uDE00") : List.of("C");
    }

    private Selection<?>[] characterUnitSelections(Query q, Expression<String> ordinary, TextExpression text) {
        ParameterExpression<String> needle = q.parameter(String.class, "needle", "B");
        ParameterExpression<Integer> start = q.parameter(Integer.class, "start", 2);
        ParameterExpression<Integer> rest = q.parameter(Integer.class, "rest", 3);
        ParameterExpression<Integer> length = q.parameter(Integer.class, "length", 1);
        ParameterExpression<Integer> edgeLength = q.parameter(Integer.class, "edgeLength", 2);
        return new Selection<?>[]{q.cb.length(ordinary), text.length(), q.cb.locate(ordinary, needle), text.locate(needle),
                q.cb.locate(ordinary, needle, start), text.locate(needle, start),
                q.cb.substring(ordinary, start, length), text.substring(start, length),
                q.cb.substring(ordinary, rest), text.substring(rest),
                q.cb.left(ordinary, edgeLength), text.left(edgeLength), q.cb.right(ordinary, edgeLength), text.right(edgeLength)};
    }

    private static void assertCharacterUnits(Tuple result, String middle) {
        assertValues(result, 3, 3, 3, 3, 3, 3, middle, middle, "B", "B", "A" + middle, "A" + middle, middle + "B", middle + "B");
        for (int index = 0; index < 6; index++) {
            assertSame(Integer.class, result.get(index).getClass());
        }
        for (int index = 6; index < 14; index++) {
            assertSame(String.class, result.get(index).getClass());
        }
    }

    @Test
    public void testLegacyBuilderNullOrderingDistinctPaginationAndReuse() {
        try (EntityManager em = emf.createEntityManager()) {
            for (Nulls precedence : List.of(Nulls.FIRST, Nulls.LAST)) {
                Query ascending = new Query(em);
                assertEquals(precedence == Nulls.FIRST ? List.of(2, 4, 3, 1) : List.of(3, 1, 2, 4),
                        ascending.orderedIds(ascending.cb.asc(ascending.root.<String>get("nullableText"), precedence)));
                Query descending = new Query(em);
                assertEquals(precedence == Nulls.FIRST ? List.of(2, 4, 1, 3) : List.of(1, 3, 2, 4),
                        descending.orderedIds(descending.cb.desc(descending.root.<String>get("nullableText"), precedence)));
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<String> criteria = cb.createQuery(String.class);
            Root<ScalarCriteriaRecord> root = criteria.from(ScalarCriteriaRecord.class);
            Expression<String> value = root.get("nullableText");
            criteria.select(value).distinct(true).orderBy(cb.asc(value, Nulls.LAST));
            TypedQuery<String> query = em.createQuery(criteria);
            assertEquals(Arrays.asList("a", "b", null), query.getResultList());
            assertEquals(List.of("b"), query.setFirstResult(1).setMaxResults(1).getResultList());
            assertEquals(List.of("a", "b"), query.setFirstResult(0).setMaxResults(2).getResultList());
            assertEquals(Arrays.asList("a", "b", null), em.createQuery(criteria).getResultList());
            assertEquals(Nulls.LAST, criteria.getOrderList().get(0).getNullPrecedence());

            ParameterExpression<String> fallback = cb.parameter(String.class, "fallback");
            Expression<String> composed = cb.coalesce(value, fallback);
            criteria.select(composed).distinct(false).orderBy(cb.asc(composed, Nulls.LAST));
            assertSame(fallback, criteria.getParameters().iterator().next());
            TypedQuery<String> composedQuery = em.createQuery(criteria).setParameter(fallback, "c");
            assertEquals(List.of("a", "b", "c", "c"), composedQuery.getResultList());
            assertEquals(List.of("b"), composedQuery.setFirstResult(1).setMaxResults(1).getResultList());
            assertEquals(List.of("a", "b", "c", "c"), em.createQuery(criteria).setParameter(fallback, "c").getResultList());
            assertEquals(Nulls.LAST, criteria.getOrderList().get(0).getNullPrecedence());
            if (emf.unwrap(Session.class).getPlatform().isMySQL()) {
                // Exercise MySQL's temporary CASE ordering with DISTINCT and
                // repeated bound operands in its projection/order expressions.
                criteria.distinct(true);
                TypedQuery<String> distinctQuery = em.createQuery(criteria).setParameter(fallback, "c");
                assertEquals(List.of("a", "b", "c"), distinctQuery.getResultList());
                assertEquals(List.of("b"), distinctQuery.setFirstResult(1).setMaxResults(1).getResultList());
                assertEquals(List.of("a", "b", "c"), em.createQuery(criteria).setParameter(fallback, "c").getResultList());
                assertEquals(Nulls.LAST, criteria.getOrderList().get(0).getNullPrecedence());
            }
        }
    }

    @Test
    public void testLegacyBuilderCharacterTrimOperandsRemainBindable() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<Character> character = q.parameter(Character.class, "trimCharacter", ']');
            ParameterExpression<String> value = q.parameter(String.class, "trimValue", "]]value]]");
            assertValues(q.row(q.cb.trim(']', value), q.cb.trim(character, value),
                    q.cb.trim(Trimspec.BOTH, ']', value), q.cb.trim(Trimspec.BOTH, character, value)),
                    "value", "value", "value", "value");
            Query nullable = new Query(em);
            assertValues(nullable.row(nullable.cb.trim('x', nullable.root.<String>get("nullableText"))), "b");
            nullable.whereId(2);
            assertValues(nullable.row(nullable.cb.trim('x', nullable.root.<String>get("nullableText"))), (Object) null);
        }
    }

    @Test
    public void testLegacyBuilderQuotientExpressionTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<Double> denominator = q.parameter(Double.class, "denominator", 2.0);
            Expression<Number> quotient = q.cb.quot(q.root.<Double>get("fraction"), denominator);
            assertEquals(Double.class, quotient.getJavaType());
            Tuple result = q.row(quotient);
            assertSame(Double.class, result.get(0).getClass());
            assertNumbers(result, 3.125);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCopiedCoalesceRetainsDerivedJoinIdentity() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(new ScalarCriteriaRecord(5, 1, 1.0, "no group", null, null));
            em.getTransaction().commit();
            em.clear();
            JpaCriteriaBuilder cb = (JpaCriteriaBuilder) em.getCriteriaBuilder();
            ExpressionBuilder primary = new ExpressionBuilder(ScalarCriteriaRecord.class);
            org.eclipse.persistence.expressions.Expression sourceGroup = primary.getAllowingNull("group");
            org.eclipse.persistence.expressions.Expression original = cb.toExpression(cb.coalesce(
                    cb.fromExpression(sourceGroup.get("rank"), Integer.class), cb.literal(0)));
            for (boolean forceInner : List.of(false, true)) {
                ExpressionBuilder replacement = new ExpressionBuilder(ScalarCriteriaRecord.class);
                org.eclipse.persistence.expressions.Expression id = replacement.get("id");
                org.eclipse.persistence.expressions.Expression copied = original.cloneUsing(replacement);
                if (forceInner) {
                    replacement.get("group", true);
                }
                ReportQuery report = new ReportQuery(ScalarCriteriaRecord.class, replacement);
                report.addAttribute("id", id, Integer.class);
                report.addAttribute("value", copied, Integer.class);
                report.addOrdering(id.ascending());
                List<ReportQueryResult> rows = (List<ReportQueryResult>) em.unwrap(JpaEntityManager.class)
                        .getActiveSession().executeQuery(report);
                assertEquals(forceInner ? List.of(1, 2, 3, 4) : List.of(1, 2, 3, 4, 5),
                        rows.stream().map(row -> row.get("id")).toList());
                assertEquals(forceInner ? List.of(10, 20, 10, 20) : List.of(10, 20, 10, 20, 0),
                        rows.stream().map(row -> row.get("value")).toList());
                assertSame(id, replacement.get("id"));
                assertTrue(((ObjectExpression) sourceGroup).shouldUseOuterJoin());
                assertSame(primary, original.getBuilder());
            }
        }
    }

    @Test
    public void testCoalesceWithLeadingParameterAndCaseRetainsQueryContext() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> criteria = cb.createQuery(Integer.class);
            Root<ScalarCriteriaRecord> root = criteria.from(ScalarCriteriaRecord.class);
            ParameterExpression<Integer> leading = cb.parameter(Integer.class, "leading");
            ParameterExpression<Integer> branch = cb.parameter(Integer.class, "branch");
            ParameterExpression<Integer> trailing = cb.parameter(Integer.class, "trailing");
            Expression<Integer> selected = cb.<Integer, Integer>selectCase(root.get("id"))
                    .when(1, branch).otherwise(root.<Integer>get("amount"));
            // Produce SQL null even on platforms that inline complex CASE operands.
            Expression<Integer> first = cb.nullif(leading, cb.literal(0));
            Expression<Integer> result = cb.coalesce(first, cb.coalesce(selected, trailing));
            criteria.select(result).orderBy(cb.asc(root.get("id")));
            assertEquals(Integer.class, result.getJavaType());
            assertEquals(Set.of(leading, branch, trailing), criteria.getParameters());
            TypedQuery<Integer> query = em.createQuery(criteria);
            query.setParameter(leading, 0).setParameter(branch, 11).setParameter(trailing, 99);
            assertEquals(List.of(11, -3, 9, 99), query.getResultList());
            query.setParameter(leading, 7);
            assertEquals(List.of(7, 7, 7, 7), query.getResultList());
            query.setParameter(leading, 0).setParameter(branch, 13).setParameter(trailing, 17);
            assertEquals(List.of(13, -3, 9, 17), query.getResultList());

            criteria.where(cb.gt(result, 8));
            assertEquals(Set.of(leading, branch, trailing), criteria.getParameters());
            TypedQuery<Integer> filtered = em.createQuery(criteria);
            filtered.setParameter(leading, 0).setParameter(branch, 11).setParameter(trailing, 99);
            assertEquals(List.of(11, 9, 99), filtered.getResultList());
            filtered.setParameter(leading, 7);
            assertTrue(filtered.getResultList().isEmpty());
            query.setParameter(leading, 5);
            assertEquals(List.of(5, 5, 5, 5), query.getResultList());
            assertEquals(Integer.class, result.getJavaType());
        }
    }

    @Test
    public void testLiteralCoalesceDoesNotMutateReusableOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Expression<String> source = q.cb.literal("original");
            Expression<String> absent = q.cb.nullLiteral(String.class);
            Expression<String> fallback = q.cb.literal("fallback");
            Expression<String> binaryExpressions = q.cb.coalesce(source, fallback);
            Expression<String> binaryValue = q.cb.coalesce(source, "fallback");
            Expression<String> incremental = q.cb.<String>coalesce().value(source).value(fallback);
            TextExpression scalar = q.cb.stringLiteral("scalar");
            assertValues(q.row(binaryExpressions, binaryValue, incremental,
                    q.cb.coalesce(absent, fallback), q.cb.coalesce(absent, "fallback"),
                    q.cb.<String>coalesce().value(absent).value(fallback),
                    scalar.coalesce(fallback), scalar.coalesce("fallback"),
                    q.cb.numericLiteral(3).coalesce(9).plus(1),
                    q.cb.concat(source, " again"), scalar.append(" again")),
                    "original", "original", "original", "fallback", "fallback", "fallback",
                    "scalar", "scalar", 4, "original again", "scalar again");
        }
    }

    @Test
    public void testLaterMappedCoalesceOperandsPreserveValueConversion() {
        try (EntityManager em = emf.createEntityManager()) {
            for (int id : new int[]{1, 2, 4}) {
                Query q = new Query(em);
                q.whereId(id);
                ParameterExpression<String> first = q.parameter(String.class, "first", id == 1 ? "code1" : null);
                TextExpression mapped = q.root.get(ScalarCriteriaRecord_.convertedText);
                Expression<String> binary = q.cb.coalesce(first, mapped);
                Expression<String> third = q.cb.<String>coalesce().value(first)
                        .value(q.cb.nullLiteral(String.class)).value(mapped);
                TextExpression scalar = id == 1 ? q.cb.stringLiteral("code1")
                        : q.root.get(ScalarCriteriaRecord_.nullableText);
                Tuple result = q.row(q.cb.length(binary), q.cb.length(third), scalar.coalesce(mapped).length());
                // The converter stores "stored:codeN". A non-null unconverted value would
                // have length 5, so this also checks conversion of a preceding parameter/literal.
                if (id == 4) {
                    assertValues(result, null, null, null);
                } else {
                    assertValues(result, 12, 12, 12);
                }
            }
        }
    }

    private static void assertMatching(EntityManager em, Function<TextExpression, Predicate> operation,
                                       Integer... ids) {
        Query q = new Query(em);
        assertEquals(Arrays.asList(ids), q.ids(operation.apply(q.text())));
    }

    private void assertReplacement(Query query, Selection<?>[] expressions, Object... expected) {
        if (emf.unwrap(Session.class).getPlatform().isDerby()) {
            // Derby has no native REPLACE function. Execute both Criteria and
            // native controls; this verifies the precise capability boundary,
            // not successful replacement or permission to ignore other errors.
            assertMissingDerbyReplace(() -> query.row(expressions));
            assertMissingDerbyReplace(() -> query.em.createNativeQuery(
                    "VALUES REPLACE('ababa', 'ba', 'XY')").getSingleResult());
        } else {
            assertValues(query.row(expressions), expected);
        }
    }

    private static void assertMissingDerbyReplace(Runnable command) {
        PersistenceException failure = assertThrows(PersistenceException.class, command::run);
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sql) {
                assertEquals("42Y03", sql.getSQLState());
                assertEquals(30000, sql.getErrorCode());
                assertTrue("Expected the missing REPLACE function signature",
                        sql.getMessage() != null && sql.getMessage().contains("'REPLACE'"));
                return;
            }
        }
        throw new AssertionError("Expected Derby's missing REPLACE SQLException", failure);
    }

    private static void assertValues(Tuple tuple, Object... values) {
        assertEquals(Arrays.asList(values), Arrays.asList(tuple.toArray()));
    }

    private static void assertNumbers(Tuple tuple, double... values) {
        assertEquals(values.length, tuple.getElements().size());
        for (int i = 0; i < values.length; i++) {
            assertTrue("Expected a numeric result at position " + i, tuple.get(i) instanceof Number);
            assertEquals("Numeric result at position " + i, values[i],
                    ((Number) tuple.get(i)).doubleValue(), Math.max(0.00001, Math.abs(values[i]) * 0.00001));
        }
    }

    @SafeVarargs
    private static NumericExpression<Double>[] numericArray(NumericExpression<Double>... values) {
        return values;
    }

    /** Constructor-projection target, deliberately independent of managed entities. */
    public static class ScalarProjection {
        public final Long number;
        public final String text;

        public ScalarProjection(Long number, String text) {
            this.number = number;
            this.text = text;
        }
    }

    private static class Query {
        private final EntityManager em;
        private final CriteriaBuilder cb;
        private final CriteriaQuery<Tuple> cq;
        private final Root<ScalarCriteriaRecord> root;
        private final Map<ParameterExpression<?>, Object> bindings = new LinkedHashMap<>();

        private Query(EntityManager em) {
            this.em = em;
            cb = em.getCriteriaBuilder();
            cq = cb.createTupleQuery();
            root = cq.from(ScalarCriteriaRecord.class);
            whereId(1);
        }

        private NumericExpression<Integer> number() {
            return root.get(ScalarCriteriaRecord_.amount);
        }

        private NumericExpression<Double> fraction() {
            return root.get(ScalarCriteriaRecord_.fraction);
        }

        private TextExpression text() {
            return root.get(ScalarCriteriaRecord_.text);
        }

        private void whereId(int id) {
            cq.where(cb.equal(root.get(ScalarCriteriaRecord_.id), id));
        }

        private void allRows() {
            cq.where();
        }

        private <T> ParameterExpression<T> parameter(Class<T> type, String name, T value) {
            ParameterExpression<T> parameter = cb.parameter(type, name);
            bindings.put(parameter, value);
            return parameter;
        }

        private Tuple row(Selection<?>... selections) {
            cq.multiselect(selections);
            return query().getSingleResult();
        }

        private List<Integer> ids(Predicate predicate) {
            cq.multiselect(root.get(ScalarCriteriaRecord_.id));
            cq.where(predicate);
            cq.orderBy(cb.asc(root.get(ScalarCriteriaRecord_.id)));
            return query().getResultList().stream().map(tuple -> tuple.get(0, Integer.class)).toList();
        }

        private List<Integer> orderedIds(Order order) {
            cq.multiselect(root.get(ScalarCriteriaRecord_.id));
            cq.where();
            cq.orderBy(order, cb.asc(root.get(ScalarCriteriaRecord_.id)));
            return query().getResultList().stream().map(tuple -> tuple.get(0, Integer.class)).toList();
        }

        private TypedQuery<Tuple> query() {
            Set<ParameterExpression<?>> actual = cq.getParameters();
            assertEquals("Every nested parameter must be discovered", bindings.size(), actual.size());
            for (ParameterExpression<?> expected : bindings.keySet()) {
                assertTrue("The original parameter object must survive scalar adaptation",
                        actual.stream().anyMatch(parameter -> parameter == expected));
            }
            TypedQuery<Tuple> query = em.createQuery(cq);
            bindings.forEach((parameter, value) -> bind(query, parameter, value));
            return query;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static void bind(TypedQuery<?> query, ParameterExpression parameter, Object value) {
            query.setParameter(parameter, value);
        }
    }
}
