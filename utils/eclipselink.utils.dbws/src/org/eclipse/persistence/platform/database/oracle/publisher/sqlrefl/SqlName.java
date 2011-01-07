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
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

//javase imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//EclipseLink imports
import org.eclipse.persistence.platform.database.oracle.publisher.Util;

/**
 * A SqlName encapsulates the name of a database entity, that is, anything declared directly within
 * a schema. A SqlName identifies the schema in which the entity is declared, and the name of the
 * entity within the schema. Schema and entity names are converted to a canonical form so that they
 * may be compared accurately. Quotes are stripped, and names that are not quoted are shifted to
 * upper case. This class implements 'equals' and 'hash', so that SqlNames may be stored in
 * Hashtables. This class is currently used only for type names. An attribute of a type is not
 * considered to be a SqlName, because it is nested within the type, not directly within the schema.
 * A SqlName instance can describe a PL/SQL type. Both the PL/SQL type name and the corresponding
 * SQL name are captured in the SqlName instance. The two names are accessed via getTypeName(), and
 * getTargetTypeName().
 */
public class SqlName extends Name {

    public final static String ROWTYPE = "ROWTYPE";
    public final static String ROWTYPE_PL = ROWTYPE + "_PL";
    public final static String ROWTYPE_SQL = ROWTYPE + "_SQL";
    private static int m_rowtypeDistinguisher = 0;
    private final static String PUBLIC = "PUBLIC.";
    static String m_defaultSchema;
    static int m_case;
    static int m_targetLang;
    protected static int m_sql2PLCounter = 0;
    protected static int m_pl2SQLCounter = 0;

    protected boolean m_fromDB;
    protected boolean m_contextFromIntype;
    protected int m_line = 0;
    protected int m_column = 0;
    protected boolean m_quoted;
    protected boolean m_printAsIs;
    /**
     * The original database type for the type represented. - If (m_sourceName==m_name), this type
     * does not need conversion and can be published into Java classes directly. - If
     * (m_sourceName!=m_name), this type is typically a PL/SQL type, which needs to be converted
     * into and out of a SQL type in the published code. In this case, m_name represents that SQL
     * type
     */
    protected String m_sourceName;
    // If the record type is defined via CURSOR%ROWTYPE
    protected boolean m_isRowType = false;
    protected boolean m_isReused = false;
    // If the type is built-in or predefined by -defaulttypemap, -addtypemap
    protected boolean m_predefined = false;
    protected Boolean m_hasConversion;
    protected String m_convertOutOf;
    protected String m_convertInto;
    protected String m_convertOutOfQualified;
    protected String m_convertIntoQualified;

    /**
     * Initializes a SqlName with the schema and name of a declared entity. If "schema" is null, the
     * default schema will be used.
     *
     * @param schema
     *            the schema in which the entity is declared
     * @param type
     *            the declared name of the entity in the schema
     */
    public SqlName(String schema, String type, boolean fromDB, int line, int col,
        SqlReflector reflector) {
        this(schema, type, fromDB, line, col, false, false, null, null, null, reflector);
    }

    public SqlName(String schema, String type, boolean fromDB, SqlReflector reflector) {
        this(schema, type, fromDB, 0, 0, false, false, null, null, null, reflector);
    }

    // Used by SqlType to add predefined identity
    // mapping between PL/SQL and SQL
    // convertInto and converOutof being null means identity mapping
    SqlName(String plsql, String sql) {
        this(null, plsql, true, true, true, null, null, sql, null/* reflect */);
    }

    // A type needs conversion if convertOutOf/convertInto/conversionTarget
    // are not null
    public SqlName(String schema, String type, boolean fromDB, boolean printAsIs,
        boolean predefined, String convertInto, String convertOutOf, String conversionTarget,
        SqlReflector reflector) {
        this(schema, type, fromDB, 0, 0, printAsIs, predefined, convertInto, convertOutOf,
            conversionTarget, reflector);
    }

