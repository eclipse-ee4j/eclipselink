package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import org.eclipse.persistence.platform.database.oracle.publisher.MethodFilter;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.AllSynonyms;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.FieldInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.MethodInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ParamInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ResultInfo;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.SingleColumnViewRow;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.UserArguments;
import org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ViewCache;

@SuppressWarnings("unchecked")
public class SqlPackageType extends SqlTypeWithMethods {
    public boolean isPackage() {
        return true;
    }

    public SqlPackageType(SqlName sqlName, SqlType parentType, MethodFilter signatureFilter,
        SqlReflectorImpl reflector) throws SQLException {
        super(sqlName, OracleTypes.PACKAGE, true, parentType, signatureFilter, reflector);
        initSecurityAttributes(sqlName);
    }

    // No database reflection. Currently used by oracle.j2ee.ws.db.genproxy.PlsqlProxy.
    public SqlPackageType(SqlName sqlName, Method[] methods, SqlReflectorImpl reflector)
        throws SQLException {
        super(sqlName, OracleTypes.PACKAGE, true, null, null, reflector);
        m_methods = methods;
        initSecurityAttributes(sqlName);
    }

    protected FieldInfo[] getFieldInfo() {
        return null;
    }

    public String getSynonymName() {
        return m_synonymName;
    }

    public String[] getExecuteNames() {
        return m_executeNames;
    }

    public boolean hasSecurityAttributes() {
        return m_synonymName != null || m_executeNames.length > 0;
    }

    protected void initSecurityAttributes(SqlName sqlName) throws SQLException {
        String owner = sqlName.getContextName();
        String name = sqlName.getTypeName();
        // ("SELECT OWNER, SYNONYM_NAME FROM ALL_SYNONYMS WHERE TABLE_OWNER=:1 AND TABLE_NAME=:2");
        Iterator iter = m_viewCache.getRows(Util.ALL_SYNONYMS, new String[0], new String[]{
            Util.TABLE_OWNER, Util.TABLE_NAME}, new Object[]{owner, name}, new String[0]);
        if (iter.hasNext()) {
            AllSynonyms row = (AllSynonyms)iter.next();
            m_synonymOwner = row.OWNER;
            m_synonymName = row.SYNONYM_NAME;
        }
        // ("SELECT GRANTEE FROM ALL_TAB_PRIVS WHERE TABLE_SCHEMA=:1 AND TABLE_NAME=:2 AND PRIVILEGE='EXECUTE'");
        iter = m_viewCache.getRows(Util.ALL_TAB_PRIVS, new String[]{Util.GRANTEE}, new String[]{
            Util.TABLE_SCHEMA, Util.TABLE_NAME, Util.PRIVILEGE}, new Object[]{owner, name,
            "EXECUTE"}, new String[0]);
        ArrayList al = new ArrayList();
        while (iter.hasNext()) {
            SingleColumnViewRow row = (SingleColumnViewRow)iter.next();
            al.add(row.getValue());
        }
        m_executeNames = (String[])al.toArray(new String[0]);
    }

