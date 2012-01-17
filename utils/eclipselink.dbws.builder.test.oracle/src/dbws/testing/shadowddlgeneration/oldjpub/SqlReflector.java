/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.MethodFilter;
import dbws.testing.shadowddlgeneration.oldjpub.PublisherException;
import dbws.testing.shadowddlgeneration.oldjpub.Util;
import dbws.testing.shadowddlgeneration.oldjpub.AllCollTypes;
import dbws.testing.shadowddlgeneration.oldjpub.AllTypes;
import dbws.testing.shadowddlgeneration.oldjpub.ElemInfo;
import dbws.testing.shadowddlgeneration.oldjpub.FieldInfo;
import dbws.testing.shadowddlgeneration.oldjpub.RowtypeInfo;
import dbws.testing.shadowddlgeneration.oldjpub.SingleColumnViewRow;
import dbws.testing.shadowddlgeneration.oldjpub.UserArguments;
import dbws.testing.shadowddlgeneration.oldjpub.ViewCache;
import dbws.testing.shadowddlgeneration.oldjpub.ViewCacheManager;
import dbws.testing.shadowddlgeneration.oldjpub.ViewRow;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_ARGUMENTS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_COLL_TYPES;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_OBJECTS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_TYPES;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_TYPE_ATTRS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ATTR_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ATTR_TYPE_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.DEFAULT_VARCHAR_LEN;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ELEM_TYPE_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.IS_COLLECTION;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.IS_PACKAGE;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.IS_TOPLEVEL;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.IS_TYPE;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.MAX_IDENTIFIER_LENGTH;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OBJECT_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OVERLOAD;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OWNER;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.PACKAGE_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.TOPLEVEL;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.TYPE_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.getDefaultTypeLen;

/*
 * SQL Type Reflection Facility
 */
public class SqlReflector {

    public static final String CHAR_CS = "CHAR_CS";
    public static final String NCHAR_CS = "NCHAR_CS";
    public static final String ROWTYPE = "ROWTYPE";
    public static final String ROWTYPE_PL = ROWTYPE + "_PL";
    public static final String ROWTYPE_SQL = ROWTYPE + "_SQL";
    private static final int MAGIC_NUMBER = 0;

    public static final SqlType BFILE_TYPE = new SqlType("BFILE", OracleTypes.BFILE);
    public static final SqlType BINARY_INTEGER_TYPE =
    // new SqlType("BINARY_INTEGER", OracleTypes.INTEGER);
    new SqlType(new SqlName("BINARY_INTEGER", "INTEGER"), OracleTypes.INTEGER);
    public static final SqlType BLOB_TYPE = new SqlType("BLOB", OracleTypes.BLOB);
    // public static final SqlType BOOLEAN_TYPE =
    // new SqlType("BOOLEAN", SqlType.OracleTypes_BOOLEAN);
    public static final SqlType CHAR_TYPE = new SqlType("CHAR", OracleTypes.CHAR);
    public static final SqlType CLOB_TYPE = new SqlType("CLOB", OracleTypes.CLOB);
    public static final SqlType DATE_TYPE = new SqlType("DATE", OracleTypes.DATE);
    public static final SqlType NCHAR_TYPE = new SqlType("NCHAR", SqlType.ORACLE_TYPES_NCHAR);
    public static final SqlType NCLOB_TYPE = new SqlType("NCLOB", SqlType.ORACLE_TYPES_NCLOB);
    /*
     * public static final SqlType TIME_TYPE = new SqlType("TIME", OracleTypes.TIME); public static
     * final SqlType TIME_WTZ_TYPE = new SqlType("TIME WITH TIME ZONE", OracleTypes.TIME); public
     * static final SqlType TIMETZ_TYPE = // alternate spelling new SqlType("TIME WITH TZ",
     * OracleTypes.TIME);
     */
    public static final SqlType TIMESTAMP_TYPE = new SqlType("TIMESTAMP", OracleTypes.TIMESTAMP);
    public static final SqlType TIMESTAMP_WTZ_TYPE =
        new SqlType("TIMESTAMP WITH TZ", -101 ); // ==OracleTypes.TIMESTAMPTZ
    public static final SqlType TIMESTAMPTZ_TYPE = // alternate spelling
        new SqlType("TIMESTAMP WITH TIME ZONE", -101 ); // ==OracleTypes.TIMESTAMPTZ
    public static final SqlType TIMESTAMPTZ_TYPE0 = // alternate spelling
        new SqlType("TIMESTAMPTZ", -101 ); // ==OracleTypes.TIMESTAMPTZ
    public static final SqlType TIMESTAMP_WLTZ_TYPE =
        new SqlType("TIMESTAMP WITH LOCAL TZ", -102 ); // ==OracleTypes.TIMESTAMPLTZ
    public static final SqlType TIMESTAMPLTZ_TYPE = // alternate spelling
    new SqlType("TIMESTAMP WITH LOCAL TIME ZONE", -102 ); // ==OracleTypes.TIMESTAMPLTZ
    public static final SqlType TIMESTAMPLTZ_TYPE0 = // alternate spelling
    new SqlType("TIMESTAMPLTZ", -102 ); // ==OracleTypes.TIMESTAMPLTZ
    public static final SqlType DECIMAL_TYPE = new SqlType("DECIMAL", OracleTypes.DECIMAL);
    public static final SqlType DOUBLE_PRECISION_TYPE = new SqlType("DOUBLE PRECISION",
        OracleTypes.DOUBLE);
    public static final SqlType FLOAT_TYPE = new SqlType("FLOAT", OracleTypes.FLOAT);
    public static final SqlType FLOAT38TYPE = new SqlType("FLOAT38", OracleTypes.NUMBER);
    // TBD: Using the two types, 101 (OracleTypes.BDOUBLE),
    // 100 (OracleType.BFLOAT), results in <unsupported type>,
    // e.g., for the command
    // jpub -u scott/tiger -sql=sys.xmltype:XMLType
    public static final SqlType BINARY_DOUBLE_TYPE = new SqlType("BINARY_DOUBLE", 101); // OracleTypes.BDOUBLE
    public static final SqlType BINARY_FLOAT_TYPE = new SqlType("BINARY_FLOAT", 100); // OracleTypes.BFLOAT
    // Note: not referencing OracleTypes.BDOULBE and BFLOAT is
    // to avoid incompatible with pre-10i JDBC drivers
    public static final SqlType INTEGER_TYPE = new SqlType("INTEGER", OracleTypes.INTEGER);
    public static final SqlType INT_TYPE = new SqlType("INT", OracleTypes.INTEGER);
    public static final SqlType LONG_TYPE =
    // new SqlType("LONG", OracleTypes.LONGVARBINARY);
    new SqlType(new SqlName("LONG", "VARCHAR2"), OracleTypes.VARCHAR);
    public static final SqlType LONG_RAW_TYPE =
    // new SqlType("LONG RAW", OracleTypes.RAW);
    new SqlType(new SqlName("LONG RAW", "RAW"), OracleTypes.RAW);
    public static final SqlType NUMBER_TYPE = new SqlType("NUMBER", OracleTypes.NUMBER);
    public static final SqlType NUMERIC_TYPE = new SqlType("NUMERIC", OracleTypes.NUMERIC);
    public static final SqlType NVARCHAR2_TYPE = new SqlType("NVARCHAR2",
        SqlType.ORACLE_TYPES_NCHAR);
    public static final SqlType PLS_INTEGER_TYPE =
    // new SqlType("PLS_INTEGER", OracleTypes.INTEGER);
    new SqlType(new SqlName("PLS_INTEGER", "INTEGER"), OracleTypes.INTEGER);
    // public static final SqlType PLSQL_RECORD_TYPE =
    // new SqlType("PL/SQL RECORD", OracleTypes.UNSUPPORTED);
    public static final SqlType RAW_TYPE = new SqlType("RAW", OracleTypes.RAW);
    public static final SqlType REAL_TYPE = new SqlType("REAL", OracleTypes.REAL);
    public static final SqlType REF_CURSOR_TYPE = new SqlType("REF CURSOR", OracleTypes.CURSOR);
    public static final SqlType PLSQL_REF_CURSOR_TYPE = new SqlType("PL/SQL REF CURSOR",
        OracleTypes.CURSOR);
    public static final SqlType ROWID_TYPE =
    // new SqlType("ROWID", OracleTypes.ROWID);
    new SqlType(new SqlName("ROWID", "VARCHAR2"), OracleTypes.VARCHAR);
    public static final SqlType SMALLINT_TYPE = new SqlType("SMALLINT", OracleTypes.SMALLINT);
    public static final SqlType STRING_TYPE = new SqlType("STRING", OracleTypes.VARCHAR);
    public static final SqlType UROWID_TYPE =
    // new SqlType("UROWID", OracleTypes.ROWID);
    new SqlType(new SqlName("UROWID", "VARCHAR2"), OracleTypes.VARCHAR);
    public static final SqlType VARCHAR_TYPE = new SqlType("VARCHAR", OracleTypes.VARCHAR);
    public static final SqlType VARCHAR2_TYPE = new SqlType("VARCHAR2", OracleTypes.VARCHAR);
    public static SqlType plsqlTableDouble = null;
    public static SqlType plsqlTableFloat = null;
    public static SqlType plsqlTableInt = null;
    public static SqlType plsqlTableShort = null;
    public static SqlType plsqlTableJldouble = null;
    public static SqlType plsqlTableJlfloat = null;
    public static SqlType plsqlTableJlinteger = null;
    public static SqlType plsqlTableJlshort = null;
    public static SqlType plsqlTableJmbigdecimal = null;
    public static SqlType plsqlTableString = null;
    public static final SqlType UNKNOWN_TYPE = new SqlType("<unknown type or type not found>",
        OracleTypes.UNSUPPORTED);

