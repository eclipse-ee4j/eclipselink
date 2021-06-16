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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

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

            Vector fieldsForTable = new Vector();
            for (Enumeration fieldsEnum = getModifyRow().keys(); fieldsEnum.hasMoreElements();) {
                DatabaseField field = (DatabaseField)fieldsEnum.nextElement();
                if (field.getTable().equals(getTable()) || (!field.hasTableName())) {
                    fieldsForTable.addElement(field);
                }
            }

            if (fieldsForTable.isEmpty()) {
                throw QueryException.objectToInsertIsEmpty(getTable());
            }

            for (int i = 0; i < fieldsForTable.size(); i++) {
                writer.write(((DatabaseField)fieldsForTable.elementAt(i)).getNameDelimited(session.getPlatform()));
                if ((i + 1) < fieldsForTable.size()) {
                    writer.write(", ");
                }
            }
            writer.write(") VALUES (");

            for (int i = 0; i < fieldsForTable.size(); i++) {
                DatabaseField field = (DatabaseField)fieldsForTable.elementAt(i);
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
