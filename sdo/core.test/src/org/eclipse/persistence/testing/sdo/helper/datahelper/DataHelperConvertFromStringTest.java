/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import commonj.sdo.Type;
import java.util.Calendar;
import java.util.Date;
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
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate(b);

        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, Date.class, null));
    }

    public void testConverFromString_Calendar() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);

        this.assertEquals(controlCalendar, (Calendar)dataHelper.convertFromStringValue(b, Calendar.class, null));
    }

    public void testConverFromString_NoQname() {
        String b = "10";
        Integer B = new Integer(b);
        this.assertEquals(B, (Integer)dataHelper.convertFromStringValue(b, Integer.class));
    }

    public void testConverFromString_Date_NoQName() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate(b);

        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, Date.class));
    }

    public void testConverFromString_Calendar_NoQname() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);

        this.assertEquals(controlCalendar, (Calendar)dataHelper.convertFromStringValue(b, Calendar.class));
    }

    public void testConverFromString_Type() {
        String b = "10";
        Integer B = new Integer(b);
        this.assertEquals(B, (Integer)dataHelper.convertFromStringValue(b, Integer.class));
    }

    public void testConverFromString_DateWithType() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate(b);

        SDOType d = SDOConstants.SDO_DATE;

        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, d));
    }

    public void testConverFromString_DateWithTypeNullQName() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate(b);

        SDOType d = SDOConstants.SDO_DATE;

        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, d, null));
    }

    public void testConverFromString_DateWithTypeQName() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate(b);

        SDOType d = SDOConstants.SDO_DATE;

        this.assertEquals(aDate, (Date)dataHelper.convertFromStringValue(b, d, SDOConstants.GYEARMONTH_QNAME));
    }

    public void testConverFromString_DateWithNullTypeNullQName() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate(b);

        SDOType d = new SDOType(aHelperContext);

        this.assertEquals(b, (String)dataHelper.convertFromStringValue(b, d, null));
    }

    public void testConverFromObject_DateWithNullTypeNullQName() {
        String b = "2000";
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate(b);

        SDOType d = new SDOType(aHelperContext);

        this.assertEquals("2000-01-01T00:00:00.0", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_DATETIME));
        this.assertEquals("2000", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_YEAR));
        this.assertEquals("2000-01", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_YEARMONTH));
        this.assertEquals("2000-01-01T00:00:00.0", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_DATE));
        this.assertEquals("00:00:00.0", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_TIME));
        this.assertEquals("----01", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_DAY));
        this.assertEquals("P2000Y1M1DT0H0M0.0S", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_DURATION));
        this.assertEquals("--01--", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_MONTH));
        this.assertEquals("--01-01", (String)dataHelper.convertToStringValue((Object)aDate, (Type)SDOConstants.SDO_MONTHDAY));
    }
}