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

import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.query.model.MapJoinAliasEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestMapJoinAliasFunctions {

    private static final String FROM = " FROM MapJoinAliasEntity p JOIN p.environment e";
    private static final String WHERE = "SELECT p" + FROM + " WHERE KEY(e) = 'osName' AND ";
    private static final String NAMED_QUERY = "MapJoinAliasEntity.byOperatingSystem";

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {MapJoinAliasEntity.class, MapJoinAliasEntity.LongToStringConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;

    @Before
    public void seed() {
        cleanup();
        emf.runInTransaction(em -> {
            em.persist(new MapJoinAliasEntity(1, "Unix", "Emacs"));
            em.persist(new MapJoinAliasEntity(2, "Windows", "Unix"));
            em.persist(new MapJoinAliasEntity(3, "UNIX", "Vim"));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> em.createQuery("SELECT p FROM MapJoinAliasEntity p", MapJoinAliasEntity.class)
                .getResultList().forEach(em::remove));
        emf.getCache().evictAll();
    }

    @Test
    public void entityManagerOriginalControlsRemainValid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertOriginalControls(em);
        }
    }

    @Test
    public void entityAgentOriginalControlsRemainValid() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertOriginalControls(agent);
        }
    }

    @Test
    public void entityManagerLowerUsesMapValueAndKey() {
        try (EntityManager em = emf.createEntityManager()) {
            assertLower(em);
        }
    }

    @Test
    public void entityAgentLowerUsesMapValueAndKey() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertLower(agent);
        }
    }

    @Test
    public void entityManagerOriginalNullIfQueryIsAccepted() {
        try (EntityManager em = emf.createEntityManager()) {
            assertOriginalNullIf(em);
        }
    }

    @Test
    public void entityAgentOriginalNullIfQueryIsAccepted() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertOriginalNullIf(agent);
        }
    }

    @Test
    public void entityManagerNullIfFirstArgumentReturnsNullOrValue() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNullIfFirstArgument(em);
        }
    }

    @Test
    public void entityAgentNullIfFirstArgumentReturnsNullOrValue() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNullIfFirstArgument(agent);
        }
    }

    @Test
    public void entityManagerNullIfSecondArgumentReturnsNullOrValue() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNullIfSecondArgument(em);
        }
    }

    @Test
    public void entityAgentNullIfSecondArgumentReturnsNullOrValue() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNullIfSecondArgument(agent);
        }
    }

    @Test
    public void entityManagerTypedParametersUseMapValues() {
        try (EntityManager em = emf.createEntityManager()) {
            assertParameters(em);
        }
    }

    @Test
    public void entityAgentTypedParametersUseMapValues() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertParameters(agent);
        }
    }

    @Test
    public void entityManagerAliasParametersDistinguishScalarAndCollectionValues() {
        try (EntityManager em = emf.createEntityManager()) {
            assertScalarAndCollectionParameters(em);
        }
    }

    @Test
    public void entityAgentAliasParametersDistinguishScalarAndCollectionValues() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertScalarAndCollectionParameters(agent);
        }
    }

    @Test
    public void entityManagerConvertedAliasesRetainDomainTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            assertConvertedAliasTypes(em);
        }
    }

    @Test
    public void entityAgentConvertedAliasesRetainDomainTypes() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertConvertedAliasTypes(agent);
        }
    }

    @Test
    public void entityManagerMixedConvertersRetainKeyAndValueTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMixedConverterTypes(em);
        }
    }

    @Test
    public void entityAgentMixedConvertersRetainKeyAndValueTypes() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMixedConverterTypes(agent);
        }
    }

    @Test
    public void entityManagerNamedQueryUsesMapValues() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNamedQuery(em);
        }
    }

    @Test
    public void entityAgentNamedQueryUsesMapValues() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNamedQuery(agent);
        }
    }

    @Test
    public void entityManagerRelatedScalarFunctionsUseMapValues() {
        try (EntityManager em = emf.createEntityManager()) {
            assertRelatedScalarFunctions(em);
        }
    }

    @Test
    public void entityAgentRelatedScalarFunctionsUseMapValues() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertRelatedScalarFunctions(agent);
        }
    }

    @Test
    public void entityManagerConditionalResultsRetainMapValueType() {
        try (EntityManager em = emf.createEntityManager()) {
            assertConditionalMapValues(em);
        }
    }

    @Test
    public void entityAgentConditionalResultsRetainMapValueType() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertConditionalMapValues(agent);
        }
    }

    @Test
    public void entityManagerConditionalResultsRetainCollectionElementType() {
        try (EntityManager em = emf.createEntityManager()) {
            assertConditionalCollectionValues(em);
        }
    }

    @Test
    public void entityAgentConditionalResultsRetainCollectionElementType() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertConditionalCollectionValues(agent);
        }
    }

    @Test
    public void entityManagerIncompatibleResultConstructorsRemainInvalid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertIncompatibleResultConstructor(em);
        }
    }

    @Test
    public void entityAgentIncompatibleResultConstructorsRemainInvalid() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertIncompatibleResultConstructor(agent);
        }
    }

    @Test
    public void entityManagerUndeclaredAliasesRemainInvalid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertUndeclaredAliases(em);
        }
    }

    @Test
    public void entityAgentUndeclaredAliasesRemainInvalid() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertUndeclaredAliases(agent);
        }
    }

    @Test
    public void entityManagerMalformedArgumentsRemainInvalid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertMalformedArguments(em);
        }
    }

    @Test
    public void entityAgentMalformedArgumentsRemainInvalid() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertMalformedArguments(agent);
        }
    }

    @Test
    public void entityManagerCollectionPathsRemainInvalid() {
        try (EntityManager em = emf.createEntityManager()) {
            assertCollectionPaths(em);
        }
    }

    @Test
    public void entityAgentCollectionPathsRemainInvalid() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertCollectionPaths(agent);
        }
    }

    private void assertOriginalControls(EntityHandler handler) {
        for (String predicate : List.of("e = 'Unix'", "LOWER(VALUE(e)) = 'unix'",
                "NULLIF(VALUE(e), '') = 'Unix'")) {
            assertNotNull(handler.createQuery(WHERE + predicate, MapJoinAliasEntity.class));
        }
        assertEquals(List.of(1L, 3L), owners(handler, "LOWER(VALUE(e)) = 'unix'"));
    }

    private void assertLower(EntityHandler handler) {
        assertEquals(List.of(1L, 3L), owners(handler, "LOWER(e) = 'unix'"));
        assertEquals(owners(handler, "LOWER(VALUE(e)) = 'unix'"), owners(handler, "LOWER(e) = 'unix'"));
        assertValues(handler, "LOWER(e)", "LOWER(VALUE(e))", String.class, List.of("unix", "windows", "unix"));
    }

    private void assertOriginalNullIf(EntityHandler handler) {
        // Preserve the reported query, without assuming portable empty-string result semantics.
        assertNotNull(handler.createQuery(WHERE + "NULLIF(e, '') = 'Unix'", MapJoinAliasEntity.class));
    }

    private void assertNullIfFirstArgument(EntityHandler handler) {
        assertValues(handler, "NULLIF(e, 'Windows')", "NULLIF(VALUE(e), 'Windows')",
                String.class, Arrays.asList("Unix", null, "UNIX"));
        assertValues(handler, "NULLIF(e, 'other')", "NULLIF(VALUE(e), 'other')",
                String.class, List.of("Unix", "Windows", "UNIX"));
        assertEquals(List.of(2L), owners(handler, "NULLIF(e, 'other') = 'Windows'"));
    }

    private void assertNullIfSecondArgument(EntityHandler handler) {
        // A mapped first argument supplies a portable SQL type without literal-parameter inference.
        String from = FROM + " JOIN p.environment c WHERE KEY(e) = 'osName' AND KEY(c) = 'comparison'";
        List<String> expected = Arrays.asList("Windows", null, "Windows");
        assertEquals(expected, handler.createQuery("SELECT NULLIF(VALUE(c), VALUE(e))" + from + " ORDER BY p.id",
                String.class).getResultList());
        assertEquals(expected, handler.createQuery("SELECT NULLIF(VALUE(c), e)" + from + " ORDER BY p.id",
                String.class).getResultList());
        assertEquals(List.of(2L), handler.createQuery("SELECT p.id" + from
                + " AND NULLIF(VALUE(c), e) IS NULL ORDER BY p.id", Long.class).getResultList());
    }

    private void assertParameters(EntityHandler handler) {
        assertEquals(List.of("unix", "unix"), handler.createQuery("SELECT LOWER(e)" + FROM
                        + " WHERE KEY(e) = :key AND LOWER(e) = :os ORDER BY p.id", String.class)
                .setParameter("key", "osName").setParameter("os", "unix").getResultList());
        assertEquals(List.of(2L), handler.createQuery("SELECT p.id" + FROM
                        + " WHERE KEY(e) = :key AND NULLIF(e, :os) IS NULL ORDER BY p.id", Long.class)
                .setParameter("key", "osName").setParameter("os", "Windows").getResultList());
    }

    private void assertNamedQuery(EntityHandler handler) {
        emf.addNamedQuery(NAMED_QUERY, handler.createQuery("SELECT p.id" + FROM
                + " WHERE KEY(e) = 'osName' AND LOWER(e) = :os ORDER BY p.id", Long.class));
        assertEquals(List.of(1L, 3L), handler.createNamedQuery(NAMED_QUERY, Long.class)
                .setParameter("os", "unix").getResultList());
        assertEquals(List.of(2L), handler.createNamedQuery(NAMED_QUERY, Long.class)
                .setParameter("os", "windows").getResultList());
    }

    private void assertScalarAndCollectionParameters(EntityHandler handler) {
        for (String predicate : List.of("SELECT p.id" + FROM + " WHERE KEY(e) = 'osName' AND e",
                "SELECT p.id" + FROM + " WHERE KEY(e) = 'osName' AND VALUE(e)",
                "SELECT p.id FROM MapJoinAliasEntity p JOIN p.operatingSystems s WHERE s")) {
            TypedQuery<Long> scalar = handler.createQuery(predicate + " = :value ORDER BY p.id", Long.class);
            assertEquals(predicate, String.class, scalar.getParameter("value").getParameterType());
            assertThrows(IllegalArgumentException.class, () -> scalar.setParameter("value", List.of("Windows")));
            assertThrows(IllegalArgumentException.class, () -> scalar.setParameter("value", 1));
            assertEquals(predicate, List.of(2L), scalar.setParameter("value", "Windows").getResultList());

            TypedQuery<Long> collection = handler.createQuery(predicate + " IN :values ORDER BY p.id", Long.class);
            assertEquals(predicate, List.of(2L),
                    collection.setParameter("values", List.of("Windows", "missing")).getResultList());
            assertEquals(predicate, List.of(), collection.setParameter("values", List.of("missing")).getResultList());
        }
    }

    private void assertRelatedScalarFunctions(EntityHandler handler) {
        assertValues(handler, "UPPER(e)", "UPPER(VALUE(e))", String.class, List.of("UNIX", "WINDOWS", "UNIX"));
        assertValues(handler, "LENGTH(e)", "LENGTH(VALUE(e))", Integer.class, List.of(4, 7, 4));
        assertValues(handler, "CONCAT(e, '!')", "CONCAT(VALUE(e), '!')",
                String.class, List.of("Unix!", "Windows!", "UNIX!"));
        assertValues(handler, "TRIM(e)", "TRIM(VALUE(e))", String.class, List.of("Unix", "Windows", "UNIX"));
        assertValues(handler, "COALESCE(NULLIF(e, 'Windows'), 'other')",
                "COALESCE(NULLIF(VALUE(e), 'Windows'), 'other')", String.class, List.of("Unix", "other", "UNIX"));
    }

    private void assertConvertedAliasTypes(EntityHandler handler) {
        for (String alias : List.of("a", "VALUE(a)", "n")) {
            String from = " FROM MapJoinAliasEntity p JOIN p." + (alias.equals("n") ? "numbers n" : "amounts a");
            assertConvertedAliasType(handler, alias, from);
        }
    }

    private void assertMixedConverterTypes(EntityHandler handler) {
        String from = " FROM MapJoinAliasEntity p JOIN p.mixedAmounts a";
        TypedQuery<Long> keys = handler.createQuery(
                "SELECT p.id" + from + " WHERE KEY(a) = :key ORDER BY p.id", Long.class);
        assertEquals(String.class, keys.getParameter("key").getParameterType());
        assertEquals(List.of("101", "101", "101"), handler.createQuery(
                "SELECT KEY(a)" + from + " ORDER BY p.id", String.class).getResultList());
        assertConvertedAliasType(handler, "a", from);
        assertConvertedAliasType(handler, "VALUE(a)", from);
    }

    private void assertConvertedAliasType(EntityHandler handler, String alias, String from) {
        assertEquals(alias, List.of(7L, 8L, 9L), handler.createQuery(
                "SELECT " + alias + from + " ORDER BY p.id", Long.class).getResultList());

        TypedQuery<Long> query = handler.createQuery(
                "SELECT p.id" + from + " WHERE " + alias + " = :value ORDER BY p.id", Long.class);
        assertEquals(alias, Long.class, query.getParameter("value").getParameterType());
        assertThrows(IllegalArgumentException.class, () -> query.setParameter("value", "7"));
        assertEquals(alias, List.of(1L), query.setParameter("value", 7L).getResultList());
        assertEquals(alias, List.of(3L), query.setParameter("value", 9L).getResultList());
    }

    private void assertConditionalMapValues(EntityHandler handler) {
        assertValues(handler, "e", "VALUE(e)", String.class, List.of("Unix", "Windows", "UNIX"));
        assertValues(handler, "COALESCE(e, 'other')", "COALESCE(VALUE(e), 'other')",
                String.class, List.of("Unix", "Windows", "UNIX"));
        assertValues(handler, "CASE WHEN p.id = 2 THEN 'other' ELSE e END",
                "CASE WHEN p.id = 2 THEN 'other' ELSE VALUE(e) END", String.class, List.of("Unix", "other", "UNIX"));
        assertValues(handler, "COALESCE(NULLIF(e, 'Windows'), 'other')",
                "COALESCE(NULLIF(VALUE(e), 'Windows'), 'other')", TextResult.class,
                List.of(new TextResult("Unix"), new TextResult("other"), new TextResult("UNIX")));
    }

    private void assertConditionalCollectionValues(EntityHandler handler) {
        String suffix = " FROM MapJoinAliasEntity p JOIN p.operatingSystems s ORDER BY p.id";
        assertEquals(List.of("Unix", "Windows", "UNIX"),
                handler.createQuery("SELECT s" + suffix, String.class).getResultList());
        assertEquals(Arrays.asList("Unix", null, "UNIX"),
                handler.createQuery("SELECT NULLIF(s, 'Windows')" + suffix, String.class).getResultList());
        assertEquals(List.of("Unix", "Windows", "UNIX"),
                handler.createQuery("SELECT COALESCE(s, 'other')" + suffix, String.class).getResultList());
        assertEquals(List.of("Unix", "other", "UNIX"), handler.createQuery(
                "SELECT CASE WHEN p.id = 2 THEN 'other' ELSE s END" + suffix, String.class).getResultList());
        assertEquals(List.of(new TextResult("Unix"), new TextResult("other"), new TextResult("UNIX")), handler.createQuery(
                "SELECT COALESCE(NULLIF(s, 'Windows'), 'other')" + suffix, TextResult.class).getResultList());
    }

    private void assertIncompatibleResultConstructor(EntityHandler handler) {
        for (String expression : List.of("NULLIF(e, 'Windows')", "NULLIF(VALUE(e), 'Windows')")) {
            IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                    () -> handler.createQuery("SELECT " + expression + FROM, NumberResult.class).getResultList());
            assertTrue(failure.getMessage(), failure.getMessage().contains("No matching JPQL result constructor"));
        }
    }

    public record TextResult(String value) {
    }

    public record NumberResult(Integer value) {
    }

    private void assertUndeclaredAliases(EntityHandler handler) {
        for (String expression : List.of("LOWER(missing)", "NULLIF(missing, 'x')", "NULLIF('x', missing)")) {
            assertInvalidQuery(handler, "SELECT " + expression + FROM);
        }
    }

    private void assertMalformedArguments(EntityHandler handler) {
        for (String expression : List.of("LOWER()", "LOWER(e, 'x')", "NULLIF(e,)",
                "NULLIF(e 'x')", "NULLIF(e, 'x', 'y')", "LOWER(VALUE())")) {
            assertInvalidQuery(handler, "SELECT " + expression + FROM);
        }
    }

    private void assertCollectionPaths(EntityHandler handler) {
        for (String expression : List.of("LOWER(p.environment)", "NULLIF(p.environment, 'x')",
                "NULLIF('x', p.environment)")) {
            assertInvalidQuery(handler, "SELECT " + expression + FROM);
        }
        assertInvalidQuery(handler, "SELECT SIZE(e)" + FROM);
        assertInvalidQuery(handler, "SELECT p" + FROM + " WHERE :value MEMBER OF e");
        assertInvalidQuery(handler, "SELECT p" + FROM + " WHERE e IS EMPTY");
        assertEquals(List.of(3, 3, 3), handler.createQuery(
                "SELECT SIZE(p.environment) FROM MapJoinAliasEntity p ORDER BY p.id", Integer.class).getResultList());
    }

    private void assertInvalidQuery(EntityHandler handler, String query) {
        IllegalArgumentException failure = assertThrows(query, IllegalArgumentException.class,
                () -> handler.createQuery(query));
        Throwable cause = failure;
        while (cause != null && !(cause instanceof JPQLException)) {
            cause = cause.getCause();
        }
        assertTrue(query + ": " + failure, cause instanceof JPQLException);
    }

    private List<Long> owners(EntityHandler handler, String predicate) {
        return handler.createQuery(WHERE + predicate + " ORDER BY p.id", MapJoinAliasEntity.class)
                .getResultList().stream().map(MapJoinAliasEntity::getId).toList();
    }

    private <T> void assertValues(EntityHandler handler, String bare, String explicit, Class<T> type, List<T> expected) {
        String suffix = FROM + " WHERE KEY(e) = 'osName' ORDER BY p.id";
        List<T> control = handler.createQuery("SELECT " + explicit + suffix, type).getResultList();
        List<T> actual = handler.createQuery("SELECT " + bare + suffix, type).getResultList();
        assertEquals(explicit, expected, control);
        assertEquals(bare, expected, actual);
        assertEquals(bare, control, actual);
    }
}
