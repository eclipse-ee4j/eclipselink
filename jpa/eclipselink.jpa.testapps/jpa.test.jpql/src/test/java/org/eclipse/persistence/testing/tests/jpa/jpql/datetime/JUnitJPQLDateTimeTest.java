/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
import jakarta.persistence.TemporalType;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimePopulator;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimeTableCreator;

import java.time.Instant;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

//Test all kinds of combinations of date time types
public class JUnitJPQLDateTimeTest extends JUnitTestCase {
    public JUnitJPQLDateTimeTest() {
        super();
    }

    public JUnitJPQLDateTimeTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    @Override
    public String getPersistenceUnitName() {
        return "datetime";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLDateTimeTest");
        suite.addTest(new JUnitJPQLDateTimeTest("testSetup"));
        suite.addTest(new JUnitJPQLDateTimeTest("testSqlDate"));
        suite.addTest(new JUnitJPQLDateTimeTest("testSqlDateToTS"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTime"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimeToTS"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimestamp"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimestampToDate"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimestampToTime"));
        suite.addTest(new JUnitJPQLDateTimeTest("testUtilDate"));
        suite.addTest(new JUnitJPQLDateTimeTest("testAssignWrongType"));
        suite.addTest(new JUnitJPQLDateTimeTest("testCalendarWithUtilDate"));
        suite.addTest(new JUnitJPQLDateTimeTest("testSqlDateWithCal"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimeWithCal"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimestampWithCal"));
        suite.addTest(new JUnitJPQLDateTimeTest("testCalendar"));
        suite.addTest(new JUnitJPQLDateTimeTest("testInstant"));
        suite.addTest(new JUnitJPQLDateTimeTest("testYear"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimestampGreaterThan"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimestampLessThan"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimestampIn"));
        suite.addTest(new JUnitJPQLDateTimeTest("testTimestampBetween"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new DateTimeTableCreator().replaceTables(getPersistenceUnitServerSession());
                DateTimePopulator dateTimePopulator = new DateTimePopulator();
                dateTimePopulator.persistExample(getPersistenceUnitServerSession());
        clearCache();
    }

    public void testSqlDate() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.date = :date").
            setParameter("date", cal.getTime(), TemporalType.DATE).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testSqlDateToTS() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.date = :date").
            setParameter("date", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testTime() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.time = :time").
            setParameter("time", cal.getTime(), TemporalType.TIME).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testTimeToTS() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.time = :time").
            setParameter("time", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testTimestamp() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp = :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testTimestampToDate() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp = :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.DATE).
            getResultList();

        assertEquals("There should be zero result", 0, result.size());
    }

    public void testTimestampToTime() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp = :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.TIME).
            getResultList();

        assertEquals("There should be zero result", 0, result.size());
    }

   public void testUtilDate() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.utilDate = :utilDate").
            setParameter("utilDate", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();

       assertEquals("There should be one result", 1, result.size());
    }

   public void testAssignWrongType() {
       Date now = new Date();
       try (EntityManager em = createEntityManager();) {
           List<?> result = em.createQuery("SELECT o FROM DateTime o WHERE o.utilDate = :utilDate AND o.localDate = :utilDate").
                   setParameter("utilDate", now).
                   getResultList();
           fail("parameter value assignment should fail on o.localDate");
       } catch (Exception e) {}

       try (EntityManager em = createEntityManager();) {
           List<?> result = em.createQuery("SELECT o FROM DateTime o WHERE o.localDate = :utilDate AND o.utilDate = :utilDate ").
                   setParameter("utilDate", now).
                   getResultList();
           fail("parameter value assignment should fail on o.localDate");
       } catch (Exception e) {}
    }

    public void testCalendarWithUtilDate() {
         GregorianCalendar cal = new GregorianCalendar();
         cal.set(1901, 11, 31, 23, 59, 59);
         cal.set(Calendar.MILLISECOND, 999);

         List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.calendar = :calendar").
             setParameter("calendar", cal.getTime(), TemporalType.TIMESTAMP).
             getResultList();

        assertEquals("There should be one result", 1, result.size());
     }

    public void testSqlDateWithCal() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.date = :date").
            setParameter("date", cal, TemporalType.DATE).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testTimeWithCal() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.time = :time").
            setParameter("time", cal, TemporalType.TIME).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testTimestampWithCal() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp = :timestamp").
            setParameter("timestamp", cal, TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testUtilDateWithCal() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.utilDate = :utilDate").
            setParameter("utilDate", cal, TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testCalendar() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.calendar = :calendar").
            setParameter("calendar", cal, TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testInstant() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Instant instant = cal.toInstant();

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.instant = :instant").
                setParameter("instant", instant).
                getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testYear() {
        Year year = Year.of(1901);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.year = :year").
                setParameter("year", year).
                getResultList();

        assertEquals("There should be one result", 1, result.size());
    }

    public void testTimestampGreaterThan() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp > :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be three result", 3, result.size());
    }

    public void testTimestampLessThan() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2001, 6, 1, 3, 45, 32);
        cal.set(Calendar.MILLISECOND, 87);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp < :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be three result", 2, result.size());
    }

//IN node is going to be fixed and then this test will run
    public void testTimestampIn() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        GregorianCalendar cal2 = new GregorianCalendar();
        cal2.set(2001, 6, 1, 3, 45, 32);
        cal2.set(Calendar.MILLISECOND, 87);

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp IN (:timestamp1, :timestamp2)").
            setParameter("timestamp1", cal.getTime(), TemporalType.TIMESTAMP).
            setParameter("timestamp2", cal2, TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be two result", 2, result.size());
    }

    public void testTimestampBetween() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        Calendar cal2 = Calendar.getInstance();

        List<?> result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp BETWEEN :timestamp1 AND :timestamp2").
            setParameter("timestamp1", cal.getTime(), TemporalType.TIMESTAMP).
            setParameter("timestamp2", cal2, TemporalType.TIMESTAMP).
            getResultList();

        assertEquals("There should be four result", 4, result.size());
    }

}
