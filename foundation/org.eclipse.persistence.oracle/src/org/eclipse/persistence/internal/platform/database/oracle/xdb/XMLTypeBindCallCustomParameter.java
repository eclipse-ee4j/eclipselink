/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.platform.database.oracle.xdb;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;

import javax.xml.transform.dom.DOMResult;

import org.eclipse.persistence.internal.databaseaccess.BindCallCustomParameter;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.w3c.dom.Document;

public class XMLTypeBindCallCustomParameter extends BindCallCustomParameter {

    public XMLTypeBindCallCustomParameter(Object obj) {
        super(obj);
    }

    @Override
    public void set(DatabasePlatform platform, PreparedStatement statement, int parameterIndex, AbstractSession session) throws SQLException {
        if (this.obj instanceof String) {
            SQLXML sqlxml = getSQLXML(statement, session);
            sqlxml.setString((String) obj);
            this.obj = sqlxml;
        } else if (this.obj instanceof Document) {
            SQLXML sqlxml = getSQLXML(statement, session);
            DOMResult result = sqlxml.setResult(DOMResult.class);
            result.setNode((Document) obj);
            this.obj = sqlxml;
        }
        super.set(platform, statement, parameterIndex, session);
    }

    @Override
    public void set(DatabasePlatform platform, CallableStatement statement, String parameterName, AbstractSession session) throws SQLException {
        if (this.obj instanceof String) {
            SQLXML sqlxml = getSQLXML(statement, session);
            sqlxml.setString((String) obj);
            this.obj = sqlxml;
        } else if (this.obj instanceof Document) {
            SQLXML sqlxml = getSQLXML(statement, session);
            DOMResult result = sqlxml.setResult(DOMResult.class);
            result.setNode((Document) obj);
            this.obj = sqlxml;
        }
        super.set(platform, statement, parameterName, session);
    }

    //Bug#5200836, unwrap the connection prior to using.
    private SQLXML getSQLXML(Statement statement, AbstractSession session) throws SQLException {
        Connection con = session.getServerPlatform().unwrapConnection(statement.getConnection());
        return con.createSQLXML();
    }
}