    public String[] getSecurityDeclarations() {
        String grant = "";
        String revoke = "";
        String schema = ((SqlName)m_name).getSchemaName();
        String wrapperPkg = "";
        if (getSynonymName() != null && schema != null && schema.length() > 0) {
            grant += "CREATE OR REPLACE PUBLIC SYNONYM " + wrapperPkg + " for " + schema + "."
                + wrapperPkg + ";\n";
            grant += "/\nshow errors\n";
            revoke += "DROP PUBLIC synonym " + wrapperPkg + ";\n";
            revoke += "show errors\n";
        }
        String[] names = getExecuteNames();
        if (names != null && names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                grant += "GRANT EXECUTE on " + schema + "." + wrapperPkg + " to " + names[i]
                    + ";\n";
                grant += "show errors\n";
                revoke += "REVOKE EXECUTE on " + schema + "." + wrapperPkg + " from " + names[i]
                    + ";\n";
                revoke += "show errors\n";
            }
        }
        return new String[]{grant, revoke};
    }

    protected String m_synonymOwner;
    protected String m_synonymName;
    protected String[] m_executeNames;

    protected MethodInfo[] getMethodInfo(String schema, String name) throws SQLException {
        /*
         * POSITION of Nth argument is N SEQUENCE of Nth argument is >= N POSITION of function
         * result is 0 SEQUENCE of function result is 1 Special case: If there are no arguments or
         * function results, a row appears anyway, with POSITION=1, SEQUENCE=0.
         * 
         * All of which explains the rather strange query below. #sql smi = {SELECT OBJECT_NAME AS
         * METHOD_NAME, OVERLOAD AS METHOD_NO, 'PUBLIC' AS METHOD_TYPE, NVL(MAX(DECODE(SEQUENCE, 0,
         * 0, POSITION)), 0) AS PARAMETERS, NVL(MAX(1-POSITION), 0) AS RESULTS FROM ALL_ARGUMENTS
         * WHERE OWNER = :schema AND PACKAGE_NAME = :name AND DATA_LEVEL = 0 group by OBJECT_NAME,
         * OVERLOAD};
         */
        String[] keys = null;
        Object[] values = null;
        if (m_methodFilter != null && m_methodFilter.isSingleMethod()) {
            keys = new String[]{Util.OWNER, Util.PACKAGE_NAME, Util.OBJECT_NAME, Util.DATA_LEVEL};
            values = new Object[]{schema, name, m_methodFilter.getSingleMethodName(),
                new Integer(0)};
        }
        else {
            keys = new String[]{Util.OWNER, Util.PACKAGE_NAME, Util.DATA_LEVEL};
            values = new Object[]{schema, name, new Integer(0)};
        }
        Iterator iter = m_viewCache.getRows(Util.ALL_ARGUMENTS, new String[0], keys, values,
            new String[0]);
        MethodInfo[] minfo = MethodInfo.groupBy(iter);
        return minfo;
    }

    protected ResultInfo getResultInfo(String schema, String name, String methodName,
        String methodNo) throws SQLException {
        if (m_resultInfoValues == null || !m_resultInfoValues.matches(schema, name)) {
            m_resultInfoValues = new ResultInfoValues(schema, name, m_methodFilter, m_viewCache);
        }
        return m_resultInfoValues.get(methodName, methodNo);
    }

    private ResultInfoValues m_resultInfoValues;

    private static class ResultInfoValues extends InfoValues {
        public ResultInfoValues(String schema, String name, MethodFilter methodFilter,
            ViewCache viewCache) throws SQLException {
            super(schema, name);
            String[] keys = null;
            Object[] values = null;
            if (methodFilter != null && methodFilter.isSingleMethod()) {
                keys = new String[]{Util.OWNER, Util.PACKAGE_NAME, Util.OBJECT_NAME,
                    Util.DATA_LEVEL, Util.POSITION};
                values = new Object[]{schema, name, methodFilter.getSingleMethodName(),
                    new Integer(0), new Integer(0)};
            }
            else {
                keys = new String[]{Util.OWNER, Util.PACKAGE_NAME, Util.DATA_LEVEL, Util.POSITION};
                values = new Object[]{schema, name, new Integer(0), new Integer(0)};
            }

            Iterator iter = viewCache.getRows(Util.ALL_ARGUMENTS, new String[0], keys, values,
                new String[0]);
            while (iter.hasNext()) {
                UserArguments item = (UserArguments)iter.next();
                String key = makeKey(item.OBJECT_NAME /* METHOD_NAME */, item.OVERLOAD/* METHOD_NO */);
                if (m_ht.get(key) == null) {
                    m_ht.put(key, item);
                }
            }
        }

        public ResultInfo get(String method_name, String method_no) throws java.sql.SQLException {
            Object row = m_ht.get(makeKey(method_name, method_no));
            ResultInfo rinfo = null;
            rinfo = new ResultInfo((UserArguments)row);
            return rinfo;
        }
    }

    protected ParamInfo[] getParamInfo(String schema, String name, String methodName,
        String methodNo) throws SQLException {
        if (m_paramInfoValues == null || !m_paramInfoValues.matches(schema, name)) {
            m_paramInfoValues = new ParamInfoValues(schema, name, m_methodFilter, m_viewCache);
        }
        return m_paramInfoValues.get(methodName, methodNo);
    }

    private ParamInfoValues m_paramInfoValues;

    private static class ParamInfoValues extends InfoValues {
        public ParamInfoValues(String schema, String name, MethodFilter methodFilter,
            ViewCache viewCache) throws SQLException {
            super(schema, name);
            String[] keys = null;
            Object[] values = null;
            if (methodFilter != null && methodFilter.isSingleMethod()) {
                keys = new String[]{Util.OWNER, Util.PACKAGE_NAME, Util.OBJECT_NAME,
                    Util.DATA_LEVEL};
                values = new Object[]{schema, name, methodFilter.getSingleMethodName(),
                    new Integer(0)};
            }
            else {
                keys = new String[]{Util.OWNER, Util.PACKAGE_NAME, Util.DATA_LEVEL};
                values = new Object[]{schema, name, new Integer(0)};
            }
            Iterator iter = viewCache.getRows(Util.ALL_ARGUMENTS, new String[0], keys, values,
                new String[0]);
            ArrayList viewRows = new ArrayList();
            while (iter.hasNext()) {
                UserArguments item = (UserArguments)iter.next();
                if (item.ARGUMENT_NAME != null) {
                    viewRows.add(item);
                }
            }
            UserArguments.orderByPosition(viewRows);
            for (int i = 0; i < viewRows.size(); i++) {
                UserArguments item = (UserArguments)viewRows.get(i);
                String key = makeKey(item.OBJECT_NAME/* METHOD_NAME */, item.OVERLOAD/* METHOD_NO */);
                ArrayList v = (ArrayList)m_ht.get(key);
                if (v == null) {
                    v = new ArrayList();
                    m_ht.put(key, v);
                }
                v.add(item);
            }
        }

        public ParamInfo[] get(String method, String method_no) throws java.sql.SQLException {
            ArrayList v = (ArrayList)m_ht.get(makeKey(method, method_no));
            if (v == null) {
                v = new ArrayList(); // zero parameter
            }
            return ParamInfo.getParamInfo(v);
        }
    }

    private static class InfoValues {
        public InfoValues(String schema, String name) throws SQLException {
            m_schema = schema;
            m_name = name;
            m_ht = new Hashtable();
        }

        protected static String makeKey(String method, String method_no) {
            return "" + method + "/" + method_no;
        }

        public boolean matches(String schema, String name) {
            return ((schema == null) ? m_schema == null : schema.equals(m_schema))
                && ((name == null) ? m_name != null : name.equals(m_name));
        }

        protected String m_schema;
        protected String m_name;
        protected Hashtable m_ht;
    }
}
