/*
 * Copyright (c) 2013, 2026 Oracle and/or its affiliates. All rights reserved.
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
//     David McCann - 2.6.0 - July 09, 2013 - Initial Implementation
package org.eclipse.persistence.tools.metadata.generation;

import static org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes.XMLType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.tools.oracleddl.metadata.DatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.FieldType;
import org.eclipse.persistence.tools.oracleddl.metadata.NumericType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLCursorType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.SizedType;

/**
 * Utility class typically used when constructing JPA/JAXB metadata from a
 * list of meta-model database types.
 *
 */
public class Util {
    public static final String ARRAY_STR = "ARRAY";
    public static final String BIGINT_STR = "BIGINT";
    public static final String BINARY_STR = "BINARY";
    public static final String BLOB_STR = "BLOB";
    public static final String BOOLEAN_STR = "BOOLEAN";
    public static final String CHAR_STR = "CHAR";
    public static final String CLOB_STR = "CLOB";
    public static final String DATE_STR = "DATE";
    public static final String DECIMAL_STR = "DECIMAL";
    public static final String DOUBLE_STR = "DOUBLE";
    public static final String FLOAT_STR = "FLOAT";
    public static final String INTEGER_STR = "INTEGER";
    public static final String LONG_STR = "LONG";
    public static final String LONGRAW_STR = "LONG RAW";
    public static final String LONGVARBINARY_STR = "LONGVARBINARY";
    public static final String NCHAR_STR = "NCHAR";
    public static final String NCLOB_STR = "NCLOB";
    public static final String NUMBER_STR = "NUMBER";
    public static final String NUMERIC_STR = "NUMERIC";
    public static final String NVARCHAR_STR = "NVARCHAR";
    public static final String NVARCHAR2_STR = "NVARCHAR2";
    public static final String OTHER_STR = "OTHER";
    public static final String RAW_STR = "RAW";
    public static final String REAL_STR = "REAL";
    public static final String ROWID_STR = "ROWID";
    public static final String ROWTYPE_STR = "%ROWTYPE";
    public static final String SMALLINT_STR = "SMALLINT";
    public static final String STRUCT_STR = "STRUCT";
    public static final String TIME_STR = "TIME";
    public static final String TIMESTAMP_STR = "TIMESTAMP";
    public static final String TINYINT_STR = "TINYINT";
    public static final String UROWID_STR = "UROWID";
    public static final String VARBINARY_STR = "VARBINARY";
    public static final String VARCHAR_STR = "VARCHAR";
    public static final String VARCHAR2_STR = "VARCHAR2";
    public static final String BINARY_INTEGER_STR = "BINARY_INTEGER";
    public static final String PLS_INTEGER_STR = "PLS_INTEGER";
    public static final String NATURAL_STR = "NATURAL";
    public static final String POSITIVE_STR = "POSITIVE";
    public static final String SIGNTYPE_STR = "SIGNTYPE";
    public static final String BINARY_INTEGER_TYPE_STR = "BinaryInteger";
    public static final String PLS_BOOLEAN_TYPE_STR = "PLSQLBoolean";
    public static final String PLS_INTEGER_TYPE_STR = "PLSQLInteger";
    public static final String NATURAL_TYPE_STR = "Natural";
    public static final String POSITIVE_TYPE_STR = "Positive";
    public static final String SIGNTYPE_TYPE_STR = "SignType";

    public static final String SYS_XMLTYPE_STR = "SYS.XMLTYPE";
    public static final String XMLTYPE_STR = "XMLTYPE";
    public static final String _TYPE_STR = "_TYPE";

    public static final String COMMA = ",";
    public static final String SINGLE_SPACE = " ";
    public static final String COMMA_SPACE_STR = COMMA + SINGLE_SPACE;

