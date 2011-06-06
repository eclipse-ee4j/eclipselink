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

public class TimestampFromStringTest extends AutoVerifyTestCase {
    public static void main(String[] args) {
        TimestampFromStringTest test = new TimestampFromStringTest();
        test.setup();
        test.test();
        test.verify();
        test.reset();
    }

    public TimestampFromStringTest() {
        super();
        setDescription("Test of Helper.timestampFromString(String stringObject)");
    }

    String currentTime;
    Timestamp optimizedTime;
    boolean optimizedDatesState;

    public void setup() {
        currentTime = new Timestamp(System.currentTimeMillis()).toString();
        optimizedDatesState = Helper.shouldOptimizeDates();
    }

    public void test() {
        Helper.setShouldOptimizeDates(true);
        optimizedTime = Helper.timestampFromString(currentTime);
    }

    public void verify() {
        if (!(currentTime.equals(optimizedTime.toString()))) {
            throw new TestErrorException("Failed to convert String to java.sql.Timestamp when shouldOptimizedDates is on");
        }
    }

    public void reset() {
        Helper.setShouldOptimizeDates(optimizedDatesState);
    }
}
