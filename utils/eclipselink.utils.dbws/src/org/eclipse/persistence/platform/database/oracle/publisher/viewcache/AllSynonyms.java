package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.sql.ResultSet;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;

public class AllSynonyms extends ViewRowFactory implements ViewRow {
    // Attributes
    public String OWNER;
    public String SYNONYM_NAME;

    public static int iOWNER;
    public static int iSYNONYM_NAME;
    private static boolean m_indexed = false;

    public AllSynonyms(ResultSet rs) throws java.sql.SQLException {
        super();
        if (!m_indexed) {
            m_indexed = true;
            iSYNONYM_NAME = rs.findColumn(Util.SYNONYM_NAME);
            iOWNER = rs.findColumn(Util.OWNER);
        }
        SYNONYM_NAME = rs.getString(iSYNONYM_NAME);
        OWNER = rs.getString(iOWNER);
    }

    public String toString() {
        return OWNER + "," + SYNONYM_NAME;
    }

    public static String[] getProjectList() {
        return new String[]{"OWNER", "SYNONYM_NAME"};
    }
}
