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
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import java.util.Collection;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * @author Guy Pelletier
 * @since TOPLink/Java 1.0
 */
public class SQLUpdateAllStatement extends SQLModifyStatement {
    protected HashMap m_updateClauses;
    protected HashMap databaseFieldsToTableAliases;

    protected SQLCall selectCallForExist;
    protected String tableAliasInSelectCallForExist;
    protected Collection primaryKeyFields;
    protected boolean shouldExtractWhereClauseFromSelectCallForExist;
    
    public void setSelectCallForExist(SQLCall selectCallForExist) {
        this.selectCallForExist = selectCallForExist;
    }
    public SQLCall getSelectCallForExist() {
        return selectCallForExist;
    }
    public void setTableAliasInSelectCallForExist(String tableAliasInSelectCallForExist) {
        this.tableAliasInSelectCallForExist = tableAliasInSelectCallForExist;
    }
    public String getTableAliasInSelectCallForExist() {
        return tableAliasInSelectCallForExist;
    }
    public void setPrimaryKeyFieldsForAutoJoin(Collection primaryKeyFields) {
        this.primaryKeyFields = primaryKeyFields;
    }
    public Collection getPrimaryKeyFieldsForAutoJoin() {
        return primaryKeyFields;
    }
    public void setUpdateClauses(HashMap updateClauses) {
        m_updateClauses = updateClauses;
    }
    public HashMap getUpdateClauses() {
        return m_updateClauses;
    }
    public void setDatabaseFieldsToTableAliases(HashMap databaseFieldsToTableAliases) {
        this.databaseFieldsToTableAliases = databaseFieldsToTableAliases;
    }
    public HashMap getDatabaseFieldsToTableAliases() {
        return databaseFieldsToTableAliases;
    }
    public void setShouldExtractWhereClauseFromSelectCallForExist(boolean shouldExtractWhereClauseFromSelectCallForExist) {
        this.shouldExtractWhereClauseFromSelectCallForExist = shouldExtractWhereClauseFromSelectCallForExist;
    }
    public boolean shouldExtractWhereClauseFromSelectCallForExist() {
        return shouldExtractWhereClauseFromSelectCallForExist;
    }

    
    /**
     * Append the string containing the SQL insert string for the given table.
     */
    public DatabaseCall buildCall(AbstractSession session) {
        SQLCall call = buildSimple(session);
        if(selectCallForExist == null) {
            return call;
        }
        Writer writer = new CharArrayWriter(100);
        try {
            writer.write(call.getSQLString());

            if(selectCallForExist != null) {
                if(shouldExtractWhereClauseFromSelectCallForExist) {
                    // Should get here only in case selectCallForExist doesn't have aliases and 
                    // targets the same table as the statement.
                    // Instead of making selectCallForExist part of " WHERE EXIST("
                    // just extract its where clause.
                    // Example: selectCallForExist.sqlString:
                    // "SELECT PROJ_ID FROM PROJECT WHERE (LEADER_ID IS NULL)
                    writeWhere(writer, selectCallForExist, call);
                    // The result is:
                    // "WHERE (LEADER_ID IS NULL)"
                } else {
                    writer.write(" WHERE EXISTS(");
                    // EXIST Example: selectCall.sqlString:
                    // "SELECT t0.EMP_ID FROM EMPLOYEE t0, SALARY t1 WHERE (((t0.F_NAME LIKE 'a') AND (t1.SALARY = 0)) AND (t1.EMP_ID = t0.EMP_ID))"
                    writeSelect(writer, selectCallForExist, tableAliasInSelectCallForExist, call, session.getPlatform());
                    // closing bracket for EXISTS
                    writer.write(")");
                }
                // Bug 301888 - DB2: UpdateAll/DeleteAll using WHERE EXIST fail.
                // If selectCallForExist has been explicitly set to not use binding then call should be set the same way. 
                if(selectCallForExist.isUsesBindingSet() && !selectCallForExist.usesBinding(session)) {
                    call.setUsesBinding(false);
                }
            }

            call.setSQLString(writer.toString());
            
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
                
        return call;
    }
    
    protected SQLCall buildSimple(AbstractSession session) {
        SQLCall call = new SQLCall();
        call.returnNothing();
        Writer writer = new CharArrayWriter(100);
        ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, getTranslationRow(), call, false, getBuilder());
        printer.setWriter(writer);

        try {
            // UPDATE //
            writer.write("UPDATE ");

            // HINT STRING //
            if (getHintString() != null) {
                writer.write(getHintString());
                writer.write(" ");
            }

            writer.write(getTable().getQualifiedNameDelimited(session.getPlatform()));

            // SET CLAUSE //
            writer.write(" SET ");

            Iterator i = m_updateClauses.keySet().iterator();
            boolean commaNeeded = false;

            while (i.hasNext()) {
                if (commaNeeded) {
                    writer.write(", ");
                }

                DatabaseField field = (DatabaseField)i.next();
                Object value = m_updateClauses.get(field);

                writer.write(field.getNameDelimited(session.getPlatform()));
                writer.write(" = ");
                if(value instanceof Expression) {
                    printer.printExpression((Expression)value);
                } else {
                    // must be SQLCall
                    SQLCall selCall = (SQLCall)value;
                    String tableAlias = (String)getDatabaseFieldsToTableAliases().get(field);
                    // should be SQLCall select
                    writer.write("(");
                    writeSelect(writer, selCall, tableAlias, call, session.getPlatform());
                    writer.write(")");
                }

                commaNeeded = true;
            }

            // WHERE CLAUSE //
            if (getWhereClause() != null) {
                writer.write(" WHERE ");
                printer.printExpression(getWhereClause());
            }

            call.setSQLString(writer.toString());
            return call;
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    protected void writeSelect(Writer writer, SQLCall selectCall, String tableAliasInSelectCall, SQLCall call, DatasourcePlatform platform) throws IOException {
        String str = selectCall.getSQLString();
        writer.write(str);
        
        boolean hasWhereClause = str.toUpperCase().indexOf(" WHERE ") >= 0;

        // Auto join
        // Example: AND t0.EMP_ID = EMP_ID
        Iterator it = getPrimaryKeyFieldsForAutoJoin().iterator();
        while(it.hasNext()) {
            if(!hasWhereClause) {
            // there is no where clause - should print WHERE
                writer.write(" WHERE ");
                hasWhereClause = true;
            } else {
                writer.write(" AND ");
            }
            String fieldName = ((DatabaseField)it.next()).getNameDelimited(platform);
            if(tableAliasInSelectCall != null) {
                writer.write(tableAliasInSelectCall);
                writer.write('.');
            }
            writer.write(fieldName);
            writer.write(" = ");
            writer.write(table.getQualifiedNameDelimited(platform));
            writer.write('.');
            writer.write(fieldName);
        }
        
        call.getParameters().addAll(selectCall.getParameters());
        call.getParameterTypes().addAll(selectCall.getParameterTypes());            
    }    

    protected boolean writeWhere(Writer writer, SQLCall selectCall, SQLCall call) throws IOException {
        String selectStr = selectCallForExist.getSQLString();

        int index = selectStr.toUpperCase().indexOf(" WHERE ");
        if(index < 0) {
            // no where clause - nothing to do
            return false;
        }

        // print the where clause
        String str = selectStr.substring(index);
        writer.write(str);

        // add parameters
        call.getParameters().addAll(selectCall.getParameters());
        call.getParameterTypes().addAll(selectCall.getParameterTypes());            

        return true;
    }
}