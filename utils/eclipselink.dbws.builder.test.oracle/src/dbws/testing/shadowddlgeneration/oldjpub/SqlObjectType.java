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
package  dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.PublisherException;
import dbws.testing.shadowddlgeneration.oldjpub.AllTypes;
import dbws.testing.shadowddlgeneration.oldjpub.FieldInfo;
import dbws.testing.shadowddlgeneration.oldjpub.MethodInfo;
import dbws.testing.shadowddlgeneration.oldjpub.ParamInfo;
import dbws.testing.shadowddlgeneration.oldjpub.ResultInfo;
import dbws.testing.shadowddlgeneration.oldjpub.ViewRow;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_METHOD_PARAMS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_TYPES;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_TYPE_ATTRS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_TYPE_METHODS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OWNER;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.TYPE_NAME;

public class SqlObjectType extends SqlTypeWithMethods {

    protected SqlType m_supertype = null;
    protected boolean m_supertypeKnown = false;
    protected boolean m_isFinal = true;
    protected int m_modifiers = 0;

    public SqlObjectType(SqlName sqlName, boolean generateMe, SqlType parentType,
        SqlReflector reflector) throws SQLException {
        this(sqlName, OracleTypes.STRUCT, generateMe, parentType, reflector);
    }

    public SqlObjectType(SqlName sqlName, int typecode, boolean generateMe, SqlType parentType,
        SqlReflector reflector) throws SQLException {
        super(sqlName, typecode, generateMe, parentType, null, reflector);
    }

    /*
     * BEGIN No database reflection. Currently used by oracle.j2ee.ws.db.genproxy.PlsqlProxy.
     */
    public SqlObjectType(SqlName sqlName, List<AttributeField> fields, SqlReflector reflector)
        throws SQLException {
        super(sqlName, OracleTypes.STRUCT, true, null, null, reflector);
        m_modifiers = Modifier.PUBLIC;
        m_fields = fields;
        m_fieldsPublishedOnly = fields;
        m_methods = new ArrayList<ProcedureMethod>();
    }

    /**
     * Determines if this Type represents an object type.
     * <p/>
     */
    public boolean isObject() {
        // special case BOOLEAN - not really a SqlObjectType
        if ("BOOLEAN".equals(m_name.getSimpleName())) {
            return false;
        }
        else {
            return true;
        }
    }

    public void setFields(List<AttributeField> fields) {
        m_fields = fields;
        m_fieldsPublishedOnly = fields;
    }

    // Type filters and Mode filters is enabled
    // protected boolean acceptMethod(Method method) { return true; }
    public TypeClass getSupertype() throws SQLException, PublisherException {
        if (m_supertypeKnown) {
            return m_supertype;
        }

        m_supertypeKnown = true;

        SqlName name = (SqlName)getNameObject();
        String schemaName = name.getSchemaName();
        String typeName = name.getTypeName();

        Iterator<ViewRow> iter = m_viewCache.getRows(ALL_TYPES, new String[0], new String[]{OWNER,
            TYPE_NAME}, new Object[]{schemaName, typeName}, new String[0]);
        if (iter.hasNext()) {
            AllTypes row = (AllTypes)iter.next();
            String supertypeName = row.supertypeName;
            String supertypeOwner = row.supertypeOwner;
            if (supertypeName != null) {
                m_supertype = m_reflector.addSqlDBType(supertypeOwner, supertypeName, null, "",
                    false, this);
            }
        }
        return m_supertype;
    }

    /**
     * Returns the modifiers for this type, encoded in an integer. The modifiers currently in use
     * are: public (always set) final abstract incomplete The modifiers are decoded using the
     * methods of java.lang.reflect.Modifier. If we ever need additional modifiers for C++, we can
     * subclass this.
     */
    public int getModifiers() throws SQLException {
        if (m_modifiers == 0) {
            m_modifiers = Modifier.PUBLIC;

            SqlName name = (SqlName)getNameObject();
            String schemaName = name.getSchemaName();
            String typeName = name.getTypeName();

            Iterator<ViewRow> iter = m_viewCache.getRows(ALL_TYPES, new String[0], new String[]{
                OWNER, TYPE_NAME}, new Object[]{schemaName, typeName}, new String[0]);
            if (iter.hasNext()) {
                AllTypes fi = (AllTypes)iter.next();
                String isFinal = fi.finalProp;
                if (isFinal.equals("YES")) {
                    m_modifiers += Modifier.FINAL;
                }

                String isInstantiable = fi.instantiable;
                if (isInstantiable.equals("NO")) {
                    m_modifiers += Modifier.ABSTRACT;
                }

                String isIncomplete = fi.incomplete;
                if (isIncomplete.equals("YES")) {
                    m_modifiers += PublisherModifier.INCOMPLETE;
                }
            }
        }
        return m_modifiers;
    }

