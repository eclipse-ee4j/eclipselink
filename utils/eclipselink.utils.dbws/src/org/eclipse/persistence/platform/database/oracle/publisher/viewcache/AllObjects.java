package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;

// Includes all the columns in ALL_TYPE_METHODS

public class AllObjects extends ViewRowFactory implements ViewRow {
    // Attributes
    public String objectName;
    public String objectType;

    public static int iOBJECT_NAME = -1;
    public static int iOBJECT_TYPE = -1;

    public AllObjects(ResultSet rset) throws java.sql.SQLException {
        super();
        if (iOBJECT_TYPE == -1) {
            iOBJECT_TYPE = rset.findColumn("OBJECT_TYPE");
            iOBJECT_NAME = rset.findColumn("OBJECT_NAME");
        }
        objectName = rset.getString(iOBJECT_NAME);
        objectType = rset.getString(iOBJECT_TYPE);
    }

    public String toString() {
        return objectName + "," + objectType;
    }

    public static String[] getProjectList() {
        return new String[]{"OBJECT_NAME", "OBJECT_TYPE"};
    }
}
