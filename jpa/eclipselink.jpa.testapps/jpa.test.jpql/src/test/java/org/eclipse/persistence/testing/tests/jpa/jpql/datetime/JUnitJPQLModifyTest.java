/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.tests.jpa.jpql.datetime;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimePopulator;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimeTableCreator;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitDomainObjectComparer;

/**
 * <p>
 * <b>Purpose</b>: Test JPQL UPDATE and DELETE queries.
 * <p>
 * <b>Description</b>: This class creates a test suite and adds tests to the
 * suite. The database gets initialized prior to each test method.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Run tests for JPQL UPDATE and DELETE queries.
 * </ul>
 * @see org.eclipse.persistence.testing.models.jpa.datetime.DateTimePopulator
 * @see JUnitDomainObjectComparer
 */

public class JUnitJPQLModifyTest extends JUnitTestCase {

    static JUnitDomainObjectComparer comparer; //the global comparer object used in all tests

    public JUnitJPQLModifyTest()
    {
        super();
    }

    public JUnitJPQLModifyTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "datetime";
    }

    //This method is run at the start of EVERY test case method
    @Override
    public void setUp()
    {
        //get session to start setup
        DatabaseSession session = getPersistenceUnitServerSession();
        // drop and create DateTime tables and persist dateTime test data
        new DateTimeTableCreator().replaceTables(session);
        DateTimePopulator dateTimePopulator = new DateTimePopulator();
        dateTimePopulator.persistExample(session);
    }

    //This method is run at the end of EVERY test case method
    @Override
    public void tearDown()
    {
        clearCache();
    }

    //This suite contains all tests contained in this class
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLModifyTest");
        suite.addTest(new JUnitJPQLModifyTest("testSetup"));
        suite.addTest(new JUnitJPQLModifyTest("updateDateTimeFields"));

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

    }

    public void updateDateTimeFields()
    {
        EntityManager em = createEntityManager();
        int exp = executeJPQLReturningInt(em, "SELECT COUNT(d) FROM DateTime d");
        String jpql = null;
        int updated = 0;

        // test query setting java.sql.Date field
        try {
            jpql = "UPDATE DateTime SET date = CURRENT_DATE";
            beginTransaction(em);
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set date: " +
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);

            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.date <= CURRENT_DATE";
            assertEquals("updateDateTimeFields set date: " +
                         "unexpected number of changed values in the database",
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        // test query setting java.sql.Time field
        try {
            jpql = "UPDATE DateTime SET time = CURRENT_TIME";
            beginTransaction(em);
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set time: " +
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);

            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.time <= CURRENT_TIME";
            assertEquals("updateDateTimeFields set time: " +
                         "unexpected number of changed values in the database",
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        // test query setting java.sql.Timestamp field
        try {
            jpql = "UPDATE DateTime SET timestamp = CURRENT_TIMESTAMP";
            beginTransaction(em);
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set timestamp: " +
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);

            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.timestamp <= CURRENT_TIMESTAMP";
            assertEquals("updateDateTimeFields set timestamp: " +
                         "unexpected number of changed values in the database",
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        // test query setting java.util.Date field
        beginTransaction(em);
        try {
            jpql = "UPDATE DateTime SET utilDate = CURRENT_TIMESTAMP";
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set utilDate: " +
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);

            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.utilDate <= CURRENT_TIMESTAMP";
            assertEquals("updateDateTimeFields set utilDate: " +
                         "unexpected number of changed values in the database",
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }

        // test query setting java.util.Calendar field
        beginTransaction(em);
        try {
            jpql = "UPDATE DateTime SET calendar = CURRENT_TIMESTAMP";
            updated = em.createQuery(jpql).executeUpdate();
            assertEquals("updateDateTimeFields set calendar: " +
                         "wrong number of updated instances", exp, updated);
            commitTransaction(em);

            // check database changes
            jpql = "SELECT COUNT(d) FROM DateTime d WHERE d.calendar <= CURRENT_TIMESTAMP";
            assertEquals("updateDateTimeFields set calendar: " +
                         "unexpected number of changed values in the database",
                         exp, executeJPQLReturningInt(em, jpql));
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
        }
    }

    /** Helper method executing a JPQL query retuning an int value. */
    private int executeJPQLReturningInt(EntityManager em, String jpql)
    {
        Query q = em.createQuery(jpql);
        Object result = q.getSingleResult();
        return ((Number)result).intValue();
    }
}
