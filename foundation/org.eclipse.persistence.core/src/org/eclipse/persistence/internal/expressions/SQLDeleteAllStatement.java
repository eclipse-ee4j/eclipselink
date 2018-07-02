/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: Print DELETE statement with non trivial WHERE clause
 * <p><b>Responsibilities</b>:<ul>
 * <li> Print DELETE statement.
 * </ul>
 * @author Andrei Ilitchev
 * @since TOPLink 10.1.3
 */
public class SQLDeleteAllStatement extends SQLDeleteStatement {

    protected Expression inheritanceExpression;

    protected SQLCall selectCallForExist;
    protected String tableAliasInSelectCallForExist;

    protected SQLCall selectCallForNotExist;
    protected String tableAliasInSelectCallForNotExist;

    // A pair of Vectors for join expression
    protected Vector aliasedFields;
    protected Vector originalFields;

    protected boolean shouldExtractWhereClauseFromSelectCallForExist;

    public void setSelectCallForExist(SQLCall selectCallForExist) {
        this.selectCallForExist = selectCallForExist;
    }
    public SQLCall getSelectCallForExist() {
        return selectCallForExist;
    }
    public void setSelectCallForNotExist(SQLCall selectCallForNotExist) {
        this.selectCallForNotExist = selectCallForNotExist;
    }
    public SQLCall getSelectCallForNotExist() {
        return selectCallForNotExist;
    }
    public void setTableAliasInSelectCallForExist(String tableAliasInSelectCallForExist) {
        this.tableAliasInSelectCallForExist = tableAliasInSelectCallForExist;
    }
    public String getTableAliasInSelectCallForExist() {
        return tableAliasInSelectCallForExist;
    }
    public void setTableAliasInSelectCallForNotExist(String tableAliasInSelectCallForNotExist) {
        this.tableAliasInSelectCallForNotExist = tableAliasInSelectCallForNotExist;
    }
    public String getTableAliasInSelectCallForNotExist() {
        return tableAliasInSelectCallForNotExist;
    }
    public void setPrimaryKeyFieldsForAutoJoin(Collection primaryKeyFields) {
        if(primaryKeyFields != null) {
            if(primaryKeyFields instanceof Vector) {
                setOriginalFieldsForJoin((Vector)primaryKeyFields);
            } else {
                setOriginalFieldsForJoin(new Vector(primaryKeyFields));
            }
            setAliasedFieldsForJoin((Vector)getOriginalFieldsForJoin().clone());
        } else {
            setOriginalFieldsForJoin(null);
            setAliasedFieldsForJoin(null);
        }
    }
    public void setOriginalFieldsForJoin(Vector originalFields) {
        this.originalFields = originalFields;
    }
    public Vector getOriginalFieldsForJoin() {
        return originalFields;
    }
    public void setAliasedFieldsForJoin(Vector aliasedFields) {
        this.aliasedFields = aliasedFields;
    }
    public Vector getAliasedFieldsForExpression() {
        return aliasedFields;
    }
    public void setInheritanceExpression(Expression inheritanceExpression) {
        this.inheritanceExpression = inheritanceExpression;
    }
    public Expression getInheritanceExpression() {
        return inheritanceExpression;
    }

    public void setShouldExtractWhereClauseFromSelectCallForExist(boolean shouldExtractWhereClauseFromSelectCallForExist) {
        this.shouldExtractWhereClauseFromSelectCallForExist = shouldExtractWhereClauseFromSelectCallForExist;
    }
    public boolean shouldExtractWhereClauseFromSelectCallForExist() {
        return shouldExtractWhereClauseFromSelectCallForExist;
    }

