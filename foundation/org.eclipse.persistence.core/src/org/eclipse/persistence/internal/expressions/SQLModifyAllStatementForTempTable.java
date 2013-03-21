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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.List;
import java.util.Vector;
import java.util.Collection;

import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * @author Andrei Ilitchev
 * @since TOPLink/Java 1.0
 */
public abstract class SQLModifyAllStatementForTempTable extends SQLModifyStatement {
    public static final int CREATE_TEMP_TABLE = 0;
    public static final int INSERT_INTO_TEMP_TABLE = 1;
    public static final int UPDATE_ORIGINAL_TABLE = 2;
    public static final int CLEANUP_TEMP_TABLE = 3;
    
    protected Collection allFields;
    protected List<DatabaseField> primaryKeyFields;
    protected SQLCall selectCall;
    protected int mode;
    
    abstract protected Collection getUsedFields();
    abstract protected void writeUpdateOriginalTable(AbstractSession session, Writer writer) throws IOException;

    public void setAllFields(Collection allFields) {
        this.allFields = allFields;
    }
    public Collection getAllFields() {
        return allFields;
    }
    public void setSelectCall(SQLCall selectCall) {
        this.selectCall = selectCall;
    }
    public SQLCall getSelectCall() {
        return selectCall;
    }
    public void setPrimaryKeyFields(List<DatabaseField> primaryKeyFields) {
        this.primaryKeyFields = primaryKeyFields;
    }
    public List<DatabaseField> getPrimaryKeyFields() {
        return primaryKeyFields;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }
    public int getMode() {
        return mode;
    }

    /**
     * Append the string containing the SQL insert string for the given table.
     */
    public DatabaseCall buildCall(AbstractSession session) {
        SQLCall call = new SQLCall();
        call.returnNothing();
        
        Writer writer = new CharArrayWriter(100);
        
        try {
            if(mode == CREATE_TEMP_TABLE) {
                session.getPlatform().writeCreateTempTableSql(writer, table, session, 
                                                new Vector(getPrimaryKeyFields()),
                                                getUsedFields(),
                                                new Vector(getAllFields()));
            } else if(mode == INSERT_INTO_TEMP_TABLE) {
                session.getPlatform().writeInsertIntoTableSql(writer, table, getUsedFields());

                call.getParameters().addAll(selectCall.getParameters());
                call.getParameterTypes().addAll(selectCall.getParameterTypes());
                
                String selectStr = selectCall.getSQLString();
                writer.write(selectStr);
                
            } else if(mode == UPDATE_ORIGINAL_TABLE) {
                writeUpdateOriginalTable(session, writer);
            } else if(mode == CLEANUP_TEMP_TABLE) {
                session.getPlatform().writeCleanUpTempTableSql(writer, table);
            } else {
                // should never happen
            }

            call.setSQLString(writer.toString());
            
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
                
        return call;
    }    
}
