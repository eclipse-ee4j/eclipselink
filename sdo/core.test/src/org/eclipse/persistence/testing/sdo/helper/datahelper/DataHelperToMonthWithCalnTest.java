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

public class DataHelperToMonthWithCalnTest extends DataHelperTestCases {
    public DataHelperToMonthWithCalnTest(String name) {
        super(name);
    }

    public void testToMonthWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.MONTH, 10);
        String tm = dataHelper.toMonth(controlCalendar);
        this.assertEquals("--11--", tm);
    }

    public void testToMonthWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        String tm = dataHelper.toMonth(controlCalendar);
        this.assertEquals("--01--", tm);
    }

    public void testToMonthWithNullInput() {
        Calendar controlCalendar = null;
        String tm = dataHelper.toMonth(controlCalendar);
        this.assertNull(tm);
    }
}