    /**
     * Returns an array of Field objects reflecting all the accessible fields of this Type object.
     * Returns an array of length 0 if this Type object has no accesible fields.
     */
    public List<AttributeField> getFields(boolean publishedOnly) throws SecurityException,
        SQLException, PublisherException {
        return getFields(0, publishedOnly);
    }

    protected List<AttributeField> getFields(int subtypeFieldCount, boolean publishedOnly)
        throws SecurityException, SQLException, PublisherException {
        List<AttributeField> declaredFields = getDeclaredFields(publishedOnly);
        SqlObjectType supertype = (SqlObjectType)getSupertype();

        if (supertype == null && subtypeFieldCount == 0) {
            return declaredFields;
        }

        int declaredFieldCount = declaredFields.size();
        int nonSuperFieldCount = declaredFieldCount + subtypeFieldCount;

        List<AttributeField> fields = supertype == null ?
            new ArrayList<AttributeField>(nonSuperFieldCount) :
            supertype.getFields(nonSuperFieldCount, publishedOnly);

        int fieldsX = fields.size() - nonSuperFieldCount;
        for (int i = 0; i < declaredFieldCount; i++) {
            fields.set(fieldsX++, declaredFields.get(i));
        }
        return fields;
    }

    protected List<FieldInfo> getFieldInfo() throws SQLException {
        SqlName sqlName = getSqlName();
        String schema = sqlName.getSchemaName();
        String type = sqlName.getTypeName();

        Iterator<ViewRow> iter = m_viewCache.getRows(ALL_TYPE_ATTRS, new String[0], new String[]{
            OWNER, TYPE_NAME, "INHERITED"}, new Object[]{schema, type, "NO"},
            new String[]{"ATTR_NO"});
        return FieldInfo.getFieldInfo(iter);
    }

    protected MethodInfo[] getMethodInfo(String schema, String name) throws SQLException {
        Iterator<ViewRow> iter = null;
        if (m_reflector.geqOracle9()) {
            iter = m_viewCache.getRows(ALL_TYPE_METHODS, new String[0], new String[]{OWNER,
                TYPE_NAME, "INSTANTIABLE"/* , "INHERITED" */},
                new String[]{schema, name, "YES"/* , "NO" */}, new String[]{"METHOD_NAME",
                    "METHOD_NO"});
        }
        else {
            iter = m_viewCache.getRows(ALL_TYPE_METHODS, new String[0], new String[]{OWNER,
                TYPE_NAME}, new String[]{schema, name}, new String[]{"METHOD_NAME"});
        }
        return MethodInfo.getMethodInfo(iter);
    }

    protected ResultInfo getResultInfo(String schema, String name, String method, String methodNo)
        throws SQLException {
        Iterator<ViewRow> iter = m_viewCache.getRows("ALL_METHOD_RESULTS", new String[0],
            new String[]{OWNER, TYPE_NAME, "METHOD_NO"}, new Object[]{schema, name, methodNo},
            new String[0]);
        return ResultInfo.getResultInfo(iter);
    }

    protected ParamInfo[] getParamInfo(String schema, String name, String method, String methodNo)
        throws SQLException {
        Iterator<ViewRow> iter = m_viewCache.getRows(ALL_METHOD_PARAMS, new String[0],
            new String[]{OWNER, TYPE_NAME, "METHOD_NO"}, new Object[]{schema, name, methodNo},
            new String[]{"PARAM_NO"});
        return ParamInfo.getParamInfo(iter);
    }

}