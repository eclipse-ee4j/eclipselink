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
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.PublisherException;
import dbws.testing.shadowddlgeneration.oldjpub.Util;
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
        ArrayList<String> sqlStmtParamNamesV = new ArrayList<String>();
        ArrayList<TypeClass> sqlStmtParamTypesV = new ArrayList<TypeClass>();
        ArrayList<Integer> sqlStmtParamModesV = new ArrayList<Integer>();
        ArrayList<String> uniqueParamNamesV = new ArrayList<String>();
        ArrayList<TypeClass> uniqueParamTypesV = new ArrayList<TypeClass>();
        ArrayList<Integer> uniqueParamModesV = new ArrayList<Integer>();
        Map<String, String> uniqueHash = new HashMap<String, String>();
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
                    String typeName = stnz.nextToken();
                    // for types like "double precision"
                    while (stnz.hasMoreTokens()) {
                        typeName += " " + stnz.nextToken();
                    }
                    String schema = null;
                    typeName = typeName.toUpperCase();
                    if (typeName.indexOf('.') > -1
                        && typeName.indexOf('.') < (typeName.length() - 1)) {
                        schema = typeName.substring(0, typeName.indexOf('.'));
                        typeName = typeName.substring(typeName.indexOf('.') + 1);
                    }
                    TypeClass tmpType = m_reflector.addSqlUserType(schema, typeName,
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
                sqlStmtParamModesV.add(Integer.valueOf(ProcedureMethod.IN));
                if (unique) {
                    uniqueParamModesV.add(Integer.valueOf(ProcedureMethod.IN));
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
            m_sqlStmtParamNames[jj] = (String)sqlStmtParamNamesV.get(j);
            m_sqlStmtParamTypes[jj] = (TypeClass)sqlStmtParamTypesV.get(j);
            m_sqlStmtParamModes[jj] = ((Integer)(sqlStmtParamModesV.get(j))).intValue();
        }
        m_paramNames = new String[uniqueParamNamesV.size()];
        m_paramTypes = new TypeClass[uniqueParamNamesV.size()];
        m_paramModes = new int[uniqueParamNamesV.size()];
        for (int j = 0; j < uniqueParamNamesV.size(); j++) {
            int jj = uniqueParamNamesV.size() - j - 1;
            m_paramNames[jj] = (String)uniqueParamNamesV.get(j);
            m_paramTypes[jj] = (TypeClass)uniqueParamTypesV.get(j);
            m_paramModes[jj] = ((Integer)(uniqueParamModesV.get(j))).intValue();
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
