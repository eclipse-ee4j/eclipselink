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

public class DataHelperToMonthTest extends DataHelperTestCases {
    public DataHelperToMonthTest(String name) {
        super(name);
    }

    public void testToMonthWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 10);
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toMonth(controlDate);
        this.assertEquals("--11", tm);
    }

    public void testToMonthWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toMonth(controlDate);
        this.assertEquals("--01", tm);
    }

    public void testToMonthWithNullInput() {
        Date controlDate = null;
        String tm = dataHelper.toMonth(controlDate);
        this.assertNull(tm);
    }
}
