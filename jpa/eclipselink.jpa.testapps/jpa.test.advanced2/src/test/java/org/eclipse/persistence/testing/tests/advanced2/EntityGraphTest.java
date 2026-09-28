/*
 * Copyright (c) 2013, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     01/28/2013-2.5 Gordon Yorke
//       - 397772: JPA 2.1 Entity Graph Support
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
package org.eclipse.persistence.testing.tests.advanced2;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import jakarta.persistence.Subgraph;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa21.advanced.Project;
import org.eclipse.persistence.testing.models.jpa21.advanced.Race;
import org.eclipse.persistence.testing.models.jpa21.advanced.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.Shoe;
import org.eclipse.persistence.testing.models.jpa21.advanced.ShoeTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityGraphTest extends JUnitTestCase {
    protected boolean m_reset = false;
    protected Map<Class<?>, Object> guaranteedIds = new HashMap<>();

    public EntityGraphTest() {}

    public EntityGraphTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "advanced2x";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityGraphTest");

        suite.addTest(new EntityGraphTest("testSimpleGraph"));
        suite.addTest(new EntityGraphTest("testEmbeddedFetchGroup"));
        suite.addTest(new EntityGraphTest("testEmbeddedFetchGroupRefresh"));
        suite.addTest(new EntityGraphTest("testsubclassSubgraphs"));
        suite.addTest(new EntityGraphTest("testMapKeyFetchGroupRefresh"));
        suite.addTest(new EntityGraphTest("testElementSubgraphOnList"));
        suite.addTest(new EntityGraphTest("testElementSubgraphOnMapValue"));
        suite.addTest(new EntityGraphTest("testKeySubgraphRejectsBasicKey"));
        suite.addTest(new EntityGraphTest("testNestedEmbeddedFetchGroup"));
        suite.addTest(new EntityGraphTest("testLoadGroup"));

        return suite;
    }

    @Override
    public void setUp() {
        m_reset = true;
        super.setUp();
        clearCache();
    }

    @Override
    public void tearDown() {
        if (m_reset) {
            m_reset = false;
        }

        super.tearDown();
    }

    /**
     * Tests a NamedStoredProcedureQuery using a positional parameter returning
     * a single result set.
     */
    public void testSimpleGraph() {
        EntityManager em = createEntityManager();

        Employee result = (Employee) em.createQuery("Select e from Employee e join treat(e.projects as LargeProject) p where p.executive is Not Null and e != p.executive").setHint(QueryHints.JPA_FETCH_GRAPH, em.getEntityGraph("Employee")).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertFalse("fetchgroup failed to be applied: department is loaded", util.isLoaded(result, "department"));
        assertTrue("Fetch Group was not applied: projects is not loaded", util.isLoaded(result, "projects"));
        for (Project project : result.getProjects()){
            assertFalse("fetchgroup failed to be applied : teamLeader is loaded", util.isLoaded(project, "teamLeader"));
            assertTrue("fetchgroup failed to be applied: properties is not loaded", util.isLoaded(project, "properties"));
            if (project instanceof LargeProject){
                assertTrue("Fetch Group was not applied: executive is not loaded", util.isLoaded(project, "executive"));
            }
        }
        closeEntityManager(em);
    }

    public void testsubclassSubgraphs(){
        EntityManager em = createEntityManager();
        EntityGraph<Project> employeeGraph = em.createEntityGraph(Project.class);
        employeeGraph.addTreatedSubgraph(LargeProject.class).addAttributeNodes("budget");
        employeeGraph.addAttributeNodes("description");
        List<Project> result = em.createQuery("Select p from Project p where type(p) = LargeProject", Project.class).setHint(QueryHints.JPA_FETCH_GRAPH, employeeGraph).getResultList();
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        for (Project project : result){
            assertFalse("Fetch Group was not applied", util.isLoaded(project, "name"));
            assertTrue("Fetch Group was not applied", util.isLoaded(project, "description"));
            assertTrue("Fetch Group was not applied", util.isLoaded(project, "budget"));
        }
    }

    public void testEmbeddedFetchGroup(){
        EntityManager em = createEntityManager();
        EntityGraph<Employee> employeeGraph = em.createEntityGraph(Employee.class);
        employeeGraph.addSubgraph("period").addAttributeNodes("startDate");
        Employee result = em.createQuery("Select e from Employee e", Employee.class).setMaxResults(1).setHint(QueryHints.JPA_FETCH_GRAPH, employeeGraph).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertFalse("FetchGroup was not applied", util.isLoaded(result, "department"));
        assertFalse("FetchGroup was not applied", util.isLoaded(result.getPeriod(), "endDate"));
        assertTrue("FetchGroup was not applied", util.isLoaded(result.getPeriod(), "startDate"));

        result.getPeriod().getEndDate();
        assertTrue("FetchGroup was not applied", util.isLoaded(result.getPeriod(), "endDate"));
        assertTrue("FetchGroup was not applied", util.isLoaded(result, "firstName"));
    }

    public void testNestedEmbeddedFetchGroup(){
        EntityManager em = createEntityManager();
        EntityGraph<Runner> fetchGraph = em.createEntityGraph(Runner.class);
        fetchGraph.addSubgraph("info").addSubgraph("status").addAttributeNodes("runningStatus");
        Runner result = em.createQuery("Select r from Runner r", Runner.class).setMaxResults(1).setHint(QueryHints.JPA_FETCH_GRAPH, fetchGraph).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertFalse("FetchGroup was not applied", util.isLoaded(result, "gender"));
        assertFalse("FetchGroup was not applied", util.isLoaded(result.getInfo(), "health"));
        assertTrue("FetchGroup was not applied", util.isLoaded(result.getInfo(), "status"));
        assertTrue("FetchGroup was not applied", util.isLoaded(result.getInfo().getStatus(), "runningStatus"));

        result.getInfo().getHealth();
        assertTrue("FetchGroup was not applied", util.isLoaded(result.getInfo(), "status"));
        assertTrue("FetchGroup was not applied", util.isLoaded(result, "gender"));
    }

    public void testLoadGroup(){
        EntityManager em = createEntityManager();
        EntityGraph<Employee> employeeGraph = em.createEntityGraph(Employee.class);
        employeeGraph.addAttributeNodes("address");
        Employee result = (Employee) em.createQuery("Select e from Employee e").setMaxResults(1).setHint(QueryHints.JPA_LOAD_GRAPH, employeeGraph).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertTrue("LoadGroup was not applied", util.isLoaded(result, "address"));
        assertTrue("LoadGroup was not applied", util.isLoaded(result, "department"));
        assertFalse("LoadGroup was not applied", util.isLoaded(result, "managedEmployees"));
    }

    public void testEmbeddedFetchGroupRefresh(){
        EntityManager em = createEntityManager();
        EntityGraph<Employee> employeeGraph = em.createEntityGraph(Employee.class);
        employeeGraph.addSubgraph("period").addAttributeNodes("startDate");
        Employee result = (Employee) em.createQuery("Select e from Employee e order by e.salary desc").setMaxResults(1).setHint(QueryHints.JPA_FETCH_GRAPH, employeeGraph).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertFalse("FetchGroup was not applied", util.isLoaded(result, "department"));
        assertFalse("FetchGroup was not applied", util.isLoaded(result.getPeriod(), "endDate"));
        assertTrue("FetchGroup was not applied", util.isLoaded(result.getPeriod(), "startDate"));
        result = (Employee) em.createQuery("Select e from Employee e order by e.salary desc").setMaxResults(1).setHint(QueryHints.JPA_FETCH_GRAPH, employeeGraph).setHint(QueryHints.REFRESH, true).getResultList().get(0);
    }

    public void testMapKeyFetchGroupRefresh(){
        EntityManager em = createEntityManager();
        EntityGraph<Runner> runnerGraph = em.createEntityGraph(Runner.class);
        runnerGraph.addKeySubgraph("shoes");
        Runner result = (Runner) em.createQuery("Select r from Runner r join r.shoes s").setHint(QueryHints.JPA_FETCH_GRAPH, runnerGraph).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertTrue("FetchGroup was not applied", util.isLoaded(result, "shoes"));
    }

    /**
     * An element subgraph describes the element of a collection, so it has to work on a collection
     * that is not a Map at all. races is a ManyToMany List, whose container policy is an
     * IndirectListContainerPolicy; asking for an element subgraph over it used to fail with a
     * ClassCastException, because the call was routed through the map key code.
     */
    public void testElementSubgraphOnList() {
        EntityManager em = createEntityManager();
        EntityGraph<Runner> runnerGraph = em.createEntityGraph(Runner.class);

        Subgraph<Race> racesGraph = runnerGraph.addElementSubgraph("races", Race.class);
        assertEquals("Element subgraph resolved to the wrong type", Race.class, racesGraph.getClassType());

        racesGraph.addAttributeNode("name");
        assertTrue("Attribute node was not added to the element subgraph", racesGraph.hasAttributeNode("name"));

        closeEntityManager(em);
    }

    /**
     * For a Map the element subgraph describes the value and the key subgraph describes the key, so
     * the two must resolve to different types. shoes is a Map&lt;ShoeTag, Shoe&gt;. addAttributeNode
     * validates the name against the type the subgraph resolved to, so asking each subgraph for an
     * attribute that only the other type has is what pins them apart.
     */
    public void testElementSubgraphOnMapValue() {
        EntityManager em = createEntityManager();
        EntityGraph<Runner> runnerGraph = em.createEntityGraph(Runner.class);

        Subgraph<Shoe> shoesGraph = runnerGraph.addElementSubgraph("shoes");
        shoesGraph.addAttributeNode("brand");
        assertTrue("Element subgraph did not resolve to the map value", shoesGraph.hasAttributeNode("brand"));

        Subgraph<ShoeTag> tagGraph = runnerGraph.addKeySubgraph("shoes");
        tagGraph.addAttributeNode("tag");
        assertTrue("Key subgraph did not resolve to the map key", tagGraph.hasAttributeNode("tag"));

        try {
            runnerGraph.<Shoe>addElementSubgraph("shoes").addAttributeNode("tag");
            fail("The element subgraph accepted an attribute of the map key, so it resolved to the key type");
        } catch (IllegalArgumentException expected) {
            // Shoe has no "tag" attribute; only ShoeTag does
        }

        closeEntityManager(em);
    }

    /**
     * A key subgraph needs a Map whose key is a managed type. personalBests is a
     * Map&lt;String, String&gt;, so its key is a basic attribute and the request has to be refused -
     * with IllegalArgumentException, which is what the specification asks for, rather than the
     * ClassCastException the guard used to produce for anything it did not expect.
     */
    public void testKeySubgraphRejectsBasicKey() {
        EntityManager em = createEntityManager();
        EntityGraph<Runner> runnerGraph = em.createEntityGraph(Runner.class);

        try {
            runnerGraph.addKeySubgraph("personalBests");
            fail("A key subgraph on a Map with a basic key should be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        closeEntityManager(em);
    }
}
