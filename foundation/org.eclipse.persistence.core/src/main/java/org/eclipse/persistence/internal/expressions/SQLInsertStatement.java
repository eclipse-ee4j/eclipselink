/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.SQLCall;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <p><b>Purpose</b>: Print INSERT statement.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Print INSERT statement.
 * </ul>
 *    @author Dorin Sandu
 *    @since TOPLink/Java 1.0
 */
public class SQLInsertStatement extends SQLModifyStatement {

    /**
     * Append the string containing the SQL insert string for the given table.
     */
    @Override
    protected SQLCall buildCallWithoutReturning(AbstractSession session) {
        SQLCall call = new SQLCall();
        call.returnNothing();

        Writer writer = new CharArrayWriter(200);
        try {
            writer.write("INSERT ");
            if (getHintString() != null) {
                writer.write(getHintString());
                writer.write(" ");
            }
            writer.write("INTO ");
            writer.write(getTable().getQualifiedNameDelimited(session.getPlatform()));
            writer.write(" (");

            List<DatabaseField> fieldsForTable = new ArrayList<>();
            for (Enumeration<DatabaseField> fieldsEnum = getModifyRow().keys(); fieldsEnum.hasMoreElements();) {
                DatabaseField field = fieldsEnum.nextElement();
                if (field.getTable().equals(getTable()) || (!field.hasTableName())) {
                    fieldsForTable.add(field);
                }
            }

            if (fieldsForTable.isEmpty()) {
                throw QueryException.objectToInsertIsEmpty(getTable());
            }

            for (int i = 0; i < fieldsForTable.size(); i++) {
                writer.write(fieldsForTable.get(i).getNameDelimited(session.getPlatform()));
                if ((i + 1) < fieldsForTable.size()) {
                    writer.write(", ");
                }
            }
            writer.write(") VALUES (");

            for (int i = 0; i < fieldsForTable.size(); i++) {
                DatabaseField field = fieldsForTable.get(i);
                call.appendModify(writer, field);
                if ((i + 1) < fieldsForTable.size()) {
                    writer.write(", ");
                }
            }
            writer.write(")");

            call.setSQLString(writer.toString());
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
        return call;
    }
}
