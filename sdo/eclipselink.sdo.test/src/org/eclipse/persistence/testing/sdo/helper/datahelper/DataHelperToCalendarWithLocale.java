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

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DataHelperToCalendarWithLocale extends DataHelperTestCases {
    public DataHelperToCalendarWithLocale(String name) {
        super(name);
    }

    public void testToCalendarWithGYearMonth() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 4);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-05", lc);
        assertTrue("Expected YEAR: " + controlCalendar.get(Calendar.YEAR) + ", but was: " + aCalendar.get(Calendar.YEAR), controlCalendar.get(Calendar.YEAR) == aCalendar.get(Calendar.YEAR));
        assertTrue("Expected MONTH: " + controlCalendar.get(Calendar.MONTH) + ", but was: " + aCalendar.get(Calendar.MONTH), controlCalendar.get(Calendar.MONTH) == aCalendar.get(Calendar.MONTH));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithGYear() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2000", lc);
        assertTrue("Expected YEAR: " + controlCalendar.get(Calendar.YEAR) + ", but was: " + aCalendar.get(Calendar.YEAR), controlCalendar.get(Calendar.YEAR) == aCalendar.get(Calendar.YEAR));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithGMonthDay() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 6);
        controlCalendar.set(Calendar.DATE, 4);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("--07-04", lc);
        assertTrue("Expected MONTH: " + controlCalendar.get(Calendar.MONTH) + ", but was: " + aCalendar.get(Calendar.MONTH), controlCalendar.get(Calendar.MONTH) == aCalendar.get(Calendar.MONTH));
        assertTrue("Expected DATE: " + controlCalendar.get(Calendar.DATE) + ", but was: " + aCalendar.get(Calendar.DATE), controlCalendar.get(Calendar.DATE) == aCalendar.get(Calendar.DATE));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithGMonth() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 10);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("--11", lc);
        assertTrue("Expected MONTH: " + controlCalendar.get(Calendar.MONTH) + ", but was: " + aCalendar.get(Calendar.MONTH), controlCalendar.get(Calendar.MONTH) == aCalendar.get(Calendar.MONTH));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithGDay() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.DATE, 15);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("---15", lc);
        assertTrue("Expected DATE: " + controlCalendar.get(Calendar.DATE) + ", but was: " + aCalendar.get(Calendar.DATE), controlCalendar.get(Calendar.DATE) == aCalendar.get(Calendar.DATE));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithTime() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.HOUR, 1);
        controlCalendar.set(Calendar.MINUTE, 21);
        controlCalendar.set(Calendar.SECOND, 12);
        controlCalendar.set(Calendar.MILLISECOND, 0);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("01:21:12", lc);
        assertTrue("Expected HOUR: " + controlCalendar.get(Calendar.HOUR) + ", but was: " + aCalendar.get(Calendar.HOUR), controlCalendar.get(Calendar.HOUR) == aCalendar.get(Calendar.HOUR));
        assertTrue("Expected MINUTE: " + controlCalendar.get(Calendar.MINUTE) + ", but was: " + aCalendar.get(Calendar.MINUTE), controlCalendar.get(Calendar.MINUTE) == aCalendar.get(Calendar.MINUTE));
        assertTrue("Expected SECOND: " + controlCalendar.get(Calendar.SECOND) + ", but was: " + aCalendar.get(Calendar.SECOND), controlCalendar.get(Calendar.SECOND) == aCalendar.get(Calendar.SECOND));
        assertTrue("Expected MILLISECOND: " + controlCalendar.get(Calendar.MILLISECOND) + ", but was: " + aCalendar.get(Calendar.MILLISECOND), controlCalendar.get(Calendar.MILLISECOND) == aCalendar.get(Calendar.MILLISECOND));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithDate() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-01-01", lc);
        assertTrue("Expected YEAR: " + controlCalendar.get(Calendar.YEAR) + ", but was: " + aCalendar.get(Calendar.YEAR), controlCalendar.get(Calendar.YEAR) == aCalendar.get(Calendar.YEAR));
        assertTrue("Expected MONTH: " + controlCalendar.get(Calendar.MONTH) + ", but was: " + aCalendar.get(Calendar.MONTH), controlCalendar.get(Calendar.MONTH) == aCalendar.get(Calendar.MONTH));
        assertTrue("Expected DATE: " + controlCalendar.get(Calendar.DATE) + ", but was: " + aCalendar.get(Calendar.DATE), controlCalendar.get(Calendar.DATE) == aCalendar.get(Calendar.DATE));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithDateTime() {
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
        assertTrue("Expected YEAR: " + controlCalendar.get(Calendar.YEAR) + ", but was: " + aCalendar.get(Calendar.YEAR), controlCalendar.get(Calendar.YEAR) == aCalendar.get(Calendar.YEAR));
        assertTrue("Expected MONTH: " + controlCalendar.get(Calendar.MONTH) + ", but was: " + aCalendar.get(Calendar.MONTH), controlCalendar.get(Calendar.MONTH) == aCalendar.get(Calendar.MONTH));
        assertTrue("Expected DATE: " + controlCalendar.get(Calendar.DATE) + ", but was: " + aCalendar.get(Calendar.DATE), controlCalendar.get(Calendar.DATE) == aCalendar.get(Calendar.DATE));
        assertTrue("Expected HOUR: " + controlCalendar.get(Calendar.HOUR) + ", but was: " + aCalendar.get(Calendar.HOUR), controlCalendar.get(Calendar.HOUR) == aCalendar.get(Calendar.HOUR));
        assertTrue("Expected MINUTE: " + controlCalendar.get(Calendar.MINUTE) + ", but was: " + aCalendar.get(Calendar.MINUTE), controlCalendar.get(Calendar.MINUTE) == aCalendar.get(Calendar.MINUTE));
        assertTrue("Expected SECOND: " + controlCalendar.get(Calendar.SECOND) + ", but was: " + aCalendar.get(Calendar.SECOND), controlCalendar.get(Calendar.SECOND) == aCalendar.get(Calendar.SECOND));
        assertTrue("Expected MILLISECOND: " + controlCalendar.get(Calendar.MILLISECOND) + ", but was: " + aCalendar.get(Calendar.MILLISECOND), controlCalendar.get(Calendar.MILLISECOND) == aCalendar.get(Calendar.MILLISECOND));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithDuration() {
        Locale lc = Locale.US;
        Calendar controlCalendar = Calendar.getInstance(lc);
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 12);
        controlCalendar.set(Calendar.MONTH, Calendar.OCTOBER);
        controlCalendar.set(Calendar.DATE, 2);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 40);
        controlCalendar.set(Calendar.SECOND, 27);
        controlCalendar.set(Calendar.MILLISECOND, 870);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("P12Y10M2DT0H40M27.87S", lc);
        assertTrue("Expected YEAR: " + controlCalendar.get(Calendar.YEAR) + ", but was: " + aCalendar.get(Calendar.YEAR), controlCalendar.get(Calendar.YEAR) == aCalendar.get(Calendar.YEAR));
        assertTrue("Expected MONTH: " + controlCalendar.get(Calendar.MONTH) + ", but was: " + aCalendar.get(Calendar.MONTH), controlCalendar.get(Calendar.MONTH) == aCalendar.get(Calendar.MONTH));
        assertTrue("Expected DATE: " + controlCalendar.get(Calendar.DATE) + ", but was: " + aCalendar.get(Calendar.DATE), controlCalendar.get(Calendar.DATE) == aCalendar.get(Calendar.DATE));
        assertTrue("Expected HOUR: " + controlCalendar.get(Calendar.HOUR) + ", but was: " + aCalendar.get(Calendar.HOUR), controlCalendar.get(Calendar.HOUR) == aCalendar.get(Calendar.HOUR));
        assertTrue("Expected MINUTE: " + controlCalendar.get(Calendar.MINUTE) + ", but was: " + aCalendar.get(Calendar.MINUTE), controlCalendar.get(Calendar.MINUTE) == aCalendar.get(Calendar.MINUTE));
        assertTrue("Expected SECOND: " + controlCalendar.get(Calendar.SECOND) + ", but was: " + aCalendar.get(Calendar.SECOND), controlCalendar.get(Calendar.SECOND) == aCalendar.get(Calendar.SECOND));
        assertTrue("Expected MILLISECOND: " + controlCalendar.get(Calendar.MILLISECOND) + ", but was: " + aCalendar.get(Calendar.MILLISECOND), controlCalendar.get(Calendar.MILLISECOND) == aCalendar.get(Calendar.MILLISECOND));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
    }

    public void testToCalendarWithNullInput() {
        Locale lc = Locale.US;
        Calendar aCalendar = dataHelper.toCalendar(null, lc);
        assertTrue("dataHelper.toCalendar(null, Locale.US) did not return null as expected.", aCalendar == null);
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
        assertTrue("Expected YEAR: " + controlCalendar.get(Calendar.YEAR) + ", but was: " + aCalendar.get(Calendar.YEAR), controlCalendar.get(Calendar.YEAR) == aCalendar.get(Calendar.YEAR));
        assertTrue("Expected MONTH: " + controlCalendar.get(Calendar.MONTH) + ", but was: " + aCalendar.get(Calendar.MONTH), controlCalendar.get(Calendar.MONTH) == aCalendar.get(Calendar.MONTH));
        assertTrue("Expected DATE: " + controlCalendar.get(Calendar.DATE) + ", but was: " + aCalendar.get(Calendar.DATE), controlCalendar.get(Calendar.DATE) == aCalendar.get(Calendar.DATE));
        assertTrue("Expected TimeZone: " + controlCalendar.getTimeZone() + ", but was: " + aCalendar.getTimeZone(), controlCalendar.getTimeZone().equals(aCalendar.getTimeZone()));
        assertTrue("Expected toString: " + controlCalendar.toString() + ", but was: " + aCalendar.toString(), controlCalendar.toString().equals(aCalendar.toString()));
    }

    public void testToCalendarWithNullString() {
        Locale lc = Locale.US;
        Calendar aCalendar = dataHelper.toCalendar(null, lc);
        assertTrue("dataHelper.toCalendar(null, Locale.US) did not return null as expected.", aCalendar == null);
    }
}
