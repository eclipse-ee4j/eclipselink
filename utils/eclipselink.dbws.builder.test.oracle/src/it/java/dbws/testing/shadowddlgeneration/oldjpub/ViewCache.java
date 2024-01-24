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
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//EclipseLink imports

import static dbws.testing.shadowddlgeneration.oldjpub.Util.ALL_ARGUMENTS;

@SuppressWarnings({"unchecked","rawtypes"})
public class ViewCache implements Externalizable {

    private static final long serialVersionUID = 5009092516275895424L;

    static final String VIEW_CACHE_PREFIX = "viewcachefor";
    public static final String PARAMETER_USER = "USER";
    public static final String PARAMETER_ALL = "ALL";

    protected transient Connection m_conn;
    protected String m_user;
    protected transient Map m_rowsCacheIndex;
    protected ArrayList m_rowsCache;
    protected int m_hits;
    protected int m_visits;
    protected boolean m_viewCacheDebug = false;

    @SuppressWarnings("unused")
    public Iterator<ViewRow> getRows(String view, String[] columns, String[] keys, Object[] values,
        String[] orderby) throws java.sql.SQLException {
        m_visits++;
        String cKey = makeKey(view, columns, keys, values, orderby);
        ArrayList rowsV = (ArrayList)m_rowsCacheIndex.get(cKey);
        ArrayList derivedRowsV = rowsV;

        // NARROW DOWN QUERIES
        if (derivedRowsV == null) {
            for (int i = 0; i < m_rowsCache.size(); i++) {
                RowsCacheEntry incoming = new RowsCacheEntry(view, columns, keys, values, null);
                RowsCacheEntry cached = (RowsCacheEntry)m_rowsCache.get(i);
                RowsCacheEntry diff = cached.compare(incoming);
                if (diff != null) {
                    if (m_viewCacheDebug) {
                        System.out.println("viewcache.hit.query.relaxed: " + i);
                    }
                    derivedRowsV = new ArrayList();
                    for (int j = 0; j < diff.getRows().size(); j++) {
                        ViewRow row = diff.getRows().get(j);
                        boolean match = true;
                        for (int k = 0; k < diff.getKeys().length; k++) {
                            if ((k + 1) < diff.getKeys().length
                                && diff.getKeys()[k].equals(diff.getKeys()[k + 1])) {
                                if (!row.equals(diff.getKeys()[k], diff.getValues()[k])
                                    && !row.equals(diff.getKeys()[k], diff.getValues()[k])) {
                                    match = false;
                                    k++;
                                }
                            }
                            else if (!row.equals(diff.getKeys()[k], diff.getValues()[k])) {
                                match = false;
                            }
                        }
                        if (match) {
                            derivedRowsV.add(row);
                        }
                    }
                    break;
                }
                else if (m_viewCacheDebug) {
                    System.out.println("viewcache.no.match.query: " + i + ", diff=" + diff);
                }
            }
        }
        // ORDER BY SEQUENCE
        if (rowsV == null && orderby.length == 1 && orderby[0].equalsIgnoreCase(Util.SEQUENCE)
            && ViewRowFactory.hasSequence(view)) {
            if (derivedRowsV == null) {
                String unorderedCKey = makeKey(view, columns, keys, values, new String[0]);
                derivedRowsV = (ArrayList)m_rowsCacheIndex.get(unorderedCKey);
            }
            if (derivedRowsV != null) {
                rowsV = (ArrayList)derivedRowsV.clone();
                UserArguments.orderBySequence(rowsV);
                if (m_viewCacheDebug) {
                    System.out.println("viewcache.hit.query.unordered.sequence");
                }
            }
        }
        else
        // ORDER BY POSITION
        if (rowsV == null && orderby.length == 1 && orderby[0].equalsIgnoreCase("POSITION")
            && ViewRowFactory.hasPosition(view)) {
            if (derivedRowsV == null) {
                String unorderedCKey = makeKey(view, columns, keys, values, new String[0]);
                derivedRowsV = (ArrayList)m_rowsCacheIndex.get(unorderedCKey);
            }
            if (derivedRowsV != null) {
                rowsV = (ArrayList)derivedRowsV.clone();
                UserArguments.orderByPosition(rowsV);
                if (m_viewCacheDebug) {
                    System.out.println("viewcache.hit.query.unordered.position");
                }
            }
        }
        if (rowsV == null) {
            rowsV = derivedRowsV;
        }
        if (rowsV == null) {
            String stmtText = makeQuery(view, columns, keys, values, orderby);
            ResultSet rset = null;
            PreparedStatement stmt = null;
            if (m_conn == null) {
                throw new SQLException("ERROR: null JDBC connection.");
            }
            try {
                stmt = m_conn.prepareStatement(stmtText);
                long millis = 0;
                if (m_viewCacheDebug) {
                    millis = System.currentTimeMillis();
                    System.out.println("viewcache.execute.query: " + stmtText);
                }
                rset = stmt.executeQuery();
                rowsV = new ArrayList<ViewRow>();
                while (rset.next()) {
                    rowsV.add(ViewRowFactory.createViewRow(view, columns, rset));
                    if (m_viewCacheDebug) {
                        System.out.print(".");
                    }
                }
                if (m_viewCacheDebug) {
                    System.out.println("\nviewcache.execute.query.result.set.next.elapse: "
                        + (System.currentTimeMillis() - millis));
                }
            }
            finally {
                if (rset != null) {
                    rset.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }

            m_rowsCacheIndex.put(cKey, rowsV);
            m_rowsCache.add(new RowsCacheEntry(view, columns, keys, values, rowsV));

        }
        else {
            m_hits++;
            if (m_viewCacheDebug) {
                String stmtText = makeQuery(view, columns, keys, values, orderby);
                System.out.println("viewcache.hit.query: " + stmtText);
            }
        }

        if (rowsV == null) {
            // not found in view cache and no database connection
            rowsV = new ArrayList();
        }
        if (m_viewCacheDebug) {
            System.out.println("viewcache.execute.query.result.size: " + rowsV.size());
            System.out.println("viewcache.hit.rate: " + cKey + ", " + m_hits + "/" + m_visits);
        }
        return rowsV.iterator();
    }

    public Object[] getOutParameters(String stmtText, Object[] inParams, int[] types)
        throws SQLException {
        m_visits++;
        Object[] objTypes = toObject(types);
        String cKey = makeKey(stmtText, new String[0], new String[0], inParams, new String[0]);
        ArrayList outParamList = (ArrayList)m_rowsCacheIndex.get(cKey);
        if (outParamList == null && m_conn != null) {
            // initialize parameter list
            outParamList = new ArrayList();
            CallableStatement stmt = m_conn.prepareCall(stmtText);
            int i = 1;
            for (; i < inParams.length + 1; i++) {
                if (inParams[i - 1] instanceof byte[]) {
                    stmt.setBytes(i, (byte[])inParams[i - 1]);
                }
                else {
                    throw new SQLException("input type not supported: "
                        + inParams[i - 1].getClass().getName());
                }
            }
            for (; i < (inParams.length + types.length + 1); i++) {
                stmt.registerOutParameter(i, types[i - inParams.length - 1]);
            }
            stmt.executeUpdate();
            for (i = inParams.length; i < inParams.length + types.length; i++) {
                int index = i - inParams.length;
                if (types[index] == OracleTypes.INTEGER) {
                    outParamList.add(stmt.getInt(i + 1));
                }
                else if (types[index] == OracleTypes.VARCHAR) {
                    outParamList.add(stmt.getString(i + 1));
                }
            }
            stmt.close();
            m_rowsCacheIndex.put(cKey, outParamList);
            m_rowsCache.add(new RowsCacheEntry(stmtText, new String[0], new String[0], objTypes,
                outParamList));
        }

        Object[] outParams = null;
        if (outParamList != null) {
            outParams = outParamList.toArray(new Object[0]);
        }
        return outParams;
    }

    private Object[] toObject(int[] types) {
        Object[] obj = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            obj[i] = types[i];
        }
        return obj;
    }

