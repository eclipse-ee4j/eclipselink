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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.PluralExpression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaBuilderImpl;
import org.eclipse.persistence.internal.jpa.querydef.InternalSelection;
import org.eclipse.persistence.internal.jpa.querydef.PluralExpressionImpl;
import org.eclipse.persistence.internal.jpa.querydef.RootImpl;
import org.eclipse.persistence.jpa.test.criteria.model.PluralCriteriaDetails;
import org.eclipse.persistence.jpa.test.criteria.model.PluralCriteriaDetails_;
import org.eclipse.persistence.jpa.test.criteria.model.PluralCriteriaElement;
import org.eclipse.persistence.jpa.test.criteria.model.PluralCriteriaElement_;
import org.eclipse.persistence.jpa.test.criteria.model.PluralCriteriaOwner;
import org.eclipse.persistence.jpa.test.criteria.model.PluralCriteriaOwner_;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Collection-level behavior, including owner correlation and SQL three-valued logic. */
@RunWith(EmfRunner.class)
public class TestPluralCriteriaExpressions {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {PluralCriteriaOwner.class, PluralCriteriaElement.class, PluralCriteriaDetails.class},
            properties = @Property(name = "eclipselink.cache.shared.default", value = "false"))
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        emf.runInTransaction(em -> {
            PluralCriteriaElement red = new PluralCriteriaElement(1, 1, "red");
            PluralCriteriaElement blue = new PluralCriteriaElement(1, 2, "blue");
            PluralCriteriaElement other = new PluralCriteriaElement(2, 1, "other");
            for (PluralCriteriaElement element : List.of(red, blue, other)) {
                em.persist(element);
            }
            PluralCriteriaOwner first = new PluralCriteriaOwner(1);
            first.nullableText = "red";
            first.tags.addAll(List.of("red", "blue"));
            first.labels.addAll(List.of("red", "red", "blue"));
            first.values.addAll(List.of("red", "green"));
            first.codes.put("red-key", "blue");
            first.codes.put("other-key", "red");
            first.codes.put("duplicate-key", "red");
            first.elements.addAll(List.of(red, blue));
            first.details.notes.addAll(List.of("inner", "other"));
            PluralCriteriaOwner second = new PluralCriteriaOwner(2);
            second.tags.add("blue");
            second.labels.add("blue");
            second.values.add("blue");
            second.codes.put("red-key", "blue");
            second.elements.add(other);
            second.details.notes.add("different");
            first.parent = second;
            PluralCriteriaOwner empty = new PluralCriteriaOwner(3);
            empty.tags = null;
            empty.labels = null;
            empty.values = null;
            empty.codes = null;
            for (PluralCriteriaOwner owner : List.of(second, first, empty, new PluralCriteriaOwner(4))) {
                em.persist(owner);
            }
            em.flush();
            // Controlled owned rows prove cardinality when a represented value is null.
            em.createNativeQuery("INSERT INTO JPA_PC_LABEL (OWNER_ID, LABEL_POS, LABEL_VALUE) VALUES (4, 0, NULL)").executeUpdate();
            em.createNativeQuery("INSERT INTO JPA_PC_MAP (OWNER_ID, MAP_KEY, MAP_VALUE) VALUES (4, 'null-key', NULL)").executeUpdate();
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            for (String table : List.of("JPA_PC_OWNER_ELEMENT", "JPA_PC_NOTE", "JPA_PC_MAP", "JPA_PC_VALUE", "JPA_PC_LABEL", "JPA_PC_TAG")) {
                em.createNativeQuery("DELETE FROM " + table).executeUpdate();
            }
            em.createQuery("update PluralCriteriaOwner o set o.parent = null").executeUpdate();
            em.createQuery("delete from PluralCriteriaOwner").executeUpdate();
            em.createQuery("delete from PluralCriteriaElement").executeUpdate();
        });
    }

    @Test
    public void testEmptinessCardinalityAndProjectionComposition() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            var tags = root.get(PluralCriteriaOwner_.tags);
            var labels = root.get(PluralCriteriaOwner_.labels);
            var values = root.get(PluralCriteriaOwner_.values);
            var codes = root.get(PluralCriteriaOwner_.codes);
            cq.multiselect(root.get(PluralCriteriaOwner_.id), tags.size(), labels.size(), values.size(), codes.size(),
                    tags.isEmpty(), tags.isNotEmpty(), cb.sum(labels.size(), 1));
            cq.orderBy(cb.asc(root.get(PluralCriteriaOwner_.id)));
            List<Tuple> rows = handler.createQuery(cq).getResultList();
            assertEquals(4, rows.size());
            int[][] counts = {{2, 3, 2, 3}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 1, 0, 1}};
            for (int row = 0; row < rows.size(); row++) {
                assertEquals(row + 1, rows.get(row).get(0));
                for (int column = 0; column < 4; column++) {
                    assertEquals(Integer.class, rows.get(row).get(column + 1).getClass());
                    assertEquals(counts[row][column], rows.get(row).get(column + 1));
                }
                assertEquals(counts[row][0] == 0, rows.get(row).get(5));
                assertEquals(counts[row][0] != 0, rows.get(row).get(6));
                assertEquals(counts[row][1] + 1, rows.get(row).get(7));
            }
            CriteriaQuery<Integer> scalar = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> scalarRoot = scalar.from(PluralCriteriaOwner.class);
            scalar.select(scalarRoot.get(PluralCriteriaOwner_.labels).size()).where(cb.equal(scalarRoot.get(PluralCriteriaOwner_.id), 3));
            assertEquals(Integer.valueOf(0), handler.createQuery(scalar).getSingleResult());
            CriteriaQuery<CountProjection> projected = cb.createQuery(CountProjection.class);
            Root<PluralCriteriaOwner> projectedRoot = projected.from(PluralCriteriaOwner.class);
            projected.select(cb.construct(CountProjection.class, projectedRoot.get(PluralCriteriaOwner_.codes).size()))
                    .where(cb.equal(projectedRoot.get(PluralCriteriaOwner_.id), 1));
            assertEquals(Integer.valueOf(3), handler.createQuery(projected).getSingleResult().count);
        });
    }

    @Test
    public void testEmptinessPredicateIncludesNullCollections() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            var tags = root.get(PluralCriteriaOwner_.tags);
            cq.select(root.get(PluralCriteriaOwner_.id)).where(tags.isEmpty()).orderBy(cb.asc(root.get(PluralCriteriaOwner_.id)));
            assertEquals(List.of(3, 4), em.createQuery(cq).getResultList());
            cq.where(tags.isNotEmpty());
            assertEquals(List.of(1, 2), em.createQuery(cq).getResultList());
        }
    }

    @Test
    public void testNegativeMembershipEmptyAndMixedCollections() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            var tags = root.get(PluralCriteriaOwner_.tags);
            cq.select(root.get(PluralCriteriaOwner_.id)).where(tags.notContains("red"))
                    .orderBy(cb.asc(root.get(PluralCriteriaOwner_.id)));
            assertEquals(List.of(2, 3, 4), em.createQuery(cq).getResultList());
            ParameterExpression<String> member = cb.parameter(String.class);
            cq.where(tags.notContains(member));
            TypedQuery<Integer> query = em.createQuery(cq);
            assertEquals(List.of(2, 3, 4), query.setParameter(member, "red").getResultList());
            assertEquals(List.of(3, 4), query.setParameter(member, null).getResultList());
        }
    }

    @Test
    public void testIndependentMembershipAndMapValues() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            var tags = root.get(PluralCriteriaOwner_.tags);
            cq.select(root.get(PluralCriteriaOwner_.id)).orderBy(cb.asc(root.get(PluralCriteriaOwner_.id)));
            cq.where(cb.and(tags.contains("red"), tags.contains("blue")));
            assertEquals(List.of(1), em.createQuery(cq).getResultList());
            cq.where(tags.notContains("red"));
            assertEquals(List.of(2, 3, 4), em.createQuery(cq).getResultList());
            cq.where(root.get(PluralCriteriaOwner_.labels).contains(cb.literal("red")));
            assertEquals(List.of(1), em.createQuery(cq).getResultList());
            cq.where(root.get(PluralCriteriaOwner_.codes).contains("red-key"));
            assertEquals(List.of(), em.createQuery(cq).getResultList());
            cq.where(root.get(PluralCriteriaOwner_.codes).contains("red"));
            assertEquals(List.of(1), em.createQuery(cq).getResultList());
            cq.where(root.get(PluralCriteriaOwner_.codes).notContains(cb.literal("red")));
            assertEquals(List.of(2, 3, 4), em.createQuery(cq).getResultList());
        }
    }

    @Test
    public void testNullOperandTruthTableAndOriginalHandleRebinding() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            ParameterExpression<String> member = cb.parameter(String.class, "member");
            var values = root.get(PluralCriteriaOwner_.codes);
            var positive = values.contains(member);
            var negative = values.notContains(member);
            cq.multiselect(root.get(PluralCriteriaOwner_.id), positive, negative, cb.not(positive),
                    cb.selectCase(Integer.class).when(positive, 1).when(negative, 2).otherwise(3));
            cq.orderBy(cb.asc(root.get(PluralCriteriaOwner_.id)));
            assertTrue(cq.getParameters().contains(member));
            TypedQuery<Tuple> query = handler.createQuery(cq);
            assertSame(member, query.getParameter("member", String.class));
            for (String binding : Arrays.asList(null, "red", "missing", "blue", null)) {
                List<Tuple> rows = query.setParameter(member, binding).getResultList();
                for (int i = 0; i < 4; i++) {
                    boolean empty = i == 2;
                    Boolean expected;
                    if (empty) {
                        expected = Boolean.FALSE;
                    } else if (binding == null) {
                        expected = null;
                    } else {
                        expected = i == 0 ? Set.of("red", "blue").contains(binding) : i == 1 && binding.equals("blue");
                    }
                    Boolean negated = expected == null ? null : Boolean.valueOf(!expected);
                    assertEquals(expected, rows.get(i).get(1));
                    assertEquals(negated, rows.get(i).get(2));
                    assertEquals(negated, rows.get(i).get(3));
                    assertEquals(expected == null ? 3 : expected ? 1 : 2, rows.get(i).get(4));
                }
            }
        });
    }

    @Test
    public void testCompositeEntityMembership() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            ParameterExpression<PluralCriteriaElement> parameter = cb.parameter(PluralCriteriaElement.class, "element");
            cq.select(root.get(PluralCriteriaOwner_.id)).where(root.get(PluralCriteriaOwner_.elements).contains(parameter));
            TypedQuery<Integer> query = em.createQuery(cq);
            assertEquals(List.of(1), query.setParameter(parameter, new PluralCriteriaElement(1, 1, "detached")).getResultList());
            assertEquals(List.of(2), query.setParameter(parameter, new PluralCriteriaElement(2, 1, "detached")).getResultList());
            assertEquals(List.of(), query.setParameter(parameter, null).getResultList());
            assertEquals(List.of(), query.setParameter(parameter, new PluralCriteriaElement(9, 9, "nonexistent")).getResultList());
            cq.where(root.get(PluralCriteriaOwner_.elements).notContains(parameter));
            cq.orderBy(cb.asc(root.get(PluralCriteriaOwner_.id)));
            assertEquals(List.of(2, 3, 4), em.createQuery(cq).setParameter(parameter, new PluralCriteriaElement(1, 1, "detached")).getResultList());
            assertEquals(List.of(1, 2, 3, 4), em.createQuery(cq).setParameter(parameter, new PluralCriteriaElement(9, 9, "nonexistent")).getResultList());
            cq.where(root.get(PluralCriteriaOwner_.elements).contains(new PluralCriteriaElement(1, 2, "literal")));
            assertEquals(List.of(1), em.createQuery(cq).getResultList());
        }
    }

    @Test
    public void testRuntimeMetamodelFactoriesAndCardinalityOrdering() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            var type = emf.getMetamodel().entity(PluralCriteriaOwner.class);
            var tags = root.get(type.getSet("tags", String.class));
            var labels = root.get(type.getList("labels", String.class));
            var values = root.get(type.getCollection("values", String.class));
            var codes = root.get(type.getMap("codes", String.class, String.class));
            var count = values.size();
            count.alias("valueCount");
            cq.multiselect(root.get(PluralCriteriaOwner_.id), count, tags.contains("red"),
                    labels.notContains("missing"), values.contains(cb.literal("green")), codes.contains("blue"));
            cq.where(count.ge(1)).orderBy(cb.desc(count));
            List<Tuple> rows = handler.createQuery(cq).getResultList();
            assertEquals(2, rows.size());
            assertEquals(1, rows.get(0).get(0));
            assertEquals(Integer.valueOf(2), rows.get(0).get("valueCount", Integer.class));
            assertEquals(List.of(true, true, true, true), rows.get(0).getElements().subList(2, 6).stream()
                    .map(rows.get(0)::get).toList());
            assertEquals(2, rows.get(1).get(0));
            assertEquals(Integer.valueOf(1), rows.get(1).get(count));
            assertEquals(List.of(false, true, false, true), rows.get(1).getElements().subList(2, 6).stream()
                    .map(rows.get(1)::get).toList());
            assertEquals("valueCount", count.getAlias());
            assertEquals(Integer.class, count.getJavaType());
            assertEquals(Set.of(root), cq.getRoots());

            CriteriaQuery<Integer> unselected = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> unselectedOwner = unselected.from(PluralCriteriaOwner.class);
            var unselectedCount = unselectedOwner.get(PluralCriteriaOwner_.labels).size();
            unselected.select(unselectedOwner.get(PluralCriteriaOwner_.id))
                    .orderBy(cb.desc(unselectedCount), cb.asc(unselectedOwner.get(PluralCriteriaOwner_.id)));
            assertEquals(List.of(1, 2, 4, 3), handler.createQuery(unselected).getResultList());
            unselected.select(cb.sum(unselectedCount, 1)).orderBy(cb.asc(unselectedCount));
            assertEquals(List.of(1, 2, 2, 4), handler.createQuery(unselected).getResultList());
            unselected.select(unselectedCount).distinct(true).orderBy(cb.asc(unselectedCount));
            assertEquals(List.of(0, 1, 3), handler.createQuery(unselected).getResultList());
            var nullableCount = cb.nullif(unselectedCount, 0);
            unselected.distinct(false).select(unselectedOwner.get(PluralCriteriaOwner_.id))
                    .orderBy(cb.asc(nullableCount, Nulls.FIRST), cb.asc(unselectedOwner.get(PluralCriteriaOwner_.id)));
            assertEquals(List.of(3, 2, 4, 1), handler.createQuery(unselected).getResultList());
            unselected.orderBy(cb.desc(nullableCount, Nulls.LAST), cb.asc(unselectedOwner.get(PluralCriteriaOwner_.id)));
            assertEquals(List.of(1, 2, 4, 3), handler.createQuery(unselected).getResultList());
            ParameterExpression<Integer> offset = cb.parameter(Integer.class, "sortOffset");
            unselected.orderBy(cb.desc(cb.sum(unselectedCount, offset), Nulls.LAST),
                    cb.asc(unselectedOwner.get(PluralCriteriaOwner_.id)));
            TypedQuery<Integer> reboundOrder = handler.createQuery(unselected);
            assertSame(offset, reboundOrder.getParameter("sortOffset", Integer.class));
            for (Integer binding : Arrays.asList(null, 2, null)) {
                assertEquals(binding == null ? List.of(1, 2, 3, 4) : List.of(1, 2, 4, 3),
                        reboundOrder.setParameter(offset, binding).getResultList());
            }

            CriteriaQuery<PluralCriteriaOwner> owners = cb.createQuery(PluralCriteriaOwner.class);
            Root<PluralCriteriaOwner> selectedOwner = owners.from(PluralCriteriaOwner.class);
            owners.select(selectedOwner).distinct(true).where(cb.le(selectedOwner.get(PluralCriteriaOwner_.id), 2))
                    .orderBy(cb.desc(selectedOwner.get(PluralCriteriaOwner_.labels).size()));
            assertEquals(List.of(1, 2), handler.createQuery(owners).getResultList().stream().map(value -> value.id).toList());

            CriteriaQuery<CountProjection> constructed = cb.createQuery(CountProjection.class);
            Root<PluralCriteriaOwner> constructedOwner = constructed.from(PluralCriteriaOwner.class);
            constructed.select(cb.construct(CountProjection.class, constructedOwner.get(PluralCriteriaOwner_.codes).size()))
                    .orderBy(cb.desc(constructedOwner.get(PluralCriteriaOwner_.labels).size()),
                            cb.asc(constructedOwner.get(PluralCriteriaOwner_.id)));
            assertEquals(List.of(3, 1, 1, 0), handler.createQuery(constructed).getResultList().stream()
                    .map(value -> value.count).toList());

            CriteriaQuery<Tuple> expanded = cb.createTupleQuery();
            Root<PluralCriteriaOwner> expandedOwner = expanded.from(PluralCriteriaOwner.class);
            Root<PluralCriteriaElement> entity = expanded.from(PluralCriteriaElement.class);
            var expandedCount = expandedOwner.get(PluralCriteriaOwner_.labels).size();
            expanded.multiselect(entity, expandedCount).where(
                    cb.equal(entity.get(PluralCriteriaElement_.firstId), 1),
                    cb.equal(entity.get(PluralCriteriaElement_.secondId), 1));
            expanded.orderBy(cb.desc(expandedCount), cb.asc(expandedOwner.get(PluralCriteriaOwner_.id)));
            List<Tuple> expandedRows = handler.createQuery(expanded).getResultList();
            assertEquals(List.of(3, 1, 1, 0), expandedRows.stream().map(row -> row.get(1, Integer.class)).toList());
            for (Tuple row : expandedRows) {
                assertEquals(2, row.getElements().size());
                PluralCriteriaElement selected = row.get(0, PluralCriteriaElement.class);
                assertEquals(1, selected.firstId);
                assertEquals(1, selected.secondId);
            }
            expanded.distinct(true).orderBy(cb.desc(expandedCount));
            assertEquals(List.of(3, 1, 0), handler.createQuery(expanded).getResultList().stream()
                    .map(row -> row.get(1, Integer.class)).toList());

            CriteriaQuery<Tuple> paged = cb.createTupleQuery();
            Root<PluralCriteriaOwner> pagedOwner = paged.from(PluralCriteriaOwner.class);
            Root<PluralCriteriaElement> pagedEntity = paged.from(PluralCriteriaElement.class);
            ParameterExpression<Integer> adjustment = cb.parameter(Integer.class, "pageAdjustment");
            var pagedCount = cb.nullif(cb.sum(pagedOwner.get(PluralCriteriaOwner_.labels).size(), adjustment), 0);
            pagedCount.alias("pagedCount");
            paged.multiselect(pagedEntity, pagedCount, pagedOwner.get(PluralCriteriaOwner_.id)).distinct(true)
                    .where(cb.equal(pagedEntity.get(PluralCriteriaElement_.firstId), 1),
                            cb.equal(pagedEntity.get(PluralCriteriaElement_.secondId), 1));
            for (Nulls precedence : List.of(Nulls.FIRST, Nulls.LAST)) {
                paged.orderBy(precedence == Nulls.FIRST ? cb.asc(pagedCount, precedence) : cb.desc(pagedCount, precedence),
                        cb.asc(pagedOwner.get(PluralCriteriaOwner_.id)));
                TypedQuery<Tuple> pagedQuery = handler.createQuery(paged);
                assertSame(adjustment, pagedQuery.getParameter("pageAdjustment", Integer.class));
                for (Integer binding : Arrays.asList(null, 0, null)) {
                    List<Integer> expected = binding == null ? List.of(1, 2, 3, 4)
                            : precedence == Nulls.FIRST ? List.of(3, 2, 4, 1) : List.of(1, 2, 4, 3);
                    pagedQuery.setParameter(adjustment, binding).setFirstResult(1).setMaxResults(2);
                    List<Tuple> page = pagedQuery.getResultList();
                    assertEquals(expected.subList(1, 3), page.stream().map(row -> row.get(2, Integer.class)).toList());
                    for (Tuple row : page) {
                        assertEquals(3, row.getElements().size());
                        assertEquals(1, row.get(0, PluralCriteriaElement.class).firstId);
                        assertEquals(1, row.get(0, PluralCriteriaElement.class).secondId);
                        assertEquals(row.get(1), row.get("pagedCount", Integer.class));
                    }
                    assertEquals(expected.subList(0, 1), pagedQuery.setFirstResult(0).setMaxResults(1).getResultList()
                            .stream().map(row -> row.get(2, Integer.class)).toList());
                    assertEquals(expected, pagedQuery.setMaxResults(Integer.MAX_VALUE).getResultList()
                            .stream().map(row -> row.get(2, Integer.class)).toList());
                }
                assertEquals(precedence, paged.getOrderList().getFirst().getNullPrecedence());
                assertEquals(Set.of(pagedOwner, pagedEntity), paged.getRoots());
            }
        });
    }

    @Test
    public void testJoinedEmbeddedAndOptionalOwners() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            var parent = root.join(PluralCriteriaOwner_.parent);
            cq.select(root.get(PluralCriteriaOwner_.id)).where(parent.get(PluralCriteriaOwner_.tags).contains("blue"));
            assertEquals(List.of(1), em.createQuery(cq).getResultList());
            cq.where(parent.get(PluralCriteriaOwner_.tags).contains("red"));
            assertEquals(List.of(), em.createQuery(cq).getResultList());
            cq.where(parent.get(PluralCriteriaOwner_.details).get(PluralCriteriaDetails_.notes).contains("different"));
            assertEquals(List.of(1), em.createQuery(cq).getResultList());

            CriteriaQuery<Integer> embedded = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> embeddedRoot = embedded.from(PluralCriteriaOwner.class);
            embedded.select(embeddedRoot.get(PluralCriteriaOwner_.id))
                    .where(embeddedRoot.get(PluralCriteriaOwner_.details).get(PluralCriteriaDetails_.notes).contains("inner"));
            assertEquals(List.of(1), em.createQuery(embedded).getResultList());

            CriteriaQuery<Integer> implicit = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> implicitRoot = implicit.from(PluralCriteriaOwner.class);
            implicit.select(implicitRoot.get(PluralCriteriaOwner_.id))
                    .where(implicitRoot.get(PluralCriteriaOwner_.parent).get(PluralCriteriaOwner_.tags).isNotEmpty());
            assertEquals(List.of(1), em.createQuery(implicit).getResultList());
            implicit.select(implicitRoot.get(PluralCriteriaOwner_.parent).get(PluralCriteriaOwner_.tags).size()).where();
            TypedQuery<Integer> implicitSize = em.createQuery(implicit);
            assertEquals(List.of(1), implicitSize.getResultList());
            implicit.select(implicitRoot.get(PluralCriteriaOwner_.id)).orderBy(cb.asc(implicitRoot.get(PluralCriteriaOwner_.id)));
            assertEquals(List.of(1, 2, 3, 4), em.createQuery(implicit).getResultList());
            assertEquals(List.of(1), implicitSize.getResultList());

            CriteriaQuery<Tuple> left = cb.createTupleQuery();
            Root<PluralCriteriaOwner> leftRoot = left.from(PluralCriteriaOwner.class);
            var optional = leftRoot.join(PluralCriteriaOwner_.parent, JoinType.LEFT);
            var optionalTags = optional.get(PluralCriteriaOwner_.tags);
            left.multiselect(leftRoot.get(PluralCriteriaOwner_.id), optionalTags.size(), optionalTags.isEmpty());
            left.orderBy(cb.asc(leftRoot.get(PluralCriteriaOwner_.id)));
            List<Tuple> rows = em.createQuery(left).getResultList();
            assertEquals(4, rows.size());
            for (int i = 0; i < rows.size(); i++) {
                assertEquals(i + 1, rows.get(i).get(0));
                assertEquals(i == 0 ? 1 : 0, rows.get(i).get(1));
                assertEquals(i != 0, rows.get(i).get(2));
            }
            assertEquals(Set.of(leftRoot), left.getRoots());
        }
    }

    @Test
    public void testImplicitOwnerTruthFilteringAndSnapshots() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<PluralCriteriaOwner> owner = cq.from(PluralCriteriaOwner.class);
            var ownerId = owner.get(PluralCriteriaOwner_.id);
            var tags = owner.get(PluralCriteriaOwner_.parent).get(PluralCriteriaOwner_.tags);
            tags.alias("parentTags");
            ParameterExpression<String> member = cb.parameter(String.class, "parentMember");
            var empty = tags.isEmpty();
            var nonempty = tags.isNotEmpty();
            var positive = tags.contains(member);
            var negative = tags.notContains(member);
            List<Expression<Boolean>> truths = List.of(empty, nonempty, positive, negative);
            cq.orderBy(cb.asc(ownerId));
            for (int index = 0; index < truths.size(); index++) {
                cq.multiselect(ownerId, truths.get(index));
                TypedQuery<Tuple> independent = handler.createQuery(cq);
                for (String binding : Arrays.asList(null, "blue", "red", null)) {
                    if (independent.getParameters().contains(member)) {
                        assertSame(member, independent.getParameter("parentMember", String.class));
                        independent.setParameter(member, binding);
                    }
                    Boolean present = binding == null ? null : "blue".equals(binding);
                    Boolean absent = present == null ? null : !present;
                    Object expected = Arrays.asList(false, true, present, absent).get(index);
                    List<Tuple> rows = independent.getResultList();
                    assertEquals(1, rows.size());
                    assertEquals(Arrays.asList(1, expected), Arrays.asList(rows.getFirst().toArray()));
                }
            }
            List<Expression<Boolean>> literalTruths = List.of(tags.contains("blue"), tags.contains("missing"),
                    tags.notContains("blue"), tags.notContains("missing"));
            for (int index = 0; index < literalTruths.size(); index++) {
                cq.multiselect(ownerId, literalTruths.get(index));
                List<Tuple> rows = handler.createQuery(cq).getResultList();
                assertEquals(1, rows.size());
                assertEquals(List.of(1, List.of(true, false, false, true).get(index)),
                        Arrays.asList(rows.getFirst().toArray()));
            }
            cq.multiselect(ownerId, empty, nonempty, positive, negative, cb.not(positive));
            TypedQuery<Tuple> combined = handler.createQuery(cq);
            assertSame(member, combined.getParameter("parentMember", String.class));
            cq.where(empty);
            TypedQuery<Tuple> emptyQuery = handler.createQuery(cq);
            cq.where(nonempty);
            TypedQuery<Tuple> nonemptyQuery = handler.createQuery(cq);
            cq.where(positive);
            TypedQuery<Tuple> positiveQuery = handler.createQuery(cq);
            cq.where(negative);
            TypedQuery<Tuple> negativeQuery = handler.createQuery(cq);
            for (String binding : Arrays.asList(null, "blue", "red", null)) {
                Boolean present = binding == null ? null : "blue".equals(binding);
                Boolean absent = present == null ? null : !present;
                assertEquals(Arrays.asList(1, false, true, present, absent, absent),
                        Arrays.asList(combined.setParameter(member, binding).getSingleResult().toArray()));
                assertEquals(List.of(), emptyQuery.setParameter(member, binding).getResultList());
                assertEquals(List.of(1), nonemptyQuery.setParameter(member, binding).getResultList().stream()
                        .map(row -> row.get(0, Integer.class)).toList());
                assertEquals(Boolean.TRUE.equals(present) ? List.of(1) : List.of(),
                        positiveQuery.setParameter(member, binding).getResultList().stream().map(row -> row.get(0, Integer.class)).toList());
                assertEquals(Boolean.TRUE.equals(absent) ? List.of(1) : List.of(),
                        negativeQuery.setParameter(member, binding).getResultList().stream().map(row -> row.get(0, Integer.class)).toList());
            }
            cq.multiselect(ownerId).where();
            TypedQuery<Tuple> replacement = handler.createQuery(cq);
            assertTrue(replacement.getParameters().isEmpty());
            assertEquals(List.of(1, 2, 3, 4), replacement.getResultList().stream().map(row -> row.get(0, Integer.class)).toList());
            assertEquals(Arrays.asList(1, false, true, true, false, false),
                    Arrays.asList(combined.setParameter(member, "blue").getSingleResult().toArray()));
            assertEquals(List.of(1), negativeQuery.setParameter(member, "red").getResultList().stream()
                    .map(row -> row.get(0, Integer.class)).toList());
            assertEquals("parentTags", tags.getAlias());
            assertEquals(Set.of(owner), cq.getRoots());

            CriteriaQuery<Tuple> left = cb.createTupleQuery();
            Root<PluralCriteriaOwner> leftOwner = left.from(PluralCriteriaOwner.class);
            var optionalTags = leftOwner.join(PluralCriteriaOwner_.parent, JoinType.LEFT).get(PluralCriteriaOwner_.tags);
            var leftEmpty = optionalTags.isEmpty();
            var leftPositive = optionalTags.contains(member);
            var leftNegative = optionalTags.notContains(member);
            left.multiselect(leftOwner.get(PluralCriteriaOwner_.id), leftEmpty, optionalTags.isNotEmpty(), leftPositive, leftNegative)
                    .orderBy(cb.asc(leftOwner.get(PluralCriteriaOwner_.id)));
            TypedQuery<Tuple> leftProjection = handler.createQuery(left);
            left.where(leftEmpty);
            TypedQuery<Tuple> leftEmptyQuery = handler.createQuery(left);
            left.where(leftNegative);
            TypedQuery<Tuple> leftNegativeQuery = handler.createQuery(left);
            for (String binding : Arrays.asList(null, "blue", "red", null)) {
                Boolean present = binding == null ? null : "blue".equals(binding);
                Boolean absent = present == null ? null : !present;
                List<Tuple> rows = leftProjection.setParameter(member, binding).getResultList();
                assertEquals(4, rows.size());
                assertEquals(Arrays.asList(1, false, true, present, absent), Arrays.asList(rows.getFirst().toArray()));
                for (int index = 1; index < rows.size(); index++) {
                    assertEquals(List.of(index + 1, true, false, false, true), Arrays.asList(rows.get(index).toArray()));
                }
                assertEquals(List.of(2, 3, 4), leftEmptyQuery.setParameter(member, binding).getResultList().stream()
                        .map(row -> row.get(0, Integer.class)).toList());
                assertEquals("red".equals(binding) ? List.of(1, 2, 3, 4) : List.of(2, 3, 4),
                        leftNegativeQuery.setParameter(member, binding).getResultList().stream().map(row -> row.get(0, Integer.class)).toList());
            }
        });
    }

    @Test
    public void testEntitySubqueryMembershipNullsAndSnapshots() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<PluralCriteriaOwner> owner = cq.from(PluralCriteriaOwner.class);
            ParameterExpression<Integer> firstId = cb.parameter(Integer.class, "subqueryFirstId");
            Subquery<PluralCriteriaElement> member = cq.subquery(PluralCriteriaElement.class);
            Root<PluralCriteriaElement> lookup = member.from(PluralCriteriaElement.class);
            member.select(lookup).where(cb.equal(lookup.get(PluralCriteriaElement_.firstId), firstId),
                    cb.equal(lookup.get(PluralCriteriaElement_.secondId), 1));
            member.alias("entityMember");
            var originalRestriction = member.getRestriction();
            MembershipQueries saved = assertEntitySubqueryMembership(handler, cb, cq, owner, member, firstId);
            assertSame(lookup, member.getSelection());
            assertSame(originalRestriction, member.getRestriction());
            assertEquals(Set.of(lookup), member.getRoots());
            assertEquals("entityMember", member.getAlias());
            member.where(cb.equal(lookup.get(PluralCriteriaElement_.firstId), firstId),
                    cb.equal(lookup.get(PluralCriteriaElement_.secondId), 2));
            cq.where(owner.get(PluralCriteriaOwner_.elements).contains(member));
            TypedQuery<Tuple> changed = handler.createQuery(cq);
            assertEquals(List.of(2), saved.positive().setParameter(firstId, 2).getResultList().stream()
                    .map(row -> row.get(0, Integer.class)).toList());
            assertEquals(List.of(), changed.setParameter(firstId, 2).getResultList());
            assertEquals(List.of(1, 3, 4), saved.negative().setParameter(firstId, 2).getResultList().stream()
                    .map(row -> row.get(0, Integer.class)).toList());
            member.where(originalRestriction);
            assertEquals(List.of(2), handler.createQuery(cq).setParameter(firstId, 2).getResultList().stream()
                    .map(row -> row.get(0, Integer.class)).toList());
            assertEquals(List.of(), changed.getResultList());

            CriteriaQuery<Tuple> nullable = cb.createTupleQuery();
            Root<PluralCriteriaOwner> nullableOwner = nullable.from(PluralCriteriaOwner.class);
            ParameterExpression<Integer> lookupId = cb.parameter(Integer.class, "nullableLookupId");
            Subquery<PluralCriteriaElement> nullableMember = nullable.subquery(PluralCriteriaElement.class);
            Root<PluralCriteriaOwner> lookupOwner = nullableMember.from(PluralCriteriaOwner.class);
            var nullableElement = lookupOwner.join(PluralCriteriaOwner_.elements, JoinType.LEFT);
            nullableMember.select(nullableElement).where(cb.equal(lookupOwner.get(PluralCriteriaOwner_.id), lookupId),
                    cb.or(cb.isNull(nullableElement.get(PluralCriteriaElement_.firstId)),
                            cb.equal(nullableElement.get(PluralCriteriaElement_.secondId), 1)));
            var nullableRestriction = nullableMember.getRestriction();
            assertEntitySubqueryMembership(handler, cb, nullable, nullableOwner, nullableMember, lookupId);
            assertSame(nullableElement, nullableMember.getSelection());
            assertSame(nullableRestriction, nullableMember.getRestriction());
            assertEquals(Set.of(lookupOwner), nullableMember.getRoots());

            assertLegacyScalarLeftJoinOn(handler, cb);
            assertCorrelatedEntitySubqueryMembership(handler, cb);
            assertDistinctAndGroupedEntitySubqueryMembership(handler, cb);
            assertEntitySubquerySelectionAndParameterMutation(handler, cb);
            assertEntitySubqueryRejectsMultipleIdentities(handler, cb);
        });
    }

    private void assertLegacyScalarLeftJoinOn(EntityHandler handler, CriteriaBuilder cb) {
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<PluralCriteriaOwner> owner = cq.from(PluralCriteriaOwner.class);
        Subquery<Integer> member = cq.subquery(Integer.class);
        Root<PluralCriteriaOwner> correlated = member.correlate(owner);
        Root<PluralCriteriaOwner> lookup = member.from(PluralCriteriaOwner.class);
        ParameterExpression<Integer> secondId = cb.parameter(Integer.class, "legacySecondId");
        ParameterExpression<Integer> sourceId = cb.parameter(Integer.class, "legacySourceId");
        var selected = lookup.join(PluralCriteriaOwner_.elements, JoinType.LEFT);
        selected.on(cb.equal(selected.get(PluralCriteriaElement_.secondId), secondId),
                cb.equal(lookup.get(PluralCriteriaOwner_.id), sourceId));
        var originalOn = selected.getOn();
        member.select(selected.get(PluralCriteriaElement_.secondId))
                .where(cb.equal(lookup.get(PluralCriteriaOwner_.id), correlated.get(PluralCriteriaOwner_.id)));
        cq.select(owner.get(PluralCriteriaOwner_.id)).orderBy(cb.asc(owner.get(PluralCriteriaOwner_.id)))
                .where(cb.equal(member, 1));
        TypedQuery<Integer> present = handler.createQuery(cq);
        cq.where(cb.isNull(member));
        TypedQuery<Integer> absent = handler.createQuery(cq);
        for (TypedQuery<Integer> query : List.of(present, absent)) {
            assertEquals(Set.of(secondId, sourceId), query.getParameters());
            assertSame(secondId, query.getParameter("legacySecondId", Integer.class));
            assertSame(sourceId, query.getParameter("legacySourceId", Integer.class));
        }
        for (Integer binding : Arrays.asList(null, 1, 2, 9, null, 1)) {
            assertEquals(binding != null && binding <= 2 ? List.of(binding) : List.of(),
                    present.setParameter(secondId, 1).setParameter(sourceId, binding).getResultList());
            assertEquals(binding == null || binding > 2 ? List.of(1, 2, 3, 4)
                            : binding == 1 ? List.of(2, 3, 4) : List.of(1, 3, 4),
                    absent.setParameter(secondId, 1).setParameter(sourceId, binding).getResultList());
        }
        assertEquals(List.of(), present.setParameter(secondId, null).getResultList());
        assertEquals(List.of(1, 2, 3, 4), absent.setParameter(secondId, null).getResultList());
        assertSame(originalOn, selected.getOn());
        assertEquals(Set.of(owner), cq.getRoots());
    }

    private void assertCorrelatedEntitySubqueryMembership(EntityHandler handler, CriteriaBuilder cb) {
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<PluralCriteriaOwner> owner = cq.from(PluralCriteriaOwner.class);
        Subquery<PluralCriteriaElement> member = cq.subquery(PluralCriteriaElement.class);
        Root<PluralCriteriaOwner> correlated = member.correlate(owner);
        Root<PluralCriteriaOwner> lookup = member.from(PluralCriteriaOwner.class);
        ParameterExpression<Integer> secondId = cb.parameter(Integer.class, "correlatedSecondId");
        var selected = lookup.join(PluralCriteriaOwner_.elements, JoinType.LEFT);
        selected.on(cb.equal(selected.get(PluralCriteriaElement_.secondId), secondId));
        var originalOn = selected.getOn();
        member.select(selected).where(cb.equal(lookup.get(PluralCriteriaOwner_.id), correlated.get(PluralCriteriaOwner_.id)));
        var originalRestriction = member.getRestriction();
        var positive = owner.get(PluralCriteriaOwner_.elements).contains(member);
        var negative = owner.get(PluralCriteriaOwner_.elements).notContains(member);
        cq.select(owner.get(PluralCriteriaOwner_.id)).orderBy(cb.asc(owner.get(PluralCriteriaOwner_.id))).where(positive);
        TypedQuery<Integer> present = handler.createQuery(cq);
        cq.where(negative);
        TypedQuery<Integer> absent = handler.createQuery(cq);
        cq.where(cb.equal(cb.selectCase(Integer.class).when(positive, 1).when(negative, 0).otherwise(-1), -1));
        TypedQuery<Integer> unknown = handler.createQuery(cq);
        for (TypedQuery<Integer> query : List.of(present, absent, unknown)) {
            assertEquals(Set.of(secondId), query.getParameters());
            assertSame(secondId, query.getParameter("correlatedSecondId", Integer.class));
        }
        for (Integer binding : Arrays.asList(null, 1, 2, 9, null, 1)) {
            List<Integer> expectedPresent = binding == null || binding > 2 ? List.of()
                    : binding == 1 ? List.of(1, 2) : List.of(1);
            List<Integer> expectedUnknown = binding == null || binding > 2 ? List.of(1, 2)
                    : binding == 1 ? List.of() : List.of(2);
            assertEquals(expectedPresent, present.setParameter(secondId, binding).getResultList());
            assertEquals(List.of(3, 4), absent.setParameter(secondId, binding).getResultList());
            assertEquals(expectedUnknown, unknown.setParameter(secondId, binding).getResultList());
        }
        assertSame(selected, member.getSelection());
        assertSame(originalOn, selected.getOn());
        assertSame(originalRestriction, member.getRestriction());
        assertSame(owner, correlated.getCorrelationParent());
        assertEquals(Set.of(owner), cq.getRoots());

        ParameterExpression<Integer> replacement = cb.parameter(Integer.class, "replacementOnSecondId");
        selected.on(cb.equal(selected.get(PluralCriteriaElement_.secondId), replacement));
        cq.where(positive);
        TypedQuery<Integer> changed = handler.createQuery(cq);
        assertEquals(Set.of(replacement), changed.getParameters());
        assertSame(replacement, changed.getParameter("replacementOnSecondId", Integer.class));
        assertEquals(List.of(1), changed.setParameter(replacement, 2).getResultList());
        assertEquals(List.of(1, 2), present.setParameter(secondId, 1).getResultList());
        assertEquals(List.of(), present.setParameter(secondId, null).getResultList());
        assertEquals(List.of(1), changed.getResultList());
    }

    private void assertDistinctAndGroupedEntitySubqueryMembership(EntityHandler handler, CriteriaBuilder cb) {
        CriteriaQuery<Integer> distinctCriteria = cb.createQuery(Integer.class);
        Root<PluralCriteriaOwner> owner = distinctCriteria.from(PluralCriteriaOwner.class);
        Subquery<PluralCriteriaElement> distinctMember = distinctCriteria.subquery(PluralCriteriaElement.class);
        Root<PluralCriteriaOwner> lookup = distinctMember.from(PluralCriteriaOwner.class);
        var selected = lookup.join(PluralCriteriaOwner_.elements);
        // Owner 1 has two tags, so this join repeats the same selected entity.
        lookup.join(PluralCriteriaOwner_.tags);
        ParameterExpression<Integer> ownerId = cb.parameter(Integer.class, "distinctLookupOwner");
        distinctMember.select(selected).distinct(true).where(cb.equal(lookup.get(PluralCriteriaOwner_.id), ownerId),
                cb.equal(selected.get(PluralCriteriaElement_.secondId), 1));
        var distinctRestriction = distinctMember.getRestriction();
        distinctCriteria.select(owner.get(PluralCriteriaOwner_.id)).orderBy(cb.asc(owner.get(PluralCriteriaOwner_.id)))
                .where(owner.get(PluralCriteriaOwner_.elements).contains(distinctMember));
        TypedQuery<Integer> present = handler.createQuery(distinctCriteria);
        distinctCriteria.where(owner.get(PluralCriteriaOwner_.elements).notContains(distinctMember));
        TypedQuery<Integer> absent = handler.createQuery(distinctCriteria);
        for (Integer binding : Arrays.asList(1, 2, 9, null, 1)) {
            assertEquals(binding != null && binding <= 2 ? List.of(binding) : List.of(),
                    present.setParameter(ownerId, binding).getResultList());
            assertEquals(binding == null || binding > 2 ? List.of(3, 4)
                            : binding == 1 ? List.of(2, 3, 4) : List.of(1, 3, 4),
                    absent.setParameter(ownerId, binding).getResultList());
        }
        assertSame(ownerId, present.getParameter("distinctLookupOwner", Integer.class));
        assertSame(selected, distinctMember.getSelection());
        assertSame(distinctRestriction, distinctMember.getRestriction());
        assertTrue(distinctMember.isDistinct());
        assertEquals(2, lookup.getJoins().size());

        CriteriaQuery<Integer> groupedCriteria = cb.createQuery(Integer.class);
        Root<PluralCriteriaOwner> groupedOwner = groupedCriteria.from(PluralCriteriaOwner.class);
        Subquery<PluralCriteriaElement> groupedMember = groupedCriteria.subquery(PluralCriteriaElement.class);
        Root<PluralCriteriaElement> groupedLookup = groupedMember.from(PluralCriteriaElement.class);
        ParameterExpression<Integer> firstId = cb.parameter(Integer.class, "groupedFirstId");
        ParameterExpression<Long> rowCount = cb.parameter(Long.class, "groupedRowCount");
        groupedMember.select(groupedLookup).where(cb.equal(groupedLookup.get(PluralCriteriaElement_.firstId), firstId),
                cb.equal(groupedLookup.get(PluralCriteriaElement_.secondId), 1));
        groupedMember.groupBy(groupedLookup.get(PluralCriteriaElement_.firstId), groupedLookup.get(PluralCriteriaElement_.secondId),
                groupedLookup.get(PluralCriteriaElement_.text)).having(cb.equal(cb.count(groupedLookup), rowCount));
        var grouping = groupedMember.getGroupList();
        var having = groupedMember.getGroupRestriction();
        groupedCriteria.select(groupedOwner.get(PluralCriteriaOwner_.id))
                .orderBy(cb.asc(groupedOwner.get(PluralCriteriaOwner_.id)))
                .where(groupedOwner.get(PluralCriteriaOwner_.elements).contains(groupedMember));
        TypedQuery<Integer> groupedPresent = handler.createQuery(groupedCriteria);
        groupedCriteria.where(groupedOwner.get(PluralCriteriaOwner_.elements).notContains(groupedMember));
        TypedQuery<Integer> groupedAbsent = handler.createQuery(groupedCriteria);
        for (TypedQuery<Integer> query : List.of(groupedPresent, groupedAbsent)) {
            assertEquals(Set.of(firstId, rowCount), query.getParameters());
            assertSame(firstId, query.getParameter("groupedFirstId", Integer.class));
            assertSame(rowCount, query.getParameter("groupedRowCount", Long.class));
        }
        for (long count : new long[] {1, 2, 1}) {
            for (Integer binding : Arrays.asList(1, 2, 9, null)) {
                boolean hasResult = count == 1 && binding != null && binding <= 2;
                assertEquals(hasResult ? List.of(binding) : List.of(),
                        groupedPresent.setParameter(firstId, binding).setParameter(rowCount, count).getResultList());
                assertEquals(!hasResult ? List.of(3, 4) : binding == 1 ? List.of(2, 3, 4) : List.of(1, 3, 4),
                        groupedAbsent.setParameter(firstId, binding).setParameter(rowCount, count).getResultList());
            }
        }
        assertSame(groupedLookup, groupedMember.getSelection());
        assertEquals(grouping, groupedMember.getGroupList());
        assertSame(having, groupedMember.getGroupRestriction());
    }

    private void assertEntitySubquerySelectionAndParameterMutation(EntityHandler handler, CriteriaBuilder cb) {
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<PluralCriteriaOwner> owner = cq.from(PluralCriteriaOwner.class);
        Subquery<PluralCriteriaElement> member = cq.subquery(PluralCriteriaElement.class);
        Root<PluralCriteriaElement> first = member.from(PluralCriteriaElement.class);
        Root<PluralCriteriaElement> second = member.from(PluralCriteriaElement.class);
        ParameterExpression<Integer> firstId = cb.parameter(Integer.class, "originalLookupFirstId");
        member.select(first).where(cb.equal(first.get(PluralCriteriaElement_.firstId), firstId),
                cb.equal(first.get(PluralCriteriaElement_.secondId), 2),
                cb.equal(second.get(PluralCriteriaElement_.firstId), 2), cb.equal(second.get(PluralCriteriaElement_.secondId), 1));
        var positive = owner.get(PluralCriteriaOwner_.elements).contains(member);
        var negative = owner.get(PluralCriteriaOwner_.elements).notContains(member);
        // Membership is already constructed: the first executable must read this current selection and restriction.
        member.select(second).where(cb.equal(first.get(PluralCriteriaElement_.firstId), firstId),
                cb.equal(first.get(PluralCriteriaElement_.secondId), 1),
                cb.equal(second.get(PluralCriteriaElement_.firstId), 2), cb.equal(second.get(PluralCriteriaElement_.secondId), 1));
        cq.select(owner.get(PluralCriteriaOwner_.id)).orderBy(cb.asc(owner.get(PluralCriteriaOwner_.id))).where(positive);
        TypedQuery<Integer> original = handler.createQuery(cq);
        cq.where(negative);
        TypedQuery<Integer> originalNegative = handler.createQuery(cq);
        assertEquals(List.of(2), original.setParameter(firstId, 1).getResultList());
        assertEquals(List.of(1, 3, 4), originalNegative.setParameter(firstId, 1).getResultList());

        ParameterExpression<Integer> replacementId = cb.parameter(Integer.class, "replacementLookupFirstId");
        member.select(first).where(cb.equal(first.get(PluralCriteriaElement_.firstId), replacementId),
                cb.equal(first.get(PluralCriteriaElement_.secondId), 1),
                cb.equal(second.get(PluralCriteriaElement_.firstId), 2), cb.equal(second.get(PluralCriteriaElement_.secondId), 1));
        cq.where(positive);
        TypedQuery<Integer> changed = handler.createQuery(cq);
        assertEquals(Set.of(replacementId), changed.getParameters());
        assertSame(replacementId, changed.getParameter("replacementLookupFirstId", Integer.class));
        for (TypedQuery<Integer> query : List.of(original, originalNegative)) {
            assertEquals(Set.of(firstId), query.getParameters());
            assertSame(firstId, query.getParameter("originalLookupFirstId", Integer.class));
        }
        assertEquals(List.of(1), changed.setParameter(replacementId, 1).getResultList());
        assertEquals(List.of(2), original.setParameter(firstId, 2).getResultList());
        assertEquals(List.of(1, 3, 4), originalNegative.setParameter(firstId, 2).getResultList());
        assertEquals(List.of(), changed.setParameter(replacementId, 9).getResultList());
        assertEquals(List.of(), original.setParameter(firstId, null).getResultList());
        assertEquals(List.of(3, 4), originalNegative.setParameter(firstId, null).getResultList());
        assertEquals(List.of(1), changed.setParameter(replacementId, 1).getResultList());
        assertSame(first, member.getSelection());
        assertEquals(Set.of(first, second), member.getRoots());
        assertEquals(Set.of(owner), cq.getRoots());
    }

    private void assertEntitySubqueryRejectsMultipleIdentities(EntityHandler handler, CriteriaBuilder cb) {
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<PluralCriteriaOwner> owner = cq.from(PluralCriteriaOwner.class);
        Subquery<PluralCriteriaElement> member = cq.subquery(PluralCriteriaElement.class);
        Root<PluralCriteriaElement> lookup = member.from(PluralCriteriaElement.class);
        member.select(lookup).where(cb.equal(lookup.get(PluralCriteriaElement_.firstId), 1));
        cq.select(owner.get(PluralCriteriaOwner_.id)).where(cb.equal(owner.get(PluralCriteriaOwner_.id), 1),
                owner.get(PluralCriteriaOwner_.elements).contains(member));
        RuntimeException failure = assertThrows(RuntimeException.class, () -> handler.createQuery(cq).getResultList());
        // The two rows share FIRST_ID but have different SECOND_ID values. Keep
        // the database's scalar-cardinality rejection rather than ANY semantics.
        Throwable cause = failure;
        while (cause != null && !(cause instanceof SQLException)) {
            cause = cause.getCause();
        }
        assertTrue("Expected a native scalar-subquery failure: " + failure, cause instanceof SQLException);
        SQLException sql = (SQLException) cause;
        assertTrue("Expected scalar cardinality, not another SQL failure: " + sql,
                "21000".equals(sql.getSQLState()) || Set.of(90053, 1427, 512, 1242, -811).contains(sql.getErrorCode()));
        assertSame(lookup, member.getSelection());
        assertEquals(Set.of(lookup), member.getRoots());
    }

    private MembershipQueries assertEntitySubqueryMembership(EntityHandler handler, CriteriaBuilder cb,
            CriteriaQuery<Tuple> cq, Root<PluralCriteriaOwner> owner, Subquery<PluralCriteriaElement> member,
            ParameterExpression<Integer> parameter) {
        var elements = owner.get(PluralCriteriaOwner_.elements);
        var positive = elements.contains(member);
        var negative = elements.notContains(member);
        positive.alias("present");
        negative.alias("absent");
        cq.multiselect(owner.get(PluralCriteriaOwner_.id), positive, negative).orderBy(cb.asc(owner.get(PluralCriteriaOwner_.id)));
        TypedQuery<Tuple> projection = handler.createQuery(cq);
        cq.where(positive);
        TypedQuery<Tuple> positiveQuery = handler.createQuery(cq);
        cq.where(negative);
        TypedQuery<Tuple> negativeQuery = handler.createQuery(cq);
        for (TypedQuery<Tuple> query : List.of(projection, positiveQuery, negativeQuery)) {
            assertSame(parameter, query.getParameter(parameter.getName(), Integer.class));
            assertEquals(Set.of(parameter), query.getParameters());
        }
        for (Integer binding : Arrays.asList(null, 1, 2, 9, 3, null, 1)) {
            List<Tuple> rows = projection.setParameter(parameter, binding).getResultList();
            assertEquals(4, rows.size());
            for (int index = 0; index < rows.size(); index++) {
                int id = index + 1;
                Boolean present;
                if (id > 2) {
                    present = false;
                } else if (binding == null || binding > 2) {
                    present = null;
                } else {
                    present = id == binding;
                }
                Boolean absent = present == null ? null : !present;
                assertEquals(Arrays.asList(id, present, absent), Arrays.asList(rows.get(index).toArray()));
                assertEquals(present, rows.get(index).get("present", Boolean.class));
                assertEquals(absent, rows.get(index).get("absent", Boolean.class));
            }
            assertEquals(binding != null && binding <= 2 ? List.of(binding) : List.of(),
                    positiveQuery.setParameter(parameter, binding).getResultList().stream().map(row -> row.get(0, Integer.class)).toList());
            List<Integer> absentIds = binding == null || binding > 2 ? List.of(3, 4)
                    : binding == 1 ? List.of(2, 3, 4) : List.of(1, 3, 4);
            assertEquals(absentIds, negativeQuery.setParameter(parameter, binding).getResultList().stream()
                    .map(row -> row.get(0, Integer.class)).toList());
        }
        assertEquals(Set.of(owner), cq.getRoots());
        assertEquals(Set.of(parameter), cq.getParameters());
        return new MembershipQueries(positiveQuery, negativeQuery);
    }

    private record MembershipQueries(TypedQuery<Tuple> positive, TypedQuery<Tuple> negative) {
    }

    @Test
    public void testOuterPathComposedAndSubqueryOperands() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> pair = cb.createTupleQuery();
            Root<PluralCriteriaOwner> first = pair.from(PluralCriteriaOwner.class);
            Root<PluralCriteriaOwner> second = pair.from(PluralCriteriaOwner.class);
            pair.multiselect(first.get(PluralCriteriaOwner_.tags).contains(second.get(PluralCriteriaOwner_.nullableText)),
                    second.get(PluralCriteriaOwner_.tags).contains(first.get(PluralCriteriaOwner_.nullableText)));
            pair.where(cb.equal(first.get(PluralCriteriaOwner_.id), 1), cb.equal(second.get(PluralCriteriaOwner_.id), 2));
            Tuple paired = em.createQuery(pair).getSingleResult();
            assertNull(paired.get(0));
            assertEquals(false, paired.get(1));
            assertEquals(Set.of(first, second), pair.getRoots());

            CriteriaQuery<Integer> composed = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> owner = composed.from(PluralCriteriaOwner.class);
            ParameterExpression<String> prefix = cb.parameter(String.class, "prefix");
            ParameterExpression<String> suffix = cb.parameter(String.class);
            Expression<String> operand = cb.concat(prefix, suffix);
            operand.alias("composedMember");
            composed.select(owner.get(PluralCriteriaOwner_.id)).where(owner.get(PluralCriteriaOwner_.tags).contains(operand))
                    .orderBy(cb.asc(owner.get(PluralCriteriaOwner_.id)));
            assertEquals(Set.of(prefix, suffix), composed.getParameters());
            TypedQuery<Integer> composedQuery = em.createQuery(composed);
            assertSame(prefix, composedQuery.getParameter("prefix", String.class));
            assertTrue(composedQuery.getParameters().contains(suffix));
            assertEquals(List.of(1), composedQuery.setParameter(prefix, "r").setParameter(suffix, "ed").getResultList());
            assertEquals(List.of(1, 2), composedQuery.setParameter(prefix, "bl").setParameter(suffix, "ue").getResultList());
            assertEquals(List.of(), composedQuery.setParameter(prefix, "miss").setParameter(suffix, "ing").getResultList());
            assertEquals("composedMember", operand.getAlias());

            CriteriaQuery<Integer> subqueryCriteria = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> subqueryOwner = subqueryCriteria.from(PluralCriteriaOwner.class);
            Subquery<String> member = subqueryCriteria.subquery(String.class);
            Root<PluralCriteriaOwner> lookup = member.from(PluralCriteriaOwner.class);
            ParameterExpression<Integer> lookupId = cb.parameter(Integer.class, "lookupId");
            member.select(lookup.get(PluralCriteriaOwner_.nullableText)).where(cb.equal(lookup.get(PluralCriteriaOwner_.id), lookupId));
            subqueryCriteria.select(subqueryOwner.get(PluralCriteriaOwner_.id))
                    .where(subqueryOwner.get(PluralCriteriaOwner_.tags).contains(member))
                    .orderBy(cb.asc(subqueryOwner.get(PluralCriteriaOwner_.id)));
            TypedQuery<Integer> memberQuery = em.createQuery(subqueryCriteria);
            assertSame(lookupId, memberQuery.getParameter("lookupId", Integer.class));
            assertEquals(List.of(1), memberQuery.setParameter(lookupId, 1).getResultList());
            assertEquals(List.of(), memberQuery.setParameter(lookupId, 2).getResultList());
            assertEquals(Set.of(subqueryOwner), subqueryCriteria.getRoots());

            CriteriaQuery<Integer> correlated = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> correlatedOwner = correlated.from(PluralCriteriaOwner.class);
            Subquery<Integer> selection = correlated.subquery(Integer.class);
            Root<PluralCriteriaOwner> correlatedRoot = selection.correlate(correlatedOwner);
            selection.select(correlatedRoot.get(PluralCriteriaOwner_.id))
                    .where(correlatedRoot.get(PluralCriteriaOwner_.tags).contains(prefix));
            correlated.select(correlatedOwner.get(PluralCriteriaOwner_.id)).where(cb.exists(selection))
                    .orderBy(cb.asc(correlatedOwner.get(PluralCriteriaOwner_.id)));
            TypedQuery<Integer> correlatedQuery = em.createQuery(correlated);
            assertSame(prefix, correlatedQuery.getParameter("prefix", String.class));
            assertEquals(List.of(1), correlatedQuery.setParameter(prefix, "red").getResultList());
            assertEquals(List.of(), correlatedQuery.setParameter(prefix, null).getResultList());
            Subquery<Integer> sizes = correlated.subquery(Integer.class);
            Root<PluralCriteriaOwner> sizeOwner = sizes.correlate(correlatedOwner);
            sizes.select(sizeOwner.get(PluralCriteriaOwner_.tags).size());
            correlated.where(cb.ge(sizes, 2));
            TypedQuery<Integer> correlatedSize = em.createQuery(correlated);
            assertTrue(correlatedSize.getParameters().isEmpty());
            assertEquals(List.of(1), correlatedSize.getResultList());
            assertEquals(List.of(1, 2), correlatedQuery.setParameter(prefix, "blue").getResultList());
            assertEquals(Set.of(correlatedOwner), correlated.getRoots());
        }
    }

    @Test
    public void testEntityPathOperandUsesCompositeIdentity() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> owner = cq.from(PluralCriteriaOwner.class);
            Root<PluralCriteriaElement> member = cq.from(PluralCriteriaElement.class);
            ParameterExpression<Integer> firstId = cb.parameter(Integer.class, "firstId");
            cq.select(owner.get(PluralCriteriaOwner_.id)).where(owner.get(PluralCriteriaOwner_.elements).contains(member),
                    cb.equal(member.get(PluralCriteriaElement_.firstId), firstId),
                    cb.equal(member.get(PluralCriteriaElement_.secondId), 1));
            TypedQuery<Integer> query = em.createQuery(cq);
            assertEquals(List.of(1), query.setParameter(firstId, 1).getResultList());
            assertEquals(List.of(2), query.setParameter(firstId, 2).getResultList());
            assertEquals(Set.of(owner, member), cq.getRoots());
        }
    }

    @Test
    public void testCorrelatedEntitySubqueryRetainsBulkUpdateAndDeleteTargets() {
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<PluralCriteriaOwner> legacy = cb.createCriteriaUpdate(PluralCriteriaOwner.class);
            Root<PluralCriteriaOwner> legacyTarget = legacy.from(PluralCriteriaOwner.class);
            ParameterExpression<Integer> legacySecondId = cb.parameter(Integer.class, "legacyBulkSecondId");
            Subquery<PluralCriteriaElement> legacyMember = legacy.subquery(PluralCriteriaElement.class);
            Root<PluralCriteriaOwner> legacyCorrelated = legacyMember.correlate(legacyTarget);
            Root<PluralCriteriaElement> legacyLookup = legacyMember.from(PluralCriteriaElement.class);
            legacyMember.select(legacyLookup).where(
                    cb.equal(legacyLookup.get(PluralCriteriaElement_.firstId), legacyCorrelated.get(PluralCriteriaOwner_.id)),
                    cb.equal(legacyLookup.get(PluralCriteriaElement_.secondId), legacySecondId));
            legacy.set(legacyTarget.get(PluralCriteriaOwner_.nullableText), "legacy-bulk")
                    .where(cb.exists(legacyMember));
            var legacyQuery = em.createQuery(legacy);
            assertSame(legacySecondId, legacyQuery.getParameter("legacyBulkSecondId", Integer.class));
            em.getTransaction().begin();
            try {
                assertEquals(0, legacyQuery.setParameter(legacySecondId, null).executeUpdate());
                assertEquals(1, legacyQuery.setParameter(legacySecondId, 2).executeUpdate());
                em.clear();
                assertEquals(List.of(1), em.createQuery(
                        "select o.id from PluralCriteriaOwner o where o.nullableText = 'legacy-bulk' order by o.id", Integer.class)
                        .getResultList());
                assertEquals(2, legacyQuery.setParameter(legacySecondId, 1).executeUpdate());
                assertEquals(0, legacyQuery.setParameter(legacySecondId, null).executeUpdate());
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.clear();
            }
            CriteriaUpdate<PluralCriteriaOwner> update = cb.createCriteriaUpdate(PluralCriteriaOwner.class);
            Root<PluralCriteriaOwner> updateTarget = update.from(PluralCriteriaOwner.class);
            ParameterExpression<Integer> updateSecondId = cb.parameter(Integer.class, "bulkUpdateSecondId");
            Subquery<PluralCriteriaElement> updateMember = update.subquery(PluralCriteriaElement.class);
            Root<PluralCriteriaOwner> updateCorrelated = updateMember.correlate(updateTarget);
            Root<PluralCriteriaElement> updateLookup = updateMember.from(PluralCriteriaElement.class);
            updateMember.select(updateLookup).where(
                    cb.equal(updateLookup.get(PluralCriteriaElement_.firstId), updateCorrelated.get(PluralCriteriaOwner_.id)),
                    cb.equal(updateLookup.get(PluralCriteriaElement_.secondId), updateSecondId));
            update.set(updateTarget.get(PluralCriteriaOwner_.nullableText), "bulk-membership")
                    .where(updateTarget.get(PluralCriteriaOwner_.elements).contains(updateMember));
            var updateQuery = em.createQuery(update);
            assertSame(updateTarget, update.getRoot());
            assertSame(updateSecondId, updateQuery.getParameter("bulkUpdateSecondId", Integer.class));
            assertEquals(Set.of(updateSecondId), updateQuery.getParameters());
            em.getTransaction().begin();
            try {
                assertEquals(0, updateQuery.setParameter(updateSecondId, null).executeUpdate());
                assertEquals(1, updateQuery.setParameter(updateSecondId, 2).executeUpdate());
                em.clear();
                assertEquals(List.of(1), em.createQuery(
                        "select o.id from PluralCriteriaOwner o where o.nullableText = 'bulk-membership' order by o.id", Integer.class)
                        .getResultList());
                assertEquals(2, updateQuery.setParameter(updateSecondId, 1).executeUpdate());
                assertEquals(0, updateQuery.setParameter(updateSecondId, null).executeUpdate());
                em.clear();
                assertEquals(List.of(1, 2), em.createQuery(
                        "select o.id from PluralCriteriaOwner o where o.nullableText = 'bulk-membership' order by o.id", Integer.class)
                        .getResultList());
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.clear();
            }
            assertEquals("red", em.createQuery("select o.nullableText from PluralCriteriaOwner o where o.id = 1", String.class)
                    .getSingleResult());
            assertNull(em.createQuery("select o.nullableText from PluralCriteriaOwner o where o.id = 2", String.class).getSingleResult());

            CriteriaDelete<PluralCriteriaOwner> delete = cb.createCriteriaDelete(PluralCriteriaOwner.class);
            Root<PluralCriteriaOwner> deleteTarget = delete.from(PluralCriteriaOwner.class);
            ParameterExpression<Integer> deleteSecondId = cb.parameter(Integer.class, "bulkDeleteSecondId");
            Subquery<PluralCriteriaElement> deleteMember = delete.subquery(PluralCriteriaElement.class);
            Root<PluralCriteriaOwner> deleteCorrelated = deleteMember.correlate(deleteTarget);
            Root<PluralCriteriaElement> deleteLookup = deleteMember.from(PluralCriteriaElement.class);
            deleteMember.select(deleteLookup).where(
                    cb.equal(deleteLookup.get(PluralCriteriaElement_.firstId), cb.sum(deleteCorrelated.get(PluralCriteriaOwner_.id), 1)),
                    cb.equal(deleteLookup.get(PluralCriteriaElement_.secondId), deleteSecondId));
            // The parent owns the tested collection, so deleting the target's
            // collection rows cannot invalidate membership before its row is deleted.
            delete.where(deleteTarget.get(PluralCriteriaOwner_.parent).get(PluralCriteriaOwner_.elements).contains(deleteMember));
            var deleteQuery = em.createQuery(delete);
            assertSame(deleteTarget, delete.getRoot());
            assertSame(deleteSecondId, deleteQuery.getParameter("bulkDeleteSecondId", Integer.class));
            assertEquals(Set.of(deleteSecondId), deleteQuery.getParameters());
            em.getTransaction().begin();
            try {
                // Existing bulk cleanup does not remove embedded collection rows.
                // Isolate target correlation while preserving the populated parent collection.
                assertEquals(2, em.createNativeQuery("DELETE FROM JPA_PC_NOTE WHERE OWNER_ID = 1").executeUpdate());
                em.clear();
                assertEquals(0, deleteQuery.setParameter(deleteSecondId, null).executeUpdate());
                assertEquals(1, deleteQuery.setParameter(deleteSecondId, 1).executeUpdate());
                em.clear();
                assertEquals(List.of(2, 3, 4), em.createQuery("select o.id from PluralCriteriaOwner o order by o.id", Integer.class)
                        .getResultList());
                assertEquals(Long.valueOf(1), em.createQuery(
                        "select count(e) from PluralCriteriaOwner o join o.elements e where o.id = 2", Long.class).getSingleResult());
                assertEquals(0, deleteQuery.setParameter(deleteSecondId, null).executeUpdate());
                assertEquals(0, deleteQuery.setParameter(deleteSecondId, 1).executeUpdate());
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                em.clear();
            }
            assertEquals(List.of(1, 2, 3, 4), em.createQuery("select o.id from PluralCriteriaOwner o order by o.id", Integer.class)
                    .getResultList());
            assertEquals(Long.valueOf(2), em.createQuery(
                    "select count(e) from PluralCriteriaOwner o join o.elements e where o.id = 1", Long.class).getSingleResult());
            assertEquals(List.of("inner", "other"), em.find(PluralCriteriaOwner.class, 1).details.notes);
            assertSame(updateTarget, updateCorrelated.getCorrelationParent());
            assertSame(deleteTarget, deleteCorrelated.getCorrelationParent());
        }
    }

    @Test
    public void testExecutableSnapshotsRetainMembershipAndSize() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            ParameterExpression<String> member = cb.parameter(String.class, "snapshotMember");
            var tags = root.get(PluralCriteriaOwner_.tags);
            tags.alias("snapshotTags");
            var original = tags.contains(member);
            cq.select(root.get(PluralCriteriaOwner_.id)).where(original).orderBy(cb.asc(root.get(PluralCriteriaOwner_.id)));
            TypedQuery<Integer> before = handler.createQuery(cq);
            cq.where(tags.notContains(member));
            TypedQuery<Integer> after = handler.createQuery(cq);
            assertSame(member, before.getParameter("snapshotMember", String.class));
            assertSame(member, after.getParameter("snapshotMember", String.class));
            assertEquals(List.of(1), before.setParameter(member, "red").getResultList());
            assertEquals(List.of(3, 4), after.setParameter(member, "blue").getResultList());
            assertEquals(List.of(1), before.getResultList());
            assertEquals(List.of(1, 2), before.setParameter(member, "blue").getResultList());
            assertEquals(List.of(2, 3, 4), after.setParameter(member, "red").getResultList());
            cq.where(original);
            assertEquals(List.of(1), handler.createQuery(cq).setParameter(member, "red").getResultList());
            assertEquals("snapshotTags", tags.getAlias());
            assertEquals(Set.of(root), cq.getRoots());

            CriteriaQuery<Integer> size = cb.createQuery(Integer.class);
            Root<PluralCriteriaOwner> sizeRoot = size.from(PluralCriteriaOwner.class);
            var originalSize = sizeRoot.get(PluralCriteriaOwner_.labels).size();
            originalSize.alias("originalSize");
            size.select(originalSize).where(cb.equal(sizeRoot.get(PluralCriteriaOwner_.id), 1));
            TypedQuery<Integer> beforeSize = handler.createQuery(size);
            size.select(sizeRoot.get(PluralCriteriaOwner_.tags).size());
            TypedQuery<Integer> afterSize = handler.createQuery(size);
            assertEquals(Integer.valueOf(3), beforeSize.getSingleResult());
            assertEquals(Integer.valueOf(2), afterSize.getSingleResult());
            assertEquals(Integer.valueOf(3), beforeSize.getSingleResult());
            assertEquals("originalSize", originalSize.getAlias());
        });
    }

    @Test
    public void testNullLiteralAndRepresentedNullableMemberCompatibility() {
        try (EntityManager em = emf.createEntityManager()) {
            Object[] stored = (Object[]) em.createNativeQuery("SELECT COUNT(*), COUNT(MAP_VALUE) FROM JPA_PC_MAP WHERE OWNER_ID = 4")
                    .getSingleResult();
            assertEquals(1, ((Number) stored[0]).intValue());
            assertEquals(0, ((Number) stored[1]).intValue());
            Object[] labels = (Object[]) em.createNativeQuery("SELECT COUNT(*), COUNT(LABEL_VALUE) FROM JPA_PC_LABEL WHERE OWNER_ID = 4")
                    .getSingleResult();
            assertEquals(1, ((Number) labels[0]).intValue());
            assertEquals(0, ((Number) labels[1]).intValue());
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            var values = root.get(PluralCriteriaOwner_.codes);
            var positive = values.contains(cb.nullLiteral(String.class));
            var negative = values.notContains(cb.nullLiteral(String.class));
            cq.multiselect(root.get(PluralCriteriaOwner_.id), positive, negative, cb.not(positive),
                    cb.selectCase(Integer.class).when(positive, 1).when(negative, 2).otherwise(3));
            cq.orderBy(cb.asc(root.get(PluralCriteriaOwner_.id)));
            List<Tuple> rows = em.createQuery(cq).getResultList();
            assertEquals(4, rows.size());
            for (int i = 0; i < rows.size(); i++) {
                assertEquals(i == 2 ? Boolean.FALSE : null, rows.get(i).get(1));
                assertEquals(i == 2 ? Boolean.TRUE : null, rows.get(i).get(2));
                assertEquals(i == 2 ? Boolean.TRUE : null, rows.get(i).get(3));
                assertEquals(i == 2 ? 2 : 3, rows.get(i).get(4));
            }
        }
        // Provider compatibility interpretation: null members do not contaminate an equality match.
        emf.runInTransaction(em -> em.createNativeQuery(
                "INSERT INTO JPA_PC_MAP (OWNER_ID, MAP_KEY, MAP_VALUE) VALUES (1, 'mixed-null-key', NULL)").executeUpdate());
        try (EntityManager em = emf.createEntityManager()) {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            Root<PluralCriteriaOwner> root = cq.from(PluralCriteriaOwner.class);
            var values = root.get(PluralCriteriaOwner_.codes);
            cq.multiselect(values.size(), values.contains("red"), values.notContains("red"),
                    values.contains("missing"), values.notContains("missing"));
            cq.where(cb.equal(root.get(PluralCriteriaOwner_.id), 1));
            assertEquals(List.of(4, true, false, false, true), Arrays.asList(em.createQuery(cq).getSingleResult().toArray()));
        } finally {
            emf.runInTransaction(em -> em.createNativeQuery(
                    "DELETE FROM JPA_PC_MAP WHERE OWNER_ID = 1 AND MAP_KEY = 'mixed-null-key'").executeUpdate());
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testDetachedSubtreeSerializationAndFurtherComposition() throws Exception {
        CriteriaBuilder detachedBuilder = new CriteriaBuilderImpl(null);
        ExpressionBuilder node = new ExpressionBuilder(PluralCriteriaOwner.class);
        RootImpl<PluralCriteriaOwner> owner = new RootImpl<>(null, null, PluralCriteriaOwner.class, node, null);
        PluralExpression<Set<String>, String> tags = new PluralExpressionImpl<>(null, (Class) Set.class,
                node.anyOf("tags"), owner, "tags", String.class);
        tags.alias("detachedTags");
        ParameterExpression<String> member = detachedBuilder.parameter(String.class, "detachedMember");
        var positive = tags.contains(member);
        positive.alias("detachedMembership");
        var size = tags.size();
        size.alias("detachedSize");
        List<Expression<?>> originals = List.of(tags, member, positive, size);
        List<org.eclipse.persistence.expressions.Expression> originalNodes = originals.stream()
                .map(expression -> ((InternalSelection) expression).getCurrentNode()).toList();
        Object[] restored = roundTrip(new Object[]{tags, member, positive, size});
        var restoredTags = (PluralExpression<Set<String>, String>) restored[0];
        var restoredMember = (ParameterExpression<String>) restored[1];
        var restoredPositive = (Expression<Boolean>) restored[2];
        var restoredSize = (Expression<Integer>) restored[3];
        assertNotSame(tags, restoredTags);
        assertNotSame(member, restoredMember);
        assertEquals("detachedTags", restoredTags.getAlias());
        assertEquals("detachedMembership", restoredPositive.getAlias());
        assertEquals("detachedSize", restoredSize.getAlias());
        assertEquals(String.class, restoredMember.getParameterType());
        assertEquals("detachedMember", restoredMember.getName());
        assertEquals(Integer.class, restoredSize.getJavaType());
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
            cq.multiselect(restoredSize, restoredPositive).orderBy(cb.asc(restoredSize));
            assertEquals(Set.of(restoredMember), cq.getParameters());
            TypedQuery<Tuple> query = handler.createQuery(cq);
            assertSame(restoredMember, query.getParameter("detachedMember", String.class));
            for (String binding : Arrays.asList(null, "red", "missing", null)) {
                List<Tuple> rows = query.setParameter(restoredMember, binding).getResultList();
                assertEquals(4, rows.size());
                assertEquals(List.of(0, 0, 1, 2), rows.stream().map(row -> row.get("detachedSize", Integer.class)).toList());
                for (int i = 0; i < rows.size(); i++) {
                    Boolean expected = Boolean.FALSE;
                    if (i >= 2 && binding == null) {
                        expected = null;
                    } else if (i == 3 && "red".equals(binding)) {
                        expected = Boolean.TRUE;
                    }
                    assertEquals(expected, rows.get(i).get("detachedMembership"));
                }
            }
            CriteriaQuery<Integer> composed = cb.createQuery(Integer.class);
            composed.select(restoredSize).where(restoredTags.notContains(restoredMember)).orderBy(cb.asc(restoredSize));
            assertEquals(List.of(0, 0, 1), handler.createQuery(composed).setParameter(restoredMember, "red").getResultList());
            assertEquals(List.of(0, 0), handler.createQuery(composed).setParameter(restoredMember, null).getResultList());
        });
        assertEquals("detachedTags", tags.getAlias());
        assertEquals("detachedMembership", positive.getAlias());
        assertEquals("detachedSize", size.getAlias());
        assertEquals("detachedMember", member.getName());
        for (int i = 0; i < originals.size(); i++) {
            assertSame(originalNodes.get(i), ((InternalSelection) originals.get(i)).getCurrentNode());
        }
    }

    private static <T> T roundTrip(T value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            @SuppressWarnings("unchecked")
            T restored = (T) input.readObject();
            return restored;
        }
    }

    public static class CountProjection {
        public final Integer count;
        public CountProjection(Integer count) {
            this.count = count;
        }
    }

    private void withHandlers(Consumer<EntityHandler> action) {
        try (EntityManager em = emf.createEntityManager()) {
            action.accept(em);
        }
        try (var agent = emf.createEntityAgent()) {
            action.accept(agent);
        }
    }
}
