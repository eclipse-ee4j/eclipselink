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
import java.util.Map;
import java.util.StringTokenizer;

//EclipseLink imports

import static dbws.testing.shadowddlgeneration.oldjpub.Util.IS_TYPE_OR_PACKAGE;

/**
 * DML or Query Method
 */
public abstract class SqlStmtMethod extends ProcedureMethod {

    protected String[] m_sqlStmtParamNames;
    protected TypeClass[] m_sqlStmtParamTypes;
    protected int[] m_sqlStmtParamModes;
    protected String m_sqlStmt; // ? represents host variables
    protected String m_sqlStmtTmp;
    protected String m_sqlStmtQuoted;
    protected SqlReflector m_reflector;

    public SqlStmtMethod(String name, int modifiers, String sqlStmt, SqlReflector reflector)
        throws SQLException, PublisherException {
        super(name, null, /* overloadNumber */
        modifiers, null, /* returnType */
        null, /* sqlStmtParamTypes */
        null, /* sqlStmtParamNames */
        null, /* sqlStmtParamModes */
        null, /* sqlStmtParamDefaults */
        -1);

        m_sqlStmt = sqlStmt;
        m_reflector = reflector;
        m_keepMethodName = true;

        /* get method information, populate m_methods */
        // System.out.println("[SqlStmtType] Method: "+methodName); //D+
        /* get host variable/parameter type information */
        // sqlj/semantics/sql/SimpleCheckerImpl.m4
        // oracle.j2ee.ws.db.sqlrefl.Map#writePredefinedName(sqlTypecode):
        // names[getJavaTypecode(sqlTypecode)]
        // SqlType#m_allTypes: CLOB_TYPE "CLOB", OracleTypes.CLOB
        // This loop accomplishes two tasks:
        // (1) Rewrite sqlStatment in the SQLJ format, e.g.,
        // Pre: sqlStmt = "select * from emp where eno=:{x NUMBER}"
        // Post: sqlStmt = "select * from emp where eno=:x"
        // (2) Reflect on the parameter names and types (sqlStmtParamNames, sqlStmtParamTypes)
        // The refection is done by running the sqlStmt against database.
        // The query used for reflection is sqlStmtTmp, e.g.,
        // select * from emp where eno=?,
        // derived by replacing bind variables. :{Y Z} => ?
        m_sqlStmtTmp = m_sqlStmt;
        ArrayList<String> sqlStmtParamNamesV = new ArrayList<>();
        ArrayList<TypeClass> sqlStmtParamTypesV = new ArrayList<>();
        ArrayList<Integer> sqlStmtParamModesV = new ArrayList<>();
        ArrayList<String> uniqueParamNamesV = new ArrayList<>();
        ArrayList<TypeClass> uniqueParamTypesV = new ArrayList<>();
        ArrayList<Integer> uniqueParamModesV = new ArrayList<>();
        Map<String, String> uniqueHash = new HashMap<>();
        int idx0 = m_sqlStmtTmp.lastIndexOf(":{");
        int idx1 = -1;
        int jdbcParamIdx = 0;
        if (idx0 >= 0) {
            idx1 = m_sqlStmtTmp.indexOf('}', idx0);
        }
        while (idx0 >= 0 && idx1 >= 0) {
            jdbcParamIdx++;
            String sqlStmtTmpTerm = m_sqlStmtTmp.substring(idx0 + ":{".length(), idx1);
            StringTokenizer stnz = new StringTokenizer(sqlStmtTmpTerm);
            boolean unique = true; // 3299079
            if (stnz.countTokens() > 1) {
                String paramName = SqlName.sqlIdToJavaId(stnz.nextToken(), false);
                if (paramName != null) {
                    unique = (uniqueHash.get(paramName) == null);
                }
                uniqueHash.put(paramName, paramName);
                sqlStmtParamNamesV.add(paramName);
                if (unique) {
                    uniqueParamNamesV.add(paramName);
                }
                try {
                    // String schema=null;
                    StringBuilder typeName = new StringBuilder(stnz.nextToken());
                    // for types like "double precision"
                    while (stnz.hasMoreTokens()) {
                        typeName.append(" ").append(stnz.nextToken());
                    }
                    String schema = null;
                    typeName = new StringBuilder(typeName.toString().toUpperCase());
                    if (typeName.toString().indexOf('.') > -1
                        && typeName.toString().indexOf('.') < (typeName.length() - 1)) {
                        schema = typeName.substring(0, typeName.toString().indexOf('.'));
                        typeName = new StringBuilder(typeName.substring(typeName.toString().indexOf('.') + 1));
                    }
                    TypeClass tmpType = m_reflector.addSqlUserType(schema, typeName.toString(),
                        IS_TYPE_OR_PACKAGE, false, 0, 0, null);
                    sqlStmtParamTypesV.add(tmpType);
                    if (unique) {
                        uniqueParamTypesV.add(tmpType);
                    }
                }
                catch (PublisherException e) {
                    // e.printStackTrace(); //D+
                    throw new SQLException(e.getMessage());
                }
                sqlStmtParamModesV.add(ProcedureMethod.IN);
                if (unique) {
                    uniqueParamModesV.add(ProcedureMethod.IN);
                }
                m_sqlStmtTmp = m_sqlStmtTmp.substring(0, idx0) + "?"
                    + m_sqlStmtTmp.substring(idx1 + 1);
                m_sqlStmt = m_sqlStmt.substring(0, idx0) + ":" + jdbcParamIdx
                    + m_sqlStmt.substring(idx1 + 1);
            }
            else {
                System.err.println("needParamNameAndType " + sqlStmtTmpTerm);
            }
            idx0 = m_sqlStmtTmp.lastIndexOf(":{");
            if (idx0 >= 0) {
                idx1 = m_sqlStmtTmp.indexOf('}', idx0);
            }
        }
        m_sqlStmtParamNames = new String[sqlStmtParamNamesV.size()];
        m_sqlStmtParamTypes = new TypeClass[sqlStmtParamNamesV.size()];
        m_sqlStmtParamModes = new int[sqlStmtParamNamesV.size()];
        for (int j = 0; j < sqlStmtParamNamesV.size(); j++) {
            int jj = sqlStmtParamNamesV.size() - j - 1;
            m_sqlStmtParamNames[jj] = sqlStmtParamNamesV.get(j);
            m_sqlStmtParamTypes[jj] = sqlStmtParamTypesV.get(j);
            m_sqlStmtParamModes[jj] = sqlStmtParamModesV.get(j);
        }
        m_paramNames = new String[uniqueParamNamesV.size()];
        m_paramTypes = new TypeClass[uniqueParamNamesV.size()];
        m_paramModes = new int[uniqueParamNamesV.size()];
        for (int j = 0; j < uniqueParamNamesV.size(); j++) {
            int jj = uniqueParamNamesV.size() - j - 1;
            m_paramNames[jj] = uniqueParamNamesV.get(j);
            m_paramTypes[jj] = uniqueParamTypesV.get(j);
            m_paramModes[jj] = uniqueParamModesV.get(j);
        }
    }

    public String getSqlStmt() {
        return m_sqlStmt;
    }

    public String getSqlStmtQuoted() {
        if (m_sqlStmtQuoted == null) {
            m_sqlStmtQuoted = Util.quote(m_sqlStmt);
        }
        return m_sqlStmtQuoted;
    }

    public String[] getSqlStmtParamNames() {
        return m_sqlStmtParamNames;
    }

    public TypeClass[] getSqlStmtParamTypes() {
        return m_sqlStmtParamTypes;
    }
}