    // for CRUD operations
    public static final String ALL_QUERYNAME = "findAll";
    public static final String PK_QUERYNAME = "findByPrimaryKey";
    public static final String CREATE_OPERATION_NAME = "create";
    public static final String UPDATE_OPERATION_NAME = "update";
    public static final String REMOVE_OPERATION_NAME = "delete";
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static final String TYPE_STR = "Type";
    public static final String SELECT_FROM_STR = "SELECT * FROM ";
    public static final String WHERE_STR = " WHERE ";
    public static final String AND_STR = " AND ";
    public static final String SET_STR = " SET ";
    public static final String VALUES_STR = " VALUES ";
    public static final String UPDATE_STR = "UPDATE ";
    public static final String INSERT_STR = "INSERT INTO ";
    public static final String DELETE_STR = "DELETE FROM ";
    public static final String EQUALS_BINDING1_STR = " = ?1";
    public static final String EQUALS_BINDING_STR = " = ?";
    public static final String QUESTION_STR = "?";


    // SQL class names
    public static final String ARRAY_CLS_STR = "java.sql.Array";
    public static final String NCLOB_CLS_STR = "java.sql.NClob";
    public static final String OPAQUE_CLS_STR = "java.sql.Struct";
    public static final String ROWID_CLS_STR = "java.sql.RowId";
    public static final String STRUCT_CLS_STR = "oracle.sql.OPAQUE";
    public static final String ORACLE_TIMESTAMP_CLS_STR = "oracle.sql.TIMESTAMP";

    // JDK class names
    static final String ARRAYLIST_CLS_STR = "java.util.ArrayList";

    // direction types
    static final String IN_STR = "IN";
    static final String INOUT_STR = "IN_OUT";
    static final String OUT_STR = "OUT";
    static final String OUT_CURSOR_STR = "OUT_CURSOR";
    static final String CURSOR_STR = "CURSOR";
    static final String RESULT_STR = "RESULT";

    // misc
    public static final String DOT = ".";
    public static final String PERCENT = "%";
    public static final String UNDERSCORE = "_";

    public static final String ITEMS_FLD_STR = "items";
    public static final String ITEMS_COL_STR = "ITEMS";

    public static final int OPAQUE = 2007;

    /**
     * Returns a unqualified entity class name based on a given table or type name.
     * The returned string will contain an upper case first char, with the
     * remaining chars in lower case format.
     */
    public static String getUnqualifiedEntityName(String tableName) {
        String first = tableName.substring(0, 1).toUpperCase();
        String rest = tableName.toLowerCase().substring(1);
        return first.concat(rest);
    }

    /**
     * Returns a entity class name based on a given table or type name. The returned
     * string will contain an upper case first char, with the remaining chars in
     * lower case format.  The packageName will be prepended to the Entity name
     * if non-null.
     */
    public static String getEntityName(String tableName, String packageName) {
        String entityName = getUnqualifiedEntityName(tableName);
        return packageName == null ? entityName : packageName + DOT + entityName;
    }

    /**
     * Return the JDBC type name for a given type String.
     */
    public static String getJDBCTypeName(String typeName) {
        // this is inefficient and should be rewritten such that checking is done once only
        return getJDBCTypeNameFromType(getJDBCTypeFromTypeName(typeName));
    }

