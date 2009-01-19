package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

public class SqlSqljType extends SqlType {

    public SqlSqljType(SqlName sqlName, int kind, SqlType parentType, SqlReflector reflector) {
        super(sqlName, OracleTypes.JAVA_STRUCT, false, parentType, reflector);
        m_kind = kind;
    }

    public int getSqljKind() {
        return m_kind;
    }

    private int m_kind;
}
