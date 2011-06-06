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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.types;

import java.text.*;
import java.util.*;

import oracle.sql.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.platform.database.oracle.*;

import org.eclipse.persistence.testing.framework.*;

/**
 * EL Bug 249500 - Daylight savings time is not printed in native SQL run against Oracle 9i and above
 * Regression test for validating that when a Calendar is inserted with native SQL enabled and binding
 * disabled, TZR TZD is printed when the Calendar reports that it is in DST (daylight savings time).
 * For other scenarios (DST not present, DST not used, DST not used anymore), only TZR is printed.  
 * 
 * Fix involves:
 * - Adding a method: TIMESTAMPHelper.shouldAppendDaylightTime() to determine if TZD data should be
 *   printed in addition to TZR data.
 * - Changed TIMESTAMPHelper.printCalendar() to check TIMESTAMPHelper.shouldAppendDaylightTime() and
 *   print the relevant data for the TZR TZD / TZR element(s).
 * - Changed Oracle9Platform.appendCalendar() to print TZR TZD or TZR depending on whether the
 *   Calendar instance passed is in DST or not.
 */
public class CalendarDaylightSavingsTest extends TestCase {

    // persistent fields
    private int testId;
    private Calendar calendar;
    
    // transient fields
    private QuerySQLTracker sqlTracker;
    private boolean oldBindingValue;
    private boolean oldNativeSqlValue;
    private CalendarDaylightSavingsTest result;

    // static test data    
    private static List<Calendar> testCalendars;

    public CalendarDaylightSavingsTest() {
        super();
    }
    
