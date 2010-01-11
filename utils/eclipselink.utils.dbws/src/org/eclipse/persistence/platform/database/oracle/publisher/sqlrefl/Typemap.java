/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 *     quwang - Aug 9, 2006
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

//javase imports
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.Util;

/**
 * NB - this class does <b>NOT</b> implement the {@link java.util.Map} API
 */

public class Typemap {

    public static final int UNSUPPORTED_TYPE = 0;
    public static final int BYTE_ARRAY = UNSUPPORTED_TYPE + 1;
    public static final int DOUBLE = BYTE_ARRAY + 1;
    public static final int FLOAT = DOUBLE + 1;
    public static final int INT = FLOAT + 1;
    public static final int SHORT = INT + 1;
    public static final int BOOLEAN = SHORT + 1;
    public static final int OS_BFILE = BOOLEAN + 1;
    public static final int OS_BLOB = OS_BFILE + 1;
    public static final int OS_CHAR = OS_BLOB + 1;
    public static final int OS_CLOB = OS_CHAR + 1;
    public static final int OS_DATE = OS_CLOB + 1;
    public static final int OS_NUMBER = OS_DATE + 1;
    public static final int OS_RAW = OS_NUMBER + 1;
    public static final int OS_ROWID = OS_RAW + 1;
    public static final int OS_NCHAR = OS_ROWID + 1;
    public static final int OS_NCLOB = OS_NCHAR + 1;
    public static final int OS_BINARY_FLOAT = OS_NCLOB + 1;
    public static final int OS_BINARY_DOUBLE = OS_BINARY_FLOAT + 1;
    public static final int OS_TIMESTAMP = OS_BINARY_DOUBLE + 1;
    public static final int OS_TIMESTAMPTZ = OS_TIMESTAMP + 1;
    public static final int OS_TIMESTAMPLTZ = OS_TIMESTAMPTZ + 1;
    public static final int OS_CUSTOMDATUM = OS_TIMESTAMPLTZ + 1;
    public static final int OS_NSTRING = OS_CUSTOMDATUM + 1;
    public static final int JL_DOUBLE = OS_NSTRING + 1;
    public static final int JL_FLOAT = JL_DOUBLE + 1;
    public static final int JL_INTEGER = JL_FLOAT + 1;
    public static final int JL_SHORT = JL_INTEGER + 1;
    public static final int JL_STRING = JL_SHORT + 1;
    public static final int JM_BIGDECIMAL = JL_STRING + 1;
    public static final int JS_ARRAY = JM_BIGDECIMAL + 1;
    public static final int JS_BLOB = JS_ARRAY + 1;
    public static final int JS_CLOB = JS_BLOB + 1;
    public static final int JS_REF = JS_CLOB + 1;
    public static final int JS_RESULTSET = JS_REF + 1;
    public static final int JS_SQLDATA = JS_RESULTSET + 1;
    public static final int JS_DATE = JS_SQLDATA + 1;
    public static final int JS_TIMESTAMP = JS_DATE + 1;
    public static final int OCI_BFILELOCATOR = JS_TIMESTAMP + 1;
    public static final int OCI_BLOBLOCATOR = OCI_BFILELOCATOR + 1;
    public static final int OCI_CLOBLOCATOR = OCI_BLOBLOCATOR + 1;
    public static final int OCI_DATE = OCI_CLOBLOCATOR + 1;
    public static final int OCI_DATETIME = OCI_DATE + 1;
    public static final int OCI_INTERVAL = OCI_DATETIME + 1;
    public static final int OCI_NUMBER = OCI_INTERVAL + 1;
    public static final int OCI_RAW = OCI_NUMBER + 1;
    public static final int OCI_STRING = OCI_RAW + 1;
    public static final int C_GENERATEDTYPE = OCI_STRING + 1;
    public static final int OCI_TABLE = C_GENERATEDTYPE + 1;
    public static final int OCCI_BFILE = OCI_TABLE + 1;
    public static final int OCCI_BYTES = OCCI_BFILE + 1;
    public static final int OCCI_BLOB = OCCI_BYTES + 1;
    public static final int OCCI_CLOB = OCCI_BLOB + 1;
    public static final int OCCI_DATE = OCCI_CLOB + 1;
    public static final int OCCI_TIMESTAMP = OCCI_DATE + 1;
    public static final int OCCI_INTERVALYM = OCCI_TIMESTAMP + 1;
    public static final int OCCI_INTERVALDS = OCCI_INTERVALYM + 1;
    public static final int OCCI_NUMBER = OCCI_INTERVALDS + 1;
    public static final int CPP_STRING = OCCI_NUMBER + 1;
    public static final int CPP_WSTRING = CPP_STRING + 1;
    public static final int OCCI_COLLECTION = CPP_WSTRING + 1;
    public static final int OCCI_REF = OCCI_COLLECTION + 1;
    public static final int OCCI_OBJ = OCCI_REF + 1;
    public static final int OCCI_COLLECTION_OF_REFS = OCCI_OBJ + 1;
    private static final int ORACLE_TYPES_BINARY_DOUBLE = 101;
    private static final int ORACLE_TYPES_BINARY_FLOAT = 100;
    private static final int ORACLE_TYPES_JAVA_STRUCT = 2008;
    private static final int ORACLE_TYPES_OPAQUE = 2007;
    private static final int ORACLE_TYPES_TIMESTAMPLTZ = -102;
    private static final int ORACLE_TYPES_TIMESTAMPTZ = -101;