    protected ViewCacheManager m_viewCacheManager;
    protected ViewCache m_viewCache;
    protected Connection m_conn;
    protected Boolean m_isPre920 = null;
    protected boolean m_geq9i;
    protected boolean m_transitive = true;
    protected String m_user;
    // A hashtable holding both predefined types and user types
    // for quick looking up.
    protected Map<Name, TypeClass> m_allTypes;
    protected Map<Name, TypeClass> m_predefTypes;
    /*
     * User types to be published can be added into m_userTypes in difference phases of publishing:
     * (1) First the types specified by the -sql option in the jpub command line are added (2)
     * JavaPublisher starts to publish the types in m_userTypes (3) When publishing a type, the
     * types on which that types depend on are reflected and added to the m_userTypes (4) On
     * finishing a type, JavaPublisher publishes the next type in the m_userTypes list. The
     * publishing order seems to be breadth-first.
     */
    protected List<TypeClass> m_userTypes;
    // -overwritedbtypes=false
    protected HashSet<String> m_allTypeNames;
    protected HashSet<String> m_allGeneratedTypeNames;
    protected int m_allGeneratedTypeNamesMagicNumber;
    protected Map<String, String> m_allDefaultArgsHolderTypeNames;
    protected HashSet<String>m_isReused;
    protected boolean m_getTypeCodeWarning = false;
    protected SqlStmtType m_sqlStmtType;
    protected int m_rowtypeDistinguisher = 0;
    protected WrapperPackageMetadata m_wrapperPackageMetadata;
    protected Map<String, SqlType> m_typeMap = new HashMap<String, SqlType>();

    public SqlReflector(Connection conn, String user) {
        m_viewCacheManager = new ViewCacheManager(conn);
        m_user = user;
        reset(conn);
    }

    public void reset(Connection conn) {
        if (m_conn != null) {
            try {
                m_conn.close();
            }
            catch (SQLException e) {
                // Closing resources. OK to ignore exception.
            }
        }
        m_conn = conn;

        m_viewCache = m_viewCacheManager.get(m_user);
        if (m_viewCache == null) {
            m_viewCache = new ViewCache(m_conn, m_user);
            m_viewCacheManager.add(m_viewCache);
        }
        else {
            m_viewCache.reset(m_conn);
        }

        m_allTypes = new HashMap<Name, TypeClass>();
        m_predefTypes = new HashMap<Name, TypeClass>();
        m_userTypes = new ArrayList<TypeClass>();
        sqlTypeInit();

        m_allTypeNames = new HashSet<String>();
        m_allGeneratedTypeNames = new HashSet<String>();
        m_allGeneratedTypeNamesMagicNumber = MAGIC_NUMBER;
        m_allDefaultArgsHolderTypeNames = new HashMap<String, String>();
        m_sqlStmtType = null;

        m_isReused = new HashSet<String>();
        m_wrapperPackageMetadata = null;

        if (conn != null) {
            m_geq9i = SqlReflector.geqOracle9(conn);
        }
    }

    public void loadAllTypeNames() {
        try {
            Iterator<ViewRow> iter = m_viewCache.getRows(ALL_TYPES, new String[]{TYPE_NAME},
                new String[0], new Object[0], new String[0]);
            while (iter.hasNext()) {
                SingleColumnViewRow row = (SingleColumnViewRow)iter.next();
                m_allTypeNames.add(row.getValue());
            }
        }
        catch (Exception e) { /* Cannot access ALL_TYPES table */
            e.printStackTrace();;
        }
    }

    // Determine the SQL type name for a PL/SQL type
    public String determineSqlName(String packageName, String[] sourceName, TypeClass parentType,
        boolean[] isRowType, List<AttributeField> fields, SqlType elemType, SqlType valueType)
        throws SQLException {
        String parentName = null; // Java name for the PL/SQL package
        if (parentType != null) {
            SqlName parentSqlName = (SqlName)parentType.getNameObject();
            if (parentSqlName != null) {
                parentName = parentSqlName.getTypeName().toUpperCase();
            }
        }
        String name = sourceName[0];
        m_allGeneratedTypeNames.add(name);
        if (name.indexOf('.') >= 0 && parentName != null) {
            // Replace the package name with the correpsonding Java name
            name = parentName + "_" + name.substring(name.indexOf('.') + 1);
            m_allGeneratedTypeNames.add(parentName);
        }
        name = name.replace('.', '_').replace(' ', '_');

        boolean toBeDistinguished = false;
        if (sourceName[0].equals("PL/SQL RECORD")) { // %ROWTYPE
            isRowType[0] = true;
            sourceName[0] = ROWTYPE_PL + (m_rowtypeDistinguisher++);
            name = ROWTYPE_SQL;
            if (packageName != null && !packageName.equals("")) {
                if (parentName != null) {
                    name = parentName + "_" + name;
                }
                else {
                    name = packageName + "_" + name;
                }
            }
            toBeDistinguished = true;
        }
        return determineSqlName(name, toBeDistinguished, fields, elemType, valueType);
    }

