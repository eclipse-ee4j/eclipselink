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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import java.util.Vector;
import java.math.BigDecimal;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class StoredProcWithOutputParamsAndResultSetTest extends TestCase {
    boolean useCustomSQL;
    boolean shouldBindAllParameters;
    boolean shouldBindAllParametersOriginal;

    public StoredProcWithOutputParamsAndResultSetTest(boolean useCustomSQL, boolean shouldBindAllParameters) {
        super();
        this.useCustomSQL = useCustomSQL;
        this.shouldBindAllParameters = shouldBindAllParameters;

        if (useCustomSQL) {
            setName(getName() + " customSQL");
        } else {
            setName(getName() + " storedProc");
        }

        if (shouldBindAllParameters) {
            setName(getName() + " Bind");
        } else {
            setName(getName() + " don'tBind");
        }
    }

    public void setup() {
        shouldBindAllParametersOriginal = getSession().getLogin().getShouldBindAllParameters();
        //right now only the stored procedure is set up in SQLServer
        if (!(getSession().getPlatform().isSQLServer() || getSession().getPlatform().isSybase() || getSession().getPlatform().isSQLAnywhere())) {
            throw new TestWarningException("This test can only be run in SQLServer. Or Sybase or SQLAnywhere");
        }
        getSession().getLogin().setShouldBindAllParameters(shouldBindAllParameters);
    }

    public void test() {
        ReadAllQuery readQuery = new ReadAllQuery(Employee.class);
        DatabaseCall call;
        SQLCall sqlCall;
        StoredProcedureCall spCall;

        // sybase can not do out params only
        boolean useInOut = getSession().getPlatform().isSybase() || getSession().getPlatform().isSQLAnywhere();
        if (useCustomSQL) {
            String prefix;
            if (useInOut) {
                prefix = "####";
            } else {
                prefix = "###";
            }
            String parameterNamePrefix = getSession().getPlatform().getStoredProcedureParameterPrefix();
            sqlCall = new SQLCall("EXECUTE Select_Output_and_ResultSet " + parameterNamePrefix + "ARG1 = #argument, " + 
                    parameterNamePrefix + "VERSION = " + prefix + "version " + getSession().getPlatform().getOutputProcedureToken());
            sqlCall.setCustomSQLArgumentType("version", BigDecimal.class);
            call = sqlCall;
        } else {
            spCall = new StoredProcedureCall();
            spCall.setProcedureName("Select_Output_and_ResultSet");
            spCall.addNamedArgument("ARG1", "argument");
            if (useInOut) {
                spCall.addNamedInOutputArgumentValue("VERSION", new Long(0), "VERSION", java.math.BigDecimal.class);
            } else {
                spCall.addNamedOutputArgument("VERSION", "VERSION", BigDecimal.class);
            }
            call = spCall;
        }
        call.setReturnsResultSet(true);
        readQuery.setCall(call);
        readQuery.addArgument("argument");
        if (useCustomSQL && useInOut) {
            readQuery.addArgument("version");
        }
        getSession().removeQuery("dblogin");
        getSession().addQuery("dblogin", readQuery);
        Vector args = new Vector(2);
        args.addElement(new Integer(1));
        if (useCustomSQL && useInOut) {
            args.addElement(new Long(0));
        }
        try {
            Vector vResult = (Vector)getSession().executeQuery("dblogin", args);
        } catch (ClassCastException e) {
            throw new TestErrorException("Stored Procedure is returning a Row and the query expects a Vector");
        }

        readQuery = new ReadAllQuery(Employee.class);
        if (useCustomSQL) {
            return;
        } else {
            spCall = new StoredProcedureCall();
            spCall.setProcedureName("Select_Output_and_ResultSet");
            spCall.addNamedArgumentValue("ARG1", new Integer(1));
            if (useInOut) {
                spCall.addNamedInOutputArgumentValue("VERSION", new Long(0), "VERSION", java.math.BigDecimal.class);
            } else {
                spCall.addNamedOutputArgument("VERSION", "VERSION", BigDecimal.class);
            }
            call = spCall;
        }
        call.setReturnsResultSet(true);
        readQuery.setCall(call);
        try {
            Vector vResult = (Vector)getSession().executeQuery(readQuery);
        } catch (ClassCastException e) {
            throw new TestErrorException("Stored Procedure is returning a Row and the query expects a Vector");
        }
    }

    public void reset() {
        getSession().getLogin().setShouldBindAllParameters(shouldBindAllParametersOriginal);
    }
}