    static final String[] NAMES = {"<unsupported type>",
        "byte[]", "double", "float", "int", "short", "boolean",
        "oracle.sql.BFILE", "oracle.sql.BLOB", "oracle.sql.CHAR", "oracle.sql.CLOB", "oracle.sql.DATE",
        "oracle.sql.NUMBER", "oracle.sql.RAW", "oracle.sql.ROWID",
        "oracle.sql.NCHAR", "oracle.sql.NCLOB",
        "oracle.sql.BINARY_FLOAT", "oracle.sql.BINARY_DOUBLE",
        "oracle.sql.TIMESTAMP", "oracle.sql.TIMESTAMPTZ", "oracle.sql.TIMESTAMPLTZ",
        null, "java.lang.String",
        "Double", "Float", "Integer", "Short", "String",
        "java.math.BigDecimal",
        "java.sql.Array", "java.sql.Blob", "java.sql.Clob", "java.sql.Ref", "java.sql.ResultSet",
        null, "java.sql.Date", "java.sql.Timestamp",
        "OCIBFileLocator *", "OCIBlobLocator *", "OCIClobLocator *", "OCIDate",
        "OCIDateTime *", "OCIInterval *",
        "OCINumber", "OCIRaw *", "OCIString *", null, null,
        "oracle::occi::Bfile", "oracle::occi::Bytes", "oracle::occi::Blob", "oracle::occi::Clob",
        "oracle::occi::Date", "oracle::occi::Timestamp", "oracle::occi::IntervalYM",
        "oracle::occi::IntervalDS", "oracle::occi::Number", "OCCI_STD_NAMESPACE::string",
        "OCCI_STD_NAMESPACE::wstring", null, null, null, null
    };
    static final String[] ACCESSOR_METHODS = {null, // "<unsupported type>",
        "Bytes", "Double", "Float", "Int", "Short", "Boolean",
        "BFILE", "BLOB", "CHAR", "CLOB", "DATE", "NUMBER", "RAW", "ROWID",
        "NCHAR", "NCLOB",
        "Object", "Object",
        "TIMESTAMP", "TIMESTAMPTZ", "TIMESTAMPLTZ",
        null, "String",
        "Double", "Float", "Int", "Short", "String",
        "BigDecimal",
        "Array", "Blob", "Clob", "Ref", "Cursor", null, "Date", "Timestamp",
        null, // "nullOCIBFileLocator *",
        null, // "OCIBlobLocator *",
        null, // "OCIClobLocator *",
        null, // "OCIDate",
        null, // "OCIDateTime *",
        null, // "OCIInterval *",
        null, // "OCINumber",
        null, // "OCIRaw *",
        null, // "OCIString *",
        null, null,
        null, // "oracle::occi::Bfile",
        null, // "oracle::occi::Bytes",
        null, // "oracle::occi::Blob",
        null, // "oracle::occi::Clob",
        null, // "oracle::occi::Date",
        null, // "oracle::occi::Timestamp",
        null, // "oracle::occi::IntervalYM",
        null, // "oracle::occi::IntervalDS",
        null, // "oracle::occi::Number",
        null, // "OCCI_STD_NAMESPACE::string",
        null, // "OCCI_STD_NAMESPACE::wstring",
        null, null, null, null
    };

