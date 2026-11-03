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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.IsoFields;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaBuilder.Case;
import jakarta.persistence.criteria.CriteriaBuilder.SimpleCase;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.LocalDateField;
import jakarta.persistence.criteria.LocalDateTimeField;
import jakarta.persistence.criteria.LocalTimeField;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.TemporalExpression;
import jakarta.persistence.metamodel.TemporalAttribute;

import org.eclipse.persistence.internal.jpa.querydef.CriteriaBuilderImpl;
import org.eclipse.persistence.internal.jpa.querydef.InternalSelection;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionCustomizer;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.platform.database.DB2Platform;
import org.eclipse.persistence.platform.database.DerbyPlatform;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.jpa.test.criteria.model.TemporalCriteriaRecord;
import org.eclipse.persistence.jpa.test.criteria.model.TemporalCriteriaRecord_;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Public query regressions for temporal expressions and declared CASE result types. */
@RunWith(EmfRunner.class)
public class TestTemporalAndTypedCaseCriteria {
    private static final LocalDateTime FIRST = LocalDateTime.of(2021, 1, 1, 12, 34, 56, 125000000);
    private static final LocalDateTime SECOND = LocalDateTime.of(2020, 12, 31, 23, 59, 58, 250000000);
    private static final LocalDateTime THIRD = LocalDateTime.of(2021, 4, 5, 10, 20, 30, 500000000);

    @Emf(createTables = DDLGen.DROP_CREATE, classes = {TemporalCriteriaRecord.class, TemporalCriteriaRecord.ClockText.class},
            properties = {@Property(name = "eclipselink.cache.shared.default", value = "false"),
                    @Property(name = "eclipselink.session.customizer",
                            value = "org.eclipse.persistence.jpa.test.criteria.TestTemporalAndTypedCaseCriteria$TemporalPrecision")})
    private EntityManagerFactory emf;

    public static class TemporalPrecision implements SessionCustomizer {
        @Override
        public void customize(Session session) {
            if (session.getPlatform() instanceof H2Platform) {
                session.getDescriptor(TemporalCriteriaRecord.class).getMappingForAttributeName("timeValue")
                        .getField().setSecondPrecision(9);
            }
        }
    }

    @Before
    public void setup() {
        emf.runInTransaction(em -> {
            TemporalCriteriaRecord first = new TemporalCriteriaRecord(1, FIRST, "first", 10);
            TemporalCriteriaRecord second = new TemporalCriteriaRecord(2, SECOND, "second", 20);
            second.parent = first;
            em.persist(first);
            em.persist(second);
            em.persist(new TemporalCriteriaRecord(3, THIRD, "third", 30));
            em.persist(new TemporalCriteriaRecord(4, null, null, null));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createQuery("UPDATE TemporalCriteriaRecord r SET r.parent = NULL").executeUpdate();
            em.createQuery("DELETE FROM TemporalCriteriaRecord").executeUpdate();
        });
    }

