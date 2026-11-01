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
package org.eclipse.persistence.jpa.namedgraphs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeNode;
import jakarta.persistence.BatchFetch;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityAgent;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.Graph;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.Subgraph;
import jakarta.persistence.ValidationMode;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.jpa.EntityGraphImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphChild;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphDetail;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphOtherChild;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphSpecialDetail;
import org.eclipse.persistence.jpa.namedgraphs.model.DistributedGraphSpecialChild;
import org.eclipse.persistence.jpa.namedgraphs.model.MissingGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.PropertyGraphBase;
import org.eclipse.persistence.jpa.namedgraphs.model.PropertyGraphRoot;
import org.eclipse.persistence.jpa.namedgraphs.model.SharedGraphChild;
import org.eclipse.persistence.jpa.namedgraphs.model.SharedGraphRoot;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Distributed graph metadata and actual fetching through static-woven mappings. */
public class TestDistributedNamedEntityGraphs {
    private static final AtomicLong IDENTITIES = new AtomicLong();
    private static final String RESOURCES = "META-INF/distributed-named-graphs/";
    private static final List<Class<?>> MODELS = List.of(DistributedGraphRoot.class, DistributedGraphChild.class,
            DistributedGraphSpecialChild.class, DistributedGraphOtherChild.class, DistributedGraphDetail.class,
            DistributedGraphSpecialDetail.class, SharedGraphRoot.class,
            SharedGraphChild.class, PropertyGraphBase.class, PropertyGraphRoot.class);
    private static final List<String> TABLES = List.of("JPA_DNG_ROOT", "JPA_DNG_CHILD", "JPA_DNG_SPECIAL",
            "JPA_DNG_DETAIL", "JPA_DNG_LIST", "JPA_DNG_MAP", "JPA_DNG_KEYS", "JPA_DNG_SHARED_ROOT",
            "JPA_DNG_SHARED_CHILD", "JPA_DNG_PROPERTY", "JPA_DNG_TYPED_KEYS", "JPA_DNG_OTHER_CHILD",
            "JPA_DNG_SPECIAL_DETAIL", "JPA_DNG_CHILD_DETAILS");
    private static EntityManagerFactory fixture;
    private static SchemaManager schema;
    private static boolean ownsSchema;

    @BeforeClass
    public static void createFixture() throws Exception {
        assertHolder(DistributedGraphRoot.class, "child");
        assertHolder(DistributedGraphChild.class, "detail");
        assertHolder(DistributedGraphSpecialChild.class, "detail");
        assertHolder(DistributedGraphOtherChild.class, "detail");
        assertHolder(DistributedGraphRoot.class, "otherChild");
        assertHolder(SharedGraphRoot.class, "child");
        assertHolder(SharedGraphChild.class, "detail");
        assertHolder(PropertyGraphRoot.class, "target");
        assertHolder(PropertyGraphRoot.class, "explicitTarget");
        assertHolder(PropertyGraphRoot.class, "inheritedTarget");
        try {
            fixture = configuration("fixture").createEntityManagerFactory();
            schema = new SchemaManager(session(fixture));
            for (String table : TABLES) {
                assertFalse("Refusing to acquire an existing fixture table: " + table, exists(table));
            }
            ownsSchema = true;
            schema.createDefaultTables(false);
            for (String table : TABLES) {
                assertTrue("Fixture DDL did not create " + table, exists(table));
            }
            try (EntityManager manager = fixture.createEntityManager()) {
                manager.getTransaction().begin();
                try {
                    DistributedGraphDetail detail = new DistributedGraphDetail(1, "detail");
                    DistributedGraphDetail omitted = new DistributedGraphDetail(2, "omitted");
                    DistributedGraphDetail eager = new DistributedGraphDetail(3, "eager");
                    DistributedGraphChild child = new DistributedGraphChild(1, "child", detail);
                    DistributedGraphSpecialChild special = new DistributedGraphSpecialChild(2, detail);
                    DistributedGraphOtherChild other = new DistributedGraphOtherChild(3, detail);
                    DistributedGraphSpecialDetail specialDetail = new DistributedGraphSpecialDetail(4);
                    DistributedGraphChild deepChild = new DistributedGraphChild(4, "deep-child", specialDetail);
                    DistributedGraphDetail siblingDetail = new DistributedGraphDetail(5, "sibling-detail");
                    DistributedGraphChild sibling = new DistributedGraphChild(5, "sibling", siblingDetail);
                    SharedGraphChild shared = new SharedGraphChild(1, detail);
                    manager.persist(detail);
                    manager.persist(omitted);
                    manager.persist(eager);
                    manager.persist(child);
                    manager.persist(special);
                    manager.persist(other);
                    manager.persist(specialDetail);
                    manager.persist(deepChild);
                    manager.persist(siblingDetail);
                    manager.persist(sibling);
                    manager.persist(shared);
                    DistributedGraphRoot first = new DistributedGraphRoot(1, child, omitted, eager);
                    first.setOtherChild(sibling);
                    manager.persist(first);
                    manager.persist(new DistributedGraphRoot(3, other, omitted, eager));
                    manager.persist(new DistributedGraphRoot(4, deepChild, omitted, eager));
                    manager.persist(new DistributedGraphRoot(2, special, omitted, eager));
                    manager.persist(new SharedGraphRoot(1, shared));
                    manager.persist(new PropertyGraphRoot(1, detail));
                    manager.getTransaction().commit();
                } finally {
                    if (manager.getTransaction().isActive()) {
                        manager.getTransaction().rollback();
                    }
                }
            }
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
                    assertFalse("Owned table must be removed: " + table, exists(table));
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
    public void distinctNamesExposeTypedNestedNodes() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngRoot");
            assertEquals(Set.of("label", "child"), names(graph));
            Subgraph<?> child = subgraph(graph, "child", DistributedGraphChild.class);
            assertEquals(Set.of("detail"), names(child));
            assertEquals(Set.of("label"), names(subgraph(child, "detail", DistributedGraphDetail.class)));
            assertEquals(DistributedGraphChild.class,
                    manager.getEntityGraph("DngChild").getGraphedType().getJavaType());
            assertEquals(Set.of("child"), names(manager.getEntityGraph("DngRepeat")));
        }
    }

