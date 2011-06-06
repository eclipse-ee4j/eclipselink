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
package org.eclipse.persistence.testing.tests.performance.java;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance for Calendar getTime.
 */
public class DatePrintingTest extends PerformanceComparisonTestCase {
    protected Date date = new Date();

    public DatePrintingTest() {
        setName("DatePrintingPerformanceComparisonTest");
        setDescription("This test compares the performance in printing dates.");
        addCalendarPrintTest();
    }

    /**
     * Prints using deprecate APIs.
     */
    @SuppressWarnings("deprecation")
    public void test() throws Exception {
        StringWriter writer = new StringWriter();
        writer.write(String.valueOf(this.date.getYear()));
        writer.write(String.valueOf(this.date.getDate()));
        writer.write(String.valueOf(this.date.getMonth()));
        writer.write(String.valueOf(this.date.getHours()));
        writer.write(String.valueOf(this.date.getMinutes()));
        writer.write(String.valueOf(this.date.getSeconds()));
        writer.toString();
    }

    /**
     * Print through converting to calendar.
     */
    public void addCalendarPrintTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DatePrintingTest.this.date);
                StringWriter writer = new StringWriter();
                writer.write(String.valueOf(calendar.get(Calendar.YEAR)));
                writer.write(String.valueOf(calendar.get(Calendar.DATE)));
                writer.write(String.valueOf(calendar.get(Calendar.MONTH)));
                writer.write(String.valueOf(calendar.get(Calendar.HOUR)));
                writer.write(String.valueOf(calendar.get(Calendar.MINUTE)));
                writer.write(String.valueOf(calendar.get(Calendar.SECOND)));
                writer.toString();
            }
        };
        test.setName("CalendarPrintTest");
        //test.setAllowableDecrease(-50);
        addTest(test);
    }
}
