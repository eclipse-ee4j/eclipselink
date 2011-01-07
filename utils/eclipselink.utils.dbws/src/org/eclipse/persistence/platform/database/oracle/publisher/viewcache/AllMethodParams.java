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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

//javase imports
import java.sql.ResultSet;
import java.sql.SQLException;

public class AllMethodParams extends ViewRowFactory implements ViewRow {

    public static int iPARAM_NAME;
    public static int iPARAM_NO;
    public static int iPARAM_MODE;
    public static int iPARAM_TYPE_MOD;
    public static int iPARAM_TYPE_OWNER;
    public static int iPARAM_TYPE_NAME;
    public static int iCHARACTER_SET_NAME;
    public static int iMETHOD_NAME;
    public static int iMETHOD_NO;
    private static boolean m_indexed = false;

    // Attributes
    public String paramName;
    public String paramNo;
    public String paramMode;
    public String paramTypeMod;
    public String paramTypeOwner;
    public String paramTypeName;
    public String characterSetName;
    public String methodName;
    public String methodNo;

    public AllMethodParams(ResultSet rs) throws SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iPARAM_NAME = rs.findColumn("PARAM_NAME");
            iPARAM_NO = rs.findColumn("PARAM_NO");
            iPARAM_MODE = rs.findColumn("PARAM_MODE");
            iPARAM_TYPE_MOD = rs.findColumn("PARAM_TYPE_MOD");
            iPARAM_TYPE_OWNER = rs.findColumn("PARAM_TYPE_OWNER");
            iPARAM_TYPE_NAME = rs.findColumn("PARAM_TYPE_NAME");
            iCHARACTER_SET_NAME = rs.findColumn("CHARACTER_SET_NAME");
            iMETHOD_NAME = rs.findColumn("METHOD_NAME");
            iMETHOD_NO = rs.findColumn("METHOD_NO");
        }
        paramName = rs.getString(iPARAM_NAME);
        paramNo = rs.getString(iPARAM_NO);
        paramMode = rs.getString(iPARAM_MODE);
        paramTypeMod = rs.getString(iPARAM_TYPE_MOD);
        paramTypeOwner = rs.getString(iPARAM_TYPE_OWNER);
        paramTypeName = rs.getString(iPARAM_TYPE_NAME);
        characterSetName = rs.getString(iCHARACTER_SET_NAME);
        methodName = rs.getString(iMETHOD_NAME);
        methodNo = rs.getString(iMETHOD_NO);
    }

    @Override
    public boolean isAllMethodParams() {
        return true;
    }

    public String toString() {
        return paramName + "," + paramNo + "," + paramMode + "," + paramTypeMod + ","
            + paramTypeOwner + "," + characterSetName + "," + methodName + "," + methodNo;
    }

    public static String[] getProjectList() {
        return new String[]{"PARAM_NAME", "PARAM_NO", "PARAM_MODE", "PARAM_TYPE_MOD",
            "PARAM_TYPE_OWNER", "CHARACTER_SET_NAME", "METHOD_NAME", "METHOD_NO"};
    }
}
