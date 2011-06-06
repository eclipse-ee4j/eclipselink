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
