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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.AttributeNode;
import jakarta.persistence.BatchFetch;
import jakarta.persistence.FetchType;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Parameter;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.RollbackException;
import jakarta.persistence.Statement;
import jakarta.persistence.StatementReference;
import jakarta.persistence.Timeout;
import jakarta.persistence.TransactionRequiredException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.persistence.jpa.entityagent.model.AgentQueryGroup;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCode;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCodeConverter;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryRecord;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryProjection;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static jakarta.persistence.sql.ResultSetMapping.*;
import static org.junit.Assert.*;

@RunWith(EmfRunner.class)
public class TestEntityAgentQueries {
    @Emf(createTables = DDLGen.DROP_CREATE, classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;

    @Emf(name = "agent-query-other-factory", createTables = DDLGen.NONE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory otherEmf;

    @Before
    public void seed() {
        cleanup();
        emf.runInTransaction(em -> {
            AgentQueryGroup first = new AgentQueryGroup(1, "First");
            AgentQueryGroup second = new AgentQueryGroup(2, "Second");
            em.persist(first);
            em.persist(second);
            em.persist(new AgentQueryRecord(1, "Alpha", first));
            em.persist(new AgentQueryRecord(2, "Beta", first));
            em.persist(new AgentQueryRecord(3, "Gamma", second));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createQuery("delete from AgentQueryRecord").executeUpdate();
            em.createQuery("delete from AgentQueryGroup").executeUpdate();
        });
        emf.getCache().evictAll();
    }

    @Test
    public void directAndHelperQueryResultsRemainDetached() {
        try (EntityAgent agent = emf.createEntityAgent(Map.of("jakarta.persistence.cache.retrieveMode", CacheRetrieveMode.USE))) {
            AgentQueryRecord first = agent.createQuery("select r from AgentQueryRecord r where r.id = 1", AgentQueryRecord.class)
                    .getSingleResult();
            agent.fetch(first.group);
            first.title = "detached";
            first.getGroup().name = "detached group";
            agent.getTransaction().begin();
            assertEquals("Alpha", agent.createNamedQuery("EAQuery.byId", AgentQueryRecord.class).setParameter("id", 1)
                    .getSingleResult().title);
            agent.getTransaction().commit();
        }
        emf.runInTransaction(EntityAgent.class, agent -> agent.createQuery("select r from AgentQueryRecord r", AgentQueryRecord.class)
                .getResultList().forEach(record -> record.title = "helper detached"));
        assertEquals("Alpha", title(1));
        assertEquals("First", emf.callInTransaction(em -> em.find(AgentQueryGroup.class, 1).name));
        assertEquals(Integer.valueOf(3), emf.callInTransaction(EntityAgent.class,
                agent -> agent.createQuery("select r from AgentQueryRecord r", AgentQueryRecord.class).getResultList().size()));
    }

    @Test
    public void statementRequiresTransactionAndSharesImmediateVisibility() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            Statement statement = agent.createStatement("update AgentQueryRecord r set r.title = :title where r.id = :id")
                    .setParameter("title", "Changed").setParameter("id", 1);
            assertThrows(TransactionRequiredException.class, statement::execute);
            assertThrows(TransactionRequiredException.class, () -> agent.createQuery("delete from AgentQueryRecord r where r.id = 99").executeUpdate());
            agent.getTransaction().begin();
            assertEquals(1, statement.execute());
            assertEquals("Changed", agent.get(AgentQueryRecord.class, 1).title);
            agent.getTransaction().rollback();
        }
        assertEquals("Alpha", title(1));
    }

