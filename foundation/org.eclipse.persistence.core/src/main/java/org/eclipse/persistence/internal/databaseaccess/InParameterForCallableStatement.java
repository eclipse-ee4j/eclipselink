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
package org.eclipse.persistence.internal.databaseaccess;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public class InParameterForCallableStatement extends BindCallCustomParameter {
    protected Object inParameter;
    protected DatabaseField inField;

    public InParameterForCallableStatement(Object inParameter, DatabaseField inField) {
        this.inParameter = inParameter;
        this.inField = inField;
    }

    @Override
    public void set(DatabasePlatform platform, PreparedStatement statement, int parameterIndex, AbstractSession session) throws SQLException {
        Object parameter = convert(inParameter, inField, session, statement.getConnection());
        platform.setParameterValueInDatabaseCall(parameter, statement, parameterIndex, session);
    }

    @Override
    public void set(DatabasePlatform platform, CallableStatement statement, String parameterName, AbstractSession session) throws SQLException {
        Object parameter = convert(inParameter, inField, session, statement.getConnection());
        platform.setParameterValueInDatabaseCall(parameter, statement, parameterName, session);
    }

    public Class getType(){
        if ((inField != null) && (inField.getType() != null)) {
            return inField.getType();
        } else if (inParameter!=null) {
            return inParameter.getClass();
        }
        return null;
    }
}
