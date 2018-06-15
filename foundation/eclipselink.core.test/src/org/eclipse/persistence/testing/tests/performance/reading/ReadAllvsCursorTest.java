/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.performance.reading;

import java.util.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of read all vs cursored read all.
 */
public class ReadAllvsCursorTest extends PerformanceComparisonTestCase {
    public ReadAllvsCursorTest() {
        setDescription("This test compares the performance of read all vs cursored read all.");
        addReadAllCursoredStreamTest();
        addReadAllScrollableCursorTest();
    }

    /**
     * Read all employees.
     */
    public void test() throws Exception {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        List results = (List)getSession().executeQuery(query);
    }

    /**
     * Read all employees with cursored stream.
     */
    public void addReadAllCursoredStreamTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.useCursoredStream();
                Cursor cursor = (Cursor)getSession().executeQuery(query);
                cursor.close();
            }
        };
        test.setName("ReadAllCursoredStreamTest");
        test.setAllowableDecrease(100);
        addTest(test);
    }

    /**
     * Read all employees with scrollable cursor.
     */
    public void addReadAllScrollableCursorTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.useScrollableCursor();
                Cursor cursor = (Cursor)getSession().executeQuery(query);
                cursor.close();
            }
        };
        test.setName("ReadAllScrollableCursorTest");
        test.setAllowableDecrease(50);
        addTest(test);
    }
}