    /**
     * Return the JDBC type (as int) for a given type name.
     */
    public static int getJDBCTypeFromTypeName(String typeName) {
        if (typeName.equals(NUMERIC_STR)) {
            return Types.NUMERIC;
        }
        if (typeName.equals(VARCHAR_STR) || typeName.equals(VARCHAR2_STR)) {
            return Types.VARCHAR;
        }
        if (typeName.equals(NVARCHAR_STR) || typeName.equals(NVARCHAR2_STR)) {
            return Types.NVARCHAR;
        }
        if (typeName.equals(DATE_STR)) {
            return Types.DATE;
        }
        if (typeName.equals(TIME_STR)) {
            return Types.TIME;
        }
        if (typeName.equals(TIMESTAMP_STR)) {
            return Types.TIMESTAMP;
        }
        if (typeName.equals(DECIMAL_STR)) {
            return Types.DECIMAL;
        }
        if (typeName.equals(INTEGER_STR)) {
            return Types.INTEGER;
        }
        if (typeName.equals(CHAR_STR)) {
            return Types.CHAR;
        }
        if (typeName.equals(NCHAR_STR)) {
            return Types.NCHAR;
        }
        if (typeName.equals(FLOAT_STR)) {
            return Types.FLOAT;
        }
        if (typeName.equals(REAL_STR)) {
            return Types.REAL;
        }
        if (typeName.equals(DOUBLE_STR)) {
            return Types.DOUBLE;
        }
        if (typeName.equals(BINARY_STR)) {
            return Types.BINARY;
        }
        if (typeName.equals(BLOB_STR)) {
            return Types.BLOB;
        }
        if (typeName.equals(CLOB_STR) ||
                 typeName.equals(LONG_STR))  {
            return Types.CLOB;
        }
        if (typeName.equals(NCLOB_STR)) {
            return Types.NCLOB;
        }
        if (typeName.equals(RAW_STR) ||
                typeName.equals(LONGRAW_STR)) {
            return Types.LONGVARBINARY;
        }
        if (typeName.equals(ROWID_STR)) {
            return Types.VARCHAR;
        }
        if (typeName.equals(UROWID_STR)) {
            return Types.VARCHAR;
        }
        if (typeName.equals(BIGINT_STR)) {
            return Types.BIGINT;
        }
        if (typeName.equals(STRUCT_STR)) {
            return Types.STRUCT;
        }
        if (typeName.equals(ARRAY_STR)) {
            return Types.ARRAY;
        }
        if (typeName.equals(ROWID_STR)) {
            return Types.ROWID;
        }
        if (typeName.equalsIgnoreCase(XMLTYPE_STR) ||
                 typeName.equalsIgnoreCase(SYS_XMLTYPE_STR)) {
            return Types.VARCHAR;
        }
        if (typeName.equals(BOOLEAN_STR)  ||
                 typeName.equals(INTEGER_STR)  ||
                 typeName.equals(SMALLINT_STR) ||
                 typeName.equals(TINYINT_STR)) {
            return Types.INTEGER;
        }
        return Types.OTHER;
    }

    /**
     * Return the JDBC type name for a given JDBC type code.
     */
    public static String getJDBCTypeNameFromType(int jdbcType) {
        String typeName = switch (jdbcType) {
            case Types.NUMERIC -> NUMERIC_STR;
            case Types.VARCHAR -> VARCHAR_STR;
            case Types.NVARCHAR -> NVARCHAR_STR;
            case Types.DECIMAL -> DECIMAL_STR;
            case Types.CHAR -> CHAR_STR;
            case Types.NCHAR -> NCHAR_STR;
            case Types.FLOAT -> FLOAT_STR;
            case Types.REAL -> REAL_STR;
            case Types.DOUBLE -> DOUBLE_STR;
            case Types.BINARY -> BINARY_STR;
            case Types.BLOB -> BLOB_STR;
            case Types.CLOB -> CLOB_STR;
            case Types.NCLOB -> NCLOB_STR;
            case Types.VARBINARY -> LONGVARBINARY_STR;
            case Types.LONGVARBINARY -> LONGVARBINARY_STR;
            case Types.DATE -> DATE_STR;
            case Types.TIME -> TIME_STR;
            case Types.TIMESTAMP -> TIMESTAMP_STR;
            case Types.BIGINT -> BIGINT_STR;
            case Types.ARRAY -> ARRAY_STR;
            case Types.STRUCT -> STRUCT_STR;
            case Types.ROWID -> ROWID_STR;
            default -> OTHER_STR;
        };
        return typeName;
    }

    /**
     * Return the Class name for a given type name using the provided DatabasePlatform.
     * Object.class.getName() will be returned if the DatabasePlatform returns null.
     */
    public static String getClassNameFromJDBCTypeName(String typeName, DatabasePlatform databasePlatform) {
        return getClassFromJDBCTypeName(typeName, databasePlatform).getName();
    }

    /**
     * Return the Class for a given type name using the provided DatabasePlatform.  Object.class will
     * be returned if the DatabasePlatform returns null.
     */
    public static Class<?> getClassFromJDBCTypeName(String typeName, DatabasePlatform databasePlatform) {
        Class<?> clz = databasePlatform.getJavaTypes().get(typeName);
        if (clz == null) {
            return CoreClassConstants.OBJECT;
        }
        return clz;
    }

