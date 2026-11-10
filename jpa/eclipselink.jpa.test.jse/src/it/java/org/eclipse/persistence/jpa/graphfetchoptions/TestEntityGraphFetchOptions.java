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
package org.eclipse.persistence.jpa.graphfetchoptions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.metamodel.Attribute;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.indirection.UnitOfWorkValueHolder;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.graphfetchoptions.model.GraphFetchChild;
import org.eclipse.persistence.jpa.graphfetchoptions.model.GraphFetchDetail;
import org.eclipse.persistence.jpa.graphfetchoptions.model.GraphFetchRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphChild;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphSpecialChild;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphOtherChild;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphDetail;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphSpecialDetail;
import org.eclipse.persistence.jpa.namedgraphs.model.SharedGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.SharedGraphChild;
import org.eclipse.persistence.jpa.namedgraphs.model.PropertyGraphBase;
import org.eclipse.persistence.jpa.namedgraphs.model.PropertyGraphRoot;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.queries.LoadGroup;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

/** Graph-local suppression of eager mappings using real woven objects. */
public class TestEntityGraphFetchOptions {
    private static final AtomicLong IDENTITIES = new AtomicLong();
    private static final String SELECT_ONE = "select r from GraphFetchRoot r where r.id = 1";
    private static final List<String> TABLES = List.of("JPA_GFO_MEMBERS", "JPA_GFO_PEERS", "JPA_GFO_KEYED", "JPA_GFO_TAGS", "JPA_GFO_LAZY_TAGS",
            "JPA_GFO_ROOT", "JPA_GFO_CHILD", "JPA_GFO_DETAIL");
    private static final Set<String> ROOT_TO_ONES = Set.of("many", "one", "unlisted", "lazy", "joined", "batched");
    private static EntityManagerFactory fixture;
    private static SchemaManager schema;
    private static boolean ownsSchema;

