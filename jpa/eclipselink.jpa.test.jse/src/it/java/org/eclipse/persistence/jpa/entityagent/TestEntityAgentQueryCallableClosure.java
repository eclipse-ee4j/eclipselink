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
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Parameter;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.Statement;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.EntityManagerFactory;

import java.util.Calendar;
import java.util.Date;
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

import static org.junit.Assert.*;

@RunWith(EmfRunner.class)
public class TestEntityAgentQueryCallableClosure {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;

    @Before
    public void seed() {
        cleanup();
        emf.runInTransaction(em -> em.persist(new AgentQueryRecord(1, "Callable", null)));
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createStatement("delete from AgentQueryRecord").execute();
            em.createStatement("delete from AgentQueryGroup").execute();
        });
        emf.getCache().evictAll();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void metadataLegacyFlushAndOptionsRemainUsableOnAnOpenAgent() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQuery<AgentQueryRecord> named = agent.createQuery(
                    "select r from AgentQueryRecord r where r.id = :id", AgentQueryRecord.class);
            Parameter<Integer> parameter = named.getParameter("id", Integer.class);
            assertEquals("id", parameter.getName());
            assertNull(parameter.getPosition());
            assertThrows(IllegalStateException.class, () -> named.getParameterValue(parameter));
            assertThrows(IllegalArgumentException.class, () -> named.getParameter("id", String.class));
            named.setParameter(parameter, 1);
            assertEquals(Integer.valueOf(1), named.getParameterValue(parameter));
            assertEquals("Callable", named.getSingleResult().title);

            TypedQuery<AgentQueryRecord> positional = agent.createQuery(
                    "select r from AgentQueryRecord r where r.id = ?1", AgentQueryRecord.class);
            assertEquals(Integer.valueOf(1), positional.getParameter(1).getPosition());
            assertEquals(Integer.class, positional.getParameter(1, Integer.class).getParameterType());
            assertThrows(IllegalArgumentException.class, () -> positional.getParameter(1, String.class));
            positional.setParameter(positional.getParameter(1, Integer.class), 1);
            positional.setFlushMode(FlushModeType.COMMIT);
            assertEquals(FlushModeType.COMMIT, positional.getFlushMode());
            AgentQueryRecord detached = positional.getSingleResult();
            detached.setTitle("Never flushed");
            positional.setFlushMode(FlushModeType.AUTO);
            assertEquals(FlushModeType.AUTO, positional.getFlushMode());
            assertEquals(PessimisticLockScope.NORMAL, positional.getLockScope());
            positional.setLockScope(PessimisticLockScope.EXTENDED);
            assertEquals(PessimisticLockScope.EXTENDED, positional.getLockScope());

            Statement statement = agent.createStatement(
                    "update AgentQueryRecord r set r.title = :title where r.id = :id");
            assertSame(statement, statement.addOption(QueryFlushMode.NO_FLUSH));
            assertEquals(QueryFlushMode.NO_FLUSH, statement.getQueryFlushMode());
            statement.setParameter("title", "Statement").setParameter("id", 1);
            agent.getTransaction().begin();
            positional.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            assertEquals("Callable", positional.getSingleResult().title);
            assertEquals(1, statement.execute());
            assertEquals("Statement", agent.get(AgentQueryRecord.class, 1).title);
            agent.getTransaction().rollback();
            assertEquals("Callable", agent.get(AgentQueryRecord.class, 1).title);
            assertEquals("Never flushed", detached.title);
        }
        try (var em = emf.createEntityManager()) {
            TypedQuery<AgentQueryRecord> positional = em.createQuery(
                    "select r from AgentQueryRecord r where r.id = ?1", AgentQueryRecord.class);
            Parameter<Integer> position = positional.getParameter(1, Integer.class);
            assertEquals(Integer.valueOf(1), position.getPosition());
            assertNull(position.getName());
            positional.setParameter(position, 1);
            assertEquals("Callable", positional.getSingleResult().title);
            TypedQuery<AgentQueryRecord> named = em.createQuery(
                    "select r from AgentQueryRecord r where r.id = :id", AgentQueryRecord.class);
            Parameter<Integer> name = named.getParameter("id", Integer.class);
            assertEquals("id", name.getName());
            assertNull(name.getPosition());
            named.setParameter(name, 1);
            assertEquals("Callable", named.getSingleResult().title);
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void dateTemporalOverloadsBindAndRebindActualPredicates() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            Date today = databaseDate(agent);
            Calendar previous = Calendar.getInstance();
            previous.setTime(today);
            previous.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = previous.getTime();
            TypedQuery<Integer> named = agent.createQuery(
                    "select r.id from AgentQueryRecord r where r.id = 1 and CURRENT_DATE = :day", Integer.class);
            named.setParameter("day", today, TemporalType.DATE);
            assertEquals(List.of(1), named.getResultList());
            named.setParameter("day", yesterday, TemporalType.DATE);
            assertTrue(named.getResultList().isEmpty());
            TypedQuery<Integer> positional = agent.createQuery(
                    "select r.id from AgentQueryRecord r where r.id = 1 and CURRENT_DATE = ?1", Integer.class);
            positional.setParameter(1, today, TemporalType.DATE);
            assertEquals(List.of(1), positional.getResultList());
            positional.setParameter(1, yesterday, TemporalType.DATE);
            assertTrue(positional.getResultList().isEmpty());

            var builder = agent.getCriteriaBuilder();
            var criteria = builder.createQuery(Integer.class);
            var record = criteria.from(AgentQueryRecord.class);
            var day = builder.parameter(Date.class, "day");
            criteria.select(record.get("id")).where(builder.equal(record.get("id"), 1),
                    builder.equal(builder.currentDate(), day));
            TypedQuery<Integer> typed = agent.createQuery(criteria);
            typed.setParameter(day, today, TemporalType.DATE);
            assertEquals(List.of(1), typed.getResultList());
            typed.setParameter(day, yesterday, TemporalType.DATE);
            assertTrue(typed.getResultList().isEmpty());
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void calendarTemporalOverloadsBindAndRebindActualPredicates() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            Calendar today = Calendar.getInstance();
            today.setTime(databaseDate(agent));
            Calendar yesterday = (Calendar) today.clone();
            yesterday.add(Calendar.DAY_OF_MONTH, -1);
            TypedQuery<Integer> named = agent.createQuery(
                    "select r.id from AgentQueryRecord r where r.id = 1 and CURRENT_DATE = :day", Integer.class);
            named.setParameter("day", today, TemporalType.DATE);
            assertEquals(List.of(1), named.getResultList());
            named.setParameter("day", yesterday, TemporalType.DATE);
            assertTrue(named.getResultList().isEmpty());
            TypedQuery<Integer> positional = agent.createQuery(
                    "select r.id from AgentQueryRecord r where r.id = 1 and CURRENT_DATE = ?1", Integer.class);
            positional.setParameter(1, today, TemporalType.DATE);
            assertEquals(List.of(1), positional.getResultList());
            positional.setParameter(1, yesterday, TemporalType.DATE);
            assertTrue(positional.getResultList().isEmpty());

            var builder = agent.getCriteriaBuilder();
            var criteria = builder.createQuery(Integer.class);
            var record = criteria.from(AgentQueryRecord.class);
            var day = builder.parameter(Calendar.class, "day");
            criteria.select(record.get("id")).where(builder.equal(record.get("id"), 1),
                    builder.equal(builder.currentDate(), day));
            TypedQuery<Integer> typed = agent.createQuery(criteria);
            typed.setParameter(day, today, TemporalType.DATE);
            assertEquals(List.of(1), typed.getResultList());
            typed.setParameter(day, yesterday, TemporalType.DATE);
            assertTrue(typed.getResultList().isEmpty());
        }
    }

    private static Date databaseDate(EntityAgent agent) {
        return agent.createQuery("select CURRENT_DATE from AgentQueryRecord r where r.id = 1", Date.class).getSingleResult();
    }
}
