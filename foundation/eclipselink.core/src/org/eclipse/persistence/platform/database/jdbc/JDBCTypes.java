/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.platform.database.jdbc;

// Javse imports
import static java.sql.Types.ARRAY;
import static java.sql.Types.BIGINT;
import static java.sql.Types.BINARY;
import static java.sql.Types.BIT;
import static java.sql.Types.BLOB;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.CHAR;
import static java.sql.Types.CLOB;
import static java.sql.Types.DATALINK;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DISTINCT;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.JAVA_OBJECT;
import static java.sql.Types.LONGVARBINARY;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NULL;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.OTHER;
import static java.sql.Types.REAL;
import static java.sql.Types.REF;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.STRUCT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARBINARY;
import static java.sql.Types.VARCHAR;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;

/**
 * <b>PUBLIC</b>: JDBC types
 * @author  Mike Norman - michael.norman@oracle.com
 * @since  Oracle TopLink 11.x.x
 */
public enum JDBCTypes implements JDBCType {

        ARRAY_TYPE(ARRAY, "ARRAY"),
        BIGINT_TYPE(BIGINT, "BIGINT"),
        BINARY_TYPE(BINARY, "BINARY"),
        BIT_TYPE(BIT, "BIT"),
        BLOB_TYPE(BLOB, "BLOB"),
        BOOLEAN_TYPE(BOOLEAN, "BOOLEAN"),
        CHAR_TYPE(CHAR, "CHAR"),
        CLOB_TYPE(CLOB, "CLOB"),
        DATALINK_TYPE(DATALINK, "DATALINK"),
        DATE_TYPE(DATE, "DATE"),
        DECIMAL_TYPE(DECIMAL, "DECIMAL"),
        DISTINCT_TYPE(DISTINCT, "DISTINCT"),
        DOUBLE_TYPE(DOUBLE, "DOUBLE"),
        FLOAT_TYPE(FLOAT, "FLOAT"),
        INTEGER_TYPE(INTEGER, "INTEGER"),
        JAVA_OBJECT_TYPE(JAVA_OBJECT, "JAVA_OBJECT"),
        LONGVARBINARY_TYPE(LONGVARBINARY, "LONGVARBINARY"),
        LONGVARCHAR_TYPE(LONGVARCHAR, "LONGVARCHAR"),
        NULL_TYPE(NULL, "NULL"),
        NUMERIC_TYPE(NUMERIC, "NUMERIC") {
            @Override
            public String buildTargetDeclaration(DatabaseField databaseField, Integer direction,
                int index) {
                return databaseTypeHelper.buildTargetDeclaration(databaseField.getName(),
                    getTypeName(), direction, index, databaseField.getPrecision(),
                    databaseField.getScale());
            }
        },
        OTHER_TYPE(OTHER, "OTHER"),
        REAL_TYPE(REAL, "REAL"),
        REF_TYPE(REF, "REF"),
        SMALLINT_TYPE(SMALLINT, "SMALLINT"),
        STRUCT_TYPE(STRUCT, "STRUCT"),
        TIME_TYPE(TIME, "TIME"),
        TIMESTAMP_TYPE(TIMESTAMP, "TIMESTAMP"),
        TINYINT_TYPE(TINYINT, "TINYINT"),
        VARBINARY_TYPE(VARBINARY, "VARBINARY"),
        VARCHAR_TYPE(VARCHAR, "VARCHAR") {
            @Override
            public String buildTargetDeclaration(DatabaseField databaseField, Integer direction,
                int index) {
                return databaseTypeHelper.buildTargetDeclaration(databaseField.getName(),
                    getTypeName(), direction, index, databaseField.getLength());
            }
        },
        ;

        private final int typeCode;
        private final String typeName;

        JDBCTypes(int typeCode, String typeName) {
            this.typeCode = typeCode;
            this.typeName = typeName;
        }
        
        public boolean isComplexDatabaseType() {
            return false;
        }

        public int getTypeCode() {
            return typeCode;
        }

        public String getTypeName() {
            return typeName;
        }

        public String buildTargetDeclaration(DatabaseField databaseField, Integer direction,
            int index) {
            return databaseTypeHelper.buildTargetDeclaration(databaseField.getName(), getTypeName(),
                direction, index);
        }

        public String buildBeginBlock(DatabaseField databaseField, Integer direction, int i) {
            return null;
        }

        public String buildOutAssignment(DatabaseField databaseField, Integer direction, int index) {
            return databaseTypeHelper.buildOutAssignment(databaseField.getName(), direction, index);
        }

        public void setConversionType(DatabaseField databaseField) {
            databaseField.sqlType = typeCode;
        }

        public static DatabaseType getDatabaseTypeForCode(int typeCode) {

            DatabaseType databaseType = null;
            switch (typeCode) {
            case ARRAY :
                databaseType = ARRAY_TYPE;
                break;
            case BIGINT :
                databaseType = BIGINT_TYPE;
                break;
            case BINARY :
                databaseType = BINARY_TYPE;
                break;
            case BIT :
                databaseType = BIT_TYPE;
                break;
            case BLOB :
                databaseType = BLOB_TYPE;
                break;
            case BOOLEAN :
                databaseType = BOOLEAN_TYPE;
                break;
            case CHAR :
                databaseType = CHAR_TYPE;
                break;
            case CLOB :
                databaseType =  CLOB_TYPE;
                break;
            case DATALINK :
                databaseType = DATALINK_TYPE;
                break;
            case DATE :
                databaseType = DATE_TYPE;
                break;
            case DECIMAL :
                databaseType = DECIMAL_TYPE;
                break;
            case DISTINCT :
                databaseType = DISTINCT_TYPE;
                break;
            case DOUBLE :
                databaseType = DOUBLE_TYPE;
                break;
            case FLOAT :
                databaseType = FLOAT_TYPE;
                break;
            case INTEGER :
                databaseType = INTEGER_TYPE;
                break;
            case JAVA_OBJECT :
                databaseType = JAVA_OBJECT_TYPE;
                break;
            case LONGVARBINARY :
                databaseType = LONGVARBINARY_TYPE;
                break;
            case LONGVARCHAR :
                databaseType = LONGVARCHAR_TYPE;
                break;
            case NULL :
                databaseType = NULL_TYPE;
                break;
            case NUMERIC :
                databaseType = NUMERIC_TYPE;
                break;
            case OTHER :
                databaseType = OTHER_TYPE;
                break;
            case REAL :
                databaseType = REAL_TYPE;
                break;
            case REF :
                databaseType = REF_TYPE;
                break;
            case SMALLINT :
                databaseType = SMALLINT_TYPE;
                break;
            case STRUCT :
                databaseType = STRUCT_TYPE;
                break;
            case TIME :
                databaseType = TIME_TYPE;
                break;
            case TIMESTAMP :
                databaseType = TIMESTAMP_TYPE;
                break;
            case TINYINT :
                databaseType = TINYINT_TYPE;
                break;
            case VARBINARY :
                databaseType = VARBINARY_TYPE;
                break;
            case VARCHAR :
                databaseType = VARCHAR_TYPE;
                break;
            }
            return databaseType;
        }
}
