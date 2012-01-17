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
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.SQLException;

//EclipseLink imports
import dbws.testing.shadowddlgeneration.oldjpub.PublisherException;

/**
 * A Method provides information about a single method this method executes a SQL statement
 */
public class QueryMethod extends SqlStmtMethod implements CursorMethod {

    protected String m_resultIterTypeName;
    protected String m_singleColName;
    protected boolean m_returnBeans;
    protected TypeClass m_returnEleType;

    /**
     * Construct a Method that is a query that returns java.lang.ResultSet or Beans
     */
    public QueryMethod(String methodName, int modifiers, String sqlStmt, boolean returnBeans,
        SqlStmtType sqlStmtType, SqlReflector reflector) throws SQLException, PublisherException {
        super(methodName, modifiers, sqlStmt, reflector);
        m_returnBeans = returnBeans;
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
}
