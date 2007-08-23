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
import java.util.Date;

public class DataHelperToDateTimeTest extends DataHelperTestCases {
    public DataHelperToDateTimeTest(String name) {
        super(name);
    }

    public void testToDateTimeWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 4);
        controlCalendar.set(Calendar.DATE, 1);
        controlCalendar.set(Calendar.HOUR, 12);
        controlCalendar.set(Calendar.MINUTE, 23);
        controlCalendar.set(Calendar.SECOND, 11);
        controlCalendar.set(Calendar.MILLISECOND, 1);
        Date controlDate = controlCalendar.getTime();
        String dt = dataHelper.toDateTime(controlDate);
        log(dt);
        this.assertEquals("2001-05-01T12:23:11.001", dt);
    }

    public void testToDateTimeWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        Date controlDate = controlCalendar.getTime();
        String dt = dataHelper.toDateTime(controlDate);
        this.assertEquals("1970-01-01T00:00:00.0", dt);
    }

    public void testToDateTimeWithNullInput() {
        Date controlDate = null;
        String dt = dataHelper.toDateTime(controlDate);
        this.assertNull(dt);
    }
}