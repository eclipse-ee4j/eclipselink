/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2025 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.tests.jpa.jpql.relationships;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.Customer;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsExamples;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;

import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Test advanced JPA Query functionality.
 * <p>
 * <b>Description</b>: This tests query hints, caching and query optimization.
 *
 */
public class AdvancedQueryTest extends JUnitTestCase {

//    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public AdvancedQueryTest() {
        super();
    }

    public AdvancedQueryTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "relationships";
    }

    // This method is run at the start of EVERY test case method.

    @Override
    public void setUp() {

    }

    // This method is run at the end of EVERY test case method.

    @Override
    public void tearDown() {
        clearCache();
    }

    //This suite contains all tests contained in this class

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedQueryTest");
        suite.addTest(new AdvancedQueryTest("testSetup"));
        suite.addTest(new AdvancedQueryTest("testMapBatchFetchingJOIN"));
        suite.addTest(new AdvancedQueryTest("testMapBatchFetchingEXISTS"));
        suite.addTest(new AdvancedQueryTest("testMapBatchFetchingIN"));
        suite.addTest(new AdvancedQueryTest("testMapJoinFetching"));
        suite.addTest(new AdvancedQueryTest("testLoadGroup"));
        suite.addTest(new AdvancedQueryTest("testConcurrentLoadGroup"));
        suite.addTest(new AdvancedQueryTest("testTearDown"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        DatabaseSession session = getPersistenceUnitServerSession();
        new RelationshipsTableManager().replaceTables(session);
        //populate the relationships model and persist as well
        new RelationshipsExamples().buildExamples(session);
    }

    public void testTearDown() {
        closeEntityManagerFactory();
    }

    /**
     * Test batch fetching of maps.
     */
    public void testMapBatchFetchingJOIN() {
        testMapBatchFetching(BatchFetchType.JOIN, 0);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testMapBatchFetchingIN() {
        testMapBatchFetching(BatchFetchType.IN, 100);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testMapBatchFetchingEXISTS() {
        testMapBatchFetching(BatchFetchType.EXISTS, 0);
    }

    /**
     * Test batch fetching of maps.
     */
    public void testMapBatchFetching(BatchFetchType type, int size) {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getPersistenceUnitServerSession());
        try {
            TypedQuery<Customer> query = em.createQuery("Select c from Customer c", Customer.class);
            query.setHint(QueryHints.BATCH_SIZE, size);
            query.setHint(QueryHints.BATCH_TYPE, type);
            query.setHint(QueryHints.BATCH, "e.CSInteractions");
            query.setHint(QueryHints.BATCH, "e.CCustomers");
            List<Customer> results = query.getResultList();
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 3) {
                fail("Should have been 3 queries but was: " + counter.getSqlStatements().size());
            }
            int queries = 5;
            for (Customer customer : results) {
                queries = queries + customer.getCSInteractions().size();
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
            }
            clearCache();
            for (Customer customer : results) {
                verifyObject(customer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test load groups.
     */
    public void testLoadGroup() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getPersistenceUnitServerSession());
        try {
            TypedQuery<Customer> query = em.createQuery("Select c from Customer c", Customer.class);
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "CSInteractions");
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "CCustomers");
            List<Customer> results = query.getResultList();
            counter.getSqlStatements().clear();
            for (Customer customer : results) {
                customer.getCSInteractions().size();
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Load group should have loaded attributes.");
            }
            clearCache();
            for (Customer customer : results) {
                verifyObject(customer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Test concurrent load groups.
     */
    public void testConcurrentLoadGroup() {
        clearCache();
        boolean concurrent = getDatabaseSession().isConcurrent();
        getDatabaseSession().setIsConcurrent(true);
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getPersistenceUnitServerSession());
        try {
            TypedQuery<Customer> query = em.createQuery("Select c from Customer c", Customer.class);
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "CSInteractions");
            query.setHint(QueryHints.LOAD_GROUP_ATTRIBUTE, "CCustomers");
            List<Customer> results = query.getResultList();
            counter.getSqlStatements().clear();
            for (Customer customer : results) {
                customer.getCSInteractions().size();
            }
            if (counter.getSqlStatements().size() > 0) {
                fail("Load group should have loaded attributes.");
            }
            clearCache();
            for (Customer customer : results) {
                verifyObject(customer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
            getDatabaseSession().setIsConcurrent(concurrent);
        }
    }

    /**
     * Test join fetching of maps.
     */
    public void testMapJoinFetching() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getPersistenceUnitServerSession());
        try {
            TypedQuery<Customer> query = em.createQuery("Select c from Customer c", Customer.class);
            query.setHint(QueryHints.LEFT_FETCH, "e.CSInteractions");
            query.setHint(QueryHints.LEFT_FETCH, "e.CCustomers");
            List<Customer> results = query.getResultList();
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 3) {
                fail("Should have been 3 queries but was: " + counter.getSqlStatements().size());
            }
            int queries = 1;
            for (Customer customer : results) {
                queries = queries + customer.getCSInteractions().size();
            }
            assertTrue("No data to join.", queries > 1);
            if (isWeavingEnabled() && counter.getSqlStatements().size() > queries) {
                fail("Should have been " + queries + " queries but was: " + counter.getSqlStatements().size());
            }
            clearCache();
            for (Customer customer : results) {
                verifyObject(customer);
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }
}
