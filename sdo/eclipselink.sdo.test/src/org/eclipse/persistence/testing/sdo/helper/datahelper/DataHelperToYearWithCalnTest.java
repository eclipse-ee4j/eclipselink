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

public class DataHelperToYearWithCalnTest extends DataHelperTestCases {
    public DataHelperToYearWithCalnTest(String name) {
        super(name);
    }

    public void testToYearWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2000);
        String tm = dataHelper.toYear(controlCalendar);
        this.assertEquals("2000", tm);
    }

    public void testToYearWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        String tm = dataHelper.toYear(controlCalendar);
        this.assertEquals("1970", tm);
    }

    public void testToYearWithNullInput() {
        Calendar controlCalendar = null;
        String tm = dataHelper.toYear(controlCalendar);
        this.assertNull(tm);
    }
}
