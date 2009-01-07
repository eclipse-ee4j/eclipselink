package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

public class SqlTableType extends SqlCollectionType {

    public SqlTableType(SqlName sqlName, boolean generateMe, SqlType parentType,
        SqlReflectorImpl reflector) {
        super(sqlName, OracleTypes.TABLE, generateMe, parentType, reflector);
    }

    /**
     * Determines if this Type represents a table type.
     * <p/>
     */
    public boolean isTable() {
        return true;
    }

}
