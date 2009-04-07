/*******************************************************************************
 * Copyright (c) 1998-2009 Oracle. All rights reserved.
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

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SerializableType;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.Sortable;

@SuppressWarnings("unchecked")
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
    private static final int DEFAULT_STRING_BUFFER_LEN = 1024;
    public static final String DEFAULT_VARCHAR_LEN = "4000";
    public static final String DEFAULT_RAW_LEN = "1000";
    public static final String DEFAULT_LONG_LEN = "32767";

    private static Hashtable m_defaultTypeLen = new Hashtable();
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

    public static String printTypeWithLength(String type) {
        return printTypeWithLength(type, 0, 0, 0);
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

    /*
     * BEGIN Used by oracle/j2ee/ws/db/publish/XMethodWriter
     */
    public static String concat(ArrayList sa) {
        return concat((String[])sa.toArray(new String[0]));
    }

    public static String concat(ArrayList sa, int from, int to) {
        return concat((String[])sa.toArray(new String[0]), from, to);
    }

    public static String concat(ArrayList sa, int from) {
        return concat((String[])sa.toArray(new String[0]), from);
    }

    public static String concat(String[] sa) {
        StringBuffer sb = new StringBuffer(DEFAULT_STRING_BUFFER_LEN);
        int numStrings = sa.length;
        for (int i = 0; i < numStrings; i++) {
            if (sa[i] != null) {
                sb.append(sa[i]);
            }
        }
        return sb.toString();
    }

    public static String concat(String[] sa, int from, int to) {
        StringBuffer sb = new StringBuffer(DEFAULT_STRING_BUFFER_LEN);
        int numStrings = sa.length;
        if (to < numStrings) {
            numStrings = to;
        }
        for (int i = from; i < numStrings; i++) {
            if (sa[i] != null) {
                sb.append(sa[i]);
            }
        }
        return sb.toString();
    }

    public static String concat(String[] sa, int from) {
        StringBuffer sb = new StringBuffer(DEFAULT_STRING_BUFFER_LEN);
        int numStrings = sa.length;
        for (int i = from; i < numStrings; i++) {
            if (sa[i] != null) {
                sb.append(sa[i]);
            }
        }
        return sb.toString();
    }

    public static String concat5(String s1, String s2, String s3, String s4, String s5) {
        StringBuffer sb = new StringBuffer(DEFAULT_STRING_BUFFER_LEN);
        sb.append(s1);
        sb.append(s2);
        sb.append(s3);
        sb.append(s4);
        sb.append(s5);
        return sb.toString();
    }

    // Make sure the throw statement compatible with java.lang.Object
    private static Method[] m_objectMethods;

    // Rename exceptions to avoid conflicts with java.lang.Object methods
    public static String[] renameExceptions(String method, String[] params,
        String[] defaultExceptions) {
        String[] ex = defaultExceptions;
        try {
            if (m_objectMethods == null) {
                m_objectMethods = Object.class.getMethods();
            }
            for (int i = 0; i < m_objectMethods.length; i++) {
                Method objMethod = m_objectMethods[i];
                Class[] objParams = objMethod.getParameterTypes();
                if (objMethod.getName().equals(method) && params.length == objParams.length) {
                    boolean match = true;
                    for (int j = 0; j > params.length; j++) {
                        if (!objParams[j].getName().equals(params[j])) {
                            match = false;
                        }
                    }
                    if (match) {
                        Class[] exceptions = objMethod.getExceptionTypes();
                        ex = new String[exceptions.length];
                        for (int j = 0; j < exceptions.length; j++) {
                            ex[j] = exceptions[j].getName();
                        }
                        break;
                    }
                }
            }
        }
        catch (Throwable t) {
            /* not an java.lang.Object method */
        }
        return ex;
    }

    // Check whether the signature conflicts with java.lang.Object methods
    public static boolean offendingObject(String method, String[] params, boolean isStatic) {
        boolean offending = false;
        ;
        try {
            if (m_objectMethods == null) {
                m_objectMethods = Object.class.getMethods();
            }
            for (int i = 0; i < m_objectMethods.length; i++) {
                Method objMethod = m_objectMethods[i];
                Class[] objParams = objMethod.getParameterTypes();
                if (objMethod.getName().equals(method) && params.length == objParams.length) {
                    boolean match = true;
                    for (int j = 0; j > params.length; j++) {
                        if (!objParams[j].getName().equals(params[j])) {
                            match = false;
                        }
                    }
                    if (match) {
                        int mods = objMethod.getModifiers();
                        if (isStatic && !Modifier.isStatic(mods)) {
                            offending = true;
                        }
                        break;
                    }
                }
            }
        }
        catch (Throwable t) {
            /* not an java.lang.Object method */
        }
        return offending;
    }

    // Wrap a value from ResultSet.get<jdbcAccessor> call into an object
    public static String wrapJdbcColumn(String targetType, String jdbcAccessor, String expr) {
        if (targetType == null || jdbcAccessor == null) {
            return expr;
        }
        if (jdbcAccessor.equals("Int") && targetType.endsWith("Integer")) {
            return "new Integer(" + expr + ")";
        }
        else if (jdbcAccessor.equals("Short") && targetType.endsWith("Short")) {
            return "new Short(" + expr + ")";
        }
        else if (jdbcAccessor.equals("Float") && targetType.endsWith("Float")) {
            return "new Float(" + expr + ")";
        }
        else if (jdbcAccessor.equals("Double") && targetType.endsWith("Double")) {
            return "new Double(" + expr + ")";
        }
        else if (jdbcAccessor.equals("Boolean") && targetType.endsWith("Boolean")) {
            return "new Boolean(" + expr + ")";
        }
        else if (jdbcAccessor.equals("Byte") && targetType.endsWith("Byte")) {
            return "new Byte(" + expr + ")";
        }
        return expr;
    }

    /*
     * END Used by oracle/j2ee/ws/db/publish/XMethodWriter
     */

    /*
     * BEGIN Used by oracle/j2ee/ws/db/genproxy, oracle/j2ee/ws/db/javarefl and
     * oracle/j2ee/ws/db/j2j
     */

    public static String getPackage(Class c) {
        return getPackage(c.getName());
    }

    public static String getPackage(String s) {
        if (s == null) {
            return null;
        }
        int pos = s.lastIndexOf(".");
        if (pos < 0) {
            pos = s.lastIndexOf("/");
        }
        return (pos < 0 ? "" : s.substring(0, pos));
    }

    public static String getPackage(String pkg, String s) {
        if (pkg != null && !pkg.equals("")) {
            return pkg;
        }
        return getPackage(s);
    }

    public static String getClassName(Class c) {
        return getClassName(c.getName());
    }

    public static String getClassName(String s) {
        int pos = s.lastIndexOf(".");
        if (pos < 0) {
            pos = s.lastIndexOf("/");
        }
        if (pos > -1) {
            return s.substring(pos + 1);
        }
        return s;
    }

    // TODO: deal correctly with inner classes
    public static String printClass(Class c) {
        if (c.isArray()) {
            return printClass(c.getComponentType()) + "[]";
        }
        else if (c == Void.class) {
            return "void";
        }
        else {
            return c.getName();
        }
    }

    // [[Ljava.lang.String; => java.lang.String[][]
    // [B => byte[]
    public static String printClass(String s) {
        if (s == null) {
            return null;
        }
        int dim = getDim(s);
        s = getBaseType(s);
        for (int i = 0; i < dim; i++) {
            s += "[]";
        }
        return s;
    }

    // java.lang.String[][] => 2
    // [[Ljava.lang.String; => 2
    public static int getDim(String s) {
        int dim = 0;
        while (s != null && s.endsWith("[]")) {
            s = "[" + s.substring(0, s.length() - "[]".length());
        }
        if (s != null) {
            dim = s.lastIndexOf("[") + 1;
        }
        return dim;
    }

    // [Ljava.lang.String; => java.lang.String
    // java.lang.String[] => java.lang.String
    public static String getBaseType(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        while (s != null && s.endsWith("[]")) {
            s = "[" + s.substring(0, s.length() - "[]".length());
        }
        int dim = s.lastIndexOf("[") + 1;
        if (dim > 0) {
            s = s.substring(dim);
        }
        if (s.charAt(s.length() - 1) == ';' && s.charAt(0) == 'L') {
            s = s.substring(1, s.length() - 1);
        }
        if (s.equals("void") || s.equals("V")) {
            s = "void";
        }
        else if (s.equals("boolean") || s.equals("Z")) {
            s = "boolean";
        }
        else if (s.equals("byte") || s.equals("B")) {
            s = "byte";
        }
        else if (s.equals("short") || s.equals("S")) {
            s = "short";
        }
        else if (s.equals("char") || s.equals("C")) {
            s = "char";
        }
        else if (s.equals("int") || s.equals("I")) {
            s = "int";
        }
        else if (s.equals("long") || s.equals("J")) {
            s = "long";
        }
        else if (s.equals("float") || s.equals("F")) {
            s = "float";
        }
        else if (s.equals("double") || s.equals("D")) {
            s = "double";
        }
        return s;
    }

    public static String getObjectWrapper(String s) {
        if (m_wrappers == null) {
            initWrapper();
        }
        return (String)m_wrappers.get(s);
    }

    public static String getObjectWrapper(Class c) {
        if (m_wrappers == null) {
            initWrapper();
        }
        return (String)m_wrappers.get(c);
    }

    private static void initWrapper() {
        m_wrappers = new Hashtable();
        m_wrappers.put("boolean", "java.lang.Boolean");
        m_wrappers.put(boolean.class, "java.lang.Boolean");
        m_wrappers.put("byte", "java.lang.Byte");
        m_wrappers.put(byte.class, "java.lang.Byte");
        m_wrappers.put("short", "java.lang.Short");
        m_wrappers.put(short.class, "java.lang.Short");
        m_wrappers.put("int", "java.lang.Integer");
        m_wrappers.put(int.class, "java.lang.Integer");
        m_wrappers.put("long", "java.lang.Long");
        m_wrappers.put(long.class, "java.lang.Long");
        m_wrappers.put("char", "java.lang.Character");
        m_wrappers.put(char.class, "java.lang.Character");
        m_wrappers.put("float", "java.lang.Float");
        m_wrappers.put(float.class, "java.lang.Float");
        m_wrappers.put("double", "java.lang.Double");
        m_wrappers.put(double.class, "java.lang.Double");
    }

    private static Hashtable m_wrappers = null;

    public static String getWrappedType(String s) {
        if (m_wrappedTypes == null) {
            initWrappedTypes();
        }
        return (String)m_wrappedTypes.get(s);
    }

    public static String getWrappedType(Class c) {
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
        m_wrappedTypes = new Hashtable();
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

    private static Hashtable m_wrappedTypes = null;

    public static boolean isPrimitive(String s) {
        if (m_primitives == null) {
            initPrimitives();
        }
        return m_primitives.get(s) != null;
    }

    public static void initPrimitives() {
        m_primitives = new Hashtable();
        m_primitives.put("boolean", "booleanValue");
        m_primitives.put(boolean.class, "booleanValue");
        m_primitives.put("byte", "byteValue");
        m_primitives.put(byte.class, "byteValue");
        m_primitives.put("short", "shortValue");
        m_primitives.put(short.class, "shortValue");
        m_primitives.put("int", "intValue");
        m_primitives.put(int.class, "intValue");
        m_primitives.put("long", "longValue");
        m_primitives.put(long.class, "longValue");
        m_primitives.put("char", "charValue");
        m_primitives.put(char.class, "charValue");
        m_primitives.put("float", "floatValue");
        m_primitives.put(float.class, "floatValue");
        m_primitives.put("double", "doubleValue");
        m_primitives.put(double.class, "doubleValue");
    }

    private static Hashtable m_primitives = null;

    public static String getPrimitiveGetter(String s) {
        if (m_getters == null) {
            initGetter();
        }
        return (String)m_getters.get(s);
    }

    public static String getPrimitiveGetter(Class c) {
        if (m_getters == null) {
            initGetter();
        }
        return (String)m_getters.get(c);
    }

    public static void initGetter() {
        m_getters = new Hashtable();
        m_getters.put("boolean", "booleanValue");
        m_getters.put(boolean.class, "booleanValue");
        m_getters.put("byte", "byteValue");
        m_getters.put(byte.class, "byteValue");
        m_getters.put("short", "shortValue");
        m_getters.put(short.class, "shortValue");
        m_getters.put("int", "intValue");
        m_getters.put(int.class, "intValue");
        m_getters.put(long.class, "longValue");
        m_getters.put("long", "longValue");
        m_getters.put(char.class, "charValue");
        m_getters.put("char", "charValue");
        m_getters.put(float.class, "floatValue");
        m_getters.put("float", "floatValue");
        m_getters.put(double.class, "doubleValue");
        m_getters.put("double", "doubleValue");
        m_getters.put("Boolean", "booleanValue");
        m_getters.put("java.lang.Boolean", "booleanValue");
        m_getters.put(Boolean.class, "booleanValue");
        m_getters.put("Byte", "byteValue");
        m_getters.put("java.lang.Byte", "byteValue");
        m_getters.put(Byte.class, "byteValue");
        m_getters.put("Short", "shortValue");
        m_getters.put("java.lang.Short", "shortValue");
        m_getters.put(Short.class, "shortValue");
        m_getters.put("Integer", "intValue");
        m_getters.put("java.lang.Integer", "intValue");
        m_getters.put(Integer.class, "intValue");
        m_getters.put(Long.class, "longValue");
        m_getters.put("Long", "longValue");
        m_getters.put("java.lang.Long", "longValue");
        m_getters.put(Character.class, "charValue");
        m_getters.put("Character", "charValue");
        m_getters.put("java.lang.Character", "charValue");
        m_getters.put(Float.class, "floatValue");
        m_getters.put("Float", "floatValue");
        m_getters.put("java.lang.Float", "floatValue");
        m_getters.put(Double.class, "doubleValue");
        m_getters.put("Double", "doubleValue");
        m_getters.put("java.lang.Double", "doubleValue");
    }

    private static Hashtable m_getters = null;

    public static String stringize(String s) {
        return "\"" + s + "\"";
    }

    public static void checkClass(String s) throws ClassNotFoundException {
        if (s == null || s.equals("void") || s.equals("boolean") || s.equals("byte")
            || s.equals("short") || s.equals("char") || s.equals("int") || s.equals("long")
            || s.equals("float") || s.equals("double")) {
            return;
        }
        else if (s.startsWith("[")) {
            checkPrimitiveClass(s.substring(1));
        }
        else if (s.endsWith("[]")) {
            checkClass(s.substring(0, s.length() - 2));
        }
        else {
            Class.forName(s);
        }
    }

    private static void checkPrimitiveClass(String s) throws ClassNotFoundException {
        if (s.startsWith("[")) {
            checkPrimitiveClass(s.substring(1));
        }
        else if (s.equals("V") || s.equals("Z") || s.equals("B") || s.equals("S") || s.equals("I")
            || s.equals("J") || s.equals("D") || s.equals("F")) {
            return;
        }
        else if (s.startsWith("L")) {
            checkClass(s.substring(1, s.length() - 1));
        }
        else {
            Class.forName(s);
        }
    }

    public static String[] getClasses(String cl) {
        if (cl == null || cl.equals("")) {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(cl, ",");
        Vector v = new Vector();
        while (st.hasMoreElements()) {
            v.addElement(st.nextToken());
        }
        String[] ret = new String[v.size()];
        v.copyInto(ret);

        return ret;
    }

    public static String[] getClasses(Class[] cl) {
        if (cl == null || cl.length == 0) {
            return new String[0];
        }
        String[] ret = new String[cl.length];
        for (int i = 0; i < cl.length; i++) {
            ret[i] = cl[i].getName();
        }

        return ret;
    }

    public static String serialize(String pre, String fromName, String toName,
        SerializableType serType, String conn) {
        return pre
            + "try\n"
            + pre
            + "{\n"
            + pre
            + " java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();\n"
            + pre
            + " java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);\n"
            + pre
            + " oos.writeObject("
            + fromName
            + ");\n"
            + pre
            + " oos.flush();\n"
            + pre
            + " bos.flush();\n"
            + pre
            + " "
            + toName
            + " = "
            + serType.getJdbcClass()
            + ".createTemporary((oracle.jdbc.OracleConnection) "
            + conn
            + ", false, "
            + serType.getJdbcClass()
            + ".DURATION_SESSION);\n"
            + pre
            + " java.io.OutputStream blobos = "
            + toName
            + ".setBinaryStream(0L);\n"
            + pre
            + " blobos.write(bos.toByteArray());\n"
            + pre
            + " blobos.flush();\n"
            + pre
            + " blobos.close();\n"
            + pre
            + " try {oos.close(); } catch (java.io.IOException _){}\n"
            + pre
            + " try {bos.close(); } catch (java.io.IOException _){}\n"
            + pre
            + "}\n"
            + pre
            + "catch (Exception io_or_cnf_e) { throw new java.sql.SQLException(io_or_cnf_e.getMessage()); }\n";
    }

    public static String deserialize(String pre, String fromName, String toName,
        SerializableType serType) {
        return pre
            + "try\n"
            + pre
            + "{\n"
            + pre
            + " java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream("
            + fromName
            + ".getBytes(1l, (int) "
            + fromName
            + ".length())));\n"
            + pre
            + " "
            + toName
            + " = ("
            + serType.getFullDeclClass()
            + ")ois.readObject();\n"
            + pre
            + " ois.close();\n"
            + pre
            + "}\n"
            + pre
            + "catch (Exception io_or_cnf_e) { throw new java.sql.SQLException(io_or_cnf_e.getMessage()); }\n";
    }

    // List files under a directory recursively
    public static File[] listFiles(String dir) {
        Vector v = new Vector();
        java.util.List dirs = new ArrayList();
        dirs.add(new File(dir));
        while (!dirs.isEmpty()) {
            File dirFile = (File)dirs.get(0);
            dirs.remove(dirFile);
            File[] files = dirFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    dirs.add(files[i]);
                }
                else {
                    v.add(files[i]);
                }
            }
        }
        File[] res = new File[v.size()];
        for (int i = 0; i < v.size(); i++) {
            res[i] = (File)v.get(i);
        }
        return res;
    }

    /*
     * END Used by oracle/j2ee/ws/db/genproxy, oracle/j2ee/ws/db/javarefl and oracle/j2ee/ws/db/j2j
     */

    /*
     * Common utilities
     */
    public static String nullify(String type) {
        if (type == null) {
            return null;
        }
        type = type.trim();
        if (type.equals("int")) {
            return "0";
        }
        if (type.equals("float")) {
            return "0";
        }
        if (type.equals("long")) {
            return "0";
        }
        if (type.equals("short")) {
            return "0";
        }
        if (type.equals("double")) {
            return "0";
        }
        if (type.equals("byte")) {
            return "0";
        }
        if (type.equals("char")) {
            return "0";
        }
        if (type.equals("boolean")) {
            return "false";
        }
        return "null";
    }

    private static Hashtable uniqueResultTypeNames = new Hashtable();

    public static String uniqueResultTypeName(String methodName, String suffix) {
        String resultTypeName = methodName + suffix;
        int count = 0;
        while (uniqueResultTypeNames.containsKey(resultTypeName)) {
            resultTypeName = methodName + (count++) + suffix;
        }
        uniqueResultTypeNames.put(resultTypeName, resultTypeName);
        return resultTypeName;
    }

    // bubble sort
    public static Object[] sort(Object[] fields) {
        ArrayList fieldList = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            boolean inserted = false;
            for (int j = 0; j < fieldList.size(); j++) {
                if (((Sortable)fields[i]).getSortingKey().compareTo(
                    ((Sortable)fieldList.get(j)).getSortingKey()) < 0) {
                    fieldList.add(j, fields[i]);
                    inserted = true;
                    break;
                }
            }
            if (!inserted)
                fieldList.add(fields[i]);
        }
        for (int i = 0; i < fields.length; i++) {
            fields[i] = (Sortable)fieldList.get(i);
        }
        return fields;
    }

    public static void mkdirForFile(String fileName) throws java.io.IOException {
        int i = -1;
        String dir = null;
        i = fileName.lastIndexOf(File.separator);
        if (i > -1 && i != 0) {
            dir = fileName.substring(0, i);
            if (!(new File(dir).exists())) {
                new File(dir).mkdirs();
            }
        }
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

    public static void initStaticVariables() {
        uniqueResultTypeNames = new Hashtable();
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

    public static String getSchemaType(String schema, String type) {
        if (schema == null || schema.equals("")) {
            return type;
        }
        else {
            return schema + "." + type;
        }
    }

    /* Check PL/SQL Reserved Words (bug 3651999) */
    public static boolean isReservedBySql(String word) {
        return m_sqlReservedMap.get(word.toUpperCase()) != null;
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
    private static HashMap m_sqlReservedMap;

    static {
        m_sqlReservedMap = new HashMap();
        for (int i = 0; i < SQL_RESERVED.length; i++) {
            m_sqlReservedMap
                .put(SQL_RESERVED[i].toUpperCase(), SQL_RESERVED[i].toUpperCase() + "_");
        }
    }

}
