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

public class DataHelperToDurationTest extends DataHelperTestCases {
    public DataHelperToDurationTest(String name) {
        super(name);
    }

    public void testToDurationWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 12);
        controlCalendar.set(Calendar.MONTH, 8);
        controlCalendar.set(Calendar.DATE, 2);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 40);
        controlCalendar.set(Calendar.SECOND, 27);
        controlCalendar.set(Calendar.MILLISECOND, 870);
        Date controlDate = controlCalendar.getTime();
        String dur = dataHelper.toDuration(controlDate);
        this.assertEquals("P12Y9M2DT0H40M27.87S", dur);
    }

    public void testToDurationWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        Date controlDate = controlCalendar.getTime();
        String dur = dataHelper.toDuration(controlDate);
        this.assertEquals("P1970Y1M1DT0H0M0.0S", dur);
    }

    public void testToDurationWithNullInput() {
        Date controlDate = null;
        String dur = dataHelper.toDuration(controlDate);
        this.assertNull(dur);
    }
}