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
//     Apr 23, 2009-1.1.1 Chris Delahunt
//       - Bug#273338: NullPointerException possible in DatabaseAccessor
package org.eclipse.persistence.testing.tests.validation;

import java.sql.SQLException;

import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * @author Chris Delahunt
 *
 */
public class ConnectionIsNullAccessorTest extends ExceptionTest {
    DatabaseAccessor dbAccessor;
    protected void setup() {
        dbAccessor = new DatabaseAccessor();
        expectedException = org.eclipse.persistence.exceptions.DatabaseException.databaseAccessorConnectionIsNull(dbAccessor, null);
    }

    public void test() {
        AbstractSession session = (AbstractSession)getSession();
        try {

            SQLCall call = new SQLCall("Select * from Employee");
            dbAccessor.prepareStatement(call, session, false);
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        } catch( SQLException sqlException){
            //ignore, it is not an expected exception for this test
        }
    }
}
