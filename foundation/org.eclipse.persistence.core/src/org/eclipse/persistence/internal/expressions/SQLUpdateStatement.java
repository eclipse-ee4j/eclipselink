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
import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: Print UPDATE statement.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Print UPDATE statement.
 * </ul>
 *    @author Dorin Sandu
 *    @since TOPLink/Java 1.0
 */
public class SQLUpdateStatement extends SQLModifyStatement {

    /**
     * Append the string containing the SQL insert string for the given table.
     */
    protected SQLCall buildCallWithoutReturning(AbstractSession session) {
        SQLCall call = new SQLCall();
        call.returnNothing();

        Writer writer = new CharArrayWriter(100);
        try {
            writer.write("UPDATE ");
            if (getHintString() != null) {
                writer.write(getHintString());
                writer.write(" ");
            }
            writer.write(getTable().getQualifiedNameDelimited(session.getPlatform()));
            writer.write(" SET ");

            ExpressionSQLPrinter printer = null;

            Vector fieldsForTable = new Vector();
            Enumeration valuesEnum = getModifyRow().getValues().elements();
            Vector values = new Vector();
            for (Enumeration fieldsEnum = getModifyRow().keys(); fieldsEnum.hasMoreElements();) {
                DatabaseField field = (DatabaseField)fieldsEnum.nextElement();
                Object value = valuesEnum.nextElement();
                if (field.getTable().equals(getTable()) || (!field.hasTableName())) {
                    fieldsForTable.addElement(field);
                    values.addElement(value);
                }
            }

            if (fieldsForTable.isEmpty()) {
                return null;
            }

            for (int i = 0; i < fieldsForTable.size(); i++) {
                DatabaseField field = (DatabaseField)fieldsForTable.elementAt(i);
                writer.write(field.getNameDelimited(session.getPlatform()));
                writer.write(" = ");
                if(values.elementAt(i) instanceof Expression) {
                    // the value in the modify row is an expression - assign it.
                    Expression exp = (Expression)values.elementAt(i);
                    if(printer == null) {
                        printer = new ExpressionSQLPrinter(session, getTranslationRow(), call, false, getBuilder());
                        printer.setWriter(writer);
                    }
                    printer.printExpression(exp);
                    
                } else {
                    // the value in the modify row is ignored, the parameter corresponding to the key field will be assigned.
                    call.appendModify(writer, field);
                }

                if ((i + 1) < fieldsForTable.size()) {
                    writer.write(", ");
                }
            }

            if (!(getWhereClause() == null)) {
                writer.write(" WHERE ");
                if(printer == null) {
                    printer = new ExpressionSQLPrinter(session, getTranslationRow(), call, false, getBuilder());
                    printer.setWriter(writer);
                }
                printer.printExpression(getWhereClause());
            }

            call.setSQLString(writer.toString());
            return call;
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
}