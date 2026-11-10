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
package org.eclipse.persistence.jpa.test.criteria;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import jakarta.persistence.EntityHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

import org.eclipse.persistence.internal.expressions.DataExpression;
import org.eclipse.persistence.internal.jpa.querydef.InternalSelection;
import org.eclipse.persistence.jpa.test.criteria.model.SnapshotCriteriaDepartment;
import org.eclipse.persistence.jpa.test.criteria.model.SnapshotCriteriaEmployee;
import org.eclipse.persistence.jpa.test.criteria.model.SnapshotCriteriaProject;
import org.eclipse.persistence.jpa.test.framework.DDLGen;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/** Completed queries own their builders and fetch graph independently of reusable Criteria sources. */
@RunWith(EmfRunner.class)
public class TestCriteriaQuerySnapshots {
    @Emf(createTables = DDLGen.DROP_CREATE,
            classes = {SnapshotCriteriaEmployee.class, SnapshotCriteriaDepartment.class, SnapshotCriteriaProject.class},
            properties = @Property(name = "eclipselink.cache.shared.default", value = "false"))
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        emf.runInTransaction(em -> {
            SnapshotCriteriaDepartment department = new SnapshotCriteriaDepartment(1, "Engineering");
            SnapshotCriteriaEmployee alice = new SnapshotCriteriaEmployee(1, "Alice", 120);
            alice.department = department;
            alice.tags.addAll(List.of("blue", "green"));
            SnapshotCriteriaEmployee bob = new SnapshotCriteriaEmployee(2, "Bob", 250);
            bob.manager = alice;
            em.persist(department);
            em.persist(alice);
            em.persist(bob);
            em.persist(new SnapshotCriteriaProject(10, "Alpha", alice));
            em.persist(new SnapshotCriteriaProject(20, "Beta", bob));
        });
    }

    @After
    public void cleanup() {
        emf.runInTransaction(em -> {
            em.createNativeQuery("UPDATE JPA_CQS_EMPLOYEE SET MANAGER_ID = NULL").executeUpdate();
            for (String table : List.of("JPA_CQS_TAG", "JPA_CQS_PROJECT", "JPA_CQS_SALARY", "JPA_CQS_EMPLOYEE", "JPA_CQS_DEPARTMENT")) {
                em.createNativeQuery("DELETE FROM " + table).executeUpdate();
            }
        });
    }

    @Test
    public void testMappedAndExplicitFetchesRetainExactRowsAcrossQueryCreation() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<SnapshotCriteriaEmployee> criteria = cb.createQuery(SnapshotCriteriaEmployee.class);
            Root<SnapshotCriteriaEmployee> employee = criteria.from(SnapshotCriteriaEmployee.class);
            employee.alias("employees");
            var tags = employee.fetch("tags", JoinType.LEFT);
            ParameterExpression<Integer> minimum = cb.parameter(Integer.class, "minimum");
            criteria.select(employee).where(cb.ge(employee.<Integer>get("id"), minimum)).orderBy(cb.asc(employee.get("id")));
            var source = ((InternalSelection) employee).getCurrentNode();
            for (int creation = 0; creation < 3; creation++) {
                TypedQuery<SnapshotCriteriaEmployee> query = handler.createQuery(criteria);
                assertSame(minimum, query.getParameter("minimum", Integer.class));
                List<SnapshotCriteriaEmployee> rows = query.setParameter(minimum, 1).getResultList();
                // Two tag rows for Alice and one null-extended row for Bob, without DISTINCT.
                assertEmployees(rows, List.of(1, 1, 2), true);
                assertEquals(List.of(2), query.setParameter(minimum, 2).getResultList().stream().map(value -> value.id).toList());
                assertSame(source, ((InternalSelection) employee).getCurrentNode());
                assertFalse(((DataExpression) source).hasBeenNormalized());
                assertEquals(Set.of(employee), criteria.getRoots());
                assertEquals(Set.of(tags), employee.getFetches());
                assertEquals("employees", employee.getAlias());
            }
        });
    }

    @Test
    public void testUnusedManagerPathDoesNotChangeLeftFetchSnapshots() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<SnapshotCriteriaEmployee> criteria = cb.createQuery(SnapshotCriteriaEmployee.class);
            Root<SnapshotCriteriaEmployee> employee = criteria.from(SnapshotCriteriaEmployee.class);
            Path<SnapshotCriteriaDepartment> unused = employee.<SnapshotCriteriaEmployee>get("manager").get("department");
            unused.alias("unusedDepartment");
            employee.fetch("manager", JoinType.LEFT);
            criteria.select(employee).orderBy(cb.asc(employee.get("id")));
            var source = ((InternalSelection) unused).getCurrentNode();
            for (int creation = 0; creation < 3; creation++) {
                List<SnapshotCriteriaEmployee> rows = handler.createQuery(criteria).getResultList();
                assertEmployees(rows, List.of(1, 2), false);
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(rows.get(0), "manager"));
                assertNull(rows.get(0).manager);
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(rows.get(1), "manager"));
                assertEquals(1, rows.get(1).manager.id);
                assertEquals("Alice", rows.get(1).manager.name);
                assertEquals(120, rows.get(1).manager.salary);
                assertSame(source, ((InternalSelection) unused).getCurrentNode());
                assertFalse(((DataExpression) source).hasBeenNormalized());
                assertEquals("unusedDepartment", unused.getAlias());
                assertEquals(Set.of(employee), criteria.getRoots());
            }
        });
    }

    @Test
    public void testSavedExecutableRetainsRestrictionAndFetchesAfterSourceMutation() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<SnapshotCriteriaEmployee> criteria = cb.createQuery(SnapshotCriteriaEmployee.class);
            Root<SnapshotCriteriaEmployee> employee = criteria.from(SnapshotCriteriaEmployee.class);
            employee.fetch("tags", JoinType.LEFT);
            ParameterExpression<Integer> minimum = cb.parameter(Integer.class, "minimum");
            ParameterExpression<Integer> selected = cb.parameter(Integer.class, "selected");
            criteria.select(employee).where(cb.ge(employee.<Integer>get("id"), minimum)).orderBy(cb.asc(employee.get("id")));
            TypedQuery<SnapshotCriteriaEmployee> original = handler.createQuery(criteria);
            employee.fetch("manager", JoinType.LEFT);
            criteria.where(cb.equal(employee.get("id"), selected));
            TypedQuery<SnapshotCriteriaEmployee> changed = handler.createQuery(criteria);
            assertEquals(Set.of(minimum), original.getParameters());
            assertEquals(Set.of(selected), changed.getParameters());
            assertSame(minimum, original.getParameter("minimum", Integer.class));
            assertSame(selected, changed.getParameter("selected", Integer.class));
            assertEmployees(original.setParameter(minimum, 1).getResultList(), List.of(1, 1, 2), true);
            assertEmployees(changed.setParameter(selected, 2).getResultList(), List.of(2), true);
            assertEmployees(original.getResultList(), List.of(1, 1, 2), true);
            assertEmployees(original.setParameter(minimum, 2).getResultList(), List.of(2), true);
            assertEmployees(changed.setParameter(selected, 1).getResultList(), List.of(1, 1), true);
            assertEmployees(handler.createQuery(criteria).setParameter(selected, 2).getResultList(), List.of(2), true);
        });
    }

    @Test
    public void testAlternateEntityProjectionKeepsSecondaryTableAndProjectRestriction() {
        withHandlers(handler -> {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<SnapshotCriteriaEmployee> criteria = cb.createQuery(SnapshotCriteriaEmployee.class);
            Root<SnapshotCriteriaProject> project = criteria.from(SnapshotCriteriaProject.class);
            Path<SnapshotCriteriaEmployee> leader = project.get("teamLeader");
            leader.alias("leader");
            ParameterExpression<String> name = cb.parameter(String.class, "projectName");
            criteria.select(leader).where(cb.equal(project.get("name"), name));
            var source = ((InternalSelection) project).getCurrentNode();
            TypedQuery<SnapshotCriteriaEmployee> original = handler.createQuery(criteria);
            for (int creation = 0; creation < 3; creation++) {
                TypedQuery<SnapshotCriteriaEmployee> query = creation == 0 ? original : handler.createQuery(criteria);
                assertSame(name, query.getParameter("projectName", String.class));
                assertEmployees(query.setParameter(name, "Alpha").getResultList(), List.of(1), false);
                assertEmployees(query.setParameter(name, "Beta").getResultList(), List.of(2), false);
                assertTrue(query.setParameter(name, "missing").getResultList().isEmpty());
                assertEquals(Set.of(project), criteria.getRoots());
                assertSame(source, ((InternalSelection) project).getCurrentNode());
                assertFalse(((DataExpression) source).hasBeenNormalized());
                assertEquals("leader", leader.getAlias());
            }
            criteria.where(cb.equal(project.get("id"), 20));
            TypedQuery<SnapshotCriteriaEmployee> changed = handler.createQuery(criteria);
            assertTrue(changed.getParameters().isEmpty());
            assertEmployees(changed.getResultList(), List.of(2), false);
            assertEmployees(original.setParameter(name, "Alpha").getResultList(), List.of(1), false);
        });
    }

    private void assertEmployees(List<SnapshotCriteriaEmployee> employees, List<Integer> expectedIds, boolean tagsFetched) {
        assertEquals(expectedIds, employees.stream().map(value -> value.id).toList());
        for (SnapshotCriteriaEmployee employee : employees) {
            assertEquals(employee.id == 1 ? "Alice" : "Bob", employee.name);
            assertEquals(employee.id == 1 ? 120 : 250, employee.salary);
            assertTrue(emf.getPersistenceUnitUtil().isLoaded(employee, "department"));
            if (employee.id == 1) {
                assertEquals(1, employee.department.id);
                assertEquals("Engineering", employee.department.name);
            } else {
                assertNull(employee.department);
            }
            if (tagsFetched) {
                assertTrue(emf.getPersistenceUnitUtil().isLoaded(employee, "tags"));
                assertEquals(employee.id == 1 ? Set.of("blue", "green") : Set.of(), employee.tags);
            }
        }
    }

    private void withHandlers(Consumer<EntityHandler> assertions) {
        try (EntityManager em = emf.createEntityManager()) {
            assertions.accept(em);
        }
        try (var agent = emf.createEntityAgent()) {
            assertions.accept(agent);
        }
    }
}
