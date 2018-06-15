/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/28/2013-2.5 Gordon Yorke
//       - 397772: JPA 2.1 Entity Graph Support
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa21.advanced.Project;
import org.eclipse.persistence.testing.models.jpa21.advanced.Runner;

public class EntityGraphTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;
    protected Map guaranteedIds = new HashMap<Class, Object>();

    public EntityGraphTestSuite() {}

    public EntityGraphTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityGraphTestSuite");

        suite.addTest(new EntityGraphTestSuite("testSimpleGraph"));
        suite.addTest(new EntityGraphTestSuite("testEmbeddedFetchGroup"));
        suite.addTest(new EntityGraphTestSuite("testEmbeddedFetchGroupRefresh"));
        suite.addTest(new EntityGraphTestSuite("testsubclassSubgraphs"));
        suite.addTest(new EntityGraphTestSuite("testMapKeyFetchGroupRefresh"));
        suite.addTest(new EntityGraphTestSuite("testNestedEmbeddedFetchGroup"));
        suite.addTest(new EntityGraphTestSuite("testLoadGroup"));

        return suite;
    }

    public void setUp() {
        m_reset = true;
        super.setUp();
        clearCache();
    }

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
        EntityGraph employeeGraph = em.createEntityGraph(Project.class);
        employeeGraph.addSubclassSubgraph(LargeProject.class).addAttributeNodes("budget");
        employeeGraph.addAttributeNodes("description");
        List<Project> result = em.createQuery("Select p from Project p where type(p) = LargeProject").setHint(QueryHints.JPA_FETCH_GRAPH, employeeGraph).getResultList();
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        for (Project project : result){
            assertFalse("Fetch Group was not applied", util.isLoaded(project, "name"));
            assertTrue("Fetch Group was not applied", util.isLoaded(project, "description"));
            assertTrue("Fetch Group was not applied", util.isLoaded(project, "budget"));
        }
    }

    public void testEmbeddedFetchGroup(){
        EntityManager em = createEntityManager();
        EntityGraph employeeGraph = em.createEntityGraph(Employee.class);
        employeeGraph.addSubgraph("period").addAttributeNodes("startDate");
        Employee result = (Employee) em.createQuery("Select e from Employee e").setMaxResults(1).setHint(QueryHints.JPA_FETCH_GRAPH, employeeGraph).getResultList().get(0);
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
        EntityGraph fetchGraph = em.createEntityGraph(Runner.class);
        fetchGraph.addSubgraph("info").addSubgraph("status").addAttributeNodes("runningStatus");
        Runner result = (Runner)em.createQuery("Select r from Runner r").setMaxResults(1).setHint(QueryHints.JPA_FETCH_GRAPH, fetchGraph).getResultList().get(0);
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
        EntityGraph employeeGraph = em.createEntityGraph(Employee.class);
        employeeGraph.addAttributeNodes("address");
        Employee result = (Employee) em.createQuery("Select e from Employee e").setMaxResults(1).setHint(QueryHints.JPA_LOAD_GRAPH, employeeGraph).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertTrue("LoadGroup was not applied", util.isLoaded(result, "address"));
        assertTrue("LoadGroup was not applied", util.isLoaded(result, "department"));
        assertFalse("LoadGroup was not applied", util.isLoaded(result, "managedEmployees"));
    }

    public void testEmbeddedFetchGroupRefresh(){
        EntityManager em = createEntityManager();
        EntityGraph employeeGraph = em.createEntityGraph(Employee.class);
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
        EntityGraph runnerGraph = em.createEntityGraph(Runner.class);
        runnerGraph.addKeySubgraph("shoes");
        Runner result = (Runner) em.createQuery("Select r from Runner r join r.shoes s").setHint(QueryHints.JPA_FETCH_GRAPH, runnerGraph).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertTrue("FetchGroup was not applied", util.isLoaded(result, "shoes"));
    }


    @Override
    public String getPersistenceUnitName() {
       return "MulitPU-1";
    }
}