    protected SqlName(String schema, String type, boolean fromDB, int line, int col,
        boolean printAsIs, boolean predefined, String convertInto, String convertOutOf,
        String conversionTarget, SqlReflector reflector) {
        super(massageSchema(schema, fromDB, predefined, reflector), fromDB ? type : reflector
            .getViewCache().dbifyName(type));
        if (m_context == null) {
            m_context = NO_CONTEXT;
        }
        m_fromDB = fromDB;
        m_contextFromIntype = (schema != null && !fromDB) ? true : false;
        m_line = line;
        m_column = col;
        m_quoted = isQuoted(type) ? true : false;
        m_printAsIs = printAsIs;
        m_predefined = predefined;
        m_convertOutOf = convertOutOf;
        m_convertInto = convertInto;
        if (conversionTarget != null) {
            m_sourceName = m_name;
            m_name = conversionTarget;
        }
        else {
            m_sourceName = m_name;
        }
        m_convertOutOfQualified = convertOutOf;
        m_convertIntoQualified = convertInto;
    }

    /**
     * Create a SqlName instance for a PL/SQL type, which requires extra identifying information,
     * such the names for the package and method that mentions this PL/SQL type.
     *
     * @param schema
     * @param type
     * @param parentType
     *            The PL/SQL package type that references to the SqlType for which the SqlName is
     *            created for
     */
    public SqlName(String schema, String type, boolean fromDB, int line, int col,
        String packageName, SqlType parentType, SqlReflector reflector) {
        this(schema, null, fromDB, line, col, false, false, null, null, null, reflector);

        // Figure out an identifying name for the SQL type
        // correpsonding to the PL/SQL type
        m_sourceName = type;
        if (m_context == null || m_context.equals("")) {
            if (type.indexOf('.') >= 0) {
                m_context = type.substring(0, type.indexOf('.'));
                m_sourceName = type.substring(type.indexOf('.') + 1);
            }
        }
        // Determine the SQL type name for a PL/SQL type
        String[] mSourceName = new String[]{m_sourceName};
        boolean[] mIsRowType = new boolean[]{m_isRowType};
        m_name = determineSqlName(packageName, mSourceName, parentType, mIsRowType, reflector);
        m_sourceName = mSourceName[0];
        m_isRowType = mIsRowType[0];
        m_name = m_fromDB ? m_name : dbifyName(m_name, reflector);
    }

    public static String interfaceIfPossible(SqlName sqlName, boolean itfIfPossible) {
        if (itfIfPossible && sqlName.hasUseItf()) {
            return sqlName.getUseItf();
        }
        else if (sqlName.hasUseClass()) {
            return sqlName.getUseClass();
        }
        else if (itfIfPossible && sqlName.hasDeclItf()) {
            return sqlName.getDeclItf();
        }
        else {
            return sqlName.getDeclClass();
        }
    }

    /**
     * Determine the SQL type name for a PL/SQL type. Generate a name in the form of
     * <prefix>_<name>. The <prefix> term is determined as follows in precedency - java user
     * interface name of the associated PL/SQL package - java base interface name of the associated
     * PL/SQL package - the associated PL/SQL package name Generated names are subject to length and
     * conflict check. If length excceeds PL/SQL identifier limit, the name will be chopped. If
     * conflicts occurs, the name will be pos-fixed with numeric values,
     *
     **/
    public static String determineSqlName(String packageName, String[] sourceName,
        TypeClass parentType, boolean[] isRowType, SqlReflector reflector) {
        String parentName = null; // Java name for the PL/SQL package
        if (parentType != null) {
            SqlName parentSqlName = (SqlName)parentType.getNameObject();
            if (parentSqlName != null) {
                parentName = interfaceIfPossible(parentSqlName, true /* itfIfPossible */)
                    .toUpperCase();
            }
        }
        String name = sourceName[0];
        reflector.addAllGeneratedTypeNames(name);
        // replace package name with parentName
        if (name.indexOf('.') >= 0 && parentName != null) {
            name = parentName + "_" + name.substring(name.indexOf('.') + 1);
            reflector.addAllGeneratedTypeNames(parentName);
        }
        name = name.replace('.', '_').replace(' ', '_');

        boolean toBeDistinguished = false;
        if (sourceName[0].equals("PL/SQL RECORD")) { // %ROWTYPE
            isRowType[0] = true;
            sourceName[0] = ROWTYPE_PL + (m_rowtypeDistinguisher++);
            name = ROWTYPE_SQL;
            if (packageName != null && !packageName.equals("")) {
                if (parentName != null)
                    name = parentName + "_" + name;
                else
                    name = packageName + "_" + name;
            }
            toBeDistinguished = true;
        }
        return reflector.determineSqlName(name, toBeDistinguished);
    }