    private String determineSqlName(String name, boolean toBeDistinguished,
        List<AttributeField> fields, SqlType elemType, SqlType valueType) throws SQLException {

        String origName = name;
        if (valueType != null) {
            String uniqueName = m_allDefaultArgsHolderTypeNames.get(origName);
            if (uniqueName != null) {
                return uniqueName;
            }
        }

        if (m_allTypeNames.contains(name)) {
            boolean match = true;
            Iterator<ViewRow> iter;
            if (fields != null) {
                HashSet<String> hs = new HashSet<String>();
                for (int i = 0; i < fields.size(); i++) {
                    AttributeField af = fields.get(i);
                    String fieldType = ((SqlName)af.getType().getNameObject())
                        .getTargetTypeName();
                    hs.add(fieldType + af.getName());
                }
                iter = m_viewCache.getRows(ALL_TYPE_ATTRS, new String[]{"CONCAT("
                    + ATTR_TYPE_NAME + "," + ATTR_NAME + ")"},
                    new String[]{TYPE_NAME}, new Object[]{name}, new String[0]);
                int count = 0;
                while (iter.hasNext()) {
                    String attr = ((SingleColumnViewRow)iter.next()).getValue();
                    if (!hs.contains(attr)) {
                        match = false;
                    }
                    count++;
                }
                match = match && (count == hs.size());
            }
            else if (elemType != null) {
                iter = m_viewCache.getRows(ALL_COLL_TYPES, new String[]{ELEM_TYPE_NAME},
                    new String[]{TYPE_NAME}, new Object[]{name}, new String[0]);
                if (iter.hasNext()) {
                    String elemTypeNameInDb = ((SingleColumnViewRow)iter.next()).getValue();
                    String elemTypeName = elemType.getSqlName().getTargetTypeName();
                    match = elemTypeNameInDb.equalsIgnoreCase(elemTypeName);
                }
                else {
                    match = false;
                }
            }
            else if (valueType != null) {
                iter = m_viewCache.getRows(ALL_TYPE_ATTRS, new String[]{ATTR_TYPE_NAME},
                    new String[]{TYPE_NAME}, new Object[]{name}, new String[0]);
                if (iter.hasNext()) {
                    String valueTypeNameInDb = ((SingleColumnViewRow)iter.next()).getValue();
                    String valueTypeName = valueType.getSqlName().getTargetTypeName();
                    match = valueTypeNameInDb.equalsIgnoreCase(valueTypeName);
                }
                else {
                    match = false;
                }
            }
            if (match) {
                m_isReused.add(name);
                return name;
            }
        }
        String uniqueName = name.toUpperCase();
        if (m_allTypeNames.contains(uniqueName)
            || (toBeDistinguished && m_allGeneratedTypeNames.contains(uniqueName))) {
            do {
                int len1 = name.length();
                int len2 = Integer.toString(m_allGeneratedTypeNamesMagicNumber++).length();
                if ((len1 + len2) > MAX_IDENTIFIER_LENGTH) {
                    name = name.substring(0, MAX_IDENTIFIER_LENGTH - len2);
                }
                uniqueName = name.toUpperCase() + m_allGeneratedTypeNamesMagicNumber;
            }
            while (m_allTypeNames.contains(uniqueName)
                || (toBeDistinguished && m_allGeneratedTypeNames.contains(uniqueName)));
        }
        m_allGeneratedTypeNames.add(uniqueName);
        if (valueType != null) {
            m_allDefaultArgsHolderTypeNames.put(origName, uniqueName);
        }

        return uniqueName;
    }

    void addAllGeneratedTypeNames(String name) {
        m_allGeneratedTypeNames.add(name.toUpperCase());
    }

    public boolean isReused(String name) {
        return m_isReused.contains(name);
    }

    /*
     * public String determineDefaultArgsHolderSqlName(String name) throws SQLException { String
     * unique = (String) m_allDefaultArgsHolderTypeNames.get(name); if (unique == null) { unique =
     * determineSqlName( name, true, null , null , name); m_allDefaultArgsHolderTypeNames.put(name,
     * unique); } return unique; }
     */
    public void setViewCachePoolCapacity(int size) {
        m_viewCacheManager.setViewCachePoolCapacity(size);
    }

