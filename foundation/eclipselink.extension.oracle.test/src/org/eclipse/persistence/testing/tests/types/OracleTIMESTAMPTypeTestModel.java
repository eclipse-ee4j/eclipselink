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


import java.sql.Connection;
import java.util.Enumeration;
import java.util.TimeZone;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.types.CalendarToTSTZWithoutSessionTZTest;

public class OracleTIMESTAMPTypeTestModel extends org.eclipse.persistence.testing.framework.TestModel {
    protected boolean useAccessors;
    public static String defaultTimeZone = TimeZone.getDefault().getID();
    public static String alternativeTimeZone = "America/Los_Angeles";
    static {
        if(defaultTimeZone.equals(alternativeTimeZone)) {
            alternativeTimeZone = "America/Chicago";
        }
    }
    
    public OracleTIMESTAMPTypeTestModel() {
        this(true);
    }

    public OracleTIMESTAMPTypeTestModel(boolean usingAccessors) {
        useAccessors = usingAccessors;
        if (useAccessors) {
            setDescription("Testing of Oracle-specific types and their mappings in TopLink (using Accessors)");
        } else {
            setDescription("Testing of Oracle-specific types and their mappings in TopLink (without Accessors)");
        }
    }

    public void addRequiredSystems() {
        addRequiredSystem(new OracleTIMESTAMPTypeTestSystem());
    }

    public void addTests() {
        if (getSession().getPlatform() instanceof Oracle9Platform) {
            try {
                Oracle9Platform platform = (Oracle9Platform)getSession().getPlatform();
                ((AbstractSession)getSession()).getAccessor().incrementCallCount((AbstractSession)getSession());
                // unwrap connection if it's wrapped
                Connection conn = platform.getConnection((AbstractSession)getSession(), ((AbstractSession)getSession()).getAccessor().getConnection());
                String driverVersion= platform.getDriverVersion(conn);
                // isTimestampInGmt==true if driverVersion is 11.1.0.7 or later and
                // oracleConnection's property "oracle.jdbc.timestampTzInGmt" is set to "true".
                TIMESTAMPTester.isTimestampInGmt = platform.isTimestampInGmt(conn);
                TIMESTAMPTester.isLtzTimestampInGmt = platform.isLtzTimestampInGmt(conn);
                ((AbstractSession)getSession()).getAccessor().decrementCallCount();
                //The combination of driver version of 9.2.0.4 or lower and JDK14 or up would cause
                //an exception, and therefore is not tested
                if (driverVersion.indexOf("9.2.0.4") == -1) {
                    //Oracle 9.2.0.x OCI driver causes an exception, and therefore is not tested. Bug 4483904

                    if (driverVersion.indexOf("9.2") == -1 || 
                        getSession().getLogin().getDatabaseURL().indexOf("oci") == -1) {
                        // set default time zone as a session time zone
                        addTest(getTIMESTAMPTestSuite(true));
                        addTest(getTIMESTAMPWithBindingTestSuite(true));
                        addTest(getTIMESTAMPUsingNativeSQLTestSuite(true));
                        addTest(getTIMESTAMPTestSuite(false));
                        addTest(getTIMESTAMPWithBindingTestSuite(false));
                        addTest(getTIMESTAMPUsingNativeSQLTestSuite(false));
                        // Known to pass with Oracle jdbc 11.2.0.0.2, fail with 11.1.0.7, 11.1.0.6
                        // Even with ojdbc 11.2.0.0.2 fails on db 9.2.0.1, but passes on 10.2.0.4, 11.1.0.6.0, 11.1.0.7.
                        if(Helper.compareVersions(driverVersion, "11.2.0.0.2") >= 0) {
                            addTest(getCalToTSTZWithBindingAndNoCalendarPrintingTestSuite());
                        }
                        addTest(getCalendarDaylightSavingsTestSuite());
                    }
                    if (!useAccessors) {
                        //Oracle 9.2.0.x OCI driver causes an exception, and therefore is not tested. Bug 4483904
                        if (driverVersion.indexOf("9.2") == 
                            -1 || getSession().getLogin().getDatabaseURL().indexOf("oci") == -1) {
                            addTest(getTIMESTAMPTCTestSuite(true));
                            addTest(getTIMESTAMPTCWithBindingTestSuite(true));
                            addTest(getTIMESTAMPTCUsingNativeSQLTestSuite(true));
                            addTest(getTIMESTAMPTCTestSuite(false));
                            addTest(getTIMESTAMPTCWithBindingTestSuite(false));
                            addTest(getTIMESTAMPTCUsingNativeSQLTestSuite(false));
                        }
                        addTest(new SerializationOfValueHolderWithTIMESTAMPTZTest());
                    }
                }
            } catch (java.sql.SQLException e) {

            }
            if (!useAccessors) {
                addTest(getCalToTSTZTestSuite());
            }
        }
    }

