/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/28/2013-2.5 Gordon Yorke 
 *       - 397772: JPA 2.1 Entity Graph Support
 *     02/13/2013-2.5 Guy Pelletier 
 *       - 397772: JPA 2.1 Entity Graph Support (XML support)
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa21.advanced.Project;

public class EntityGraphTestSuite extends JUnitTestCase {
    protected boolean m_reset = false;
    protected Map guaranteedIds = new HashMap<Class, Object>();

    public EntityGraphTestSuite() {}
    
    public EntityGraphTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("EntityGraphTestSuite");
         
        suite.addTest(new EntityGraphTestSuite("testSimpleGraph"));
        
        // Add the equivalent XML tests.
        suite.addTest(XMLEntityGraphTestSuite.suite());
        
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
//                assertTrue("Fetch Group was not applied: executive is not loaded", util.isLoaded(project, "executive"));
            }
        }
        em.close();
    }
    
    public void testInheritanceGraph(){
        
    }
    
    public void testLoadGraph(){
        
    }
    
    public void testLoadFetchGraph(){
    }
    public void testNestedFetchGroup(){
        
    }
    public void testEmbeddedFetchGroup(){
        
    }
    public void testEmbededNestedFetchGroup(){
        
    }
}
