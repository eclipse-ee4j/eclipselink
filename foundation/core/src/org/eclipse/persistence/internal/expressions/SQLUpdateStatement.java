/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.exceptions.*;
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
            writer.write(getTable().getQualifiedName());
            writer.write(" SET ");

            Vector fieldsForTable = new Vector();
            for (Enumeration fieldsEnum = getModifyRow().keys(); fieldsEnum.hasMoreElements();) {
                DatabaseField field = (DatabaseField)fieldsEnum.nextElement();
                if (field.getTable().equals(getTable()) || (!field.hasTableName())) {
                    fieldsForTable.addElement(field);
                }
            }

            if (fieldsForTable.isEmpty()) {
                return null;
            }

            for (int i = 0; i < fieldsForTable.size(); i++) {
                DatabaseField field = (DatabaseField)fieldsForTable.elementAt(i);
                writer.write(field.getName());
                writer.write(" = ");
                call.appendModify(writer, field);

                if ((i + 1) < fieldsForTable.size()) {
                    writer.write(", ");
                }
            }

            if (!(getWhereClause() == null)) {
                writer.write(" WHERE ");
                ExpressionSQLPrinter printer = new ExpressionSQLPrinter(session, getTranslationRow(), call, false);
                printer.setWriter(writer);
                printer.printExpression(getWhereClause());
            }

            call.setSQLString(writer.toString());
            return call;
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }
}