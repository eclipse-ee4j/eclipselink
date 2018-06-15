/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DataHelperToYearTest extends DataHelperTestCases {
    public DataHelperToYearTest(String name) {
        super(name);
    }

    public void testToYearWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toYear(controlDate);
        this.assertEquals("2000", tm);
    }

    public void testToYearWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        controlCalendar.clear();
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toYear(controlDate);
        this.assertEquals("1970", tm);
    }

    public void testToYearWithNullInput() {
        Date controlDate = null;
        String tm = dataHelper.toYear(controlDate);
        this.assertNull(tm);
    }
}
