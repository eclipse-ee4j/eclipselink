/* 1998 (c) Oracle Corporation */
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import org.eclipse.persistence.platform.database.oracle.publisher.PublisherException;
import org.eclipse.persistence.platform.database.oracle.publisher.Util;
import static org.eclipse.persistence.platform.database.oracle.publisher.Util.IS_TYPE_OR_PACKAGE;

/**
 * DML or Query Method
 */
@SuppressWarnings("unchecked")
public abstract class SqlStmtMethod extends Method {

    public SqlStmtMethod(String name, int modifiers, String sqlStmt, SqlReflectorImpl reflector)
        throws SQLException,
        org.eclipse.persistence.platform.database.oracle.publisher.PublisherException {
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
        Vector sqlStmtParamNamesV = new Vector();
        Vector sqlStmtParamTypesV = new Vector();
        Vector sqlStmtParamModesV = new Vector();
        Vector uniqueParamNamesV = new Vector();
        Vector uniqueParamTypesV = new Vector();
        Vector uniqueParamModesV = new Vector();
        Hashtable uniqueHash = new Hashtable();
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
                sqlStmtParamNamesV.addElement(paramName);
                if (unique) {
                    uniqueParamNamesV.addElement(paramName);
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
                    Type tmpType = m_reflector.addSqlUserType(schema, typeName, IS_TYPE_OR_PACKAGE,
                        false, 0, 0, null);
                    sqlStmtParamTypesV.addElement(tmpType);
                    if (unique) {
                        uniqueParamTypesV.addElement(tmpType);
                    }
                }
                catch (PublisherException e) {
                    // e.printStackTrace(); //D+
                    throw new SQLException(e.getMessage());
                }
                sqlStmtParamModesV.addElement(new Integer(Method.IN));
                if (unique) {
                    uniqueParamModesV.addElement(new Integer(Method.IN));
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
        m_sqlStmtParamTypes = new Type[sqlStmtParamNamesV.size()];
        m_sqlStmtParamModes = new int[sqlStmtParamNamesV.size()];
        for (int j = 0; j < sqlStmtParamNamesV.size(); j++) {
            int jj = sqlStmtParamNamesV.size() - j - 1;
            m_sqlStmtParamNames[jj] = (String)sqlStmtParamNamesV.elementAt(j);
            m_sqlStmtParamTypes[jj] = (Type)sqlStmtParamTypesV.elementAt(j);
            m_sqlStmtParamModes[jj] = ((Integer)(sqlStmtParamModesV.elementAt(j))).intValue();
        }
        m_paramNames = new String[uniqueParamNamesV.size()];
        m_paramTypes = new Type[uniqueParamNamesV.size()];
        m_paramModes = new int[uniqueParamNamesV.size()];
        for (int j = 0; j < uniqueParamNamesV.size(); j++) {
            int jj = uniqueParamNamesV.size() - j - 1;
            m_paramNames[jj] = (String)uniqueParamNamesV.elementAt(j);
            m_paramTypes[jj] = (Type)uniqueParamTypesV.elementAt(j);
            m_paramModes[jj] = ((Integer)(uniqueParamModesV.elementAt(j))).intValue();
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

    public Type[] getSqlStmtParamTypes() {
        return m_sqlStmtParamTypes;
    }

    protected String[] m_sqlStmtParamNames;
    protected Type[] m_sqlStmtParamTypes;
    protected int[] m_sqlStmtParamModes;

    protected String m_sqlStmt; // ? represents host variables
    protected String m_sqlStmtTmp;
    protected String m_sqlStmtQuoted;
    protected SqlReflectorImpl m_reflector;
}
