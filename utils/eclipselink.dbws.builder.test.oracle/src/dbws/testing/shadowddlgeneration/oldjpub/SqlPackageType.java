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
package  dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.MethodFilter;
import dbws.testing.shadowddlgeneration.oldjpub.Util;
import dbws.testing.shadowddlgeneration.oldjpub.FieldInfo;
import dbws.testing.shadowddlgeneration.oldjpub.MethodInfo;
import dbws.testing.shadowddlgeneration.oldjpub.ParamInfo;
import dbws.testing.shadowddlgeneration.oldjpub.ResultInfo;
import dbws.testing.shadowddlgeneration.oldjpub.UserArguments;
import dbws.testing.shadowddlgeneration.oldjpub.ViewCache;
import dbws.testing.shadowddlgeneration.oldjpub.ViewRow;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_ARGUMENTS;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.DATA_LEVEL;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OBJECT_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.OWNER;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.PACKAGE_NAME;
import static dbws.testing.shadowddlgeneration.oldjpub.Util.POSITION;

public class SqlPackageType extends SqlTypeWithMethods {

    protected String m_synonymOwner;
    protected String m_synonymName;
    protected String[] m_executeNames;
    protected ParamInfoValues m_paramInfoValues;

    public boolean isPackage() {
        return true;
    }

    public SqlPackageType(SqlName sqlName, SqlType parentType, MethodFilter signatureFilter,
        SqlReflector reflector) throws SQLException {
        super(sqlName, OracleTypes.PACKAGE, true, parentType, signatureFilter, reflector);
        //initSecurityAttributes(sqlName);
    }

    public SqlPackageType(SqlName sqlName, List<ProcedureMethod> methods, SqlReflector reflector)
        throws SQLException {
        super(sqlName, OracleTypes.PACKAGE, true, null, null, reflector);
        m_methods = methods;
        //initSecurityAttributes(sqlName);
    }

    protected List<FieldInfo> getFieldInfo() {
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

    /*
    protected void initSecurityAttributes(SqlName sqlName) throws SQLException {
        String owner = sqlName.getContextName();
        String name = sqlName.getTypeName();
        // ("SELECT OWNER, SYNONYM_NAME FROM ALL_SYNONYMS WHERE TABLE_OWNER=:1 AND TABLE_NAME=:2");
        Iterator<ViewRow> iter = m_viewCache.getRows(ALL_SYNONYMS, new String[0], new String[]{
            TABLE_OWNER, TABLE_NAME}, new Object[]{owner, name}, new String[0]);
        if (iter.hasNext()) {
            AllSynonyms row = (AllSynonyms)iter.next();
            m_synonymOwner = row.OWNER;
            m_synonymName = row.SYNONYM_NAME;
        }
        // ("SELECT GRANTEE FROM ALL_TAB_PRIVS WHERE TABLE_SCHEMA=:1 AND TABLE_NAME=:2 AND PRIVILEGE='EXECUTE'");
        iter = m_viewCache.getRows(ALL_TAB_PRIVS, new String[]{GRANTEE}, new String[]{TABLE_SCHEMA,
            TABLE_NAME, PRIVILEGE}, new Object[]{owner, name, "EXECUTE"}, new String[0]);
        ArrayList<String> al = new ArrayList<String>();
        while (iter.hasNext()) {
            SingleColumnViewRow row = (SingleColumnViewRow)iter.next();
            al.add(row.getValue());
        }
        m_executeNames = al.toArray(new String[al.size()]);
    }
    */

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
            keys = new String[]{OWNER, PACKAGE_NAME, OBJECT_NAME, DATA_LEVEL};
            values = new Object[]{schema, name, m_methodFilter.getSingleMethodName(),
                Integer.valueOf(0)};
        }
        else {
            keys = new String[]{OWNER, Util.PACKAGE_NAME, Util.DATA_LEVEL};
            values = new Object[]{schema, name, Integer.valueOf(0)};
        }
        Iterator<ViewRow> iter = m_viewCache.getRows(ALL_ARGUMENTS, new String[0], keys, values,
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
                keys = new String[]{OWNER, PACKAGE_NAME, OBJECT_NAME,
                    DATA_LEVEL, POSITION};
                values = new Object[]{schema, name, methodFilter.getSingleMethodName(),
                    Integer.valueOf(0), Integer.valueOf(0)};
            }
            else {
                keys = new String[]{OWNER, PACKAGE_NAME, DATA_LEVEL, POSITION};
                values = new Object[]{schema, name, Integer.valueOf(0), Integer.valueOf(0)};
            }

            Iterator<ViewRow> iter = viewCache.getRows(ALL_ARGUMENTS, new String[0], keys, values,
                new String[0]);
            while (iter.hasNext()) {
                UserArguments item = (UserArguments)iter.next();
                String key = makeKey(item.OBJECT_NAME /* METHOD_NAME */, item.OVERLOAD/* METHOD_NO */);
                if (m_ht.get(key) == null) {
                    ArrayList<ViewRow> itemWrapper = new ArrayList<ViewRow>();
                    itemWrapper.add(item);
                    m_ht.put(key, itemWrapper);
                }
            }
        }

        public ResultInfo get(String method_name, String method_no) throws java.sql.SQLException {
            ArrayList<ViewRow> row = m_ht.get(makeKey(method_name, method_no));
            ResultInfo rinfo = null;
            rinfo = new ResultInfo((UserArguments)row.get(0));
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

    private static class InfoValues {
        protected String m_schema;
        protected String m_name;
        protected Map<String, ArrayList<ViewRow>> m_ht;

        public InfoValues(String schema, String name) throws SQLException {
            m_schema = schema;
            m_name = name;
            m_ht = new HashMap<String, ArrayList<ViewRow>>();
        }

        protected static String makeKey(String method, String method_no) {
            return "" + method + "/" + method_no;
        }

        public boolean matches(String schema, String name) {
            return ((schema == null) ? m_schema == null : schema.equals(m_schema))
                && ((name == null) ? m_name != null : name.equals(m_name));
        }
    }
    private static class ParamInfoValues extends InfoValues {
        public ParamInfoValues(String schema, String name, MethodFilter methodFilter,
            ViewCache viewCache) throws SQLException {
            super(schema, name);
            String[] keys = null;
            Object[] values = null;
            if (methodFilter != null && methodFilter.isSingleMethod()) {
                keys = new String[]{OWNER, PACKAGE_NAME, OBJECT_NAME, DATA_LEVEL};
                values = new Object[]{schema, name, methodFilter.getSingleMethodName(),
                    Integer.valueOf(0)};
            }
            else {
                keys = new String[]{OWNER, PACKAGE_NAME, DATA_LEVEL};
                values = new Object[]{schema, name, Integer.valueOf(0)};
            }
            Iterator<ViewRow> iter = viewCache.getRows(ALL_ARGUMENTS, new String[0], keys, values,
                new String[0]);
            ArrayList<ViewRow> viewRows = new ArrayList<ViewRow>();
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
                ArrayList<ViewRow> v = m_ht.get(key);
                if (v == null) {
                    v = new ArrayList<ViewRow>();
                    m_ht.put(key, v);
                }
                v.add(item);
            }
        }

        public ParamInfo[] get(String method, String method_no) throws SQLException {
            ArrayList<ViewRow> v = m_ht.get(makeKey(method, method_no));
            if (v == null) {
                v = new ArrayList<ViewRow>(); // zero parameter
            }
            return ParamInfo.getParamInfo(v);
        }
    }
}