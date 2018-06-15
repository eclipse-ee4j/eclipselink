/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.util.Vector;
import java.util.Collection;

import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * @author Andrei Ilitchev
 * @since TOPLink/Java 1.0
 */
public class SQLUpdateAllStatementForTempTable extends SQLModifyAllStatementForTempTable {
    protected Collection assignedFields;
    public void setAssignedFields(Collection assignedFields) {
        this.assignedFields = assignedFields;
    }
    public Collection getAssignedFields() {
        return assignedFields;
    }

    @Override
    protected Collection getUsedFields() {
        Vector usedFields = new Vector(getPrimaryKeyFields());
        usedFields.addAll(getAssignedFields());
        return usedFields;
    }

    @Override
    protected void writeUpdateOriginalTable(AbstractSession session, Writer writer) throws IOException {
        session.getPlatform().writeUpdateOriginalFromTempTableSql(writer, getTable(),
                                                        new Vector(getPrimaryKeyFields()),
                                                        new Vector(getAssignedFields()));
    }
}
