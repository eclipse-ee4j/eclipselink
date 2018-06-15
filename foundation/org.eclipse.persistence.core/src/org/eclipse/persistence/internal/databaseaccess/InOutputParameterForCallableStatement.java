/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.databaseaccess;

import java.sql.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public class InOutputParameterForCallableStatement extends OutputParameterForCallableStatement {
    protected Object inParameter;

    public InOutputParameterForCallableStatement(Object inParameter, OutputParameterForCallableStatement outParameter) {
        super(outParameter);
        if (inParameter == null) {
            this.inParameter = getOutputField();
        } else {
            this.inParameter = inParameter;
        }
    }

    public InOutputParameterForCallableStatement(Object inParameter, DatabaseField outField, AbstractSession session) {
        if ((outField.getType() == null) && (inParameter != null)) {
            DatabaseField typeField = outField.clone();
            if (inParameter instanceof DatabaseField) {
                typeField.setType(((DatabaseField)inParameter).getType());
            } else if (inParameter instanceof InParameterForCallableStatement){
                typeField.setType( ((InParameterForCallableStatement)inParameter).getType() );
            } else {
                typeField.setType(inParameter.getClass());
            }
            outField = typeField;
        }
        obj = outField;
        prepare(session);
        if (inParameter == null) {
            this.inParameter = getOutputField();
        } else {
            this.inParameter = inParameter;
        }
    }

    @Override
    public void set(DatabasePlatform platform, PreparedStatement statement, int index, AbstractSession session) throws SQLException {
        platform.setParameterValueInDatabaseCall(inParameter, statement, index, session);
        super.set(platform, statement, index, session);
    }

    @Override
    public String toString() {
        String strIn;
        if (inParameter instanceof DatabaseField) {
            strIn = "null";
        } else {
            strIn = inParameter.toString();
        }
        return strIn + " " + super.toString();
    }
}
