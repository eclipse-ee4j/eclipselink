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

public class DataHelperToMonthDayWithCalnTest extends DataHelperTestCases {
    public DataHelperToMonthDayWithCalnTest(String name) {
        super(name);
    }

    public void testToMonthDayWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 3);
        controlCalendar.set(Calendar.DATE, 1);
        String tm = dataHelper.toMonthDay(controlCalendar);
        this.assertEquals("--04-01", tm);
    }

    public void testToMonthDayWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        String tm = dataHelper.toMonthDay(controlCalendar);
        log("HH" + tm);
        this.assertEquals("--01-01", tm);
    }

    public void testToMonthDayWithNullInput() {
        Calendar controlCalendar = null;
        String tm = dataHelper.toMonthDay(controlCalendar);
        this.assertNull(tm);
    }
}