    public static String getTimeZone(boolean useDefaultTimeZone) {
        String timeZone;
        if(useDefaultTimeZone) {
            timeZone = defaultTimeZone;
        } else {
            timeZone = alternativeTimeZone;
        }
        return timeZone;
    }
    
    public static String getTimeZoneInfo(String timeZone) {
        if(timeZone == null || timeZone.length()==0) {
            return " without time zone";
        } else {
            return " with " + (defaultTimeZone.equals(timeZone) ? "default" : "alternative") + " time zone " + timeZone;
        }
    }
    
    public static TestSuite getTIMESTAMPTestSuite(boolean useDefaultTimeZone) {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPDirectToFieldTester.testInstances().elements();

        String timeZone = getTimeZone(useDefaultTimeZone);
        suite.setName("TIMESTAMP & TIMESTAMPTZ Types Test Suite" + getTimeZoneInfo(timeZone));
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ with TopLink");

        while (examples.hasMoreElements()) {
            TIMESTAMPTester  tester = (TIMESTAMPTester)examples.nextElement();
            tester.setSessionTimezone(timeZone);
            WriteTypeObjectTest wTest = new WriteTypeObjectTest(tester);
            wTest.setShouldBindAllParameters(false);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPWithBindingTestSuite(boolean useDefaultTimeZone) {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPDirectToFieldTester.testInstances1().elements();

        String timeZone = getTimeZone(useDefaultTimeZone);
        suite.setName("TIMESTAMP & TIMESTAMPTZ Types with binding Test Suite" + getTimeZoneInfo(timeZone));
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ with binding with TopLink");

        while (examples.hasMoreElements()) {
            TIMESTAMPTester  tester = (TIMESTAMPTester)examples.nextElement();
            tester.setSessionTimezone(timeZone);
            WriteTypeObjectTest wTest = new WriteTypeObjectTest(tester);
            wTest.setShouldBindAllParameters(true);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPUsingNativeSQLTestSuite(boolean useDefaultTimeZone) {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPDirectToFieldTester.testInstances2().elements();

        String timeZone = getTimeZone(useDefaultTimeZone);
        suite.setName("TIMESTAMP & TIMESTAMPTZ Types using native sql Test Suite" + getTimeZoneInfo(timeZone));
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ using native sql with TopLink");

        while (examples.hasMoreElements()) {
            TIMESTAMPTester  tester = (TIMESTAMPTester)examples.nextElement();
            tester.setSessionTimezone(timeZone);
            WriteTypeObjectTest wTest = new WriteTypeObjectTest(tester);
            wTest.setShouldBindAllParameters(false);
            wTest.setUseNativeSQL(true);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPTCTestSuite(boolean useDefaultTimeZone) {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPTypeConversionTester.testInstances().elements();

        String timeZone = getTimeZone(useDefaultTimeZone);
        suite.setName("TIMESTAMP & TIMESTAMPTZ Type Conversion Test Suite" + getTimeZoneInfo(timeZone));
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ/TIMESTAMPLTZ with type conversion");

        while (examples.hasMoreElements()) {
            TIMESTAMPTester  tester = (TIMESTAMPTester)examples.nextElement();
            tester.setSessionTimezone(timeZone);
            suite.addTest(new WriteTypeObjectTest(tester));
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPTCWithBindingTestSuite(boolean useDefaultTimeZone) {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPTypeConversionTester.testInstances1().elements();

        String timeZone = getTimeZone(useDefaultTimeZone);
        suite.setName("TIMESTAMP & TIMESTAMPTZ with Type Conversion and with binding Test Suite" + getTimeZoneInfo(timeZone));
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ with binding with type conversion");

        while (examples.hasMoreElements()) {
            TIMESTAMPTester  tester = (TIMESTAMPTester)examples.nextElement();
            tester.setSessionTimezone(timeZone);
            WriteTypeObjectTest wTest = new WriteTypeObjectTest(tester);
            wTest.setShouldBindAllParameters(true);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPTCUsingNativeSQLTestSuite(boolean useDefaultTimeZone) {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPTypeConversionTester.testInstances2().elements();

        String timeZone = getTimeZone(useDefaultTimeZone);
        suite.setName("TIMESTAMP & TIMESTAMPTZ with Type Conversion and with native sql Test Suite" + getTimeZoneInfo(timeZone));
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ using native sql with type conversion");

        while (examples.hasMoreElements()) {
            TIMESTAMPTester  tester = (TIMESTAMPTester)examples.nextElement();
            tester.setSessionTimezone(timeZone);
            WriteTypeObjectTest wTest = new WriteTypeObjectTest(tester);
            wTest.setUseNativeSQL(true);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getCalToTSTZTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = CalendarToTSTZWithoutSessionTZTest.testInstances().elements();

        suite.setName("Calendar to TIMESTAMPTZ Test Suite");
        suite.setDescription("Tests if Calendar to TIMESTAMPTZ without session timezone works properly");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    public static TestSuite getCalToTSTZWithBindingAndNoCalendarPrintingTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = CalendarToTSTZWithBindingTest.testInstancesWithBindingAndNoCalendarPrinting().elements();

        suite.setName("Calendar to TIMESTAMPTZ Test Suite With Binding, No Calendar Printing");
        suite.setDescription("Tests if Calendar to TIMESTAMPTZ with binding and direct Calendar usage works properly");

        while (examples.hasMoreElements()) {
            suite.addTest((CalendarToTSTZWithBindingTest)examples.nextElement());
        }
        return suite;
    }
    
    public static TestSuite getCalendarDaylightSavingsTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = CalendarDaylightSavingsTest.testInstancesWithNoBindingAndNativeSql().elements();

        suite.setName("Calendar Daylight Savings Test Suite");
        suite.setDescription("Tests if daylight savings-aware calendar printing works properly");

        while (examples.hasMoreElements()) {
            suite.addTest((TestCase)examples.nextElement());
        }
        return suite;
    }
    
    public void setup() {
        DatabaseSession session = (DatabaseSession)getSession();
        if (useAccessors) {
            if (getSession().getPlatform() instanceof Oracle9Platform) {
                session.addDescriptor(TIMESTAMPDirectToFieldTester.descriptorWithAccessors());
                session.addDescriptor(CalendarToTSTZWithBindingTest.descriptorWithAccessors());
                session.addDescriptor(CalendarDaylightSavingsTest.descriptorWithAccessors());
            }
        } else {
            if (getSession().getPlatform() instanceof Oracle9Platform) {
                session.addDescriptor(TIMESTAMPDirectToFieldTester.descriptor());
                session.addDescriptor(TIMESTAMPTypeConversionTester.descriptor());
                session.addDescriptor(CalendarToTSTZWithoutSessionTZTest.descriptor());
                session.addDescriptor(TIMESTAMPTZOwner.descriptor());
                session.addDescriptor(CalendarToTSTZWithBindingTest.descriptor());
                session.addDescriptor(CalendarDaylightSavingsTest.descriptor());
            }
        }
    }
}