    public static void initStaticVariables() {
        setDefaultSchema(SqlName.NO_CONTEXT);
        // m_convFuns = new Hashtable();
        // m_convNameCounter = 0;
        m_rowtypeDistinguisher = 0;
        // added as well
        m_sql2PLCounter = 0;
        m_pl2SQLCounter = 0;
    }

    /**
     * Returns the schema name of the declared entity.
     */
    public String getSchemaName() {
        return m_context;
    }

    /**
     * Returns the name of the original database type. For a PL/SQL type, returns the name of the
     * declared PL/SQL type within the schema. not uppercased The name is prefixed by the package
     * name and ".", except for CURSOR%ROWTYPE
     */
    public String getTypeName() {
        return m_sourceName;
    }

    /**
     * Return the SQL type name. If this SqlName instance represents a PL/SQL type, or a type
     * requring conversions into and out of a SQL type, the method returns the SQL type name.
     * Otherwise, this method returns the same name as getTypeName().
     */

    public String getTargetTypeName() {
        // Deal with SQL synonymous
        if (m_name.equals("TIMESTAMP WITH LOCAL TZ")) {
            return "TIMESTAMP WITH LOCAL TIME ZONE";
        }
        if (m_name.equals("TIMESTAMP WITH TZ")) {
            return "TIMESTAMP WITH TIME ZONE";
        }
        // "BOOLEAN" is really "PL/SQL BOOLEAN" which doesn't have a JDBC equivalent
        // use INTEGER and SYS.SQLJUTL.INT2BOOL/SYS.SQLJUTL.BOOL2INT conversion
        if (m_name.equals("BOOLEAN")) {
            return "INTEGER";
        }
        return m_name;
    }

    public String getFullTargetTypeName(int schemaName) {
        return getFullTypeName(getTargetTypeName(), schemaName);
    }

    /**
     * Returns the use class name of a SqlName. This includes the package name if the Java class is
     * not in the current package. If the Java name has not been created yet, it is constructed
     * using the CASE and PACKAGE options.
     *
     * * @param SqlName the SQL name of the type
     *
     * @param currPackage
     *            the package from which the class is referenced
     * @return the name of an SQL type.
     */
    public String getUseClass(String currPackage) {
        return ((JavaName)getLangName()).getUseClass(currPackage);
    }

    public String getUseClass() {
        return ((JavaName)getLangName()).getUseClass();
    }

    public String getUseClass(boolean full) {
        return ((JavaName)getLangName()).getUseClass(full);
    }

    /**
     * Returns the Java use package name of an SqlName.
     */
    public String getUsePackage() {
        return ((JavaName)getLangName()).getUsePackage();
    }

    /**
     * return ture, if this type has user subclass
     */
    public boolean hasUseClass() {
        return ((JavaName)getLangName()).hasUseClass();
    }

    /**
     * Returns the use interface name of a SqlName.
     */
    public String getUseItf() {
        return ((JavaName)getLangName()).getUseItf();
    }

    /**
     * Returns the use interface name of a SqlName.
     */
    public String getUseItf(String currPackage) {
        return ((JavaName)getLangName()).getUseItf(currPackage);
    }

    /**
     * Returns the Java use interface
     */
    public String getUseItfPackage() {
        return ((JavaName)getLangName()).getUseItfPackage();
    }

    /**
     * If both decl interface and use interface are defined, we use the latter
     */
    public boolean hasUseItf() {
        return getUseItf() != null;
    }

    public boolean hasDeclItf() {
        return getDeclItf() != null;
    }

    /**
     * Returns the boolean whether SqlName is from the DB.
     */
    public boolean getFromDB() {
        return m_fromDB;
    }

    /**
     * Returns the line number where the sqlname was found by the parser.
     */
    public int getLine() {
        return m_line;
    }

    /**
     * Returns the column number where the sqlname was found by the parser.
     */
    public int getColumn() {
        return m_column;
    }

    /**
     * Returns the boolean whether SqlName was quoted or not
     */
    public boolean isQuoted() {
        return m_quoted;
    }

