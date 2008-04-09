/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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


import java.util.Enumeration;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.oracle.Oracle9Platform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.types.CalendarToTSTZWithoutSessionTZTest;

public class OracleTIMESTAMPTypeTestModel extends org.eclipse.persistence.testing.framework.TestModel {
    protected boolean useAccessors;

    public OracleTIMESTAMPTypeTestModel() {
        this(true);
    }

    public OracleTIMESTAMPTypeTestModel(boolean usingAccessors) {
        useAccessors = usingAccessors;
        setName(toString());
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
                ((AbstractSession)getSession()).getAccessor().incrementCallCount((AbstractSession)getSession());
                String driverVersion=((AbstractSession)getSession()).getAccessor().getConnection().getMetaData().getDriverVersion();
                ((AbstractSession)getSession()).getAccessor().decrementCallCount();
                //The combination of driver version of 9.2.0.4 or lower and JDK14 or up would cause
                //an exception, and therefore is not tested
                if (driverVersion.indexOf("9.2.0.4") == -1) {
                    //Oracle 9.2.0.x oci dirver causes an exception, and therefore is not tested. Bug 4483904

                    if (driverVersion.indexOf("9.2") == -1 || 
                        getSession().getLogin().getDatabaseURL().indexOf("oci") == -1) {
                        addTest(getTIMESTAMPTestSuite());
                        addTest(getTIMESTAMPWithBindingTestSuite());
                        addTest(getTIMESTAMPUsingNativeSQLTestSuite());
                    }
                    if (!useAccessors) {
                        //Oracle 9.2.0.x oci driver causes an exception, and therefore is not tested. Bug 4483904
                        if (driverVersion.indexOf("9.2") == 
                            -1 || getSession().getLogin().getDatabaseURL().indexOf("oci") == -1) {
                            addTest(getTIMESTAMPTCTestSuite());
                            addTest(getTIMESTAMPTCWithBindingTestSuite());
                            addTest(getTIMESTAMPTCUsingNativeSQLTestSuite());
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

    public static TestSuite getTIMESTAMPTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPDirectToFieldTester.testInstances().elements();

        suite.setName("TIMESTAMP & TIMESTAMPTZ Types Test Suite");
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ with TopLink");

        while (examples.hasMoreElements()) {
            WriteTypeObjectTest wTest = new WriteTypeObjectTest((TypeTester)examples.nextElement());
            wTest.setShouldBindAllParameters(false);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPWithBindingTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPDirectToFieldTester.testInstances1().elements();

        suite.setName("TIMESTAMP & TIMESTAMPTZ Types with binding Test Suite");
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ with binding with TopLink");

        while (examples.hasMoreElements()) {
            WriteTypeObjectTest wTest = new WriteTypeObjectTest((TypeTester)examples.nextElement());
            wTest.setShouldBindAllParameters(true);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPUsingNativeSQLTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPDirectToFieldTester.testInstances2().elements();

        suite.setName("TIMESTAMP & TIMESTAMPTZ Types using native sql Test Suite");
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ using native sql with TopLink");

        while (examples.hasMoreElements()) {
            WriteTypeObjectTest wTest = new WriteTypeObjectTest((TypeTester)examples.nextElement());
            wTest.setShouldBindAllParameters(false);
            wTest.setUseNativeSQL(true);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPTCTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPTypeConversionTester.testInstances().elements();

        suite.setName("TIMESTAMP & TIMESTAMPTZ Type Conversion Test Suite");
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ/TIMESTAMPLTZ with type conversion");

        while (examples.hasMoreElements()) {
            suite.addTest(new WriteTypeObjectTest((TypeTester)examples.nextElement()));
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPTCWithBindingTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPTypeConversionTester.testInstances1().elements();

        suite.setName("TIMESTAMP & TIMESTAMPTZ with Type Conversion and with binding Test Suite");
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ with binding with type conversion");

        while (examples.hasMoreElements()) {
            WriteTypeObjectTest wTest = new WriteTypeObjectTest((TypeTester)examples.nextElement());
            wTest.setShouldBindAllParameters(true);
            suite.addTest(wTest);
        }
        return suite;
    }

    public static TestSuite getTIMESTAMPTCUsingNativeSQLTestSuite() {
        TestSuite suite = new TestSuite();
        Enumeration examples = TIMESTAMPTypeConversionTester.testInstances2().elements();

        suite.setName("TIMESTAMP & TIMESTAMPTZ with Type Conversion and with native sql Test Suite");
        suite.setDescription("Tests the use of TIMESTAMP/TIMESTAMPTZ using native sql with type conversion");

        while (examples.hasMoreElements()) {
            WriteTypeObjectTest wTest = new WriteTypeObjectTest((TypeTester)examples.nextElement());
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

    public void setup() {
        DatabaseSession session = (DatabaseSession)getSession();
        if (useAccessors) {
            if (getSession().getPlatform() instanceof Oracle9Platform) {
                session.addDescriptor(TIMESTAMPDirectToFieldTester.descriptorWithAccessors());
            }
        } else {
            if (getSession().getPlatform() instanceof Oracle9Platform) {
                session.addDescriptor(TIMESTAMPDirectToFieldTester.descriptor());
                session.addDescriptor(TIMESTAMPTypeConversionTester.descriptor());
                session.addDescriptor(CalendarToTSTZWithoutSessionTZTest.descriptor());
                session.addDescriptor(TIMESTAMPTZOwner.descriptor());
            }
        }
    }

    public String toString() {
        if (useAccessors) {
            return super.toString() + " with Accessors";
        } else {
            return super.toString() + " without Accessors";
        }
    }
}
