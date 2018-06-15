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

public class DataHelperToMonthWithCalnTest extends DataHelperTestCases {
    public DataHelperToMonthWithCalnTest(String name) {
        super(name);
    }

    public void testToMonthWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 10);
        String tm = dataHelper.toMonth(controlCalendar);
        this.assertEquals("--11", tm);
    }

    public void testToMonthWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        String tm = dataHelper.toMonth(controlCalendar);
        this.assertEquals("--01", tm);
    }

    public void testToMonthWithNullInput() {
        Calendar controlCalendar = null;
        String tm = dataHelper.toMonth(controlCalendar);
        this.assertNull(tm);
    }
}
