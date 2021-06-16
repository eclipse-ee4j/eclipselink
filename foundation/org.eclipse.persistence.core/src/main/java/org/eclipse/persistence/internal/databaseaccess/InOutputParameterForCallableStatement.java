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

import java.sql.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public class InOutputParameterForCallableStatement extends OutputParameterForCallableStatement {
    protected Object inParameter;

    public InOutputParameterForCallableStatement(Object inParameter, OutputParameterForCallableStatement outParameter) {
        // 'inParameter' is stored in this class
        // The outParameter is stored under BindCallCustomParameter.obj
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
        // 'inParameter' is stored in this class
        // The outParameter is stored under BindCallCustomParameter.obj
        obj = outField;
        prepare(session);
        if (inParameter == null) {
            this.inParameter = getOutputField();
        } else {
            this.inParameter = inParameter;
        }
    }

    @Override
    public void set(DatabasePlatform platform, PreparedStatement statement, int parameterIndex, AbstractSession session) throws SQLException {
        //Set the 'inParameter' on the statement
        platform.setParameterValueInDatabaseCall(inParameter, statement, parameterIndex, session);
        //Set the outParameter on the statement
        super.set(platform, statement, parameterIndex, session);
    }

    @Override
    public void set(DatabasePlatform platform, CallableStatement statement, String parameterName, AbstractSession session) throws SQLException {
        //Set the 'inParameter' on the statement
        platform.setParameterValueInDatabaseCall(inParameter, statement, parameterName, session);
        //Set the outParameter on the statement
        super.set(platform, statement, parameterName, session);
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
