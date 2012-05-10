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

public class AllMethodResults extends ViewRowFactory implements ViewRow {

    public static int iRESULT_TYPE_MOD;
    public static int iRESULT_TYPE_OWNER;
    public static int iRESULT_TYPE_NAME;
    public static int iCHARACTER_SET_NAME;
    public static int iMETHOD_NAME;
    public static int iMETHOD_NO;
    private static boolean m_indexed = false;

    // Attributes
    public String resultTypeMod;
    public String resultTypeOwner;
    public String resultTypeName;
    public String characterSetName;
    public String methodName;
    public String methodNo;

    public AllMethodResults(ResultSet rs) throws SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iRESULT_TYPE_MOD = rs.findColumn("RESULT_TYPE_MOD");
            iRESULT_TYPE_OWNER = rs.findColumn("RESULT_TYPE_OWNER");
            iRESULT_TYPE_NAME = rs.findColumn("RESULT_TYPE_NAME");
            iCHARACTER_SET_NAME = rs.findColumn("CHARACTER_SET_NAME");
            iMETHOD_NAME = rs.findColumn("METHOD_NAME");
            iMETHOD_NO = rs.findColumn("METHOD_NO");
        }
        resultTypeMod = rs.getString(iRESULT_TYPE_MOD);
        resultTypeOwner = rs.getString(iRESULT_TYPE_OWNER);
        resultTypeName = rs.getString(iRESULT_TYPE_NAME);
        characterSetName = rs.getString(iCHARACTER_SET_NAME);
        methodName = rs.getString(iMETHOD_NAME);
        methodNo = rs.getString(iMETHOD_NO);
    }

    @Override
    public boolean isAllMethodResults() {
        return true;
    }

    public String toString() {
        return resultTypeMod + "," + resultTypeOwner + "," + characterSetName + "," + methodName
            + "," + methodNo;
    }

    public static String[] getProjectList() {
        return new String[]{"RESULT_TYPE_MOD", "RESULT_TYPE_OWNER", "CHARACTER_SET_NAME",
            "METHOD_NAME", "METHOD_NO"};
    }
}
