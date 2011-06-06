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
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.performance.Address;
import org.eclipse.persistence.testing.models.performance.toplink.*;

/**
 * This test compares the performance of instance creation.
 */
public class InstanceCreationTest extends PerformanceComparisonTestCase {
    public InstanceCreationTest() {
        setName("InstanceCreation PerformanceComparisonTest");
        setDescription("This test compares the performance of instance creation.");
        addAddressTest();
        addEmployeeTest();
        addCalendarTest();
        addDateTest();
    }

    /**
     * new Object.
     */
    public void test() throws Exception {
        new Object();
    }

    /**
     * New Address.
     */
    public void addAddressTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                new Address();
            }
        };
        test.setName("NewAddressTest");
        test.setAllowableDecrease(-50);
        addTest(test);
    }

    /**
     * New Employee.
     */
    public void addEmployeeTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                new Employee();
            }
        };
        test.setName("NewEmployeeTest");
        test.setAllowableDecrease(-200);
        addTest(test);
    }

    /**
     * New Calendar.
     */
    public void addCalendarTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                Calendar calendar = Calendar.getInstance();
                calendar.set(1973, 10, 11);
            }
        };
        test.setName("NewCalendarTest");
        test.setAllowableDecrease(-200);
        addTest(test);
    }

    /**
     * New Date.
     */
    public void addDateTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            @SuppressWarnings("deprecation")
            public void test() {
                new Date(1973, 10, 11);
            }
        };
        test.setName("NewDateTest");
        test.setAllowableDecrease(-200);
        addTest(test);
    }
}
