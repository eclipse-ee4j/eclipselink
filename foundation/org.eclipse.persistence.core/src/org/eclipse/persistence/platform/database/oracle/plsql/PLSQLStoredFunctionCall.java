/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.platform.database.oracle.plsql;

import static org.eclipse.persistence.internal.helper.DatabaseType.DatabaseTypeHelper.databaseTypeHelper;
import static org.eclipse.persistence.internal.helper.Helper.NL;

import java.util.List;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.exceptions.ValidationException;

/**
 * <b>Purpose</b>: Used to define a platform independent function call.
 * Note that not all platforms support stored functions.
 * This supports output parameters.
 * Functions can also be called through custom SQL.
 */
public class PLSQLStoredFunctionCall extends PLSQLStoredProcedureCall {

    public PLSQLStoredFunctionCall() {
        super();
        this.arguments.add(new PLSQLargument("RESULT", this.originalIndex++, OUT, JDBCTypes.VARCHAR_TYPE));
    }
    
    public PLSQLStoredFunctionCall(DatabaseType databaseType) {
        super();        
        DatabaseType dt = databaseType.isComplexDatabaseType() ? 
            ((ComplexDatabaseType)databaseType).clone() : databaseType;
        this.arguments.add(new PLSQLargument("RESULT", this.originalIndex++, OUT, dt));
    }
    
    public PLSQLStoredFunctionCall(DatabaseType databaseType, int length) {
        super();        
        DatabaseType dt = databaseType.isComplexDatabaseType() ? 
            ((ComplexDatabaseType)databaseType).clone() : databaseType;
        this.arguments.add(new PLSQLargument("RESULT", this.originalIndex++, OUT, dt, length));
    }
    
    public PLSQLStoredFunctionCall(DatabaseType databaseType, int length, int scale) {
        super();        
        DatabaseType dt = databaseType.isComplexDatabaseType() ? 
            ((ComplexDatabaseType)databaseType).clone() : databaseType;
        this.arguments.add(new PLSQLargument("RESULT", this.originalIndex++, OUT, dt, length, scale));
    }

    /**
     * INTERNAL:
     * Return call header for the call string.
     */
    @Override
    public String getCallHeader(DatabasePlatform platform) {
        return platform.getFunctionCallHeader();
    }

    /**
     * INTERNAL:
     * Return the first index of parameter to be placed inside brackets
     * in the call string.
     */
    @Override
    public int getFirstParameterIndexForCallString() {
        return 1;
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean isStoredFunctionCall() {
        return true;
    }
    
    @Override
    public boolean isStoredPLSQLFunctionCall() {
        return true;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void prepareInternal(AbstractSession session) {
        if (session.getPlatform().supportsStoredFunctions()) {
            super.prepareInternal(session);
        } else {
            throw ValidationException.platformDoesNotSupportStoredFunctions(Helper.getShortClassName(session.getPlatform()));
        }
    }

    /**
     * PUBLIC:
     * Define the return type of the function.
     */
    public void setResult(DatabaseType databaseType) {
        this.arguments.get(0).databaseType = databaseType;
    }

    /**
     * PUBLIC:
     * Define the return type of the function.
     */
    public void setResult(DatabaseType databaseType, int length) {
        this.arguments.get(0).databaseType = databaseType;
        this.arguments.get(0).length = length;
    }

    /**
     * PUBLIC:
     * Define the return type of the function.
     */
    public void setResult(DatabaseType databaseType, int length, int scale) {
        this.arguments.get(0).databaseType = databaseType;
        this.arguments.get(0).length = length;
        this.arguments.get(0).scale = scale;
    }

    /**
     * INTERNAL Generate portion of the Anonymous PL/SQL block that invokes the target function.
     */
    @Override
    protected void buildProcedureInvocation(StringBuilder sb, List<PLSQLargument> arguments) {
        sb.append("  ");
        PLSQLargument argument = arguments.get(0);
        sb.append(databaseTypeHelper.buildTarget(argument));
        sb.append(" := ");
        sb.append(getProcedureName());
        sb.append("(");
        int size = arguments.size();
        for (int index = 1; index < size; index++) {
            argument = arguments.get(index);
            sb.append(argument.name);
            sb.append("=>");
            sb.append(databaseTypeHelper.buildTarget(argument));
            if ((index + 1) < size) {
                sb.append(", ");
            }
        }
        sb.append(");");
        sb.append(NL);
    }
}
