/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.helper;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.*;
import java.util.Date;
import java.sql.Timestamp;

public class TimestampFromDateTest extends AutoVerifyTestCase {
    public static void main(String[] args) {
        TimestampFromDateTest test = new TimestampFromDateTest();
        test.setup();
        test.test();
        test.verify();
        test.reset();
    }

    public TimestampFromDateTest() {
        super();
        setDescription("Test of Helper.timestampFromDate(java.util.Date dateObject)");
    }

    Date currentTime;
    Timestamp optimizedTime;
    boolean optimizedDatesState;

    public void setup() {
        currentTime = Helper.utilDateFromLong(new Long(System.currentTimeMillis()));
        optimizedDatesState = Helper.shouldOptimizeDates();
    }

    public void test() {
        Helper.setShouldOptimizeDates(true);
        optimizedTime = Helper.timestampFromDate(currentTime);
    }

    public void verify() {
        String testTime = new Timestamp(currentTime.getTime()).toString();
        if (!(testTime.equals(optimizedTime.toString()))) {
            throw new TestErrorException("Failed to convert java.util.Date to java.sql.Timestamp when shouldOptimizedDates is on");
        }
    }

    public void reset() {
        Helper.setShouldOptimizeDates(optimizedDatesState);
    }
}
