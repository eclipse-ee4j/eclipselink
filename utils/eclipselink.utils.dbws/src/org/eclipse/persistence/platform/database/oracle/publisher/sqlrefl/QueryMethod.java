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
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;

/**
 * A Method provides information about a single method this method executes a SQL statement
 */
public class QueryMethod extends SqlStmtMethod implements CursorMethod {

    /**
     * Construct a Method that is a query that returns java.lang.ResultSet or Beans
     */
    public QueryMethod(String methodName, int modifiers, String sqlStmt, boolean returnBeans,
        SqlStmtType sqlStmtType, SqlReflector reflector) throws SQLException,
        org.eclipse.persistence.platform.database.oracle.publisher.PublisherException {
        super(methodName, modifiers, sqlStmt, reflector);
        m_returnBeans = returnBeans;
        /*
         * // replace bind variables with "?", remove WHERE clauses String sqlStmtWhere = null;
         * SQLUtil sqlUtil = new SQLUtil(null, m_sqlStmtTmp);
         * 
         * if (!m_returnBeans) { m_returnType = SqlReflector.REF_CURSOR_TYPE; return; } Connection
         * connection = m_reflector.getConnection(); if (connection == null) { throw new
         * JPubException("ERROR: Cannot process queries without database connection"); } try {
         * Enumeration queryText = null; queryText = sqlUtil.queryText(); sqlStmtWhere = (String)
         * queryText.nextElement(); //System.out.println("[SqlStmtType] sqlStmtWhere: " +
         * sqlStmtWhere); //D+
         * 
         * // describe ResultSet // copied from SimpleCheckerImpl#describeResultSet(el, conn, op);
         * PreparedStatement ps = connection.prepareStatement(sqlStmtWhere); int pos = 0; for (int
         * pi = 1; pi <= m_sqlStmtParamNames.length; pi++) { SqlStmtType.bindNull((SqlType)
         * m_sqlStmtParamTypes[pi - 1], pi, ps, m_reflector); } ResultSet rset = ps.executeQuery();
         * ResultSetMetaData rmd = rset.getMetaData(); // get result column information int
         * resultColCount = rmd.getColumnCount(); Type[] resultColTypes = new Type[resultColCount];
         * String[] resultColNames = new String[resultColCount]; String[] resultColTypeNames = new
         * String[resultColCount];
         * 
         * for (int j = 1; j <= resultColCount; j++) { resultColNames[j - 1] = rmd.getColumnName(j);
         * resultColTypeNames[j - 1] = rmd.getColumnTypeName(j); } rset.close(); ps.close(); for
         * (int j = 1; j <= resultColCount; j++) { String colSchema = null; String colType =
         * resultColTypeNames[j - 1]; if (colType.indexOf('.') > -1 && (colType.indexOf('.') <
         * (colType.length() - 1))) { colSchema = colType.substring(0, colType.indexOf('.'));
         * colType = colType.substring(colType.indexOf('.') + 1); } resultColTypes[j - 1] =
         * m_reflector.addSqlDBType(colSchema, colType, null, null, false, null);
         * 
         * } String returnEleTypeName = null; m_resultIterTypeName =
         * Util.uniqueResultTypeName(methodName, "Iterator"); Field[] fields = null; if
         * (resultColTypes != null && resultColTypes.length == 1) { m_singleColName =
         * resultColNames[0]; m_returnEleType = resultColTypes[0]; if (m_returnEleType instanceof
         * JavaType) { returnEleTypeName = ((JavaType) m_returnEleType).getTypeName(); } else {
         * returnEleTypeName = ((SqlType) m_returnEleType).getSqlName().getUseClass(); } } else if
         * (resultColTypes != null) { if (sqlStmtType.getSqlName().getUseItf() != null) {
         * returnEleTypeName = Util.uniqueResultTypeName(sqlStmtType.getSqlName().getUseItf() + "_"
         * + methodName, "Row"); } else { returnEleTypeName =
         * Util.uniqueResultTypeName(sqlStmtType.getSqlName().getUseClass("") + "_" + methodName,
         * "Row"); } fields = new Field[resultColTypes.length]; for (int i = 0; i <
         * resultColTypes.length; i++) { //MYOBJ(ENAME, SAL) => MYOBJ String resultColName =
         * resultColNames[i]; if (resultColName.indexOf("(") > -1) { resultColName =
         * resultColName.substring(0, resultColName.indexOf("(")); } fields[i] = new
         * Field(resultColName, resultColTypes[i], 0, 0, 0, null, m_reflector); } m_returnEleType =
         * m_reflector.addJavaType(returnEleTypeName, fields, null, true, null); } else {
         * m_returnEleType = m_reflector.addJavaType(returnEleTypeName, new Field[0], null, true,
         * null); } m_returnType = new JavaArrayType(m_returnEleType, m_reflector,
         * SqlReflector.REF_CURSOR_TYPE); } catch (Exception e) { throw new
         * SQLException(e.getMessage()); }
         */
    }

    public String getResultIterTypeName() {
        return m_resultIterTypeName;
    }

    public TypeClass getReturnEleType() {
        return m_returnEleType;
    }

    public boolean isSingleCol() {
        return (m_singleColName != null);
    }

    public String singleColName() {
        return m_singleColName;
    }

    public boolean returnBeans() {
        return m_returnBeans;
    }

    public boolean returnResultSet() {
        return !m_returnBeans;
    }

    private String m_resultIterTypeName;
    private String m_singleColName;
    private boolean m_returnBeans;
    private TypeClass m_returnEleType;
}
