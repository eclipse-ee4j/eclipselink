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
import java.sql.Timestamp;

public class TimestampFromLongTest extends AutoVerifyTestCase {
    public static void main(String[] args) {
        TimestampFromLongTest test = new TimestampFromLongTest();
        test.setup();
        test.test();
        test.verify();
        test.reset();
    }

    public TimestampFromLongTest() {
        super();
        setDescription("Test of Helper.timestampFromLong(Long longObject)");
    }

    Long currentTime;
    Timestamp optimizedTime;
    boolean optimizedDatesState;

    public void setup() {
        currentTime = new Long(System.currentTimeMillis());
        optimizedDatesState = Helper.shouldOptimizeDates();
    }

    public void test() {
        Helper.setShouldOptimizeDates(true);
        optimizedTime = Helper.timestampFromLong(currentTime);
    }

    public void verify() {
        String testTime = new Timestamp(currentTime.longValue()).toString();
        if (!(testTime.equals(optimizedTime.toString()))) {
            throw new TestErrorException("Failed to convert Long to java.sql.Timestamp when shouldOptimizedDates is on");
        }
    }

    public void reset() {
        Helper.setShouldOptimizeDates(optimizedDatesState);
    }
}
