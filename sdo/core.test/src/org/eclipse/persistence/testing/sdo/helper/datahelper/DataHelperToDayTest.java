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

public class DataHelperToDayTest extends DataHelperTestCases {
    public DataHelperToDayTest(String name) {
        super(name);
    }

    public void testToDateTimeWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.DATE, 15);
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toDay(controlDate);
        this.assertEquals("----15", tm);
    }

    public void testToDateTimeWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toDay(controlDate);
        this.assertEquals("----01", tm);
    }

    public void testToDateTimeWithNullInput() {
        Date controlDate = null;
        String tm = dataHelper.toDay(controlDate);
        this.assertNull(tm);
    }
}