    /**
     * Returns the boolean whether SqlName was quoted or not
     */
    public static boolean isQuoted(String str) {
        if (str != null && str.startsWith("\"") && str.endsWith("\"")) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the declaration class name of an SqlName. The declaration class name is the name of
     * the class that JPub generates. It is different from the use class name, the name of the class
     * name generated when the class is used, rather than declared, if the use class is
     * user-written. The user tells JPub that this is the case by putting the clause
     * "GENERATE <decl name> AS <use name>" in the input file.
     *
     * * @return the decl class name of a type.
     */
    public String getDeclClass() {
        return getLangName().getDeclClass();
    }

    /**
     * Returns the declaration package name of an SqlName. The declaration package name is the name
     * of the package that JPub generates. It is different from the use package name, the name of
     * the package name generated when the package is used, rather than declared, if the use package
     * is user-written. The user tells JPub that this is the case by putting the clause "GENERATE
     * <decl name> AS <use name> in the input file.
     *
     * * @return the decl package name of a type.
     */
    public String getDeclPackage() {
        return getLangName().getDeclPackage();
    }

    public String getDeclClass(String currPackage) {
        return ((JavaName)getLangName()).getDeclClass(currPackage);
    }

    /**
     * Returns the declaration interface name of a SqlName.
     */
    public String getDeclItf() {
        return ((JavaName)getLangName()).getDeclItf();
    }

    /**
     * Returns the declaration interface name of a SqlName.
     */
    public String getDeclItf(String currPackage) {
        return ((JavaName)getLangName()).getDeclItf(currPackage);
    }

    /**
     * Returns the Java declaration interface of an SqlName.
     */
    public String getDeclItfPackage() {
        return ((JavaName)getLangName()).getDeclItfPackage();
    }

    /**
     * Returns the LangName of this SqlName.
     */
    public LangName getLangName() {
        LangName j = (LangName)getAnnotation();

        if (j == null) {
            j = setLangName("", null, null, null, null, null, null, null, true);
        }

        return j;
    }

    /**
     * Sets the LangName associated with this SqlName. Create a new C or Java name, and associate it
     * with this SqlName.
     */
    public LangName setLangName(String packageName, String useName, String useItf,
        String generatedName, String generatedItf, String hfile, SqlType firstParent,
        String cppfile, boolean userNameGiven) {
        LangName langName = (LangName)getAnnotation();

        if (langName == null) {
            if (useName == null) {
                useName = sqlIdToJavaId(m_name, true);
            }
            langName = (LangName)new JavaName(packageName, useName, useItf, generatedName,
                generatedItf);

            setAnnotation(langName);
        }

        /*
         * For JPub only: If there is a previous entry for this SqlName, this is an error. The root
         * cause of this error, multiple requests to unparse this SqlName, has already been
         * reported, so we won't report a separate error here.
         */
        return langName;
    }

    /**
     * Returns the complete name of the declared entity. The returned name includes the schema name
     * and the name of the entity within the schema.
     */
    public String toString() {
        String fullName = (m_sourceName.equals("")) ? "<top-level scope>"
            : ((m_printAsIs || m_sourceName.indexOf(".") < 0) ? m_sourceName : "\"" + m_sourceName
                + "\"");

        if (m_context != NO_CONTEXT) {
            String schemaName = (m_printAsIs || m_context.indexOf(".") < 0) ? m_context : "\""
                + m_context + "\"";

            fullName = schemaName + "." + fullName;
        }

        return stripPublic(fullName);
    }

    /**
     * Returns the name of the declared entity as a quoted string. If a non-null schema name was
     * supplied when this SqlName was constructed, the omitSchemaName flag is ignored, and the
     * returned name includes the schema name. If a null schema name was supplied when this SqlName
     * was declared, the returned name includes the schema name only if omitSchemaName == false.
     *
     * * @param omitSchemaName suggestion not to include the schema name
     *
     * @return the name of the declared entity as a quoted string
     */
    public String toQuotedString(boolean omitSchemaName) {
        return "\"" + toString(omitSchemaName) + "\"";
    }

    private String toString(boolean omitSchemaName) {
        if (!m_context.equals(m_defaultSchema)) {
            omitSchemaName = false;
        }

        /* This is correct, but SQLJ and JDBC don't support it yet. */
        /*
         * return "\"\\\"" + ((omitSchemaName) ? "" : m_context + "\\\".\\\"") + m_type + "\\\"\"";
         */

        /*
         * This doesn't handle embedded dots in schema and type names, but it's what JDBC and SQLJ
         * expect.
         */
        String name = null;
        if (omitSchemaName == true || m_context == null || m_context.equals("")) {
            name = getTargetTypeName();
        }
        else {
            name = m_context + "." + getTargetTypeName();
        }
        return stripPublic(name);
    }

    public String getQuotedSimpleName() {
        return (m_quoted ? "\"" : "") + getSimpleName() + (m_quoted ? "\"" : "");
    }

    /**
     * Initialize the SqlName class with the default schema name, used when a SqlName is created
     * without an explciit schema name.
     *
     * * @param defaultSchema the name of the default schema
     */
    public static void setDefaultSchema(String defaultSchema) {
        m_defaultSchema = defaultSchema;
    }

    /**
     * Set the default style of case conversion used to generate the Java equivalent of an unknown
     * SQL name. A name is unknown if its Java name was not specified via addType() or
     * addAttribute().
     */
    public static void setCase(int caseOption) {
        m_case = caseOption;
    }

    public static int getCase() {
        return m_case;
    }

    public static void setTargetLang(int targetLang) {
        m_targetLang = targetLang;
    }

    public static int getTargetLang() {
        return m_targetLang;
    }

    public static boolean langIsOtt() {
        return false;
    }

    public static boolean langIsC() {
        return false;
    }

    public static boolean langIsCpp() {
        return false;
    }

    public static boolean containsLowerChar(String s) {
        char carr[] = s.toCharArray();
        int len = carr.length;
        char ch;
        for (int i = 0; i < len; i++) {
            ch = carr[i];
            if (Character.isLetterOrDigit(ch) && Character.isLowerCase(ch)) {
                return true;
            }
        }
        return false;
    }

    public static String sqlIdToJavaId(String s, boolean wordBoundary) {
        return sqlIdToJavaId(s, wordBoundary, false);
    }

    public static String sqlIdToJavaId(String s, boolean wordBoundary, boolean avoidJavaPrimitives) {
        if (s.equals("__return"))
            return "_return";

        boolean needToWarn = false;
        char carr[] = s.toCharArray();
        int len = carr.length;
        char ch;
        int destPos = 0;
        if (m_case == Util.CASE_MIXED) {
            for (int sourcePos = 0; sourcePos < len; sourcePos++) {
                ch = carr[sourcePos];
                if (Character.isLetterOrDigit(ch)) {
                    carr[destPos++] = wordBoundary ? Character.toTitleCase(ch) : Character
                        .toLowerCase(ch);
                    wordBoundary = false;
                }
                else {
                    /* silently remove character */
                    wordBoundary = true;
                }
            }
        }
        else {
            for (int sourcePos = 0; sourcePos < len; sourcePos++) {
                ch = carr[sourcePos];
                if (Character.isJavaIdentifierPart(ch)) {
                    switch (m_case) {
                        case Util.CASE_UPPER:
                            carr[destPos] = Character.toUpperCase(ch);
                            break;
                        case Util.CASE_LOWER:
                            carr[destPos] = Character.toLowerCase(ch);
                            break;
                        case Util.CASE_OPPOSITE:
                            if (Character.isUpperCase(ch)) {
                                carr[destPos] = Character.toLowerCase(ch);
                            }
                            else if (Character.isLowerCase(ch)) {
                                carr[destPos] = Character.toUpperCase(ch);
                            }
                            break;
                        default:
                            carr[destPos] = ch;
                            break;
                    }
                    destPos++;
                }
                else {
                    /* remove character */
                    needToWarn = true;
                }
            }
        }
        if (destPos == 0) {
            // TODO -- error message
        }
        else if (needToWarn) {
            // TODO -- warn
        }
        return new String(carr, 0, destPos);
    }

    public static boolean isAlpha(char ch) {
        String lettersStr = new String("abcdefghijklmnopqrstuvwxyz");
        char[] chars = lettersStr.toCharArray();

        ch = Character.toLowerCase(ch);
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            if (chars[i] == ch) {
                return true;
            }
        }
        return false;
    }

