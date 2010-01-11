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

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;

public class StoredProcedureTest_Inout_Out_In extends TestCase {
    DatabaseRecord row;
    int mode;
    boolean useCustomSQL;
    boolean useArgumentNames;
    boolean shouldBindAllParameters;
    Integer in = new Integer(1);
    Integer inout = new Integer(2);
    static final int PROC = 0;
    static final int FUNC = 1;

    public static StoredProcedureTest_Inout_Out_In proc(boolean useArgumentNames, boolean shouldBindAllParameters) {
        return new StoredProcedureTest_Inout_Out_In(PROC, false, useArgumentNames, shouldBindAllParameters);
    }

    public static StoredProcedureTest_Inout_Out_In procUseCustomSQL(boolean shouldBindAllParameters) {
        return new StoredProcedureTest_Inout_Out_In(PROC, true, false, shouldBindAllParameters);
    }

    public static StoredProcedureTest_Inout_Out_In func(boolean useArgumentNames, boolean shouldBindAllParameters) {
        return new StoredProcedureTest_Inout_Out_In(FUNC, false, useArgumentNames, shouldBindAllParameters);
    }

    public static StoredProcedureTest_Inout_Out_In funcUseCustomSQL(boolean shouldBindAllParameters) {
        return new StoredProcedureTest_Inout_Out_In(FUNC, true, false, shouldBindAllParameters);
    }

    public StoredProcedureTest_Inout_Out_In(int mode, boolean useCustomSQL, boolean useArgumentNames, boolean shouldBindAllParameters) {
        this.useCustomSQL = useCustomSQL;
        this.useArgumentNames = useArgumentNames;
        this.shouldBindAllParameters = shouldBindAllParameters;
        this.mode = mode;

        if (mode == PROC) {
            setName(getName() + " proc");
        } else if (mode == FUNC) {
            setName(getName() + " func");
        }

        if (useCustomSQL) {
            setName(getName() + " custom");
        } else {
            setName(getName() + " stored");
            if (mode == PROC) {
                setName(getName() + "Proc ");
            } else if (mode == FUNC) {
                setName(getName() + "Func ");
            }
            if (useArgumentNames) {
                setName(getName() + " named");
            }
        }

        if (shouldBindAllParameters) {
            setName(getName() + " Bind");
        } else {
            setName(getName() + " don'tBind");
        }
    }

    public void setup() {
        // right now only the stored procedure is set up in Oracle
        if (!(getSession().getPlatform().isOracle())) {
            throw new TestWarningException("This test can only be run in Oracle");
        }
    }

    public void test() {
        DatabaseCall call = null;
        if (useCustomSQL) {
            String str;
            SQLCall sqlCall = null;
            if (mode == PROC) {
                str = "BEGIN StoredProcedure_InOut_Out_In(####P_INOUT, ###P_OUT, #P_IN); END;";
                sqlCall = new SQLCall(str);
            } else if (mode == FUNC) {
                str = "BEGIN ###RESULT := StoredFunction_InOut_Out_In(####P_INOUT, ###P_OUT, #P_IN); END;";
                sqlCall = new SQLCall(str);
                sqlCall.setCustomSQLArgumentType("RESULT", Integer.class);
            }
            sqlCall.setCustomSQLArgumentType("P_OUT", Integer.class);
            call = sqlCall;
        } else {
            StoredProcedureCall spCall = null;
            if (mode == PROC) {
                spCall = new StoredProcedureCall();
                spCall.setProcedureName("StoredProcedure_InOut_Out_In");
            } else if (mode == FUNC) {
                StoredFunctionCall sfCall = new StoredFunctionCall();
                sfCall.setProcedureName("StoredFunction_InOut_Out_In");
                sfCall.setResult("RESULT", Integer.class);
                spCall = sfCall;
            }
            if (useArgumentNames) {
                //			spCall.addNamedInOutputArgument("P_INOUT", "P_INOUT", "P_INOUT", Integer.class);
                spCall.addNamedInOutputArgument("P_INOUT");
                spCall.addNamedOutputArgument("P_OUT", "P_OUT", Integer.class);
                spCall.addNamedArgument("P_IN");
            } else {
                //			spCall.addUnamedInOutputArgument("P_INOUT", Integer.class);
                spCall.addUnamedInOutputArgument("P_INOUT");
                spCall.addUnamedOutputArgument("P_OUT", Integer.class);
                spCall.addUnamedArgument("P_IN");
            }
            call = spCall;
        }

        DataReadQuery query = new DataReadQuery();
        query.setShouldBindAllParameters(shouldBindAllParameters);
        query.setCall(call);
        query.addArgument("P_IN");
        query.addArgument("P_INOUT");

        Vector args = new Vector(2);
        args.addElement(in);
        args.addElement(inout);

        row = (DatabaseRecord)((Vector)getSession().executeQuery(query, args)).firstElement();
    }

    public void verify() {
        Number inoutExpected = in;
        Number outExpected = inout;

        Integer inoutReturned = new Integer(((Number)row.get("P_INOUT")).intValue());
        Integer outReturned = new Integer(((Number)row.get("P_OUT")).intValue());

        if (!inoutExpected.equals(inoutReturned)) {
            throw new TestErrorException("Invalid value P_INOUT = " + inoutReturned + "; should be " + inoutExpected);
        }
        if (!outExpected.equals(outReturned)) {
            throw new TestErrorException("Invalid value P_OUT = " + outReturned + "; should be " + outExpected);
        }

        if (mode == FUNC) {
            Integer result = new Integer(((Number)row.get("RESULT")).intValue());

            if (!result.equals(outReturned)) {
                throw new TestErrorException("Invalid value RESULT = " + result + "; should be " + outReturned);
            }
        }
    }
}
