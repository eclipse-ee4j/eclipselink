/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Apr 23, 2009-1.1.1 Chris Delahunt 
 *       - Bug#273338: NullPointerException possible in DatabaseAccessor 
 ******************************************************************************/  
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
