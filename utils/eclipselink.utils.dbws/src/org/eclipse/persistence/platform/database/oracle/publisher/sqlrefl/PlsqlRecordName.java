package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;

/**
 * Created by IntelliJ IDEA. User: quwang Date: Aug 9, 2006 Time: 8:26:59 PM To change this template
 * use File | Settings | File Templates.
 */
public class PlsqlRecordName extends SqlName {

    /**
     * Create a SqlName instance for a PL/SQL type, which requires extra identifying information,
     * such the names for the package and method that mentions this PL/SQL type.
     * 
     * * @param schema
     * 
     * @param type
     * @param parentType
     *            The PL/SQL package type that references to the SqlType for which the SqlName is
     *            created for
     */
    public PlsqlRecordName(String schema, String type, boolean fromDB, int line, int col,
        String packageName, SqlType parentType, Field[] fields, SqlReflectorImpl reflector)
        throws SQLException {
        super(schema, null, fromDB, line, col, false, false, null, null, null, reflector);

        m_context = Util.getSchema(schema, type);
        if (m_context == null) {
            m_context = NO_CONTEXT;
        }
        m_sourceName = Util.getType(schema, type);

        String[] mSourceName = new String[]{m_sourceName};
        boolean[] mIsRowType = new boolean[]{m_isRowType};
        m_name = reflector.determineSqlName(packageName, mSourceName, parentType, mIsRowType,
            fields, null /* elemType */, null /* valueType */);
        m_isReused = reflector.isReused(m_name);
        m_sourceName = mSourceName[0];
        m_isRowType = mIsRowType[0];
        m_name = m_fromDB ? m_name : reflector.getViewCache().dbifyName(m_name);
    }
}