    protected String m_package;
    protected Map<String, String> m_field_map = null;
    protected int m_mapping;
    protected int arrayMap;
    protected int bfileMap;
    protected int binaryMap;
    protected int blobMap;
    protected int charMap;
    protected int ncharMap;
    protected int clobMap;
    protected int cursorMap;
    protected int dateMap;
    protected int timeMap;
    protected int timestampMap;
    protected int intervalYMMap;
    protected int intervalDSMap;
    protected int decimalMap;
    protected int doubleMap;
    protected int floatMap;
    protected int integerMap;
    protected int longVarBinaryMap;
    protected int numericMap;
    protected int realMap;
    protected int refMap;
    protected int rowidMap;
    protected int smallintMap;
    protected int structMap;
    protected int varcharMap;
    protected int tableMap;
    protected int binaryFloatMap;
    protected int binaryDoubleMap;
    protected SqlReflector m_reflector;

    public Typemap(TypeClass s, SqlReflector reflector) {
        if (s != null && (s instanceof JavaType)) {
            m_field_map = null;
            m_package = s.getNameObject().getDeclPackage();
        }
        else if (s != null) {
            SqlName sn = ((SqlType)s).getSqlName();
            m_field_map = ((SqlType)s).getAttributes();
            m_package = sn.getDeclPackage();
        }
        m_reflector = reflector;
        mapInit();
    }

    void mapInit() {
        bfileMap = OS_BFILE;
        cursorMap = JS_RESULTSET;
        rowidMap = OS_ROWID;
    }

    /**
     * Determine the programming language name for a given SQL field.
     */
    public String getMemberName(String sqlName, boolean wordBoundary, boolean onlyIfRegistered,
        Name name) {
        return null;
    }

    public String getMemberNameAsSuffix(String sqlName) {
        return null;
    }

    public String getMemberName(String sqlName, Name name) {
        return getMemberName(sqlName, false, false, name);
    }

    public String getMemberName(String sqlName) {
        return getMemberName(sqlName, false, false, null);
    }

    public String getMemberName(String sqlName, boolean wordBoundary, boolean onlyIfRegistered) {
        return getMemberName(sqlName, wordBoundary, onlyIfRegistered, null);
    }

    /**
     * Determine the programming language type for a given SQL type.
     */
    public String writeTypeName(TypeClass type) {
        return null;
    }

    public String writeTypeName(TypeClass type, boolean itfIfPossible) {
        return null;
    }

    /**
     */
    public int getMapping() {
        return m_mapping;
    }

    public int getJavaTypecode(int sqlTypecode) {
        if (!m_mapInitialized) {
            javaMapping();
            m_mapInitialized = true;
        };

        switch (sqlTypecode) {
            case OracleTypes.ARRAY:
                return arrayMap;
            case OracleTypes.BFILE:
                return bfileMap;
            case OracleTypes.BINARY:
                return binaryMap;
            case OracleTypes.BLOB:
                return blobMap;
            case OracleTypes.CHAR:
                return charMap;
            case OracleTypes.CLOB:
                return clobMap;
            case OracleTypes.CURSOR:
                return cursorMap;
            case OracleTypes.DATE:
                return dateMap;
            case OracleTypes.TIME:
                return timeMap;
            case OracleTypes.TIMESTAMP:
                return timestampMap;
            case ORACLE_TYPES_TIMESTAMPTZ:
                return (timestampMap == JS_TIMESTAMP) ? timestampMap : OS_TIMESTAMPTZ;
            case ORACLE_TYPES_TIMESTAMPLTZ:
                return OS_TIMESTAMPLTZ;
            case OracleTypes.INTERVALYM:
                return intervalYMMap;
            case OracleTypes.INTERVALDS:
                return intervalDSMap;
            case OracleTypes.DECIMAL:
                return decimalMap;
            case OracleTypes.DOUBLE:
                return doubleMap;
            case OracleTypes.FLOAT:
                return floatMap;
            case OracleTypes.INTEGER:
                return integerMap;
            case OracleTypes.LONGVARBINARY:
                return longVarBinaryMap;
            case OracleTypes.NUMERIC:
                return numericMap;
            case ORACLE_TYPES_OPAQUE:
                return OS_CUSTOMDATUM;
            case ORACLE_TYPES_JAVA_STRUCT:
                return structMap;
            case OracleTypes.REAL:
                return realMap;
            case OracleTypes.REF:
                return refMap;
            case OracleTypes.ROWID:
                return rowidMap;
            case OracleTypes.SMALLINT:
                return smallintMap;
            case OracleTypes.STRUCT:
                return structMap;
            case OracleTypes.VARCHAR:
                return varcharMap;
            case OracleTypes.TABLE:
                return tableMap;
            case SqlType.ORACLE_TYPES_NCHAR:
                return ncharMap;
            case SqlType.ORACLE_TYPES_NCLOB:
                return OS_NCLOB;
            case SqlType.ORACLE_TYPES_BOOLEAN:
                return BOOLEAN;
            case OracleTypes.PLSQL_INDEX_TABLE:
                return tableMap;
            case OracleTypes.PLSQL_NESTED_TABLE:
                return tableMap;
            case OracleTypes.PLSQL_VARRAY_TABLE:
                return tableMap;
            case ORACLE_TYPES_BINARY_FLOAT:
                return binaryFloatMap;
            case ORACLE_TYPES_BINARY_DOUBLE:
                return binaryDoubleMap;
            default:
                return UNSUPPORTED_TYPE;
        }
    }

