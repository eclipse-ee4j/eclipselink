/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.timestamptz;

import java.util.Calendar;
import java.util.TimeZone;
import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.timestamptz.*;

public class TimeStampTZJUnitTestSuite extends JUnitTestCase {
        
    public TimeStampTZJUnitTestSuite() {
        super();
    }
    
    public TimeStampTZJUnitTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("TimeStampTZJUnitTestSuite");
        suite.addTest(new TimeStampTZJUnitTestSuite("testSetup"));
        suite.addTest(new TimeStampTZJUnitTestSuite("testNoZone"));
        suite.addTest(new TimeStampTZJUnitTestSuite("testTimeStampTZ"));
        suite.addTest(new TimeStampTZJUnitTestSuite("testTimeStampLTZ"));
        suite.addTest(new TimeStampTZJUnitTestSuite("testTimeStampTZDST"));
        return suite;
    }
    
    public void testSetup() {
        new TimestampTableCreator().replaceTables(JUnitTestCase.getServerSession("timestamptz"));
    }
    
    /* Test TimeStampTZ with no zone set */
    public void testNoZone() {
        int year = 2000, month = 1, date = 10, hour = 11, minute = 21, second = 31;
        Integer tsId = null;
        java.util.Calendar originalCal = null, dbCal = null;
        
        EntityManager em = createEntityManager("timestamptz");
        beginTransaction(em);
        try {
            TStamp ts = new TStamp();
            originalCal = java.util.Calendar.getInstance();
            originalCal.set(year, month, date, hour, minute, second);
            ts.setNoZone(originalCal);
            em.persist(ts);
            em.flush();
            tsId = ts.getId();
            commitTransaction(em);
        } catch (Exception e) {
            e.printStackTrace();
            rollbackTransaction(em);
        } finally {
            clearCache();
            dbCal = em.find(TStamp.class, tsId).getNoZone();
            closeEntityManager(em);
        }
        
        assertEquals("The date retrived from db is not the one set to the child ",  dbCal, originalCal);
        assertTrue("The year is not macth", year == dbCal.get(java.util.Calendar.YEAR));
        assertTrue("The month is not match", month == dbCal.get(java.util.Calendar.MONTH));
        assertTrue("The date is not match", date == dbCal.get(java.util.Calendar.DATE));
        assertTrue("The hour is not match", hour == dbCal.get(java.util.Calendar.HOUR));
        assertTrue("The minute is not match", minute == dbCal.get(java.util.Calendar.MINUTE));
        assertTrue("The second is not match", second == dbCal.get(java.util.Calendar.SECOND));
    }    

