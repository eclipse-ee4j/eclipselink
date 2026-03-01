/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//EclipseLink imports

import static dbws.testing.shadowddlgeneration.oldjpub.Util.SCHEMA_IF_NEEDED;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.printTypeWithLength;

public class SqlType extends TypeClass {

    // The following type constants are used by the database
    // to distinguish the various Opaque and SQLJ Object type
    // flavours

    public static final int CODE_OPAQUE = 58;
    public static final int CODE_SQLJTYPE = 108;

    public static final int SQLJTYPE_SQLDATA = 1;
    public static final int SQLJTYPE_CUSTOMDATUM = 2;
    public static final int SQLJTYPE_SERIALIZABLE = 3;
    public static final int SQLJTYPE_ORADATA = 5;
    public static final int SQLJTYPE_BOTH = 6;
    public static final int SQLJTYPE_BOTH8I = 7;

    // The following type constants are internal to JDBC
    public static final int ORACLE_TYPES_NCHAR = -72054;
    public static final int ORACLE_TYPES_NCLOB = -72055;
    public static final int ORACLE_TYPES_BOOLEAN = -72056;
    public static final int ORACLE_TYPES_TBD = -72057;

    protected String m_version;
    protected SqlReflector m_reflector; // null for predefined types
    protected ViewCache m_viewCache;
    protected SqlType m_parentType;
    protected boolean m_isReused;

    public SqlName getSqlName() {
        return (SqlName)m_name;
    }

    public boolean isRef() {
        return getTypecode() == OracleTypes.REF;
    }

    public boolean isCollection() {
        return (getTypecode() == OracleTypes.ARRAY || getTypecode() == OracleTypes.TABLE || isPlsqlTable());
    }

    public boolean isPlsqlTable() {
        return (getTypecode() == OracleTypes.PLSQL_INDEX_TABLE
            || getTypecode() == OracleTypes.PLSQL_NESTED_TABLE || getTypecode() == OracleTypes.PLSQL_VARRAY_TABLE);
    }

    public boolean isPlsqlRecord() {
        return (getTypecode() == OracleTypes.PLSQL_RECORD);
    }

    public boolean isOpaque() {
        return getTypecode() == OracleTypes.OPAQUE;
    }

    public boolean isJavaStruct() {
        return getTypecode() == OracleTypes.JAVA_STRUCT;
    }

    public boolean isSqlStatement() {
        return false;
    }

    public int getSqljKind() {
        return 0;
    }

    public boolean isStruct() {
        return getTypecode() == OracleTypes.STRUCT;
    }

    /**
     * Returns the version string of a SqlType.
     *
     * * @return the version string of the type.
     */
    public String getVersion() {
        return m_version;
    }

    /**
     * Set the version string of a declared type.
     *
     * * @param version the version string of the type
     */
    public void setVersion(String version) {
        m_version = version;
    }

    /**
     * Get the attribute hashtable associated with a SqlType. The attributes in the hashtable were
     * registered via addAttribute().
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getAttributes() {
        return (Map<String, String>)getAnnotation();
    }

    /**
     * Add an attribute to the collection of attributes of a SqlType. JPub only adds attributes
     * mentioned in the input file.
     */
    public void addAttribute(String sqlField, String javaField) {
        // TODO: check for duplicate Java field names!
        String dbField = m_viewCache.dbifyName(sqlField);
        if (javaField == null) {
            javaField = sqlField;
        }
        @SuppressWarnings("unchecked")
        Map<String, String> h = (Map<String, String>)getAnnotation();
        List<String> v = getNamedTranslations();
        if (h == null) {
            h = new HashMap<>();
            setAnnotation(h);
        }
        if (v == null) {
            v = new ArrayList<>();
            setNamedTranslations(v);
        }
        Object old = h.put(dbField, javaField);
        v.add(sqlField);
        if (old != null) {
            throw new IllegalArgumentException("Redeclaration of field " + dbField + " in " + this
                + "!");
        }
    }

    /**
     * SqlType CONSTRUCTORS
     */
    /**
     * This constructor is used for user-defined and REF types. It may not be called more than once
     * for the same non-null sqlName.
     */
    @SuppressWarnings("this-escape")
    protected SqlType(SqlName sqlName, int typecode, boolean generateMe, SqlType parentType,
        SqlReflector reflector) {
        this(sqlName, typecode, generateMe, false, parentType, reflector);
        m_isReused = false;
    }

    @SuppressWarnings("this-escape")
    SqlType(SqlName sqlName, int typecode, boolean generateMe, boolean isPrimitive,
        SqlType parentType, SqlReflector reflector) {
        super(sqlName, typecode, isPrimitive);
        m_isReused = false;
        m_reflector = reflector;
        if (m_reflector != null) {
            m_viewCache = m_reflector.getViewCache();
        }
        if (sqlName != null) { // For SqlRefType, sqlName==null
            if (m_reflector != null) {
                m_reflector.addType(sqlName, this, generateMe);
            }
        }
        m_parentType = parentType;
    }

