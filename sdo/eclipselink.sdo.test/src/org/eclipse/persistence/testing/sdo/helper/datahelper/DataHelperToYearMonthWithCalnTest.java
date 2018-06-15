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

public class DataHelperToYearMonthWithCalnTest extends DataHelperTestCases {
    public DataHelperToYearMonthWithCalnTest(String name) {
        super(name);
    }

    public void testToYearMonthWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 3);
        String tm = dataHelper.toYearMonth(controlCalendar);
        this.assertEquals("2001-04", tm);
    }

    public void testToYearMonthWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        String tm = dataHelper.toYearMonth(controlCalendar);
        this.assertEquals("1970-01", tm);
    }

    public void testToYearMonthWithNullInput() {
        Calendar controlCalendar = null;
        String tm = dataHelper.toYearMonth(controlCalendar);
        this.assertNull(tm);
    }
}