    @BeforeClass
    public static void createFixture() throws Exception {
        assertTrue("Fetch-group weaving is required", FetchGroupTracker.class.isAssignableFrom(GraphFetchRoot.class));
        for (String attribute : ROOT_TO_ONES) {
            assertHolder(GraphFetchRoot.class, attribute);
        }
        assertHolder(GraphFetchChild.class, "detail");
        assertHolder(GraphFetchChild.class, "otherDetail");
        try {
            fixture = configuration("fixture", false).createEntityManagerFactory();
            for (String attribute : ROOT_TO_ONES) {
                assertRuntimeIndirection(fixture, GraphFetchRoot.class, attribute);
            }
            assertRuntimeIndirection(fixture, GraphFetchChild.class, "detail");
            assertRuntimeIndirection(fixture, GraphFetchChild.class, "otherDetail");
            schema = new SchemaManager(session(fixture));
            for (String table : TABLES) {
                assertFalse("Refusing to acquire an existing fixture table: " + table, exists(table));
            }
            ownsSchema = true;
            schema.createDefaultTables(false);
            for (String table : TABLES) {
                assertTrue("Provider DDL must create " + table, exists(table));
            }
            try (EntityManager manager = fixture.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    for (int rootId = 1; rootId <= 2; rootId++) {
                        List<GraphFetchChild> children = new ArrayList<>();
                        for (int index = 1; index <= 8; index++) {
                            int childId = (rootId - 1) * 10 + index;
                            GraphFetchDetail detail = new GraphFetchDetail(childId * 2 - 1);
                            GraphFetchDetail other = new GraphFetchDetail(childId * 2);
                            GraphFetchChild child = new GraphFetchChild(childId, detail, other);
                            manager.persist(detail);
                            manager.persist(other);
                            manager.persist(child);
                            children.add(child);
                        }
                        manager.persist(new GraphFetchRoot(rootId, children));
                    }
                    manager.getTransaction().commit();
                } finally {
                    if (manager.getTransaction().isActive()) {
                        manager.getTransaction().rollback();
                    }
                }
            }
            fixture.getCache().evictAll();
        } catch (Exception | AssertionError failure) {
            try {
                removeFixture();
            } catch (Exception | AssertionError cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }

    @AfterClass
    public static void removeFixture() {
        try {
            if (ownsSchema) {
                schema.dropDefaultTables();
                for (String table : TABLES) {
                    assertFalse("Owned fixture table must be removed: " + table, exists(table));
                }
            }
        } finally {
            ownsSchema = false;
            if (fixture != null && fixture.isOpen()) {
                fixture.close();
            }
        }
    }

    @Test
    public void explicitLazyOptionSuppressesEagerManyToOneForFind() throws Exception {
        assertSuppressedRead("many", "child-1");
    }

    @Test
    public void removingUnlistedAttributeSuppressesItsEagerMapping() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.removeAttributeNode("many");
            assertTrue(graph.getAttributeNode("many").getOptions().contains(FetchType.LAZY));
            GraphFetchRoot root = manager.find(graph, 1);
            assertDeferred(fixture, root, "many");
            assertUnlistedEager(fixture, root);
            assertEquals("child-1", root.getMany().getLabel());
        }
    }

    @Test
    public void removingAddedAttributeSuppressesItsEagerMapping() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addAttributeNode("one");
            graph.removeAttributeNode("one");
            assertTrue(graph.getAttributeNode("one").getOptions().contains(FetchType.LAZY));
            assertDeferred(fixture, manager.find(graph, 1), "one");
        }
    }

    @Test
    public void readdingRemovedAttributeRestoresEagerFetching() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.removeAttributeNode("many");
            graph.addAttributeNode("many");
            assertTrue(graph.getAttributeNode("many").getOptions().contains(FetchType.EAGER));
            GraphFetchRoot root = manager.find(graph, 1);
            assertLoaded(fixture, root, "many");
            assertEquals("child-1", root.getMany().getLabel());
        }
    }

    @Test
    public void replacingLazyOptionWithEagerChangesTheNextFind() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = lazyGraph(fixture, "many");
            assertDeferred(fixture, manager.find(graph, 1), "many");
            assertTrue("Execution must not replace the graph's LAZY option",
                    graph.getAttributeNode("many").getOptions().contains(FetchType.LAZY));
            manager.clear();
            graph.getAttributeNode("many").addOption(FetchType.EAGER);
            GraphFetchRoot eager = manager.find(graph, 1);
            assertLoaded(fixture, eager, "many");
            assertEquals("child-1", eager.getMany().getLabel());
        }
    }

    @Test
    public void removingMetamodelAttributeSuppressesEagerFetching() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.removeAttributeNode(fixture.getMetamodel().entity(GraphFetchRoot.class).getAttribute("many"));
            assertDeferred(fixture, manager.find(graph, 1), "many");
        }
    }

    @Test
    public void removingAttributeTypeSuppressesOnlyThoseMappings() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.removeAttributeNodes(Attribute.PersistentAttributeType.MANY_TO_ONE);
            GraphFetchRoot root = manager.find(graph, 1);
            for (String attribute : List.of("many", "unlisted", "lazy", "joined", "batched")) {
                assertTrue(graph.getAttributeNode(attribute).getOptions().contains(FetchType.LAZY));
                assertDeferred(fixture, root, attribute);
            }
            assertLoaded(fixture, root, "one");
            assertLoaded(fixture, root, "members");
        }
    }

    @Test
    public void emptyLoadGraphRetainsMappingDefaults() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            GraphFetchRoot root = manager.find(emptyGraph(fixture), 1);
            for (String attribute : List.of("many", "one", "unlisted", "members", "peers", "tags", "note")) {
                assertLoaded(fixture, root, attribute);
            }
            assertDeferred(fixture, root, "lazy");
        }
    }

    @Test
    public void loadGraphHintDistinguishesLazyNodeFromUnlistedEagerMapping() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            GraphFetchRoot root = manager.find(GraphFetchRoot.class, 1,
                    Map.of("jakarta.persistence.loadgraph", lazyGraph(fixture, "many")));
            assertDeferred(fixture, root, "many");
            assertUnlistedEager(fixture, root);
        }
    }

    @Test
    public void fetchGraphKeepsExplicitEagerAndSuppressesLazyAndUnlistedNodes() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = lazyGraph(fixture, "many");
            graph.addAttributeNode("one");
            GraphFetchRoot root = manager.find(GraphFetchRoot.class, 1, Map.of("jakarta.persistence.fetchgraph", graph));
            assertDeferred(fixture, root, "many");
            assertDeferred(fixture, root, "unlisted");
            assertLoaded(fixture, root, "one");
            assertEquals("child-2", root.getOne().getLabel());
        }
    }

    @Test
    public void explicitLazyOptionSuppressesEagerOneToOne() throws Exception {
        assertSuppressedRead("one", "child-2");
    }

    @Test
    public void explicitLazyOptionSuppressesEagerOneToMany() throws Exception {
        assertSuppressedRead("members", List.of("child-4"));
    }

    @Test
    public void explicitLazyOptionSuppressesEagerManyToMany() throws Exception {
        assertSuppressedRead("peers", List.of("child-5"));
    }

    @Test
    public void explicitLazyOptionSuppressesEagerElementCollection() throws Exception {
        assertSuppressedRead("tags", List.of("tag-1"));
    }

    @Test
    public void explicitLazyOptionSuppressesEagerBasic() throws Exception {
        assertSuppressedRead("note", "root-1");
    }

    @Test
    public void nestedLazyOptionDoesNotSuppressUnlistedNestedEagerMapping() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addSubgraph("many", GraphFetchChild.class).addAttributeNode("detail").addOption(FetchType.LAZY);
            GraphFetchRoot root = manager.find(graph, 1);
            assertLoaded(fixture, root, "many");
            assertDeferred(fixture, root.getMany(), "detail");
            assertLoaded(fixture, root.getMany(), "otherDetail");
            assertEquals("detail-1", root.getMany().getDetail().getLabel());
        }
    }

    @Test
    public void nestedRemovalSuppressesEagerRelationship() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addSubgraph("many", GraphFetchChild.class).removeAttributeNode("detail");
            GraphFetchRoot root = manager.find(graph, 1);
            assertDeferred(fixture, root.getMany(), "detail");
            assertEquals("detail-1", root.getMany().getDetail().getLabel());
        }
    }

    @Test
    public void collectionSubgraphAppliesLazyOptionToItsElements() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addSubgraph("members", GraphFetchChild.class).addAttributeNode("detail").addOption(FetchType.LAZY);
            GraphFetchRoot root = manager.find(graph, 1);
            assertLoaded(fixture, root, "members");
            GraphFetchChild member = root.getMembers().getFirst();
            assertEquals("child-4", member.getLabel());
            assertDeferred(fixture, member, "detail");
            assertLoaded(fixture, member, "otherDetail");
            assertEquals("detail-7", member.getDetail().getLabel());
        }
    }

    @Test
    public void graphTypedQuerySuppressesEagerMappings() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            GraphFetchRoot root = manager.createQuery(SELECT_ONE, lazyGraph(fixture, "many", "members"))
                    .getSingleResult();
            assertDeferred(fixture, root, "many");
            assertDeferred(fixture, root, "members");
            assertUnlistedEager(fixture, root);
        }
    }

    @Test
    public void queryWithEntityGraphSuppressesEagerMappings() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            GraphFetchRoot root = manager.createQuery(SELECT_ONE)
                    .withEntityGraph(lazyGraph(fixture, "many", "tags")).getSingleResult();
            assertDeferred(fixture, root, "many");
            assertDeferred(fixture, root, "tags");
            assertUnlistedEager(fixture, root);
        }
    }

    @Test
    public void queryHintsApplyLoadAndFetchGraphSemantics() throws Exception {
        for (String hint : List.of("jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph")) {
            try (EntityManager manager = fixture.createEntityManager()) {
                GraphFetchRoot root = manager.createQuery(SELECT_ONE, GraphFetchRoot.class)
                        .setHint(hint, lazyGraph(fixture, "many")).getSingleResult();
                assertDeferred(fixture, root, "many");
                if (hint.endsWith("loadgraph")) {
                    assertUnlistedEager(fixture, root);
                } else {
                    assertDeferred(fixture, root, "unlisted");
                }
            }
        }
    }

    @Test
    public void replacingQueryGraphRestoresDefaultEagerFetching() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            TypedQuery<GraphFetchRoot> query = manager.createQuery(SELECT_ONE, GraphFetchRoot.class);
            query.setHint("jakarta.persistence.loadgraph", lazyGraph(fixture, "many", "members"));
            GraphFetchRoot first = query.getSingleResult();
            assertDeferred(fixture, first, "many");
            assertDeferred(fixture, first, "members");
            manager.clear();
            query.setHint("jakarta.persistence.loadgraph", emptyGraph(fixture));
            GraphFetchRoot second = query.getSingleResult();
            assertLoaded(fixture, second, "many");
            assertLoaded(fixture, second, "members");
            assertEquals("child-1", second.getMany().getLabel());
        }
    }

    @Test
    public void laterOrdinaryQueryDoesNotInheritSuppression() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            assertDeferred(fixture, manager.createQuery(SELECT_ONE, lazyGraph(fixture, "many")).getSingleResult(), "many");
            manager.clear();
            GraphFetchRoot ordinary = manager.createQuery(SELECT_ONE, GraphFetchRoot.class).getSingleResult();
            assertLoaded(fixture, ordinary, "many");
            assertEquals("child-1", ordinary.getMany().getLabel());
            manager.clear();
            assertLoaded(fixture, manager.find(GraphFetchRoot.class, 1), "many");
        }
    }

    @Test
    public void lazyOptionSuppressesMappingJoinFetch() throws Exception {
        assertSuppressedRead("joined", "child-7");
    }

    @Test
    public void explicitQueryFetchJoinIsPreservedDespiteGraphLazyOption() {
        assertExplicitJoin(false);
    }

    @Test
    public void explicitNestedQueryFetchJoinIsPreservedDespiteGraphRemoval() {
        assertExplicitJoin(true);
    }

    private static void assertExplicitJoin(boolean nested) {
        for (String hint : List.of("jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph")) {
            List<String> statements = new ArrayList<>();
            List<String> refetches = new ArrayList<>();
            List<String> warnings = new ArrayList<>();
            SessionEventAdapter observer = new SessionEventAdapter() {
                @Override
                public void preExecuteCall(SessionEvent event) {
                    if (event.getCall() instanceof DatabaseCall call) {
                        statements.add(call.getSQLString());
                        Class<?> type = call.getQuery().getReferenceClass();
                        if (call.getQuery() instanceof ReadObjectQuery
                                && (type == GraphFetchChild.class || nested && type == GraphFetchDetail.class)
                                && call.getParameters().stream().anyMatch(value -> "1".equals(String.valueOf(value)))) {
                            refetches.add(call.getSQLString());
                        }
                    }
                }
            };
            DefaultSessionLog capture = new DefaultSessionLog() {
                @Override
                public void log(SessionLogEntry entry) {
                    if (entry.getLevel() == SessionLog.WARNING) {
                        warnings.add(entry.getMessage());
                    }
                }
            };
            capture.setLevel(SessionLog.WARNING);
            ServerSession server = session(fixture);
            SessionLog previous = server.getSessionLog();
            server.setSessionLog(capture);
            server.getEventManager().addListener(observer);
            try (EntityManager manager = fixture.createEntityManager()) {
                EntityGraph<GraphFetchRoot> graph = lazyGraph(fixture, "many");
                if (nested) {
                    graph.addSubgraph("many", GraphFetchChild.class).removeAttributeNode("detail");
                }
                GraphFetchRoot root = manager.createQuery("select r from GraphFetchRoot r join fetch r.many"
                        + (nested ? " join fetch r.many.detail" : "") + " where r.id = 1", GraphFetchRoot.class)
                        .setHint(hint, graph).getSingleResult();
                int beforeAccess = statements.size();
                assertLoaded(fixture, root, "many");
                assertNotNull(root.getMany());
                if (nested) {
                    assertLoaded(fixture, root.getMany(), "detail");
                    assertEquals("detail-1", root.getMany().getDetail().getLabel());
                }
                assertEquals("Joined path access must not query again", beforeAccess, statements.size());
                assertTrue("Explicitly joined path must not be separately read: " + refetches, refetches.isEmpty());
                assertTrue("Joined fetch group must not warn: " + warnings,
                        warnings.stream().noneMatch(message -> message.contains("outside_fetch_group")));
                assertTrue(statements.stream().anyMatch(sql -> sql.contains("JPA_GFO_ROOT")
                        && sql.contains("JPA_GFO_CHILD") && (!nested || sql.contains("JPA_GFO_DETAIL"))));
                System.out.println("GRAPH JOIN SQL nested=" + nested + ", hint=" + hint + ", statements=" + statements
                        + ", refetches=" + refetches + ", warnings=" + warnings);
                // A nested fetch graph may omit the parent's basic label. Its
                // later access is independent of fetching the joined edge.
                assertEquals("child-1", root.getMany().getLabel());
            } finally {
                server.getEventManager().removeListener(observer);
                server.setSessionLog(previous);
            }
        }
    }

    @Test
    public void lazyOptionSuppressesMappingBatchFetchAcrossQueryRows() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            List<GraphFetchRoot> roots = manager.createQuery("select r from GraphFetchRoot r order by r.id",
                    lazyGraph(fixture, "batched")).getResultList();
            assertEquals(2, roots.size());
            for (GraphFetchRoot root : roots) {
                assertDeferred(fixture, root, "batched");
                assertUnlistedEager(fixture, root);
                assertEquals("child-" + ((root.getId() - 1) * 10 + 8), root.getBatched().getLabel());
            }
        }
    }

    @Test
    public void entityAgentCanFetchSuppressedToOneAndCollections() {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            GraphFetchRoot root = agent.find(lazyGraph(fixture, "many", "members", "tags"), 1);
            assertFalse(fixture.getPersistenceUnitUtil().isLoaded(root, "many"));
            assertFalse(fixture.getPersistenceUnitUtil().isLoaded(root, "members"));
            assertNoIndirectionTagsFallback(root);
            assertUnlistedEager(fixture, root);
            assertEquals("child-1", agent.fetch(root.getMany()).getLabel());
            assertEquals("child-4", agent.fetch(root.getMembers()).getFirst().getLabel());
            assertEquals(List.of("tag-1"), agent.fetch(root.getTags()));
            assertLoaded(fixture, root, "many");
            assertLoaded(fixture, root, "members");
            assertLoaded(fixture, root, "tags");
        }
    }

    @Test
    public void entityAgentQuerySuppressionDoesNotAffectTheNextOrdinaryRead() {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            GraphFetchRoot root = agent.createQuery(SELECT_ONE).withEntityGraph(lazyGraph(fixture, "many"))
                    .getSingleResult();
            assertFalse(fixture.getPersistenceUnitUtil().isLoaded(root, "many"));
            assertEquals("child-1", agent.fetch(root.getMany()).getLabel());
            GraphFetchRoot ordinary = agent.find(GraphFetchRoot.class, 1);
            assertLoaded(fixture, ordinary, "many");
            assertEquals("child-1", ordinary.getMany().getLabel());
        }
    }

    @Test
    public void entityAgentCanFetchSuppressedNestedAssociation() {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addSubgraph("many", GraphFetchChild.class).addAttributeNode("detail").addOption(FetchType.LAZY);
            GraphFetchRoot root = agent.find(graph, 1);
            assertLoaded(fixture, root, "many");
            GraphFetchChild child = root.getMany();
            assertFalse(fixture.getPersistenceUnitUtil().isLoaded(child, "detail"));
            assertLoaded(fixture, child, "otherDetail");
            GraphFetchDetail reference = child.getDetail();
            assertNotNull(reference);
            assertSame(reference, agent.fetch(reference));
            assertEquals("detail-1", reference.getLabel());
            assertLoaded(fixture, child, "detail");
        }
    }

    @Test
    public void entityAgentCanFetchDeferredDetailOfMappingJoinedParent() {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addSubgraph("joined", GraphFetchChild.class).removeAttributeNode("detail");
            GraphFetchRoot root = agent.find(graph, 1);
            assertLoaded(fixture, root, "joined");
            assertAgentNestedDetail(agent, root.getJoined(), 7);
        }
    }

    @Test
    public void entityAgentCanFetchDeferredDetailOfMappingBatchedParents() {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addSubgraph("batched", GraphFetchChild.class).removeAttributeNode("detail");
            List<GraphFetchRoot> roots = agent.createQuery("select r from GraphFetchRoot r order by r.id", graph)
                    .getResultList();
            assertEquals(2, roots.size());
            for (GraphFetchRoot root : roots) {
                assertLoaded(fixture, root, "batched");
                assertAgentNestedDetail(agent, root.getBatched(), (root.getId() - 1) * 10 + 8);
            }
        }
    }

    @Test
    public void isLoadedDoesNotExecuteQueriesForSuppressedAgentAssociations() {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            AtomicInteger executions = new AtomicInteger();
            SessionEventAdapter observer = new SessionEventAdapter() {
                @Override
                public void preExecuteQuery(SessionEvent event) {
                    executions.incrementAndGet();
                }
            };
            Session agentSession = agent.unwrap(Session.class);
            agentSession.getEventManager().addListener(observer);
            try {
                GraphFetchRoot root = agent.find(lazyGraph(fixture, "many", "members", "tags"), 1);
                assertTrue("Observer must detect the initial read", executions.get() > 0);
                int before = executions.get();
                for (int repeat = 0; repeat < 2; repeat++) {
                    assertFalse(fixture.getPersistenceUnitUtil().isLoaded(root, "many"));
                    assertFalse(fixture.getPersistenceUnitUtil().isLoaded(root, "members"));
                    assertNoIndirectionTagsFallback(root);
                    assertLoaded(fixture, root, "unlisted");
                }
                assertEquals("isLoaded must inspect existing state without executing queries", before, executions.get());
                assertEquals("child-1", agent.fetch(root.getMany()).getLabel());
                assertTrue("Explicit fetching must execute the deferred read", executions.get() > before);
            } finally {
                agentSession.getEventManager().removeListener(observer);
            }
        }
    }

    @Test
    public void disablingEagerIndirectionRetainsUsableEagerAssociations() {
        try (EntityManagerFactory factory = configuration("no-eager-indirection", false)
                .property(PersistenceUnitProperties.WEAVING_EAGER, "false").createEntityManagerFactory()) {
            ForeignReferenceMapping mapping = (ForeignReferenceMapping) session(factory)
                    .getDescriptor(GraphFetchRoot.class).getMappingForAttributeName("many");
            assertFalse("Control must actually disable eager indirection", mapping.usesIndirection());
            EntityGraph<GraphFetchRoot> graph = lazyGraph(factory, "many");
            try (EntityManager manager = factory.createEntityManager()) {
                GraphFetchRoot root = manager.find(graph, 1);
                // LAZY is a hint: a mapping without indirection may remain eager.
                assertEquals("child-1", root.getMany().getLabel());
                assertUnlistedEager(factory, root);
            }
            try (EntityAgent agent = factory.createEntityAgent()) {
                GraphFetchRoot root = agent.find(graph, 1);
                assertEquals("child-1", agent.fetch(root.getMany()).getLabel());
                assertUnlistedEager(factory, root);
            }
        }
    }

    @Test
    public void nativeFetchGroupSurvivesLoadGraphReplacementAndClearing() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            FetchGroup nativeGroup = new FetchGroup();
            nativeGroup.addAttribute("id");
            // Native groups have historically been normalized with required version state.
            nativeGroup.addAttribute("version");
            nativeGroup.addAttribute("lazy");
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addAttributeNode("lazy");
            TypedQuery<GraphFetchRoot> query = manager.createQuery(SELECT_ONE, GraphFetchRoot.class)
                    .setHint(QueryHints.FETCH_GROUP, nativeGroup)
                    .setHint("jakarta.persistence.loadgraph", graph);
            GraphFetchRoot first = query.getSingleResult();
            assertDeferred(fixture, first, "note");
            assertLoaded(fixture, first, "lazy");
            assertEquals("child-6", first.getLazy().getLabel());

            manager.clear();
            query.setHint("jakarta.persistence.loadgraph", emptyGraph(fixture));
            GraphFetchRoot replaced = query.getSingleResult();
            assertDeferred(fixture, replaced, "note");
            assertDeferred(fixture, replaced, "lazy");

            manager.clear();
            query.setHint("jakarta.persistence.loadgraph", null);
            GraphFetchRoot cleared = query.getSingleResult();
            assertDeferred(fixture, cleared, "note");
            assertDeferred(fixture, cleared, "lazy");
            assertEquals("root-1", cleared.getNote());
            assertEquals(Set.of("id", "version", "lazy"), nativeGroup.getAttributeNames());
        }
    }

    @Test
    public void nativeLoadGroupSurvivesFetchGraphReplacementAndClearing() {
        try (EntityManager manager = fixture.createEntityManager()) {
            LoadGroup nativeGroup = new LoadGroup();
            nativeGroup.addAttribute("lazy");
            EntityGraph<GraphFetchRoot> firstGraph = emptyGraph(fixture);
            firstGraph.addAttributeNode("many");
            TypedQuery<GraphFetchRoot> query = manager.createQuery(SELECT_ONE, GraphFetchRoot.class)
                    .setHint(QueryHints.LOAD_GROUP, nativeGroup)
                    .setHint("jakarta.persistence.fetchgraph", firstGraph);
            GraphFetchRoot first = query.getSingleResult();
            assertLoaded(fixture, first, "many");
            assertLoaded(fixture, first, "lazy");
            assertEquals("child-6", first.getLazy().getLabel());

            manager.clear();
            EntityGraph<GraphFetchRoot> replacement = emptyGraph(fixture);
            replacement.addAttributeNode("one");
            query.setHint("jakarta.persistence.fetchgraph", replacement);
            GraphFetchRoot replaced = query.getSingleResult();
            assertLoaded(fixture, replaced, "one");
            assertLoaded(fixture, replaced, "lazy");

            manager.clear();
            query.setHint("jakarta.persistence.fetchgraph", null);
            GraphFetchRoot cleared = query.getSingleResult();
            assertLoaded(fixture, cleared, "many");
            assertLoaded(fixture, cleared, "lazy");
            assertEquals("child-6", cleared.getLazy().getLabel());
            assertEquals(Set.of("lazy"), nativeGroup.getAttributeNames());
        }
    }

    @Test
    public void negativeLoadGraphPreservesNativeFetchSelectionAndCopies() throws Exception {
        List<String> failures = new ArrayList<>();
        for (boolean named : List.of(false, true)) {
            for (boolean removed : List.of(false, true)) {
                try (EntityManagerFactory factory = configuration("native-negative", false).createEntityManagerFactory();
                        EntityManager manager = factory.createEntityManager()) {
                    FetchGroup nativeGroup = new FetchGroup("native-selection");
                    nativeGroup.addAttribute("id");
                    nativeGroup.addAttribute("version");
                    nativeGroup.addAttribute("lazy.id");
                    nativeGroup.addAttribute("lazy.label");
                    if (named) {
                        session(factory).getDescriptor(GraphFetchRoot.class).getFetchGroupManager().addFetchGroup(nativeGroup);
                    }
                    EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
                    graph.addAttributeNode("lazy");
                    if (removed) {
                        graph.removeAttributeNode("many");
                    } else {
                        graph.addAttributeNode("many").addOption(FetchType.LAZY);
                    }
                    TypedQuery<GraphFetchRoot> query = manager.createQuery(SELECT_ONE, GraphFetchRoot.class)
                            .setHint(named ? QueryHints.FETCH_GROUP_NAME : QueryHints.FETCH_GROUP,
                                    named ? nativeGroup.getName() : nativeGroup);
                    for (String stage : List.of("negative", "empty", "cleared")) {
                        query.setHint("jakarta.persistence.loadgraph", stage.equals("negative") ? graph
                                : stage.equals("empty") ? emptyGraph(factory) : null);
                        for (boolean copied : List.of(false, true)) {
                            manager.clear();
                            ObjectLevelReadQuery execution = (ObjectLevelReadQuery) query.unwrap(JpaQuery.class).getDatabaseQuery();
                            GraphFetchRoot root;
                            if (copied) {
                                ObjectLevelReadQuery copy = (ObjectLevelReadQuery) execution.clone();
                                copy.copyFromQuery(execution);
                                root = (GraphFetchRoot) manager.unwrap(JpaEntityManager.class).createQuery(copy).getSingleResult();
                            } else {
                                root = query.getSingleResult();
                            }
                            String context = "named=" + named + ", removed=" + removed + ", stage=" + stage
                                    + ", copied=" + copied;
                            boolean noteLoaded = factory.getPersistenceUnitUtil().isLoaded(root, "note");
                            System.out.println("GRAPH NATIVE DIAGNOSTIC " + context + ", note=" + noteLoaded
                                    + ", query=" + execution.getFetchGroup() + ", name=" + execution.getFetchGroupName()
                                    + ", result=" + ((FetchGroupTracker) root)._persistence_getFetchGroup());
                            if (noteLoaded) {
                                failures.add(context + ": native note mask lost");
                            } else {
                                assertDeferred(factory, root, "note");
                            }
                            if (stage.equals("negative")) {
                                assertLoaded(factory, root, "lazy");
                                assertEquals("child-6", root.getLazy().getLabel());
                            }
                            assertEquals(Set.of("id", "version", "lazy"), nativeGroup.getAttributeNames());
                            assertEquals(Set.of("id", "label"), nativeGroup.getItem("lazy").getGroup().getAttributeNames());
                        }
                    }
                    // Native explicit selection takes precedence over graph suppression.
                    manager.clear();
                    FetchGroup conflicting = nativeGroup.clone();
                    conflicting.addAttribute("many");
                    GraphFetchRoot root = manager.createQuery(SELECT_ONE, GraphFetchRoot.class)
                            .setHint(QueryHints.FETCH_GROUP, conflicting)
                            .setHint("jakarta.persistence.loadgraph", graph).getSingleResult();
                    if (!factory.getPersistenceUnitUtil().isLoaded(root, "many")) {
                        failures.add("named=" + named + ", removed=" + removed + ": explicit native many selection lost");
                    }
                }
            }
        }
        assertTrue("Native selection must survive negative graph composition: " + failures, failures.isEmpty());
    }

    @Test
    public void fullCacheQueryAcquisitionHonorsGraphExclusions() throws Exception {
        List<String> failures = new ArrayList<>();
        for (boolean eagerIndirection : List.of(false, true)) {
            try (EntityManagerFactory factory = configuration("query-cache", true)
                    .property(PersistenceUnitProperties.WEAVING_EAGER, Boolean.toString(eagerIndirection)).createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                GraphFetchRoot warm = warmGraphCache(factory, manager);
                GraphFetchRoot original = (GraphFetchRoot) session(factory).getIdentityMapAccessor()
                        .getFromIdentityMap(1, GraphFetchRoot.class);
                AtomicInteger calls = new AtomicInteger();
                session(factory).getEventManager().addListener(new SessionEventAdapter() {
                    @Override
                    public void preExecuteCall(SessionEvent event) { calls.incrementAndGet(); }
                });
                for (String api : List.of("graph-first", "withEntityGraph", "jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph",
                        "row:graph-first", "row:withEntityGraph", "row:jakarta.persistence.loadgraph", "row:jakarta.persistence.fetchgraph")) {
                    for (boolean removed : List.of(false, true)) {
                        manager.clear();
                        EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
                        for (String attribute : List.of("many", "members")) {
                            if (removed) {
                                graph.removeAttributeNode(attribute);
                            } else {
                                graph.addAttributeNode(attribute).addOption(FetchType.LAZY);
                            }
                        }
                        int beforeRead = calls.get();
                        GraphFetchRoot result = graphQuery(manager, graph, api);
                        if (api.startsWith("row:")) {
                            assertTrue("A non-primary-key query must execute SQL", calls.get() > beforeRead);
                        }
                        assertFalse(result == warm || result == original);
                        assertTrue(manager.contains(result));
                        String context = "indirection=" + eagerIndirection + ", api=" + api + ", removed=" + removed;
                        int beforeInspection = calls.get();
                        System.out.println("GRAPH QUERY CACHE DIAGNOSTIC " + context + ", result="
                                + diagnosticState(factory, result) + ", original=" + diagnosticState(factory, original));
                        for (String attribute : List.of("many", "members")) {
                            if (factory.getPersistenceUnitUtil().isLoaded(result, attribute)) {
                                failures.add(context + ": " + attribute + " loaded");
                            } else {
                                try {
                                    assertDeferred(factory, result, attribute);
                                } catch (AssertionError failure) {
                                    failures.add(context + ": " + attribute + ": " + failure.getMessage());
                                }
                            }
                            assertLoaded(factory, original, attribute);
                        }
                        assertEquals("State inspection must not query", beforeInspection, calls.get());
                        assertEquals(1, result.getId());
                        assertTrue(result.getVersion() > 0);
                        assertEquals("child-1", result.getMany().getLabel());
                        assertEquals("child-4", result.getMembers().getFirst().getLabel());
                        assertSame(result, manager.find(GraphFetchRoot.class, 1));
                        if (!api.endsWith("fetchgraph")) {
                            assertUnlistedEager(factory, result);
                        }
                    }
                }
            }
        }
        assertTrue("Query acquisition must honor graph exclusions on fresh cache clones: " + failures, failures.isEmpty());
    }

    @Test
    public void fullCacheNestedGraphSelectionReachesRelatedClones() throws Exception {
        List<String> failures = new ArrayList<>();
        for (boolean eagerIndirection : List.of(false, true)) {
            try (EntityManagerFactory factory = configuration("nested-cache", true)
                    .property(PersistenceUnitProperties.WEAVING_EAGER, Boolean.toString(eagerIndirection)).createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                warmGraphCache(factory, manager);
                for (String api : List.of("find", "graph-first", "row:graph-first")) {
                    for (String option : List.of("lazy", "removed", "eager")) {
                        manager.clear();
                        EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
                        for (String attribute : List.of("many", "members", "lazy")) {
                            var subgraph = graph.addSubgraph(attribute, GraphFetchChild.class);
                            if (option.equals("removed")) {
                                subgraph.removeAttributeNode("detail");
                            } else {
                                subgraph.addAttributeNode("detail").addOption(option.equals("lazy") ? FetchType.LAZY : FetchType.EAGER);
                            }
                        }
                        GraphFetchRoot result = api.equals("find") ? manager.find(graph, 1) : graphQuery(manager, graph, api);
                        assertLoaded(factory, result, "many");
                        assertLoaded(factory, result, "members");
                        assertLoaded(factory, result, "lazy");
                        for (GraphFetchChild child : List.of(result.getMany(), result.getMembers().getFirst(), result.getLazy())) {
                            GraphFetchChild original = (GraphFetchChild) session(factory).getIdentityMapAccessor()
                                    .getFromIdentityMap(child.getId(), GraphFetchChild.class);
                            assertFalse(child == original);
                            boolean loaded = factory.getPersistenceUnitUtil().isLoaded(child, "detail");
                            String context = "indirection=" + eagerIndirection + ", api=" + api + ", option=" + option
                                    + ", child=" + child.getId() + ", detail=" + loaded;
                            System.out.println("GRAPH NESTED CACHE DIAGNOSTIC " + context + ", group="
                                    + ((FetchGroupTracker) child)._persistence_getFetchGroup());
                            if (loaded != option.equals("eager")) {
                                failures.add(context);
                            } else if (!loaded) {
                                assertDeferred(factory, child, "detail");
                            }
                            assertLoaded(factory, child, "otherDetail");
                            assertLoaded(factory, original, "detail");
                            assertLoaded(factory, original, "otherDetail");
                            assertSame(child, manager.find(GraphFetchChild.class, child.getId()));
                            assertEquals("detail-" + (child.getId() * 2 - 1), child.getDetail().getLabel());
                            assertEquals("detail-" + (child.getId() * 2), child.getOtherDetail().getLabel());
                        }
                        assertEquals(1, result.getId());
                        assertTrue(result.getVersion() > 0);
                        assertUnlistedEager(factory, result);
                    }
                }
            }
        }
        assertTrue("Nested graph selections must reach fresh cached child clones: " + failures, failures.isEmpty());
    }

    @Test
    public void nestedCacheSelectionPreservesManagedChildrenAndCallbackReads() throws Exception {
        for (boolean eagerIndirection : List.of(false, true)) {
            try (EntityManagerFactory factory = configuration("nested-cache-scope", true)
                    .property(PersistenceUnitProperties.WEAVING_EAGER, Boolean.toString(eagerIndirection)).createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                warmGraphCache(factory, manager);
                manager.find(GraphFetchChild.class, 11);
                manager.clear();
                EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
                graph.addSubgraph("many", GraphFetchChild.class).removeAttributeNode("detail");
                List<GraphFetchChild> callbackReads = new ArrayList<>();
                DescriptorEventAdapter listener = new DescriptorEventAdapter() {
                    @Override
                    public void postClone(DescriptorEvent event) {
                        GraphFetchChild child = (GraphFetchChild) event.getObject();
                        if (child.getId() == 1) {
                            GraphFetchChild unrelated = manager.find(GraphFetchChild.class, 11);
                            assertLoaded(factory, unrelated, "detail");
                            callbackReads.add(unrelated);
                        }
                    }
                };
                session(factory).getDescriptor(GraphFetchChild.class).getEventManager().addListener(listener);
                try {
                    GraphFetchRoot result = graphQuery(manager, graph, "row:graph-first");
                    assertDeferred(factory, result.getMany(), "detail");
                    assertEquals(1, callbackReads.size());
                    assertEquals("detail-21", callbackReads.getFirst().getDetail().getLabel());
                } finally {
                    session(factory).getDescriptor(GraphFetchChild.class).getEventManager().removeListener(listener);
                }
                manager.clear();
                GraphFetchChild loaded = manager.find(GraphFetchChild.class, 1);
                GraphFetchDetail loadedDetail = loaded.getDetail();
                GraphFetchRoot result = graphQuery(manager, graph, "row:graph-first");
                assertSame(loaded, result.getMany());
                assertSame(loadedDetail, result.getMany().getDetail());
                assertLoaded(factory, loaded, "detail");
            }
        }
    }

    private static GraphFetchRoot warmGraphCache(EntityManagerFactory factory, EntityManager manager) {
        GraphFetchRoot root = manager.find(GraphFetchRoot.class, 1);
        for (GraphFetchChild child : List.of(root.getMany(), root.getMembers().getFirst(), root.getLazy())) {
            assertNotNull(child.getDetail().getLabel());
            assertNotNull(child.getOtherDetail().getLabel());
        }
        assertLoaded(factory, root, "many");
        assertLoaded(factory, root, "members");
        manager.clear();
        assertTrue(factory.getCache().contains(GraphFetchRoot.class, 1));
        return root;
    }

    @Test
    public void valueSubgraphDoesNotSuppressEntityMapKeyDefaults() throws Exception {
        List<String> failures = new ArrayList<>();
        for (boolean eagerIndirection : List.of(false, true)) {
            try (EntityManagerFactory factory = configuration("map-key-scope", true)
                    .property(PersistenceUnitProperties.WEAVING_EAGER, Boolean.toString(eagerIndirection)).createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                GraphFetchRoot warm = manager.find(GraphFetchRoot.class, 1);
                Map.Entry<GraphFetchChild, GraphFetchChild> warmEntry = warm.getKeyed().entrySet().iterator().next();
                assertEquals(2, warmEntry.getKey().getId());
                assertEquals(1, warmEntry.getValue().getId());
                assertLoaded(factory, warmEntry.getKey(), "detail");
                assertLoaded(factory, warmEntry.getValue(), "detail");
                GraphFetchChild originalKey = (GraphFetchChild) session(factory).getIdentityMapAccessor()
                        .getFromIdentityMap(2, GraphFetchChild.class);
                GraphFetchChild originalValue = (GraphFetchChild) session(factory).getIdentityMapAccessor()
                        .getFromIdentityMap(1, GraphFetchChild.class);
                assertNotNull(originalKey);
                assertNotNull(originalValue);
                ForeignReferenceMapping detailMapping = (ForeignReferenceMapping) session(factory)
                        .getDescriptor(GraphFetchChild.class).getMappingForAttributeName("detail");
                assertEquals(eagerIndirection, detailMapping.usesIndirection());
                AtomicInteger calls = new AtomicInteger();
                session(factory).getEventManager().addListener(new SessionEventAdapter() {
                    @Override
                    public void preExecuteCall(SessionEvent event) { calls.incrementAndGet(); }
                });
                for (String api : List.of("find", "row", "copy")) {
                    for (String option : List.of("lazy", "removed", "eager")) {
                        manager.clear();
                        assertFalse(manager.contains(warmEntry.getKey()));
                        assertFalse(manager.contains(warmEntry.getValue()));
                        EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
                        var values = graph.addSubgraph("keyed", GraphFetchChild.class);
                        values.addAttributeNode("label");
                        values.addAttributeNode("otherDetail");
                        if (option.equals("removed")) {
                            values.removeAttributeNode("detail");
                        } else {
                            values.addAttributeNode("detail").addOption(option.equals("lazy") ? FetchType.LAZY : FetchType.EAGER);
                        }
                        GraphFetchRoot result;
                        if (api.equals("find")) {
                            result = manager.find(GraphFetchRoot.class, 1, Map.of("jakarta.persistence.fetchgraph", graph));
                        } else {
                            TypedQuery<GraphFetchRoot> query = manager.createQuery(
                                    "select r from GraphFetchRoot r where r.note = 'root-1'", GraphFetchRoot.class)
                                    .setHint("jakarta.persistence.fetchgraph", graph);
                            ObjectLevelReadQuery databaseQuery = (ObjectLevelReadQuery) query.unwrap(JpaQuery.class).getDatabaseQuery();
                            assertTrue(databaseQuery.isReadAllQuery());
                            if (api.equals("copy")) {
                                ObjectLevelReadQuery copy = (ObjectLevelReadQuery) databaseQuery.clone();
                                copy.copyFromQuery(databaseQuery);
                                result = (GraphFetchRoot) manager.unwrap(JpaEntityManager.class).createQuery(copy).getSingleResult();
                            } else {
                                result = query.getSingleResult();
                            }
                        }
                        // No eager root alias may register either child before map cloning.
                        assertDeferred(factory, result, "many");
                        assertDeferred(factory, result, "one");
                        assertLoaded(factory, result, "keyed");
                        assertEquals(1, result.getKeyed().size());
                        Map.Entry<GraphFetchChild, GraphFetchChild> entry = result.getKeyed().entrySet().iterator().next();
                        GraphFetchChild key = entry.getKey();
                        GraphFetchChild value = entry.getValue();
                        assertFalse(key == value || key == warmEntry.getKey() || value == warmEntry.getValue());
                        assertFalse(key == originalKey || value == originalValue);
                        assertEquals(2, key.getId());
                        assertEquals(1, value.getId());
                        int beforeInspection = calls.get();
                        boolean keyLoaded = factory.getPersistenceUnitUtil().isLoaded(key, "detail");
                        boolean valueLoaded = factory.getPersistenceUnitUtil().isLoaded(value, "detail");
                        String context = "indirection=" + eagerIndirection + ", api=" + api + ", option=" + option;
                        System.out.println("GRAPH MAP KEY DIAGNOSTIC " + context + ", key.detail=" + keyLoaded
                                + ", value.detail=" + valueLoaded + ", key.group=" + ((FetchGroupTracker) key)._persistence_getFetchGroup()
                                + ", value.group=" + ((FetchGroupTracker) value)._persistence_getFetchGroup());
                        if (!keyLoaded) {
                            assertDeferred(factory, key, "detail");
                            failures.add(context + ": value selection deferred key.detail");
                        } else {
                            Field storage = GraphFetchChild.class.getDeclaredField(eagerIndirection ? "_persistence_detail_vh" : "detail");
                            storage.setAccessible(true);
                            Object actual = storage.get(key);
                            assertNotNull("The key's eager relationship needs actual loaded storage", actual);
                            if (eagerIndirection) {
                                assertTrue(((ValueHolderInterface<?>) actual).isInstantiated());
                            }
                        }
                        if (option.equals("eager")) {
                            assertLoaded(factory, value, "detail");
                        } else {
                            assertDeferred(factory, value, "detail");
                        }
                        assertLoaded(factory, key, "otherDetail");
                        assertLoaded(factory, value, "otherDetail");
                        assertEquals("Map state inspection must not query", beforeInspection, calls.get());
                        assertEquals("detail-3", key.getDetail().getLabel());
                        assertEquals("detail-1", value.getDetail().getLabel());
                        assertLoaded(factory, originalKey, "detail");
                        assertLoaded(factory, originalValue, "detail");
                    }
                }
                manager.clear();
                FetchGroup nativeGroup = new FetchGroup();
                nativeGroup.addAttribute("id");
                nativeGroup.addAttribute("version");
                nativeGroup.addAttribute("keyed");
                LoadGroup nativeLoad = new LoadGroup();
                nativeLoad.addAttribute("keyed");
                GraphFetchRoot nativeResult = manager.createQuery(SELECT_ONE, GraphFetchRoot.class)
                        .setHint(QueryHints.FETCH_GROUP, nativeGroup).setHint(QueryHints.LOAD_GROUP, nativeLoad).getSingleResult();
                Map.Entry<GraphFetchChild, GraphFetchChild> nativeEntry = nativeResult.getKeyed().entrySet().iterator().next();
                assertLoaded(factory, nativeEntry.getKey(), "detail");
                assertLoaded(factory, nativeEntry.getValue(), "detail");
                GraphFetchChild managedKey = nativeEntry.getKey();
                GraphFetchDetail managedDetail = managedKey.getDetail();
                EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
                graph.addSubgraph("keyed", GraphFetchChild.class).removeAttributeNode("detail");
                manager.detach(nativeResult);
                GraphFetchRoot retained = manager.find(GraphFetchRoot.class, 1, Map.of("jakarta.persistence.fetchgraph", graph));
                GraphFetchChild retainedKey = retained.getKeyed().keySet().iterator().next();
                assertSame(managedKey, retainedKey);
                assertSame(managedDetail, retainedKey.getDetail());
            }
        }
        assertTrue("Value-only graph policy must not leak into map keys: " + failures, failures.isEmpty());
    }

    private static GraphFetchRoot graphQuery(EntityManager manager, EntityGraph<GraphFetchRoot> graph, String api) {
        boolean row = api.startsWith("row:");
        String entry = row ? api.substring(4) : api;
        String jpql = row ? "select r from GraphFetchRoot r where r.note = 'root-1'" : SELECT_ONE;
        TypedQuery<GraphFetchRoot> query;
        if (entry.equals("graph-first")) {
            query = manager.createQuery(jpql, graph);
        } else if (entry.equals("withEntityGraph")) {
            query = manager.createQuery(jpql).withEntityGraph(graph);
        } else {
            query = manager.createQuery(jpql, GraphFetchRoot.class).setHint(entry, graph);
        }
        ObjectLevelReadQuery databaseQuery = (ObjectLevelReadQuery) query.unwrap(JpaQuery.class).getDatabaseQuery();
        assertEquals("The diagnostic must distinguish row acquisition from primary-key optimization", row, databaseQuery.isReadAllQuery());
        System.out.println("GRAPH QUERY TYPE " + api + "=" + databaseQuery.getClass().getSimpleName());
        return query.getSingleResult();
    }

    @Test
    public void deferredSharedCacheStateSupportsLaterGraphAndOrdinaryReads() throws Exception {
        try (EntityManagerFactory factory = configuration("partial-cache", true).createEntityManagerFactory()) {
            EntityGraph<GraphFetchRoot> graph = lazyGraph(factory, "many", "members");
            try (EntityManager first = factory.createEntityManager()) {
                GraphFetchRoot root = first.find(graph, 1);
                assertDeferred(factory, root, "many");
                assertDeferred(factory, root, "members");
            }
            assertTrue("First read must populate the shared cache", factory.getCache().contains(GraphFetchRoot.class, 1));
            try (EntityManager second = factory.createEntityManager()) {
                GraphFetchRoot root = second.find(graph, 1);
                assertDeferred(factory, root, "many");
                assertDeferred(factory, root, "members");
                assertEquals("child-1", root.getMany().getLabel());
                assertEquals("child-4", root.getMembers().getFirst().getLabel());
            }
            try (EntityManager ordinary = factory.createEntityManager()) {
                GraphFetchRoot root = ordinary.find(GraphFetchRoot.class, 1);
                assertLoaded(factory, root, "many");
                assertLoaded(factory, root, "members");
                assertEquals("child-1", root.getMany().getLabel());
            }
        }
    }

    @Test
    public void fullyLoadedSharedCacheStateRemainsUsableWithLazyOptions() throws Exception {
        try (EntityManagerFactory factory = configuration("full-cache", true).createEntityManagerFactory()) {
            try (EntityManager warm = factory.createEntityManager()) {
                assertLoaded(factory, warm.find(GraphFetchRoot.class, 1), "many");
            }
            assertTrue(factory.getCache().contains(GraphFetchRoot.class, 1));
            try (EntityManager manager = factory.createEntityManager()) {
                GraphFetchRoot root = manager.find(lazyGraph(factory, "many", "members"), 1);
                assertDeferred(factory, root, "many");
                assertDeferred(factory, root, "members");
                assertEquals("child-1", root.getMany().getLabel());
                assertEquals("child-4", root.getMembers().getFirst().getLabel());
                assertUnlistedEager(factory, root);
            }
        }
    }

    @Test
    public void persistedSharedCacheHonorsGraphOnFreshManagedCopy() throws Exception {
        List<String> failures = new ArrayList<>();
        int id = 100;
        for (boolean eagerIndirection : List.of(false, true)) {
            for (boolean shared : List.of(false, true)) {
                try (EntityManagerFactory factory = configuration("persisted-cache", shared)
                        .property(PersistenceUnitProperties.WEAVING_EAGER, Boolean.toString(eagerIndirection)).createEntityManagerFactory();
                        EntityManager manager = factory.createEntityManager()) {
                    for (String option : List.of("lazy", "removed", "eager", "default")) {
                        List<GraphFetchChild> children = new ArrayList<>();
                        for (int child = 1; child <= 8; child++) {
                            children.add(manager.find(GraphFetchChild.class, child));
                        }
                        GraphFetchRoot persisted = new GraphFetchRoot(++id, children);
                        manager.getTransaction().begin();
                        manager.persist(persisted);
                        manager.getTransaction().commit();
                        manager.clear();
                        Object original = session(factory).getIdentityMapAccessor()
                                .getFromIdentityMap(id, GraphFetchRoot.class);
                        assertEquals(shared, original != null);
                        EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
                        if (option.equals("lazy")) {
                            graph.addAttributeNode("many").addOption(FetchType.LAZY);
                        } else if (option.equals("removed")) {
                            graph.removeAttributeNode("many");
                        } else if (option.equals("eager")) {
                            graph.addAttributeNode("many").addOption(FetchType.EAGER);
                        }
                        AtomicInteger calls = new AtomicInteger();
                        AtomicInteger queries = new AtomicInteger();
                        List<String> groups = new ArrayList<>();
                        SessionEventAdapter observer = new SessionEventAdapter() {
                            @Override
                            public void preExecuteCall(SessionEvent event) { calls.incrementAndGet(); }
                            @Override
                            public void preExecuteQuery(SessionEvent event) { queries.incrementAndGet(); }
                            @Override
                            public void postExecuteQuery(SessionEvent event) {
                                if (event.getQuery() instanceof ObjectLevelReadQuery query
                                        && query.getReferenceClass() == GraphFetchRoot.class) {
                                    groups.add("fetch=" + query.getFetchGroup() + ", load=" + query.getLoadGroup());
                                }
                            }
                        };
                        session(factory).getEventManager().addListener(observer);
                        try {
                            GraphFetchRoot result = manager.find(graph, id);
                            boolean loaded = factory.getPersistenceUnitUtil().isLoaded(result, "many");
                            int callsBeforeInspection = calls.get();
                            int queriesBeforeInspection = queries.get();
                            String observation = "indirection=" + eagerIndirection + ", shared=" + shared
                                    + ", option=" + option + ", PU.many.before=" + loaded
                                    + ", samePersisted=" + (result == persisted) + ", calls=" + calls.get()
                                    + ", queries=" + queries.get() + ", query=" + groups
                                    + ", result=" + diagnosticState(factory, result)
                                    + ", original=" + (original == null ? "absent" : diagnosticState(factory, original));
                            System.out.println("GRAPH CACHE DIAGNOSTIC " + observation);
                            if (callsBeforeInspection != calls.get() || queriesBeforeInspection != queries.get()) {
                                failures.add("Inspection loaded state: " + observation + ", calls.after=" + calls.get()
                                        + ", queries.after=" + queries.get());
                            }
                            assertFalse(result == persisted);
                            boolean deferred = option.equals("lazy") || option.equals("removed");
                            if (loaded == deferred) {
                                failures.add(observation);
                            }
                            if (!loaded) {
                                assertDeferred(factory, result, "many");
                            }
                            assertEquals(id, result.getId());
                            assertLoaded(factory, result, "version");
                            assertTrue(result.getVersion() > 0);
                            assertEquals("child-1", result.getMany().getLabel());
                            assertSame(result, manager.find(graph, id));
                            if (original != null) {
                                assertLoaded(factory, original, "many");
                            }
                        } finally {
                            session(factory).getEventManager().removeListener(observer);
                            manager.clear();
                            manager.getTransaction().begin();
                            manager.remove(manager.find(GraphFetchRoot.class, id));
                            manager.getTransaction().commit();
                        }
                    }
                }
            }
        }
        assertTrue("Fresh clones must honor graph exclusions: " + failures, failures.isEmpty());
    }

    private static void assertNoIndirectionTagsFallback(GraphFetchRoot root) {
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) session(fixture)
                .getDescriptor(GraphFetchRoot.class).getMappingForAttributeName("tags");
        assertFalse("EAGER element-collection control has no deferred holder", mapping.usesIndirection());
        // FetchType LAZY is a hint (FetchType API); EntityGraph permits extra
        // state. Detached no-indirection data must stay usable and truthfully loaded.
        assertLoaded(fixture, root, "tags");
        assertEquals(List.of("tag-1"), root.getTags());
    }

    @Test
    public void entityAgentFetchesSupportedDeferredElementCollection() {
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) session(fixture)
                .getDescriptor(GraphFetchRoot.class).getMappingForAttributeName("lazyTags");
        assertTrue("Positive element-collection case requires real indirection", mapping.usesIndirection());
        try (EntityAgent agent = fixture.createEntityAgent()) {
            GraphFetchRoot root = agent.find(lazyGraph(fixture, "lazyTags", "tags"), 1);
            assertNoIndirectionTagsFallback(root);
            List<String> tags = root.getTags();
            assertSame(tags, agent.fetch(tags));
            List<String> lazyTags = root.getLazyTags();
            assertTrue(lazyTags instanceof IndirectContainer);
            assertFalse(((IndirectContainer) lazyTags).getValueHolder().isInstantiated());
            AtomicInteger queries = new AtomicInteger();
            SessionEventAdapter observer = new SessionEventAdapter() {
                @Override
                public void preExecuteQuery(SessionEvent event) { queries.incrementAndGet(); }
            };
            Session agentSession = agent.unwrap(Session.class);
            agentSession.getEventManager().addListener(observer);
            try {
                for (int repeat = 0; repeat < 3; repeat++) {
                    assertFalse(fixture.getPersistenceUnitUtil().isLoaded(root, "lazyTags"));
                    assertFalse(Persistence.getPersistenceUtil().isLoaded(root, "lazyTags"));
                    assertNoIndirectionTagsFallback(root);
                    assertSame(tags, agent.fetch(tags));
                }
                assertEquals(0, queries.get());
                assertEquals(List.of("lazy-tag-1"), agent.fetch(lazyTags));
                assertTrue(queries.get() > 0);
                assertLoaded(fixture, root, "lazyTags");
                assertTrue(((IndirectContainer) lazyTags).getValueHolder().isInstantiated());
            } finally {
                agentSession.getEventManager().removeListener(observer);
            }
        }
    }

    @Test
    public void entityAgentEagerElementCollectionGraphLoadsExactContents() {
        try (EntityAgent agent = fixture.createEntityAgent()) {
            EntityGraph<GraphFetchRoot> graph = emptyGraph(fixture);
            graph.addAttributeNode("lazyTags").addOption(FetchType.EAGER);
            GraphFetchRoot root = agent.find(graph, 1);
            assertLoaded(fixture, root, "lazyTags");
            assertEquals(List.of("lazy-tag-1"), root.getLazyTags());
        }
    }

    private static String diagnosticState(EntityManagerFactory factory, Object root) throws Exception {
        Field holderField = GraphFetchRoot.class.getDeclaredField("_persistence_many_vh");
        holderField.setAccessible(true);
        ValueHolderInterface<?> holder = (ValueHolderInterface<?>) holderField.get(root);
        return "group=" + ((FetchGroupTracker) root)._persistence_getFetchGroup()
                + ", holder=" + (holder == null ? "null" : holder.getClass().getSimpleName()
                        + ":" + holder.isInstantiated())
                + ", PU.entity=" + factory.getPersistenceUnitUtil().isLoaded(root)
                + ", PU.many=" + factory.getPersistenceUnitUtil().isLoaded(root, "many")
                + ", global.entity=" + Persistence.getPersistenceUtil().isLoaded(root)
                + ", global.many=" + Persistence.getPersistenceUtil().isLoaded(root, "many");
    }

    @Test
    public void alreadyManagedLoadedIdentityAndDirtyStateArePreserved() {
        try (EntityManager manager = fixture.createEntityManager()) {
            GraphFetchRoot original = manager.find(GraphFetchRoot.class, 1);
            GraphFetchChild originalMany = original.getMany();
            List<GraphFetchChild> originalMembers = original.getMembers();
            original.setNote("local-change");
            original.setMany(original.getOne());
            GraphFetchChild assignedMany = original.getMany();
            GraphFetchRoot result = manager.find(lazyGraph(fixture, "many", "members", "note"), 1);
            assertSame(original, result);
            assertSame(assignedMany, result.getMany());
            assertFalse(originalMany == assignedMany);
            assertSame(originalMembers, result.getMembers());
            assertEquals("local-change", result.getNote());
            assertLoaded(fixture, result, "many");
            assertLoaded(fixture, result, "members");
        }
    }

    @Test
    public void cacheCloneCallbackLoadedStateRemainsLoaded() throws Exception {
        try (EntityManagerFactory factory = configuration("callback-cache", true)
                .property(PersistenceUnitProperties.WEAVING_EAGER, "false").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            manager.find(GraphFetchRoot.class, 1);
            manager.clear();
            DescriptorEventAdapter listener = new DescriptorEventAdapter() {
                @Override
                public void postClone(DescriptorEvent event) {
                    GraphFetchRoot root = (GraphFetchRoot) event.getObject();
                    assertEquals("child-1", root.getMany().getLabel());
                }
            };
            session(factory).getDescriptor(GraphFetchRoot.class).getEventManager().addListener(listener);
            try {
                GraphFetchRoot root = manager.find(lazyGraph(factory, "many"), 1);
                assertLoaded(factory, root, "many");
                assertEquals("child-1", root.getMany().getLabel());
            } finally {
                session(factory).getDescriptor(GraphFetchRoot.class).getEventManager().removeListener(listener);
            }
        }
    }

    @Test
    public void graphCloneScopeRestoresAfterNestedQueriesAndCallbackFailure() throws Exception {
        try (EntityManagerFactory factory = configuration("scope-cache", true).createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            manager.find(GraphFetchRoot.class, 1);
            manager.find(GraphFetchRoot.class, 2);
            manager.clear();
            List<GraphFetchRoot> nested = new ArrayList<>();
            DescriptorEventAdapter listener = new DescriptorEventAdapter() {
                @Override
                public void postClone(DescriptorEvent event) {
                    GraphFetchRoot root = (GraphFetchRoot) event.getObject();
                    if (root.getId() == 1) {
                        nested.add(manager.find(lazyGraph(factory, "many"), 2));
                    }
                }
            };
            var events = session(factory).getDescriptor(GraphFetchRoot.class).getEventManager();
            events.addListener(listener);
            try {
                assertDeferred(factory, manager.find(lazyGraph(factory, "many"), 1), "many");
                assertEquals(1, nested.size());
                assertDeferred(factory, nested.getFirst(), "many");
            } finally {
                events.removeListener(listener);
            }
            manager.clear();
            DescriptorEventAdapter failure = new DescriptorEventAdapter() {
                @Override
                public void postClone(DescriptorEvent event) { throw new IllegalStateException("graph callback failure"); }
            };
            events.addListener(failure);
            try {
                assertThrows(RuntimeException.class, () -> manager.find(lazyGraph(factory, "many"), 1));
            } finally {
                events.removeListener(failure);
            }
            manager.clear();
            GraphFetchRoot ordinary = manager.find(GraphFetchRoot.class, 1);
            assertLoaded(factory, ordinary, "many");
            assertEquals("child-1", ordinary.getMany().getLabel());
        }
    }

    @Test
    public void automaticRefreshCallbacksIsolateGraphCloneSelection() throws Exception {
        List<String> failures = new ArrayList<>();
        for (String mode : List.of("graph-control", "no-graph-refresh", "graph-refresh", "nested-refresh", "callback-failure")) {
            try (EntityManagerFactory factory = configuration("refresh-callback", true).createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                manager.find(GraphFetchRoot.class, 1);
                manager.find(GraphFetchRoot.class, 2);
                GraphFetchRoot originalOwner = (GraphFetchRoot) session(factory).getIdentityMapAccessor()
                        .getFromIdentityMap(2, GraphFetchRoot.class);
                GraphFetchChild originalTarget = originalOwner.getLazy();
                assertEquals(16, originalTarget.getId());
                assertEquals("detail-31", originalTarget.getDetail().getLabel());
                manager.clear();
                EntityGraph<GraphFetchRoot> retainedGraph = emptyGraph(factory);
                retainedGraph.addAttributeNodes("id", "version");
                GraphFetchRoot retained = manager.find(GraphFetchRoot.class, 2,
                        Map.of("jakarta.persistence.fetchgraph", retainedGraph));
                UnitOfWorkValueHolder<?> lazyHolder = (UnitOfWorkValueHolder<?>) relationshipHolder(retained, "lazy");
                assertFalse(lazyHolder.isInstantiated());
                assertTrue("The callback must use cached-holder cloning", lazyHolder.getWrappedValueHolder().isInstantiated());
                UnitOfWorkImpl unitOfWork = (UnitOfWorkImpl) manager.unwrap(JpaEntityManager.class).getUnitOfWork();
                assertFalse("The unrelated target must initially be unmanaged",
                        unitOfWork.getCloneToOriginals().containsValue(originalTarget));
                assertFalse("The nested callback target must initially be unmanaged",
                        unitOfWork.getCloneToOriginals().containsValue(originalOwner.getMany()));
                UnitOfWorkValueHolder<?> manyHolder = (UnitOfWorkValueHolder<?>) relationshipHolder(retained, "many");
                assertFalse(manyHolder.isInstantiated());
                assertTrue(manyHolder.getWrappedValueHolder().isInstantiated());
                assertRuntimeIndirection(factory, GraphFetchChild.class, "detail");
                var childDescriptor = session(factory).getDescriptor(GraphFetchChild.class);
                assertFalse(childDescriptor.shouldAcquireCascadedLocks());
                assertTrue(childDescriptor.getCacheInvalidationPolicy().shouldRefreshInvalidObjectsOnClone());
                EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
                graph.addSubgraph("many", GraphFetchChild.class).addAttributeNode("detail").addOption(FetchType.LAZY);
                graph.addSubgraph("members", GraphFetchChild.class).addAttributeNode("detail").addOption(FetchType.LAZY);
                AtomicInteger callbacks = new AtomicInteger();
                AtomicInteger nestedCallbacks = new AtomicInteger();
                GraphFetchChild.setPostLoad(child -> {
                    if (child.getId() != 1 && !(child.getId() == 16 && "nested-refresh".equals(mode))) {
                        return;
                    }
                    List<String> stack = StackWalker.getInstance().walk(frames -> frames
                            .map(frame -> frame.getClassName() + "." + frame.getMethodName()).toList());
                    int refreshDispatch = stack.indexOf("org.eclipse.persistence.descriptors.DescriptorEventManager.executeEntityRefreshEvent");
                    int deferredDispatch = stack.indexOf("org.eclipse.persistence.internal.sessions.UnitOfWorkImpl.executeDeferredEvents");
                    if (refreshDispatch < 0 || (deferredDispatch >= 0 && deferredDispatch < refreshDispatch)) {
                        return;
                    }
                    assertTrue(stack.contains("org.eclipse.persistence.internal.sessions.UnitOfWorkImpl.completeRefreshOperation"));
                    assertTrue(stack.contains("org.eclipse.persistence.internal.sessions.AbstractSession.checkAndRefreshInvalidObject"));
                    if (child.getId() == 16) {
                        nestedCallbacks.incrementAndGet();
                        assertEquals(2, stack.stream().filter(frame -> frame.equals(
                                "org.eclipse.persistence.internal.sessions.UnitOfWorkImpl.completeRefreshOperation")).count());
                        recordCallbackDetail(factory, retained.getMany(), 11, mode, failures);
                    } else {
                        callbacks.incrementAndGet();
                        recordCallbackDetail(factory, retained.getLazy(), 16, mode, failures);
                        if ("callback-failure".equals(mode)) {
                            throw new IllegalStateException("automatic refresh callback failure");
                        }
                    }
                });
                try {
                    if (!"graph-control".equals(mode)) {
                        session(factory).getIdentityMapAccessor().invalidateObject(1, GraphFetchChild.class);
                    }
                    if ("nested-refresh".equals(mode)) {
                        session(factory).getIdentityMapAccessor().invalidateObject(16, GraphFetchChild.class);
                    }
                    GraphFetchRoot result = null;
                    if ("callback-failure".equals(mode)) {
                        RuntimeException failure = assertThrows(RuntimeException.class, () -> manager.find(graph, 1));
                        Throwable cause = failure;
                        while (cause.getCause() != null) {
                            cause = cause.getCause();
                        }
                        assertEquals("automatic refresh callback failure", cause.getMessage());
                        recordCallbackDetail(factory, retained.getMany(), 11, mode + "-after", failures);
                    } else {
                        result = "no-graph-refresh".equals(mode)
                                ? manager.find(GraphFetchRoot.class, 1) : manager.find(graph, 1);
                    }
                    if ("graph-control".equals(mode)) {
                        assertEquals(0, callbacks.get());
                        assertDeferred(factory, result.getMany(), "detail");
                        assertFalse(lazyHolder.isInstantiated());
                    } else {
                        assertEquals("An automatic refresh must deliver the JPA callback", 1, callbacks.get());
                        assertTrue(lazyHolder.isInstantiated());
                    }
                    assertEquals("nested-refresh".equals(mode) ? 1 : 0, nestedCallbacks.get());
                    assertTrue("The invocation must restore the caller scope", unitOfWork.getGraphCloneSelection(originalTarget) == null);
                    if (result != null && !"no-graph-refresh".equals(mode)) {
                        assertDeferred(factory, result.getMembers().getFirst(), "detail");
                    }
                    assertLoaded(factory, originalTarget, "detail");
                } finally {
                    GraphFetchChild.setPostLoad(null);
                }
            }
        }
        assertTrue("Refresh callbacks must not inherit a graph selection: " + failures, failures.isEmpty());
    }

    private static void recordCallbackDetail(EntityManagerFactory factory, GraphFetchChild child, int id,
                                            String mode, List<String> failures) {
        assertEquals(id, child.getId());
        try {
            boolean holderLoaded = relationshipHolder(child, "detail").isInstantiated();
            boolean loaded = factory.getPersistenceUnitUtil().isLoaded(child, "detail");
            boolean globalLoaded = Persistence.getPersistenceUtil().isLoaded(child, "detail");
            if (!holderLoaded || !loaded || !globalLoaded) {
                failures.add(mode + ": holder=" + holderLoaded + ", PU=" + loaded + ", global=" + globalLoaded);
            }
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
        assertEquals("detail-" + (id * 2 - 1), child.getDetail().getLabel());
    }

    private static ValueHolderInterface<?> relationshipHolder(Object owner, String attribute) throws ReflectiveOperationException {
        Field field = owner.getClass().getDeclaredField("_persistence_" + attribute + "_vh");
        field.setAccessible(true);
        return (ValueHolderInterface<?>) field.get(owner);
    }

    @Test
    public void partialCacheCloneCommitPreservesUnfetchedRelationshipAndVersion() throws Exception {
        try (EntityManagerFactory factory = configuration("dirty-cache", true)
                .property(PersistenceUnitProperties.WEAVING_EAGER, "false").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            GraphFetchRoot warm = manager.find(GraphFetchRoot.class, 1);
            int version = warm.getVersion();
            manager.clear();
            try {
                manager.getTransaction().begin();
                GraphFetchRoot root = manager.find(lazyGraph(factory, "many", "tags"), 1);
                assertDeferred(factory, root, "many");
                assertDeferred(factory, root, "tags");
                root.setNote("graph-dirty");
                manager.getTransaction().commit();
                manager.clear();
                GraphFetchRoot reloaded = manager.find(GraphFetchRoot.class, 1);
                assertEquals("graph-dirty", reloaded.getNote());
                assertTrue(reloaded.getVersion() > version);
                assertEquals("child-1", reloaded.getMany().getLabel());
                assertEquals(List.of("tag-1"), reloaded.getTags());
            } finally {
                if (manager.getTransaction().isActive()) {
                    manager.getTransaction().rollback();
                }
                manager.clear();
                manager.getTransaction().begin();
                manager.find(GraphFetchRoot.class, 1).setNote("root-1");
                manager.getTransaction().commit();
            }
        }
    }

    @Test
    public void graphResolutionPreservesInheritedExclusionsAndTypedOverrides() {
        PersistenceConfiguration config = configuration("graph-metadata", false)
                .property(PersistenceUnitProperties.DDL_GENERATION, "none");
        for (Class<?> model : List.of(DistributedGraphRoot.class, DistributedGraphChild.class,
                DistributedGraphSpecialChild.class, DistributedGraphOtherChild.class, DistributedGraphDetail.class,
                DistributedGraphSpecialDetail.class, SharedGraphRoot.class, SharedGraphChild.class,
                PropertyGraphBase.class, PropertyGraphRoot.class)) {
            config.managedClass(model);
        }
        try (EntityManagerFactory factory = config.createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            EntityGraph<DistributedGraphChild> graph = factory.getMetamodel().entity(DistributedGraphChild.class)
                    .createEntityGraph();
            graph.removeAttributeNode("label");
            graph.addTreatedSubgraph(DistributedGraphSpecialChild.class).addAttributeNode("label");
            graph.addTreatedSubgraph(DistributedGraphOtherChild.class).removeAttributeNode("other");
            ObjectLevelReadQuery query = (ObjectLevelReadQuery) manager.createQuery("select c from DngChild c",
                    DistributedGraphChild.class).setHint("jakarta.persistence.loadgraph", graph)
                    .unwrap(JpaQuery.class).getDatabaseQuery();
            FetchGroup raw = query.getFetchGroup();
            Set<String> rawNames = Set.copyOf(raw.getAttributeNames());
            var descriptor = session(factory).getDescriptor(DistributedGraphChild.class);
            FetchGroup defaults = descriptor.getFetchGroupManager().getDefaultFetchGroup();
            Set<String> defaultNames = defaults == null ? null : Set.copyOf(defaults.getAttributeNames());
            FetchGroup resolved = raw.resolveGraph(session(factory), descriptor, false);
            for (int repeat = 0; repeat < 2; repeat++) {
                FetchGroup special = (FetchGroup) resolved.findGroup(session(factory)
                        .getDescriptor(DistributedGraphSpecialChild.class));
                FetchGroup other = (FetchGroup) resolved.findGroup(session(factory)
                        .getDescriptor(DistributedGraphOtherChild.class));
                assertFalse(resolved.containsAttribute("label"));
                assertTrue(special.containsAttribute("label"));
                assertTrue(special.containsAttribute("special"));
                assertTrue(special.containsAttribute("id"));
                assertFalse(other.containsAttribute("label"));
                assertFalse(other.containsAttribute("other"));
                assertTrue(other.containsAttribute("id"));
                for (FetchGroup group : List.of(resolved, special, other)) {
                    assertEquals(group.getAttributeNames(), group.getAllItems().keySet());
                    for (String name : List.of("id", "label", "special", "other")) {
                        assertEquals(group.getAttributeNames().contains(name), group.containsAttributeInternal(name));
                    }
                }
                // Inheritance contains parent/subtype back references. Repeated
                // resolution must terminate and must not mutate the input tree.
                resolved = resolved.resolveGraph(session(factory), descriptor, false);
            }
            assertEquals(rawNames, raw.getAttributeNames());
            assertSame(defaults, descriptor.getFetchGroupManager().getDefaultFetchGroup());
            assertEquals(defaultNames, defaults == null ? null : defaults.getAttributeNames());
            assertTrue(graph.getAttributeNode("label").getOptions().contains(FetchType.LAZY));
        }
    }

    private static void assertSuppressedRead(String attribute, Object expected) throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            GraphFetchRoot root = manager.find(lazyGraph(fixture, attribute), 1);
            assertDeferred(fixture, root, attribute);
            assertUnlistedEager(fixture, root);
            Object actual = root.getClass().getMethod("get" + Character.toUpperCase(attribute.charAt(0))
                    + attribute.substring(1)).invoke(root);
            if (actual instanceof GraphFetchChild child) {
                actual = child.getLabel();
            } else if (actual instanceof List<?> values) {
                actual = values.stream().map(value -> value instanceof GraphFetchChild child
                        ? child.getLabel() : value).toList();
            }
            assertEquals(expected, actual);
            assertLoaded(fixture, root, attribute);
        }
    }

    private static void assertAgentNestedDetail(EntityAgent agent, GraphFetchChild child, int childId) {
        assertEquals("child-" + childId, child.getLabel());
        assertFalse(fixture.getPersistenceUnitUtil().isLoaded(child, "detail"));
        assertLoaded(fixture, child, "otherDetail");
        GraphFetchDetail reference = child.getDetail();
        assertNotNull(reference);
        assertSame(reference, agent.fetch(reference));
        assertEquals("detail-" + (childId * 2 - 1), reference.getLabel());
        assertLoaded(fixture, child, "detail");
    }

    private static void assertLoaded(EntityManagerFactory factory, Object owner, String attribute) {
        assertTrue("Attribute must be loaded: " + attribute, factory.getPersistenceUnitUtil().isLoaded(owner, attribute));
    }

    private static void assertUnlistedEager(EntityManagerFactory factory, GraphFetchRoot root) {
        assertLoaded(factory, root, "unlisted");
        assertEquals("child-" + ((root.getId() - 1) * 10 + 3), root.getUnlisted().getLabel());
    }

    private static void assertDeferred(EntityManagerFactory factory, Object owner, String attribute) throws Exception {
        assertFalse("Attribute must remain deferred: " + attribute,
                factory.getPersistenceUnitUtil().isLoaded(owner, attribute));
        boolean toOne = owner instanceof GraphFetchRoot ? ROOT_TO_ONES.contains(attribute)
                : Set.of("detail", "otherDetail").contains(attribute);
        if (toOne) {
            ForeignReferenceMapping mapping = (ForeignReferenceMapping) session(factory)
                    .getDescriptor(owner.getClass()).getMappingForAttributeName(attribute);
            if (!mapping.usesIndirection()) {
                assertExcluded(owner, attribute);
                Field field = owner.getClass().getDeclaredField(attribute);
                field.setAccessible(true);
                assertTrue("A deferred no-indirection attribute must contain no loaded value", field.get(owner) == null);
                return;
            }
            Field field = owner.getClass().getDeclaredField("_persistence_" + attribute + "_vh");
            field.setAccessible(true);
            Object holder = field.get(owner);
            if (holder != null) {
                assertTrue(holder instanceof ValueHolderInterface<?>);
                assertFalse("A deferred holder must not be initialized", ((ValueHolderInterface<?>) holder).isInstantiated());
            } else {
                assertExcluded(owner, attribute);
            }
        } else if (Set.of("members", "peers", "tags").contains(attribute)) {
            Field field = owner.getClass().getDeclaredField(attribute);
            field.setAccessible(true);
            Object value = field.get(owner);
            if (value instanceof IndirectContainer container) {
                assertFalse("A deferred collection must not be initialized", container.getValueHolder().isInstantiated());
            } else {
                assertExcluded(owner, attribute);
            }
        } else {
            assertExcluded(owner, attribute);
        }
    }

    private static void assertExcluded(Object owner, String attribute) {
        assertTrue("Missing deferred storage requires a woven partial object", owner instanceof FetchGroupTracker);
        FetchGroupTracker tracker = (FetchGroupTracker) owner;
        assertNotNull("Partial objects require an actual fetch group", tracker._persistence_getFetchGroup());
        assertFalse("The fetch group must exclude " + attribute, tracker._persistence_isAttributeFetched(attribute));
    }

    private static void assertHolder(Class<?> type, String attribute) throws Exception {
        assertTrue("Real eager weaving required: " + type.getName() + "." + attribute,
                ValueHolderInterface.class.isAssignableFrom(type.getDeclaredField("_persistence_" + attribute + "_vh").getType()));
    }

    private static void assertRuntimeIndirection(EntityManagerFactory factory, Class<?> type, String attribute) {
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) session(factory).getDescriptor(type)
                .getMappingForAttributeName(attribute);
        assertTrue("Runtime eager indirection required: " + type.getName() + "." + attribute, mapping.usesIndirection());
    }

    private static EntityGraph<GraphFetchRoot> emptyGraph(EntityManagerFactory factory) {
        return factory.getMetamodel().entity(GraphFetchRoot.class).createEntityGraph();
    }

    private static EntityGraph<GraphFetchRoot> lazyGraph(EntityManagerFactory factory, String... attributes) {
        EntityGraph<GraphFetchRoot> graph = emptyGraph(factory);
        for (String attribute : attributes) {
            graph.addAttributeNode(attribute).addOption(FetchType.LAZY);
        }
        return graph;
    }

    private static PersistenceConfiguration configuration(String kind, boolean sharedCache) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        properties.put(PersistenceUnitProperties.WEAVING_EAGER, "true");
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, Boolean.toString(sharedCache));
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        for (String name : List.of(PersistenceUnitProperties.JDBC_DRIVER, PersistenceUnitProperties.JDBC_URL,
                PersistenceUnitProperties.JDBC_USER, PersistenceUnitProperties.JDBC_PASSWORD,
                PersistenceUnitProperties.TARGET_DATABASE)) {
            String value = System.getProperty(name);
            if (value != null) {
                properties.put(name, value);
            }
        }
        return new PersistenceConfiguration("graph-fetch-options-" + kind + "-" + IDENTITIES.incrementAndGet())
                .provider(PersistenceProvider.class.getName())
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .validationMode(ValidationMode.NONE)
                .sharedCacheMode(sharedCache ? SharedCacheMode.ALL : SharedCacheMode.NONE)
                .managedClass(GraphFetchRoot.class)
                .managedClass(GraphFetchChild.class)
                .managedClass(GraphFetchDetail.class)
                .properties(properties);
    }

    private static ServerSession session(EntityManagerFactory factory) {
        return factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
    }

    private static boolean exists(String name) {
        TableDefinition table = new TableDefinition();
        table.setName(name);
        return schema.checkTableExists(table);
    }
}
