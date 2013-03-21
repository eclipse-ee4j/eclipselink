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
package org.eclipse.persistence.internal.databaseaccess;

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
    
    public void set(DatabasePlatform platform, PreparedStatement statement, int index, AbstractSession session) throws SQLException {
        Object parameter = convert(inParameter,inField, session, statement.getConnection());
        platform.setParameterValueInDatabaseCall(parameter, statement, index, session);
    }
    
    public Class getType(){
        if ((inField!=null) && (inField.getType()!=null)){
            return inField.getType();
        }else if (inParameter!=null){
            return inParameter.getClass();
        }
        return null;
    }
}