    /**
     * Alter the given type name if required.
     */
    protected static String processTypeName(String typeName) {
        if (!(getJDBCTypeFromTypeName(typeName) == Types.OTHER)) {
            if (typeName.equals(XMLTYPE_STR)) {
                typeName = XMLType.name();
            } else {
                // OR Metadata doesn't handle VARCHAR2
                if (typeName.equals(VARCHAR2_STR)) {
                    typeName = VARCHAR_STR;
                }
                // for BOOLEAN we want to wrap the type in a PLSQLrecord (in ORMetadata.getDatabaseTypeEnum) to
                // force the appropriate conversion method in PLSQLStoredProcedureCall (declare block, etc)
                if (!typeName.equals(BOOLEAN_STR)) {
                    typeName = typeName.concat(_TYPE_STR);
                }
            }
        } else {
            String oPLSQLTypeName = getOraclePLSQLTypeForName(typeName);
            if (oPLSQLTypeName != null) {
                typeName = oPLSQLTypeName;
            }
        }
        return typeName;
    }

    /**
     * Returns a Java class name based on a given qualified name.  If the name has
     * a package prepended to it, the the returned string  will be  in the format
     * 'packagename.Name', otherwise the format will be 'Name'.   For  example,  given
     * the name 'test.EMPLOYEE', the  method would return  the string 'test.Employee'.
     *
     */
    public static String getGeneratedJavaClassName(String name) {
        int dotIdx = name.lastIndexOf(DOT);
        if (dotIdx == -1) {
            // no package
            return getGeneratedJavaClassName(name, null);
        }
        String packageName = name.substring(0, dotIdx).toLowerCase();
        String typeName = name.substring(dotIdx + 1);
        String first = typeName.substring(0, 1).toUpperCase();
        String rest = typeName.toLowerCase().substring(1);
        return packageName + DOT + first + rest;
    }

    /**
     * Returns a Java class name based on a given name and package.  The returned
     * string  will be  in the format  'packagename.Name'.   For  example,  given
     * the name 'EMPLOYEE'  and packageName 'TEST', the  method would return  the
     * string 'test.Employee'.
     *
     */
    public static String getGeneratedJavaClassName(String name, String packageName) {
        String first = name.substring(0, 1).toUpperCase();
        String rest = name.toLowerCase().substring(1);
        return packageName == null || packageName.isEmpty() ? first + rest : packageName.toLowerCase() + DOT + first + rest;
    }

    /**
     * Return a qualified type name for the given DatabaseType. If the type is a
     * PLSQLType or a PLSQLCursor, and there is a package name available on the
     * type's parent, a string will be returned in the format 'package.typename'.
     * Otherwise the type name will be returned.
     */
    public static String getQualifiedTypeName(DatabaseType dbType) {
        if (dbType.isPLSQLType()) {
            String packageName = ((PLSQLType) dbType).getParentType().getPackageName();
            // for %ROWTYPE we build a record with no package name - need to check for null
            return packageName != null ? packageName + DOT + dbType.getTypeName() : dbType.getTypeName();
        }

        if (dbType.isPLSQLCursorType()) {
            // handle cursor
            PLSQLCursorType cursor = ((PLSQLCursorType) dbType);
            return cursor.getParentType().getPackageName() + DOT + cursor.getCursorName();
        }

        return dbType.getTypeName();
    }

    /**
     * Return a qualified compatible type name for the given DatabaseType. If the type is a
     * PLSQLType, and there is a package name available on the type's parent, a string will
     * be returned in the format 'package_typename'. Otherwise the type name will be
     * returned. A compatible type is the JDBC type equivalent of a PL/SQL type.
     */
    public static String getQualifiedCompatibleTypeName(DatabaseType dbType) {
        String packageName = null;

        if (dbType.isPLSQLType()) {
            PLSQLType plsqlType = (PLSQLType) dbType;
            packageName = plsqlType.getParentType().getPackageName();
        }

        return packageName != null ? packageName + UNDERSCORE + dbType.getTypeName() : dbType.getTypeName();
    }


    /**
     * Indicates if a given argument type name is considered a PL/SQL scalar
     * argument, i.e. is one of BOOLEAN, BINARY_INTEGER, PLS_INTEGER, etc.
     */
    public static boolean isArgPLSQLScalar(String argTypeName) {
        return argTypeName.equals("BOOLEAN")
            || argTypeName.equals("PLS_INTEGER")
            || argTypeName.equals("BINARY_INTEGER")
            || argTypeName.equals("NATURAL")
            || argTypeName.equals("POSITIVE")
            || argTypeName.equals("SIGNTYPE");
    }

