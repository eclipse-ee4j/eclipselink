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
package org.eclipse.persistence.testing.tests.performance.reading;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.performance.toplink.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of cursored streams vs scrollable cursors.
 */
public class ReadAllStreamvsCursorTest extends PerformanceComparisonTestCase {
    public ReadAllStreamvsCursorTest() {
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
        for (int index = 0; index < 10; index++) {
            if (stream.hasMoreElements()) {
                stream.nextElement();
            }
        }
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
                for (int index = 0; index < 10; index++) {
                    if (cursor.hasNext()) {
                        cursor.next();
                    }
                }
                cursor.close();
            }
        };
        test.setName("ReadAllScrollableCursorTest");
        test.setAllowableDecrease(-90);
        addTest(test);
    }
}