    @Test
    public void testExtractFieldsAndPrecision() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TemporalExpression<LocalDateTime> stamp = q.root.get(TemporalCriteriaRecord_.stampValue);
            for (int id : List.of(1, 2, 3)) {
                q.id(id);
                LocalDateTime stored = em.createQuery("SELECT r.stampValue FROM TemporalCriteriaRecord r WHERE r.id = :id", LocalDateTime.class)
                        .setParameter("id", id).getSingleResult();
                Tuple row = q.row(stamp.extract(LocalDateTimeField.YEAR), stamp.extract(LocalDateTimeField.QUARTER),
                        stamp.extract(LocalDateTimeField.MONTH), stamp.extract(LocalDateTimeField.WEEK),
                        stamp.extract(LocalDateTimeField.DAY), stamp.extract(LocalDateTimeField.HOUR),
                        stamp.extract(LocalDateTimeField.MINUTE), stamp.extract(LocalDateTimeField.SECOND),
                        q.cb.extract(LocalDateTimeField.DATE, stamp), q.cb.extract(LocalDateTimeField.TIME, stamp),
                        stamp.extract(LocalDateTimeField.YEAR).plus(1));
                assertValues(row, stored.getYear(), stored.get(IsoFields.QUARTER_OF_YEAR), stored.getMonthValue(),
                        stored.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR), stored.getDayOfMonth(), stored.getHour(),
                        stored.getMinute(), stored.getSecond() + stored.getNano() / 1_000_000_000d,
                        stored.toLocalDate(), nativeTime(em, stored), stored.getYear() + 1);
                assertEquals(LocalDateTime.class, stamp.getJavaType());
            }
        }
    }

    @Test
    public void testIsoWeeksAcrossCalendarBoundariesAndRepeatedBindings() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<LocalDate> value = q.cb.parameter(LocalDate.class, "date");
            q.cq.multiselect(q.cb.extract(LocalDateField.WEEK, value), q.cb.extract(LocalDateField.YEAR, value));
            TypedQuery<Tuple> query = em.createQuery(q.cq);
            List<LocalDate> dates = Arrays.asList(null, LocalDate.of(1700, 1, 1), LocalDate.of(1752, 12, 31),
                    LocalDate.of(1753, 1, 1), LocalDate.of(2015, 12, 31), LocalDate.of(2016, 1, 1),
                    LocalDate.of(2017, 1, 1), LocalDate.of(2019, 12, 30), LocalDate.of(2020, 2, 29),
                    LocalDate.of(2020, 12, 31), LocalDate.of(2021, 1, 1), LocalDate.of(2024, 12, 30), LocalDate.of(2021, 4, 5),
                    LocalDate.of(1900, 3, 1), LocalDate.of(2000, 2, 29), LocalDate.of(2000, 3, 1), null);
            for (LocalDate date : dates) {
                query.setParameter(value, date);
                assertValues(query.getSingleResult(), date == null ? null : date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
                        date == null ? null : date.getYear());
            }
            assertSame(value, q.cq.getParameters().iterator().next());
            assertEquals(LocalDate.class, value.getParameterType());
        }
    }

    @Test
    public void testDateAndTimeFieldsAndTemporalLiterals() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TemporalExpression<LocalDate> date = q.root.get(TemporalCriteriaRecord_.dateValue);
            TemporalExpression<LocalTime> time = q.root.get(TemporalCriteriaRecord_.timeValue);
            assertValues(q.row(date.extract(LocalDateField.YEAR), date.extract(LocalDateField.QUARTER),
                    date.extract(LocalDateField.MONTH), date.extract(LocalDateField.WEEK), date.extract(LocalDateField.DAY),
                    time.extract(LocalTimeField.HOUR), time.extract(LocalTimeField.MINUTE), time.extract(LocalTimeField.SECOND)),
                    2021, 1, 1, 53, 1, 12, 34, 56d);
            assertValues(q.row(q.cb.temporalLiteral(FIRST.toLocalDate()), q.cb.temporalLiteral(FIRST.toLocalTime().withNano(0)),
                    q.cb.temporalLiteral(FIRST).extract(LocalDateTimeField.YEAR)), FIRST.toLocalDate(), FIRST.toLocalTime().withNano(0), 2021);
            assertThrows(IllegalArgumentException.class, () -> q.cb.temporalLiteral((LocalDate) null));
        }
    }

    @Test
    public void testTypedCaseFactoriesAndProjectionTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Expression<String> text = q.root.get(TemporalCriteriaRecord_.text);
            Expression<String> simple = text.selectCase(String.class).when("first", "one").otherwise((String) null);
            Expression<String> builderSimple = q.cb.selectCase(text, String.class).when("first", q.cb.literal("two")).otherwise("other");
            Expression<String> searched = q.cb.selectCase(String.class).when(q.cb.equal(text, "first"), "three").otherwise(q.cb.nullLiteral(String.class));
            Expression<Number> promoted = q.cb.selectCase(Number.class).when(q.cb.equal(text, "first"), 1).otherwise(2.5d);
            assertValues(q.row(simple, builderSimple, searched, promoted), "one", "two", "three", 1d);
            assertEquals(String.class, simple.getJavaType());
            assertEquals(Double.class, promoted.getJavaType());
            q.id(4);
            assertValues(q.row(simple, builderSimple, searched, promoted), null, "other", null, 2.5d);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRuntimeJoinedAndCorrelatedTemporalPaths() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TemporalAttribute<TemporalCriteriaRecord, LocalDate> runtime =
                    (TemporalAttribute<TemporalCriteriaRecord, LocalDate>) emf.getMetamodel()
                            .entity(TemporalCriteriaRecord.class).getSingularAttribute("dateValue", LocalDate.class);
            assertValues(q.row(q.root.get(runtime).extract(LocalDateField.YEAR)), 2021);
            q.id(2);
            Join<TemporalCriteriaRecord, TemporalCriteriaRecord> parent = q.root.join(TemporalCriteriaRecord_.parent);
            assertValues(q.row(parent.get(TemporalCriteriaRecord_.dateValue).extract(LocalDateField.YEAR)), 2021);
            assertEquals(Set.of(q.root), q.cq.getRoots());

            CriteriaQuery<Integer> query = q.cb.createQuery(Integer.class);
            Root<TemporalCriteriaRecord> root = query.from(TemporalCriteriaRecord.class);
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<TemporalCriteriaRecord> correlated = subquery.correlate(root);
            subquery.select(correlated.get(TemporalCriteriaRecord_.dateValue).extract(LocalDateField.YEAR));
            query.select(root.get(TemporalCriteriaRecord_.id)).where(q.cb.equal(subquery, 2020));
            assertEquals(List.of(2), em.createQuery(query).getResultList());
        }
    }

    @Test
    public void testTemporalComparisonsOrderingAndAggregates() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TemporalExpression<LocalDate> date = q.root.get(TemporalCriteriaRecord_.dateValue);
            LocalDate low = SECOND.toLocalDate();
            LocalDate high = FIRST.toLocalDate();
            q.cq.multiselect(q.root.get(TemporalCriteriaRecord_.id)).where(q.cb.and(
                    date.between(low, high), date.between(q.cb.literal(low), q.cb.literal(high)),
                    date.after(low.minusDays(1)), date.after(q.cb.literal(low.minusDays(1))),
                    date.before(high.plusDays(1)), date.before(q.cb.literal(high.plusDays(1))),
                    date.greaterThanOrEqualTo(low), date.lessThanOrEqualTo(q.cb.literal(high))));
            q.cq.orderBy(date.asc());
            assertEquals(List.of(2, 1), em.createQuery(q.cq).getResultList().stream().map(row -> row.get(0)).toList());
            q.cq.where().orderBy(date.desc(Nulls.LAST));
            assertEquals(List.of(3, 1, 2, 4), em.createQuery(q.cq).getResultList().stream().map(row -> row.get(0)).toList());
            q.cq.orderBy();
            assertValues(q.row(date.min(), date.max()), low, THIRD.toLocalDate());
        }
    }

    @Test
    public void testTemporalNullFunctionsAndRebinding() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            TemporalExpression<LocalDate> date = q.root.get(TemporalCriteriaRecord_.dateValue);
            ParameterExpression<LocalDate> fallback = q.cb.parameter(LocalDate.class, "fallback");
            Expression<LocalDate> absent = q.cb.nullLiteral(LocalDate.class);
            q.cq.multiselect(date.coalesce(fallback), date.coalesce((LocalDate) null), date.coalesce(absent),
                    date.nullif((LocalDate) null), date.nullif(absent), date.nullif(fallback),
                    date.coalesce(fallback).extract(LocalDateField.YEAR));
            q.id(4);
            assertEquals(Set.of(fallback), q.cq.getParameters());
            assertSame(fallback, q.cq.getParameters().iterator().next());
            TypedQuery<Tuple> query = em.createQuery(q.cq);
            for (LocalDate binding : Arrays.asList(FIRST.toLocalDate(), null, SECOND.toLocalDate(), FIRST.toLocalDate())) {
                query.setParameter(fallback, binding);
                assertValues(query.getSingleResult(), binding, null, null, null, null, null,
                        binding == null ? null : binding.getYear());
            }
            q.id(1);
            query = em.createQuery(q.cq).setParameter(fallback, FIRST.toLocalDate());
            assertValues(query.getSingleResult(), FIRST.toLocalDate(), FIRST.toLocalDate(), FIRST.toLocalDate(),
                    FIRST.toLocalDate(), FIRST.toLocalDate(), null, 2021);
            Query fresh = new Query(em);
            assertValues(fresh.row(fresh.root.get(TemporalCriteriaRecord_.dateValue)
                    .nullif(FIRST.toLocalDate()).coalesce(SECOND.toLocalDate())), SECOND.toLocalDate());
            assertTemporalNullFunctions(em, LocalDate.class, FIRST.toLocalDate(), SECOND.toLocalDate());
            assertTemporalNullFunctions(em, LocalDateTime.class, FIRST, SECOND);
            assertTemporalNullFunctions(em, LocalTime.class, nativeTime(em, FIRST), nativeTime(em, SECOND));
        }
    }

    private <T extends Temporal & Comparable<? super T>> void assertTemporalNullFunctions(
            EntityManager em, Class<T> type, T first, T second) {
        Query q = new Query(em);
        ParameterExpression<T> parameter = q.cb.parameter(type, "temporalOperand");
        TemporalExpression<T> literal = q.cb.temporalLiteral(first);
        q.cq.multiselect(literal.nullif(parameter), literal.nullif(parameter).coalesce(second),
                literal.coalesce(parameter).nullif(first), literal.nullif(first).coalesce(parameter),
                literal.coalesce(parameter).nullif(second));
        TypedQuery<Tuple> query = em.createQuery(q.cq);
        for (T binding : Arrays.asList(null, first, second, null, first)) {
            boolean equal = first.equals(binding);
            assertQueryValues(query.setParameter(parameter, binding), equal ? null : first,
                    equal ? second : first, null, binding, first);
        }
        assertSame(parameter, query.getParameters().iterator().next());
    }

    @Test
    public void testTemporalNullFunctionProjectionTypes() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            ParameterExpression<LocalDateTime> parameter = cb.parameter(LocalDateTime.class, "projectionStamp");
            TemporalExpression<LocalDateTime> stamp = cb.temporalLiteral(FIRST).nullif(parameter);
            TemporalExpression<LocalDate> date = cb.temporalLiteral(FIRST.toLocalDate()).nullif(SECOND.toLocalDate());
            TemporalExpression<LocalTime> time = cb.temporalLiteral(FIRST.toLocalTime().withNano(0))
                    .nullif(SECOND.toLocalTime().withNano(0));
            CriteriaQuery<LocalDateTime> scalar = cb.createQuery(LocalDateTime.class);
            scalar.select(stamp).where(cb.equal(scalar.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            CriteriaQuery<LocalDate> dateScalar = cb.createQuery(LocalDate.class);
            dateScalar.select(date).where(cb.equal(dateScalar.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            CriteriaQuery<LocalTime> timeScalar = cb.createQuery(LocalTime.class);
            timeScalar.select(time).where(cb.equal(timeScalar.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            CriteriaQuery<Tuple> tuple = cb.createTupleQuery();
            tuple.multiselect(date, time, stamp)
                    .where(cb.equal(tuple.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            CriteriaQuery<TemporalProjection> constructor = cb.createQuery(TemporalProjection.class);
            constructor.multiselect(date, time, stamp)
                    .where(cb.equal(constructor.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            CriteriaQuery<Object[]> array = cb.createQuery(Object[].class);
            array.multiselect(date, time, stamp)
                    .where(cb.equal(array.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<LocalDateTime> scalarQuery = handler.createQuery(scalar);
            TypedQuery<Tuple> tupleQuery = handler.createQuery(tuple);
            TypedQuery<TemporalProjection> constructorQuery = handler.createQuery(constructor);
            assertEquals(FIRST.toLocalDate(), handler.createQuery(dateScalar).getSingleResult());
            assertEquals(FIRST.toLocalTime().withNano(0), handler.createQuery(timeScalar).getSingleResult());
            TypedQuery<Object[]> arrayQuery = handler.createQuery(array);
            var report = (org.eclipse.persistence.queries.ReportQuery) arrayQuery.unwrap(JpaQuery.class).getDatabaseQuery();
            List<Class<?>> types = List.of(LocalDate.class, LocalTime.class, LocalDateTime.class);
            for (int index = 0; index < types.size(); index++) {
                var item = report.getItems().get(index);
                assertEquals(types.get(index), item.getResultType());
                assertEquals(types.get(index), ((org.eclipse.persistence.internal.expressions.FunctionExpression)
                        item.getAttributeExpression()).getResultType());
            }
            for (LocalDateTime binding : Arrays.asList(null, FIRST, SECOND, null)) {
                LocalDateTime expected = FIRST.equals(binding) ? null : FIRST;
                assertEquals(expected, scalarQuery.setParameter(parameter, binding).getSingleResult());
                TemporalProjection projected = constructorQuery.setParameter(parameter, binding).getSingleResult();
                assertEquals(FIRST.toLocalDate(), projected.date);
                assertEquals(FIRST.toLocalTime().withNano(0), projected.time);
                assertEquals(expected, projected.stamp);
                assertValues(tupleQuery.setParameter(parameter, binding).getSingleResult(),
                        FIRST.toLocalDate(), FIRST.toLocalTime().withNano(0), expected);
                Object[] values = arrayQuery.setParameter(parameter, binding).getSingleResult();
                assertEquals(FIRST.toLocalDate(), values[0]);
                assertEquals(FIRST.toLocalTime().withNano(0), values[1]);
                assertEquals(expected, values[2]);
            }
            array.where().distinct(true);
            List<Object[]> distinct = handler.createQuery(array).setParameter(parameter, SECOND).getResultList();
            assertEquals(1, distinct.size());
            assertEquals(FIRST.toLocalDate(), distinct.getFirst()[0]);
            assertEquals(FIRST.toLocalTime().withNano(0), distinct.getFirst()[1]);
            assertEquals(FIRST, distinct.getFirst()[2]);
        });
    }

    public static class TemporalProjection {
        public final LocalDate date;
        public final LocalTime time;
        public final LocalDateTime stamp;

        public TemporalProjection(LocalDate date, LocalTime time, LocalDateTime stamp) {
            this.date = date;
            this.time = time;
            this.stamp = stamp;
        }
    }

    @Test
    public void testExtractParameterRebindingAndSourceReuse() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            ParameterExpression<LocalDateTime> value = cb.parameter(LocalDateTime.class, "stamp");
            value.alias("original");
            Expression<Integer> year = cb.extract(LocalDateTimeField.YEAR, value);
            Expression<Double> second = cb.extract(LocalDateTimeField.SECOND, value);
            year.alias("year");
            cq.multiselect(year, second, cb.selectCase(LocalDateTime.class).when(cb.literal(true), value)).where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
            assertEquals(Set.of(value), cq.getParameters());
            assertSame(value, cq.getParameters().iterator().next());
            assertEquals("original", value.getAlias());
            TypedQuery<Tuple> query = handler.createQuery(cq);
            for (LocalDateTime binding : Arrays.asList(null, FIRST, null, SECOND, FIRST)) {
                query.setParameter(value, binding);
                assertValues(query.getSingleResult(), binding == null ? null : binding.getYear(),
                        binding == null ? null : binding.getSecond() + binding.getNano() / 1_000_000_000d, binding);
            }
            assertEquals(LocalDateTime.class, value.getParameterType());
        });
    }

    @Test
    public void testAllSimpleCaseArmsAndElseReplacement() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<Integer> fourth = q.cb.parameter(Integer.class, "fourth");
            ParameterExpression<String> result = q.cb.parameter(String.class, "result");
            ParameterExpression<String> replaced = q.cb.parameter(String.class, "replaced");
            SimpleCase<Integer, String> expression = q.cb.selectCase(q.root.get(TemporalCriteriaRecord_.id), String.class);
            expression.when(1, "one").when(2, q.cb.literal("two"))
                    .when(q.cb.literal(3), "three").when(fourth, result);
            expression.otherwise(replaced);
            expression.otherwise((String) null);
            q.cq.multiselect(expression).where().orderBy(q.cb.asc(q.root.get(TemporalCriteriaRecord_.id)));
            assertEquals(Set.of(fourth, result), q.cq.getParameters());
            TypedQuery<Tuple> query = em.createQuery(q.cq).setParameter(fourth, 4).setParameter(result, "four");
            assertEquals(List.of("one", "two", "three", "four"), query.getResultList().stream().map(row -> row.get(0)).toList());
            query.setParameter(result, null);
            assertEquals(Arrays.asList("one", "two", "three", null), query.getResultList().stream().map(row -> row.get(0)).toList());
        }
    }

    @Test
    public void testOrderedNestedCasesAndAbsentElse() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Case<Number> inner = q.cb.selectCase(Number.class).when(q.cb.equal(q.root.get(TemporalCriteriaRecord_.id), 1), 1);
            Case<Number> outer = q.cb.selectCase(Number.class).when(q.cb.isNotNull(q.root.get(TemporalCriteriaRecord_.text)), inner);
            inner.otherwise(2.5d);
            Expression<String> ordered = q.cb.selectCase(String.class)
                    .when(q.cb.gt(q.root.get(TemporalCriteriaRecord_.amount), 0), "positive")
                    .when(q.cb.gt(q.root.get(TemporalCriteriaRecord_.amount), 20), q.cb.literal("large"));
            q.id(3);
            assertValues(q.row(outer, ordered), 2.5d, "positive");
            assertEquals(Double.class, outer.getJavaType());
            q.id(4);
            assertValues(q.row(outer, ordered), null, null);
        }
    }

    @Test
    public void testConcreteAndPolymorphicCaseResults() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Expression<Boolean> first = q.cb.equal(q.root.get(TemporalCriteriaRecord_.id), 1);
            Expression<CharSequence> text = q.cb.selectCase(CharSequence.class).when(first, "text").otherwise((CharSequence) null);
            Expression<Object> object = q.cb.selectCase(Object.class).when(first, q.root.get(TemporalCriteriaRecord_.text)).otherwise("other");
            Expression<Number> decimal = q.cb.selectCase(Number.class).when(first, 1).otherwise(new BigDecimal("2.5"));
            Expression<Long> concrete = q.cb.selectCase(Long.class).when(first, 9L).otherwise((Long) null);
            Expression<LocalDate> date = q.cb.selectCase(LocalDate.class).when(first, FIRST.toLocalDate()).otherwise(q.cb.nullLiteral(LocalDate.class));
            Expression<LocalTime> time = q.cb.selectCase(LocalTime.class).when(first, FIRST.toLocalTime().withNano(0)).otherwise((LocalTime) null);
            Expression<LocalDateTime> stamp = q.cb.selectCase(LocalDateTime.class).when(first, q.root.get(TemporalCriteriaRecord_.stampValue)).otherwise((LocalDateTime) null);
            assertValues(q.row(text, object, decimal, concrete, date, time, stamp),
                    "text", "first", BigDecimal.ONE, 9L, FIRST.toLocalDate(), FIRST.toLocalTime().withNano(0), FIRST);
            assertEquals(String.class, text.getJavaType());
            assertEquals(String.class, object.getJavaType());
            q.id(4);
            Tuple row = q.row(text, object, decimal, concrete, date, time, stamp);
            assertNull(row.get(0));
            assertEquals("other", row.get(1));
            assertEquals(0, new BigDecimal("2.5").compareTo(row.get(2, BigDecimal.class)));
            for (int i = 3; i < 7; i++) {
                assertNull(row.get(i));
            }
        }
    }

    @Test
    public void testCaseNullParametersWithBothHandlers() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            ParameterExpression<String> value = cb.parameter(String.class, "value");
            ParameterExpression<Integer> selector = cb.parameter(Integer.class, "selector");
            Expression<String> simple = selector.selectCase(String.class).when(1, value).otherwise("other");
            Expression<String> searched = cb.selectCase(String.class).when(cb.equal(selector, 1), value).otherwise(cb.nullLiteral(String.class));
            cq.multiselect(simple, searched).where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
            assertEquals(Set.of(selector, value), cq.getParameters());
            TypedQuery<Tuple> query = handler.createQuery(cq).setParameter(selector, 1);
            for (String binding : Arrays.asList(null, "text", null, "again")) {
                query.setParameter(value, binding);
                assertValues(query.getSingleResult(), binding, binding);
            }
            query.setParameter(selector, 2);
            assertValues(query.getSingleResult(), "other", null);
        });
    }

    @Test
    public void testImplicitConstructorAndLegacyControls() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Projection> cq = cb.createQuery(Projection.class);
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            Expression<String> typed = root.get(TemporalCriteriaRecord_.text).selectCase(String.class).when("first", "one").otherwise("other");
            cq.multiselect(root.get(TemporalCriteriaRecord_.dateValue).extract(LocalDateField.YEAR), typed)
                    .where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
            Projection result = handler.createQuery(cq).getSingleResult();
            assertEquals(Integer.valueOf(2021), result.year);
            assertEquals("one", result.text);
            Projection jpql = handler.createQuery("SELECT EXTRACT(YEAR FROM r.stampValue), CASE r.text WHEN 'first' THEN 'one' ELSE 'other' END FROM TemporalCriteriaRecord r WHERE r.id = 1", Projection.class).getSingleResult();
            assertEquals(result.year, jpql.year);
            assertEquals(result.text, jpql.text);
        });
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Expression<String> oldSimple = q.cb.<Integer, String>selectCase(q.root.get(TemporalCriteriaRecord_.id)).when(1, "one").otherwise("other");
            Expression<String> oldSearched = q.cb.<String>selectCase().when(q.cb.equal(q.root.get(TemporalCriteriaRecord_.id), 1), "one").otherwise("other");
            assertValues(q.row(oldSimple, oldSearched), "one", "one");
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

    public static class Projection {
        public final Integer year;
        public final String text;

        public Projection(Integer year, String text) {
            this.year = year;
            this.text = text;
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testAllNullCaseContextsAndVariableWidthRebinding() {
        try (EntityManager em = emf.createEntityManager()) {
            for (Class type : List.of(String.class, Long.class, Double.class, BigDecimal.class,
                    LocalDate.class, LocalTime.class, LocalDateTime.class, Number.class, Object.class)) {
                Query q = new Query(em);
                Expression<?> simple = q.cb.selectCase(q.root.get(TemporalCriteriaRecord_.id), type)
                        .when(1, (Object) null).otherwise((Object) null);
                Expression<?> searched = q.cb.selectCase(type).when(q.cb.literal(true), (Object) null)
                        .otherwise(q.cb.nullLiteral(type));
                assertEquals(type, simple.getJavaType());
                assertEquals(type, searched.getJavaType());
                assertValues(q.row(simple, searched), null, null);
            }
            Query q = new Query(em);
            ParameterExpression<String> text = q.cb.parameter(String.class, "text");
            Expression<String> simple = q.root.get(TemporalCriteriaRecord_.id).selectCase(String.class)
                    .when(1, text).otherwise("other");
            Expression<String> searched = q.cb.selectCase(String.class).when(q.cb.literal(true), text)
                    .otherwise((String) null);
            q.cq.multiselect(simple, searched);
            TypedQuery<Tuple> query = em.createQuery(q.cq);
            for (String value : Arrays.asList(null, "text", "a much longer value ", "x  ", null)) {
                assertValues(query.setParameter(text, value).getSingleResult(), value, value);
            }
        }
    }

    @Test
    public void testDetachedExpressionsSerializeAndExecuteWithParameterIdentity() throws Exception {
        CriteriaBuilder cb = new CriteriaBuilderImpl(null);
        ParameterExpression<Integer> selector = cb.parameter(Integer.class, "serializedSelector");
        ParameterExpression<Integer> match = cb.parameter(Integer.class, "serializedMatch");
        ParameterExpression<LocalDateTime> result = cb.parameter(LocalDateTime.class, "serializedResult");
        ParameterExpression<LocalDateTime> otherwise = cb.parameter(LocalDateTime.class, "serializedElse");
        Expression<LocalDateTime> searched = cb.selectCase(LocalDateTime.class)
                .when(cb.equal(selector, match), result).otherwise(otherwise);
        Expression<LocalDateTime> simple = cb.selectCase(selector, LocalDateTime.class).when(match, result).otherwise(otherwise);
        TemporalExpression<LocalDateTime> temporal = cb.temporalLiteral(FIRST).coalesce(result).nullif(SECOND);
        searched.alias("searchedCase");
        simple.alias("simpleCase");
        temporal.alias("temporalValue");
        List<Expression<?>> originals = List.of(searched, temporal, simple, selector, match, result, otherwise);
        List<org.eclipse.persistence.expressions.Expression> originalNodes = originals.stream()
                .map(value -> ((InternalSelection) value).getCurrentNode()).toList();
        List<org.eclipse.persistence.expressions.ExpressionBuilder> originalBuilders = originalNodes.stream()
                .map(org.eclipse.persistence.expressions.Expression::getBuilder).toList();
        List<String> originalAliases = originals.stream().map(Expression::getAlias).toList();
        List<? extends Class<?>> originalTypes = originals.stream().map(Expression::getJavaType).toList();
        Object[] restored = roundTrip(originals.toArray());
        @SuppressWarnings("unchecked")
        Expression<LocalDateTime> restoredSearched = (Expression<LocalDateTime>) restored[0];
        @SuppressWarnings("unchecked")
        TemporalExpression<LocalDateTime> restoredTemporal = (TemporalExpression<LocalDateTime>) restored[1];
        @SuppressWarnings("unchecked")
        Expression<LocalDateTime> restoredSimple = (Expression<LocalDateTime>) restored[2];
        @SuppressWarnings("unchecked")
        ParameterExpression<Integer> restoredSelector = (ParameterExpression<Integer>) restored[3];
        @SuppressWarnings("unchecked")
        ParameterExpression<Integer> restoredMatch = (ParameterExpression<Integer>) restored[4];
        @SuppressWarnings("unchecked")
        ParameterExpression<LocalDateTime> restoredResult = (ParameterExpression<LocalDateTime>) restored[5];
        @SuppressWarnings("unchecked")
        ParameterExpression<LocalDateTime> restoredElse = (ParameterExpression<LocalDateTime>) restored[6];
        List<ParameterExpression<?>> restoredHandles = List.of(restoredSelector, restoredMatch, restoredResult, restoredElse);
        List<ParameterExpression<?>> originalHandles = List.of(selector, match, result, otherwise);
        for (int index = 0; index < restored.length; index++) {
            Expression<?> value = (Expression<?>) restored[index];
            assertNotSame(originals.get(index), value);
            assertEquals(originalAliases.get(index), value.getAlias());
            assertEquals(originalTypes.get(index), value.getJavaType());
        }
        for (int index = 0; index < restoredHandles.size(); index++) {
            assertEquals(originalHandles.get(index).getName(), restoredHandles.get(index).getName());
            assertEquals(originalHandles.get(index).getParameterType(), restoredHandles.get(index).getParameterType());
        }
        restoredSimple.alias("restoredSimple");
        withHandlers(handler -> {
            CriteriaBuilder live = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = live.createTupleQuery();
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            Expression<LocalDateTime> composed = live.selectCase(LocalDateTime.class)
                    .when(live.literal(true), restoredSimple).otherwise((LocalDateTime) null);
            cq.multiselect(restoredSearched, restoredSimple, composed, restoredTemporal,
                    restoredTemporal.extract(LocalDateTimeField.SECOND),
                    restoredTemporal.nullif(FIRST).coalesce(SECOND).extract(LocalDateTimeField.SECOND))
                    .where(live.equal(root.get(TemporalCriteriaRecord_.id), 1));
            assertEquals(Set.copyOf(restoredHandles), cq.getParameters());
            for (ParameterExpression<?> handle : restoredHandles) {
                assertSame(handle, cq.getParameters().stream().filter(parameter -> parameter.getName().equals(handle.getName())).findFirst().orElseThrow());
            }
            TypedQuery<Tuple> query = handler.createQuery(cq);
            Object[][] bindings = {{null, 1, null, null}, {1, 1, FIRST, SECOND}, {2, 1, SECOND, FIRST},
                    {null, null, FIRST, null}, {1, 1, null, SECOND}, {2, 1, null, null}};
            for (Object[] binding : bindings) {
                LocalDateTime expected = binding[0] != null && binding[0].equals(binding[1])
                        ? (LocalDateTime) binding[2] : (LocalDateTime) binding[3];
                query.setParameter(restoredSelector, (Integer) binding[0]).setParameter(restoredMatch, (Integer) binding[1])
                        .setParameter(restoredResult, (LocalDateTime) binding[2]).setParameter(restoredElse, (LocalDateTime) binding[3]);
                assertQueryValues(query, expected, expected, expected, FIRST, 56.125d, 58.25d);
            }
        });
        assertEquals(Set.copyOf(originalHandles), cb.createTupleQuery().multiselect(searched, simple, temporal).getParameters());
        for (int index = 0; index < originals.size(); index++) {
            assertSame(originalNodes.get(index), ((InternalSelection) originals.get(index)).getCurrentNode());
            assertSame(originalBuilders.get(index), originalNodes.get(index).getBuilder());
            assertEquals(originalAliases.get(index), originals.get(index).getAlias());
            assertEquals(originalTypes.get(index), originals.get(index).getJavaType());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(T value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) input.readObject();
        }
    }

    @Test
    public void testMappedSimpleCaseTemporalNullRebinding() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            ParameterExpression<LocalTime> parameter = cb.parameter(LocalTime.class, "mappedCaseTime");
            Expression<LocalTime> value = cb.selectCase(root.get(TemporalCriteriaRecord_.id), LocalTime.class)
                    .when(1, parameter).otherwise((LocalTime) null);
            cq.multiselect(value, cb.extract(LocalTimeField.SECOND, value))
                    .where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<Tuple> query = handler.createQuery(cq);
            // Inspect query metadata only after rebinding: getDatabaseQuery() clones
            // a shared query and can hide stale JDBC field metadata.
            for (LocalTime binding : Arrays.asList(null, LocalTime.of(12, 34, 56), LocalTime.of(13, 45, 57), null)) {
                assertQueryValues(query.setParameter(parameter, binding), binding, seconds(binding));
            }
            assertSame(parameter, query.getParameters().iterator().next());
        });
    }

    @Test
    public void testFractionalLocalTimeValuesNullFunctionsAndProjections() {
        for (LocalTime first : fractionalTimeInputs()) {
            assertFractionalLocalTimeValuesNullFunctionsAndProjections(first, first.withNano(first.getNano() == 125000000 ? 250000000 : 123456788));
        }
    }

    private void assertFractionalLocalTimeValuesNullFunctionsAndProjections(LocalTime first, LocalTime second) {
        boolean derby;
        try (EntityManager em = emf.createEntityManager()) {
            derby = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform().isDerby();
        }
        // The Derby expectations record an inherited binder restriction, not
        // fractional-expression conformance. Both original inputs remain intact.
        LocalTime expectedFirst = derby ? first.withNano(0) : first;
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            ParameterExpression<LocalTime> parameter = cb.parameter(LocalTime.class, "fractionalTime");
            parameter.alias("originalTime");
            TemporalExpression<LocalTime> literal = cb.temporalLiteral(first);
            literal.alias("originalLiteral");
            Object originalNode = ((InternalSelection) literal).getCurrentNode();
            TemporalExpression<LocalTime> nullable = literal.nullif(parameter);
            TemporalExpression<LocalTime> dynamic = literal.nullif(first).coalesce(parameter);
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            Expression<LocalTime> searched = cb.selectCase(LocalTime.class)
                    .when(cb.equal(root.get(TemporalCriteriaRecord_.id), 1), dynamic).otherwise(literal);
            Expression<LocalTime> simple = cb.selectCase(root.get(TemporalCriteriaRecord_.id), LocalTime.class)
                    .when(1, dynamic).otherwise(literal);
            cq.multiselect(literal, dynamic, nullable, nullable.coalesce(second),
                    cb.extract(LocalTimeField.SECOND, parameter), literal.extract(LocalTimeField.SECOND),
                    nullable.extract(LocalTimeField.SECOND), nullable.coalesce(second).extract(LocalTimeField.SECOND),
                    cb.selectCase(Integer.class).when(cb.equal(literal, parameter), 1).otherwise(0),
                    cb.selectCase(Integer.class).when(cb.lessThan(literal, parameter), 1).otherwise(0), searched, simple)
                    .where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<Tuple> query = handler.createQuery(cq);
            CriteriaQuery<LocalTime> scalar = cb.createQuery(LocalTime.class);
            scalar.select(nullable).where(cb.equal(scalar.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<LocalTime> scalarQuery = handler.createQuery(scalar);
            CriteriaQuery<TimeProjection> constructor = cb.createQuery(TimeProjection.class);
            constructor.multiselect(nullable)
                    .where(cb.equal(constructor.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<TimeProjection> constructorQuery = handler.createQuery(constructor);
            CriteriaQuery<Object[]> array = cb.createQuery(Object[].class);
            array.multiselect(nullable, nullable.extract(LocalTimeField.SECOND))
                    .where(cb.equal(array.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1)).distinct(true);
            TypedQuery<Object[]> arrayQuery = handler.createQuery(array);
            for (LocalTime binding : Arrays.asList(null, first, second, null, second, first)) {
                LocalTime expectedBinding = binding == null ? null : derby ? binding.withNano(0) : binding;
                boolean equal = expectedFirst.equals(expectedBinding);
                LocalTime expectedNullIf = equal ? null : expectedFirst;
                LocalTime expectedCoalesce = equal ? derby ? second.withNano(0) : second : expectedFirst;
                assertQueryValues(query.setParameter(parameter, binding), expectedFirst, expectedBinding, expectedNullIf,
                        expectedCoalesce, seconds(expectedBinding), seconds(expectedFirst), seconds(expectedNullIf),
                        seconds(expectedCoalesce), equal ? 1 : 0,
                        expectedBinding != null && expectedFirst.isBefore(expectedBinding) ? 1 : 0,
                        expectedBinding, expectedBinding);
                assertEquals(expectedNullIf, scalarQuery.setParameter(parameter, binding).getSingleResult());
                assertEquals(expectedNullIf, constructorQuery.setParameter(parameter, binding).getSingleResult().time);
                Object[] values = arrayQuery.setParameter(parameter, binding).getSingleResult();
                assertEquals(expectedNullIf, values[0]);
                assertEquals(seconds(expectedNullIf), values[1]);
            }
            assertSame(parameter, query.getParameters().iterator().next());
            assertEquals(LocalTime.class, parameter.getParameterType());
            assertEquals("originalTime", parameter.getAlias());
            assertSame(originalNode, ((InternalSelection) literal).getCurrentNode());
            assertEquals("originalLiteral", literal.getAlias());
            assertEquals(LocalTime.class, literal.getJavaType());
        });
    }

    @Test
    public void testFractionalLocalTimeRawCaseArms() {
        for (LocalTime first : fractionalTimeInputs()) {
            assertFractionalLocalTimeRawCaseArms(first, first.withNano(first.getNano() == 125000000 ? 250000000 : 123456788));
        }
    }

    private void assertFractionalLocalTimeRawCaseArms(LocalTime first, LocalTime second) {
        try (EntityManager em = emf.createEntityManager()) {
            boolean derby = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform().isDerby();
            LocalTime expectedFirst = derby ? first.withNano(0) : first;
            Query q = new Query(em);
            ParameterExpression<LocalTime> parameter = q.cb.parameter(LocalTime.class, "caseTime");
            Expression<LocalTime> searched = q.cb.selectCase(LocalTime.class)
                    .when(q.cb.equal(q.root.get(TemporalCriteriaRecord_.id), 1), parameter).otherwise((LocalTime) null);
            Expression<LocalTime> simple = q.cb.selectCase(q.root.get(TemporalCriteriaRecord_.id), LocalTime.class)
                    .when(1, first).otherwise(parameter);
            Expression<String> parameterSelector = q.cb.selectCase(parameter, String.class)
                    .when(first, "matched").otherwise("other");
            Expression<String> valueSelector = q.cb.selectCase(q.cb.temporalLiteral(first), String.class)
                    .when(parameter, "matched").otherwise("other");
            q.cq.multiselect(searched, simple, q.cb.extract(LocalTimeField.SECOND, searched),
                    q.cb.extract(LocalTimeField.SECOND, simple), parameterSelector, valueSelector);
            TypedQuery<Tuple> query = em.createQuery(q.cq);
            for (LocalTime binding : Arrays.asList(null, first, second, null)) {
                LocalTime expectedBinding = binding == null ? null : derby ? binding.withNano(0) : binding;
                assertQueryValues(query.setParameter(parameter, binding), expectedBinding, expectedFirst,
                        seconds(expectedBinding), seconds(expectedFirst),
                        expectedFirst.equals(expectedBinding) ? "matched" : "other",
                        expectedFirst.equals(expectedBinding) ? "matched" : "other");
            }
            assertSame(parameter, query.getParameters().iterator().next());
        }
    }

    @Test
    public void testMappedTimeConsumersWithFractionalLocalTimeValues() {
        for (LocalTime first : fractionalTimeInputs()) {
            assertMappedTimeConsumersWithFractionalLocalTimeValues(first, first.withNano(first.getNano() == 125000000 ? 250000000 : 123456788));
        }
    }

    private void assertMappedTimeConsumersWithFractionalLocalTimeValues(LocalTime first, LocalTime second) {
        try (EntityManager em = emf.createEntityManager()) {
            boolean derby = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform().isDerby();
            LocalTime expectedFirst = derby ? first.withNano(0) : first;
            Query q = new Query(em);
            ParameterExpression<LocalTime> parameter = q.cb.parameter(LocalTime.class, "mappedTime");
            TemporalExpression<LocalTime> mapped = q.root.get(TemporalCriteriaRecord_.timeValue);
            TemporalExpression<LocalTime> literal = q.cb.temporalLiteral(first);
            q.cq.multiselect(mapped.nullif(parameter), mapped.coalesce(parameter), literal.nullif(mapped), literal.coalesce(mapped),
                    mapped.nullif(parameter).extract(LocalTimeField.SECOND), mapped.coalesce(parameter).extract(LocalTimeField.SECOND),
                    literal.nullif(mapped).extract(LocalTimeField.SECOND),
                    q.cb.selectCase(Integer.class).when(q.cb.equal(mapped, literal), 1).otherwise(0),
                    q.cb.selectCase(Integer.class).when(q.cb.lessThan(mapped, literal), 1).otherwise(0));
            LocalTime stored = first.withNano(0);
            for (int id : List.of(1, 4)) {
                q.id(id);
                TypedQuery<Tuple> query = em.createQuery(q.cq);
                assertSame(parameter, query.getParameters().iterator().next());
                for (LocalTime binding : Arrays.asList(null, first, second, stored, null)) {
                    LocalTime expectedBinding = binding == null ? null : derby ? binding.withNano(0) : binding;
                    LocalTime expectedMapped = id == 1 ? stored : null;
                    LocalTime expectedNullIf = stored.equals(expectedBinding) ? null : expectedMapped;
                    LocalTime expectedCoalesce = id == 1 ? stored : expectedBinding;
                    LocalTime expectedLiteralNullIf = expectedFirst.equals(expectedMapped) ? null : expectedFirst;
                    assertQueryValues(query.setParameter(parameter, binding), expectedNullIf, expectedCoalesce,
                            expectedLiteralNullIf, expectedFirst, seconds(expectedNullIf), seconds(expectedCoalesce),
                            seconds(expectedLiteralNullIf), expectedFirst.equals(expectedMapped) ? 1 : 0,
                            expectedMapped != null && expectedMapped.isBefore(expectedFirst) ? 1 : 0);
                }
            }
        }
    }

    @Test
    public void testTemporalComparisonOverloadsAndMappedConverterContext() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            ParameterExpression<LocalTime> clock = cb.parameter(LocalTime.class, "comparisonTime");
            TemporalExpression<LocalTime> mapped = root.get(TemporalCriteriaRecord_.convertedTime);
            LocalTime value = FIRST.toLocalTime().withNano(0);
            TemporalExpression<LocalTime> literal = cb.temporalLiteral(value);
            List<jakarta.persistence.criteria.Predicate> predicates = List.of(
                    cb.equal(mapped, clock), cb.equal(clock, mapped), cb.equal(mapped, value), mapped.equalTo(value),
                    cb.notEqual(mapped, clock), cb.notEqual(mapped, value), mapped.notEqualTo(value),
                    cb.greaterThan(mapped, clock), cb.greaterThan(mapped, value), mapped.greaterThan(clock),
                    cb.greaterThanOrEqualTo(mapped, clock), cb.greaterThanOrEqualTo(mapped, value),
                    cb.lessThan(mapped, clock), cb.lessThan(mapped, value), mapped.lessThan(clock),
                    cb.lessThanOrEqualTo(mapped, clock), cb.lessThanOrEqualTo(mapped, value),
                    cb.between(mapped, clock, clock), cb.between(mapped, value, value), cb.between(value, mapped, mapped),
                    mapped.between(clock, clock), mapped.before(clock), mapped.after(clock),
                    cb.equal(mapped, (Object) null), cb.equal(mapped, cb.nullLiteral(LocalTime.class)),
                    cb.notEqual(mapped, (Object) null), cb.notEqual(mapped, cb.nullLiteral(LocalTime.class)),
                    cb.equal(literal, clock), literal.equalTo(clock), literal.notEqualTo(clock));
            List<Selection<?>> selections = new java.util.ArrayList<>();
            selections.add(mapped);
            for (var predicate : predicates) {
                selections.add(cb.selectCase(Integer.class).when(predicate, 1).otherwise(0));
            }
            cq.multiselect(selections).where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<Tuple> query = handler.createQuery(cq);
            for (LocalTime binding : Arrays.asList(null, value, value.plusSeconds(1), null)) {
                boolean eq = value.equals(binding);
                boolean ne = binding != null && !eq;
                boolean lt = binding != null && value.isBefore(binding);
                List<Integer> expected = List.of(eq ? 1 : 0, eq ? 1 : 0, 1, 1,
                        ne ? 1 : 0, 0, 0, 0, 0, 0, eq ? 1 : 0, 1,
                        lt ? 1 : 0, 0, lt ? 1 : 0, binding == null ? 0 : 1, 1,
                        eq ? 1 : 0, 1, 1, eq ? 1 : 0, lt ? 1 : 0, 0,
                        0, 0, 1, 1, eq ? 1 : 0, eq ? 1 : 0, ne ? 1 : 0);
                Tuple row = query.setParameter(clock, binding).getSingleResult();
                assertEquals(value, row.get(0));
                for (int i = 0; i < expected.size(); i++) {
                    assertEquals("comparison " + i + " binding " + binding, expected.get(i), row.get(i + 1));
                }
            }
            assertSame(clock, query.getParameters().iterator().next());
        });
    }

    @Test
    public void testCastWrappedCaseCoalesceParametersAndResults() {
        String sqlType;
        try (EntityManager em = emf.createEntityManager()) {
            // CAST takes a native SQL type; MySQL accepts CHAR rather than VARCHAR.
            sqlType = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform().isMySQL()
                    ? "CHAR(32)" : "VARCHAR(32)";
        }
        withHandlers(handler -> {
            for (String receiver : List.of("case r.id when :selector then r.text else :missing end",
                    "case when r.id=1 then case r.id when :selector then r.text else :missing end else :missing end")) {
                TypedQuery<String> query = handler.createQuery("select coalesce(cast(" + receiver
                        + " as " + sqlType + "), :fallback) from TemporalCriteriaRecord r where r.id=1", String.class);
                for (int selector : List.of(1, 2, 1)) {
                    for (String fallback : Arrays.asList(null, "fallback", "longer fallback", null)) {
                        String result = query.setParameter("selector", selector).setParameter("missing", null)
                                .setParameter("fallback", fallback).getSingleResult();
                        assertEquals(selector == 1 ? "first" : fallback, result);
                    }
                }
            }
        });
    }

    @Test
    public void testDerivedNativeTimeAndContextualTimeConsumers() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            ParameterExpression<LocalTime> clock = q.cb.parameter(LocalTime.class, "derivedTime");
            TemporalExpression<LocalTime> mapped = q.root.get(TemporalCriteriaRecord_.timeValue);
            Expression<LocalTime> extracted = q.cb.extract(LocalDateTimeField.TIME, q.root.get(TemporalCriteriaRecord_.stampValue));
            Expression<LocalTime> legacyCase = q.cb.<LocalTime>selectCase()
                    .when(q.cb.equal(q.root.get(TemporalCriteriaRecord_.id), 1), mapped).otherwise(mapped);
            Expression<LocalTime> legacySimple = q.cb.<Integer, LocalTime>selectCase(q.root.get(TemporalCriteriaRecord_.id))
                    .when(1, mapped).otherwise(mapped);
            Expression<LocalTime> legacyCoalesce = q.cb.<LocalTime>coalesce().value(mapped).value(mapped);
            List<Selection<?>> selections = new java.util.ArrayList<>();
            for (Expression<LocalTime> source : List.of(extracted, legacyCase, legacySimple, legacyCoalesce)) {
                selections.add(q.cb.extract(LocalTimeField.SECOND, q.cb.coalesce(source, clock)));
                selections.add(q.cb.extract(LocalTimeField.SECOND, q.cb.nullif(source, clock)));
                selections.add(q.cb.selectCase(Integer.class).when(q.cb.equal(source, clock), 1).otherwise(0));
                selections.add(q.cb.selectCase(Integer.class).when(q.cb.equal(source, source), 1).otherwise(0));
            }
            q.cq.multiselect(selections);
            TypedQuery<Tuple> query = em.createQuery(q.cq);
            LocalTime stored = FIRST.toLocalTime().withNano(0);
            LocalTime extractedValue = nativeTime(em, FIRST);
            boolean derby = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform().isDerby();
            for (LocalTime binding : Arrays.asList(null, stored, FIRST.toLocalTime(), null)) {
                LocalTime effective = binding == null ? null : derby ? binding.withNano(0) : binding;
                Tuple row = query.setParameter(clock, binding).getSingleResult();
                for (int i = 0; i < 4; i++) {
                    LocalTime source = i == 0 ? extractedValue : stored;
                    assertEquals(seconds(source), row.get(i * 4));
                    assertEquals(source.equals(effective) ? null : seconds(source), row.get(i * 4 + 1));
                    assertEquals(source.equals(effective) ? 1 : 0, row.get(i * 4 + 2));
                    assertEquals(1, row.get(i * 4 + 3));
                }
            }
        }
        withHandlers(handler -> {
            try (EntityManager em = emf.createEntityManager()) {
                Query q = new Query(em);
                ParameterExpression<LocalDateTime> stamp = q.cb.parameter(LocalDateTime.class, "clockSource");
                ParameterExpression<LocalTime> clock = q.cb.parameter(LocalTime.class, "clockMatch");
                Expression<LocalTime> extracted = q.cb.extract(LocalDateTimeField.TIME, stamp);
                q.cq.multiselect(extracted, q.cb.extract(LocalTimeField.SECOND, extracted),
                        q.cb.nullif(extracted, clock), q.cb.coalesce(extracted, clock),
                        q.cb.selectCase(Integer.class).when(q.cb.equal(extracted, clock), 1).otherwise(0));
                TypedQuery<Tuple> query = handler.createQuery(q.cq);
                assertSame(stamp, query.getParameter("clockSource", LocalDateTime.class));
                assertSame(clock, query.getParameter("clockMatch", LocalTime.class));
                boolean derby = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform().isDerby();
                for (LocalDateTime input : Arrays.asList(null, FIRST, FIRST.withYear(1960), FIRST.withNano(250000000), null)) {
                    LocalTime expected = input == null ? null : nativeTime(em, input);
                    for (LocalTime binding : Arrays.asList(null, FIRST.toLocalTime(), FIRST.toLocalTime().withNano(250000000), null)) {
                        LocalTime effective = binding == null ? null : derby ? binding.withNano(0) : binding;
                        boolean equal = expected != null && expected.equals(effective);
                        Tuple row = query.setParameter(stamp, input).setParameter(clock, binding).getSingleResult();
                        assertValues(row, expected, seconds(expected), equal ? null : expected,
                                expected == null ? effective : expected, equal ? 1 : 0);
                    }
                }
            }
        });
    }

    @Test
    public void testRawTemporalCaseRebindingAcrossProjectionFamilies() {
        for (LocalTime value : fractionalTimeInputs()) {
            withHandlers(handler -> {
                CriteriaBuilder cb = emf.getCriteriaBuilder();
                ParameterExpression<LocalTime> clock = cb.parameter(LocalTime.class, "projectedClock");
                Expression<LocalTime> searched = cb.selectCase(LocalTime.class).when(cb.literal(true), clock)
                        .otherwise((LocalTime) null);
                Expression<LocalTime> simple = cb.selectCase(cb.literal(1), LocalTime.class).when(1, clock)
                        .otherwise((LocalTime) null);
                for (Expression<LocalTime> expression : List.of(searched, simple)) {
                    CriteriaQuery<LocalTime> scalar = cb.createQuery(LocalTime.class);
                    scalar.select(expression).where(cb.equal(scalar.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
                    CriteriaQuery<TimeProjection> constructor = cb.createQuery(TimeProjection.class);
                    constructor.multiselect(expression).where(cb.equal(constructor.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
                    CriteriaQuery<Object[]> array = cb.createQuery(Object[].class);
                    array.multiselect(expression, cb.extract(LocalTimeField.SECOND, expression))
                            .where(cb.equal(array.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
                    TypedQuery<LocalTime> scalarQuery = handler.createQuery(scalar);
                    TypedQuery<TimeProjection> constructorQuery = handler.createQuery(constructor);
                    TypedQuery<Object[]> arrayQuery = handler.createQuery(array);
                    boolean derby;
                    try (EntityManager em = emf.createEntityManager()) {
                        derby = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform().isDerby();
                    }
                    for (LocalTime binding : Arrays.asList(null, value, value.plusSeconds(1), null)) {
                        LocalTime expected = binding == null ? null : derby ? binding.withNano(0) : binding;
                        assertEquals(expected, scalarQuery.setParameter(clock, binding).getSingleResult());
                        assertEquals(expected, constructorQuery.setParameter(clock, binding).getSingleResult().time);
                        Object[] row = arrayQuery.setParameter(clock, binding).getSingleResult();
                        assertEquals(expected, row[0]);
                        assertEquals(seconds(expected), row[1]);
                    }
                }
            });
        }
    }

    public static class TimeProjection {
        public final LocalTime time;

        public TimeProjection(LocalTime time) {
            this.time = time;
        }
    }

    private List<LocalTime> fractionalTimeInputs() {
        LocalTime first = LocalTime.of(12, 34, 56, 125000000);
        try (EntityManager em = emf.createEntityManager()) {
            DatabasePlatform platform = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform();
            // Both platforms retain nine digits in standalone LocalTime values.
            // Also exercise unequal operands differing only after the sixth digit.
            return platform instanceof H2Platform || platform.isDB2()
                    ? List.of(first, first.withNano(123456789)) : List.of(first);
        }
    }

    private static Double seconds(LocalTime time) {
        return time == null ? null : time.getSecond() + time.getNano() / 1_000_000_000d;
    }

    @Test
    public void testTemporalValueContextsPreserveAvailablePrecision() {
        try (EntityManager em = emf.createEntityManager()) {
            DatabasePlatform platform = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform();
            // H2 supports all nine digits. Other timestamp fixtures use exact milliseconds.
            LocalDateTime stamp = FIRST.withNano(platform instanceof H2Platform ? 123456789 : 125000000);
            LocalTime time = stamp.toLocalTime();
            // Derby's pre-existing LocalTime binder loses fractions. Keep the input
            // intact and identify its observed restriction separately from DB2.
            LocalTime expectedTime = platform instanceof DerbyPlatform ? time.withNano(0) : time;
            Query q = new Query(em);
            ParameterExpression<LocalDateTime> parameter = q.cb.parameter(LocalDateTime.class, "preciseStamp");
            ParameterExpression<LocalTime> timeParameter = q.cb.parameter(LocalTime.class, "preciseTime");
            TemporalExpression<LocalDateTime> literal = q.cb.temporalLiteral(stamp);
            Expression<LocalDateTime> typed = q.cb.selectCase(LocalDateTime.class)
                    .when(q.cb.literal(true), parameter).otherwise((LocalDateTime) null);
            q.cq.multiselect(literal, q.cb.temporalLiteral(time), typed,
                    literal.coalesce(parameter).nullif(SECOND), q.cb.extract(LocalDateTimeField.SECOND, parameter),
                    q.cb.extract(LocalTimeField.SECOND, timeParameter), literal.extract(LocalDateTimeField.SECOND),
                    q.cb.temporalLiteral(time).extract(LocalTimeField.SECOND));
            TypedQuery<Tuple> query = em.createQuery(q.cq);
            for (LocalDateTime binding : Arrays.asList(null, stamp, null, stamp)) {
                assertQueryValues(query.setParameter(parameter, binding).setParameter(timeParameter, binding == null ? null : time),
                        stamp, expectedTime, binding, stamp,
                        binding == null ? null : stamp.getSecond() + stamp.getNano() / 1_000_000_000d,
                        binding == null ? null : expectedTime.getSecond() + expectedTime.getNano() / 1_000_000_000d,
                        stamp.getSecond() + stamp.getNano() / 1_000_000_000d,
                        expectedTime.getSecond() + expectedTime.getNano() / 1_000_000_000d);
            }
            LocalTime storedTime = platform instanceof H2Platform ? time : time.withNano(0);
            emf.runInTransaction(writer -> {
                TemporalCriteriaRecord record = new TemporalCriteriaRecord(5, FIRST, "precision", 50);
                record.timeValue = storedTime;
                writer.persist(record);
            });
            em.clear();
            assertEquals(storedTime, em.find(TemporalCriteriaRecord.class, 5).timeValue);
            Query stored = new Query(em);
            stored.id(5);
            TemporalExpression<LocalTime> storedExpression = stored.root.get(TemporalCriteriaRecord_.timeValue);
            assertValues(stored.row(storedExpression, storedExpression.extract(LocalTimeField.SECOND)),
                    storedTime, storedTime.getSecond() + storedTime.getNano() / 1_000_000_000d);
        }
    }

    @Test
    public void testSubqueryCreatedBeforeParentRootRetainsTypedResults() {
        withHandlers(handler -> {
            for (boolean caseSelection : List.of(false, true)) {
                CriteriaBuilder cb = emf.getCriteriaBuilder();
                CriteriaQuery<Double> cq = cb.createQuery(Double.class);
                Subquery<Double> subquery = cq.subquery(Double.class);
                Root<TemporalCriteriaRecord> inner = subquery.from(TemporalCriteriaRecord.class);
                Expression<Double> second = cb.extract(LocalDateTimeField.SECOND, inner.get(TemporalCriteriaRecord_.stampValue));
                Expression<Double> selected = caseSelection ? cb.selectCase(Double.class)
                        .when(cb.equal(inner.get(TemporalCriteriaRecord_.id), 1), second).otherwise(0d) : second;
                subquery.select(selected).where(cb.equal(inner.get(TemporalCriteriaRecord_.id), 1));
                Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
                cq.select(cb.selectCase(Double.class).when(cb.greaterThan(subquery, 56d), 1.5d).otherwise(0d))
                        .where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
                var original = (org.eclipse.persistence.internal.expressions.FunctionExpression) ((InternalSelection) selected).getCurrentNode();
                assertEquals(Double.class, original.getResultType());
                TypedQuery<Double> query = handler.createQuery(cq);
                assertEquals(Double.valueOf(1.5d), query.getSingleResult());
                var report = (org.eclipse.persistence.queries.ReportQuery) query.unwrap(JpaQuery.class).getDatabaseQuery();
                List<org.eclipse.persistence.internal.expressions.SubSelectExpression> subselects = new java.util.ArrayList<>();
                new org.eclipse.persistence.internal.expressions.ExpressionIterator<Void>() {
                    @Override
                    public void iterate(org.eclipse.persistence.expressions.Expression expression) {
                        if (expression instanceof org.eclipse.persistence.internal.expressions.SubSelectExpression subselect) {
                            subselects.add(subselect);
                        }
                    }
                }.iterateOn(report.getItems().getFirst().getAttributeExpression());
                assertEquals(1, subselects.size());
                var prepared = (org.eclipse.persistence.internal.expressions.FunctionExpression)
                        subselects.getFirst().getSubQuery().getItems().getFirst().getAttributeExpression();
                assertEquals("Prepared subquery selection case=" + caseSelection, Double.class, prepared.getResultType());
                assertEquals(Double.class, original.getResultType());
            }
        });
    }

    @Test
    public void testLegacyCreatedQueryRetainsCaseValuesAfterMutation() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Case<String> value = q.cb.<String>selectCase().when(q.cb.equal(q.root.get(TemporalCriteriaRecord_.id), 2), "then");
            value.otherwise("before");
            q.cq.multiselect(value);
            TypedQuery<Tuple> original = em.createQuery(q.cq);
            value.otherwise("after");
            assertValues(original.getSingleResult(), "before");
            assertValues(em.createQuery(q.cq).getSingleResult(), "after");
        }
    }

    @Test
    public void testCreatedQueryRetainsCaseValuesAndTypeAfterCriteriaMutation() {
        withHandlers(handler -> {
            for (boolean simple : List.of(false, true)) {
                CriteriaBuilder cb = emf.getCriteriaBuilder();
                CriteriaQuery<Tuple> cq = cb.createTupleQuery();
                Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
                cq.where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
                Case<Number> searched = cb.selectCase(Number.class).when(cb.literal(false), 1);
                SimpleCase<Integer, Number> selected = cb.selectCase(cb.literal(0), Number.class).when(1, 1);
                Expression<Number> value = simple ? selected.otherwise(2) : searched.otherwise(2);
                value.alias("originalCase");
                Expression<Number> sum = cb.sum(value, 1);
                sum.alias("originalSum");
                cq.multiselect(value, sum);
                TypedQuery<Tuple> original = handler.createQuery(cq);
                if (simple) {
                    selected.otherwise(2.5d);
                } else {
                    searched.otherwise(2.5d);
                }
                value.alias("changedCase");
                assertEquals(Double.class, value.getJavaType());
                Tuple first = original.getSingleResult();
                assertValues(first, 2, 3);
                assertEquals(Integer.class, first.getElements().get(0).getJavaType());
                assertEquals(Integer.class, first.getElements().get(1).getJavaType());
                assertEquals(2, first.get(value));
                assertEquals(3, first.get(sum));
                assertEquals(2, first.get("originalCase"));
                assertEquals("originalCase", first.getElements().get(0).getAlias());
                assertEquals(2, first.get(first.getElements().get(0)));
                assertValues(original.getSingleResult(), 2, 3);
                Tuple changed = handler.createQuery(cq).getSingleResult();
                assertValues(changed, 2.5d, 3.5d);
                assertEquals(Double.class, sum.getJavaType());
                assertEquals(Double.class, changed.getElements().get(1).getJavaType());
                assertEquals("changedCase", changed.getElements().get(0).getAlias());
                assertEquals("originalSum", changed.getElements().get(1).getAlias());
                assertValues(original.getSingleResult(), 2, 3);
            }
        });
    }

    @Test
    public void testMutableNumericCompositionsAcrossProjectionFamilies() throws Exception {
        CriteriaBuilderImpl detached = new CriteriaBuilderImpl(null);
        ParameterExpression<Integer> parameter = detached.parameter(Integer.class, "numericMatch");
        Case<Number> choice = detached.selectCase(Number.class).when(detached.literal(false), 1);
        choice.otherwise(2);
        Expression<Number> nested = detached.abs(detached.neg(detached.sum(choice, 1)));
        nested.alias("nestedNumber");
        Object[] restored = roundTrip(new Object[]{choice, parameter, nested});
        @SuppressWarnings("unchecked")
        Case<Number> restoredChoice = (Case<Number>) restored[0];
        @SuppressWarnings("unchecked")
        ParameterExpression<Integer> restoredParameter = (ParameterExpression<Integer>) restored[1];
        @SuppressWarnings("unchecked")
        Expression<Number> restoredNested = (Expression<Number>) restored[2];
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            // Reset the restored CASE for each handler's before/after pair.
            restoredChoice.otherwise(2);
            Expression<Number> nullable = cb.nullif(restoredChoice, restoredParameter);
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            cq.multiselect(restoredNested, cb.prod(restoredChoice, 2), cb.prod(2, restoredChoice),
                    cb.diff(restoredChoice, 1), cb.diff(5, restoredChoice),
                    cb.quot(restoredChoice, 2d), cb.quot(5d, restoredChoice),
                    cb.coalesce(nullable, restoredChoice), cb.<Number>coalesce().value(nullable).value(restoredChoice),
                    nullable, cb.nullif(restoredChoice, (Number) null))
                    .where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
            CriteriaQuery<Number> scalar = cb.createQuery(Number.class);
            scalar.select(restoredNested).where(cb.equal(scalar.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            CriteriaQuery<NumberProjection> constructor = cb.createQuery(NumberProjection.class);
            constructor.multiselect(restoredNested)
                    .where(cb.equal(constructor.from(TemporalCriteriaRecord.class).get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<Tuple> old = handler.createQuery(cq);
            TypedQuery<Number> oldScalar = handler.createQuery(scalar);
            TypedQuery<NumberProjection> oldConstructor = handler.createQuery(constructor);
            restoredChoice.otherwise(2.5d);
            TypedQuery<Tuple> current = handler.createQuery(cq);
            TypedQuery<Number> currentScalar = handler.createQuery(scalar);
            TypedQuery<NumberProjection> currentConstructor = handler.createQuery(constructor);
            for (Integer binding : Arrays.asList(null, 2, 3, null)) {
                assertQueryValues(old.setParameter(restoredParameter, binding), 3, 4, 4, 1, 3,
                        1d, 2.5d, 2, 2, Integer.valueOf(2).equals(binding) ? null : 2, 2);
                assertQueryValues(current.setParameter(restoredParameter, binding), 3.5d, 5d, 5d, 1.5d, 2.5d,
                        1.25d, 2d, 2.5d, 2.5d, 2.5d, 2.5d);
                assertEquals(Integer.valueOf(3), oldScalar.getSingleResult());
                assertEquals(Double.valueOf(3.5d), currentScalar.getSingleResult());
                assertEquals(Integer.valueOf(3), oldConstructor.getSingleResult().value);
                assertEquals(Double.valueOf(3.5d), currentConstructor.getSingleResult().value);
            }
            assertSame(restoredParameter, current.getParameter("numericMatch"));
            assertEquals("nestedNumber", restoredNested.getAlias());
        });
        assertEquals(Integer.class, choice.getJavaType());
        assertEquals(Integer.class, nested.getJavaType());
        assertEquals("numericMatch", parameter.getName());
    }

    public static class NumberProjection {
        public final Number value;

        public NumberProjection(Number value) {
            this.value = value;
        }
    }

    @Test
    public void testCreatedConstructorResolvesCurrentNumericOverload() {
        withHandlers(handler -> {
            for (boolean explicit : List.of(false, true)) {
                CriteriaBuilder cb = emf.getCriteriaBuilder();
                CriteriaQuery<NumericProjection> cq = cb.createQuery(NumericProjection.class);
                Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
                Case<Number> value = cb.selectCase(Number.class).when(cb.literal(false), 1);
                value.otherwise(2);
                if (explicit) {
                    cq.select(cb.construct(NumericProjection.class, value));
                } else {
                    cq.multiselect(value);
                }
                cq.where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
                TypedQuery<NumericProjection> original = handler.createQuery(cq);
                CriteriaQuery<Tuple> tuple = cb.createTupleQuery();
                Root<TemporalCriteriaRecord> tupleRoot = tuple.from(TemporalCriteriaRecord.class);
                Selection<NumericProjection> nested = cq.getSelection();
                tuple.multiselect(nested, value, cb.construct(NumberProjection.class, value))
                        .where(cb.equal(tupleRoot.get(TemporalCriteriaRecord_.id), 1));
                Selection<Tuple> tupleSelection = tuple.getSelection();
                TypedQuery<Tuple> oldTuple = handler.createQuery(tuple);
                CriteriaQuery<Object[]> array = cb.createQuery(Object[].class);
                Root<TemporalCriteriaRecord> arrayRoot = array.from(TemporalCriteriaRecord.class);
                array.multiselect(nested, value).where(cb.equal(arrayRoot.get(TemporalCriteriaRecord_.id), 1));
                Selection<Object[]> arraySelection = array.getSelection();
                TypedQuery<Object[]> oldArray = handler.createQuery(array);
                value.otherwise(2.5d);
                assertEquals(Integer.valueOf(2), original.getSingleResult().value);
                assertEquals(Double.valueOf(2.5d), handler.createQuery(cq).getSingleResult().value);
                Tuple previousTuple = oldTuple.getSingleResult();
                Tuple currentTuple = handler.createQuery(tuple).getSingleResult();
                assertEquals(Integer.valueOf(2), previousTuple.get(0, NumericProjection.class).value);
                assertEquals(Double.valueOf(2.5d), currentTuple.get(0, NumericProjection.class).value);
                assertEquals(Integer.valueOf(2), previousTuple.get(2, NumberProjection.class).value);
                assertEquals(Double.valueOf(2.5d), currentTuple.get(2, NumberProjection.class).value);
                assertSame(tupleSelection, tuple.getSelection());
                assertEquals(Tuple.class, tuple.getResultType());
                assertSame(nested, cq.getSelection());
                assertEquals(Integer.valueOf(2), ((NumericProjection) oldArray.getSingleResult()[0]).value);
                assertEquals(Double.valueOf(2.5d), ((NumericProjection) handler.createQuery(array).getSingleResult()[0]).value);
                assertSame(arraySelection, array.getSelection());
                assertEquals(Object[].class, array.getResultType());
                assertEquals(Integer.valueOf(2), original.getSingleResult().value);
            }
        });
    }

    public static class NumericProjection {
        public final Number value;

        public NumericProjection(Integer value) {
            this.value = value;
        }

        public NumericProjection(Double value) {
            this.value = value;
        }
    }

    @Test
    public void testIndependentSubqueryParametersPreserveOuterRootsAndJoins() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            ParameterExpression<String> name = cb.parameter(String.class, "innerParentName");
            Subquery<Integer> inner = cq.subquery(Integer.class);
            Root<TemporalCriteriaRecord> child = inner.from(TemporalCriteriaRecord.class);
            Join<TemporalCriteriaRecord, TemporalCriteriaRecord> parent = child.join(TemporalCriteriaRecord_.parent);
            parent.on(cb.equal(parent.get(TemporalCriteriaRecord_.text), name));
            ParameterExpression<Integer> amount = cb.parameter(Integer.class, "nestedAmount");
            Subquery<Integer> nested = inner.subquery(Integer.class);
            Root<TemporalCriteriaRecord> ownNested = nested.from(TemporalCriteriaRecord.class);
            Root<TemporalCriteriaRecord> correlated = nested.correlate(child);
            nested.select(ownNested.get(TemporalCriteriaRecord_.id)).where(
                    cb.equal(ownNested.get(TemporalCriteriaRecord_.id), correlated.get(TemporalCriteriaRecord_.id)),
                    cb.equal(ownNested.get(TemporalCriteriaRecord_.amount), amount));
            Set<Root<?>> nestedRoots = Set.copyOf(nested.getRoots());
            inner.select(child.get(TemporalCriteriaRecord_.id)).where(cb.equal(child.get(TemporalCriteriaRecord_.id), 2), cb.exists(nested));
            cq.select(root.get(TemporalCriteriaRecord_.id)).where(cb.exists(inner)).orderBy(cb.asc(root.get(TemporalCriteriaRecord_.id)));
            Set<Root<?>> outerRoots = Set.copyOf(cq.getRoots());
            Set<Root<?>> innerRoots = Set.copyOf(inner.getRoots());
            assertEquals(Set.of(root), outerRoots);
            for (int repeat = 0; repeat < 2; repeat++) {
                TypedQuery<Integer> query = handler.createQuery(cq);
                assertEquals("Outer roots after createQuery", outerRoots, cq.getRoots());
                assertEquals(innerRoots, inner.getRoots());
                assertTrue(root.getJoins().isEmpty());
                assertEquals(Set.of(parent), child.getJoins());
                assertSame(name, query.getParameter("innerParentName"));
                assertSame(amount, query.getParameter("nestedAmount"));
                assertEquals(Set.of(name, amount), query.getParameters());
                assertEquals(nestedRoots, nested.getRoots());
                query.setParameter(amount, 20);
                for (String binding : Arrays.asList(null, "first", "missing", "first", null)) {
                    List<Integer> expected = "first".equals(binding) ? List.of(1, 2, 3, 4) : List.of();
                    List<Integer> actual = query.setParameter(name, binding).getResultList();
                    if (!expected.equals(actual)) {
                        throw new AssertionError("ON binding=" + binding + "; expected=" + expected + "; actual=" + actual
                                + "; SQL=" + query.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString());
                    }
                }
                assertTrue(query.setParameter(name, "first").setParameter(amount, (Integer) null).getResultList().isEmpty());
                assertEquals(List.of(1, 2, 3, 4), query.setParameter(amount, 20).getResultList());
                assertEquals(outerRoots, cq.getRoots());
                assertEquals(innerRoots, inner.getRoots());
                assertEquals(nestedRoots, nested.getRoots());
            }
            TypedQuery<Integer> beforeOnMutation = handler.createQuery(cq);
            ParameterExpression<String> replacement = cb.parameter(String.class, "replacedOnName");
            parent.on(cb.equal(parent.get(TemporalCriteriaRecord_.text), replacement));
            TypedQuery<Integer> afterOnMutation = handler.createQuery(cq);
            assertEquals(Set.of(name, amount), beforeOnMutation.getParameters());
            assertEquals(Set.of(replacement, amount), afterOnMutation.getParameters());
            assertSame(replacement, afterOnMutation.getParameter("replacedOnName"));
            assertEquals(List.of(1, 2, 3, 4), beforeOnMutation.setParameter(name, "first").setParameter(amount, 20).getResultList());
            assertTrue(afterOnMutation.setParameter(replacement, "missing").setParameter(amount, 20).getResultList().isEmpty());
            assertEquals(List.of(1, 2, 3, 4), afterOnMutation.setParameter(replacement, "first").getResultList());
            assertEquals(List.of(1, 2, 3, 4), beforeOnMutation.getResultList());
            assertEquals(outerRoots, cq.getRoots());
        });
    }

    @Test
    public void testMappedTimeAggregatesComposeWithFractionalParameters() {
        boolean derby;
        try (EntityManager em = emf.createEntityManager()) {
            derby = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform().isDerby();
        }
        for (LocalTime first : fractionalTimeInputs()) {
            withHandlers(handler -> {
                CriteriaBuilder cb = emf.getCriteriaBuilder();
                for (boolean maximum : List.of(false, true)) {
                    for (boolean timestampCarrier : List.of(false, true)) {
                        for (int id : List.of(1, 4, 0)) {
                            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
                            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
                            TemporalExpression<LocalTime> source = timestampCarrier ? cb.temporalLiteral(first)
                                    : root.get(TemporalCriteriaRecord_.timeValue);
                            Expression<LocalTime> aggregate = maximum ? source.max() : source.min();
                            ParameterExpression<LocalTime> parameter = cb.parameter(LocalTime.class, "aggregateTime");
                            Expression<LocalTime> coalesce = cb.coalesce(aggregate, parameter);
                            Expression<LocalTime> reverse = cb.coalesce(parameter, aggregate);
                            Expression<LocalTime> nullable = cb.nullif(aggregate, parameter);
                            Expression<LocalTime> reverseNull = cb.nullif(parameter, aggregate);
                            cq.multiselect(coalesce, reverse, nullable, reverseNull, cb.extract(LocalTimeField.SECOND, reverse),
                                    cb.extract(LocalTimeField.SECOND, reverseNull))
                                    .where(cb.equal(root.get(TemporalCriteriaRecord_.id), id));
                            TypedQuery<Tuple> query = handler.createQuery(cq);
                            LocalTime stored = id == 0 || id == 4 && !timestampCarrier ? null
                                    : timestampCarrier && !derby ? first : FIRST.toLocalTime().withNano(0);
                            LocalTime second = first.withNano(first.getNano() == 125000000 ? 250000000 : 123456788);
                            for (LocalTime binding : Arrays.asList(null, FIRST.toLocalTime().withNano(0), first, second, null)) {
                                // Original fractional bindings remain intact; Derby retains its recorded binder restriction.
                                LocalTime effective = binding == null ? null : derby ? binding.withNano(0) : binding;
                                LocalTime expectedReverse = effective == null ? stored : effective;
                                LocalTime expectedNull = java.util.Objects.equals(effective, stored) ? null : effective;
                                assertQueryValues(query.setParameter(parameter, binding), stored == null ? effective : stored,
                                        expectedReverse, java.util.Objects.equals(stored, effective) ? null : stored,
                                        expectedNull, seconds(expectedReverse), seconds(expectedNull));
                            }
                            assertSame(parameter, query.getParameter("aggregateTime"));
                        }
                    }
                }
            });
        }
    }

    @Test
    public void testCreatedScalarAndConstructorQueriesKeepOriginalParameters() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Projection> cq = cb.createQuery(Projection.class);
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            ParameterExpression<String> parameter = cb.parameter(String.class, "snapshotText");
            SimpleCase<Integer, String> simple = cb.selectCase(root.get(TemporalCriteriaRecord_.id), String.class)
                    .when(2, "other");
            simple.otherwise(parameter);
            cq.multiselect(root.get(TemporalCriteriaRecord_.dateValue).extract(LocalDateField.YEAR), simple)
                    .where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<Projection> original = handler.createQuery(cq);
            assertEquals(Set.of(parameter), original.getParameters());
            assertSame(parameter, original.getParameter("snapshotText"));
            simple.otherwise("changed");
            for (String binding : Arrays.asList(null, "bound", "next", null)) {
                Projection result = original.setParameter(parameter, binding).getSingleResult();
                assertEquals(Integer.valueOf(2021), result.year);
                assertEquals(binding, result.text);
            }
            TypedQuery<Projection> changed = handler.createQuery(cq);
            assertTrue(changed.getParameters().isEmpty());
            assertEquals("changed", changed.getSingleResult().text);
            assertEquals(Set.of(parameter), original.getParameters());

            CriteriaQuery<String> scalar = cb.createQuery(String.class);
            Root<TemporalCriteriaRecord> scalarRoot = scalar.from(TemporalCriteriaRecord.class);
            Case<String> searched = cb.selectCase(String.class).when(cb.literal(false), "other");
            searched.otherwise(parameter);
            scalar.select(searched).where(cb.equal(scalarRoot.get(TemporalCriteriaRecord_.id), 1));
            TypedQuery<String> oldScalar = handler.createQuery(scalar);
            searched.otherwise("new");
            assertEquals("original", oldScalar.setParameter(parameter, "original").getSingleResult());
            assertEquals("new", handler.createQuery(scalar).getSingleResult());
        });
    }

    @Test
    public void testNestedCasesInCorrelatedSubqueryAndSetComponents() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Subquery<Double> subquery = q.cq.subquery(Double.class);
            Root<TemporalCriteriaRecord> correlated = subquery.correlate(q.root);
            Case<Double> value = q.cb.selectCase(Double.class)
                    .when(q.cb.equal(correlated.get(TemporalCriteriaRecord_.id), 1), 7.5d);
            subquery.select(value.otherwise((Double) null));
            q.cq.where(q.cb.equal(q.root.get(TemporalCriteriaRecord_.id), 1), q.cb.equal(subquery, 7.5d));
            assertValues(q.row(q.root.get(TemporalCriteriaRecord_.id)), 1);

            CriteriaQuery<String> left = q.cb.createQuery(String.class);
            Root<TemporalCriteriaRecord> leftRoot = left.from(TemporalCriteriaRecord.class);
            SimpleCase<Integer, String> component = leftRoot.get(TemporalCriteriaRecord_.id).selectCase(String.class)
                    .when(2, "other");
            component.otherwise("before");
            left.select(component).where(q.cb.equal(leftRoot.get(TemporalCriteriaRecord_.id), 1));
            CriteriaQuery<String> right = q.cb.createQuery(String.class);
            Root<TemporalCriteriaRecord> rightRoot = right.from(TemporalCriteriaRecord.class);
            right.select(q.cb.literal("right")).where(q.cb.equal(rightRoot.get(TemporalCriteriaRecord_.id), 2));
            CriteriaSelect<String> union = q.cb.unionAll(left, right);
            TypedQuery<String> original = em.createQuery(union);
            component.otherwise("after");
            assertEquals(Set.of("before", "right"), Set.copyOf(original.getResultList()));
            assertEquals(Set.of("after", "right"), Set.copyOf(em.createQuery(union).getResultList()));
        }
    }

    @Test
    public void testMutatedCorrelatedCaseDiscoversOnlyActiveOriginalParameters() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<TemporalCriteriaRecord> root = cq.from(TemporalCriteriaRecord.class);
            ParameterExpression<Long> before = cb.parameter(Long.class, "before");
            ParameterExpression<Long> after = cb.parameter(Long.class, "after");
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<TemporalCriteriaRecord> correlated = subquery.correlate(root);
            Case<Long> value = cb.selectCase(Long.class)
                    .when(cb.equal(correlated.get(TemporalCriteriaRecord_.id), 2), 1L);
            subquery.select(value.otherwise(before));
            cq.select(root.get(TemporalCriteriaRecord_.id))
                    .where(cb.equal(root.get(TemporalCriteriaRecord_.id), 1), cb.equal(subquery, 11L));
            TypedQuery<Integer> original = handler.createQuery(cq);
            assertEquals(Set.of(before), original.getParameters());
            assertSame(before, original.getParameter("before"));
            value.otherwise(after);
            TypedQuery<Integer> changed = handler.createQuery(cq);
            assertEquals(Set.of(after), changed.getParameters());
            assertSame(after, changed.getParameter("after"));
            assertEquals(Integer.valueOf(1), original.setParameter(before, 11L).getSingleResult());
            assertEquals(Integer.valueOf(1), changed.setParameter(after, 11L).getSingleResult());
            assertTrue(changed.setParameter(after, 12L).getResultList().isEmpty());
            assertEquals(Integer.valueOf(1), original.setParameter(before, 11L).getSingleResult());
        });
    }

    @Test
    public void testSharedCaseSnapshotAcrossProjectionGroupingHavingAndOrder() {
        try (EntityManager em = emf.createEntityManager()) {
            Query q = new Query(em);
            Expression<Integer> id = q.root.get(TemporalCriteriaRecord_.id);
            Expression<Integer> amount = q.root.get(TemporalCriteriaRecord_.amount);
            Case<Integer> value = q.cb.selectCase(Integer.class).when(q.cb.isNotNull(amount), amount);
            value.otherwise(id);
            q.cq.multiselect(value, q.cb.count(q.root)).where(q.cb.conjunction())
                    .groupBy(value, id, amount).having(q.cb.isNotNull(value)).orderBy(q.cb.asc(value));
            TypedQuery<Tuple> original = em.createQuery(q.cq);
            value.otherwise(amount);
            List<Tuple> before = original.getResultList();
            assertEquals(4, before.size());
            assertValues(before.get(0), 4, 1L);
            assertValues(before.get(1), 10, 1L);
            assertValues(before.get(2), 20, 1L);
            assertValues(before.get(3), 30, 1L);
            List<Tuple> after = em.createQuery(q.cq).getResultList();
            assertEquals(3, after.size());
            assertValues(after.get(0), 10, 1L);
        }
    }

    private static LocalTime nativeTime(EntityManager em, LocalDateTime value) {
        DatabasePlatform platform = em.unwrap(JpaEntityManager.class).getActiveSession().getPlatform();
        // These native SQL TIME types have precision zero. Timestamp SECOND above remains exact.
        return platform instanceof DerbyPlatform || platform instanceof DB2Platform
                ? value.toLocalTime().withNano(0) : value.toLocalTime();
    }

    private static void assertQueryValues(TypedQuery<Tuple> query, Object... expected) {
        try {
            assertValues(query.getSingleResult(), expected);
        } catch (AssertionError failure) {
            throw new AssertionError(failure.getMessage() + "; SQL: "
                    + query.unwrap(JpaQuery.class).getDatabaseQuery().getSQLString(), failure);
        }
    }

    private static void assertValues(Tuple row, Object... expected) {
        assertEquals(expected.length, row.getElements().size());
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] instanceof BigDecimal decimal) {
                assertEquals(0, decimal.compareTo(row.get(i, BigDecimal.class)));
            } else {
                assertEquals("Value at " + i, expected[i], row.get(i));
            }
            if (expected[i] != null) {
                assertEquals("Materialized class at " + i, expected[i].getClass(), row.get(i).getClass());
                assertTrue("Selection class at " + i, row.getElements().get(i).getJavaType().isInstance(row.get(i)));
            }
        }
    }

    private static class Query {
        private final EntityManager em;
        private final CriteriaBuilder cb;
        private final CriteriaQuery<Tuple> cq;
        private final Root<TemporalCriteriaRecord> root;

        private Query(EntityManager em) {
            this.em = em;
            cb = em.getCriteriaBuilder();
            cq = cb.createTupleQuery();
            root = cq.from(TemporalCriteriaRecord.class);
            id(1);
        }

        private void id(int id) {
            cq.where(cb.equal(root.get(TemporalCriteriaRecord_.id), id));
        }

        private Tuple row(Selection<?>... selections) {
            cq.multiselect(selections);
            return em.createQuery(cq).getSingleResult();
        }
    }
}
