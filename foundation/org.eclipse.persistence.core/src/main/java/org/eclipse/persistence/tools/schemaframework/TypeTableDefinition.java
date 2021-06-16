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
package org.eclipse.persistence.tools.schemaframework;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Allow for tabels of Oracle 8 object-relational user defined type to be created.
 * </p>
 */
public class TypeTableDefinition extends TableDefinition {

    /** The name of the type that this table is of. */
    protected String typeName;
    protected String additional = "";

    public TypeTableDefinition() {
        super();
        this.typeName = "";
    }

    /**
     * INTERNAL:
     * Return the create table statement.
     */
    @Override
    public Writer buildCreationWriter(AbstractSession session, Writer writer) {
        try {
            writer.write("CREATE TABLE " + getFullName() + " OF " + getTypeName() + " (");
            List<String> keyFields = getPrimaryKeyFieldNames();
            if ((!keyFields.isEmpty()) && session.getPlatform().supportsPrimaryKeyConstraint()) {
                writer.write("PRIMARY KEY (");
                for (Iterator<String> iterator = keyFields.iterator(); iterator.hasNext();) {
                    writer.write(iterator.next());
                    if (iterator.hasNext()) {
                        writer.write(", ");
                    }
                }
                writer.write(")");
            }
            writer.write(")");
            writer.write(additional);
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * PUBLIC:
     * The name of the type that this table is of.
     */
    public String getAdditonal() {
        return additional;
    }

    /**
     * PUBLIC:
     * The name of the type that this table is of.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * PUBLIC:
     * The name of the type that this table is of.
     */
    public void setAdditional(String additional) {
        this.additional = additional;
    }

    /**
     * PUBLIC:
     * The name of the type that this table is of.
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
