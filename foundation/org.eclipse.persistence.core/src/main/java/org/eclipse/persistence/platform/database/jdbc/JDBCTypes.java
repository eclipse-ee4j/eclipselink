/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.platform.database.jdbc;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;

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
import static java.sql.Types.NCHAR;
import static java.sql.Types.NULL;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.NVARCHAR;
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
import static org.eclipse.persistence.internal.core.helper.CoreClassConstants.BIGDECIMAL;
import static org.eclipse.persistence.internal.core.helper.CoreClassConstants.OBJECT;
import static org.eclipse.persistence.internal.core.helper.CoreClassConstants.SQLDATE;
import static org.eclipse.persistence.internal.core.helper.CoreClassConstants.SHORT;
import static org.eclipse.persistence.internal.core.helper.CoreClassConstants.STRING;
import static org.eclipse.persistence.internal.helper.ClassConstants.Void_Class;
import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;

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
        NCHAR_TYPE(NCHAR, "NCHAR"),
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
        NVARCHAR_TYPE(NVARCHAR, "NVARCHAR") {
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
        NVARCHAR2_TYPE(NVARCHAR, "NVARCHAR2") {
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
        };

        private final int typeCode;
        private final String typeName;

        JDBCTypes(int typeCode, String typeName) {
            this.typeCode = typeCode;
            this.typeName = typeName;
        }

        @Override
        public boolean isComplexDatabaseType() {
            return false;
        }

        @Override
        public boolean isJDBCType() {
            return true;
        }

        @Override
        public int getSqlCode() {
            return typeCode;
        }

        @Override
        public int getConversionCode() {
            return getSqlCode();
        }

        @Override
        public String getTypeName() {
            return typeName;
        }

        @Override
        public int computeInIndex(PLSQLargument inArg, int newIndex,
            ListIterator<PLSQLargument> i) {
            return databaseTypeHelper.computeInIndex(inArg, newIndex);
        }

        @Override
        public int computeOutIndex(PLSQLargument outArg, int newIndex,
            ListIterator<PLSQLargument> i) {
            return databaseTypeHelper.computeOutIndex(outArg, newIndex);
        }

        @Override
        public void buildInDeclare(StringBuilder sb, PLSQLargument inArg) {
            databaseTypeHelper.declareTarget(sb, inArg, this);
            sb.append(" := :");
            sb.append(inArg.inIndex);
            sb.append(";");
            sb.append(NL);
        }

        @Override
        public void buildOutDeclare(StringBuilder sb, PLSQLargument outArg) {
            databaseTypeHelper.declareTarget(sb, outArg, this);
            sb.append(";");
            sb.append(NL);
        }

        @Override
        public void buildBeginBlock(StringBuilder sb, PLSQLargument arg, PLSQLStoredProcedureCall call) {
            // nothing to do for simple types
        }

        @Override
        public void buildOutAssignment(StringBuilder sb, PLSQLargument outArg, PLSQLStoredProcedureCall call) {
            databaseTypeHelper.buildOutAssignment(sb, outArg, call);
        }

        @Override
        public void translate(PLSQLargument arg, AbstractRecord translationRow,
                AbstractRecord copyOfTranslationRow, List<DatabaseField> copyOfTranslationFields,
                List<DatabaseField> translationRowFields, List translationRowValues,
                StoredProcedureCall call) {
            databaseTypeHelper.translate(arg, translationRow, copyOfTranslationRow,
                copyOfTranslationFields, translationRowFields, translationRowValues, call);
        }

        @Override
        public void buildOutputRow(PLSQLargument outArg, AbstractRecord outputRow,
                DatabaseRecord newOutputRow, List<DatabaseField> outputRowFields, List outputRowValues) {
            databaseTypeHelper.buildOutputRow(outArg, outputRow,
                newOutputRow, outputRowFields, outputRowValues);
        }

        @Override
        public void logParameter(StringBuilder sb, ParameterType direction, PLSQLargument arg,
                AbstractRecord translationRow, DatabasePlatform platform) {
            databaseTypeHelper.logParameter(sb, direction, arg, translationRow, platform);
        }

        public static DatabaseType getDatabaseTypeForCode(int typeCode) {

            DatabaseType databaseType = switch (typeCode) {
                case ARRAY -> ARRAY_TYPE;
                case BIGINT -> BIGINT_TYPE;
                case BINARY -> BINARY_TYPE;
                case BIT -> BIT_TYPE;
                case BLOB -> BLOB_TYPE;
                case BOOLEAN -> BOOLEAN_TYPE;
                case CHAR -> CHAR_TYPE;
                case CLOB -> CLOB_TYPE;
                case DATALINK -> DATALINK_TYPE;
                case DATE -> DATE_TYPE;
                case DECIMAL -> DECIMAL_TYPE;
                case DISTINCT -> DISTINCT_TYPE;
                case DOUBLE -> DOUBLE_TYPE;
                case FLOAT -> FLOAT_TYPE;
                case INTEGER -> INTEGER_TYPE;
                case JAVA_OBJECT -> JAVA_OBJECT_TYPE;
                case LONGVARBINARY -> LONGVARBINARY_TYPE;
                case LONGVARCHAR -> LONGVARCHAR_TYPE;
                case NCHAR -> NCHAR_TYPE;
                case NULL -> NULL_TYPE;
                case NUMERIC -> NUMERIC_TYPE;
                case NVARCHAR -> NVARCHAR_TYPE;
                case OTHER -> OTHER_TYPE;
                case REAL -> REAL_TYPE;
                case REF -> REF_TYPE;
                case SMALLINT -> SMALLINT_TYPE;
                case STRUCT -> STRUCT_TYPE;
                case TIME -> TIME_TYPE;
                case TIMESTAMP -> TIMESTAMP_TYPE;
                case TINYINT -> TINYINT_TYPE;
                case VARBINARY -> VARBINARY_TYPE;
                case VARCHAR -> VARCHAR_TYPE;
                default -> null;
            };
            return databaseType;
        }

        public static Class<?> getClassForCode(int typeCode) {
            Class<?> clz = switch (typeCode) {
                case ARRAY -> Array.class;
                case DECIMAL, BIGINT, NUMERIC -> BIGDECIMAL;
                case BLOB, BINARY, LONGVARBINARY, VARBINARY -> ClassConstants.BLOB;
                case CLOB -> ClassConstants.CLOB;
                case BOOLEAN -> CoreClassConstants.BOOLEAN;
                case DISTINCT, DATALINK, JAVA_OBJECT, OTHER, REF -> OBJECT;
                case NULL -> Void_Class;
                case DATE -> SQLDATE;
                case TIMESTAMP -> CoreClassConstants.TIMESTAMP;
                case DOUBLE -> CoreClassConstants.DOUBLE;
                case REAL, FLOAT -> CoreClassConstants.FLOAT;
                case INTEGER -> CoreClassConstants.INTEGER;
                case BIT, SMALLINT, TINYINT -> SHORT;
                case STRUCT -> Struct.class;
                case TIME -> CoreClassConstants.TIME;
                default -> STRING;
            };
            return clz;
        }
}
