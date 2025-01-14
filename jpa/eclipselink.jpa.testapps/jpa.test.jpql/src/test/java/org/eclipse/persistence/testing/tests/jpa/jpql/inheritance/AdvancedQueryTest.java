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

package org.eclipse.persistence.testing.tests.jpa.jpql.inheritance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.Engineer;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritancePopulator;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;
import org.eclipse.persistence.testing.models.jpa.inheritance.Person;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;

import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Test advanced JPA Query functionality.
 * <p>
 * <b>Description</b>: This tests query hints, caching and query optimization.
 *
 */
public class AdvancedQueryTest extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public AdvancedQueryTest() {
        super();
    }

    public AdvancedQueryTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "inheritance";
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
        suite.addTest(new AdvancedQueryTest("testBatchFetchingInheritance"));
        suite.addTest(new AdvancedQueryTest("testBatchFetchOuterJoin"));
        suite.addTest(new AdvancedQueryTest("testTearDown"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        DatabaseSession session = getPersistenceUnitServerSession();
        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();
        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        new InheritanceTableCreator().replaceTables(session);
        InheritancePopulator inheritancePopulator = new InheritancePopulator();
        inheritancePopulator.buildExamples();

        //Persist the examples in the database
        inheritancePopulator.persistExample(session);
    }

    public void testTearDown() {
        closeEntityManagerFactory();
    }

    /**
     * Test batch fetching with outer joins.
     */
    public void testBatchFetchOuterJoin() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getPersistenceUnitServerSession());
        try {
            TypedQuery<Person> query = em.createQuery("Select p from Person p left join p.bestFriend f order by f.title", Person.class);
            query.setHint(QueryHints.BATCH, "p.bestFriend");
            List<Person> result = query.getResultList();
            if (result.size() != 8) {
                fail("Should have been 8 results but was: " + result.size());
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 2) {
                fail("Should have been 2 query but was: " + counter.getSqlStatements().size());
            }
            for (Person person : result) {
                person.getBestFriend();
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 2) {
                fail("Should have been 2 queries but was: " + counter.getSqlStatements().size());
            }
            clearCache();
            for (Person person : result) {
                verifyObject(person);
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
     * Test batch fetching on inheritance.
     */
    public void testBatchFetchingInheritance() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getPersistenceUnitServerSession());
        try {
            TypedQuery<Person> query = em.createQuery("Select p from Person p", Person.class);
            query.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
            query.setHint(QueryHints.BATCH, "p.company");
            List<Person> result = query.getResultList();
            if (isWeavingEnabled() && counter.getSqlStatements().size() != 5) {
                fail("Should have been 5 query but was: " + counter.getSqlStatements().size());
            }
            for (Person person : result) {
                if (person instanceof Engineer) {
                    ((Engineer)person).getCompany();
                }
            }
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 5) {
                fail("Should have been 5 queries but was: " + counter.getSqlStatements().size());
            }
            clearCache();
            for (Person person : result) {
                verifyObject(person);
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
