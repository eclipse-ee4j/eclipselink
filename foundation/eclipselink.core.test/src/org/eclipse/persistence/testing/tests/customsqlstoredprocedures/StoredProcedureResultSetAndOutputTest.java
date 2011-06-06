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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.exceptions.*;

/**
 * CR# 3978 DatabaseAccessor.executeCall(...) not currently set up to retrieve BOTH a ResultSet and
 * an OUT parameter from the same  stored procedure. This problem only surfaces on database
 * platforms that provide stored procedure support for BOTH ResultSets and OUT parameters e.g. DB2
 * and Sybase
 */
public class StoredProcedureResultSetAndOutputTest extends TestCase {
    protected StoredProcedureCall call = null;
    protected DatabaseException caughtException = null;
    protected Vector events = null;
    protected Vector result = null;
    private boolean shouldBindAllParameters;
    private boolean shouldBindAllParametersOriginal;

    public StoredProcedureResultSetAndOutputTest() {
        this(true);
    }

    public StoredProcedureResultSetAndOutputTest(boolean shouldBindAllParameters) {
        super();
        this.shouldBindAllParameters = shouldBindAllParameters;
        setName(getName() + " bind = " + shouldBindAllParameters);
    }

    /**
     * this method is required to retrieve the "call" variable in all sub-classes
     */
    public StoredProcedureCall getStoredProcedureCall() {
        return call;
    }

    public void setup() {
        shouldBindAllParametersOriginal = getSession().getLogin().shouldBindAllParameters();
        // Note: Normally we build a stored procedure for execution in the setup method.
        // DB2 does not allow us to do that, so we assume the procedure exists.
        // Stored procecures with both result sets and output parameters are not supported
        // on all DBs currently we just test DB2.
        // ET: This test does not support jcc driver(bug 3551317)
        if (!(getSession().getPlatform().isDB2()) || (getSession().getLogin().getDatabaseURL().indexOf("jcc") == -1)) {
            throw new TestWarningException("This test can only be run in DB2, and not support jcc driver.");
        }
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        // Setup the event listener for the output parameters
        events = new Vector();
        getSession().getEventManager().addListener(new StoredProcedureOutputListener(this.events));

        call = new StoredProcedureCall();

        getSession().getLogin().setShouldBindAllParameters(shouldBindAllParameters);
        buildStoredProcedureCall();
    }

    /**
     * build instance of StoredProcedureCall
     */
    public void buildStoredProcedureCall() {
        call.setProcedureName("OUT_RES_TEST");
        call.addUnamedOutputArgument("OUT1");
        call.addUnamedOutputArgument("OUT2");
        call.addUnamedOutputArgument("OUT3");
        call.setReturnsResultSet(true);
    }

    /**
     * ReadAllQuery used to determine if correct results obtained from stored procedure call
     */
    public void test() {
        ReportQuery query = new ReportQuery(Employee.class, new org.eclipse.persistence.expressions.ExpressionBuilder());
        query.setCall(call);
        query.addAttribute("firstName");
        try {
            result = (Vector)getSession().executeQuery(query);
        } catch (DatabaseException de) {
            caughtException = de;
        }
    }

    public void verify() throws Exception {
        if (caughtException != null) {
            throw new TestErrorException("Test to return a ResultSet and an OUT parameter from a stored procedure failed.\n" + "This exception thrown while testing test case.\n" + "----- StoredProcedureResultSetAndOutputTest() -----\n" + caughtException.getMessage());
        }
        if ((result == null) || (result.size() != 12)) {
            throw new TestErrorException("The stored procedure was executed but no values were returned in the result set: " + result);

        }
        if (events.isEmpty() || (((DatabaseRecord)((SessionEvent)events.elementAt(0)).getResult()).getFields().size() != 3)) {
            throw new TestErrorException("The stored procedure was executed but the return values were not returned.");
        }
    }

    public void reset() {
        getSession().getLogin().setShouldBindAllParameters(shouldBindAllParametersOriginal);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
