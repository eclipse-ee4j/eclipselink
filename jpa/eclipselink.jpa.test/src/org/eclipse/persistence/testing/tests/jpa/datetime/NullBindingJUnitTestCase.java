/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     07/07/2014-2.5.3 Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.testing.tests.jpa.datetime;

import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTime;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimeTableCreator;

/**
 * <p>
 * <b>Purpose</b>: Test binding of null values to temporal type fields in TopLink's JPA
 * implementation.
 * <p>
 * <b>Description</b>: This class creates a test suite and adds tests to the suite. The database
 * gets initialized prior to the test methods.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li>Run tests for binding of null values to temporal type fields in TopLink's JPA implementation.
 * </ul>
 *
 * @see org.eclipse.persistence.testing.models.jpa.datetime.DateTimeTableCreator
 */
public class NullBindingJUnitTestCase extends JUnitTestCase {
    private static int datetimeId;

    public NullBindingJUnitTestCase() {
        super();
    }

    public NullBindingJUnitTestCase(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Null Binding DateTime");
        suite.addTest(new NullBindingJUnitTestCase("testSetup"));
        suite.addTest(new NullBindingJUnitTestCase("testCreateDateTime"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifySqlDate"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyTime"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyTimestamp"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyUtilDate"));
        suite.addTest(new NullBindingJUnitTestCase("testNullifyCalendar"));
        suite.addTest(new NullBindingJUnitTestCase("testDateTimeMap"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the
     * server.
     */
    public void testSetup() {
        new DateTimeTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }

    /**
     * Creates the DateTime instance used in later tests.
     */
    public void testCreateDateTime() {
        EntityManager em = createEntityManager();

        DateTime dt;

        beginTransaction(em);
        dt =
            new DateTime(new java.sql.Date(0), new java.sql.Time(0), new java.sql.Timestamp(0), new java.util.Date(0),
                java.util.Calendar.getInstance());
        em.persist(dt);

        datetimeId = dt.getId();
        commitTransaction(em);

    }

    /**
     */
    public void testNullifySqlDate() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            beginTransaction(em);
            dt = em.find(DateTime.class, datetimeId);
            dt.setDate(null);
            commitTransaction(em);
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.sql.Date field to null", dt2.getDate() == null);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyTime() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            beginTransaction(em);
            dt = em.find(DateTime.class, datetimeId);
            dt.setTime(null);
            commitTransaction(em);
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.sql.Time field to null", dt2.getTime() == null);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyTimestamp() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            beginTransaction(em);
            dt = em.find(DateTime.class, datetimeId);
            dt.setTimestamp(null);
            commitTransaction(em);
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.sql.Timestamp field to null", dt2.getTimestamp() == null);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyUtilDate() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            beginTransaction(em);
            dt = em.find(DateTime.class, datetimeId);
            dt.setUtilDate(null);
            commitTransaction(em);
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.util.Date field to null", dt2.getUtilDate() == null);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    /**
     */
    public void testNullifyCalendar() {
        EntityManager em = createEntityManager();
        Query q;
        DateTime dt, dt2;

        try {
            beginTransaction(em);
            dt = em.find(DateTime.class, datetimeId);
            dt.setCalendar(null);
            commitTransaction(em);
            q = em.createQuery("SELECT dt FROM DateTime dt WHERE dt.id = " + datetimeId);
            dt2 = (DateTime) q.getSingleResult();
            assertTrue("Error setting java.util.Calendar field to null", dt2.getCalendar() == null);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }

    public void testDateTimeMap() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            DateTime dt = em.find(DateTime.class, datetimeId);
            assertNotNull(dt);
            Map<Date, DateTime> map = dt.getUniSelfMap();
            assertNotNull(map);
            // Make sure that we find ourselves in the map!
            assertTrue(map.values().contains(dt));
            closeEntityManagerAndTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            throw e;
        }
    }
}