    private String makeKey(String view, String[] columns, String[] keys, Object[] values,
        String[] orderby) {
        StringBuilder key = new StringBuilder(view);
        if (columns.length != 0) {
            key.append("[");
            for (int i = 0; i < columns.length; i++) {
                key.append(columns[i]).append(i > 0 ? ", " : "");
            }
            key.append("]");
        }
        key.append("(");
        for (String string : keys) {
            key.append(string).append(", ");
        }
        key.append(": ");
        for (Object value : values) {
            if (value instanceof byte[]) {
                byte[] bytes = (byte[]) value;
                for (byte aByte : bytes) {
                    key.append(aByte);
                }
                key.append(", ");
            } else {
                key.append(value).append(", ");
            }
        }
        key.append(")");
        for (String s : orderby) {
            key.append(s).append("(o)");
        }
        return key.toString().toUpperCase(); // On Windows, all keys are capitalized during deserialization
    }

    /*
     * makeQuery( "ALL_OBJECT", new String[]{"OWNER", "OBJEC_TYPE", "OBJECT_TYPE"}, new
     * String[]{"SCOTT", "PROCEDURE", "FUN%CTION"}) => WEHERE OWNER='SCOTT' AND
     * (OBJECT_TYPE='PROCEDURE OR OBJECT_TYPE like 'FUN%CTION')
     */
    private String makeQuery(String view, String[] columns, String[] keys, Object[] values,
        String[] orderby) {
        StringBuilder stmt = new StringBuilder("SELECT " + ViewRowFactory.getProject(view, columns) + " FROM " + view);
        if (keys.length > 0) {
            stmt.append(" WHERE ");
        }
        boolean inOR = false;
        for (int i = 0; i < keys.length; i++) {
            // OR
            if (!inOR && i < (keys.length - 1) && keys[i].equals(keys[i + 1])) {
                stmt.append("(");
                inOR = true;
            }
            stmt.append(keys[i]);
            if (values[i] == null || "".equals(values[i])) {
                stmt.append(" IS NULL");
            }
            else if ("NOT NULL".equals(values[i])) {
                stmt.append(" IS NOT NULL");
            }
            else if (values[i] instanceof java.lang.String) {
                stmt.append("='").append(values[i]).append("'");
            }
            else if ((values[i] instanceof java.lang.String)
                && ((String) values[i]).contains("%")) {
                stmt.append(" like '").append(values[i]).append("'");
            }
            else {
                stmt.append("=").append(values[i]);
            }
            if (inOR && i < keys.length - 1 && keys[i].equals(keys[i + 1])) {
                stmt.append(" OR ");
            }
            else if (inOR && i < keys.length - 1) {
                stmt.append(") AND ");
                inOR = false;
            }
            else if (inOR) {
                stmt.append(")");
                inOR = false;
            }
            else if (i < keys.length - 1) {
                stmt.append(" AND ");
            }
        }
        for (int i = 0; i < orderby.length; i++) {
            if (i == 0) {
                stmt.append(" ORDER BY ");
            }
            stmt.append(orderby[i]);
            if (i != (orderby.length - 1)) {
                stmt.append(", ");
            }
        }
        return stmt.toString();
    }

