/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.TemporalType;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimePopulator;
import org.eclipse.persistence.testing.models.jpa.datetime.DateTimeTableCreator;

//Test all kinds of combinations of date time types
public class JUnitJPQLDateTimeTestSuite extends JUnitTestCase {
    public JUnitJPQLDateTimeTestSuite() {
        super();
    }

    public JUnitJPQLDateTimeTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("JUnitJPQLDateTimeTestSuite");
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testSetup"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testSqlDate"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testSqlDateToTS"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTime"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimeToTS"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimestamp"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimestampToDate"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimestampToTime"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testUtilDate"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testCalenderWithUtilDate"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testSqlDateWithCal"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimeWithCal"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimestampWithCal"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testCalendar"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimestampGreaterThan"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimestampLessThan"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimestampIn"));
        suite.addTest(new JUnitJPQLDateTimeTestSuite("testTimestampBetween"));

        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new DateTimeTableCreator().replaceTables(JUnitTestCase.getServerSession());
                DateTimePopulator dateTimePopulator = new DateTimePopulator();                
                dateTimePopulator.persistExample(getServerSession());   
        clearCache();
    }
    
    public void testSqlDate() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.date = :date").
            setParameter("date", cal.getTime(), TemporalType.DATE).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testSqlDateToTS() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.date = :date").
            setParameter("date", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testTime() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.time = :time").
            setParameter("time", cal.getTime(), TemporalType.TIME).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testTimeToTS() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.time = :time").
            setParameter("time", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testTimestamp() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp = :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testTimestampToDate() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp = :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.DATE).
            getResultList();
        
        assertTrue("There should be zero result", result.size() == 0);
    }

    public void testTimestampToTime() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp = :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.TIME).
            getResultList();
        
        assertTrue("There should be zero result", result.size() == 0);
    }

   public void testUtilDate() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.utilDate = :utilDate").
            setParameter("utilDate", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testCalenderWithUtilDate() {
         GregorianCalendar cal = new GregorianCalendar(); 
         cal.set(1901, 11, 31, 23, 59, 59);
         cal.set(Calendar.MILLISECOND, 999);

         List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.calendar = :calendar").
             setParameter("calendar", cal.getTime(), TemporalType.TIMESTAMP).
             getResultList();
         
         assertTrue("There should be one result", result.size() == 1);
     }

    public void testSqlDateWithCal() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.date = :date").
            setParameter("date", cal, TemporalType.DATE).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testTimeWithCal() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.time = :time").
            setParameter("time", cal, TemporalType.TIME).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testTimestampWithCal() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp = :timestamp").
            setParameter("timestamp", cal, TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testUtilDateWithCal() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.utilDate = :utilDate").
            setParameter("utilDate", cal, TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testCalendar() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.calendar = :calendar").
            setParameter("calendar", cal, TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be one result", result.size() == 1);
    }

    public void testTimestampGreaterThan() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp > :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be three result", result.size() == 3);
    }

    public void testTimestampLessThan() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(2001, 6, 1, 3, 45, 32);
        cal.set(Calendar.MILLISECOND, 87);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp < :timestamp").
            setParameter("timestamp", cal.getTime(), TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be three result", result.size() == 2);
    }

//IN node is going to be fixed and then this test will run
    public void testTimestampIn() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        GregorianCalendar cal2 = new GregorianCalendar(); 
        cal2.set(2001, 6, 1, 3, 45, 32);
        cal2.set(Calendar.MILLISECOND, 87);

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp IN (:timestamp1, :timestamp2)").
            setParameter("timestamp1", cal.getTime(), TemporalType.TIMESTAMP).
            setParameter("timestamp2", cal2, TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be two result", result.size() == 2);
    }

    public void testTimestampBetween() {
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.set(1901, 11, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);

        Calendar cal2 = Calendar.getInstance(); 

        List result = createEntityManager().createQuery("SELECT OBJECT(o) FROM DateTime o WHERE o.timestamp BETWEEN :timestamp1 AND :timestamp2").
            setParameter("timestamp1", cal.getTime(), TemporalType.TIMESTAMP).
            setParameter("timestamp2", cal2, TemporalType.TIMESTAMP).
            getResultList();
        
        assertTrue("There should be four result", result.size() == 4);
    }

}
