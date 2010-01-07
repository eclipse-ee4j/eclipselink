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
import java.util.Date;

public class DataHelperToYearMonthDayTest extends DataHelperTestCases {
    public DataHelperToYearMonthDayTest(String name) {
        super(name);
    }

    public void testToMonthDayWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 0);
        controlCalendar.set(Calendar.DATE, 01);
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toYearMonthDay(controlDate);
        this.assertEquals("2001-01-01", tm);
    }

    public void testToMonthDayWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toYearMonthDay(controlDate);
        this.assertEquals("1970-01-01", tm);
    }

    public void testToMonthDayWithNullInput() {
        Date controlDate = null;
        String tm = dataHelper.toYearMonthDay(controlDate);
        this.assertNull(tm);
    }
}