    public ViewCache() {
    }

    public ViewCache(Connection conn, String user) {
        m_conn = conn;
        m_user = user;
        m_rowsCacheIndex = new HashMap();
        m_rowsCache = new ArrayList();
        m_hits = 0;
        m_visits = 0;
    }

    /*
     * initialize the cache using queries of the viewcache passed in
     */
    public void init(ViewCache viewCache) {
        int len = m_rowsCache.size();
        for (Object o : m_rowsCache) {
            RowsCacheEntry entry = (RowsCacheEntry) o;
            try {
                getRows(entry);
            } catch (Exception e) {
                System.err.println("WARNING: error in refreshing view cache for view "
                        + entry.getView());
            }
        }
    }

    /*
     * refresh the cache
     */
    public void refresh() {
        int len = m_rowsCache.size();
        for (Object o : m_rowsCache) {
            RowsCacheEntry entry = (RowsCacheEntry) o;
            try {
                getRows(entry);
            } catch (Exception e) {
                System.err.println("WARNING: error in refreshing view cache for view "
                        + entry.getView());
            }
        }
    }

    private Iterator getRows(RowsCacheEntry entry) throws SQLException {
        return getRows(entry.getView(), entry.getSelects(), entry.getKeys(), entry.getValues(),
            new String[0]);
    }

    public void fetch(String packageName, MethodFilter sigf) throws SQLException {
        if (m_viewCacheDebug) {
            System.out.println("viewcache.fetch: " + packageName);
        }

        packageName = dbifyName(packageName);
        if (PARAMETER_ALL.equalsIgnoreCase(packageName)
            || PARAMETER_USER.equalsIgnoreCase(packageName)) {
            getRows(ALL_ARGUMENTS, new String[0], new String[]{}, new Object[]{}, new String[0]);
        }
        else {
            String[] keys = null;
            Object[] values = null;
            if (sigf != null && sigf.isSingleMethod()) {
                keys = new String[]{Util.PACKAGE_NAME, Util.OBJECT_NAME};
                values = new Object[]{packageName, sigf.getSingleMethodName()};
            }
            else {
                keys = new String[]{Util.PACKAGE_NAME};
                values = new Object[]{packageName};
            }
            getRows(ALL_ARGUMENTS, new String[0], keys, values, new String[0]);
        }
    }

