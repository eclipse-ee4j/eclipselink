/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher;

//javase imports
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Util {

    public static final int CASE_SAME = 1;
    public static final int CASE_UPPER = 2;
    public static final int CASE_LOWER = 3;
    public static final int CASE_MIXED = 4;
    public static final int CASE_OPPOSITE = 5;

    public static final String ALL_OBJECTS = "ALL_OBJECTS";
    public static final String ALL_ARGUMENTS = "ALL_ARGUMENTS";
    public static final String USER_ARGUMENTS = "USER_ARGUMENTS";

    public static final String ALL_TYPES = "ALL_TYPES";
    public static final String ALL_TYPE_ATTRS = "ALL_TYPE_ATTRS";
    public static final String ALL_COLL_TYPES = "ALL_COLL_TYPES";

    public static final int SCHEMA_ALWAYS = 1;
    public static final int SCHEMA_IF_NEEDED = 2;
    public static final int SCHEMA_FROM_INTYPE = 3;
    public static final int SCHEMA_OMIT = 4;

    public static final String DUAL = "DUAL";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    public static final String OBJECT_NAME = "OBJECT_NAME";
    public static final String ARGUMENT_NAME = "ARGUMENT_NAME";
    public static final String DATA_LEVEL = "DATA_LEVEL";
    public static final String POSITION = "POSITION";
    public static final String SEQUENCE = "SEQUENCE";
    public static final String OWNER = "OWNER";
    public static final String OVERLOAD = "OVERLOAD";
    public static final String NOT_NULL = "NOT NULL";
    public static final String IS_NULL = "IS NULL";

    public static final int METHODS_NONE = 1;
    public static final int METHODS_NAMED = 2;
    public static final int METHODS_ALL = 4;
    public static final int METHODS_ALWAYS = 8;
    public static final int METHODS_OVERLOAD = 16;
    public static final int METHODS_UNIQUE = 32;
    public static final int METHODS_RETRY = 64;
    public static final int METHODS_PARAM_INTERFACE = 128;

    public static final int JDBC_USERTYPES = 16;
    public static final int USERTYPES_MASK = JDBC_USERTYPES;

    public static final String TOPLEVEL = "TOPLEVEL";
    public static final int IS_OBJECT = 1;
    public static final int IS_COLLECTION = 2;
    public static final int IS_TYPE = 3;
    public static final int IS_PACKAGE = 4;
    public static final int IS_TYPE_OR_PACKAGE = IS_TYPE | IS_PACKAGE;
    private static final int IS_TOPLEVEL_FUNCTION_ONLY = 8;
    public static final int IS_TOPLEVEL = IS_TOPLEVEL_FUNCTION_ONLY | IS_PACKAGE;
    private static final int IS_SQLSTATEMENT_ONLY = 16;
    public static final int IS_SQLSTATEMENTS = IS_TYPE | IS_TOPLEVEL_FUNCTION_ONLY
        | IS_SQLSTATEMENT_ONLY;

    public static final String TYPE_NAME = "TYPE_NAME";
    public static final String ATTR_NAME = "ATTR_NAME";
    public static final String ATTR_TYPE_NAME = "ATTR_TYPE_NAME";
    public static final String ELEM_TYPE_NAME = "ELEM_TYPE_NAME";

    public static final String ALL_TYPE_METHODS = "ALL_TYPE_METHODS";
    public static final String ALL_METHOD_RESULTS = "ALL_METHOD_RESULTS";
    public static final String ALL_METHOD_PARAMS = "ALL_METHOD_PARAMS";
    public static final String ALL_QUEUE_TABLES = "ALL_QUEUE_TABLES";
    public static final String ALL_SYNONYMS = "ALL_SYNONYMS";
    public static final String ALL_TAB_PRIVS = "ALL_TAB_PRIVS";
    public static final String TABLE_OWNER = "TABLE_OWNER";
    public static final String TABLE_NAME = "TABLE_NAME";
    public static final String TABLE_SCHEMA = "TABLE_SCHEMA";
    public static final String PRIVILEGE = "PRIVILEGE";
    public static final String GRANTEE = "GRANTEE";
    public static final String SYNONYM_NAME = "SYNONYM_NAME";
    public static final String SUPERTYPE_NAME = "SUPERTYPE_NAME";
    public static final int MAX_IDENTIFIER_LENGTH = 29;
    public static final String DEFAULT_VARCHAR_LEN = "4000";
    public static final String DEFAULT_RAW_LEN = "1000";
    public static final String DEFAULT_LONG_LEN = "32767";

    private static Map<String, String> m_defaultTypeLen = new HashMap<String, String>();
    static {
        m_defaultTypeLen.put("VARCHAR", DEFAULT_VARCHAR_LEN);
        m_defaultTypeLen.put("VARCHAR2", DEFAULT_VARCHAR_LEN);
        m_defaultTypeLen.put("NVARCHAR2", DEFAULT_VARCHAR_LEN);
        m_defaultTypeLen.put("RAW", DEFAULT_RAW_LEN);
        m_defaultTypeLen.put("LONG", DEFAULT_LONG_LEN);
        m_defaultTypeLen.put("LONG_CHAR", DEFAULT_LONG_LEN);
        m_defaultTypeLen.put("LONG_RAW", DEFAULT_LONG_LEN);
    }

    public static String getDefaultTypeLen(String type) {
        return (String)m_defaultTypeLen.get(type.toUpperCase().replace(' ', '_'));
    }

    public static String printTypeWithLength(String type, int length, int precision, int scale) {
        if (type == null) {
            return "<unsupported type>";
        }
        if (type.equalsIgnoreCase("NCHAR")) {
            type = "CHAR";
        }
        if (type.equalsIgnoreCase("NVARCHAR2")) {
            type = "VARCHAR2";
        }
        if (type.equalsIgnoreCase("NCLOB")) {
            type = "CLOB";
        }

        if (length != 0) {
            if (type.equals("NUMBER")) {
                if (precision != 0 && scale != 0) {
                    return type + "(" + precision + ", " + scale + ")";
                }
                else if (precision != 0) {
                    return type + "(" + precision + ")";
                }
            }
            else if (type.equals("FLOAT")) {
                if (precision != 0) {
                    return type + "(" + precision + ")";
                }
            }
            else if (type.equals("NCHAR") || type.equals("NVARCHAR2")) {
                return type + "(" + (length / 2) + ")";
            }
            else {
                return type + "(" + length + ")";
            }
        }
        if (getDefaultTypeLen(type) == null) {
            return type;
        }
        else {
            return type + "(" + getDefaultTypeLen(type) + ")";
        }
    }

    public static String getWrappedType(String s) {
        if (m_wrappedTypes == null) {
            initWrappedTypes();
        }
        return (String)m_wrappedTypes.get(s);
    }

    public static String getWrappedType(Class<?> c) {
        if (m_wrappedTypes == null) {
            initWrappedTypes();
        }
        return (String)m_wrappedTypes.get(c);
    }

    public static boolean isWrappedType(String s) {
        if (m_wrappedTypes == null) {
            initWrappedTypes();
        }
        return m_wrappedTypes.get(s) != null;
    }

    private static void initWrappedTypes() {
        m_wrappedTypes = new HashMap<Object, Object>();
        m_wrappedTypes.put("java.lang.Boolean", "boolean");
        m_wrappedTypes.put("Boolean", "boolean");
        m_wrappedTypes.put(Boolean.class, "boolean");
        m_wrappedTypes.put("java.lang.Byte", "byte");
        m_wrappedTypes.put("Byte", "byte");
        m_wrappedTypes.put(Byte.class, "byte");
        m_wrappedTypes.put("java.lang.Short", "short");
        m_wrappedTypes.put("Short", "short");
        m_wrappedTypes.put(Short.class, "short");
        m_wrappedTypes.put("java.lang.Integer", "int");
        m_wrappedTypes.put("Integer", "int");
        m_wrappedTypes.put(Integer.class, "int");
        m_wrappedTypes.put("java.lang.Long", "long");
        m_wrappedTypes.put("Long", "long");
        m_wrappedTypes.put(Long.class, "long");
        m_wrappedTypes.put("java.lang.Character", "char");
        m_wrappedTypes.put("Character", "char");
        m_wrappedTypes.put(Character.class, "char");
        m_wrappedTypes.put("java.lang.Float", "float");
        m_wrappedTypes.put("Float", "float");
        m_wrappedTypes.put(Float.class, "float");
        m_wrappedTypes.put("java.lang.Double", "double");
        m_wrappedTypes.put("Double", "double");
        m_wrappedTypes.put(Double.class, "double");
    }
    private static Map<Object, Object> m_wrappedTypes = null;

    /*
     * Common utilities
     */
    private static Map<String, String> uniqueResultTypeNames = new HashMap<String, String>();
    public static String uniqueResultTypeName(String methodName, String suffix) {
        String resultTypeName = methodName + suffix;
        int count = 0;
        while (uniqueResultTypeNames.containsKey(resultTypeName)) {
            resultTypeName = methodName + (count++) + suffix;
        }
        uniqueResultTypeNames.put(resultTypeName, resultTypeName);
        return resultTypeName;
    }

    public static String quote(String text) {
        return "\"" + escapeQuote(text) + "\"";
    }

    public static String escapeQuote(String text) {
        if (text == null) {
            return text;
        }
        String textQuoted = "";
        if (text.startsWith("\"")) {
            textQuoted += "\\\"";
        }
        StringTokenizer stn = new StringTokenizer(text, "\"");
        while (stn.hasMoreTokens()) {
            String token = stn.nextToken();
            textQuoted += token;
            if (stn.hasMoreTokens()) {
                textQuoted += "\\\"";
            }
        }
        if (text.endsWith("\"")) {
            textQuoted += "\\\"";
        }
        return textQuoted;
    }

    public static String getSchema(String schema, String type) {
        if (schema == null || schema.equals("")) {
            if (type.indexOf('.') >= 0) {
                return type.substring(0, type.indexOf('.'));
            }
        }
        return schema;
    }

    public static String getType(String schema, String type) {
        if (schema == null || schema.equals("")) {
            if (type.indexOf('.') >= 0) {
                return type.substring(type.indexOf('.') + 1);
            }
        }
        return type;
    }

    public static String unreserveSql(String word) {
        String unreserve = (String)m_sqlReservedMap.get(word.toUpperCase());
        if (unreserve == null) {
            unreserve = word;
        }
        return unreserve;
    }

    private static final String[] SQL_RESERVED = new String[]{"ALL", "ALTER", "AND", "ANY",
        "ARRAY", "AS", "ASC", "AT", "AUTHID", "AVG", "BEGIN", "BETWEEN", "BINARY_INTEGER", "BODY",
        "BOOLEAN", "BULK", "BY", "CASE", "CHAR", "CHAR_BASE", "CHECK", "CLOSE", "CLUSTER",
        "COALESCE", "COLLECT", "COMMENT", "COMMIT", "COMPRESS", "CONNECT", "CONSTANT", "CREATE",
        "CURRENT", "CURRVAL", "CURSOR", "DATE", "DAY", "DECLARE", "DECIMAL", "DEFAULT", "DELETE",
        "DESC", "DISTINCT", "DO", "DROP", "ELSE", "ELSIF", "END", "EXCEPTION", "EXCLUSIVE",
        "EXECUTE", "EXISTS", "EXIT", "EXTENDS", "EXTRACT", "FALSE", "FETCH", "FLOAT", "FOR",
        "FORALL", "FROM", "FUNCTION", "GOTO", "GROUP", "HAVING", "HEAP", "HOUR", "IF", "IMMEDIATE",
        "IN", "INDEX", "INDICATOR", "INSERT", "INTEGER", "INTERFACE", "INTERSECT", "INTERVAL",
        "INTO", "IS", "ISOLATION", "JAVA", "LEVEL", "LIKE", "LIMITED", "LOCK", "LONG", "LOOP",
        "MAX", "MIN", "MINUS", "MINUTE", "MLSLABEL", "MOD", "MODE", "MONTH", "NATURAL", "NATURALN",
        "NEW", "NEXTVAL", "NOCOPY", "NOT", "NOWAIT", "NULL", "NULLIF", "NUMBER", "NUMBER_BASE",
        "OCIROWID", "OF", "ON", "OPAQUE", "OPEN", "OPERATOR", "OPTION", "OR", "ORDER",
        "ORGANIZATION", "OTHERS", "OUT", "PACKAGE", "PARTITION", "PCTFREE", "PLS_INTEGER",
        "POSITIVE", "POSITIVEN", "PRAGMA", "PRIOR", "PRIVATE", "PROCEDURE", "PUBLIC", "RAISE",
        "RANGE", "RAW", "REAL", "RECORD", "REF", "RELEASE", "RETURN", "REVERSE", "ROLLBACK", "ROW",
        "ROWID", "ROWNUM", "ROWTYPE", "SAVEPOINT", "SECOND", "SELECT", "SEPARATE", "SET", "SHARE",
        "SMALLINT", "SPACE", "SQL", "SQLCODE", "SQLERRM", "START", "STDDEV", "SUBTYPE",
        "SUCCESSFUL", "SUM", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TIME", "TIMESTAMP",
        "TIMEZONE_REGION", "TIMEZONE_ABBR", "TIMEZONE_MINUTE", "TIMEZONE_HOUR", "TO", "TRIGGER",
        "TRUE", "TYPE", "UID", "UNION", "UNIQUE", "UPDATE", "USE", "USER", "VALIDATE", "VALUES",
        "VARCHAR", "VARCHAR2", "VARIANCE", "VIEW", "WHEN", "WHENEVER", "WHERE", "WHILE", "WITH",
        "WORK", "WRITE", "YEAR", "ZONE"};
    private static Map<String, String> m_sqlReservedMap;
    static {
        m_sqlReservedMap = new HashMap<String, String>();
        for (int i = 0; i < SQL_RESERVED.length; i++) {
            m_sqlReservedMap
                .put(SQL_RESERVED[i].toUpperCase(), SQL_RESERVED[i].toUpperCase() + "_");
        }
    }

}