    public CalendarDaylightSavingsTest(int testId, Calendar calendar) {
        super();
        setTestId(testId);
        setCalendar(calendar);
        setDescription(super.getDescription() + " -> " + formatCalendarAsString(calendar));
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
    
    public int getTestId() {
        return testId;
    }
    
    public void setTestId(int testId) {
        this.testId = testId;
    }
    
    /**
     * Fancy mechanism for creating test Calendars for each TimeZone:
     * - 10 years in the past
     * - 10 years in the future
     * - this current date
     */
    public static List<Calendar> getTestCalendars() {
        if (testCalendars == null) {
            final String[] timeZones = new String[] {
                "US/Eastern",
                "US/Pacific",
                "EST",
                "PST",
                "GMT",
                "UTC",
                "America/New_York",
                "America/Los_Angeles",
                "Asia/Tokyo",
                "Europe/London",
                "Australia/Sydney", // uses DST
                "Australia/Brisbane", // used to use DST
                "Asia/Calcutta", // used to use DST
                "Asia/Bangkok", // never used DST
                "Asia/Riyadh", // never used DST
                "Asia/Jayapura", // never used DST
            };
            
            testCalendars = new ArrayList<Calendar>(timeZones.length * 4);
            for (int i = 0; i < timeZones.length; i++) {
            
                int[] months = new int[] { 0, -6 }; // - 6 months and current month
                for (int j = 0; j < months.length; j++) {
                
                    int[] years = new int[] { -10, 0, 10 }; // -10 years, current year and +10 years
                    for (int k = 0; k < years.length; k++) {
                        TimeZone timeZone = TimeZone.getTimeZone(timeZones[i]);
                        Calendar cal = Calendar.getInstance(timeZone);
                        cal.set(Calendar.DATE, 1);
                        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + months[j]);
                        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + years[k]);
                        cal.set(Calendar.HOUR, 1);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        cal.set(Calendar.AM_PM, Calendar.PM);
                        testCalendars.add(cal);
                    }
                }
            }
        }
        return testCalendars;
    }
    
    public String toString() {
        return "Test #: " + getTestId() + " -> " + formatCalendarAsString(this.calendar);
    }

    /**
     * Return a test instance for every item of test data 
     */
    public static Vector testInstancesWithNoBindingAndNativeSql() {
        Vector tests = new Vector(getTestCalendars().size());
        for (int i = 0; i < getTestCalendars().size(); i++) {
            tests.add(new CalendarDaylightSavingsTest(i + 1, getTestCalendars().get(i)));
        }
        return tests;
    }
    
    public void setup() {
        if (!(getSession().getPlatform() instanceof Oracle9Platform)) {
            throw new TestWarningException("Test is only supported on Oracle9 platform and above, as TIMESTAMPTZ is used");
        }
        Oracle9Platform platform = (Oracle9Platform) getSession().getPlatform();
        
        this.oldBindingValue = platform.shouldBindAllParameters();
        this.oldNativeSqlValue = platform.usesNativeSQL();
        
        // parameter binding must be off and native SQL must be on
        platform.setShouldBindAllParameters(false);
        platform.setUsesNativeSQL(true);
        
        // delete myself if I exist because we need to perform an insert not an update
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            Object objectToDelete = uow.readObject(
                getClass(), 
                new ExpressionBuilder().get("testId").equal(getTestId()));
            if (objectToDelete != null) {
                uow.deleteObject(objectToDelete);
                uow.commit();
            }
        } catch (Exception e) {
            throw new TestWarningException("Error whilst removing test object: " + getTestId(), e);
        }

        // create tracker object for query SQL
        sqlTracker = new QuerySQLTracker(getSession());
    }
    
    public void test() {
        // write myself out to the database
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(this);
        uow.commit();
        
        getSession().getIdentityMapAccessor().initializeIdentityMap(getClass());

        // read myself back from the database (bypassing the cache) with my id
        // this is to check that the object was correctly written to the database and that it exists
        ReadObjectQuery query = new ReadObjectQuery(CalendarDaylightSavingsTest.class);
        ExpressionBuilder builder = query.getExpressionBuilder();
        Expression expression = builder.get("testId").equal(getTestId());
        query.setSelectionCriteria(expression);
        query.setCacheUsage(query.DoNotCheckCache);
        
        // cache the result for later verification
        result = (CalendarDaylightSavingsTest)getSession().executeQuery(query);
    }
    
    public void verify() {
        final String TZR = "TZR";
        final String TZR_TZD = "TZR TZD";
        
        // add in check to determine if the SQL executed has the correct TZD and TZR fields in it
        List<String> statements = sqlTracker.getSqlStatements();
        if (statements.isEmpty()) {
            throw new TestErrorException("Test: " + getTestId() + " - No statements executed!");
        }


        if (this.result == null) {
            throw new TestErrorException("Result from database was null for test id: " + getTestId());
        }
        
        // get the SQL statement that was executed
        String statement = statements.get(0).toUpperCase();
        
        // if the TimeZone is in daylight time, we expect that the statement contains 'TZR TZD'
        // if the TimeZone is not in daylight time, we expect that the statement contains only 'TZR'
        TimeZone timeZone = calendar.getTimeZone();
        Date date = calendar.getTime();
        if (timeZone.inDaylightTime(date) && !statement.contains(TZR_TZD)) {
            throw new TestErrorException("Test: " + getTestId() + " - Expected sql statement string with: " + TZR_TZD + " -> " + statement); 
        } else if (!timeZone.inDaylightTime(date) && !statement.contains(TZR)) {
            throw new TestErrorException("Test: " + getTestId() + " - Expected sql statement string with: " + TZR + " -> " + statement);
        }
    }
    
    public void reset() {
        // Compatibility for Oracle 9 and above is checked in the setup() method
        Oracle9Platform platform = (Oracle9Platform) getSession().getPlatform();
        platform.setUsesNativeSQL(this.oldNativeSqlValue);
        platform.setShouldBindAllParameters(this.oldBindingValue);
        // reset the SQL tracker
        if (sqlTracker != null) {
            sqlTracker.remove();
            sqlTracker = null;
        }
    }

    // Calendar pretty printing, not used for DB operations

    protected static String formatCalendarAsString(Calendar cal) {
        StringBuffer buffer = new StringBuffer();

        TimeZone zone = cal.getTimeZone();
        
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        sdf.setCalendar(cal);
        sdf.setTimeZone(zone);
        buffer.append(sdf.format(cal.getTime()));
        
        buffer.append(" " );
        buffer.append(zone.getID());

        // SimpleDateFormat is not good enough to print exactly what I need to see from the TimeZone
        if (zone.inDaylightTime(cal.getTime())) {
            buffer.append(" ");
            buffer.append(zone.getDisplayName(true, TimeZone.SHORT));
        }
        
        return buffer.toString();
    }
    
    /*
    public static void main(String args[]) {
        List<Calendar> calendarz = getTestCalendars();
        Iterator<Calendar> i = calendarz.iterator();
        while (i.hasNext()) {
            Calendar cal = i.next();
            System.out.println(formatCalendarAsString(cal));
        }
    }*/

    // Descriptor and Table information

    private static RelationalDescriptor commonDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
	descriptor.setJavaClass(CalendarDaylightSavingsTest.class);
	descriptor.setTableName(tableDefinition().getName());
	descriptor.setPrimaryKeyFieldName("TEST_ID");
	descriptor.addDirectMapping("testId", "getTestId", "setTestId", "TEST_ID");
        return descriptor;
    }

    public static RelationalDescriptor descriptorWithAccessors() {
        RelationalDescriptor descriptor = commonDescriptor();
	descriptor.addDirectMapping("calendar", "getCalendar", "setCalendar", "TSTZ_DATA");
        return descriptor;
    }
    
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = commonDescriptor();
	descriptor.addDirectMapping("calendar", "TSTZ_DATA");
        return descriptor;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName("CALENDARTOTSTZ_NATIVE");
        definition.addField("TEST_ID", Integer.class);
        definition.addField("TSTZ_DATA", TIMESTAMPTZ.class);
        return definition;
    }
    
}
