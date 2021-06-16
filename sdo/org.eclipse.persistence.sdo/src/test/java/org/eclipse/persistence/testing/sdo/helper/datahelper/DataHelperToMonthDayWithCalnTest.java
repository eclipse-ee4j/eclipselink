/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
