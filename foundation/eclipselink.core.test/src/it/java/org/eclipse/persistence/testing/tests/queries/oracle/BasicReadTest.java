/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.oracle;

import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;

public class BasicReadTest extends TestCase {
    static String HINT_STRING = "/*+ ALL_ROWS */";
    static String INNER_HINT = "/*+ RULE */";
    DatabaseQuery query;
    boolean subselect = false;

    public BasicReadTest(DatabaseQuery theQuery) {
        query = theQuery;
    }

    @Override
    public void setup() {
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test is intended for Oracle databases only");
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public void test() {
        query.setHintString(HINT_STRING);
        getSession().executeQuery(query);
    }

    @Override
    public void verify() throws Exception {
        String SQLString = query.getSQLString();
        if (!SQLString.contains(HINT_STRING)) {
            throw new TestErrorException("Hint String not in SQL String");
        }
        if (subselect) {
            if (!SQLString.contains(INNER_HINT)) {
                throw new TestErrorException("Inner Hint String not in SQL String");
            }
        }
    }

    public void setSubselect(boolean sub) {
        subselect = sub;
    }

}
