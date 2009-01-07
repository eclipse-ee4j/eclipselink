package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;

public class AllMethodResults extends ViewRowFactory implements ViewRow {
    // Attributes
    public String resultTypeMod;
    public String resultTypeOwner;
    public String resultTypeName;
    public String characterSetName;
    public String methodName;
    public String methodNo;

    public static int iRESULT_TYPE_MOD;
    public static int iRESULT_TYPE_OWNER;
    public static int iRESULT_TYPE_NAME;
    public static int iCHARACTER_SET_NAME;
    public static int iMETHOD_NAME;
    public static int iMETHOD_NO;
    private static boolean m_indexed = false;

    public AllMethodResults(ResultSet rs) throws java.sql.SQLException {
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

    public String toString() {
        return resultTypeMod + "," + resultTypeOwner + "," + characterSetName + "," + methodName
            + "," + methodNo;
    }

    public static String[] getProjectList() {
        return new String[]{"RESULT_TYPE_MOD", "RESULT_TYPE_OWNER", "CHARACTER_SET_NAME",
            "METHOD_NAME", "METHOD_NO"};
    }
}
