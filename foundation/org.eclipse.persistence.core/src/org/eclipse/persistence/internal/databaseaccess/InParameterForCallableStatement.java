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
        } else if (inParameter != null) {
            return inParameter.getClass();
        }
        return null;
    }
}
