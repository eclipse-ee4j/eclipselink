package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

public class SqlOpaqueType extends SqlType {

    public SqlOpaqueType(SqlName sqlName, boolean generateMe, SqlType parentType,
        SqlReflector reflector) {
        super(sqlName, OracleTypes.OPAQUE, generateMe, parentType, reflector);
    }

    /**
     * Determines if this Type represents an Opaque type.
     * <p/>
     */
    public boolean isOpaque() {
        // INFEASIBLE
        return true;
    }

}
