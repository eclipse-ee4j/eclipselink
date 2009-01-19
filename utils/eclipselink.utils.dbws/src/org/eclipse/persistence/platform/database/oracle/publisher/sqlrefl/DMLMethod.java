/* 1998 (c) Oracle Corporation */
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;

/**
 * A Method provides information about a single method this method executes a SQL statement
 */
public class DMLMethod extends SqlStmtMethod {

    /**
     * Construct a Method that is a DML (which returns an int)
     * <p/>
     * m_isBatched <= isBatched
     */
    public DMLMethod(String name, int modifiers, String sqlStmt, boolean isBatched,
        SqlReflector reflector) throws SQLException,
        org.eclipse.persistence.platform.database.oracle.publisher.PublisherException {
        super(name, modifiers, sqlStmt, reflector);
        m_returnType = new JavaBaseType("int", null, null, SqlReflector.INT_TYPE);
        m_isBatched = isBatched;
        if (isBatched) {
            Type[] batchParamTypes = new Type[m_paramTypes.length];
            for (int i = 0; i < m_paramTypes.length; i++) {
                batchParamTypes[i] = new JavaArrayType((SqlType)m_paramTypes[i], m_reflector, null);
            }
            m_paramTypes = batchParamTypes;
        }
    }

    // A DML that is batched
    public boolean isBatched() {
        return m_isBatched;
    }

    public static boolean canDMLBatch(String[] dmlBatchOff, Type[] params) {
        if (dmlBatchOff != null) {
            for (int i = 0; i < params.length; i++) {
                for (int j = 0; j < dmlBatchOff.length; j++) {
                    String offName = dmlBatchOff[j].toUpperCase();
                    String paramName = params[i].getName().toUpperCase();
                    if (offName.equals(paramName) || paramName.endsWith(offName)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    boolean m_isBatched; // This DML method is batched or not
}
