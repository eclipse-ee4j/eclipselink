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
import java.sql.Time;

public class TimeFromDateTest extends AutoVerifyTestCase {
    public static void main(String[] args) {
        TimeFromDateTest test = new TimeFromDateTest();
        test.setup();
        test.test();
        test.verify();
        test.reset();
    }

    public TimeFromDateTest() {
        super();
        setDescription("Test of Helper.timeFromDate(java.util.Date dateObject)");
    }

    Date testDate;
    Time nonOptimizedTime;
    Time optimizedTime;
    boolean optimizedDatesState;

    public void setup() {
        testDate = Helper.utilDateFromLong(new Long(System.currentTimeMillis()));
        optimizedDatesState = Helper.shouldOptimizeDates();
    }

    public void test() {
        Helper.setShouldOptimizeDates(false);
        nonOptimizedTime = Helper.timeFromDate(testDate);

        Helper.setShouldOptimizeDates(true);
        optimizedTime = Helper.timeFromDate(testDate);
    }

    public void verify() {
        String testTime = new Time(testDate.getTime()).toString();

        if (!(testTime.equals(nonOptimizedTime.toString()))) {
            throw new TestErrorException("Failed to convert java.util.Date to java.sql.Time when shouldOptimizedDates is off");
        }
        if (!(testTime.equals(optimizedTime.toString()))) {
            throw new TestErrorException("Failed to convert java.util.Date to java.sql.Time when shouldOptimizedDates is on");
        }
    }

    public void reset() {
        Helper.setShouldOptimizeDates(optimizedDatesState);
    }
}
