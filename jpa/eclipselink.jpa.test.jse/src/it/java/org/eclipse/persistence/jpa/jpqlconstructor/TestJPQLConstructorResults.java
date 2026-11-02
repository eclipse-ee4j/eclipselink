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
package org.eclipse.persistence.jpa.jpqlconstructor;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.eclipse.persistence.descriptors.partitioning.HashPartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.RangePartitioningPolicy;
import org.eclipse.persistence.descriptors.partitioning.ValuePartitioningPolicy;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.jpa.QueryImpl;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.remote.Transporter;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.jpqlconstructor.model.Author;
import org.eclipse.persistence.jpa.jpqlconstructor.model.Book;
import org.eclipse.persistence.jpa.jpqlconstructor.model.CodeConverter;
import org.eclipse.persistence.jpa.jpqlconstructor.model.Edition;
import org.eclipse.persistence.jpa.jpqlconstructor.model.ObjectConverter;
import org.eclipse.persistence.jpa.jpqlconstructor.model.Results;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.queries.ConstructorReportItem;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.serializers.JavaSerializer;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestJPQLConstructorResults {
    private static final String PAIRS = "select b.id, b.title from ICRBook b order by b.id";
    private static final String TITLES = "select b.title from ICRBook b order by b.id";
    private static final String EXPLICIT = "select new " + Results.Summary.class.getName()
            + "(b.id, b.title) from ICRBook b order by b.id";
    private static final List<Results.Summary> SUMMARIES = List.of(new Results.Summary(1, "Alpha"),
            new Results.Summary(2, "Beta"), new Results.Summary(3, "Gamma"));

    @Emf(name = "jpql-constructor-results", createTables = DDLGen.DROP_CREATE,
            classes = {Book.class, Author.class, Edition.class, CodeConverter.class, ObjectConverter.class},
            mappingFiles = "META-INF/jpql-constructor-results/orm.xml",
            properties = @Property(name = "jakarta.persistence.validation.mode", value = "NONE"))
    private EntityManagerFactory emf;

    @Before
    public void seed() {
        cleanup();
        emf.runInTransaction(em -> {
            em.persist(new Edition(1, "en"));
            Author author = new Author(1, "Author");
            em.persist(author);
            em.persist(new Book(1, "Alpha", null, 2, author));
            em.persist(new Book(2, "Beta", "Second", 4, author));
            em.persist(new Book(3, "Gamma", "Third", 6, author));
        });
    }

    @After
    public void cleanup() {
        Results.BOOK_OBSERVER.remove();
        emf.runInTransaction(em -> {
            em.createStatement("delete from ICREdition").execute();
            em.createStatement("delete from ICRBook").execute();
            em.createStatement("delete from ICRAuthor").execute();
        });
        emf.getCache().evictAll();
    }

    private void withHandlers(Consumer<EntityHandler> assertions) {
        try (EntityManager em = emf.createEntityManager()) {
            assertions.accept(em);
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertions.accept(agent);
        }
    }

    @Test
    public void recordsAndOrdinaryClassesReceiveOrderedValues() {
        withHandlers(handler -> {
            assertEquals(SUMMARIES, handler.createQuery(PAIRS, Results.Summary.class).getResultList());
            List<Results.Ordinary> ordinary = handler.createQuery(PAIRS, Results.Ordinary.class).getResultList();
            assertEquals(3, ordinary.size());
            for (int i = 0; i < ordinary.size(); i++) {
                assertEquals(SUMMARIES.get(i).id(), ordinary.get(i).id);
                assertEquals(SUMMARIES.get(i).title(), ordinary.get(i).title);
            }
        });
    }

    @Test
    public void scalarFallbackAndDirectAssignableResultsCoexist() {
        withHandlers(handler -> {
            assertEquals(List.of(new Results.Text("Alpha"), new Results.Text("Beta"), new Results.Text("Gamma")),
                    handler.createQuery(TITLES, Results.Text.class).getResultList());
            assertEquals(List.of("Alpha", "Beta", "Gamma"), handler.createQuery(TITLES, CharSequence.class).getResultList());
            assertEquals(1, handler.createQuery("select b.id from ICRBook b where b.id = 1", java.lang.Number.class).getSingleResult());
            assertEquals("Alpha", handler.createQuery(TITLES, Object.class).setMaxResults(1).getSingleResult());
        });
    }

    @Test
    public void entitySelectionSupportsReadAllAndPrimaryKeyOptimization() {
        withHandlers(handler -> {
            List<Results.BookResult> all = handler.createQuery("select b from ICRBook b order by b.id", Results.BookResult.class).getResultList();
            assertEquals(List.of(1, 2, 3), all.stream().map(result -> result.value().id).toList());
            Results.BookResult one = handler.createQuery("select b from ICRBook b where b.id = :id", Results.BookResult.class)
                    .setParameter("id", 2).getSingleResult();
            assertEquals("Beta", one.value().title);
            Book direct = handler.createQuery("select b from ICRBook b where b.id = :id", Book.class)
                    .setParameter("id", 2).getSingleResult();
            assertEquals("Beta", direct.title);
        });
    }

    @Test
    public void joinedEntitySelectionAndFetchJoinRetainTheirMeaning() {
        withHandlers(handler -> {
            Results.AuthorResult result = handler.createQuery("select a from ICRBook b join b.author a where b.id = 1", Results.AuthorResult.class).getSingleResult();
            assertEquals("Author", result.value().name);
            Results.BookResult book = handler.createQuery("select b from ICRBook b join fetch b.author where b.id = 2", Results.BookResult.class).getSingleResult();
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(book.value(), "author"));
            assertEquals("Author", book.value().author.name);
        });
    }

    @Test
    public void entityManagerArgumentsAreManagedInsideTheConstructor() {
        AtomicInteger calls = new AtomicInteger();
        try (EntityManager em = emf.createEntityManager()) {
            Results.BOOK_OBSERVER.set(book -> {
                assertTrue(em.contains(book));
                calls.incrementAndGet();
            });
            Results.BookResult result = em.createQuery("select b from ICRBook b where b.id = 1", Results.BookResult.class).getSingleResult();
            assertSame(em.find(Book.class, 1), result.value());
            assertEquals(1, calls.get());
            assertThrows(IllegalArgumentException.class, () -> emf.getMetamodel().entity(Results.BookResult.class));
        } finally {
            Results.BOOK_OBSERVER.remove();
        }
    }

    @Test
    public void agentArgumentsAreDetachedBeforeConstructorObservation() {
        AtomicInteger calls = new AtomicInteger();
        try (EntityManager observer = emf.createEntityManager(); EntityAgent agent = emf.createEntityAgent()) {
            Book managed = observer.find(Book.class, 1);
            Results.BOOK_OBSERVER.set(book -> {
                assertFalse(observer.contains(book));
                assertNotSame(managed, book);
                book.title = "Changed inside constructor";
                calls.incrementAndGet();
            });
            Results.BookResult result = agent.createQuery("select b from ICRBook b where b.id = 1", Results.BookResult.class).getSingleResult();
            assertEquals("Changed inside constructor", result.value().title);
            assertEquals("Alpha", managed.title);
            assertEquals(1, calls.get());
        } finally {
            Results.BOOK_OBSERVER.remove();
        }
        assertEquals("Alpha", emf.callInTransaction(em -> em.find(Book.class, 1).title));
    }

    @Test
    public void convertersSupplyDomainArgumentsAndPreserveWhereBindings() {
        withHandlers(handler -> {
            Results.CodeResult result = handler.createQuery("select b.code from ICRBook b where b.code = :code", Results.CodeResult.class)
                    .setParameter("code", new Results.Code("Beta")).getSingleResult();
            assertEquals(new Results.Code("Beta"), result.value());
            assertEquals(new Results.Summary(2, "Beta"), handler.createQuery(
                    "select b.id, b.title from ICRBook b where b.code = :code", Results.Summary.class)
                    .setConvertedParameter("code", new Results.Code("Beta"), CodeConverter.class).getSingleResult());
        });
    }

    @Test
    public void nullValuesUseDeclaredTypesIncludingEmptyResults() {
        withHandlers(handler -> {
            assertEquals(new Results.Nullable(1, null), handler.createQuery(
                    "select b.id, b.subtitle from ICRBook b where b.id = 1", Results.Nullable.class).getSingleResult());
            assertTrue(handler.createQuery("select b.id, b.subtitle from ICRBook b where b.id < 0", Results.Nullable.class).getResultList().isEmpty());
        });
    }

    @Test
    public void exactSignaturesDoNotRetryPrimitiveAlternatives() {
        withHandlers(handler -> {
            assertEquals("Integer", handler.createQuery("select b.id from ICRBook b where b.id = 1", Results.Overloaded.class).getSingleResult().selected);
            for (int id : List.of(1, -1)) {
                assertThrows(IllegalArgumentException.class, () -> handler.createQuery(
                        "select b.id, b.title from ICRBook b where b.id = " + id, Results.Primitives.class));
            }
            assertThrows(IllegalArgumentException.class, () -> handler.createNamedQuery("ICR.summary", Results.Primitives.class));
            assertEquals(new Results.Primitives(1, "Alpha"), handler.createQuery(
                    "select new " + Results.Primitives.class.getName() + "(b.id, b.title) from ICRBook b where b.id = 1",
                    Results.Primitives.class).getSingleResult());
            var pending = handler.createQuery("select :value, b.title from ICRBook b where b.id = -1", Results.Primitives.class);
            pending.setParameter("value", null, Integer.class);
            jakarta.persistence.PersistenceException failure = assertThrows(jakarta.persistence.PersistenceException.class, pending::getResultList);
            assertTrue(failure.getCause() instanceof IllegalArgumentException);
            assertEquals("Alpha", handler.createQuery(TITLES, Results.Hidden.class).setMaxResults(1).getSingleResult().value);
        });
    }

    @Test
    public void incompatibleAndAmbiguousConstructorsFailBeforeRowsAreRead() {
        withHandlers(handler -> {
            for (String predicate : List.of(" where b.id = 1", " where b.id < 0")) {
                String query = "select b.id, b.title from ICRBook b" + predicate;
                for (Class<?> type : List.of(Results.Text.class, Results.Reversed.class, Results.Assignable.class,
                        Results.AbstractResult.class, Results.InterfaceResult.class)) {
                    assertThrows(type.getName(), IllegalArgumentException.class, () -> handler.createQuery(query, type));
                }
                assertThrows(IllegalArgumentException.class, () -> handler.createQuery(
                        "select b.id from ICRBook b" + predicate, Results.LongNumber.class));
                assertThrows(IllegalArgumentException.class, () -> handler.createQuery(
                        "select b.id, b.quantity from ICRBook b" + predicate, Results.Ambiguous.class));
            }
        });
    }

    @Test
    public void functionsArithmeticAndConstantsRetainSemanticTypes() {
        withHandlers(handler -> {
            Results.Functions result = handler.createQuery("select coalesce(b.subtitle, b.title), nullif(b.title, 'Alpha'), "
                    + "b.quantity + 1, case when b.id = 1 then 'yes' else 'no' end from ICRBook b where b.id = 1", Results.Functions.class).getSingleResult();
            assertEquals(new Results.Functions("Alpha", null, 3, "yes"), result);
            assertEquals(new Results.Summary(7, "constant"), handler.createQuery(
                    "select 7, 'constant' from ICRBook b where b.id = 1", Results.Summary.class).getSingleResult());
        });
    }

    @Test
    public void nullableAndNestedConditionalResultsRetainConstructorTypes() {
        withHandlers(handler -> {
            assertEquals(new Results.Text(null), handler.createQuery(
                    "select case b.id when 2 then b.title else null end from ICRBook b where b.id = 1", Results.Text.class).getSingleResult());
            assertEquals(new Results.Text("fallback"), handler.createQuery(
                    "select coalesce(case b.id when 2 then b.title else null end, 'fallback') from ICRBook b where b.id = 1",
                    Results.Text.class).getSingleResult());
            assertEquals(new Results.Text("Alpha"), handler.createQuery(
                    "select case when b.id = 1 then coalesce(b.subtitle, b.title) else null end from ICRBook b where b.id = 1",
                    Results.Text.class).getSingleResult());
        });
    }

    @Test
    public void unresolvedComputedScalarsDoNotMasqueradeAsObjectConstructorArguments() {
        withHandlers(handler -> {
            String selection = "select coalesce(function('LOWER', :first), function('LOWER', :second)) from ICRBook b where b.id";
            String jpql = selection + " = 1";
            var scalar = handler.createQuery(jpql, String.class);
            assertEquals("fallback", scalar.setParameter("first", null, String.class).setParameter("second", "fallback").getSingleResult());
            assertEquals("bound", scalar.setParameter("first", "bound").setParameter("second", null, String.class).getSingleResult());
            for (String predicate : List.of(" = 1", " = -1")) {
                for (Class<?> target : List.of(Results.Text.class, Results.ObjectValue.class)) {
                    assertThrows(IllegalArgumentException.class, () -> handler.createQuery(
                            selection + predicate, target));
                }
            }
            assertEquals("direct", handler.createQuery(jpql, Object.class)
                    .setParameter("first", "direct").setParameter("second", null, String.class).getSingleResult());
        });
    }

    @Test
    public void declaredObjectConditionalBranchesRemainRealObjectTypes() {
        withHandlers(handler -> {
            String jpql = "select coalesce(b.declaredValue, b.declaredValue) from ICRBook b where b.id = 1";
            assertEquals(new Results.ObjectValue("Alpha"), handler.createQuery(jpql, Results.ObjectValue.class).getSingleResult());
            assertThrows(IllegalArgumentException.class, () -> handler.createQuery(jpql, String.class));
            assertThrows(IllegalArgumentException.class, () -> handler.createQuery(jpql, Results.Text.class));
            for (String expression : List.of("coalesce(b.declaredValue, b.title)", "coalesce(b.title, b.declaredValue)",
                    "case when b.id = 1 then b.declaredValue else b.title end")) {
                String declared = "select " + expression + " from ICRBook b where b.id = 1";
                assertEquals(new Results.ObjectValue("Alpha"), handler.createQuery(declared, Results.ObjectValue.class).getSingleResult());
                assertEquals("Alpha", handler.createQuery(declared, Object.class).getSingleResult());
                assertThrows(IllegalArgumentException.class, () -> handler.createQuery(declared, String.class));
            }
        });
    }

    @Test
    public void equivalentConditionalOperandsChooseExactIntegerConstructors() {
        withHandlers(handler -> {
            for (String expression : List.of("case when b.id = 1 then b.quantity else 0 end",
                    "case b.id when 1 then b.quantity else 0 end", "coalesce(b.quantity, 0)",
                    "coalesce(case when b.id = 1 then b.quantity else null end, 0)",
                    "case when b.id = 1 then coalesce(b.quantity, 0) else null end",
                    "coalesce(b.smallQuantity, b.smallQuantity)", "coalesce(b.tinyQuantity, b.tinyQuantity)",
                    "case when b.id = 1 then b.smallQuantity else b.tinyQuantity end")) {
                for (int id : List.of(1, -1)) {
                    String jpql = "select " + expression + " from ICRBook b where b.id = " + id;
                    var query = handler.createQuery(jpql, Results.Number.class);
                    ConstructorReportItem item = (ConstructorReportItem) query.unwrap(ReportQuery.class).getItems().get(0);
                    assertArrayEquals(new Class<?>[]{Integer.class}, item.getConstructorArgTypes());
                    assertEquals(id == 1 ? List.of(new Results.Number(2)) : List.of(), query.getResultList());
                    assertEquals(id == 1 ? List.of(2) : List.of(), handler.createQuery(jpql, Integer.class).getResultList());
                }
            }
        });
    }

    @Test
    public void explicitNullCaseResultBranchesWorkForScalarsAndConstructors() {
        withHandlers(handler -> {
            for (String expression : List.of("case when b.id = 1 then null else b.quantity end",
                    "case when b.id = 2 then b.quantity else null end",
                    "case b.id when 1 then null else b.quantity end", "case b.id when 2 then b.quantity else null end")) {
                String jpql = "select " + expression + " from ICRBook b where b.id = ";
                assertEquals(new Results.Number(null), handler.createQuery(jpql + 1, Results.Number.class).getSingleResult());
                assertNull(handler.createQuery(jpql + 1, Integer.class).getSingleResult());
                assertEquals(new Results.Number(4), handler.createQuery(jpql + 2, Results.Number.class).getSingleResult());
                assertTrue(handler.createQuery(jpql + -1, Results.Number.class).getResultList().isEmpty());
            }
            for (String expression : List.of("case when b.id = 1 then null else b.title end",
                    "case b.id when 2 then b.title else null end")) {
                String jpql = "select " + expression + " from ICRBook b where b.id = ";
                assertEquals(new Results.Text(null), handler.createQuery(jpql + 1, Results.Text.class).getSingleResult());
                assertNull(handler.createQuery(jpql + 1, String.class).getSingleResult());
                assertEquals(new Results.Text("Beta"), handler.createQuery(jpql + 2, Results.Text.class).getSingleResult());
            }
            assertEquals(2, ((java.lang.Number) handler.createQuery("select coalesce(case when b.id = 1 then b.quantity "
                    + "else null end, 0) from ICRBook b where b.id = 1").getSingleResult()).intValue());
        });
    }

    @Test
    public void caseNullLiteralPolicyDoesNotChangeParameterOrPredicateBindings() {
        withHandlers(handler -> {
            for (String expression : List.of("case when b.id = :choice then :selected else b.quantity end",
                    "case b.id when :choice then :selected else b.quantity end")) {
                var query = handler.createQuery("select " + expression + " from ICRBook b where b.id = 1 and b.title = :title",
                        Results.Number.class).setParameter("choice", 1).setParameter("title", "Alpha");
                assertEquals(new Results.Number(null), query.setParameter("selected", null, Integer.class).getSingleResult());
                assertEquals(new Results.Number(7), query.setParameter("selected", 7).getSingleResult());
                assertEquals(new Results.Number(2), query.setParameter("choice", 2).setParameter("selected", null, Integer.class).getSingleResult());
                assertTrue(query.setParameter("title", null, String.class).getResultList().isEmpty());
                assertEquals(new Results.Number(2), query.setParameter("title", "Alpha").getSingleResult());
            }
        });
    }

    @Test
    public void numericConditionalOperandsUsePromotedSemanticTypes() {
        withHandlers(handler -> {
            for (String expression : List.of("case when b.id = 1 then b.quantity else 0L end",
                    "case b.id when 1 then b.quantity else 0L end", "coalesce(b.quantity, 0L)", "nullif(b.quantity, 0L)")) {
                for (int id : List.of(1, -1)) {
                    String jpql = "select " + expression + " from ICRBook b where b.id = " + id;
                    assertEquals(id == 1 ? List.of(new Results.LongNumber(2L)) : List.of(),
                            handler.createQuery(jpql, Results.LongNumber.class).getResultList());
                    assertEquals(id == 1 ? List.of(2L) : List.of(), handler.createQuery(jpql, Long.class).getResultList());
                    assertThrows(IllegalArgumentException.class, () -> handler.createQuery(jpql, Results.Number.class));
                }
            }
            for (String expression : List.of("coalesce(cast(b.quantity as FLOAT), 0.0)",
                    "case when b.id = 1 then cast(b.quantity as FLOAT) else 0.0 end")) {
                String jpql = "select " + expression + " from ICRBook b where b.id = 1";
                assertEquals(new Results.DoubleNumber(2.0), handler.createQuery(jpql, Results.DoubleNumber.class).getSingleResult());
                assertEquals(Double.valueOf(2.0), handler.createQuery(jpql, Double.class).getSingleResult());
                assertThrows(IllegalArgumentException.class, () -> handler.createQuery(jpql, Results.FloatNumber.class));
            }
        });
    }

    @Test
    public void unresolvedAggregateOperandsPreserveScalarConversionWithoutDtoInference() {
        withHandlers(handler -> {
            for (String operator : List.of("min", "max")) {
                String jpql = "select " + operator + "(function('LOWER', b.title)) from ICRBook b where b.id = ";
                assertEquals("alpha", handler.createQuery(jpql + 1, String.class).getSingleResult());
                assertNull(handler.createQuery(jpql + -1, String.class).getSingleResult());
                for (int id : List.of(1, -1)) {
                    assertThrows(IllegalArgumentException.class, () -> handler.createQuery(jpql + id, Results.Text.class));
                    assertThrows(IllegalArgumentException.class, () -> handler.createQuery(jpql + id, Results.ObjectValue.class));
                }
            }
            String sum = "select sum(function('ABS', b.quantity)) from ICRBook b where b.id = ";
            assertEquals(Long.valueOf(2L), handler.createQuery(sum + 1, Long.class).getSingleResult());
            assertNull(handler.createQuery(sum + -1, Long.class).getSingleResult());
            for (int id : List.of(1, -1)) {
                assertThrows(IllegalArgumentException.class, () -> handler.createQuery(sum + id, Results.LongNumber.class));
            }
        });
    }

    @Test
    public void incompatibleKnownOperandsCannotUseUnknownScalarConversion() {
        withHandlers(handler -> {
            for (String expression : List.of("case when b.id = 1 then b.title else b.quantity end",
                    "case b.id when 1 then b.title else b.quantity end", "coalesce(b.title, b.quantity)",
                    "coalesce(b.title, b.quantity, function('LOWER', b.title))",
                    "coalesce(function('LOWER', b.title), b.title, b.quantity)",
                    "case when b.id = 1 then coalesce(b.title, b.quantity) else function('LOWER', b.title) end",
                    "coalesce(b.declaredValue, coalesce(b.title, b.quantity))")) {
                for (int id : List.of(1, -1)) {
                    String jpql = "select " + expression + " from ICRBook b where b.id = " + id;
                    for (Class<?> target : List.of(String.class, Results.Text.class, Results.ObjectValue.class,
                            Object.class, Object[].class, Tuple.class)) {
                        assertThrows(jpql, IllegalArgumentException.class, () -> handler.createQuery(jpql, target));
                    }
                }
            }
        });
    }

    @Test
    public void incompatibleProjectionMetadataSurvivesCloningRegistrationAndTransport() {
        AbstractSession session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        withHandlers(handler -> {
            String jpql = "select coalesce(b.title, b.quantity) from ICRBook b where b.id = -1";
            var query = handler.createQuery(jpql);
            DatabaseQuery original = query.unwrap(DatabaseQuery.class);
            String property = "eclipselink.jpql.incompatible-result";
            assertEquals(Boolean.TRUE, original.getProperty(property));
            DatabaseQuery copy = (DatabaseQuery) original.clone();
            assertEquals(Boolean.TRUE, copy.getProperty(property));
            Transporter envelope = new Transporter(copy);
            Transporter restored = (Transporter) JavaSerializer.instance.deserialize(JavaSerializer.instance.serialize(envelope, session), session);
            DatabaseQuery transported = (DatabaseQuery) restored.getObject();
            assertEquals(Boolean.TRUE, transported.getProperty(property));
            query.unwrap(QueryImpl.class).setDatabaseQuery(transported);
            emf.addNamedQuery("ICR.incompatible", query);
            for (Class<?> target : List.of(Results.ObjectValue.class, Object.class, Object[].class)) {
                assertThrows(IllegalArgumentException.class, () -> handler.createNamedQuery("ICR.incompatible", target));
            }
            assertEquals(new Results.Text("Alpha"), handler.createQuery(
                    "select coalesce(b.title, b.subtitle) from ICRBook b where b.id = 1", Results.Text.class).getSingleResult());
            assertEquals(new Results.ObjectValue("Alpha"), handler.createQuery(
                    "select coalesce(b.declaredValue, b.declaredValue) from ICRBook b where b.id = 1", Results.ObjectValue.class).getSingleResult());
        });
    }

    @Test
    public void castTypesSurviveEveryTypePreservingWrapper() {
        withHandlers(handler -> {
            for (String expression : List.of("abs(cast(b.quantity as FLOAT))", "ceiling(cast(b.quantity as FLOAT))",
                    "floor(cast(b.quantity as FLOAT))", "round(cast(b.quantity as FLOAT), 0)",
                    "nullif(cast(b.quantity as FLOAT), 0)", "-cast(b.quantity as FLOAT)",
                    "(cast(b.quantity as FLOAT))", "min(cast(b.quantity as FLOAT))", "max(cast(b.quantity as FLOAT))")) {
                for (int id : List.of(1, -1)) {
                    String jpql = "select " + expression + " from ICRBook b where b.id = " + id;
                    ReportQuery query = handler.createQuery(jpql, Results.FloatNumber.class).unwrap(ReportQuery.class);
                    assertArrayEquals(jpql, new Class<?>[]{Float.class}, ((ConstructorReportItem) query.getItems().get(0)).getConstructorArgTypes());
                    assertEquals(Float.class, handler.createQuery(jpql, Float.class).unwrap(ReportQuery.class).getResultClass());
                }
            }
            for (int id : List.of(1, -1)) {
                ReportQuery sum = handler.createQuery("select sum(cast(b.quantity as FLOAT)) from ICRBook b where b.id = " + id,
                        Results.DoubleNumber.class).unwrap(ReportQuery.class);
                assertArrayEquals(new Class<?>[]{Double.class}, ((ConstructorReportItem) sum.getItems().get(0)).getConstructorArgTypes());
            }
        });
    }

    @Test
    public void wrappedCastsReachScalarAndConstructorResults() {
        withHandlers(handler -> {
            for (String expression : List.of("abs(cast(b.quantity as FLOAT))", "nullif(cast(b.quantity as FLOAT), 0)",
                    "ceiling(cast(b.quantity as FLOAT))", "floor(cast(b.quantity as FLOAT))", "round(cast(b.quantity as FLOAT), 0)",
                    "(cast(b.quantity as FLOAT))", "abs(-cast(b.quantity as FLOAT))")) {
                String jpql = "select " + expression + " from ICRBook b where b.id = ";
                assertEquals(new Results.FloatNumber(2.0f), handler.createQuery(jpql + 1, Results.FloatNumber.class).getSingleResult());
                assertEquals(Float.valueOf(2.0f), handler.createQuery(jpql + 1, Float.class).getSingleResult());
                assertTrue(handler.createQuery(jpql + -1, Results.FloatNumber.class).getResultList().isEmpty());
                assertTrue(handler.createQuery(jpql + -1, Float.class).getResultList().isEmpty());
            }
            assertEquals(new Results.FloatNumber(-2.0f), handler.createQuery(
                    "select -cast(b.quantity as FLOAT) from ICRBook b where b.id = 1", Results.FloatNumber.class).getSingleResult());
        });
    }

    @Test
    public void aggregateCastsRetainWideningAndEmptyResultSemantics() {
        withHandlers(handler -> {
            for (String operator : List.of("min", "max")) {
                String jpql = "select " + operator + "(cast(b.quantity as FLOAT)) from ICRBook b where b.id = ";
                assertEquals(new Results.FloatNumber(2.0f), handler.createQuery(jpql + 1, Results.FloatNumber.class).getSingleResult());
                assertEquals(new Results.FloatNumber(null), handler.createQuery(jpql + -1, Results.FloatNumber.class).getSingleResult());
                assertNull(handler.createQuery(jpql + -1, Float.class).getSingleResult());
            }
            String sum = "select sum(cast(b.quantity as FLOAT)) from ICRBook b where b.id = ";
            assertEquals(new Results.DoubleNumber(2.0), handler.createQuery(sum + 1, Results.DoubleNumber.class).getSingleResult());
            assertEquals(new Results.DoubleNumber(null), handler.createQuery(sum + -1, Results.DoubleNumber.class).getSingleResult());
            assertEquals(Double.valueOf(2.0), handler.createQuery(sum + 1, Double.class).getSingleResult());
        });
    }

    @Test
    public void fixedResultFunctionsDoNotInheritTheirCastOperandType() {
        withHandlers(handler -> {
            Map<String, Class<?>> expressions = Map.of("sqrt(cast(b.quantity as INTEGER))", Double.class,
                    "power(cast(b.quantity as INTEGER), 2)", Double.class, "exp(cast(b.quantity as INTEGER))", Double.class,
                    "ln(cast(b.quantity as INTEGER))", Double.class, "sign(cast(b.quantity as FLOAT))", Integer.class,
                    "mod(cast(b.quantity as INTEGER), 2)", Integer.class, "avg(cast(b.quantity as FLOAT))", Double.class,
                    "count(cast(b.quantity as FLOAT))", Long.class, "length(cast(b.quantity as STRING))", Integer.class);
            for (var entry : expressions.entrySet()) {
                String jpql = "select " + entry.getKey() + " from ICRBook b where b.id = 1";
                Class<?> dto = entry.getValue() == Double.class ? Results.DoubleNumber.class
                        : entry.getValue() == Long.class ? Results.LongNumber.class : Results.Number.class;
                ReportQuery query = handler.createQuery(jpql, dto).unwrap(ReportQuery.class);
                assertArrayEquals(jpql, new Class<?>[]{entry.getValue()}, ((ConstructorReportItem) query.getItems().get(0)).getConstructorArgTypes());
            }
        });
    }

    @Test
    public void standardCastTargetsChooseConstructorsBeforeExecution() {
        Map<String, Class<?>> targets = Map.of("STRING", Results.Text.class, "INTEGER", Results.Number.class,
                "LONG", Results.LongNumber.class, "FLOAT", Results.FloatNumber.class, "DOUBLE", Results.DoubleNumber.class);
        withHandlers(handler -> {
            for (var target : targets.entrySet()) {
                for (int id : List.of(1, -1)) {
                    String jpql = "select cast(b.quantity as " + target.getKey() + ") from ICRBook b where b.id = " + id;
                    ReportQuery query = handler.createQuery(jpql, target.getValue()).unwrap(ReportQuery.class);
                    assertEquals(target.getValue(), ((ConstructorReportItem) query.getItems().get(0)).getConstructor().getDeclaringClass());
                    assertThrows(IllegalArgumentException.class, () -> handler.createQuery(jpql, Results.BookResult.class));
                }
            }
            ReportQuery arithmetic = handler.createQuery(
                    "select (cast(b.quantity as INTEGER) + 5) * cast(b.quantity as DOUBLE) from ICRBook b",
                    Results.DoubleNumber.class).unwrap(ReportQuery.class);
            assertArrayEquals(new Class<?>[]{Double.class},
                    ((ConstructorReportItem) arithmetic.getItems().get(0)).getConstructorArgTypes());
        });
    }

    @Test
    public void castValuesAndEnclosingArithmeticReachConstructors() {
        withHandlers(handler -> {
            assertEquals(new Results.FloatNumber(2.0f), handler.createQuery(
                    "select cast(b.quantity as FLOAT) from ICRBook b where b.id = 1", Results.FloatNumber.class).getSingleResult());
            assertEquals(2.0f, handler.createQuery("select cast(b.quantity as FLOAT) from ICRBook b where b.id = 1",
                    java.lang.Number.class).getSingleResult().floatValue(), 0.0f);
            assertEquals(new Results.FloatNumber(6.0f), handler.createQuery(
                    "select (cast(b.quantity as FLOAT) + 1) * 2 from ICRBook b where b.id = 1", Results.FloatNumber.class).getSingleResult());
            assertTrue(handler.createQuery("select cast(b.quantity as FLOAT) from ICRBook b where b.id = -1",
                    Results.FloatNumber.class).getResultList().isEmpty());
        });
    }

    @Test
    public void unknownFunctionsAndVendorCastsRetainDirectScalarConversion() {
        withHandlers(handler -> {
            assertEquals("alpha", handler.createQuery("select function('LOWER', b.title) from ICRBook b where b.id = 1",
                    String.class).getSingleResult());
            assertEquals(2, handler.createQuery("select function('ABS', b.quantity) from ICRBook b where b.id = 1",
                    java.lang.Number.class).getSingleResult().intValue());
            assertEquals("2", handler.createQuery("select cast(b.quantity as CHAR(20)) from ICRBook b where b.id = 1",
                    String.class).getSingleResult().trim());
            assertEquals(2, handler.createQuery("select cast(b.quantity as DECIMAL(10,0)) from ICRBook b where b.id = 1",
                    java.lang.Number.class).getSingleResult().intValue());
            assertEquals(java.time.LocalDate.of(2026, 1, 2), handler.createQuery(
                    "select function('LOWER', '2026-01-02') from ICRBook b where b.id = 1", java.time.LocalDate.class).getSingleResult());
            assertEquals(java.util.UUID.fromString("12345678-1234-1234-1234-123456789abc"), handler.createQuery(
                    "select function('LOWER', '12345678-1234-1234-1234-123456789abc') from ICRBook b where b.id = 1",
                    java.util.UUID.class).getSingleResult());
        });
    }

    @Test
    public void unknownFunctionTypesCannotBeInferredFromRowsOrObjectConstructors() {
        withHandlers(handler -> {
            for (int id : List.of(1, -1)) {
                for (Class<?> target : List.of(Results.Text.class, Results.ObjectValue.class)) {
                    assertThrows(IllegalArgumentException.class, () -> handler.createQuery(
                            "select function('LOWER', b.title) from ICRBook b where b.id = " + id, target));
                }
            }
            TypedQuery<String> declaredObject = handler.createQuery("select :value from ICRBook b where b.id = -1", String.class);
            declaredObject.setParameter("value", null, Object.class);
            jakarta.persistence.PersistenceException failure = assertThrows(jakarta.persistence.PersistenceException.class,
                    declaredObject::getResultList);
            assertTrue(failure.getCause() instanceof IllegalArgumentException);
        });
    }

    @Test
    public void aggregateSelectionUsesEstablishedJpaTypes() {
        withHandlers(handler -> {
            assertEquals(new Results.Aggregates(3L, 12L, 4.0, 2, 6), handler.createQuery(
                    "select count(b), sum(b.quantity), avg(b.quantity), min(b.quantity), max(b.quantity) from ICRBook b",
                    Results.Aggregates.class).getSingleResult());
            assertEquals("Alpha", handler.createQuery("select min(b.title) from ICRBook b", Results.Text.class).getSingleResult().value());
            assertEquals(0, new BigDecimal("12.75").compareTo(handler.createQuery(
                    "select sum(b.amount) from ICRBook b", Results.Decimal.class).getSingleResult().value()));
        });
    }

    @Test
    public void contextualNamedAndPositionalParametersRetainBindings() {
        withHandlers(handler -> {
            assertEquals(new Results.Summary(2, "Beta"), handler.createQuery(
                    "select :id, b.title from ICRBook b where b.id = :id", Results.Summary.class).setParameter("id", 2).getSingleResult());
            assertEquals(new Results.Summary(3, "Gamma"), handler.createQuery(
                    "select ?1, b.title from ICRBook b where b.id = ?1", Results.Summary.class).setParameter(1, 3).getSingleResult());
        });
    }

    @Test
    public void unresolvedParameterBindingsAreResolvedBeforeExecutionAndMayChangeType() {
        withHandlers(handler -> {
            TypedQuery<Results.Bindings> query = handler.createQuery("select :value from ICRBook b where b.id = 1", Results.Bindings.class);
            assertEquals("first", query.setParameter("value", "first").getSingleResult().value);
            assertEquals(42, query.setParameter("value", 42).getSingleResult().value);
            assertEquals("last", query.setParameter("value", "last").getSingleResult().value);
        });
    }

    @Test
    public void explicitlyTypedNullParameterSelectsItsConstructor() {
        withHandlers(handler -> {
            TypedQuery<Results.Bindings> query = handler.createQuery("select :value from ICRBook b where b.id = 1", Results.Bindings.class);
            query.setParameter("value", null, String.class);
            assertNull(query.getSingleResult().value);
            query.setParameter("value", null, Integer.class);
            assertNull(query.getSingleResult().value);
        });
    }

    @Test
    public void unresolvedNullParameterCannotChooseAnOverloadEvenWithNoRows() {
        withHandlers(handler -> {
            for (int id : List.of(1, -1)) {
                TypedQuery<Results.Bindings> query = handler.createQuery("select :value from ICRBook b where b.id = " + id, Results.Bindings.class);
                query.setParameter("value", null);
                jakarta.persistence.PersistenceException failure = assertThrows(jakarta.persistence.PersistenceException.class, query::getResultList);
                assertTrue(failure.getCause() instanceof IllegalArgumentException);
                assertTrue(failure.getCause().getMessage().contains("explicit binding type"));
            }
        });
    }

    @Test
    public void constructorThrownCauseIsRetained() {
        withHandlers(handler -> {
            RuntimeException failure = assertThrows(RuntimeException.class, () -> handler.createQuery(TITLES, Results.Throwing.class).getResultList());
            Throwable cause = failure;
            while (cause != null && !(cause instanceof IllegalStateException && cause.getMessage().startsWith("ICR constructor failure:"))) {
                cause = cause.getCause();
            }
            assertNotNull("Constructor cause must remain available", cause);
        });
    }

    @Test
    public void alternateResultClassesAndUntypedQueriesDoNotContaminateTheCache() {
        withHandlers(handler -> {
            for (int i = 0; i < 3; i++) {
                assertEquals(SUMMARIES, handler.createQuery(PAIRS, Results.Summary.class).getResultList());
                assertEquals("Beta", handler.createQuery(PAIRS, Results.Ordinary.class).setFirstResult(1).setMaxResults(1).getSingleResult().title);
                List<?> untyped = handler.createQuery(PAIRS).getResultList();
                assertArrayEquals(new Object[] {1, "Alpha"}, (Object[]) untyped.get(0));
                assertEquals(3, untyped.size());
                assertEquals(SUMMARIES, handler.createQuery(PAIRS, Results.Summary.class).getResultList());
            }
        });
    }

    @Test
    public void explicitAndNestedConstructorsKeepIndependentMetadata() {
        withHandlers(handler -> {
            for (int i = 0; i < 2; i++) {
                assertEquals(new Results.Nested(SUMMARIES.get(0)), handler.createQuery(EXPLICIT, Results.Nested.class).setMaxResults(1).getSingleResult());
                assertEquals(SUMMARIES, handler.createQuery(EXPLICIT, Results.Summary.class).getResultList());
                assertEquals(SUMMARIES, handler.createQuery(EXPLICIT).getResultList());
            }
        });
    }

    @Test
    public void arrayAndCriteriaTupleControlsRemainIntact() {
        withHandlers(handler -> {
            assertArrayEquals(new Object[] {1, "Alpha"}, handler.createQuery(PAIRS, Object[].class).setMaxResults(1).getSingleResult());
            assertArrayEquals(new Object[] {"Alpha"}, handler.createQuery(TITLES, Object[].class).setMaxResults(1).getSingleResult());
            var builder = handler.getCriteriaBuilder();
            var tupleQuery = builder.createTupleQuery();
            var root = tupleQuery.from(Book.class);
            tupleQuery.multiselect(root.get("id").alias("id"), root.get("title").alias("title"));
            tupleQuery.where(builder.equal(root.get("id"), 1));
            Tuple tuple = handler.createQuery(tupleQuery).getSingleResult();
            assertEquals(1, tuple.get("id"));
            assertEquals("Alpha", tuple.get("title"));
            var constructorQuery = builder.createQuery(Results.Summary.class);
            var book = constructorQuery.from(Book.class);
            constructorQuery.select(builder.construct(Results.Summary.class, book.get("id"), book.get("title")));
            constructorQuery.where(builder.equal(book.get("id"), 1));
            assertEquals(SUMMARIES.get(0), handler.createQuery(constructorQuery).getSingleResult());
        });
    }

    @Test
    public void ofTypeRemainsNarrowingAndDoesNotConstructDtos() {
        withHandlers(handler -> {
            assertThrows(IllegalArgumentException.class, () -> handler.createQuery(PAIRS).ofType(Results.Summary.class));
            assertEquals(List.of("Alpha", "Beta", "Gamma"), handler.createQuery(TITLES).ofType(String.class).getResultList());
        });
    }

    @Test
    public void annotationAndXmlDeclarationsApplyToUntypedNamedQueries() {
        withHandlers(handler -> {
            for (String name : List.of("ICR.summary", "ICR.xmlSummary")) {
                assertEquals(SUMMARIES, handler.createNamedQuery(name).getResultList());
            }
            Results.BookResult book = (Results.BookResult) handler.createNamedQuery("ICR.entity").setParameter("id", 2).getSingleResult();
            assertEquals("Beta", book.value().title);
        });
    }

    @Test
    public void typedNamedOverridesPreserveLaterDeclaredAndRawResults() {
        withHandlers(handler -> {
            for (String name : List.of("ICR.summary", "ICR.xmlSummary", "ICR.raw")) {
                assertEquals("Alpha", handler.createNamedQuery(name, Results.Ordinary.class).setMaxResults(1).getSingleResult().title);
                assertEquals(SUMMARIES, handler.createNamedQuery(name, Results.Summary.class).getResultList());
            }
            assertEquals(SUMMARIES, handler.createNamedQuery("ICR.summary").getResultList());
            assertEquals(SUMMARIES, handler.createNamedQuery("ICR.xmlSummary").getResultList());
            assertArrayEquals(new Object[] {1, "Alpha"}, (Object[]) handler.createNamedQuery("ICR.raw").getResultList().get(0));
            Book direct = handler.createNamedQuery("ICR.entity", Book.class).setParameter("id", 1).getSingleResult();
            assertEquals("Alpha", direct.title);
        });
    }

    @Test
    public void discoveryAndExplicitTypedReferencesRetainResultMetadata() {
        withHandlers(handler -> {
            Map<String, TypedQueryReference<Results.Summary>> discovered = emf.getNamedQueries(Results.Summary.class);
            assertEquals(Results.Summary.class, discovered.get("ICR.summary").getResultType());
            assertEquals(Results.Summary.class, discovered.get("ICR.xmlSummary").getResultType());
            assertEquals(SUMMARIES, handler.createQuery(discovered.get("ICR.summary")).getResultList());
            assertEquals("Beta", handler.createQuery(new QueryReference<>("ICR.summary", Results.Ordinary.class))
                    .setFirstResult(1).setMaxResults(1).getSingleResult().title);
        });
    }

    @Test
    public void registeredConstructorQueryOutlivesItsAgentAndUsesTheExecutingOwner() {
        TypedQueryReference<Results.BookResult> reference;
        try (EntityAgent creator = emf.createEntityAgent()) {
            var query = creator.createQuery("select b from ICRBook b where b.id = 1", Results.BookResult.class);
            query.getSingleResult().value().title = "Detached creator";
            reference = emf.addNamedQuery("ICR.registeredEntity", query);
        }
        try (EntityManager em = emf.createEntityManager()) {
            Results.BookResult result = em.createQuery(reference).getSingleResult();
            assertTrue(em.contains(result.value()));
            assertEquals("Alpha", result.value().title);
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            Results.BookResult first = agent.createQuery(reference).getSingleResult();
            Results.BookResult second = agent.createQuery(reference).getSingleResult();
            assertNotSame(first.value(), second.value());
            assertEquals("Alpha", second.value().title);
        }
    }

    @Test
    public void augmentationKeepsModifiedPredicatesAndConstructorSelections() {
        withHandlers(handler -> {
            var original = emf.addNamedQuery("ICR.augment", handler.createQuery(PAIRS, Results.Summary.class));
            var builder = handler.getCriteriaBuilder();
            var augmented = builder.augment(original, query ->
                    query.where(builder.equal(query.getRoots().iterator().next().get("id"), 2)));
            assertEquals(SUMMARIES.get(1), handler.createQuery(augmented).getSingleResult());
            assertEquals(SUMMARIES, handler.createQuery(original).getResultList());
        });
    }

    @Test
    public void queryOptionsPaginationCountAndEmptyResultsRemainIndependent() {
        withHandlers(handler -> {
            TypedQuery<Results.Summary> query = handler.createQuery(PAIRS, Results.Summary.class)
                    .setHint("eclipselink.query-results-cache", false).setFirstResult(1).setMaxResults(1);
            assertEquals(SUMMARIES.get(1), query.getSingleResult());
            assertEquals(3L, query.getResultCount());
            query.setFirstResult(0).setMaxResults(3);
            assertEquals(SUMMARIES, query.getResultList());
            assertTrue(handler.createQuery("select distinct b.id, b.title from ICRBook b where b.id < :id",
                    Results.Summary.class).setParameter("id", 0).getResultList().isEmpty());
            assertEquals(SUMMARIES, handler.createQuery(PAIRS, Results.Summary.class).getResultList());
        });
    }

    @Test
    public void entityClassConstructorsProduceUnmanagedInstances() {
        try (EntityManager em = emf.createEntityManager()) {
            Book result = em.createQuery(PAIRS, Book.class).setMaxResults(1).getSingleResult();
            assertEquals("Alpha", result.title);
            assertFalse(em.contains(result));
            assertNotSame(em.find(Book.class, 1), result);
        }
    }

    @Test
    public void compositeIdPackagingRemainsIntact() {
        withHandlers(handler -> assertEquals(new Edition.Key(1, "en"), handler.createQuery(
                "select id(e) from ICREdition e", Edition.Key.class).getSingleResult()));
    }

    @Test
    public void collectionElementsAndMapKeysValuesAndEntriesUseElementTypes() {
        withHandlers(handler -> {
            assertEquals(new Results.Text("Alpha"), handler.createQuery(
                    "select t from ICRBook b join b.tags t where b.id = 1", Results.Text.class).getSingleResult());
            assertEquals(new Results.Text("rating"), handler.createQuery(
                    "select key(s) from ICRBook b join b.scores s where b.id = 1", Results.Text.class).getSingleResult());
            assertEquals(new Results.Number(2), handler.createQuery(
                    "select value(s) from ICRBook b join b.scores s where b.id = 1", Results.Number.class).getSingleResult());
            Results.EntryResult entry = handler.createQuery(
                    "select entry(s) from ICRBook b join b.scores s where b.id = 1", Results.EntryResult.class).getSingleResult();
            assertEquals("rating", entry.value().getKey());
            assertEquals(2, entry.value().getValue());
        });
    }

    @Test
    public void assignableParameterTargetSurvivesDifferentNumericBindings() {
        withHandlers(handler -> {
            TypedQuery<java.lang.Number> query = handler.createQuery(
                    "select :value from ICRBook b where b.id = 1", java.lang.Number.class);
            assertEquals(Integer.valueOf(3), query.setParameter("value", 3).getSingleResult());
            assertEquals(Long.valueOf(4), query.setParameter("value", 4L).getSingleResult());
            assertEquals(Integer.valueOf(5), query.setParameter("value", 5).getSingleResult());
        });
    }

    @Test
    public void objectParameterTargetAcceptsAnUntypedNullWithoutConstructorResolution() {
        withHandlers(handler -> assertNull(handler.createQuery(
                "select :value from ICRBook b where b.id = 1", Object.class)
                .setParameter("value", null).getSingleResult()));
    }

    @Test
    public void untypedSelectedNullsAndExplicitBindingTypesSurviveReuse() {
        withHandlers(handler -> {
            var query = handler.createQuery("select :rawValue from ICRBook rawBook where rawBook.id = 1");
            assertNull(query.setParameter("rawValue", null).getSingleResult());
            assertNull(query.setParameter("rawValue", null, String.class).getSingleResult());
            assertEquals("later", query.setParameter("rawValue", "later").getSingleResult());
            assertNull(query.setParameter("rawValue", null, Integer.class).getSingleResult());
            assertEquals(12, ((java.lang.Number) query.setParameter("rawValue", 12).getSingleResult()).intValue());
            assertNull(query.setParameter("rawValue", null).getSingleResult());
        });
    }

    @Test
    public void selectedNullObjectCountAndNumericRebindingsRemainIndependent() {
        withHandlers(handler -> {
            var query = handler.createQuery("select :countValue from ICRBook countedBook where countedBook.id = 1", Object.class);
            assertNull(query.setParameter("countValue", null).getSingleResult());
            assertEquals(1L, query.getResultCount());
            assertEquals(17, ((java.lang.Number) query.setParameter("countValue", 17).getSingleResult()).intValue());
            assertEquals(1L, query.getResultCount());
            assertNull(query.setParameter("countValue", null, String.class).getSingleResult());
            assertEquals(1L, query.getResultCount());
        });
    }

    @Test
    public void selectedNullBindingDoesNotChangePredicateParameters() {
        withHandlers(handler -> {
            var query = handler.createQuery("select :sharedValue from ICRBook b where b.id = 1 "
                    + "and (:sharedValue is null or b.title = :sharedValue)", Object.class);
            assertNull(query.setParameter("sharedValue", null, String.class).getSingleResult());
            assertEquals("Alpha", query.setParameter("sharedValue", "Alpha").getSingleResult());
            assertTrue(query.setParameter("sharedValue", "absent").getResultList().isEmpty());
            assertNull(query.setParameter("sharedValue", null, String.class).getSingleResult());
            var membership = handler.createQuery("select :selectedValue from ICRBook b where b.title in :titles order by b.id", Object.class);
            assertEquals(java.util.Arrays.asList(null, null), membership.setParameter("selectedValue", null, String.class)
                    .setParameter("titles", List.of("Alpha", "Beta")).getResultList());
            List<?> numeric = membership.setParameter("selectedValue", 7).setParameter("titles", List.of("Gamma")).getResultList();
            assertEquals(1, numeric.size());
            assertTrue(numeric.get(0) instanceof java.lang.Number);
            assertEquals(0, new java.math.BigDecimal("7").compareTo(new java.math.BigDecimal(numeric.get(0).toString())));
            assertTrue(membership.setParameter("titles", List.of("missing")).getResultList().isEmpty());
            assertEquals(java.util.Collections.singletonList(null), membership.setParameter("selectedValue", null, String.class)
                    .setParameter("titles", List.of("Alpha")).getResultList());
        });
    }

    @Test
    public void explicitlyNullCollectionBindingsRetainMembershipSemantics() {
        withHandlers(handler -> {
            var query = handler.createQuery("select b.title from ICRBook b where b.title in :titles order by b.title", String.class);
            assertTrue(query.setParameter("titles", null, java.util.Collection.class).getResultList().isEmpty());
            assertEquals(List.of("Alpha", "Beta"), query.setParameter("titles", List.of("Alpha", "Beta")).getResultList());
            assertTrue(query.setParameter("titles", List.of()).getResultList().isEmpty());
            assertTrue(query.setParameter("titles", null).getResultList().isEmpty());
            assertTrue(query.setParameter("titles", null, java.util.Collection.class).getResultList().isEmpty());
        });
    }

    @Test
    public void typedNullParametersRetainContextualSqlTypes() {
        withHandlers(handler -> {
            var numeric = handler.createQuery("select coalesce(:numberValue, b.quantity) from ICRBook b where b.id = 1", Object.class);
            assertEquals(2, ((java.lang.Number) numeric.setParameter("numberValue", null, Integer.class).getSingleResult()).intValue());
            assertEquals(7, ((java.lang.Number) numeric.setParameter("numberValue", 7).getSingleResult()).intValue());
            assertEquals(2, ((java.lang.Number) numeric.setParameter("numberValue", null, Integer.class).getSingleResult()).intValue());
            var text = handler.createQuery("select coalesce(:textValue, b.title) from ICRBook b where b.id = 1", Object.class);
            assertEquals("Alpha", text.setParameter("textValue", null, String.class).getSingleResult());
            assertEquals("bound", text.setParameter("textValue", "bound").getSingleResult());
            var conditional = handler.createQuery("select case when b.id = 1 then :caseValue else b.quantity end "
                    + "from ICRBook b where b.id = 1", Object.class);
            assertNull(conditional.setParameter("caseValue", null, Integer.class).getSingleResult());
            assertEquals(8, ((java.lang.Number) conditional.setParameter("caseValue", 8).getSingleResult()).intValue());
        });
    }

    @Test
    public void selectedNullParametersPreserveNestedPredicateBindingsAndNamedCopies() {
        withHandlers(handler -> {
            var query = handler.createQuery("select :nestedValue from ICRBook b where b.id = 1 "
                    + "and exists (select nested.id from ICRBook nested where nested.id = b.id "
                    + "and (:nestedValue is null or nested.title = :nestedValue))", Object.class);
            assertNull(query.setParameter("nestedValue", null, String.class).getSingleResult());
            emf.addNamedQuery("ICR.nullNested", query);
            var named = handler.createNamedQuery("ICR.nullNested", Object.class);
            assertEquals("Alpha", named.setParameter("nestedValue", "Alpha").getSingleResult());
            assertNull(named.setParameter("nestedValue", null, String.class).getSingleResult());
            assertTrue(named.setParameter("nestedValue", "absent").getResultList().isEmpty());
            assertNull(query.getSingleResult());
        });
    }

    @Test
    public void positionalNullCountPreservesDistinctGroupsAndPredicateBindings() {
        withHandlers(handler -> {
            var distinct = handler.createQuery("select distinct ?1 from ICRBook b", Object.class);
            assertEquals(1L, distinct.setParameter(1, null, String.class).getResultCount());
            assertNull(distinct.getSingleResult());
            var pairs = handler.createQuery("select distinct ?1, b.id from ICRBook b", Object[].class);
            assertEquals(3L, pairs.setParameter(1, null, String.class).getResultCount());
            var grouped = handler.createQuery("select ?1, count(b) from ICRBook b group by b.author.id", Object[].class);
            assertEquals(1L, grouped.setParameter(1, null, Integer.class).getResultCount());
            var repeated = handler.createQuery("select ?1, ?1 from ICRBook b where b.id = 1 and (?1 is null or b.title = ?1)", Object[].class);
            assertArrayEquals(new Object[]{null, null}, repeated.setParameter(1, null, String.class).getSingleResult());
            assertEquals(1L, repeated.getResultCount());
            assertArrayEquals(new Object[]{"Alpha", "Alpha"}, repeated.setParameter(1, "Alpha").getSingleResult());
            assertEquals(0L, repeated.setParameter(1, "absent").getResultCount());
        });
    }

    @Test
    public void convertedSelectedNullUsesRelationalBindingType() {
        withHandlers(handler -> {
            var query = handler.createQuery("select :convertedValue from ICRBook b where b.id = 1", Object.class);
            assertNull(query.setConvertedParameter("convertedValue", null, CodeConverter.class).getSingleResult());
            assertEquals("converted", query.setConvertedParameter("convertedValue", new Results.Code("converted"),
                    CodeConverter.class).getSingleResult());
            assertNull(query.setConvertedParameter("convertedValue", null, CodeConverter.class).getSingleResult());
        });
    }

    @Test
    public void registeredPendingParametersDoNotRetainTheCreatorsBindingType() {
        try (EntityAgent creator = emf.createEntityAgent()) {
            var query = creator.createQuery("select :value from ICRBook b where b.id = 1", Results.Bindings.class);
            assertEquals("creator", query.setParameter("value", "creator").getSingleResult().value);
            emf.addNamedQuery("ICR.pending", query);
        }
        withHandlers(handler -> {
            var query = handler.createNamedQuery("ICR.pending", Results.Bindings.class);
            assertEquals(42, query.setParameter("value", 42).getSingleResult().value);
            assertEquals("later", query.setParameter("value", "later").getSingleResult().value);
            assertEquals(new Results.Number(7), handler.createNamedQuery("ICR.pending", Results.Number.class)
                    .setParameter("value", 7).getSingleResult());
        });
    }

    @Test
    public void directPackagingRetainsUnresolvedParameterProvenanceAcrossRegistration() {
        withHandlers(handler -> {
            var source = handler.createQuery("select :value from ICRBook b where b.id = 1", Results.Bindings.class);
            source.setParameter("value", "original").getSingleResult();
            emf.addNamedQuery("ICR.pendingSource", source);
            for (Class<?> type : List.of(Object.class, Object[].class)) {
                var direct = handler.createNamedQuery("ICR.pendingSource", type);
                direct.setParameter("value", null).getSingleResult();
                emf.addNamedQuery("ICR.pendingDirect", direct);
                assertEquals(new Results.Number(9), handler.createNamedQuery("ICR.pendingDirect", Results.Number.class)
                        .setParameter("value", 9).getSingleResult());
            }
        });
    }

    @Test
    public void pendingQueryGraphsSerializeWithoutLosingDeferredResultMetadata() {
        withHandlers(handler -> {
            for (Class<?> resultClass : List.of(Object.class, Object[].class)) {
                var query = handler.createQuery("select :value from ICRBook b where b.id = 1", resultClass);
                roundTripQuery(query);
                query.setParameter("value", 7);
                roundTripQuery(query);
                assertTransportValue(7, query.getSingleResult());
                roundTripQuery(query);
                assertTransportValue("rebound", query.setParameter("value", "rebound").getSingleResult());
                roundTripQuery(query);
                assertTransportValue(null, query.setParameter("value", null, Integer.class).getSingleResult());
                roundTripQuery(query);
                assertTransportValue("after null", query.setParameter("value", "after null").getSingleResult());
                emf.addNamedQuery("ICR.serializedPending", query);
                withHandlers(owner -> assertEquals(new Results.Number(9), owner.createNamedQuery(
                        "ICR.serializedPending", Results.Number.class).setParameter("value", 9).getSingleResult()));
            }
        });
    }

    @Test
    public void typedNullArgumentRecordsRetainSerializationContracts() {
        AbstractSession session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        withHandlers(handler -> {
            var query = handler.createQuery("select :value from ICRBook b where b.id = 1", Object.class);
            for (Class<?> type : List.of(Integer.class, String.class, Object.class, java.util.Collection.class)) {
                AbstractRecord row = query.unwrap(DatabaseQuery.class).rowFromArguments(List.of(new ParameterExpression.TypedNull(type)), session);
                AbstractRecord restored = (AbstractRecord) JavaSerializer.instance.deserialize(JavaSerializer.instance.serialize(row, session), session);
                assertNull(restored.get("value"));
                assertEquals(type, restored.getField(new org.eclipse.persistence.internal.helper.DatabaseField("value")).getType());
            }

        });
    }

    private DatabaseQuery roundTripQuery(TypedQuery<?> query) {
        AbstractSession session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        DatabaseQuery original = query.unwrap(DatabaseQuery.class);
        String property = "eclipselink.jpql.constructor-parameters";
        assertNotNull(original.getProperty(property));
        Transporter envelope = new Transporter(original);
        Transporter restoredEnvelope = (Transporter) JavaSerializer.instance.deserialize(JavaSerializer.instance.serialize(envelope, session), session);
        DatabaseQuery restored = (DatabaseQuery) restoredEnvelope.getObject();
        assertNotNull(restored.getProperty(property));
        assertEquals(original.getArguments(), restored.getArguments());
        assertEquals(original.getProperty(property), restored.getProperty(property));
        query.unwrap(QueryImpl.class).setDatabaseQuery(restored);
        return restored;
    }

    private void assertTransportValue(Object expected, Object result) {
        Object actual = result instanceof Object[] values ? values[0] : result;
        if (expected instanceof java.lang.Number number) {
            assertTrue(actual instanceof java.lang.Number);
            assertEquals(0, new BigDecimal(number.toString()).compareTo(new BigDecimal(actual.toString())));
        } else {
            assertEquals(expected, actual);
        }
    }

    @Test
    public void basicMapEntryUntypedAndExplicitConstructorControlsReturnKeysAndValues() {
        withHandlers(handler -> {
            String from = " from ICRBook b join b.scores s where b.id = 1";
            assertEntry((Map.Entry<?, ?>) handler.createQuery("select entry(s)" + from).getSingleResult(), "rating", 2);
            Object[] row = (Object[]) handler.createQuery("select b.id, entry(s), b.title" + from).getSingleResult();
            assertEquals(3, row.length);
            assertEquals(1, row[0]);
            assertEntry((Map.Entry<?, ?>) row[1], "rating", 2);
            assertEquals("Alpha", row[2]);
            Results.EntryTitle explicit = handler.createQuery("select new " + Results.EntryTitle.class.getName()
                    + "(entry(s), b.title)" + from, Results.EntryTitle.class).getSingleResult();
            assertEntry(explicit.entry(), "rating", 2);
            assertEquals("Alpha", explicit.title());
        });
    }

    @Test
    public void mapEntryOffsetsCoverFirstMiddleLastAndRepeatedSelections() {
        emf.runInTransaction(em -> em.find(Book.class, 1).scores.put("other", 3));
        withHandlers(handler -> {
            String from = " from ICRBook b join b.scores s where b.id = 1 and key(s) = 'rating'";
            Results.EntryTitle first = handler.createQuery("select entry(s), b.title" + from, Results.EntryTitle.class).getSingleResult();
            assertEntry(first.entry(), "rating", 2);
            assertEquals("Alpha", first.title());
            Results.TitleEntry last = handler.createQuery("select b.title, entry(s)" + from, Results.TitleEntry.class).getSingleResult();
            assertEntry(last.entry(), "rating", 2);
            assertEquals("Alpha", last.title());
            Results.Entries repeated = handler.createQuery("select entry(s), entry(s), b.title" + from, Results.Entries.class).getSingleResult();
            assertEntry(repeated.first(), "rating", 2);
            assertEntry(repeated.second(), "rating", 2);
            assertEquals("Alpha", repeated.title());
            Results.MixedEntries mixed = handler.createQuery("select b.id, entry(s), b.title, entry(t), b.quantity"
                    + " from ICRBook b join b.scores s join b.scores t where b.id = 1 and key(s) = 'rating' and key(t) = 'other'",
                    Results.MixedEntries.class).getSingleResult();
            assertEquals(Integer.valueOf(1), mixed.id());
            assertEntry(mixed.first(), "rating", 2);
            assertEntry(mixed.second(), "other", 3);
            assertEquals("Alpha", mixed.title());
            assertEquals(Integer.valueOf(2), mixed.quantity());
        });
    }

    @Test
    public void mapEntryConvertersApplyToKeysAndValuesBeforeConstruction() {
        withHandlers(handler -> {
            Results.ConvertedEntry result = handler.createQuery(
                    "select entry(c) from ICRBook b join b.codes c where b.id = 1", Results.ConvertedEntry.class).getSingleResult();
            assertEntry(result.entry(), new Results.Code("code-key"), new Results.Code("Alpha"));
            assertEntry((Map.Entry<?, ?>) handler.createQuery("select entry(c) from ICRBook b join b.codes c where b.id = 1").getSingleResult(),
                    new Results.Code("code-key"), new Results.Code("Alpha"));
        });
    }

    @Test
    public void distinctMapEntriesRetainDifferentKeysForEqualValues() {
        emf.runInTransaction(em -> em.find(Book.class, 1).scores.put("same-value", 2));
        withHandlers(handler -> {
            List<Results.EntryResult> entries = handler.createQuery(
                    "select distinct entry(s) from ICRBook b join b.scores s join b.tags t where b.id = 1", Results.EntryResult.class).getResultList();
            assertEquals(2, entries.size());
            assertEquals(Set.of("rating", "same-value"), Set.copyOf(entries.stream().map(result -> result.value().getKey()).toList()));
            entries.forEach(result -> assertEquals(2, result.value().getValue()));
        });
    }

    @Test
    public void entityValuedMapEntryConstructorControlRetainsTheEntityArgument() {
        withHandlers(handler -> {
            Results.EntryResult entry = handler.createQuery(
                    "select entry(b) from ICRAuthor a join a.books b where b.id = 1", Results.EntryResult.class).getSingleResult();
            assertEquals("Alpha", entry.value().getKey());
            Book book = (Book) entry.value().getValue();
            assertEquals(Integer.valueOf(1), book.id);
            if (handler instanceof EntityManager em) {
                assertTrue(em.contains(book));
            }
        });
    }

    @Test
    public void typedNullRowsPreserveRedirectorsAndPartitionRouting() {
        AbstractSession session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        withHandlers(handler -> {
            var query = handler.createQuery("select :route from ICRBook b where b.id = 1", Object.class);
            AbstractRecord[] captured = new AbstractRecord[1];
            query.unwrap(DatabaseQuery.class).setRedirector((databaseQuery, arguments, activeSession) -> {
                captured[0] = (AbstractRecord) arguments;
                return List.of("redirected");
            });
            for (Class<?> type : Arrays.asList(null, Integer.class, String.class)) {
                if (type == null) {
                    query.setParameter("route", null);
                } else {
                    query.setParameter("route", null, type);
                }
                assertEquals("redirected", query.getSingleResult());
                assertNull(captured[0].get("route"));
                if (type != null) {
                    assertEquals(type, captured[0].getField(new DatabaseField("route")).getType());
                }
                for (boolean union : List.of(false, true)) {
                    List<String> routed = new ArrayList<>();
                    for (PartitioningPolicy policy : routingPolicies(union, routed)) {
                        routed.clear();
                        var connections = policy.getConnectionsForQuery(session, query.unwrap(DatabaseQuery.class), captured[0]);
                        if (union) {
                            assertEquals(2, connections.size());
                            assertEquals(Set.of("low", "high"), Set.copyOf(routed));
                        } else {
                            assertNull(connections);
                            assertTrue(routed.isEmpty());
                        }
                    }
                }
            }
            query.setParameter("route", 7);
            assertEquals("redirected", query.getSingleResult());
            assertEquals(7, captured[0].get("route"));
            List<String> routed = new ArrayList<>();
            for (PartitioningPolicy policy : routingPolicies(true, routed)) {
                routed.clear();
                assertEquals(1, policy.getConnectionsForQuery(session, query.unwrap(DatabaseQuery.class), captured[0]).size());
                assertEquals(List.of(policy instanceof RangePartitioningPolicy ? "low"
                        : policy instanceof HashPartitioningPolicy ? "high" : "unmapped"), routed);
            }
            assertEquals("redirected", query.setParameter("route", null, Integer.class).getSingleResult());
            assertNull(captured[0].get("route"));
            assertEquals(Integer.class, captured[0].getField(new DatabaseField("route")).getType());
        });
    }

    private List<PartitioningPolicy> routingPolicies(boolean union, List<String> routed) {
        RangePartitioningPolicy range = new RangePartitioningPolicy("route", union) {
            @Override
            public Accessor getAccessor(String name, AbstractSession session, DatabaseQuery query, boolean dead) {
                routed.add(name);
                return new DatabaseAccessor();
            }
        };
        range.addPartition("low", 0, 9);
        range.addPartition("high", 10, 19);
        HashPartitioningPolicy hash = new HashPartitioningPolicy("route", union) {
            @Override
            public Accessor getAccessor(String name, AbstractSession session, DatabaseQuery query, boolean dead) {
                routed.add(name);
                return new DatabaseAccessor();
            }
        };
        hash.addConnectionPool("low");
        hash.addConnectionPool("high");
        ValuePartitioningPolicy value = new ValuePartitioningPolicy("route", union) {
            @Override
            public Accessor getAccessor(String name, AbstractSession session, DatabaseQuery query, boolean dead) {
                routed.add(name);
                return new DatabaseAccessor();
            }
        };
        value.addPartition(1, "low");
        value.addPartition(2, "high");
        value.setDefaultConnectionPool("unmapped");
        return List.of(range, hash, value);
    }

    @Test
    public void cachedNamedQueriesKeepTypedAndDefaultShapesAcrossRealHits() {
        AbstractSession session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        AtomicInteger selects = new AtomicInteger();
        SessionEventAdapter listener = selectCounter(selects);
        session.getEventManager().addListener(listener);
        try (EntityManager em = emf.createEntityManager()) {
            for (String name : List.of("ICR.raw", "ICR.summary")) {
                for (boolean reverse : List.of(false, true)) {
                    session.getIdentityMapAccessor().clearQueryCache(name);
                    Class<?> firstType = reverse ? Results.Ordinary.class : Results.Summary.class;
                    Class<?> secondType = reverse ? Results.Summary.class : Results.Ordinary.class;
                    var first = em.createNamedQuery(name, firstType).setHint("eclipselink.query-results-cache", true)
                            .setHint("eclipselink.read-only", true);
                    var second = em.createNamedQuery(name, secondType).setHint("eclipselink.query-results-cache", true)
                            .setHint("eclipselink.read-only", true);
                    assertSummaryShape(first.getResultList(), firstType);
                    assertSummaryShape(second.getResultList(), secondType);
                    assertSummaryShape(first.getResultList(), firstType);
                    int warmed = selects.get();
                    assertSummaryShape(second.getResultList(), secondType);
                    assertSummaryShape(first.getResultList(), firstType);
                    assertEquals(warmed, selects.get());
                    session.getIdentityMapAccessor().clearQueryCache(name);
                    assertSummaryShape(first.getResultList(), firstType);
                    assertEquals(warmed + 1, selects.get());
                }
                session.getIdentityMapAccessor().clearQueryCache(name);
                var original = em.createNamedQuery(name).setHint("eclipselink.query-results-cache", true)
                        .setHint("eclipselink.read-only", true);
                var override = em.createNamedQuery(name, Results.Ordinary.class).setHint("eclipselink.query-results-cache", true)
                        .setHint("eclipselink.read-only", true);
                Class<?> defaultType = name.equals("ICR.raw") ? Object[].class : Results.Summary.class;
                assertSummaryShape(original.getResultList(), defaultType);
                assertSummaryShape(override.getResultList(), Results.Ordinary.class);
                assertSummaryShape(original.getResultList(), defaultType);
                int warmed = selects.get();
                assertSummaryShape(override.getResultList(), Results.Ordinary.class);
                assertSummaryShape(original.getResultList(), defaultType);
                assertEquals(warmed, selects.get());
                assertEquals(name, override.unwrap(DatabaseQuery.class).getName());
            }
        } finally {
            session.getEventManager().removeListener(listener);
            session.getIdentityMapAccessor().clearQueryCache("ICR.raw");
            session.getIdentityMapAccessor().clearQueryCache("ICR.summary");
        }
    }

    private SessionEventAdapter selectCounter(AtomicInteger count) {
        return new SessionEventAdapter() {
            @Override
            public void preExecuteCall(SessionEvent event) {
                if (event.getCall() instanceof SQLCall call && call.getSQLString().startsWith("SELECT")) {
                    count.incrementAndGet();
                }
            }
        };
    }

    private void assertSummaryShape(List<?> values, Class<?> type) {
        assertEquals(3, values.size());
        for (int index = 0; index < values.size(); index++) {
            Object value = values.get(index);
            assertEquals(type, value.getClass());
            Results.Summary expected = SUMMARIES.get(index);
            if (value instanceof Results.Summary summary) {
                assertEquals(expected, summary);
            } else if (value instanceof Results.Ordinary ordinary) {
                assertEquals(expected.id(), ordinary.id);
                assertEquals(expected.title(), ordinary.title);
            } else {
                assertArrayEquals(new Object[]{expected.id(), expected.title()}, (Object[]) value);
            }
        }
    }

    @Test
    public void cachedParameterBindingsKeepResolvedConstructorSignatures() {
        AbstractSession session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        String name = "ICR.cachedBinding";
        try (EntityManager em = emf.createEntityManager()) {
            emf.addNamedQuery(name, em.createQuery("select :value from ICRBook b where b.id = 1", Object.class));
            var integer = em.createNamedQuery(name, Results.CachedBinding.class).setHint("eclipselink.query-results-cache", true)
                    .setHint("eclipselink.read-only", true).setParameter("value", null, Integer.class);
            var string = em.createNamedQuery(name, Results.CachedBinding.class).setHint("eclipselink.query-results-cache", true)
                    .setHint("eclipselink.read-only", true).setParameter("value", null, String.class);
            for (int repeat = 0; repeat < 3; repeat++) {
                assertEquals("Integer", integer.getSingleResult().selected);
                assertEquals("String", string.getSingleResult().selected);
            }
            assertEquals(7, integer.setParameter("value", 7).getSingleResult().value);
            assertEquals("text", string.setParameter("value", "text").getSingleResult().value);
            assertEquals("Integer", integer.setParameter("value", null, Integer.class).getSingleResult().selected);
            assertEquals("String", string.setParameter("value", null, String.class).getSingleResult().selected);
        } finally {
            session.getIdentityMapAccessor().clearQueryCache(name);
        }
    }

    @Test
    public void distinctEntriesSeparateDelimiterCollisionsAndRepeatedComponents() {
        emf.runInTransaction(em -> {
            Book first = em.find(Book.class, 1);
            first.labels.put("b_c", "a");
            first.labels.put("c", "a_b");
            first.tags.add("duplicate join");
            em.find(Book.class, 2).labels.put("b_c", "a");
        });
        withHandlers(handler -> {
            String from = " from ICRBook b join b.labels l join b.tags t where b.id <= 2";
            List<?> untyped = handler.createQuery("select distinct entry(l)" + from).getResultList();
            assertEquals(2, untyped.size());
            assertEquals(Set.of(Map.entry("b_c", "a"), Map.entry("c", "a_b")),
                    Set.copyOf(untyped.stream().map(value -> entryValue((Map.Entry<?, ?>) value)).toList()));
            for (String select : List.of("select distinct entry(l)", "select distinct new " + Results.EntryResult.class.getName() + "(entry(l))")) {
                List<Results.EntryResult> values = handler.createQuery(select + from, Results.EntryResult.class).getResultList();
                assertEquals(2, values.size());
                assertEquals(Set.of(Map.entry("b_c", "a"), Map.entry("c", "a_b")),
                        Set.copyOf(values.stream().map(value -> entryValue(value.value())).toList()));
            }
            List<Results.MixedEntries> mixed = handler.createQuery("select distinct b.id, entry(l), b.title, entry(l), b.quantity"
                    + " from ICRBook b join b.labels l join b.tags t where b.id = 1", Results.MixedEntries.class).getResultList();
            assertEquals(2, mixed.size());
            mixed.forEach(value -> {
                assertEquals(Integer.valueOf(1), value.id());
                assertEquals("Alpha", value.title());
                assertEquals(Integer.valueOf(2), value.quantity());
                assertEquals(entryValue(value.first()), entryValue(value.second()));
            });
        });
    }

    private Map.Entry<?, ?> entryValue(Map.Entry<?, ?> value) {
        return Map.entry(value.getKey(), value.getValue());
    }

    @Test
    public void distinctConvertedEntriesUseEqualityDespiteEqualHashes() {
        Results.Code first = new Results.Code("Aa");
        Results.Code second = new Results.Code("BB");
        assertEquals(first.hashCode(), second.hashCode());
        emf.runInTransaction(em -> {
            Book book = em.find(Book.class, 1);
            book.codes.clear();
            book.codes.put(first, new Results.Code("same"));
            book.codes.put(second, new Results.Code("same"));
            book.tags.add("duplicate join");
        });
        withHandlers(handler -> {
            List<Results.ConvertedEntry> values = handler.createQuery("select distinct entry(c) from ICRBook b"
                    + " join b.codes c join b.tags t where b.id = 1", Results.ConvertedEntry.class).getResultList();
            assertEquals(2, values.size());
            assertEquals(Set.of(first, second), Set.copyOf(values.stream().map(value -> value.entry().getKey()).toList()));
            values.forEach(value -> assertEquals(new Results.Code("same"), value.entry().getValue()));
        });
    }

    @Test
    public void programmaticScalarReportsHonorConfiguredNullDefaults() {
        AbstractSession session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        Object previous = session.getPlatform().getConversionManager().getDefaultNullValue(String.class);
        try {
            for (String sentinel : Arrays.asList(null, "SENTINEL")) {
                session.getLogin().setDefaultNullValue(String.class, sentinel);
                for (String literal : Arrays.asList(null, "actual")) {
                    ExpressionBuilder builder = new ExpressionBuilder();
                    ReportQuery report = new ReportQuery(Book.class, builder);
                    report.addArgument("literal", String.class);
                    report.addItem("literal", builder.getParameter("literal"));
                    report.setSelectionCriteria(builder.get("id").equal(1));
                    report.returnSingleAttribute();
                    // The argument supplies SQL typing only. No mapping or result
                    // converter may apply the default before scalar packaging.
                    assertEquals(Arrays.asList(literal), session.executeQuery(report, Arrays.asList(literal)));
                    assertNull(report.getItems().getFirst().getMapping());
                    assertNull(report.getItems().getFirst().getDescriptor());
                    report.setResultClass(String.class);
                    assertEquals(Arrays.asList(literal == null ? sentinel : literal),
                            session.executeQuery(report, Arrays.asList(literal)));
                }
            }
            withHandlers(handler -> assertEquals(new Results.Summary(1, "Alpha"), handler.createQuery(
                    "select b.id, b.title from ICRBook b where b.id = 1", Results.Summary.class).getSingleResult()));
        } finally {
            session.getLogin().setDefaultNullValue(String.class, previous);
        }
    }

    private void assertEntry(Map.Entry<?, ?> entry, Object key, Object value) {
        assertEquals(key, entry.getKey());
        assertEquals(value, entry.getValue());
    }

    private record QueryReference<T>(String name, Class<T> type) implements TypedQueryReference<T> {
        @Override
        public String getName() { return name; }
        @Override
        public Class<T> getResultType() { return type; }
        @Override
        public Map<String, Object> getHints() { return Map.of(); }
        @Override
        public Set<TypedQuery.Option> getOptions() { return Set.of(); }
        @Override
        public String getEntityGraphName() { return null; }
        @Override
        public List<Class<?>> getParameterTypes() { return null; }
        @Override
        public List<String> getParameterNames() { return null; }
        @Override
        public List<Object> getArguments() { return null; }
    }
}