    public int getHits() {
        return m_hits;
    }

    public int getVisits() {
        return m_visits;
    }

    public void reset(Connection conn) {
        m_conn = conn;
    }

    public void close() {
        try {
            if (m_conn != null) {
                m_conn.close();
            }
        }
        catch (Exception e) {
            // Closing resources. OK to ignore exception.
        }
    }

    public String getUser() {
        return m_user;
    }

    public String getFileName(String dir) {
        return getFileName(dir, m_user);
    }

    public static String getFileName(String dir, String user) {
        return (dir == null ? "" : dir + File.separator) + VIEW_CACHE_PREFIX + user.toLowerCase();
    }

    @Override
    public void readExternal(java.io.ObjectInput in) throws IOException, ClassNotFoundException {
        if (m_viewCacheDebug) {
            System.out.println("viewcache.read.external");
        }
        // summary
        m_user = (String)in.readObject();
        m_hits = (Integer) in.readObject();
        m_visits = (Integer) in.readObject();

        // m_rowsCache
        int rowsCacheSize = (Integer) in.readObject();
        m_rowsCache = new ArrayList(rowsCacheSize);
        for (int i = 0; i < rowsCacheSize; i++) {
            RowsCacheEntry rce = (RowsCacheEntry)in.readObject();
            m_rowsCache.add(rce);
        }

        // m_rowsCacheIndex (String, ArrayList<ViewRow>)
        int rowsCacheIndexSize = (Integer) in.readObject();
        m_rowsCacheIndex = new HashMap(rowsCacheIndexSize);
        for (int i = 0; i < rowsCacheIndexSize; i++) {
            String key = (String)in.readObject();
            int rowsSize = (Integer) in.readObject();
            ArrayList rows = new ArrayList(rowsSize);
            for (int j = 0; j < rowsSize; j++) {
                ViewRow row = (ViewRow)in.readObject();
                rows.add(row);
            }
            m_rowsCacheIndex.put(key, rows);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        if (m_viewCacheDebug) {
            System.out.println("viewcache.write.external");
        }
        // summary
        out.writeObject(m_user);
        out.writeObject(m_hits);
        out.writeObject(m_visits);

        // m_rowsCache
        out.writeObject(m_rowsCache.size());
        for (Object o : m_rowsCache) {
            RowsCacheEntry rce = (RowsCacheEntry) o;
            out.writeObject(rce);
        }

        // m_rowsCacheIndex (String, ArrayList<ViewRow>)
        out.writeObject(m_rowsCacheIndex.size());
        Iterator keys = m_rowsCacheIndex.keySet().iterator();
        Iterator values = m_rowsCacheIndex.values().iterator();
        while (keys.hasNext()) {
            out.writeObject(keys.next());
            ArrayList rows = (ArrayList)values.next();
            out.writeObject(rows.size());
            for (Object row : rows) {
                out.writeObject(row);
            }
        }
    }

    public String printSummary() {
        // summary
        StringBuilder text = new StringBuilder();
        text.append("****").append(getFileName(null)).append("****").append("\n");
        text.append("schema: ").append(m_user).append("\n");
        text.append("hits/visits: ").append(m_hits).append("/").append(m_visits).append("\n");
        for (Object o : m_rowsCache) {
            RowsCacheEntry rce = (RowsCacheEntry) o;
            text.append(rce.printSummary());
        }
        return text.toString();
    }

    /*
     * if the name is quoted, the quotes are removed.
     *
     * public static String dbifyName(String s, SqlReflector reflector) { if (s == null ||
     * s.equals("") || reflector == null || reflector.getConnection() == null) { return s; } return
     * dbifyName(s, reflector.getViewCache()); }
     */

    public String dbifyName(String s) {
        if (s == null) {
            return "";
        }
        else if (s.isEmpty()) {
            return "";
        }
        else if (SqlName.isQuoted(s)) {
            return s.substring(1, s.length() - 1);
        }
        String upper_s = s.toUpperCase();
        String dbName = "";
        try {
            Iterator rowIter = getRows(Util.DUAL, new String[]{"UPPER('" + s + "') AS UPPER_NAME"},
                new String[0], new Object[0], new String[0]);
            if (rowIter.hasNext()) {
                SingleColumnViewRow row = (SingleColumnViewRow)rowIter.next();
                dbName = row.getValue();
            }
            else {
                // not found in viewCache and no database connection
                dbName = upper_s;
            }
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
            dbName = upper_s;
        }
        return dbName;
    }

}
