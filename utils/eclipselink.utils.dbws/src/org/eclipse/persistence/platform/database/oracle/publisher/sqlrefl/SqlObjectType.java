package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import java.util.Iterator;
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.AllTypes;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.FieldInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.MethodInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ParamInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ResultInfo;

@SuppressWarnings("unchecked")
public class SqlObjectType extends SqlTypeWithMethods {

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
    public SqlObjectType(SqlName sqlName, Field[] fields, SqlReflector reflector)
        throws SQLException {
        super(sqlName, OracleTypes.STRUCT, true, null, null, reflector);
        m_modifiers = Modifier.PUBLIC;
        m_fields = fields;
        m_fieldsPublishedOnly = fields;
        m_methods = new Method[]{};
    }

    public void setFields(Field[] fields) {
        m_fields = fields;
        m_fieldsPublishedOnly = fields;
    }

    /*
     * END used by oracle.j2ee.ws.db.genproxy.PlsqlProxy
     */

    // Type filters and Mode filters is enabled
    // protected boolean acceptMethod(Method method) { return true; }
    public Type getSupertype() throws SQLException, PublisherException {
        if (m_supertypeKnown) {
            return m_supertype;
        }

        m_supertypeKnown = true;

        SqlName name = (SqlName)getNameObject();
        String schemaName = name.getSchemaName();
        String typeName = name.getTypeName();

        Iterator iter = m_viewCache.getRows("ALL_TYPES", new String[0], new String[]{"OWNER",
            "TYPE_NAME"}, new Object[]{schemaName, typeName}, new String[0]);
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

            Iterator iter = m_viewCache.getRows("ALL_TYPES", new String[0], new String[]{"OWNER",
                "TYPE_NAME"}, new Object[]{schemaName, typeName}, new String[0]);
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
                    m_modifiers += Modifier.INCOMPLETE;
                }
            }
        }
        return m_modifiers;
    }

    /**
     * Returns an array of Field objects reflecting all the accessible fields of this Type object.
     * Returns an array of length 0 if this Type object has no accesible fields.
     */
    public Field[] getFields(boolean publishedOnly) throws SecurityException,
        java.sql.SQLException, PublisherException {
        return getFields(0, publishedOnly);
    }

    protected Field[] getFields(int subtypeFieldCount, boolean publishedOnly)
        throws SecurityException, java.sql.SQLException, PublisherException {
        Field[] declaredFields = getDeclaredFields(publishedOnly);
        SqlObjectType supertype = (SqlObjectType)getSupertype();

        if (supertype == null && subtypeFieldCount == 0) {
            return declaredFields;
        }

        int declaredFieldCount = declaredFields.length;
        int nonSuperFieldCount = declaredFieldCount + subtypeFieldCount;

        Field[] fields = supertype == null ? new Field[nonSuperFieldCount] : supertype.getFields(
            nonSuperFieldCount, publishedOnly);

        int fieldsX = fields.length - nonSuperFieldCount;
        for (int i = 0; i < declaredFieldCount; i++) {
            fields[fieldsX++] = declaredFields[i];
        }

        return fields;
    }

    protected FieldInfo[] getFieldInfo() throws SQLException {
        SqlName sqlName = getSqlName();
        String schema = sqlName.getSchemaName();
        String type = sqlName.getTypeName();

        Iterator iter = m_viewCache.getRows(Util.ALL_TYPE_ATTRS, new String[0], new String[]{
            "OWNER", Util.TYPE_NAME, "INHERITED"}, new Object[]{schema, type, "NO"},
            new String[]{"ATTR_NO"});
        return FieldInfo.getFieldInfo(iter);
    }

    protected MethodInfo[] getMethodInfo(String schema, String name) throws SQLException {
        Iterator iter = null;
        if (m_reflector.geqOracle9()) {
            iter = m_viewCache.getRows("ALL_TYPE_METHODS", new String[0], new String[]{"OWNER",
                "TYPE_NAME", "INSTANTIABLE"/* , "INHERITED" */},
                new String[]{schema, name, "YES"/* , "NO" */}, new String[]{"METHOD_NAME",
                    "METHOD_NO"});
        }
        else {
            iter = m_viewCache.getRows("ALL_TYPE_METHODS", new String[0], new String[]{"OWNER",
                "TYPE_NAME"}, new String[]{schema, name}, new String[]{"METHOD_NAME"});
        }
        return MethodInfo.getMethodInfo(iter);
    }

    protected ResultInfo getResultInfo(String schema, String name, String method, String methodNo)
        throws SQLException {
        Iterator iter = m_viewCache
            .getRows("ALL_METHOD_RESULTS", new String[0], new String[]{"OWNER", "TYPE_NAME",
                "METHOD_NO"}, new Object[]{schema, name, methodNo}, new String[0]);
        return ResultInfo.getResultInfo(iter);
    }

    protected ParamInfo[] getParamInfo(String schema, String name, String method, String methodNo)
        throws SQLException {
        Iterator iter = m_viewCache.getRows("ALL_METHOD_PARAMS", new String[0], new String[]{
            "OWNER", "TYPE_NAME", "METHOD_NO"}, new Object[]{schema, name, methodNo},
            new String[]{"PARAM_NO"});
        return ParamInfo.getParamInfo(iter);
    }

    SqlType m_supertype = null;
    boolean m_supertypeKnown = false;
    boolean m_isFinal = true;
    int m_modifiers = 0;
}
