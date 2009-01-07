package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;

// Includes all the columns in ALL_ARGUMENTS and USER_AUGUMENTS

public class AllQueueTables extends ViewRowFactory implements ViewRow, java.io.Serializable {
    // Attributes
    public String QUEUE_TABLE;
    public String OBJECT_TYPE; // DO _NOT_ REFACTOR THIS!
    public String RECIPIENTS; // DO _NOT_ REFACTOR THIS!
    public String OWNER; // DO _NOT_ REFACTOR THIS!

    public static int iQUEUE_TABLE;
    public static int iOBJECT_TYPE;
    public static int iRECIPIENTS;
    public static int iOWNER;
    private static boolean m_indexed = false;

    protected AllQueueTables() {
        super();
    }

    public AllQueueTables(ResultSet rs) throws java.sql.SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iQUEUE_TABLE = rs.findColumn("QUEUE_TABLE");
            iOBJECT_TYPE = rs.findColumn("OBJECT_TYPE");
            iRECIPIENTS = rs.findColumn("RECIPIENTS");
            iOWNER = rs.findColumn("OWNER");
        }
        QUEUE_TABLE = rs.getString(iQUEUE_TABLE);
        OBJECT_TYPE = rs.getString(iOBJECT_TYPE);
        RECIPIENTS = rs.getString(iRECIPIENTS);
        OWNER = rs.getString(iOWNER);
    }

    public String toString() {
        return QUEUE_TABLE + "," + OBJECT_TYPE + "," + RECIPIENTS + "," + OWNER;
    }

    public static String[] getProjectList() {
        return new String[]{"QUEUE_TABLE", "OBJECT_TYPE", "RECIPIENTS", "OWNER",};
    }
}
