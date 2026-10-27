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
import jakarta.persistence.Statement;
import jakarta.persistence.TypedQueryReference;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Selection;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorCustomizer;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCode;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryCodeConverter;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryGroup;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryProjection;
import org.eclipse.persistence.jpa.entityagent.model.AgentQueryRecord;
import org.eclipse.persistence.jpa.test.criteria.model.CriteriaCar;
import org.eclipse.persistence.jpa.test.criteria.model.CriteriaCarId;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(EmfRunner.class)
public class TestJPQLCriteriaResultTypes {
    private static final String TITLES = "select r.title as caption from AgentQueryRecord r order by r.id";
    private static final String PROJECTION = "select new org.eclipse.persistence.jpa.entityagent.model.AgentQueryProjection(r) "
            + "from AgentQueryRecord r where r.id = :id";

    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {AgentQueryRecord.class, AgentQueryGroup.class, AgentQueryCodeConverter.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "eclipselink.descriptor.customizer.AgentQueryRecord",
                            value = "org.eclipse.persistence.jpa.entityagent.TestJPQLCriteriaResultTypes$QueryKeyCustomizer"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory emf;

    @Emf(name = "criteria-map-metadata", createTables = DDLGen.NONE,
            classes = {CriteriaCar.class, CriteriaCarId.class},
            properties = {@Property(name = "eclipselink.weaving", value = "false"),
                    @Property(name = "eclipselink.ddl-generation", value = "none"),
                    @Property(name = "jakarta.persistence.validation.mode", value = "NONE")})
    private EntityManagerFactory mapEmf;

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
            em.createStatement("delete from AgentQueryRecord").execute();
            em.createStatement("delete from AgentQueryGroup").execute();
        });
        emf.getCache().evictAll();
    }

    @Test
    public void entityManagerScalarImportsExposeDomainTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            assertScalarImports(em);
        }
    }

    @Test
    public void entityAgentScalarImportsExposeDomainTypes() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertScalarImports(agent);
        }
    }

    @Test
    public void entityManagerEntityAndMultipleSelectionsRetainTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            assertEntityAndMultipleSelections(em);
        }
    }

    @Test
    public void entityAgentEntityAndMultipleSelectionsRetainTypes() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertEntityAndMultipleSelections(agent);
        }
    }

    @Test
    public void entityManagerTypedAndConstructorImportsRetainTypes() {
        try (EntityManager em = emf.createEntityManager()) {
            assertTypedAndConstructorImports(em);
        }
    }

    @Test
    public void entityAgentTypedAndConstructorImportsRetainTypes() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertTypedAndConstructorImports(agent);
        }
    }

    @Test
    public void entityManagerImportedCriteriaRemainEditableAndIndependent() {
        try (EntityManager em = emf.createEntityManager()) {
            assertEditableImports(em);
        }
    }

    @Test
    public void entityAgentImportedCriteriaRemainEditableAndIndependent() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertEditableImports(agent);
        }
    }

    @Test
    public void entityManagerAugmentedScalarReferenceRemainsIndependent() {
        try (EntityManager em = emf.createEntityManager()) {
            assertAugmentedReference(em);
        }
    }

    @Test
    public void entityAgentAugmentedScalarReferenceRemainsIndependent() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertAugmentedReference(agent);
        }
    }

    @Test
    public void entityManagerUntypedAndTypedStatementImportsRemainUsable() {
        try (EntityManager em = emf.createEntityManager()) {
            assertStatementImports(em);
        }
    }

    @Test
    public void entityAgentUntypedAndTypedStatementImportsRemainUsable() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertStatementImports(agent);
        }
    }

    @Test
    public void entityManagerMapMetadataDoesNotUseValueType() {
        try (EntityManager em = mapEmf.createEntityManager()) {
            assertMapMetadata(em);
        }
    }

    @Test
    public void entityAgentMapMetadataDoesNotUseValueType() {
        try (EntityAgent agent = mapEmf.createEntityAgent()) {
            assertMapMetadata(agent);
        }
    }

    @Test
    public void entityManagerUnmappedQueryKeyRetainsUnknownType() {
        try (EntityManager em = emf.createEntityManager()) {
            assertUnmappedQueryKey(em);
        }
    }

    @Test
    public void entityAgentUnmappedQueryKeyRetainsUnknownType() {
        try (EntityAgent agent = emf.createEntityAgent()) {
            assertUnmappedQueryKey(agent);
        }
    }

    @Test
    public void metadataInspectionDoesNotExecuteSqlOrRegisterQueries() {
        TypedQueryReference<String> original;
        try (EntityManager em = emf.createEntityManager()) {
            original = emf.addNamedQuery("EAQuery.criteriaMetadata", em.createQuery(TITLES, String.class));
        }
        var session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        Set<String> names = Set.copyOf(emf.getNamedQueries(Object.class).keySet());
        int registered = session.getQueries().values().stream().mapToInt(List::size).sum();
        AtomicInteger calls = new AtomicInteger();
        SessionEventAdapter listener = new SessionEventAdapter() {
            @Override
            public void preExecuteCall(SessionEvent event) {
                calls.incrementAndGet();
            }
        };
        session.getEventManager().addListener(listener);
        try {
            CriteriaBuilder builder = emf.getCriteriaBuilder();
            for (int index = 0; index < 3; index++) {
                assertTypes(builder.createQuery(TITLES), String.class, String.class);
                assertTypes(builder.createQuery(PROJECTION), AgentQueryProjection.class, AgentQueryProjection.class);
                TypedQueryReference<String> augmented = builder.augment(original, query ->
                        assertTypes(query, String.class, String.class));
                assertEquals(String.class, augmented.getResultType());
            }
            assertEquals(names, emf.getNamedQueries(Object.class).keySet());
            assertEquals(registered, session.getQueries().values().stream().mapToInt(List::size).sum());
            assertEquals("Metadata inspection must not execute a database call", 0, calls.get());
            try (EntityManager em = emf.createEntityManager()) {
                assertEquals(List.of("Alpha", "Beta", "Gamma"), em.createQuery(TITLES, String.class).getResultList());
            }
            assertTrue("The listener must observe an actual query execution", calls.get() > 0);
        } finally {
            session.getEventManager().removeListener(listener);
        }
    }

    public static class QueryKeyCustomizer implements DescriptorCustomizer {
        @Override
        public void customize(ClassDescriptor descriptor) {
            descriptor.addDirectQueryKey("queryTitle", "TITLE");
        }
    }

    private void assertUnmappedQueryKey(EntityHandler handler) {
        var descriptor = emf.unwrap(JpaEntityManagerFactory.class).getServerSession()
                .getDescriptor(AgentQueryRecord.class);
        assertNull(descriptor.getMappingForAttributeName("queryTitle"));
        assertTrue(descriptor.getQueryKeyNamed("queryTitle").isDirectQueryKey());
        String jpql = "select r.queryTitle from AgentQueryRecord r where r.id = :id";
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        CriteriaQuery<?> untyped = builder.createQuery(jpql);
        assertTypes(untyped, Object.class, Object.class);
        assertEquals("Alpha", handler.createQuery(untyped).setParameter("id", 1).getSingleResult());
        CriteriaQuery<String> typed = builder.createQuery(String.class, jpql);
        assertTypes(typed, String.class, Object.class);
        assertEquals("Beta", handler.createQuery(typed).setParameter("id", 2).getSingleResult());
    }

    private void assertMapMetadata(EntityHandler handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        String from = " from CriteriaCar car join car.origin origin";
        // Unresolved entry metadata must not be inferred from the map value mapping.
        assertTypes(builder.createQuery("select ENTRY(origin)" + from), Object.class, Object.class);
        assertTypes(builder.createQuery("select KEY(origin)" + from), String.class, String.class);
        assertTypes(builder.createQuery(String.class, "select KEY(origin)" + from), String.class, String.class);
        assertTypes(builder.createQuery("select VALUE(origin)" + from), String.class, String.class);
    }

    private void assertScalarImports(EntityHandler handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        CriteriaQuery<?> title = builder.createQuery(
                "select r.title as caption from AgentQueryRecord r where r.id = :id");
        assertTypes(title, String.class, String.class);
        assertEquals("caption", title.getSelection().getAlias());
        assertEquals(1, title.getParameters().size());
        assertEquals("id", title.getParameters().iterator().next().getName());
        assertEquals("Alpha", handler.createQuery(title).setParameter("id", 1).getSingleResult());

        CriteriaQuery<?> id = builder.createQuery("select r.id from AgentQueryRecord r where r.id = :id");
        assertTypes(id, Integer.class, Integer.class);
        assertEquals(2, handler.createQuery(id).setParameter("id", 2).getSingleResult());

        CriteriaQuery<?> code = builder.createQuery("select r.code from AgentQueryRecord r where r.id = :id");
        assertTypes(code, AgentQueryCode.class, AgentQueryCode.class);
        assertEquals(new AgentQueryCode("Gamma"), handler.createQuery(code).setParameter("id", 3).getSingleResult());
    }

    private void assertEntityAndMultipleSelections(EntityHandler handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        CriteriaQuery<?> entity = builder.createQuery("select r from AgentQueryRecord r where r.id = :id");
        assertTypes(entity, AgentQueryRecord.class, AgentQueryRecord.class);
        assertEquals(Integer.valueOf(1), ((AgentQueryRecord) handler.createQuery(entity)
                .setParameter("id", 1).getSingleResult()).id);

        CriteriaQuery<?> multiple = builder.createQuery(
                "select r.title as caption, r.id, r.code from AgentQueryRecord r where r.id = :id");
        assertTypes(multiple, Object[].class, Object[].class);
        List<Selection<?>> items = multiple.getSelection().getCompoundSelectionItems();
        assertEquals(List.of(String.class, Integer.class, AgentQueryCode.class),
                items.stream().map(Selection::getJavaType).toList());
        assertEquals("caption", items.get(0).getAlias());
        assertArrayEquals(new Object[]{"Alpha", 1, new AgentQueryCode("Alpha")},
                (Object[]) handler.createQuery(multiple).setParameter("id", 1).getSingleResult());
    }

    private void assertTypedAndConstructorImports(EntityHandler handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        CriteriaQuery<Object> typed = builder.createQuery(Object.class,
                "select r.title from AgentQueryRecord r where r.id = :id");
        assertTypes(typed, Object.class, String.class);
        assertEquals("Beta", handler.createQuery(typed).setParameter("id", 2).getSingleResult());

        CriteriaQuery<?> constructor = builder.createQuery(PROJECTION);
        assertTypes(constructor, AgentQueryProjection.class, AgentQueryProjection.class);
        assertEquals(AgentQueryRecord.class,
                constructor.getSelection().getCompoundSelectionItems().get(0).getJavaType());
        AgentQueryProjection projection = (AgentQueryProjection) handler.createQuery(constructor)
                .setParameter("id", 3).getSingleResult();
        assertEquals(Integer.valueOf(3), projection.record().id);
        assertEquals("Gamma", projection.record().title);
    }

    private void assertEditableImports(EntityHandler handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        String jpql = "select r.title as caption from AgentQueryRecord r where r.id = 1 order by r.id";
        CriteriaQuery<?> edited = builder.createQuery(jpql);
        CriteriaQuery<?> untouched = builder.createQuery(jpql);
        assertNotSame(edited.getSelection(), untouched.getSelection());
        assertTypes(edited, String.class, String.class);
        assertEquals(1, edited.getOrderList().size());
        edited.where(builder.equal(edited.getRoots().iterator().next().get("id"), 2));
        assertEquals("Beta", handler.createQuery(edited).getSingleResult());
        assertEquals("Alpha", handler.createQuery(untouched).getSingleResult());
        assertEquals("Alpha", handler.createQuery(builder.createQuery(jpql)).getSingleResult());

        CriteriaQuery<String> replaced = builder.createQuery(String.class, jpql);
        var root = replaced.getRoots().iterator().next();
        replaced.select(builder.upper(root.get("title")));
        replaced.where(builder.equal(root.get("id"), 3));
        assertTypes(replaced, String.class, String.class);
        assertEquals("GAMMA", handler.createQuery(replaced).getSingleResult());
        assertEquals("Alpha", handler.createQuery(untouched).getSingleResult());
    }

    private void assertAugmentedReference(EntityHandler handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        TypedQueryReference<String> original = emf.addNamedQuery("EAQuery.criteriaScalar",
                handler.createQuery(TITLES, String.class));
        TypedQueryReference<String> augmented = builder.augment(original, query -> {
            assertTypes(query, String.class, String.class);
            query.where(builder.equal(query.getRoots().iterator().next().get("id"), 2));
        });
        assertEquals(String.class, augmented.getResultType());
        assertEquals("Beta", handler.createQuery(augmented).getSingleResult());
        assertEquals(List.of("Alpha", "Beta", "Gamma"), handler.createQuery(original).getResultList());
        assertEquals(List.of("Alpha", "Beta", "Gamma"), handler.createQuery(
                emf.getNamedQueries(String.class).get(original.getName())).getResultList());
    }

    private void assertStatementImports(EntityHandler handler) {
        CriteriaBuilder builder = handler.getCriteriaBuilder();
        CriteriaQuery<?> select = builder.createQuery("select r.title from AgentQueryRecord r where r.id = :id");
        assertTypes(select, String.class, String.class);
        assertEquals("Alpha", handler.createQuery(select).setParameter("id", 1).getSingleResult());
        var update = builder.createCriteriaUpdate(
                "update AgentQueryRecord r set r.title = :title where r.id = :id");
        assertEquals(AgentQueryRecord.class, update.getRoot().getJavaType());
        assertEquals(2, update.getParameters().size());
        assertEquals(1, execute(handler, handler.createStatement(update).setParameter("title", "Untyped").setParameter("id", 2)));
        assertEquals(List.of("Alpha", "Untyped", "Gamma"), handler.createQuery(TITLES, String.class).getResultList());

        var delete = builder.createCriteriaDelete("delete from AgentQueryRecord r where r.id < :id");
        assertEquals(AgentQueryRecord.class, delete.getRoot().getJavaType());
        assertEquals(1, delete.getParameters().size());
        assertEquals(2, execute(handler, handler.createStatement(delete).setParameter("id", 3)));
        assertEquals(List.of(3), handler.createQuery("select r.id from AgentQueryRecord r order by r.id", Integer.class).getResultList());

        var typedUpdate = builder.createCriteriaUpdate(AgentQueryRecord.class,
                "update AgentQueryRecord r set r.title = :title where r.id = 3");
        assertEquals(1, execute(handler, handler.createStatement(typedUpdate).setParameter("title", "Typed")));
        assertEquals(List.of("Typed"), handler.createQuery(TITLES, String.class).getResultList());
        var typedDelete = builder.createCriteriaDelete(AgentQueryRecord.class,
                "delete from AgentQueryRecord r where r.id = 3");
        assertEquals(1, execute(handler, handler.createStatement(typedDelete)));
        assertEquals(List.of(), handler.createQuery(TITLES, String.class).getResultList());

        assertThrows(IllegalArgumentException.class, () -> builder.createQuery("delete from AgentQueryRecord"));
        assertThrows(IllegalArgumentException.class, () -> builder.createCriteriaUpdate("select r from AgentQueryRecord r"));
        assertThrows(IllegalArgumentException.class, () -> builder.createCriteriaDelete("select r from AgentQueryRecord r"));
        assertThrows(IllegalArgumentException.class, () -> builder.createCriteriaUpdate(AgentQueryGroup.class,
                "update AgentQueryRecord r set r.title = 'Wrong'"));
        assertThrows(IllegalArgumentException.class, () -> builder.createCriteriaDelete(AgentQueryGroup.class,
                "delete from AgentQueryRecord r"));
    }

    private int execute(EntityHandler handler, Statement statement) {
        var transaction = handler.getTransaction();
        transaction.begin();
        try {
            int count = statement.execute();
            transaction.commit();
            return count;
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    private void assertTypes(CriteriaQuery<?> query, Class<?> resultType, Class<?> selectionType) {
        assertEquals(resultType, query.getResultType());
        assertEquals(selectionType, query.getSelection().getJavaType());
    }
}
