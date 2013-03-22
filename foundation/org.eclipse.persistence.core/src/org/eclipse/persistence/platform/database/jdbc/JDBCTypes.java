/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.platform.database.jdbc;

import java.sql.Array;
import java.sql.Struct;
import java.util.List;
import java.util.ListIterator;
import static java.lang.Integer.MIN_VALUE;
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

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import static org.eclipse.persistence.internal.helper.ClassConstants.BIGDECIMAL;
import static org.eclipse.persistence.internal.helper.ClassConstants.JavaSqlDate_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.JavaSqlTime_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.JavaSqlTimestamp_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.Object_Class;
import static org.eclipse.persistence.internal.helper.ClassConstants.SHORT;
import static org.eclipse.persistence.internal.helper.ClassConstants.STRING;
import static org.eclipse.persistence.internal.helper.ClassConstants.Void_Class;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;

/**
 * <b>PUBLIC</b>: JDBC types
 * @author  Mike Norman - michael.norman@oracle.com
 * @since  Oracle TopLink 11.x.x
 */
@SuppressWarnings("unchecked")
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
            protected void buildInitialDeclare(StringBuilder sb, PLSQLargument arg) {
                databaseTypeHelper.declareTarget(sb, arg, this);
                if (arg.precision != MIN_VALUE) {
                    sb.append("(");
                    sb.append(arg.precision);
                    if (arg.scale != MIN_VALUE) {
                        sb.append(",");
                        sb.append(arg.scale);
                    }
                    sb.append(")");
                }
            }
            @Override
            public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
                buildInitialDeclare(sb, inArg);
                sb.append(" := :");
                sb.append(inArg.inIndex);
                sb.append(";");
                sb.append(NL);
            }
            @Override
            public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
                buildInitialDeclare(sb, outArg);
                sb.append(";");
                sb.append(NL);
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
            protected void buildInitialDeclare(StringBuilder sb, PLSQLargument arg) {
                databaseTypeHelper.declareTarget(sb, arg, this);
                if (arg.length != MIN_VALUE) {
                    sb.append("(");
                    sb.append(arg.length);
                    sb.append(")");
                }
            }
            @Override
            public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
                buildInitialDeclare(sb, inArg);
                sb.append(" := :");
                sb.append(inArg.inIndex);
                sb.append(";");
                sb.append(NL);
            }
            @Override
            public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
                buildInitialDeclare(sb, outArg);
                sb.append(";");
                sb.append(NL);
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

        public boolean isJDBCType() {
            return true;
        }

        public int getSqlCode() {
            return typeCode;
        }
        
        public int getConversionCode() {
            return getSqlCode();
        }

        public String getTypeName() {
            return typeName;
        }
        
        public int computeInIndex(PLSQLargument inArg, int newIndex,
            ListIterator<PLSQLargument> i) {
            return databaseTypeHelper.computeInIndex(inArg, newIndex);
        }

        public int computeOutIndex(PLSQLargument outArg, int newIndex,
            ListIterator<PLSQLargument> i) {
            return databaseTypeHelper.computeOutIndex(outArg, newIndex);
        }

        public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
            databaseTypeHelper.declareTarget(sb, inArg, this);
            sb.append(" := :");
            sb.append(inArg.inIndex);
            sb.append(";");
            sb.append(NL);
        }

        public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
            databaseTypeHelper.declareTarget(sb, outArg, this);
            sb.append(";");
            sb.append(NL);
        }

        public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
            // nothing to do for simple types
        }

        public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
            databaseTypeHelper.buildOutAssignment(sb, outArg, call);
        }

        public void translate(PLSQLargument arg, AbstractRecord translationRow,
                AbstractRecord copyOfTranslationRow, List<DatabaseField> copyOfTranslationFields,
                List<DatabaseField> translationRowFields, List translationRowValues,
                StoredProcedureCall call) {
            databaseTypeHelper.translate(arg, translationRow, copyOfTranslationRow,
                copyOfTranslationFields, translationRowFields, translationRowValues, call);
        }

        public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
                DatabaseRecord newOutputRow, List<DatabaseField> outputRowFields, List outputRowValues) {
            databaseTypeHelper.buildOutputRow(outArg, outputRow,
                newOutputRow, outputRowFields, outputRowValues);
        }

        public void logParameter(StringBuilder sb, Integer direction, PLSQLargument arg,
                AbstractRecord translationRow, DatabasePlatform platform) {
            databaseTypeHelper.logParameter(sb, direction, arg, translationRow, platform);
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

        public static Class<?> getClassForCode(int typeCode) {
            Class<?> clz = STRING;
            switch (typeCode) {
                case ARRAY :
                    clz = Array.class;
                    break;
                case DECIMAL :
                case BIGINT :
                case NUMERIC :
                    clz = BIGDECIMAL;
                    break;
                case BLOB :
                case BINARY :
                case LONGVARBINARY :
                case VARBINARY :
                    clz = ClassConstants.BLOB;
                    break;
                case CLOB :
                    clz =  ClassConstants.CLOB;
                    break;
                case BOOLEAN :
                    clz = ClassConstants.BOOLEAN;
                    break;
                case DISTINCT :
                case DATALINK :
                case JAVA_OBJECT :
                case OTHER :
                case REF :
                    clz = Object_Class;
                    break;
                case NULL :
                    clz = Void_Class;
                    break;
                case DATE :
                    clz = JavaSqlDate_Class;
                    break;
                case TIMESTAMP :
                    clz = JavaSqlTimestamp_Class;
                    break;
                case DOUBLE :
                    clz = ClassConstants.DOUBLE;
                    break;
                case REAL :
                case FLOAT :
                    clz = ClassConstants.FLOAT;
                    break;
                case INTEGER :
                    clz = ClassConstants.INTEGER;
                    break;
                case BIT :
                case SMALLINT :
                case TINYINT :
                    clz = SHORT;
                    break;
                case STRUCT :
                    clz = Struct.class;
                    break;
                case TIME :
                    clz = JavaSqlTime_Class;
                    break;
                }
            return clz;
        }
}