    /**
     * This constructor is used for predefined SQL types. It may not be called more than once for
     * the same name.
     */
    public SqlType(String name, int typecode) {
        super(new SqlName(null, name, true, false, true, null, null, null, null/* reflector */),
            typecode, true);
    }

    /**
     * This constructor is used for predefined PL/SQL to SQL type mapping.
     */
    SqlType(SqlName name, int typecode) {
        super(name, typecode, true);
    }

    // Determine whether "this" type depends on "t"
    // For instance, if "t" is a field of "this",
    // or the element type of "this"
    private List<SqlType> m_dependTypes = null;

    boolean dependsOn(SqlType t) {
        if (m_dependTypes == null) {
            m_dependTypes = new ArrayList<>();
            if (isPlsqlRecord() || this instanceof DefaultArgsHolderType) {
                try {
                    List<AttributeField> fields = getDeclaredFields(true);
                    for (AttributeField field : fields) {
                        SqlType st = (SqlType) field.getType();
                        if (st.isPlsqlRecord() || st.isPlsqlTable()) {
                            m_dependTypes.add(st);
                        }
                    }
                }
                catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            else if (this instanceof PlsqlTableType) { // to fend off PlsqlIndexTableType
                try {
                    SqlType st = (SqlType)((PlsqlTableType)this).getComponentType();
                    if (st.isPlsqlRecord() || st.isPlsqlTable()) {
                        m_dependTypes.add(st);
                    }
                }
                catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        if (m_dependTypes.contains(t)) {
            return true;
        }
        for (SqlType mDependType : m_dependTypes) {
            if (mDependType.dependsOn(t)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasConversion() {
        // For SqlRefType, getSqlName()==null
        if (getSqlName() == null) {
            return false;
        }
        return getSqlName().hasConversion();
    }

    /**
     * Reports the SQL type into which the PL/SQL can be converted. Returns null if not a PL/SQL
     * type or if it doed not have a user-defined conversion.
     */
    public String getTargetTypeName() {
        return getSqlName().getTargetTypeName();
    }

    public String getTargetTypeName(int schemaNames) {
        return getSqlName().getFullTargetTypeName(schemaNames);
    }

    public String getTypeName() {
        return getSqlName().getTypeName();
    }

    /**
     * Returns the PL/SQL function to be used for converting this PL/SQL into a SQL type.
     * <br>
     * Returns null if this is not a PL/SQL type or if it does not have user-defined conversions.
     */
    //static Hashtable m_convFuns = new Hashtable();

    @Override
    public String getOutOfConversion() {
        // For SqlRefType, getSqlName()==null
        if (getSqlName() == null) {
            return null;
        }
        return getSqlName().getOutOfConversion();
    }

    /**
     * Returns the PL/SQL function to be used for converting a SQL type into this PL/SQL type.
     * <br>
     * Returns null if this is not a PL/SQL type or if it does not have user-defined conversions.
     */
    @Override
    public String getIntoConversion() {
        // For SqlRefType, getSqlName()==null
        if (getSqlName() == null) {
            return null;
        }
        return getSqlName().getIntoConversion();
    }

    // Return the conversion function name
    // prefixed with package name,
    // if the conversion function is a generated one
    @Override
    public String getOutOfConversionQualified() {
        // For SqlRefType, getSqlName()==null
        if (getSqlName() == null) {
            return null;
        }
        return getSqlName().getOutOfConversionQualified();
    }

    // Return the conversion function name
    // prefixed with package name,
    // if the conversion function is a generated one
    @Override
    public String getIntoConversionQualified() {
        // For SqlRefType, getSqlName()==null
        if (getSqlName() == null) {
            return null;
        }
        return getSqlName().getIntoConversionQualified();
    }

    // SQL declaration of the generated SQL types
    public String getSqlTypeDecl() throws SQLException, PublisherException {
        StringBuilder sqlTypeDecl = new StringBuilder();
        if (isPlsqlRecord() && !getSqlName().isReused()) {
            sqlTypeDecl.append("CREATE OR REPLACE TYPE ").append(getTargetTypeName()).append(" AS OBJECT (\n");
            List<AttributeField> fields = this.getFields(true);
            for (int i = 0; i < fields.size(); i++) {
                if (i != 0) {
                    sqlTypeDecl.append(",\n");
                }
                sqlTypeDecl.append("      ").append(Util.unreserveSql(fields.get(i).getName())).append(" ");
                sqlTypeDecl.append(fields.get(i).printTypeWithLength(SCHEMA_IF_NEEDED));
            }
            sqlTypeDecl.append("\n);");
        }
        else if (isPlsqlTable() && !getSqlName().isReused()) {
            PlsqlTableType plType = (PlsqlTableType)this;
            SqlType eleType = (SqlType)plType.getComponentType();
            sqlTypeDecl.append("CREATE OR REPLACE TYPE ").append(getTargetTypeName());
            sqlTypeDecl.append(" AS TABLE OF ");
            sqlTypeDecl.append(printTypeWithLength(eleType.getTargetTypeName(SCHEMA_IF_NEEDED), plType
                    .getElemTypeLength(), plType.getElemTypePrecision(), plType.getElemTypeScale()));
            sqlTypeDecl.append(";");
        }
        else if ((this instanceof DefaultArgsHolderType holder) && !getSqlName().isReused()) {
            sqlTypeDecl.append("CREATE OR REPLACE TYPE ").append(getTypeName()).append(" AS OBJECT (\n");
            AttributeField vfield = holder.getFields(false).get(0);
            sqlTypeDecl.append("      ").append(vfield.getName()).append(" ");
            sqlTypeDecl.append(vfield.printTypeWithLength());
            sqlTypeDecl.append("\n);");
        }
        return sqlTypeDecl.toString();
    }

    // Drop the generated SQL type
    public String getSqlTypeDrop() throws SQLException, PublisherException {
        //return getSqlName().isReused() ? "" : "DROP TYPE " + getTargetTypeName() + " FORCE; \n"
        //    + "show errors\n";
        return "DROP TYPE " + getTargetTypeName() + " FORCE;";
    }

    // PL/SQL package declaration for conversion functions
    // between SQL and PL/SQL
    public String getConversionFunDecl() throws PublisherException, SQLException {
        if (!hasConversion()) {
            return "";
        }
        StringBuilder sqlConvPkgDecl = new StringBuilder();

        // If this type is a ROWTYPE, then redefine it as a normal PL/SQL record
        if (getSqlName().isRowType()) {
            sqlConvPkgDecl.append("\t-- Redefine a PL/SQL RECORD type originally defined via CURSOR%ROWTYPE" + "\n");
            sqlConvPkgDecl.append("\tTYPE ").append(getTypeName()).append(" IS RECORD (\n");
            List<AttributeField> fields = this.getFields(true);
            for (int i = 0; i < fields.size(); i++) {
                if (i != 0) {
                    sqlConvPkgDecl.append(",\n");
                }
                sqlConvPkgDecl.append("\t\t").append(fields.get(i).getName()).append(" ").append(fields.get(i).printTypeWithLength());
            }
            sqlConvPkgDecl.append(");");
        }

        // PL/SQL to SQL
        sqlConvPkgDecl.append("\t-- Declare the conversion functions the PL/SQL type ").append(getTypeName()).append("\n").append("\tFUNCTION ").append(getOutOfConversion()).append("(aPlsqlItem ").append(getTypeName()).append(")\n").append(" \tRETURN ").append(getTargetTypeName()).append(";\n");

        // SQL to PL/SQL
        sqlConvPkgDecl.append("\tFUNCTION ").append(getIntoConversion()).append("(aSqlItem ").append(getTargetTypeName()).append(")\n").append("\tRETURN ").append(getTypeName()).append(";");
        return sqlConvPkgDecl.toString();
    }

    // PL/SQL package body for conversion functions between SQL and PL/SQL
    public String getConversionPL2SQLFunBody() throws SQLException, PublisherException {
        if (!hasConversion()) {
            return "";
        }
        // PL/SQL to SQL
        String sqlConvPkgBody = "\tFUNCTION " + getOutOfConversion() + "(aPlsqlItem "
            + getTypeName() + ")\n " + "\tRETURN " + getTargetTypeName() + " IS \n" + "\taSqlItem "
            + getTargetTypeName() + "; \n" + "\tBEGIN \n"
            + getOutOfConvStmts("\t\t", "aPlsqlItem", "aSqlItem") + "\t\tRETURN aSqlItem;\n"
            + "\tEND " + getOutOfConversion() + ";\n";
        return sqlConvPkgBody;
    }

    // PL/SQL package body for conversion functions between SQL and PL/SQL
    public String getConversionSQL2PLFunBody() throws SQLException, PublisherException {
        if (!hasConversion()) {
            return "";
        }
        // SQL to PL/SQL
        String sqlConvPkgBody = "\tFUNCTION " + getIntoConversion() + "(aSqlItem "
            + getTargetTypeName() + ") \n" + "\tRETURN " + getTypeName() + " IS \n"
            + "\taPlsqlItem " + getTypeName() + "; \n" + "\tBEGIN \n"
            + getIntoConvStmts("\t\t", "aSqlItem", "aPlsqlItem") + "\t\tRETURN aPlsqlItem;\n"
            + "\tEND " + getIntoConversion() + ";\n";
        return sqlConvPkgBody;
    }

    // both conversion functions
    public String getBothConversions() throws SQLException, PublisherException {
        return getConversionSQL2PLFunBody() + getConversionPL2SQLFunBody();
    }

    // Return assignment statements from SQL objects to PL/SQL records
    public String getIntoConvStmts(String formatPrefix, String maybeSql, String maybePlsql)
        throws SQLException, PublisherException {
        if (!isPlsqlRecord() && !isPlsqlTable()) {
            return formatPrefix + maybePlsql + "." + getName() + " := " + maybeSql + getName()
                + ";\n";
        }
        StringBuilder stmts = new StringBuilder();
        if (isPlsqlRecord()) {
            List<AttributeField> fields = getFields(true);
            for (int i = 0; i < java.lang.reflect.Array.getLength(fields); i++) {
                AttributeField af = fields.get(i);
                stmts.append(formatPrefix).append(maybePlsql).append(".").append(af.getName()).append(" := ").append((af.getType().hasConversion() && af.getType()
                        .getIntoConversion() != null) ? af.getType().getIntoConversion()
                        + "(" + maybeSql + "." + Util.unreserveSql(af.getName()) + ");\n"
                        : maybeSql + "." + Util.unreserveSql(af.getName()) + ";\n");
            }
        }
        else { // if (isPlsqlTable())
            TypeClass compType = ((PlsqlTableType)this).getComponentType();
            if (getTypecode() == OracleTypes.PLSQL_NESTED_TABLE
                || getTypecode() == OracleTypes.PLSQL_VARRAY_TABLE) {
                stmts.append(formatPrefix).append(maybePlsql).append(" := ").append(getTypeName()).append("();\n");
                stmts.append(formatPrefix).append(maybePlsql).append(".EXTEND").append("(").append(maybeSql).append(".COUNT);\n");
            }
            stmts.append(formatPrefix).append("IF ").append(maybeSql).append(".COUNT>0 THEN\n").append(formatPrefix).append("FOR I IN 1..").append(maybeSql).append(".COUNT LOOP\n").append(formatPrefix).append("\t").append(maybePlsql).append("(I)").append(" := ").append((compType.hasConversion() && compType.getIntoConversion() != null) ? compType
                    .getIntoConversion()
                    + "(" + maybeSql + "(I)" + ");\n" : maybeSql + "(I);\n").append(formatPrefix).append("END LOOP; \n").append(formatPrefix).append("END IF;\n");
        }
        return stmts.toString();
    }

    // Return assignment statements from PL/SQL records to SQL objects
    public String getOutOfConvStmts(String formatPrefix, String maybePlsql, String maybeSql)
        throws SQLException, PublisherException {
        if (!isPlsqlRecord() && !isPlsqlTable()) {
            return formatPrefix + maybeSql + " := " + maybePlsql + ";\n";
        }
        StringBuilder stmts = new StringBuilder();
        if (isPlsqlRecord()) {
            List<AttributeField> fields = getFields(true);
            stmts.append(formatPrefix).append(maybeSql).append(" := ").append(getTargetTypeName()).append("(NULL");
            for (int i = 1; i < fields.size(); i++) {
                stmts.append(", NULL");
            }
            stmts.append(");\n");

            for (AttributeField af : fields) {
                stmts.append(formatPrefix).append(maybeSql).append(".").append(Util.unreserveSql(af.getName())).append(" := ").append((af.getType().hasConversion() && af.getType()
                        .getOutOfConversion() != null) ? af.getType().getOutOfConversion()
                        + "(" + maybePlsql + "." + af.getName() + ");\n" : maybePlsql + "."
                        + af.getName() + ";\n");
            }
        }
        else { // if (isPlsqlTable())
            TypeClass compType = ((PlsqlTableType)this).getComponentType();
            stmts.append(formatPrefix).append(maybeSql).append(" := ").append(getTargetTypeName()).append("();\n").append(formatPrefix).append(maybeSql).append(".EXTEND(").append(maybePlsql).append(".COUNT);\n").append(formatPrefix).append("IF ").append(maybePlsql).append(".COUNT>0 THEN\n").append(formatPrefix).append("FOR I IN ").append(maybePlsql).append(".FIRST..").append(maybePlsql).append(".LAST LOOP\n").append(formatPrefix).append("\t").append(maybeSql).append("(I + 1 - ").append(maybePlsql).append(".FIRST)").append(" := ").append((compType.hasConversion() || compType.getOutOfConversion() != null) ? compType
                    .getOutOfConversion()
                    + "(" + maybePlsql + "(I)" + ");\n" : maybePlsql + "(I);\n").append(formatPrefix).append("END LOOP; \n").append(formatPrefix).append("END IF; \n");
        }
        return stmts.toString();
    }
}
