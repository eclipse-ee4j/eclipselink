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

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DataHelperToCalendarWithLocale extends DataHelperTestCases {
    public DataHelperToCalendarWithLocale(String name) {
        super(name);
    }

    public void testToDateWithGYearMonth() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 4);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-05", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToDateWithGYear() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2000", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToDateWithGMonthDay() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 6);
        controlCalendar.set(Calendar.DATE, 4);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("--07-04", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToDateWithGMonth() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 10);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("--11--", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToDateWithGDay() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.DATE, 15);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("----15", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToDateWithTime() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.HOUR, 1);
        controlCalendar.set(Calendar.MINUTE, 21);
        controlCalendar.set(Calendar.SECOND, 12);
        controlCalendar.set(Calendar.MILLISECOND, 0);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("01:21:12", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToDateWithDate() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-01-01", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToDateWithDateTime() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 0);
        controlCalendar.set(Calendar.SECOND, 1);
        controlCalendar.set(Calendar.MILLISECOND, 0);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-01-01T00:00:01", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToDateWithDuration() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 12);
        controlCalendar.set(Calendar.MONTH, 9);
        controlCalendar.set(Calendar.DATE, 2);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 40);
        controlCalendar.set(Calendar.SECOND, 27);
        controlCalendar.set(Calendar.MILLISECOND, 870);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("P12Y10M2DT0H40M27.87S", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendarWithNullInput() {
        try {
            Locale lc = Locale.US;
            Calendar aCalendar = dataHelper.toCalendar(null, lc);
            this.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testToCalendarWithInvalidInput() {
        try {
            Locale lc = Locale.US;
            Calendar aCalendar = dataHelper.toCalendar("--2000", lc);
            this.assertNull(aCalendar);
        } catch (IllegalArgumentException e) {
        }
    }

    public void testToCalendarWithNullLocale() {
        Locale lc = null;
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-01-01", lc);
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendarWithNullString() {
        Locale lc = Locale.US;
        try {
            Calendar aCalendar = dataHelper.toCalendar(null, lc);
            this.fail();
        } catch (IllegalArgumentException e) {
        }
    }
}