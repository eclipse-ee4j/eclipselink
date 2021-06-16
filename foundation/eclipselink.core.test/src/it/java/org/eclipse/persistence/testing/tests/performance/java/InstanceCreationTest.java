/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
