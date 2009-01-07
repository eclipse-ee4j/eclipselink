package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

public class SqlArrayType extends SqlCollectionType {

    public SqlArrayType(SqlName sqlName, boolean generateMe, SqlType parentType,
        SqlReflectorImpl reflector) {
        super(sqlName, OracleTypes.ARRAY, generateMe, parentType, reflector);
    }

    /**
     * Determines if this Type represents an Array type.
     * <p/>
     */
    public boolean isArray() {
        // INFEASIBLE
        return true;
    }

}
