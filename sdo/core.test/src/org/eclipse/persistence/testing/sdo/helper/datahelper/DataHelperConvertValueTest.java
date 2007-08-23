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

public class DataHelperConvertValueTest extends DataHelperTestCases {
    public DataHelperConvertValueTest(String name) {
        super(name);
    }

    public void testToCalendarWithGYearMonth() {
        String b = "true";
        Boolean B = new Boolean(b);
        this.assertEquals(B, (Boolean)dataHelper.convertValue(b, Boolean.class, null));
    }
}