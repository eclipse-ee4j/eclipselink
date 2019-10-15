/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     John Vandale - initial API and implementation
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.exceptions.DatabaseException;

/**
 * Bug 309142 - Test that invoking hasNext() on a forward only result set does not
 * throw an SQLException.  This test uses a stored procedure call to
 * get back a forward only result set.
 */
public class ScrollableCursorForwardOnlyResultSetTest extends TestCase {
    protected ScrollableCursor cursor;
    protected DatabaseException caughtException = null;

    public ScrollableCursorForwardOnlyResultSetTest() {
        setDescription("Test the scrollable cursor hasNext() on a forward only result set");
    }

    protected void setup() {
        if (!(getSession().getPlatform().isOracle())) {
            throwWarning("This test is intended for Oracle databases only.");
        }
    }

    public void test() {
        //stored procedure call
        StoredProcedureCall spCall = new StoredProcedureCall();
        spCall.setProcedureName("Read_All_Employees");
        spCall.useNamedCursorOutputAsResultSet("RESULT_CURSOR");

        //query
        DirectReadQuery query = new DirectReadQuery();
        query.setCall(spCall);
        query.useScrollableCursor();
        cursor = (ScrollableCursor)getSession().executeQuery(query);

        // If the driver returns a forward-only ResultSet initialized to afterLast there's nothing ScrollableCursor can do with it.
        try{
            if ((cursor.getResultSet().isAfterLast()) && (cursor.getResultSet().getType() == java.sql.ResultSet.TYPE_FORWARD_ONLY)) {
                throwWarning("The ResultSet returned from the query is TYPE_FORWARD_ONLY and initialized to afterLast.");
            }
        } catch (java.sql.SQLException sqle) {
            throwWarning("Unexpected SQLException thrown while checking the ResultSet.");
        }

        // iterate the cursor
        try {
            while (cursor.hasNext()) {
                cursor.next();
            }
        } catch (org.eclipse.persistence.exceptions.DatabaseException dbe) {
            caughtException = dbe;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Verify if the scrollable cursor can iterate a forward only result set
     */
    protected void verify() {
        if (caughtException != null) {
            if (caughtException.getDatabaseErrorCode() == 17075) {//SQLException: Invalid operation for forward only result set
                throwError("The scrollable cursor can't iterate a forward only result set", caughtException);
            } else {
                throwError(caughtException.getMessage(), caughtException);
            }
        }
    }
}