    protected boolean m_mapInitialized = false;

    protected String writePredefinedName(int typecode) {
        return NAMES[getJavaTypecode(typecode)];
    }

    protected void javaMapping() {
        bfileMap = OS_BFILE;
        cursorMap = JS_RESULTSET;
        rowidMap = OS_ROWID;
    }

    public SqlType getPlsqlTableType(SqlType elemType) {
        int typecode = getJavaTypecode(elemType.getTypecode());
        switch (typecode) {
            case DOUBLE:
                return SqlReflector.plsqlTableDouble;
            case FLOAT:
                return SqlReflector.plsqlTableFloat;
            case INT:
                return SqlReflector.plsqlTableInt;
            case SHORT:
                return SqlReflector.plsqlTableShort;
            case JL_DOUBLE:
                return SqlReflector.plsqlTableJldouble;
            case JL_FLOAT:
                return SqlReflector.plsqlTableJlfloat;
            case JL_INTEGER:
                return SqlReflector.plsqlTableJlinteger;
            case JL_SHORT:
                return SqlReflector.plsqlTableJlshort;
            case JM_BIGDECIMAL:
                return SqlReflector.plsqlTableJmbigdecimal;
            case JL_STRING:
                return SqlReflector.plsqlTableString;
            default:
                return null;
        }
    }

    // Comparable to oracle.sqlj.codegen.engine.CGUnparser#accessorMethod
    public String accessorMethod(TypeClass type) {
        if ("boolean".equals(writeTypeName(type)) || "Boolean".equals(writeTypeName(type))
            || "java.lang.Boolean".equals(writeTypeName(type))) {
            return "Boolean";
        }
        else if (type instanceof PlsqlIndexTableType) {
            return "PlsqlIndexTable";
        }
        int typecode = getJavaTypecode(type.getTypecode());
        String acc = null;
        if (typecode != UNSUPPORTED_TYPE) {
            acc = ACCESSOR_METHODS[typecode];
        }
        if (acc != null) {
            return acc;
        }
        String className = writeTypeName(type);
        if (className != null) {
            if (Util.isWrappedType(className)) {
                className = Util.getWrappedType(className);
            }
            if (className.equals("String")) {
                return "String";
            }
            else if (className.equals("byte")) {
                return "Byte";
            }
            else if (className.equals("char")) {
                return "Byte";
            }
            else if (className.equals("float")) {
                return "Float";
            }
            else if (className.equals("int")) {
                return "Int";
            }
            else if (className.equals("boolean")) {
                return "Boolean";
            }
            else if (className.equals("long")) {
                return "Long";
            }
            else if (className.equals("short")) {
                return "Short";
            }
            else if (className.equals("double")) {
                return "Double";
            }
        }
        if (m_reflector.isUserType(type) && m_mapping == 0) {
            return "CustomDatum";
        }
        else if (isOpaque(type)) {
            return "ORAData";
        }
        return "Object";
    }

    private boolean isOpaque(TypeClass type) {
        boolean opaque = false;
        if (type instanceof SqlType) {
            SqlType sqlType = (SqlType)type;
            String name = sqlType.getSqlName().getFullTargetTypeName(Util.SCHEMA_ALWAYS);
            if ("SYS.XMLTYPE".equalsIgnoreCase(name)) {
                opaque = true;
            }
        }
        return opaque;
    }

}
