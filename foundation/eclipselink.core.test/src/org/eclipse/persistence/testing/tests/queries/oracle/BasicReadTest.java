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
package org.eclipse.persistence.testing.tests.queries.oracle;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

public class BasicReadTest extends TestCase {
    static String HINT_STRING = "/*+ ALL_ROWS */";
    static String INNER_HINT = "/*+ RULE */";
    DatabaseQuery query;
    boolean subselect = false;

    public BasicReadTest(DatabaseQuery theQuery) {
        query = theQuery;
    }

    public void setup() {
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test is intended for Oracle databases only");
        }
    }

    public void reset() {
    }

    public void test() {
        query.setHintString(HINT_STRING);
        getSession().executeQuery(query);
    }

    public void verify() throws Exception {
        String SQLString = query.getSQLString();
        if (SQLString.indexOf(HINT_STRING) == -1) {
            throw new TestErrorException("Hint String not in SQL String");
        }
        if (subselect) {
            if (SQLString.indexOf(INNER_HINT) == -1) {
                throw new TestErrorException("Inner Hint String not in SQL String");
            }
        }
    }

    public void setSubselect(boolean sub) {
        subselect = sub;
    }

}
