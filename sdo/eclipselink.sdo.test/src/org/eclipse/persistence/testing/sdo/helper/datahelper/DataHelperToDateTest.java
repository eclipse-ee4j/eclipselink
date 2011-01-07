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
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;

public class DataHelperToDateTest extends DataHelperTestCases {
    public DataHelperToDateTest(String name) {
        super(name);
    }

    public void testToDateWithGYearMonth() {
        Calendar controlCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, Calendar.MAY);
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("2001-05");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateWithGYear() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("2000");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateWithGMonthDay() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 6);
        controlCalendar.set(Calendar.DATE, 4);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("--07-04");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateWithGMonth() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 10);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("--11");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateWithGDay() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.DATE, 15);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("---15");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateWithTime() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.HOUR, 1);
        controlCalendar.set(Calendar.MINUTE, 21);
        controlCalendar.set(Calendar.SECOND, 12);
        controlCalendar.set(Calendar.MILLISECOND, 37);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("01:21:12.037");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateWithDate() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("2001-01-01");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateWithDateTime() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 9);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 0);
        controlCalendar.set(Calendar.SECOND, 1);
        controlCalendar.set(Calendar.MILLISECOND, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("2001-10-01T00:00:01.001");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateWithDuration() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 12);
        controlCalendar.set(Calendar.MONTH, 9);
        controlCalendar.set(Calendar.DATE, 2);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 40);
        controlCalendar.set(Calendar.SECOND, 27);
        controlCalendar.set(Calendar.MILLISECOND, 87);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        Date aDate = dataHelper.toDate("P12Y10M2DT0H40M27.087S");
        this.assertEquals(controlDate, aDate);
    }

    public void testToDateBeforeGregorianChange() {
        // Ensure that if we are converting to a Date that occurred before the
        // Gregorian switchover (October 15, 1582) we do not shift the date.
        Date dateObj = dataHelper.toDate("0001-01-01-05:00");

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("MM/dd/yyyy G");

        String controlString = "01/01/0001 AD";

        assertEquals(controlString, sdf.format(dateObj));
    }

    public void testToDateBeforeGregorianChangeBC() {
        // Ensure that if we are converting to a Date that occurred before the
        // Gregorian switchover (October 15, 1582) we do not shift the date.
        Date dateObj = dataHelper.toDate("-2006-03-31T03:30:45.001-05:00");

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("MM/dd/yyyy G");

        String controlString = "03/31/2006 BC";

        assertEquals(controlString, sdf.format(dateObj));
    }

    public void testToDateWithNullInput() {
        String s = null;
        Date aDate = dataHelper.toDate(s);
        assertTrue("dataHelper.toDate(null) did not return null as expected.", aDate == null);
    }

    public void testToCalendaWithInvalidInput() {
        try {
            Date aCalendar = dataHelper.toDate("--2000");
            this.fail();
        } catch (IllegalArgumentException e) {
        }
    }

}