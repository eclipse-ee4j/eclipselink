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
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.XMLConversionException;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class DateAndTimeTestCases extends OXTestCase {

    private static final String EXCEPTION_NOT_THROWN = "An exception was expected but none were thrown.";
    private static final String WRONG_EXCEPTION_THROWN = "The incorrect exception was thrown.";

    // 1975-02-21
    private long CONTROL_DATE = 162190800000L;

    // 1975-02-21T07:47:15.0
    private long CONTROL_DATE_TIME_0MS = 162218835000L;

    // 1975-02-21T07:47:15.001
    private long CONTROL_DATE_TIME_1MS = 162218835001L;

    // 1975-02-21T07:47:15.01
    private long CONTROL_DATE_TIME_10MS = 162218835010L;

    // 1975-02-21T07:47:15.1
    private long CONTROL_DATE_TIME_100MS = 162218835100L;

    // 07:47:15.0
    private long CONTROL_TIME_0MS = 46035000L;

    // 07:47:15.001
    private long CONTROL_TIME_1MS = 46035001L;

    // 07:47:15.01
    private long CONTROL_TIME_10MS = 46035010L;

    // 07:47:15.1
    private long CONTROL_TIME_100MS = 46035100L;

    // ---21
    private long CONTROL_G_DAY = 1746000000L;

    // --02
    private long CONTROL_G_MONTH = 2696400000L;

    // --02-21
    private long CONTROL_G_MONTH_DAY = 4424400000L;

    // 1975
    private long CONTROL_G_YEAR = 157784400000L;

    // 1975-02
    private long CONTROL_G_YEAR_MONTH = 160462800000L;

    private static final String CONTROL_DST_TIME_ZONE = "US/Eastern";
    private static final String CONTROL_DST_INPUT_DATE_TIME = "2003-08-29T05:00:00-02:00";

    // XML Conversion Manager
    private XMLConversionManager xcm;

    public DateAndTimeTestCases(String name) {
        super(name);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(1975, 1, 21);

        CONTROL_DATE = cal.getTimeInMillis();

        cal.clear();
        cal.set(1975, 1, 21, 7, 47, 15);
        this.CONTROL_DATE_TIME_0MS = cal.getTimeInMillis();
        this.CONTROL_DATE_TIME_100MS = cal.getTimeInMillis() + 100;
        this.CONTROL_DATE_TIME_10MS = cal.getTimeInMillis() + 10;
        this.CONTROL_DATE_TIME_1MS = cal.getTimeInMillis() + 1;

        cal.clear();
        cal.set(1970, 0, 1, 7, 47, 15);

        this.CONTROL_TIME_0MS = cal.getTimeInMillis();
        this.CONTROL_TIME_100MS = cal.getTimeInMillis() + 100;
        this.CONTROL_TIME_10MS = cal.getTimeInMillis() + 10;
        this.CONTROL_TIME_1MS = cal.getTimeInMillis() + 1;

        cal.clear();
        cal.set(Calendar.DAY_OF_MONTH, 21);
        this.CONTROL_G_DAY = cal.getTimeInMillis();

        cal.clear();
        cal.set(Calendar.MONTH, 1);
        this.CONTROL_G_MONTH = cal.getTimeInMillis();

        cal.clear();
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 21);
        this.CONTROL_G_MONTH_DAY = cal.getTimeInMillis();

        cal.clear();
        cal.set(Calendar.YEAR, 1975);
        this.CONTROL_G_YEAR = cal.getTimeInMillis();

        cal.clear();
        cal.set(Calendar.YEAR, 1975);
        cal.set(Calendar.MONTH, 1);
        this.CONTROL_G_YEAR_MONTH = cal.getTimeInMillis();
    }

    public void setUp() {
        xcm = XMLConversionManager.getDefaultXMLManager();
    }

    public void testUtilDateToUtilDate() {
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_1MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(control, java.util.Date.class);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_default_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_default_0ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21T07:47:15.0";
        String test = (String)xcm.convertObject(utilDate, String.class);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_default_1ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_1MS);
        String control = "1975-02-21T07:47:15.001";
        String test = (String)xcm.convertObject(utilDate, String.class);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_default_10ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_10MS);
        String control = "1975-02-21T07:47:15.01";
        String test = (String)xcm.convertObject(utilDate, String.class);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_default_100ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_100MS);
        String control = "1975-02-21T07:47:15.1";
        String test = (String)xcm.convertObject(utilDate, String.class);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_date_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_dateTime_before_epoch() {
        // the default timezone will be applied such that the returned datetime
        // should be 5 hours earlier
        String control = "1965-01-01T00:00:00.001";

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1965);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MILLISECOND, 1);
        java.util.Date utilDate = cal.getTime();

        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_dateTime_negative_year() throws Exception{
        // the default timezone will be applied such that the returned datetime
        // should be 5 hours earlier
        String control = "-2006-01-01T00:00:00.001";

        GregorianCalendar cal = new GregorianCalendar();
        cal.clear();
        cal.set(Calendar.ERA, java.util.GregorianCalendar.BC);

        cal.set(Calendar.YEAR, 2006);

        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MILLISECOND, 1);

        java.util.Date utilDate = cal.getTime();

        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_date() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_dateTime_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_dateTime_0ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21T07:47:15.0";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_dateTime_1ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_1MS);
        String control = "1975-02-21T07:47:15.001";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_dateTime_10ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_10MS);
        String control = "1975-02-21T07:47:15.01";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_dateTime_100ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_100MS);
        String control = "1975-02-21T07:47:15.1";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_time_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_time_0ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "07:47:15.0";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_time_1ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_1MS);
        String control = "07:47:15.001";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_time_10ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_10MS);
        String control = "07:47:15.01";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_time_100ms() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_100MS);
        String control = "07:47:15.1";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gDay_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gDay() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "---21";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gMonth_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gMonth() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "--02";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gMonthDay_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gMonthDay() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "--02-21";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gYear_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gYear() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gYearMonth_null() {
        java.util.Date utilDate = null;
        String control = null;
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_gYearMonth() {
        java.util.Date utilDate = new java.util.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975-02";
        String test = (String)xcm.convertObject(utilDate, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testUtilDateToString_default_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.util.Date utilDate = (java.util.Date) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.util.Date.class);
        String testString = (String) xmlConversionManager.convertObject(utilDate, String.class);
        this.assertEquals("2003-08-29T03:00:00.0-04:00", testString);
    }

    public void testUtilDateToString_dateTime_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.util.Date utilDate = (java.util.Date) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.util.Date.class);
        String testString = (String) xmlConversionManager.convertObject(utilDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals("2003-08-29T03:00:00.0-04:00", testString);
    }

    public void testUtilDateToString_time_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.util.Date utilDate = (java.util.Date) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.util.Date.class);
        String testString = (String) xmlConversionManager.convertObject(utilDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals("03:00:00.0-04:00", testString);
    }

    public void testStringToUtilDate_default_null() {
        String string = null;
        java.util.Date control = null;
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_default_0ms() {
        String string = "1975-02-21T07:47:15.0";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_0MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_default_1ms() {
        String string = "1975-02-21T07:47:15.001";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_1MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_default_10ms() {
        String string = "1975-02-21T07:47:15.01";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_10MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_default_100ms() {
        String string = "1975-02-21T07:47:15.1";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_100MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_default_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.util.Date.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_default_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.util.Date.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_default_negative3() {
        try {
            String string = "1975$02$21T07$47$15";
            xcm.convertObject(string, java.util.Date.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_default_negative4() {
        try {
            String string = "1975-02-21$07:47:15";
            xcm.convertObject(string, java.util.Date.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_default_negative5() {
        try {
            String string = "10";
            xcm.convertObject(string, java.util.Date.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_date_null() {
        String string = null;
        java.util.Date control = null;
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_date() {
        String string = "1975-02-21";
        java.util.Date control = new java.util.Date(CONTROL_DATE);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_date_negative1() {
        String string = "1975-02-21T07:47:15";
        java.util.Date control = new java.util.Date(CONTROL_DATE);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_date_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_date_negative3() {
        try {
            String string = "1975$02$21";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_date_negative4() {
        try {
            String string = "10";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_dateTime_null() {
        String string = null;
        java.util.Date control = null;
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_dateTime() {
        String string = "1975-02-21T07:47:15";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_0MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_dateTime_0ms() {
        String string = "1975-02-21T07:47:15.0";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_0MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_dateTime_1ms() {
        String string = "1975-02-21T07:47:15.001";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_1MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_dateTime_10ms() {
        String string = "1975-02-21T07:47:15.01";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_10MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_dateTime_100ms() {
        String string = "1975-02-21T07:47:15.1";
        java.util.Date control = new java.util.Date(CONTROL_DATE_TIME_100MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_dateTime_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_dateTime_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_dateTime_negative3() {
        try {
            String string = "1975$02$21T07$47$15";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_dateTime_negative4() {
        try {
            String string = "1975-02-21$07:47:15";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_time_null() {
        String string = null;
        java.util.Date control = null;
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_time() {
        String string = "07:47:15";
        java.util.Date control = new java.util.Date(CONTROL_TIME_0MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_time_0ms() {
        String string = "07:47:15.0";
        java.util.Date control = new java.util.Date(CONTROL_TIME_0MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_time_1ms() {
        String string = "07:47:15.001";
        java.util.Date control = new java.util.Date(CONTROL_TIME_1MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_time_10ms() {
        String string = "07:47:15.01";
        java.util.Date control = new java.util.Date(CONTROL_TIME_10MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_time_100ms() {
        String string = "07:47:15.1";
        java.util.Date control = new java.util.Date(CONTROL_TIME_100MS);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_time_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
        }
    }

    public void testStringToUtilDate_time_negative2() {
        try {
            String string = "1975-02-21T07:47:15";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_time_negative3() {
        try {
            String string = "07-47$15";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_time_negative4() {
        try {
            String string = "10";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_gDay_null() {
        String string = null;
        java.util.Date control = null;
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gDay() {
        String string = "---21";
        java.util.Date control = new java.util.Date(CONTROL_G_DAY);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.G_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_gMonth_null() {
        String string = null;
        java.util.Date control = null;
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gMonth() {
        String string = "--02";
        java.util.Date control = new java.util.Date(CONTROL_G_MONTH);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.G_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_gMonthDay_null() {
        String string = null;
        java.util.Date control = null;
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gMonthDay() {
        String string = "--02-21";
        java.util.Date control = new java.util.Date(CONTROL_G_MONTH_DAY);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gMonthDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.G_MONTH_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_gYear_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gYear() {
        String string = "1975";
        java.util.Date control = new java.util.Date(CONTROL_G_YEAR);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gYear_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.G_YEAR_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToUtilDate_gYearMonth_null() {
        String string = null;
        java.util.Date control = null;
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gYearMonth() {
        String string = "1975-02";
        java.util.Date control = new java.sql.Date(CONTROL_G_YEAR_MONTH);
        java.util.Date test = (java.util.Date)xcm.convertObject(string, java.util.Date.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToUtilDate_gYearMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Date.class, XMLConstants.G_YEAR_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    // -------------------------------------------------------------------------
    public void testSqlDateToSqlDate() {
        java.sql.Date control = new java.sql.Date(CONTROL_DATE);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(control, java.sql.Date.class);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_default_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_default() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21";
        String test = (String)xcm.convertObject(sqlDate, String.class);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_date_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_date() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_dateTime_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_dateTime_0ms() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_dateTime_1ms() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_1MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_dateTime_10ms() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_10MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_dateTime_100ms() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_100MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_time_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_time_0ms() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_time_1ms() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_1MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_time_10ms() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_10MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_time_100ms() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_100MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gDay_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gDay() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "---21";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gMonth_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gMonth() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "--02";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gMonthDay_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gMonthDay() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "--02-21";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gYear_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gYear() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gYearMonth_null() {
        java.sql.Date sqlDate = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_gYearMonth() {
        java.sql.Date sqlDate = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        String control = "1975-02";
        String test = (String)xcm.convertObject(sqlDate, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlDateToString_dateTime_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.sql.Date sqlDate = (java.sql.Date) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        String testString = (String) xmlConversionManager.convertObject(sqlDate, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals("2003-08-29T03:00:00-04:00", testString);
    }

    public void testSqlDateToString_time_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.sql.Date sqlDate = (java.sql.Date) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        String testString = (String) xmlConversionManager.convertObject(sqlDate, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals("03:00:00-04:00", testString);
    }

    public void testStringToSqlDate_default_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_default() {
        String string = "1975-02-21";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_default_negative1() {
        String string = "1975-02-21T07:47:15";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_default_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.sql.Date.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_default_negative3() {
        try {
            String string = "1975$02$21";
            xcm.convertObject(string, java.sql.Date.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_date_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_date() {
        String string = "1975-02-21";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_date_negative1() {
        String string = "1975-02-21T07:47:15";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_date_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_date_negative3() {
        try {
            String string = "1975$02$21";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_dateTime_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_dateTime() {
        String string = "1975-02-21T07:47:15";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_dateTime_0ms() {
        String string = "1975-02-21T07:47:15.0";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_dateTime_1ms() {
        String string = "1975-02-21T07:47:15.001";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_dateTime_10ms() {
        String string = "1975-02-21T07:47:15.01";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_dateTime_100ms() {
        String string = "1975-02-21T07:47:15.1";
        java.sql.Date control = new java.sql.Date(CONTROL_DATE_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_dateTime_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_dateTime_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_dateTime_negative3() {
        try {
            String string = "1975$02$21T07$47$15";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_dateTime_negative4() {
        try {
            String string = "1975-02-21$07:47:15";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_time_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_time() {
        String string = "07:47:15";
        java.sql.Date control = new java.sql.Date(CONTROL_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_time_0ms() {
        String string = "07:47:15.0";
        java.sql.Date control = new java.sql.Date(CONTROL_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_time_1ms() {
        String string = "07:47:15.001";
        java.sql.Date control = new java.sql.Date(CONTROL_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_time_10ms() {
        String string = "07:47:15.01";
        java.sql.Date control = new java.sql.Date(CONTROL_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_time_100ms() {
        String string = "07:47:15.1";
        java.sql.Date control = new java.sql.Date(CONTROL_TIME_0MS);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_time_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_time_negative2() {
        try {
            String string = "1975-02-21T07:47:15";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_time_negative3() {
        try {
            String string = "07$47$15";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_gDay_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gDay() {
        String string = "---21";
        java.sql.Date control = new java.sql.Date(CONTROL_G_DAY);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_gMonth_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gMonth() {
        String string = "--02";
        java.sql.Date control = new java.sql.Date(CONTROL_G_MONTH);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_gMonthDay_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gMonthDay() {
        String string = "--02-21";
        java.sql.Date control = new java.sql.Date(CONTROL_G_MONTH_DAY);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gMonthDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_MONTH_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_gYear_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gYear() {
        String string = "1975";
        java.sql.Date control = new java.sql.Date(CONTROL_G_YEAR);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gYear_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_YEAR_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlDate_gYearMonth_null() {
        String string = null;
        java.sql.Date control = null;
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gYearMonth() {
        String string = "1975-02";
        java.sql.Date control = new java.sql.Date(CONTROL_G_YEAR_MONTH);
        java.sql.Date test = (java.sql.Date)xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlDate_gYearMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Date.class, XMLConstants.G_YEAR_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    // -------------------------------------------------------------------------
    public void testSqlTimeToSqlTime() {
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(control, java.sql.Time.class);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_default_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_default() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_TIME_0MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_default_0ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_TIME_0MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_default_1ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_TIME_1MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_default_10ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_TIME_10MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_default_100ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_TIME_100MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_date_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_date() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_dateTime_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_dateTime() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_dateTime_0ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_dateTime_1ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_1MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_dateTime_10ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_10MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_dateTime_100ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_100MS);
        String control = "1975-02-21T07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_time_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_time() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_time_0ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_time_1ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_1MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_time_10ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_10MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_time_100ms() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_100MS);
        String control = "07:47:15";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gDay_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gDay() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "---21";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gMonth_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gMonth() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "--02";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gMonthDay_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gMonthDay() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "--02-21";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gYear_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gYear() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "1975";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gYearMonth_null() {
        java.sql.Time sqlTime = null;
        String control = null;
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_gYearMonth() {
        java.sql.Time sqlTime = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        String control = "1975-02";
        String test = (String)xcm.convertObject(sqlTime, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimeToString_default_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.sql.Time sqlTime = (java.sql.Time) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        String testString = (String) xmlConversionManager.convertObject(sqlTime, String.class);
        this.assertEquals("03:00:00-04:00", testString);
    }

    public void testSqlTimeToString_dateTime_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.sql.Time sqlTime = (java.sql.Time) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        String testString = (String) xmlConversionManager.convertObject(sqlTime, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals("2003-08-29T03:00:00-04:00", testString);
    }

    public void testSqlTimeToString_time_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.sql.Time sqlTime = (java.sql.Time) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        String testString = (String) xmlConversionManager.convertObject(sqlTime, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals("03:00:00-04:00", testString);
    }

    public void testStringToSqlTime_default_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_default() {
        String string = "07:47:15";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_default_0ms() {
        String string = "07:47:15.0";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_default_1ms() {
        String string = "07:47:15.001";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_default_10ms() {
        String string = "07:47:15.01";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_default_100ms() {
        String string = "07:47:15.1";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_default_negative1() {
        try {
            String string = "1975-02-21T07:47:15";
            xcm.convertObject(string, Time.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_default_negative2() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, Time.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_default_negative3() {
        try {
            String string = "07$47$15";
            xcm.convertObject(string, Time.class);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_date_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_date() {
        String string = "1975-02-21";
        java.sql.Time control = new java.sql.Time(CONTROL_DATE);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_date_negative1() {
        String string = "1975-02-21T07:47:15";
        java.sql.Time control = new java.sql.Time(CONTROL_DATE);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_date_negative2() {
        try {
            String string = "07:47:15";
            Time test = (Time)xcm.convertObject(string, Time.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_date_negative3() {
        try {
            String string = "1975$02$21";
            Time test = (Time)xcm.convertObject(string, Time.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_dateTime_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_dateTime() {
        String string = "1975-02-21T07:47:15";
        java.sql.Time control = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_dateTime_0ms() {
        String string = "1975-02-21T07:47:15.0";
        java.sql.Time control = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_dateTime_1ms() {
        String string = "1975-02-21T07:47:15.001";
        java.sql.Time control = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_dateTime_10ms() {
        String string = "1975-02-21T07:47:15.01";
        java.sql.Time control = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_dateTime_100ms() {
        String string = "1975-02-21T07:47:15.1";
        java.sql.Time control = new java.sql.Time(CONTROL_DATE_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_dateTime_negative1() {
        try {
            String string = "1975-02-21";
            Time test = (Time)xcm.convertObject(string, Time.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_dateTime_negative2() {
        try {
            String string = "07:47:15";
            Time test = (Time)xcm.convertObject(string, Time.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
        }
    }

    public void testStringToSqlTime_dateTime_negative3() {
        try {
            String string = "1975$02$21T07$47$15";
            Time test = (Time)xcm.convertObject(string, Time.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_dateTime_negative4() {
        try {
            String string = "1975-02-21$07:47:15";
            Time test = (Time)xcm.convertObject(string, Time.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_time_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_time() {
        String string = "07:47:15";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_time_0ms() {
        String string = "07:47:15.0";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_time_1ms() {
        String string = "07:47:15.001";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_time_10ms() {
        String string = "07:47:15.01";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_time_100ms() {
        String string = "07:47:15.1";
        java.sql.Time control = new java.sql.Time(CONTROL_TIME_0MS);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_time_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, Time.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
        }
    }

    public void testStringToSqlTime_time_negative2() {
        try {
            String string = "1975-02-21T07:47:15";
            xcm.convertObject(string, Time.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_time_negative3() {
        try {
            String string = "07$47$15";
            xcm.convertObject(string, Time.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_gDay_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gDay() {
        String string = "---21";
        java.sql.Time control = new java.sql.Time(CONTROL_G_DAY);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_gMonth_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gMonth() {
        String string = "--02";
        java.sql.Time control = new java.sql.Time(CONTROL_G_MONTH);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_gMonthDay_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gMonthDay() {
        String string = "--02-21";
        java.sql.Time control = new java.sql.Time(CONTROL_G_MONTH_DAY);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gMonthDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_MONTH_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_gYear_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gYear() {
        String string = "1975";
        java.sql.Time control = new java.sql.Time(CONTROL_G_YEAR);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gYear_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_YEAR_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToSqlTime_gYearMonth_null() {
        String string = null;
        java.sql.Time control = null;
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gYearMonth() {
        String string = "1975-02";
        java.sql.Time control = new java.sql.Time(CONTROL_G_YEAR_MONTH);
        java.sql.Time test = (java.sql.Time)xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToSqlTime_gYearMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Time.class, XMLConstants.G_YEAR_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    // -------------------------------------------------------------------------
    public void testTimestampToTimestamp() {
        Timestamp control = new Timestamp(CONTROL_DATE_TIME_1MS);
        Timestamp test = (Timestamp)xcm.convertObject(control, Timestamp.class);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_default_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_default_0ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21T07:47:15.0";
        String test = (String)xcm.convertObject(timestamp, String.class);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_default_1ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_1MS);
        String control = "1975-02-21T07:47:15.001";
        String test = (String)xcm.convertObject(timestamp, String.class);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_default_10ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_10MS);
        String control = "1975-02-21T07:47:15.01";
        String test = (String)xcm.convertObject(timestamp, String.class);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_default_100ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_100MS);
        String control = "1975-02-21T07:47:15.1";
        String test = (String)xcm.convertObject(timestamp, String.class);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_default_123456789ns() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        timestamp.setNanos(123456789);
        String control = "1975-02-21T07:47:15.123456789";
        String test = (String)xcm.convertObject(timestamp, String.class);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_date_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_date() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_dateTime_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_dateTime_0ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "1975-02-21T07:47:15.0";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_dateTime_1ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_1MS);
        String control = "1975-02-21T07:47:15.001";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_dateTime_10ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_10MS);
        String control = "1975-02-21T07:47:15.01";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_dateTime_100ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_100MS);
        String control = "1975-02-21T07:47:15.1";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_dateTime_123456789ns() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        timestamp.setNanos(123456789);
        String control = "1975-02-21T07:47:15.123456789";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_dateTime_negative_year_123456789ns() {
        // the default timezone will be applied such that the returned timestamp
        // should be 5 hours earlier
        String control = "-2006-01-01T00:00:00.123456789";

        GregorianCalendar cal = new GregorianCalendar();
        cal.setGregorianChange(new java.util.Date(Long.MIN_VALUE));
        cal.clear();
        cal.set(Calendar.ERA, java.util.GregorianCalendar.BC);

        cal.set(Calendar.YEAR, 2006);

        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MILLISECOND, 1);

        java.util.Date utilDate = cal.getTime();

        java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());
        timestamp.setNanos(123456789);

        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_dateTime_before_epoch_123456789ns() {
        // the default timezone will be applied such that the returned timestamp
        // should be 5 hours earlier
        String control = "1965-01-01T00:00:00.123456789";

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1965);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        java.util.Date utilDate = cal.getTime();

        java.sql.Timestamp timestamp = new java.sql.Timestamp(utilDate.getTime());
        timestamp.setNanos(123456789);

        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_time_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_time_0ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "07:47:15.0";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_time_1ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_1MS);
        String control = "07:47:15.001";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_time_10ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_10MS);
        String control = "07:47:15.01";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_time_100ms() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_100MS);
        String control = "07:47:15.1";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_time_123456789ns() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        timestamp.setNanos(123456789);
        String control = "07:47:15.123456789";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gDay_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gDay() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "---21";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gMonth_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gMonth() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "--02";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gMonthDay_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gMonthDay() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "--02-21";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gYear_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gYear() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "1975";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gYearMonth_null() {
        java.sql.Timestamp timestamp = null;
        String control = null;
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testTimestampToString_gYearMonth() {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        String control = "1975-02";
        String test = (String)xcm.convertObject(timestamp, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testSqlTimestampToString_default_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        String testString = (String) xmlConversionManager.convertObject(sqlTimestamp, String.class);
        this.assertEquals("2003-08-29T03:00:00.0-04:00", testString);
    }

    public void testSqlTimestampToString_dateTime_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        String testString = (String) xmlConversionManager.convertObject(sqlTimestamp, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals("2003-08-29T03:00:00.0-04:00", testString);
    }

    public void testSqlTimestampToString_time_dstTimeZone() {
        XMLConversionManager xmlConversionManager = (XMLConversionManager) XMLConversionManager.getDefaultXMLManager().clone();
        xmlConversionManager.setTimeZone(TimeZone.getTimeZone(CONTROL_DST_TIME_ZONE));
        xmlConversionManager.setTimeZoneQualified(true);
        java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp) xmlConversionManager.convertObject(CONTROL_DST_INPUT_DATE_TIME, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        String testString = (String) xmlConversionManager.convertObject(sqlTimestamp, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals("03:00:00.0-04:00", testString);
    }

    public void testStringToTimestamp_default_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_default_0ms() {
        String string = "1975-02-21T07:47:15.0";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_default_1ms() {
        String string = "1975-02-21T07:47:15.001";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_1MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_default_10ms() {
        String string = "1975-02-21T07:47:15.01";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_10MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_default_100ms() {
        String string = "1975-02-21T07:47:15.1";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_100MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_default_123456789ns() {
        String string = "1975-02-21T07:47:15.123456789";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        control.setNanos(123456789);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_default_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.sql.Timestamp.class);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_default_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.sql.Timestamp.class);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_default_negative3() {
        try {
            String string = "1975$02$21T07$47$15";
            xcm.convertObject(string, java.sql.Timestamp.class);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_default_negative4() {
        try {
            String string = "1975-02-21$07:47:15";
            xcm.convertObject(string, java.sql.Timestamp.class);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_default_negative5() {
        try {
            String string = "1975-02-21T07:47:15.12345678$";
            xcm.convertObject(string, java.sql.Timestamp.class);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_date_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_date() {
        String string = "1975-02-21";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_date_negative1() {
        String string = "1975-02-21T07:47:15";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_date_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_date_negative3() {
        try {
            String string = "1975$02$21";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_dateTime_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_dateTime() {
        String string = "1975-02-21T07:47:15";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_dateTime_0ms() {
        String string = "1975-02-21T07:47:15.0";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_dateTime_1ms() {
        String string = "1975-02-21T07:47:15.001";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_1MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_dateTime_10ms() {
        String string = "1975-02-21T07:47:15.01";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_10MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_dateTime_100ms() {
        String string = "1975-02-21T07:47:15.1";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_100MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_dateTime_123456789ns() {
        String string = "1975-02-21T07:47:15.123456789";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_DATE_TIME_0MS);
        control.setNanos(123456789);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_dateTime_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_dateTime_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_dateTime_negative3() {
        try {
            String string = "1975$02$21T07$47$15";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
        }
    }

    public void testStringToTimestamp_dateTime_negative4() {
        try {
            String string = "1975-02-21$07:47:15";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.DATE_TIME_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_time_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_time() {
        String string = "07:47:15";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_TIME_0MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_time_0ms() {
        String string = "07:47:15.0";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_TIME_0MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_time_1ms() {
        String string = "07:47:15.001";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_TIME_1MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_time_10ms() {
        String string = "07:47:15.01";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_TIME_10MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_time_100ms() {
        String string = "07:47:15.1";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_TIME_100MS);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_time_123456789ns() {
        String string = "07:47:15.123456789";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_TIME_0MS);
        control.setNanos(123456789);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_time_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_TIME_FORMAT);
        }
    }

    public void testStringToTimestamp_time_negative2() {
        try {
            String string = "1975-02-21T07:47:15";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_time_negative3() {
        try {
            String string = "07$47$15";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.TIME_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_TIMESTAMP_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_gDay_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gDay() {
        String string = "---21";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_G_DAY);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_gMonth_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gMonth() {
        String string = "--02";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_G_MONTH);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_gMonthDay_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gMonthDay() {
        String string = "--02-21";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_G_MONTH_DAY);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gMonthDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_MONTH_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_gYear_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gYear() {
        String string = "1975";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_G_YEAR);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gYear_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_YEAR_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToTimestamp_gYearMonth_null() {
        String string = null;
        java.sql.Timestamp control = null;
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gYearMonth() {
        String string = "1975-02";
        java.sql.Timestamp control = new java.sql.Timestamp(CONTROL_G_YEAR_MONTH);
        java.sql.Timestamp test = (java.sql.Timestamp)xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToTimestamp_gYearMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.sql.Timestamp.class, XMLConstants.G_YEAR_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    // -------------------------------------------------------------------------
    public void testCalendarToCalendar() {
        Calendar control = Calendar.getInstance();
        Calendar test = (Calendar)xcm.convertObject(control, Calendar.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_date_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_date() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 1975);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DATE, 21);
        String control = "1975-02-21";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_dateTime_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_dateTime_0ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21T07:47:15.0";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_dateTime_1ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_1MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21T07:47:15.001";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_dateTime_10ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_10MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21T07:47:15.01";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_dateTime_100ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_100MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21T07:47:15.1";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_time_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_time_0ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, 7);
        calendar.set(Calendar.MINUTE, 47);
        calendar.set(Calendar.SECOND, 15);
        calendar.set(Calendar.MILLISECOND, 0);
        String control = "07:47:15.0";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_time_1ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, 7);
        calendar.set(Calendar.MINUTE, 47);
        calendar.set(Calendar.SECOND, 15);
        calendar.set(Calendar.MILLISECOND, 1);
        String control = "07:47:15.001";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_time_10ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, 7);
        calendar.set(Calendar.MINUTE, 47);
        calendar.set(Calendar.SECOND, 15);
        calendar.set(Calendar.MILLISECOND, 10);
        String control = "07:47:15.01";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_default_time_100ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, 7);
        calendar.set(Calendar.MINUTE, 47);
        calendar.set(Calendar.SECOND, 15);
        calendar.set(Calendar.MILLISECOND, 100);
        String control = "07:47:15.1";
        String test = (String)xcm.convertObject(calendar, String.class);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_date_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_date() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_dateTime_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_dateTime_0ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21T07:47:15.0";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_dateTime_1ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_1MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21T07:47:15.001";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_dateTime_10ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_10MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21T07:47:15.01";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_dateTime_100ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_100MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02-21T07:47:15.1";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_time_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_time_0ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "07:47:15.0";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_time_1ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_1MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "07:47:15.001";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_time_10ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_10MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "07:47:15.01";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_time_100ms() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_100MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "07:47:15.1";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gDay_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "---21";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gMonth_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "--02--";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gMonthDay_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gMonthDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "--02-21";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gYear_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gYearMonth_null() {
        Calendar calendar = null;
        String control = null;
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testCalendarToString_gYearMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        calendar.clear(Calendar.ZONE_OFFSET);
        String control = "1975-02";
        String test = (String)xcm.convertObject(calendar, String.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_dateTime_null() {
        String string = null;
        Calendar control = null;
        Calendar test = (Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_dateTime() {
        String string = "1975-02-21T07:47:15";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_dateTime_0ms() {
        String string = "1975-02-21T07:47:15.0";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_dateTime_1ms() {
        String string = "1975-02-21T07:47:15.001";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_1MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_dateTime_10ms() {
        String string = "1975-02-21T07:47:15.01";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_10MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_dateTime_100ms() {
        String string = "1975-02-21T07:47:15.1";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_100MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_date_null() {
        String string = null;
        Calendar control = null;
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_date() {
        String string = "1975-02-21";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_time_null() {
        String string = null;
        Calendar control = null;
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_time() {
        String string = "07:47:15";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_0MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_time_0ms() {
        String string = "07:47:15.0";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_0MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_time_1ms() {
        String string = "07:47:15.001";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_1MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_time_10ms() {
        String string = "07:47:15.01";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_10MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_default_time_100ms() {
        String string = "07:47:15.1";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_100MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_date_null() {
        String string = null;
        Calendar control = null;
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_date() {
        String string = "1975-02-21";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_date_negative1() {
        try {
            String string = "1975-02-21T07:47:15";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
        }
    }

    public void testStringToCalendar_date_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_date_negative3() {
        try {
            String string = "1975$02$21";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_dateTime_null() {
        String string = null;
        Calendar control = null;
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_dateTime() {
        String string = "1975-02-21T07:47:15";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_dateTime_0ms() {
        String string = "1975-02-21T07:47:15.0";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_0MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_dateTime_1ms() {
        String string = "1975-02-21T07:47:15.001";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_1MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_dateTime_10ms() {
        String string = "1975-02-21T07:47:15.01";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_10MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_dateTime_100ms() {
        String string = "1975-02-21T07:47:15.1";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_DATE_TIME_100MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_dateTime_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
        }
    }

    public void testStringToCalendar_dateTime_negative2() {
        try {
            String string = "07:47:15";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_dateTime_negative3() {
        try {
            String string = "1975$02$21T07$47$15";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_dateTime_negative4() {
        try {
            String string = "1975-02-21$07:47:15";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.DATE_TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_DATE_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_time_null() {
        String string = null;
        Calendar control = null;
        Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_time() {
        String string = "07:47:15";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_0MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_time_0ms() {
        String string = "07:47:15.0";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_0MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_time_1ms() {
        String string = "07:47:15.001";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_1MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_time_10ms() {
        String string = "07:47:15.01";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_10MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_time_100ms() {
        String string = "07:47:15.1";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_TIME_100MS);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_time_negative1() {
        try {
            String string = "1975-02-21";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_time_negative2() {
        try {
            String string = "1975-02-21T07:47:15";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
        }
    }

    public void testStringToCalendar_time_negative3() {
        try {
            String string = "07$47$15";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.TIME_QNAME);
        } catch (ConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == ConversionException.INCORRECT_TIME_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_gDay_null() {
        String string = null;
        Calendar control = null;
        Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gDay() {
        String string = "---21";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_G_DAY);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_gMonth_null() {
        String string = null;
        Calendar control = null;
        Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gMonth() {
        String string = "--02";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_G_MONTH);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_gMonthDay_null() {
        String string = null;
        Calendar control = null;
        Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gMonthDay() {
        String string = "--02-21";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_G_MONTH_DAY);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_MONTH_DAY_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gMonthDay_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_MONTH_DAY_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_MONTH_DAY_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_gYear_null() {
        String string = null;
        Calendar control = null;
        Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gYear() {
        String string = "1975";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_G_YEAR);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_YEAR_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gYear_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_YEAR_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testStringToCalendar_gYearMonth_null() {
        String string = null;
        Calendar control = null;
        Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gYearMonth() {
        String string = "1975-02";
        Calendar control = Calendar.getInstance();
        control.clear();
        control.setTimeInMillis(CONTROL_G_YEAR_MONTH);
        java.util.Calendar test = (java.util.Calendar)xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_YEAR_MONTH_QNAME);
        this.assertEquals(control, test);
    }

    public void testStringToCalendar_gYearMonth_negative1() {
        try {
            String string = "INVALID";
            xcm.convertObject(string, java.util.Calendar.class, XMLConstants.G_YEAR_MONTH_QNAME);
        } catch (XMLConversionException e) {
            assertTrue(WRONG_EXCEPTION_THROWN, e.getErrorCode() == XMLConversionException.INCORRECT_G_YEAR_MONTH_FORMAT);
            return;
        }
        fail(EXCEPTION_NOT_THROWN);
    }

    public void testXMLGregorianCalendarToStringWithDateSchemaType() throws Exception {
        DatatypeFactory factory = DatatypeFactory.newInstance();

        XMLGregorianCalendar xgc = factory.newXMLGregorianCalendar("2005-11-05");
        String result = (String)xcm.convertObject(xgc, String.class, XMLConstants.DATE_QNAME);
        assertEquals("2005-11-05", result);
    }

    public void testStringToCalendarToStringDateOnly() throws Exception {
        String controlString = "2010-01-01";
        Calendar cal = xcm.convertStringToCalendar(controlString, XMLConstants.DATE_QNAME);
        String s = xcm.convertObject(cal, String.class).toString();
        assertEquals(controlString, s);
    }

    public void testMultiConversion() throws Exception {
        // Test XMLGregorianCalendar -> xml -> Calendar -> xml -> XMLGregorianCalendar
        DatatypeFactory factory = DatatypeFactory.newInstance();

        XMLGregorianCalendar xgc1 = factory.newXMLGregorianCalendar(6, 5, 1, 10, 0, 0, 0, 0);

        String xml1 = xcm.convertObject(xgc1, String.class).toString();

        Calendar c = (Calendar) xcm.convertObject(xml1, Calendar.class);

        String xml2 = xcm.convertObject(c, String.class).toString();

        XMLGregorianCalendar xgc2 = (XMLGregorianCalendar) xcm.convertObject(xml2, XMLGregorianCalendar.class);

        assertTrue(xgc1.compare(xgc2) == DatatypeConstants.EQUAL);
    }

}