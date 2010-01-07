/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      ailitchev ported the original test written by dminsky
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.types;

import java.text.*;
import java.util.*;

import oracle.sql.*;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.platform.database.oracle.*;

import org.eclipse.persistence.testing.framework.*;

/**
 * Testcase for resolution of issues with TIMESTAMPTZ compatibility and correctness.
 * @author dminsky
 */
public class CalendarToTSTZWithBindingTest extends TestCase {

    // persistent fields
    private Calendar calendar;
    private int testId;
    
    // transient fields
    private boolean printCalendarIntoTimestampTZ;
    private boolean oldBindingValue;
    private Boolean oldPrintingValue;
    private DatabaseRecord result;

    // test data
    private String originalCalendarString;
    private static final String[] calendarStrings = new String[] {
        "01/01/2006 00:00:00 US/Eastern",
        "01/01/2006 00:00:00 US/Pacific",

        "04/01/2006 19:00:00 US/Eastern",
        "04/01/2006 19:00:00 US/Pacific",

        "04/01/2006 20:59:00 US/Eastern",
        "04/01/2006 20:59:00 US/Pacific",

        "04/01/2006 21:00:00 US/Eastern",
        "04/01/2006 21:00:00 US/Pacific",
    
        "04/01/2006 21:01:00 US/Eastern",
        "04/01/2006 21:01:00 US/Pacific",
    
        "04/01/2006 22:00:00 US/Eastern",
        "04/01/2006 22:00:00 US/Pacific",
    
        "10/29/2006 01:00:00 US/Eastern",
        "10/29/2006 01:00:00 PST",
    
        "10/29/2006 01:00:00 EST", 
        "10/29/2006 01:00:00 PST",
            
        "03/10/2007 21:00:00 EST",
        "03/10/2007 21:00:00 PST",
            
        "03/10/2007 22:00:00 EST",
        "03/10/2007 22:00:00 PST",
            
        "10/28/2007 01:00:00 US/Eastern",
        "10/28/2007 01:00:00 PST",
    
        "11/04/2007 01:00:00 EST",
        "11/04/2007 01:00:00 PST",
            
        "12/31/2007 23:00:00 US/Eastern",
        "12/31/2007 23:00:00 US/Pacific"
    };

    public CalendarToTSTZWithBindingTest() {
        super();
    }
    
    public CalendarToTSTZWithBindingTest(int testId, String calendarString, boolean printCalendarIntoTimestampTZ) {
        super();
        this.printCalendarIntoTimestampTZ = printCalendarIntoTimestampTZ;
        setTestId(testId);
        setOriginalCalendarString(calendarString);
        setCalendar(formatStringAsCalendar(calendarString));
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
    
    public void setOriginalCalendarString(String originalCalendarString) {
        this.originalCalendarString = originalCalendarString;
    }
    
    public String getOriginalCalendarString() {
        return this.originalCalendarString;
    }
    
    public String toString() {
        return "Test #: " + getTestId() + " -> " + getOriginalCalendarString();
    }

/*    public static Vector testInstancesWithBindingAndCalendarPrinting() {
        Vector tests = new Vector(calendarStrings.length);
        for (int i = 0; i < calendarStrings.length; i++) {
            tests.add(new CalendarToTSTZWithBindingTest(i + 1, calendarStrings[i], true));
        }
        return tests;
    }*/
    
    public static Vector testInstancesWithBindingAndNoCalendarPrinting() {
        Vector tests = new Vector(calendarStrings.length);
        for (int i = 0; i < calendarStrings.length; i++) {
            tests.add(new CalendarToTSTZWithBindingTest(i + (calendarStrings.length + 1), calendarStrings[i], false));
        }
        return tests;
    }
    
    public void setup() {
        if (!(getSession().getPlatform() instanceof Oracle9Platform)) {
            throw new TestWarningException("Test is only supported on Oracle9 platform and above, as TIMESTAMPTZ is used");
        }
        Oracle9Platform platform = (Oracle9Platform) getSession().getPlatform();
        
//        this.oldPrintingValue = platform.getPrintCalendarIntoTimestampTZ();
        this.oldBindingValue = platform.shouldBindAllParameters();
        
//        platform.setPrintCalendarIntoTimestampTZ(Boolean.TRUE);
        platform.setShouldBindAllParameters(true);
        
        // write myself out
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(this);
        uow.commit();
    }
    
    public void test() {
        // read data back from database literally in order to check contents of database rather than the driver's TIMESTAMPTZ
        String sql = "select TEST_ID, to_char(TSTZ_DATA,'MM/DD/YYYY hh24:mi:ss TZR') as TSTZ_DATA FROM " +  commonDescriptor().getTableName() +  " where TEST_ID = " + getTestId();
        Vector result = getSession().executeSQL(sql);
        if (result != null & !result.isEmpty()) {
            this.result = (DatabaseRecord) result.firstElement();
        }
    }
    
    public void verify() throws Exception {
        String expectedResult = getOriginalCalendarString();
        if (this.result == null) {
            throw new TestErrorException("Unexpected exception - database returned no data for test id: " + getTestId() + " expected: " + expectedResult);
        }
        String actualResult = (String) result.get("TSTZ_DATA");
        
        if (!expectedResult.equalsIgnoreCase(actualResult)) {
            throw new TestErrorException("Data from database is not equal for test id: "  + getTestId() + " - got: " + actualResult + " expected: " + expectedResult);
        }
    }
    
    public void reset() {
        // Compatibility for Oracle 9 and above is checked in the setup() method
        Oracle9Platform platform = (Oracle9Platform) getSession().getPlatform();
//        platform.setPrintCalendarIntoTimestampTZ(this.oldPrintingValue);
        platform.setShouldBindAllParameters(this.oldBindingValue);

        // delete myself
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.deleteObject(this);
        uow.commit();
    }

    // Calendar formatting behaviour
   
    protected Calendar formatStringAsCalendar(String calendarString) {
        try {
            String dateOnly = calendarString.substring(0,19);
            String timeZoneStr = calendarString.substring(20,calendarString.length());
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            simpleDateFormat.setTimeZone(timeZone);
            Date referenceDate = simpleDateFormat.parse(dateOnly.toUpperCase());
            Calendar referenceCalendar = new GregorianCalendar(timeZone);
            referenceCalendar.setTime(referenceDate);
            simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
            simpleDateFormat.setTimeZone(timeZone);
            return referenceCalendar;
        } catch (ParseException p) {
            throw new TestErrorException("Could not parse Calendar String: " + calendarString);
        }
    }

    // Descriptor and Table information

    private static RelationalDescriptor commonDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
    descriptor.setJavaClass(CalendarToTSTZWithBindingTest.class);
    descriptor.setTableName("CALENDARTOTSTZ_BINDING");
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
        definition.setName("CALENDARTOTSTZ_BINDING");
        definition.addField("TEST_ID", Integer.class);
        definition.addField("TSTZ_DATA", TIMESTAMPTZ.class);
        return definition;
    }
}
