/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
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
