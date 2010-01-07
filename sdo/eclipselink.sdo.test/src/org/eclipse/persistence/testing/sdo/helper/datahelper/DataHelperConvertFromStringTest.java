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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import commonj.sdo.Type;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class DataHelperConvertFromStringTest extends DataHelperTestCases {
    public DataHelperConvertFromStringTest(String name) {
        super(name);
    }

    public void testConverFromString() {
        String b = "10";
        Integer B = new Integer(b);
        this.assertEquals(B, (Integer)dataHelper.convertFromStringValue(b, Integer.class, null));
    }

    public void testConverFromString_Date() {
        String b = "2000";
        Date aDate = dataHelper.toDate(b);

        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, Date.class, null));
    }

    public void testConverFromString_Calendar() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = (Calendar)dataHelper.convertFromStringValue(b, Calendar.class, null);
        assertTrue("Expected YEAR: " + controlCalendar.get(Calendar.YEAR) + ", but was: " + aCalendar.get(Calendar.YEAR), controlCalendar.get(Calendar.YEAR) == aCalendar.get(Calendar.YEAR));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testConverFromString_NoQname() {
        String b = "10";
        Integer B = new Integer(b);
        this.assertEquals(B, (Integer)dataHelper.convertFromStringValue(b, Integer.class));
    }

    public void testConverFromString_Date_NoQName() {
        String b = "2000";
        Date aDate = dataHelper.toDate(b);
        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, Date.class));
    }

    public void testConverFromString_Calendar_NoQname() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = (Calendar)dataHelper.convertFromStringValue(b, Calendar.class, null);
        assertTrue("Expected YEAR: " + controlCalendar.get(Calendar.YEAR) + ", but was: " + aCalendar.get(Calendar.YEAR), controlCalendar.get(Calendar.YEAR) == aCalendar.get(Calendar.YEAR));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testConverFromString_Type() {
        String b = "10";
        Integer B = new Integer(b);
        this.assertEquals(B, (Integer)dataHelper.convertFromStringValue(b, Integer.class));
    }

    public void testConverFromString_DateWithType() {
        String b = "2000";
        Date aDate = dataHelper.toDate(b);
        SDOType d = SDOConstants.SDO_DATE;
        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, d));
    }

    public void testConverFromString_DateWithTypeNullQName() {
        String b = "2000";
        Date aDate = dataHelper.toDate(b);
        SDOType d = SDOConstants.SDO_DATE;
        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, d, null));
    }

    public void testConverFromString_DateWithTypeQName() {
        String b = "2000";
        Date aDate = dataHelper.toDate(b);
        SDOType d = SDOConstants.SDO_DATE;
        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, d, SDOConstants.GYEARMONTH_QNAME));
    }

    public void testConverFromString_DateWithNullTypeNullQName() {
        String b = "2000";
        Date aDate = dataHelper.toDate(b);
        SDOType d = new SDOType(aHelperContext);
        this.assertEquals(b, (String)dataHelper.convertFromStringValue(b, d, null));
    }

    public void testConverFromObject_DateWithNullTypeNullQName() {
        String b = "2000";
        Date aDate = dataHelper.toDate(b);
        
        this.assertEquals("2000-01-01T00:00:00.0Z", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_DATETIME));
        this.assertEquals("2000", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_YEAR));
        this.assertEquals("2000-01", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_YEARMONTH));
        this.assertEquals("2000-01-01T00:00:00.0Z", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_DATE));
        this.assertEquals("00:00:00.0Z", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_TIME));
        this.assertEquals("---01", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_DAY));
        this.assertEquals("P2000Y1M1DT0H0M0.0S", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_DURATION));
        this.assertEquals("--01", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_MONTH));
        this.assertEquals("--01-01", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_MONTHDAY));
    }
    
    public void testConverFromObject_Date_GMTDefault() {
    	// Original date string, will be interpreted as GMT by default
    	String origDateString = "1999-05-31T15:55:00.000";
    	
    	// String converted to date -- this will be converted to VM's time zone
    	Date aDate = dataHelper.toDate(origDateString);
    	
    	// Format the date back to GMT and make sure it equals the original
    	// date string
		DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSS");
		f.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateString = f.format(aDate);
		
		this.assertEquals(origDateString, dateString);
    }
}
