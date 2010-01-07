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

public class DataHelperToDurationWithCalnTest extends DataHelperTestCases {
    public DataHelperToDurationWithCalnTest(String name) {
        super(name);
    }

    public void testToDateTimeWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 12);
        controlCalendar.set(Calendar.MONTH, 10);
        controlCalendar.set(Calendar.DATE, 2);
        controlCalendar.set(Calendar.HOUR, 0);
        controlCalendar.set(Calendar.MINUTE, 40);
        controlCalendar.set(Calendar.SECOND, 27);
        controlCalendar.set(Calendar.MILLISECOND, 870);
        String tm = dataHelper.toDuration(controlCalendar);
        this.assertEquals("P12Y11M2DT0H40M27.87S", tm);
    }

    public void testToDateTimeWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        String tm = dataHelper.toDuration(controlCalendar);
        this.assertEquals("P1970Y1M1DT0H0M0.0S", tm);
    }

    public void testToDateTimeWithNullInput() {
        Calendar controlCalendar = null;
        String tm = dataHelper.toDuration(controlCalendar);
        this.assertNull(tm);
    }
}
