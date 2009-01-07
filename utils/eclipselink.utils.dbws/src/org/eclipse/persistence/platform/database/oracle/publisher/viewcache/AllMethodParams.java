package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;

public class AllMethodParams extends ViewRowFactory implements ViewRow {
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

    public AllMethodParams(ResultSet rs) throws java.sql.SQLException {
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

    public String toString() {
        return paramName + "," + paramNo + "," + paramMode + "," + paramTypeMod + ","
            + paramTypeOwner + "," + characterSetName + "," + methodName + "," + methodNo;
    }

    public static String[] getProjectList() {
        return new String[]{"PARAM_NAME", "PARAM_NO", "PARAM_MODE", "PARAM_TYPE_MOD",
            "PARAM_TYPE_OWNER", "CHARACTER_SET_NAME", "METHOD_NAME", "METHOD_NO"};
    }
}
