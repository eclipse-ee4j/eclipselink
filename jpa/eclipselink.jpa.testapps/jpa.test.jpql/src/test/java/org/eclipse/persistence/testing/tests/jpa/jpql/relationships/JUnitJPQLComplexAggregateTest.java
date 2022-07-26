/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsExamples;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Test complex aggregate EJBQL functionality.
 * <p>
 * <b>Description</b>: This class creates a test suite, initializes the database
 * and adds tests to the suite.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for complex aggregate EJBQL functionality
 * </ul>
 * @see JUnitDomainObjectComparer
 */

//This test suite demonstrates the bug 4616218, waiting for bug fix
public class JUnitJPQLComplexAggregateTest extends JUnitTestCase {
    private static JUnitDomainObjectComparer comparer;        //the global comparer object used in all tests

    public JUnitJPQLComplexAggregateTest()
    {
        super();
    }

    public JUnitJPQLComplexAggregateTest(String name)
    {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "relationships";
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown()
    {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLComplexAggregateTest");
        suite.addTest(new JUnitJPQLComplexAggregateTest("testSetup"));

        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountOnJoinedVariableSimplePK"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountOnJoinedVariableOverManyToManySelfRefRelationship"));
        suite.addTest(new JUnitJPQLComplexAggregateTest("complexCountOnJoinedVariableOverManyToManySelfRefRelationshipPortable"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        clearCache();
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();

        //initialize the global comparer object
        comparer = new JUnitDomainObjectComparer();

        //set the session for the comparer to use
        comparer.setSession((AbstractSession)session.getActiveSession());

        new RelationshipsTableManager().replaceTables(session);
        //populate the relationships model and persist as well
        new RelationshipsExamples().buildExamples(session);
    }

    /**
     * Test case glassfish issue 2497:
     */
    public void complexCountOnJoinedVariableSimplePK() {
        EntityManager em = createEntityManager();

        // Need to create the expected result manually, because using the
        // TopLink query API would run into the same issue 2497.
        List<Long> expectedResult = Arrays.asList(1L, 0L,
                0L, 1L);
        Collections.sort(expectedResult);

        String jpql = "SELECT COUNT(o) FROM Customer c LEFT JOIN c.orders o GROUP BY c.name";
        Query q = em.createQuery(jpql);
        List result = q.getResultList();
        Collections.sort(result);

        Assert.assertEquals("Complex COUNT on joined variable simple PK", expectedResult, result);

        jpql = "SELECT COUNT(DISTINCT o) FROM Customer c LEFT JOIN c.orders o GROUP BY c.name";
        q = em.createQuery(jpql);
        result = q.getResultList();
        Collections.sort(result);

        Assert.assertEquals("Complex COUNT DISTINCT on joined variable simple PK", expectedResult, result);
    }

    /**
     * Test case glassfish issue 2440:
     * On derby a JPQL query including a LEFT JOIN on a ManyToMany
     * relationship field of the same class (self-referencing relationship)
     * runs into a NPE in SQLSelectStatement.appendFromClauseForOuterJoin.
     */
    public void complexCountOnJoinedVariableOverManyToManySelfRefRelationshipPortable() {
        EntityManager em = createEntityManager();

        List<Object[]> expectedResult = Arrays.asList(new Object[][] { {0L, "Jane Smith"}, {1L, "John Smith"}, {0L, "Karen McDonald"}, { 0L, "Robert Sampson"} });

        String jpql = "SELECT COUNT(cc), c.name FROM Customer c LEFT JOIN c.CCustomers cc GROUP BY c.name order by c.name";
        Query q = em.createQuery(jpql);
        List<Object[]> result = q.getResultList();

        final String description = "Complex COUNT on joined variable over ManyToMany self refrenceing relationship";

        Assert.assertEquals(description + " size mismatch", expectedResult.size(), result.size());

        for (int i = 0; i < result.size(); i++) {
            Object[] expected = expectedResult.get(i);
            Object[] actual = result.get(i);
            Assert.assertEquals(expected[0], actual[0]);
            Assert.assertEquals(expected[1], actual[1]);
        }
    }

    /**
     * Test case glassfish issue 2440:
     * On derby a JPQL query including a LEFT JOIN on a ManyToMany
     * relationship field of the same class (self-referencing relationship)
     * runs into a NPE in SQLSelectStatement.appendFromClauseForOuterJoin.
     */
    public void complexCountOnJoinedVariableOverManyToManySelfRefRelationship() {
        if (getPersistenceUnitServerSession().getPlatform().isMaxDB()) {
            return; // bug 327108 MaxDB can't order by non-selec tlist column c.name
        }

        EntityManager em = createEntityManager();

        List<Long> expectedResult = Arrays.asList(0L, 1L, 0L, 0L);

        String jpql = "SELECT COUNT(cc) FROM Customer c LEFT JOIN c.CCustomers cc GROUP BY c.name order by c.name";
        Query q = em.createQuery(jpql);
        List result = q.getResultList();

        Assert.assertEquals("Complex COUNT on joined variable over ManyToMany self refrenceing relationship failed",
                            expectedResult, result);
    }
}
