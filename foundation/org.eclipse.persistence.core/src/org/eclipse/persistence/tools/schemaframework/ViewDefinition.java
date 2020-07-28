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
package org.eclipse.persistence.tools.schemaframework;

import java.io.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p>
 * <b>Purpose</b>: Allow for creation of views.
 * </p>
 */
public class ViewDefinition extends DatabaseObjectDefinition {
    protected String selectClause;

    public ViewDefinition() {
        super();
        this.selectClause = "";
    }

    /**
     * INTERNAL:
     * Return the DDL to create the view.
     */
    public Writer buildCreationWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write(session.getPlatform().getCreateViewString());
            writer.write(getFullName());
            writer.write(" AS (");
            writer.write(getSelectClause());
            writer.write(")");
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * INTERNAL:
     * Return the DDL to drop the view.
     */
    public Writer buildDeletionWriter(AbstractSession session, Writer writer) throws ValidationException {
        try {
            writer.write("DROP VIEW " + getFullName());
        } catch (IOException ioException) {
            throw ValidationException.fileError(ioException);
        }
        return writer;
    }

    /**
     * The select clause is the select statement that is mapped into the view.
     * This is database specific SQL code.
     */
    public String getSelectClause() {
        return selectClause;
    }

    /**
     * The select clause is the select statement that is mapped into the view.
     * This is database specific SQL code.
     */
    public void setSelectClause(String selectClause) {
        this.selectClause = selectClause;
    }
}