    private void sqlTypeInit() {
        if (!m_allTypes.isEmpty()) {
            m_allTypes = new HashMap<Name, TypeClass>();
        }
        m_allTypes.put(BFILE_TYPE.m_name, BFILE_TYPE);
        m_allTypes.put(BINARY_INTEGER_TYPE.m_name, BINARY_INTEGER_TYPE);
        m_allTypes.put(BLOB_TYPE.m_name, BLOB_TYPE);
        // m_allTypes.put(BOOLEAN_TYPE.m_name, BOOLEAN_TYPE);
        m_allTypes.put(CHAR_TYPE.m_name, CHAR_TYPE);
        m_allTypes.put(CLOB_TYPE.m_name, CLOB_TYPE);
        m_allTypes.put(DATE_TYPE.m_name, DATE_TYPE);
        m_allTypes.put(NCHAR_TYPE.m_name, NCHAR_TYPE);
        m_allTypes.put(NCLOB_TYPE.m_name, NCLOB_TYPE);
        /*
         * m_allTypes.put(TIME_TYPE.m_name, TIME_TYPE);
         * m_allTypes.put(TIME_WTZ_TYPE.m_name,TIME_WTZ_TYPE);
         * m_allTypes.put(TIMETZ_TYPE.m_name, TIMETZ_TYPE);
         */
        m_allTypes.put(TIMESTAMP_TYPE.m_name, TIMESTAMP_TYPE);
        m_allTypes.put(TIMESTAMP_WTZ_TYPE.m_name, TIMESTAMP_WTZ_TYPE);
        m_allTypes.put(TIMESTAMPTZ_TYPE.m_name, TIMESTAMPTZ_TYPE);
        m_allTypes.put(TIMESTAMPTZ_TYPE0.m_name, TIMESTAMPTZ_TYPE0);
        m_allTypes.put(TIMESTAMP_WLTZ_TYPE.m_name, TIMESTAMP_WLTZ_TYPE);
        m_allTypes.put(TIMESTAMPLTZ_TYPE.m_name, TIMESTAMPLTZ_TYPE);
        m_allTypes.put(TIMESTAMPLTZ_TYPE0.m_name, TIMESTAMPLTZ_TYPE0);
        /*
         * m_allTypes.put(INTERVAL_YM_TYPE.m_name, INTERVAL_YM_TYPE);
         * m_allTypes.put(INTERVAL_YM_TYPE.m_name, INTERVAL_YM_TYPE);
         */
        m_allTypes.put(DECIMAL_TYPE.m_name, DECIMAL_TYPE);
        m_allTypes.put(BINARY_DOUBLE_TYPE.m_name, BINARY_DOUBLE_TYPE);
        m_allTypes.put(DOUBLE_PRECISION_TYPE.m_name, DOUBLE_PRECISION_TYPE);
        m_allTypes.put(FLOAT_TYPE.m_name, FLOAT_TYPE);
        m_allTypes.put(FLOAT38TYPE.m_name, FLOAT38TYPE);
        m_allTypes.put(BINARY_FLOAT_TYPE.m_name, BINARY_FLOAT_TYPE);
        m_allTypes.put(INTEGER_TYPE.m_name, INTEGER_TYPE);
        m_allTypes.put(INT_TYPE.m_name, INT_TYPE);
        m_allTypes.put(LONG_TYPE.m_name, LONG_TYPE);
        m_allTypes.put(LONG_RAW_TYPE.m_name, LONG_RAW_TYPE);
        m_allTypes.put(NUMBER_TYPE.m_name, NUMBER_TYPE);
        m_allTypes.put(NUMERIC_TYPE.m_name, NUMERIC_TYPE);
        m_allTypes.put(NVARCHAR2_TYPE.m_name, NVARCHAR2_TYPE);
        m_allTypes.put(PLS_INTEGER_TYPE.m_name, PLS_INTEGER_TYPE);
        m_allTypes.put(RAW_TYPE.m_name, RAW_TYPE);
        m_allTypes.put(REAL_TYPE.m_name, REAL_TYPE);
        m_allTypes.put(REF_CURSOR_TYPE.m_name, REF_CURSOR_TYPE);
        m_allTypes.put(PLSQL_REF_CURSOR_TYPE.m_name, PLSQL_REF_CURSOR_TYPE);
        m_allTypes.put(ROWID_TYPE.m_name, ROWID_TYPE);
        m_allTypes.put(SMALLINT_TYPE.m_name, SMALLINT_TYPE);
        m_allTypes.put(STRING_TYPE.m_name, STRING_TYPE);
        m_allTypes.put(UROWID_TYPE.m_name, UROWID_TYPE);
        m_allTypes.put(VARCHAR_TYPE.m_name, VARCHAR_TYPE);
        m_allTypes.put(VARCHAR2_TYPE.m_name, VARCHAR2_TYPE);

        String name;
        SqlName sqlName;

        name = "PLSQL_TABLE_DOUBLE";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "double[]", null, "double[]", null));
        plsqlTableDouble = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_FLOAT";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "float[]", null, "float[]", null));
        plsqlTableFloat = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_INT";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "int[]", null, "int[]", null));
        plsqlTableInt = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_SHORT";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "short[]", null, "short[]", null));
        plsqlTableShort = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_JLDOUBLE";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "Double[]", null, "Double[]", null));
        plsqlTableJldouble = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_JLFLOAT";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "Float[]", null, "Float[]", null));
        plsqlTableJlfloat = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_JLINTEGER";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "Integer[]", null, "Integer[]", null));
        plsqlTableJlinteger = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_JLSHORT";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "Short[]", null, "Short[]", null));
        plsqlTableJlshort = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_JLINTEGER";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "Integer[]", null, "Integer[]", null));
        plsqlTableJlinteger = new PlsqlIndexTableType(sqlName, true);

        name = "PLSQL_TABLE_JMBIGDECIMAL";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "java.math.BigDecimal[]", null,
            "java.math.BigDecimal[]", null));
        plsqlTableJmbigdecimal = new PlsqlIndexTableType(sqlName, true);
        name = "PLSQL_TABLE_STRING";
        sqlName = new SqlName(name, name);
        sqlName.setAnnotation(new JavaName("", "String[]", null, "String[]", null));
        plsqlTableString = new PlsqlIndexTableType(sqlName, false);

        m_allTypes.put(plsqlTableDouble.m_name, plsqlTableDouble);
        m_allTypes.put(plsqlTableFloat.m_name, plsqlTableFloat);
        m_allTypes.put(plsqlTableInt.m_name, plsqlTableInt);
        m_allTypes.put(plsqlTableShort.m_name, plsqlTableShort);
        m_allTypes.put(plsqlTableJldouble.m_name, plsqlTableJldouble);
        m_allTypes.put(plsqlTableJlfloat.m_name, plsqlTableJlfloat);
        m_allTypes.put(plsqlTableJlshort.m_name, plsqlTableJlshort);
        m_allTypes.put(plsqlTableJlinteger.m_name, plsqlTableJlinteger);
        m_allTypes.put(plsqlTableJmbigdecimal.m_name, plsqlTableJmbigdecimal);
        m_allTypes.put(plsqlTableString.m_name, plsqlTableString);
        String hint = "/*[32767]*/";
        plsqlTableDouble.setHint(hint);
        plsqlTableFloat.setHint(hint);
        plsqlTableInt.setHint(hint);
        plsqlTableShort.setHint(hint);
        plsqlTableJldouble.setHint(hint);
        plsqlTableJlfloat.setHint(hint);
        plsqlTableJlinteger.setHint(hint);
        plsqlTableJlshort.setHint(hint);
        plsqlTableString.setHint(hint);
        plsqlTableJmbigdecimal.setHint(hint);

        m_allTypes.put(UNKNOWN_TYPE.m_name, UNKNOWN_TYPE);
    }

    public Connection getConnection() {
        return m_conn;
    }

    public SqlType addSqlType(SqlName sqlName, int whatIsIt, boolean mustBeNew, boolean generateMe,
        SqlType parentType, String modifier) throws SQLException, PublisherException {
        return addSqlType(sqlName, whatIsIt, mustBeNew, generateMe, parentType, modifier, false,
            null);
    }

    public SqlType addSqlType(SqlName sqlName, int whatIsIt, boolean mustBeNew, boolean generateMe,
        SqlType parentType, String modifier, boolean ncharFormOfUse, MethodFilter signatureFilter)
        throws SQLException, PublisherException {
        SqlType result = null;
        try {
            if (ncharFormOfUse) {
                if (sqlName.getTypeName().equals("CHAR")) {
                    sqlName = NCHAR_TYPE.getSqlName();
                }
                else if (sqlName.getTypeName().equals("VARCHAR2")) {
                    sqlName = NVARCHAR2_TYPE.getSqlName();
                }
                else if (sqlName.getTypeName().equals("CLOB")) {
                    sqlName = NCLOB_TYPE.getSqlName();
                }
            }

            SqlType t = findType(sqlName);
            boolean predefined = false;
            if (t == null) {
                t = findPredefType(sqlName);
                predefined = true;
            }

            if (t != null) {
                result = t;
                if (mustBeNew && predefined) {
                    throw new PublisherException("duplicate type " + result.toString());
                }
                else if (mustBeNew) {
                    t.setNameObject(sqlName);
                }
                return result;
            }

            String schema = sqlName.getSchemaName();
            String type = sqlName.getTypeName();
            if (whatIsIt == IS_TOPLEVEL) {
                return new SqlToplevelType(sqlName, parentType, signatureFilter, this);
            }
            else if ((whatIsIt & IS_PACKAGE) != 0) {
                Iterator<ViewRow> rowIter = m_viewCache.getRows(ALL_OBJECTS,
                    new String[]{"'PACKAGE' AS TYPECODE"}, new String[]{OWNER, OBJECT_NAME,
                        "OBJECT_TYPE", "STATUS"}, new Object[]{schema, type, "PACKAGE", "VALID"},
                    new String[0]);

                if (rowIter.hasNext()) {
                    m_viewCache.fetch(type, signatureFilter);
                    result = new SqlPackageType(sqlName, parentType, signatureFilter, this);
                }
                if (result != null) {
                    return result;
                }
                if (whatIsIt == IS_PACKAGE) {
                    throw new PublisherException("package not found " + sqlName.toString());
                }
            }

            /* determine whether this is an opaque type */
            Iterator<ViewRow> iter = m_viewCache.getRows(ALL_TYPES, new String[0], new String[]{
                OWNER, TYPE_NAME, "PREDEFINED"}, new Object[]{schema, type, "NO"},
                new String[0]);
            if (iter.hasNext()) {
                AllTypes allTypes = (AllTypes)iter.next();
                String typeCode = allTypes.typeCode;
                byte[] typeOID = allTypes.typeOid;
                int dbTypeCode = 0;
                int kind = 0;
                if (!m_getTypeCodeWarning) {
                    try {
                        Object[] outParams = m_viewCache.getOutParameters(
                            "BEGIN sys.sqljutl.get_typecode(:1, :2, :3, :4); END;",
                            new Object[]{typeOID}, new int[]{OracleTypes.INTEGER,
                                OracleTypes.VARCHAR, OracleTypes.INTEGER});
                        if (outParams == null) {
                            throw new SQLException("no data from sqljutl.get_typecode call");
                        }
                        dbTypeCode = ((Integer)outParams[0]).intValue();
                        kind = ((Integer)outParams[2]).intValue();
                    }
                    catch (SQLException exn) {
                        String msg = exn.getMessage();
                        if (isPre920()) {
                            // an older database version will not have GET_TYPECODE
                            msg = null;
                            m_getTypeCodeWarning = true;
                        }
                        else if (msg.indexOf("PLS-00201") > 0) {
                            msg = "cannot determine type " + type;
                            m_getTypeCodeWarning = true;
                        }
                        else {
                            msg = "error determining type " + type;
                        }
                        if (msg != null) {
                            throw new PublisherException(msg);
                        }
                    }
                }

                if (dbTypeCode == SqlType.CODE_OPAQUE) {
                    result = new SqlObjectType(sqlName, OracleTypes.OPAQUE, generateMe, parentType,
                        this);
                }
                else if (dbTypeCode == SqlType.CODE_SQLJTYPE && kind != 0) {
                    result = new SqlSqljType(sqlName, kind, parentType, this);
                }
                else if (typeCode.equals("OBJECT")) {
                    result = new SqlObjectType(sqlName, generateMe, parentType, this);
                    if (SqlName.langIsOtt()) {
                        /*
                         * For C only. Maura - I'm not sure about this, but
                         * it seems that a ref typedef has to be generated
                         * for every struct. That's the purpose of the
                         * following SqlRefType
                         */
                        new SqlRefType(sqlName, result, parentType, generateMe, this);
                    }
                    if (PublisherModifier.isIncomplete(result.getModifiers())) {
                        /*
                         * give warning
                         * about incomplete
                         * type and
                         * continue
                         */
                        int line = sqlName.getLine();
                        int column = sqlName.getColumn();
                        String mesg = "incomplete type " + sqlName.toString();

                        if (line > 0 || column > 0) {
                            mesg = "" + line + "." + column + ": " + mesg;
                        }
                        System.err.println(mesg);
                    }
                }
                else if (typeCode.equals("COLLECTION")) {
                    if ((whatIsIt & IS_COLLECTION) == 0) {
                        throw new PublisherException("collection found " + sqlName.toString());
                    }
                    Iterator<ViewRow> iter3 = m_viewCache.getRows(ALL_COLL_TYPES, new String[0],
                        new String[]{OWNER, TYPE_NAME}, new Object[]{schema, type},
                        new String[0]);
                    if (iter3.hasNext()) {
                        AllCollTypes act = (AllCollTypes)iter3.next();
                        String collTypeCode = act.collType;
                        if (collTypeCode.equals("TABLE")) {
                            result = new SqlTableType(sqlName, generateMe, parentType, this);
                        }
                        else {
                            // "VARYING ARRAY"
                            result = new SqlArrayType(sqlName, generateMe, parentType, this);
                        }
                    }
                }
            }
            if (result != null) {
                return result;
            }
            else {
                // Note that this type is not supported, so we will not look
                // it up again.
                result = addPredefType(sqlName, OracleTypes.UNSUPPORTED);
                // Now add an error message
                if ((whatIsIt & IS_PACKAGE) != 0) {
                    throw new PublisherException("type not found " + sqlName.toString());
                }
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        catch (PublisherException ex) {
            System.err.println(ex.getMessage());
        }
        return result;
    }

    public SqlType addSqlUserType(String schema, String type, int whatIsIt, boolean mustBeNew,
        int line, int col, MethodFilter signatureFilter) throws SQLException, PublisherException {
        SqlName sn = new SqlName(schema, type, false, line, col, this);
        return addSqlType(sn, whatIsIt, mustBeNew, true, null, null, false, signatureFilter);
    }

    private SqlType addSqlDBType(SqlName sqlName, String modifier, boolean ncharFormOfUse,
        SqlType parentType) throws SQLException, PublisherException {

        /* constructed types are not hashed */
        if (modifier != null && modifier.equals("REF")) {
            SqlType t = addSqlType(sqlName, IS_TYPE, false, m_transitive, parentType, modifier);
            SqlType rt = new SqlRefType(sqlName, t, parentType, m_transitive, this);
            return rt;
        }
        else {
            return addSqlType(sqlName, IS_TYPE, false, m_transitive, parentType, modifier,
                ncharFormOfUse, null);
        }
    }

    // Used for adding types other than PL/SQL types
    public SqlType addSqlDBType(String schema, String type, String subtype, String modifier,
        boolean ncharFormOfUse, SqlType parentType) throws SQLException, PublisherException {
        int line = 0;
        int col = 0;

        if (parentType != null) {
            SqlName psqlname = (SqlName)parentType.getNameObject();
            line = psqlname.getLine();
            col = psqlname.getColumn();
        }
        if (subtype != null) {
            if (type != null) {
                type = type + "." + subtype;
            }
            else {
                type = subtype;
            }
            if (schema != null) {
                type = schema + "." + type;
                schema = null;
            }
        }
        SqlName sn = new SqlName(schema, type, true, line, col, this);
        sn.setAnnotation(genPattern((LangName)sn.getAnnotation(), sn.getSimpleName(), true));

        return addSqlDBType(sn, modifier, ncharFormOfUse, parentType);
    }

    /*
     * Add SQL or PL/SQL types from PL/SQL packages (1) For SQL types, delegate to addSqlDBType (2)
     * For PL/SQL, create PlsqlRecorType or PlsqlTableType
     */
    public SqlType addPlsqlDBType(String schema, String type, String subtype, String modifier,
        boolean ncharFormOfUse, String packageName, String methodName, String methodNo,
        int sequence, SqlType parentType) throws SQLException, PublisherException {
        return addPlsqlDBType(schema, type, subtype, modifier, ncharFormOfUse, packageName,
            methodName, methodNo, sequence, parentType, false);
    }

    public SqlType addPlsqlDBType(String schema, String type, String subtype, String modifier,
        boolean ncharFormOfUse, String packageName, String methodName, String methodNo,
        int sequence, SqlType parentType, boolean isGrandparent) throws SQLException,
        PublisherException {
        int line = 0;
        int col = 0;

        if (parentType != null) {
            SqlName psqlname = (SqlName)parentType.getNameObject();
            line = psqlname.getLine();
            col = psqlname.getColumn();
        }
        if (subtype != null) {
            if (type != null) {
                type = type + "." + subtype;
            }
            else {
                type = subtype;
            }
            if (schema != null) {
                type = schema + "." + type;
                schema = null;
            }
        }
        // For SQL types, delegate to addSqlDBType
        if (modifier == null
            || (!modifier.equals("PL/SQL RECORD") && !modifier.equals("PL/SQL TABLE")
                && (!modifier.equals("TABLE") || (subtype == null && type.indexOf(".") == -1)) && (!modifier
                .equals("VARRAY") || (subtype == null && type.indexOf(".") == -1)))) {
            SqlName sn = new SqlName(schema, type, true, line, col, this);
            sn.setAnnotation(genPattern((LangName)sn.getAnnotation(), sn.getSimpleName(), true));
            return addSqlDBType(sn, modifier, ncharFormOfUse, parentType);
        }

        // Start processing PL/SQL types
        // Check wether the ROWTYPE has bee published
        List<RowtypeInfo> rowtypeInfoA = null;
        if (modifier != null && type != null && modifier.equals("PL/SQL RECORD")
            && type.equals("PL/SQL RECORD")) {
            rowtypeInfoA = reflectRowtypeInfo(packageName, methodName, methodNo, sequence);
            for (int i = 0; i < m_userTypes.size(); i++) {
                boolean found = true;
                TypeClass p = m_userTypes.get(i);
                if (!(p instanceof PlsqlRecordType)) {
                    continue;
                }
                List<RowtypeInfo> rowtypeInfoB = ((PlsqlRecordType)p).getRowtypeInfo();
                if (rowtypeInfoB == null || rowtypeInfoA == null
                    || rowtypeInfoA.size() != rowtypeInfoB.size()) {
                    found = false;
                    continue;
                }
                for (int ja = 0; ja < rowtypeInfoA.size(); ja++) {
                    boolean microFound = false;
                    for (int jb = 0; jb < rowtypeInfoB.size(); jb++) {
                        if (rowtypeInfoA.get(ja).equals(rowtypeInfoB.get(jb))) {
                            microFound = true;
                            break;
                        }
                    }
                    if (!microFound) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    return (SqlType)p;
                }
            }
        }

        // Create the PL/SQL type
        SqlType result = findPredefType(schema, type);
        boolean predefined = (result != null);
        if (!predefined) {
            result = findType(schema, type);
        }
        if (result != null) {
            if (result.getTypecode() != SqlType.ORACLE_TYPES_TBD) {
                return result;
            }
            else if (!predefined) {
                // Deals with PL/SQL types: Augument the predefined type
                // by -typemap with more specific PL/SQL type information.
                if (modifier != null && modifier.equals("PL/SQL RECORD")) {
                    result.setTypecode(OracleTypes.PLSQL_RECORD);
                }
                else if (modifier != null && modifier.equals("PL/SQL TABLE")) {
                    result.setTypecode(OracleTypes.PLSQL_INDEX_TABLE);
                }
                else if (modifier != null && modifier.equals("TABLE")) {
                    result.setTypecode(OracleTypes.PLSQL_NESTED_TABLE);
                }
                else if (modifier != null && modifier.equals("VARRAY")) {
                    result.setTypecode(OracleTypes.PLSQL_VARRAY_TABLE);
                }
                return result;
            }
        }

        // Case 1: result is null
        // Case 2: result not null, predefined, OracleTypes_TBD
        if (modifier != null && modifier.equals("PL/SQL RECORD")) {
            List<FieldInfo> fieldInfo = PlsqlRecordType.getFieldInfo(packageName, methodName, methodNo,
                sequence, this);
            List<AttributeField> fields = SqlTypeWithFields.reflectFields(false, fieldInfo, this,
                parentType, true /* isGrandparent */);
            // if predefined, overwrite entry already in m_predefiendTypes
            if (predefined) {
                String hint = result.getHint();
                result = new PlsqlRecordType(result.getSqlName(), fieldInfo, fields, rowtypeInfoA,
                    false /* generateMe */, parentType, this);
                result.setHint(hint);
            }
            else {
                // Prepare SqlName for the PL/SQL type
                SqlName sqlName = new PlsqlRecordName(schema, type, true, line, col, packageName,
                    parentType, fields, this);
                sqlName.setAnnotation(genPattern((LangName)sqlName.getAnnotation(), sqlName
                    .getSimpleName(), true));

                result = new PlsqlRecordType(sqlName, fieldInfo, fields, rowtypeInfoA,
                    true /* generateMe */, parentType, this);
            }
        }
        else {
            int oracleType = OracleTypes.UNSUPPORTED;
            if (modifier != null && modifier.equals("PL/SQL TABLE")) {
                oracleType = OracleTypes.PLSQL_INDEX_TABLE;
            }
            else if (modifier != null && modifier.equals("TABLE") && type.indexOf(".") > -1) {
                oracleType = OracleTypes.PLSQL_NESTED_TABLE;
            }
            else if (modifier != null && modifier.equals("VARRAY") && type.indexOf(".") > -1) {
                oracleType = OracleTypes.PLSQL_VARRAY_TABLE;
            }
            if (oracleType != OracleTypes.UNSUPPORTED) {
                ElemInfo elemInfo = PlsqlTableType.getElemInfo(schema, type, packageName,
                    methodName, methodNo, m_viewCache);
                int[] details = new int[PlsqlTableType.NUMBER_OF_DETAILS];

                SqlType elemType = (SqlType)PlsqlTableType.getComponentType(elemInfo, this,
                    parentType, details);

                if (predefined) {
                    String hint = result.getHint();
                    result = PlsqlTableType.newInstance(result.getSqlName(), oracleType, elemInfo,
                        elemType, details, false, parentType, isGrandparent, this);
                    result.setHint(hint);
                }
                else {
                    // Prepare SqlName for the PL/SQL type
                    SqlName sqlName = new PlsqlTableName(schema, type, true, line, col,
                        packageName, parentType, elemType, this);
                    sqlName.setAnnotation(genPattern((LangName)sqlName.getAnnotation(), sqlName
                        .getSimpleName(), true));

                    result = PlsqlTableType.newInstance(sqlName, oracleType, elemInfo, elemType,
                        details, true, parentType, isGrandparent, this);
                }
            }
        }
        if (result != null) {
            return result;
        }
        throw new PublisherException("unsupported Type " + schema + "." + type);
    }

    /*
     * look up rowtype columns
     */
    List<RowtypeInfo> reflectRowtypeInfo(String packageName, String methodName, String methodNo,
        int sequence) throws SQLException {
        // Although package_name and type_name derived from getPlsqlTypeName()
        // can be used for the queries, we use method and sequence
        // to be more general. If this type is defined via CURSOR%ROWTYPE,
        // method, method_no and sequence has to be used to
        // identify this type in ALL_ARGUMENTS.
        Iterator<ViewRow> iter = m_viewCache.getRows(ALL_ARGUMENTS, new String[0], new String[]{
           PACKAGE_NAME, OBJECT_NAME, OVERLOAD}, new Object[]{packageName, methodName,
            methodNo}, new String[0]);
        ArrayList<ViewRow> viewRows = new ArrayList<ViewRow>();
        while (iter.hasNext()) { // DISTINCT not enforced
            UserArguments item = (UserArguments)iter.next();
            viewRows.add(item);
        }
        List<RowtypeInfo> rowtypeInfoA = RowtypeInfo.getRowtypeInfo(viewRows);
        int data_level = 0;
        for (int i = 0; i < rowtypeInfoA.size(); i++) {
            RowtypeInfo rti = rowtypeInfoA.get(i);
            if (sequence == -1 || sequence == rti.sequence()) {
                data_level = rti.data_level();
                break;
            }
        }
        int next_rec_sequence = -1;
        for (int i = 0; i < rowtypeInfoA.size(); i++) {
            RowtypeInfo rti = rowtypeInfoA.get(i);
            if (data_level == rti.data_level() && (sequence == -1 || sequence < rti.sequence())) {
                next_rec_sequence = rti.sequence();
                break;
            }
        }
        data_level++;
        ArrayList<RowtypeInfo> rowtypeInfoW = new ArrayList<RowtypeInfo>();
        for (int i = 0; i < rowtypeInfoA.size(); i++) {
            RowtypeInfo rti = rowtypeInfoA.get(i);
            if ((sequence == -1 || sequence < rti.sequence()) && data_level == rti.data_level()
            // && data_level <= rti.data_level()
                && (next_rec_sequence == -1 || next_rec_sequence > rti.sequence())) {
                rowtypeInfoW.add(rti);
            }
        }
        return rowtypeInfoW;
    }

    public SqlType addPredefType(String schema, String type, int typecode, String javaName,
        String convertInto, String convertOutOf, String conversionTarget) throws PublisherException {
        SqlName sn = new SqlName(schema, type, false, false, true, convertInto, convertOutOf,
            conversionTarget, this);

        // Does the Java type contain a type annotation?
        int pos;
        String annotation;
        boolean isPlsqlIndexTable = false;
        int maxLen = Integer.valueOf(DEFAULT_VARCHAR_LEN);
        int maxElemLen = -1;
        boolean isNumeric = true;
        if ((pos = javaName.indexOf("[")) >= 0) {
            annotation = "/*" + javaName.substring(pos).trim() + "*/";
            isPlsqlIndexTable = true;
            int pos1 = pos;
            int pos2 = javaName.indexOf("]");
            if (pos2 > -1) {
                try {
                    maxLen = Integer.parseInt(javaName.substring(pos1 + 1, pos2));
                }
                catch (NumberFormatException e) {
                    System.err.println("ERROR: number format error: " + javaName);
                }
            }
            pos1 = javaName.indexOf("(");
            pos2 = javaName.indexOf(")");
            if (pos1 > -1 && pos2 > -1) {
                try {
                    maxElemLen = Integer.parseInt(javaName.substring(pos1 + 1, pos2));
                }
                catch (NumberFormatException e) {
                    System.err.println("ERROR: number format error: " + javaName);
                }
            }
            javaName = javaName.substring(0, pos);
            if ("String".equals(javaName) || "java.lang.String".equals(javaName)) {
                isNumeric = false;
            }
            javaName = javaName + "[]";
            if (maxElemLen == -1) {
                maxElemLen = Integer.valueOf(getDefaultTypeLen("VARCHAR2"));
            }
        }
        else {
            annotation = "";
        }

        sn.setLangName(null, javaName, null, null, null, null, null, null, true);

        SqlType st = null;
        if (isPlsqlIndexTable) {
            st = new PlsqlIndexTableType(sn, isNumeric, maxLen, maxElemLen);
            m_predefTypes.put(sn, st);
        }
        else {
            st = addPredefType(sn, typecode);
        }
        st.setHint(annotation);
        return st;
    }

    public SqlType addPredefType(SqlName name, int typecode) throws PublisherException {
        // To determine whether the Java type implements ORAData or CustomData
        boolean isPrimitive = true;
        String declClassName = ((name.getDeclPackage() != null && name.getDeclPackage().length() > 0) ? (name
            .getDeclPackage() + ".")
            : "")
            + name.getDeclClass();
        Class<?> declClass = null;
        try {
            declClass = Class.forName(declClassName);
        }
        catch (Throwable t) {
            try {
                declClass = Class.forName("java.lang." + declClassName);
            }
            catch (Throwable t2) {
                // class did not check out as a primitive class
            }
        }
        if (declClass == null) {
            // Cannot load that class. Assume it is not primitive.
            isPrimitive = false;
        }
        else if (declClass != null) {
            try {
                declClass.getField("_SQL_TYPENAME");
                isPrimitive = false;
            }
            catch (Throwable t) {
                try {
                    declClass.getField("_SQL_NAME");
                    isPrimitive = false;
                }
                catch (Throwable t2) {
                    // class has neither _SQL_TYPENAME nor _SQL_NAME
                }
            }
        }
        if (declClassName.equals("boolean") || declClassName.equals("int")
            || declClassName.equals("short") || declClassName.equals("double")
            || declClassName.equals("float") || declClassName.equals("long")
            || declClassName.equals("byte") || declClassName.equals("char")
            || declClassName.endsWith("[]")) {
            isPrimitive = true;
        }

        if (typecode == OracleTypes.UNSUPPORTED || typecode == SqlType.ORACLE_TYPES_TBD) {
            if ("INTEGER".equals(name.getTargetTypeName())) {
                typecode = OracleTypes.INTEGER;
            }
            if ("CHAR".equals(name.getTargetTypeName())) {
                typecode = OracleTypes.CHAR;
            }
        }

        SqlType st = null;
        if (!isPrimitive) { // for sure this is a SQL Object Type
            try {
                st = new SqlObjectType(name, typecode, false, null, this);
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        else {
            st = new SqlType(name, typecode, false, isPrimitive, null, this);
        }
        if (st != null) {
            m_predefTypes.put(name, st);
        }
        else {
            throw new PublisherException("Cannot find Type " + name);
        }
        return st;
    }

    public void addType(Name name, TypeClass type, boolean generateMe) {
        if (generateMe) {
            m_userTypes.add(type);
        }
        if (name instanceof SqlName && type instanceof SqlType) {
            m_allTypes.put(name, type);
        }
    }

    /*
     * Add default parameter holder types for PL/SQL methods
     */
    public SqlType addDefaultArgsHolderType(SqlType valueType, String packageName,
        SqlType parentType, boolean ncharFormOfUse) throws SQLException, PublisherException {
        String typeName = "";
        if (valueType.getSqlName().getTypeName().indexOf(".") > -1) {
            typeName += valueType.getSqlName().getSimpleName();
        }
        else {
            typeName += valueType.getSqlName().getTypeName();
        }
        SqlName sqlName = new DefaultArgsHolderName(null, typeName, true, 0, 0, packageName,
            parentType, valueType, this);
        SqlType result = findType(sqlName);
        if (result == null) {
            sqlName.setAnnotation(genPattern((LangName)sqlName.getAnnotation(), sqlName
                .getSimpleName(), true));
            result = new DefaultArgsHolderType(sqlName, valueType, ncharFormOfUse, this);
        }
        return result;
    }

    public boolean isUserType(TypeClass t) {
        if (t instanceof SqlType) {
            for (int i = 0; i < m_userTypes.size(); i++) {
                if (m_userTypes.get(i).equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    private SqlType findType(SqlName sqlName) {
        return _findType(m_allTypes, sqlName);
    }

    private SqlType findType(String schema, String type) {
        SqlName sqlName = new SqlName(Util.getSchema(schema, type), Util.getType(schema, type),
            false, this);
        return _findType(m_allTypes, sqlName);
    }

    private SqlType findPredefType(SqlName sqlName) {
        return _findType(m_predefTypes, sqlName);
    }

    private SqlType findPredefType(String schema, String type) {
        SqlName sqlName = new SqlName(schema, type, false, this);
        return _findType(m_predefTypes, sqlName);
    }

    private SqlType _findType(Map<Name, TypeClass> ht, SqlName sqlName) {
        return (SqlType)ht.get(sqlName);
        /*
         * Enumeration keys = ht.keys(); while (keys.hasMoreElements()) { Object key =
         * keys.nextElement(); if (sqlName.equals(key)) { return (SqlType) ht.get(key); } } return
         * null;
         */
    }

    public SqlType findType(String name) {
        String schema = Util.getSchema(null, name);
        name = Util.getType(null, name);
        SqlName sqlName = new SqlName(schema, name, false, this);
        SqlType sqlType = findType(sqlName);
        if (sqlType == null) {
            sqlType = findPredefType(sqlName);
        }
        return sqlType;
    }

    /**
     * Add all types declared in the given schema to the set of types to be translated.
     */
    public void addAllTypes(String schema) throws SQLException, PublisherException {
        if (m_conn != null) {
            Iterator<ViewRow> iter = m_viewCache
                .getRows(ALL_TYPES, new String[0], new String[]{OWNER, "PREDEFINED"},
                    new Object[]{schema, "NO"}, new String[]{TYPE_NAME});
            while (iter.hasNext()) {
                AllTypes allTypes = (AllTypes)iter.next();
                addSqlDBType(schema, allTypes.typeName, null, "", false, null);
            }
        }
    }

    public void addAllPackages(String schema) throws SQLException, PublisherException {
        if (m_conn != null) {
            PreparedStatement stmt = m_conn.prepareStatement("SELECT OBJECT_NAME AS TYPE_NAME FROM " +
                    "ALL_OBJECTS WHERE OWNER = :1 AND OBJECT_TYPE = 'PACKAGE' AND STATUS='VALID'");
            stmt.setString(1, schema);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SqlName sn = new SqlName(schema, rs.getString(1), true, this);
                // determine generated Java names using -genPattern setting.
                sn
                    .setAnnotation(genPattern((LangName)sn.getAnnotation(), sn.getSimpleName(),
                        true));
                addSqlType(sn, Util.IS_PACKAGE, false, true, null, null);
            }
            rs.close();
            stmt.close();

            stmt = m_conn
                .prepareStatement("SELECT COUNT(OBJECT_NAME) AS ARG_COUNT FROM ALL_ARGUMENTS WHERE OWNER = :1 AND PACKAGE_NAME IS NULL AND DATA_LEVEL = 0");
            stmt.setString(1, schema);
            rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) {
                    SqlType t = addSqlUserType(schema, "", Util.IS_TOPLEVEL, true, 0, 0, null);
                    String userName = SqlName.sqlIdToJavaId(TOPLEVEL, true);
                    t.getSqlName().setLangName("", userName, null, null, null, null, null, null,
                        true);
                }
            }
            rs.close();
            stmt.close();
        }
    }

    /* Add Java non-array types for publishing */
    public JavaType addJavaType(String typeName, List<AttributeField> fields,
        List<ProcedureMethod> methods, boolean genPattern, TypeClass sqlType) throws SQLException {
        if (typeName == null) {
            return null;
        }
        JavaType jt = null;
        for (int i = 0; i < m_userTypes.size(); i++) {
            if (m_userTypes.get(i) instanceof JavaType) {
                JavaType jTypeTmp = (JavaType)m_userTypes.get(i);
                JavaName jNameTmp = new JavaName(null, typeName, null, null, null);
                if (jTypeTmp.getJavaName().equals(jNameTmp)) {
                    jt = jTypeTmp;
                }
            }
        }
        if (jt != null) {
            return jt;
        }
        JavaName javaName = new JavaName(null, typeName, null, null, null);
        if (genPattern) {
            javaName = (JavaName)genPattern(javaName, typeName, false);
        }
        jt = new JavaBaseType(javaName, fields, methods, sqlType);
        m_userTypes.add(jt);
        return jt;
    }

    /**
     * Search among userTypes, and return a boolean indicating whether a subclass of the receiver
     * type has methods
     */
    public boolean hasMethodsInSubclasses(TypeClass who) throws SQLException, PublisherException {
        Iterator<TypeClass> iter = m_userTypes.iterator();
        while (iter.hasNext()) {
            TypeClass t = iter.next();
            if (!(t instanceof SqlType)) {
                continue;
            }
            SqlType st = (SqlType)t;
            boolean stHasMethods = st.hasMethods();
            while ((st = (SqlType)st.getSupertype()) != null) {
                if (who.getName() != null && who.getName().equals(st.getName())) {
                    if (stHasMethods) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    public void createSqlStmtType(SqlName sqlName) throws SQLException {
        if (m_sqlStmtType == null) {
            m_sqlStmtType = new SqlStmtType(sqlName, this);
        }
    }

    public boolean isPre920() {
        if (m_isPre920 == null) {
            try {
                String v = m_conn.getMetaData().getDatabaseProductVersion().toUpperCase();
                if (v.startsWith("ORACLE DATABASE 10G") || v.startsWith("ORACLE DATABASE 11G")) {
                    m_isPre920 = Boolean.valueOf(false);
                    return false;
                }
                int pos = v.indexOf("ORACLE");
                if (0 < pos) {
                    v = v.substring(pos);
                }
                String vp = v.substring(0, "ORACLExx".length()).toUpperCase();
                if (vp.equals("ORACLE12")
                    || vp.equals("ORACLE11")
                    || vp.equals("ORACLE10")
                    || (vp.equals("ORACLE9I") && (v.indexOf("9.2.") > 0 || v.indexOf("9.3.") > 0 || v
                        .indexOf("9.4.") > 0))) {
                    m_isPre920 = Boolean.valueOf(false);
                }
            }
            catch (Exception e) {
                // Connection is pre 9.2.0
            }
            if (m_isPre920 == null) {
                m_isPre920 = Boolean.valueOf(true);
            }
        }
        return m_isPre920.booleanValue();
    }

    public boolean geqOracle9() {
        return m_geq9i;
    }

    public static boolean geqOracle9(Connection conn) {
        boolean geq9i = false;
        try {
            String dv = conn.getMetaData().getDatabaseProductVersion().toUpperCase();

            int pos = dv.indexOf("ORACLE");
            if (0 < pos) {
                dv = dv.substring(pos);
            }
            geq9i = dv.startsWith("ORACLE9") || dv.startsWith("ORACLE DATABASE 10G")
                || dv.startsWith("ORACLE DATABASE 11G") || dv.startsWith("ORACLE1")
                || dv.startsWith("ORACLE2") || dv.startsWith("ORACLE3");
        }
        catch (SQLException se) {
            /* assume the database is old */
        }
        return geq9i;
    }

    public void setTransitive(boolean transitive) {
        m_transitive = transitive;
    }

    // Return true if a column is null
    public static boolean isNull(String col) {
        return (col == null || col.length() == 0);
    }

    public boolean noUserTypes() {
        return m_userTypes.isEmpty();
    }

    public Map<String, SqlType> getTypeMap() {
        return m_typeMap;
    }

    /**
     * Used for implicitly publishing SQL types Based on the -genPattern setting, sets the LangName
     * associated with this SqlName.
     */
    private LangName genPattern(LangName langName, String simpleName, boolean sql2Java) {
        String genPattern = null;
        if (genPattern == null) {
            return langName;
        }
        @SuppressWarnings("unused") String useName = simpleName;
        if (sql2Java) {
            useName = SqlName.sqlIdToJavaId(simpleName, true);
        }
        // return sqlstring entry (e.g., a:b:c#d) according to m_genPattern.
        // Entries in itemTokens are used to replace %?
        // in m_genPattern. For instance, itemTokens can be
        // - {sqlTypeName, JavaTypeName, JavaSubclassName, JavaItf}
        // - {sqlTypeName}
        String sql = genPattern;
        for (int i = genPattern.length() - 1; i >= 0; i--) {
            if (genPattern.charAt(i) == '%' && i != genPattern.length() - 1) {
                int j = genPattern.charAt(i + 1) - '0';
                sql = sql.substring(0, i) + ((j == 1) ? simpleName : useName)
                    + sql.substring(i + 2);
                // System.out.println("SqlName: sql = "+sql); //D+
            }
        }
        // Derive useName, subclassName, interface name
        String useItf = null;
        String generatedName = null;
        String generatedItf = null;
        StringTokenizer st = new StringTokenizer(sql, "#");
        String[] tokens = new String[st.countTokens()];
        for (int i = 0; 0 < st.countTokens(); i++) {
            tokens[i] = st.nextToken();
            if (i == 1) {
                generatedItf = tokens[1];
                useItf = tokens[1];
            }
        }
        st = new StringTokenizer(tokens[0], ":");
        tokens = new String[st.countTokens()];
        for (int i = 0; 0 < st.countTokens(); i++) {
            tokens[i] = st.nextToken();
            if (i == 0) {
                useName = tokens[0];
            }
            else if (i == 1) {
                useName = tokens[1];
                generatedName = tokens[0];
            }
        }
        if (useName == null) {
            useItf = null;
        }
        if (generatedName == null) {
            generatedItf = null;
        }
        if (useItf != null) {
            generatedItf = null;
        }
        JavaName retJavaName = new JavaName("", useName, useItf, generatedName, generatedItf);
        return retJavaName;
    }

    public String determineSqlName(String name, boolean toBeDistinguished) {
        // toBeDistinguished is a marker indicating that the name is chopped
        // or post-fixed due to length over-limit or name conflicts. If true,
        // the new name needs to go through another round of conflicts checking.
        if (name.length() > MAX_IDENTIFIER_LENGTH) {
            name = name.substring(0, MAX_IDENTIFIER_LENGTH);
            toBeDistinguished = true;
        }
        String uniqueName = name.toUpperCase();
        if (m_allTypeNames.contains(uniqueName)
            || (toBeDistinguished && m_allGeneratedTypeNames.contains(uniqueName))) {
            do {
                int len1 = name.length();
                int len2 = Integer.toString(m_allGeneratedTypeNamesMagicNumber++).length();
                if ((len1 + len2) > MAX_IDENTIFIER_LENGTH) {
                    name = name.substring(0, MAX_IDENTIFIER_LENGTH - len2);
                }
                uniqueName = name.toUpperCase() + m_allGeneratedTypeNamesMagicNumber;
            }
            while (m_allTypeNames.contains(uniqueName)
                || (toBeDistinguished && m_allGeneratedTypeNames.contains(uniqueName)));
        }
        m_allGeneratedTypeNames.add(uniqueName);
        return uniqueName;
    }


    public ViewCache getViewCache() {
        return m_viewCache;
    }

    public void getViewCache(ViewCache vc) {
        m_viewCache = vc;
    }

    public void close() {
        try {
            if (m_conn != null) {
                m_conn.close();
            }
            if (m_viewCache != null) {
                m_viewCache.close();
            }
        }
        catch (Exception e) {
            // Closing resources. OK to ignore exception.
        }
    }

    /**
     * Returns all the user-defined SqlTypes. New types may be appended to the end of the
     * enumeration as the existing types are published or navigated.
     */
    public Iterator<TypeClass> getUserTypes() {
        return m_userTypes.iterator();
    }

    /**
     * @return metadata for generated PL/SQL wrapper package
     */

    public WrapperPackageMetadata getWrapperPackageMetadata() {
        return m_wrapperPackageMetadata;
    }

    public void addWrapperMethodMetadata(String name, String[] paramTypes, String[] paramNames,
        String returnType) {
        if (m_wrapperPackageMetadata == null) {
            m_wrapperPackageMetadata = new WrapperPackageMetadata("");
        }
        m_wrapperPackageMetadata.addMethod(new WrapperMethodMetadata(name, paramTypes, paramNames,
            returnType));
    }

}