/* Test TimeStampTZ with time zone set with midnight time*/
    public void testTimeStampTZ() {
        int year = 2000, month = 1, date = 10, hour = 0, minute = 0, second = 0;
        Integer tsId = null;
        Calendar originalCal = null, dbCal = null;
        String zoneId = "Europe/London";
        
        EntityManager em = createEntityManager("timestamptz");
        beginTransaction(em);
        try {
            TStamp ts = new TStamp();
            originalCal = Calendar.getInstance(TimeZone.getTimeZone(zoneId));
            originalCal.set(Calendar.AM_PM, Calendar.AM);
            originalCal.set(year, month, date, 0, 0, 0);
            originalCal.set(Calendar.MILLISECOND, 0);
            ts.setTsTZ(originalCal);
            em.persist(ts);
            em.flush();
            tsId = ts.getId();
            commitTransaction(em);
            closeEntityManager(em);
            clearCache();
            em = createEntityManager("timestamptz");
            dbCal = em.find(TStamp.class, tsId).getTsTZ();
            
            assertEquals("The timezone id is not the one set to the field",  dbCal.getTimeZone().getID(), zoneId);
            assertTrue("The AM is not match", Calendar.AM == dbCal.get(java.util.Calendar.AM_PM));
            assertTrue("The year is not macth", year == dbCal.get(java.util.Calendar.YEAR));
            assertTrue("The month is not match", month == dbCal.get(java.util.Calendar.MONTH));
            assertTrue("The date is not match", date == dbCal.get(java.util.Calendar.DATE));
            assertTrue("The hour is not match", hour == dbCal.get(java.util.Calendar.HOUR));
            assertTrue("The minute is not match", minute == dbCal.get(java.util.Calendar.MINUTE));
            assertTrue("The second is not match", second == dbCal.get(java.util.Calendar.SECOND));
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    /* Test TimeStampLTZ */
    public void testTimeStampLTZ() {
        int year = 2000, month = 3, date = 21, hour = 11, minute = 45, second = 50;
        Integer tsId = null;
        Calendar originalCal = null, dbCal = null;
        String zoneId = "America/Los_Angeles";
        
        EntityManager em = createEntityManager("timestamptz");
        beginTransaction(em);
        try {
            TStamp ts = new TStamp();
            originalCal = Calendar.getInstance(TimeZone.getTimeZone(zoneId));
            originalCal.set(Calendar.AM_PM, Calendar.AM);
            originalCal.set(year, month, date, hour, minute, second);
            originalCal.set(Calendar.MILLISECOND, 0);
            ts.setTsLTZ(originalCal);
            em.persist(ts);
            em.flush();
            tsId = ts.getId();
            commitTransaction(em);
        } catch (Exception e) {
            e.printStackTrace();
            rollbackTransaction(em);
        } finally {
            clearCache();
            dbCal = em.find(TStamp.class, tsId).getTsLTZ();
            closeEntityManager(em);
        }
       
        assertTrue("The year is not macth", year == dbCal.get(java.util.Calendar.YEAR));
        assertTrue("The month is not match", month == dbCal.get(java.util.Calendar.MONTH));
        assertTrue("The date is not match", date == dbCal.get(java.util.Calendar.DATE));
        int hourDiffFromDB = dbCal.get(Calendar.HOUR_OF_DAY) - originalCal.get(Calendar.HOUR_OF_DAY);
        int hourDiffFromZone = (dbCal.get(Calendar.ZONE_OFFSET) - originalCal.get(Calendar.ZONE_OFFSET))/ 3600000;
        assertTrue("The hour is not match", hourDiffFromDB == hourDiffFromZone);
        assertTrue("The minute is not match", minute == dbCal.get(java.util.Calendar.MINUTE));
        assertTrue("The second is not match", second == dbCal.get(java.util.Calendar.SECOND));
    }
    
    /* Test TimeStampTZ with daylightsaving time*/
    public void testTimeStampTZDST() {
        int year = 2008, month = 2, date = 10, hour = 11, minute = 0, second = 0;
        Integer tsId = null;
        Calendar originalCal = null, dbCal = null;
        String zoneIdRemote = "Europe/London";
        
        EntityManager em = createEntityManager("timestamptz");
        beginTransaction(em);
        try {
            TStamp ts = new TStamp();
            originalCal = Calendar.getInstance(TimeZone.getTimeZone(zoneIdRemote));
            originalCal.set(Calendar.AM_PM, Calendar.AM);
            originalCal.set(year, month, date, hour, minute, second);
            originalCal.set(Calendar.MILLISECOND, 0);
            ts.setTsLTZ(originalCal);
            em.persist(ts);
            em.flush();
            tsId = ts.getId();
            commitTransaction(em);
            closeEntityManager(em);
            clearCache();
            em = createEntityManager("timestamptz");
            dbCal = em.find(TStamp.class, tsId).getTsLTZ();
            int hourDiffFromDB = dbCal.get(Calendar.HOUR_OF_DAY) - originalCal.get(Calendar.HOUR_OF_DAY);
            int hourDiffFromZone = (dbCal.get(Calendar.ZONE_OFFSET) - originalCal.get(Calendar.ZONE_OFFSET))/ 3600000;
            assertTrue("The yhour is not macth", (hourDiffFromZone + dbCal.get(Calendar.DST_OFFSET)/3600000) == hourDiffFromDB);
        } finally {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

}
