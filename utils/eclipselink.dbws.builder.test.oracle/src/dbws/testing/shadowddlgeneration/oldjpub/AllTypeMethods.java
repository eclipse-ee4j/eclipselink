/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.ResultSet;
import java.sql.SQLException;

// Includes all the columns in ALL_TYPE_METHODS

public class AllTypeMethods extends ViewRowFactory implements ViewRow {

    public static int iMETHOD_NAME = -1;
    public static int iMETHOD_NO = -1;
    public static int iMETHOD_TYPE = -1;
    public static int iPARAMETERS = -1;
    public static int iRESULTS = -1;

    // Attributes
    public String methodName;
    public String methodNo;
    public String methodType;
    public int parameters;
    public int results;

    public AllTypeMethods(ResultSet rset) throws SQLException {
        super();
        if (iMETHOD_NAME == -1) {
            iMETHOD_NAME = rset.findColumn("METHOD_NAME");
            iMETHOD_NO = rset.findColumn("METHOD_NO");
            iMETHOD_TYPE = rset.findColumn("METHOD_TYPE");
            iPARAMETERS = rset.findColumn("PARAMETERS");
            iRESULTS = rset.findColumn("RESULTS");

        }
        methodName = rset.getString(iMETHOD_NAME);
        methodNo = rset.getString(iMETHOD_NO);
        methodType = rset.getString(iMETHOD_TYPE);
        parameters = rset.getInt(iPARAMETERS);
        results = rset.getInt(iRESULTS);
    }

    @Override
    public boolean isAllTypeMethods() {
        return true;
    }

    public String toString() {
        return methodName + "," + methodNo + "," + methodType + "," + parameters + "," + results;
    }

    public static String[] getProjectList() {
        return new String[]{"METHOD_NAME", "METHOD_NO", "METHOD_TYPE", "PARAMETERS", "RESULTS"};
    }
}
