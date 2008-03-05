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
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import java.util.Calendar;
import java.util.TimeZone;

public class DataHelperToCalendarTest extends DataHelperTestCases {
    public DataHelperToCalendarTest(String name) {
        super(name);
    }

    public void testToCalendarWithGYearMonth() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 4);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-05");
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendaWithGYear() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001");
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendaWithGMonthDay() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 11);
        controlCalendar.set(Calendar.DATE, 4);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("--12-04");
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendaWithGMonth() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 9);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("--10--");
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendaWithGDay() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.DATE, 31);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("----31");
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendaeWithTime() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.HOUR, 1);
        controlCalendar.set(Calendar.MINUTE, 21);
        controlCalendar.set(Calendar.SECOND, 12);
        controlCalendar.set(Calendar.MILLISECOND, 0);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aDate = dataHelper.toCalendar("01:21:12");
        this.assertEquals(controlCalendar, aDate);
    }

    public void testToCalendaWithDate() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-01-01");
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendaWithDateTime() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 0);
        controlCalendar.set(Calendar.SECOND, 1);
        controlCalendar.set(Calendar.MILLISECOND, 0);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("2001-01-01T00:00:01");
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendaWithDuration() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 12);
        controlCalendar.set(Calendar.MONTH, 9);
        controlCalendar.set(Calendar.DATE, 2);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 40);
        controlCalendar.set(Calendar.SECOND, 27);
        controlCalendar.set(Calendar.MILLISECOND, 870);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar aCalendar = dataHelper.toCalendar("P12Y10M2DT0H40M27.87S");
        log(controlCalendar.getTime().toString());
        log(aCalendar.getTime().toString());
        this.assertEquals(controlCalendar, aCalendar);
    }

    public void testToCalendaWithNullInput() {
        try {
            Calendar aCalendar = dataHelper.toCalendar(null);
            this.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testToCalendaWithInvalidInput() {
        try {
            Calendar aCalendar = dataHelper.toCalendar("----2000");
            this.fail();
        } catch (IllegalArgumentException e) {
        }
    }
}