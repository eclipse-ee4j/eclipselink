/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.platform.database.oracle;

// Javse imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.SimpleDatabaseType;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;

/**
 * <b>PUBLIC</b>: Oracle PL/SQL types
 * @author  Mike Norman - michael.norman@oracle.com
 * @since  Oracle TopLink 11.x.x
 */
public enum OraclePLSQLTypes implements SimpleDatabaseType, OraclePLSQLType {

    BinaryInteger("BINARY_INTEGER"),
    Dec("DEC") ,
    Int("INT"),
    Natural("NATURAL"),
    NaturalN("NATURALN"),
    PLSQLBoolean("BOOLEAN") {
        @Override
        public String buildTargetDeclaration(DatabaseField databaseField, Integer direction, int index) {
            StringBuilder sb = new StringBuilder(databaseField.getName());
            sb.append("_TARGET ");
            sb.append(getTypeName());
            if (direction == IN || direction == INOUT) {
                sb.append(" := SYS.SQLJUTL.INT2BOOL(");
                sb.append(":");
                sb.append(index);
                sb.append(")");
            }
            sb.append("; ");
            return sb.toString();
        }
        @Override
        public String buildOutAssignment(DatabaseField databaseField, Integer direction, int index) {
            StringBuilder sb = new StringBuilder(" :");
            sb.append(index);
            sb.append(" := SYS.SQLJUTL.BOOL2INT(");
            sb.append(databaseField.getName());
            sb.append("_TARGET);");
            return sb.toString();
        }
    },
    PLSQLInteger("PLS_INTEGER"),
    Positive("POSITIVE"),
    PositiveN("POSITIVEN"),
    SignType("SIGNTYPE"),
    ;

    private final String typeName;

    OraclePLSQLTypes(String typeName) {
        this.typeName = typeName;
    }

    public int getTypeCode() {
        return java.sql.Types.OTHER; // same as oracle.jdbc.OracleTypes.OTHER
    }

    public String getTypeName() {
        return typeName;
    }

    public String buildTargetDeclaration(DatabaseField databaseField, Integer direction, int index) {
        return databaseTypeHelper.buildTargetDeclaration(databaseField.getName(), getTypeName(),
            direction, index);
    }

    public String buildOutAssignment(DatabaseField databaseField, Integer direction, int index) {
        return databaseTypeHelper.buildOutAssignment(databaseField.getName(), direction, index);
    }

    public void setConversionType(DatabaseField databaseField) {
        databaseField.sqlType = JDBCTypes.NUMERIC_TYPE.getTypeCode(); // widest compatible type
    }

    public boolean isJDBCType() {
        return false;
    }
}
