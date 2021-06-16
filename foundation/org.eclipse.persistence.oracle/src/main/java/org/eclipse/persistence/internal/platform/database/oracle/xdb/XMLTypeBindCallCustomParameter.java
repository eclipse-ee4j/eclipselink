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
