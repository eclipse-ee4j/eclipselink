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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of cursored streams vs scrollable cursors.
 */
public class ReadAllStreamvsCursorSizeTest extends PerformanceComparisonTestCase {
    public ReadAllStreamvsCursorSizeTest() {
        setDescription("This test compares the performance of cursored streams vs scrollable cursors.");
        addReadAllScrollableCursorTest();
    }

    /**
     * Read all employees with cursored stream.
     */
    public void test() throws Exception {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.useCursoredStream(1, 1);
        CursoredStream stream = (CursoredStream)getSession().executeQuery(query);
        stream.size();
        stream.close();
    }

    /**
     * Read all employees with scrollable cursor.
     */
    public void addReadAllScrollableCursorTest() {
        PerformanceComparisonTestCase test = new PerformanceComparisonTestCase() {
            public void test() {
                ReadAllQuery query = new ReadAllQuery(Employee.class);
                query.useScrollableCursor();
                ScrollableCursor cursor = (ScrollableCursor)getSession().executeQuery(query);
                cursor.size();
                cursor.close();
            }
        };
        test.setName("ReadAllScrollableCursorSizeTest");
        addTest(test);
    }

    /**
     * Throw a warning until this CR is fixed.
     */
    public void verify() {
        try {
            super.verify();
        } catch (TestErrorException slow) {
            throw new TestWarningException("Cursor size fetches all rows, should use count for size, known bug 3313298 and 3662233)");
        }
    }
}
