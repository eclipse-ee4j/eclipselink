package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import static org.eclipse.persistence.platform.database.oracle.publisher.Util.MAX_IDENTIFIER_LENGTH;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.getDefaultTypeLen;

public class PlsqlIndexTableType extends SqlType {
    PlsqlIndexTableType(SqlName sqlName, boolean isNumeric) {
        super(sqlName, OracleTypes.PLSQL_INDEX_TABLE);
        m_maxLen = MAX_IDENTIFIER_LENGTH;
        if (isNumeric) {
            m_elemTypecode = OracleTypes.NUMERIC;
            m_maxElemLen = 0;
        }
        else {
            m_elemTypecode = OracleTypes.CHAR;
            m_maxElemLen = Integer.valueOf(getDefaultTypeLen("VARCHAR2"));

        }
    }

    PlsqlIndexTableType(SqlName sqlName, boolean isNumeric, int maxLen, int maxElemLen) {
        super(sqlName, OracleTypes.PLSQL_INDEX_TABLE);
        m_elemTypecode = isNumeric ? OracleTypes.NUMERIC : OracleTypes.CHAR;
        m_maxLen = maxLen;
        m_maxElemLen = maxElemLen;
    }

    public int getElemTypecode() {
        return m_elemTypecode;
    }

    public int getMaxLen() {
        return m_maxLen;
    }

    public int getMaxElemLen() {
        return m_maxElemLen;
    }

    private int m_elemTypecode;
    private int m_maxLen;
    private int m_maxElemLen;
}
