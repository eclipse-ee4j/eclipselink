package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;

// Includes all the columns in ALL_TYPE_METHODS

public class AllTypeMethods extends ViewRowFactory implements ViewRow {
    // Attributes
    public String methodName;
    public String methodNo;
    public String methodType;
    public int parameters;
    public int results;

    public static int iMETHOD_NAME = -1;
    public static int iMETHOD_NO = -1;
    public static int iMETHOD_TYPE = -1;
    public static int iPARAMETERS = -1;
    public static int iRESULTS = -1;

    public AllTypeMethods(ResultSet rset) throws java.sql.SQLException {
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

    public String toString() {
        return methodName + "," + methodNo + "," + methodType + "," + parameters + "," + results;
    }

    public static String[] getProjectList() {
        return new String[]{"METHOD_NAME", "METHOD_NO", "METHOD_TYPE", "PARAMETERS", "RESULTS"};
    }
}
