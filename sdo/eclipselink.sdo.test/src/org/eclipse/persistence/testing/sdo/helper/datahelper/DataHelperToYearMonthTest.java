/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

public class DataHelperToYearMonthTest extends DataHelperTestCases {
    public DataHelperToYearMonthTest(String name) {
        super(name);
    }

    public void testToYearMonthWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.YEAR, 2001);
        controlCalendar.set(Calendar.MONTH, 4);
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toYearMonth(controlDate);
        this.assertEquals("2001-05", tm);
    }

    public void testToYearMonthWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        Date controlDate = controlCalendar.getTime();
        String tm = dataHelper.toYearMonth(controlDate);
        this.assertEquals("1970-01", tm);
    }

    public void testToYearMonthWithNullInput() {
        Date controlDate = null;
        String tm = dataHelper.toYearMonth(controlDate);
        this.assertNull(tm);
    }
}