    @Test
    public void namedAndReferenceStatementsExecuteOnAgentTransaction() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            StatementReference reference = emf.addNamedStatement("EAQuery.registeredStatement",
                    agent.createStatement("update AgentQueryRecord r set r.title = :title where r.id = :id"));
            agent.getTransaction().begin();
            assertEquals(1, agent.createNamedStatement("EAQuery.rename").setParameter("title", "Named")
                    .setParameter("id", 1).execute());
            assertEquals(1, agent.createStatement(reference).setParameter("title", "Reference")
                    .setParameter("id", 2).execute());
            agent.getTransaction().commit();
        }
        assertEquals("Named", title(1));
        assertEquals("Reference", title(2));
    }

    @Test
    public void namedAndTypedReferencesPreserveResultsAndOptions() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQueryReference<AgentQueryRecord> reference = emf.addNamedQuery("EAQuery.registeredQuery",
                    agent.createQuery("select r from AgentQueryRecord r where r.id = :id", AgentQueryRecord.class)
                            .setCacheStoreMode(CacheStoreMode.BYPASS).setTimeout(1000));
            assertEquals("Beta", agent.createQuery(reference).setParameter("id", 2).getSingleResult().title);
            assertEquals("Gamma", agent.createNamedQuery("EAQuery.byId").ofType(AgentQueryRecord.class)
                    .setParameter("id", 3).getSingleResult().title);
            assertTrue(reference.getOptions().contains(CacheStoreMode.BYPASS));
            assertNull(reference.getArguments());
        }
    }

    @Test
    public void argumentReferencesBindNamesTypesAndNullValues() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQueryReference<AgentQueryRecord> named = new ArgumentReference("EAQuery.byTitle", null,
                    List.of(String.class), List.of("title"), List.of("Beta"));
            assertEquals("Beta", agent.createQuery(named).getSingleResult().title);
            TypedQueryReference<AgentQueryRecord> typedNull = new ArgumentReference("EAQuery.byTitle", AgentQueryRecord.class,
                    List.of(String.class), List.of("title"), java.util.Collections.singletonList(null));
            TypedQuery<AgentQueryRecord> query = agent.createQuery(typedNull);
            assertTrue(query.isBound(query.getParameter("title")));
            assertNull(query.getParameterValue("title"));
            assertTrue(query.getResultList().isEmpty());
            try (EntityManager em = emf.createEntityManager()) {
                assertEquals("Beta", em.createQuery(named).getSingleResult().title);
                assertTrue(em.createQuery(typedNull).getResultList().isEmpty());
            }
        }
    }

    @Test
    public void positionalReferencesIgnoreMethodParameterNames() {
        TypedQueryReference<AgentQueryRecord> reference = new ArgumentReference("EAQuery.nativeById", AgentQueryRecord.class,
                List.of(Integer.class), List.of("methodArgumentName"), List.of(3));
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertEquals("Gamma", agent.createQuery(reference).getSingleResult().title);
        }
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals("Gamma", em.createQuery(reference).getSingleResult().title);
        }
    }

    @Test
    public void criteriaQueryAndStatementUseSameAgent() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            CriteriaBuilder builder = agent.getCriteriaBuilder();
            CriteriaQuery<AgentQueryRecord> query = builder.createQuery(AgentQueryRecord.class);
            Root<AgentQueryRecord> root = query.from(AgentQueryRecord.class);
            query.select(root).where(builder.equal(root.get("id"), 2));
            assertEquals("Beta", agent.createQuery(query).getSingleResult().title);
            CriteriaUpdate<AgentQueryRecord> update = builder.createCriteriaUpdate(AgentQueryRecord.class);
            Root<AgentQueryRecord> target = update.from(AgentQueryRecord.class);
            update.set(target.get("title"), "Criteria").where(builder.equal(target.get("id"), 2));
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement(update).execute());
            assertEquals("Criteria", agent.createQuery(query).getSingleResult().title);
            agent.getTransaction().commit();
        }
        assertEquals("Criteria", title(2));
    }

    @Test
    public void graphFactoriesAndAdaptationLoadDetachedAssociations() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            EntityGraph<AgentQueryRecord> graph = agent.getEntityGraph(AgentQueryRecord.class, "EAQuery.group");
            AgentQueryRecord record = agent.createQuery("select r from AgentQueryRecord r where r.id = 1", graph).getSingleResult();
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(record, "group"));
            assertEquals("First", record.getGroup().name);
            AgentQueryRecord adapted = agent.createQuery("select r from AgentQueryRecord r where r.id = 2")
                    .withEntityGraph(graph).getSingleResult();
            assertEquals("First", adapted.getGroup().name);
            assertThrows(IllegalStateException.class,
                    () -> agent.createQuery("update AgentQueryRecord r set r.title = 'x'").withEntityGraph(graph));
        }
    }

    @Test
    public void ordinaryEntityManagerGraphReadPreservesManagedIdentityAndDescriptorTemplate() {
        var session = emf.unwrap(org.eclipse.persistence.jpa.JpaEntityManagerFactory.class).getServerSession();
        var template = session.getDescriptor(AgentQueryRecord.class).getQueryManager().getReadObjectQuery();
        var originalGraph = template.getLoadGroup();
        var originalCall = template.getCall();
        emf.getCache().evictAll();
        try (EntityManager manager = emf.createEntityManager()) {
            var graph = manager.getEntityGraph(AgentQueryRecord.class, "EAQuery.group");
            AgentQueryRecord row = manager.createQuery("select managedGraph from AgentQueryRecord managedGraph where managedGraph.id=1",
                    AgentQueryRecord.class).setHint("jakarta.persistence.loadgraph", graph).getSingleResult();
            assertTrue(manager.contains(row));
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(row, "group"));
            assertEquals("First", row.getGroup().name);
            assertSame(row, manager.find(AgentQueryRecord.class, 1));
        }
        assertSame(originalGraph, template.getLoadGroup());
        assertSame(originalCall, template.getCall());
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentQueryRecord row = agent.createQuery("select plainGraph from AgentQueryRecord plainGraph where plainGraph.id=1",
                    AgentQueryRecord.class).getSingleResult();
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(row.getGroup(), "name"));
            agent.fetch(row.getGroup());
            assertEquals("First", row.getGroup().name);
        }
        assertSame(originalGraph, template.getLoadGroup());
        assertSame(originalCall, template.getCall());
    }

    @Test
    public void nativeEntityNamedMappingAndScalarFactoriesWork() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentQueryRecord nativeEntity = agent.createNativeQuery("select * from EA_QUERY_RECORD where ID = ?1", AgentQueryRecord.class)
                    .setParameter(1, 1).getSingleResult();
            assertEquals("Alpha", nativeEntity.title);
            assertEquals("Beta", agent.createNamedQuery("EAQuery.nativeById", AgentQueryRecord.class)
                    .setParameter(1, 2).getSingleResult().title);
            assertEquals(3, agent.createNativeQuery("select * from EA_QUERY_RECORD", "EAQuery.recordMapping").getResultList().size());
            assertEquals("Gamma", agent.createNativeQuery("select TITLE from EA_QUERY_RECORD where ID = ?1", String.class)
                    .setParameter(1, 3).getSingleResult());
            nativeEntity.title = "native detached";
        }
        assertEquals("Alpha", title(1));
    }

    @Test
    public void nativeTupleCompoundAndNestedConstructorMappingsWork() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            var id = column("ID", Integer.class).withAlias("key");
            var title = column("TITLE", String.class);
            String sql = "select ID, TITLE from EA_QUERY_RECORD where ID = 1";
            Tuple tuple = agent.createNativeQuery(sql, tuple(id, title)).getSingleResult();
            assertEquals(Integer.valueOf(1), tuple.get(id));
            assertEquals("Alpha", tuple.get("TITLE"));
            assertEquals(Integer.valueOf(1), tuple.get("key", Integer.class));
            Object[] compound = agent.createNativeQuery(sql).withResultSetMapping(compound(id, title)).getSingleResult();
            assertArrayEquals(new Object[] {1, "Alpha"}, compound);
            QuerySummary summary = agent.createNativeQuery(sql,
                    constructor(QuerySummary.class, constructor(QueryIdentity.class, id), title)).getSingleResult();
            assertEquals(new QuerySummary(new QueryIdentity(1), "Alpha"), summary);
        }
    }

    @Test
    public void nativeNestedEntityMappingsInvokePostLoadOnce() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            String sql = "select * from EA_QUERY_RECORD where ID = 1";
            var entity = entity(AgentQueryRecord.class);
            assertEquals(1, agent.createNativeQuery(sql, entity).getSingleResult().loadCallbacks);
            Object[] compound = agent.createNativeQuery(sql, compound(entity, column("ID", Integer.class))).getSingleResult();
            assertEquals(1, ((AgentQueryRecord) compound[0]).loadCallbacks);
            Tuple tuple = agent.createNativeQuery(sql, tuple(entity, column("ID", Integer.class))).getSingleResult();
            assertEquals(1, tuple.get(0, AgentQueryRecord.class).loadCallbacks);
            QueryEntitySummary summary = agent.createNativeQuery(sql,
                    constructor(QueryEntitySummary.class, entity, column("ID", Integer.class))).getSingleResult();
            assertEquals(1, summary.entity().loadCallbacks);
        }
    }

    @Test
    public void resultCountIgnoresPaginationAndPreservesQueryState() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQuery<AgentQueryRecord> query = agent.createQuery(
                    "select r from AgentQueryRecord r where r.id >= :minimum order by r.id", AgentQueryRecord.class)
                    .setParameter("minimum", 1).setFirstResult(1).setMaxResults(1);
            assertEquals(3L, query.getResultCount());
            assertEquals(1, query.getFirstResult());
            assertEquals(1, query.getMaxResults());
            assertEquals("Beta", query.getSingleResult().title);
            assertEquals(2L, agent.createQuery("select distinct r.group.id from AgentQueryRecord r", Integer.class).getResultCount());
            assertEquals(2L, agent.createQuery("select r.group.id, count(r) from AgentQueryRecord r group by r.group.id", Object[].class)
                    .getResultCount());
            assertEquals(2L, agent.createNativeQuery("select ID from EA_QUERY_RECORD where ID >= ?1 order by ID", Integer.class)
                    .setParameter(1, 2).getResultCount());
        }
    }

    @Test
    public void typedConvertedAndPositionalBindingsWork() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQuery<AgentQueryRecord> query = agent.createQuery(
                    "select r from AgentQueryRecord r where r.id = ?1 and r.title = ?2", AgentQueryRecord.class);
            assertThrows(IllegalArgumentException.class, () -> query.setParameters(1));
            assertThrows(IllegalArgumentException.class, () -> query.setParameters(1, "Alpha", "extra"));
            assertEquals("Alpha", query.setParameters(1, "Alpha").getSingleResult().title);
            assertEquals("Beta", query.setParameter(1, 2, Integer.class)
                    .setConvertedParameter(2, new QueryTitle("Beta"), QueryTitleConverter.class).getSingleResult().title);
            assertThrows(IllegalArgumentException.class, () -> query.setParameter(0, 1));
            assertThrows(IllegalArgumentException.class, () -> query.setParameter("missing", 1));
            assertThrows(IllegalArgumentException.class, () -> query.setParameter(1, "wrong"));
        }
    }

    @Test
    public void mappedConvertedBindingsApplyTheExplicitConverterOnce() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQuery<AgentQueryRecord> query = agent.createQuery(
                    "select r from AgentQueryRecord r where r.code = :code", AgentQueryRecord.class);
            query.setConvertedParameter("code", new AgentQueryCode("Alpha"), AgentQueryCodeConverter.class);
            AgentQueryCodeConverter.conversions = 0;
            assertEquals("Alpha", query.getSingleResult().title);
            assertEquals(1, AgentQueryCodeConverter.conversions);
            assertEquals(1L, query.getResultCount());
            query.setHint("eclipselink.jdbc.bind-parameters", false);
            assertEquals("Alpha", query.getSingleResult().title);
            query.setHint("eclipselink.jdbc.bind-parameters", true);
            assertEquals("Beta", query.setParameter("code", new AgentQueryCode("Beta")).getSingleResult().title);
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement("update AgentQueryRecord r set r.title = :title where r.code = :code")
                    .setParameter("title", "Converted update")
                    .setConvertedParameter("code", new AgentQueryCode("Gamma"), AgentQueryCodeConverter.class).execute());
            agent.getTransaction().commit();
        }
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals("Converted update", em.createQuery(
                    "select r from AgentQueryRecord r where r.code = :code", AgentQueryRecord.class)
                    .setConvertedParameter("code", new AgentQueryCode("Gamma"), AgentQueryCodeConverter.class).getSingleResult().title);
        }
    }

    @Test
    public void mappedTypedNullAndMetamodelBindingsKeepTheirMappingConversion() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQuery<AgentQueryRecord> query = agent.createQuery(
                    "select r from AgentQueryRecord r where r.code = :code", AgentQueryRecord.class);
            assertTrue(query.setParameter("code", null, AgentQueryCode.class).getResultList().isEmpty());
            assertTrue(query.setConvertedParameter("code", null, AgentQueryCodeConverter.class).getResultList().isEmpty());
            var codeType = agent.getMetamodel().entity(AgentQueryRecord.class)
                    .getSingularAttribute("code", AgentQueryCode.class).getType();
            assertEquals("Alpha", query.setParameter("code", new AgentQueryCode("Alpha"), codeType).getSingleResult().title);
        }
    }

    @Test
    public void optionsAreIndependentAndConfigurationCanPrecedeTransaction() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQuery<AgentQueryRecord> query = agent.createQuery("select r from AgentQueryRecord r where r.id = 1", AgentQueryRecord.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE).setTimeout(1000).setQueryFlushMode(QueryFlushMode.NO_FLUSH);
            query.addOption(Timeout.milliseconds(2000));
            Set<TypedQuery.Option> options = query.getOptions();
            assertTrue(options.contains(Timeout.milliseconds(2000)));
            options.clear();
            assertFalse(query.getOptions().isEmpty());
            assertThrows(TransactionRequiredException.class, query::getResultList);
            agent.getTransaction().begin();
            assertEquals("Alpha", query.getSingleResult().title);
            agent.getTransaction().rollback();
        }
    }

    @Test
    public void adaptersRejectWrongKindsAndCardinality() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertThrows(IllegalStateException.class, () -> agent.createQuery("select r from AgentQueryRecord r").asStatement());
            assertThrows(IllegalStateException.class,
                    () -> agent.createQuery("delete from AgentQueryRecord").ofType(AgentQueryRecord.class));
            assertThrows(IllegalArgumentException.class,
                    () -> agent.createQuery("select r from AgentQueryRecord r").ofType(String.class));
            assertThrows(NonUniqueResultException.class,
                    () -> agent.createQuery("select r from AgentQueryRecord r", AgentQueryRecord.class).getSingleResult());
            TypedQuery<AgentQueryRecord> missing = agent.createQuery("select r from AgentQueryRecord r where r.id = 99", AgentQueryRecord.class);
            assertNull(missing.getSingleResultOrNull());
            assertThrows(NoResultException.class, missing::getSingleResult);
            try (var stream = agent.createQuery("select r from AgentQueryRecord r", AgentQueryRecord.class).getResultStream()) {
                assertEquals(3L, stream.count());
            }
        }
    }

    @Test
    public void acquiredQueryObjectsRejectUseAfterClose() {
        EntityAgent agent = emf.createEntityAgent();
        TypedQuery<AgentQueryRecord> query = agent.createQuery("select r from AgentQueryRecord r", AgentQueryRecord.class);
        Statement statement = agent.createNativeStatement("delete from EA_QUERY_RECORD where ID = ?1");
        agent.close();
        assertThrows(IllegalStateException.class, query::getResultList);
        assertThrows(IllegalStateException.class, query::getOptions);
        assertThrows(IllegalStateException.class, () -> query.setParameter("id", 1));
        assertThrows(IllegalStateException.class, statement::getHints);
        assertThrows(IllegalStateException.class, statement::execute);
    }

    @Test
    public void ordinaryEntityManagerStillMaintainsIdentityAndFlushesChanges() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            AgentQueryRecord record = em.find(AgentQueryRecord.class, 1);
            assertSame(record, em.createQuery("select r from AgentQueryRecord r where r.id = 1", AgentQueryRecord.class).getSingleResult());
            record.setTitle("Managed");
            assertEquals("Managed", em.createQuery("select r.title from AgentQueryRecord r where r.id = 1", String.class).getSingleResult());
            em.getTransaction().commit();
        }
        assertEquals("Managed", title(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void convertedCriteriaParametersRetainConversionThroughQueryMetadata() {
        CriteriaBuilder builder = emf.getCriteriaBuilder();
        CriteriaQuery<AgentQueryRecord> criteria = builder.createQuery(AgentQueryRecord.class);
        Root<AgentQueryRecord> root = criteria.from(AgentQueryRecord.class);
        var parameter = builder.convertedParameter(AgentQueryCodeConverter.class);
        criteria.select(root).where(root.get("code").equalTo(parameter));
        try (EntityAgent agent = emf.createEntityAgent()) {
            TypedQuery<AgentQueryRecord> query = agent.createQuery(criteria);
            query.setParameter(parameter, new AgentQueryCode("Alpha"));
            AgentQueryCodeConverter.conversions = 0;
            assertEquals("Alpha", query.getSingleResult().title);
            assertEquals(1, AgentQueryCodeConverter.conversions);
            Parameter<AgentQueryCode> recovered = (Parameter<AgentQueryCode>) query.getParameters().iterator().next();
            assertEquals(AgentQueryCode.class, recovered.getParameterType());
            assertEquals("Beta", query.setParameter(recovered, new AgentQueryCode("Beta")).getSingleResult().title);
            assertTrue(query.setParameter(recovered, null).getResultList().isEmpty());
        }
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals("Gamma", em.createQuery(criteria).setParameter(parameter, new AgentQueryCode("Gamma"))
                    .getSingleResult().title);
        }
    }

    @Test
    public void factoryResultSetMappingsPreserveTypesAndCanBeExecutedByBothHandlers() {
        var summaries = emf.getResultSetMappings(AgentQueryRecord.Summary.class);
        assertTrue(summaries.containsKey("EAQuery.summaryMapping"));
        assertFalse(summaries.containsKey("EAQuery.titleMapping"));
        assertTrue(emf.getResultSetMappings(Object.class).keySet().containsAll(summaries.keySet()));
        assertTrue(emf.getResultSetMappings(String.class).containsKey("EAQuery.titleMapping"));
        String sql = "select ID, TITLE from EA_QUERY_RECORD where ID = 1";
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertEquals(new AgentQueryRecord.Summary(1, "Alpha"),
                    agent.createNativeQuery(sql, summaries.get("EAQuery.summaryMapping")).getSingleResult());
        }
        try (EntityManager em = emf.createEntityManager()) {
            assertEquals(new AgentQueryRecord.Summary(1, "Alpha"),
                    em.createNativeQuery(sql, summaries.get("EAQuery.summaryMapping")).getSingleResult());
        }
    }

    @Test
    public void namedResultMappingLocksRequireTransactionOnlyAtExecution() {
        var mapping = emf.getResultSetMappings(AgentQueryRecord.class).get("EAQuery.lockedMapping");
        assertNotNull(mapping);
        try (EntityAgent agent = emf.createEntityAgent()) {
            var named = agent.createNativeQuery("select * from EA_QUERY_RECORD where ID = 1", "EAQuery.lockedMapping");
            var typed = agent.createNativeQuery("select * from EA_QUERY_RECORD where ID = 2", mapping);
            assertThrows(TransactionRequiredException.class, named::getResultList);
            assertThrows(TransactionRequiredException.class, typed::getResultList);
            agent.getTransaction().begin();
            assertEquals(1, named.getResultList().size());
            assertEquals("Beta", typed.getSingleResult().title);
            agent.getTransaction().commit();
        }
    }

    @Test
    public void nativeStatementsInvalidateSharedCacheOnlyAfterCommit() {
        assertEquals("Alpha", title(1));
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            assertEquals(1, em.createNativeStatement("update EA_QUERY_RECORD set TITLE = ?1 where ID = ?2")
                    .setParameters("rolled back", 1).execute());
            assertEquals("Alpha", title(1));
            em.getTransaction().rollback();
            assertEquals("Alpha", title(1));
            em.getTransaction().begin();
            assertEquals(1, em.createNativeStatement("update EA_QUERY_RECORD set TITLE = ?1 where ID = ?2")
                    .setParameters("committed", 1).execute());
            em.getTransaction().commit();
            em.clear();
            assertEquals("committed", em.find(AgentQueryRecord.class, 1).title);
        }
    }

    @Test
    public void nativeStatementCacheBypassDoesNotInvalidateSharedEntries() {
        assertEquals("Alpha", title(1));
        emf.runInTransaction(em -> em.createNativeStatement("update EA_QUERY_RECORD set TITLE = ?1 where ID = ?2")
                .setParameters("bypassed", 1).setCacheStoreMode(CacheStoreMode.BYPASS).executeUpdate());
        assertEquals("Alpha", title(1));
        try (EntityAgent agent = emf.createEntityAgent(CacheRetrieveMode.BYPASS, CacheStoreMode.BYPASS)) {
            assertEquals("bypassed", agent.get(AgentQueryRecord.class, 1).title);
        }
    }

    @Test
    public void graphNodesExposeMetamodelAndPreserveExistingSubgraphs() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            var graph = agent.createEntityGraph(AgentQueryRecord.class);
            var type = emf.getMetamodel().entity(AgentQueryRecord.class);
            assertSame(type, graph.getGraphedType());
            assertFalse(graph.hasAttributeNode("group"));
            assertThrows(NoSuchElementException.class, () -> graph.getAttributeNode("group"));
            AttributeNode<AgentQueryGroup> node = graph.addAttributeNode("group");
            assertSame(type.getAttribute("group"), node.getAttribute());
            var child = node.addSubgraph();
            child.addAttributeNode("name");
            assertSame(emf.getMetamodel().entity(AgentQueryGroup.class), child.getGraphedType());
            assertSame(node, graph.addAttributeNode("group"));
            assertSame(child, graph.addSubgraph("group"));
            assertTrue(node.getOptions().contains(FetchType.EAGER));
            assertTrue(node.getSubgraphs().get(AgentQueryGroup.class).hasAttributeNode("name"));
            var row = agent.createQuery("select r from AgentQueryRecord r where r.id = 1", graph).getSingleResult();
            assertEquals("First", row.getGroup().name);
        }
    }

    @Test
    public void graphOptionsAndRemovedNodesSurviveIndependentNamedCopies() {
        var graph = emf.getMetamodel().entity(AgentQueryRecord.class).createEntityGraph();
        AttributeNode<AgentQueryGroup> node = graph.addAttributeNode("group");
        node.addOption(new BatchFetch(2)).addOption(new BatchFetch(5)).addOption(CacheRetrieveMode.BYPASS);
        var options = node.getOptions();
        assertTrue(options.contains(new BatchFetch(5)));
        assertFalse(options.contains(new BatchFetch(2)));
        options.clear();
        assertFalse(node.getOptions().isEmpty());
        graph.removeAttributeNode("group");
        assertTrue(graph.hasAttributeNode("group"));
        assertTrue(graph.getAttributeNode("group").getOptions().contains(FetchType.LAZY));
        assertEquals(1, graph.getAttributeNodes().size());
        emf.addNamedEntityGraph("EAQuery.optionCopy", graph);
        try (EntityAgent agent = emf.createEntityAgent()) {
            var first = agent.getEntityGraph(AgentQueryRecord.class, "EAQuery.optionCopy");
            var second = agent.getEntityGraph(AgentQueryRecord.class, "EAQuery.optionCopy");
            assertTrue(first.getAttributeNode("group").getOptions().contains(FetchType.LAZY));
            first.addAttributeNode("group").addOption(new BatchFetch(8));
            assertTrue(first.getAttributeNode("group").getOptions().contains(FetchType.EAGER));
            assertTrue(second.getAttributeNode("group").getOptions().contains(FetchType.LAZY));
            assertTrue(second.getAttributeNode("group").getOptions().contains(new BatchFetch(5)));
            assertTrue(emf.getMetamodel().entity(AgentQueryRecord.class).getNamedEntityGraphs()
                    .get("EAQuery.optionCopy").getAttributeNode("group").getOptions().contains(FetchType.LAZY));
        }
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void graphInvalidTargetsFailWithoutMutatingValidNodes() {
        try (EntityManager em = emf.createEntityManager()) {
            var graph = em.createEntityGraph(AgentQueryRecord.class);
            assertThrows(IllegalArgumentException.class, () -> graph.hasAttributeNode("missing"));
            assertThrows(IllegalArgumentException.class, () -> graph.addSubgraph("title"));
            assertFalse(graph.hasAttributeNode("title"));
            assertThrows(IllegalArgumentException.class, () -> graph.addKeySubgraph("title"));
            assertThrows(IllegalArgumentException.class, () -> graph.addElementSubgraph("group"));
            assertThrows(IllegalArgumentException.class, () -> graph.addSubgraph("group", String.class));
            assertThrows(IllegalArgumentException.class, () -> graph.addTreatedSubgraph((Class) AgentQueryGroup.class));
            Attribute foreign = emf.getMetamodel().entity(AgentQueryGroup.class).getAttribute("id");
            assertThrows(IllegalArgumentException.class, () -> graph.addAttributeNode(foreign));
            graph.addAttributeNode("group");
            graph.removeAttributeNodes(Attribute.PersistentAttributeType.MANY_TO_ONE);
            assertTrue(graph.getAttributeNode("group").getOptions().contains(FetchType.LAZY));
            graph.addSubgraph("group").addAttributeNode("name");
            assertTrue(graph.getAttributeNode("group").getOptions().contains(FetchType.EAGER));
        }
    }

    @Test
    public void jpqlConstructorEntityArgumentsAreDetachedBeforeConstruction() {
        assertEquals("Alpha", title(1));
        AgentQueryProjection projection;
        try (EntityAgent agent = emf.createEntityAgent()) {
            projection = agent.createQuery("select new org.eclipse.persistence.jpa.entityagent.model.AgentQueryProjection(r) "
                    + "from AgentQueryRecord r where r.id = 1", AgentQueryProjection.class).getSingleResult();
            assertEquals(1, projection.record().loadCallbacks);
            agent.fetch(projection.record().group);
            projection.record().title = "untracked";
            agent.getTransaction().begin();
            agent.getTransaction().commit();
            assertEquals("Alpha", agent.get(AgentQueryRecord.class, 1).title);
        }
        assertEquals("First", projection.record().getGroup().name);
        assertEquals("Alpha", title(1));
    }

    @Test
    public void criteriaConstructorEntityArgumentsAreDetachedAndOrdinaryEmRemainsManaged() {
        var cb = emf.getCriteriaBuilder();
        var criteria = cb.createQuery(AgentQueryProjection.class);
        var root = criteria.from(AgentQueryRecord.class);
        criteria.select(cb.construct(AgentQueryProjection.class, root)).where(cb.equal(root.get("id"), 1));
        AgentQueryProjection projection;
        try (EntityAgent agent = emf.createEntityAgent()) {
            projection = agent.createQuery(criteria).getSingleResult();
            assertEquals(1, projection.record().loadCallbacks);
            agent.fetch(projection.record().group);
            projection.record().title = "untracked";
        }
        assertEquals("First", projection.record().getGroup().name);
        try (EntityManager em = emf.createEntityManager()) {
            var managed = em.createQuery(criteria).getSingleResult().record();
            assertTrue(em.contains(managed));
            assertEquals("Alpha", managed.title);
            em.getTransaction().begin();
            managed.setTitle("managed constructor result");
            em.getTransaction().commit();
        }
        assertEquals("managed constructor result", title(1));
    }

    @Test
    public void namedConstructorQueriesDoNotRetainTheCreatingAgent() {
        String name = "EAQuery.namedProjection";
        try (EntityAgent creator = emf.createEntityAgent()) {
            var source = creator.createQuery("select new org.eclipse.persistence.jpa.entityagent.model.AgentQueryProjection(r) "
                    + "from AgentQueryRecord r where r.id = 1", AgentQueryProjection.class)
                    .setHint("eclipselink.query-results-cache", true);
            source.getSingleResult().record().title = "must not enter result cache";
            assertEquals("Alpha", source.getSingleResult().record().title);
            emf.addNamedQuery(name, source);
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentQueryProjection result = agent.createNamedQuery(name, AgentQueryProjection.class).getSingleResult();
            assertEquals(1, result.record().loadCallbacks);
            result.record().title = "untracked";
        }
        assertEquals("Alpha", title(1));
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            AgentQueryProjection result = em.createNamedQuery(name, AgentQueryProjection.class).getSingleResult();
            assertTrue(em.contains(result.record()));
            result.record().setTitle("managed named projection");
            em.getTransaction().commit();
        }
        assertEquals("managed named projection", title(1));
    }

    @Test
    public void namedNativeConstructorMappingsUseTheExecutingHandler() {
        String name = "EAQuery.namedNativeProjection";
        try (EntityAgent creator = emf.createEntityAgent()) {
            var mapping = constructor(AgentQueryProjection.class, entity(AgentQueryRecord.class));
            var source = creator.createNativeQuery("select * from EA_QUERY_RECORD where ID = 1", mapping)
                    .setHint("eclipselink.query-results-cache", true);
            source.getSingleResult().record().title = "must not enter result cache";
            assertEquals("Alpha", source.getSingleResult().record().title);
            emf.addNamedQuery(name, source);
        }
        try (EntityAgent agent = emf.createEntityAgent()) {
            AgentQueryProjection result = agent.createNamedQuery(name, AgentQueryProjection.class).getSingleResult();
            assertEquals(1, result.record().loadCallbacks);
            result.record().title = "untracked";
        }
        assertEquals("Alpha", title(1));
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            AgentQueryProjection result = em.createNamedQuery(name, AgentQueryProjection.class).getSingleResult();
            assertTrue(em.contains(result.record()));
            result.record().setTitle("managed named native projection");
            em.getTransaction().commit();
        }
        assertEquals("managed named native projection", title(1));
    }

    private String title(int id) {
        return emf.callInTransaction(em -> em.find(AgentQueryRecord.class, id).title);
    }

    private record ArgumentReference(String name, Class<AgentQueryRecord> resultType,
                                     List<Class<?>> parameterTypes, List<String> parameterNames,
                                     List<Object> arguments) implements TypedQueryReference<AgentQueryRecord> {
        @Override
        public String getName() { return name; }
        @Override
        public Class<AgentQueryRecord> getResultType() { return resultType; }
        @Override
        public List<Class<?>> getParameterTypes() { return parameterTypes; }
        @Override
        public List<String> getParameterNames() { return parameterNames; }
        @Override
        public List<Object> getArguments() { return arguments; }
        @Override
        public Map<String, Object> getHints() { return Map.of(); }
        @Override
        public Set<TypedQuery.Option> getOptions() { return Set.of(); }
        @Override
        public String getEntityGraphName() { return null; }
    }

    public record QueryEntitySummary(AgentQueryRecord entity, int id) {
    }

    public record QueryIdentity(int id) {
    }

    public record QuerySummary(QueryIdentity identity, String title) {
    }

    public record QueryTitle(String title) {
    }

    public static class QueryTitleConverter implements AttributeConverter<QueryTitle, String> {
        @Override
        public String convertToDatabaseColumn(QueryTitle value) {
            return value == null ? null : value.title();
        }
        @Override
        public QueryTitle convertToEntityAttribute(String value) {
            return value == null ? null : new QueryTitle(value);
        }
    }
    @Test
    public void mutatedNamedGraphLoadsCachedCollectionsWithoutChangingNamedMetadata() {
        emf.getCache().evictAll();
        try (EntityManager em = emf.createEntityManager()) {
            AgentQueryGroup cached = em.find(AgentQueryGroup.class, 1);
            assertFalse(emf.getPersistenceUnitUtil().isLoaded(cached, "records"));
            EntityGraph<AgentQueryGroup> graph = em.getEntityGraph(AgentQueryGroup.class, "EAQueryGroup.name");
            graph.addAttributeNode("records");
            AgentQueryGroup loaded = em.createQuery("select g from AgentQueryGroup g where g.id = 1", graph).getSingleResult();
            assertSame(cached, loaded);
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(loaded, "records"));
            assertEquals(2, loaded.records.size());
            assertFalse(em.getEntityGraph(AgentQueryGroup.class, "EAQueryGroup.name").hasAttributeNode("records"));
            em.clear();
            AgentQueryGroup adapted = em.createQuery("select g from AgentQueryGroup g where g.id = 1")
                    .withEntityGraph(graph).getSingleResult();
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(adapted, "records"));
            assertEquals(2, adapted.records.size());
        }
    }

    @Test
    public void augmentationDoesNotRegisterAnonymousQueriesOrStatements() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            CriteriaBuilder builder = agent.getCriteriaBuilder();
            var select = emf.getNamedQueries(AgentQueryRecord.class).get("EAQuery.byId");
            var update = emf.getNamedStatements().get("EAQuery.rename");
            var delete = emf.addNamedStatement("EAQuery.augment.registryDelete",
                    agent.createStatement("delete from AgentQueryRecord r where r.id = :id"));
            var session = emf.unwrap(org.eclipse.persistence.jpa.JpaEntityManagerFactory.class).getServerSession();
            int queryCount = session.getQueries().values().stream().mapToInt(List::size).sum();
            Set<String> queryNames = Set.copyOf(emf.getNamedQueries(Object.class).keySet());
            Set<String> statementNames = Set.copyOf(emf.getNamedStatements().keySet());
            for (int index = 0; index < 24; index++) {
                assertNull(builder.augment(select, criteria -> {}).getName());
                assertNull(builder.augment(update, criteria -> {}).getName());
                assertNull(builder.augment(delete, criteria -> {}).getName());
                assertThrows(IllegalArgumentException.class,
                        () -> builder.augment(select, String.class, criteria -> {}));
                assertThrows(IllegalStateException.class, () -> builder.augment(update, criteria -> {
                    ((CriteriaUpdate<?>) criteria).set("title", "failed augmentation");
                    throw new IllegalStateException("failed augmentation");
                }));
                assertThrows(IllegalStateException.class, () -> builder.augment(delete, criteria -> {
                    throw new IllegalStateException("failed augmentation");
                }));
            }
            assertEquals(queryCount, session.getQueries().values().stream().mapToInt(List::size).sum());
            assertEquals(queryNames, emf.getNamedQueries(Object.class).keySet());
            assertEquals(statementNames, emf.getNamedStatements().keySet());
            assertEquals("Alpha", agent.createQuery(select).setParameter("id", 1).getSingleResult().title);
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement(update).setParameter("id", 1).setParameter("title", "Original").execute());
            assertEquals("Original", agent.get(AgentQueryRecord.class, 1).title);
            agent.getTransaction().rollback();
        }
    }

    @Test
    public void augmentedReferencesOutliveTheirCreatorAndKeepIndependentExecutionState() {
        TypedQueryReference<AgentQueryRecord> reference;
        TypedQueryReference<String> titles;
        Map<String, Object> hints = new LinkedHashMap<>(Map.of("eclipselink.jdbc.fetch-size", 7));
        List<Object> arguments = new ArrayList<>(List.of(1));
        try (EntityManager creator = emf.createEntityManager()) {
            var original = new AugmentationReference("EAQuery.byId", hints, arguments);
            var builder = creator.getCriteriaBuilder();
            reference = builder.augment(builder.augment(original, criteria -> {}), criteria -> {});
            var scalarSource = new ArgumentReference("EAQuery.byId", AgentQueryRecord.class,
                    List.of(Integer.class), List.of("id"), List.of(1));
            titles = builder.augment(builder.augment(scalarSource, criteria -> {}), String.class,
                    criteria -> criteria.select(criteria.getRoots().iterator().next().get("title")));
        }
        hints.put("eclipselink.jdbc.fetch-size", 99);
        arguments.set(0, 3);
        assertEquals(7, reference.getHints().get("eclipselink.jdbc.fetch-size"));
        assertEquals(List.of(1), reference.getArguments());
        assertEquals("EAQuery.group", reference.getEntityGraphName());
        assertEquals(Set.of(QueryFlushMode.NO_FLUSH), reference.getOptions());
        try (EntityAgent agent = emf.createEntityAgent(); EntityManager em = emf.createEntityManager()) {
            var first = agent.createQuery(reference).setParameter("id", 2).setHint("eclipselink.jdbc.fetch-size", 3);
            var second = agent.createQuery(reference);
            assertEquals("Beta", first.getSingleResult().title);
            AgentQueryRecord alpha = second.getSingleResult();
            assertEquals("Alpha", alpha.title);
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(alpha, "group"));
            assertEquals(7, second.getHints().get("eclipselink.jdbc.fetch-size"));
            assertEquals(QueryFlushMode.NO_FLUSH, second.getQueryFlushMode());
            var managedFirst = em.createQuery(reference).setParameter("id", 3);
            var managedSecond = em.createQuery(reference);
            assertEquals("Gamma", managedFirst.getSingleResult().title);
            AgentQueryRecord managed = managedSecond.getSingleResult();
            assertEquals("Alpha", managed.title);
            assertTrue(em.contains(managed));
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(managed, "group"));
            assertEquals(String.class, titles.getResultType());
            assertEquals("Alpha", agent.createQuery(titles).getSingleResult());
            assertEquals("Alpha", em.createQuery(titles).getSingleResult());
        }
        try (EntityAgent other = otherEmf.createEntityAgent(); EntityManager otherEm = otherEmf.createEntityManager()) {
            assertThrows(IllegalArgumentException.class, () -> other.createQuery(reference));
            assertThrows(IllegalArgumentException.class, () -> otherEm.createQuery(reference));
            assertThrows(IllegalArgumentException.class,
                    () -> other.getCriteriaBuilder().augment(reference, criteria -> {}));
        }
    }

    @Test
    public void augmentedUpdateAndDeleteReferencesOutliveTheirCreatorForBothHandlers() {
        StatementReference update;
        StatementReference delete;
        List<Object> arguments = new ArrayList<>(List.of("Bound", 1));
        try (EntityAgent creator = emf.createEntityAgent()) {
            var builder = creator.getCriteriaBuilder();
            var originalUpdate = new AugmentationStatementReference("EAQuery.rename", arguments);
            update = builder.augment(builder.augment(originalUpdate, criteria -> {}), criteria -> {});
            var originalDelete = emf.addNamedStatement("EAQuery.augment.retainedDelete",
                    creator.createStatement("delete from AgentQueryRecord r where r.id = :id"));
            delete = builder.augment(builder.augment(originalDelete, criteria -> {}), criteria -> {
                var criteriaDelete = (CriteriaDelete<?>) criteria;
                criteriaDelete.where(builder.and(criteriaDelete.getRestriction(),
                        builder.equal(criteriaDelete.getRoot().get("group").get("id"), 1)));
            });
        }
        arguments.set(0, "Changed source");
        arguments.set(1, 3);
        assertEquals(List.of("Bound", 1), update.getArguments());
        assertEquals(Set.of(QueryFlushMode.NO_FLUSH), update.getOptions());
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement(update).execute());
            assertEquals("Bound", agent.get(AgentQueryRecord.class, 1).title);
            var first = agent.createStatement(update).setParameter("id", 1).setParameter("title", "First");
            var second = agent.createStatement(update).setParameter("id", 2).setParameter("title", "Second");
            assertEquals(1, first.execute());
            assertEquals(1, second.execute());
            assertEquals("First", agent.get(AgentQueryRecord.class, 1).title);
            assertEquals("Second", agent.get(AgentQueryRecord.class, 2).title);
            assertEquals(0, agent.createStatement(delete).setParameter("id", 3).execute());
            assertEquals(1, agent.createStatement(delete).setParameter("id", 2).execute());
            agent.getTransaction().rollback();
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                assertEquals(1, em.createStatement(update).execute());
                assertEquals("Bound", em.createQuery("select r.title from AgentQueryRecord r where r.id = 1", String.class)
                        .getSingleResult());
                assertEquals(1, em.createStatement(update).setParameter("id", 1).setParameter("title", "Managed").execute());
                assertEquals("Managed", em.createQuery("select r.title from AgentQueryRecord r where r.id = 1", String.class)
                        .getSingleResult());
                assertEquals(0, em.createStatement(delete).setParameter("id", 3).execute());
                assertEquals(1, em.createStatement(delete).setParameter("id", 2).execute());
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
        try (EntityAgent other = otherEmf.createEntityAgent(); EntityManager otherEm = otherEmf.createEntityManager()) {
            assertThrows(IllegalArgumentException.class, () -> other.createStatement(update));
            assertThrows(IllegalArgumentException.class, () -> otherEm.createStatement(delete));
            assertThrows(IllegalArgumentException.class,
                    () -> other.getCriteriaBuilder().augment(delete, criteria -> {}));
        }
    }

    @Test
    public void retainedAugmentationCriteriaCannotChangeStatementTemplate() {
        List<CriteriaUpdate<?>> retained = new ArrayList<>();
        StatementReference reference;
        try (EntityAgent creator = emf.createEntityAgent()) {
            var original = emf.getNamedStatements().get("EAQuery.rename");
            reference = creator.getCriteriaBuilder().augment(original, criteria -> {
                var update = (CriteriaUpdate<?>) criteria;
                update.set("version", 41);
                retained.add(update);
            });
        }
        retained.get(0).set("version", 42);
        try (EntityAgent agent = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement(reference).setParameter("title", "Snapshot")
                    .setParameter("id", 1).execute());
            assertEquals(Integer.valueOf(41), agent.get(AgentQueryRecord.class, 1).version);
            agent.getTransaction().rollback();
        }
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            try {
                assertEquals(1, em.createStatement(reference).setParameter("title", "Snapshot")
                        .setParameter("id", 2).execute());
                assertEquals(Integer.valueOf(41), em.createQuery(
                        "select r.version from AgentQueryRecord r where r.id = 2", Integer.class).getSingleResult());
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    private record AugmentationReference(String name, Map<String, Object> hints,
                                         List<Object> arguments) implements TypedQueryReference<AgentQueryRecord> {
        @Override
        public String getName() { return name; }
        @Override
        public Class<AgentQueryRecord> getResultType() { return AgentQueryRecord.class; }
        @Override
        public Map<String, Object> getHints() { return hints; }
        @Override
        public Set<TypedQuery.Option> getOptions() { return Set.of(QueryFlushMode.NO_FLUSH); }
        @Override
        public String getEntityGraphName() { return "EAQuery.group"; }
        @Override
        public List<Class<?>> getParameterTypes() { return List.of(Integer.class); }
        @Override
        public List<String> getParameterNames() { return List.of("id"); }
        @Override
        public List<Object> getArguments() { return arguments; }
    }

    private record AugmentationStatementReference(String name, List<Object> arguments) implements StatementReference {
        @Override
        public String getName() { return name; }
        @Override
        public Map<String, Object> getHints() { return Map.of(); }
        @Override
        public Set<Statement.Option> getOptions() { return Set.of(QueryFlushMode.NO_FLUSH); }
        @Override
        public List<Class<?>> getParameterTypes() { return List.of(String.class, Integer.class); }
        @Override
        public List<String> getParameterNames() { return List.of("title", "id"); }
        @Override
        public List<Object> getArguments() { return arguments; }
    }

    @Test
    public void augmentedNamedQueriesPreserveOriginalOrderingParametersAndOptions() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            var original = emf.addNamedQuery("EAQuery.augment.read", agent.createQuery(
                    "select r from AgentQueryRecord r where r.id >= :minimum order by r.id desc", AgentQueryRecord.class)
                    .setQueryFlushMode(QueryFlushMode.NO_FLUSH).setHint("eclipselink.jdbc.fetch-size", 7));
            CriteriaBuilder builder = agent.getCriteriaBuilder();
            var augmented = builder.augment(original, criteria -> {
                assertNotNull(criteria.getRestriction());
                assertNotNull(criteria.getSelection());
                assertEquals(1, criteria.getOrderList().size());
                Root<?> root = criteria.getRoots().iterator().next();
                criteria.where(builder.and(criteria.getRestriction(), builder.equal(root.get("group").get("id"), 1)));
            });
            assertEquals(original.getHints(), augmented.getHints());
            assertEquals(original.getOptions(), augmented.getOptions());
            assertEquals(List.of(2, 1), agent.createQuery(augmented).setParameter("minimum", 1)
                    .getResultList().stream().map(record -> record.id).toList());
            assertEquals(List.of(3, 2, 1), agent.createQuery(original).setParameter("minimum", 1)
                    .getResultList().stream().map(record -> record.id).toList());
            var second = builder.augment(augmented, criteria -> criteria.where(
                    builder.and(criteria.getRestriction(), builder.equal(criteria.getRoots().iterator().next().get("id"), 1))));
            assertEquals(1, agent.createQuery(second).setParameter("minimum", 1).getSingleResult().id.intValue());
        }
    }

    @Test
    public void augmentedReferencesInheritUnexecutedNamedPagination() {
        String name = "EAQuery.augment.pagination";
        try (EntityManager creator = emf.createEntityManager()) {
            emf.addNamedQuery(name, creator.createQuery(
                    "select r from AgentQueryRecord r order by r.id", AgentQueryRecord.class)
                    .setFirstResult(1).setMaxResults(1));
        }
        var original = emf.getNamedQueries(AgentQueryRecord.class).get(name);
        var builder = emf.getCriteriaBuilder();
        var augmented = builder.augment(builder.augment(original, criteria -> {}), criteria -> {});
        try (EntityAgent agent = emf.createEntityAgent(); EntityManager em = emf.createEntityManager()) {
            for (TypedQuery<AgentQueryRecord> query : List.of(agent.createQuery(original), em.createQuery(original),
                    agent.createQuery(augmented), em.createQuery(augmented))) {
                assertEquals(1, query.getFirstResult());
                assertEquals(1, query.getMaxResults());
                assertEquals(List.of(2), query.getResultList().stream().map(record -> record.id).toList());
            }
            for (TypedQuery<AgentQueryRecord> query : List.of(agent.createQuery(original), em.createQuery(original),
                    agent.createQuery(augmented), em.createQuery(augmented))) {
                query.setFirstResult(0).setMaxResults(2);
                assertEquals(0, query.getFirstResult());
                assertEquals(2, query.getMaxResults());
                assertEquals(List.of(1, 2), query.getResultList().stream().map(record -> record.id).toList());
            }
            for (TypedQuery<AgentQueryRecord> query : List.of(agent.createQuery(augmented), em.createQuery(augmented))) {
                assertEquals(1, query.getFirstResult());
                assertEquals(1, query.getMaxResults());
                assertEquals(List.of(2), query.getResultList().stream().map(record -> record.id).toList());
            }
            for (TypedQuery<AgentQueryRecord> query : List.of(
                    agent.createQuery("select r from AgentQueryRecord r order by r.id", AgentQueryRecord.class),
                    em.createQuery("select r from AgentQueryRecord r order by r.id", AgentQueryRecord.class))) {
                assertEquals(0, query.getFirstResult());
                assertEquals(Integer.MAX_VALUE, query.getMaxResults());
                assertEquals(3, query.getResultList().size());
            }
        }
    }

    @Test
    public void augmentedReferencesInheritNamedLocksBeforeExplicitOverrides() {
        for (LockModeType lock : List.of(LockModeType.OPTIMISTIC, LockModeType.PESSIMISTIC_WRITE)) {
            String name = "EAQuery.augment.lock." + lock.name();
            try (EntityManager creator = emf.createEntityManager()) {
                emf.addNamedQuery(name, creator.createQuery(
                        "select r from AgentQueryRecord r where r.id = 1", AgentQueryRecord.class).setLockMode(lock));
            }
            var original = emf.getNamedQueries(AgentQueryRecord.class).get(name);
            assertTrue("Discovered metadata must supply the inherited lock", original.getOptions().isEmpty());
            var augmented = emf.getCriteriaBuilder().augment(original, criteria -> {});
            var overridden = emf.getCriteriaBuilder().augment(new LockOptionReference(name, LockModeType.NONE), criteria -> {});
            try (EntityAgent agent = emf.createEntityAgent(); EntityManager em = emf.createEntityManager()) {
                for (TypedQuery<AgentQueryRecord> query : List.of(agent.createQuery(original), em.createQuery(original),
                        agent.createQuery(augmented), em.createQuery(augmented))) {
                    assertEquals(lock, query.getLockMode());
                    assertThrows(TransactionRequiredException.class, query::getResultList);
                    query.setLockMode(LockModeType.NONE);
                    assertEquals(LockModeType.NONE, query.getLockMode());
                    assertEquals(Integer.valueOf(1), query.getSingleResult().id);
                }
                for (TypedQuery<AgentQueryRecord> query : List.of(agent.createQuery(overridden), em.createQuery(overridden))) {
                    assertEquals(LockModeType.NONE, query.getLockMode());
                    assertEquals(Integer.valueOf(1), query.getSingleResult().id);
                }
                assertEquals(lock, agent.createQuery(augmented).getLockMode());
                assertEquals(lock, em.createQuery(augmented).getLockMode());
            }
        }
    }

    @Test
    public void augmentedNamedForceIncrementRetainsCommitObligation() {
        String name = "EAQuery.augment.forceIncrement";
        try (EntityManager creator = emf.createEntityManager()) {
            emf.addNamedQuery(name, creator.createQuery(
                    "select r from AgentQueryRecord r where r.id = 1", AgentQueryRecord.class)
                    .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT));
        }
        var augmented = emf.getCriteriaBuilder().augment(emf.getNamedQueries(AgentQueryRecord.class).get(name), criteria -> {});
        try (EntityAgent agent = emf.createEntityAgent()) {
            int version = agent.get(AgentQueryRecord.class, 1).version;
            agent.getTransaction().begin();
            assertTrue(agent.createQuery(augmented).getSingleResult().version >= version);
            agent.getTransaction().commit();
            assertTrue(agent.get(AgentQueryRecord.class, 1).version > version);
        }
        try (EntityManager em = emf.createEntityManager(); EntityAgent observer = emf.createEntityAgent()) {
            int version = observer.get(AgentQueryRecord.class, 1).version;
            em.getTransaction().begin();
            try {
                assertTrue(em.createQuery(augmented).getSingleResult().version >= version);
                em.getTransaction().commit();
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
            assertTrue(observer.get(AgentQueryRecord.class, 1).version > version);
        }
    }

    @Test
    public void augmentedNamedOptimisticReadRejectsCompetingVersionAtCommit() {
        String name = "EAQuery.augment.optimistic";
        try (EntityManager creator = emf.createEntityManager()) {
            emf.addNamedQuery(name, creator.createQuery(
                    "select r from AgentQueryRecord r where r.id = 1", AgentQueryRecord.class)
                    .setLockMode(LockModeType.OPTIMISTIC));
        }
        var augmented = emf.getCriteriaBuilder().augment(emf.getNamedQueries(AgentQueryRecord.class).get(name), criteria -> {});
        try (EntityAgent agent = emf.createEntityAgent(); EntityAgent peer = emf.createEntityAgent()) {
            agent.getTransaction().begin();
            AgentQueryRecord read = agent.createQuery(augmented).getSingleResult();
            AgentQueryRecord changed = peer.get(AgentQueryRecord.class, 1);
            changed.setTitle("Competing update");
            peer.update(changed);
            assertTrue(changed.version > read.version);
            RollbackException failure = assertThrows(RollbackException.class, agent.getTransaction()::commit);
            assertTrue(failure.getCause() instanceof OptimisticLockException);
            assertFalse(agent.getTransaction().isActive());
            assertEquals("Competing update", peer.get(AgentQueryRecord.class, 1).title);
        }
    }

    private record LockOptionReference(String name, LockModeType lock) implements TypedQueryReference<AgentQueryRecord> {
        @Override
        public String getName() { return name; }
        @Override
        public Class<AgentQueryRecord> getResultType() { return AgentQueryRecord.class; }
        @Override
        public Map<String, Object> getHints() { return Map.of(); }
        @Override
        public Set<TypedQuery.Option> getOptions() { return Set.of(lock); }
        @Override
        public String getEntityGraphName() { return null; }
        @Override
        public List<Class<?>> getParameterTypes() { return List.of(); }
        @Override
        public List<String> getParameterNames() { return List.of(); }
        @Override
        public List<Object> getArguments() { return List.of(); }
    }

    @Test
    public void augmentationCanChangeSelectionAndRejectsForeignOrNativeReferences() {
        try (EntityAgent agent = emf.createEntityAgent(); EntityManager other = otherEmf.createEntityManager()) {
            var original = emf.addNamedQuery("EAQuery.augment.type", agent.createQuery(
                    "select r from AgentQueryRecord r order by r.id", AgentQueryRecord.class));
            CriteriaBuilder builder = agent.getCriteriaBuilder();
            var titles = builder.augment(original, String.class, criteria -> {
                assertNull(criteria.getSelection());
                criteria.select(criteria.getRoots().iterator().next().get("title"));
            });
            assertEquals(List.of("Alpha", "Beta", "Gamma"), agent.createQuery(titles).getResultList());
            assertEquals(3, agent.createQuery(original).getResultList().size());
            assertThrows(IllegalArgumentException.class, () -> builder.augment(original, String.class, criteria -> {}));
            var foreign = otherEmf.addNamedQuery(original.getName(), other.createQuery(
                    "select r from AgentQueryRecord r", AgentQueryRecord.class));
            assertThrows(IllegalArgumentException.class, () -> builder.augment(foreign, criteria -> {}));
            var nativeReference = emf.addNamedQuery("EAQuery.augment.native", agent.createNativeQuery(
                    "select * from EA_QUERY_RECORD", AgentQueryRecord.class));
            assertThrows(IllegalArgumentException.class, () -> builder.augment(nativeReference, criteria -> {}));
        }
    }

    @Test
    public void augmentedStatementsRetainAssignmentsAndDoNotModifyOriginal() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            CriteriaBuilder builder = agent.getCriteriaBuilder();
            StatementReference original = emf.addNamedStatement("EAQuery.augment.update", agent.createStatement(
                    "update AgentQueryRecord r set r.title = :title"));
            StatementReference augmented = builder.augment(original, statement -> {
                CriteriaUpdate<?> update = (CriteriaUpdate<?>) statement;
                update.where(builder.equal(update.getRoot().get("id"), 2));
            });
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement(augmented).setParameter("title", "Changed").execute());
            assertEquals("Changed", agent.get(AgentQueryRecord.class, 2).title);
            assertEquals("Alpha", agent.get(AgentQueryRecord.class, 1).title);
            agent.getTransaction().rollback();
            agent.getTransaction().begin();
            assertEquals(3, agent.createStatement(original).setParameter("title", "All").execute());
            agent.getTransaction().rollback();
        }
    }

    @Test
    public void jpqlCriteriaProducersSupportReadUpdateDeleteAndTargetValidation() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            CriteriaBuilder builder = agent.getCriteriaBuilder();
            var read = builder.createQuery(AgentQueryRecord.class, "select r from AgentQueryRecord r where r.id = 1");
            read.where(builder.equal(read.getRoots().iterator().next().get("id"), 2));
            assertEquals("Beta", agent.createQuery(read).getSingleResult().title);
            assertEquals(3, agent.createQuery(builder.createQuery("select r from AgentQueryRecord r")).getResultList().size());
            var update = builder.createCriteriaUpdate(AgentQueryRecord.class,
                    "update AgentQueryRecord r set r.title = :title where r.id = 1");
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement(update).setParameter("title", "Updated").execute());
            var delete = builder.createCriteriaDelete(AgentQueryRecord.class, "delete from AgentQueryRecord r where r.id = 3");
            assertEquals(1, agent.createStatement(delete).execute());
            agent.getTransaction().rollback();
            agent.getTransaction().begin();
            assertEquals(1, agent.createStatement(builder.createCriteriaUpdate(
                    "update AgentQueryRecord r set r.title = 'Untyped' where r.id = 2")).execute());
            assertEquals(1, agent.createStatement(builder.createCriteriaDelete(
                    "delete from AgentQueryRecord r where r.id = 3")).execute());
            agent.getTransaction().rollback();
            assertThrows(IllegalArgumentException.class, () -> builder.createCriteriaUpdate(AgentQueryGroup.class,
                    "update AgentQueryRecord r set r.title = 'Bad'"));
            assertThrows(IllegalArgumentException.class, () -> builder.createCriteriaDelete(AgentQueryGroup.class,
                    "delete from AgentQueryRecord r"));
        }
    }

    @Test
    public void scalarNamedReferencesExposeAndPreserveTheirActualResultType() {
        TypedQueryReference<String> reference;
        try (EntityAgent creator = emf.createEntityAgent()) {
            reference = emf.addNamedQuery("EAQuery.scalarReference", creator.createQuery(
                    "select r.title from AgentQueryRecord r where r.id = :id", String.class));
            assertEquals(String.class, reference.getResultType());
        }
        TypedQueryReference<String> discovered = emf.getNamedQueries(String.class).get(reference.getName());
        assertNotNull(discovered);
        assertEquals(String.class, discovered.getResultType());
        assertFalse(emf.getNamedQueries(AgentQueryRecord.class).containsKey(reference.getName()));
        try (EntityAgent agent = emf.createEntityAgent(); EntityManager em = emf.createEntityManager()) {
            assertEquals("Alpha", agent.createQuery(reference).setParameter("id", 1).getSingleResult());
            assertEquals("Beta", agent.createQuery(discovered).setParameter("id", 2).getSingleResult());
            assertEquals("Gamma", em.createQuery(discovered).setParameter("id", 3).getSingleResult());
        }
    }

    @Test
    public void constructorNamedReferencesExposeProjectionTypeAndKeepHandlerSemantics() {
        TypedQueryReference<AgentQueryProjection> reference;
        try (EntityAgent creator = emf.createEntityAgent()) {
            reference = emf.addNamedQuery("EAQuery.projectionReference", creator.createQuery(
                    "select new org.eclipse.persistence.jpa.entityagent.model.AgentQueryProjection(r) "
                            + "from AgentQueryRecord r where r.id = :id", AgentQueryProjection.class));
            assertEquals(AgentQueryProjection.class, reference.getResultType());
        }
        TypedQueryReference<AgentQueryProjection> discovered = emf.getNamedQueries(AgentQueryProjection.class).get(reference.getName());
        assertNotNull(discovered);
        assertEquals(AgentQueryProjection.class, discovered.getResultType());
        try (EntityAgent agent = emf.createEntityAgent(); EntityManager em = emf.createEntityManager()) {
            AgentQueryRecord detached = agent.createQuery(discovered).setParameter("id", 1).getSingleResult().record();
            assertFalse(em.contains(detached));
            detached.title = "Detached mutation";
            assertEquals("Alpha", agent.createQuery(reference).setParameter("id", 1).getSingleResult().record().title);
            AgentQueryRecord managed = em.createQuery(discovered).setParameter("id", 2).getSingleResult().record();
            assertTrue(em.contains(managed));
        }
    }

    @Test
    public void untypedNamedGraphAndGraphListReturnIndependentUsableDefinitions() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            var untyped = agent.getEntityGraph("EAQuery.group");
            assertEquals(AgentQueryRecord.class, untyped.getGraphedType().getJavaType());
            assertTrue(untyped.hasAttributeNode("group"));
            var graphs = agent.getEntityGraphs(AgentQueryRecord.class);
            var listed = graphs.stream().filter(graph -> "EAQuery.group".equals(graph.getName())).findFirst().orElseThrow();
            assertTrue(listed.hasAttributeNode("group"));
            untyped.addAttributeNode("title");
            assertFalse(listed.hasAttributeNode("title"));
            listed.addAttributeNode("code");
            assertFalse(agent.getEntityGraph("EAQuery.group").hasAttributeNode("code"));
            graphs.clear();
            assertFalse(agent.getEntityGraphs(AgentQueryRecord.class).isEmpty());
            assertEquals("First", agent.createQuery("select r from AgentQueryRecord r where r.id = 1")
                    .withEntityGraph(agent.getEntityGraph(AgentQueryRecord.class, "EAQuery.group"))
                    .getSingleResult().getGroup().name);
            assertThrows(IllegalArgumentException.class, () -> agent.getEntityGraph("EAQuery.missing"));
            assertThrows(IllegalArgumentException.class, () -> agent.getEntityGraphs(String.class));
        }
    }

}
