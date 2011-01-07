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
import java.sql.Time;

public class TimeFromLongTest extends AutoVerifyTestCase {
    public static void main(String[] args) {
        TimeFromLongTest test = new TimeFromLongTest();
        test.setup();
        test.test();
        test.verify();
        test.reset();
    }

    public TimeFromLongTest() {
        super();
        setDescription("Test of Helper.timeFromLong(Long longObject)");
    }

    Long currentTime;
    Time nonOptimizedTime;
    Time optimizedTime;
    boolean optimizedDatesState;

    public void setup() {
        currentTime = new Long(System.currentTimeMillis());
        optimizedDatesState = Helper.shouldOptimizeDates();
    }

    public void test() {
        Helper.setShouldOptimizeDates(false);
        nonOptimizedTime = Helper.timeFromLong(currentTime);

        Helper.setShouldOptimizeDates(true);
        optimizedTime = Helper.timeFromLong(currentTime);
    }

    public void verify() {
        Time testTime = new Time(currentTime.longValue());

        if (!(testTime.equals(nonOptimizedTime) && testTime.toString().equals(nonOptimizedTime.toString()))) {
            throw new TestErrorException("Failed to convert Long to java.sql.Time when shouldOptimizedDates is off");
        }
        if (!(testTime.equals(optimizedTime) && testTime.toString().equals(optimizedTime.toString()))) {
            throw new TestErrorException("Failed to convert Long to java.sql.Time when shouldOptimizedDates is on");
        }
    }

    public void reset() {
        Helper.setShouldOptimizeDates(optimizedDatesState);
    }
}