    private static String massageSchema(String schema, boolean fromDB, boolean predefined,
        SqlReflector reflector) {
        if (schema == null) {
            schema = NO_CONTEXT;
        }

        if (predefined) {
            schema = schema.length() > 0 ? reflector.getViewCache().dbifyName(schema) : schema;
        }
        else if (!fromDB) {
            schema = schema.length() > 0 ? reflector.getViewCache().dbifyName(schema)
                : m_defaultSchema;
        }

        return schema;
    }

    /**
     * Returns true if and only if two Names are equal.
     */
    // Used for mapping SqlName to SqlType in
    // SqlType.m_predefinedTypes and SqlType.m_namedTypes
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SqlName)) {
            return false;
        }
        SqlName n = (SqlName)obj;
        boolean eq = (m_context == null || n.m_context == null || m_context.equals("") || n.m_context
            .equals("")) ? m_sourceName.equalsIgnoreCase(n.m_sourceName) : (m_context
            .equalsIgnoreCase(n.m_context) && m_sourceName.equalsIgnoreCase(n.m_sourceName));
        return eq;
    }

    /**
     * Returns a hash code for the Name. Implemented so that Names may be put in Hashtables.
     */
    public int hashCode() {
        return m_sourceName.hashCode();
    }

    // Is this a PL/SQL record type defined
    // via CURSOR%ROWTYPE
    public boolean isRowType() {
        return m_isRowType;
    }

    // Is this type built-in or predefined
    public boolean isPredefined() {
        return m_predefined;
    }

    public boolean hasConversion() {
        if (m_hasConversion == null) {
            m_hasConversion = Boolean.valueOf(getIntoConversion() != null
                || getOutOfConversion() != null);
        }
        return m_hasConversion.booleanValue();
    }

    /**
     * Returns the PL/SQL function to be used for converting this PL/SQL into a SQL type.
     * <p/>
     * Returns null if this is not a PL/SQL type or if it does not have user-defined conversions.
     */
    public String getOutOfConversion() {
        if (m_name.equals(m_sourceName)) {
            return null;
        }
        if (isPredefined()) {
            return m_convertOutOf;
        }
        if (m_convertOutOf != null) {
            return m_convertOutOf;
        }
        m_convertOutOf = "PL_TO_SQL" + (m_pl2SQLCounter++);
        // m_convertOutOfQualified = JavaPublisher.getDeclaredWrapperPackage() + "." +
        // m_convertOutOf;

        return m_convertOutOf;
    }

    /**
     * Returns the PL/SQL function to be used for converting a SQL type into this PL/SQL type.
     * <p/>
     * Returns null if this is not a PL/SQL type or if it does not have user-defined conversions.
     */
    public String getIntoConversion() {
        if (m_name.equals(m_sourceName)) {
            return null;
        }
        if (isPredefined()) {
            return m_convertInto;
        }
        if (m_convertInto != null) {
            return m_convertInto;
        }
        m_convertInto = "SQL_TO_PL" + (m_sql2PLCounter++);
        // m_convertIntoQualified = JavaPublisher.getDeclaredWrapperPackage() + "." + m_convertInto;
        return m_convertInto;
    }

    // Return the conversion function name
    // prefixed with package name,
    // if the conversion function is a generated one
    public String getOutOfConversionQualified() {
        if (getOutOfConversion() == null) {
            return null;
        }
        return m_convertOutOfQualified;
    }

    // Return the conversion function name
    // prefixed with package name,
    // if the conversion function is a generated one
    public String getIntoConversionQualified() {
        if (getIntoConversion() == null) {
            return null;
        }
        return m_convertIntoQualified;
    }

    public String getFullTypeName(int schemaName) {
        return getFullTypeName(getTypeName(), schemaName);
    }

    private String getFullTypeName(String typeName, int schemaName) {
        String name = null;
        if (m_context != null && !m_context.equals("") && m_contextFromIntype) {
            name = m_context + "." + typeName;
        }
        else {
            name = typeName;
        }
        return stripPublic(name);
    }

    public boolean isReused() {
        return m_isReused;
    }

    public static String dbifyName(String s, SqlReflector reflector) {

        if (s == null || s.equals("") || reflector == null || reflector.getConnection() == null)
            return s;
        return dbifyName(s, reflector.getConnection());
    }

    public static String dbifyName(String s, Connection conn) {
        String upper_s = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement("SELECT UPPER(:1) AS UPPER_NAME FROM DUAL");
            stmt.setString(1, s);
            rs = stmt.executeQuery();
            if (rs.next()) {
                upper_s = rs.getString(1);
            }
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            upper_s = s;
        }
        finally {
            if (rs != null)
                try {
                    rs.close();
                }
                catch (SQLException _) {
                };
            if (stmt != null)
                try {
                    stmt.close();
                }
                catch (SQLException _) {
                };
        }

        String dbName = isQuoted(s) ? s.substring(1, s.length() - 1) : (upper_s == null) ? ""
            : upper_s;
        return dbName;
    }

    private String stripPublic(String name) {
        if (name.startsWith(PUBLIC)) {
            name = name.substring(PUBLIC.length());
        }
        return name;
    }

}