    /**
     * Return the Oracle PL/SQL name for a given PL/SQL scalar type.
     */
    public static String getOraclePLSQLTypeForName(String typeName) {
        if (typeName.equals(BINARY_INTEGER_STR))
            return BINARY_INTEGER_TYPE_STR;
        if (typeName.equals(BOOLEAN_STR))
            return PLS_BOOLEAN_TYPE_STR;
        if (typeName.equals(PLS_INTEGER_STR))
            return PLS_INTEGER_TYPE_STR;
        if (typeName.equals(NATURAL_STR))
            return NATURAL_TYPE_STR;
        if (typeName.equals(POSITIVE_STR))
            return POSITIVE_TYPE_STR;
        if (typeName.equals(SIGNTYPE_STR))
            return SIGNTYPE_TYPE_STR;
        return null;
    }

    /**
     * Return the attribute-type name for a given FieldType.
     *
     * For CHAR sized type, java.lang.Character will be returned.
     * For CHAR non-sized type, java.lang.String will be returned.
     * For non-CHAR type, the database platform will be used to get the type name.
     * If the type to be returned is oracle.sql.Timestamp, java.sql.Timestamp will be
     * returned instead (to handle conversion issue with Oracle11Platform)
     */
    protected static String getAttributeTypeNameForFieldType(FieldType fldType, DatabasePlatform dbPlatform) {
        String typeName = getJDBCTypeName(fldType.getTypeName());
        String attributeType;
        if (CHAR_STR.equalsIgnoreCase(typeName) && fldType.getEnclosedType().isSizedType()) {
            SizedType sizedType = (SizedType) fldType.getEnclosedType();
            if (sizedType.getSize() == 1) {
                attributeType = CoreClassConstants.CHAR.getName();
            } else {
                attributeType = CoreClassConstants.STRING.getName();
            }
        } else {
            attributeType = getClassNameFromJDBCTypeName(typeName.toUpperCase(), dbPlatform);
        }
        // handle issue with java.sql.Timestamp conversion and Oracle11 platform
        if (attributeType.contains(ORACLE_TIMESTAMP_CLS_STR)) {
            attributeType = ClassConstants.TIMESTAMP.getName();
        } else if (attributeType.contains(CoreClassConstants.ABYTE.getName())) {
            attributeType = CoreClassConstants.APBYTE.getName();
        }
        return attributeType;
    }

    /**
     * Return the type name to be used for a given database type.  The given
     * DatabaseType's typeName attribute is typically returned, however, in
     * some cases special handling may be required.  For example, in the
     * case of a NumericType instance, "DECIMAL" should be used for the
     * type name.
     */
    protected static String getTypeNameForDatabaseType(DatabaseType dataType) {
        String typeName = dataType.getTypeName();
        if (dataType.isNumericType()) {
            NumericType numericDataType = (NumericType)dataType;
            if (numericDataType.getScale() > 0) {
                typeName = DECIMAL_STR;
            }
        }
        return typeName;
    }

    /**
     * Convenience method that detects multiple procedures with the same procedure
     * name, and updates the overload value on the relevant ProcedureTypes
     * accordingly.
     *
     * The first ProcedureType will have an overload value of 0, the second 1,
     * and so on.
     */
    protected static void handleOverloading(List<ProcedureType> procedures) {
        Map<String, List<ProcedureType>> overloadMap = new HashMap<>();
        for (ProcedureType procedure : procedures) {
            String procedureName = procedure.getProcedureName();
            List<ProcedureType> multipleProcedures = overloadMap.get(procedureName);
            if (multipleProcedures == null) {
                multipleProcedures = new ArrayList<>();
                overloadMap.put(procedureName, multipleProcedures);
            }
            multipleProcedures.add(procedure);
        }
        for (List<ProcedureType> procs : overloadMap.values()) {
            if (procs.size() > 1) {
                for (int i = 0, len = procs.size(); i < len; i++) {
                    procs.get(i).setOverload(i);
                }
            }
        }

    }
}
