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
package org.eclipse.persistence.jpa.entityagent;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCodeConverter;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryGroup;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryRecord;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(EmfRunner.class)
public class TestJPQLCaseWithoutElse {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;

    @Emf(name = "case-without-else-no-validation", createTables = DDLGen.NONE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE"),
                    @Property(name = "eclipselink.jpql.validation", value = "None")})
    private EntityManagerFactory unvalidatedEmf;

    @Before
    public void seed() {
        cleanup();
        emf.runInTransaction(em -> {
            AgentQueryGroup group = new AgentQueryGroup(1, "First");
            em.persist(group);
            em.persist(new AgentQueryRecord(1, "Alpha", group));
            em.persist(new AgentQueryRecord(2, "Beta", group));
            em.persist(new AgentQueryRecord(3, "Gamma", group));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createQuery("delete from AgentQueryRecord").executeUpdate();
            em.createQuery("delete from AgentQueryGroup").executeUpdate();
        });
        emf.getCache().evictAll();
        unvalidatedEmf.getCache().evictAll();
    }

    @Test
    public void entityManagerSearchedCaseReturnsMatchedValuesAndNull() {
        try (EntityManager em = emf.createEntityManager()) {
            assertSearchedCase(em);
        }
    }

    @Test
    public void entityAgentSearchedCaseReturnsMatchedValuesAndNull() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertSearchedCase(agent);
        }
    }

    @Test
    public void entityManagerSimpleCaseReturnsMatchedValuesAndNull() {
        try (EntityManager em = emf.createEntityManager()) {
            assertSimpleCase(em);
        }
    }

    @Test
    public void entityAgentSimpleCaseReturnsMatchedValuesAndNull() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertSimpleCase(agent);
        }
    }

    @Test
    public void entityManagerCaseRetainsNumericResultTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNumericCases(em);
        }
    }

    @Test
    public void entityAgentCaseRetainsNumericResultTypes() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNumericCases(agent);
        }
    }

    @Test
    public void entityManagerCoalesceHandlesUnmatchedCase() {
        try (EntityManager em = emf.createEntityManager()) {
            assertCoalesce(em);
        }
    }

    @Test
    public void entityAgentCoalesceHandlesUnmatchedCase() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertCoalesce(agent);
        }
    }

    @Test
    public void entityManagerNestedCaseRetainsBranches() {
        try (EntityManager em = emf.createEntityManager()) {
            assertNestedCase(em);
        }
    }

    @Test
    public void entityAgentNestedCaseRetainsBranches() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertNestedCase(agent);
        }
    }

    @Test
    public void entityManagerExplicitElseAndOtherSelectionsRemainIndependent() {
        try (EntityManager em = emf.createEntityManager()) {
            assertExplicitElseAndOtherSelections(em);
        }
    }

    @Test
    public void entityAgentExplicitElseAndOtherSelectionsRemainIndependent() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertExplicitElseAndOtherSelections(agent);
        }
    }

    @Test
    public void entityManagerCaseExecutesWithValidationDisabled() {
        assertEquals("None", unvalidatedEmf.getProperties().get("eclipselink.jpql.validation"));
        try (EntityManager em = unvalidatedEmf.createEntityManager()) {
            assertSearchedCase(em);
            assertSimpleCase(em);
            assertCoalesce(em);
        }
    }

    @Test
    public void entityAgentCaseExecutesWithValidationDisabled() {
        assertEquals("None", unvalidatedEmf.getProperties().get("eclipselink.jpql.validation"));
        try (EntityAgent agent = unvalidatedEmf.createEntityAgent()) {
            assertSearchedCase(agent);
            assertSimpleCase(agent);
            assertCoalesce(agent);
        }
    }

    private void assertSearchedCase(EntityHandler handler) {
        assertEquals(Arrays.asList("Alpha", "Beta", null), handler.createQuery(
                "select case when r.id = :first then r.title when r.id = :second then r.title end "
                        + "from AgentQueryRecord r order by r.id", String.class)
                .setParameter("first", 1).setParameter("second", 2).getResultList());
    }

    private void assertSimpleCase(EntityHandler handler) {
        assertEquals(Arrays.asList("Alpha", "Beta", null), handler.createQuery(
                "select case r.id when :first then r.title when :second then r.title end "
                        + "from AgentQueryRecord r order by r.id", String.class)
                .setParameter("first", 1).setParameter("second", 2).getResultList());
    }

    private void assertNumericCases(EntityHandler handler) {
        assertEquals(Arrays.asList(1, 12, null), results(handler,
                "case when r.id = 1 then r.id when r.id = 2 then r.id + 10 end", Integer.class));
        assertEquals(Arrays.asList(11, 22, null), results(handler,
                "case r.id when 1 then 11 when 2 then 22 end", Integer.class));
    }

    private void assertCoalesce(EntityHandler handler) {
        assertEquals(List.of("Alpha", "fallback", "fallback"), results(handler,
                "coalesce(case when r.id = 1 then r.title end, 'fallback')", String.class));
        assertEquals(List.of(-1, 12, -1), results(handler,
                "coalesce(case when r.id = 2 then r.id + 10 end, -1)", Integer.class));
    }

    private void assertNestedCase(EntityHandler handler) {
        assertEquals(Arrays.asList("Alpha", "Beta", null), results(handler,
                "case when r.id < 3 then case r.id when 1 then 'Alpha' when 2 then 'Beta' end end",
                String.class));
        assertEquals(Arrays.asList("Alpha", null, null), results(handler,
                "case r.id when 1 then case when r.title = 'Alpha' then r.title end end", String.class));
    }

    private void assertExplicitElseAndOtherSelections(EntityHandler handler) {
        assertEquals(List.of("Alpha", "other", "other"), results(handler,
                "case when r.id = 1 then r.title else 'other' end", String.class));
        assertEquals(List.of("other", "Beta", "other"), results(handler,
                "case r.id when 2 then r.title else 'other' end", String.class));
        List<Object[]> rows = handler.createQuery(
                "select r.id, case when r.id = 1 then r.title end, "
                        + "case r.id when 2 then r.id end from AgentQueryRecord r order by r.id",
                Object[].class).getResultList();
        assertEquals(3, rows.size());
        assertArrayEquals(new Object[]{1, "Alpha", null}, rows.get(0));
        assertArrayEquals(new Object[]{2, null, 2}, rows.get(1));
        assertArrayEquals(new Object[]{3, null, null}, rows.get(2));
    }

    private <T> List<T> results(EntityHandler handler, String expression, Class<T> resultType) {
        return handler.createQuery("select " + expression + " from AgentQueryRecord r order by r.id", resultType)
                .getResultList();
    }
}