    /**
     * Return SQL call for the statement, through generating the SQL string.
     */
    public DatabaseCall buildCall(AbstractSession session) {
        SQLCall call = (SQLCall)super.buildCall(session);

        Writer writer = new CharArrayWriter(100);
        try {
            // because where clause is null,
            // call.sqlString == "DELETE FROM getTable().getQualifiedName()"
            writer.write(call.getSQLString());

            boolean whereWasPrinted = true;
            if(selectCallForExist != null) {
                if(shouldExtractWhereClauseFromSelectCallForExist) {
                    // Should get here only in case selectCallForExist doesn't have aliases and
                    // targets the same table as the statement.
                    // Instead of making selectCallForExist part of " WHERE EXIST("
                    // just extract its where clause.
                    // Example: selectCallForExist.sqlString:
                    // "SELECT PROJ_ID FROM PROJECT WHERE (LEADER_ID IS NULL)
                    whereWasPrinted = writeWhere(writer, selectCallForExist, call);
                    // The result is:
                    // "WHERE (LEADER_ID IS NULL)"
                } else {
                    writer.write(" WHERE EXISTS(");
                    // EXIST Example: selectCallForExist.sqlString:
                    // "SELECT t0.EMP_ID FROM EMPLOYEE t0, SALARY t1 WHERE (((t0.F_NAME LIKE 'a') AND (t1.SALARY = 0)) AND (t1.EMP_ID = t0.EMP_ID))"
                    writeSelect(writer, selectCallForExist, tableAliasInSelectCallForExist, call, session.getPlatform());
                    // closing bracket for EXISTS
                    writer.write(")");
                    // The result is (target table is SALARY):
                    // "WHERE EXISTS(SELECT t0.EMP_ID FROM EMPLOYEE t0, SALARY t1 WHERE (((t0.F_NAME LIKE 'a') AND (t1.SALARY = 0)) AND (t1.EMP_ID = t0.EMP_ID)) AND t1.EMP_ID = SALARY.EMP_ID)"
                }
                // Bug 301888 - DB2: UpdateAll/DeleteAll using WHERE EXIST fail.
                // If selectCallForExist has been explicitly set to not use binding then call should be set the same way.
                if(selectCallForExist.isUsesBindingSet() && !selectCallForExist.usesBinding(session)) {
                    call.setUsesBinding(false);
                }
            } else if (inheritanceExpression != null) {
                writer.write(" WHERE ");
                // Example: (PROJ_TYPE = 'L')
                ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, getTranslationRow(), call, false, getBuilder());
                printer.setWriter(writer);
                printer.printExpression(inheritanceExpression);
                // The result is:
                // "(PROJ_TYPE = 'L')"
            } else {
                whereWasPrinted = false;
            }

            if(selectCallForNotExist != null) {
                if(whereWasPrinted) {
                    writer.write(" AND");
                } else {
                    writer.write(" WHERE");
                }
                writer.write(" NOT EXISTS(");
                // NOT EXIST Example: selectCall.sqlString:
                // "SELECT t0.EMP_ID FROM EMPLOYEE t0, SALARY t1 WHERE (t1.EMP_ID = t0.EMP_ID)"
                writeSelect(writer, selectCallForNotExist, tableAliasInSelectCallForNotExist, call, session.getPlatform());
                // closing bracket for EXISTS
                writer.write(")");
                // The result is (target table is EMPLOYEE):
                // "WHERE NOT EXISTS(SELECT t0.EMP_ID FROM EMPLOYEE t0, SALARY t1 WHERE ((t1.EMP_ID = t0.EMP_ID)) AND t0.EMP_ID = EMPLOYEE.EMP_ID)"
            }

            call.setSQLString(writer.toString());

        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }

        return call;
    }

    protected void writeSelect(Writer writer, SQLCall selectCall, String tableAliasInSelectCall, SQLCall call, DatasourcePlatform platform) throws IOException {
        String str = selectCall.getSQLString();
        writer.write(str);

        boolean hasWhereClause = str.toUpperCase().indexOf(" WHERE ") >= 0;

        // join aliased fields to original fields
        // Examples:
        //   table aliase provided: AND t0.EMP_ID = EMPLOYEE.EMP_ID
        //   table aliase not provided: AND EMP_ID = EMPLOYEE.EMP_ID
        for(int i=0; i < originalFields.size(); i++) {
            if(i==0 && !hasWhereClause) {
            // there is no where clause - should print WHERE
                writer.write(" WHERE ");
            } else {
                writer.write(" AND ");
            }
            if(tableAliasInSelectCall != null) {
                writer.write(tableAliasInSelectCall);
                writer.write('.');
            }
            writer.write(((DatabaseField)aliasedFields.elementAt(i)).getNameDelimited(platform));
            writer.write(" = ");
            writer.write(table.getQualifiedNameDelimited(platform));
            writer.write('.');
            writer.write(((DatabaseField)originalFields.elementAt(i)).getNameDelimited(platform));
        }

        // add parameters
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