    @Test
    public void sharedNamesHaveOnePublicRootInEitherRegistrationOrder() {
        for (boolean reverse : List.of(false, true)) {
            PersistenceConfiguration configuration = configuration("registration-order", reverse);
            try (EntityManagerFactory factory = configuration.createEntityManagerFactory();
                    EntityManager manager = factory.createEntityManager()) {
                EntityGraph<SharedGraphRoot> graph = manager.getEntityGraph(SharedGraphRoot.class, "DngShared");
                assertEquals(Set.of("child"), names(graph));
                assertEquals(Set.of("detail"), names(subgraph(graph, "child", SharedGraphChild.class)));
                assertEquals(SharedGraphRoot.class, manager.getEntityGraph("DngShared").getGraphedType().getJavaType());
                assertFalse(factory.getNamedEntityGraphs(SharedGraphChild.class).containsKey("DngShared"));
                assertThrows(IllegalArgumentException.class,
                        () -> manager.getEntityGraph(SharedGraphChild.class, "DngShared"));
            }
        }
    }

    @Test
    public void selectedPropertyMappedSuperclassAndExplicitAccessContribute() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<PropertyGraphRoot> graph = manager.getEntityGraph(PropertyGraphRoot.class, "DngProperty");
            assertEquals(Set.of("label", "target", "explicitTarget", "inheritedTarget"), names(graph));
            PropertyGraphRoot plain = manager.find(PropertyGraphRoot.class, 1);
            assertLazy(plain, "target");
            assertLazy(plain, "explicitTarget");
            assertLazy(plain, "inheritedTarget");
            manager.clear();
            PropertyGraphRoot loaded = manager.find(graph, 1);
            for (String attribute : List.of("target", "explicitTarget", "inheritedTarget")) {
                assertTrue(attribute, fixture.getPersistenceUnitUtil().isLoaded(loaded, attribute));
            }
            assertEquals("detail", loaded.getTarget().getLabel());
            assertEquals("detail", loaded.getExplicitTarget().getLabel());
            assertEquals("detail", loaded.getInheritedTarget().getLabel());
        }
    }

    @Test
    public void entityInheritanceIncludesSelectedBaseMappings() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphSpecialChild> graph = manager.getEntityGraph(DistributedGraphSpecialChild.class, "DngInherited");
            assertEquals(Set.of("label", "detail", "special"), names(graph));
            DistributedGraphSpecialChild child = manager.find(graph, 2);
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(child, "detail"));
            assertEquals("detail", child.getDetail().getLabel());
            assertEquals("special-value", child.getSpecial());
        }
    }

    @Test
    public void mixedNestedAndDistributedNodesPreserveBothContributions() {
        try (EntityManager manager = fixture.createEntityManager()) {
            Subgraph<?> child = subgraph(manager.getEntityGraph("DngMixed"), "child", DistributedGraphChild.class);
            assertEquals(Set.of("label", "detail"), names(child));
            assertEquals(Set.of("label"), names(subgraph(child, "detail", DistributedGraphDetail.class)));
            Subgraph<?> multiple = subgraph(manager.getEntityGraph("DngMultiple"), "child", DistributedGraphChild.class);
            assertEquals(Set.of("label", "detail"), names(multiple));
            EntityGraph<?> all = manager.getEntityGraph("DngAll");
            assertEquals(Set.of("id", "label", "child", "otherChild", "omitted", "eager", "children", "byName", "keyed", "typedKeys"), names(all));
            assertTrue(subgraph(all, "child", DistributedGraphChild.class).hasAttributeNode("detail"));
        }
    }

    @Test
    public void collectionMapValueAndTreatedBranchesKeepTheirTypes() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngTyped");
            for (String attribute : List.of("child", "children")) {
                assertEquals(Set.of(DistributedGraphChild.class, DistributedGraphSpecialChild.class),
                        graph.getAttributeNode(attribute).getSubgraphs().keySet());
                assertTrue(subgraph(graph, attribute, DistributedGraphChild.class).hasAttributeNode("detail"));
                assertTrue(subgraph(graph, attribute, DistributedGraphSpecialChild.class).hasAttributeNode("special"));
            }
            assertEquals(Set.of(DistributedGraphChild.class), graph.getAttributeNode("byName").getSubgraphs().keySet());
            assertTrue("Fetch has no implicit map key graph", graph.getAttributeNode("byName").getKeySubgraphs().isEmpty());
            DistributedGraphRoot root = manager.find(graph, 2);
            for (String attribute : List.of("child", "children", "byName")) {
                assertTrue(attribute, fixture.getPersistenceUnitUtil().isLoaded(root, attribute));
            }
            assertTrue(root.getChild() instanceof DistributedGraphSpecialChild);
            assertEquals("special-value", ((DistributedGraphSpecialChild) root.getChild()).getSpecial());
            assertEquals(2, root.getChildren().getFirst().getId());
            assertEquals(2, root.getByName().get("child").getId());
        }
    }

    @Test
    public void legacyKeyAndSubclassGraphsRemainUsable() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngLegacy");
            AttributeNode<?> keyed = graph.getAttributeNode("keyed");
            assertEquals(Set.of(DistributedGraphDetail.class), keyed.getKeySubgraphs().keySet());
            assertTrue(keyed.getKeySubgraphs().get(DistributedGraphDetail.class).hasAttributeNode("label"));
            assertTrue(keyed.getSubgraphs().get(DistributedGraphChild.class).hasAttributeNode("label"));
            DistributedGraphRoot root = manager.find(graph, 1);
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "keyed"));
            assertEquals("omitted", root.getKeyed().keySet().iterator().next().getLabel());
            manager.clear();
            EntityGraph<DistributedGraphChild> subclass = manager.getEntityGraph(DistributedGraphChild.class, "DngSubclass");
            DistributedGraphChild special = manager.find(subclass, 2);
            assertTrue(special instanceof DistributedGraphSpecialChild);
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(special, "special"));
            assertEquals("special-value", ((DistributedGraphSpecialChild) special).getSpecial());
        }
    }

    @Test
    public void typedKeySubgraphsRetainBaseAndSubtypeAcrossCopies() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<?> graph = manager.getEntityGraph("DngTypedKeys");
            Map<?, ? extends Subgraph<?>> keys = graph.getAttributeNode("typedKeys").getKeySubgraphs();
            assertEquals(Set.of(DistributedGraphChild.class, DistributedGraphSpecialChild.class), keys.keySet());
            assertEquals(Set.of("label"), names(keys.get(DistributedGraphChild.class)));
            assertEquals(Set.of("special"), names(keys.get(DistributedGraphSpecialChild.class)));
            assertEquals(Set.of("label"), names(subgraph(graph, "typedKeys", DistributedGraphDetail.class)));
            keys.get(DistributedGraphChild.class).addAttributeNode("detail");
            EntityGraph<?> second = manager.getEntityGraph("DngTypedKeys");
            assertFalse(second.getAttributeNode("typedKeys").getKeySubgraphs().get(DistributedGraphChild.class)
                    .hasAttributeNode("detail"));
            assertEquals(Set.of("special"), names(second.getAttributeNode("typedKeys").getKeySubgraphs()
                    .get(DistributedGraphSpecialChild.class)));
        }
    }

    @Test
    public void distributedLazyOptionsOverrideOverlappingNestedEagerNodes() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngMixedLazy");
            Subgraph<?> childGraph = subgraph(graph, "child", DistributedGraphChild.class);
            assertTrue(childGraph.getAttributeNode("detail").getOptions().contains(FetchType.LAZY));
            DistributedGraphRoot root = manager.find(graph, 1);
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "child"));
            assertLazy(root.getChild(), "detail");
            assertEquals("detail", root.getChild().getDetail().getLabel());
        }
    }

    @Test
    public void plainFindHonorsUnqualifiedFetchWithoutActivatingNamedGraphs() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            DistributedGraphRoot plain = manager.find(DistributedGraphRoot.class, 1);
            assertLazy(plain, "child");
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(plain, "omitted"));
            assertFalse(fixture.getPersistenceUnitUtil().isLoaded(plain, "children"));
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(plain, "eager"));
            assertEquals("child", plain.getChild().getLabel());
            assertLazy(plain.getChild(), "detail");
            assertEquals("omitted", plain.getOmitted().getLabel());
            assertThrows(IllegalArgumentException.class, () -> manager.getEntityGraph(""));
        }
    }

    @Test
    public void findLoadAndFetchGraphsFetchSelectedState() throws Exception {
        for (String hint : List.of("jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph")) {
            try (EntityManager manager = fixture.createEntityManager()) {
                EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngRoot");
                DistributedGraphRoot root = manager.find(DistributedGraphRoot.class, 1, Map.of(hint, graph));
                assertFetchedPath(root);
                if (hint.endsWith("loadgraph")) {
                    assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "omitted"));
                    assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "eager"));
                } else {
                    assertUnloaded(root, "omitted");
                }
            }
        }
        try (EntityManager manager = fixture.createEntityManager()) {
            DistributedGraphRoot root = manager.find(manager.getEntityGraph(DistributedGraphRoot.class, "DngRoot"), 1);
            assertFetchedPath(root);
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "omitted"));
        }
    }

    @Test
    public void graphQueryEntryPointsFetchSelectedState() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngRoot");
            assertFetchedPath(manager.createQuery("select r from DngRoot r where r.id = 1", graph).getSingleResult());
            manager.clear();
            assertFetchedPath(manager.createQuery("select r from DngRoot r where r.id = 1")
                    .withEntityGraph(graph).getSingleResult());
            manager.clear();
            assertFetchedPath(manager.createQuery("select r from DngRoot r where r.id = 1", DistributedGraphRoot.class)
                    .setHint("jakarta.persistence.fetchgraph", graph).getSingleResult());
        }
    }

    @Test
    public void sharedGraphFetchesChildAndDetail() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            SharedGraphRoot plain = manager.find(SharedGraphRoot.class, 1);
            assertLazy(plain, "child");
            manager.clear();
            SharedGraphRoot loaded = manager.find(manager.getEntityGraph(SharedGraphRoot.class, "DngShared"), 1);
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(loaded, "child"));
            SharedGraphChild child = loaded.getChild();
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(child, "detail"));
            assertEquals("detail", child.getDetail().getLabel());
        }
    }

    @Test
    public void namedLazyOptionsAndIndependentCopiesRemainIsolated() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> lazy = manager.getEntityGraph(DistributedGraphRoot.class, "DngLazy");
            assertTrue(lazy.getAttributeNode("child").getOptions().contains(FetchType.LAZY));
            for (String hint : List.of("jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph")) {
                manager.clear();
                assertLazy(manager.find(DistributedGraphRoot.class, 1, Map.of(hint, lazy)), "child");
            }
            EntityGraph<DistributedGraphRoot> first = manager.getEntityGraph(DistributedGraphRoot.class, "DngLazyNested");
            EntityGraph<DistributedGraphRoot> second = manager.getEntityGraph(DistributedGraphRoot.class, "DngLazyNested");
            Subgraph<?> firstChild = subgraph(first, "child", DistributedGraphChild.class);
            Subgraph<?> secondChild = subgraph(second, "child", DistributedGraphChild.class);
            assertNotSame(firstChild, secondChild);
            assertTrue(firstChild.getAttributeNode("detail").getOptions().contains(FetchType.LAZY));
            firstChild.addAttributeNode("detail").addOption(new BatchFetch(9));
            assertTrue(firstChild.getAttributeNode("detail").getOptions().contains(FetchType.EAGER));
            assertTrue(secondChild.getAttributeNode("detail").getOptions().contains(FetchType.LAZY));
            assertFalse(secondChild.getAttributeNode("detail").getOptions().contains(new BatchFetch(9)));
            assertTrue(subgraph(manager.getEntityGraph("DngLazyNested"), "child", DistributedGraphChild.class)
                    .getAttributeNode("detail").getOptions().contains(FetchType.LAZY));
            EntityGraph<?> eager = manager.getEntityGraph("DngRoot");
            assertTrue(eager.getAttributeNode("child").getOptions().contains(FetchType.EAGER));
            assertTrue(eager.getAttributeNode("child").getOptions().contains(CacheRetrieveMode.BYPASS));
            assertTrue(eager.getAttributeNode("child").getOptions().contains(CacheStoreMode.BYPASS));
            eager.getAttributeNode("child").getOptions().clear();
            assertTrue(eager.getAttributeNode("child").getOptions().contains(FetchType.EAGER));
            manager.clear();
            assertLazy(manager.find(DistributedGraphRoot.class, 1), "child");
        }
    }

    @Test
    public void runtimeCreatedGraphAndEntityAgentRemainCompatible() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> graph = manager.createEntityGraph(DistributedGraphRoot.class);
            graph.addSubgraph("child").addSubgraph("detail").addAttributeNodes("label");
            assertFetchedPath(manager.find(graph, 1));
        }
        try (EntityAgent agent = fixture.createEntityAgent()) {
            EntityGraph<DistributedGraphRoot> graph = agent.getEntityGraph(DistributedGraphRoot.class, "DngRoot");
            assertFetchedPath(agent.find(graph, 1));
            assertFetchedPath(agent.createQuery("select r from DngRoot r where r.id = 1", graph).getSingleResult());
        }
    }

    @Test
    public void publicListingsAndMissingLookupsPreserveRootTypes() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<? extends DistributedGraphRoot> listed = fixture.getNamedEntityGraphs(DistributedGraphRoot.class).get("DngRoot");
            assertNotNull(listed);
            assertEquals(DistributedGraphRoot.class, listed.getGraphedType().getJavaType());
            assertTrue(fixture.getMetamodel().entity(DistributedGraphRoot.class).getNamedEntityGraphs().containsKey("DngRoot"));
            assertTrue(manager.getEntityGraphs(DistributedGraphRoot.class).stream().anyMatch(graph -> "DngRoot".equals(graph.getName())));
            listed.addAttributeNode("omitted");
            assertFalse(manager.getEntityGraph("DngRoot").hasAttributeNode("omitted"));
            assertThrows(IllegalArgumentException.class, () -> manager.getEntityGraph("DngDoesNotExist"));
            assertThrows(IllegalArgumentException.class, () -> manager.getEntityGraph(DistributedGraphChild.class, "DngRoot"));
        }
    }

    @Test
    public void warmSharedCacheFindsApplyDistributedGraphs() throws Exception {
        for (String operation : List.of("find", "jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph")) {
            try (EntityManagerFactory factory = configuration("warm-find").sharedCacheMode(SharedCacheMode.ALL)
                    .property(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true").createEntityManagerFactory()) {
                try (EntityManager warm = factory.createEntityManager()) {
                    DistributedGraphRoot plain = warm.find(DistributedGraphRoot.class, 1);
                    assertLazy(plain, "child");
                }
                assertTrue("Control must populate the shared cache", factory.getCache().contains(DistributedGraphRoot.class, 1));
                try (EntityManager manager = factory.createEntityManager()) {
                    EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngRoot");
                    DistributedGraphRoot root = "find".equals(operation) ? manager.find(graph, 1)
                            : manager.find(DistributedGraphRoot.class, 1, Map.of(operation, graph));
                    assertFetchedPath(root);
                }
            }
        }
    }

    @Test
    public void warmSharedCacheQueriesAndEntityAgentApplyDistributedGraphs() throws Exception {
        try (EntityManagerFactory factory = configuration("warm-query").sharedCacheMode(SharedCacheMode.ALL)
                .property(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "true").createEntityManagerFactory()) {
            try (EntityManager warm = factory.createEntityManager()) {
                assertLazy(warm.find(DistributedGraphRoot.class, 1), "child");
            }
            assertTrue(factory.getCache().contains(DistributedGraphRoot.class, 1));
            try (EntityManager manager = factory.createEntityManager()) {
                EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngRoot");
                assertFetchedPath(manager.createQuery("select r from DngRoot r where r.id = 1", graph).getSingleResult());
                manager.clear();
                assertFetchedPath(manager.createQuery("select r from DngRoot r where r.id = 1", DistributedGraphRoot.class)
                        .setHint("jakarta.persistence.loadgraph", graph).getSingleResult());
            }
            try (EntityAgent agent = factory.createEntityAgent()) {
                EntityGraph<DistributedGraphRoot> graph = agent.getEntityGraph(DistributedGraphRoot.class, "DngRoot");
                assertFetchedPath(agent.find(graph, 1));
                assertFetchedPath(agent.find(graph, 1));
            }
        }
    }

    @Test
    public void xmlReplacementAndMetadataCompleteSuppressAnnotations() throws Exception {
        try (EntityManagerFactory factory = configuration("xml-override").mappingFile(RESOURCES + "orm.xml").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            assertEquals(Set.of("label"), names(manager.getEntityGraph("DngRoot")));
            assertThrows(IllegalArgumentException.class, () -> manager.getEntityGraph("DngTyped"));
            Subgraph<?> xmlChild = subgraph(manager.getEntityGraph("DngRepeat"), "child", DistributedGraphChild.class);
            assertEquals(Set.of("id", "label", "detail"), names(xmlChild));
            assertEquals(Set.of("label"), names(subgraph(xmlChild, "detail", DistributedGraphDetail.class)));
            EntityGraph<DistributedGraphRoot> all = manager.getEntityGraph(DistributedGraphRoot.class, "DngAll");
            assertEquals(Set.of("id", "label", "child", "otherChild", "omitted", "eager", "children", "byName", "keyed", "typedKeys"), names(all));
            assertTrue(all.getAttributeNode("child").getOptions().contains(FetchType.EAGER));
            assertEquals(Set.of("detail"), names(subgraph(all, "child", DistributedGraphChild.class)));
            assertFetchedPath(manager.find(all, 1));
            assertFalse(manager.getEntityGraph("DngRoot").hasAttributeNode("child"));
        }
        try (EntityManagerFactory factory = configuration("complete").managedClass(MissingGraphRoot.class)
                .mappingFile(RESOURCES + "complete-orm.xml").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            assertEquals(Set.of("child"), names(manager.getEntityGraph("DngMissing")));
            AttributeNode<?> child = manager.getEntityGraph("DngMissing").getAttributeNode("child");
            assertTrue(child.getSubgraphs().isEmpty());
            assertTrue(child.getOptions().contains(FetchType.EAGER));
            assertFalse(child.getOptions().contains(FetchType.LAZY));
            assertFalse(child.getOptions().contains(CacheRetrieveMode.BYPASS));
            assertFalse(child.getOptions().contains(CacheStoreMode.BYPASS));
            assertFalse(child.getOptions().contains(new BatchFetch(13)));
        }
    }

    @Test
    public void xmlRetainedNodesPreserveNamedFetchOptionsAndLazyExecution() throws Exception {
        try (EntityManagerFactory factory = configuration("xml-options").mappingFile(RESOURCES + "orm.xml").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            EntityGraph<?> lazy = manager.getEntityGraph("DngLazy");
            assertTrue(lazy.getAttributeNode("child").getOptions().containsAll(
                    Set.of(FetchType.LAZY, CacheRetrieveMode.BYPASS, CacheStoreMode.BYPASS, new BatchFetch(7))));
            for (String hint : List.of("jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph")) {
                manager.clear();
                assertLazy(manager.find(DistributedGraphRoot.class, 1, Map.of(hint, lazy)), "child");
            }
        }
    }

    @Test
    public void xmlBootstrapAndSeparateFactoriesHaveIndependentGraphs() {
        Map<String, Object> properties = properties();
        properties.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, RESOURCES + "persistence.xml");
        properties.put(PersistenceUnitProperties.SESSION_NAME, identity("xml-session"));
        try (EntityManagerFactory first = Persistence.createEntityManagerFactory("distributed-named-graphs", properties);
                EntityManagerFactory second = configuration("independent").createEntityManagerFactory();
                EntityManager one = first.createEntityManager(); EntityManager two = second.createEntityManager()) {
            EntityGraph<?> firstGraph = one.getEntityGraph("DngRoot");
            firstGraph.addAttributeNode("omitted");
            assertFalse(two.getEntityGraph("DngRoot").hasAttributeNode("omitted"));
            assertFalse(one.getEntityGraph("DngRoot").hasAttributeNode("omitted"));
            assertEquals(Set.of("child"), names(two.getEntityGraph("DngShared")));
        }
    }

    @Test
    public void reusedLocalLabelsKeepSiblingAdditionsAndLazyOptionsIndependent() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> lazy = manager.getEntityGraph(DistributedGraphRoot.class, "DngReusedLazy");
            Subgraph<?> left = subgraph(lazy, "child", DistributedGraphChild.class);
            Subgraph<?> right = subgraph(lazy, "otherChild", DistributedGraphChild.class);
            assertNotSame(left, right);
            assertTrue(left.getAttributeNode("detail").getOptions().contains(FetchType.LAZY));
            assertTrue(right.getAttributeNode("detail").getOptions().contains(FetchType.EAGER));
            for (String hint : List.of("jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph")) {
                manager.clear();
                DistributedGraphRoot root = manager.find(DistributedGraphRoot.class, 1, Map.of(hint, lazy));
                assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "child"));
                assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "otherChild"));
                assertLazy(root.getChild(), "detail");
                assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root.getOtherChild(), "detail"));
                assertEquals("sibling-detail", root.getOtherChild().getDetail().getLabel());
            }
            EntityGraph<?> added = manager.getEntityGraph("DngReusedAdd");
            assertEquals(Set.of("label", "detail"), names(subgraph(added, "child", DistributedGraphChild.class)));
            assertEquals(Set.of("label"), names(subgraph(added, "otherChild", DistributedGraphChild.class)));
            left.addAttributeNode("label");
            assertFalse(right.hasAttributeNode("label"));
            assertFalse(subgraph(manager.getEntityGraph("DngReusedLazy"), "child", DistributedGraphChild.class).hasAttributeNode("label"));
        }
    }

    @Test
    public void reusedLocalLabelsKeepAddedNodesOutOfTheOtherSibling() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngReusedAdd");
            assertEquals(Set.of("label", "detail"), names(subgraph(graph, "child", DistributedGraphChild.class)));
            assertEquals(Set.of("label"), names(subgraph(graph, "otherChild", DistributedGraphChild.class)));
            DistributedGraphRoot root = manager.find(graph, 1);
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "child"));
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "otherChild"));
            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root.getChild(), "detail"));
            assertLazy(root.getOtherChild(), "detail");
        }
    }

    @Test
    public void reusedNestedValueAndKeyLabelsKeepTheirAttachmentBoundaries() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<?> graph = manager.getEntityGraph("DngReusedDeep");
            Subgraph<?> left = subgraph(graph, "child", DistributedGraphChild.class);
            Subgraph<?> right = subgraph(graph, "otherChild", DistributedGraphChild.class);
            for (String attribute : List.of("detail", "details")) {
                assertEquals(Set.of("id", "label"), names(subgraph(left, attribute, DistributedGraphDetail.class)));
                assertEquals(Set.of("label"), names(subgraph(right, attribute, DistributedGraphDetail.class)));
            }
            assertEquals(Set.of("id", "label"), names(left.getAttributeNode("details").getKeySubgraphs().get(DistributedGraphDetail.class)));
            Subgraph<?> rightKey = right.getAttributeNode("details").getKeySubgraphs().get(DistributedGraphDetail.class);
            assertEquals(Set.of("label"), names(rightKey));
            subgraph(right, "details", DistributedGraphDetail.class).addAttributeNode("id");
            assertEquals(Set.of("label"), names(rightKey));
        }
    }

    @Test
    public void distributedCopiesPreserveBothSubclassGraphs() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<?> standalone = manager.getEntityGraph("DngSubclass");
            Set<Class<?>> expected = Set.of(DistributedGraphSpecialChild.class, DistributedGraphOtherChild.class);
            assertEquals(expected, ((EntityGraphImpl<?>) standalone).getAttributeGroup().getSubClassGroups().keySet());
            for (int id : List.of(2, 3)) {
                manager.clear();
                EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngDistributedSubclass");
                Subgraph<?> childGraph = subgraph(graph, "child", DistributedGraphChild.class);
                // Association conversion includes the declared base alongside both treated branches.
                Set<Class<?>> associationTypes = Set.of(DistributedGraphChild.class, DistributedGraphSpecialChild.class,
                        DistributedGraphOtherChild.class);
                assertEquals(associationTypes, graph.getAttributeNode("child").getSubgraphs().keySet());
                assertEquals(associationTypes, ((EntityGraphImpl<?>) childGraph).getAttributeGroup().getSubClassGroups().keySet());
                childGraph.addAttributeNode("detail");
                assertFalse(subgraph(manager.getEntityGraph("DngDistributedSubclass"), "child", DistributedGraphChild.class).hasAttributeNode("detail"));
                assertFalse(manager.getEntityGraph("DngSubclass").hasAttributeNode("detail"));
                graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngDistributedSubclass");
                DistributedGraphRoot root = manager.find(graph, id);
                assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "child"));
                DistributedGraphChild child = root.getChild();
                String attribute = id == 2 ? "special" : "other";
                assertTrue(fixture.getPersistenceUnitUtil().isLoaded(child, attribute));
                assertEquals(id == 2 ? "special-value" : "other-value", id == 2
                        ? ((DistributedGraphSpecialChild) child).getSpecial() : ((DistributedGraphOtherChild) child).getOther());
            }
        }
    }

    @Test
    public void lazyAndOptionlessNestedGraphsComposeInEitherOrder() throws Exception {
        for (String name : List.of("DngLazyFirst", "DngLazyLast")) {
            try (EntityManager manager = fixture.createEntityManager()) {
                EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, name);
                Subgraph<?> child = subgraph(graph, "child", DistributedGraphChild.class);
                assertTrue(name, child.getAttributeNode("detail").getOptions().contains(FetchType.LAZY));
                assertFalse(name, ((EntityGraphImpl<?>) child).getAttributeGroup().containsAttribute("detail"));
                for (String hint : List.of("jakarta.persistence.loadgraph", "jakarta.persistence.fetchgraph")) {
                    manager.clear();
                    DistributedGraphRoot root = manager.find(DistributedGraphRoot.class, 1, Map.of(hint, graph));
                    assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "child"));
                    assertLazy(root.getChild(), "detail");
                }
            }
        }
    }

    @Test
    public void recursiveAssociationValueAndKeyTypesPreserveBaseAndSubtypeInEitherOrder() {
        for (String name : List.of("DngDeepDefaultFirst", "DngDeepSubtypeFirst", "DngDeepExplicitFirst", "DngDeepExplicitLast")) {
            try (EntityManager manager = fixture.createEntityManager()) {
                EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, name);
                Subgraph<?> child = subgraph(graph, "child", DistributedGraphChild.class);
                String baseAttribute = name.contains("Explicit") ? "id" : "label";
                for (String attribute : List.of("detail", "details")) {
                    assertEquals(name + ": " + attribute, Set.of(DistributedGraphDetail.class, DistributedGraphSpecialDetail.class),
                            child.getAttributeNode(attribute).getSubgraphs().keySet());
                    assertEquals(Set.of(baseAttribute), names(subgraph(child, attribute, DistributedGraphDetail.class)));
                    assertEquals(Set.of("specialDetail"), names(subgraph(child, attribute, DistributedGraphSpecialDetail.class)));
                }
                Map<?, ? extends Subgraph<?>> keys = child.getAttributeNode("details").getKeySubgraphs();
                assertEquals(Set.of(DistributedGraphDetail.class, DistributedGraphSpecialDetail.class), keys.keySet());
                assertEquals(Set.of(baseAttribute), names(keys.get(DistributedGraphDetail.class)));
                assertEquals(Set.of("specialDetail"), names(keys.get(DistributedGraphSpecialDetail.class)));
                for (int id : List.of(1, 4)) {
                    manager.clear();
                    DistributedGraphRoot root = manager.find(graph, id);
                    assertTrue(fixture.getPersistenceUnitUtil().isLoaded(root, "child"));
                    DistributedGraphChild value = root.getChild();
                    assertTrue(fixture.getPersistenceUnitUtil().isLoaded(value, "detail"));
                    assertTrue(fixture.getPersistenceUnitUtil().isLoaded(value, "details"));
                    assertEquals(id == 4 ? DistributedGraphSpecialDetail.class : DistributedGraphDetail.class, value.getDetail().getClass());
                    assertEquals(1, value.getDetails().size());
                    for (DistributedGraphDetail detail : List.of(value.getDetail(), value.getDetails().keySet().iterator().next(),
                            value.getDetails().values().iterator().next())) {
                        if (id == 4) {
                            assertTrue(fixture.getPersistenceUnitUtil().isLoaded(detail, "specialDetail"));
                            assertEquals("specialDetail-value", ((DistributedGraphSpecialDetail) detail).getSpecialDetail());
                        } else {
                            assertEquals("detail", detail.getLabel());
                        }
                    }
                }
            }
        }
    }

    @Test
    public void recursiveDefaultAndExplicitBaseTypesComposeInEitherOrder() {
        try (EntityManager manager = fixture.createEntityManager()) {
            for (String name : List.of("DngDeepBaseFirst", "DngDeepDefaultLast")) {
                Subgraph<?> child = subgraph(manager.getEntityGraph(name), "child", DistributedGraphChild.class);
                for (String attribute : List.of("detail", "details")) {
                    assertEquals(Set.of(DistributedGraphDetail.class), child.getAttributeNode(attribute).getSubgraphs().keySet());
                    assertEquals(Set.of("id", "label"), names(subgraph(child, attribute, DistributedGraphDetail.class)));
                }
                assertEquals(Set.of(DistributedGraphDetail.class), child.getAttributeNode("details").getKeySubgraphs().keySet());
                assertEquals(Set.of("id", "label"), names(child.getAttributeNode("details").getKeySubgraphs().get(DistributedGraphDetail.class)));
            }
        }
    }

    @Test
    public void graphFindPreservesManagedIdentityAndPendingChanges() throws Exception {
        try (EntityManager manager = fixture.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                DistributedGraphRoot plain = manager.find(DistributedGraphRoot.class, 1);
                assertLazy(plain, "child");
                plain.setLabel("pending-local");
                assertSame(plain, manager.find(DistributedGraphRoot.class, 1));
                assertLazy(plain, "child");
                DistributedGraphRoot fetched = manager.find(manager.getEntityGraph(DistributedGraphRoot.class, "DngRoot"), 1);
                assertSame(plain, fetched);
                assertTrue(manager.contains(fetched));
                assertEquals("pending-local", fetched.getLabel());
                assertFetchedPath(fetched);
            } finally {
                manager.getTransaction().rollback();
            }
        }
        try (EntityManager manager = fixture.createEntityManager()) {
            assertEquals("root-1", manager.find(DistributedGraphRoot.class, 1).getLabel());
        }
    }

    @Test
    public void graphFindPreservesMissingAndRemovedLookupBehavior() {
        try (EntityManager manager = fixture.createEntityManager()) {
            EntityGraph<DistributedGraphRoot> graph = manager.getEntityGraph(DistributedGraphRoot.class, "DngRoot");
            assertNull(manager.find(DistributedGraphRoot.class, 9999));
            assertNull(manager.find(graph, 9999));
            manager.getTransaction().begin();
            try {
                DistributedGraphRoot root = manager.find(DistributedGraphRoot.class, 2);
                manager.remove(root);
                assertNull(manager.find(DistributedGraphRoot.class, 2));
                assertNull(manager.find(graph, 2));
            } finally {
                manager.getTransaction().rollback();
            }
        }
    }

    @Test
    public void metadataCompleteSubclassRetainsEntityParentFetches() {
        try (EntityManagerFactory factory = configuration("entity-parent-complete")
                .mappingFile(RESOURCES + "inherited-complete-orm.xml").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            EntityGraph<DistributedGraphSpecialChild> graph = manager.getEntityGraph(DistributedGraphSpecialChild.class, "DngInherited");
            assertEquals(Set.of("detail", "special"), names(graph));
            assertEquals(Set.of("label"), names(subgraph(graph, "detail", DistributedGraphDetail.class)));
            assertTrue(graph.getAttributeNode("detail").getOptions().containsAll(
                    Set.of(FetchType.EAGER, CacheRetrieveMode.BYPASS, CacheStoreMode.BYPASS, new BatchFetch(19))));
            assertSuppressedOptions(graph.getAttributeNode("special"), 13);
            DistributedGraphSpecialChild child = manager.find(graph, 2);
            assertTrue(factory.getPersistenceUnitUtil().isLoaded(child, "detail"));
            assertEquals("detail", child.getDetail().getLabel());
        }
    }

    @Test
    public void metadataCompleteSubclassRetainsMappedSuperclassFetches() {
        try (EntityManagerFactory factory = configuration("mapped-parent-complete")
                .mappingFile(RESOURCES + "inherited-complete-orm.xml").createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            EntityGraph<PropertyGraphRoot> graph = manager.getEntityGraph(PropertyGraphRoot.class, "DngProperty");
            assertEquals(Set.of("inheritedTarget", "target"), names(graph));
            assertEquals(Set.of("label"), names(subgraph(graph, "inheritedTarget", DistributedGraphDetail.class)));
            assertTrue(graph.getAttributeNode("inheritedTarget").getOptions().containsAll(
                    Set.of(FetchType.EAGER, CacheRetrieveMode.BYPASS, CacheStoreMode.BYPASS, new BatchFetch(17))));
            assertSuppressedOptions(graph.getAttributeNode("target"), 11);
            PropertyGraphRoot property = manager.find(graph, 1);
            assertTrue(factory.getPersistenceUnitUtil().isLoaded(property, "inheritedTarget"));
            assertEquals("detail", property.getInheritedTarget().getLabel());
        }
    }

    @Test
    public void persistenceUnitMetadataCompleteSuppressesAllFetchAnnotations() {
        PersistenceConfiguration configuration = new PersistenceConfiguration(identity("pu-complete"))
                .provider(PersistenceProvider.class.getName())
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .validationMode(ValidationMode.NONE)
                .sharedCacheMode(SharedCacheMode.NONE)
                .properties(properties())
                .mappingFile(RESOURCES + "pu-complete-orm.xml");
        List.of(DistributedGraphChild.class, DistributedGraphSpecialChild.class, DistributedGraphDetail.class,
                PropertyGraphBase.class, PropertyGraphRoot.class).forEach(configuration::managedClass);
        try (EntityManagerFactory factory = configuration.createEntityManagerFactory();
                EntityManager manager = factory.createEntityManager()) {
            EntityGraph<?> inherited = manager.getEntityGraph("DngInherited");
            assertEquals(Set.of("detail", "special"), names(inherited));
            assertSuppressedOptions(inherited.getAttributeNode("detail"), 19);
            assertSuppressedOptions(inherited.getAttributeNode("special"), 13);
            EntityGraph<?> property = manager.getEntityGraph("DngProperty");
            assertEquals(Set.of("inheritedTarget", "target"), names(property));
            assertSuppressedOptions(property.getAttributeNode("inheritedTarget"), 17);
            assertSuppressedOptions(property.getAttributeNode("target"), 11);
        }
    }

    private static void assertSuppressedOptions(AttributeNode<?> node, int batchSize) {
        assertTrue(node.getSubgraphs().isEmpty());
        assertTrue(node.getOptions().contains(FetchType.EAGER));
        assertFalse(node.getOptions().contains(CacheRetrieveMode.BYPASS));
        assertFalse(node.getOptions().contains(CacheStoreMode.BYPASS));
        assertFalse(node.getOptions().contains(new BatchFetch(batchSize)));
    }

    static PersistenceConfiguration configuration(String kind) {
        return configuration(kind, false);
    }

    private static PersistenceConfiguration configuration(String kind, boolean reverse) {
        PersistenceConfiguration configuration = new PersistenceConfiguration(identity(kind))
                .provider(PersistenceProvider.class.getName())
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .validationMode(ValidationMode.NONE)
                .sharedCacheMode(SharedCacheMode.NONE)
                .properties(properties());
        List<Class<?>> models = new ArrayList<>(MODELS);
        if (reverse) {
            Collections.reverse(models);
        }
        models.forEach(configuration::managedClass);
        return configuration;
    }

    private static void assertFetchedPath(DistributedGraphRoot root) {
        assertNotNull(root);
        assertTrue("Inspect loaded state before navigating child", fixture.getPersistenceUnitUtil().isLoaded(root, "child"));
        DistributedGraphChild child = root.getChild();
        assertTrue("Inspect loaded state before navigating detail", fixture.getPersistenceUnitUtil().isLoaded(child, "detail"));
        assertEquals("detail", child.getDetail().getLabel());
    }

    private static void assertHolder(Class<?> type, String attribute) throws Exception {
        assertTrue("Real static weaving required: " + type.getName() + "." + attribute,
                ValueHolderInterface.class.isAssignableFrom(holderField(type, attribute).getType()));
    }

    private static void assertLazy(Object owner, String attribute) throws Exception {
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) session(fixture).getDescriptor(owner.getClass())
                .getMappingForAttributeName(attribute);
        assertTrue("Named Fetch must not change mapping defaults", mapping.isLazy());
        assertTrue(mapping.usesIndirection());
        assertUnloaded(owner, attribute);
    }

    private static void assertUnloaded(Object owner, String attribute) throws Exception {
        assertFalse("Attribute must remain unloaded: " + attribute,
                fixture.getPersistenceUnitUtil().isLoaded(owner, attribute));
        Field field = holderField(owner.getClass(), attribute);
        field.setAccessible(true);
        Object holder = field.get(owner);
        if (holder == null) {
            assertTrue("A missing holder requires a woven partial object", owner instanceof FetchGroupTracker);
            assertFalse("The partial object's fetch group must exclude " + attribute,
                    ((FetchGroupTracker) owner)._persistence_isAttributeFetched(attribute));
        } else {
            assertTrue(holder instanceof ValueHolderInterface<?>);
            assertFalse(((ValueHolderInterface<?>) holder).isInstantiated());
        }
    }

    private static boolean exists(String name) {
        TableDefinition table = new TableDefinition();
        table.setName(name);
        return schema.checkTableExists(table);
    }

    private static Field holderField(Class<?> type, String attribute) throws NoSuchFieldException {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField("_persistence_" + attribute + "_vh");
            } catch (NoSuchFieldException ignored) {
                // Both entity and mapped-superclass attributes may be inherited.
            }
        }
        throw new NoSuchFieldException(type.getName() + "." + attribute + " has no lazy value holder");
    }

    private static String identity(String kind) {
        return "distributed-named-graphs-" + kind + "-" + IDENTITIES.incrementAndGet();
    }

    private static Set<String> names(Graph<?> graph) {
        return graph.getAttributeNodes().stream().map(AttributeNode::getAttributeName).collect(Collectors.toSet());
    }

    private static Map<String, Object> properties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        properties.put(PersistenceUnitProperties.VALIDATION_MODE, "NONE");
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        for (String name : List.of(PersistenceUnitProperties.JDBC_DRIVER, PersistenceUnitProperties.JDBC_URL,
                PersistenceUnitProperties.JDBC_USER, PersistenceUnitProperties.JDBC_PASSWORD,
                PersistenceUnitProperties.TARGET_DATABASE)) {
            String value = System.getProperty(name);
            if (value != null) {
                properties.put(name, value);
            }
        }
        return properties;
    }

    private static ServerSession session(EntityManagerFactory factory) {
        return factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
    }

    private static Subgraph<?> subgraph(Graph<?> graph, String attribute, Class<?> type) {
        Subgraph<?> subgraph = graph.getAttributeNode(attribute).getSubgraphs().get(type);
        assertNotNull(attribute + " must retain typed branch " + type.getName(), subgraph);
        assertEquals(type, subgraph.getClassType());
        return subgraph;
    }
}
