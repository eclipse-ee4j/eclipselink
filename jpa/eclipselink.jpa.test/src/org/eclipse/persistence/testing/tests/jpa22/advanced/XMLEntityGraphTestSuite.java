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
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
package org.eclipse.persistence.testing.tests.jpa22.advanced;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa22.advanced.xml.Employee;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.LargeProject;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.Project;

public class XMLEntityGraphTestSuite extends JUnitTestCase {
    public XMLEntityGraphTestSuite() {}

    public XMLEntityGraphTestSuite(String name) {
        super(name);
        setPuName("MulitPU-4");
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "MulitPU-4";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("XMLEntityGraphTestSuite");

        // These tests call stored procedures that return a result set.
        suite.addTest(new XMLEntityGraphTestSuite("testSimpleGraph"));
        return suite;
    }

    /**
     * Tests a NamedStoredProcedureQuery using a positional parameter returning
     * a single result set.
     */
    public void testSimpleGraph() {
        EntityManager em = createEntityManager();

        Employee result = (Employee) em.createQuery("Select e from XMLEmployee e join treat(e.projects as XMLLargeProject) p where p.executive is Not Null and e != p.executive").setHint(QueryHints.JPA_FETCH_GRAPH, em.getEntityGraph("XMLEmployee")).getResultList().get(0);
        PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
        assertFalse("fetchgroup failed to be applied: department is loaded", util.isLoaded(result, "department"));
        assertTrue("Fetch Group was not applied: projects is not loaded", util.isLoaded(result, "projects"));
        for (Project project : result.getProjects()){
            assertFalse("fetchgroup failed to be applied : teamLeader is loaded", util.isLoaded(project, "teamLeader"));
            assertTrue("fetchgroup failed to be applied: properties is not loaded", util.isLoaded(project, "properties"));
            if (project instanceof LargeProject){
                //assertTrue("Fetch Group was not applied: executive is not loaded", util.isLoaded(project, "executive"));
            }
        }
        closeEntityManager(em);
    }
}

