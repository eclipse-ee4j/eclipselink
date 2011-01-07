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
package org.eclipse.persistence.testing.tests.types;

import java.util.*;
import java.sql.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This class will test Date, Time, & Timestamp capabilities
 */
public class TimeDateTester extends TypeTester {
    public java.sql.Date sqlDate;
    public java.util.Date utilDate;
    public Time time;
    public Timestamp timestamp;

    /**
    * Creates a new empty tester. This should only be called by RelationalDescriptor when reading from database row.
    */
    public TimeDateTester() {
        super("NEW");
    }

    public TimeDateTester(String nameOfTest, int year, int month, int date, int hrs, int min, int sec, int nano) {
        super(nameOfTest);
        Calendar c = Calendar.getInstance();
        c.set(year, month, date, hrs, min, sec);
        c.set(Calendar.MILLISECOND, nano / 1000000);
        utilDate = c.getTime();
        sqlDate = Helper.dateFromYearMonthDate(year, month, date);
        time = Helper.timeFromHourMinuteSecond(hrs, min, sec);
        timestamp = Helper.timestampFromYearMonthDateHourMinuteSecondNanos(year, month, date, hrs, min, sec, nano);
    }

    public TimeDateTester(String nameOfTest, Calendar dateTime) {
        this(nameOfTest, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DATE), dateTime.get(Calendar.HOUR), dateTime.get(Calendar.MINUTE), dateTime.get(Calendar.SECOND), 0);
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(TimeDateTester.class);
        descriptor.setTableName("TIMEDATE");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("utilDate", "UTILDATE");
        descriptor.addDirectMapping("sqlDate", "SQLDATE");
        descriptor.addDirectMapping("time", "TTIME");
        descriptor.addDirectMapping("timestamp", "TSTAMP");
        return descriptor;
    }

    public static RelationalDescriptor descriptorWithAccessors() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(TimeDateTester.class);
        descriptor.setTableName("TIMEDATE");
        descriptor.setPrimaryKeyFieldName("NAME");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("testName", "getTestName", "setTestName", "NAME");
        descriptor.addDirectMapping("utilDate", "getUtilDate", "setUtilDate", "UTILDATE");
        descriptor.addDirectMapping("sqlDate", "getSQLDate", "setSQLDate", "SQLDATE");
        descriptor.addDirectMapping("time", "getTime", "setTime", "TTIME");
        descriptor.addDirectMapping("timestamp", "getTimestamp", "setTimestamp", "TSTAMP");
        return descriptor;
    }

    public java.sql.Date getSQLDate() {
        return sqlDate;
    }

    public java.sql.Time getTime() {
        return time;
    }

    public java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public java.util.Date getUtilDate() {
        return utilDate;
    }

    public void setSQLDate(java.sql.Date aDate) {
        sqlDate = aDate;
    }

    public void setTime(java.sql.Time aTime) {
        time = aTime;
    }

    public void setTimestamp(java.sql.Timestamp aTimestamp) {
        timestamp = aTimestamp;
    }

    public void setUtilDate(java.util.Date aDate) {
        utilDate = aDate;
    }

    /**
     *Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition(Session session) {
        TableDefinition definition = TypeTester.tableDefinition();

        definition.setName("TIMEDATE");

        definition.addField("UTILDATE", Timestamp.class);
        definition.addField("SQLDATE", java.sql.Date.class);
        definition.addField("TTIME", Time.class);
        definition.addField("TSTAMP", Timestamp.class);
        return definition;
    }

    static public Vector testInstances() {
        Vector tests = new Vector();
        tests.addElement(new TimeDateTester("NOW", Calendar.getInstance()));
        tests.addElement(new TimeDateTester("ZERO", 1900, 0, 1, 0, 0, 0, 0));// I don't know if this make any sense
        tests.addElement(new TimeDateTester("New Years Eve", 1997, 11, 31, 23, 59, 59, 900000000));
        tests.addElement(new TimeDateTester("New Years Day", 1998, 1, 1, 0, 0, 1, 0));
        tests.addElement(new TimeDateTester("New Years Eve 1999", 1999, 11, 31, 23, 59, 59, 0));
        tests.addElement(new TimeDateTester("New Years Day 2000", 2000, 1, 1, 0, 0, 1, 0));
        tests.addElement(new TimeDateTester("Doug's Birth", 1970, 8, 13, 16, 34, 0, 0));
        tests.addElement(new TimeDateTester("Doug's 100th Birthday", 2070, 8, 13, 16, 34, 0, 0));
        Calendar yearsBack100 = Calendar.getInstance();
        yearsBack100.set(Calendar.YEAR, (yearsBack100.get(Calendar.YEAR) - 100));
        tests.addElement(new TimeDateTester("100 Years ago", yearsBack100));
        Calendar yearsAhead100 = Calendar.getInstance();
        yearsAhead100.set(Calendar.YEAR, (yearsAhead100.get(Calendar.YEAR) + 100));
        tests.addElement(new TimeDateTester("100 Years from now", yearsAhead100));
        tests.addElement(new TimeDateTester("Before 1970", 1902, 8, 13, 16, 34, 25, 900000000));
        return tests;
    }

    public String toString() {
        return getTestName() + " -> " + timestamp;
    }

    protected void verify(WriteTypeObjectTest testCase) throws TestException {
        try {
            super.verify(testCase);
        } catch (TestException e) {
            // JConnect in non native mode
            if ((caughtException != null) && caughtException.toString().equals("EXCEPTION: org.eclipse.persistence.exceptions.DatabaseException \n" + "DESCRIPTION: null \n" + "INTERNAL EXCEPTION: java.sql.SQLException: JZ0S8: An escape sequence in a SQL Query was malformed.\n" + "ERROR CODE: 0\n\n")) {
                throw new TestProblemException("JConnect does not do dates in non-native SQL:\n" + caughtException.getInternalException());
            }

            // Some databases miss handle milliseconds
            Timestamp objectTimeInCache = getTimestamp();
            if (testCase.getObjectFromDatabase() == null) {
                throw e;
            }
            Timestamp objectTimeInDB = ((TimeDateTester)testCase.getObjectFromDatabase()).getTimestamp();
            int nunoSecond = objectTimeInCache.getNanos() / 100000000;
            objectTimeInCache.setNanos(0);
            if (getTimestamp().equals(objectTimeInDB)) {
                throw new TestWarningException("The nanoseconds should be: " + nunoSecond + " but were read back as: " + objectTimeInDB.getNanos());
            }

            throw e;
        }
    }
}
