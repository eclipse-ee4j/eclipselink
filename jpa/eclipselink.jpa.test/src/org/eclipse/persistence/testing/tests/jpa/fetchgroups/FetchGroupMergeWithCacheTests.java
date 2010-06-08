/*******************************************************************************
 * Copyright (c) 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;

import org.junit.Test;

import junit.framework.TestSuite;

public class FetchGroupMergeWithCacheTests extends BaseFetchGroupTests {
    public FetchGroupMergeWithCacheTests() {
        super();
    }

    public FetchGroupMergeWithCacheTests(String name) {
        super(name);
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("FetchGroupMergeWithCacheTests");
        
        suite.addTest(new FetchGroupMergeWithCacheTests("testSetup"));
        suite.addTest(new FetchGroupMergeWithCacheTests("cacheFull_QueryWithFetchGroup_Simple"));
        suite.addTest(new FetchGroupMergeWithCacheTests("cacheFull_FindWithFetchGroup_Simple"));
        
        return suite;
    }

    @Test
    // The full object is in shared cache, query uses FetchGroup
    public void cacheFull_QueryWithFetchGroup_Simple() throws Exception {
        // Place the object into the shared cache
        EntityManager em = createEntityManager();
        Employee emp = findMinimumEmployee(em);
        int id = emp.getId();
        try {
            // emp doesn't have FetchGroup
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);
        } finally {
            closeEntityManager(em);
        }
                        
        // Now read it with a FetchGroup
        EntityManager em2 = createEntityManager();
        Query query = em2.createQuery("SELECT e FROM Employee e WHERE e.id = "+id);        
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");
        query.setHint(QueryHints.FETCH_GROUP, fg);
        Employee resultEmployee = (Employee)query.getSingleResult();
        try {
            // Query with pk (that clearly can't return more than one object) now uses ReadObjectQuery - doesn't hit the db. 
            assertEquals(1, getQuerySQLTracker(em2).getTotalSQLSELECTCalls());
            // resultEmployee doesn't have FetchGroup because - it's picked up from the shared cache
            // where it was saved without FetchGroup.
            assertNoFetchGroup(resultEmployee);
        } finally {
            closeEntityManager(em2);
        }
    }    

    @Test
    // The full object is in shared cache, query uses FetchGroup
    public void cacheFull_FindWithFetchGroup_Simple() throws Exception {
        // Place the object into the shared cache
        EntityManager em = createEntityManager();
        Employee emp = findMinimumEmployee(em);
        int id = emp.getId();
        try {
            // emp doesn't have FetchGroup
            assertEquals(1, getQuerySQLTracker(em).getTotalSQLSELECTCalls());
            assertNoFetchGroup(emp);
        } finally {
            closeEntityManager(em);
        }
                        
        // Now find it with a FetchGroup
        EntityManager em2 = createEntityManager();
        FetchGroup fg = new FetchGroup();
        fg.addAttribute("firstName");
        fg.addAttribute("lastName");
        HashMap properties = new HashMap(1);
        properties.put(QueryHints.FETCH_GROUP, fg);
        Employee resultEmployee = em2.find(Employee.class, id, properties);
        try {
            // Find always uses ReadObjectQuery, it doesn't hits the db
            assertEquals(1, getQuerySQLTracker(em2).getTotalSQLSELECTCalls());
            // resultEmployee doesn't have FetchGroup because - it's picked up from the shared cache
            // where it was saved without FetchGroup.
            assertNoFetchGroup(resultEmployee);
        } finally {
            closeEntityManager(em2);
        }
    }    
}
