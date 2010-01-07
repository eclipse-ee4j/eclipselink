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

public class DataHelperToDayWithCalnTest extends DataHelperTestCases {
    public DataHelperToDayWithCalnTest(String name) {
        super(name);
    }

    public void testToDateTimeWithFullSetting() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        controlCalendar.set(Calendar.DATE, 15);
        String tm = dataHelper.toDay(controlCalendar);
        this.assertEquals("---15", tm);
    }

    public void testToDateTimeWithDefault() {
        Calendar controlCalendar = Calendar.getInstance();
        controlCalendar.clear();
        String tm = dataHelper.toDay(controlCalendar);
        this.assertEquals("---01", tm);
    }

    public void testToDateTimeWithNullInput() {
        Calendar controlCalendar = null;
        String tm = dataHelper.toDay(controlCalendar);
        this.assertNull(tm);
    }
}
